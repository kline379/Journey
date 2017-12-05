function addYelp(element) {	
	id = element.getAttribute('data-target-id');
	title = element.getAttribute("title")
	$.ajax({
		type: "POST",
		url: "yelpreview?" + $.param({ location: title }),
        dataType: "json",
		success: function (result) {

			el = document.getElementById("list" + id);
			console.log(result.length)
			
			if (result.length <= 0) {
				var headerElement = document.createElement('li');
				headerElement.textContent = "Sorry, we couldn't find any restaurants nearby!";
				headerElement.className = 'list-group-item';
				el.appendChild(headerElement);
			}

			for (var i = 0; i < result.length; i++) {
				var r = result[i];

				var listElement = document.createElement('li');
				var linkElement = document.createElement('a');
				var spanElement = document.createElement('span');

				linkElement.textContent = r['_Name'];
				spanElement.textContent = '(Rating: ' + r['_Rating'] + '/5)';

				linkElement.href = r['_Link'];
				linkElement.target = '_blank';

				spanElement.className = 'restaurant-rating';

				listElement.appendChild(linkElement);
				listElement.appendChild(spanElement);
				listElement.className = 'list-group-item';

				el.appendChild(listElement);
			}
		}, 
		error: function (result) {
		}
	});
}
