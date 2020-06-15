function showInsertDialog(ev, cat, sch, obj, b) {
	showDbDialog(ev, 'dml-insert', { q: obj, catalog: cat, schema: sch, backTo: b }, obj);
	return false;
}

function showUpdateDialog(ev, cat, sch, obj, ids, b) {
	var params = { q: obj, catalog: cat, schema: sch, backTo: b };
	for (var i = 0; i < ids.length; i++)
		params['ids['+i+']'] = ids[i];
	showDbDialog(ev, 'dml-update', params, obj);
	return false;
}

function showCopyDialog(ev, cat, sch, obj, ids, b) {
	var params = { q: obj, catalog: cat, schema: sch, backTo: b };
	for (var i = 0; i < ids.length; i++)
		params['ids['+i+']'] = ids[i];
	showDbDialog(ev, 'dml-copy', params, obj);
	return false;
}

function showDeleteDialog(ev, cat, sch, obj, ids, b) {
	var params = { q: obj, catalog: cat, schema: sch, backTo: b };
	for (var i = 0; i < ids.length; i++)
		params['ids['+i+']'] = ids[i];
	showDbDialog(ev, 'dml-delete', params, obj);
	return false;
}

function showAlterColumnDialog(ev, cat, sch, obj, col) {
	showDbDialog(ev, 'ddl-alter-column', { q: obj, catalog: cat, schema: sch, name: col }, col);
	return false;
}

function showDropColumnDialog(ev, cat, sch, obj, col) {
	showDbDialog(ev, 'ddl-drop-column', { q: obj, catalog: cat, schema: sch, name: col }, col);
	return false;
}

function showAddColumnDialog(ev, cat, sch, obj) {
	showDbDialog(ev, 'ddl-add-column', { q: obj, catalog: cat, schema: sch }, obj);
	return false;
}

function showDropIndexDialog(ev, cat, sch, obj, ix) {
	showDbDialog(ev, 'ddl-drop-index', { q: obj, catalog: cat, schema: sch, name: ix }, ix);
	return false;
}

function showAddIndexDialog(ev, cat, sch, obj) {
	showDbDialog(ev, 'ddl-add-index', { q: obj, catalog: cat, schema: sch }, obj);
	return false;
}

function showDropPrimaryKeyDialog(ev, cat, sch, obj, ix) {
	showDbDialog(ev, 'ddl-drop-primarykey', { q: obj, catalog: cat, schema: sch, name: ix }, ix);
	return false;
}

function showAddPrimaryKeyDialog(ev, cat, sch, obj) {
	showDbDialog(ev, 'ddl-add-primarykey', { q: obj, catalog: cat, schema: sch }, obj);
	return false;
}

function showDropForeignKeyDialog(ev, cat, sch, obj, fk) {
	showDbDialog(ev, 'ddl-drop-foreignkey', { q: obj, catalog: cat, schema: sch, name: fk }, fk);
	return false;
}

function showAddForeignKeyDialog(ev, cat, sch, obj) {
	showDbDialog(ev, 'ddl-add-foreignkey', { q: obj, catalog: cat, schema: sch }, obj);
	return false;
}

function showRevokeDialog(ev, cat, sch, obj, priv, gr) {
	showDbDialog(ev, 'ddl-revoke', { q: obj, catalog: cat, schema: sch, privilege: priv, grantee: gr }, priv);
	return false;
}

function showGrantDialog(ev, cat, sch, obj) {
	showDbDialog(ev, 'ddl-grant', { q: obj, catalog: cat, schema: sch }, obj);
	return false;
}

function showAlterTableDialog(ev, cat, sch, obj) {
	showDbDialog(ev, 'ddl-alter-table', { q: obj, catalog: cat, schema: sch }, obj);
	return false;
}

function showDropTableDialog(ev, cat, sch, obj) {
	showDbDialog(ev, 'ddl-drop-table', { q: obj, catalog: cat, schema: sch }, obj);
	return false;
}

function showCreateTableDialog(ev, cat, sch) {
	showDbDialog(ev, 'ddl-create-table', { catalog: cat, schema: sch }, sch);
	return false;
}
