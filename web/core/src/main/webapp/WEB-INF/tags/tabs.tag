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
	tag description="Custom tag that creates a tab control from the contents of a Map: String -> TabItem.
	If the map key starts with '$', it will be localized. 
	'%%' in editLinkTemplate will be replaced with the TabItem's name."
%><%@
	attribute name="items" required="true" type="java.util.Map" rtexprvalue="true" description="Map: String -> TabItem"
%><%@
	attribute name="selectedItem" required="false" type="java.lang.Object" rtexprvalue="true" description="The selected map key"
%><%@
	attribute name="name" required="false" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="detailLinkTemplate" required="false" type="java.lang.String" rtexprvalue="true" description="Optional detail link for tabs, %% will be replaced with the TabItem name"
%><%@
	attribute name="editActionTemplate" required="false" type="java.lang.String" rtexprvalue="true" description="Option JavaScript code to edit a tab"
%><%@
	attribute name="editLinkTemplate" required="false" type="java.lang.String" rtexprvalue="true" description="Optional edit link for tabs, %% will be replaced with the TabItem name"
%><%@
	attribute name="editTitle" required="false" type="java.lang.String" rtexprvalue="true" description="Optional edit link title"
%><%@
	attribute name="var" required="true" type="java.lang.String" rtexprvalue="false" description="Scoped variable for the current TabItem"
%><%@
	attribute name="varKey" required="true" type="java.lang.String" rtexprvalue="false" description="Scoped variable for the current key"
%><%@
	attribute name="varLink" required="true" type="java.lang.String" rtexprvalue="false" description="Scoped variable for the detail link"
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
	variable name-from-attribute="varLink" alias="detailLink" scope="NESTED"
%><%@
	variable name-from-attribute="varParams" alias="params" scope="NESTED"
%><%@
	variable name-from-attribute="varParamString" alias="paramString" scope="NESTED"
%><div class="tab-row" id="tab-row-${name}">
<c:forEach items="${items}" var="i" varStatus="st"
>		<div class="tab<c:if test="${(i.key == selectedItem) || (st.first && (selectedItem == null))}"> tab-active</c:if>" id="tab-${name}${st.index}"><span class="action" onclick="return showTab('${name}', ${st.index});"><c:choose
			><c:when test="${empty i.key}"
				><fmt:message key="emptyTab"
			/></c:when
			><c:otherwise
				><c:choose
					><c:when test="${fn:startsWith(i.key, '$')}"><fmt:message key="${fn:substring(i.key, 1, -1)}"><fmt:param value="${i.value.count}"/></fmt:message></c:when
					><c:otherwise><fmt:message key="customTab"><fmt:param value="${fn:escapeXml(i.key)}"/><fmt:param value="${i.value.count}"/></fmt:message></c:otherwise
				></c:choose
				><c:choose
					><c:when test="${currentConnection.editorActive && (not empty editActionTemplate) && (not empty i.value.name)}"
						></span> <span class="action" title="${editTitle}" onclick="${fn:replace(editActionTemplate, '%%', i.value.name)}"><fmt:message key="editQueryIcon"/></c:when
					><c:when test="${currentConnection.editorActive && (not empty editLinkTemplate) && (not empty i.value.name)}"
						></span> <span><a class="action" title="${editTitle}" href="${fn:replace(editLinkTemplate, '%%', i.value.name)}"><fmt:message key="editQueryIcon"/></a></c:when
				></c:choose
			></c:otherwise
			></c:choose></span></div>
</c:forEach
>		<hr/>
	</div>
	
	<div class="tab-content" id="tab-content-${name}">
<c:forEach items="${items}" var="i" varStatus="st"
>		<div class="tab-page" id="tab-page-${name}${st.index}"<c:if test="${(i.key != selectedItem) && (!st.first || (selectedItem != null))}"> style="display: none;"</c:if
			>><%--c:if test="${(not empty detailLinkTemplate) && (not empty i.value.name)}"
				><c:set var="linkInsert" value="${i.value.name}${i.value.paramString}"
				/><c:set var="detailLink" value="${fn:replace(detailLinkTemplate, '%%', linkInsert)}"
			/></c:if
			--%><c:set var="key" value="${name}${st.index}"
			/><c:set var="content" value="${i.value.payload}"
			/><c:set var="params" value="${i.value.params}"
			/><c:set var="paramString" value="${i.value.paramString}"
			/><jsp:doBody
		/></div>
</c:forEach
>	</div>