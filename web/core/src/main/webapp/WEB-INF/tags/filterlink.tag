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
	attribute name="value" required="true" type="java.lang.Object" rtexprvalue="true" description="The value to display"
%><%@
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="column" required="true" type="java.lang.Integer" rtexprvalue="true" description="The column index"
%><%@
	attribute name="target" required="false" type="de.tweerlei.dbgrazer.query.model.TargetDef" rtexprvalue="true" description="The link TargetDef"
%><%@
	attribute name="targetElement" required="false" type="java.lang.String" rtexprvalue="true" description="The link target element"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@
	taglib prefix="util" uri="http://tweerlei.de/dbgrazer/web/taglib/JspFunctions"
%><c:choose
><c:when test="${value == null}">&#x2205;</c:when
><c:when test="${target == null}"><span class="aggfilter" onclick="return aggfilter(this, '${label}', ${column});">${fn:escapeXml(value)}</span></c:when
><c:when test="${target.parameter}"><span class="aggfilter" onclick="return aggfilter(this, '${label}', ${column});">${fn:escapeXml(util:getLinkTitle(value))}</span>&nbsp;<span class="action" onclick="return chooseTarget(event, '${target.parameterName}', '${util:paramEncode(value)}', '${targetElement}');">&#x25bc;</span></c:when
><c:when test="${not empty targetElement}"><span class="aggfilter" onclick="return aggfilter(this, '${label}', ${column});">${fn:escapeXml(util:getLinkTitle(value))}</span>&nbsp;<a class="action" href="db/${currentConnection.linkName}/result.html?q=${target.queryName}${util:paramEncode(value)}" onclick="return runQuery(event, '${target.queryName}', '${util:paramEncode(value)}', '${targetElement}');">&#x25ba;</a></c:when
><c:otherwise><span class="aggfilter" onclick="return aggfilter(this, '${label}', ${column});">${fn:escapeXml(util:getLinkTitle(value))}</span>&nbsp;<a class="action" href="db/${currentConnection.linkName}/result.html?q=${target.queryName}${util:paramEncode(value)}">&#x25ba;</a></c:otherwise
></c:choose>