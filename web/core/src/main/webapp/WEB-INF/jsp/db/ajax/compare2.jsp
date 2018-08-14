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
%><spring:form id="f1" action="db/${currentConnection.linkName}/compare.html" method="get" modelAttribute="model" onsubmit="return submitDialog(this);" onreset="return closeDialog();">
	<input id="queryName" type="hidden" name="q" value="${model.query.name}"/>
	<dl>
<ui:params items="${model.query.parameters}" path="params" values="${values}"
/>		<dt><label for="f1-connection"><fmt:message key="connection"/></label></dt>
		<dd><select id="f1-connection" name="connection2" onchange="return selectComplete(this);">
			<option value=""><fmt:message key="noSelection"/></option><c:forEach items="${allConnections}" var="c">
			<option value="${c.value}"<c:if test="${c.value == selectedConnection}"> selected="selected"</c:if>>${c.key}</option>
</c:forEach></select></dd>
<ui:params items="${model.query.parameters}" path="params2" values="${values}"
/>		<dt>&nbsp;</dt>
		<dd><input id="f1-submit" type="submit" value="<fmt:message key="compare"/>"<c:if test="${empty selectedConnection}"> disabled="disabled"</c:if>/>
			<input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</spring:form>
