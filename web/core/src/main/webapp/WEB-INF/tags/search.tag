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
	tag description="Custom tag that displays the query search form"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><div class="h1-buttons"><form action="db/${currentConnection.linkName}/index.html" method="get">
		<label for="searchTerm"><fmt:message key="searchTerm"/>:</label>
		<input id="searchTerm" type="text" name="q" value="${fn:escapeXml(currentConnection.search)}" onkeyup="newSearchTimer.start(500, this.value);"/>
<c:if test="${not empty currentConnection.search}"><a class="action" href="db/${currentConnection.linkName}/index.html?q=" title="<fmt:message key="delete"/>">&#x2716;</a>
</c:if
><%--<input type="submit" value="<fmt:message key="searchQueries"/>"/>
--%>	</form></div>