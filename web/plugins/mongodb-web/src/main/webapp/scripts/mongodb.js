function showCollection(ev, database, collection, id, value, view) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	var el = $('tab-page-combo0');
	if (el) {
		var params = { database: database, collection: collection };
		if (!Object.isUndefined(id)) {
			params.id = id;
		}
		if (!Object.isUndefined(value)) {
			params.value = value;
		}
		if (!Object.isUndefined(view)) {
			params.view = view;
		}
		WSApi.getDBAsync('documents', params, function(txt) {
			el.innerHTML = extractLocalStyles(txt);
			tw_contentChanged();
			HashMonitor.set(params);
			Tabs.hashChanged(HashMonitor.values);
		});
	}
	return false;
}

function showDocument(ev, database, collection, id, formatting, coloring, lineno, struct) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	var el = $('explorer-right');
	if (el) {
		var params = { database: database, collection: collection, id: id };
		if (formatting) {
			params.formatting = formatting;
		}
		if (coloring) {
			params.coloring = coloring;
		}
		if (lineno) {
			params.lineno = lineno;
		}
		if (struct) {
			params.struct = struct;
		}
		WSApi.getDBAsync('document', params, function(txt) {
			el.innerHTML = extractLocalStyles(txt);
			tw_contentChanged();
			HashMonitor.set(params);
			Tabs.hashChanged(HashMonitor.values);
		});
	}
	return false;
}

function refreshDatabase() {
	var database = HashMonitor.get('database');
	var collection = HashMonitor.get('collection');
	var id = HashMonitor.get('id');
	var format = HashMonitor.get('format');
	var formatting = HashMonitor.get('formatting');
	if (database) {
		AutoRefresh.stop();
		var el = $('explorer-right');
		if (el) {
			if (database && collection && id) {
				var params = { database: database, collection: collection, id: id };
				if (!Object.isUndefined(format)) {
					params.format = format;
				}
				if (formatting) {
					params.formatting = 'true';
				}
				WSApi.getDBAsync('document', params, function(txt) {
					el.innerHTML = extractLocalStyles(txt);
					tw_contentChanged();
					Tabs.hashChanged(HashMonitor.values);
					startAutoRefresh();
				});
			} else if (database) {
				var params = { database: database, collection: collection };
				if (!Object.isUndefined(id)) {
					params.id = id;
				}
				WSApi.getDBAsync('documents', params, function(txt) {
					el.innerHTML = extractLocalStyles(txt);
					tw_contentChanged();
					Tabs.hashChanged(HashMonitor.values);
					startAutoRefresh();
				});
			}
		}
	}
	return false;
}

function restoreDatabase() {
	var database = HashMonitor.get('database');
	var collection = HashMonitor.get('collection');
	var id = HashMonitor.get('id');
	var formatting = HashMonitor.get('formatting');
	var coloring = HashMonitor.get('coloring');
	var lineno = HashMonitor.get('lineno');
	var struct = HashMonitor.get('struct');
	
	if (database && collection && id) {
		showDocument(null, database, collection, id, coloring, lineno, struct);
	} else if (database) {
		showCollection(null, database, collection, id);
	}
}

HashMonitor.addListener(restoreDatabase);
