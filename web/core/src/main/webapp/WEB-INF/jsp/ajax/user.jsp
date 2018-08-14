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
%><div class="menucolumn">
<c:choose><c:when test="${currentUser.principal != null}"
><div class="menuitem"><a href="profile.html"><fmt:message key="profile"/></a></div>
<hr class="menuseparator"/>
<div class="menuitem"><a href="logout.html"><fmt:message key="logout"/> (<fmt:message key="logoutShortcut"/>)</a></div>
</c:when><c:otherwise
><div class="menuitem"><span onclick="return showLoginDialog(event);"><fmt:message key="login"/> (<fmt:message key="loginShortcut"/>)</span></div>
</c:otherwise></c:choose
></div>
