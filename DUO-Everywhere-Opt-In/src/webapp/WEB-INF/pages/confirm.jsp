<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<t:template>
    <jsp:body>
        <div id="OptInSuccess">
            <main class="main-content">
                <section class="steps-container">
                    <div class="step step-1">
                        <i title="Completed: Step 1 of 3: DUO Everywhere Opt-In - Summary" class="fas fa-info"></i>
                        <div aria-hidden="true">Summary</div>
                    </div>
                    <hr class="hr-1" aria-hidden="true" >
                    <div class="step step-2">
                        <i title="Completed: Step 2 of 3: DUO Everywhere Opt-In - DUO Setup" class="fas fa-shield-alt"></i>
                        <div aria-hidden="true">DUO Setup</div>
                    </div>
                    <hr class="hr-2" aria-hidden="true" >
                    <div class="step step-3">
                        <i title="Current: Step 3 of 3: DUO Everywhere Opt-In - Enrolled" class="fas fa-check"></i>
                        <div aria-hidden="true">Enrolled</div>
                    </div>
                </section>
                <h2 class="form-header">You have successfully opted into Duo Everywhere! Thank you for supporting a more secure Yale.</h2>
                <p>For questions, please contact the <a target="_blank" href="https://yale.service-now.com/it?id=get_help">ITS Help Desk</a>.</p>
 
                <form:form name="Opt-In Logout" class="opt-in-logout" action="${pageContext.request.contextPath}/logout/cas" method="get">
                    <div class="form-group">
                        <div class="button-container">
                            <button type="submit" title="Logout" name="Logout" class="btn btn-yale font-weight-bold mr-12">Logout</button>
                        </div>
                    </div>
                </form:form>
            </main>
        </div>     
    </jsp:body>
</t:template>