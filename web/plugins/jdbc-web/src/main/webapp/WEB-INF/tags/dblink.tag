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
	tag description="Custom tag that displays a value and optionally links it to a query or parameter"
%><%@
	attribute name="value" required="true" type="java.lang.Object" rtexprvalue="true" description="The link value"
%><%@
	attribute name="label" required="false" type="java.lang.String" rtexprvalue="true" description="The value to display"
%><%@
	attribute name="fk" required="false" type="de.tweerlei.common5.jdbc.model.ForeignKeyDescription" rtexprvalue="true" description="The foreign key"
%><%@
	attribute name="dir" required="false" type="java.lang.Boolean" rtexprvalue="true" description="The direction (true = up, false = down)"
%><%@
	attribute name="targetElement" required="false" type="java.lang.String" rtexprvalue="true" description="The link target element"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><c:choose
><c:when test="${value == null}">&#x2205;</c:when
><c:when test="${fk == null && (fn:startsWith(value, 'http://') || fn:startsWith(value, 'https://'))}"><a class="external" href="${fn:escapeXml(value)}" target="_blank">${fn:escapeXml(fn:substringAfter(value, '//'))}</a></c:when
><c:when test="${fk == null}">${fn:escapeXml((empty label) ? value : label)}</c:when
><c:when test="${not empty targetElement}"><a href="db/${currentConnection.linkName}/dbobject.html?catalog=${fk.tableName.catalogName}&amp;schema=${fk.tableName.schemaName}&amp;object=${fk.tableName.objectName}#!where=<c:forEach items="${fk.columns}" var="c">${dir ? c.key : c.value}!3!${fn:escapeXml(value)}</c:forEach>" onclick="return showDBObject(event, '${fk.tableName.catalogName}', '${fk.tableName.schemaName}', '${fk.tableName.objectName}', null, null, null, '<c:forEach items="${fk.columns}" var="c">${dir ? c.key : c.value}=${fn:escapeXml(value)}</c:forEach>', '', '${targetElement}');"<c:if test="${empty label}"> title="${fk.tableName.objectName}"</c:if>>${fn:escapeXml((empty label) ? value : label)}</a></c:when
><c:otherwise><a href="db/${currentConnection.linkName}/dbobject.html?catalog=${fk.tableName.catalogName}&amp;schema=${fk.tableName.schemaName}&amp;object=${fk.tableName.objectName}#!where=<c:forEach items="${fk.columns}" var="c">${dir ? c.key : c.value}!3!${fn:escapeXml(value)}</c:forEach>"<c:if test="${empty label}"> title="${fk.tableName.objectName}"</c:if>>${fn:escapeXml((empty label) ? value : label)}</a></c:otherwise
></c:choose>