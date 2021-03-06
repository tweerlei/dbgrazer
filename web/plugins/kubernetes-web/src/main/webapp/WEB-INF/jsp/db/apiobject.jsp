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
%><c:set var="pageTitle" value="${name}"/><%@
	include file="../include/header.jspf"
%>
	<script type="text/javascript">/*<![CDATA[*/
	
	function submitForm(frm, p) {
		$('resulttype').value = p;
		getFormInto(frm, 'result');
		return false;
	}
	
	function submitDownloadForm(event, format) {
		return postForm('submitform', event, 'db/${currentConnection.linkName}/submit-simple-export.html', { resultformat: format }, '_blank');
	}
	
	function confirmSubmit(frm, p, title, content) {
		return showJSConfirmDialog(title, content, function() {
			return submitForm(frm, p);
		});
	}
	
	/*]]>*/</script>
	
	<ui:headline1 label="${pageTitle}">
	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
	<div class="h1-actions">
		<span class="action" title="<fmt:message key="refresh"/>" onclick="return reloadPage();"><fmt:message key="refreshIcon"/></span>
		<a class="action" title="<fmt:message key="newWindow"/>" href="db/${currentConnection.linkName}/apiobject.html?namespace=${namespace}&amp;kind=${kind}&amp;name=${name}" target="_blank"><fmt:message key="newWindowIcon"/></a>
	</div>
	<div id="tools-1" class="hidden"><div class="menucolumn">
<c:if test="${currentConnection.writable}"
>		<div class="menuitem"><a href="db/${currentConnection.linkName}/kube-apply.html?namespace=${namespace}&amp;kind=${kind}&amp;name=${name}" target="_blank"><fmt:message key="applyApiObject"/></a></div>
		<div class="menuitem"><span onclick="return showConfirmDialog('<fmt:message key="deleteApiObject"/>', '<fmt:message key="confirmDeleteApiObject"/>', 'db/${currentConnection.linkName}/kube-delete.html', { namespace: '${namespace}', kind: '${kind}', name: '${name}' });"><fmt:message key="deleteApiObject"/></span></div>
		<hr class="menuseparator"/>
</c:if
>		<div class="menuitem"><a href="#" onclick="return clearClusterCache();"><fmt:message key="clearCache"/></a></div>
	</div></div>
	</ui:headline1>
	
	<ui:tabs items="${tabs}" var="rs" varKey="label" varLink="detailLink" varParams="detailParams" varParamString="detailParamString" name="result"><%@
		include file="result/apiobject.jspf"
	%></ui:tabs>
<%@
	include file="../include/footer.jspf"
%>
