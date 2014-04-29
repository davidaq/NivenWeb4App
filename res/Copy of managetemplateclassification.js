var page_opration_count = 0;// 防止页面第一次载入时刷新2次

$(function() {
	var pageIndex = 0; // 页面索引初始值
	var pageSize = 10; // 每页显示条数初始化，修改显示条数，修改这里即可
	var maxentries = 10;
	initTable(pageIndex);// 初始化页面

	function initPagination(pageIndex, pageSize, maxentries) {
		page_opration_count = 0;
		$("#Pagination").pagination(maxentries, {
			num_edge_entries : 2,
			current_page : pageIndex,
			num_display_entries : 10,
			link_to : "javascript:;",
			items_per_page : pageSize,
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
		Server.getTemplateClassification((pageIndex) * pageSize, pageSize)
				.onSuccess(
						function(tclist) {
							maxentries = tclist.iTotalRecords;
							var innerhtml = "";
							for (var i = 0; i < tclist.aaData.length; i++) {
								innerhtml += templateClass(
										tclist.aaData[i].TC_ID,
										tclist.aaData[i].TC_Title,
										"category_1 mix_all",
										tclist.aaData[i].TC_Cover);
							}
							$('#template-list')[0].innerHTML = innerhtml;
							// $('#template-list').html(innerhtml);
							initPagination(pageIndex, pageSize, maxentries);
						});
	}

	function templateClass(id, title, _class, imgsrc) {
		var innerhtml = '<div class="col-md-3 col-sm-4 mix '
				+ _class
				+ '"style="  display: block; opacity: 1;">'
				+ '	<div class="mix-inner">'
				+ '		<div class="mix-title"><span>'
				+ title
				+ '</span></div>'
				+ '		<img class="img-responsive" src="'
				+ imgsrc
				+ '"alt="">'
				+ '		<div class="mix-details">'
				+ '			<h4>您要对该模板做什么操作？</h4>'
				+ '<a class="mix-link" value="'
				+ id
				+ '"> <i class="fa fa-times-circle"></i></a>'
				+ '<a class="mix-preview fancybox-button"'
				+ '	href="assets/img/works/img4.jpg"'
				+ '	tppabs="http://www.keenthemes.com/preview/metronic_admin/assets/img/works/img4.jpg"'
				+ '	title="Project Name" data-rel="fancybox-button"> <i	class="fa fa-edit"></i>'
				+ '</a>' + '</div>' + '</div>' + '</div>';
		return innerhtml;
	}
});