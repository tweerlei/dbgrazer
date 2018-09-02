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
	tag description="Custom tag that creates a dashboard panel"
%><%@
	attribute name="title" required="true" type="java.lang.String" rtexprvalue="true" description="The panel title"
%><%@
	attribute name="zoomable" required="false" type="java.lang.Boolean" rtexprvalue="true" description="Whether to generate a zoom button"
%><%@
	attribute name="collapsed" required="false" type="java.lang.Boolean" rtexprvalue="true" description="Whether to initially collapse the panel"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><div class="dashboard<c:if test="${collapsed}"> collapsed</c:if>">
			<div class="dashboard-title">
				<div class="dashboard-tab">${title}<c:if test="${zoomable}"> <span class="action" title="<fmt:message key="maximize"/>" onclick="return zoomTab(event);"><fmt:message key="maximizeIcon"/></span></c:if
													><c:if test="${collapsed}"> <span class="action" title="<fmt:message key="expand"/>" onclick="return expandTab(event);">&#x25ba;</span></c:if></div>
				<hr/>
			</div>
			<div class="dashboard-body"><jsp:doBody/></div>
		</div>