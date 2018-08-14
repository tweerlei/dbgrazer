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
%><c:if test="${loginEnabled}"><c:choose><c:when test="${currentUser.principal == null}"
><c:if test="${!loginRequired}"><form id="f1" action="login.html" method="post" onsubmit="return submitDialog(this);" onreset="return closeDialog();">
	<dl>
		<dt><label for="f1-username"><fmt:message key="username"/></label></dt>
		<dd><input id="f1-username" type="text" name="username"/></dd>
		<dt><label for="f1-password"><fmt:message key="password"/></label></dt>
		<dd><input id="f1-password" type="password" name="password"/></dd>
		<dt>&nbsp;</dt>
		<dd><input id="f1-submit" type="submit" value="<fmt:message key="login"/>"/> <input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</form></c:if></c:when><c:otherwise
><form id="f1" action="logout.html" method="get" onsubmit="return submitDialog(this);" onreset="return closeDialog();">
	<p><fmt:message key="loggedInAs"><fmt:param value="${currentUser.principal.login}"/></fmt:message>.</p>
	<p><input id="f1-submit" type="submit" value="<fmt:message key="logout"/>"/> <input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></p>
</form></c:otherwise></c:choose
></c:if><%-- No line break: response must be empty if login is not allowed --%>