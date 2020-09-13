package edu.yale.its.iam.mfa.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.yale.its.iam.mfa.dao.DuoDao;
import edu.yale.its.iam.mfa.dao.GrouperDao;
import edu.yale.its.iam.mfa.domain.DuoUser;

@Controller
public class OptinController{

	@Autowired
	private DuoDao duoDao;
	@Autowired
	private GrouperDao grouperDao;
	
	@Value("${duo.host}")
	private String host;
	
	@Value("${grouper.optin.provisionto}")
	private String optinGroup;
	
	private static final Logger logger=Logger.getLogger(OptinController.class.getName());
	
	public final static int SORRY_GENERAL=0;
	private final static int SORRY_ALREADY_ENROLLED=1;
	private final static int SORRY_NOT_ELIGIBLE=2;
	private final static int SORRY_NOT_KNOWN_DUO=3;

	
	/**
	 * If mapping "" or "/", it only maps /mfa/optin/, but not /mfa/optin
	 * @return
	 */
	@GetMapping("/landing")
	public ModelAndView renderLanding() {
		DuoUser user=getUser(false);
		ModelAndView errorResult=checkEligibility(user,true);
		if(errorResult!=null) return errorResult;
		ModelAndView result=new ModelAndView("landing");
		result.addObject("user",user);
		return result;
	}
	
	@RequestMapping("/verify")
	public ModelAndView renderDeviceManager(@RequestParam(name="acceptOptinTerms", defaultValue="true") String acceptTerms) {
		DuoUser user=getUser(false);
		ModelAndView errorResult=checkEligibility(user,true);
		if(errorResult!=null) return errorResult;
		if(!"true".equals(acceptTerms)) {
			ModelAndView landingResult=new ModelAndView("landing");
			landingResult.addObject("user",user);
			List<String> errorMsg=new ArrayList<>();
			errorMsg.add("Please accept the terms.");
			landingResult.addObject("errorMsg",errorMsg);
			return landingResult;
		}
		ModelAndView result=new ModelAndView("devicemanager");
		result.addObject("user",user);
		result.addObject("duoHost",host);
		result.addObject("sigRequest",duoDao.signRequest(user.getNetid()));
		result.addObject("ackLandlineChecked",false);
		result.addObject("ackRegisterDeviceChecked",false);
		return result;
	}
	
	@PostMapping("/submit")
	public ModelAndView verifySubmit(
			@RequestParam(name="optinswitch", defaultValue="false") String optinSwitch,
			@RequestParam(name="ackLandline", defaultValue="false") String ackLandline, 
			@RequestParam(name="ackRegisterDevice", defaultValue="false") String ackRegisterDevice) {
		DuoUser user=getUser(true);
		ModelAndView errorResult=checkEligibility(user,false);
		if(errorResult!=null) return errorResult;
		boolean optinSwitchChecked="true".equals(optinSwitch);
		boolean ackLandlineChecked="true".equals(ackLandline);
		if(optinSwitchChecked
				&&(user.isHasMobile()
						||user.isHasToken()
						||(user.isHasPhone()&&ackLandlineChecked)
				  )
		  ) {
			try {
				int optinResult=grouperDao.addMember(user.getNetid(), optinGroup);
				if(optinResult<2) {
					ModelAndView confirmResult=new ModelAndView("confirm");
					confirmResult.addObject("user",user);
					confirmResult.addObject("confirmMsg", "You are all set! Thank you for DUO Everywhere opt-in.");
					return confirmResult;
				}
			}catch(Exception e) {
				//e.printStackTrace();
				logger.severe("Unable to optin due to GrouperDao failure.");
			}
			ModelAndView sorryResult=new ModelAndView("sorry");
			sorryResult.addObject("sorryType",SORRY_GENERAL);
			sorryResult.addObject("sorryMsg","Opt-in failed. Please try again later.");
			return sorryResult;
		}else {
			List<String> errorMsg=new ArrayList<>();
			if(!optinSwitchChecked) errorMsg.add("Please confirm that you want to opt in above to continue.");
			if(!(user.isHasPhone()||user.isHasToken())) errorMsg.add("In order to continue with the opt-in process, you need to have at least one device registered with your DUO account. For help setting up your DUO account see <a target='_blank' href='https://yale.service-now.com/it/storage.admin@yale.edu?id=support_article&sys_id=13e804751bf28c90b0f9fee58d4bcbae'>DUO Everywhere: Opting in and managing devices</a>.");
			if(
				(!(user.isHasMobile()||user.isHasToken()))
				&& user.isHasPhone() 
				&& !ackLandlineChecked){
				errorMsg.add("You have landline phone only. Please click on the acknowledgement.");
			}
					
			//stay in same dm page
			ModelAndView dmResult=new ModelAndView("devicemanager");
			dmResult.addObject("user",user);
			dmResult.addObject("duoHost",host);
			dmResult.addObject("sigRequest",duoDao.signRequest(user.getNetid()));
			dmResult.addObject("ackLandlineChecked",ackLandlineChecked);
			dmResult.addObject("errorMsg",errorMsg);
			return dmResult;
		}
	}
	
	//Help is external page. No mapping needed. @GetMapping("/help")
	public ModelAndView showHelp() {
		return new ModelAndView("help");
	}
	
	private DuoUser getUser(boolean recheckDuo) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DuoUser user=null;
		if (principal instanceof DuoUser) {
			user= (DuoUser)principal;
		} else {
			return null;
		}
		if(recheckDuo) {
			try{
				user.setFoundInDuo(false);
				DuoUser user2=duoDao.findDuoUser(user.getNetid());
				user.setFoundInDuo(user2.isFoundInDuo());
				user.setHasPhone(user2.isHasPhone());
				user.setHasMobile(user2.isHasMobile());
				user.setHasToken(user2.isHasToken());
				user.setLandlineNumbers(user2.getLandlineNumbers());
				user.setTotalDevices(user2.getTotalDevices());
				user.setFirstPhoneNumber(user2.getFirstPhoneNumber());
			}catch(Exception e) {
				logger.severe("DuoUserDetalsService: error from DuoDao");
			}
		}
		return user;
	}
	
	private ModelAndView checkEligibility(DuoUser user, boolean checkEnrolled) {
		ModelAndView sorryResult=new ModelAndView("sorry");
		if(user==null) {
			sorryResult.addObject("sorryType", SORRY_GENERAL);
			sorryResult.addObject("sorryMsg", "Security detail service is unavailable. DuoUser is empty");
			return sorryResult;
		}
		logger.info("User is principal "+user.getNetid()+" found "+user.isFoundInDuo());
		sorryResult.addObject("user", user);
		if(checkEnrolled&&user.isFoundInGroupEnrolled()) {			
			sorryResult.addObject("sorryType",SORRY_ALREADY_ENROLLED);
			return sorryResult;
		}
		if(!user.isFoundInGroupEligible()) {
			sorryResult.addObject("sorryType",SORRY_NOT_ELIGIBLE);
			return sorryResult;
		}
		if(!user.isFoundInDuo()) {
			sorryResult.addObject("sorryType",SORRY_NOT_KNOWN_DUO);
			return sorryResult;
		}
		return null;
	}
}