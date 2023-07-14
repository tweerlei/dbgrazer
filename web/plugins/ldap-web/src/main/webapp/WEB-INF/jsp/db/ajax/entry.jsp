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
%><c:set var="targetElement" value="explorer-right"
/><ui:headline2 label="${path}" key="ldapRoot" zoomable="true">
	<form id="export-form" class="h2-actions" action="#" method="get" onsubmit="return false;">
		<input type="hidden" name="path" value="${fn:escapeXml(path)}"/>
		<input id="exportfmt" type="hidden" name="format" value=""/>
		<span class="menu" onclick="showDbMenu(event, 'ldaplinks');"><fmt:message key="download"/></span>
	</form>
	<div class="h2-actions">
		<span class="menu" onclick="showDbMenu(event, 'ldapsublinks');"><fmt:message key="downloadSubtree"/></span>
	</div>
	<div class="h2-actions">
		<a class="action" title="<fmt:message key="showQuery"/>" href="db/${currentConnection.linkName}/entry.html?path=${tools:urlEncode(path)}"><fmt:message key="showQueryIcon"/></a>
	</div>
	</ui:headline2>
	
	<ui:tabs items="${tabs}" var="rs" varKey="label" varLink="detailLink" varParams="detailParams" varParamString="detailParamString" name="result"><%@
		include file="../result/attributes.jspf"
	%></ui:tabs>
