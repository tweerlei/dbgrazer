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
%><fmt:message key="scriptQuery" var="pageTitle"/><%@
	include file="../include/header.jspf"
%>
	<script type="text/javascript">/*<![CDATA[*/
	
	function submitForm(frm) {
		getFormInto(frm, 'result', null, null, true);
		return false;
	}
	
	function submitFormTo(frm, ev, url) {
		return postForm(frm, ev, url);
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
		<a class="action" title="<fmt:message key="newWindow"/>" href="db/${currentConnection.linkName}/submitexec.html" target="_blank"><fmt:message key="newWindowIcon"/></a>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
		<div class="menuitem"><span onclick="return showDbDialog(event, 'query-save', { q: '${currentConnection.customQuery.name}' }, '<fmt:message key="save"/>');"><fmt:message key="save"/></span></div>
		<div class="menuitem"><span onclick="return showDbDialog(event, 'query-load', { q: '${currentConnection.customQuery.name}' }, '<fmt:message key="load"/>');"><fmt:message key="load"/></span></div>
		<div class="menuitem"><span onclick="return showDbDialog(event, 'query-upload', '', '<fmt:message key="file" var="formatName"/><fmt:message key="uploadAs"><fmt:param value="${formatName}"/></fmt:message>');"><fmt:message key="uploadAs"><fmt:param value="${formatName}"/></fmt:message></span></div>
<c:if test="${currentConnection.editorEnabled}"
>		<hr class="menuseparator"/>
		<div class="menuitem"><span onclick="return submitFormTo('submitform', event, 'db/${currentConnection.linkName}/edit.html');"><fmt:message key="newQuery"/></span></div>
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
			<spring:form id="submitform" cssClass="full" action="db/${currentConnection.linkName}/ajax/submitexec.html" modelAttribute="model" method="post" onsubmit="return submitForm(this);">
				<dl>
					<dt><label for="statement"><fmt:message key="sqlStatement"/></label></dt>
					<dd><div>
						<span class="action" title="<fmt:message key="clear"/>" onclick="return clearElement('statement');"><fmt:message key="clearIcon"/></span>
						<span class="action" title="<fmt:message key="maximize"/>" onclick="return zoomContent('statement');"><fmt:message key="maximizeIcon"/></span></div>
						<spring:textarea id="statement" cssClass="large" path="statement" cols="80" rows="25"/>
						<div>[ <span id="statement-row">1</span> : <span id="statement-column">1</span> ]</div></dd>
					<dt><fmt:message key="executeAs"/></dt>
					<dd><c:forEach items="${resultTypes}" var="t"
						><fmt:message key="execute_${t}" var="lbl"/><spring:radiobutton id="runmode-${t}" path="type" value="${t}" label=" ${lbl}"/> &nbsp; </c:forEach
						></dd>
					<dt>&nbsp;</dt>
					<dd><input type="button" value="<fmt:message key="execute"/>" onclick="submitForm(form);"/>
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
