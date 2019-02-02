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
