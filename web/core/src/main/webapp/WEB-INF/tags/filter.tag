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
	tag description="Custom tag that creates an input element for filtering another element's content"
%><%@
	attribute name="id" required="true" type="java.lang.String" rtexprvalue="true" description="Input element ID"
%><%@
	attribute name="target" required="true" type="java.lang.String" rtexprvalue="true" description="Element IDs to apply the filter to, separated by commas"
%><%@
	attribute name="form" required="false" type="java.lang.Boolean" rtexprvalue="true" description="Whether to wrap the input element in a dummy form element"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><c:if test="${form}"
><form class="filter" action="#" method="get" onsubmit="return false;"></c:if
><label for="${id}"><fmt:message key="filter"/>:</label> <input type="text" name="${id}" id="${id}" oninput="selectFilter.start(500, this.value, 'filtercount-${id}'<c:forTokens items="${target}" delims=", " var="f">, '${f}'</c:forTokens>);"/><span id="filtercount-${id}"></span><c:if test="${form}"
></form></c:if
>