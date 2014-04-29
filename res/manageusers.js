var TableEditable = function() {

	return {

		// main function to initiate the module
		init : function() {

			var oTable = $('#sample_editable_1')
					.dataTable(
							{
								"aLengthMenu" : [ [ 5, 15, 20, 50, 100 ],
										[ 5, 15, 20, 50, 100 ] // change per
								// page values
								// here
								],
								// set the initial value
								"iDisplayLength" : 20,
								"bSearchable" : true,
								"bStateSave" : true,
								"bProcessing" : false,
								"bServerSide" : true,
								"sPaginationType" : "bootstrap",
								"oLanguage" : {
									"sLengthMenu" : "每页显示  _MENU_ 条记录",
									"sZeroRecords" : "Nothing found - 没有记录",
									"sInfo" : "显示第  _START_ 条到第  _END_ 条记录,一共  _TOTAL_ 条记录",
									"sInfoEmpty" : "显示0条记录",
									"sInfoFiltered" : "(从 _MAX_ 条数据中检索)",
									"sSearch" : "搜索:",
									"oPaginate" : {
										"sFirst" : "首页",
										"sPrevious" : "上一页",
										"sNext" : "下一页",
										"sLast" : "尾页"
									}

								},
								"fnServerData" : function(sSource, aoData,
										fnCallback) {
									Server.getUser(aoData).onSuccess(
											function(userlist) {
												fnCallback(userlist);
											});
								},
								"sAjaxSource" : "",
								"aoColumnDefs" : [ {

									"bSortable" : false,
									"aTargets" : [ 4, 5 ]
								} ],
								"aoColumns" : [
										{
											"mData" : "U_Username"
										},
										{
											"mData" : "U_Nickname"
										},
										{
											"mData" : "U_Regist_Time"
										},
										{
											"mData" : "U_Active_Minute"
										},
										{
											"fnRender" : function(obj) {
												return '<a class="reset" href="javascript:;">重置密码 </a>';
											}
										},
										{
											"fnRender" : function(obj) {
												return '<a class="delete" href="javascript:;">删除</a>';
											}
										} ]
							});

			jQuery('#sample_editable_1_wrapper .dataTables_filter input')
					.addClass("form-control input-medium input-inline"); // modify
			// table
			// search
			// input
			jQuery('#sample_editable_1_wrapper .dataTables_length select')
					.addClass("form-control input-small"); // modify
			// table
			// per
			// page
			// dropdown
			jQuery('#sample_editable_1_wrapper .dataTables_length select')
					.select2({
						showSearchInput : false
					// hide search box with special css
					// class
					}); // initialize select2 dropdown

			var nEditing = null;

			$('#sample_editable_1 a.delete').live('click', function(e) {
				e.preventDefault();
				var nRow = $(this).parents('tr')[0];
				var userid = $(nRow).children('td')[0].innerHTML;
				var useremail = $(nRow).children('td')[1].innerHTML;
				if (confirm("你确定要删除用户 " + useremail + "?") == false) {
					return;
				}

				Server.deleteUserByID(userid);
				oTable.fnDeleteRow(nRow);
			});

			$('#add_user #save').live(
					'click',
					function(e) {
						e.preventDefault();
						var username = $('#add_user #username').val();
						var password = $('#add_user #password').val();
						var nickname = $('#add_user #nickname').val();
						var headportrait = $('#add_user #headportrait').val();
						if (username == "" || password == "") {
							App.alert({
								container : $('#add_user .modal-body'), // alerts
								// parent
								// container(by
								// default
								// placed after
								// the page
								// breadcrumbs)
								place : "prepent", // append or prepent in
								// container
								type : "info", // alert's type
								message : "邮箱和密码不能为空！", // alert's message
								close : true, // make alert closable
								reset : true, // close all previouse alerts
								// first
								focus : false, // auto scroll to the alert
							// after shown
							});
						} else {
							Server.addUser(username, nickname, password,
									headportrait).onSuccess(function(result) {
								if (result == 1) {
									App.alert({
										place : "prepent", // append or prepent
										// in
										// container
										type : "success", // alert's type
										message : username + "添加成功！", // alert's
										// message
										close : true, // make alert closable
										reset : true, // close all previouse
										// alerts first
										focus : false, // auto scroll to the
									// alert after
									// shown
									});
									oTable.fnDraw();
								} else {

									App.alert({
										place : "prepent", // append or prepent
										// in
										// container
										type : "danger", // alert's type
										message : username + "添加失败！", // alert's
																		// message
										close : true, // make alert closable
										reset : true, // close all previouse
										// alerts first
										focus : false, // auto scroll to the
									// alert after
									// shown
									});
								}
								$('#add_user').modal('hide');
							});

						}

					});

			$('#sample_editable_1 a.reset').live('click', function(e) {
				e.preventDefault();
				var nRow = $(this).parents('tr')[0];
				var userid = $(nRow).children('td')[0].innerHTML;
				var useremail = $(nRow).children('td')[1].innerHTML;
				bootbox.dialog({
					message : "确定要充值密码并发邮件给该用户吗？",
					title : "重置密码确认",
					buttons : {
						success : {
							label : "发送",
							className : "green",
							callback : function() {
								Server.resetPassword(useremail);
								alert("已经发送邮件给用户");
							}
						},
						danger : {
							label : "取消",
							className : "red",
							callback : function() {
							}
						}
					}
				});
			});


			$('#sample_editable_1').delegate(
					'.odd,.even',
					'click',
					function(event) {
						var nTr = this;
						if (oTable.fnIsOpen(nTr)) {
							/* This row is already open - close it */
							$(this).addClass("row-details-close").removeClass(
									"row-details-open");
							oTable.fnClose(nTr);
						} else {
							/* Open this row */
							$(this).addClass("row-details-open").removeClass(
									"row-details-close");
							oTable.fnOpen(nTr, fnFormatDetails(oTable, nTr),
									'details');
						}
					});
			function fnFormatDetails(oTable, nTr) {
				var aData = oTable.fnGetData(nTr);
				var sOut = '<table>';
				sOut += '<tr><td>用户ID:</td><td>' + aData.U_ID + '</td></tr>';
				sOut += '<tr><td>昵称:</td><td>' + aData.U_Nickname
						+ '</td></tr>';
				sOut += '<tr><td>注册时间:</td><td>' + aData.U_Regist_Time
						+ '</td></tr>';
				sOut += '<tr><td>活跃时间累计(分钟):</td><td>' + aData.U_Active_Minute
						+ '</td></tr>';
				sOut += '<tr><td>头像:</td><td><img class="head-portrait" src="'
						+ aData.U_Head_Portrait + '"></td></tr>';
				sOut += '</table>';

				return sOut;
			}
		}

	};

}();