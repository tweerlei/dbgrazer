var Menu = new PopupWindow('menu', true);
var Popup = new PopupWindow('popup', false);

/*
 * Table functions (extending table.js)
 */

Table.classNameRegExp = /\s+/;

Table.hasClassName = function(e, c) {
	var cn = e.className;
	if (!cn) {
		return false;
	} else if (cn == c) {
		return true;
	} else {
		var ca = cn.split(this.classNameRegExp);
		var ci = ca.indexOf(c);
		return (ci >= 0);
	}
};

Table.addClassName = function(e, c) {
	var cn = e.className;
	if (!cn) {
		e.className = c;
	} else if (cn != c) {
		var ca = cn.split(this.classNameRegExp);
		var ci = ca.indexOf(c);
		if (ci < 0) {
			ca.push(c);
			e.className = ca.join(' ');
		}
	}
};

Table.removeClassName = function(e, c) {
	var cn = e.className;
	if (cn == c) {
		e.className = '';
	} else if (cn) {
		var ca = cn.split(this.classNameRegExp);
		var ci = ca.indexOf(c);
		if (ci >= 0) {
			ca.splice(ci, 1);
			e.className = ca.join(' ');
		}
	}
};

Table.highlightRow = function(c) { 
	var r = c.parentNode;
	var ix;
	var coff = 0;
	for (ix = 0; ix < r.cells.length; ix++) {
		if (r.cells[ix] == c) {
			ix = coff;
			break;
		}
		coff += r.cells[ix].colSpan;
	}
	
	var t = Table.resolve(r);
	
	var bodies = t.tBodies;
	if (bodies==null || bodies.length==0) { 
		return; 
	}
	
	for (var i=0,L=bodies.length; i<L; i++) {
		var tbrows = bodies[i].rows;
		for (var cRowIndex=0,RL=tbrows.length; cRowIndex<RL; cRowIndex++) {
			var cRow=tbrows[cRowIndex];
			var rcells = cRow.cells;
			coff = 0;
			for (var cColIndex=0,CL=rcells.length; cColIndex<CL; cColIndex++) {
				var rcell = rcells[cColIndex];
				if (cRow == r) {
					this.removeClassName(rcell, 'crosshair');
					this.addClassName(rcell, 'highlight');
				} else if (coff == ix) {
					this.removeClassName(rcell, 'highlight');
					this.addClassName(rcell, 'crosshair');
				} else {
					this.removeClassName(rcell, 'highlight');
					this.removeClassName(rcell, 'crosshair');
				}
				coff += rcell.colSpan;
			}
		}
	}
};

Table.highlightColumn = function(c) {
	var r = c.parentNode;
	var ix;
	for (ix = 0; ix < r.cells.length; ix++) {
		if (r.cells[ix] == c) {
			break;
		}
	}
	if (ix == 0) {
		// Don't highlight heading column
		return;
	}
	
	var t = Table.resolve(r);
	
	var bodies = t.tBodies;
	if (bodies==null || bodies.length==0) { 
		return; 
	}
	
	for (var i=0,L=bodies.length; i<L; i++) {
		var tbrows = bodies[i].rows;
		for (var cRowIndex=0,RL=tbrows.length; cRowIndex<RL; cRowIndex++) {
			var cRow=tbrows[cRowIndex];
			var rcells = cRow.cells;
			for (var cColIndex=1,CL=rcells.length; cColIndex<CL; cColIndex++) {
				var rcell = rcells[cColIndex];
				if (cColIndex == ix) {
					this.removeClassName(rcell, 'crosshair');
					this.addClassName(rcell, 'highlight');
				} else if (cRow == r) {
					this.removeClassName(rcell, 'highlight');
					this.addClassName(rcell, 'crosshair');
				} else {
					this.removeClassName(rcell, 'highlight');
					this.removeClassName(rcell, 'crosshair');
				}
			}
		}
	}
};

/*
 * SELECT list filter
 */

var selectFilter = new Timer(function() {
	var s = arguments[0];
	var r = arguments[1];
	for (var i = 2; i < arguments.length; i++) {
		Forms.filter(arguments[i], s, r);
	}
});

var searchTimer = new Timer(function() {
	var s = arguments[0];
	var p = $('search-result');
	if ((s.length >= 3) && p) {
		WSApi.getSearchResult(s, function(txt) {
			p.innerHTML = txt;
		});
	}
});

var newSearchTimer = new Timer(function() {
	var s = arguments[0];
	if (s.length >= 3) {
		showDbDialog(null, 'search', { q: s }, Messages.searchTitle);
	}
});

var waitTimer = new Timer(function(n) {
	var el = $('waitTime');
	if (el) {
		el.innerHTML = formatTime(n);
		if (n % 5 == 1) {
			var p = $('waitProgress');
			if (p) {
				WSApi.getProgress(function(txt) {
					p.innerHTML = txt;
				});
			}
		}
		this.start(1000, n + 1);
	}
});

var ajaxTimer = new Timer(function() {
	showCancelDialog(Messages.waitTitle, Messages.waitText);
});

/*
 * Form functions
 */

function toggleScaling(e) {
	var el = $(e);
	if (el) {
		if (el.hasClassName('scaled')) {
			el.removeClassName('scaled');
			el.addClassName('unscaled');
		} else if (el.hasClassName('unscaled')) {
			el.removeClassName('unscaled');
			el.addClassName('scaled');
		}
		if (el.resizer) {
			el.resizer.resize();
		}
	}
	return true;
}

function extractLocalStyles(txt) {
	// Hack: extract custom CSS rules from response
	var start = txt.indexOf('<style>');
	var end = txt.indexOf('</style>');
	if ((start >= 0) && (end > start)) {
		var css = txt.substring(start + 7, end);
		$('local-styles').innerHTML = css;
		txt = txt.substring(0, start) + txt.substring(end + 8);
	}
	return txt;
}

function setFormFields(fields) {
	if (fields) {
		for (var i in fields) {
			var e = $(i);
			if (e) {
				e.value = fields[i];
			}
		}
	}
}

function getFormInto(f, target, fields, cb, prog) {
	var frm = $(f);
	var el = $(target);
	if (frm && el) {
//		el.hide();
//		el.innerHTML = '';
		if (prog) {
			showProgressDialog(Messages.submitFormTitle, Messages.submitFormText);
		} else {
			showCancelDialog(Messages.submitFormTitle, Messages.submitFormText);
		}
		
		setFormFields(fields);
		Forms.submitAsync(frm, null, function(txt) {
			el.innerHTML = extractLocalStyles(txt);
//			el.show();
		}, function() {
			Dialog.hide();
			if (cb) {
				cb(el);
			}
			tw_contentChanged();
		});
	}
	return false;
}

function postForm(f, ev, url, fields, target) {
	var frm = $(f);
	if (frm) {
		setFormFields(fields);
		Forms.submit(frm, ev, url, target, true);	// suppress onsubmit
	}
	return false;
}

function submitFormWithout(f, ev, url, id, target) {
	var frm = $(f);
	if (frm) {
		var el = $(id);
		if (el) {
			el.parentNode.removeChild(el);
		}
		if (target) {
			el = frm.elements['q'];
			var query = el.value;
			el.parentNode.removeChild(el);
			var params = '&'+frm.serialize(false);
			return runQuery(ev, query, params, target);
		} else {
			Forms.submit(frm, ev, url, target, true);	// suppress onsubmit
		}
	}
	return false;
}

function submitForm(f) {
	showCancelDialog(Messages.submitFormTitle, Messages.submitFormText);
	return true;
}

function cancelForm() {
	var res = $$('input[type=reset]');
	if (res.length == 1) {
		Forms.reset(res[0].form);	// execute onreset
	}
	return false;
}

function cancelWait() {
//	Scheduler.cancel();
	waitTimer.stop();
	Forms.cancelAsync();
	WSApi.cancelAsync();
}

function cancelSubmit() {
	if (window.stop) {
		window.stop();
	}
}

function downloadElement(e, type) {
	var el = $(e);
	if (el) {
		var uri = 'data:'+type+','+encodeURIComponent(el.textContent);
		window.open(uri);
//		Elements.selectText(el);
	}
	return false;
}

function setFieldContent(f, e) {
	var field = $(f);
	var el = $(e);
	
	if (field && el) {
		field.value = el.textContent;
		return true;
	}
	
	return false;
}

function formatTime(seconds) {
	var h = Math.floor(seconds / 3600);
	var m = Math.floor((seconds % 3600) / 60);
	var s = seconds % 60;
	
	if (h > 0) {
		return h + ':' + (m < 10 ? '0' : '') + m + ':' + (s < 10 ? '0' : '') + s;
	} else if (m > 0) {
		return m + ':' + (s < 10 ? '0' : '') + s;
	} else {
		return s + ' sec';
	}
}

/*
 * Global callbacks
 */

function tw_windowOnResize() {
	var h = Elements.getWindowHeight();
	
	var f = $('main');
	// hack: there is no way to measure the margin height
	// but the x-position is determined by the margin width which is the same size.
	var ym = Elements.getX(f);
	
	f = $('footer');
	var yf = Elements.getHeight(f);
	
	$$('.tab-body').each(function(e) {
		var y = Elements.getY(e);
		// subtract margin 3 times: 1x margin between content and footer, 2x content padding
		Elements.resizeTo(e, undefined, h - y - 3 * ym - yf);
	});
	
	f = $('fullscreen');
	if (f && f.visible()) {
		f.hide();
		var tab = f.parentNode;
		var w = Elements.getWidth(tab);
		var h = Elements.getHeight(tab);
		Elements.resizeTo(f, w, h);
		f.show();
	}
}

function tw_contentChanged() {
	// Make tables in new content sortable
	Table.auto();
	$$('table.multiple tbody, table.aggregate tbody').each(function(e) {
		e.onclick = function(ev) {
			var el = Event.element(ev);
			if (el) {
				var r = (el.tagName == 'TD') ? el : el.up('td');
				Table.highlightRow(r);
			}
			return true;
		};
	});
	$$('table.transposed tbody').each(function(e) {
		e.onclick = function(ev) {
			var el = Event.element(ev);
			if (el) {
				var r = (el.tagName == 'TD') ? el : el.up('td');
				Table.highlightColumn(r);
			}
			return true;
		};
	});
	$$('iframe.result').each(function(e) {
		if (!e.contentDocument.body.firstChild) {
			var el = $('text'+e.id.substr(4));
			if (el) {
				var txt = el.innerText;
				e.contentDocument.open();
				e.contentDocument.write(txt);
				e.contentDocument.close();
			}
		}
	});
	$$('.wiki a').each(function(e) {
		var x = e.getAttribute('href');	// .href always returns the absolute URL
		if ((x.indexOf(':') < 0) && (x.indexOf('/') < 0) && (x.indexOf('?') < 0)) {
			e.href = 'db/' + WSApi.currentDB + '/result.html?q=' + x;
		} else if (x.startsWith('./')) {
			e.href = 'db/' + WSApi.currentDB + '/' + x.substr(2);
		} else if (x.startsWith('../')) {
			e.href = 'db/' + x.substr(3);
		}
	});
	$$('.wiki img').each(function(e) {
		var x = e.getAttribute('src');	// .src always returns the absolute URL
		if ((x.indexOf(':') < 0) && (x.indexOf('/') < 0) && (x.indexOf('?') < 0)) {
			e.src = 'db/' + WSApi.currentDB + '/graph-image.html?q=' + x;
		}
	});
	$$('td.zoomable').each(function(e) {
		e.onclick = function() { zoomContent(e); };
	});
	// Establish keyboard handlers
	Forms.init();
	// Resize new content
	tw_windowOnResize();
}

function imageLoadingFailed(ev) {
	var img = Event.element(ev);
	if (img) {
		var p = document.createElement('p');
		p.innerHTML = '<strong>' + Messages.imageFailed + '</strong>';
		img.parentNode.insertBefore(p, img); 
		img.hide();
	}
}

function imageLoaded(ev) {
	var img = Event.element(ev);
	if (img) {
		var r = new ImageMap(img);
		img.resizer = r;
		r.resize();
	}
}

/*
 * Menu functions
 */

function getMenuParentNode(ev) {
	var el = Event.element(ev);
	if (el.tagName == 'A') {
		return (el.parentNode);
	}
	return (el);
}

function showTopMenu(ev, page, params) {
	Event.stop(ev);
	AutoRefresh.stop();
	var div = getMenuParentNode(ev);
	WSApi.getAsync(page, params, function(txt) {
		Menu.showLeft(div, txt);
	});
	return false;
}

function showTopMenuRight(ev, page, params) {
	Event.stop(ev);
	AutoRefresh.stop();
	var div = getMenuParentNode(ev);
	WSApi.getAsync(page, params, function(txt) {
		Menu.showRight(div, txt);
	});
	return false;
}

function showDbMenu(ev, page, params) {
	Event.stop(ev);
	AutoRefresh.stop();
	var div = getMenuParentNode(ev);
	WSApi.getDBAsync(page, params, function(txt) {
		Menu.showLeft(div, txt);
	});
	return false;
}

function showMenu(ev, page, params) {
	if (page.substr(0, 3) == 'db:') {
		return showDbMenu(ev, page.substr(3), params);
	} else {
		return showTopMenu(ev, page, params);
	}
}

function showElementMenu(ev, el) {
	Event.stop(ev);
	AutoRefresh.stop();
	var div = getMenuParentNode(ev);
	var txt = $(el).innerHTML;
	if (txt) {
		Menu.showLeft(div, txt);
	}
	return false;
}

function chooseTarget(ev, param, args, target) {
	Event.stop(ev);
	AutoRefresh.stop();
	var div = getMenuParentNode(ev);
	WSApi.getTargets(param, args, target, function(txt) {
		Menu.showLeft(div, txt);
	});
	return false;
}

/*
 * Tab functions
 */

function showTab(n, i) {
	Tabs.show(n, i);
	return false;
}

/*
 * Dialog functions
 */

function closeDialog() {
	Dialog.close();
	return false;
}

function submitDialog(frm) {
	if (frm.target) {
		Dialog.hide.bind(Dialog).defer();
	} else {
		Dialog.setWaiting(true);
	}
	return true;
}

function showWaitDialog() {
	AutoRefresh.stop();
	Menu.hide();
	Popup.hide();
	Dialog.show(Messages.waitTitle, '');
	return true;
}

function showCancelDialog(title, content) {
	AutoRefresh.stop();
	Menu.hide();
	Popup.hide();
	Dialog.show(title, '<form id="f1" action="#" method="get" onsubmit="return closeDialog();" onreset="return closeDialog();">'
		+'<p class="wait">' + content + ' <span id="waitTime">' + formatTime(0) + '</span></p>'
		+'<dl><dt>&nbsp;</dt><dd><input id="f1-reset" type="reset" value="' + Messages.cancel + '"/></dd></dl><hr/>'
		+'</form>', cancelWait, false);
	waitTimer.start(1000, 1);
	return true;
}

function showProgressDialog(title, content) {
	AutoRefresh.stop();
	Menu.hide();
	Popup.hide();
	Dialog.show(title, '<form id="f1" action="#" method="get" onsubmit="return cancelTasks();" onreset="return cancelTasks();">'
		+'<p class="wait">' + content + ' <span id="waitTime">' + formatTime(0) + '</span><span id="waitProgress"></span></p>'
		+'<dl><dt>&nbsp;</dt><dd><input id="f1-reset" type="reset" value="' + Messages.cancel + '"/></dd></dl><hr/>'
		+'</form>', cancelWait, true);
	waitTimer.start(1000, 1);
	return true;
}

function showErrorDialog(title, content) {
	AutoRefresh.stop();
	Menu.hide();
	Popup.hide();
	Dialog.close();
	Dialog.hide();
	Dialog.show(title, '<form id="f1" action="#" method="get" onsubmit="return closeDialog();" onreset="return closeDialog();">'
		+'<p class="warn">' + content + '</p>'
		+'<dl><dt>&nbsp;</dt><dd><input id="f1-reset" type="reset" value="' + Messages.cancel + '"/></dd></dl><hr/>'
		+'</form>');
	return true;
}

function showConfirmDialog(title, content, action, params) {
	AutoRefresh.stop();
	Menu.hide();
	Popup.hide();
	Dialog.show(title, '<form id="f1" action="' + action + '" method="get" onreset="return closeDialog();">'
		+(params ? '<input type="hidden" name="q" value="'+params+'"/>' : '')
		+'<p>' + content + '</p>'
		+'<dl><dt>&nbsp;</dt><dd><input id="f1-submit" type="submit" value="' + Messages.yes + '"/> <input id="f1-reset" type="reset" value="' + Messages.no + '"/></dd></dl><hr/>'
		+'</form>');
	return false;
}

function showJSConfirmDialog(title, content, cb) {
	AutoRefresh.stop();
	Menu.hide();
	Popup.hide();
	Dialog.show(title, '<form id="f1" action="#" method="get" onreset="return closeDialog();">'
		+'<p>' + content + '</p>'
		+'<dl><dt>&nbsp;</dt><dd><input id="f1-submit" type="submit" value="' + Messages.yes + '"/> <input id="f1-reset" type="reset" value="' + Messages.no + '"/></dd></dl><hr/>'
		+'</form>');
	$('f1').onsubmit = cb;
	return false;
}

function showElementDialog(ev, title, id) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	Menu.hide();
	Popup.hide();
	var el = $(id);
	if (el) {
		var txt = el.innerHTML;
		if (txt) {
			Dialog.show(title, txt);
		}
	}
	return false;
}

function showTopDialog(ev, page, params, label) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	Menu.hide();
	Popup.hide();
	WSApi.getAsync(page, params, function(txt) {
		if (txt) {
			Dialog.show(label, txt);
		}
	});
	return false;
}

function showDbDialog(ev, page, params, label) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	Menu.hide();
	Popup.hide();
	WSApi.getDBAsync(page, params, function(txt) {
		if (txt) {
			Dialog.show(label, txt);
		}
	});
	return false;
}

function showDialog(ev, page, params, label) {
	if (page.substr(0, 3) == 'db:') {
		return showDbDialog(ev, page.substr(3), params, label);
	} else {
		return showTopDialog(ev, page, params, label);
	}
}

function showFormDialog(ev, page, frm, label) {
	var params = frm.serialize(true);
	return showDialog(ev, page, params, label);
}

/*
 * Popup functions
 */

function showTopPopup(ev, page, params, label) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	Menu.hide();
	var el = Event.element(ev);
	WSApi.getAsync(page, params, function(txt) {
		Popup.showLeft(el, txt);
	});
	return false;
}

function showDbPopup(ev, page, params, label) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	Menu.hide();
	var el = Event.element(ev);
	WSApi.getDBAsync(page, params, function(txt) {
		Popup.showLeft(el, txt);
	});
	return false;
}

function showPopup(ev, page, params, label) {
	if (page.substr(0, 3) == 'db:') {
		return showDbPopup(ev, page.substr(3), params, label);
	} else {
		return showTopPopup(ev, page, params, label);
	}
}

/*
 * Navigation functions
 */

function gotoPage(page) {
	window.location.href = page;
	return false;
}

function reloadPage() {
	showCancelDialog(Messages.waitTitle, Messages.waitText);
	window.location.reload();
	return false;
}

function forceReload() {
	window.location.reload();
	return false;
}

function goBack() {
	history.go(-1);
	return false;
}

function hideError(id) {
	var el = $('error-'+id);
	if (el) {
		el.hide();
		tw_windowOnResize();
	}
	return false;
}

function showLoginDialog(ev) {
	showTopDialog(ev, 'login', null, Messages.loginTitle);
	return false;
}

function showReloginDialog(ev) {
	showTopDialog(ev, 'relogin', { q: WSApi.currentUser }, Messages.loginTitle);
	return false;
}

function forceRelogin() {
	Dialog.close();
	Dialog.hide();
	if (WSApi.currentUser) {
		showReloginDialog();
	} else {
		showLoginDialog();
	}
}

function showSearchDialog(ev) {
	showDbDialog(ev, 'search', null, Messages.searchTitle);
	return false;
}

function generateWSLink(ev, title, frm) {
	frm = $(frm);
	showDbDialog(ev, 'wslinks', {
			catalog: frm.elements['catalog'].value,
			schema: frm.elements['schema'].value,
			object: frm.elements['object'].value,
			where: frm.elements['where'].value,
			order: frm.elements['order'].value
			}, title);
	return false;
}

function generateQueryLink(ev, title, frm) {
	frm = $(frm);
	var params = {
		q: frm.elements['q'].value,
		index: frm.elements['index'].value
	};
	for (var i = 0; i < frm.elements.length; i++) {
		if (frm.elements[i].name.startsWith('params[')) {
			params[frm.elements[i].name] = frm.elements[i].value;
		}
	}
	showDbDialog(ev, 'querylinks', params, title);
	return false;
}

function generateResultLink(ev, title, frm) {
	frm = $(frm);
	var params = {
		q: frm.elements['q'].value
	};
	for (var i = 0; i < frm.elements.length; i++) {
		if (frm.elements[i].name.startsWith('params[')) {
			params[frm.elements[i].name] = frm.elements[i].value;
		}
	}
	showDbDialog(ev, 'resultlinks', params, title);
	return false;
}

/*
 * Query editor
 */

function toggleTextField(f) {
	var enabled = Forms.toggleEnabled(f);
	if (enabled) {
		var e = $(f);
		e.focus();
		e.select();
	}
	return false;
}

function setSelectedValue(n, v, t) {
	var d = $(n);
	if (d) {
		d.value = v;
	}
	var l = $(n + '-value');
	if (l) {
		l.innerHTML = t;
	}
}

function applySelection(src, dst) {
	var s = $(src);
	var o = s.options[s.selectedIndex];
	
	setSelectedValue(dst, o.value, o.text);
	
	Popup.hide();
	return false;
}

function showLine(e) {
	var id = e.id + "-enabled";
	var flg = $(id);
	if (flg) {
		flg.value = 'true';
	}
	e.show();
	Forms.focusFirstField(e);
}

function hideLine(e) {
	var id = e.id + "-enabled";
	var flg = $(id);
	if (flg) {
		flg.value = 'false';
	}
	e.select('input[type=text]').each(function(el) {
		el.value = '';
	});
	e.hide();
}

function addLine(ev, name) {
	for (var i = 0; ; i++) {
		var id = name + '-' + i;
		var e = $(id);
		if (!e) {
			break;
		}
		if (!e.visible()) {
			showLine(e);
			break;
		}
	}
	return false;
}

function removeLine(ev, prefix, index) {
	var e = $(prefix + '-' + index);
	if (e && e.visible()) {
		hideLine(e);
	}
	return false;
}

function moveLineDown(ev, prefix, index) {
	var fromLine = $(prefix + '-' + index);
	var toLine = $(prefix + '-' + (index + 1));
	if (fromLine && toLine) {
		Forms.exchangeContents(fromLine, toLine);
		showLine(toLine);
	}
	return false;
}

function moveLineUp(ev, prefix, index) {
	var fromLine = $(prefix + '-' + index);
	var toLine = $(prefix + '-' + (index - 1));
	if (fromLine && toLine) {
		Forms.exchangeContents(fromLine, toLine);
		showLine(toLine);
	}
	return false;
}

function setTheme(f) {
	WSApi.setTheme($F(f), forceReload);
	return false;
}

function setLocale(f) {
	WSApi.setLocale($F(f), forceReload);
	return false;
}

function setTimezone(f) {
	WSApi.setTimezone($F(f), forceReload);
	return false;
}

function setGraphType(q, t, s, v) {
	WSApi.setGraphType(q, t, s, v, reloadPage);
	return false;
}

function toggleEditMode() {
	WSApi.toggleEditMode(forceReload);
	return false;
}

function toggleFormatter(q, v) {
	WSApi.toggleFormatter(q, $F(v), reloadPage);
	return false;
}

function toggleFormatMode(q, v) {
	WSApi.toggleFormatMode(q, v, reloadPage);
	return false;
}

function toggleColoringMode(q, v) {
	WSApi.toggleColoringMode(q, v, reloadPage);
	return false;
}

function toggleLineNumberMode(q, v) {
	WSApi.toggleLineNumberMode(q, v, reloadPage);
	return false;
}

function toggleTrimColumnsMode(q, v) {
	WSApi.toggleTrimColumnsMode(q, v, reloadPage);
	return false;
}

function clearDbCache() {
	WSApi.clearCache(reloadPage);
	return false;
}

function cancelTasks() {
	WSApi.cancelTasks(function(txt) {});
	return false;
}

function startAutoRefresh() {
	var i = HashMonitor.getNumber('refresh');
	if (i > 0) {
		AutoRefresh.start(i, 'next-auto-refresh');
	}
	return false;
}

function changeAutoRefresh(intvl) {
	AutoRefresh.stop();
	var t = parseInt(intvl);
	HashMonitor.set({ refresh: t }, true);
	startAutoRefresh();
	return false;
}

function toggleAutoRefresh(ev) {
	if (AutoRefresh.isActive()) {
		changeAutoRefresh(0);
	} else {
		showTopMenu(ev, 'autorefreshs');
	}
	return false;
}

function relogin(frm) {
	showCancelDialog(Messages.submitFormTitle, Messages.submitFormText);
	WSApi.login($F('f1-username'), $F('f1-password'), function(txt) {
		Dialog.hide();
	});
	return false;
}

function runQuery(ev, query, param, target) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	Menu.hide();
	Popup.hide();
	var el = $(target);
	if (el) {
		if (target.indexOf('drilldown') == 0) {
			WSApi.getDrilldownResult(target.substr(9), query, param, function(txt) {
				el.innerHTML = txt;
				tw_contentChanged();
			});
		} else {
			WSApi.getQueryResult(query, param, function(txt) {
				el.innerHTML = extractLocalStyles(txt);
				tw_contentChanged();
				HashMonitor.set({ detail: query, params: param });
				Tabs.hashChanged(HashMonitor.values);
			});
		}
	}
	return false;
}

function restoreQuery() {
	var d = HashMonitor.get('detail');
	var p = HashMonitor.get('params');
	
	if (d) {
		runQuery(null, d, p, 'explorer-right');
	}
}

function rerunQuery() {
	var d = HashMonitor.get('detail');
	var p = HashMonitor.get('params');
	if (d) {
		AutoRefresh.stop();
		var el = $('explorer-right');
		if (el) {
			WSApi.getQueryResult(d, p, function(txt) {
				el.innerHTML = extractLocalStyles(txt);
				tw_contentChanged();
				Tabs.hashChanged(HashMonitor.values);
				startAutoRefresh();
			});
			return true;
		}
	}
	return false;
}

function resetTimechart() {
	AutoRefresh.stop();
	WSApi.resetTimechart(reloadPage);
	return false;
}

function formatText(txt, fmt, cb) {
	WSApi.formatText(txt, fmt, cb);
}

function showDatePicker(ev, id) {
	Event.stop(ev);
	new CalendarDateSelect($(id), { time : true, year_range : 10, popup_by : Event.element(ev) });
	return false;
}

function getMarker() {
	var marker = $('marker');
	if (!marker) {
		marker = Elements.create(document.body, 'div', 'marker');
	}
	return marker;
}

function zoomView(ev) {
	var t = false;
	$$('.hideable').each(function(e) {
		if (e.visible()) {
			e.hide();
			t = true;
		} else {
			e.show();
		}
	});
	HashMonitor.set({ zoom: t }, true);
	tw_windowOnResize();
	return false;
}

function expandTab(ev) {
	var el = Event.element(ev);
	var tab = el.up('.dashboard');
	if (tab) {
		if (tab.hasClassName('collapsed')) {
			tab.removeClassName('collapsed');
			el.innerHTML = '&#x25bc;';
		} else {
			tab.addClassName('collapsed');
			el.innerHTML = '&#x25ba;';
		}
	}
	return false;
}

function zoomTab(ev) {
	var el = Event.element(ev);
	var tab = el.up('.dashboard');
	if (tab) {
		var marker = getMarker();
		
		if (tab.hasClassName('zoomed')) {
			var p1 = $(marker.parentNode);
			if (p1.hasClassName('column2')) {
				var p2 = $(p1.parentNode);
				p2.childElements().each(function(n) { n.show(); });
				
				p1.insertBefore(tab, marker);
				document.body.appendChild(marker);
				tab.removeClassName('zoomed');
			}
		} else {
			var p1 = $(tab.parentNode);
			if (p1.hasClassName('column2')) {
				var p2 = $(p1.parentNode);
				p2.childElements().each(function(n) { n.hide(); });
				
				p1.insertBefore(marker, tab);
				p2.insertBefore(tab, p2.childNodes[0]);
				tab.addClassName('zoomed');
			}
		}
	}
	return false;
}

function zoomLeft() {
	var left = $('explorer-left');
	var right = $('explorer-right');
	
	if (left && right) {
		if (left.hasClassName('zoomed')) {
			left.style.width = '';
			left.removeClassName('zoomed');
			right.show();
		} else {
			right.hide();
			left.style.width = '100%';
			left.addClassName('zoomed');
		}
	}
	tw_windowOnResize();
	return false;
}

function zoomRight() {
	var left = $('explorer-left');
	var right = $('explorer-right');
	
	if (left && right) {
		if (right.hasClassName('zoomed')) {
			right.style.width = '';
			right.removeClassName('zoomed');
			left.show();
		} else {
			left.hide();
			right.style.width = '100%';
			right.addClassName('zoomed');
		}
	}
	tw_windowOnResize();
	return false;
}

function clearElement(e) {
	var el = $(e);
	if (el)
		el.value = '';
	return false;
}

var zoomContentSource = null;

function zoomContent(e) {
	var el = $(e);
	var frm = $('zoomform');
	var full = $('fullscreen');
	if (el && frm && full && !full.visible()) {
		var txt;
		zoomContentSource = el;
		if (zoomContentSource.tagName == 'TEXTAREA') {
			txt = zoomContentSource.value;
		} else {
			txt = zoomContentSource.innerText;
		}
		getFormInto(frm, full, { zoomstmt: txt }, function() {
			full.show();
			tw_windowOnResize();
		});
	}
	return false;
}

function unzoomContent() {
	var full = $('fullscreen');
	if (full && full.visible()) {
		if (zoomContentSource.tagName == 'TEXTAREA') {
			var el = $('zoomresult');
			if (el) {
				zoomContentSource.value = el.value;
			}
		}
		full.hide();
		tw_windowOnResize();
	}
	return false;
}

function toggleElement(e) {
	var el = $(e);
	if (el) {
		el.toggle();
		tw_windowOnResize();
	}
	return false;
}

/*
 * Tree functions
 */

function toggleStaticTreeItem(ev, label, left) {
	var e = Tree.getItem(label, left);
	if (e && !Tree.collapseStaticItem(e)) {
		Tree.expandStaticItem(e);
	}
	return false;
}

function toggleTreeItem(ev, label, query, level, param, left, target) {
	var e = Tree.getItem(label, left);
	if (e && !Tree.collapseItem(e)) {
		WSApi.getTreeItem(query, level, label, left, target, param, function(txt) {
			Tree.expandItem(e, txt);
			HashMonitor.set({ node: label+'-'+left }, true);
		});
	}
	return false;
}

function selectTreeItem() {
	var l = HashMonitor.get('node');
	if (l) {
		Tree.selectPath(l, false);
	} else {
		var e = $('treeselection');
		if (e) {
			l = e.textContent;
			if (l) {
				Tree.selectPath(l, true);
			}
		}
	}
}

function loadQueryLevel(ev, query, level, param, left) {
	if (ev) {
		Event.stop(ev);
	}
	AutoRefresh.stop();
	var el = $('explorer-left');
	if (el) {
		WSApi.getQueryLevel(query, level, param, left, 'explorer-right', function(txt) {
			el.innerHTML = txt;
			tw_contentChanged();
			HashMonitor.set({ level: left }, true);
		});
	}
	return false;
}

function loadCustomQueryHistory(index) {
	WSApi.loadCustomQueryHistory(index, function() {
		// don't use reloadPage, it might be customized by the current page
		showCancelDialog(Messages.waitTitle, Messages.waitText);
		forceReload();
	});
	return false;
}

function loadCustomQuery(name, del) {
	if (del) {
		WSApi.deleteCustomQuery(name, function() {
			// don't use reloadPage, it might be customized by the current page
			showCancelDialog(Messages.waitTitle, Messages.waitText);
			forceReload();
		});
	} else {
		WSApi.loadCustomQuery(name, function() {
			// don't use reloadPage, it might be customized by the current page
			showCancelDialog(Messages.waitTitle, Messages.waitText);
			forceReload();
		});
	}
	return false;
}

function saveCustomQuery(name, statement) {
	WSApi.saveCustomQuery(name, statement, function() {
		// don't use reloadPage, it might be customized by the current page
		showCancelDialog(Messages.waitTitle, Messages.waitText);
		forceReload();
	});
	return false;
}

function selectQueryLevel() {
	var qn = $('queryName');
	if (qn) {
		var l = HashMonitor.get('level');
		if (l) {
			var path = l.split('-');
			var param = path[path.length - 1];
			loadQueryLevel(null, qn.value, path.length, param, l);
		} else {
			var e = $('mlselection');
			if (e) {
				l = e.textContent;
				if (l) {
					var path = l.split('-');
					var param = path[path.length - 1];
					loadQueryLevel(null, qn.value, path.length, param, l);
				}
			}
		}
	}
}

function runInitialQuery() {
	var e = $('detailQuery');
	if (e) {
		Forms.click(e);
	}
}

/*
 * Tooltip functions
 */

function showTip(ev, e) {
	Tooltip.show(e, Event.pointerX(ev) - 10, Event.pointerY(ev) - 10);
	return false;
}

function hideTip(ev, e) {
	Tooltip.hide(e);
	return false;
}

function selectComplete(el) {
	var s = $F(el);
	if (s) {
		$('f1-submit').disabled = false;
	} else {
		$('f1-submit').disabled = true;
	}
	return false;
}

function aggsubmit(f, frm) {
	var node = $('aggregate-'+frm);
	getFormInto(f, node, null, function(el) {
		var rows = el.select('tr').length;
		$('aggcount-'+frm).innerHTML = rows;
	});
	return false;
}

function aggfilter(el, frm, index) {
	var f = $('aggform-'+frm);
	if (f) {
		var inp = f.elements.namedItem('exprs['+index+']');
		if (inp) {
			var txt = el.textContent;
			inp.value = "='"+txt+"'";
			aggsubmit(f, frm);
		}
	}
	return false;
}

/*
 * Outline generation
 */

function showOutline(ev, id) {
	Event.stop(ev);
	AutoRefresh.stop();
	var menu = $('outline-'+id);
	var text = $('text-'+id);
	if (menu && text) {
		var div = getMenuParentNode(ev);
		var items = [];
		var nextId = 0;
		text.select('.identifier').sortBy(function(el) {
			return el.innerHTML;
		}).each(function(el) {
			var nodeId = 'text-'+id+'-'+nextId;
			el.id = nodeId;
			items[nextId] = '<div class="menuitem"><span onclick="return gotoNode(\'' + nodeId + '\');">' + el.innerHTML + '</span></div>';
			nextId++;
		});
		
		if (nextId > 0) {
			WSApi.getMenuRows(nextId, function(n) {
				var txt = '<div class="menucolumn">';
				for (var i = 0; i < nextId; i++) {
					if ((i > 0) && (i % n == 0)) {
						txt += '</div><div class="menucolumn">';
					}
					txt += items[i];
				}
				txt += '</div>';
				Menu.showLeft(div, txt);
			});
		} else {
			var txt = '<div class="menucolumn">' + menu.innerHTML + '</div>';
			Menu.showLeft(div, txt);
		}
	}
	return false;
}

function gotoNode(id) {
	var node = $(id);
	if (node) {
		var posXY = node.positionedOffset();
		var p = node.parentNode.parentNode.parentNode.parentNode; // span -> code -> span -> pre -> div
		p.scrollTop = posXY[1] - p.offsetHeight / 3;
		p.select('.identifier').each(function(el) {
			el.removeClassName('match');
		});
		node.addClassName('match');
	}
	return false;
}

/*
 * Global event handlers
 */

document.observe('click', function(ev) {
	var e = Event.element(ev);
	if ((e.className == 'menuitem') || (e.className == 'tab')) {
		Forms.click(e.firstChild, ev);
	}
});

document.observe('dom:loaded', function(ev) {
	Dialog.setWaitMessage('<p class="wait">' + Messages.waitText + '</p>');
	
	WSApi.beforeRequest = function() {
		if (Dialog.isActive()) {
			Dialog.setWaiting(true);
		} else {
			ajaxTimer.start(500);
		}
		return true;
	};
	
	WSApi.afterRequest = function() {
		ajaxTimer.stop();
		Dialog.close();
		Dialog.setWaiting(false);
	};
	
	WSApi.onRequestFailure = function(txt) {
		var plaintext = txt||'';
		plaintext = plaintext.replace(/^.*<body[^>]*>/i, '');
		plaintext = plaintext.replace(/<\/body[^>]*>.*$/i, '');
		plaintext = plaintext.replace(/<[^>]+>/g, '');
		showErrorDialog(Messages.waitTitle, plaintext);
	};
	
	AutoRefresh.onRefresh = function() {
		reloadPage();
	};
	
	Tabs.onactivate = function(e) {
		var b = e.select('.tab-body').first();
		if (b) {
			var f = $('footer');
			var yf = Elements.getHeight(f);
			var h = Elements.getWindowHeight();
			
			var y = Elements.getY(b);
			Elements.resizeTo(b, undefined, h - y - 3 * yf);
		}
		Forms.focusFirstField(e);
	};
	Tabs.linkWithHash('result');
	HashMonitor.addListener(restoreQuery);
	HashMonitor.addListener(startAutoRefresh);
	HashMonitor.notifyListeners();
	selectTreeItem();
	selectQueryLevel();
	runInitialQuery();
	
	Event.observe(window, 'resize', tw_windowOnResize);
	
	tw_contentChanged();
	
	if (HashMonitor.getBoolean('zoom')) {
		zoomView(null);
	}
	
	var e = Tabs.current();
	if (e) {
		Forms.focusFirstField(e);
	}
	
	KeyboardHandler.addHandler(27 /* Esc */, KeyboardHandler.NORMAL, function(ev) {
		if (Dialog.isActive()) {
			cancelSubmit();
			closeDialog();
		} else {
			cancelForm();
		}
		return false;
	});
	KeyboardHandler.addHandler(37 /* Left */, KeyboardHandler.ALT | KeyboardHandler.SHIFT, function(ev) {
		Tabs.showPrevious('result');
		return false;
	});
	KeyboardHandler.addHandler(38 /* Up */, KeyboardHandler.ALT | KeyboardHandler.SHIFT, function(ev) {
		Tabs.showFirst('result');
		return false;
	});
	KeyboardHandler.addHandler(39 /* Right */, KeyboardHandler.ALT | KeyboardHandler.SHIFT, function(ev) {
		Tabs.showNext('result');
		return false;
	});
	KeyboardHandler.addHandler(40 /* Down */, KeyboardHandler.ALT | KeyboardHandler.SHIFT, function(ev) {
		Tabs.showLast('result');
		return false;
	});
	KeyboardHandler.addHandler('L', KeyboardHandler.ALT | KeyboardHandler.SHIFT, function(ev) {
		showLoginDialog.defer(ev);
		return false;
	});
	KeyboardHandler.addHandler('F', KeyboardHandler.ALT | KeyboardHandler.SHIFT, function(ev) {
		showSearchDialog.defer(ev);
		return false;
	});
	KeyboardHandler.addHandler('E', KeyboardHandler.ALT | KeyboardHandler.SHIFT, function(ev) {
		toggleEditMode.defer();
		return false;
	});
	
	startAutoRefresh();
});
