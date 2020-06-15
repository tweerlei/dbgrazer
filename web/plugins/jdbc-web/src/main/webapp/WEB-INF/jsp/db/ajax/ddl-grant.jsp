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
%><spring:form id="f1" action="db/${currentConnection.linkName}/ddl-grant.html" method="post" modelAttribute="model" onsubmit="return submitDML(this);" onreset="return closeDialog();">
	<spring:hidden path="catalog"/>
	<spring:hidden path="schema"/>
	<spring:hidden path="object"/>
	<div id="dmlerror"></div>
	<dl>
		<dt><spring:label path="privilege"><fmt:message key="action"/></spring:label></dt>
		<dd><spring:input path="privilege"/></dd>
		<dt><spring:label path="grantee"><fmt:message key="grantee"/></spring:label></dt>
		<dd><spring:input path="grantee"/></dd>
		<dt><spring:label path="grantable"><fmt:message key="grantable"/></spring:label></dt>
		<dd><spring:checkbox path="grantable"/></dd>
		<dt>&nbsp;</dt>
		<dd><input id="f1-submit" type="submit" value="<fmt:message key="insertRow"/>"/> <input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</spring:form>
