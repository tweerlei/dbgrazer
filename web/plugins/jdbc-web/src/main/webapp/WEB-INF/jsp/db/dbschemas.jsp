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
%><ui:set text="${catalog}" key="defaultCatalog" var="pageTitle"/><%@
	include file="../include/header.jspf"
%>
	<ui:headline1><jsp:attribute name="content"><a href="db/${currentConnection.linkName}/dbcatalogs.html"><fmt:message key="schemaBrowser"/></a>
		&raquo; ${pageTitle}
	</jsp:attribute><jsp:body>
	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
	<div id="tools-1" class="hidden"><div class="menucolumn">
		<div class="menuitem"><a href="#" onclick="return clearDbCache();"><fmt:message key="clearCache"/></a></div>
	</div></div>
	</jsp:body></ui:headline1>
	
	<c:set var="links" value="db/${currentConnection.linkName}/dbcatalogs.html,db/${currentConnection.linkName}/dbschemas.html?catalog=${catalog}"
	/><c:set var="links" value="${fn:split(links, ',')}"
	/><ui:explorer><ui:multilevel query="${query.name}" levels="${query.subQueries}" params="${params}" links="${links}" items="${tabs}" var="rs" varKey="label" varParams="detailParams" varParamString="detailParamString" name="combo"
		><ui:result-dbobjects rs="${rs}" label="${label}" link="db/${currentConnection.linkName}/dbobjects.html?catalog=${catalog}&amp;schema=%%" emptyText="defaultSchema" targetElement="${targetElement}"
	/></ui:multilevel></ui:explorer>
<%@
	include file="../include/footer.jspf"
%>
