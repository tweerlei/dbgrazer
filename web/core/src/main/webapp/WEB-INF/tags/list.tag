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
	tag description="Custom tag that displays a list of queries"
%><%@
	attribute name="items" required="true" type="java.util.Collection" rtexprvalue="true" description="The list of Query objects"
%><%@
	attribute name="title" required="true" type="java.lang.String" rtexprvalue="true" description="The title to display"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><c:if test="${not empty items}"
><h2>${title}</h2><hr/>
		<ul>
<c:forEach items="${items}" var="q"
>			<li><c:choose
					><c:when test="${q.type.explorer}"><span title="<fmt:message key="type_explorer"/>">&#x25eb;</span></c:when
					><c:when test="${q.type.resultType == 'VISUALIZATION'}"><span title="<fmt:message key="type_visualization"/>">&#x25ea;</span></c:when
					><c:when test="${q.type.resultType.view}"><span title="<fmt:message key="type_view"/>">&#x25a3;</span></c:when
					><c:otherwise><span title="<fmt:message key="type_other"/>">&#x25a4;</span></c:otherwise
				></c:choose> <a href="db/${currentConnection.linkName}/run-query.html?q=${q.name}">${q.name}</a><c:if test="${currentConnection.editorActive}"
				> <a class="action" title="<fmt:message key="editQuery"/>" href="db/${currentConnection.linkName}/edit.html?q=${q.name}"><fmt:message key="editQueryIcon"/></a></c:if
			></li>
</c:forEach
>		</ul><hr/>
		<br/></c:if
>