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
%><c:set var="pageTitle" value="${topic}"/><%@
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
		<span class="action" title="<fmt:message key="refresh"/>" onclick="return reloadPage();">&#x21ba;</span>
		<a class="action" title="<fmt:message key="newWindow"/>" href="db/${currentConnection.linkName}/dir.html?path=${tools:urlEncode(path)}" target="_blank">&#x2750;</a>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
		<div class="menuitem"><span onclick="return showDbDialog(event, 'send-message', { topic: '${topic}', partition: '${partition}' }, '<fmt:message key="send"/>');"><fmt:message key="send"/></span></div>
	</div></div>
	</ui:headline1>
	
	<ui:tabs items="${tabs}" var="rs" varKey="label" varLink="detailLink" varParams="detailParams" varParamString="detailParamString" name="result"><%@
		include file="result/messages.jspf"
	%></ui:tabs>
<%@
	include file="../include/footer.jspf"
%>
