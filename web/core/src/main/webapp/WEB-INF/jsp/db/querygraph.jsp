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
%><c:set var="pageTitle" value="${title}"/><%@
	include file="../include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}">
	<div class="h1-actions">
<c:if test="${currentConnection.editorActive}"
>		<a class="action" title="<fmt:message key="editQuery"/>" href="db/${currentConnection.linkName}/edit.html?q=${title}">&#x270e;</a>
</c:if
>	</div>
	</ui:headline1>
	
	<div class="tab-page"><div class="tab-body">
		${imagemap}
		<img src="db/${currentConnection.linkName}/qgraph.html?q=${title}&amp;key=${imageId}&amp;t=${currentDate.time}" usemap="#${imagemapId}" class="scaled" onclick="return toggleScaling(this);" alt="<fmt:message key="imageLoading"/>" onload="imageLoaded(event);" onerror="imageLoadingFailed(event);" title="${title}"/>
	</div></div>
<%@
	include file="../include/footer.jspf"
%>
