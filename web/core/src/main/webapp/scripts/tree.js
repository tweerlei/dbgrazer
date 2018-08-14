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
