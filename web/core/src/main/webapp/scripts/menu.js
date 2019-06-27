/*
 * Popup control, creates a single element:
 * 
 * <div id="id"></div>
 */

var PopupWindow = Class.create({
	
	initialize: function(id, ah) {
		this.targetId = id;
		this.target = null;
		this.active = false;
		this.parent = null;
		this.autohide = ah;
	},
	
	create: function() {
		if (!this.target) {
			this.target = Elements.create(document.body, 'div', this.targetId);
			this.target.hide();
			
			var inst = this;
			document.observe('click', function() {
				inst.hide();
			});
			
			if (!this.autohide) {
				this.target.observe('click', function(ev) {
					Event.stop(ev);
				});
			}
		}
	},
	
	show: function(parent, content, right) {
		if (!content) {
			return false;
		}
		
		this.create();
		var m = this.target;
		
		if (right) {
			Elements.moveTo(m, 0, Elements.getScrolledY(parent) + Elements.getHeight(parent), true);
		} else {
			Elements.moveTo(m, Elements.getScrolledX(parent), Elements.getScrolledY(parent) + Elements.getHeight(parent));
		}
		m.innerHTML = content;
		m.show();
		Forms.init();
		Forms.focusFirstField(m);
		
		this.active = true;
		this.parent = parent;
		return true;
	},
	
	showLeft: function(parent, content) {
		return this.show(parent, content, false);
	},
	
	showRight: function(parent, content) {
		return this.show(parent, content, true);
	},
	
	hide: function() {
		if (this.active) {
			this.target.hide();
			this.active = false;
			this.parent = null;
		}
	}
});
