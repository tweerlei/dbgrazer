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
%><c:set var="pageTitle" value="${title}"/><%@
	include file="../include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}" group="${model.query.groupName}">
	<ui:search/>
	<form id="h1-form" class="h1-actions" action="db/${currentConnection.linkName}/query.html" method="get" onsubmit="return showFormDialog(event, 'db:query', this, '<fmt:message key="change"/>');">
		<input id="queryName" type="hidden" name="q" value="${model.query.name}"/>
<c:forEach items="${model.params}" var="p"
><c:if test="${(additionalParams != null) && (p.key >= fn:length(model.query.parameters)) && (p.key < fn:length(model.query.parameters) + fn:length(additionalParams))}"
>		<em>${fn:escapeXml(additionalParams[p.key - fn:length(model.query.parameters)].name)} = ${fn:escapeXml(p.value)}</em> <span class="action" title="<fmt:message key="remove"/>" onclick="return submitFormWithout('h1-form', event, 'db/${currentConnection.linkName}/result.html', 'qparams${p.key}');">&#x2716;</span>
		&nbsp;
</c:if
>		<input type="hidden" id="qparams${p.key}" name="params[${p.key}]" value="${fn:escapeXml(p.value)}"/>
</c:forEach
>		<input id="h1fmt" type="hidden" name="format" value=""/>
<c:if test="${currentConnection.editorEnabled && (model.query.sourceSchema.subschema)}"
>		<ui:info name="connectionSpecific"><fmt:message key="subschemaQuery"><fmt:param value="${currentConnection.schemaVersion}"/></fmt:message></ui:info>
		&nbsp;
</c:if><c:if test="${not empty model.params}"
>		<input type="submit" value="<fmt:message key="change"/>"/>
</c:if
>	</form>
<c:if test="${model.query.type.name == 'TIMECHART'}"
>	<div class="h1-actions">
		<span class="button" onclick="return resetTimechart();"><fmt:message key="reset"/></span>
	</div>
</c:if><c:if test="${!model.query.type.explorer && (not empty formats)}"
>	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
</c:if
>	<div class="h1-actions">
<c:forEach items="${extensions}" var="i"><c:choose
><c:when test="${not empty i.onclick}"
>		<span class="action" title="<fmt:message key="${i.title}"/>" onclick="${fn:escapeXml(i.onclick)}"><fmt:message key="${i.label}"/></span>
</c:when><c:otherwise
>		<a class="action" title="<fmt:message key="${i.title}"/>" href="${fn:escapeXml(i.href)}"><fmt:message key="${i.label}"/></a>
</c:otherwise></c:choose></c:forEach
>		<span class="action" title="<fmt:message key="refresh"/>" onclick="return reloadPage();">&#x21ba;</span>
		<span><span class="action" title="<fmt:message key="autorefresh"/>" onclick="return toggleAutoRefresh(event);">&#x231a; <span id="next-auto-refresh"></span></span></span>
		<a class="action" title="<fmt:message key="newWindow"/>" href="db/${currentConnection.linkName}/result.html?q=${model.query.name}${paramString}" target="_blank">&#x2750;</a>
<c:if test="${currentConnection.editorActive}"
>		<a class="action" title="<fmt:message key="editQuery"/>" href="db/${currentConnection.linkName}/edit.html?q=${model.query.name}">&#x270e;</a>
</c:if
>	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
<c:forEach items="${formats}" var="f"
>		<div class="menuitem"><span onclick="return postForm('h1-form', event, 'db/${currentConnection.linkName}/result-export.html', {h1fmt:'${f}'});"><fmt:message key="${f}" var="formatName"/><fmt:message key="downloadAs"><fmt:param value="${formatName}"/></fmt:message></span></div>
</c:forEach
>		<hr class="menuseparator"/>
		<div class="menuitem"><span onclick="return generateResultLink(event, '<fmt:message key="permalink"/>', 'h1-form');"><fmt:message key="permalink"/></span></div>
		<hr class="menuseparator"/>
		<div class="menuitem"><span onclick="return showDbDialog(event, 'compare', 'q=${model.query.name}${paramString}', '${model.query.name}');"><fmt:message key="fullCompare"/></span></div>
	</div></div>
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
* Drill down view
*
***********************************************************************
--%><c:when test="${model.query.type.name == 'DRILLDOWN'}"
>	<ui:panels level="0" items="${results}" var="rs" varKey="label" varLink="detailLink" varTarget="targetElement" varParams="detailParams" varParamString="detailParamString" name="panel"
		editTitle="${editQuery}" editLinkTemplate="db/${currentConnection.linkName}/edit.html?q=%%&amp;backTo=${model.query.name}"
		detailTitle="${showQuery}" detailLinkTemplate="db/${currentConnection.linkName}/result.html?q=%%"><%@
		include file="result/panel.jspf"
	%></ui:panels>
</c:when><%--
***********************************************************************
*
* Explorer view
*
***********************************************************************
--%><c:when test="${model.query.type.name == 'EXPLORER'}"><c:set var="targetElement" value="explorer-right"
/>	<script type="text/javascript">/*<![CDATA[*/
	
	// on reload (caused by changing the chart type), reload the last query
	function reloadPage() {
		rerunQuery();
		return false;
	}
	
	/*]]>*/</script>
	
<c:if test="${not empty detailQuery}"
>	<span id="detailQuery" onclick="return runQuery(event, '${detailQuery}', '${paramString}', '${targetElement}');"></span>
</c:if
>	<ui:explorer><ui:combo items="${results}" var="rs" varKey="label" varParams="detailParams" varParamString="detailParamString" name="combo"
		editTitle="${editQuery}" editLinkTemplate="db/${currentConnection.linkName}/edit.html?q=%%&amp;backTo=${model.query.name}"><%@
		include file="result/tab.jspf"
	%></ui:combo></ui:explorer>
</c:when><%--
***********************************************************************
*
* Navigator view
*
***********************************************************************
--%><c:when test="${model.query.type.name == 'NAVIGATOR'}"><c:set var="targetElement" value="explorer-right"
/>	<script type="text/javascript">/*<![CDATA[*/
	
	// on reload (caused by changing the chart type), reload the last query
	function reloadPage() {
		rerunQuery();
		return false;
	}
	
	/*]]>*/</script>
	
	<div id="explorer-left"><div class="combo-head"><fmt:message key="navigation"/></div>
		<div id="left-content"><div class="tab-page"><div class="tab-body"><ui:result-related rs="${rs}" params="${model.params}" paramString="${paramString}" targetElement="${targetElement}"/></div></div></div>
	</div>
	
	<div id="explorer-right"><%@
		include file="result/explorer.jspf"
	%></div>
	<hr/>
</c:when><%--
***********************************************************************
*
* Multilevel view
*
***********************************************************************
--%><c:when test="${model.query.type.name == 'MULTILEVEL'}"><c:set var="targetElement" value="explorer-right"
/>	<script type="text/javascript">/*<![CDATA[*/
	
	// on reload (caused by changing the chart type), reload the last query
	function reloadPage() {
		rerunQuery();
		return false;
	}
	
	/*]]>*/</script>
	
	<ui:explorer><ui:multilevel query="${model.query.name}" subQuery="${subquery.name}" levels="${model.query.subQueries}" params="${model.params}" items="${results}" var="rs" varKey="label" varParams="detailParams" varParamString="detailParamString" name="combo"
		editTitle="${editQuery}" editLinkTemplate="db/${currentConnection.linkName}/edit.html?q=%%&amp;backTo=${model.query.name}"><%@
		include file="result/tab.jspf"
	%></ui:multilevel></ui:explorer>
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
		return aggsubmit($('aggform-result0'), 'result0');
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
