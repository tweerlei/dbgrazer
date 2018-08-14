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
%><style>/*<![CDATA[*/
<c:forEach items="${tableColumns}" var="tc" varStatus="st1"><c:forEach items="${tc}" var="col" varStatus="st2"><c:choose
><c:when test="${col.type.name == 'INTEGER'}"
>		#table-result${st1.index} td:nth-child(${st2.index + 1}) { text-align: right; }
</c:when><c:when test="${col.type.name == 'FLOAT'}"
>		#table-result${st1.index} td:nth-child(${st2.index + 1}) { text-align: right; }
</c:when
></c:choose></c:forEach
>	
</c:forEach
>	/*]]>*/</style><c:set var="targetElement" value="explorer-right"
/><ui:errors label="result" var="e"/><%@
	include file="../result/explorer.jspf"
%>