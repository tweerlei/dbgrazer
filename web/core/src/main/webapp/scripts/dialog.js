/*
 * Dialog control, creates an overlay and a floating dialog element:
 * 
 * <div id="overlay"></div>
 * <div id="dialog">
 *   <div id="dialog-title">
 *     <div id="dialog-close"><span class="action">&#x2716;</span></div>
 *     <div id="dialog-name"></div>
 *   </div>
 *   <div id="dialog-content">
 *     <div id="dialog-form"></div>
 *     <div id="dialog-wait"></div>
 *   </div>
 * </div>
 */

var Dialog = {
	overlay: null,
	target: null,
	dialogName: null,
	dialogClose: null,
	dialogForm: null,
	dialogWait: null,
	active: false,
	closeEnabled: true,
	onClose: null,
	
	create: function() {
		if (!this.overlay) {
			var cf = function() {
				Dialog.close();
				return false;
			};
			
			this.overlay = Elements.create(document.body, 'div', 'overlay');
			this.overlay.onclick = cf;
			this.overlay.hide();
			
			this.target = Elements.create(document.body, 'div', 'dialog');
			this.target.hide();
			var title = Elements.create(this.target, 'div', 'dialog-title');
			this.dialogClose = Elements.create(title, 'div', 'dialog-close');
			var link = Elements.create(this.dialogClose, 'span');
			link.className = 'action';
			link.onclick = cf;
			link.innerHTML = '&#x2716;';
			this.dialogName = Elements.create(title, 'div', 'dialog-name');
			var content = Elements.create(this.target, 'div', 'dialog-content');
			this.dialogForm = Elements.create(content, 'div', 'dialog-form');
			this.dialogWait = Elements.create(content, 'div', 'dialog-wait');
			this.dialogWait.innerHTML = '&nbsp;';
			
			Event.observe(window, 'resize', function() {
				Dialog.adjust();
			});
		}
	},
	
	isActive: function() {
		return this.active;
	},
	
	show: function(title, content, cc, dc) {
		if (this.active) {
			return false;
		}
		
		this.create();
		this.dialogName.innerHTML = title || '&nbsp;';
		this.dialogForm.innerHTML = content || '';
		this.overlay.show();
		this.target.show();
		this.onClose = cc;
		this.active = true;
		if (content) {
			this.setWaiting(false);
			if (dc) {
				this.setCloseDisabled(true);
			}
		} else {
			this.setWaiting(true);
		}
		
		Forms.init();
		Forms.focusFirstField(this.target);
		return true;
	},
	
	adjust: function() {
		if (this.active) {
			var ww = Elements.getWindowWidth();
			var wh = Elements.getWindowHeight();
			
			Elements.resizeTo(this.overlay, ww, wh);
			
			var w = Elements.getWidth(this.target);
			var h = Elements.getHeight(this.target);
			Elements.moveTo(this.target, Math.floor((ww - w) / 2), Math.floor((wh - h) / 3));
		}
	},
	
	setWaiting: function(b) {
		if (this.active) {
			if (b) {
				this.dialogForm.hide();
				this.dialogWait.show();
			} else {
				this.dialogForm.show();
				this.dialogWait.hide();
			}
			this.setCloseDisabled(b);
			this.adjust();
		}
	},
	
	setCloseDisabled: function(b) {
		if (this.active) {
			if (b) {
				this.dialogClose.hide();
			} else {
				this.dialogClose.show();
			}
			this.closeEnabled = !b;
		}
	},
	
	setWaitMessage: function(txt) {
		this.create();
		this.dialogWait.innerHTML = txt;
	},
	
	close: function() {
		if (this.active && this.closeEnabled) {
			if (this.onClose) {
				this.onClose();
			}
			this.hide();
		}
	},
	
	hide: function() {
		if (this.active) {
			if (document.activeElement) {
				document.activeElement.blur();
			}
			this.target.hide();
			this.overlay.hide();
			this.dialogForm.innerHTML = '';
			this.active = false;
		}
	},
	
	/* The end needs no comma */
	_end: null
};
