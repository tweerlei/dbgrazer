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
	tag description="Custom tag that creates a modification history list
	'%%' in editActionTemplate will be replaced with the entry version."
%><%@
	attribute name="items" required="true" type="java.util.List" rtexprvalue="true" description="The HistoryEntry items"
%><%@
	attribute name="editActionTemplate" required="false" type="java.lang.String" rtexprvalue="true" description="Optional JavaScript to edit an entry, %% will be replaced with the entry version"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><c:forEach items="${history}" var="h"
>		<div class="menuitem"><c:choose
			><c:when test="${not empty editActionTemplate}"><span onclick="${fn:replace(editActionTemplate, '%%', h.version)}"><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${h.date}"/></span></c:when
			><c:otherwise><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${h.date}"/></c:otherwise
			></c:choose> - ${fn:escapeXml(h.message)}</div>
</c:forEach
>