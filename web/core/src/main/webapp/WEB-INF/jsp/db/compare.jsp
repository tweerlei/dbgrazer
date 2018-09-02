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
	include file="../include/include.jspf"
%><fmt:message key="resultCompareHeader" var="pageTitle"><fmt:param value="${title}"/></fmt:message><%@
	include file="../include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}">
	<form class="h1-actions" action="db/${model.connection2}/compare.html" method="get">
		<a href="db/${currentConnection.linkName}/result.html?q=${model.query.name}${paramString}">${currentConnection.linkName}</a>
		<fmt:message key="to"/>
		<a href="db/${model.connection2}/result.html?q=${model.query.name}${paramString2}">${model.connection2}</a>
		<input type="hidden" name="q" value="${model.query.name}"/>
		<input type="hidden" name="connection2" value="${currentConnection.linkName}"/>
		<ui:hidden items="${model.params2}" name="params"/>
		<ui:hidden items="${model.params}" name="params2"/>
		<input type="submit" value="<fmt:message key="reverse"/>"/>
	</form>
	
	<form id="h1-form" class="h1-actions" action="#" method="get" onsubmit="return showFormDialog(event, 'db:compare2', this, '<fmt:message key="change"/>');">
		<input id="queryName" type="hidden" name="q" value="${model.query.name}"/>
		<ui:hidden items="${model.params}" name="params"/>
		<ui:hidden items="${model.params2}" name="params2"/>
		<input type="hidden" name="connection2" value="${model.connection2}"/>
		<input id="h1fmt" type="hidden" name="format" value=""/>
<c:if test="${currentConnection.editorEnabled && (model.query.sourceSchema.subschema)}"
>		<ui:info name="connectionSpecific"><fmt:message key="subschemaQuery"><fmt:param value="${currentConnection.schemaVersion}"/></fmt:message></ui:info>
		&nbsp;
</c:if
>		<input type="submit" value="<fmt:message key="change"/>"/>
	</form>
<c:if test="${!model.query.type.explorer && (not empty formats)}"
>	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
</c:if
>	<div class="h1-actions">
		<span class="action" title="<fmt:message key="refresh"/>" onclick="return reloadPage();"><fmt:message key="refreshIcon"/></span>
		<span><span class="action" title="<fmt:message key="autorefresh"/>" onclick="return toggleAutoRefresh(event);"><fmt:message key="autorefreshIcon"/> <span id="next-auto-refresh"></span></span></span>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
<c:forEach items="${formats}" var="f"
>		<div class="menuitem"><span onclick="return postForm('h1-form', event, 'db/${currentConnection.linkName}/compare-export-full.html', {h1fmt:'${f}'});"><fmt:message key="${f}" var="formatName"/><fmt:message key="downloadAs"><fmt:param value="${formatName}"/></fmt:message></span></div>
</c:forEach
>	</div></div>
	</ui:headline1>
	
<fmt:message key="editQuery" var="editQuery"/><fmt:message key="showQuery" var="showQuery"/><c:choose><%--
***********************************************************************
*
* Dashboard view
*
***********************************************************************
--%><c:when test="${model.query.type.name == 'DASHBOARD'}"
>	<ui:dashboard items="${results}" var="rs" varKey="label" varLink="detailLink" varParams="detailParams" varParamString="detailParamString" name="panel"
		orientation="${model.query.attributes['orientation']}"
		editTitle="${editQuery}" editLinkTemplate="db/${currentConnection.linkName}/edit.html?q=%%&amp;backTo=${model.query.name}"
		detailTitle="${showQuery}" detailLinkTemplate="db/${currentConnection.linkName}/result.html?q=%%"><%@
		include file="result/dashboard.jspf"
	%></ui:dashboard>
</c:when><%--
***********************************************************************
*
* Panel view
*
***********************************************************************
--%><c:when test="${model.query.type.name == 'PANELS'}"
>	<ui:panels items="${results}" var="rs" varKey="label" varLink="detailLink" varTarget="targetElement" varParams="detailParams" varParamString="detailParamString" name="panel"
		editTitle="${editQuery}" editLinkTemplate="db/${currentConnection.linkName}/edit.html?q=%%&amp;backTo=${model.query.name}"
		detailTitle="${showQuery}" detailLinkTemplate="db/${currentConnection.linkName}/result.html?q=%%"><%@
		include file="result/panel.jspf"
	%></ui:panels>
</c:when><%--
***********************************************************************
*
* Tabbed view
*
***********************************************************************
--%><c:otherwise><c:if test="${model.query.type.name == 'EDITABLE'}"
>	<script type="text/javascript">/*<![CDATA[*/
	
	// on reload, re-submit the form
	function reloadPage() {
		return aggsubmit($('aggform1'), 1);
	}
	
	/*]]>*/</script>
</c:if
>	
	<ui:tabs items="${results}" var="rs" varKey="label" varLink="detailLink" varParams="detailParams" varParamString="detailParamString" name="result"
		editTitle="${editQuery}" editLinkTemplate="db/${currentConnection.linkName}/edit.html?q=%%&amp;backTo=${model.query.name}"
		detailLinkTemplate="db/${currentConnection.linkName}/result.html?q=%%"><%@
		include file="result/tab.jspf"
	%></ui:tabs>
</c:otherwise></c:choose
><%@
	include file="../include/footer.jspf"
%>
