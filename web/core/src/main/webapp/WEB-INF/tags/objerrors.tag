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
	tag description="Custom tag that creates div tags for entries in the errorLog"
%><%@
	attribute name="var" required="true" type="java.lang.String" rtexprvalue="false" description="Scoped variable for the current error message"
%><%@
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	variable name-from-attribute="var" alias="content" scope="NESTED"
%><c:forEach items="${currentUser.objectErrorsForDisplay}" var="e" varStatus="st">
		<div id="error-${label}${st.index}" class="error"><span class="action" onclick="return hideError('${label}${st.index}');"><fmt:message key="removeIcon"/></span> <fmt:message key="${e.key}"><c:forEach items="${e.params}" var="p"><fmt:param value="${p}"/></c:forEach></fmt:message><c:set var="content" value="${e.info}"/><jsp:doBody/></div></c:forEach
>