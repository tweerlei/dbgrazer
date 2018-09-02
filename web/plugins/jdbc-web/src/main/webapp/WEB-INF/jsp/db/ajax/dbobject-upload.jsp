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
%><div id="uploadResult"><div class="tab-body">
		<ui:headline2 key="uploadData">
		<div class="h2-actions">
			<span class="button" onclick="return downloadElement('src', 'text/plain;charset=utf-8');"><fmt:message key="downloadText"/></span>
		</div>
		<div class="h2-actions">
			<span class="action" title="<fmt:message key="maximize"/>" onclick="return toggleElement('zoomable1');"><fmt:message key="maximizeIcon"/></span>
		</div>
		<div class="h2-actions">
			<span class="nowrap"><fmt:message key="duration"><fmt:param value="${rs.queryTime}"/></fmt:message></span>
		</div>
		</ui:headline2>
		
<c:choose><c:when test="${alreadyRunning != null}"
>		<p><fmt:message key="alreadyRunning"/></p>
		<p><ui:progress items="${progress}"/></p>
		<form action="#" method="get" onsubmit="return cancelTasks();">
			<input type="submit" value="<fmt:message key="cancel"/>"/>
		</form>
</c:when><c:when test="${cancelled != null}"
>		<p><fmt:message key="cancelledByUser"/></p>
</c:when><c:when test="${exception != null}"
>		<p><fmt:message key="dataError"/></p>
		
		<ui:headline2 key="errorDetails"/>
		
		<pre>${fn:escapeXml(exception.message)}</pre>
</c:when><c:otherwise
>		<pre id="src">${fn:escapeXml(rs.firstValue)}</pre>
</c:otherwise></c:choose
>	</div></div>
	<script type="text/javascript">
		window.top.window.uploadFinished(document.getElementById('uploadResult'));
	</script>