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
%><form id="f1" action="db/${currentConnection.linkName}/copy-query.html" method="post" onsubmit="return submitDialog(this);" onreset="return closeDialog();">
	<input id="f1-query" type="hidden" name="q" value="${fn:escapeXml(query)}"/>
	<dl>
		<dt><label for="f1-connection"><fmt:message key="connection"/></label></dt>
		<dd><select id="f1-connection" name="connection">
<c:forEach items="${connections}" var="c"><option value="${c.value}">${c.key}</option>
</c:forEach></select></dd>
		<dt>&nbsp;</dt>
		<dd><input id="f1-overwrite" type="checkbox" name="overwrite"/> <label for="f1-overwrite"><fmt:message key="overwrite"/></label></dd>
		<dt>&nbsp;</dt>
		<dd><input id="f1-submit" type="submit" value="<fmt:message key="copy"/>"/> <input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</form>
