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
%><c:set var="pageTitle" value="${model.query.name}"/><%@
	include file="../include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}" group="${model.query.groupName}">
	<ui:search/>
	<div class="h1-actions">
<c:forEach items="${extensions}" var="i"><c:choose
><c:when test="${not empty i.onclick}"
>		<span class="action" title="<fmt:message key="${i.title}"/>" onclick="${fn:escapeXml(i.onclick)}"><fmt:message key="${i.label}"/></span>
</c:when><c:otherwise
>		<a class="action" title="<fmt:message key="${i.title}"/>" href="${fn:escapeXml(i.href)}"><fmt:message key="${i.label}"/></a>
</c:otherwise></c:choose></c:forEach
><c:if test="${model.query.type.resultType != 'RECURSIVE'}"
>		<span class="action" title="<fmt:message key="refresh"/>" onclick="return reloadPage();">&#x21ba;</span>
		<a class="action" title="<fmt:message key="newWindow"/>" href="db/${currentConnection.linkName}/query.html?q=${model.query.name}${paramString}" target="_blank">&#x2750;</a>
</c:if><c:if test="${currentConnection.editorActive}"
>		<a class="action" title="<fmt:message key="editQuery"/>" href="db/${currentConnection.linkName}/edit.html?q=${model.query.name}">&#x270e;</a>
</c:if
>	</div>
	</ui:headline1>
	
<c:choose><c:when test="${model.query.type.resultType.name == 'RECURSIVE'}">
	<div class="tab-page"><div class="tab-body">
		<spring:form cssClass="full" method="get" action="db/${currentConnection.linkName}/result.html" modelAttribute="model" onsubmit="return submitForm(this);" onreset="return goBack();">
			<input type="hidden" name="q" value="${model.query.name}"/>
			<dl>
<ui:params items="${model.query.parameters}" path="params" values="${values}"
/>				<dt>&nbsp;</dt>
				<dd><input type="submit" value="<fmt:message key="apply"/>"/>
					<input type="reset" value="<fmt:message key="cancel"/>"/></dd>
			</dl><hr/>
		</spring:form>
	</div></div>
</c:when><c:otherwise>
	<script type="text/javascript">/*<![CDATA[*/
	
	// on reload (caused by changing the chart type), reload the last query
	function reloadPage() {
		if (!rerunQuery()) {
			getFormInto($('model'), 'explorer-right');
		}
		return false;
	}
	
	/*]]>*/</script>
	
	<ui:explorer><div class="combo-head"><fmt:message key="queryParams"/></div>
		<div id="left-content"><div class="tab-page"><div class="tab-body">
		<spring:form cssClass="left" method="get" action="db/${currentConnection.linkName}/ajax/result.html" modelAttribute="model" onsubmit="return getFormInto(this, 'explorer-right');">
			<input type="hidden" name="q" value="${model.query.name}"/>
			<input type="hidden" name="historize" value="true"/>
			<dl>
<ui:params items="${model.query.parameters}" path="params" values="${values}"
/>				<dt>&nbsp;</dt>
				<dd><input type="submit" value="<fmt:message key="apply"/>"/></dd>
			</dl><hr/>
		</spring:form>
		</div></div></div></ui:explorer>
</c:otherwise></c:choose>
<%@
	include file="../include/footer.jspf"
%>
