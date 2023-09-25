function showNamespace(id, link) {
	var ns = $F(id);
	var url = link.replace('%%', ns);
	window.location.href = url;
	return false;
}

function showApiObject(ev, namespace, kind, name, format, formatting) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	var el = $('explorer-right');
	if (el) {
		var params = { namespace: namespace, kind: kind, name: name };
		if (!Object.isUndefined(format)) {
			params.format = format;
		}
		if (formatting) {
			params.formatting = 'true';
		}
		WSApi.getDBAsync('apiobject', params, function(txt) {
			replaceElementContent(el, txt);
			tw_contentChanged();
			HashMonitor.set(params);
			Tabs.hashChanged(HashMonitor.values);
		});
	}
	return false;
}

function refreshApiObject() {
	var namespace = HashMonitor.get('namespace');
	var kind = HashMonitor.get('kind');
	var name = HashMonitor.get('name');
	var format = HashMonitor.get('format');
	var formatting = HashMonitor.get('formatting');
	if (namespace) {
		AutoRefresh.stop();
		var el = $('explorer-right');
		if (el) {
			if (namespace && kind && name) {
				var params = { namespace: namespace, kind: kind, name: name };
				if (!Object.isUndefined(format)) {
					params.format = format;
				}
				if (formatting) {
					params.formatting = 'true';
				}
				WSApi.getDBAsync('apiobject', params, function(txt) {
					replaceElementContent(el, txt);
					tw_contentChanged();
					Tabs.hashChanged(HashMonitor.values);
					startAutoRefresh();
				});
			}
		}
	}
	return false;
}

function restoreApiObject() {
	var namespace = HashMonitor.get('namespace');
	var kind = HashMonitor.get('kind');
	var name = HashMonitor.get('name');
	var format = HashMonitor.get('format');
	var formatting = HashMonitor.get('formatting');
	
	if (namespace && kind && name) {
		showApiObject(null, namespace, kind, name, format, formatting);
	}
}

function clearClusterCache() {
	WSApi.getDBAsync('cluster-reload', null, reloadPage);
	return false;
}

var lastSelection = '';

function saveSelection() {
	var txt = window.getSelection().toString();
	if (txt) {
		lastSelection = txt;
	}
}

function decodeBase64() {
	if (lastSelection) {
		AutoRefresh.stop();
		Menu.hide();
		Popup.hide();
		Dialog.show('Base64', '<pre>'+atob(lastSelection).escapeHTML()+'</pre>');
	}
	return false;
}

HashMonitor.addListener(restoreApiObject);
