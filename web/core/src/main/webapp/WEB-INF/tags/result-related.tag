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
	tag description="Custom tag that generates related queries from a RowSet"
%><%@
	attribute name="rs" required="true" type="de.tweerlei.dbgrazer.query.model.RowSet" rtexprvalue="true" description="The RowSet"
%><%@
	attribute name="targetElement" required="false" type="java.lang.String" rtexprvalue="true" description="The link target element"
%><%@
	attribute name="params" required="false" type="java.util.Map" rtexprvalue="true" description="The query parameters"
%><%@
	attribute name="paramString" required="false" type="java.lang.String" rtexprvalue="true" description="The query parameters encoded for URL usage"
%><%@
	attribute name="tab" required="false" type="java.lang.Boolean" rtexprvalue="true" description="Whether to wrap the content in a tab-body tag"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%>
<c:if test="${!rs.attributes['parentQuery'].type.explorer}"
>		<div<c:if test="${tab}"> class="tab-header"</c:if>><fmt:message key="relatedQueries"><fmt:param value="${rs.attributes['parentQuery'].name}"/></fmt:message>:</div>
</c:if
>		<div<c:if test="${tab}"> class="tab-body"</c:if>>
<c:if test="${not empty rs.rows[0].values}"
>		<br/>
		<ui:headline2 key="views"/>
			<ul>
<c:forEach items="${rs.rows[0].values}" var="v"
>				<li><c:choose
					><c:when test="${v.type.explorer}"><span title="<fmt:message key="type_explorer"/>">&#x25eb;</span></c:when
					><c:when test="${v.type.resultType == 'VISUALIZATION'}"><span title="<fmt:message key="type_visualization"/>">&#x25ea;</span></c:when
					><c:when test="${v.type.resultType.view}"><span title="<fmt:message key="type_view"/>">&#x25a3;</span></c:when
					><c:otherwise><span title="<fmt:message key="type_other"/>">&#x25a4;</span></c:otherwise
				></c:choose> <a href="db/${currentConnection.linkName}/result.html?q=${v.name}${paramString}"<c:if test="${not empty targetElement}"> onclick="return runQuery(event, '${v.name}', '${paramString}${rs.attributes['parentQuery'].type.explorer ? '&amp;related=false' : ''}', '${targetElement}');"</c:if>>${v.name}</a><c:if test="${currentConnection.editorActive}"
					> <a class="action" title="<fmt:message key="editQuery"/>" href="db/${currentConnection.linkName}/edit.html?q=${v.name}">&#x270e;</a></c:if
				></li>
</c:forEach
>			</ul><hr/>
</c:if><c:if test="${not empty rs.rows[1].values}"
>		<br/>
		<ui:headline2 key="queries"/>
			<ul>
<c:forEach items="${rs.rows[1].values}" var="v"
>				<li><c:choose
					><c:when test="${v.type.explorer}"><span title="<fmt:message key="type_explorer"/>">&#x25eb;</span></c:when
					><c:when test="${v.type.resultType == 'VISUALIZATION'}"><span title="<fmt:message key="type_visualization"/>">&#x25ea;</span></c:when
					><c:when test="${v.type.resultType.view}"><span title="<fmt:message key="type_view"/>">&#x25a3;</span></c:when
					><c:otherwise><span title="<fmt:message key="type_other"/>">&#x25a4;</span></c:otherwise
				></c:choose> <a href="db/${currentConnection.linkName}/query.html?q=${v.name}${paramString}" target="_blank">${v.name}</a><c:if test="${currentConnection.editorActive}"
					> <a class="action" title="<fmt:message key="editQuery"/>" href="db/${currentConnection.linkName}/edit.html?q=${v.name}">&#x270e;</a></c:if
				></li>
</c:forEach
>			</ul><hr/>
</c:if
>		</div>
		