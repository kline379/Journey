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

			if(result.length > 0) {
				var header = document.createElement("h3");
				header.textContent = "Restaurants";
				el.appendChild(header);


				var row = document.createElement("tr");
				
				var nameH = document.createElement("th");
				nameH.textContent = "Name";

				var ratingH = document.createElement("th");
				ratingH.textContent = "Rating";

				row.appendChild(nameH);
				row.appendChild(ratingH);

				el.appendChild(row);
			}

			for(var i = 0; i < result.length; i++) {
				r = result[i];
				var toadd = document.createElement('tr');

				var name = document.createElement('td');
				var anc = document.createElement('a');
				anc.textContent = r["_Name"];
				anc.setAttribute("href", r["_Link"]);
				anc.setAttribute("target", "_blank");
				name.appendChild(anc);
				toadd.appendChild(name);

				var rating = document.createElement('td');
				rating.textContent = r["_Rating"];
				toadd.appendChild(rating);		

				el.appendChild(toadd);
			}
		}, 
		error: function (result) {
		}
	});
}
