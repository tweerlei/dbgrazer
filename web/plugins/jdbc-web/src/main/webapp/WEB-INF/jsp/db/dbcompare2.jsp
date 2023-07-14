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
%><fmt:message key="structureCompare" var="pageTitle"/><%@
	include file="../include/header.jspf"
%>
	<script type="text/javascript">/*<![CDATA[*/
	
	function submitForm(frm) {
		showProgressDialog(Messages.submitFormTitle, Messages.submitFormText);
		return true;
	}
	
	function uploadFinished(f) {
		var content = f.innerHTML;
		if (content) {
			$('result').innerHTML = content;
		}
		$('formTarget').src = '';
		Dialog.hide();
		tw_contentChanged();
	}
	
	/*]]>*/</script>
	
	<ui:headline1><jsp:attribute name="content">${pageTitle}
		&raquo; <a href="db/${currentConnection.linkName}/dbschemas.html?catalog=${model.catalog}"><ui:message text="${model.catalog}" key="defaultCatalog"/></a>
		&raquo; <a href="db/${currentConnection.linkName}/dbobjects.html?catalog=${model.catalog}&amp;schema=${model.schema}"><ui:message text="${model.schema}" key="defaultSchema"/></a>
		&raquo; <a href="db/${currentConnection.linkName}/dbobject.html?catalog=${model.catalog}&amp;schema=${model.schema}&amp;object=${model.object}">${model.object}</a>
	</jsp:attribute><jsp:body>
	<form id="h1-form" class="h1-actions" action="db/${currentConnection.linkName}/dbcompare2.html" method="get" onsubmit="return showFormDialog(event, 'db:choose-object', this, '<fmt:message key="change"/>');">
		<input type="hidden" name="backTo" value="dbcompare2"/>
		<input type="hidden" name="catalog" value="${model.catalog}"/>
		<input type="hidden" name="schema" value="${model.schema}"/>
		<input type="hidden" name="object" value="${model.object}"/>
		<input type="submit" value="<fmt:message key="change"/>"/>
	</form>
	</jsp:body></ui:headline1>
	
	<div class="tab-page"><div id="zoomable1" class="tab-header">
		<spring:form id="submitform" cssClass="full" action="db/${currentConnection.linkName}/ajax/dbcompare2.html" method="post" modelAttribute="model" target="formTarget" enctype="multipart/form-data" onsubmit="return submitForm(this);">
			<spring:hidden path="catalog"/>
			<spring:hidden path="schema"/>
			<spring:hidden path="object"/>
			<dl>
				<dt><spring:label for="f1-connection" path="connection2"><fmt:message key="connection"/></spring:label></dt>
				<dd><spring:select id="f1-connection" path="connection2" onchange="return selectConnection(this);">
					<spring:option value="."><fmt:message key="noSelection"/></spring:option>
<c:forEach items="${allConnections}" var="c"
>					<spring:option value="${c.value}">${c.key}</spring:option>
</c:forEach
>					</spring:select></dd>
				<dt><spring:label for="f1-catalog" path="catalog2"><fmt:message key="catalog"/></spring:label></dt>
				<dd><spring:select id="f1-catalog" path="catalog2" onchange="return selectCatalog(this);" disabled="${empty catalogs}">
					<spring:option value="."><fmt:message key="noSelection"/></spring:option>
<c:forEach items="${catalogs}" var="c"
>					<spring:option value="${c}"><ui:message text="${c}" key="defaultCatalog"/></spring:option>
</c:forEach
>					</spring:select></dd>
				<dt><spring:label for="f1-schema" path="schema2"><fmt:message key="schema"/></spring:label></dt>
				<dd><spring:select id="f1-schema" path="schema2" onchange="return selectObjComplete(this);" disabled="${empty schemas}">
					<spring:option value="."><fmt:message key="noSelection"/></spring:option>
<c:forEach items="${schemas}" var="c"
>					<spring:option value="${c}"><ui:message text="${c}" key="defaultSchema"/></spring:option>
</c:forEach
>					</spring:select></dd>
				<dt><label for="f1-file"><fmt:message key="file"/></label></dt>
				<dd><input type="file" id="f1-file" name="file" onchange="selectComplete(this);"/></dd>
				<dt><spring:label path="prefix"><fmt:message key="prefix"/></spring:label></dt>
				<dd><spring:input path="prefix"/></dd>
<c:if test="${currentConnection.writable}"
>				<dt><spring:label path="mode"><fmt:message key="executeAs"/></spring:label></dt>
				<dd><spring:select path="mode">
					<spring:option value=""><fmt:message key="preview"/></spring:option>
<c:forEach items="${resultTypes}" var="t"
>					<spring:option value="${t}"><fmt:message key="execute_${t}"/></spring:option>
</c:forEach
>				</spring:select></dd>
</c:if
>				<dt>&nbsp;</dt>
				<dd><input id="f1-submit" type="submit" value="<fmt:message key="compare"/>"<c:if test="${model.schema2 == null}"> disabled="disabled"</c:if>/></dd>
			</dl><hr/>
		</spring:form>
		<iframe id="formTarget" name="formTarget" class="hidden"></iframe>
	</div><div id="result"><div class="tab-body">
	</div></div></div>
<%@
	include file="../include/footer.jspf"
%>
