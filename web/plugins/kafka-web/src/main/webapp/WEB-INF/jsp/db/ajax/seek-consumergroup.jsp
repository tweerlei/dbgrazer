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
%><spring:form id="f3" action="db/${currentConnection.linkName}/seek-consumergroup.html" method="post" modelAttribute="model" onsubmit="return submitDML(this);" onreset="return closeDialog();">
	<div id="dmlerror"></div>
	<input type="hidden" name="group" value="${fn:escapeXml(group)}"/>
	<dl>
		<dt><label for="f3-topic"><fmt:message key="kafkaTopic"/></label></dt>
		<dd><input type="text" id="f3-topic" name="topic" value="${fn:escapeXml(topic)}"/></dd>
		<dt><label for="f3-partition"><fmt:message key="kafkaPartition"/></label></dt>
		<dd><input type="text" id="f3-partition" name="partition" value="${partition}"/></dd>
		<dt><label for="f3-offset"><fmt:message key="kafkaOffset"/></label></dt>
		<dd><input type="text" id="f3-offset" name="offset" value="${offset}"/></dd>
		<dt>&nbsp;</dt>
		<dd><input id="f3-submit" type="submit" value="<fmt:message key="send"/>"/> <input id="f3-reset" type="reset" value="<fmt:message key="cancel"/>"/></dd>
	</dl><hr/>
</spring:form>
