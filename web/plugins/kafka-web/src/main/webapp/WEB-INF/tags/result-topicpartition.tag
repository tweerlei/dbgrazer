<%@
	tag description="Custom tag that generates a table from a RowSet"
%><%@
	attribute name="rs" required="true" type="de.tweerlei.dbgrazer.query.model.RowSet" rtexprvalue="true" description="The RowSet"
%><%@
	attribute name="link" required="true" type="java.lang.String" rtexprvalue="true" description="The expand link"
%><%@
	attribute name="jsLink" required="false" type="java.lang.String" rtexprvalue="true" description="The expand JavaScript link"
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
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%><div class="tab-header">
			<ui:filter id="filter-table-${label}" target="table-${label}" form="true"/><hr/>
		</div>
		<div class="tab-body">
		<table id="table-${label}" class="multiple table-autosort">
			<thead>
				<tr>
<c:if test="${rs.attributes['moreLevels']}"
>					<th>&nbsp;</th>
</c:if><c:forEach items="${rs.columns}" begin="1" var="c"
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
<c:if test="${rs.attributes['moreLevels']}"><c:set var="rowid" value="${fn:contains(row.values[0], ' ') ? fn:substringBefore(row.values[0], ' ') : row.values[0]}"
/>					<td><a class="action" title="<fmt:message key="expand"/>" href="${fn:replace(link, '%%', rowid)}">&#x25ba;</a></td>
</c:if><c:forEach items="${row.values}" begin="1" var="v" varStatus="st"
>					<td><c:choose
						><c:when test="${rs.attributes['moreLevels']}"><ui:message text="${v}" key=""/></c:when
						><c:otherwise><a href="${fn:replace(link, '%%', v)}" onclick="${fn:replace(jsLink, '%%', v)}">${v}</a></c:otherwise
					></c:choose></td>
</c:forEach
>				</tr>
</c:forEach
>			</tbody>
		</table>
		</div>