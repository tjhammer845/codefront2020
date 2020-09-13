<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<t:template>
    <jsp:body>
        <div id="OptInFail">
            <main class="main-content">
                <c:if test="${0 eq sorryType}"><!-- Try Again -->
                    <h2 class="form-header">Please try again later</h2>
                    <p>${sorryMsg}</p>
                </c:if>  
                <c:if test="${1 eq sorryType}"><!-- Already enrolled -->
                    <h2 class="form-header">You have already opted in to DUO Everywhere.</h2>
                    <p>Thank you for supporting a more secure Yale. </p>                        
                    <p>If you have questions, please contact the <a target="_blank" href="https://yale.service-now.com/it?id=get_help">ITS Help Desk</a>.</p>
                </c:if>            
                <c:if test="${2 eq sorryType}"><!-- Not eligible -->
                    <h2 class="form-header">You are not currently eligible for DUO Everywhere opt-in.</h2>
                    <p>If you have received direct communication to opt in to DUO Everywhere, please contact the <a target="_blank" href="https://yale.service-now.com/it?id=get_help">ITS Help Desk</a>.</p>
                    <p>Thank you for supporting a more secure Yale.</p>
                </c:if>
                <c:if test="${3 eq sorryType}"><!-- Not known to Duo/pre sync -->
                    <h2 class="form-header">DUO was unable to locate your NetID.</h2>
                    <p>At this time we are unable to match your Yale NetID with a DUO account. If you have recently received your NetID, please try again in 24 hours.</p>
                    <p>If this error persists, please contact the <a target="_blank" href="https://yale.service-now.com/it?id=get_help">ITS Help Desk</a>.</p>
                    <p>Thank you for supporting a more secure Yale.</p>
                </c:if>
                <form:form name="Opt-In Logout" class="opt-in-logout" action="${pageContext.request.contextPath}/logout/cas" method="get">
                    <fieldset class="form-group">
                            <div class="button-container">
                                <button type="submit" title="Logout" name="Logout" class="btn btn-yale font-weight-bold mr-12">Logout</button>
                            </div>
                    </fieldset>
                </form:form>
            </main>
        </div>  
    </jsp:body>
</t:template>