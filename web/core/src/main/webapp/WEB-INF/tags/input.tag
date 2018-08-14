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
	tag description="Custom tag that creates an INPUT control that uses an AJAX dialog for selecting a value"
%><%@
	attribute name="name" required="true" type="java.lang.String" rtexprvalue="true" description="The control name"
%><%@
	attribute name="id" required="true" type="java.lang.String" rtexprvalue="true" description="The element ID"
%><%@
	attribute name="value" required="false" type="java.lang.String" rtexprvalue="true" description="The default value"
%><%@
	attribute name="page" required="true" type="java.lang.String" rtexprvalue="true" description="The AJAX page name"
%><%@
	attribute name="disabled" required="false" type="java.lang.Boolean" rtexprvalue="true" description="Whether to initially disable the control"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><span class="select"><input type="text"<c:if test="${size != null}"></c:if> id="${id}" name="${name}" value="${fn:escapeXml(value)}"<c:if test="${disabled}"> disabled="disabled"</c:if>/><span class="action" onclick="return Forms.isEnabled('${id}') ? showPopup(event, '${page}', { q: \$F('${id}'), id: '${id}' }, '<fmt:message key="chooseValue"/>') : false;"> &#x25bc;</span></span>