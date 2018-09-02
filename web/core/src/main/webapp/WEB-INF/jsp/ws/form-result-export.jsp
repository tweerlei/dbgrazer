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
		<form action="ws/${currentConnection.linkName}/result-export.html" method="get" target="_blank" onreset="return goBack();">
			<dl>
				<dt><fmt:message key="query"/></dt>
				<dd><input type="text" name="q"/></dd>
				<dt><fmt:message key="download"/></dt>
				<dd><input type="text" name="format"/> <a href="ws/${currentConnection.linkName}/explinks.html" target="_blank">Supported formats</a></dd>
				<dt><fmt:message key="queryParams"/></dt>
				<dd><table class="props">
					<thead>
						<tr>
							<th><fmt:message key="position"/></th>
							<th><fmt:message key="value"/></th>
							<th><fmt:message key="action"/></th>
						</tr>
					</thead>
					<tfoot>
						<tr>
							<td colspan="5"><span class="action" title="<fmt:message key="add"/>" onclick="addLine(event, 'params');"><fmt:message key="addIcon"/></span></td>
						</tr>
					</tfoot>
					<tbody><c:forEach begin="0" end="9" var="i" varStatus="st">
						<tr id="params-${i}"<c:if test="${!st.first}"> style="display: none;"</c:if>>
							<td>${i+1}</td>
							<td><input type="text" name="params[${i}]" id="params${i}.value"/></td>
							<td><c:if test="${!st.last}">
								<span class="action" title="<fmt:message key="down"/>" onclick="return moveLineDown(event, 'params', ${i});"><fmt:message key="downIcon"/></span>
</c:if><c:if test="${!st.first}"
>								<span class="action" title="<fmt:message key="up"/>" onclick="return moveLineUp(event, 'params', ${i});"><fmt:message key="upIcon"/></span>
								<span class="action" title="<fmt:message key="remove"/>" onclick="return removeLine(event, 'params', ${i});"><fmt:message key="removeIcon"/></span>
</c:if
>							</td>
						</tr></c:forEach>
					</tbody>
				</table></dd>
				<dt>&nbsp;</dt>
				<dd><input type="submit" value="<fmt:message key="apply"/>"/>
					<input type="reset" value="<fmt:message key="cancel"/>"/></dd>
			</dl>
		</form>
	</div></div>
<%@
	include file="../include/footer.jspf"
%>
