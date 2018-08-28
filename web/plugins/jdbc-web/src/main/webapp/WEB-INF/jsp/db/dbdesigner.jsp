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
%><fmt:message key="designer" var="pageTitle"/><%@
	include file="../include/header.jspf"
%><ui:objerrors label="" var="e"> <a href="db/${currentConnection.linkName}/dbdesigner-remove.html?catalog=${e.catalogName}&amp;schema=${e.schemaName}&amp;object=${e.objectName}"><fmt:message key="remove"/></a></ui:objerrors>
	<ui:headline1 label="${pageTitle}: ${currentConnection.design.name}${currentConnection.design.modified ? '*' : ''}">
	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
<c:if test="${not empty imagemapId}"
>	<div class="h1-actions">
		<span class="menu" onclick="showDbMenu(event, 'designlinks');"><fmt:message key="download"/></span>
	</div>
</c:if
>	
	<div id="tools-1" class="hidden"><div class="menucolumn">
		<div class="menuitem"><span onclick="return showDbDialog(event, 'design-add', null, '<fmt:message key="add"/>');"><fmt:message key="add"/></span></div>
		<div class="menuitem"><span onclick="return showDbDialog(event, 'design-addall', null, '<fmt:message key="addAll"/>');"><fmt:message key="addAll"/></span></div>
<c:if test="${not empty imagemapId}"
>		<div class="menuitem"><span onclick="return showDbDialog(event, 'design-save', { q: '${currentConnection.design.name}' }, '<fmt:message key="save"/>');"><fmt:message key="save"/></span></div>
</c:if
>		<div class="menuitem"><span onclick="return showDbDialog(event, 'design-load', { q: '${currentConnection.design.name}' }, '<fmt:message key="load"/>');"><fmt:message key="load"/></span></div>
		<div class="menuitem"><span onclick="return showConfirmDialog('<fmt:message key="reset"/>', '<fmt:message key="resetText"/>', 'db/${currentConnection.linkName}/dbdesigner-reset.html');"><fmt:message key="reset"/></span></div>
		<hr class="menuseparator"/>
		<div class="menuitem"><span onclick="return showDbDialog(event, 'design-data', { q: '${currentConnection.design.name}' }, '<fmt:message key="downloadData"/>');"><fmt:message key="downloadData"/></span></div>
		<div class="menuitem"><a href="db/${currentConnection.linkName}/dbdesigner-compare.html" target="_blank"><fmt:message key="structureCompare"/></a></div>
		<div class="menuitem"><a href="db/${currentConnection.linkName}/dbdesigner-count.html" target="_blank"><fmt:message key="countCompare"/></a></div>
	</div></div>
	</ui:headline1>
	
	<div class="tab-page">
<c:choose><c:when test="${empty imagemapId}"
>		<div class="tab-body"><fmt:message key="noData"/></div>
</c:when><c:otherwise
>		<div class="tab-header">
			<div id="tools-2" class="hidden"><div class="menucolumn">
				<div class="menuitem"><span onclick="return postForm('dlform', event, 'db/${currentConnection.linkName}/designgraph-image.html', null, '_blank');"><fmt:message key="Image"/></span></div>
<c:if test="${currentUser.dotDisplayEnabled}"
>				<div class="menuitem"><span onclick="return postForm('dlform', event, 'db/${currentConnection.linkName}/designgraph-source.html', null, '_blank');"><fmt:message key="DOT"/></span></div>
</c:if
>			</div></div>
			<div class="filter">
				<span class="menu" onclick="return showElementMenu(event, 'tools-2');"><fmt:message key="download"/></span>
			</div>
			<form id="dlform" class="filter" action="db/${currentConnection.linkName}/dbdesigner.html" method="get" onsubmit="return false;">
				<input type="hidden" name="preview" value="${!currentConnection.designerPreviewMode}"/>
				<input id="preview" type="checkbox" name="dummy" value="true"<c:if test="${currentConnection.designerPreviewMode}"> checked="checked"</c:if> onchange="postForm(form, event);"/> <label for="preview"><fmt:message key="previewMode"/></label>
			</form><form class="filter" action="db/${currentConnection.linkName}/dbdesigner.html" method="get" onsubmit="return false;">
				<input type="hidden" name="compact" value="${!currentConnection.designerCompactMode}"/>
				<input id="compact" type="checkbox" name="dummy" value="true"<c:if test="${currentConnection.designerCompactMode}"> checked="checked"</c:if> onchange="postForm(form, event);"/> <label for="compact"><fmt:message key="compactMode"/></label>
			</form><form class="filter" action="db/${currentConnection.linkName}/dbdesigner.html" method="get" onsubmit="return false;">
				<input type="hidden" name="sort" value="${!currentConnection.sortColumns}"/>
				<input id="sort" type="checkbox" name="dummy" value="true"<c:if test="${currentConnection.sortColumns}"> checked="checked"</c:if> onchange="postForm(form, event);"/> <label for="sort"><fmt:message key="sortColumns"/></label>
			</form>
			<hr/>
		</div>
		<div class="tab-body">
			${imagemap}
			<img src="db/${currentConnection.linkName}/designgraph.html?key=${imageId}&amp;t=${currentDate.time}" usemap="#${imagemapId}" style="max-width: 100%;" onclick="return toggleScaling(this);" alt="<fmt:message key="imageLoading"/>" onload="imageLoaded(event);" onerror="imageLoadingFailed(event);"/>
		</div>
</c:otherwise></c:choose
>	</div>
<%@
	include file="../include/footer.jspf"
%>
