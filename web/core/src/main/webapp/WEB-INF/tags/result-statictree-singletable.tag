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
%><%--
	Glassfish bug: Application JARs are not on the JSP precompile classpath
	attribute name="rs" required="true" type="de.tweerlei.dbgrazer.query.model.RowContainer" rtexprvalue="true" description="The RowSet"
--%><%@
	attribute name="rs" required="true" type="java.lang.Object" rtexprvalue="true" description="The RowSet"
%><%@
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="root" required="true" type="java.lang.Object" rtexprvalue="true" description="The current root row"
%><%@
	attribute name="level" required="true" type="java.lang.Integer" rtexprvalue="true" description="The tree hierarchy level"
%><%@
	attribute name="targetElement" required="false" type="java.lang.String" rtexprvalue="true" description="The link target element"
%><%@
	attribute name="left" required="false" type="java.lang.String" rtexprvalue="true" description="The parent IDs, separated by dashes"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@
	taglib prefix="tools" uri="http://tweerlei.de/springtools/tags"
%><%@
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%><c:set var="offset" value="${rs.query.attributes['hideId'] ? 1 : 0}"
/><c:set var="rowoffset" value="${fn:startsWith(root.columns[0].name, '--') ? 1 : 0}"
/><c:if test="${level == 0}"><table class="multiple">
		<thead>
			<tr>
				<th colspan="${rs.attributes['depth'] + 2}" title="${fn:escapeXml(root.columns[0 + offset + rowoffset].typeName)}">${fn:escapeXml(root.columns[0 + offset + rowoffset].name)}</th>
<c:forEach items="${root.columns}" var="c" begin="${1 + offset + rowoffset}"
>				<th title="${fn:escapeXml(c.typeName)}">${fn:escapeXml(c.name)}</th>
</c:forEach
>			</tr>
		</thead>
		<tbody></c:if><c:forEach items="${root.rows}" var="row"
			><c:set var="rowid" value="${left}${fn:contains(row.values[rowoffset], ' ') ? fn:substringBefore(row.values[rowoffset], ' ') : row.values[rowoffset]}"
			/><tr class="treerow" id="treerow-${label}-${rowid}"><c:forEach begin="1" end="${level}"><td>&nbsp;</td></c:forEach><td class="treebutton"><c:choose
					><c:when test="${not empty row.rows}"><span class="action" title="<fmt:message key="expand"/>" onclick="return toggleStaticTreeItem(event, '${label}', '${rowid}');">&#x25bc;</span></c:when
					><c:otherwise>&#x25b7;</c:otherwise
				></c:choose></td><c:forEach items="${row.values}" var="v" varStatus="st" begin="${0 + offset + rowoffset}" end="${0 + offset + rowoffset}"
					><td colspan="${rs.attributes['depth'] - level + 1}"><c:if test="${not empty rs.columns[st.index].targetQuery.queryName}">${rs.columns[st.index].targetQuery.queryName}: </c:if><ui:link value="${row.values[st.index]}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/></td></c:forEach
				><c:forEach items="${row.values}" var="v" varStatus="st" begin="${1 + offset + rowoffset}"
					><td><ui:link value="${row.values[st.index]}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/></td></c:forEach
			></tr><c:if test="${not empty row.rows}"><ui:result-statictree-singletable rs="${rs}" label="${label}" left="${rowid}-" root="${row}" level="${level + 1}" targetElement="${targetElement}"/></c:if></c:forEach
		><c:if test="${level == 0}"></tbody>
	</table></c:if>