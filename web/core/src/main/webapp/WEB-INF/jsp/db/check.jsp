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
%><fmt:message key="checkQueries" var="pageTitle"/><%@
	include file="../include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}"/>
	<div class="tab-page"><div class="tab-body">
<c:choose><c:when test="${empty errors}"><fmt:message key="noProblems"/></c:when
><c:otherwise
>		<table>
			<thead>
				<tr>
					<th><fmt:message key="query"/></th>
					<th><fmt:message key="result"/></th>
				</tr>
			</thead>
			<tbody><c:forEach items="${errors}" var="e">
				<tr>
					<td><a href="db/${currentConnection.linkName}/edit.html?q=${e.key}">${e.key}</a></td>
					<td><c:forEach items="${e.value}" var="m"><fmt:message key="${m.errorKey}"><fmt:param value="${m.param}"/></fmt:message><br/></c:forEach></td>
				</tr></c:forEach>
			</tbody>
		</table>
</c:otherwise></c:choose
>	</div></div>
<%@
	include file="../include/footer.jspf"
%>
