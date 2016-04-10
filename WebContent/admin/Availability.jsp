<%-- 
	Project: ClubHub Content and User Management System 
	Author(s): A. Dicks-Stephen, B. Lamaa, J. Thiessen
	Student Number: 100563954, 100911472, 100898311
	Date: March 11, 2016
	Description: Availibility.jsp
 --%>

<% request.setAttribute("thisPage", "Update Availability"); %>
<%@ page import="utilities.GameDao"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ include file="/WEB-INF/header_backend.jsp"%>
	<jsp:useBean id="now" class="java.util.Date"/>
		<fmt:formatDate value="${now}" pattern="yyyy-MM-dd" var="nowDate" />
<% GameDao slot = new GameDao();
slot.findOpenGameSlotsForUser(request);
slot.findAllOfUsersSlots(request);
%>


<form action="/clubhub/GameController" method="post" class="form"
	role="form">
	<c:if test="${!empty errorString}">
		<div class="alert alert-danger" role="alert">
			<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
			<span class="sr-only">Error:</span> ${errorString }
		</div>
	</c:if>
	<c:if test="${!empty successString}">
		<div class="alert alert-success" role="alert">
			<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
			<span class="sr-only">Success:</span> ${successString }
		</div>
	</c:if>
	<p>Games listed below are open for user registration; please select those that you are available to play. This information
		will be used by the league administrator to schedule games. When the schedule is created, you will be notified at the email address provided in your profile.
		
		<p>If a conflict arises, deselect the checkbox in question and this
		information will be provided to the administrator. Conflicts that occur after scheduling can be managed on their individual game pages.</p>
	<p><label><input type="checkbox" name="selectall" onClick="toggleBox(this)"/> Select all</label></p>
	<!--   -->
	<h4>Past Slots</h4>
	<c:forEach items="${slots}" var="slot">
		<c:if test="${slot.scheduledDateWithYear le nowDate}">
			<input type="checkbox" name="slots" value="${slot.id}"
				<c:forEach items="${user.slotid}" var="uslot">
					<c:if test="${(slot.id == uslot) && (slot.conflict == 0) }"> checked</c:if>
				</c:forEach>>   ${slot.scheduledDate} | ${slot.dayOfWeek}s at ${slot.time} | ${slot.gender} ${slot.seasonName } ${slot.year}  <br>
		</c:if>
	</c:forEach>
	<h4>Future Slots</h4>
	<c:forEach items="${slots}" var="slot">
		<c:if test="${slot.scheduledDateWithYear ge nowDate}">
			<input type="checkbox" name="slots" value="${slot.id}"
				<c:forEach items="${user.slotid}" var="uslot">
					<c:if test="${(slot.id == uslot) && (slot.conflict == 0) }"> checked</c:if>
				</c:forEach>>   ${slot.scheduledDate} | ${slot.dayOfWeek}s at ${slot.time} | ${slot.gender} ${slot.seasonName } ${slot.year}  <br>
		</c:if>
	</c:forEach>
	<button class="btn btn-primary" type="submit" value="availability"
		name="option">Submit</button>
</form>




<%@ include file="/WEB-INF/footer_backend.jsp"%>