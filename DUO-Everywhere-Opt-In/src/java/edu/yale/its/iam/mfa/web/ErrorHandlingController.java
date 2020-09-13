package edu.yale.its.iam.mfa.web;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorHandlingController {
	
	private static final Logger logger=Logger.getLogger(ErrorHandlingController.class.getName());
	
	@RequestMapping("/error")
	@ExceptionHandler(Exception.class)
	public ModelAndView handleError(HttpServletRequest req, Exception ex) {
		logger.severe("Request: " + req.getRequestURL() + " raised " + ex);
		//ex.printStackTrace();
		ModelAndView mav = new ModelAndView("sorry");
		mav.addObject("sorryType", OptinController.SORRY_GENERAL);
		mav.addObject("sorryMsg", "An error occurred. Please try again later.");
		return mav;
	}
}
