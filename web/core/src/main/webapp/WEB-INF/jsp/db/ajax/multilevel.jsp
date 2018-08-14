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
%><c:choose><c:when test="${exception != null}">
		<div id="left-content">${fn:escapeXml(exception.message)}</div>
</c:when><%--c:when test="${allEmpty}">
		<div id="left-content"><fmt:message key="empty"/></div>
</c:when--%><c:otherwise><fmt:message key="editQuery" var="editQuery"/><ui:multilevel query="${model.query.name}" subQuery="${subquery.name}" levels="${model.query.subQueries}" params="${params}" items="${results}" var="rs" varKey="label" varParams="detailParams" varParamString="detailParamString" name="combo"
		editTitle="${editQuery}" editLinkTemplate="db/${currentConnection.linkName}/edit.html?q=%%&amp;backTo=${model.query.name}"><%@
		include file="../result/tab.jspf"
	%></ui:multilevel></c:otherwise></c:choose>