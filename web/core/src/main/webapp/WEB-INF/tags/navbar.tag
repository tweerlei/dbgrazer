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
	tag description="Custom tag that creates a navigation bar control from the contents of a Map: String -> TabItem.
	If the map key starts with '$', it will be localized. 
	'%%' in editLinkTemplate will be replaced with the TabItem's name."
%><%@
	attribute name="items" required="true" type="java.util.Map" rtexprvalue="true" description="Map: String -> TabItem"
%><%@
	attribute name="selectedItem" required="false" type="java.lang.Object" rtexprvalue="true" description="The selected map key"
%><%@
	attribute name="name" required="false" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="editLinkTemplate" required="false" type="java.lang.String" rtexprvalue="true" description="Optional edit link, %% will be replaced with the TabItem name"
%><%@
	attribute name="editTitle" required="false" type="java.lang.String" rtexprvalue="true" description="Optional edit link title"
%><%@
	attribute name="var" required="true" type="java.lang.String" rtexprvalue="false" description="Scoped variable for the current TabItem"
%><%@
	attribute name="varKey" required="true" type="java.lang.String" rtexprvalue="false" description="Scoped variable for the current key"
%><%@
	attribute name="varParams" required="true" type="java.lang.String" rtexprvalue="false" description="Scoped variable for the query parameter list"
%><%@
	attribute name="varParamString" required="true" type="java.lang.String" rtexprvalue="false" description="Scoped variable for the query parameter string"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@
	variable name-from-attribute="var" alias="content" scope="NESTED"
%><%@
	variable name-from-attribute="varKey" alias="key" scope="NESTED"
%><%@
	variable name-from-attribute="varParams" alias="params" scope="NESTED"
%><%@
	variable name-from-attribute="varParamString" alias="paramString" scope="NESTED"
%><div id="left-content"><c:forEach items="${items}" var="i" varStatus="st"><div class="combo-head"><c:choose
	><c:when test="${empty i.key}"
		><fmt:message key="emptyTab"
	/></c:when
	><c:otherwise
		><c:choose
			><c:when test="${fn:startsWith(i.key, '$')}"><fmt:message key="${fn:substring(i.key, 1, -1)}"><fmt:param value="${i.value.count}"/></fmt:message></c:when
			><c:otherwise><fmt:message key="customTab"><fmt:param value="${fn:escapeXml(i.key)}"/><fmt:param value="${i.value.count}"/></fmt:message></c:otherwise
		></c:choose
	></c:otherwise
	></c:choose><c:if test="${currentConnection.editorActive && (not empty editLinkTemplate) && (not empty i.value.name)}"
		> <a class="action" title="${editTitle}" href="${fn:replace(editLinkTemplate, '%%', i.value.name)}"><fmt:message key="editQueryIcon"/></a></c:if
	></div>
	<div id="combo-body-${name}${st.index}" class="combo-body"><div class="tab-page" id="tab-page-${name}${st.index}"><c:set var="key" value="${name}${st.index}"
		/><c:set var="content" value="${i.value.payload}"
		/><c:set var="params" value="${i.value.params}"
		/><c:set var="paramString" value="${i.value.paramString}"
		/><jsp:doBody
	/></div></div></c:forEach
></div>