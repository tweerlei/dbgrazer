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
%><ui:set text="${model.originalName}" key="newQuery" var="pageTitle"/><%@
	include file="../include/header.jspf"
%><fmt:message key="noQuery" var="noQuery"/>
	<ui:headline1 label="${pageTitle}">
<c:if test="${not empty model.originalName}"
>	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
	<div class="h1-actions">
		<span class="menu" onclick="return showDbMenu(event, 'queryhistory', { q: '${model.originalName}' });"><fmt:message key="changeLog"/></span>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
<c:if test="${currentConnection.submitEnabled && currentConnection.type.customQuerySupported}"
>		<form id="toolsform-1" class="hidden" action="db/${currentConnection.linkName}/submit-${currentConnection.type}.html" method="post" target="_blank">
			<input type="hidden" name="statement" value="${fn:escapeXml(model.statement)}"/>
			<input type="hidden" name="type" value="${model.type}"/>
<c:forEach items="${model.attributes}" var="a"
>			<input type="hidden" name="attributes[${a.key}]" value="${fn:escapeXml(a.value)}"/>
</c:forEach
>		</form>
</c:if
>		<div class="menuitem"><span onclick="return toggleTextField('name');"><fmt:message key="rename"/></span></div>
		<div class="menuitem"><span onclick="return showConfirmDialog('<fmt:message key="delete"/>', '<fmt:message key="deleteQueryText"/>', 'db/${currentConnection.linkName}/remove-query.html', '${model.originalName}');"><fmt:message key="delete"/></span></div>
		<div class="menuitem"><span onclick="return gotoPage('db/${currentConnection.linkName}/edit.html?template=${model.originalName}');"><fmt:message key="copy"/></span></div>
<c:if test="${!model.viewType}"
>		<div class="menuitem"><span onclick="return postForm('toolsform-1', event);"><fmt:message key="sqlQuery"/></span></div>
		<div class="menuitem"><span onclick="return gotoPage('db/${currentConnection.linkName}/edit.html?subquery=${model.originalName}');"><fmt:message key="createView"/></span></div>
</c:if
>		<div class="menuitem"><span onclick="return showDbDialog(event, 'copy-query', { q: '${model.originalName}' }, '<fmt:message key="copyToSchema"/>');"><fmt:message key="copyToSchema"/></span></div>
		<div class="menuitem"><a href="db/${currentConnection.linkName}/querygraph.html?q=${model.originalName}"><fmt:message key="showGraph"/></a></div>
	</div></div>
</c:if
>	</ui:headline1>
	
	<div class="tab-page">
		<form id="zoomform" class="hidden" method="post" action="db/${currentConnection.linkName}/ajax/formatstmt.html">
			<input id="zoomstmt" type="hidden" name="statement" value=""/>
			<input type="hidden" name="format" value=""/>
		</form>
		<div class="tab-body">
<c:forEach items="${resultTypes}" var="j"
>		<div class="hidden" id="tooltip-${j}"><fmt:message key="help_${j}"/></div>
</c:forEach
>		
		<spring:form cssClass="full" method="post" action="db/${currentConnection.linkName}/edit.html" modelAttribute="model" onsubmit="return showWaitDialog();" onreset="return goBack();">
			<input type="hidden" name="q" value="${model.originalName}"/>
			<spring:hidden path="backTo"/>
			<spring:errors id="errors" path="*" element="strong"/>
			<dl>
				<dt><spring:label path="name"><fmt:message key="name"/></spring:label></dt>
				<dd><spring:input path="name" disabled="${not empty model.originalName}"/></dd>
				<dt><fmt:message key="appliesTo"/></dt>
				<dd><c:forEach items="${schemas}" var="s"><c:choose
					><c:when test="${s.mainSchema}"><fmt:message key="applyToSchema" var="connectionSpecific"><fmt:param value="${s.name}"/></fmt:message><spring:radiobutton path="scope" value="${s}" label=" ${connectionSpecific}"/> &nbsp; </c:when
					><c:when test="${s.querySet}"><fmt:message key="applyToDialect" var="connectionSpecific"><fmt:param value="${s.version}"/></fmt:message><spring:radiobutton path="scope" value="${s}" label=" ${connectionSpecific}"/> &nbsp; </c:when
					><c:when test="${s.subschema}"><fmt:message key="applyToVersion" var="connectionSpecific"><fmt:param value="${s.version}"/></fmt:message><spring:radiobutton path="scope" value="${s}" label=" ${connectionSpecific}"/> &nbsp; </c:when
					></c:choose></c:forEach><br/>
					<fmt:message key="welcomeQuery" var="connectionSpecific"/><spring:checkbox path="welcome" label=" ${connectionSpecific}"/></dd>
				<dt><label for="groupName"><fmt:message key="queryGroup"/></label></dt>
				<dd><ui:input name="groupName" id="groupName" value="${model.groupName}" page="db:select-group"/></dd>
<c:if test="${not empty referencing}"
>				<dt><fmt:message key="referencedBy"/></dt>
				<dd><c:forEach items="${referencing}" var="q"><a href="db/${currentConnection.linkName}/edit.html?q=${q.name}&amp;backTo=${model.backTo}">${q.name}</a> </c:forEach></dd>
</c:if
>				<dt><fmt:message key="queryParams"/></dt>
				<dd><table class="props">
					<thead>
						<tr>
							<th><fmt:message key="position"/></th>
							<th><fmt:message key="name"/></th>
							<th><fmt:message key="columnType"/></th>
							<th><fmt:message key="valueQuery"/></th>
							<th><fmt:message key="action"/></th>
						</tr>
					</thead>
					<tfoot>
						<tr>
							<td colspan="5"><span class="action" title="<fmt:message key="add"/>" onclick="addLine(event, 'params');"><fmt:message key="addIcon"/></span></td>
						</tr>
					</tfoot>
					<tbody><c:forEach begin="0" end="${fn:length(model.params) - 1}" var="i" varStatus="st">
						<tr id="params-${i}"<c:if test="${!st.first && empty model.params[i].name}"> style="display: none;"</c:if>>
							<td>${i+1}</td>
							<td><ui:input name="params[${i}].name" id="params${i}.name" value="${model.params[i].name}" page="db:select-param"/></td>
							<td><spring:select path="params[${i}].type"><c:forEach items="${columnTypes}" var="j"
								><spring:option value="${j}"><fmt:message key="${j}"/></spring:option></c:forEach
							></spring:select></td>
							<td><ui:select name="params[${i}].valueQuery" id="params${i}.valueQuery" label="${noQuery}" value="${model.params[i].valueQuery}" page="db:select-valuequery"/></td>
							<td><c:if test="${!st.last}">
								<span class="action" title="<fmt:message key="down"/>" onclick="return moveLineDown(event, 'params', ${i});"><fmt:message key="downIcon"/></span>
</c:if><c:if test="${!st.first}"
>								<span class="action" title="<fmt:message key="up"/>" onclick="return moveLineUp(event, 'params', ${i});"><fmt:message key="upIcon"/></span>
</c:if
>								<span class="action" title="<fmt:message key="editQuery"/>" onclick="return gotoLineTarget(event, 'params', '${i}.valueQuery', '${model.backTo}');"><fmt:message key="editQueryIcon"/></span>
							</td>
						</tr></c:forEach>
					</tbody>
					</table></dd>
				<dt><spring:label path="type"><fmt:message key="queryType"/></spring:label></dt>
				<dd><spring:select path="type" onchange="return switchResultType(this);">
						<optgroup label="<fmt:message key="query"/>"><c:forEach items="${resultTypes}" var="j"><c:if test="${!j.resultType.view}"><spring:option value="${j}" cssClass="query"><fmt:message key="${j}"/></spring:option></c:if></c:forEach></optgroup>
						<optgroup label="<fmt:message key="view"/>"><c:forEach items="${resultTypes}" var="j"><c:if test="${j.resultType.view}"><spring:option value="${j}" cssClass="view"><fmt:message key="${j}"/></spring:option></c:if></c:forEach></optgroup>
					</spring:select> <span class="action" onclick="return showTip(event, \$F('type'));" onmouseover="return showTip(event, \$F('type'));"><fmt:message key="tooltipIcon"/></span></dd>
<c:forEach items="${allAttributes}" var="a"
>				<dt class="query-bytype<c:forEach items="${resultTypes}" var="j"><c:if test="${tools:containsKey(j.supportedAttributes, a.key)}"> query-${j}</c:if></c:forEach>"<c:if test="${!tools:containsKey(model.resultType.supportedAttributes, a.key)}"> style="display: none;"</c:if>><spring:label path="attributes[${a.key}]"><fmt:message key="${a.key}"/></spring:label></dt>
				<dd class="query-bytype<c:forEach items="${resultTypes}" var="j"><c:if test="${tools:containsKey(j.supportedAttributes, a.key)}"> query-${j}</c:if></c:forEach>"<c:if test="${!tools:containsKey(model.resultType.supportedAttributes, a.key)}"> style="display: none;"</c:if>><c:choose
						><c:when test="${a.value.simpleName == 'Boolean'}"><spring:select path="attributes[${a.key}]">
							<spring:option value=""><fmt:message key="default"/></spring:option>
							<spring:option value="TRUE"><fmt:message key="${a.key}Yes"/></spring:option>
							<spring:option value="FALSE"><fmt:message key="${a.key}No"/></spring:option>
						</spring:select></c:when
						><c:when test="${a.value.simpleName == 'GraphType'}"><spring:select path="attributes[${a.key}]">
							<spring:option value=""><fmt:message key="default"/></spring:option>
<c:forEach items="${graphTypes}" var="v"
>							<spring:option value="${v}"><fmt:message key="${v}"/></spring:option>
</c:forEach
>						</spring:select></c:when
						><c:when test="${a.value.simpleName == 'ChartType'}"><spring:select path="attributes[${a.key}]">
							<spring:option value=""><fmt:message key="default"/></spring:option>
<c:forEach items="${chartTypes}" var="v"
>							<spring:option value="${v}"><fmt:message key="${v}"/></spring:option>
</c:forEach
>						</spring:select></c:when
						><c:when test="${a.value.simpleName == 'ChartScaling'}"><spring:select path="attributes[${a.key}]">
							<spring:option value=""><fmt:message key="default"/></spring:option>
<c:forEach items="${chartScalings}" var="v"
>							<spring:option value="${v}"><fmt:message key="${v}"/></spring:option>
</c:forEach
>						</spring:select></c:when
						><c:when test="${a.value.simpleName == 'TextFormatter'}"><spring:select path="attributes[${a.key}]">
							<spring:option value=""><fmt:message key="default"/></spring:option>
<c:forEach items="${formatters}" var="v"
>							<spring:option value="${v}"><fmt:message key="${v}"/></spring:option>
</c:forEach
>						</spring:select></c:when
						><c:when test="${a.value.simpleName == 'DataExtractor'}"><spring:select path="attributes[${a.key}]">
							<spring:option value=""><fmt:message key="default"/></spring:option>
<c:forEach items="${extractors}" var="v"
>							<spring:option value="${v}"><fmt:message key="${v}"/></spring:option>
</c:forEach
>						</spring:select></c:when
						><c:when test="${a.value.simpleName == 'RowTransformer'}"><spring:select path="attributes[${a.key}]">
							<spring:option value=""><fmt:message key="default"/></spring:option>
<c:forEach items="${transformers}" var="v"
>							<spring:option value="${v}"><fmt:message key="${v}"/></spring:option>
</c:forEach
>						</spring:select></c:when
						><c:otherwise><spring:input path="attributes[${a.key}]"/></c:otherwise
				></c:choose> <ui:info name="attributes-${a.key}"><fmt:message key="help_${a.key}"/></ui:info></dd>
</c:forEach
>				<dt class="query-only"<c:if test="${model.viewType}"> style="display: none;"</c:if>><fmt:message key="links"/></dt>
				<dd class="query-only"<c:if test="${model.viewType}"> style="display: none;"</c:if>><table class="props">
					<thead>
						<tr>
							<th><fmt:message key="column"/></th>
							<th><fmt:message key="query"/></th>
							<th><fmt:message key="action"/></th>
						</tr>
					</thead>
					<tfoot>
						<tr>
							<td colspan="3"><span class="action" title="<fmt:message key="add"/>" onclick="addLine(event, 'links');"><fmt:message key="addIcon"/></span></td>
						</tr>
					</tfoot>
					<tbody><c:forEach begin="0" end="${fn:length(model.links) - 1}" var="i" varStatus="st">
						<tr id="links-${i}"<c:if test="${!st.first && empty model.links[i]}"> style="display: none;"</c:if>>
							<td>${i+1}</td>
							<td><ui:select name="links[${i}]" id="links${i}" label="${noQuery}" text="${fn:startsWith(model.links[i], '*') ? fn:substring(model.links[i], 1, -1) : model.links[i]}" value="${model.links[i]}" page="db:select-linkquery"/></td>
							<td><c:if test="${!st.last}">
								<span class="action" title="<fmt:message key="down"/>" onclick="return moveLineDown(event, 'links', ${i});"><fmt:message key="downIcon"/></span>
</c:if><c:if test="${!st.first}"
>								<span class="action" title="<fmt:message key="up"/>" onclick="return moveLineUp(event, 'links', ${i});"><fmt:message key="upIcon"/></span>
</c:if
>								<span class="action" title="<fmt:message key="editQuery"/>" onclick="return gotoLineTarget(event, 'links', ${i}, '${model.backTo}');"><fmt:message key="editQueryIcon"/></span>
							</td>
						</tr></c:forEach>
					</tbody>
					</table></dd>
				<dt class="view-only"<c:if test="${!model.viewType}"> style="display: none;"</c:if>><fmt:message key="subqueries"/></dt>
				<dd class="view-only"<c:if test="${!model.viewType}"> style="display: none;"</c:if>><table class="props">
					<thead>
						<tr>
							<th><fmt:message key="position"/></th>
							<th><fmt:message key="query"/></th>
							<th><fmt:message key="queryParams"/></th>
							<th><fmt:message key="action"/></th>
						</tr>
					</thead>
					<tfoot>
						<tr>
							<td colspan="4"><span class="action" title="<fmt:message key="add"/>" onclick="addLine(event, 'views');"><fmt:message key="addIcon"/></span>
								<span class="button" onclick="addMatchingViews();"><fmt:message key="addMatchingQueries"/></span></td>
						</tr>
					</tfoot>
					<tbody><c:forEach begin="0" end="${fn:length(model.views) - 1}" var="i" varStatus="st">
						<tr id="views-${i}"<c:if test="${!st.first && empty model.views[i].name}"> style="display: none;"</c:if>>
							<td>${i+1}</td>
							<td><ui:select name="views[${i}].name" id="views${i}" label="${noQuery}" value="${model.views[i].name}" page="db:select-viewquery"/></td>
							<td><spring:input path="views[${i}].parameter"/></td>
							<td><c:if test="${!st.last}">
								<span class="action" title="<fmt:message key="down"/>" onclick="return moveLineDown(event, 'views', ${i});"><fmt:message key="downIcon"/></span>
</c:if><c:if test="${!st.first}"
>								<span class="action" title="<fmt:message key="up"/>" onclick="return moveLineUp(event, 'views', ${i});"><fmt:message key="upIcon"/></span>
</c:if
>								<span class="action" title="<fmt:message key="editQuery"/>" onclick="return gotoLineTarget(event, 'views', ${i}, '${model.backTo}');"><fmt:message key="editQueryIcon"/></span>
							</td>
						</tr></c:forEach>
					</tbody>
					</table></dd>
				<dt class="query-only"<c:if test="${model.viewType}"> style="display: none;"</c:if>><spring:label path="statement"><fmt:message key="sqlStatement"/></spring:label></dt>
				<dd class="query-only"<c:if test="${model.viewType}"> style="display: none;"</c:if>><div>
					<span class="action" title="<fmt:message key="clear"/>" onclick="return clearElement('statement');"><fmt:message key="clearIcon"/></span>
					<span class="action" title="<fmt:message key="maximize"/>" onclick="return zoomForm('statement');"><fmt:message key="maximizeIcon"/></span>
					<ui:info name="sqlStatement"><fmt:message key="help_sqlStatement"/></ui:info></div>
					<spring:textarea path="statement" cssClass="large" cols="80" rows="25"/></dd>
				<dt>&nbsp;</dt>
				<dd><input type="submit" value="<fmt:message key="apply"/>"/>
					<input type="reset" value="<fmt:message key="cancel"/>"/></dd>
			</dl><hr/>
		</spring:form>
	</div></div>
<%@
	include file="../include/footer.jspf"
%>
