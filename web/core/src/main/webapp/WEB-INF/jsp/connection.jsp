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
%><fmt:message key="chooseConnection" var="pageTitle"/><%@
	include file="include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}"/>
	
	<div class="tab-page"><div class="tab-body"><c:choose
><c:when test="${loginRequired && currentUser.principal == null}"
		><div class="login"><fmt:message key="loginRequired"/>
		<form id="f1" action="login.html" method="post">
			<dl>
				<dt><label for="f1-username"><fmt:message key="username"/></label></dt>
				<dd><input id="f1-username" type="text" name="username"/></dd>
				<dt><label for="f1-password"><fmt:message key="password"/></label></dt>
				<dd><input id="f1-password" type="password" name="password"/></dd>
				<dt>&nbsp;</dt>
				<dd><input id="f1-submit" type="submit" value="<fmt:message key="login"/>"/></dd>
			</dl><hr/>
		</form></div>
</c:when><c:otherwise
		><div class="login"><fmt:message key="loginRequired"/></div></c:otherwise
></c:choose></div></div>
<%@
	include file="include/footer.jspf"
%>
