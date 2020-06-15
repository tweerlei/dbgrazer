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
%><ui:set text="${schema}" key="defaultSchema" var="pageTitle"/><%@
	include file="../include/header.jspf"
%><c:set var="targetElement" value="explorer-right"
/>
	<script type="text/javascript">/*<![CDATA[*/
	
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
	
	function reloadPage() {
		return refreshDBObject();
	}
	
	/*]]>*/</script>
	
	<ui:headline1><jsp:attribute name="content"><a href="db/${currentConnection.linkName}/dbcatalogs.html"><fmt:message key="schemaBrowser"/></a>
		&raquo; <a href="db/${currentConnection.linkName}/dbschemas.html?catalog=${catalog}"><ui:message text="${catalog}" key="defaultCatalog"/></a>
		&raquo; ${pageTitle}
	</jsp:attribute><jsp:body>
	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
	<div class="h1-actions">
		<span class="menu" onclick="showDbMenu(event, 'schemalinks');"><fmt:message key="download"/></span>
	</div>
	<div class="h1-actions">
		<span class="action" title="<fmt:message key="refresh"/>" onclick="return reloadPage();"><fmt:message key="refreshIcon"/></span>
		<a class="action" title="<fmt:message key="newWindow"/>" href="db/${currentConnection.linkName}/dbobjects.html?catalog=${catalog}&amp;schema=${schema}" target="_blank"><fmt:message key="newWindowIcon"/></a>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
		<div class="menuitem"><a href="db/${currentConnection.linkName}/dbcompare.html?catalog=${catalog}&amp;schema=${schema}" target="_blank"><fmt:message key="structureCompare"/></a></div>
		<div class="menuitem"><a href="db/${currentConnection.linkName}/dbcount.html?catalog=${catalog}&amp;schema=${schema}" target="_blank"><fmt:message key="countCompare"/></a></div>
		<div class="menuitem"><a href="db/${currentConnection.linkName}/srccompare.html?catalog=${catalog}&amp;schema=${schema}" target="_blank"><fmt:message key="ddlCompare"/></a></div>
<c:if test="${currentConnection.writable}"
>		<hr class="menuseparator"/>
		<div class="menuitem"><a href="#" onclick="return showCreateTableDialog(event, '${catalog}', '${schema}');"><fmt:message key="createTable"/></a></div>
</c:if
>		<hr class="menuseparator"/>
		<div class="menuitem"><a href="#" onclick="return clearDbCache();"><fmt:message key="clearCache"/></a></div>
	</div></div>
	</jsp:body></ui:headline1>
	
	<c:set var="links" value="db/${currentConnection.linkName}/dbcatalogs.html,db/${currentConnection.linkName}/dbschemas.html?catalog=${catalog},db/${currentConnection.linkName}/dbobjects.html?catalog=${catalog}&amp;schema=${schema}"
	/><c:set var="links" value="${fn:split(links, ',')}"
	/><ui:explorer><ui:multilevel query="${query.name}" levels="${query.subQueries}" params="${params}" links="${links}" items="${tabs}" var="rs" varKey="label" varParams="detailParams" varParamString="detailParamString" name="combo"
		><ui:result-dbobjects rs="${rs}" label="${label}" link="db/${currentConnection.linkName}/dbobject.html?catalog=${catalog}&amp;schema=${schema}&amp;object=%%" jsLink="return showDBObject(event, '${catalog}', '${schema}', '%%');" targetElement="${targetElement}"
	/></ui:multilevel></ui:explorer>
<%@
	include file="../include/footer.jspf"
%>
