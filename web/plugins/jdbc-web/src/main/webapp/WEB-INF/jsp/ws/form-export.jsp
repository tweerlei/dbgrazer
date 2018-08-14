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
%><fmt:message key="restAPI" var="pageTitle"/><%@
	include file="../include/ws-header.jspf"
%>
	<ui:headline1 label="${currentConnection.description}"/>
	
	<div class="tab-page"><div class="tab-body">
		<form action="ws/${currentConnection.linkName}/export.html" method="get" target="_blank" onreset="return goBack();">
			<dl>
				<dt><fmt:message key="catalog"/></dt>
				<dd><input type="text" name="catalog"/></dd>
				<dt><fmt:message key="schema"/></dt>
				<dd><input type="text" name="schema"/></dd>
				<dt><fmt:message key="object"/></dt>
				<dd><input type="text" name="object"/></dd>
				<dt><fmt:message key="WHERE"/></dt>
				<dd><input type="text" name="where"/></dd>
				<dt><fmt:message key="ORDER_BY"/></dt>
				<dd><input type="text" name="order"/></dd>
				<dt><fmt:message key="download"/></dt>
				<dd><input type="text" name="format"/> <a href="ws/${currentConnection.linkName}/tbllinks.html" target="_blank">Supported formats</a></dd>
 				<dt>&nbsp;</dt>
				<dd><input type="submit" value="<fmt:message key="apply"/>"/>
					<input type="reset" value="<fmt:message key="cancel"/>"/></dd>
			</dl>
		</form>
	</div></div>
<%@
	include file="../include/footer.jspf"
%>
