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
%><ui:set text="${model.originalName}" key="newConnection" var="pageTitle"/><%@
	include file="include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}">
<c:if test="${not empty model.originalName}"
>	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
	<div class="h1-actions">
		<span class="menu" onclick="return showTopMenu(event, 'linkhistory', { q: '${model.originalName}'});"><fmt:message key="changeLog"/></span>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
		<div class="menuitem"><span onclick="return toggleTextField('name');"><fmt:message key="rename"/></span></div>
		<div class="menuitem"><span onclick="return showConfirmDialog('<fmt:message key="delete"/>', '<fmt:message key="deleteLinkText"/>', 'remove-link.html', '${model.originalName}');"><fmt:message key="delete"/></span></div>
		<div class="menuitem"><span onclick="return gotoPage('link.html?template=${model.originalName}');"><fmt:message key="copy"/></span></div>
		<ui:extensions items="${extensions}" separatorBefore="true"/>
	</div></div>
</c:if
>	</ui:headline1>
	
	<div class="tab-page"><div class="tab-body">
		<spring:form cssClass="full" method="post" action="link.html" modelAttribute="model" onsubmit="return showWaitDialog();" onreset="return goBack();">
			<input id="originalName" type="hidden" name="q" value="${model.originalName}"/>
			<spring:errors id="errors" path="*" element="strong"/>
			<dl>
				<dt><spring:label path="name"><fmt:message key="name"/></spring:label></dt>
				<dd><spring:input path="name" disabled="${not empty model.originalName}"/></dd>
				<dt><spring:label path="setName"><fmt:message key="connectionSet"/></spring:label></dt>
				<dd><ui:input name="setName" id="setName" value="${model.setName}" page="select-set"/> <fmt:message key="applyToAll" var="lblApply"/><spring:checkbox path="applySetToAll" value="true" label=" ${lblApply}"/></dd>
				<dt><spring:label path="description"><fmt:message key="description"/></spring:label></dt>
				<dd><spring:input path="description"/></dd>
				<dt><spring:label path="group"><fmt:message key="connectionGroup"/></spring:label></dt>
				<dd><ui:input name="group" id="group" value="${model.group}" page="select-group" disabled="${model.showOnLogin}"/> <fmt:message key="applyToAll" var="lblApply"/><spring:checkbox path="applyGroupToAll" value="true" label=" ${lblApply}"/></dd>
				<dt>&nbsp;</dt>
				<dd><fmt:message key="loginGroup" var="loginGroup"/><spring:checkbox path="showOnLogin" value="true" label=" ${loginGroup}" onclick="return !toggleTextField('group');"/></dd>
				<dt><spring:label path="type"><fmt:message key="connectionType"/></spring:label></dt>
				<dd><spring:select path="type">
						<c:forEach items="${connectionTypes}" var="j"><spring:option value="${j}"><fmt:message key="${j}"/></spring:option></c:forEach>
					</spring:select></dd>
				<dt><spring:label path="driver"><fmt:message key="driverClassName"/></spring:label></dt>
				<dd><spring:input path="driver"/></dd>
				<dt><spring:label path="url"><fmt:message key="jdbcUrl"/></spring:label></dt>
				<dd><spring:input path="url"/> <fmt:message key="applyToAll" var="lblApply"/><spring:checkbox path="applyUrlToAll" value="true" label=" ${lblApply}"/></dd>
				<dt><spring:label path="username"><fmt:message key="username"/></spring:label></dt>
				<dd><spring:input path="username"/></dd>
				<dt><spring:label path="password"><fmt:message key="newPassword"/></spring:label></dt>
				<dd><spring:password path="password"/></dd>
				<dt>&nbsp;</dt>
				<dd><fmt:message key="writable" var="writableLabel"/><spring:checkbox path="writable" label=" ${writableLabel}"/></dd>
				<dt><spring:label path="preDMLStatement"><fmt:message key="preDMLStatement"/></spring:label></dt>
				<dd><spring:input path="preDMLStatement"/></dd>
				<dt><spring:label path="postDMLStatement"><fmt:message key="postDMLStatement"/></spring:label></dt>
				<dd><spring:input path="postDMLStatement"/></dd>
				<dt><spring:label path="schema"><fmt:message key="schemaName"/></spring:label></dt>
				<dd><ui:input name="schema" id="schema" value="${model.schema}" page="select-schema"/></dd>
				<dt><spring:label path="subSchema"><fmt:message key="schemaVersion"/></spring:label></dt>
				<dd><ui:input name="subSchema" id="subSchema" value="${model.subSchema}" page="select-subschema"/></dd>
				<dt><spring:label path="dialect"><fmt:message key="sqlDialect"/></spring:label></dt>
				<dd><spring:select path="dialect" items="${dialects}"/></dd>
				<dt><spring:label path="querySets"><fmt:message key="querySets"/></spring:label></dt>
				<dd><c:forEach items="${querySets}" var="s" varStatus="st"><spring:checkbox id="querySets_${st.index}" path="querySets['${s}']" value="true" label=" ${s}"/><br/></c:forEach></dd>
				<dt><spring:label path="newQuerySet"><fmt:message key="newQuerySet"/></spring:label></dt>
				<dd><spring:input path="newQuerySet"/></dd>
				<dt><fmt:message key="driverProperties"/></dt>
				<dd><table class="props">
					<thead>
						<tr>
							<th><fmt:message key="name"/></th>
							<th><fmt:message key="value"/></th>
						</tr>
					</thead>
					<tfoot>
						<tr>
							<td colspan="2"><span class="action" title="<fmt:message key="add"/>" onclick="return addLine(event, 'props');"><fmt:message key="addIcon"/></span></td>
						</tr>
					</tfoot>
					<tbody><c:forEach begin="0" end="${fn:length(model.propNames) - 1}" var="i" varStatus="st">
						<tr id="props-${i}"<c:if test="${!st.first && empty model.propNames[i]}"> style="display: none;"</c:if>>
							<td><spring:input path="propNames[${i}]"/></td>
							<td><spring:input path="propValues[${i}]"/></td>
						</tr></c:forEach>
					</tbody>
					</table></dd>
				<dt>&nbsp;</dt>
				<dd><input type="submit" value="<fmt:message key="apply"/>"/>
					<input type="reset" value="<fmt:message key="cancel"/>"/></dd>
			</dl><hr/>
		</spring:form>
	</div></div>
<%@
	include file="include/footer.jspf"
%>
