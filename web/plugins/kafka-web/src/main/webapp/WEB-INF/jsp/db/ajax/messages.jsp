<%@
	include file="../../include/include.jspf"
%><c:set var="targetElement" value="explorer-right"
/><c:forEach items="${tabs}" var="i" varStatus="st"
	><c:set var="label" value="combo${st.index}"
	/><c:set var="rs" value="${i.value.payload}"
	/><%@
		include file="../result/messages.jspf"
%></c:forEach>