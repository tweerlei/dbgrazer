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
%><form action="#" method="get" onsubmit="return false;">
	<div><ui:filter id="filter-select-1" target="select-1"/><hr/></div>
	<ui:headline3 key="theme"/>
	<select name="value1" id="select-1" size="20" ondblclick="return applySelection(this, '${fn:escapeXml(target)}');">
		<option value=""<c:if test="${value == ''}"> selected="selected"</c:if>><fmt:message key="default"/></option>
<c:forEach items="${themes}" var="v"
>		<option value="${v}"<c:if test="${v == value}"> selected="selected"</c:if>><fmt:message key="theme_${v}"/></option>
</c:forEach
>	</select>
</form>
