<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<t:template>

    <jsp:body>
        <div id="OptIn1">
            <main class="main-content">
                <section class="steps-container">
                    <div class="step step-1">
                        <i title="Current: Step 1 of 3: DUO Everywhere Opt-In - Summary" class="fas fa-info"></i>
                        <div aria-hidden="true">Summary</div>
                    </div>
                    <hr class="hr-1" aria-hidden="true" >
                    <div class="step step-2">
                        <i title="Step 2 of 3: DUO Everywhere Opt-In - DUO Setup" class="fas fa-shield-alt"></i>
                        <div aria-hidden="true">DUO Setup</div>
                    </div>
                    <hr class="hr-2" aria-hidden="true" >
                    <div class="step step-3">
                        <i title="Step 3 of 3: DUO Everywhere Opt-In - Enrolled" class="fas fa-check"></i>
                        <div aria-hidden="true">Enrolled</div>
                    </div>
                </section>
                <h2 class="form-header">Introducing DUO Everywhere</h2>
                <p>DUO Everywhere adds an additional layer of security to confirming your identity when signing into protected Yale accounts,
                 applications or websites. This significantly improves our individual account security and helps us all create a more secure Yale.</p>
                 <p>Once you opt in, you will need to authenticate with DUO when you first log in to CAS, MS Outlook and MS Office 0365. You will 
                     be asked to do this regardless of your location or network. You will be prompted to authenticate once per session, per application or browser, per device.</p>
                <h2 class="form-header">What to expect once I Opt-In</h2>
                <ul>
                    <li><p>Processing times for opting in varies by session type. Browser sessions will begin receiving DUO authentication prompts immediately
                            after opt-in, but MS O365 sessions will begin receiving prompts within 3-6 hours of opt-in.</p></li>
                    <li><p>Depending on your email application and settings, you may experience difficulty accessing your email. 
                        For more information on this subject, please view the Knowledge Base article 
                        <a target="_blank" href="https://yale.service-now.com/it?id=support_article&sys_id=8ebf55981bcf4014863f2fc4bd4bcb89">“Office 365 Email: DUO Everywhere.”</a></p></li>
                </ul>
                <h2 class="form-header">Recommendations for a more convenient experience</h2>
                <ul>
                    <li><p><strong>Register at least two</strong> <a target="_blank" href="https://yale.service-now.com/it?id=support_article&sys_id=13e804751bf28c90b0f9fee58d4bcbae">DUO authentication methods</a> that will be available to you whenever accessing Yale resources or conducting
                            university business. Note: You will be able to update your DUO account settings on the next page.</p></li>
                    <li><p><strong>Select the</strong> <a target="_blank" href="https://yale.service-now.com/it?id=support_article&sys_id=f00df6a11b470814d3040dc2cd4bcb5f">“remember me for 90 days” option</a> to reduce the number of DUO authentication requests you will receive within a 90 day timespan.</p></li>
                    <li><p><strong>Review</strong> <a target="_blank" href="https://yale.service-now.com/it?id=support_article&sys_id=84a416f31b0f04108024da83cd4bcbd1">“DUO Everywhere Frequently Asked Questions”</a>.</p></li>
                </ul>
                <p>For questions, please contact the ITS Help Desk at <a target="_blank" href="https://yale.service-now.com/it?id=get_help">helpme.yale.edu</a>.</p>
                <form:form name="Opt-In Acknowledgement" class="opt-in-acknowledgement" method="POST" action="${pageContext.request.contextPath}/optin/verify">
                    <div class="form-group">
                        <div class="button-container">
                            <input type="hidden" name="acceptOptinTerms" value="true">
                            <button type="submit" title="Continue" name="Continue" class="btn btn-yale font-weight-bold mr-12">Continue</button>
                        </div>
                    </fieldset>
                </form:form>
            </main>
        </div>
    </jsp:body>
</t:template>