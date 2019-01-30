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
%><fmt:message key="ldapBrowser" var="pageTitle"/><%@
	include file="../include/header.jspf"
%><c:set var="targetElement" value="explorer-right"
/>
	<script type="text/javascript">/*<![CDATA[*/
	
	function reloadPage() {
		return refreshEntry();
	}
	
	/*]]>*/</script>
	
	<ui:headline1 label="${pageTitle}">
	<div class="h1-actions">
		<span class="action" title="<fmt:message key="refresh"/>" onclick="return reloadPage();"><fmt:message key="refreshIcon"/></span>
		<a class="action" title="<fmt:message key="newWindow"/>" href="db/${currentConnection.linkName}/ldap.html" target="_blank"><fmt:message key="newWindowIcon"/></a>
	</div>
	</ui:headline1>
	
	<div id="explorer-left"><ui:combo items="${tabs}" var="rs" varKey="label" varParams="detailParams" varParamString="detailParamString" name="combo"
		><div class="tab-body"><div class="treerow" id="treerow-${label}-"><div class="treebutton"><span class="action" title="<fmt:message key="expand"/>" onclick="return toggleLdapTreeItem(event, '${label}', '', '', '${targetElement}');">&#x25bc;</span></div><div class="treelabel"><a href="db/${currentConnection.linkName}/entry.html?path=" onclick="return showEntry(event, '');"><fmt:message key="ldapRoot"/></a></div><ui:result-ldaptree rs="${rs}" label="${label}" targetElement="${targetElement}"
	/></div></div></ui:combo></div>
	
	<div id="explorer-right"><ui:headline2 key="ldapRoot"/>
	
	<ui:tabs items="${tabs2}" var="rs" varKey="label" varLink="detailLink" varParams="detailParams" varParamString="detailParamString" name="result"><%@
		include file="result/attributes.jspf"
	%></ui:tabs></div>
	<hr/>
<%@
	include file="../include/footer.jspf"
%>
