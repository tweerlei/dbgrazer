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
%><fmt:message key="config" var="pageTitle"/><%@
	include file="include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}">
	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
	<div class="h1-actions">
		<span class="menu" onclick="return showTopMenu(event, 'confighistory');"><fmt:message key="changeLog"/></span>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
		<div class="menuitem"><span onclick="return showConfirmDialog('<fmt:message key="reloadConfig"/>', '<fmt:message key="reloadConfigText"/>', 'reload-config.html');"><fmt:message key="reloadConfig"/></span></div>
	</div></div>
	</ui:headline1>
	
	<div class="tab-page"><div class="tab-header">
		<ui:filter id="filter-table-result0" target="table-result0" form="true"/>
		<hr/>
	</div><div class="tab-body">
		<table id="table-result0" class="single">
			<thead>
				<tr>
					<th><fmt:message key="name"/></th>
					<th><fmt:message key="description"/></th>
<c:if test="${currentUser.configEditorEnabled}"
>					<th><fmt:message key="action"/></th>
</c:if
>					<th><fmt:message key="value"/></th>
				</tr>
			</thead>
			<tbody>
<c:forEach items="${allSettings}" var="c"
>				<tr>
					<td>${fn:escapeXml(c.key)}</td>
					<td><fmt:message key="help_${c.key}"/></td>
<c:if test="${currentUser.configEditorEnabled}"
>					<td><span class="action" title="<fmt:message key="change"/>" onclick="showDialog(event, 'config', {q: '${fn:escapeXml(c.key)}'}, '${fn:escapeXml(c.key)}');"><fmt:message key="editIcon"/></span><c:if test="${settings[c.key] != null}"
>						<span class="action" title="<fmt:message key="remove"/>" onclick="showConfirmDialog('${fn:escapeXml(c.key)}', '<fmt:message key="deleteConfigText"/>', 'unset-config.html', { q: '${fn:escapeXml(c.key)}' });"><fmt:message key="removeIcon"/></span></c:if
>						</td>
</c:if
>					<td><c:choose
						><c:when test="${(settings[c.key] != null) && (settings[c.key] != c.value)}"><em>${fn:escapeXml(settings[c.key])}</em></c:when
						><c:when test="${c.value == null}"><fmt:message key="null"/></c:when
						><c:otherwise>${fn:escapeXml(c.value)}</c:otherwise
						></c:choose></td>
				</tr>
</c:forEach
>			</tbody>
		</table>
	</div></div>
<%@
	include file="include/footer.jspf"
%>
