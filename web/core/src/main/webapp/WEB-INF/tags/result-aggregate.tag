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
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%>		<form id="aggform-${label}" action="db/${currentConnection.linkName}/ajax/aggregate.html" method="post" onsubmit="return aggsubmit(this, '${label}');">
			<input type="hidden" name="q" value="${rs.query.name}"/>
			<input type="hidden" name="index" value="${rs.subQueryIndex}"/>
			<input id="aggformat" type="hidden" name="format" value=""/>
			<input type="hidden" name="formIndex" value="${label}"/>
			<input type="hidden" name="target" value="${targetElement}"/>
			<ui:hidden items="${params}" name="params"/>
			
			<table id="table-${label}" class="aggregate table-autosort">
				<thead>
					<tr>
						<th class="table-sortable table-sortable:numeric"><fmt:message key="COUNT"/></th>
<c:forEach items="${rs.columns}" var="c" varStatus="st"
>						<th class="table-sortable <c:choose
							><c:when test="${c.type == 'INTEGER' || c.type == 'FLOAT'}">table-sortable:numeric</c:when
							><c:when test="${c.type == 'DATE'}">table-sortable:date</c:when
							><c:otherwise>table-sortable:ignorecase</c:otherwise
							></c:choose>">${fn:escapeXml(c.name)}<input type="hidden" name="columns[${st.index}]" value="${fn:escapeXml(c.name)}"/></th>
</c:forEach
>					</tr>
					<tr>
						<th><fmt:message key="filter"/> (<span id="aggcount-${label}">${fn:length(rs.rows)}</span>):</th>
<c:forEach items="${rs.columns}" var="c" varStatus="st"
>						<th><input type="text" name="exprs[${st.index}]" size="10"/></th>
</c:forEach
>					</tr>
					<tr>
						<th><input type="submit" value="<fmt:message key="apply"/>"/></th>
<c:forEach items="${rs.columns}" var="c" varStatus="st"
>						<th><select name="funcs[${st.index}]">
<c:forEach items="${rs.attributes['funcs']}" var="f"><c:if test="${!f.numberRequired || c.type == 'INTEGER' || c.type == 'FLOAT'}"
>							<option value="${f}"><fmt:message key="${f}"/></option>
</c:if></c:forEach
>						</select></th>
</c:forEach
>					</tr>
				</thead>
				<tbody id="aggregate-${label}">
<c:forEach items="${rs.rows}" var="row"
>					<tr>
						<td>1</td>
<c:forEach items="${row.values}" var="v" varStatus="st"
>						<td><ui:filterlink value="${v}" label="${label}" column="${st.index}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/></td>
</c:forEach
>					</tr>
</c:forEach
>				</tbody>
			</table>
		</form>
