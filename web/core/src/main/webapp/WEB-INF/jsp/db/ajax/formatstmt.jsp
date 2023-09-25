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
%><form class="content" action="db/${currentConnection.linkName}/ajax/formatstmt.html" method="post" onsubmit="return getFormInto(this, this.parentNode);">
	<div class="filter"><fmt:message key="formatter"/> <select id="f1-format" name="format" onchange="Forms.submit(form);">
			<option value=""><fmt:message key="default"/></option>
<c:forEach items="${formats}" var="f"
>			<option value="${f}"<c:if test="${f == format}"> selected="selected"</c:if>><fmt:message key="${f}"/></option>
</c:forEach
>	</select></div>
	<div class="filter"><span class="action" title="<fmt:message key="maximize"/>" onclick="return unzoomForm();"><fmt:message key="maximizeIcon"/></span></div>
	<hr/>
	<textarea id="zoomresult" name="statement" style="width: 100%; height: 95%">${result}</textarea>
</form>