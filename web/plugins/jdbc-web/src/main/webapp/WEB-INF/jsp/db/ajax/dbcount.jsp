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
	include file="../../include/include.jspf"
%><div class="tab-header">
		<ui:headline2 key="countCompare">
		<div class="h2-actions">
<c:if test="${not empty model.connection2}"
>			:
			<a href="db/${model.connection2}/dbcatalogs.html">${model.connection2}</a> &raquo;
			<a href="db/${model.connection2}/dbschemas.html?catalog=${model.catalog2}"><ui:message text="${model.catalog2}" key="defaultCatalog"/></a> &raquo;
			<a href="db/${model.connection2}/dbobjects.html?catalog=${model.catalog2}&amp;schema=${model.schema2}"><ui:message text="${model.schema2}" key="defaultSchema"/></a>
</c:if
>			<span class="action" title="<fmt:message key="maximize"/>" onclick="return toggleElement('zoomable1');"><fmt:message key="maximizeIcon"/></span>
		</div>
		</ui:headline2>
		
		<form class="filter" action="#" method="get" onsubmit="return false;">
		<div class="filter">
			<input id="f-compact" type="checkbox" name="compact" onchange="return compactResult(this, 'countresult');"/><label for="f-compact"> <fmt:message key="compact"/></label>
		</div>
		</form>
		<hr/>
	</div>
	<div class="tab-body">
<c:choose><c:when test="${alreadyRunning != null}"
>		<p><fmt:message key="alreadyRunning"/></p>
		<p><ui:progress items="${progress}"/></p>
		<form action="#" method="get" onsubmit="return cancelTasks();">
			<input type="submit" value="<fmt:message key="cancel"/>"/>
		</form>
</c:when><c:when test="${cancelled != null}"
>		<p><fmt:message key="cancelledByUser"/></p>
</c:when><c:when test="${exception != null}"
>		<p><fmt:message key="dataError"/></p>
		
		<ui:headline2 key="errorDetails"/>
		
		<pre>${fn:escapeXml(exception.message)}</pre>
</c:when><c:otherwise
>		<table id="countresult" class="multiple table-autosort">
			<thead>
				<tr>
					<th class="table-sortable table-sortable:ignorecase"><fmt:message key="tableName"/></th>
					<th class="table-sortable table-sortable:numeric">${currentConnection.linkName}</th>
<c:if test="${not empty model.connection2}"
>					<th class="table-sortable table-sortable:numeric">${model.connection2}</th>
					<th><fmt:message key="action"/></th>
</c:if
>				</tr>
			</thead>
			<tbody>
<c:forEach items="${rowCounts}" var="c"
>				<tr>
					<td>${c.key}</td>
<c:choose><c:when test="${c.value.srcName != null}"
>					<td><a href="db/${currentConnection.linkName}/dbobject.html?catalog=${c.value.srcName.catalogName}&amp;schema=${c.value.srcName.schemaName}&amp;object=${c.value.srcName.objectName}" target="_blank">${c.value.srcCount}</a></td>
</c:when><c:otherwise
>					<td>&nbsp;</td>
</c:otherwise></c:choose
><c:if test="${not empty model.connection2}"><c:choose><c:when test="${c.value.dstName != null}"
>					<td<c:choose><c:when test="${c.value.srcCount < c.value.dstCount}"> class="diff-add"</c:when><c:when test="${c.value.srcCount > c.value.dstCount}"> class="diff-del"</c:when></c:choose>><a href="db/${model.connection2}/dbobject.html?catalog=${c.value.dstName.catalogName}&amp;schema=${c.value.dstName.schemaName}&amp;object=${c.value.dstName.objectName}" target="_blank">${c.value.dstCount}</a></td>
</c:when><c:otherwise
>					<td>&nbsp;</td>
</c:otherwise></c:choose
><c:choose><c:when test="${c.value.srcName != null && c.value.dstName != null}"
>					<td><a href="db/${currentConnection.linkName}/dml.html?catalog=${c.value.srcName.catalogName}&amp;schema=${c.value.srcName.schemaName}&amp;object=${c.value.srcName.objectName}&amp;connection2=${model.connection2}&amp;catalog2=${c.value.dstName.catalogName}&amp;schema2=${c.value.dstName.schemaName}" target="_blank"><fmt:message key="fullCompare"/></a></td>
</c:when><c:otherwise
>					<td>&nbsp;</td>
</c:otherwise></c:choose
></c:if
>				</tr>
</c:forEach
>			</tbody>
		</table>
</c:otherwise></c:choose
>	</div>
