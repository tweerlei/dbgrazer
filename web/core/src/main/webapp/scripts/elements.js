/*
 * Generic element handling
 */

var Elements = {

	getX: function(e) {
		var ret = 0;
		while (e != null) {
			ret += e.offsetLeft;
			e = e.offsetParent;
		}
		return (ret);
	},

	getY: function(e) {
		var ret = 0;
		while (e != null) {
			ret += e.offsetTop;
			e = e.offsetParent;
		}
		return (ret);
	},

	getScrolledX: function(e) {
		var ret = Elements.getX(e);
		while (e.localName != null) {
			ret -= e.scrollLeft;
			e = e.parentNode;
		}
		return (ret);
	},

	getScrolledY: function(e) {
		var ret = Elements.getY(e);
		while (e.localName != null) {
			ret -= e.scrollTop;
			e = e.parentNode;
		}
		return (ret);
	},

	getWidth: function(e) {
		return (e.offsetWidth);
	},

	getHeight: function(e) {
		return (e.offsetHeight);
	},

	getWindowWidth: function() {
		var windowWidth, windowHeight;
		
		if (self.innerHeight) {	// all except Explorer
			if (document.documentElement.clientWidth){
				windowWidth = document.documentElement.clientWidth; 
			} else {
				windowWidth = self.innerWidth;
			}
			windowHeight = self.innerHeight;
		} else if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode
			windowWidth = document.documentElement.clientWidth;
			windowHeight = document.documentElement.clientHeight;
		} else if (document.body) { // other Explorers
			windowWidth = document.body.clientWidth;
			windowHeight = document.body.clientHeight;
		}
		
		return windowWidth;
	},

	getWindowHeight: function() {
		var windowWidth, windowHeight;
		
		if (self.innerHeight) {	// all except Explorer
			if (document.documentElement.clientWidth){
				windowWidth = document.documentElement.clientWidth; 
			} else {
				windowWidth = self.innerWidth;
			}
			windowHeight = self.innerHeight;
		} else if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode
			windowWidth = document.documentElement.clientWidth;
			windowHeight = document.documentElement.clientHeight;
		} else if (document.body) { // other Explorers
			windowWidth = document.body.clientWidth;
			windowHeight = document.body.clientHeight;
		}
		
		return windowHeight;
	},

	moveTo: function(e, x, y, fromRight, fromBottom) {
		if (typeof(x) != 'undefined') {
			if (fromRight) {
				e.style.left = '';
				e.style.right = x + 'px';
			} else {
				e.style.left = x + 'px';
				e.style.right = '';
			}
		}
		if (typeof(y) != 'undefined') {
			if (fromBottom) {
				e.style.top = '';
				e.style.bottom = y + 'px';
			} else {
				e.style.top = y + 'px';
				e.style.bottom = '';
			}
		}
	},

	resizeTo: function(e, w, h) {
		if (typeof(w) != 'undefined')
			e.style.width = w + 'px';
		if (typeof(h) != 'undefined')
			e.style.height = h + 'px';
	},
	
	create: function(parent, tag, id) {
		var e = document.createElement(tag);
		if (id) {
			e.id = id;
		}
		if (parent) {
			parent.appendChild(e);
		}
		return e;
	},
	
	selectText: function(e) {
		if (document.body.createTextRange) {
			var range = document.body.createTextRange();
			range.moveToElementText(e);
			range.select();
		} else if (window.getSelection) {
			var range = document.createRange();
			range.selectNodeContents(e);
			var selection = window.getSelection();            
			selection.removeAllRanges();
			selection.addRange(range);
		}
	},
	
	/* the end needs no comma */
	_end: null
};
