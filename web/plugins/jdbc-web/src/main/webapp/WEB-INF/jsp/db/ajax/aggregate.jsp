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
%><c:choose><c:when test="${exception != null}">
<tr>
	<td colspan="1000"><pre>${fn:escapeXml(exception.message)}</pre></td>
</tr>
</c:when><c:when test="${empty result.rows}">
<tr>
	<td colspan="1000"><fmt:message key="noData"/></td>
</tr>
</c:when><c:otherwise><c:forEach items="${result.rows}" var="row">
<tr>
<c:forEach items="${row.values}" var="v" varStatus="st"><c:choose><c:when test="${st.first}"
>	<td>${v}</td>
</c:when><c:otherwise
>	<td><ui:filterlink value="${v}" label="${fn:escapeXml(label)}" column="${st.index - 1}" target="${result.columns[tools:toInt(st.index - 1)].targetQuery}" targetElement="${fn:escapeXml(targetElement)}"/></td>
</c:otherwise></c:choose></c:forEach
></tr>
</c:forEach></c:otherwise></c:choose>