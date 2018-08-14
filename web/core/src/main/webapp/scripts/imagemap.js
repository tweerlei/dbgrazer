/*
 * Image map resizing
 */

var ImageMap = Class.create({
	
	initialize: function(imageId) {
		var img = $(imageId);
		if (!img || !img.complete || !img.naturalWidth || !img.naturalHeight) {
			return;
		}
		var map = img.useMap;
		if (!map) {
			return;
		}
		map = $(map.substr(1));
		if (!map) {
			return;
		}
		
		this.img = img;
		this.map = map;
		this.areas = map.getElementsByTagName('area');
		this.coords = [];
		for (var n = 0; n < this.areas.length; n++) {
			this.coords[n] = this.areas[n].coords.match(/[0-9]+/g);
		}
	},
	
	resize: function() {
		if (this.img) {
			var fx = this.img.clientWidth / this.img.naturalWidth;
			var fy = this.img.clientHeight / this.img.naturalHeight;
			for (var n = 0; n < this.areas.length; n++) {
				var c = [];
				for (var m = 0; m < this.coords[n].length; m++) {
					c[m] = Math.round(this.coords[n][m] * ((m % 1) ? fy : fx));
				}
				this.areas[n].coords = c.join(',');
			}
		}
		return true;
	}
});
