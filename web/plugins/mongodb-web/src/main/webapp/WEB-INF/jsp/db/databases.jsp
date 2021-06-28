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
%><fmt:message key="mongoDBBrowser" var="pageTitle"/><%@
	include file="../include/header.jspf"
%><c:set var="targetElement" value="explorer-right"
/>
	<script type="text/javascript">/*<![CDATA[*/
	
	function reloadPage() {
		return refreshDatabase();
	}
	
	/*]]>*/</script>
	
	<ui:headline1 label="${pageTitle}">
	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
	<div class="h1-actions">
		<span class="action" title="<fmt:message key="refresh"/>" onclick="return reloadPage();"><fmt:message key="refreshIcon"/></span>
		<a class="action" title="<fmt:message key="newWindow"/>" href="db/${currentConnection.linkName}/topics.html" target="_blank"><fmt:message key="newWindowIcon"/></a>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
<c:if test="${currentConnection.writable}"
>		<div class="menuitem"><span onclick="return showDbDialog(event, 'save-document', null, '<fmt:message key="send"/>');"><fmt:message key="send"/></span></div>
</c:if
>	</div></div>
	</ui:headline1>
	
	<div id="submitresult"></div>
	
	<c:set var="links" value="db/${currentConnection.linkName}/databases.html"
	/><c:set var="links" value="${fn:split(links, ',')}"
	/><div id="explorer-left"><ui:multilevel query="${query.name}" levels="${query.subQueries}" params="${params}" links="${links}" items="${tabs}" var="rs" varKey="label" varParams="detailParams" varParamString="detailParamString" name="combo"
		><ui:result-database rs="${rs}" label="${label}" link="db/${currentConnection.linkName}/collections.html?database=%%" targetElement="${targetElement}"
	/></ui:multilevel></div>

	<div id="explorer-right"><ui:tabs items="${results}" var="rs" varKey="label" varLink="detailLink" varParams="detailParams" varParamString="detailParamString" name="result"><%@
        include file="result/tab.jspf"
    %></ui:tabs
    ></div>
	<hr/>
<%@
	include file="../include/footer.jspf"
%>
