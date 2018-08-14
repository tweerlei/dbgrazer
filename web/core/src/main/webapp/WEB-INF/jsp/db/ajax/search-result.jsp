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
%>		<fmt:message key="lists" var="title"/><ui:list items="${result.lists}" title="${title}"/>
		<fmt:message key="views" var="title"/><ui:list items="${result.views}" title="${title}"/>
		<fmt:message key="queries" var="title"/><ui:list items="${result.queries}" title="${title}"/>
<c:if test="${currentConnection.writable}"
>		<fmt:message key="actions" var="title"/><ui:list items="${result.actions}" title="${title}"/>
</c:if><c:if test="${currentConnection.editorActive}"
>		<fmt:message key="subqueries" var="title"/><ui:list items="${result.subqueries}" title="${title}"/>
</c:if
>