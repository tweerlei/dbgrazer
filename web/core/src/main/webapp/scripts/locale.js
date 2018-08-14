/*
 * JS texts for default language
 */

var Messages = {
	
	submitFormTitle: '',
	submitFormText: 'Query is running...',
	waitTitle: '',
	waitText: 'Please stand by...',
	
	loginTitle: 'Log in',
	searchTitle: 'Search queries',
	
	deleteQueryText: 'Are you sure you want to delete this query?',
	deleteLinkText: 'Are you sure you want to delete this connection?',
	deleteUserText: 'Are you sure you want to delete this user?',
	
	reloadQueriesText: 'Reload queries?',
	reloadConnectionsText: 'Reload connections?',
	reloadConfigText: 'Reload configuration?',
	
	resetText: 'Are you sure you want to remove all objects?',
	
	imageFailed: 'No data found.',
	
	cancel: 'cancel',
	yes: 'yes',
	no: 'no',
	
	/* the end needs no comma */
	_end: null
};

/*
 * Sortable tables customization
 */

Table.AutoSortTitle = 'sort';

/*
 * Date picker customization
 */

Date.prototype.toFormattedString = function(include_time) {
	str = Date.padded2(this.getMonth()+1) + '/' + Date.padded2(this.getDate()) + '/' + this.getFullYear();
	if (include_time) { str += ' ' + this.getHours() + ':' + this.getPaddedMinutes() + ':00' }
	return str;
};

Date.parseFormattedString = function(string) {
	var regexp = '([0-9]{1,2})/([0-9]{1,2})/([0-9]{2,4})( ([0-9]{1,2})(:([0-9]{1,2})(:([0-9]{1,2}))?)?)?';
	var d = string.match(new RegExp(regexp));
	
	if (d) {
		var date = new Date(d[3], d[1] - 1, d[2]);
		
		if (d[5]) { date.setHours(d[5]); }
		if (d[7]) { date.setMinutes(d[7]); }
		if (d[9]) { date.setSeconds(d[9]); }
		return date;
	} else {
		return new Date();
	}
};
