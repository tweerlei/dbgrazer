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
	tag description="Custom tag that generates tree rows from a RowSet"
%><%@
	attribute name="rs" required="true" type="de.tweerlei.dbgrazer.query.model.RowSet" rtexprvalue="true" description="The RowSet"
%><%@
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="level" required="true" type="java.lang.Integer" rtexprvalue="true" description="The tree hierarchy level"
%><%@
	attribute name="targetElement" required="false" type="java.lang.String" rtexprvalue="true" description="The link target element"
%><%@
	attribute name="left" required="false" type="java.lang.String" rtexprvalue="true" description="The parent IDs, separated by dashes"
%><%@
	attribute name="first" required="false" type="java.lang.Boolean" rtexprvalue="true" description="Whether the RowSet is the primary result for this level"
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
%><c:set var="offset" value="${rs.attributes['parentQuery'].attributes['hideId'] ? 1 : 0}"
/><c:set var="rowoffset" value="${fn:startsWith(rs.columns[0].name, '--') ? 1 : 0}"
/><table class="multiple"><thead>
			<tr>
				<th>&nbsp;</th>
<c:forEach items="${rs.columns}" var="c" begin="${0 + offset + rowoffset}"
>				<th title="${fn:escapeXml(c.typeName)}">${fn:escapeXml(c.name)}</th>
</c:forEach
>			</tr>
		</thead><tbody><c:forEach items="${rs.rows}" var="row" varStatus="rst"
			><c:set var="rowid" value="${left}${rst.index}"
			/><tr class="treerow" id="treerow-${label}-${rowid}" data-param="${fn:escapeXml(util:paramExtract(row.values[0])[0])}"><td class="treebutton"><c:choose
					><c:when test="${first && rs.attributes['moreLevels']}"><span class="action" title="<fmt:message key="expand"/>" onclick="return toggleTreeItem(event, '${label}', '${rowid}', '${rs.attributes['parentQuery'].name}', '${targetElement}');">&#x25ba;</span></c:when
					><c:when test="${first}">&#x25b7;</c:when
					><c:otherwise>&#x25ab;</c:otherwise
				></c:choose></td><c:forEach items="${row.values}" var="v" varStatus="st" begin="${0 + offset + rowoffset}"
				><td><c:if test="${st.first && not empty rs.columns[st.index].targetQuery.queryName}">${rs.columns[st.index].targetQuery.queryName}: </c:if><ui:link value="${row.values[st.index]}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/></td></c:forEach
			></tr><tr style="display: none;"><td>&nbsp;</td><td id="treechild-${label}-${rowid}" colspan="${fn:length(rs.columns) - offset - rowoffset}">&nbsp;</td></tr></c:forEach
		></tbody>
	</table>