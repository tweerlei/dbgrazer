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
	tag description="Custom tag that generates tree rows from a TableDescription"
%><%@
	attribute name="infos" required="true" type="java.util.Map" rtexprvalue="true" description="Map: Table display name -> TableDescription"
%><%@
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="dir" required="true" type="java.lang.Boolean" rtexprvalue="true" description="The direction (true = up, false = down)"
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
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%><c:forEach items="${infos}" var="i"><c:set var="rowid" value="${i.key}"
		/><div class="treerow" id="treerow-${label}-${left}${rowid}"><div class="treebutton"><c:choose
			><c:when test="${dir && not empty i.value.info.referencedKeys}"><span class="action" title="<fmt:message key="expand"/>" onclick="return toggleTableTreeItem(event, '${label}', '${i.value.info.name.catalogName}', '${i.value.info.name.schemaName}', '${i.value.info.name.objectName}', '${dir}', '${left}${rowid}', '${targetElement}');">&#x25ba;</span></c:when
			><c:when test="${!dir && not empty i.value.info.referencingKeys}"><span class="action" title="<fmt:message key="expand"/>" onclick="return toggleTableTreeItem(event, '${label}', '${i.value.info.name.catalogName}', '${i.value.info.name.schemaName}', '${i.value.info.name.objectName}', '${dir}', '${left}${rowid}', '${targetElement}');">&#x25ba;</span></c:when
			><c:otherwise>&#x25b7;</c:otherwise
		></c:choose></div><div class="treelabel"><div class="plan-title"><a href="db/${currentConnection.linkName}/dbobject.html?catalog=${i.value.info.name.catalogName}&amp;schema=${i.value.info.name.schemaName}&amp;object=${i.value.info.name.objectName}"<c:if test="${not empty targetElement}"
			> onclick="return showDBObject(event, '${i.value.info.name.catalogName}', '${i.value.info.name.schemaName}', '${i.value.info.name.objectName}');"</c:if
			>>${i.key}</a></div>
<c:if test="${i.value.fk != null}"
>			<div class="plan-detail"><c:forEach items="${i.value.fk.columns}" var="c" varStatus="st"><c:if test="${!st.first}">, </c:if>${c.key}</c:forEach> &#x2192; <c:forEach items="${i.value.fk.columns}" var="c" varStatus="st"><c:if test="${!st.first}">, </c:if>${c.value}</c:forEach></div></c:if
></div></div></c:forEach>