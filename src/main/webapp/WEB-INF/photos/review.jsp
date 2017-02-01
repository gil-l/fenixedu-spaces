<%--

    Copyright © 2014 Instituto Superior Técnico

    This file is part of FenixEdu Spaces.

    FenixEdu Spaces is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu Spaces is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu Spaces.  If not, see <http://www.gnu.org/licenses/>.

--%>
<!DOCTYPE html> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:url var="viewUrl" value="/spaces-view/view/${space.externalId}" />
<div class="page-header">
  	<h1>
  		<spring:message code="title.photo.review"/>
  		<br><small><a href="${viewUrl}"><c:out value="${space.presentationName}"/></a></small>
  	</h1>
</div>	

<c:choose>
    <c:when test="${empty space.parent}">
    	<spring:url var="topLevel" value="/spaces" />
        <p><a href="${topLevel}">&laquo; <spring:message code="button.back" /></a></p>
    </c:when>
    <c:otherwise>
    	<spring:url var="parentSpace" value="/spaces-view/view/${space.parent.externalId}" />
        <p><a href="${parentSpace}">&laquo; <spring:message code="button.back" /></a></p>
    </c:otherwise>
</c:choose>
	
<c:if test="${not empty submissions.pageList}">
  	<spring:url var="pageUrl" value="/spaces/photos/review/${space.externalId}" />
	<ul class="pagination">
		<li><a href="${searchPageUrl}?p=f">&laquo;</a></li>
		<c:forEach var="page" begin="${submissions.firstLinkedPage}" end="${submissions.lastLinkedPage}">
			<c:set var="pageNumber" value="${page+1}"/>
			<c:if test="${page == submissions.page}">
				<li class="active"><a href="${searchPageUrl}?p=${pageNumber}">${pageNumber}</a></li>
			</c:if>
			<c:if test="${page != submissions.page}">
				<li><a href="${searchPageUrl}?p=${pageNumber}">${pageNumber}</a></li>
			</c:if>
		</c:forEach>
		<li><a href="${searchPageUrl}?p=l">&raquo;</a></li>
</ul>
	<table class="table">
		<thead>
			<th><spring:message code="label.space" /></th>
			<th><spring:message code="label.photo" /></th>
			<th><spring:message code="label.photo.user" /></th>
			<th><spring:message code="label.photo.date" /></th>
			<th></th>
		</thead>
		<tbody>
			<c:forEach var="photoSubmission" items="${submissions.pageList}">
			<c:set var="photo" value="${photoSubmission.photo}" />
			<c:set var="subject" value="${photoSubmission.submitor}" />
			<c:set var="date" value="${photoSubmission.created}" />
  			<spring:url var="spacePhotoUrl" value="/spaces-view/photo/${photo.externalId}" />
  			<spring:url var="spaceUrl" value="/spaces-view/view/${photoSubmission.spacePending.externalId}" />
  			<spring:url var="formUrl" value="/spaces/photos/${photoSubmission.externalId}" />
			<tr>
				<td>
					<a href="${spaceUrl}"><c:out value="${photoSubmission.spacePending.presentationName}"/></a>
				</td>
				<td>
					<img src="${spacePhotoUrl}" class="img-responsive" style="max-height: 450px;"/>
				</td>
				<td>
					<a href="mailto:${subject.email}"><c:out value="${subject.name}"/></a> (<c:out value="${subject.username}"/>)
				</td>
				<td><c:out value="${date.toString('dd/MM/yyyy hh:mm')}"/></td>
				<td>
					<form id="form${photoSubmission.externalId}" role="form" class="accept" action="${formUrl}/accept" method="POST">
							${csrf.field()}
						<input type="hidden" name="space" value="${space.externalId}">
						<input type="hidden" name="page" value="${page}">
						<div class="container-fluid">
							<div class="form-group">
								<label class="radio-inline">
									<input type="radio" onclick="$('#rejectMessage${photoSubmission.externalId}').addClass('hidden'); $('#rejectMessage${photoSubmission.externalId}').prop('required', false); $('#form${photoSubmission.externalId}').attr('action', '${formUrl}/accept');" name="decision" required><spring:message code="label.photo.accept" />
								</label>

								<label class="radio-inline">
									<input type="radio" onclick="$('#rejectMessage${photoSubmission.externalId}').removeClass('hidden'); $('#rejectMessage${photoSubmission.externalId}').prop('required', true); $('#form${photoSubmission.externalId}').attr('action', '${formUrl}/reject');" name="decision" required><spring:message code="label.photo.reject" />
								</label>
							</div>

							<div class="row form-group">
								<div class="col-md-12">
									<input id="rejectMessage${photoSubmission.externalId}" type="text" class="form-control input hidden" name="rejectMessage" required placeholder="<spring:message code='label.photo.rejection.motive' />">
								</div>	
							</div>

							<div class="row form-group">
								<div class="col-md-2">
									<button type="submit" class="btn btn-primary"><spring:message code="label.submit" /></button>
								</div>
							</div>
						</div> 
					</form>
				</td>
			</tr>
		</c:forEach>
	</tbody>
	</table>
</c:if>

<c:if test="${empty submissions.pageList}">
	<div id="new-info-panel" class="infoop2">
            <p class="mvert0"><em><spring:message code="space.photo.none"></spring:message></em></p>
    </div>
</c:if>

