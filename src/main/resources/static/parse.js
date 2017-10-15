function parse(element) {
  const modalId = element.getAttribute('data-target-id');
  const modal = document.getElementById(modalId);
  const descriptionNode = modal.getElementsByClassName("description")[0];
  const description = descriptionNode.innerHTML;
  const parsed = wtf.plaintext(description);
  const startPos = (parsed.charAt(0) == '[')? 1 : 0;
  const stopPos = parsed.indexOf('\n');
  descriptionNode.innerHTML = parsed.substring(startPos, stopPos);
}
