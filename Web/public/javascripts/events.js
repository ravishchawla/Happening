$(document).ready(function() {

	//$('#events-feed').load('html/eventbox.html #events-root-box');
	//$('#events-feed').load('html/eventbox.html');
	var query = "/events/eventlist";


	$('.dropdown-profile').hover(function() {
		$(this).find('.dropdown-menu').first().stop(true, true).slideDown(150);
	}, function() {
		$(this).find('.dropdown-menu').first().stop(true, true).slideUp(105);
	});

	loaddata(query, true);

	//http://stackoverflow.com/questions/9709841/dynamic-search-results-on-keystroke
	$(".search").keyup(function() {
		var val = $.trim(this.value);

		if (val === "") {
			$(".feed-box").show();
		} else {
			console.log(val);
			$(".feed-box").hide();
			$(".feed-box").has("p:contains(" + val + ")").show();
		}
	});

});



loadcity = function(city) {
	console.log('reaload with ' + window.parent.cities[city]);
	query = "/events/eventlist/city/" + window.parent.cities[city];

	loaddata(query, false);
}

loadcities = function() {

	console.log(window.parent.cities);
	window.parent.cities.forEach(function(val) {
		var func = "loadcity('" + val + "')";

		window.parent.cities[key] = val;
		var listitem = '<li><a onClick="loadcity(' + key + ')">' + val + '</a></li>';

		$(".cities-dropdown-list").append(listitem);
	});
}

loaddata = function(query, populate) {

	$.getJSON(query, function(data) {

		$('#events-feed').empty();
		if (populate) {
			$(".cities-dropdown-list").empty();
			window.parent.cities = [];
		}

		var counter = 0;
		$.each(data, function(key, val) {
			$.get("html/eventbox.html", function(htmldata) {


				var mapsLocation = "https://www.google.com/maps?q=" + val.location;
				//var mapsID = val.eventname.replace(/ /g, "-");
				var mapsID = "modal" + Math.floor(Math.random() * 1000000000001);
				var dom = $(htmldata);

				if (populate && (window.parent.cities.indexOf(val.city) == -1)) {
					window.parent.cities[counter] = val.city;
					var listitem = '<li><a onClick="loadcity(' + counter + ')">' + val.city + '</a></li>';
					$(".cities-dropdown-list").append(listitem);
					counter++;
				}
				//console.log(window.parent.cities);
				dom.find(".modal-box").attr("id", mapsID);
				dom.find(".modal-toggle-image").attr("data-target", "#" + mapsID);
				dom.find(".event_title").html(val.eventname);
				dom.find(".modal-title").html(val.eventname);
				dom.find(".modal-body").html(val.description);
				dom.find(".modal-maps").attr("href", mapsLocation);
				dom.find(".event_location").html(val.city);
				dom.find(".event_location").attr("href", mapsLocation);
				dom.find(".event_date").html(val.date + " " + val.time);
				dom.find(".modal-date").html(val.date + " " + val.time);


				//console.log(htmldata);
				$('#events-feed').append(dom);
			});
		});
	});
}

locateme = function() {
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			alert("Coming Soon!");
		});
	} else {

	}
}

logout = function() {
	var remove = $.removeCookie('token');
	console.log(remove);
	remove = $.removeCookie('username');
	console.log(remove);
	location.href = "/";
}