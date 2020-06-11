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
	tag description="Custom tag that creates a dashboard from the contents of a Map: String -> TabItem
	If the map key starts with '$', it will be localized. 
	'%%' in editLinkTemplate will be replaced with the TabItem's name."
%><%@
	attribute name="items" required="true" type="java.util.Map" rtexprvalue="true" description="Map: String -> TabItem"
%><%@
	attribute name="name" required="false" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="orientation" required="true" type="java.lang.Object" rtexprvalue="true" description="Panel layout: Columns first or rows first"
%><%@
	attribute name="detailLinkTemplate" required="false" type="java.lang.String" rtexprvalue="true" description="Optional detail link for tabs, %% will be replaced with the TabItem name"
%><%@
	attribute name="detailTitle" required="false" type="java.lang.String" rtexprvalue="true" description="Optional detail link title"
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
	taglib prefix="ui" tagdir="/WEB-INF/tags"
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
%><div class="tab-page"><div class="tab-body">
<c:choose><c:when test="${orientation}"
>		<div class="column2"><c:forEach items="${items}" end="${(fn:length(items) - 1) / 2}" var="r" varStatus="st"
			><c:choose
				><c:when test="${empty r.key}"><fmt:message var="label" key="emptyTab"/></c:when
				><c:otherwise
					><c:choose
						><c:when test="${fn:startsWith(r.key, '$')}"><fmt:message var="label" key="${fn:substring(r.key, 1, -1)}"><fmt:param value="${r.value.count}"/></fmt:message></c:when
						><c:otherwise><fmt:message var="label" key="customTab"><fmt:param value="${fn:escapeXml(r.key)}"/><fmt:param value="${r.value.count}"/></fmt:message></c:otherwise
					></c:choose
					><c:if test="${(not empty detailLinkTemplate) && (not empty r.value.name)}"
						><c:set var="linkInsert" value="${r.value.name}${r.value.paramString}"
						/><c:set var="detailLink" value="${fn:replace(detailLinkTemplate, '%%', linkInsert)}"
						/><c:set var="label" value="${label} ${'<a class=\"action\" title=\"'}${detailTitle}${'\" href=\"'}${detailLink}${'\">&#x279a;</a>'}"
					/></c:if
					><c:if test="${currentConnection.editorActive && (not empty editLinkTemplate) && (not empty r.value.name)}"
						><c:set var="label" value="${label} ${'<a class=\"action\" title=\"'}${editTitle}${'\" href=\"'}${fn:replace(editLinkTemplate, '%%', r.value.name)}${'\">&#x270e;</a>'}"
					/></c:if
				></c:otherwise
			></c:choose
			><c:set var="key" value="${name}${st.index}"
			/><c:set var="content" value="${r.value.payload}"
			/><c:set var="params" value="${r.value.params}"
			/><c:set var="paramString" value="${r.value.paramString}"
			/><ui:panel title="${label}" collapsed="${r.key == '$emptyTab'}"><jsp:doBody/></ui:panel
			></c:forEach>
		</div>
		<div class="column2"><c:forEach items="${items}" begin="${(fn:length(items) + 1) / 2}" var="r" varStatus="st"
			><c:choose
				><c:when test="${empty r.key}"><fmt:message var="label" key="emptyTab"/></c:when
				><c:otherwise
					><c:choose
						><c:when test="${fn:startsWith(r.key, '$')}"><fmt:message var="label" key="${fn:substring(r.key, 1, -1)}"><fmt:param value="${r.value.count}"/></fmt:message></c:when
						><c:otherwise><fmt:message var="label" key="customTab"><fmt:param value="${fn:escapeXml(r.key)}"/><fmt:param value="${r.value.count}"/></fmt:message></c:otherwise
					></c:choose
					><c:if test="${(not empty detailLinkTemplate) && (not empty r.value.name)}"
						><c:set var="linkInsert" value="${r.value.name}${r.value.paramString}"
						/><c:set var="detailLink" value="${fn:replace(detailLinkTemplate, '%%', linkInsert)}"
						/><c:set var="label" value="${label} ${'<a class=\"action\" title=\"'}${detailTitle}${'\" href=\"'}${detailLink}${'\">&#x279a;</a>'}"
					/></c:if
					><c:if test="${currentConnection.editorActive && (not empty editLinkTemplate) && (not empty r.value.name)}"
						><c:set var="label" value="${label} ${'<a class=\"action\" title=\"'}${editTitle}${'\" href=\"'}${fn:replace(editLinkTemplate, '%%', r.value.name)}${'\">&#x270e;</a>'}"
					/></c:if
				></c:otherwise
			></c:choose
			><c:set var="key" value="${name}${st.index}"
			/><c:set var="content" value="${r.value.payload}"
			/><c:set var="params" value="${r.value.params}"
			/><c:set var="paramString" value="${r.value.paramString}"
			/><ui:panel title="${label}" collapsed="${r.key == '$emptyTab'}"><jsp:doBody/></ui:panel
			></c:forEach>
		</div>
</c:when><c:otherwise
>		<div class="column2"><c:forEach items="${items}" begin="0" step="2" var="r" varStatus="st"
			><c:choose
				><c:when test="${empty r.key}"><fmt:message var="label" key="emptyTab"/></c:when
				><c:otherwise
					><c:choose
						><c:when test="${fn:startsWith(r.key, '$')}"><fmt:message var="label" key="${fn:substring(r.key, 1, -1)}"><fmt:param value="${r.value.count}"/></fmt:message></c:when
						><c:otherwise><fmt:message var="label" key="customTab"><fmt:param value="${fn:escapeXml(r.key)}"/><fmt:param value="${r.value.count}"/></fmt:message></c:otherwise
					></c:choose
					><c:if test="${(not empty detailLinkTemplate) && (not empty r.value.name)}"
						><c:set var="linkInsert" value="${r.value.name}${r.value.paramString}"
						/><c:set var="detailLink" value="${fn:replace(detailLinkTemplate, '%%', linkInsert)}"
						/><c:set var="label" value="${label} ${'<a class=\"action\" title=\"'}${detailTitle}${'\" href=\"'}${detailLink}${'\">&#x279a;</a>'}"
					/></c:if
					><c:if test="${currentConnection.editorActive && (not empty editLinkTemplate) && (not empty r.value.name)}"
						><c:set var="label" value="${label} ${'<a class=\"action\" title=\"'}${editTitle}${'\" href=\"'}${fn:replace(editLinkTemplate, '%%', r.value.name)}${'\">&#x270e;</a>'}"
					/></c:if
				></c:otherwise
			></c:choose
			><c:set var="key" value="${name}${st.index}"
			/><c:set var="content" value="${r.value.payload}"
			/><c:set var="params" value="${r.value.params}"
			/><c:set var="paramString" value="${r.value.paramString}"
			/><ui:panel title="${label}" collapsed="${r.key == '$emptyTab'}"><jsp:doBody/></ui:panel
			></c:forEach>
		</div>
		<div class="column2"><c:forEach items="${items}" begin="1" step="2" var="r" varStatus="st"
			><c:choose
				><c:when test="${empty r.key}"><fmt:message var="label" key="emptyTab"/></c:when
				><c:otherwise
					><c:choose
						><c:when test="${fn:startsWith(r.key, '$')}"><fmt:message var="label" key="${fn:substring(r.key, 1, -1)}"><fmt:param value="${r.value.count}"/></fmt:message></c:when
						><c:otherwise><fmt:message var="label" key="customTab"><fmt:param value="${fn:escapeXml(r.key)}"/><fmt:param value="${r.value.count}"/></fmt:message></c:otherwise
					></c:choose
					><c:if test="${(not empty detailLinkTemplate) && (not empty r.value.name)}"
						><c:set var="linkInsert" value="${r.value.name}${r.value.paramString}"
						/><c:set var="detailLink" value="${fn:replace(detailLinkTemplate, '%%', linkInsert)}"
						/><c:set var="label" value="${label} ${'<a class=\"action\" title=\"'}${detailTitle}${'\" href=\"'}${detailLink}${'\">&#x279a;</a>'}"
					/></c:if
					><c:if test="${currentConnection.editorActive && (not empty editLinkTemplate) && (not empty r.value.name)}"
						><c:set var="label" value="${label} ${'<a class=\"action\" title=\"'}${editTitle}${'\" href=\"'}${fn:replace(editLinkTemplate, '%%', r.value.name)}${'\">&#x270e;</a>'}"
					/></c:if
				></c:otherwise
			></c:choose
			><c:set var="key" value="${name}${st.index}"
			/><c:set var="content" value="${r.value.payload}"
			/><c:set var="params" value="${r.value.params}"
			/><c:set var="paramString" value="${r.value.paramString}"
			/><ui:panel title="${label}" collapsed="${r.key == '$emptyTab'}"><jsp:doBody/></ui:panel
			></c:forEach>
		</div>
</c:otherwise></c:choose
>		<hr/>
	</div></div>