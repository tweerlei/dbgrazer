/*
 * Generic timer
 * 
 * function func(a, b, c) {
 *   do something;
 * }
 * 
 * var t1 = new Timer(func);		// create a timer that will execute func
 * t1.start(1000, 1, 'test', 2);	// schedule execution of func with arguments 1, 'test', 2 in 1000 msec
 * t1.start(2000, 2, 'test', 3);	// re-schedule execution with different args in 2000 msec, func will NOT be called after 1000 msec
 * t1.stop();						// cancel execution if called before the timeout expires 
 */

var Timer = Class.create({
	
	initialize: function(f) {
		this.active = false;
		this.callback = f;
	},
	
	start: function() {
		this.stop();
		this.args = $A(arguments);
		var t = this.args.shift();
		this.id = window.setTimeout(this.execute.bind(this), t);
		this.active = true;
	},
	
	stop: function() {
		if (this.active) {
			window.clearTimeout(this.id);
			this.active = false;
		}
	},
	
	execute: function() {
		this.active = false;
		this.callback.apply(this, this.args);
	}
});
