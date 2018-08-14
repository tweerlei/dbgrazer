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
	tag description="Custom tag that displays a menu with multiple columns"
%><%@
	attribute name="items" required="true" type="java.util.Map" rtexprvalue="true" description="Map: Menu item name -> URL"
%><%@
	attribute name="rows" required="true" type="java.lang.Integer" rtexprvalue="true" description="The number of rows per column"
%><%@
	attribute name="url" required="false" type="java.lang.String" rtexprvalue="true" description="Optional base URL to prepend to all generated links"
%><%@
	attribute name="action" required="false" type="java.lang.String" rtexprvalue="true" description="Optional JavaScript to activate an item"
%><%@
	attribute name="separate" required="false" type="java.lang.Boolean" rtexprvalue="true" description="Insert a separator between items starting with different letters"
%><%@
	attribute name="subitems" required="false" type="java.lang.Boolean" rtexprvalue="true" description="If true, map items are interpreted as Map: subitem name -> URL"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@
	taglib prefix="util" uri="http://tweerlei.de/dbgrazer/web/taglib/JspFunctions"
%><%@
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%><div class="menucolumn">
<c:choose><c:when test="${empty items}"
	><div class="menutext"><fmt:message key="noEntry"/></div>
</c:when><c:otherwise
	><c:set var="itemStart" value=" "
	/><c:set var="itemCount" value="0"
	/><c:set var="lim" value="${util:getMenuRows(fn:length(items), menuRatio)}"
	/><c:forEach items="${items}" var="c" varStatus="st"
		><c:choose><c:when test="${(st.index > 0) && (st.index mod lim == 0)}"
			><c:if test="${!fn:startsWith(c.key, itemStart)}"
				><c:set var="itemStart" value="${fn:substring(c.key, 0, 1)}"
				/><c:set var="itemCount" value="0"
			/></c:if></div>
<div class="menucolumn">
</c:when><c:when test="${!fn:startsWith(c.key, itemStart)}"
			><c:if test="${separate && itemCount > 1}"
				><hr class="menuseparator"/>
</c:if
			><c:set var="itemStart" value="${fn:substring(c.key, 0, 1)}"
			/><c:set var="itemCount" value="0"
		/></c:when></c:choose
		><div class="menuitem"><c:choose><c:when test="${subitems && fn:length(c.value) == 1}"
			><c:forEach items="${c.value}" var="d"><a href="${url}${fn:escapeXml(d.value)}"<c:if test="${not empty action}"> onclick="${fn:replace(action, '%%', fn:escapeXml(d.value))}"</c:if>><ui:shorten value="${c.key}" length="50"/></a></c:forEach></c:when
		><c:when test="${subitems}"
			><div class="hidden" id="submenu-${st.index}"><div class="menucolumn"><c:forEach items="${c.value}" var="d"
				><div class="menuitem"><a href="${url}${fn:escapeXml(d.value)}"<c:if test="${not empty action}"> onclick="${fn:replace(action, '%%', fn:escapeXml(d.value))}"</c:if>><ui:shorten value="${d.key}" length="50"/></a></div></c:forEach
			></div></div><span onclick="return showElementMenu(event, 'submenu-${st.index}');"><ui:shorten value="${c.key}" length="50"/> &#x25ba;</span></c:when
		><c:otherwise
			><a href="${url}${fn:escapeXml(c.value)}"<c:if test="${not empty action}"> onclick="${fn:replace(action, '%%', fn:escapeXml(c.value))}"</c:if>><ui:shorten value="${c.key}" length="50"/></a></c:otherwise
		></c:choose></div>
<c:set var="itemCount" value="${itemCount + 1}"
	/></c:forEach
></c:otherwise></c:choose
></div>