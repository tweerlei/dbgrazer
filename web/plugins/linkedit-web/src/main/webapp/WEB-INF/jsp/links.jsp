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
%><fmt:message key="connections" var="pageTitle"/><%@
	include file="include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}">
	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
		<div class="menuitem"><a href="link.html"><fmt:message key="newConnection"/></a></div>
		<hr class="menuseparator"/>
		<div class="menuitem"><span onclick="return showConfirmDialog('<fmt:message key="reloadConnections"/>', '<fmt:message key="reloadConnectionsText"/>', 'reload-connections.html');"><fmt:message key="reloadConnections"/></span></div>
		<ui:extensions items="${extensions}" separatorBefore="true"/>
	</div></div>
	</ui:headline1>
	
	<fmt:message key="rename" var="editTitle"/><ui:tabs items="${links}" var="l" varKey="label" varLink="detailLink" varParams="detailParams" varParamString="detailParamString" name="result"
			editTitle="${editTitle}" editActionTemplate="return showTopDialog(event, 'schema', { q: '%%' }, '${editTitle}');">
		<div class="tab-body">
			<table class="single">
				<thead>
					<tr>
						<th><fmt:message key="name"/></th>
						<th><fmt:message key="connectionSet"/></th>
						<th><fmt:message key="description"/></th>
						<th><fmt:message key="username"/></th>
						<th><fmt:message key="connectionGroup"/></th>
						<th><fmt:message key="connectionType"/></th>
						<th><fmt:message key="sqlDialect"/></th>
						<th><fmt:message key="writable"/></th>
						<th><fmt:message key="querySets"/></th>
					</tr>
				</thead>
				<tbody>
<c:forEach items="${l}" var="c"
>					<tr>
						<td><a href="link.html?q=${c.name}">${c.name}</a></td>
						<td>${fn:escapeXml(c.setName)}</td>
						<td>${fn:escapeXml(c.description)}</td>
						<td>${c.username}</td>
						<td>${c.groupName}</td>
						<td>${c.type}</td>
						<td>${c.dialectName}</td>
						<td><fmt:message key="${c.writable}"/></td>
						<td><c:forEach items="${c.querySetNames}" var="n" varStatus="st"><c:if test="${!st.first}">, </c:if>${n}</c:forEach></td>
					</tr>
</c:forEach
>				</tbody>
			</table>
		</div>
	</ui:tabs>
<%@
	include file="include/footer.jspf"
%>
