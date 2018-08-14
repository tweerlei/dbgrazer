<%--
 * Copyright 2018 tweerlei Wruck + Buchmeier GbR - http://www.tweerlei.de/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
--%><%@
	tag description="Custom tag that generates a graph from a RowSet"
%><%@
	attribute name="rs" required="true" type="de.tweerlei.dbgrazer.query.model.RowSet" rtexprvalue="true" description="The RowSet"
%><%@
	attribute name="label" required="false" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="params" required="false" type="java.util.Map" rtexprvalue="true" description="The query parameters"
%><%@
	attribute name="paramString" required="false" type="java.lang.String" rtexprvalue="true" description="The query parameters encoded for URL usage"
%><%@
	attribute name="detailLink" required="false" type="java.lang.String" rtexprvalue="true" description="Optional detail link"
%><%@
	attribute name="tab" required="false" type="java.lang.Boolean" rtexprvalue="true" description="Whether to wrap the content in a tab-body tag"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%><c:choose><c:when test="${empty rs.attributes['imageId']}"
><div<c:if test="${tab}"> class="tab-body"</c:if>><strong><fmt:message key="noData"/></strong></div></c:when
><c:otherwise><div<c:if test="${tab}"> class="tab-header"</c:if>>
<c:if test="${not empty label}"
>			<div id="tools-${label}" class="hidden"><div class="menucolumn">
				<div class="menuitem"><span onclick="return postForm('dlform-${label}', event, 'db/${currentConnection.linkName}/graph-image.html', null, '_blank');"><fmt:message key="Image"/></span></div>
<c:if test="${currentUser.dotDisplayEnabled && rs.attributes['sourceText']}"
>				<div class="menuitem"><span onclick="return postForm('dlform-${label}', event, 'db/${currentConnection.linkName}/graph-source.html', null, '_blank');"><fmt:message key="DOT"/></span></div>
</c:if
>			</div></div>
</c:if
>
			<form id="dlform-${label}" class="hideable" action="db/${currentConnection.linkName}/graph-source.html" method="get">
				<input type="hidden" name="q" value="${rs.query.name}"/>
				<ui:hidden items="${params}" name="params"/>
<c:forEach items="${rs.attributes['optionNames']}" var="p"
>				<span class="menu" onclick="showDbMenu(event, 'graphtypes', { q: '${rs.query.name}', type: '${rs.query.type}', setting: '${p}', category: '${rs.attributes['optionCode']}' });"><fmt:message key="${p}"/></span>
</c:forEach
><c:if test="${not empty label}"
>				<span class="menu" onclick="return showElementMenu(event, 'tools-${label}');"><fmt:message key="download"/></span>
</c:if
>			</form><hr/>
		</div>
		<div<c:if test="${tab}"> class="tab-body"</c:if>>
<c:choose><c:when test="${not empty rs.attributes['imagemap']}"
>			${rs.attributes['imagemap']}
			<c:if test="${not empty detailLink}"><a href="${detailLink}"></c:if
				><img src="db/${currentConnection.linkName}/graph.html?q=${rs.query.name}&amp;key=${rs.attributes['imageId']}&amp;t=${currentDate.time}${paramString}" usemap="#${rs.attributes['imagemapId']}" class="scaled"<c:if test="${empty detailLink}"> onclick="return toggleScaling(this);"</c:if> alt="<fmt:message key="imageLoading"/>" onload="imageLoaded(event);" onerror="imageLoadingFailed(event);" title="${rs.query.name}"/><c:if test="${not empty detailLink}"
			></a></c:if>
</c:when><c:otherwise
>			<c:if test="${not empty detailLink}"><a href="${detailLink}"></c:if
				><img src="db/${currentConnection.linkName}/graph.html?q=${rs.query.name}&amp;key=${rs.attributes['imageId']}&amp;t=${currentDate.time}${paramString}" class="scaled"<c:if test="${empty detailLink}"> onclick="return toggleScaling(this);"</c:if> alt="<fmt:message key="imageLoading"/>" onload="imageLoaded(event);" onerror="imageLoadingFailed(event);" title="${rs.query.name}"/><c:if test="${not empty detailLink}"
			></a></c:if>
</c:otherwise></c:choose
>		</div>
		</c:otherwise></c:choose>