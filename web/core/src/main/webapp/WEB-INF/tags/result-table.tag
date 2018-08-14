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
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="targetElement" required="false" type="java.lang.String" rtexprvalue="true" description="The link target element"
%><%@
	attribute name="params" required="false" type="java.util.Map" rtexprvalue="true" description="The query parameters"
%><%@
	attribute name="paramString" required="false" type="java.lang.String" rtexprvalue="true" description="The query parameters encoded for URL usage"
%><%@
	attribute name="detailLink" required="false" type="java.lang.String" rtexprvalue="true" description="Optional detail link"
%><%@
	attribute name="pkColumns" required="false" type="java.util.List" rtexprvalue="true" description="The PK column indices"
%><%@
	attribute name="catalogName" required="false" type="java.lang.String" rtexprvalue="true" description="The table catalog name"
%><%@
	attribute name="schemaName" required="false" type="java.lang.String" rtexprvalue="true" description="The table schema name"
%><%@
	attribute name="objectName" required="false" type="java.lang.String" rtexprvalue="true" description="The table name"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%><c:if test="${not empty detailLink && rs.moreAvailable}"
>		<div><strong><fmt:message key="moreData"/></strong> <a href="${detailLink}"><fmt:message key="more"/></a></div>
</c:if
>		<table id="table-${label}" class="multiple table-autosort">
			<thead>
				<tr>
<c:if test="${rs.attributes['diff'] || rs.query.attributes['colorize']}"
>					<th class="hidden">${fn:escapeXml(rs.columns[0].name)}</th>
</c:if><c:forEach items="${rs.columns}" var="c" begin="${(rs.attributes['diff'] || rs.query.attributes['colorize']) ? 1 : 0}"
>					<th class="table-sortable <c:choose
						><c:when test="${c.type == 'INTEGER' || c.type == 'FLOAT'}">table-sortable:numeric</c:when
						><c:when test="${c.type == 'DATE'}">table-sortable:date</c:when
						><c:otherwise>table-sortable:ignorecase</c:otherwise
					></c:choose>" title="${fn:escapeXml(c.typeName)}">${fn:escapeXml(c.name)}</th>
</c:forEach
>				</tr>
			</thead>
<c:if test="${rs.attributes['sumValues'] != null}"
>			<tfoot>
				<tr>
<c:choose><c:when test="${rs.query.attributes['colorize']}"
>					<td class="hidden"></td>
<c:forEach items="${rs.attributes['sumValues'].values}" var="v" varStatus="st" begin="1"
>					<td><c:choose><c:when test="${v == null}">&nbsp;</c:when><c:otherwise><ui:link value="${v}"/></c:otherwise></c:choose></td>
</c:forEach
></c:when><c:otherwise
><c:forEach items="${rs.attributes['sumValues'].values}" var="v" varStatus="st"
>					<td><c:choose><c:when test="${v == null}">&nbsp;</c:when><c:otherwise><ui:link value="${v}"/></c:otherwise></c:choose></td>
</c:forEach
></c:otherwise></c:choose
>				</tr>
			</tfoot>
</c:if
>			<tbody>
<c:choose><c:when test="${rs.attributes['diff']}"
><c:forEach items="${rs.rows}" var="row"
>				<tr class="${(row.values[0] == '-') ? 'diff-del' : 'diff-add'}">
					<td class="hidden"></td>
<c:forEach items="${row.values}" var="v" varStatus="st" begin="1"
>					<td><ui:link value="${v}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/></td>
</c:forEach
>				</tr>
</c:forEach
></c:when><c:when test="${rs.query.attributes['colorize']}"
><c:forEach items="${rs.rows}" var="row"
>				<tr class="colored-${row.values[0]}">
					<td class="hidden"></td>
<c:forEach items="${row.values}" var="v" varStatus="st" begin="1"
>					<td><ui:link value="${v}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/></td>
</c:forEach
>				</tr>
</c:forEach
></c:when><c:otherwise
><c:forEach items="${rs.rows}" var="row"
>				<tr>
<c:forEach items="${row.values}" var="v" varStatus="st"
>					<td<c:if test="${st.first && rs.query.type == 'PIVOT'}"> class="heading"</c:if>><c:choose
						><c:when test="${st.first && rs.query.attributes['dimension'] != null}"><ui:dimlink value="${v}" rs="${rs}" targetElement="${targetElement}"/></c:when
						><c:otherwise><ui:link value="${v}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/><c:if test="${(not empty objectName) && (not empty pkColumns) && (st.index == pkColumns[0])}"
><c:set var="args" value="event, '${catalogName}', '${schemaName}', '${objectName}', ["
/><c:forEach items="${pkColumns}" var="pk" varStatus="pkst"
	><c:if test="${!pkst.first}"
		><c:set var="args" value="${args}, "
	/></c:if
	><c:set var="args" value="${args}'${row.values[pk]}'"
/></c:forEach
><c:set var="args" value="${args}], '${rs.query.name}'"
/>						<span class="action" title="<fmt:message key="updateRow"/>" onclick="return showUpdateDialog(${args});">&#x270d;</span> <span class="action" title="<fmt:message key="copyRow"/>" onclick="return showCopyDialog(${args});">&#x271a;</span> <span class="action" title="<fmt:message key="deleteRow"/>" onclick="return showDeleteDialog(${args});">&#x2716;</span></c:if
						></c:otherwise
					></c:choose></td>
</c:forEach
>				</tr>
</c:forEach
></c:otherwise></c:choose
>			</tbody>
		</table>
