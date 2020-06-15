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
%><spring:form id="f1" action="db/${currentConnection.linkName}/ddl-add-foreignkey.html" method="post" modelAttribute="model" onsubmit="return submitDML(this);" onreset="return closeDialog();">
	<spring:hidden path="catalog"/>
	<spring:hidden path="schema"/>
	<spring:hidden path="object"/>
	<input id="f1-connection" type="hidden" name="c" value="${currentConnection.linkName}"/>
	<div id="dmlerror"></div>
	<dl>
		<dt><spring:label path="name"><fmt:message key="name"/></spring:label></dt>
		<dd><spring:input path="name"/></dd>
		<dt><label for="f1-catalog"><fmt:message key="catalog"/></label></dt>
		<dd><select id="f1-catalog" name="catalog2" onchange="return selectCatalog(this);"<c:if test="${empty catalogs}"> disabled="disabled"</c:if>>
			<option value="."><fmt:message key="noSelection"/></option>
<c:forEach items="${catalogs}" var="c"
>			<option value="${c}"<c:if test="${c == catalog}"> selected="selected"</c:if>><ui:message text="${c}" key="defaultCatalog"/></option>
</c:forEach
>			</select></dd>
		<dt><label for="f1-schema"><fmt:message key="schema"/></label></dt>
		<dd><select id="f1-schema" name="schema2" onchange="return selectSchema(this);"<c:if test="${empty schemas}"> disabled="disabled"</c:if>>
			<option value="."><fmt:message key="noSelection"/></option>
<c:forEach items="${schemas}" var="c"
>			<option value="${c}"<c:if test="${c == schema}"> selected="selected"</c:if>><ui:message text="${c}" key="defaultSchema"/></option>
</c:forEach
>			</select></dd>
		<dt><label for="f1-object"><fmt:message key="object"/></label></dt>
		<dd><select id="f1-object" name="object2" onchange="return selectObject(this);"<c:if test="${empty objects}"> disabled="disabled"</c:if>>
			<option value="."><fmt:message key="noSelection"/></option>
<c:forEach items="${objects}" var="c"
>			<option value="${c.objectName}"<c:if test="${c.objectName == object}"> selected="selected"</c:if>>${c.objectName}</option>
</c:forEach
>			</select></dd>
		<dt><spring:label path="fromColumns[0]"><fmt:message key="columns"/></spring:label></dt>
		<dd><spring:select path="fromColumns[0]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select> <spring:select path="toColumns[0]" cssClass="dbcolumn">
				<spring:option value=""/>
			</spring:select><br/>
			<spring:select path="fromColumns[1]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select> <spring:select path="toColumns[1]" cssClass="dbcolumn">
				<spring:option value=""/>
			</spring:select><br/>
			<spring:select path="fromColumns[2]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select> <spring:select path="toColumns[2]" cssClass="dbcolumn">
				<spring:option value=""/>
			</spring:select><br/>
			<spring:select path="fromColumns[3]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select> <spring:select path="toColumns[3]" cssClass="dbcolumn">
				<spring:option value=""/>
			</spring:select><br/>
			<spring:select path="fromColumns[4]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select> <spring:select path="toColumns[4]" cssClass="dbcolumn">
				<spring:option value=""/>
			</spring:select><br/>
			<spring:select path="fromColumns[5]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select> <spring:select path="toColumns[5]" cssClass="dbcolumn">
				<spring:option value=""/>
			</spring:select><br/>
			<spring:select path="fromColumns[6]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select> <spring:select path="toColumns[6]" cssClass="dbcolumn">
				<spring:option value=""/>
			</spring:select><br/>
			<spring:select path="fromColumns[7]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select> <spring:select path="toColumns[7]" cssClass="dbcolumn">
				<spring:option value=""/>
			</spring:select><br/>
			<spring:select path="fromColumns[8]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select> <spring:select path="toColumns[8]" cssClass="dbcolumn">
				<spring:option value=""/>
			</spring:select><br/>
			<spring:select path="fromColumns[9]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select> <spring:select path="toColumns[9]" cssClass="dbcolumn">
				<spring:option value=""/>
			</spring:select></dd>
		<dt>&nbsp;</dt>
		<dd><input id="f1-submit" type="submit" value="<fmt:message key="insertRow"/>"<c:if test="${object == null}"> disabled="disabled"</c:if>/>
			<input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</spring:form>
