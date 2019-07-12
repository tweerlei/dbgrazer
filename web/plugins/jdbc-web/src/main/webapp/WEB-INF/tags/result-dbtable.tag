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
	attribute name="pkColumns" required="false" type="java.util.List" rtexprvalue="true" description="The PK column indices"
%><%@
	attribute name="foreignKeys" required="false" type="java.util.List" rtexprvalue="true" description="The foreign key definitions"
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
%>		<table id="table-${label}" class="multiple dbtable table-autosort">
			<thead>
				<tr>
<c:if test="${(not empty objectName) && (not empty pkColumns)}"
>					<th class="actions">&nbsp;</th>
</c:if
><c:forEach items="${rs.columns}" var="c"
>					<th class="table-sortable <c:choose
						><c:when test="${c.type == 'INTEGER' || c.type == 'FLOAT'}">table-sortable:numeric</c:when
						><c:when test="${c.type == 'DATE'}">table-sortable:date</c:when
						><c:otherwise>table-sortable:ignorecase</c:otherwise
					></c:choose>" title="${fn:escapeXml(c.typeName)}">${fn:escapeXml(c.name)}</th>
</c:forEach
>				</tr>
			</thead>
			<tbody>
<c:forEach items="${rs.rows}" var="row"
>				<tr>
<c:if test="${(not empty objectName) && (not empty pkColumns)}"
>					<td class="actions"><span class="action" title="<fmt:message key="referencedBy"/>" onclick="showDbMenu(event, 'dbrefs', { catalog: '${catalogName}', schema: '${schemaName}', object: '${objectName}', pk: '${row.values[pkColumns[0]]}', target: '${targetElement}' });">&#x25bc;</span><c:set var="args" value="event, '${catalogName}', '${schemaName}', '${objectName}', ["
						/><c:forEach items="${pkColumns}" var="pk" varStatus="pkst"
							><c:if test="${!pkst.first}"
								><c:set var="args" value="${args}, "
							/></c:if
						><c:set var="args" value="${args}'${row.values[pk]}'"
						/></c:forEach
						><c:set var="args" value="${args}], '${rs.query.name}'"
						/>&nbsp;<span class="action" title="<fmt:message key="updateRow"/>" onclick="return showUpdateDialog(${args});"><fmt:message key="editIcon"
						/></span>&nbsp;<span class="action" title="<fmt:message key="copyRow"/>" onclick="return showCopyDialog(${args});"><fmt:message key="copyIcon"
						/></span>&nbsp;<span class="action" title="<fmt:message key="deleteRow"/>" onclick="return showDeleteDialog(${args});"><fmt:message key="removeIcon"
						/></span></td>
</c:if
><c:forEach items="${row.values}" var="v" varStatus="st"
>					<td<c:if test="${fn:length(v) >= 100}"> class="zoomable"</c:if>><ui:dblink value="${v}" fk="${foreignKeys[st.index]}" targetElement="${targetElement}"/></td>
</c:forEach
>				</tr>
</c:forEach
>			</tbody>
		</table>
		<div id="order-table-${label}" class="hidden"><span class="action" title="<fmt:message key="addOrderAsc"/>" onclick="addOrderAsc(event);"><fmt:message key="orderAscIcon"/></span>
			&nbsp;<span class="action" title="<fmt:message key="addOrderDesc"/>" onclick="addOrderDesc(event);"><fmt:message key="orderDescIcon"/></span>
			&nbsp;<span class="action" title="<fmt:message key="removeOrder"/>" onclick="removeOrder(event);"><fmt:message key="removeOrderIcon"/></span></div>
		<div id="where-table-${label}" class="hidden"><span class="action" title="<fmt:message key="addWhere"/>" onclick="addWhere(event);"><fmt:message key="addWhereIcon"/></span>
			&nbsp;<span class="action" title="<fmt:message key="addWhereNot"/>" onclick="addWhereNot(event);"><fmt:message key="addWhereNotIcon"/></span>
			&nbsp;<span class="action" title="<fmt:message key="removeWhere"/>" onclick="removeWhere(event);"><fmt:message key="removeWhereIcon"/></span></div>
