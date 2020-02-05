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
	include file="../../include/include.jspf"
%><c:choose><c:when test="${exception != null}">
		<div class="tab-body">
			<pre>${fn:escapeXml(exception.message)}</pre>
		</div>
</c:when><c:when test="${rs == null || fn:length(rs.rows) == 0}">
		<div class="tab-body">
			<strong><fmt:message key="noData"/></strong>
		</div>
</c:when><c:otherwise>
		<div class="tab-header">
			<div id="sql" class="hidden"><pre><c:forEach items="${headers.rows}" var="row"
>${fn:escapeXml(row.values[0])}: ${fn:escapeXml(row.values[1])}
</c:forEach></pre></div>
			<div class="filter">
				<span class="action" title="<fmt:message key="maximize"/>" onclick="return toggleElement('zoomable1');"><fmt:message key="maximizeIcon"/></span>
			</div>
			<form class="filter" action="#" method="get" onsubmit="return false;"><div class="filter">
				<input id="format-result" type="checkbox" name="dummy1" value="true"<c:if test="${!rs.attributes['verbatim']}"> checked="checked"</c:if> onchange="toggleFormatMode('', ${rs.attributes['verbatim'] ? 'true' : 'false'});"/> <label for="format-result"><fmt:message key="format"><fmt:param value="${rs.query.attributes['formatter']}"/></fmt:message></label>
			</div></form>
			<form class="filter" action="#" method="get" onsubmit="return false;"><div class="filter">
				<input id="coloring-result" type="checkbox" name="dummy2" value="true"<c:if test="${!rs.attributes['plainColoring']}"> checked="checked"</c:if> onchange="toggleColoringMode('', ${rs.attributes['plainColoring'] ? 'true' : 'false'});"/> <label for="coloring-result"><fmt:message key="syntaxColoring"/></label>
			</div></form>
			<form class="filter" action="#" method="get" onsubmit="return false;"><div class="filter">
				<input id="lineno-result" type="checkbox" name="dummy4" value="true"<c:if test="${rs.attributes['lineNumbers']}"> checked="checked"</c:if> onchange="toggleLineNumberMode('', ${rs.attributes['lineNumbers'] ? 'false' : 'true'});"/> <label for="lineno-result"><fmt:message key="lineNumbers"/></label>
			</div></form>
			<div class="filter">
				<span class="button" onclick="return showElementDialog(event, '<fmt:message key="headerTab"/>', 'sql');"><fmt:message key="showHeaders"/></span>
			</div>
			<div class="filter">
				<fmt:message key="duration"><fmt:param value="${rs.queryTime}"/></fmt:message>
			</div>
<c:if test="${rs.moreAvailable}"
>			<div class="filter">
				<strong><fmt:message key="moreData"/></strong>
			</div>
</c:if
>			<hr/>
		</div>
		<div class="tab-body">
			<pre id="text-result">${rs.firstValue}</pre>
		</div>
</c:otherwise></c:choose>
