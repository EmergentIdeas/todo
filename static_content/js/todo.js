function todoEditUrl(actionNode, focusNode) {
	return webAppContextPath + "/list-item/" + actionNode.attr('data-id') + "/edit";
}

function todoDeleteUrl(actionNode, focusNode) {
	return webAppContextPath + "/list-item/" + actionNode.attr('data-id') + "/delete";
}

function todoCreateUrl(itemType, parentId) {
	return webAppContextPath + "/list/" + parentId + "/item/create";
}


$(function() {
	urlCreator.registerEdit('todo-item', todoEditUrl);
	urlCreator.registerDelete('todo-item', todoDeleteUrl);
	urlCreator.registerCreate('todo-item', todoCreateUrl);
	urlCreator.registerParentSelector('todo-item', '#listItems');
	
	var listItems = $('#listItems');
	listItems.on('click', '.icon-remove', deleteActionItem);
	listItems.on('click', '.icon-edit', editActionItem);
	
});

