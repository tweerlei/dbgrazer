function showPartition(ev, topic, partition, offset, key, value, compact) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	var el = $('tab-page-combo0');
	if (el) {
		var params = { topic: topic, partition: partition };
		if (!Object.isUndefined(offset)) {
			params.offset = offset;
		}
		if (!Object.isUndefined(key)) {
			params.key = key;
		}
		if (!Object.isUndefined(value)) {
			params.value = value;
		}
		if (compact) {
			params.compact = true;
		}
		WSApi.getDBAsync('messages', params, function(txt) {
			replaceElementContent(el, txt);
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
			replaceElementContent(el, txt);
			tw_contentChanged();
			HashMonitor.set(params);
			Tabs.hashChanged(HashMonitor.values);
		});
	}
	return false;
}

function showConsumerGroup(ev, group) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	var el = $('explorer-right');
	if (el) {
		var params = { group: group };
		WSApi.getDBAsync('consumergroup', params, function(txt) {
			replaceElementContent(el, txt);
			tw_contentChanged();
			HashMonitor.set(params);
			Tabs.hashChanged(HashMonitor.values);
		});
	}
	return false;
}

function refreshTopic() {
	var topic = HashMonitor.get('topic');
	var partition = HashMonitor.get('partition');
	var offset = HashMonitor.get('offset');
	var key = HashMonitor.get('key');
	var value = HashMonitor.get('value');
	var compact = HashMonitor.get('compact');
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
					replaceElementContent(el, txt);
					tw_contentChanged();
					Tabs.hashChanged(HashMonitor.values);
					startAutoRefresh();
				});
			} else if (topic) {
				var params = { topic: topic, partition: partition };
				if (!Object.isUndefined(offset)) {
					params.offset = offset;
				}
				if (!Object.isUndefined(key)) {
					params.key = key;
				}
				if (!Object.isUndefined(value)) {
					params.value = value;
				}
				if (compact) {
					params.compact = true;
				}
				WSApi.getDBAsync('messages', params, function(txt) {
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

function refreshConsumerGroup() {
	var group = HashMonitor.get('group');
	if (group) {
		AutoRefresh.stop();
		var el = $('explorer-right');
		if (el) {
			var params = { group: group };
			WSApi.getDBAsync('consumergroup', params, function(txt) {
				replaceElementContent(el, txt);
				tw_contentChanged();
				Tabs.hashChanged(HashMonitor.values);
				startAutoRefresh();
			});
		}
	}
	return false;
}

function restoreTopic() {
	var topic = HashMonitor.get('topic');
	var partition = HashMonitor.get('partition');
	var offset = HashMonitor.get('offset');
	var key = HashMonitor.get('key');
	var value = HashMonitor.get('value');
	var compact = HashMonitor.get('compact');
	var format = HashMonitor.get('format');
	var formatting = HashMonitor.get('formatting');
	var group = HashMonitor.get('group');
	
	if (topic && partition && offset) {
		showMessage(null, topic, partition, offset, format, formatting);
	} else if (topic) {
		showPartition(null, topic, partition, offset, key, value, compact);
	} else if (group) {
		showConsumerGroup(null, group);
	}
}

function clearKafkaCache() {
	WSApi.getDBAsync('kafka-reload', null, reloadPage);
	return false;
}

HashMonitor.addListener(restoreTopic);
