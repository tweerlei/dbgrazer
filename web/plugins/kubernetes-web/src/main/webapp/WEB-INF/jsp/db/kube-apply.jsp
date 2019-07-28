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
%><fmt:message key="applyApiObject" var="pageTitle"/><%@
	include file="../include/header.jspf"
%>
	<script type="text/javascript">/*<![CDATA[*/
	
	function submitForm(frm) {
		getFormInto(frm, 'result', null, null, true);
		return false;
	}
	
	/*]]>*/</script>
	
	<ui:headline1><jsp:attribute name="content">${pageTitle}
		&raquo; <a href="db/${currentConnection.linkName}/namespace.html?namespace=${namespace}">${namespace}</a>
		&raquo; <a href="db/${currentConnection.linkName}/namespace.html?namespace=${namespace}&amp;kind=${kind}">${kind}</a>
	</jsp:attribute></ui:headline1>
	
	<div class="tab-page"><div id="zoomable1" class="tab-header">
		<form id="submitform" class="full" action="db/${currentConnection.linkName}/ajax/kube-apply.html" method="post" onsubmit="return submitForm(this);">
			<input type=hidden name="namespace" value="${namespace}"/>
			<input type=hidden name="kind" value="${kind}"/>
			<dl>
				<dt><label for="f1-name"><fmt:message key="name"/></label></dt>
				<dd><input type="text" id="f1-name" name="name" value="${name}"/></dd>
				<dt><label for="f1-content"><fmt:message key="object"/></label></dt>
				<dd><textarea id="f1-content" name="content" cols="80" rows="25">${content}</textarea></dd>
				<dt><label for="f1-mode"><fmt:message key="executeAs"/></label></dt>
				<dd><select id="f1-mode" name="mode">
<c:forEach items="${modes}" var="t"
>					<option value="${t}">${t}</option>
</c:forEach
>				</select></dd>
				<dt>&nbsp;</dt>
				<dd><input id="f1-submit" type="submit" value="<fmt:message key="apply"/>"/></dd>
			</dl><hr/>
		</form>
	</div><div id="result"><div class="tab-body">
	</div></div></div>
<%@
	include file="../include/footer.jspf"
%>
