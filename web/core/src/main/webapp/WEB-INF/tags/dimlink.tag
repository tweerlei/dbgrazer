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
	tag description="Custom tag that displays a value and links it to a multidimensional query"
%><%@
	attribute name="value" required="true" type="java.lang.Object" rtexprvalue="true" description="The value to display"
%><%@
	attribute name="targetElement" required="false" type="java.lang.String" rtexprvalue="true" description="The link target element"
%><%@
	attribute name="rs" required="true" type="de.tweerlei.dbgrazer.query.model.RowSet" rtexprvalue="true" description="The RowSet"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@
	taglib prefix="tools" uri="http://tweerlei.de/springtools/tags"
%><%@
	taglib prefix="util" uri="http://tweerlei.de/dbgrazer/web/taglib/JspFunctions"
%><c:choose
><c:when test="${not empty targetElement}"><a href="db/${currentConnection.linkName}/result.html?q=${model.query.name}${paramString}&amp;params%5B${rs.query.attributes['dimension']}%5D=${tools:urlEncode(value)}" onclick="return runQuery(event, '${model.query.name}', '${paramString}&amp;params%5B${rs.query.attributes['dimension']}%5D=${tools:urlEncode(value)}', '${targetElement}');">${fn:escapeXml(util:getLinkTitle(value))}</a></c:when
><c:otherwise><a href="db/${currentConnection.linkName}/result.html?q=${model.query.name}${paramString}&amp;params%5B${rs.query.attributes['dimension']}%5D=${tools:urlEncode(value)}">${fn:escapeXml(util:getLinkTitle(value))}</a></c:otherwise
></c:choose>