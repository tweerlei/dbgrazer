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
	include file="../include/include.jspf"
%><c:set var="pageTitle" value="${object}"/><%@
	include file="../include/header.jspf"
%>
	<script type="text/javascript">/*<![CDATA[*/
	
	function runInitialQuery() {
		var where = HashMonitor.get('where');
		var order = HashMonitor.get('order');
		if (where) {
			showDBObjectData(null, '${catalog}', '${schema}', '${object}', where, order);
		}
	}
	
	function submitForm(frm, p) {
		$('resulttype').value = p;
		getFormInto(frm, 'result');
		return false;
	}
	
	function submitDownloadForm(event, format) {
		return postForm('submitform', event, 'db/${currentConnection.linkName}/submit-simple-export.html', { resultformat: format }, '_blank');
	}
	
	function confirmSubmit(frm, p, title, content) {
		return showJSConfirmDialog(title, content, function() {
			return submitForm(frm, p);
		});
	}
	
	/*]]>*/</script>
	
	<ui:headline1><jsp:attribute name="content"><a href="db/${currentConnection.linkName}/dbcatalogs.html"><fmt:message key="schemaBrowser"/></a>
		&raquo; <a href="db/${currentConnection.linkName}/dbschemas.html?catalog=${catalog}"><ui:message text="${catalog}" key="defaultCatalog"/></a>
		&raquo; <a href="db/${currentConnection.linkName}/dbobjects.html?catalog=${catalog}&amp;schema=${schema}"><ui:message text="${schema}" key="defaultSchema"/></a>
		&raquo; ${pageTitle}
	</jsp:attribute><jsp:body>
	<form id="export-form" class="h1-actions" action="#" method="get" onsubmit="return false;">
		<input type="hidden" name="catalog" value="${catalog}"/>
		<input type="hidden" name="schema" value="${schema}"/>
		<input type="hidden" name="object" value="${object}"/>
		<input id="exportfmt" type="hidden" name="format" value=""/>
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</form>
	<div class="h1-actions">
		<span class="menu" onclick="showDbMenu(event, 'metalinks');"><fmt:message key="download"/></span>
	</div>
	<div class="h1-actions">
		<a class="action" title="<fmt:message key="newWindow"/>" href="db/${currentConnection.linkName}/dbobject.html?catalog=${catalog}&amp;schema=${schema}&amp;object=${object}" target="_blank">&#x2750;</a>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
<c:if test="${currentConnection.editorEnabled}"
>		<form id="toolsform-1" class="hidden" action="db/${currentConnection.linkName}/edit.html" method="post" target="_blank">
			<input type="hidden" name="type" value="MULTIPLE"/>
			<input type="hidden" name="statement" value="${fn:escapeXml(statement)}"/>
		</form>
		<div class="menuitem"><span onclick="return postForm('toolsform-1', event);"><fmt:message key="newQuery"/></span></div>
		<div class="menuitem"><span onclick="return postForm('toolsform-1', event, 'db/${currentConnection.linkName}/submit-JDBC.html');"><fmt:message key="sqlQuery"/></span></div>
</c:if><c:if test="${currentConnection.designerEnabled}"
>		<div class="menuitem"><a href="db/${currentConnection.linkName}/dbdesigner-start.html?catalog=${catalog}&amp;schema=${schema}&amp;object=${object}"><fmt:message key="designer"/></a></div>
</c:if><c:if test="${currentConnection.editorEnabled || currentConnection.designerEnabled}"
>		<hr class="menuseparator"/>
</c:if
>		<div class="menuitem"><a href="db/${currentConnection.linkName}/dbcompare2.html?catalog=${catalog}&amp;schema=${schema}&amp;object=${object}" target="_blank"><fmt:message key="structureCompare"/></a></div>
		<div class="menuitem"><a href="db/${currentConnection.linkName}/dbcount2.html?catalog=${catalog}&amp;schema=${schema}&amp;object=${object}" target="_blank"><fmt:message key="countCompare"/></a></div>
		<div class="menuitem"><a href="db/${currentConnection.linkName}/dml.html?catalog=${catalog}&amp;schema=${schema}&amp;object=${object}" target="_blank"><fmt:message key="fullCompare"/></a></div>
		<hr class="menuseparator"/>
		<div class="menuitem"><a href="db/${currentConnection.linkName}/dbobject-upload.html?catalog=${catalog}&amp;schema=${schema}&amp;object=${object}" target="_blank"><fmt:message key="uploadData"/></a></div>
	</div></div>
	</jsp:body></ui:headline1>
	
	<ui:tabs items="${tabs}" var="ix" varKey="label" varLink="detailLink" varParams="detailParams" varParamString="detailParamString" name="result"><%@
		include file="result/dbobject.jspf"
	%></ui:tabs>
<%@
	include file="../include/footer.jspf"
%>
