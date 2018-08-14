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
	include file="../../include/include.jspf"
%><form action="#" method="get" onsubmit="return false;">
	<div><ui:filter id="filter-select-1" target="select-1 select-2 select-3"/></div>
	<div class="float-left"><ui:headline3 key="unusedQueries"/>
	<select name="value1" id="select-1" size="20" ondblclick="return applySelection(this, '${fn:escapeXml(target)}');">
		<option value=""><fmt:message key="noQuery"/></option>
<c:forEach items="${allQueries.queries}" var="v"
>		<option value="${v}"<c:if test="${v == value}"> selected="selected"</c:if>>${v}</option>
</c:forEach
>	</select></div>
	<div class="float-left"><ui:headline3 key="usedQueries"/>
	<select name="value2" id="select-2" size="20" ondblclick="return applySelection(this, '${fn:escapeXml(target)}');">
		<option value=""><fmt:message key="noQuery"/></option>
<c:forEach items="${allQueries.subqueries}" var="v"
>		<option value="${v}"<c:if test="${v == value}"> selected="selected"</c:if>>${v}</option>
</c:forEach
>	</select></div>
	<div class="float-left"><ui:headline3 key="lists"/>
	<select name="value3" id="select-3" size="20" ondblclick="return applySelection(this, '${fn:escapeXml(target)}');">
		<option value=""><fmt:message key="noQuery"/></option>
<c:forEach items="${allQueries.lists}" var="v"
>		<option value="${v}"<c:if test="${v == value}"> selected="selected"</c:if>>${v}</option>
</c:forEach
>	</select></div><hr/>
</form>
