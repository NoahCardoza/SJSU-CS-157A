<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--@elvariable id="user" type="com.example.demo.beans.entities.User"--%>
<%
    Boolean isLoggedIn = session.getAttribute("user_id") != null;
    request.setAttribute("isLoggedIn", isLoggedIn);

    String path = (String) request.getAttribute("jakarta.servlet.forward.servlet_path");
    if (path == null) {
        path = request.getServletPath();
    }
%>

<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">Hidden Gems</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link <%= path.startsWith("/index.jsp") ? "active" : "" %>" aria-current="page" href="index.jsp">Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= path.startsWith("/login") ? "active" : "" %>" href="<%= isLoggedIn ? "logout.jsp" : "login" %>"><%= isLoggedIn ? "Logout" : "Login" %></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= path.startsWith("/locations") ? "active" : "" %>" href="/locations">Locations</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= path.startsWith("/search") ? "active" : "" %>" href="/search">Search</a>
                </li>
                <c:if test="${user.administrator}">
                    <li class="nav-item">
                        <a class="nav-link <%= path.startsWith("/admin") ? "active" : "" %>" href="/admin">Admin</a>
                    </li>
                </c:if>
                <c:if test="${user.administrator || user.moderator}">
                    <li class="nav-item">
                        <a class="nav-link <%= path.startsWith("/moderator") ? "active" : "" %>" href="/moderation">Moderation</a>
                    </li>
                </c:if>
                <c:if test="${!isLoggedIn}">
                    <li class="nav-item">
                        <a class="nav-link <%= path.equals("/signup") ? "active" : "" %>" href="signup">Signup</a>
                    </li>
                </c:if>
            </ul>
        </div>
    </div>
</nav>