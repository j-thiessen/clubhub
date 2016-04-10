<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="row">
	<div class="col-xs-12 col-sm-5 col-sm-offset-1">
		<h3>Upcoming Games</h3>
		<%@ include file="/WEB-INF/dashboardUserListUpcomingGames.jsp"%>
	</div>

	<div class="col-xs-12 col-sm-5 col-sm-offset-1">
		<h3>Recent Games</h3>
		<%@ include file="/WEB-INF/dashboardUserListRecentGames.jsp"%>
	</div>
</div>