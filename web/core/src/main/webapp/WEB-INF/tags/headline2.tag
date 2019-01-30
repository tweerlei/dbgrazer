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
	tag description="Custom tag that creates a level 2 headline either from a message key, a verbatim label or raw HTML"
%><%@
	attribute name="key" required="false" type="java.lang.String" rtexprvalue="true" description="The headline message key"
%><%@
	attribute name="label" required="false" type="java.lang.String" rtexprvalue="true" description="The headline text"
%><%@
	attribute name="content" required="false" type="java.lang.String" rtexprvalue="true" description="The headline HTML content"
%><%@
	attribute name="zoomable" required="false" type="java.lang.Boolean" rtexprvalue="true" description="Whether to generate a zoom button"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><c:if test="${zoomable}"><div class="h2-buttons"><span class="action" title="<fmt:message key="maximize"/>" onclick="return zoomRight();"><fmt:message key="maximizeIcon"/></span></div>
</c:if
>		<h2><c:choose
			><c:when test="${not empty label}">${fn:escapeXml(label)}</c:when
			><c:when test="${not empty key}"><fmt:message key="${key}"/></c:when
			><c:otherwise>${content}</c:otherwise
></c:choose></h2><jsp:doBody/><hr/>