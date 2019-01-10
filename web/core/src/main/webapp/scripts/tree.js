/*
 * Tree control
 */

var Tree = {
	expandMessage: '&#x25bc;',
	collapseMessage: '&#x25ba;',
	
	getItem: function(label, id) {
		return $('treerow-'+label+'-'+id);
	},
	
	processItem: function(label, id, activate) {
		var e = this.getItem(label, id);
		if (e) {
			if (activate) {
				this.activateItem(e);
			} else {
				this.toggleItem(e);
			}
		}
		return true;
	},
	
	getTablePath: function(e, a) {
		a.push(e.getAttribute('data-param'));
		var td = e.parentNode.parentNode.parentNode;	// TBODY -> TABLE -> parent
		if (td.tagName == 'TD') {
			var p = td.parentNode.previousSibling;	// TR -> previous row
			if (p && p.className == 'treerow') {
				this.getTablePath(p, a);
			}
		}
	},
	
	getSimplePath: function(e, a) {
		a.push(e.getAttribute('data-param'));
		var p = e.parentNode;
		if (p.className == 'treerow') {
			this.getSimplePath(p, a);
		}
	},
	
	getPath: function(e) {
		var ret = [];
		if (e.tagName == 'TR') {
			this.getTablePath(e, ret);
		} else {
			this.getSimplePath(e, ret);
		}
		return ret;
	},
	
	resolveTablePath: function(e, path, a) {
		if (path.length == 0) {
			return;
		}
		if (e.nodeType != 1 || e.tagName != 'TABLE') {
			return;
		}
		var param = path[0];
		var tb = e.firstChild.nextSibling;	// THEAD -> TBODY
		var row = tb.childElements().find(function(n) {
			return (n.getAttribute('data-param') == param);
		});
		if (row) {
			a.push(row.id);
			var t = row.nextSibling.firstChild.firstChild;	// next TR -> TD -> child
			this.resolveTablePath(t, path.slice(1), a);
		}
	},
	
	resolveSimplePath: function(e, path, a) {
		if (path.length == 0) {
			return;
		}
		var param = path[0];
		var row = e.childElements().find(function(n) {
			return (n.getAttribute('data-param') == param);
		});
		if (row) {
			a.push(row.id);
			this.resolveSimplePath(row, path.slice(1), a);
		}
	},
	
	resolvePath: function(label, path) {
		var ret = [];
		var root = $('tree-'+label);
		if (root) {
			var e = root.firstChild;
			if (e.tagName == 'TABLE') {
				this.resolveTablePath(e, path, ret);
			} else {
				this.resolveSimplePath(e, path, ret);
			}
		}
		return ret;
	},
	
	selectParamPath: function(label, path, activate) {
		var ids = this.resolvePath(label, path);
		var offset = label.length + 1;
		for (var i = 0; i < ids.length; i++) {
			WSApi.scheduler.add(this.processItem.bind(this), label, ids[i].substr(offset), activate && (i == ids.length - 1));
		}
	},
	
	selectPath: function(l, activate) {
		var path = l.split('-');
		var label = path[0];
		var id = '';
		for (var i = 1; i < path.length; i++) {
			if (i > 1) {
				id += '-';
			}
			id += path[i];
			WSApi.scheduler.add(this.processItem.bind(this), label, id, activate && (i == path.length - 1));
		}
	},
	
	toggleItem: function(e) {
		var l = e.firstChild.firstChild;	// button link
		if (l.onclick) {
			l.onclick();
			return true;
		}
		return false;
	},
	
	activateItem: function(e) {
		var l = e.firstChild.nextSibling;	// label
		var a = l.childElements().find(function(c) {
			return (c.tagName == 'A');
		});
		if (a && a.onclick) {
			a.onclick();
			return true;
		}
		return false;
	},
	
	collapseSimpleItem: function(e) {
		var lbl = e.firstChild;
		var n = 0;
		Element.adjacent(lbl, '.treerow').each(function(c) {
			e.removeChild(c);
			n++;
		});
		return n;
	},
	
	collapseTableItem: function(e) {
		var lbl = e.nextSibling;
		if (lbl && !lbl.id && lbl.visible()) {
			lbl.hide();
			lbl.firstChild.nextSibling.innerHTML = '&nbsp;';
			return 1;
		}
		return 0;
	},
	
	collapseItem: function(e) {
		var n;
		if (e.tagName == 'TR') {
			n = this.collapseTableItem(e);
		} else {
			n = this.collapseSimpleItem(e);
		}
		if (n) {
			e.down('.treebutton').firstChild.innerHTML = this.collapseMessage;
		}
		return n;
	},
	
	expandSimpleItem: function(e, content) {
//		Element.adjacent(e, '.treerow').each(function(c) {
//			Tree.collapseSimpleItem(c);
//		});
		e.innerHTML = e.innerHTML + content;
		return 1;
	},
	
	expandTableItem: function(e, content) {
//		Element.adjacent(e, '.treerow').each(function(c) {
//			Tree.collapseTableItem(c);
//		});
		var lbl = e.nextSibling;
		if (lbl && !lbl.id && !lbl.visible()) {
			lbl.firstChild.nextSibling.innerHTML = content;
			lbl.show();
			return 1;
		}
		return 0;
	},
	
	expandItem: function(e, content) {
		var n;
		if (e.tagName == 'TR') {
			n = this.expandTableItem(e, content);
		} else {
			n = this.expandSimpleItem(e, content);
		}
		if (n) {
			e.down('.treebutton').firstChild.innerHTML = this.expandMessage;
		}
		return n;
	},
	
	collapseStaticSimpleItem: function(e) {
		var lbl = e.firstChild;
		var n = 0;
		Element.adjacent(lbl, '.treerow').each(function(c) {
			if (c.visible()) {
				c.hide();
				n++;
			}
		});
		return n;
	},
	
	collapseStaticTableItem: function(e) {
		var lbl = e.nextSibling;
		if (lbl && !lbl.id) {
			if (lbl.visible()) {
				lbl.hide();
				return 1;
			}
		} else {
			var n = 0;
			Element.adjacent(e, '.treerow').each(function(c) {
				if (c.id.startsWith(e.id) && c.visible()) {
					c.hide();
					n++;
				}
			});
			return n;
		}
		return 0;
	},
	
	collapseStaticItem: function(e) {
		var n;
		if (e.tagName == 'TR') {
			n = this.collapseStaticTableItem(e);
		} else {
			n = this.collapseStaticSimpleItem(e);
		}
		if (n) {
			e.down('.treebutton').firstChild.innerHTML = this.collapseMessage;
		}
		return n;
	},
	
	expandStaticTableItem: function(e) {
		var lbl = e.nextSibling;
		if (lbl && !lbl.id) {
			if (!lbl.visible()) {
				lbl.show();
				return 1;
			}
		} else {
			var n = 0;
			Element.adjacent(e, '.treerow').each(function(c) {
				if (c.id.startsWith(e.id) && !c.visible()) {
					c.show();
					n++;
				}
			});
			return n;
		}
		return 0;
	},
	
	expandStaticSimpleItem: function(e) {
		var lbl = e.firstChild;
		var n = 0;
		Element.adjacent(lbl, '.treerow').each(function(c) {
			if (!c.visible()) {
				c.show();
				n++;
			}
		});
		return n;
	},
	
	expandStaticItem: function(e) {
		var n;
		if (e.tagName == 'TR') {
			n = this.expandStaticTableItem(e);
		} else {
			n = this.expandStaticSimpleItem(e);
		}
		if (n) {
			e.down('.treebutton').firstChild.innerHTML = this.expandMessage;
		}
		return n;
	},
	
	/* The end needs no comma */
	_end: null
};
