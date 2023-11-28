<%--@elvariable id="disableLocationSelect" type="java.lang.Boolean"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri="/WEB-INF/custom-functions.tld" prefix="cfn" %>
<%@ taglib prefix="hg" tagdir="/WEB-INF/tags" %>

<%--@elvariable id="form" type="List<com.example.demo.beans.forms.AmenityForm>"--%>
<%--@elvariable id="locations" type="List<com.example.demo.daos.LocationDao>"--%>
<%--@elvariable id="amenityTypeAttributes" type="java.util.List<com.example.demo.beans.entities.AmenityTypeAttribute>"--%>

<!DOCTYPE html>
<html>
    <head>
        <title>LHG | Amenity | Create</title>
        <%@include file="../../includes/head.jsp" %>
    </head>
    <body>
        <%@include file="../../includes/nav.jsp" %>
        <div class="container mt-5">
            <h1>Create Amenity</h1>

           <form method="POST" class="mt-5" id="new-location-form">
               <div class="row">
                   <%@include file="../../includes/alerts.jsp" %>
               </div>
                <div class="row">
                    <div class="col mb-3 d-flex justify-content-between align-items-center">
                        <div>
                            Location:
                            <b>
                                <c:choose>
                                    <c:when test="${empty form.locationName}">
                                        None
                                    </c:when>
                                    <c:otherwise>
                                        ${form.locationName}
                                    </c:otherwise>
                                </c:choose>
                            </b>
                        </div>
                        <button
                                class="btn btn-primary float-end"
                                ${disableLocationSelect ? 'disabled' : ''}
                                onclick="$('#new-location-form').attr('action', '${param['id'] ? '/amenities?f=locationSelect&id=' + param['id'] : '/amenities?f=locationSelect'}').submit()"
                        >Select Location</button>
                    </div>
                </div>

                <div class="row">
                    <div class="col mb-3 d-flex justify-content-between align-items-center">
                        <div>
                            Amenity Type:
                            <b>
                                <c:choose>
                                    <c:when test="${empty form.typeName}">
                                        None
                                    </c:when>
                                    <c:otherwise>
                                        ${form.typeName}
                                    </c:otherwise>
                                </c:choose>
                            </b>
                        </div>
                        <button
                                class="btn btn-primary float-end"
                                onclick="$('#new-location-form').attr('action', '${param['id'] ? '/amenities?f=typeSelect&id=' + param['id'] : '/amenities?f=typeSelect'}').submit()"
                        >Select Amenity Type</button>
                    </div>
                </div>

                <input type="hidden" name="locationId" id="locationId" value="${form.locationId}">
                <input type="hidden" name="locationName" id="locationName" value="${form.locationName}">
                <input type="hidden" name="typeId" id="typeId" value="${form.typeId}">
                <input type="hidden" name="typeName" id="typeName" value="${form.typeName}">
                <input type="hidden" name="amenityTypeAttributes" id="amenityTypeAttributes" value="${form.attributes}">

               <c:if test="${amenityTypeAttributes.size() > 0}">
                   <p class="fw-bold">Attributes</p>
                   <div class="row">
                       <c:forEach var="amenityTypeAttribute" items="${amenityTypeAttributes}">
                           <div class="form-group col-12 col-sm-6 col-md-4 g-2">
                               <div class="form-group">
                                   <label for="amenityTypeAttribute-${amenityTypeAttribute.id}">${amenityTypeAttribute.name}</label>
                                   <c:choose>
                                       <c:when test="${amenityTypeAttribute.type == 'text'}">
                                           <input required type="text" id="amenityTypeAttribute-${amenityTypeAttribute.id}" name="amenityTypeAttribute-${amenityTypeAttribute.id}" class="form-control mb-3" placeholder="${amenityTypeAttribute.generatePlaceholderText()}" />
                                       </c:when>
                                       <c:when test="${amenityTypeAttribute.type == 'number'}">
                                           <input required type="number" id="amenityTypeAttribute-${amenityTypeAttribute.id}" name="amenityTypeAttribute-${amenityTypeAttribute.id}" class="form-control mb-3" placeholder="${amenityTypeAttribute.generatePlaceholderText()}" />
                                       </c:when>
                                       <c:when test="${amenityTypeAttribute.type == 'boolean'}">
                                           <select required id="amenityTypeAttribute-${amenityTypeAttribute.id}" name="amenityTypeAttribute-${amenityTypeAttribute.id}" class="form-control form-select mb-3">
                                               <option value="" style="display:none;">${amenityTypeAttribute.generatePlaceholderText()}</option>
                                               <option value="1">Yes</option>
                                               <option value="0">No</option>
                                           </select>
                                       </c:when>
                                   </c:choose>
                               </div>
                           </div>
                       </c:forEach>
                   </div>
               </c:if>


                <label for="name" class="form-label">Name</label>
                <input type="text" id="name" name="name" value="${form.name}" class="form-control mb-3" placeholder="Name" />
                <label for="description" class="form-label">Description</label>
                <textarea id="description" name="description" class="form-control mb-3" placeholder="Description">${form.description}</textarea>
                <input type="hidden" name="redirect" value="${pathWithQueryString}">
                <button type="submit" class="btn btn-primary w-100" name="action" value="submit">Create</button>
        </div>
    </body>
</html>
