var page_opration_count = 0;// 防止页面第一次载入时刷新2次

$(function() {
	var pageIndex = 0; // 页面索引初始值
	var pageSize = 36; // 每页显示条数初始化，修改显示条数，修改这里即可
	var maxentries = 10;
	var URLParams = new Array();
	var aParams = document.location.search.substr(1).split('&');
	for (i = 0; i < aParams.length; i++) {
		var aParam = aParams[i].split('=');
		URLParams[aParam[0]] = aParam[1];
	}
	initTable(pageIndex);// 初始化页面

	// $('#picture-list').dragsort({ dragSelector: ".picture-list-item",
	// dragEnd: function() { }, dragBetween: false, placeHolderTemplate: "<div
	// class='col-md-2 picture-list-item'><img><span></span><div></div></div>"
	// });

	function initPagination(_pageIndex, _pageSize, _maxentries) {
		page_opration_count = 0;
		pageIndex = _pageIndex;
		pageSize = _pageSize;
		maxentries = _maxentries;
		$("#Pagination").pagination(_maxentries, {
			num_edge_entries : 2,
			current_page : _pageIndex,
			num_display_entries : 10,
			link_to : "javascript:;",
			items_per_page : _pageSize,
			prev_text : '上一页', // 上一页按钮里text
			next_text : '下一页',
			callback : PageCallback
		});
	}

	function PageCallback(pageIndex, jq) {
		if (page_opration_count != 0) {// 防止页面刷新的时候初再次初始化
			initTable(pageIndex);
		}
		page_opration_count++;
	}

	function initTable(pageIndex) {// 页面初始化调用
		if (typeof (URLParams['templateclassificationid']) != "undefined") {
			Server.getTemplateListByClassificationID(
					URLParams['templateclassificationid'],
					(pageIndex) * pageSize, pageSize).onSuccess(
					getTemplateListSuccess);
		} else if (typeof (URLParams['musicid']) != "undefined") {
			Server.getTemplateListByMusicID(URLParams['musicid'],
					(pageIndex) * pageSize, pageSize).onSuccess(
					getTemplateListSuccess);
		} else {
			Server.getTemplateList((pageIndex) * pageSize, pageSize).onSuccess(
					getTemplateListSuccess);
		}

		function getTemplateListSuccess(tclist) {
			maxentries = tclist.iTotalRecords;
			var innerhtml = "";
			for (var i = 0; i < tclist.aaData.length; i++) {
				innerhtml += templateClass(tclist.aaData[i].T_ID,
						tclist.aaData[i].T_Title, "", tclist.aaData[i].T_Cover,
						tclist.aaData[i].T_Author, tclist.aaData[i].T_Frequency);
			}
			// $('#picture-list')[0].innerHTML = innerhtml;
			$('#picture-list').html(innerhtml);
			initPagination(pageIndex, pageSize, maxentries);
		}
	}

	function templateClass(id, title, _class, imgsrc, author, frequency) {
		var innerhtml = '<div class="col-md-4 picture-list-item '
				+ _class
				+ '" templateid="'
				+ id
				+ '">'
				+ '<div class="col-md-6 picture-img"'
				+ '">'
				+ '<img class="img-responsive" alt="" src="'
				+ imgsrc
				+ '">'
				+ '<div class="checker" style="display: none"><span><input type="checkbox"></span></div>'
				+ '</div>'
				+ '<div class="col-md-6">'
				+ '<h3><span class="picture-title">'
				+ title
				+ '</span></h3>'
				+ '<p>作者：<span class="picture-author">'
				+ author
				+ '</span></p>'
				+ '<p>使用频率：<span class="picture-count">'
				+ frequency
				+ '</span></p>'
				+ '<button type="button" id="check-item" class="btn default btn-sm">所属专辑</button>'
				+ '<button type="button" id="delete-item" class="btn red btn-sm">删除</button>'
				+ '</div>' + '</div>';
		return innerhtml;
	}

	$('#picture-list').delegate('.picture-list-item', 'hover', function(event) {
		var checker = $(this).find('.checker');
		if (event.type == 'mouseenter') {
			checker.show(100);
		} else {
			if ($(this).find('.checker > span').hasClass('checked')) {
				checker.show(100);
			} else {
				checker.hide(100);

			}
		}
	});

	$('#picture-list ').delegate('.picture-list-item .picture-img', 'click',
			function(event) {
				var checker = $(this).find('.checker > span');
				var img = $(this).find('img');
				if (checker.hasClass('checked')) {
					checker.removeClass('checked');
					img.removeClass('img-thumbnail');
				} else {
					checker.addClass('checked');
					img.addClass('img-thumbnail');
				}
			});

	$('#picture-list').delegate(
			'.picture-title',
			'click',
			function(event) {
				event.stopPropagation();
				var str = $(this).text();
				$(this).html(
						'<input typp="text" class="form-control" value="' + str
								+ '">');
				$(this).find('input').focus();
			});

	$('#picture-list').delegate('.picture-title input', 'click',
			function(event) {
				event.stopPropagation();
			});

	$('#picture-list').delegate('.picture-title input', 'blur',
			function(event) {
				event.stopPropagation();
				var titleStr = $(this).val();
				$(this).parent().html(titleStr);
			});

	$('#picture-list').delegate(
			'.picture-author',
			'click',
			function(event) {
				event.stopPropagation();
				var str = $(this).text();
				$(this).html(
						'<input typp="text" class="form-control" value="' + str
								+ '">');
				$(this).find('input').focus();
			});

	$('#picture-list').delegate('.picture-author input', 'click',
			function(event) {
				event.stopPropagation();
			});

	$('#picture-list').delegate('.picture-author input', 'blur',
			function(event) {
				event.stopPropagation();
				var str = $(this).val();
				$(this).parent().html(str);
			});

	$('#delete-selected')
			.click(
					function(event) {
						var deleteItem = $(
								'#picture-list .picture-list-item .checker > span.checked')
								.parents('.picture-list-item');
						var id = [];
						deleteItem.each(function(index) {
							id[index] = $(this).attr('templateid');
						});
						Server.deleteTemplateByID(id).onSuccess(
								function(result) {

								});
						deleteItem.hide(300, function() {
							$(this).remove();
							initTable(pageIndex);
						});
					});

	$('#picture-list').delegate('button#delete-item', 'click', function(event) {
		event.stopPropagation();
		var deleteItem = $(this).parents('.picture-list-item');

		var id = [];
		deleteItem.each(function(index) {
			id[index] = $(this).attr('templateid');
		});
		console.log(id);
		Server.deleteTemplateByID(id).onSuccess(function(result) {

		});
		deleteItem.hide(300, function() {
			$(this).remove();
			initTable(pageIndex);
		});

	});

	$('#picture-list').delegate('button#check-item', 'click', function(event) {
		event.stopPropagation();

	});

});