<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Hidden Gems</title>
        <%@include file="includes/head.jsp" %>
    </head>
    <body>
        <%@include file="includes/nav.jsp" %>

        <div class="container mt-5">
            <%@include file="./includes/alerts.jsp" %>
            <div class="d-flex flex-column flex-md-row align-items-center justify-content-center">
                <div class="mb-4 me-md-4 mb-md-0">
                    <img src="<c:url value="/img/logo-lg.png"/>" alt="Hidden Gems" style="height: 300px;">
                </div>
                <div class="">
                    <h2>
                        Welcome to our platform
                    </h2>
                    <p>
                        Where community collaboration meets the power of location-based knowledge
                        sharing.
                    </p>
                    <p>
                        We believe that local insights should be accessible to all, and that's why
                        we've created a space for people to report, review, and vote on locations and their amenities.
                        Think of it as a geo-based wiki, driven by your experiences and expertise. Whether you want to share
                        the hidden gems in your neighborhood, rave about a fantastic new park, or help fellow travelers discover
                        the best rest stops along a highway, this is the place to do it. Together, we're building a valuable
                        resource that empowers everyone to explore, connect, and make informed decisions about the places we
                        love. Join us in creating a global community of location enthusiasts, and let's map the world, one
                        review at a time.
                    </p>
                    <a href="<c:url value="/locations?f=map" />" class="btn btn-primary float-end">View Map</a>
                </div>
            </div>
            <c:if test="${not empty user}">
                <p>
                    Welcome, ${user.email}!
                </p>
            </c:if>
        </div>
    </body>
</html>