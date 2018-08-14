function addToFavorites(ev, q) {
	Event.stop(ev);
	var el = Event.element(ev);
	WSApi.getDBAsync('add-favorite', { q: q }, function(txt) {
		el.innerHTML = '&#x2605;';
		el.onclick = function(e) {
			return removeFromFavorites(e, q);
		};
	});
	return false;
}

function removeFromFavorites(ev, q) {
	Event.stop(ev);
	var el = Event.element(ev);
	WSApi.getDBAsync('remove-favorite', { q: q }, function(txt) {
		el.innerHTML = '&#x2606;';
		el.onclick = function(e) {
			return addToFavorites(e, q);
		};
	});
	return false;
}
