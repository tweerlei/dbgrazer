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
%><spring:form id="f1" action="db/${currentConnection.linkName}/ddl-create-table.html" method="post" modelAttribute="model" onsubmit="return submitDML(this);" onreset="return closeDialog();">
	<spring:hidden path="catalog"/>
	<spring:hidden path="schema"/>
	<div id="dmlerror"></div>
	<dl>
		<dt><spring:label path="object"><fmt:message key="object"/></spring:label></dt>
		<dd><spring:input path="object"/></dd>
		<dt><spring:label path="objectComment"><fmt:message key="comment"/></spring:label></dt>
		<dd><spring:input path="objectComment"/></dd>
		<dt>&nbsp;</dt>
		<dd><fmt:message key="firstColumn"/></dd>
		<dt><spring:label path="primaryKey"><fmt:message key="primaryKey"/></spring:label></dt>
		<dd><spring:checkbox path="primaryKey"/></dd>
		<dt><spring:label path="name"><fmt:message key="name"/></spring:label></dt>
		<dd><spring:input path="name"/></dd>
		<dt><spring:label path="typeName"><fmt:message key="columnType"/></spring:label></dt>
		<dd><spring:input path="typeName"/></dd>
		<dt><spring:label path="length"><fmt:message key="length"/></spring:label></dt>
		<dd><spring:input path="length"/></dd>
		<dt><spring:label path="decimals"><fmt:message key="decimals"/></spring:label></dt>
		<dd><spring:input path="decimals"/></dd>
		<dt><spring:label path="nullable"><fmt:message key="nullable"/></spring:label></dt>
		<dd><spring:checkbox path="nullable"/></dd>
		<dt><spring:label path="defaultValue"><fmt:message key="defaultValue"/></spring:label></dt>
		<dd><spring:checkbox path="defaultPresent" onchange="toggleTextField('defaultValue');"/> <spring:input path="defaultValue" disabled="true"/></dd>
		<dt><spring:label path="comment"><fmt:message key="comment"/></spring:label></dt>
		<dd><spring:input path="comment"/></dd>
		<dt>&nbsp;</dt>
		<dd><input id="f1-submit" type="submit" value="<fmt:message key="createTable"/>"/> <input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</spring:form>
