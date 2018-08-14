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
%><form id="f1" action="db/${currentConnection.linkName}/rename-param.html" method="post" onsubmit="return submitDialog(this);" onreset="return closeDialog();">
	<input type="hidden" name="from" value="${fn:escapeXml(paramName)}"/>
	<dl>
		<dt><label for="f1-to"><fmt:message key="newName"/></label></dt>
		<dd><input id="f1-to" type="text" name="to" value="${fn:escapeXml(paramName)}"/></dd>
		<dt>&nbsp;</dt>
		<dd><input id="f1-submit" type="submit" value="<fmt:message key="rename"/>"/> <input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</form>
