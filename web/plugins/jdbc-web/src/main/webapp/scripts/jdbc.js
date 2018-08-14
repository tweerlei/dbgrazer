/*
 * Schema selection dialog
 */

function selectConnection(el) {
	var c = $F(el);
	if (c == '') {
		$('f1-catalog').disabled = true;
		$('f1-schema').disabled = true;
		$('f1-submit').disabled = false;
		return false;
	} else if (c == '.') {
		$('f1-catalog').disabled = true;
	} else {
		var txt = WSApi.getDBAsync('dbcatalogs', { c: c }, function(txt) {
			$('f1-catalog').innerHTML = txt;
			$('f1-catalog').disabled = false;
		});
	}
	$('f1-schema').disabled = true;
	$('f1-submit').disabled = true;
	return false;
}

function selectCatalog(el) {
	var cat = $F(el);
	if (cat == '.') {
		$('f1-schema').disabled = true;
	} else {
		var c = $F('f1-connection');
		if (c == '.') {
			$('f1-schema').disabled = true;
		} else {
			var txt = WSApi.getDBAsync('dbschemas', { c: c, catalog: cat }, function(txt) {
				$('f1-schema').innerHTML = txt;
				$('f1-schema').disabled = false;
			});
		}
	}
	$('f1-submit').disabled = true;
	return false;
}

function selectSchema(el) {
	var sch = $F(el);
	if (sch == '.') {
		$('f1-object').disabled = true;
	} else {
		var c = $F('f1-connection');
		var cat = $F('f1-catalog');
		if ((c == '.') || (cat == '.')) {
			$('f1-object').disabled = true;
		} else {
			var txt = WSApi.getDBAsync('dbobjects', { c: c, catalog: cat, schema: sch }, function(txt) {
				$('f1-object').innerHTML = txt;
				$('f1-object').disabled = false;
			});
		}
	}
	$('f1-submit').disabled = true;
	return false;
}

function selectObjComplete(el) {
	var s = $F(el);
	if (s == '.') {
		$('f1-submit').disabled = true;
	} else {
		$('f1-submit').disabled = false;
	}
	return false;
}

function showInsertDialog(ev, cat, sch, obj, b) {
	showDbDialog(ev, 'insert-simple', { q: obj, catalog: cat, schema: sch, backTo: b }, obj);
	return false;
}

function showUpdateDialog(ev, cat, sch, obj, ids, b) {
	var params = { q: obj, catalog: cat, schema: sch, backTo: b };
	for (var i = 0; i < ids.length; i++)
		params['ids['+i+']'] = ids[i];
	showDbDialog(ev, 'update-simple', params, obj);
	return false;
}

function showCopyDialog(ev, cat, sch, obj, ids, b) {
	var params = { q: obj, catalog: cat, schema: sch, backTo: b };
	for (var i = 0; i < ids.length; i++)
		params['ids['+i+']'] = ids[i];
	showDbDialog(ev, 'copy-simple', params, obj);
	return false;
}

function showDeleteDialog(ev, cat, sch, obj, ids, b) {
	var params = { q: obj, catalog: cat, schema: sch, backTo: b };
	for (var i = 0; i < ids.length; i++)
		params['ids['+i+']'] = ids[i];
	showDbDialog(ev, 'delete-simple', params, obj);
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

function toggleTableTreeItem(ev, label, catalog, schema, obj, dir, left, target) {
	var e = Tree.getItem(label, left);
	if (e && !Tree.collapseItem(e)) {
		WSApi.getDBAsync('dbtree', { catalog: catalog, schema: schema, object: obj, label: label, dir: dir, left: left, target: target }, function(txt) {
			Tree.expandItem(e, txt);
		});
	}
	return false;
}

function getDBObject(catalog, schema, obj, depth, all, sort, cb) {
	var params = {
		catalog: catalog,
		schema: schema,
		object: obj
	};
	
	if (depth) {
		params.depth = depth;
	}
	if (!Object.isUndefined(all)) {
		params.allSchemas = (all ? 'true' : 'false');
	}
	if (!Object.isUndefined(sort)) {
		params.sort = (sort ? 'true' : 'false');
	}
	return WSApi.getDBAsync('dbobject', params, cb);
}

function showDBObject(ev, catalog, schema, obj, depth, all, sort, where, order, target) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	Menu.hide();
	Popup.hide();
	var el = $('explorer-right');
	if (el) {
		getDBObject(catalog, schema, obj, depth, all, sort, function(txt) {
			el.innerHTML = extractLocalStyles(txt);
			tw_contentChanged();
			HashMonitor.set({ catalog: catalog, schema: schema, object: obj, depth: depth, all: all, sort: sort });
			Tabs.hashChanged(HashMonitor.values);
			if (where) {
				showDBObjectData(null, catalog, schema, obj, where, order, target);
			}
		});
	}
	return false;
}

function showDBObjectData(ev, catalog, schema, obj, where, order, target) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	Menu.hide();
	Popup.hide();
	var el = $('result');
	if (el) {
		WSApi.postDBAsync('submit-simple', { catalog: catalog, schema: schema, object: obj, where: where, order: order||'', target: target||'' }, function(txt) {
			el.innerHTML = extractLocalStyles(txt);
			tw_contentChanged();
			HashMonitor.set({ catalog: catalog, schema: schema, object: obj, where: where, order: order });
			var w = $('where');
			if (w) {
				w.innerHTML = where;
			}
			var o = $('order');
			if (o) {
				o.innerHTML = order||'';
			}
			Tabs.show('result', 6);
			startAutoRefresh();
		});
	}
	return false;
}

function refreshDBObject() {
	var catalog = HashMonitor.get('catalog');
	var schema = HashMonitor.get('schema');
	var obj = HashMonitor.get('object');
	var depth = HashMonitor.get('depth');
	var all = HashMonitor.get('all');
	var sort = HashMonitor.get('sort');
	if (obj) {
		AutoRefresh.stop();
		var el = $('explorer-right');
		if (el) {
			getDBObject(catalog, schema, obj, depth, all, sort, function(txt) {
				el.innerHTML = extractLocalStyles(txt);
				tw_contentChanged();
				Tabs.hashChanged(HashMonitor.values);
				startAutoRefresh();
			});
		}
	}
	return false;
}

function restoreDBObject() {
	var catalog = HashMonitor.get('catalog');
	var schema = HashMonitor.get('schema');
	var obj = HashMonitor.get('object');
	var depth = HashMonitor.getNumber('depth');
	var all = HashMonitor.getBoolean('all');
	var sort = HashMonitor.getBoolean('sort');
	var where = HashMonitor.get('where');
	var order = HashMonitor.get('order');
	
	if (obj) {
		showDBObject(null, catalog, schema, obj, depth, all, sort, where, order, 'explorer-right');
	}
}

HashMonitor.addListener(restoreDBObject);
