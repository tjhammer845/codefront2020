package edu.yale.its.iam.mfa.dao;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.ResourceAccessException;

import com.duosecurity.client.Http;
import com.duosecurity.duoweb.DuoWeb;
import com.duosecurity.duoweb.Util;

import edu.yale.its.iam.mfa.domain.DuoUser;

@Repository
public class DuoDao {

	@Value("${duo.host}")
	private String host;
	
	//Admin API - IAM
	@Value("${duo.admin.ikey}")
	private String admin_ikey;
	@Value("${duo.admin.skey}")
	private String admin_skey;
	
	//Device Manager Portal (for MFA everywhere opt in)
	@Value("${duo.dm.ikey}")
	private String dm_ikey;
	@Value("${duo.dm.skey}")
	private String dm_skey;
	@Value("${duo.dm.akey.secret}")
	private String dm_akey_secret;
	
	private String dm_akey=null;
	
	private static final Pattern extract_number=Pattern.compile("\\d+");
	private static final Logger logger=Logger.getLogger(DuoDao.class.getName());
	
	@PostConstruct
	public void init() {
		try {
			dm_akey=Util.hmacSign(dm_skey, dm_akey_secret);
		}catch(NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
			//impossible
		}
	}
	
	public DuoUser findDuoUser(String netid){
		logger.fine("Entering DuoDao findDuoUser for "+netid);
		boolean userFound=false;
		boolean hasToken=false;
		boolean hasPhone=false;
		boolean betterThanLandline=false;
		String landlineNumbers="";
		String firstPhoneNumber=null;
		int totalDevices=0;
		try {
			Http request = new Http("GET", host, "/admin/v1/users", 15);
			request.addParam("username", netid);
			request.addParam("limit", "1");
			request.signRequest(admin_ikey,admin_skey);
			JSONArray resArray=(JSONArray)request.executeRequest();
			if(resArray.length()==0) return new DuoUser(netid,false,false,false, false,"",0,null);
			JSONObject response= resArray.getJSONObject(0);
			//need to check "status":"active"?
			userFound=true;
			if(response.has("phones")) {
				JSONArray phones=response.getJSONArray("phones");
				totalDevices+=phones.length();
				for(int i=0;i<phones.length();i++) {
					hasPhone=true;
					JSONObject phone=phones.getJSONObject(i);
					String phoneType=phone.getString("type");
					String number=phone.getString("number");
					String maskedNumber=getMaskedPhone(number);
					if(maskedNumber!=null) firstPhoneNumber=maskedNumber;
					if("landline".equalsIgnoreCase(phoneType)&&maskedNumber!=null) {
						landlineNumbers+=(landlineNumbers.isEmpty()?"":", ")+maskedNumber;
					}
					if("mobile".equalsIgnoreCase(phoneType)) {
						betterThanLandline=true;
					}
					//if phone type is unknown, look into capabilities, if "phone" is the only capability, then it's landline
					if("unknown".equalsIgnoreCase(phoneType) && phone.has("capabilities")) {
						JSONArray capabilities=phone.getJSONArray("capabilities");
						for(int j=0;j<capabilities.length();j++) {
							String capability=capabilities.getString(j);
							if("phone".equalsIgnoreCase(capability)&&maskedNumber!=null) {
								landlineNumbers+=(landlineNumbers.isEmpty()?"":", ")+maskedNumber;
							}else {
								//"push" "sms" "mobile_otp" = capability
								betterThanLandline=true;
							}
						}
					}
				}
			}
			if(response.has("tokens")){
				JSONArray tokens=response.getJSONArray("tokens");
				totalDevices+=tokens.length();
				hasToken=tokens.length()>0;
			}
			return new DuoUser(netid,userFound,hasToken,hasPhone,betterThanLandline,landlineNumbers,totalDevices,firstPhoneNumber);
		}catch(Exception e) {
			e.printStackTrace();
			logger.severe("Error in DuoDao findDuoUser: "+e.getClass().getName()+"/"+e.getMessage());
			throw new ResourceAccessException("DuoDao error: "+e.getClass().getName()+"/"+e.getMessage());
		}finally {
			logger.fine("Leaving DuoDao findDuoUser for "+netid);
		}
	}
	
	//get masked phone number in format "******1234". If original phone has less than 4 digits, return null.
	private String getMaskedPhone(String phone) {
        if(phone==null) return null;
        Matcher m = extract_number.matcher(phone);
        String result="";
        while(m.find()) {
            result+=m.group();
        }
        return (result.length()>=4)?"******"+result.substring(result.length()-4):null;        
	}
	
	public String signRequest(String netid) {
		return DuoWeb.signRequest(dm_ikey, dm_skey, dm_akey, netid);
	}
}
