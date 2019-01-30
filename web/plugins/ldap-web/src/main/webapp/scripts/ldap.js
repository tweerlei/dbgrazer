function toggleLdapTreeItem(ev, label, path, left, target) {
	var e = Tree.getItem(label, left);
	if (e && !Tree.collapseItem(e)) {
		WSApi.getDBAsync('ldaptree', { path: path, label: label, left: left, target: target }, function(txt) {
			Tree.expandItem(e, txt);
			HashMonitor.set({ node: label+'-'+left }, true);
		});
	}
	return false;
}

function showEntry(ev, path) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	var el = $('explorer-right');
	if (el) {
		WSApi.getDBAsync('entry', { path: path }, function(txt) {
			el.innerHTML = extractLocalStyles(txt);
			tw_contentChanged();
			HashMonitor.set({ path: path });
			Tabs.hashChanged(HashMonitor.values);
		});
	}
	return false;
}

function refreshEntry() {
	var path = HashMonitor.get('path');
	if (path) {
		AutoRefresh.stop();
		var el = $('explorer-right');
		if (el) {
			WSApi.getDBAsync('entry', { path: path }, function(txt) {
				el.innerHTML = extractLocalStyles(txt);
				tw_contentChanged();
				Tabs.hashChanged(HashMonitor.values);
				startAutoRefresh();
			});
		}
	}
	return false;
}

function restoreEntry() {
	var path = HashMonitor.get('path');
	
	if (path) {
		showEntry(null, path);
	}
}

HashMonitor.addListener(restoreEntry);
