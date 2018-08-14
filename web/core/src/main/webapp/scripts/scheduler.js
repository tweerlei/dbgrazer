/*
 * Schedule jobs for execution
 * 
 * function func1(a, b, c) {
 *   return true;	// task completed
 * }
 * 
 * var sch = new Scheduler();
 * var taskID;
 * 
 * function func2(a, b) {
 *   taskID = sch.currentTaskID;	// remember the current task's ID
 *   return false;	// task not completed
 * }
 * 
 * sch.add(func1, 1, 'test', 2);	// Schedule execution of func1 as soon as possible
 * sch.add(func2, 1, 'test');		// Schedule execution of func2 after execution of func1
 * sch.add(func1, 2, 'test', 3);	// Schedule another execution of func1 after completing func2
 * 
 * // now, func1 will be called once (1, 'test', 2), then func2. Since func2 returs false,
 * // scheduling will pause and func1 will NOT be called a second time. 
 * 
 * sch.complete(taskID);			// signal asynchronous task completion
 * 
 * // the func2 task is completed now and func1 will be called the second time (2, 'test', 3).
 */

var Scheduler = Class.create({
	
	initialize: function() {
		this.tasks = [];
		this.nextID = 0;
		this.currentTaskID = false;
	},
	
	add: function() {
		var a = $A(arguments);
		var t = a.shift();
		this.nextID++;
		if (this.currentTaskID) {
			this.tasks.unshift({ id: this.nextID, callback: t, args: a });
		} else {
			this.tasks.push({ id: this.nextID, callback: t, args: a });
		}
		
		if (!this.currentTaskID && (this.tasks.length == 1)) {
			this.currentTaskID = -1;
			this.complete(this.currentTaskID);
		}
	},
	
	complete: function(taskID) {
		if (this.currentTaskID && (taskID == this.currentTaskID)) {
			this.currentTaskID = false;
			this.execute.bind(this).defer();
		}
	},
	
	execute: function() {
		if (!this.currentTaskID && (this.tasks.length > 0)) {
			var t = this.tasks.shift();
			this.currentTaskID = t.id;
			if (t.callback.apply(this, t.args)) {
				this.complete(this.currentTaskID);
			}
		}
	},
	
	cancel: function() {
		this.tasks = [];
		this.currentTaskID = false;
	}
});
