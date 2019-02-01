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
%><c:choose><c:when test="${exception != null}"
	><div class="treerow"><div class="treebutton">&#x25b7;</div><div class="treelabel">${fn:escapeXml(exception.message)}</div></div>
</c:when><c:when test="${fn:length(rs.rows) == 0}"
	><div class="treerow"><div class="treebutton">&#x25b7;</div><div class="treelabel"><fmt:message key="empty"/></div></div>
</c:when><c:otherwise
	><ui:result-dirtree rs="${rs}" label="${fn:escapeXml(label)}" left="${fn:escapeXml(left)}" targetElement="${fn:escapeXml(targetElement)}"
/></c:otherwise></c:choose>