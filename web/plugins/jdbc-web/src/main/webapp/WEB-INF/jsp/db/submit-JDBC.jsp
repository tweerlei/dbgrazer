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
		unzoomElement();
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
		return submitFormTo('submitform', event, 'db/${currentConnection.linkName}/submit-JDBC-export.html', format, '_blank');
	}
	
	function formatQuery(frm) {
		var e = $('statement');
		var stmt = Forms.getSelectedText(e);
		formatText(stmt, 'SQL', function(txt) {
			Forms.replaceSelectedText(e, txt);
		});
		return false;
	}
	
	// on reload (caused by changing the chart type), just reload the chart image
	function reloadPage() {
		return submitForm($('submitform'), 'chart');
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
		<a class="action" title="<fmt:message key="newWindow"/>" href="db/${currentConnection.linkName}/submit-JDBC.html" target="_blank">&#x2750;</a>
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
		<div id="fullscreen" style="display: none;"><form class="content" action="#" onsubmit="return unzoomElement();">
			<div><input type="button" value="<fmt:message key="format"><fmt:param value="SQL"/></fmt:message>" onclick="formatQuery(form);"/>
			<span class="action" title="<fmt:message key="maximize"/>" onclick="return unzoomElement();">&#x25f1;</span></div>
		</form></div>
		<div id="zoomable1" class="tab-header">
			<spring:form id="submitform" cssClass="full" action="db/${currentConnection.linkName}/ajax/submit-JDBC.html" modelAttribute="model" method="post" onsubmit="return submitForm(this, 'table');">
				<spring:hidden id="query" path="statement"/>
				<spring:hidden id="resulttype" path="view"/>
				<spring:hidden id="resultformat" path="format"/>
				<dl>
					<dt><label for="statement"><fmt:message key="sqlStatement"/><c:if test="${currentConnection.writable}"> <fmt:message key="dmlAllowed"/></c:if></label></dt>
					<dd><div><input type="button" value="<fmt:message key="format"><fmt:param value="SQL"/></fmt:message>" onclick="formatQuery(form);"/>
						<span class="action" title="<fmt:message key="delete"/>" onclick="return clearElement('statement');">&#x232b;</span>
						<span class="action" title="<fmt:message key="maximize"/>" onclick="return zoomElement('statement');">&#x25f1;</span></div>
						<spring:textarea id="statement" cssClass="medium" path="query" cols="80" rows="25"/></dd>
					<dt><fmt:message key="queryParams"/></dt>
					<dd><table class="props">
						<thead>
							<tr>
								<th><fmt:message key="position"/></th>
								<th><fmt:message key="value"/></th>
								<th><fmt:message key="columnType"/></th>
								<th><fmt:message key="action"/></th>
							</tr>
						</thead>
						<tfoot>
							<tr>
								<td colspan="5"><span class="action" title="<fmt:message key="more"/>" onclick="addLine(event, 'params');">&#x271a;</span></td>
							</tr>
						</tfoot>
						<tbody><c:forEach begin="0" end="${fn:length(model.params) - 1}" var="i" varStatus="st">
							<tr id="params-${i}"<c:if test="${empty model.params[i].value}"> style="display: none;"</c:if>>
								<td>${i+1}<spring:hidden path="params[${i}].enabled" id="params-${i}-enabled"/></td>
								<td><spring:input path="params[${i}].value" id="params${i}.value"/></td>
								<td><spring:select path="params[${i}].type"><c:forEach items="${columnTypes}" var="j"
									><spring:option value="${j}"><fmt:message key="${j}"/></spring:option></c:forEach
								></spring:select></td>
								<td><c:if test="${!st.last}">
									<span class="action" title="<fmt:message key="down"/>" onclick="return moveLineDown(event, 'params', ${i});">&#x21d3;</span>
</c:if><c:if test="${!st.first}"
>									<span class="action" title="<fmt:message key="up"/>" onclick="return moveLineUp(event, 'params', ${i});">&#x21d1;</span>
</c:if
>									<span class="action" title="<fmt:message key="remove"/>" onclick="return removeLine(event, 'params', ${i});">&#x2716;</span>
								</td>
							</tr></c:forEach>
						</tbody>
					</table></dd>
					<dt>&nbsp;</dt>
					<dd><input type="submit" value="<fmt:message key="execute"/>"/>
						<input type="button" value="<fmt:message key="showPlan"/>" onclick="submitForm(form, 'plan');"/>
						</dd>
					<dt><label for="form-name"><fmt:message key="tableName"/></label></dt>
					<dd><spring:input id="form-name" path="tableName"/>
						<span class="menu" onclick="showDbMenu(event, 'tbllinks');"><fmt:message key="download"/></span>
						<fmt:message key="downloadAllRows" var="l"/><spring:checkbox id="form-all" path="allRows" value="true" label=" ${l}"/>
						</dd>
					<dt><label for="form-label"><fmt:message key="chartTitle"/></label></dt>
					<dd><spring:input id="form-label" path="label"/>
						<input type="button" value="<fmt:message key="showChart"/>" onclick="submitForm(form, 'chart');"/>
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
