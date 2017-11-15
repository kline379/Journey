function addYelp(element) {	
	$.ajax({
		type: "POST",
		url: "yelpreview",
		data: "moon",
        dataType: "json",
		success: function (result) {
			const modalId = element.getAttribute('data-target-id');
			const modal = document.getElementById(modalId);
		    const modalBodyNode = modal.getElementsByClassName("modal-body")[0];
		    const cardText = modalBodyNode.innerHTML;
			cardText += "<p>hello!</p>"
		}, 
		error: function (result) {
		}
	});
	/*
  const modalId = element.getAttribute('data-target-id');
  const modal = document.getElementById(modalId);
  const descriptionNode = modal.getElementsByClassName("description")[0];
  const description = descriptionNode.innerHTML;
  const parsed = wtf.plaintext(description);
  const startPos = (parsed.charAt(0) == '[')? 1 : 0;
  const stopPos = parsed.indexOf('\n');
  descriptionNode.innerHTML = parsed.substring(startPos, stopPos);
  */
}
