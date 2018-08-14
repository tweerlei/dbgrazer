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
	tag description="Custom tag that creates a SELECT-like control that uses an AJAX dialog for selecting its value"
%><%@
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Text to display when nothing is selected"
%><%@
	attribute name="name" required="true" type="java.lang.String" rtexprvalue="true" description="The control name"
%><%@
	attribute name="id" required="true" type="java.lang.String" rtexprvalue="true" description="The element ID"
%><%@
	attribute name="value" required="false" type="java.lang.String" rtexprvalue="true" description="The default value"
%><%@
	attribute name="text" required="false" type="java.lang.String" rtexprvalue="true" description="Optional text to display instead of the selected value"
%><%@
	attribute name="page" required="true" type="java.lang.String" rtexprvalue="true" description="The AJAX page name"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><span class="select"><input type="hidden" id="${id}" name="${name}" value="${fn:escapeXml(value)}"/><span class="menu" id="${id}-value" onclick="return showPopup(event, '${page}', { q: \$F('${id}'), id: '${id}' }, '<fmt:message key="chooseValue"/>');"><c:choose
><c:when test="${not empty text}">${fn:escapeXml(text)}</c:when
><c:when test="${not empty value}">${fn:escapeXml(value)}</c:when
><c:otherwise>${label}</c:otherwise
></c:choose></span></span>