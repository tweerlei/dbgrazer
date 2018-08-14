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
	tag description="Custom tag that creates hidden form fields"
%><%@
	attribute name="items" required="true" type="java.util.Map" rtexprvalue="true" description="Map: Index to value"
%><%@
	attribute name="name" required="true" type="java.lang.String" rtexprvalue="true" description="Input name"
%><%@
	attribute name="id" required="false" type="java.lang.String" rtexprvalue="true" description="Input ID"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><c:forEach items="${items}" var="p"
><input type="hidden"<c:if test="${not empty id}"> id="${id}${p.key}"</c:if> name="${name}[${p.key}]" value="${fn:escapeXml(p.value)}"/></c:forEach>
