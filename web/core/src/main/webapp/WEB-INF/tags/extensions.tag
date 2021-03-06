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
	tag description="Custom tag that generates menu items for ExtensionDef objects"
%><%@
	attribute name="items" required="true" type="java.util.List" rtexprvalue="true" description="ExtensionDef objects"
%><%@
	attribute name="separatorBefore" required="false" type="java.lang.Boolean" rtexprvalue="true" description="Prepend a separator if any items were generated"
%><%@
	attribute name="separatorAfter" required="false" type="java.lang.Boolean" rtexprvalue="true" description="Append a separator if any items were generated"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><c:if test="${separatorBefore && not empty items}"><hr class="menuseparator"/></c:if
><c:forEach items="${items}" var="i"
	><div class="menuitem"><c:choose
		><c:when test="${not empty i.onclick}"><span onclick="${fn:escapeXml(i.onclick)}"><fmt:message key="${i.label}"/></span></c:when
		><c:otherwise><a href="${fn:escapeXml(i.href)}"><fmt:message key="${i.label}"/></a></c:otherwise
	></c:choose></div></c:forEach
><c:if test="${separatorAfter && not empty items}"><hr class="menuseparator"/></c:if>