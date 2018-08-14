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
	tag description="Custom tag that displays a text or a localized message if the text is empty"
%><%@
	attribute name="text" required="true" type="java.lang.String" rtexprvalue="true" description="The text to display"
%><%@
	attribute name="key" required="true" type="java.lang.String" rtexprvalue="true" description="The alternative message key"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><c:choose
	><c:when test="${empty text}"><fmt:message key="${key}"/></c:when
	><c:otherwise>${text}</c:otherwise
></c:choose>