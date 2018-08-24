function showPartition(ev, topic, partition, offset) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	var el = $('explorer-right');
	if (el) {
		var params = { topic: topic, partition: partition };
		if (!Object.isUndefined(offset)) {
			params.offset = offset;
		}
		WSApi.getDBAsync('partition', params, function(txt) {
			el.innerHTML = extractLocalStyles(txt);
			tw_contentChanged();
			HashMonitor.set(params);
			Tabs.hashChanged(HashMonitor.values);
		});
	}
	return false;
}

function showMessage(ev, topic, partition, offset, format, formatting) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	var el = $('explorer-right');
	if (el) {
		var params = { topic: topic, partition: partition, offset: offset };
		if (!Object.isUndefined(format)) {
			params.format = format;
		}
		if (formatting) {
			params.formatting = 'true';
		}
		WSApi.getDBAsync('message', params, function(txt) {
			el.innerHTML = extractLocalStyles(txt);
			tw_contentChanged();
			HashMonitor.set(params);
			Tabs.hashChanged(HashMonitor.values);
		});
	}
	return false;
}

function submitDML(frm) {
	Forms.submitAsync(frm);
	return false;
}

function submitDMLError(txt) {
	var fld = $('dmlerror');
	if (fld) {
		if (txt) {
			fld.innerHTML = '<p class="error">' + txt + '</p>';
		} else {
			fld.innerHTML = '';
		}
	}
}

function submitDMLSuccess(txt) {
	var fld = $('submitresult');
	if (fld) {
		if (txt) {
			fld.innerHTML = '<p class="notice">' + txt + '</p>';
		} else {
			fld.innerHTML = '';
		}
	}
	closeDialog();
	var frm = $('submitform');
	if (frm) {
		submitForm(frm, 'table');
	} else {
		reloadPage();
	}
}

function refreshTopic() {
	var topic = HashMonitor.get('topic');
	var partition = HashMonitor.get('partition');
	var offset = HashMonitor.get('offset');
	var format = HashMonitor.get('format');
	var formatting = HashMonitor.get('formatting');
	if (topic) {
		AutoRefresh.stop();
		var el = $('explorer-right');
		if (el) {
			if (topic && partition && offset) {
				var params = { topic: topic, partition: partition, offset: offset };
				if (!Object.isUndefined(format)) {
					params.format = format;
				}
				if (formatting) {
					params.formatting = 'true';
				}
				WSApi.getDBAsync('message', params, function(txt) {
					el.innerHTML = extractLocalStyles(txt);
					tw_contentChanged();
					Tabs.hashChanged(HashMonitor.values);
					startAutoRefresh();
				});
			} else if (topic && partition) {
				var params = { topic: topic, partition: partition };
				WSApi.getDBAsync('partition', params, function(txt) {
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

function restoreTopic() {
	var topic = HashMonitor.get('topic');
	var partition = HashMonitor.get('partition');
	var offset = HashMonitor.get('offset');
	var format = HashMonitor.get('format');
	var formatting = HashMonitor.get('formatting');
	
	if (topic && partition && offset) {
		showMessage(null, topic, partition, offset, format, formatting);
	} else if (topic && partition) {
		showPartition(null, topic, partition, null);
	}
}

HashMonitor.addListener(restoreTopic);
