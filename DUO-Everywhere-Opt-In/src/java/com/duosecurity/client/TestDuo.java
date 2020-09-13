package com.duosecurity.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

/**
Example admin get user:
 {
	"firstname":"Haibei",
	"notes":"",
	"desktoptokens":[],
	"created":1556310136,
	"last_login":1580998546,
	"alias1":"haibei.zhang@yale.edu",
	"groups":[{
		"push_enabled":true,
		"group_id":"DGK36B79GXF0QTTFKRHF",
		"sms_enabled":true,
		"name":"Duo_Active",
		"mobile_otp_enabled":true,
		"voice_enabled":true,
		"desc":"",
		"status":"Active"
		}],
	"phones":[{
		"postdelay":"",
		"extension":"",
		"capabilities":["auto","push","sms","phone","mobile_otp"],
		"last_seen":"2020-02-06T17:10:00",
		"type":"Mobile",
		"platform":"Google Android",
		"number":"+18602051594",
		"predelay":"",
		"name":"",
		"model":"Samsung Galaxy S6",
		"sms_passcodes_sent":false,
		"activated":true,
		"phone_id":"DPFMI0N91ORPJM2PAHUF"
		}],
	"alias3":null,
	"alias2":null,
	"alias4":null,
	"lastname":"Zhang",
	"realname":"Zhang, Haibei",
	"is_enrolled":true,
	"user_id":"DUFVO3DLUIUP69QLWLIV",
	"webauthncredentials":[],
	"last_directory_sync":1580933154,
	"tokens":[],
	"email":"haibei.zhang@yale.edu",
	"u2ftokens":[],
	"status":"active",
	"username":"hz393"
}

Logic:
if tokens>0 good
if phones>0 && none type=Mobile then landline only
if phones==0 then bad

 * @author hz393
 *
 */

public class TestDuo {

	//Same URL everywhere
	private static final String host="api-08659818.duosecurity.com";
	
	//Auth API
	private static final String auth_ikey="DIKMH9HQA8AMIZ43N565";
	private static final String auth_skey="0O3EFUTGSKcCZ3WdnjKU2g8ZAHHfCIjWoHquesxi";
	
	//Admin API - IAM
	private static final String admin_ikey="DIY1QX2V5YF5TBJBX285";
	private static final String admin_skey="wT2RLcdmPROypO9sUekIHZ8aBXckM28uqBiTjCMJ";
	
	private static final String netid="wcwelch";
	
	private static final Logger logger=Logger.getLogger(TestDuo.class.getName());
	
	public static void main(String[] args) {
		//testAuth();
		testAdmin();
	}
	
	public static void testAuth() {
		
		logger.info("TestDuo");
		
        JSONObject response = null;
        
        try{
        	/*
        	 * AUTH
        	 */
            Http request = new Http("POST",
                                    host,
                                    "/auth/v2/preauth",
                                    15);
            
            request.addParam("username", netid);        
            request.signRequest(auth_ikey,auth_skey);

            // Duo code calls home. It either throws an exception or else it returns user status.
            response = (JSONObject)request.executeRequest();
            request = null; // cleanup the request object

            // The user status comes back as "result"
            String status = response.getString("result");
            
            // Possible values are auth, allow, deny, enroll
            logger.info("Status of "+netid+ " is "+status);
            
            

        }catch(Exception e) {
        	e.printStackTrace();
        }
    }
	
	public static void testAdmin() {
    	/*
    	 * ADMIN
    	 * Search by /admin/v1/users/user_id is not working, NetID is username, not user_id
    	 */


        String realname=null;
        boolean userFound=false;
        boolean hasToken=false;
        boolean betterThanLandline=false;
        List<String> phoneTypes=new ArrayList<>();
        
        try {
            Http request = new Http("GET",
                    host,
                    "/admin/v1/users",
                    15);
            request.addParam("username", netid);
            request.addParam("limit", "1");request.signRequest(admin_ikey,admin_skey);
            JSONArray resArray=(JSONArray)request.executeRequest();
            if(resArray.length()>0) {
            	JSONObject response= resArray.getJSONObject(0);
            	userFound=true;
            	if(response.has("phones")) {
            		JSONArray phones=response.getJSONArray("phones");
            		for(int i=0;i<phones.length();i++) {
            			JSONObject phone=phones.getJSONObject(i);
            			String phoneType=phone.getString("type");
            			phoneTypes.add(phoneType);
            			if("Mobile".equalsIgnoreCase(phoneType)) {
            				betterThanLandline=true;
            			}
            		}
            	}
            	if(response.has("tokens")){
            		JSONArray tokens=response.getJSONArray("tokens");
            		hasToken=tokens.length()>0;
            	}
            	realname=response.getString("realname");
            }
        }catch(Exception e) {
        	e.printStackTrace();
        }


        logger.info("user realname: "+realname);
        logger.info("user found: "+userFound);
        logger.info("user has token: "+hasToken);
        logger.info("user phone types: "+phoneTypes);
        logger.info("user phone better than landline: "+betterThanLandline);
	}
}
