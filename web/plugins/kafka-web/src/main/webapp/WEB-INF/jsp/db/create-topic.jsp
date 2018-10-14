<%@
	include file="../include/js.jspf"
%><c:choose
><c:when test="${exceptionText == null}">forceReload();</c:when
><c:otherwise>submitDMLError(${exceptionText});</c:otherwise
></c:choose>