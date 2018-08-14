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
	tag description="Custom tag that creates a combo box control from the contents of a Map: String -> TabItem.
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
%><div class="combo-head"><div class="float-right"><span class="action" title="<fmt:message key="maximize"/>" onclick="return zoomLeft();">&#x25f1;</span></div><c:choose
><c:when test="${fn:length(items) <= 1}"><c:forEach items="${items}" var="i" varStatus="st"><c:choose
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
		> <a class="action" title="${editTitle}" href="${fn:replace(editLinkTemplate, '%%', i.value.name)}">&#x270e;</a></c:if
	></c:forEach
></c:when
><c:otherwise><form action="#" method="get" onsubmit="return false;"><select id="combo-${name}" name="combo" onchange="return showTab('${name}', this.selectedIndex);">
<c:forEach items="${items}" var="i" varStatus="st"
><c:if test="${empty queryName}"
	><c:set var="queryName" value="${i.value.name}"
/></c:if>	<option id="combo-${name}${st.index}" value="${st.index}"<c:if test="${(i.key == selectedItem) || (st.first && (selectedItem == null))}"> selected="selected"</c:if>><c:choose
		><c:when test="${empty i.key}"
			><fmt:message key="emptyTab"
		/></c:when
		><c:otherwise
			><c:choose
				><c:when test="${fn:startsWith(i.key, '$')}"><fmt:message key="${fn:substring(i.key, 1, -1)}"><fmt:param value="${i.value.count}"/></fmt:message></c:when
				><c:otherwise><fmt:message key="customTab"><fmt:param value="${fn:escapeXml(i.key)}"/><fmt:param value="${i.value.count}"/></fmt:message></c:otherwise
			></c:choose
		></c:otherwise
		></c:choose></option>
</c:forEach
></select><c:if test="${currentConnection.editorActive && (not empty editLinkTemplate) && (not empty queryName)}"
		> <a class="action" title="${editTitle}" href="${fn:replace(editLinkTemplate, '%%', queryName)}">&#x270e;</a></c:if
></form></c:otherwise
></c:choose></div>
	
	<div id="left-content"><div id="combo-body-${name}" class="combo-body">
<c:forEach items="${items}" var="i" varStatus="st"
>		<div class="tab-page" id="tab-page-${name}${st.index}"<c:if test="${(i.key != selectedItem) && (!st.first || (selectedItem != null))}"> style="display: none;"</c:if
			>><c:set var="key" value="${name}${st.index}"
			/><c:set var="content" value="${i.value.payload}"
			/><c:set var="params" value="${i.value.params}"
			/><c:set var="paramString" value="${i.value.paramString}"
			/><jsp:doBody
		/></div>
</c:forEach
>	</div></div>