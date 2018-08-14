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
%><%--
	Glassfish bug: Application JARs are not on the JSP precompile classpath
	attribute name="rs" required="true" type="de.tweerlei.dbgrazer.query.model.RowContainer" rtexprvalue="true" description="The RowSet"
--%><%@
	attribute name="rs" required="true" type="java.lang.Object" rtexprvalue="true" description="The RowSet"
%><%@
	attribute name="label" required="true" type="java.lang.String" rtexprvalue="true" description="Unique control name, used for generating element IDs"
%><%@
	attribute name="root" required="true" type="java.lang.Object" rtexprvalue="true" description="The current root row"
%><%@
	attribute name="targetElement" required="false" type="java.lang.String" rtexprvalue="true" description="The link target element"
%><%@
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@
	taglib prefix="ui" tagdir="/WEB-INF/tags"
%><c:choose
	><c:when test="${empty root.rows}"
	></c:when><c:when test="${rs.query.attributes['tables'] && rs.query.type.singleColumnSet}"
		><ui:result-statictree-singletable rs="${rs}" label="${label}" root="${root}" level="0" targetElement="${targetElement}"
	/></c:when><c:when test="${rs.query.attributes['tables']}"
		><ui:result-statictree-table rs="${rs}" label="${label}" root="${root}" targetElement="${targetElement}"
	/></c:when><c:otherwise
		><ui:result-statictree-div rs="${rs}" label="${label}" root="${root}" targetElement="${targetElement}"
	/></c:otherwise
></c:choose>