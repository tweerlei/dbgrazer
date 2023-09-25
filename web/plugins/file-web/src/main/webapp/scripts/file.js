function toggleDirTreeItem(ev, label, path, left, target) {
	var e = Tree.getItem(label, left);
	if (e && !Tree.collapseItem(e)) {
		WSApi.getDBAsync('dirtree', { path: path, label: label, left: left, target: target }, function(txt) {
			Tree.expandItem(e, txt);
			HashMonitor.set({ node: label+'-'+left }, true);
		});
	}
	return false;
}

function submitDirSuccess(txt, left) {
	var fld = $('submitresult');
	if (fld) {
		if (txt) {
			fld.innerHTML = '<p class="notice">' + txt + '</p>';
		} else {
			fld.innerHTML = '';
		}
	}
	closeDialog();
	var e = Tree.getItem('combo0', left);
	if (e) {
		Tree.collapseItem(e);
		Tree.toggleItem(e);
	}
}

function showDir(ev, path) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	var el = $('explorer-right');
	if (el) {
		WSApi.getDBAsync('dir', { path: path }, function(txt) {
			replaceElementContent(el, txt);
			tw_contentChanged();
			HashMonitor.set({ path: path });
			Tabs.hashChanged(HashMonitor.values);
		});
	}
	return false;
}

function refreshDir() {
	var path = HashMonitor.get('path');
	if (path) {
		AutoRefresh.stop();
		var el = $('explorer-right');
		if (el) {
			WSApi.getDBAsync('dir', { path: path }, function(txt) {
				replaceElementContent(el, txt);
				tw_contentChanged();
				Tabs.hashChanged(HashMonitor.values);
				startAutoRefresh();
			});
		}
	}
	return false;
}

function restoreDir() {
	var path = HashMonitor.get('path');
	
	if (path) {
		showDir(null, path);
	}
}

HashMonitor.addListener(restoreDir);
