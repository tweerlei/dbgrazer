/*
 * JS texts for language 'de'
 */

var Messages = {
	
	submitFormTitle: '',
	submitFormText: 'Abfrage läuft...',
	waitTitle: '',
	waitText: 'Bitte einen Moment Geduld...',
	
	loginTitle: 'Anmelden',
	searchTitle: 'Abfragen suchen',
	
	imageFailed: 'Keine Daten gefunden.',
	
	cancel: 'abbrechen',
	yes: 'ja',
	no: 'nein',
	
	/* the end needs no comma */
	_end: null
};

/*
 * Sortable tables customization
 */

Table.AutoSortTitle = 'sortieren';

Sort.numeric = Sort.numeric_comma;

/*
 * Date picker customization
 */

Date.weekdays = $w('Mo Di Mi Do Fr Sa So');
Date.months = $w('Januar Februar März April Mai Juni Juli August September Oktober November Dezember');
Date.first_day_of_week = 1;

Date.prototype.getAMPMHour = function() { return this.getHours(); };
Date.prototype.getAMPM = function() { return ''; };

Date.prototype.toFormattedString = function(include_time) {
	str = Date.padded2(this.getDate()) + '.' + Date.padded2(this.getMonth()+1) + '.' + this.getFullYear();
	if (include_time) { str += ' ' + Date.padded2(this.getHours()) + ':' + this.getPaddedMinutes() + ':00' }
	return str;
};

Date.parseFormattedString = function(string) {
	var regexp = '([0-9]{1,2})\.([0-9]{1,2})\.([0-9]{2,4})( ([0-9]{1,2})(:([0-9]{1,2})(:([0-9]{1,2}))?)?)?';
	var d = string.match(new RegExp(regexp));
	
	if (d) {
		var date = new Date(d[3], d[2] - 1, d[1]);
		
		if (d[5]) { date.setHours(d[5]); }
		if (d[7]) { date.setMinutes(d[7]); }
		if (d[9]) { date.setSeconds(d[9]); }
		return date;
	} else {
		return new Date();
	}
};

CalendarDateSelect.prototype._translations = {
  "OK": "OK",
  "Now": "jetzt",
  "Today": "heute",
  "Clear": "löschen"
};
