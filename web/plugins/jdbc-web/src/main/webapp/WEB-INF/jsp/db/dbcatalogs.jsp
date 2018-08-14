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
%><fmt:message key="schemaBrowser" var="pageTitle"/><%@
	include file="../include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}"/>
	
	<c:set var="links" value="db/${currentConnection.linkName}/dbcatalogs.html"
	/><c:set var="links" value="${fn:split(links, ',')}"
	/><div id="explorer-left"><ui:multilevel query="${query.name}" levels="${query.subQueries}" params="${params}" links="${links}" items="${tabs}" var="rs" varKey="label" varParams="detailParams" varParamString="detailParamString" name="combo"
		><ui:result-dbobjects rs="${rs}" label="${label}" link="db/${currentConnection.linkName}/dbschemas.html?catalog=%%" emptyText="defaultCatalog" targetElement="${targetElement}"
	/></ui:multilevel></div>
	
	<div id="explorer-right"><div class="tab-page"><div class="tab-body">
		<dl>
<c:forEach items="${dbinfo}" var="i"
>			<dt>${fn:escapeXml(i.key)}</dt>
			<dd>${fn:escapeXml(i.value)}</dd>
</c:forEach
>		</dl><hr/>
	</div></div></div>
	<hr/>
<%@
	include file="../include/footer.jspf"
%>
