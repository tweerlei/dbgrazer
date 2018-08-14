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
	tag description="Custom tag that displays an SQLExecutionPlan object"
%><%--
	Glassfish bug: Application JARs are not on the JSP precompile classpath
	attribute name="plan" required="true" type="de.tweerlei.ermtools.dialect.SQLExecutionPlan" rtexprvalue="true" description="The SQLExecutionPlan"
--%><%@
	attribute name="plan" required="true" type="java.lang.Object" rtexprvalue="true" description="The SQLExecutionPlan"
%><%@
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%><div class="treerow" id="treerow-${label}-${plan.id}"><div class="treebutton"><c:choose
	><c:when test="${not empty plan.children}"><span class="action" title="<fmt:message key="expand"/>" onclick="return toggleStaticTreeItem(event, '${label}', '${plan.id}');">&#x25bc;</span></c:when
	><c:otherwise>&#x25b7;</c:otherwise
></c:choose></div><div class="treelabel">
	<div class="plan-title">${fn:escapeXml(plan.operation)} ${fn:escapeXml(plan.objectName)} ${fn:escapeXml(plan.other)}</div>
	<div class="plan-detail">${fn:escapeXml(plan.filter)} Rows: ${plan.rows} Cost: ${plan.cost}</div>
</div>
<c:forEach items="${plan.children}" var="c"><ui:plan label="${label}" plan="${c}"/></c:forEach>
</div>