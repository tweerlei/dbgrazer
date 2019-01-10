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
					WSApi.onRequestFailure(response.responseText);
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
	
	/*
	 * API functions
	 */
	
	login: function(u, p, cb) {
		return this.postAsync('login', { username: u, password: p }, cb);
	},
	
	setTheme: function(t, cb) {
		return this.getAsync('theme', { q: t }, cb);
	},
	
	setLocale: function(t, cb) {
		return this.getAsync('locale', { q: t }, cb);
	},
	
	setTimezone: function(t, cb) {
		return this.getAsync('timezone', { q: t }, cb);
	},
	
	setGraphType: function(q, t, s, v, cb) {
		return this.getDBAsync('graphtype', { q: q, t: t, s: s, v: v }, cb);
	},
	
	toggleEditMode: function(cb) {
		return this.getAsync('editmode', null, cb);
	},
	
	toggleFormatter: function(q, v, cb) {
		return this.getDBAsync('formatter', { q: q, v: v }, cb);
	},
	
	toggleFormatMode: function(q, v, cb) {
		return this.getDBAsync('formatmode', { q: q, v: (v ? 'true' : 'false') }, cb);
	},
	
	toggleColoringMode: function(q, v, cb) {
		return this.getDBAsync('coloringmode', { q: q, v: (v ? 'true' : 'false') }, cb);
	},
	
	toggleLineNumberMode: function(q, v, cb) {
		return this.getDBAsync('linemode', { q: q, v: (v ? 'true' : 'false') }, cb);
	},
	
	toggleTrimColumnsMode: function(q, v, cb) {
		return this.getDBAsync('trimcols', { q: q, v: (v ? 'true' : 'false') }, cb);
	},
	
	getTreeItem: function(query, level, label, left, target, params, cb) {
		var p = {
			q: query,
			level: level,
			label: label,
			left: left,
			target: target
		};
		
		for (var i = 0; i < params.length; i++)
			p['params['+i+']'] = params[i];
		
		return this.getDBAsync('tree', p, cb);
	},
	
	getTargets: function(param, args, target, cb) {
		return this.getDBAsync('targets', 'p='+param+args+'&target='+target, cb);
	},
	
	getProgress: function(cb) {
		return this.getAsync('progress', null, cb, true);
	},
	
	cancelTasks: function(cb) {
		return this.getAsync('cancel', null, cb, true);
	},
	
	formatText: function(stmt, fmt, cb) {
		return this.postDBAsync('formattext', { statement: stmt, format: fmt }, cb);
	},
	
	loadCustomQuery: function(name, cb) {
		return this.postDBAsync('query-load', { q: name }, cb);
	},
	
	deleteCustomQuery: function(name, cb) {
		return this.postDBAsync('query-delete', { q: name }, cb);
	},
	
	saveCustomQuery: function(name, statement, cb) {
		return this.postDBAsync('query-save', { q: name, statement: statement }, cb);
	},
	
	loadCustomQueryHistory: function(index, cb) {
		return this.postDBAsync('submit-history', { q: index }, cb);
	},
	
	resetTimechart: function(cb) {
		return this.getDBAsync('timechart-reset', null, cb);
	},
	
	clearCache: function(cb) {
		return this.getDBAsync('metadata-reset', null, cb);
	},
	
	getSearchResult: function(q, cb) {
		return this.getDBAsync('search-result', { q: q }, cb);
	},
	
	getQueryResult: function(query, param, cb) {
		return this.getDBAsync('result', 'q='+query+param, cb);
	},
	
	getDrilldownResult: function(level, query, param, cb) {
		return this.getDBAsync('drilldown', 'level='+level+'&q='+query+param, cb);
	},
	
	getQueryLevel: function(query, level, params, target, cb) {
		var p = {
			q: query,
			level: level,
			target: target
		};
		
		for (var i = 0; i < params.length; i++)
			p['params['+i+']'] = params[i];
		
		return this.getDBAsync('multilevel', p, cb);
	},
	
	getMenuRows: function(n, cb) {
		return this.getAsync('menurows', { q: n }, cb);
	},
	
	/* the end needs no comma */
	_end: null
};
