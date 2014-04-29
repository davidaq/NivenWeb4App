var page_opration_count = 0;// 防止页面第一次载入时刷新2次
var listdatas = [];
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
		var id = URLParams['id'];
		if (typeof (id) == "undefined") {
			id = null;
		}
		Server.getMusic(id, (pageIndex) * pageSize, pageSize).onSuccess(
				function(list) {
					maxentries = list.iTotalRecords;
					var innerhtml = "";
					for (var i = 0; i < list.aaData.length; i++) {
						listdatas[list.aaData[i].M_ID] = list.aaData[i]
						innerhtml += musicClass(list.aaData[i].M_ID,
								list.aaData[i].M_Title, "",
								list.aaData[i].M_Cover,
								list.aaData[i].M_Artists,
								list.aaData[i].M_Frequency);
					}
					// $('#picture-list')[0].innerHTML = innerhtml;
					$('#picture-list').html(innerhtml);
					initPagination(pageIndex, pageSize, maxentries);
				});
	}

	function musicClass(id, title, _class, imgsrc, author, frequency) {
		var innerhtml = '<div class="col-md-4 picture-list-item '
				+ _class
				+ '" musicid="'
				+ id
				+ '">'
				+ '<div class="col-md-6 picture-img"'
				+ '">'
				+ '<img class="img-responsive" alt="" src="'
				+ imgsrc
				+ '">'
				+ '<div class="checker" style="display: none"><span><input type="checkbox"></span></div>'
				+ '</div>'
				+ '<div class="col-md-6 picture-infomation">'
				+ '<h3><span class="picture-title">'
				+ title
				+ '</span></h3>'
				+ '<p>作者：<span class="picture-author">'
				+ author
				+ '</span></p>'
				+ '<p>使用频率：<span class="picture-count">'
				+ frequency
				+ '</span></p>'
				+ '<a id="check-item" class="btn default btn-sm" href="managetemplate.html?musicid='
				+ id
				+ '">所属专辑</a>'
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
			'.picture-infomation h3,.picture-infomation p',
			'click',
			function(event) {
				var id = $(this).parents('.picture-list-item').attr('musicid');
				var checkinfo = $('#check-info');
				checkinfo.find('.check-id').val(listdatas[id].M_ID);
				checkinfo.find('.check-title').val(listdatas[id].M_Title);
				checkinfo.find('.check-author').val(listdatas[id].M_Artists);
				checkinfo.find('.check-cover').val(listdatas[id].M_Cover);
				checkinfo.find('.check-frequency').val(
						listdatas[id].M_Frequency);
				checkinfo.modal();
			});

	$(document).delegate(
			'#check-info .check-save',
			'click',
			function(event) {
				var checkinfo = $('#check-info');
				var id = checkinfo.find('.check-id').val();
				var title = checkinfo.find('.check-title').val();
				var author = checkinfo.find('.check-author').val();
				var cover = checkinfo.find('.check-cover').val();
				Server.editMusicByID(id, title, author, cover).onSuccess(
						function(result) {
							initTable(pageIndex);
						});
				$('#check-info').modal('hide');
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
						Server.deleteMusicByIDs(id).onSuccess(function(result) {

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
		Server.deleteMusicByIDs(id).onSuccess(function(result) {

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