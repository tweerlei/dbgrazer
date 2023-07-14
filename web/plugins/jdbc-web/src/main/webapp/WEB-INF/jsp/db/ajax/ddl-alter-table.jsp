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
%><spring:form id="f1" action="db/${currentConnection.linkName}/ddl-alter-table.html" method="post" modelAttribute="model" onsubmit="return submitDML(this);" onreset="return closeDialog();">
	<spring:hidden path="catalog"/>
	<spring:hidden path="schema"/>
	<spring:hidden path="object"/>
	<div id="dmlerror"></div>
	<dl>
		<dt><spring:label path="newName"><fmt:message key="object"/></spring:label></dt>
		<dd><spring:input path="newName"/></dd>
		<dt><spring:label path="newSchema"><fmt:message key="schema"/></spring:label></dt>
		<dd><spring:select path="newSchema">
<c:forEach items="${schemas}" var="c"
>			<spring:option value="${c}"><ui:message text="${c}" key="defaultSchema"/></spring:option>
</c:forEach
>			</spring:select></dd>
		<dt><spring:label path="objectComment"><fmt:message key="comment"/></spring:label></dt>
		<dd><spring:input path="objectComment"/></dd>
		<dt>&nbsp;</dt>
		<dd><input id="f1-submit" type="submit" value="<fmt:message key="updateRow"/>"/> <input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</spring:form>
