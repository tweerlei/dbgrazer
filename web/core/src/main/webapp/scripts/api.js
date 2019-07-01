/*
 * AJAX interface to the application backend
 */

var WSApi = {
	
	/* The current DB name */
	currentDB: null,
	
	/* The logged in user */
	currentUser: null,
	
	/* The AJAX pre request callback */
	beforeRequest: false,
	
	/* The AJAX post request callback */
	afterRequest: false,
	
	/* The AJAX error callback */
	onRequestFailure: false,
	
	/* The current AJAX request */
	currentRequest: false,
	
	/* The scheduler for asynchronous requests */
	scheduler: new Scheduler(),
	
	/*
	 * Generic functions
	 */
	
	processRequest: function(method, url, params, succ, bypass) {
		if (!bypass && Object.isFunction(this.beforeRequest) && !this.beforeRequest()) {
			return true;
		}
		
		var taskID = this.scheduler.currentTaskID;
		this.currentRequest = new Ajax.Request(url, {
			method: method,
			parameters: params,
			asynchronous: true,
			onSuccess: function(response) {
				WSApi.currentRequest = false;
				if (!bypass && Object.isFunction(WSApi.afterRequest)) {
					WSApi.afterRequest(response);
				}
				var ct = response.getHeader('Content-type') || '';
				if (ct.match(/^text\/javascript/)) {
					return; // eval'd by prototype
				} else if (ct.match(/^application\/json/)) {
					succ(response.responseJSON); // parsed by prototype
				} else {
					succ(response.responseText);
				}
			},
			onFailure: function(response) {
				WSApi.currentRequest = false;
				if (!bypass && Object.isFunction(WSApi.afterRequest)) {
					WSApi.afterRequest(response);
				}
				if (!bypass && Object.isFunction(WSApi.onRequestFailure)) {
					WSApi.onRequestFailure(response.responseText || response.statusText);
				}
			},
			on0: function(response) {
				WSApi.currentRequest = false;
				if (!bypass && Object.isFunction(WSApi.afterRequest)) {
					WSApi.afterRequest(response);
				}
				if (!bypass && Object.isFunction(WSApi.onRequestFailure)) {
					WSApi.onRequestFailure('Server not reachable');
				}
			},
			onComplete: function() {
				WSApi.currentRequest = false;
				WSApi.scheduler.complete(taskID);
			}
		});
		
		return false;
	},
	
	requestAsync: function(method, url, params, succ, bypass) {
		this.scheduler.add(this.processRequest.bind(this), method, url, params, succ, bypass);
		return true;
	},
	
	cancelAsync: function() {
		if (this.currentRequest) {
			this.currentRequest.transport.abort();
			this.currentRequest = false;
		}
	},
	
	getAsync: function(url, params, cb, bypass) {
		return this.requestAsync('get', 'ajax/' + encodeURIComponent(url) + '.html', params, cb, bypass);
	},
	
	getDBAsync: function(url, params, cb, bypass) {
		if (!this.currentDB) {
			return false;
		}
		return this.requestAsync('get', 'db/' + this.currentDB + '/ajax/' + encodeURIComponent(url) + '.html', params, cb, bypass);
	},
	
	postAsync: function(url, params, cb, bypass) {
		return this.requestAsync('post', 'ajax/' + encodeURIComponent(url) + '.html', params, cb, bypass);
	},
	
	postDBAsync: function(url, params, cb, bypass) {
		if (!this.currentDB) {
			return false;
		}
		return this.requestAsync('post', 'db/' + this.currentDB + '/ajax/' + encodeURIComponent(url) + '.html', params, cb, bypass);
	},
	
	/* the end needs no comma */
	_end: null
};
