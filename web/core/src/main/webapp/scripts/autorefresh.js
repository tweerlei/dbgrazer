/*
 * Auto refresh support:
 * Shows a countdown in a specified element and performs a customizable action when reaching 0
 */

var AutoRefresh = {
	timeout: 0,
	elem: null,
	
	start: function(t, id) {
		if (!this.elem) {
			this.timeout = t + 1;
			this.elem = $(id);
			this.tick();
		}
	},
	
	stop: function() {
		if (this.elem) {
			this.elem.innerHTML = '';
			this.elem = null;
		}
	},
	
	tick: function() {
		if (this.elem) {
			this.timeout--;
			if (this.timeout <= 0) {
				this.elem.innerHTML = '';
				this.elem = null;
				this.onRefresh();
			} else {
				this.elem.innerHTML = '' + this.timeout;
				this.tick.bind(this).delay(1);
			}
		}
	},
	
	isActive: function() {
		return (!!this.elem);
	},
	
	onRefresh: function() {
	},
	
	/* The end needs no comma */
	_end: null
};
