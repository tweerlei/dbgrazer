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
%><form id="f1" action="rename-schema.html" method="post" onsubmit="return submitDialog(this);" onreset="return closeDialog();">
	<input type="hidden" name="fromName" value="${fn:escapeXml(schemaName)}"/>
	<input type="hidden" name="fromVersion" value="${fn:escapeXml(schemaVersion)}"/>
	<dl>
		<dt><label for="f1-toName"><fmt:message key="schemaName"/></label></dt>
		<dd><input id="f1-toName" type="text" name="toName" value="${fn:escapeXml(schemaName)}"/></dd>
		<dt><label for="f1-toVersion"><fmt:message key="schemaVersion"/></label></dt>
		<dd><input id="f1-toVersion" type="text" name="toVersion" value="${fn:escapeXml(schemaVersion)}"/></dd>
		<dt>&nbsp;</dt>
		<dd><input id="f1-submit" type="submit" value="<fmt:message key="rename"/>"/> <input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</form>
