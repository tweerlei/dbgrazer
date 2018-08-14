/*
 * Maintain the location fragment identifier, supporting multiple attributes to be passed as
 * http://url?query#!attr1=value1&attr2=value2
 */

var HashMonitor = {
	
	lastHash: '',
	values: {},
	listeners: [],
	
	init: function() {
		Event.observe(window, 'hashchange', function(ev) {
			HashMonitor.hashChanged();
		});
		HashMonitor.hashChanged();
	},
	
	addListener: function(l) {
		HashMonitor.listeners.push(l);
//		l(HashMonitor.values);
	},
	
	notifyListeners: function() {
		for (var l = 0; l < HashMonitor.listeners.length; l++) {
			HashMonitor.listeners[l](HashMonitor.values);
		}
	},
	
	hashChanged: function() {
		// some browsers URL-decode window.location.hash, some don't.
		// window.location.href seems to reflect what we set using assign() and replace()
		var h = window.location.href.replace(/^[^#]*/, '');
		if (h != HashMonitor.lastHash) {
			HashMonitor.lastHash = h;
			HashMonitor.values = {};
			if (h.indexOf('#!') == 0) {
				var parts = h.substr(2).split('&');
				for (var i = 0; i < parts.length; i++) {
					var j = parts[i].indexOf('=');
					if (j > 0) {
						var k = parts[i].substr(0, j);
						var v = decodeURIComponent(parts[i].substr(j + 1)).replace(/!4!/g, '#').replace(/!3!/g, '=').replace(/!2!/g, '&').replace(/!1!/g, '!');
						if (v !== '') {
							HashMonitor.values[k] = v;
						}
					}
				}
			}
			HashMonitor.notifyListeners();
		}
	},
	
	updateHash: function(replace) {
		var h = '#!';
		var first = true;
		for (var i in HashMonitor.values) {
			if (first) {
				first = false;
			} else {
				h += '&';
			}
			var tmp = HashMonitor.values[i].replace(/!/g, '!1!').replace(/&/g, '!2!').replace(/=/g, '!3!').replace(/#/g, '!4!');
			h = h + i + '=' + encodeURIComponent(tmp);
		}
		HashMonitor.lastHash = h;
		
		var url = window.location.href.replace(/#.*$/, '');
		if (replace) {
			window.location.replace(url + h);
		} else {
			window.location.assign(url + h);
		}
	},
	
	get: function(n) {
		return HashMonitor.values[n] ? HashMonitor.values[n] : '';
	},
	
	getBoolean: function(n) {
		return (HashMonitor.get(n) === 'true');
	},
	
	getNumber: function(n) {
		return Number(HashMonitor.get(n));
	},
	
	set: function(values, replace) {
		if (typeof(values) === 'object') {
			for (var i in values) {
				if (values[i]) {
					HashMonitor.values[i] = String(values[i]);
				} else {
					delete HashMonitor.values[i];
				}
			}
			HashMonitor.updateHash(replace);
		}
	},
	
	/* The end needs no comma */
	_end: null
};

HashMonitor.init();
