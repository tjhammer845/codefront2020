package edu.yale.its.iam.mfa.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class DuoUser implements UserDetails{

	private static final long serialVersionUID = 1L;
	private String netid;
	private String displayName;
	private boolean foundInGroupEligible;
	private boolean foundInGroupEnrolled;
	private boolean foundInDuo;
	private boolean hasToken;
	private boolean hasPhone; //landline or mobile
	private boolean hasMobile;
	private int totalDevices;
	private String firstPhoneNumber; //only useful when totalDevices=1
	private String landlineNumbers; //list of landline numbers delimited by ,
	
	private boolean enabled;
	public static final String ROLE_AUTHENTICATED="ROLE_AUTHENTICATED";
	public static final String ROLE_ELIGIBLE="ROLE_ELIGIBLE";
	public static final String ROLE_ENROLLED="ROLE_ENROLLED";
	private List<GrantedAuthority> authorities;
	
	public DuoUser() {
		this.netid=null;
		this.foundInGroupEligible=false;
		this.foundInGroupEnrolled=false;
		this.foundInDuo=false;
		this.hasToken=false;
		this.hasPhone=false;
		this.hasMobile=false;
		this.landlineNumbers="";
		this.totalDevices=0;
		this.firstPhoneNumber=null;
		this.enabled=false;
		this.authorities=new ArrayList<>();
	}
	
	public DuoUser(String netid, boolean foundInDuo,
			boolean hasToken, boolean hasPhone, boolean hasMobile, String landlineNumbers, int totalDevices, String firstPhoneNumber) {
		super();
		this.netid = netid;
		this.foundInGroupEligible=false;
		this.foundInGroupEnrolled=false;
		this.foundInDuo = foundInDuo;
		this.hasToken = hasToken;
		this.hasPhone = hasPhone;
		this.hasMobile = hasMobile;
		this.landlineNumbers = landlineNumbers;
		this.totalDevices = totalDevices;
		this.firstPhoneNumber = firstPhoneNumber;
		this.enabled=false;
		this.authorities=new ArrayList<>();
	}

	public String getNetid() {
		return netid;
	}

	public void setNetid(String netid) {
		this.netid = netid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isFoundInGroupEligible() {
		return foundInGroupEligible;
	}

	public void setFoundInGroupEligible(boolean foundInGroupEligible) {
		this.foundInGroupEligible = foundInGroupEligible;
	}

	public boolean isFoundInGroupEnrolled() {
		return foundInGroupEnrolled;
	}

	public void setFoundInGroupEnrolled(boolean foundInGroupEnrolled) {
		this.foundInGroupEnrolled = foundInGroupEnrolled;
	}

	public boolean isFoundInDuo() {
		return foundInDuo;
	}

	public void setFoundInDuo(boolean foundInDuo) {
		this.foundInDuo = foundInDuo;
	}

	public boolean isHasToken() {
		return hasToken;
	}

	public void setHasToken(boolean hasToken) {
		this.hasToken = hasToken;
	}

	public boolean isHasPhone() {
		return hasPhone;
	}

	public void setHasPhone(boolean hasPhone) {
		this.hasPhone = hasPhone;
	}

	public boolean isHasMobile() {
		return hasMobile;
	}

	public void setHasMobile(boolean hasMobile) {
		this.hasMobile = hasMobile;
	}

	public String getLandlineNumbers() {
		return landlineNumbers;
	}

	public void setLandlineNumbers(String landlineNumbers) {
		this.landlineNumbers = landlineNumbers;
	}
	
	public int getTotalDevices() {
		return totalDevices;
	}

	public void setTotalDevices(int totalDevices) {
		this.totalDevices = totalDevices;
	}

	public String getFirstPhoneNumber() {
		return firstPhoneNumber;
	}

	public void setFirstPhoneNumber(String firstPhoneNumber) {
		this.firstPhoneNumber = firstPhoneNumber;
	}

	
	
	/*
	 * Spring UserDetails methods:
	 */


	public void addAuthority(String authority) {
		authorities.add(new SimpleGrantedAuthority(authority));
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		return netid;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	public void setEnabled(boolean enabled) {
		this.enabled=enabled;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}	
}
