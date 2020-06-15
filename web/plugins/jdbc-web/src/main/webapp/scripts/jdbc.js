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

function selectObject(el) {
	var obj = $F(el);
	if (obj == '.') {
		//$('f1-object').disabled = true;
	} else {
		var c = $F('f1-connection');
		var cat = $F('f1-catalog');
		var sch = $F('f1-schema');
		if ((c == '.') || (cat == '.') || (sch == '.')) {
			//$('f1-object').disabled = true;
		} else {
			var txt = WSApi.getDBAsync('dbcolumns', { c: c, catalog: cat, schema: sch, object: obj }, function(txt) {
				$$('select.dbcolumn').each(function(e)
					{
					e.innerHTML = txt;
					});
			});
		}
	}
	return selectObjComplete(el);
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

function clearDbCache() {
	WSApi.getDBAsync('metadata-reset', null, reloadPage);
	return false;
}

function setOrder(f, v) {
	var order = $('order');
	if (order) {
		var re = new RegExp('(^|,) *' + f + '( [^,]*)?(?=,|$)');
		var cl = v ? (f + ' ' + v) : '';
		if (order.value.match(re)) {
			order.value = order.value.replace(re, cl).replace(/^ *, */, '').replace(/ *, *$/, '');
		} else if (order.value) {
			order.value = order.value + ', ' + cl;
		} else {
			order.value = cl;
		}
		submitForm($('submitform'), 'table');
	}
}

function addOrderAsc(ev) {
	var cell = Popup.parent;
	setOrder(cell.innerText, 'ASC');
}

function addOrderDesc(ev) {
	var cell = Popup.parent;
	setOrder(cell.innerText, 'DESC');
}

function removeOrder(ev) {
	var cell = Popup.parent;
	setOrder(cell.innerText);
}

function setWhere(f, v) {
	var where = $('where');
	if (where) {
		var re = new RegExp('(^| and | or ) *' + f + ' .+?(?= and| or|$)', 'i');
		var cl = v ? (f + ' ' + v) : '';
		if (where.value.match(re)) {
			where.value = where.value.replace(re, cl).replace(/^ *(and|or) +/i, '').replace(/ +(and|or) *$/i, '');
		} else if (where.value) {
			where.value = where.value + ' AND ' + cl;
		} else {
			where.value = cl;
		}
		submitForm($('submitform'), 'table');
	}
}

function addWhere(ev) {
	var cell = Popup.parent;
	var n = cell.previousSiblings().length;
	var hdr = cell.up('table').down('tr').childElements()[n];
	if (cell.innerText === '\u2205')
		setWhere(hdr.innerText, 'IS NULL');
	else
		setWhere(hdr.innerText, '= \'' + cell.innerText.replace(/'/g, "''") + '\'');
}

function addWhereNot(ev) {
	var cell = Popup.parent;
	var n = cell.previousSiblings().length;
	var hdr = cell.up('table').down('tr').childElements()[n];
	if (cell.innerText === '\u2205')
		setWhere(hdr.innerText, 'IS NOT NULL');
	else
		setWhere(hdr.innerText, '<> \'' + cell.innerText.replace(/'/g, "''") + '\'');
}

function removeWhere(ev) {
	var cell = Popup.parent;
	var n = cell.previousSiblings().length;
	var hdr = cell.up('table').down('tr').childElements()[n];
	setWhere(hdr.innerText);
}

HashMonitor.addListener(restoreDBObject);
