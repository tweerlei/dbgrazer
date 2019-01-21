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
	tag description="Custom tag that generates DL tags from a 1-row RowSet"
%><%@
	attribute name="rs" required="true" type="de.tweerlei.dbgrazer.query.model.RowSet" rtexprvalue="true" description="The RowSet"
%><%@
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="columns" required="true" type="java.lang.Integer" rtexprvalue="true" description="Number of columns"
%><%@
	attribute name="targetElement" required="false" type="java.lang.String" rtexprvalue="true" description="The link target element"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@
	taglib prefix="tools" uri="http://tweerlei.de/springtools/tags"
%><%@
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%><c:choose><c:when test="${fn:startsWith(rs.columns[0].name, '--')}"
><div class="column${columns}">
		<div class="subheading">${fn:escapeXml(rs.firstRow.values[0])}</div>
		<dl id="result-${label}" class="result">
<c:forEach items="${rs.firstRow.values}" begin="1" var="v" varStatus="st"
><c:choose><c:when test="${fn:startsWith(rs.columns[st.index].name, '--')}"
>		</dl><hr/></div>
		<div class="column${columns}">
		<div class="subheading">${fn:escapeXml(v)}</div>
		<dl class="result">
</c:when><c:otherwise
>			<dt title="${fn:escapeXml(empty rs.columns[st.index].typeName ? rs.columns[st.index].name : rs.columns[st.index].typeName)}">${fn:escapeXml(rs.columns[st.index].name)}</dt>
			<dd><ui:link value="${v}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/></dd>
</c:otherwise></c:choose></c:forEach
>		</dl><hr/></div><hr/></c:when
><c:otherwise
><div class="column${columns}"><dl id="result-${label}" class="result">
<c:set var="limit" value="${2 * tools:toInt((fn:length(rs.firstRow.values) - 1) / (2 * columns)) + 2}"
/><c:forEach items="${rs.firstRow.values}" var="v" varStatus="st"
><c:if test="${(st.index > 0) && (st.index mod limit == 0)}"
>		</dl><hr/></div>
		<div class="column${columns}"><dl class="result">
</c:if
>			<dt title="${fn:escapeXml(empty rs.columns[st.index].typeName ? rs.columns[st.index].name : rs.columns[st.index].typeName)}">${fn:escapeXml(rs.columns[st.index].name)}</dt>
			<dd><ui:link value="${v}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/></dd>
</c:forEach
>		</dl><hr/></div><hr/></c:otherwise
></c:choose>