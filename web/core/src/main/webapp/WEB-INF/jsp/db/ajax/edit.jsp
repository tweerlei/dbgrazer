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
	include file="../../include/include.jspf"
%><ui:extensions items="${extensions}" separatorAfter="true"/><c:if test="${currentConnection.submitEnabled && currentConnection.type.customQuerySupported}"
><div class="menuitem"><a href="db/${currentConnection.linkName}/submit-${currentConnection.type}.html"><fmt:message key="sqlQuery"/></a></div>
</c:if><c:if test="${currentConnection.editorEnabled}"
><div class="menuitem"><a href="db/${currentConnection.linkName}/edit.html"><fmt:message key="newQuery"/></a></div>
<div class="menuitem"><a href="db/${currentConnection.linkName}/check.html"><fmt:message key="checkQueries"/></a></div>
<div class="menuitem"><a href="db/${currentConnection.linkName}/types.html"><fmt:message key="queriesByType"/></a></div>
<div class="menuitem"><a href="db/${currentConnection.linkName}/parameters.html"><fmt:message key="queriesByParam"/></a></div>
<div class="menuitem"><a href="db/${currentConnection.linkName}/dialect.html"><fmt:message key="querySets"/></a></div>
<c:if test="${not empty currentConnection.schemaVersion}"
><div class="menuitem"><a href="db/${currentConnection.linkName}/subschema.html"><fmt:message key="subschemaQueries"><fmt:param value="${currentConnection.schemaVersion}"/></fmt:message></a></div>
</c:if
><div class="menuitem"><span onclick="return showConfirmDialog('<fmt:message key="reloadQueries"/>', Messages.reloadQueriesText, 'db/${currentConnection.linkName}/reload-queries.html');"><fmt:message key="reloadQueries"/></span></div>
<hr class="menuseparator"/>
<div class="menuitem"><span onclick="return toggleEditMode();"><fmt:message key="previewMode"/> (<fmt:message key="previewModeShortcut"/>)</span></div>
</c:if
><div class="menuitem"><span onclick="return showSearchDialog(event);"><fmt:message key="searchQueries"/> (<fmt:message key="searchQueriesShortcut"/>)</span></div>
<c:if test="${currentConnection.wsApiEnabled}"
><hr class="menuseparator"/>
<div class="menuitem"><a href="ws/${currentConnection.linkName}/index.html"><fmt:message key="restAPI"/></a></div>
</c:if
>