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
		<ui:headline2 key="structureCompare">
		<form class="h2-actions" action="db/${model.connection2}/dbcompare2.html" method="get">
			:
			<a href="db/${model.connection2}/dbcatalogs.html">${model.connection2}</a> &raquo;
			<a href="db/${model.connection2}/dbschemas.html?catalog=${model.catalog2}"><ui:message text="${model.catalog2}" key="defaultCatalog"/></a> &raquo;
			<a href="db/${model.connection2}/dbobjects.html?catalog=${model.catalog2}&amp;schema=${model.schema2}"><ui:message text="${model.schema2}" key="defaultSchema"/></a> &raquo;
			<a href="db/${model.connection2}/dbobject.html?catalog=${model.catalog2}&amp;schema=${model.schema2}&amp;object=${model.object}">${model.object}</a>
			<input type="hidden" name="catalog" value="${model.catalog2}"/>
			<input type="hidden" name="schema" value="${model.schema2}"/>
			<input type="hidden" name="object" value="${model.object}"/>
			<input type="hidden" name="connection2" value="${currentConnection.linkName}"/>
			<input type="hidden" name="catalog2" value="${model.catalog}"/>
			<input type="hidden" name="schema2" value="${model.schema}"/>
			<input type="submit" value="<fmt:message key="reverse"/>"/>
		</form>
		<div class="h2-actions">
			<span class="button" onclick="return downloadElement('src', 'text/plain;charset=utf-8');"><fmt:message key="downloadText"/></span>
		</div>
		<div class="h2-actions">
			<span class="action" title="<fmt:message key="maximize"/>" onclick="return toggleElement('zoomable1');">&#x25f1;</span>
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
>		<pre id="src">${fn:escapeXml(result)}</pre>
</c:otherwise></c:choose
>	</div></div>
	<script type="text/javascript">
		window.top.window.uploadFinished(document.getElementById('uploadResult'));
	</script>