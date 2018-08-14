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
		getFormInto(frm, 'result', null, null, true);
		return false;
	}
	
	/*]]>*/</script>
	
	<ui:headline1><jsp:attribute name="content">
		${pageTitle}: <fmt:message key="designer"/>
	</jsp:attribute></ui:headline1>
	
	<div class="tab-page"><div id="zoomable1" class="tab-header">
		<spring:form id="submitform" cssClass="full" action="db/${currentConnection.linkName}/ajax/design-compare.html" method="post" modelAttribute="model" onsubmit="return submitForm(this);">
			<dl>
				<dt><spring:label for="f1-connection" path="connection2"><fmt:message key="connection"/></spring:label></dt>
				<dd><spring:select id="f1-connection" path="connection2" onchange="return selectObjComplete(this);">
					<spring:option value="."><fmt:message key="noSelection"/></spring:option>
<c:forEach items="${allConnections}" var="c"
>					<spring:option value="${c.value}">${c.key}</spring:option>
</c:forEach
>					</spring:select></dd>
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
				<dd><input id="f1-submit" type="submit" value="<fmt:message key="compare"/>"<c:if test="${selectedConnection == null}"> disabled="disabled"</c:if>/></dd>
			</dl><hr/>
		</spring:form>
	</div><div id="result"><div class="tab-body">
	</div></div></div>
<%@
	include file="../include/footer.jspf"
%>
