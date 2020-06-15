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
%><spring:form id="f1" action="db/${currentConnection.linkName}/ddl-add-index.html" method="post" modelAttribute="model" onsubmit="return submitDML(this);" onreset="return closeDialog();">
	<spring:hidden path="catalog"/>
	<spring:hidden path="schema"/>
	<spring:hidden path="object"/>
	<div id="dmlerror"></div>
	<dl>
		<dt><spring:label path="name"><fmt:message key="name"/></spring:label></dt>
		<dd><spring:input path="name"/></dd>
		<dt><spring:label path="unique"><fmt:message key="unique"/></spring:label></dt>
		<dd><spring:checkbox path="unique"/></dd>
		<dt><spring:label path="columns[0]"><fmt:message key="columns"/></spring:label></dt>
		<dd><spring:select path="columns[0]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select><br/>
			<spring:select path="columns[1]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select><br/>
			<spring:select path="columns[2]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select><br/>
			<spring:select path="columns[3]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select><br/>
			<spring:select path="columns[4]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select><br/>
			<spring:select path="columns[5]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select><br/>
			<spring:select path="columns[6]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select><br/>
			<spring:select path="columns[7]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select><br/>
			<spring:select path="columns[8]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select><br/>
			<spring:select path="columns[9]">
				<spring:option value=""/>
				<spring:options items="${columns}" itemLabel="name" itemValue="name"/>
			</spring:select></dd>
		<dt>&nbsp;</dt>
		<dd><input id="f1-submit" type="submit" value="<fmt:message key="insertRow"/>"/> <input id="f1-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</spring:form>
