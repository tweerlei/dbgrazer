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
%><form id="f1" action="db/${currentConnection.linkName}/dbdesigner-addall.html" method="get" onsubmit="return submitDialog(this);" onreset="return closeDialog();">
	<input id="f1-connection" type="hidden" name="c" value="${currentConnection.linkName}"/>
	<dl>
		<dt><label for="f1-catalog"><fmt:message key="catalog"/></label></dt>
		<dd><select id="f1-catalog" name="catalog" onchange="return selectCatalog(this);"<c:if test="${empty catalogs}"> disabled="disabled"</c:if>>
			<option value="."><fmt:message key="noSelection"/></option>
<c:forEach items="${catalogs}" var="c"
>			<option value="${c}"<c:if test="${c == catalog}"> selected="selected"</c:if>><ui:message text="${c}" key="defaultCatalog"/></option>
</c:forEach
>			</select></dd>
		<dt><label for="f1-schema"><fmt:message key="schema"/></label></dt>
		<dd><select id="f1-schema" name="schema" onchange="return selectObjComplete(this);"<c:if test="${empty schemas}"> disabled="disabled"</c:if>>
			<option value="."><fmt:message key="noSelection"/></option>
<c:forEach items="${schemas}" var="c"
>			<option value="${c}"<c:if test="${c == schema}"> selected="selected"</c:if>><ui:message text="${c}" key="defaultSchema"/></option>
</c:forEach
>			</select></dd>
		<dt><label for="f1-filter"><fmt:message key="filter"/></label></dt>
		<dd><input id="f1-filter" type="text" name="object"/> <ui:info name="f1-filter"><fmt:message key="help_REGEXP"/></ui:info></dd>
		<dt>&nbsp;</dt>
		<dd><input id="f1-submit" type="submit" value="<fmt:message key="apply"/>"<c:if test="${schema == null}"> disabled="disabled"</c:if>/> <input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</form>
