/*
 * Tab control, optionally linked with the location hash.
 * Multiple tab controls per page are supported, each identified by a NAME,
 * but only one can be linked with the hash.
 * 
 * <div id="tab-row-NAME">
 *   <div id="tab-NAME0" class="tab tab-active"></div>
 *   <div id="tab-NAME1" class="tab"></div>
 *   <div id="tab-NAME2" class="tab"></div>
 *   ...
 * </div>
 * <div>
 *   <div id="tab-page-NAME0" class="tab-page"></div>
 *   <div id="tab-page-NAME1" class="tab-page" style="display: none;"></div>
 *   <div id="tab-page-NAME2" class="tab-page" style="display: none;"></div>
 *   ...
 * </div>
 */

var Tabs = {
	linked: false,
	
	onactivate: function(e) {
	},
	
	linkWithHash: function(n) {
		this.linked = n;
		HashMonitor.addListener(Tabs.hashChanged.bind(Tabs));
	},
	
	activate: function(id) {
		var e = $('tab-page-'+id);
		if (e && !e.visible()) {
			Element.adjacent(e, 'div.tab-page').each(function(c) {
				c.hide();
			});
			
			e.show();
			this.onactivate(e);
			
			var t = $('tab-'+id);
			if (t) {
				Element.adjacent(t, 'div.tab').each(function(c) {
					c.removeClassName('tab-active');
				});
				
				t.addClassName('tab-active');
			} else {
				var c = $('combo-'+id);
				if (c) {
					c.parentNode.selectedIndex = c.index;
				}
			}
			return true;
		}
		return false;
	},
	
	show: function(n, i) {
		var id = '' + n + i;
		if (this.activate(id)) {
			if (this.linked == n) {
				HashMonitor.set({ tab: id }, true);
			}
			return true;
		}
		return false;
	},
	
	hashChanged: function(v) {
		var id = v['tab'];
		if (id) {
			return this.activate(id);
		}
		return false;
	},
	
	current: function() {
		var r = null;
		$$('div.tab-page').each(function(e) {
			if (e.visible) {
				r = e;
			}
		});
		return (r);
	},
	
	minIndex: function(n) {
		return 0;
	},
	
	maxIndex: function(n) {
		var row = $('tab-content-'+n);
		if (row) {
			return row.childElements().size() - 2;
		}
		return 0;
	},
	
	currentIndex: function(n) {
		var tab = $$('#tab-row-'+n+' .tab-active').first();
		if (tab) {
			var id = tab.id.substr(4 + n.length);
			return parseInt(id);
		} else {
			var cb = $('combo-'+n);
			if (cb) {
				return (cb.selectedIndex);
			}
		}
		return 0;
	},
	
	showNext: function(n) {
		return this.show(n, this.currentIndex(n) + 1);
	},
	
	showPrevious: function(n) {
		return this.show(n, this.currentIndex(n) - 1);
	},
	
	showFirst: function(n) {
		return this.show(n, this.minIndex(n));
	},
	
	showLast: function(n) {
		return this.show(n, this.maxIndex(n));
	},
	
	/* The end needs no comma */
	_end: null
};
