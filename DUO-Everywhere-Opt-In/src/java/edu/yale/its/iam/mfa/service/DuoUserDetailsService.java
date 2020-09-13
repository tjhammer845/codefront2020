package edu.yale.its.iam.mfa.service;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.yale.its.iam.mfa.dao.DuoDao;
import edu.yale.its.iam.mfa.dao.GrouperDao;
import edu.yale.its.iam.mfa.domain.DuoUser;

@Service("duoUserDetailsService")
public class DuoUserDetailsService implements UserDetailsService{

	@Autowired
	private GrouperDao grouperDao; 
	//private QueryActiveDirectoryUtil adDao;
	//Use ad instead of grouper for better performance but the group->ad connection is unreliable
	
	@Autowired
	private DuoDao duoDao;
	
	@Value("${auth.eligible}")
	private String grouperGroupEligible;

	@Value("${auth.enrolled}")
	private String grouperGroupEnrolled;
	
	private static final Logger logger=Logger.getLogger(DuoUserDetailsService.class.getName());
	
	@SuppressWarnings("unchecked")
	@Override
	public UserDetails loadUserByUsername(String netid) throws UsernameNotFoundException {
		logger.fine("Entering DuoUserDetailsService for "+netid);
		logger.fine("grouper.group.eligible="+grouperGroupEligible);
		logger.fine("grouper.group.enrolled="+grouperGroupEnrolled);
		Set<String> memberships=null;
		String displayName=null;
		DuoUser user=new DuoUser();
		try{
			Map<String, Object> nameAndMemberships=grouperDao.getMemberships(netid);
			displayName=(String)nameAndMemberships.get("name");
			memberships=(Set<String>)nameAndMemberships.get("memberships");
			
			/*
			Map<String, Set<String>> adResult=adDao.getAttributes(netid, "displayName","memberOf");
			memberships=adResult.get("memberOf");
			Set<String> names=adResult.get("displayName");
			if(names!=null&&!names.isEmpty()) {
				displayName=names.iterator().next();
			}
			*/
		}catch(Exception e) {
			logger.severe("DuoUserDetailsService: error from GrouperDao");
		}
		user.setNetid(netid);
		user.setDisplayName(displayName);
		//only ROLE_AUTHENTICATED is used by spring security. ROLE_ELIGIBLE and ROLE_ENROLLED are not used
		user.addAuthority(DuoUser.ROLE_AUTHENTICATED);
		boolean eligible=memberships!=null&&memberships.contains(grouperGroupEligible);
		boolean enrolled=memberships!=null&&memberships.contains(grouperGroupEnrolled);
		if(eligible) {
			user.addAuthority(DuoUser.ROLE_ELIGIBLE);
			try{
				DuoUser user2=duoDao.findDuoUser(netid);
				user.setFoundInDuo(user2.isFoundInDuo());
				user.setHasPhone(user2.isHasPhone());
				user.setHasMobile(user2.isHasMobile());
				user.setHasToken(user2.isHasToken());
				user.setLandlineNumbers(user2.getLandlineNumbers());
				user.setTotalDevices(user2.getTotalDevices());
				user.setFirstPhoneNumber(user2.getFirstPhoneNumber());
			}catch(Exception e) {
				logger.severe("DuoUserDetailsService: error from DuoDao");
			}
		}
		
		if(enrolled) {
			user.addAuthority(DuoUser.ROLE_ENROLLED);
		}
		user.setEnabled(true); //every authenticated user is allowed
		user.setFoundInGroupEligible(eligible);
		user.setFoundInGroupEnrolled(enrolled);
		return user;
	}

}
