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
--%><%--
	Send a 500 response in case of an internal error
--%><%@
	include file="../include/ws.jspf"
%><c:choose><c:when test="${alreadyRunning != null}"
><fmt:message key="alreadyRunning"/>
<c:forEach items="${progress}" var="p"
	><fmt:message key="${p.key}"><fmt:param value="${p.value.done}"/></fmt:message
	><c:if test="${p.value.cancelled}"> - <fmt:message key="cancelled"/></c:if>
</c:forEach
></c:when><c:when test="${cancelled != null}"
><fmt:message key="cancelledByUser"/>
</c:when><c:when test="${exception != null}"
>${exception.message}
</c:when><c:otherwise
>${result}
</c:otherwise></c:choose
>