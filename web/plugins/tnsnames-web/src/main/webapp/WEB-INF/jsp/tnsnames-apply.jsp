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
%><fmt:message key="connections" var="pageTitle"/><%@
	include file="include/header.jspf"
%>
	<script type="text/javascript">/*<![CDATA[*/
	
	function toggleNewConnection(el) {
		Forms.toggleEnabled('name');
		Forms.toggleEnabled('description');
		Forms.toggleEnabled('username');
		Forms.toggleEnabled('password');
		Forms.toggleEnabled('schema');
		Forms.toggleEnabled('subSchema');
		return false;
	}
	
	/*]]>*/</script>
	
	<ui:headline1 label="${pageTitle}"/>
	
	<div class="tab-page"><div class="tab-body">
		<spring:form action="tnsnames-apply.html" method="post" commandName="model" onreset="return gotoPage('links.html');">
			<spring:errors id="errors" path="*" element="strong"/>
			<dl>
				<dt><spring:label path="entry">TNSName</spring:label></dt>
				<dd><spring:select path="entry">
<c:forEach items="${entries}" var="i"
>					<option value="${i.key}" title="${fn:escapeXml(i.value)}">${i.key}</option>
</c:forEach
>				</spring:select></dd>
				<dt><spring:label path="link"><fmt:message key="connection"/></spring:label></dt>
				<dd><spring:select path="link" onchange="return toggleNewConnection(this);">
					<option value=""><fmt:message key="newConnection"/></option>
<c:forEach items="${links}" var="l"
>					<option value="${l.name}" title="${fn:escapeXml(l.url)}">${l.description}</option>
</c:forEach
>				</spring:select>
				<fmt:message key="applyToAll" var="lblApply"/><spring:checkbox path="applyToAll" value="true" label=" ${lblApply}"/></dd>
				<dt><spring:label path="name"><fmt:message key="name"/></spring:label></dt>
				<dd><spring:input path="name"/></dd>
				<dt><spring:label path="description"><fmt:message key="description"/></spring:label></dt>
				<dd><spring:input path="description"/></dd>
				<dt><spring:label path="username"><fmt:message key="username"/></spring:label></dt>
				<dd><spring:input path="username"/></dd>
				<dt><spring:label path="password"><fmt:message key="newPassword"/></spring:label></dt>
				<dd><spring:password path="password"/></dd>
				<dt><spring:label path="schema"><fmt:message key="schemaName"/></spring:label></dt>
				<dd><ui:input name="schema" id="f1-schema" value="" page="select-schema"/></dd>
				<dt><spring:label path="subSchema"><fmt:message key="schemaVersion"/></spring:label></dt>
				<dd><ui:input name="subSchema" id="f1-subSchema" value="" page="select-subschema"/></dd>
				<dt>&nbsp;</dt>
				<dd><input id="f1-submit" type="submit" value="<fmt:message key="apply"/>"/> <input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
			</dl><hr/>
		</spring:form>
<c:choose><c:when test="${updated == null}"
></c:when><c:when test="${updated == 0}"
>		<div class="notice"><fmt:message key="created"><fmt:param value="1"/></fmt:message></div>
</c:when><c:when test="${updated > 0}"
>		<div class="notice"><fmt:message key="updated"><fmt:param value="${updated}"/></fmt:message></div>
</c:when></c:choose>
	</div></div>
<%@
	include file="include/footer.jspf"
%>
