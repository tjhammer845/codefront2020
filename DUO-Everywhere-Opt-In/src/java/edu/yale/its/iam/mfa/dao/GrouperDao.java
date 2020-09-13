package edu.yale.its.iam.mfa.dao;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.ResourceAccessException;

@Repository
public class GrouperDao {
	@Value("${grouper.url}")
    private String url ;
    private String SUBJECTS_PATH = "/subjects";
    private String GROUPS_PATH = "/groups";

    @Value("${grouper.admin.clientId}")
    private String clientId;
    @Value("${grouper.admin.clientSecret}")
    private String clientSecret;
    
    private HttpClient client;
    
    public static final String GROUPER_RESULT_EMPTY="GROUPER_RESULT_EMPTY";
    public static final Logger logger = Logger.getLogger(GrouperDao.class.getName());

    public GrouperDao() {
    }
  
    @PostConstruct
    public void init() {
    	client = HttpClient.newBuilder()
    			.version(Version.HTTP_1_1)
    			.followRedirects(Redirect.NORMAL)
    			.connectTimeout(Duration.ofSeconds(30))
    			.build();
    }
    /**
     * Get user name and memberships
     * @param netid
     * @return a map of 2 entries:
     * 			"name" -> user's display name like Smith, John (js100)
     * 			"memberships" -> Set<String> of group names user belongs to
     */
    public Map<String,Object> getMemberships(String netid){
    	logger.fine("Entering GrouperDao getGroups ");
    	logger.fine("GrouperDao url="+url);
    	logger.fine("GrouperDao clientId="+clientId);
        InputStream inputStream=null;
        JsonReader jsonReader=null;
        Set<String> resultSet=new HashSet<>();
        Map<String, Object> result= new HashMap<>();
        String displayName=null;
    	if(netid==null||!netid.matches("[a-zA-Z0-9]+")) {
    		resultSet.add(GROUPER_RESULT_EMPTY);
    		result.put("memberships",resultSet);
    		return result;
    	}
        try {
        	HttpRequest request=HttpRequest.newBuilder()
        			.uri(new URI(url+SUBJECTS_PATH))
        			.POST(BodyPublishers.ofString("{" + 
        					"  \"WsRestGetGroupsRequest\":{" + 
        					"    \"subjectLookups\":[{" + 
        					"        \"subjectIdentifier\": \""+netid+"\"" + 
        					"    }]\r\n" + 
        					"  }\r\n" + 
        					"}"))
        			.header("Content-Type", "text/x-json")
                    .header("Authorization", basicAuth(clientId, clientSecret))
                    .timeout(Duration.ofSeconds(10))
                    .build();
        	HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());
        	inputStream = response.body();
        	jsonReader = Json.createReader(inputStream);
        	JsonObject resultObj=jsonReader.readObject();
        	logger.fine("VALUE:"+resultObj.toString());
        	if(resultObj!=null) {
        		JsonObject wsGetGroupsObj=resultObj.getJsonObject("WsGetGroupsResults");
        		if(wsGetGroupsObj!=null) {
        			JsonArray resultsArr=wsGetGroupsObj.getJsonArray("results");
        			if(resultsArr!=null&&resultsArr.size()>0) {
        				JsonObject firstResult=resultsArr.getJsonObject(0);
            			JsonArray membershipArr=firstResult.getJsonArray("wsGroups");
            			if(membershipArr!=null&&membershipArr.size()>0) {
            				for(JsonValue group:membershipArr) {
            					String groupName=((JsonObject)group).getString("name");
            					if(groupName!=null) {
            						resultSet.add(groupName);
            					}
            				}
            			}
            			JsonObject subject=firstResult.getJsonObject("wsSubject");
            			JsonString subjectName;
            			if(subject!=null&& (subjectName=subject.getJsonString("name"))!=null) {
            				displayName=subjectName.getString();
            			}
        			}
        		}

        	}
        	if(resultSet.isEmpty())resultSet.add(GROUPER_RESULT_EMPTY);
        	result.put("name", displayName);
        	result.put("memberships", resultSet);
        	logger.fine("Exiting GrouperDao getGroups "+resultSet);
        	return result;
        }catch(Exception e){
        	e.printStackTrace();
        	logger.severe("Error GrouperDao getMembership: "+e.getMessage());
        	throw new ResourceAccessException("GrouperDao error: "+e.getClass().getName()+"/"+e.getMessage());
        }finally {
        	try{
        		if(jsonReader!=null) jsonReader.close();
        		if(inputStream!=null) inputStream.close();
        	}catch(Exception e) {}
        }
    }
    
    /**
     * Add a netid to a group
     * @param netid
     * @param group
     * @return 	0 - success/added 
     * 			1 - success/already a member 
     * 			2 - failure
     */
    public int addMember(String netid, String group){
    	logger.fine("Entering GrouperDao addMember ");
    	logger.fine("GrouperDao url="+url);
    	logger.fine("GrouperDao clientId="+clientId);
        InputStream inputStream=null;
        JsonReader jsonReader=null;
        int result=2;
    	if(netid==null||group==null||!netid.matches("[a-zA-Z0-9]+")) {
    		return 2;
    	}
        try {
        	HttpRequest request=HttpRequest.newBuilder()
        			.uri(new URI(url+GROUPS_PATH))
        			.POST(BodyPublishers.ofString(
        					"{"+
        						"\"WsRestAddMemberRequest\":{"+
        							"\"subjectLookups\":[{"+
        									"\"subjectSourceId\":\"sourceId\","+
        									"\"subjectIdentifier\":\""+netid+"\""+
        								"}],"+
        							"\"wsGroupLookup\":{"+
        								"\"groupName\":\""+group+"\""+
        							"}"+
        						"}"+
        					"}"))
        			.header("Content-Type", "text/x-json")
                    .header("Authorization", basicAuth(clientId, clientSecret))
                    .timeout(Duration.ofSeconds(10))
                    .build();
        	HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());
        	inputStream = response.body();
        	jsonReader = Json.createReader(inputStream);
        	JsonObject resultObj=jsonReader.readObject();
        	logger.fine("VALUE:"+resultObj.toString());
        	if(resultObj!=null) {
        		JsonObject addMemberResultsObj=resultObj.getJsonObject("WsAddMemberResults");
        		if(addMemberResultsObj!=null) {

        				JsonArray resultsArr=addMemberResultsObj.getJsonArray("results");
        				if(resultsArr!=null&&resultsArr.size()>0) {
        					JsonObject firstResultMetaData=((JsonObject)resultsArr.get(0)).getJsonObject("resultMetadata");
        					if(firstResultMetaData!=null) {
        						boolean success="T".equals(firstResultMetaData.getString("success"));
        						boolean added="SUCCESS".equals(firstResultMetaData.getString("resultCode"));
        						boolean existed="SUCCESS_ALREADY_EXISTED".equals(firstResultMetaData.getString("resultCode"));
        						if(success&&added) return 0;
        						if(success&&existed) return 1;
        					}        				
            		}
        		}
        	}
        	logger.fine("Exiting GrouperDao addMember "+result);
        	return result;
        }catch(Exception e){
        	e.printStackTrace();
        	logger.severe("Error GrouperDao addMember: "+e.getMessage());
        	throw new ResourceAccessException("GrouperDao error: "+e.getClass().getName()+"/"+e.getMessage());
        }finally {
        	try{
        		if(jsonReader!=null) jsonReader.close();
        		if(inputStream!=null) inputStream.close();
        	}catch(Exception e) {}
        }
    }
    
    private static String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
