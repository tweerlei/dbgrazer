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
/><ui:headline2 label="${name}" zoomable="true">
<c:if test="${currentConnection.writable}"
>	<div class="h2-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-2');"><fmt:message key="actions"/></span>
	</div>
</c:if
>	<div class="h2-actions">
		<a class="action" title="<fmt:message key="showQuery"/>" href="db/${currentConnection.linkName}/apiobject.html?namespace=${namespace}&amp;kind=${kind}&amp;name=${name}"><fmt:message key="showQueryIcon"/></a>
	</div>
	<div id="tools-2" class="hidden"><div class="menucolumn">
		<div class="menuitem"><a href="db/${currentConnection.linkName}/kube-apply.html?namespace=${namespace}&amp;kind=${kind}&amp;name=${name}" target="_blank"><fmt:message key="applyApiObject"/></a></div>
        <div class="menuitem"><span onclick="return showConfirmDialog('<fmt:message key="deleteApiObject"/>', '<fmt:message key="confirmDeleteApiObject"/>', 'db/${currentConnection.linkName}/kube-delete.html', { namespace: '${namespace}', kind: '${kind}', name: '${name}' });"><fmt:message key="deleteApiObject"/></span></div>
	</div></div>
	</ui:headline2>
	
	<ui:tabs items="${tabs}" var="rs" varKey="label" varLink="detailLink" varParams="detailParams" varParamString="detailParamString" name="result"><%@
		include file="../result/apiobject.jspf"
	%></ui:tabs>
