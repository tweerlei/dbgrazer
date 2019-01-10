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
	tag description="Custom tag that generates a table from a RowSet"
%><%@
	attribute name="rs" required="true" type="de.tweerlei.dbgrazer.query.model.RowSet" rtexprvalue="true" description="The RowSet"
%><%@
	attribute name="level" required="true" type="java.lang.Integer" rtexprvalue="true" description="The tree hierarchy level"
%><%@
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="targetElement" required="false" type="java.lang.String" rtexprvalue="true" description="The link target element"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@
	taglib prefix="util" uri="http://tweerlei.de/dbgrazer/web/taglib/JspFunctions"
%><%@
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%><c:set var="offset" value="${(rs.query.attributes['colorize'] ? 1 : 0) + (rs.attributes['parentQuery'].attributes['hideId'] ? 1 : 0)}"
/><div class="tab-header">
			<ui:filter id="filter-table-${label}" target="table-${label}" form="true"/><hr/>
		</div>
		<div class="tab-body">
		<table id="table-${label}" class="multiple table-autosort">
			<thead>
				<tr>
<c:if test="${rs.attributes['moreLevels']}"
>					<th>&nbsp;</th>
</c:if><c:forEach items="${rs.columns}" var="c" begin="${0 + offset}" end="${1 + offset}"
>					<th class="table-sortable <c:choose
						><c:when test="${c.type == 'INTEGER' || c.type == 'FLOAT'}">table-sortable:numeric</c:when
						><c:when test="${c.type == 'DATE'}">table-sortable:date</c:when
						><c:otherwise>table-sortable:ignorecase</c:otherwise
					></c:choose>" title="${fn:escapeXml(c.typeName)}">${fn:escapeXml(c.name)}</th>
</c:forEach
><c:if test="${fn:length(rs.columns) > 2 + offset}"
>					<th>&nbsp;</th>
</c:if
>				</tr>
			</thead>
			<tbody>
<c:choose><c:when test="${rs.query.attributes['colorize']}"
><c:forEach items="${rs.rows}" var="row" varStatus="rst"
>				<tr class="colored-${row.values[0]}">
<c:if test="${rs.attributes['moreLevels']}"><c:set var="rowid" value="${rst.index}"
/>					<td><span class="action" title="<fmt:message key="expand"/>" onclick="return expandQueryLevel(event, '${rs.attributes['parentQuery'].name}', ${level}, '${fn:escapeXml(util:paramExtract(row.values[1])[0])}');">&#x25ba;</span></td>
</c:if
><c:forEach items="${row.values}" var="v" varStatus="st" begin="${0 + offset}" end="${1 + offset}"
>					<td><ui:link value="${v}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/></td>
</c:forEach
><c:if test="${fn:length(row.values) > 2 + offset}"
>					<td><ui:info name="info-${label}-${rowid}"><c:forEach items="${row.values}" var="v" varStatus="st" begin="${2 + offset}"
						>${fn:escapeXml(rs.columns[st.index].name)} = <ui:link value="${v}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/>
</c:forEach></ui:info></td>
</c:if
>				</tr>
</c:forEach
></c:when><c:otherwise
><c:forEach items="${rs.rows}" var="row" varStatus="rst"
>				<tr>
<c:set var="rowid" value="${rst.index}"
/><c:if test="${rs.attributes['moreLevels']}"
>					<td><span class="action" title="<fmt:message key="expand"/>" onclick="return expandQueryLevel(event, '${rs.attributes['parentQuery'].name}', ${level}, '${fn:escapeXml(util:paramExtract(row.values[0])[0])}');">&#x25ba;</span></td>
</c:if><c:forEach items="${row.values}" var="v" varStatus="st" begin="${0 + offset}" end="${1 + offset}"
>					<td><ui:link value="${v}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/></td>
</c:forEach
><c:if test="${fn:length(row.values) > 2 + offset}"
>					<td><ui:info name="info-${label}-${rowid}"><c:forEach items="${row.values}" var="v" varStatus="st" begin="${2 + offset}"
						>${fn:escapeXml(rs.columns[st.index].name)} = <ui:link value="${v}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/>
</c:forEach></ui:info></td>
</c:if
>				</tr>
</c:forEach
></c:otherwise></c:choose
>			</tbody>
		</table>
		</div>