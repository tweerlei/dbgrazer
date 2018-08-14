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
	include file="include/include.jspf"
%><fmt:message key="stats" var="pageTitle"/><%@
	include file="include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}"/>
	
	<div class="tab-page"><div class="tab-body">
		<div class="column2">
			<fmt:message key="activeConnectionsTab" var="title"><fmt:param value="${fn:length(cstats)}"/></fmt:message><ui:panel title="${title}" zoomable="true">
				<table class="single">
					<thead>
						<tr>
							<th><fmt:message key="connection"/></th>
							<th><fmt:message key="schemaName"/></th>
							<th><fmt:message key="schemaVersion"/></th>
							<th><fmt:message key="activeSessions"/></th>
							<th><fmt:message key="dbCache"/></th>
						</tr>
					</thead>
					<tbody>
<c:forEach items="${cstats}" var="c"
>						<tr>
							<td>${fn:escapeXml(c.key)}</td>
							<td>${c.value.schema.name}</td>
							<td>${c.value.schema.version}</td>
							<td>${c.value.sessionCount}</td>
							<td>${c.value.cacheSize}</td>
						</tr>
</c:forEach
>					</tbody>
				</table>
			</ui:panel>
			<fmt:message key="activeSchemasTab" var="title"><fmt:param value="${fn:length(qstats)}"/></fmt:message><ui:panel title="${title}" zoomable="true">
				<table class="single">
					<thead>
						<tr>
							<th><fmt:message key="schemaName"/></th>
							<th><fmt:message key="schemaVersion"/></th>
							<th><fmt:message key="loadedQueries"/></th>
						</tr>
					</thead>
					<tbody>
<c:forEach items="${qstats}" var="c"
>						<tr>
							<td>${fn:escapeXml(c.key.name)}</td>
							<td>${fn:escapeXml(c.key.version)}</td>
							<td>${c.value}</td>
						</tr>
</c:forEach
>					</tbody>
				</table>
			</ui:panel>
			<fmt:message key="activeUsersTab" var="title"><fmt:param value="${fn:length(ustats)}"/></fmt:message><ui:panel title="${title}" zoomable="true">
				<table class="single">
					<thead>
						<tr>
							<th><fmt:message key="username"/></th>
							<th><fmt:message key="creationTime"/></th>
							<th><fmt:message key="loginTime"/></th>
							<th><fmt:message key="lastRequestTime"/></th>
						</tr>
					</thead>
					<tbody>
<c:forEach items="${ustats}" var="c"
>						<tr>
							<td>${fn:escapeXml(c.username)}</td>
							<td><fmt:formatDate value="${c.creationTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
							<td><fmt:formatDate value="${c.loginTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
							<td><fmt:formatDate value="${c.lastRequestTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
						</tr>
</c:forEach
>					</tbody>
				</table>
			</ui:panel>
		</div>
		<div class="column2">
			<fmt:message key="modulesTab" var="title"><fmt:param value="${fn:length(libs)}"/></fmt:message><ui:panel title="${title}" zoomable="true">
				<table class="single">
					<thead>
						<tr>
							<th><fmt:message key="module"/></th>
							<th><fmt:message key="moduleVersion"/></th>
						</tr>
					</thead>
					<tbody>
<c:forEach items="${libs}" var="c"
>						<tr>
							<td>${c.key}</td>
							<td>${c.value}</td>
						</tr>
</c:forEach
>					</tbody>
				</table>
			</ui:panel>
		</div>
		<hr/>
	</div></div>
<%@
	include file="include/footer.jspf"
%>
