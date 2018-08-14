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
	include file="include/include.jspf"
%><fmt:message key="errorOccurred" var="pageTitle"/><%@
	include file="include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}"/>

	<div class="tab-page"><div class="tab-body">
		<p><fmt:message key="confError"/></p>
		
		<p><fmt:message key="errorQuery"/>: ${fn:escapeXml(query)}<c:if test="${currentConnection.editorEnabled}">
			<a class="action" title="<fmt:message key="editQuery"/>" href="db/${currentConnection.linkName}/edit.html?q=${fn:escapeXml(query)}">&#x270e;</a>
		</c:if></p>
		
		<p><span class="link" onclick="return goBack();"><fmt:message key="back"/></span></p>
	</div></div>
<%@
	include file="include/footer.jspf"
%>
