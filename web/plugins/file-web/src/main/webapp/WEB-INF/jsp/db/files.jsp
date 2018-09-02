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
%><fmt:message key="fileBrowser" var="pageTitle"/><%@
	include file="../include/header.jspf"
%><c:set var="targetElement" value="explorer-right"
/>
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
	
	function reloadPage() {
		return refreshDir();
	}
	
	/*]]>*/</script>
	
	<ui:headline1 label="${pageTitle}">
	<div class="h1-actions">
		<span class="action" title="<fmt:message key="refresh"/>" onclick="return reloadPage();"><fmt:message key="refreshIcon"/></span>
		<a class="action" title="<fmt:message key="newWindow"/>" href="db/${currentConnection.linkName}/files.html" target="_blank"><fmt:message key="newWindowIcon"/></a>
	</div>
	</ui:headline1>
	
	<div id="explorer-left"><ui:combo items="${tabs}" var="rs" varKey="label" varParams="detailParams" varParamString="detailParamString" name="combo"
		><div class="tab-body"><div class="treerow" id="treerow-${label}-"><div class="treebutton"><span class="action" title="<fmt:message key="expand"/>" onclick="return toggleDirTreeItem(event, '${label}', '', '', '${targetElement}');">&#x25bc;</span></div><div class="treelabel"><a href="db/${currentConnection.linkName}/dir.html?path=%2F" onclick="return showDir(event, '/');">/</a><c:if test="${currentConnection.writable}"
				> <span class="action" title="<fmt:message key="createDirectory"/>" onclick="return showDbDialog(event, 'file-mkdir', {path: '/', left: ''}, '<fmt:message key="createDirectory"/>');"><fmt:message key="addIcon"/></span></c:if
			></div><ui:result-dirtree rs="${rs}" label="${label}" targetElement="${targetElement}"
	/></div></div></ui:combo></div>
	
	<div id="explorer-right"><ui:headline2 label="/">
<c:if test="${currentConnection.writable}"
>	<div class="h2-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-2');"><fmt:message key="actions"/></span>
	</div>
	<div id="tools-2" class="hidden"><div class="menucolumn">
		<div class="menuitem"><span onclick="return showDbDialog(event, 'file-upload', {path: '${fn:escapeXml(path)}'}, '<fmt:message key="uploadFile"/>');"><fmt:message key="uploadFile"/></span></div>
	</div></div>
</c:if
>	</ui:headline2>
	
	<ui:tabs items="${tabs2}" var="rs" varKey="label" varLink="detailLink" varParams="detailParams" varParamString="detailParamString" name="result"><%@
		include file="result/dircontent.jspf"
	%></ui:tabs></div>
	<hr/>
<%@
	include file="../include/footer.jspf"
%>
