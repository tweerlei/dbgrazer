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
	tag description="Custom tag that displays a list of progress items"
%><%@
	attribute name="items" required="true" type="java.util.Map" rtexprvalue="true" description="Map: Task name to TaskProgress object"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><c:forEach items="${items}" var="p"
><br/><c:choose
><c:when test="${p.value.todo > 0}"><fmt:message key="${p.key}_progress"><fmt:param value="${p.value.done}"/><fmt:param value="${p.value.todo}"/></fmt:message></c:when
><c:otherwise><fmt:message key="${p.key}"><fmt:param value="${p.value.done}"/></fmt:message></c:otherwise
></c:choose><c:if test="${p.value.cancelled}"> - <fmt:message key="cancelled"/></c:if
></c:forEach>