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
	tag description="Custom tag that generates tree rows from a directory list RowSet"
%><%@
	attribute name="rs" required="true" type="de.tweerlei.dbgrazer.query.model.RowSet" rtexprvalue="true" description="RowSet"
%><%@
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="targetElement" required="false" type="java.lang.String" rtexprvalue="true" description="The link target element"
%><%@
	attribute name="left" required="false" type="java.lang.String" rtexprvalue="true" description="The parent IDs, separated by dashes"
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
%><c:forEach items="${rs.rows}" var="row" varStatus="st"><c:set var="rowid" value="${st.index}"
		/><div class="treerow" id="treerow-${label}-${left}${rowid}"><div class="treebutton"><span class="action" title="<fmt:message key="expand"/>" onclick="return toggleDirTreeItem(event, '${label}', '${fn:escapeXml(row.values[0])}', '${left}${rowid}', '${targetElement}');">&#x25ba;</span></div><div class="treelabel"><a href="db/${currentConnection.linkName}/dir.html?path=${tools:urlEncode(row.values[0])}"<c:if test="${not empty targetElement}"
			> onclick="return showDir(event, '${fn:escapeXml(row.values[0])}');"</c:if
			>>${fn:escapeXml(file:basename(row.values[0]))}</a><c:if test="${currentConnection.writable}"
				> <span class="action" title="<fmt:message key="renameDirectory"/>" onclick="return showDbDialog(event, 'file-rendir', {path: '${fn:escapeXml(row.values[0])}', left: '${left}'}, '<fmt:message key="renameDirectory"/>');">&#x270d;</span> <span class="action" title="<fmt:message key="createDirectory"/>" onclick="return showDbDialog(event, 'file-mkdir', {path: '${fn:escapeXml(row.values[0])}', left: '${left}${rowid}'}, '<fmt:message key="createDirectory"/>');">&#x271a;</span> <span class="action" title="<fmt:message key="removeDirectory"/>" onclick="return showDbDialog(event, 'file-rmdir', {path: '${fn:escapeXml(row.values[0])}', left: '${left}'}, '<fmt:message key="removeDirectory"/>');">&#x2716;</span></c:if></div>
</div></c:forEach>