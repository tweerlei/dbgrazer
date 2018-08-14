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
%><fmt:message key="users" var="pageTitle"/><%@
	include file="include/header.jspf"
%>
	<ui:headline1 label="${pageTitle}">
	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
		<div class="menuitem"><a href="user.html"><fmt:message key="newUser"/></a></div>
	</div></div>
	</ui:headline1>
	
	<ui:tabs items="${users}" var="l" varKey="label" varLink="detailLink" varParams="detailParams" varParamString="detailParamString" name="result">
		<div class="tab-body">
			<ul>
<c:forEach items="${l}" var="c"
>				<li><a href="user.html?q=${c}">${c}</a></li>
</c:forEach
>			</ul><hr/>
		</div>
	</ui:tabs>
<%@
	include file="include/footer.jspf"
%>
