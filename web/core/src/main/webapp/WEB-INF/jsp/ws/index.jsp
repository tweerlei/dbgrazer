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
%><fmt:message key="restAPI" var="pageTitle"/><%@
	include file="../include/ws-header.jspf"
%>
	<ui:headline1 label="${currentConnection.description}"/>
	
	<div class="tab-page"><div class="tab-body">
		<ui:headline2 key="loadedQueries"/>
		<ul>
			<li><a href="ws/${currentConnection.linkName}/form-result-export.html"><fmt:message key="downloadAs"><fmt:param value="Excel, PDF"/></fmt:message></a></li>
			<li><a href="ws/${currentConnection.linkName}/form-query-export.html"><fmt:message key="download"/></a></li>
			<li><a href="ws/${currentConnection.linkName}/form-query-fullexport.html"><fmt:message key="downloadAllRows"/></a></li>
		</ul><hr/>
<c:if test="${not empty extensions}"
>		<br/>
		<ui:headline2 key="${currentConnection.type}"/>
		<ul>
<c:forEach items="${extensions}" var="i"
>			<li><a href="${fn:escapeXml(i.href)}"><fmt:message key="${i.label}"/></a></li>
</c:forEach
>		</ul><hr/>
</c:if
>		<br/>
		<ui:headline2 key="currentSession"/>
		<ul>
			<li><a href="ws/${currentConnection.linkName}/denied.html"><fmt:message key="logout"/></a></li>
		</ul><hr/>
	</div></div>
<%@
	include file="../include/footer.jspf"
%>
