/*
 * Generic keyboard handling
 *
 * Usage:
 *
 * KeyboardHandler.addHandler('E', KeyboardHandler.ALT | KeyboardHandler.SHIFT, function(ev) {
 *   // handle key event for Alt-Shift-E
 *   // ev is the DOM event object
 * });
 */

var KeyboardHandler = {
	
	/* modifiers */
	NORMAL: 0x0000000,
	SHIFT:  0x1000000,
	CTRL:   0x2000000,
	ALT:    0x4000000,
	META:   0x8000000,
	
	handlers: null,
	
	keyDown: function(e) {
		var ix = 'k' + (e.keyCode | (e.shiftKey ? this.SHIFT : this.NORMAL) | (e.ctrlKey ? this.CTRL : this.NORMAL) | (e.altKey ? this.ALT : this.NORMAL) | (e.metaKey ? this.META : this.NORMAL));
		var h = this.handlers[ix];
		if (h && !h(e)) {
			Event.stop(e);
			return false;
		}
		return true;
	},
	
	addHandler: function(code, modifier, handler) {
		if (!this.handlers) {
			this.handlers = [];
			document.observe('keydown', function(e) {
				return KeyboardHandler.keyDown(e);
			});
		}
		var keyCode = (typeof code == 'string') ? code.charCodeAt(0) : code;
		this.handlers['k' + (keyCode|modifier)] = handler;
	},
	
	/* The end needs no comma */
	_end: null
};
