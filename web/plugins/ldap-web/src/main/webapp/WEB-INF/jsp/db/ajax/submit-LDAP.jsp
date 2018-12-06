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
</c:when><c:otherwise><style>/*<![CDATA[*/
<c:forEach items="${tableColumns}" var="tc" varStatus="st1"><c:forEach items="${tc}" var="col" varStatus="st2"><c:choose
><c:when test="${col.type.name == 'INTEGER'}"
>		#table-result${st1.index} td:nth-child(${st2.index + 1}) { text-align: right; }
</c:when><c:when test="${col.type.name == 'FLOAT'}"
>		#table-result${st1.index} td:nth-child(${st2.index + 1}) { text-align: right; }
</c:when
></c:choose></c:forEach
>	
</c:forEach
>	/*]]>*/</style>
		<div class="tab-header">
			<div id="sql" class="hidden"><pre>${fn:escapeXml(sql)}</pre></div>
			<div class="filter">
				<span class="button" onclick="return showElementDialog(event, '<fmt:message key="columnTypes"/>', 'sql');"><fmt:message key="columnTypes"/></span>
			</div>
			<div class="filter">
				<span class="nowrap"><fmt:message key="rowCount"><fmt:param value="${fn:length(rs.rows)}"/></fmt:message></span>
			</div>
			<div class="filter">
				<span class="action" title="<fmt:message key="maximize"/>" onclick="return toggleElement('zoomable1');"><fmt:message key="maximizeIcon"/></span>
			</div>
			<div class="filter">
				<span class="nowrap"><fmt:message key="duration"><fmt:param value="${rs.queryTime}"/></fmt:message></span>
			</div>
<c:if test="${rs.moreAvailable}"
>			<div class="filter">
				<strong><fmt:message key="moreData"/></strong>
			</div>
</c:if
>			<hr/>
		</div>
		<div class="tab-body">
			<ui:result-table rs="${rs}" label="result0"/>
		</div>
</c:otherwise></c:choose>