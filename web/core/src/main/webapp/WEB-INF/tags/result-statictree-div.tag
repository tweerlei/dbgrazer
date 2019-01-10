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
/><c:forEach items="${root.rows}" var="row" varStatus="rst"
		><c:set var="rowid" value="${left}${rst.index}"
		/><div class="treerow" id="treerow-${label}-${rowid}"><div class="treebutton"><c:choose
			><c:when test="${not empty row.rows}"><span class="action" title="<fmt:message key="expand"/>" onclick="return toggleStaticTreeItem(event, '${label}', '${rowid}');">&#x25bc;</span></c:when
			><c:otherwise>&#x25b7;</c:otherwise
		></c:choose></div><div class="treelabel"><c:forEach items="${row.values}" var="v" varStatus="st" begin="${0 + offset + rowoffset}" end="${1 + offset + rowoffset}"><c:choose
				><c:when test="${st.first}"><c:if test="${not empty rs.columns[st.index].targetQuery.queryName}">${rs.columns[st.index].targetQuery.queryName}: </c:if><ui:link value="${row.values[st.index]}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/></c:when
				><c:when test="${st.last}"> <em><ui:link value="${row.values[st.index]}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/></em></c:when
			></c:choose></c:forEach
			><c:if test="${fn:length(row.values) > 2 + offset + rowoffset}">
	<div class="plan-detail"><ui:link value="${row.values[2 + offset + rowoffset]}" target="${rs.columns[tools:toInt(2 + offset + rowoffset)].targetQuery}" targetElement="${targetElement}"/><c:if test="${fn:length(row.values) > 3 + offset + rowoffset}"> <ui:info name="${label}-${rowid}"><c:forEach items="${row.values}" var="v" varStatus="st" begin="${3 + offset + rowoffset}"
				>${fn:escapeXml(root.columns[st.index].name)} = <ui:link value="${v}" target="${rs.columns[st.index].targetQuery}" targetElement="${targetElement}"/>
</c:forEach></ui:info></c:if
			></div></c:if
			></div><ui:result-statictree-div rs="${rs}" label="${label}" left="${rowid}-" root="${row}" targetElement="${targetElement}"/></div>
</c:forEach>