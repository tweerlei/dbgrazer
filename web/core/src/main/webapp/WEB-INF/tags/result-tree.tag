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
	tag description="Custom tag that generates tree rows from a RowSet"
%><%@
	attribute name="rs" required="true" type="de.tweerlei.dbgrazer.query.model.RowSet" rtexprvalue="true" description="The RowSet"
%><%@
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="level" required="true" type="java.lang.Integer" rtexprvalue="true" description="The tree hierarchy level"
%><%@
	attribute name="targetElement" required="false" type="java.lang.String" rtexprvalue="true" description="The link target element"
%><%@
	attribute name="left" required="false" type="java.lang.String" rtexprvalue="true" description="The parent IDs, separated by dashes"
%><%@
	attribute name="first" required="false" type="java.lang.Boolean" rtexprvalue="true" description="Whether the RowSet is the primary result for this level"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%><c:choose
	><c:when test="${empty rs.rows}"
	></c:when><c:when test="${rs.attributes['parentQuery'].attributes['tables']}"
		><ui:result-tree-table rs="${rs}" label="${label}" level="${level}" targetElement="${targetElement}" left="${left}" first="${first}"
	/></c:when><c:otherwise
		><ui:result-tree-div rs="${rs}" label="${label}" level="${level}" targetElement="${targetElement}" left="${left}" first="${first}"
	/></c:otherwise
></c:choose
>