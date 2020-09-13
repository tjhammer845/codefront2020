<%@tag description="AMI Page template" pageEncoding="UTF-8"%>
<%@attribute name="headTitle" %>
<%@attribute name="headJavascript" %>
<%@attribute name="bodyJavascript" %>
<%@attribute name="bodyTitleLeft" %>
<%@attribute name="bodyTitleRight" %>
<%@attribute name="navBarLeft" fragment="true" %>
<%@attribute name="navBarRight" fragment="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>DUO Everywhere Opt-In | Yale University</title>
    
    <!-- Yale Favicon -->
    <link rel="shortcut icon" type="image/x-icon" href="assets/images/favicon.ico"/>

    <!-- Yale Styles -->
    <link rel="stylesheet" href="assets/css/yale-ui.min.css">
    <link rel="stylesheet" href="assets/css/base.min.css">

    <!-- FontAwesome -->
    <script src="https://kit.fontawesome.com/d66d59b84c.js" crossorigin="anonymous"></script>

    <!-- JQuery -->
    <script src="https://code.jquery.com/jquery-1.9.1.min.js"></script>
</head>
<body>
    <a class="btn btn-primary" id="BackToTop"></a>
    <section class="main-section">
        <header>
            <h1>DUO Everywhere <span class="text-nowrap">Opt-In</span></h1>
            <nav class="nav-bar">
                <ul class="nav left-nav" aria-label="left-nav">
                	<c:if test="${not empty user}">
                    <li class="nav-item">
                        <a class="nav-link" target="_blank" href="https://veritas.its.yale.edu/netid/" aria-current="page">Manage NetID (${user.username})</a>
                    </li>
                    </c:if>
                </ul>
                <ul class="nav right-nav" aria-label="right-nav">
                    <li class="nav-item">
                        <a class="nav-link" target="_blank" href="https://usability.yale.edu/web-accessibility/accessibility-yale" aria-current="page">Accessibility</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" target="_blank" href="https://yale.service-now.com/it?id=support_article&sys_id=84a416f31b0f04108024da83cd4bcbd1" aria-current="page">Help</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/logout/cas" aria-current="page">Logout</a>
                    </li>
                </ul>
            </nav>
        </header>

		<jsp:doBody/>

        <footer>
            <div class="footer-content">
                <img class="footer-image" src="assets/images/yale-logo-sprite.svg" alt="Yale University" />
                <div class="footer_legal_copy">Copyright &copy; 2020 Yale University<br>
                    All Rights Reserved</div>
            </div>
        </footer>
    </section>

    <!-- Imported Scripts -->
    <script src="assets/js/back-to-top.min.js"></script>
    <script src="assets/js/yale-ui-scripts.js"></script>   
</body>
</html>

