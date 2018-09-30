<%@
	include file="../include/include.jspf"
%><fmt:message key="kafkaBrowser" var="pageTitle"/><%@
	include file="../include/header.jspf"
%><c:set var="targetElement" value="explorer-right"
/>
	<script type="text/javascript">/*<![CDATA[*/
	
	function reloadPage() {
		return refreshTopic();
	}
	
	/*]]>*/</script>
	
	<ui:headline1 label="${pageTitle}">
	<div class="h1-actions">
		<span class="menu" onclick="return showElementMenu(event, 'tools-1');"><fmt:message key="actions"/></span>
	</div>
	<div class="h1-actions">
		<span class="action" title="<fmt:message key="refresh"/>" onclick="return reloadPage();"><fmt:message key="refreshIcon"/></span>
		<a class="action" title="<fmt:message key="newWindow"/>" href="db/${currentConnection.linkName}/files.html" target="_blank"><fmt:message key="newWindowIcon"/></a>
	</div>
	
	<div id="tools-1" class="hidden"><div class="menucolumn">
		<div class="menuitem"><span onclick="return showDbDialog(event, 'send-message', { topic: '${topic}' }, '<fmt:message key="send"/>');"><fmt:message key="send"/></span></div>
	</div></div>
	</ui:headline1>
	
	<div id="submitresult"></div>
	
	<c:set var="links" value="db/${currentConnection.linkName}/topics.html,db/${currentConnection.linkName}/partitions.html?topic=${topic}"
	/><c:set var="links" value="${fn:split(links, ',')}"
	/><ui:explorer><ui:multilevel query="${query.name}" levels="${query.subQueries}" params="${params}" links="${links}" items="${tabs}" var="rs" varKey="label" varParams="detailParams" varParamString="detailParamString" name="combo"
		><ui:result-topicpartition rs="${rs}" label="${label}" link="db/${currentConnection.linkName}/messages.html?topic=${topic}&amp;partition=%%" targetElement="${targetElement}"
	/></ui:multilevel></ui:explorer>
<%@
	include file="../include/footer.jspf"
%>
