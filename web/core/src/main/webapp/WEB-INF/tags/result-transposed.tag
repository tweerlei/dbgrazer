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
	tag description="Custom tag that generates a transposed table from a RowSet"
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
>		<table id="xtable-${label}" class="transposed table-autosort">
			<thead>
				<tr>
					<th class="table-sortable table-sortable:ignorecase">${fn:escapeXml(rs.columns[0].name)}</th>
<c:forEach items="${rs.rows}" var="row"
>					<th class="table-sortable table-sortable:ignorecase">${fn:escapeXml(row.values[0])}</th>
</c:forEach
>				</tr>
			</thead>
			<tbody>
<c:forEach items="${rs.columns}" var="c" varStatus="st" begin="1"
>				<tr>
					<td class="heading" title="${fn:escapeXml(c.typeName)}">${fn:escapeXml(c.name)}</td>
<c:forEach items="${rs.rows}" var="row"
>					<td><ui:link value="${row.values[st.index]}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/></td>
</c:forEach
>				</tr>
</c:forEach
>			</tbody>
		</table>
