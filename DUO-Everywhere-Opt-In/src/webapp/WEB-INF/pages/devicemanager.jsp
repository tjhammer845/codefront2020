<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<t:template>    
    <jsp:body>   
        <script type="text/javascript">
            var checkbox = "#checkToAgree";
            var button = "#ContinueButton";
            var form = ".form-group";
            function checkForm(form) {
            if (!form.optinswitch.checked) {
                $(".form-group").addClass("error-border");
                $(
                "<div class='error-wrapper'><ul class='error-messages'><li class='error-message'>Please confirm that you want to opt in above to continue.</li></ul></div>"
                ).appendTo(".opt-in-acknowledgement");
                form.optinswitch.focus();
                return false;
            }
            return true;
            }
        </script> 
        <div id="OptIn3">         
            <main class="main-content">
                <section class="steps-container">
                    <div class="step step-1">
                        <i title="Completed: Step 1 of 3: DUO Everywhere Opt-In - Summary" class="fas fa-info"></i>
                        <div aria-hidden="true">Summary</div>
                    </div>
                    <hr class="hr-1" aria-hidden="true" >
                    <div class="step step-2">
                        <i title="Current: Step 2 of 3: DUO Everywhere Opt-In - DUO Setup" class="fas fa-shield-alt"></i>
                        <div aria-hidden="true">DUO Setup</div>
                    </div>
                    <hr class="hr-2" aria-hidden="true" >
                    <div class="step step-3">
                        <i title="Step 3 of 3: DUO Everywhere Opt-In - Enrolled" class="fas fa-check"></i>
                        <div aria-hidden="true">Enrolled</div>
                    </div>
                </section>
                <c:choose>
                    <c:when test = "${user.hasPhone and (not user.hasToken) and (not user.hasMobile)}">
                        <h2 class="form-header">Currently, you only have a landline phone registered with DUO.</h2>
                        <p>Your landline number, ${user.landlineNumbers}, is currently set as your only device on your DUO account.
                         Your registered device needs to be available whenever accessing Yale resources or conducting university business. 
                        Due to the access limitations of landline phones, which are non-portable, it is recommended to have additional authentication methods on your account.</p>
                        <p class="list-title">Recommended setup includes:</p>
                        <ul>
                            <li><p>Registering at least two devices</p></li>
                            <li><p>If applicable, setting up a smartphone as the primary authentication device</p></li>
                            <li><p>Alternative primary or secondary devices can include: </p>
                            <ul>
                                <li><p>An office landline</p></li>
                                <li><p>A tablet device </p></li>
                                <li><p>A backup code</p></li>
                            </ul></li>
                        </ul>
                        <h2 class="form-header">You can access your DUO device list below. </h2>
                        <p>DUO will need to confirm your identity. Please choose an authentication method below to begin. 
                            For help setting up your DUO account see <a target="_blank" href="https://yale.service-now.com/it/storage.admin@yale.edu?id=support_article&sys_id=13e804751bf28c90b0f9fee58d4bcbae">DUO Everywhere: Opting in and managing devices</a>.</p>
                    </c:when>                
                    <c:when test = "${(user.hasToken or user.hasMobile) and (user.totalDevices eq 1)}">
                        <h2 class="form-header">Yale strongly recommends having two devices registered to your DUO account.</h2>
                        <p>Currently you only have one device registered <c:if test="${not empty user.firstPhoneNumber}">(${user.firstPhoneNumber})</c:if>. 
                            Yale recommends adding a secondary device.</p>
                        <p class="list-title">Recommended setup includes:</p>
                        <ul>
                            <li><p>Registering at least two devices</p></li>
                            <li><p>If applicable, setting up a smartphone as the primary authentication device</p></li>
                            <li><p>Alternative primary or secondary devices can include: </p>
                            <ul>
                                <li><p>An office landline</p></li>
                                <li><p>A tablet device </p></li>
                                <li><p>A backup code</p></li>
                            </ul></li>
                        </ul>
                        <h2 class="form-header">You can access your DUO device list below.</h2>
                        <p>DUO will need to confirm your identity. Please choose an authentication method below to begin. 
                            For help setting up your DUO account see <a target="_blank" href="https://yale.service-now.com/it/storage.admin@yale.edu?id=support_article&sys_id=13e804751bf28c90b0f9fee58d4bcbae">DUO Everywhere: Opting in and managing devices</a>.</p>
                     </c:when>
                    <c:when test = "${(not user.hasPhone) and (not user.hasToken)}">
                        <h2 class="form-header">You do not have any devices set up in DUO. You must register at least one device to continue.</h2>
                        <p>The devices you register with DUO will receive a security prompt to authenticate your user session. 
                            These devices need to be available <strong>whenever</strong> accessing Yale resources or conducting university business.</p>
                            <p class="list-title">Recommended setup includes:</p>
                            <ul>
                                <li><p>Registering at least two devices</p></li>
                                <li><p>If applicable, setting up a smartphone as the primary authentication device</p></li>
                                <li><p>Alternative primary or secondary devices can include: </p>
                                <ul>
                                    <li><p>An office landline</p></li>
                                    <li><p>A tablet device </p></li>
                                    <li><p>A backup code</p></li>
                                </ul></li>
                            </ul>
                            <h2 class="form-header">You can manage your DUO device list below.</h2>
                            <p>For help setting up your DUO account see <a target="_blank" href="https://yale.service-now.com/it/storage.admin@yale.edu?id=support_article&sys_id=13e804751bf28c90b0f9fee58d4bcbae">DUO Everywhere: Opting in and managing devices</a>.</p>
                    </c:when>
                    <c:otherwise>
                        <h2 class="form-header">Please review your registered DUO devices to ensure that they are up to date.</h2>
                        <p>The devices you register with DUO will receive a security prompt to authenticate your user session.
                             At least one of these devices needs to be available <strong>whenever</strong> accessing Yale resources or conducting university business.</p>
                        <p class="list-title">Recommended setup includes:</p>
                        <ul>
                            <li><p>Registering at least two devices</p></li>
                            <li><p>If applicable, setting up a smartphone as the primary authentication device</p></li>
                            <li><p>Alternative primary or secondary devices can include: </p>
                            <ul>
                                <li><p>An office landline</p></li>
                                <li><p>A tablet device </p></li>
                                <li><p>A backup code</p></li>
                            </ul></li>
                        </ul>
                        <h2 class="form-header">You can access your DUO device list below. </h2>
                        <p>DUO will need to confirm your identity. Please choose an authentication method below to begin. 
                            For help setting up your DUO account see <a target="_blank" href="https://yale.service-now.com/it/storage.admin@yale.edu?id=support_article&sys_id=13e804751bf28c90b0f9fee58d4bcbae">DUO Everywhere: Opting in and managing devices</a>.</p>
                    </c:otherwise>
                </c:choose>
                <div class="well"><p>After DUO configuration, do <strong>NOT</strong> forget to check the acknowledgement box to continue!</p></div>                       
                <section class="duo-iframe-container">
                    <c:if test="${user.foundInDuo}">
                        <script src="${pageContext.request.contextPath}/assets/js/Duo-Web-v2.js"></script>
                        <script>Duo.init({'host': '${duoHost}', 'sig_request': '${sigRequest}',}); </script>
                        <iframe id="duo_iframe" title="DUO Setup for Enrollment" style="border: none; margin:auto;"></iframe>
                        <style>
                            #duo_iframe {
                                width: 100%;
                                min-width: 304px;
                                max-width: 620px;
                                height: 400px;
                                padding: 0 1.25rem;
                            }
                        </style>
                    </c:if>
                </section>
                <form:form name="Opt-In Acknowledgement" class="opt-in-acknowledgement" method="POST" action="${pageContext.request.contextPath}/optin/submit" onsubmit="return checkForm(this);">
                    <fieldset class="form-group ${(empty errorMsg)?'':'error-border'}">
                        <legend>Opt-In Acknowledgement</legend>
                            <div class="custom-control custom-checkbox">
                                <input type="hidden" name="ackLandline" value="true">
                                <input name="optinswitch" type="checkbox" class="custom-control-input" id="checkToAgree" aria-label="Checkbox to agree" value="true" ${user.foundInGroupEnrolled?'checked disabled':''}>
                                <label class="custom-control-label" for="checkToAgree">I acknowledge that I am opting in to DUO Everywhere.</label>
                            </div>

                            <div class="button-container">
                                <button type="submit" title="Continue" name="Continue" id="ContinueButton" class="btn btn-yale font-weight-bold mr-12">Continue</button>
                            </div>
                    </fieldset>
                    <c:if test="${not empty errorMsg}">
                        <div class="error-wrapper">
                            <ul class="error-messages">
                                <c:forEach items="${errorMsg}" var="em">
                                <li class="error-message">${em}</li>
                                </c:forEach>
                            </ul>
                        </div>
                        </c:if>
                </form:form>     
            </main>
        </div>
    </jsp:body>
</t:template>