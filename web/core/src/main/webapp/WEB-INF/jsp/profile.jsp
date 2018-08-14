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
%><fmt:message key="profile" var="pageTitle"/><%@
	include file="include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}: ${currentUser.principal.login}"/>
	
	<div class="tab-page"><div class="tab-body">
<c:choose><c:when test="${model.virtual}"
>		<fmt:message key="readOnlyUser"/>
</c:when><c:otherwise
>		<spring:form cssClass="full" method="post" action="profile.html" modelAttribute="model" onsubmit="return showWaitDialog();" onreset="return goBack();">
			<fmt:message key="default" var="defLabel"
			/><c:if test="${not empty model.attributes['Theme']}"><fmt:message key="theme_${model.attributes['Theme']}" var="currentTheme"/></c:if
			><spring:errors id="errors" path="*" element="strong"/>
			<dl>
				<dt><spring:label path="name"><fmt:message key="name"/></spring:label></dt>
				<dd><spring:input path="name"/></dd>
<c:if test="${not empty currentUser.principal.password}"
>				<dt><spring:label path="password"><fmt:message key="newPassword"/></spring:label></dt>
				<dd><spring:password path="password"/></dd>
				<dt><spring:label path="password2"><fmt:message key="confirmation"/></spring:label></dt>
				<dd><spring:password path="password2"/></dd>
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
</c:otherwise></c:choose
>	</div></div>
<%@
	include file="include/footer.jspf"
%>
