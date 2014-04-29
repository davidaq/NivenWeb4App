var ServerInvoke = function(action, method, send) {
	var data = {
		action : action,
		method : method,
		parameter : JSON.stringify(send)
	};
	this.callback = false;
	this.ajaxParam = {
		type : "POST",
		dataType : "json",
		url : "/page-invoke.act",
		async : false,
		data : data
	};
};
ServerInvoke.prototype.invoke = function() {
	var me = this;
	setTimeout(function() {
		$.ajax(me.ajaxParam);
	}, 10);
	return this;
};
ServerInvoke.prototype.onSuccess = function(callback) {
	this.ajaxParam.success = callback;
	return this;
};