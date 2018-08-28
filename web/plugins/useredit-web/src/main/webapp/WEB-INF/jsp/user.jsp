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
%><ui:set text="${model.originalLogin}" key="newUser" var="pageTitle"/><%@
	include file="include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}">
<c:if test="${not empty model.originalLogin}"
>	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
	<div class="h1-actions">
		<span class="menu" onclick="return showTopMenu(event, 'userhistory', { q: '${model.originalLogin}' });"><fmt:message key="changeLog"/></span>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
		<div class="menuitem"><span onclick="return toggleTextField('login');"><fmt:message key="rename"/></span></div>
		<div class="menuitem"><span onclick="return showConfirmDialog('<fmt:message key="delete"/>', '<fmt:message key="deleteUserText"/>', 'remove-user.html', '${model.originalLogin}');"><fmt:message key="delete"/></span></div>
		<div class="menuitem"><span onclick="return gotoPage('user.html?template=${model.originalLogin}');"><fmt:message key="copy"/></span></div>
	</div></div>
</c:if
>	</ui:headline1>
	
	<div class="tab-page"><div class="tab-body">
		<spring:form cssClass="full" method="post" action="user.html" modelAttribute="model" onsubmit="return showWaitDialog();" onreset="return goBack();">
			<input type="hidden" name="q" value="${model.originalLogin}"/>
			<fmt:message key="default" var="defLabel"
			/><c:if test="${not empty model.attributes['Theme']}"><fmt:message key="theme_${model.attributes['Theme']}" var="currentTheme"/></c:if
			><spring:errors id="errors" path="*" element="strong"/>
			<dl>
				<dt><spring:label path="login"><fmt:message key="username"/></spring:label></dt>
				<dd><spring:input path="login" disabled="${not empty model.originalLogin}"/></dd>
				<dt><spring:label path="name"><fmt:message key="name"/></spring:label></dt>
				<dd><spring:input path="name"/></dd>
				<dt><spring:label path="password"><fmt:message key="newPassword"/></spring:label></dt>
				<dd><spring:password path="password" disabled="${model.noPassword}"/></dd>
				<dt>&nbsp;</dt>
				<dd><fmt:message key="customAuth" var="customAuth"/><spring:checkbox path="noPassword" value="true" label=" ${customAuth}" onclick="return !toggleTextField('password');"/></dd>
				<dt><fmt:message key="userRoles"/></dt>
				<dd><c:forEach items="${authorities}" var="a"><fmt:message key="${a}" var="l"/><div><spring:checkbox id="authorities_${a}" path="authorities[${a}]" value="true" label=" ${l}"/></div>
					</c:forEach></dd>
<c:if test="${not empty groups}"
>				<dt><fmt:message key="connectionGroups"/></dt>
				<dd><table class="props">
					<thead>
						<tr>
							<th><fmt:message key="connectionGroup"/></th>
<c:forEach items="${authorities}" var="a"><c:if test="${!a.global}"
>							<th><fmt:message key="${a}"/></th>
</c:if></c:forEach
>						</tr>
					</thead>
					<tbody>
<c:forEach items="${groups}" var="g"
>						<tr>
							<td>${g}</td>
<c:forEach items="${authorities}" var="a"><c:if test="${!a.global}"
>							<td class="center"><spring:checkbox id="groups_${g}_${a}" path="groups['${g}'][${a}]" value="true"/></td>
</c:if></c:forEach
>						</tr>
</c:forEach
>					</tbody>
					</table></dd>
</c:if
>				<dt><spring:label path="attributes[Theme]"><fmt:message key="theme"/></spring:label></dt>
				<dd><ui:select name="attributes[Theme]" id="attributesTheme" label="${defLabel}" value="${model.attributes['Theme']}" text="${currentTheme}" page="select-theme"/></dd>
				<dt><spring:label path="attributes[Locale]"><fmt:message key="language"/></spring:label></dt>
				<dd><ui:select name="attributes[Locale]" id="attributesLocale" label="${defLabel}" value="${model.attributes['Locale']}" text="${model.localeName}" page="select-locale"/></dd>
				<dt><spring:label path="attributes[TimeZone]"><fmt:message key="timezone"/></spring:label></dt>
				<dd><ui:select name="attributes[TimeZone]" id="attributesTimeZone" label="${defLabel}" value="${model.attributes['TimeZone']}" text="${model.timeZoneName}" page="select-timezone"/></dd>
				<dt>&nbsp;</dt>
				<dd><input type="submit" value="<fmt:message key="apply"/>"/>
					<input type="reset" value="<fmt:message key="cancel"/>"/></dd>
			</dl><hr/>
		</spring:form>
	</div></div>
<%@
	include file="include/footer.jspf"
%>
