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
%><fmt:message key="fullCompare" var="pageTitle"/><%@
	include file="../include/header.jspf"
%>
	<script type="text/javascript">/*<![CDATA[*/
	
	function submitForm(frm) {
		getFormInto(frm, 'result', null, null, true);
		return false;
	}
	
	/*]]>*/</script>
	
	<ui:headline1><jsp:attribute name="content">${pageTitle}
		&raquo; <a href="db/${currentConnection.linkName}/dbschemas.html?catalog=${model.catalog}"><ui:message text="${model.catalog}" key="defaultCatalog"/></a>
		&raquo; <a href="db/${currentConnection.linkName}/dbobjects.html?catalog=${model.catalog}&amp;schema=${model.schema}"><ui:message text="${model.schema}" key="defaultSchema"/></a>
		&raquo; <a href="db/${currentConnection.linkName}/dbobject.html?catalog=${model.catalog}&amp;schema=${model.schema}&amp;object=${model.object}">${model.object}</a>
	</jsp:attribute><jsp:body>
	<form id="h1-form" class="h1-actions" action="db/${currentConnection.linkName}/dml.html" method="get" onsubmit="return showFormDialog(event, 'db:choose-object', this, '<fmt:message key="change"/>');">
		<input type="hidden" name="backTo" value="dml"/>
		<input type="hidden" name="catalog" value="${model.catalog}"/>
		<input type="hidden" name="schema" value="${model.schema}"/>
		<input type="hidden" name="object" value="${model.object}"/>
		<input type="submit" value="<fmt:message key="change"/>"/>
	</form>
	</jsp:body></ui:headline1>
	
	<div class="tab-page"><div class="tab-header">
		<spring:form id="submitform" cssClass="full" action="db/${currentConnection.linkName}/ajax/dml.html" method="post" modelAttribute="model" onsubmit="return submitForm(this);">
			<spring:hidden path="catalog"/>
			<spring:hidden path="schema"/>
			<spring:hidden path="object"/>
			<div class="column"><dl>
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
				<dt><spring:label path="filter"><fmt:message key="WHERE"/></spring:label></dt>
				<dd><spring:input path="filter"/></dd>
				<dt><spring:label path="runMode"><fmt:message key="runMode"/></spring:label></dt>
				<dd><spring:select path="runMode">
<c:forEach items="${runModes}" var="t"
>					<spring:option value="${t}"><fmt:message key="run_${t}"/></spring:option>
</c:forEach
>					</spring:select></dd>
				<dt>&nbsp;</dt>
				<dd><fmt:message key="useInsert" var="lbl"/><spring:checkbox path="useInsert" value="true" label=" ${lbl}"/><br/>
					<fmt:message key="useUpdate" var="lbl"/><spring:checkbox path="useUpdate" value="true" label=" ${lbl}"/><br/>
					<fmt:message key="useDelete" var="lbl"/><spring:checkbox path="useDelete" value="true" label=" ${lbl}"/><br/>
					<fmt:message key="useMerge" var="lbl"/><spring:checkbox path="useMerge" value="true" label=" ${lbl}"/></dd>
				</dd>
				<dt><spring:label path="order"><fmt:message key="compareMode"/></spring:label></dt>
				<dd><spring:select path="order">
<c:forEach items="${orders}" var="o"
>					<spring:option value="${o}"><fmt:message key="order_${o}"/></spring:option>
</c:forEach
>				</spring:select></dd>
<c:if test="${currentConnection.writable}"
>				<dt><spring:label path="execMode"><fmt:message key="executeAs"/></spring:label></dt>
				<dd><spring:select path="execMode">
<c:forEach items="${resultTypes}" var="t"
>					<spring:option value="${t}"><fmt:message key="execute_${t}"/></spring:option>
</c:forEach
>				</spring:select></dd>
</c:if
>				<dt>&nbsp;</dt>
				<dd><input id="f1-submit" type="submit" value="<fmt:message key="compare"/>"<c:if test="${model.schema2 == null}"> disabled="disabled"</c:if>/></dd>
			</dl><hr/></div><div class="column">
				<spring:label path="pkColumns"><fmt:message key="idCompare"/></spring:label><br/>
				<spring:select path="pkColumns" multiple="multiple" size="8">
<c:forEach items="${allColumns}" var="c" varStatus="st"
>					<spring:option value="${c.name}">${c.name}</spring:option>
</c:forEach
>				</spring:select>
			</div><div class="column">
				<spring:label path="dataColumns"><fmt:message key="dataCompare"/></spring:label><br/>
				<spring:select path="dataColumns" multiple="multiple" size="8">
<c:forEach items="${allColumns}" var="c" varStatus="st"
>					<spring:option value="${c.name}">${c.name}</spring:option>
</c:forEach
>				</spring:select>
			</div><hr/>
		</spring:form>
	</div><div id="result"><div class="tab-body">
	</div></div></div>
<%@
	include file="../include/footer.jspf"
%>
