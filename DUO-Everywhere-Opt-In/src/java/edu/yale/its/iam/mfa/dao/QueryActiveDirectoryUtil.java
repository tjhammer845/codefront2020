package edu.yale.its.iam.mfa.dao;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class QueryActiveDirectoryUtil {

	public static final Logger LOG = Logger.getLogger(QueryActiveDirectoryUtil.class.getName());
	
	private String YALE_INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

	@Value("${AD.url}")
	private String YALE_PROVIDER_URL;
	@Value("${AD.password}")
	private String YALE_SECURITY_CREDENTIALS;
	@Value("${AD.username}")
	private String YALE_SECURITY_PRINCIPAL;
	@Value("${AD.base}")
	private String searchBase;
	private static String YALE_SECURITY_AUTHENTICATION = "simple";
	@Value("${auth.memberof.ou}")
	private String memberofOu;
	@Value("${auth.memberof.dc}")
	private String memberofDc;
	
	//get attribute in result
	private static String userAccountControlAttribute = "userAccountControl";
	private static String distinguishedNameAttribute ="distinguishedName";
	private static String userSearchAttribute = "samaccountname";

	//error
	private static String FAILED_TO_CONNECT_AD = "FAILED_TO_CONNECT_AD_LDAP_CONTEXT_IS_NULL";
	private static String FAILED_TO_RETRIEVE_UAC = "FAILED_TO_RETRIEVE_UAC";
	private static String EXCEPTION_WHILE_CONN_AD = "EXCEPTION_OCCURRED_WHILE_GETTING_LDAP_CONTEXT_TO_CONNECT_AD";
	private static String EXCEPTION_WHILE_SEARCHING = "EXCEPTION_OCCURRED_WHILE_GETTING_LDAP_CONTEXT_AND_DOING_SEARCH";
	private static String LDAP_CONTEXT_CLOSE_EXCEPTION = "FAILED_TO_CLOSE_LDAP_CONTEXT_WHILE_RETREIVING_ATTRIBUTE_FROM_AD";
	private static String FAILED_TO_LOCK_NETID = "FAILED_TO_LOCK_NETID";
	private static String FAILED_TO_UNLOCK_NETID = "FAILED_TO_UNLOCK_NETID";
	
	//UAC code
	private static int ACCOUNT_DISABLED_FLAG = 0x0002;
	private static int UF_NORMAL_ACCOUNT = 0x0200;
	private static int UF_PASSWD_NOTREQD = 0x0020;
	private static int UF_ACCOUNTDISABLE = 0x0002;
	

	/**
	 * @param netid
	 * @return netid status
	 * 
	 * 
	 */
	public String isUserAccountLocked(String netid) {
		int uac = -1;
		boolean accountIsDisabled = true;

		String userAccountControl = getUserAccountControl(netid);

		if (null == userAccountControl) {
			LOG.severe(FAILED_TO_RETRIEVE_UAC);
			return null;
		}

		uac = Integer.parseInt(userAccountControl);
		accountIsDisabled = (uac & ACCOUNT_DISABLED_FLAG) == ACCOUNT_DISABLED_FLAG;
		
		if(accountIsDisabled){
			//546
			return "LOCKED";
		}
		
		if(!accountIsDisabled){
			//544
			return "UNLOCKED";
		}
		
		return null;
	}

	/**
	 * @param netid
	 * @return value for searched attribute such user account control
	 */
	private String getUserAccountControl(String netid) {
		return getSingleAttribute(netid, userAccountControlAttribute);
	}

	/**
	 * @return ldap context to connect AD
	 */
	private InitialLdapContext getLdapContext() {
		InitialLdapContext ldapContext = null;
		Hashtable<String, String> ldapEnv = new Hashtable<String, String>(11);
		ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY,YALE_INITIAL_CONTEXT_FACTORY);
		ldapEnv.put(Context.PROVIDER_URL,YALE_PROVIDER_URL);
		ldapEnv.put(Context.SECURITY_AUTHENTICATION, YALE_SECURITY_AUTHENTICATION);
		ldapEnv.put(Context.SECURITY_PRINCIPAL,YALE_SECURITY_PRINCIPAL);
		ldapEnv.put(Context.SECURITY_CREDENTIALS,YALE_SECURITY_CREDENTIALS );
		
		
		try {
			LOG.info("Using AD URL = "+YALE_PROVIDER_URL);
			LOG.info("Using AD Account = "+YALE_SECURITY_PRINCIPAL);
			LOG.info("Using AD Search Base = "+searchBase);
			ldapContext =  new InitialLdapContext(ldapEnv, null);
			
		} catch (Exception e) {
			
			LOG.severe(EXCEPTION_WHILE_CONN_AD);
		}
		
		return ldapContext;
	}

	/**
	 * @param netid
	 * @param attributeName
	 * @return value for searched attribute else null
	 */
	private String getSingleAttribute(String netid, String attributeName) {
		InitialLdapContext ldapContext = null;
		try{
		
		ldapContext = getLdapContext();

		if (null == ldapContext) {

			LOG.severe(FAILED_TO_CONNECT_AD);
			return null;
		}

		String returnedAtts[] = { attributeName };
		SearchControls searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchCtls.setReturningAttributes(returnedAtts);

		String searchFilter = "(&(objectClass=user)(" + userSearchAttribute + "={0}))";
				

		NamingEnumeration<?> answer = ldapContext.search(searchBase,searchFilter, new String[] { netid }, searchCtls);
		
		SearchResult sr = null;
			if (answer.hasMoreElements()) {
				sr = (SearchResult) answer.next();
				Attributes attrs = sr.getAttributes();
				if (attrs != null) {
					LOG.fine("SEARCHED ATTRIBUTE = "+ attributeName + " FOR " + netid);
					LOG.fine("ATTRIBUTE VALUE  = "+ attrs.get(attributeName).get().toString());
					return attrs.get(attributeName).get().toString();
				}
			}
		
		}
		catch (Exception e) {
			
			LOG.severe(EXCEPTION_WHILE_SEARCHING);
		}
		finally{
			
			try {
				ldapContext.close();
			} catch (Exception e) {
				LOG.severe(LDAP_CONTEXT_CLOSE_EXCEPTION);
			}
		}
		return null;
	}

	/**
	 * @param netid
	 * @param memberOfOU OPTIONAL. See below
	 * @param memberOfDC OPTIONAL. Ordered list like {"Prod","YaleGroups"}, {"yu","yale","edu"}.
	 * 						 If not null, the returned memberOf attribute must be in the specified context.
	 * @param attributeNames Array of attr names
	 * @return map of attribute name -> Set of values 
	 *       Value is a Set of strings.
	 *       if name="memberOf", try to extract the first "cn=xyz" part (xyz only) as value, instead of full value. This introduces
	 *       security concern, so the optional memberOfOU and memberOfDC params are added.
	 *       
	 */
	public Map<String, Set<String>> getAttributes(String netid, String[] memberOfOU, String[] memberOfDC, String... attributeNames) {
		LOG.fine("Entering QueryActiveDirectoryUtil getAttributes");
		InitialLdapContext ldapContext = null;
		Map<String,Set<String>> result=new HashMap<>();
		try{
			if(netid==null||netid.isEmpty()) {
				LOG.fine("Returning QueryActiveDirectoryUtil getAttributes: "+result);
				return result;
			}
			ldapContext = getLdapContext();
			if (null == ldapContext) {
				throw new Exception(FAILED_TO_CONNECT_AD);
			}
			
			SearchControls searchCtls = new SearchControls();
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			searchCtls.setReturningAttributes(attributeNames);
			String searchFilter = "(&(objectClass=user)(" + userSearchAttribute + "={0}))";
			NamingEnumeration<?> answer = ldapContext.search(searchBase,searchFilter, new String[] { netid }, searchCtls);
			SearchResult sr = null;
			if (answer.hasMoreElements()) {
				sr = (SearchResult) answer.next();
				Attributes attrs = sr.getAttributes();
				if (attrs != null) {					
					for(String attributeName:attributeNames) {
						Attribute attr=attrs.get(attributeName);
						if(attr==null) continue;
						LOG.fine("SEARCHED ATTRIBUTE = "+ attributeName + " FOR " + netid);
						LOG.fine("ATTRIBUTE size  = "+ attr.size()+" values");
						NamingEnumeration<?> valenum=attr.getAll();
						Set<String> valset=new HashSet<>();
						while(valenum.hasMoreElements()) {
							Object valueobj=valenum.next();
							if(valueobj==null) continue;
							if(!"memberOf".equals(attributeName)) {
								valset.add(valueobj.toString());
								continue;
							}
							int currentDC=memberOfDC==null?-1:memberOfDC.length-1;
							int currentOU=memberOfOU==null?-1:memberOfOU.length-1;
							try {
								LdapName groupDn=new LdapName(valueobj.toString());
								for(Rdn rdn : groupDn.getRdns()) {
									String rdnType=rdn.getType();
									String rdnValue=(String)rdn.getValue();
									//RDN is ordered from broadest to narrowest e.g. dc=edu, dc=yale, ......
									if(memberOfDC!=null&&"DC".equalsIgnoreCase(rdnType)) {
										if(currentDC<0||!memberOfDC[currentDC].equals(rdnValue)) {
											break;//dc mismatch
										}else {
											currentDC--;
										}
									}else if(memberOfOU!=null&&"OU".equalsIgnoreCase(rdnType)) {
										if(currentOU<0||!memberOfOU[currentOU].equals(rdnValue)) {
											break;//ou mismatch
										}else {
											currentOU--;
										}
									}else if("CN".equalsIgnoreCase(rdnType)) {
										if(rdnValue!=null&&currentDC==-1&&currentOU==-1) {
											valset.add(rdnValue);
											break;
										}
									}
								}
							}catch(InvalidNameException e) {
								valset.add(valueobj.toString());
							}
						}
						result.put(attributeName, valset);						
					}
				}
			}
			LOG.fine("Returning QueryActiveDirectoryUtil getAttributes: "+result);
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
			LOG.severe("Error QueryActiveDirectoryUtil getAttributes: "+EXCEPTION_WHILE_SEARCHING);
			return result;
		}
		finally{			
			try {
				if(ldapContext!=null)ldapContext.close();
			} catch (Exception e) {
				LOG.severe(LDAP_CONTEXT_CLOSE_EXCEPTION);
			}
		}
	}
	
	public Map<String, Set<String>> getAttributes(String netid, String... attributeNames){
		return getAttributes(netid, memberofOu.split("\\."), memberofDc.split("\\."), attributeNames);
	}
	/**
	 * @param netid
	 * @param netidLastStatus
	 * @return netid status after unlock action else null
	 */
	public String unlockNetIdAD(String netid, String netidLastStatus) {
		LOG.info("UNLOCK FOR NETID = " + netid + " STATUS AT THE TIME OF ACTION = "+ netidLastStatus);
		String netidStatusAfterAction = null;
		InitialLdapContext ldapContext = getLdapContext();

		if (null == ldapContext) {
			LOG.severe(FAILED_TO_CONNECT_AD);
			return null;
		}
		
		byte[] logonHours = new byte[21];
		for (int i = 0; i < logonHours.length; i++) {
		    logonHours[i] = (byte)0xFF;
		}
		
	     ModificationItem[] mods = new ModificationItem[2];
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(userAccountControlAttribute,Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWD_NOTREQD)));
        mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("logonHours",logonHours));
		
	    String userDistinguishedName = getSingleAttribute(netid,distinguishedNameAttribute );
	    
	    try {
			ldapContext.modifyAttributes(userDistinguishedName, mods);
			netidStatusAfterAction = isUserAccountLocked(netid);
			LOG.fine("netidStatusAfterAction = "+netidStatusAfterAction);
			
			if(netidStatusAfterAction.equals("UNLOCKED")){
				
				return "NETID_UNLOCKED_SUCCESSFUL";
				
			}
			
		} catch (Exception e) {
		   LOG.severe(FAILED_TO_UNLOCK_NETID);
		}
	    
	    LOG.info("UNLOCKED NETID FOR = " + netid + " HAVING DISTINGUISHED NAME =" + userDistinguishedName);
		return null;
	    
	}
	
	
	/**
	 * @param netid
	 * @param netidLastStatus
	 * @return netid status after lock action else null
	 */
	public String lockNetIdAD(String netid, String netidLastStatus) {
		LOG.info("LOCK FOR NETID = " + netid + " STATUS AT THE TIME OF ACTION = "+ netidLastStatus);
		String netidStatusAfterAction = null;
		DirContext ldapDirContext = getLdapContext();

		if (null == ldapDirContext) {
			
			LOG.severe(FAILED_TO_CONNECT_AD);
			return null;
		}
		
	    byte[] logonHours = new byte[21];
		for (int i = 0; i < logonHours.length; i++) {
		    logonHours[i] = (byte)0x00;
		}

	    ModificationItem[] mods = new ModificationItem[1];
	    mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(userAccountControlAttribute,Integer.toString(UF_ACCOUNTDISABLE + UF_PASSWD_NOTREQD)));
		//mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("logonHours",logonHours));
	    
	    
	    String userDistinguishedName = getSingleAttribute(netid,distinguishedNameAttribute );
	    
	    try {
			ldapDirContext.modifyAttributes(userDistinguishedName, mods);
			netidStatusAfterAction = isUserAccountLocked(netid);
			LOG.fine("netidStatusAfterAction = "+netidStatusAfterAction);
			if(netidStatusAfterAction.equals("LOCKED")){
				
				return "NETID_LOCKED_SUCCESSFUL";
				
			}
			
		} catch (Exception e) {
			
			 LOG.severe(FAILED_TO_LOCK_NETID);
		}
	    
	    LOG.info("LOCKED NETID FOR = " + netid + " HAVING DISTINGUISHED NAME =" + userDistinguishedName);
		return null;
	}
		
}
