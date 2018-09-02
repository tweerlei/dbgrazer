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
	tag description="Custom tag that generates a table from a RowSet"
%><%@
	attribute name="rs" required="true" type="de.tweerlei.dbgrazer.query.model.RowSet" rtexprvalue="true" description="The RowSet"
%><%@
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="targetElement" required="false" type="java.lang.String" rtexprvalue="true" description="The link target element"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@
	taglib prefix="tools" uri="http://tweerlei.de/springtools/tags"
%><%@
	taglib prefix="file" uri="http://tweerlei.de/dbgrazer/web/taglib/file/FileFunctions"
%><%@
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%><table id="table-${label}" class="multiple table-autosort">
			<thead>
				<tr>
<c:forEach items="${rs.columns}" var="c" begin="0" end="2"
>					<th class="table-sortable <c:choose
						><c:when test="${c.type == 'INTEGER' || c.type == 'FLOAT'}">table-sortable:numeric</c:when
						><c:when test="${c.type == 'DATE'}">table-sortable:date</c:when
						><c:otherwise>table-sortable:ignorecase</c:otherwise
					></c:choose>" title="${fn:escapeXml(c.typeName)}">${fn:escapeXml(c.name)}</th>
</c:forEach
>				</tr>
			</thead>
			<tbody>
<c:forEach items="${rs.rows}" var="row"
>				<tr>
<c:forEach items="${row.values}" var="v" varStatus="st" begin="0" end="2"
>					<td><c:choose
						><c:when test="${st.first}"><a href="db/${currentConnection.linkName}/file.html?path=${tools:urlEncode(v)}">${fn:escapeXml(file:basename(v))}</a><c:if test="${currentConnection.writable}"
							> <span class="action" title="<fmt:message key="renameFile"/>" onclick="return showDbDialog(event, 'file-rename', {path: '${fn:escapeXml(v)}'}, '<fmt:message key="renameFile"/>');"><fmt:message key="editIcon"/></span> <span class="action" title="<fmt:message key="removeFile"/>" onclick="return showDbDialog(event, 'file-remove', {path: '${fn:escapeXml(v)}'}, '<fmt:message key="removeFile"/>');"><fmt:message key="removeIcon"/></span></c:if
						></c:when><c:otherwise>${fn:escapeXml(v)}</c:otherwise
					></c:choose></td>
</c:forEach
>				</tr>
</c:forEach
>			</tbody>
		</table>
