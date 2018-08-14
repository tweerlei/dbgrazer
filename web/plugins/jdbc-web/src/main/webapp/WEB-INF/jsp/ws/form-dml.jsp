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
		<form action="ws/${currentConnection.linkName}/dml.html" method="get" target="_blank" onreset="return goBack();">
			<dl>
				<dt><fmt:message key="catalog"/></dt>
				<dd><input type="text" name="catalog"/></dd>
				<dt><fmt:message key="schema"/></dt>
				<dd><input type="text" name="schema"/></dd>
				<dt><fmt:message key="object"/></dt>
				<dd><input type="text" name="object"/></dd>
				<dt><fmt:message key="connection"/></dt>
				<dd><select id="f1-connection" name="connection2">
					<option value="."><fmt:message key="noSelection"/></option>
<c:forEach items="${allConnections}" var="c"
>					<option value="${c.value}">${c.key}</option>
</c:forEach
>					</select></dd>
				<dt><fmt:message key="catalog"/></dt>
				<dd><input type="text" name="catalog2"/></dd>
				<dt><fmt:message key="schema"/></dt>
				<dd><input type="text" name="schema2"/></dd>
				<dt><fmt:message key="WHERE"/></dt>
				<dd><input type="text" name="filter"/></dd>
				<dt><fmt:message key="compareMode"/></dt>
				<dd><select id="f1-order" name="order">
<c:forEach items="${orders}" var="o"
>					<option value="${o}"><fmt:message key="order_${o}"/></option>
</c:forEach
>				</select></dd>
				<dt><fmt:message key="executeAs"/></dt>
				<dd><select id="f1-mode" name="mode">
					<option value=""><fmt:message key="preview"/></option>
<c:forEach items="${resultTypes}" var="t"
>					<option value="${t}"><fmt:message key="execute_${t}"/></option>
</c:forEach
>				</select></dd>
				<dt>&nbsp;</dt>
				<dd><input type="submit" value="<fmt:message key="apply"/>"/>
					<input type="reset" value="<fmt:message key="cancel"/>"/></dd>
			</dl>
		</form>
	</div></div>
<%@
	include file="../include/footer.jspf"
%>
