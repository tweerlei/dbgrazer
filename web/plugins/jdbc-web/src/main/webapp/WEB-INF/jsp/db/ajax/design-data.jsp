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
%><spring:form id="f1" action="db/${currentConnection.linkName}/designdata.html" method="post" modelAttribute="model" target="_blank" onreset="return closeDialog();">
	<dl>
		<dt><spring:label path="table"><fmt:message key="tableName"/></spring:label></dt>
		<dd><spring:select id="f1-table" path="table">
<c:forEach items="${tables}" var="t"
>			<spring:option value="${t.value}">${t.key}</spring:option>
</c:forEach
>			</spring:select></dd>
		<dt><spring:label path="where"><fmt:message key="WHERE"/></spring:label></dt>
		<dd><spring:textarea path="where" cssClass="small" cols="80" rows="5"/></dd>
		<dt><spring:label path="mode"><fmt:message key="traversalMode"/></spring:label></dt>
		<dd><spring:select id="f1-mode" path="mode">
<c:forEach items="${modes}" var="f"
>			<spring:option value="${f}"><fmt:message key="${f}"/></spring:option>
</c:forEach
>			</spring:select></dd>
		<dt><spring:label path="format"><fmt:message key="download"/></spring:label></dt>
		<dd><spring:select id="f1-format" path="format">
<c:forEach items="${downloadFormats}" var="f"
>			<spring:option value="${f}"><fmt:message key="${f}"/></spring:option>
</c:forEach
>			</spring:select></dd>
		<dt>&nbsp;</dt>
		<dd><input id="f1-submit" type="submit" value="<fmt:message key="apply"/>"/>
			<input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</spring:form>
