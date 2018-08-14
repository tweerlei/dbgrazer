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

function showMessage(ev, topic, partition, offset, format) {
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
		WSApi.getDBAsync('message', params, function(txt) {
			el.innerHTML = extractLocalStyles(txt);
			tw_contentChanged();
			HashMonitor.set(params);
			Tabs.hashChanged(HashMonitor.values);
		});
	}
	return false;
}
