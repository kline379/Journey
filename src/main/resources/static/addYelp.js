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

			if (result.length > 0) {
				var headerElement = document.createElement('li');

				var nameElement = document.createElement('span');
				nameElement.textContent = 'Name';
				nameElement.className = 'restaurant-header';

				var ratingElement = document.createElement('span');
				ratingElement.textContent = 'Rating';
				ratingElement.className = 'rating-header';

				headerElement.appendChild(nameElement);
				headerElement.appendChild(ratingElement);
				headerElement.className = 'list-group-item';

				el.appendChild(headerElement);
			}
			else {
				var headerElement = document.createElement('li');
				headerElement.textContent = "Sorry, we couldn't find any restaurants nearby!";
				headerElement.className = 'list-group-item';
				el.appendChild(headerElement);
			}

			for(var i = 0; i < result.length; i++) {
				var r = result[i];

				var nameElement = document.createElement('span');
				nameElement.className = 'restaurant';
				var linkElement = document.createElement('a')
				linkElement.textContent = r['_Name'];
				linkElement.href = r['_Link'];
				linkElement.target = '_blank';
				nameElement.appendChild(linkElement);

				var ratingElement = document.createElement('span')
				ratingElement.className = 'rating';
				ratingElement.textContent = r['_Rating'];

				var listElement = document.createElement('li');
				listElement.className = 'list-group-item';
				listElement.appendChild(nameElement);
				listElement.appendChild(ratingElement);

				el.appendChild(listElement);
			}
		}, 
		error: function (result) {
		}
	});
}
