/*
 * Form control
 */

var Forms = {
	
	/* The current AJAX request */
	currentRequest: false,
	
	/* The scheduler for asynchronous requests */
	scheduler: new Scheduler(),
	
	isEnabled: function(e) {
		var el = $(e);
		if (el) {
			return !el.disabled;
		}
		return false;
	},
	
	toggleEnabled: function(e) {
		var el = $(e);
		if (el) {
			el.disabled = !el.disabled;
			return !el.disabled;
		}
		return false;
	},
	
	focusFirstField: function(p) {
		var e;
		if (p) {
			e = p.select('input[type="text"]', 'input[type="password"]', 'input[type="submit"]', 'select', 'textarea').first();
		} else {
			e = $$('input[type="text"]', 'input[type="password"]', 'input[type="submit"]', 'select', 'textarea').first();
		}
		if (e) {
			e.focus();
			e.select();
			return true;
		}
		return false;
	},
	
	getSelection: function(e) {
		var content = e.value;
		var from = e.selectionStart;
		var to = e.selectionEnd;
		
		// normalize
		from -= content.substr(0, from).split("\r\n").length - 1;
		to -= content.substr(0, to).split("\r\n").length - 1;
		content = content.replace(/\r\n/g, "\n");
		
		if (to == from) {
			// extend selection to current paragraph
			var left = content.substr(0, from);
			var m = left.match(/[\s\S]*\n\s*\n/g);
			from = 0;
			if (m) {
				for (var i = 0; i < m.length; i++) {
					from += m[i].length;
				}
			}
			
			var right = content.substr(to);
			var i = right.search(/\n\s*\n/);
			if (i < 0) {
				to = content.length;
			} else {
				to += i;
			}
		}
		
		var ret = {
			content: content,
			start: from,
			end: to
		};
		return ret;
	},
	
	setSelection: function(e, start, end, content) {
		if (content) {
			e.value = content;
		}
		e.selectionStart = start;
		e.selectionEnd = end;
	},
	
	getSelectedText: function(e) {
		var sel = this.getSelection(e);
		
		return sel.content.substring(sel.start, sel.end);
	},
	
	replaceSelectedText: function(e, txt) {
		var sel = this.getSelection(e);
		
		var left = sel.content.substr(0, sel.start);
		var right = sel.content.substr(sel.end);
		var content = left + txt + right;
		
		this.setSelection(e, left.length, left.length + txt.length, content);
		return content;
	},
	
	init: function() {
		// Establish keyboard handlers
		$$('select').each(function(el) {
			el.onkeydown = Forms.selectOnKeypress;
		});
		$$('textarea').each(function(el) {
			el.onkeydown = Forms.textareaOnKeypress;
			if (el.id && $(el.id + '-row') && $(el.id + '-column')) {
				el.onkeyup = Forms.textareaOnKeyup;
				el.onmouseup = Forms.textareaOnKeyup;
			} else {
				el.onkeyup = null;
				el.onmouseup = null;
			}
		});
	},
	
	selectOnKeypress: function(ev) {
		if (ev.keyCode == 13) {
			var f = Event.element(ev).form;
			Forms.submit(f, ev);
			return false;
		}
		return true;
	},
	
	textareaOnKeypress: function(ev) {
		if (ev.keyCode == 13) {
			if (ev.ctrlKey) {
				// submit using Ctrl+Enter (respecting onsubmit)
				var f = Event.element(ev).form;
				Forms.submit(f, ev);
				return false;
			} else if (!ev.altKey && !ev.metaKey) {
				// autoindent
				var el = Event.element(ev);
				var content = el.value;
				var from = el.selectionStart;
				var to = el.selectionEnd;
				var left = content.substr(0, from);
				var right = content.substr(to);
				
				var bol = left.lastIndexOf("\n");
				if (bol < 0) {
					bol = left;
				} else {
					bol = left.substr(bol + 1);
				}
				var m = bol.match(/^(\s*)/);
				if (m) {
					left = left + "\n" + m[1];
				} else {
					left = left + "\n";
				}
				
				el.value = left + right;
				el.selectionStart = left.length;
				el.selectionEnd = left.length;
				return false;
			}
		} else if (ev.keyCode == 9) {
			if (!ev.ctrlKey && !ev.altKey && !ev.metaKey) {
				// indent
				var el = Event.element(ev);
				var content = el.value;
				var from = el.selectionStart;
				var to = el.selectionEnd;
				var left = content.substr(0, from);
				var right = content.substr(to);
				
				left = left + "\t";
				
				el.value = left + right;
				el.selectionStart = left.length;
				el.selectionEnd = left.length;
				return false;
			}
		}
		return true;
	},
	
	textareaOnKeyup: function(ev) {
		var el = Event.element(ev);
		var content = el.value;
		var to = el.selectionEnd;
		
		var lines = content.substr(0, to).split("\n");
		var rownum = lines.length - 1;
		var colnum = lines[rownum].length;
		
		$(el.id + '-row').innerHTML = rownum + 1;
		$(el.id + '-column').innerHTML = colnum + 1;
		
		return true;
	},
	
	filter: function(e, s, r) {
		var el = $(e);
		if (el) {
			// Fix width when removing items
			Elements.resizeTo(el, Elements.getWidth(el));
			var srch = s.toLowerCase();
			var list;
			var n = 0;
			switch (el.tagName) {
				case 'SELECT':
					list = el.options;
					for (var i = 0; i < el.length; i++) {
						var o = list[i];
						if (!s) {
							o.show();
						} else if (o.value && (o.text.toLowerCase().indexOf(srch) < 0)) {
							o.hide();
						} else {
							o.show();
							n++;
						}
					}
					break;
				case 'UL':
				case 'OL':
					list = el.select('li');
					for (var i = 0; i < list.length; i++) {
						var o = list[i];
						if (!s) {
							o.show();
						} else if (o.textContent.toLowerCase().indexOf(srch) < 0) {
							o.hide();
						} else {
							o.show();
							n++;
						}
					}
					break;
				case 'TABLE':
					list = el.select('tbody tr');
					for (var i = 0; i < list.length; i++) {
						var o = list[i];
						if (!s) {
							o.show();
						} else if (o.textContent.toLowerCase().indexOf(srch) < 0) {
							o.hide();
						} else {
							o.show();
							n++;
						}
					}
					break;
			}
			var rel = $(r);
			if (rel) {
				if (n && s) {
					rel.innerHTML = ' ('+n+')';
				} else {
					rel.innerHTML = '';
				}
			}
		}
	},
	
	exchangeFieldContents: function(a, b) {
		var tmp;
		switch (a.type) {
			case 'hidden': {
				var la = $(a.id + '-value');
				var lb = $(b.id + '-value');
				if (la && lb) {
					tmp = la.innerHTML;
					la.innerHTML = lb.innerHTML;
					lb.innerHTML = tmp;
				}
			}
			case 'text':
			case 'password':
			case 'textarea':
				tmp = b.value;
				b.value = a.value;
				a.value = tmp;
				break;
			case 'select-one':
				tmp = b.selectedIndex;
				b.selectedIndex = a.selectedIndex;
				a.selectedIndex = tmp;
				break;
			case 'radio':
			case 'checkbox':
				tmp = b.checked;
				b.checked = a.checked;
				a.checked = tmp;
				break;
		}
	},
	
	exchangeContents: function(a, b) {
		var from = a.select('input', 'select', 'textarea');
		var to = b.select('input', 'select', 'textarea');
		
		for (var i = 0; i < from.length; i++) {
			this.exchangeFieldContents(from[i], to[i]);
		}
	},
	
	click: function(f, ev, no) {
		if (no || !f.onclick || f.onclick(ev)) {
			f.click(ev);
		}
	},
	
	submit: function(f, ev, url, target, no) {
		var a = f.action;
		if (url) {
			f.action = url;
		}
		var t = f.target;
		if (target) {
			f.target = target;
		}
		if (no || !f.onsubmit || f.onsubmit(ev)) {
			f.submit(ev);
		}
		if (url) {
			f.action = a;
		}
		if (target) {
			f.target = t;
		}
	},
	
	reset: function(f, ev, no) {
		if (no || !f.onreset || f.onreset(ev)) {
			f.reset(ev);
		}
	},
	
	disable: function(f) {
		var frm = $(f);
		var s = [];
		frm.getElements().each(function (e) {
			if (!e.disabled) {
				s.push(e);
				e.disable();
			}
		});
		return s;
	},
	
	enable: function(f, s) {
		s.each(function (e) {
			e.enable();
		});
	},
	
	processRequest: function(f, url, h1, h2) {
		var a = f.action;
		if (url) {
			f.action = url;
		}
		var params = f.serialize(true); // serialize doesn't work for disabled forms, so do it here
		var formState = Forms.disable(f);
		
		var taskID = this.scheduler.currentTaskID;
		this.currentRequest = f.request({
			parameters: params,
			asynchronous: true,
			onSuccess: function(response) {
				Forms.currentRequest = false;
				var ct = response.getHeader('Content-type') || '';
				if (ct.match(/^text\/javascript/)) {
					h2 = null; // eval'd by prototype
				} else if (ct.match(/^application\/json/)) {
					if (h1) {
						h1(response.responseJSON); // parsed by prototype
					}
				} else {
					if (h1 && response.responseText) {
						h1(response.responseText);
					}
				}
			},
			onComplete: function() {
				Forms.currentRequest = false;
				Forms.enable(f, formState);
				if (h2) {
					h2();
				}
				Forms.scheduler.complete(taskID);
			}
		});
		
		if (url) {
			f.action = a;
		}
		return false;
	},
	
	submitAsync: function(f, url, h1, h2) {
		this.scheduler.add(this.processRequest.bind(this), f, url, h1, h2);
	},
	
	cancelAsync: function() {
		if (this.currentRequest) {
			this.currentRequest.transport.abort();
			this.currentRequest = false;
		}
	},
	
	/* The end needs no comma */
	_end: null
};
