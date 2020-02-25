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
%><form action="db/${currentConnection.linkName}/query-load.html" method="post" onsubmit="return loadCustomQuery(\$F('select-1'), \$F('f1-delete') == 'true');" onreset="return closeDialog();">
	<input id="f1-delete" type="hidden" name="delete" value="false"/>
	<div><ui:filter id="filter-select-1" target="select-1"/><hr/></div>
	<ui:headline3 key="customQueries"/>
	<select name="q" id="select-1" size="20" ondblclick="Forms.submit(form, event);">
<c:forEach items="${designs}" var="v"
>		<option value="${fn:escapeXml(v)}"<c:if test="${v == design}"> selected="selected"</c:if>>${fn:escapeXml(v)}</option>
</c:forEach
>	</select>
	<dl>
		<dt>&nbsp;</dt>
		<dd><input id="f1-submit" type="submit" value="<fmt:message key="load"/>"/> <input type="button" value="<fmt:message key="delete"/>" onclick="\$('f1-delete').value = 'true'; Forms.submit(form, event);"/> <input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</form>
