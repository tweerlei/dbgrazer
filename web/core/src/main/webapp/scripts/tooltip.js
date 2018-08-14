/*
 * Tooltip control, creates a single element:
 * 
 * <div id="tooltip"></div>
 */

var Tooltip = {
	target: null,
	
	create: function() {
		if (!this.target) {
			this.target = Elements.create(document.body, 'div', 'tooltip');
			this.target.hide();
			
			var inst = this;
			this.target.observe('mouseout', function() {
				inst.hide();
			});
		}
	},
	
	show: function(n, x, y) {
		this.create();
		var m = this.target;
		
		var tt = $('tooltip-'+n);
		if (tt) {
			m.innerHTML = tt.innerHTML;
			m.show();
			Elements.moveTo(m, x, y);
			return true;
		}
		return false;
	},
	
	hide: function(n) {
		var m = this.target;
		if (m) {
			m.hide();
		}
	},
	
	/* The end needs no comma */
	_end: null
};
