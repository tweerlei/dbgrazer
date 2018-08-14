function editQuery(name, src) {
	if (WSApi.currentDB) {
		if (src) {
			gotoPage('db/'+WSApi.currentDB+'/edit.html?q=' + name + "&backTo=" + src);
		} else {
			gotoPage('db/'+WSApi.currentDB+'/edit.html?q=' + name);
		}
	}
	return false;
}

function gotoLineTarget(ev, prefix, index, src) {
	var from = $(prefix + index);
	var q = $F(from);
	if (q) {
		return editQuery(q, src);
	}
	return false;
}

function switchResultType(e) {
	var s = $(e);
	var o = s.options[s.selectedIndex];
	var v = o.value;
	if (o.hasClassName('view')) {
		$$('.view-only').each(Element.show);
		$$('.query-only').each(Element.hide);
	} else {
		$$('.view-only').each(Element.hide);
		$$('.query-only').each(Element.show);
	}
	$$('.query-bytype').each(function(el) {
		el.toggle(el.hasClassName('query-'+v));
	});
	return true;
}

function addMatchingViews() {
	var params = [];
	for (var i = 0; ; i++) {
		var e = $('params'+i+'.name');
		if (!e) {
			break;
		}
		if (e.value) {
			params[params.length] = e.value;
		}
	}
	
	var p = {};
	for (var i = 0; i < params.length; i++) {
		p['params['+i+']'] = params[i];
	}
	
	WSApi.getDBAsync('match-views', p, function(arr) {
		var views = [];
		for (var i = 0; ; i++) {
			var e = $('views'+i);
			if (e) {
				views[views.length] = e;
			} else {
				break;
			}
		}
		
		var matching = [];
		var found;
		for (var i = 0; i < arr.length; i++) {
			found = false;
			for (var j = 0; j < views.length; j++) {
				if (arr[i] == views[j].value) {
					found = true;
					break;
				}
			}
			if (!found) {
				for (var j = 0; j < views.length; j++) {
					if (!views[j].value) {
						setSelectedValue(views[j].id, arr[i], arr[i]);
						$('views-'+j).show();
						break;
					}
				}
			}
		}
	});
	
	return false;
}
