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
%><fmt:message key="sqlQuery" var="pageTitle"/><%@
	include file="../include/header.jspf"
%>
	<script type="text/javascript">/*<![CDATA[*/
	
	function prepareForm() {
		var e = $('statement');
		$('query').value = Forms.getSelectedText(e);
	}
	
	function submitForm(frm, p) {
		prepareForm();
		getFormInto(frm, 'result', { resulttype: p });
		return false;
	}
	
	function submitFormTo(frm, ev, url, format, target) {
		prepareForm();
		return postForm(frm, ev, url, { resultformat: format }, target);
	}
	
	function submitDownloadForm(event, format) {
		return submitFormTo('submitform', event, 'db/${currentConnection.linkName}/submit-MONGODB-export.html', format, '_blank');
	}
	
	// on reload (caused by changing the chart type), just reload the chart image
	function reloadPage() {
		if ($('resulttype').value == 'chart') {
			getFormInto($('submitform'), 'result');
		}
		return false;
	}
	
	/*]]>*/</script>
	
	<ui:headline1 label="${pageTitle}: ${currentConnection.customQuery.name}${currentConnection.customQuery.modified ? '*' : ''}">
	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
	<div class="h1-actions">
		<span class="menu" onclick="return showDbMenu(event, 'submit-history');"><fmt:message key="history"/></span>
	</div>
	<div class="h1-actions">
		<a class="action" title="<fmt:message key="newWindow"/>" href="db/${currentConnection.linkName}/submit-MONGODB.html" target="_blank"><fmt:message key="newWindowIcon"/></a>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
		<div class="menuitem"><span onclick="return showDbDialog(event, 'query-save', { q: '${currentConnection.customQuery.name}' }, '<fmt:message key="save"/>');"><fmt:message key="save"/></span></div>
		<div class="menuitem"><span onclick="return showDbDialog(event, 'query-load', { q: '${currentConnection.customQuery.name}' }, '<fmt:message key="load"/>');"><fmt:message key="load"/></span></div>
		<div class="menuitem"><span onclick="return showDbDialog(event, 'query-upload', '', '<fmt:message key="file" var="formatName"/><fmt:message key="uploadAs"><fmt:param value="${formatName}"/></fmt:message>');"><fmt:message key="uploadAs"><fmt:param value="${formatName}"/></fmt:message></span></div>
<c:if test="${currentConnection.editorEnabled}"
>		<hr class="menuseparator"/>
		<div class="menuitem"><span onclick="return submitFormTo('submitform', event, 'db/${currentConnection.linkName}/edit.html', '', '_blank');"><fmt:message key="newQuery"/></span></div>
</c:if
>	</div></div>
	</ui:headline1>
	
	<div class="tab-page">
		<div id="fullscreen" style="display: none;"></div>
		<form id="zoomform" class="hidden" method="post" action="db/${currentConnection.linkName}/ajax/formatstmt.html">
			<input id="zoomstmt" type="hidden" name="statement" value=""/>
			<input type="hidden" name="format" value=""/>
		</form>
		<div id="zoomable1" class="tab-header">
			<spring:form id="submitform" cssClass="full" action="db/${currentConnection.linkName}/ajax/submit-MONGODB.html" modelAttribute="model" method="post" onsubmit="return submitForm(this, 'table');">
				<spring:hidden id="query" path="statement"/>
				<spring:hidden id="resultformat" path="format"/>
				<dl>
					<dt><label for="database"><fmt:message key="mongoDatabase"/></label>
					<dd><spring:select path="database"><spring:options items="${databases}"/></spring:select></dd>
					<dt><label for="statement"><fmt:message key="sqlStatement"/><c:if test="${currentConnection.writable}"> <fmt:message key="dmlAllowed"/></c:if></label></dt>
					<dd><div>
						<span class="action" title="<fmt:message key="clear"/>" onclick="return clearElement('statement');"><fmt:message key="clearIcon"/></span>
						<span class="action" title="<fmt:message key="maximize"/>" onclick="return zoomForm('statement');"><fmt:message key="maximizeIcon"/></span></div>
						<spring:textarea id="statement" cssClass="medium" path="query" cols="80" rows="25"/></dd>
					<dt>&nbsp;</dt>
					<dd><input type="submit" value="<fmt:message key="execute"/>"/>
						<span class="menu" onclick="showDbMenu(event, 'tbllinks');"><fmt:message key="download"/></span>
						</dd>
				</dl><hr/>
			</spring:form>
		</div>
		<div id="result">
			<div class="tab-body"></div>
		</div>
	</div>
<%@
	include file="../include/footer.jspf"
%>
