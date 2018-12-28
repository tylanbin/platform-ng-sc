var tree_roles = null;
$(function() {
	$.ajax({
		type : 'get',
		url : 'api/auth/role/tree',
		dataType : 'json',
		async : false,
		success : function(data) {
			tree_roles = data;
		},
		error : function() {
			tree_roles = [];
		}
	});
})

var userEditIndex = undefined;
function endUserEditing() {
	if (userEditIndex == undefined) {
		return true;
	}
	if ($('#dg-user').datagrid('validateRow', userEditIndex)) {
		$('#dg-user').datagrid('endEdit', userEditIndex);
		userEditIndex = undefined;
		return true;
	} else {
		return false;
	}
}
function onClickUserRow(index) {
	if (userEditIndex != index) {
		if (endUserEditing()) {
			$('#dg-user').datagrid('selectRow', index).datagrid('beginEdit', index);
			userEditIndex = index;
		} else {
			$('#dg-user').datagrid('selectRow', userEditIndex);
		}
	}
}
function appendUser() {
	if (endUserEditing()) {
		$('#dg-user').datagrid('appendRow', {
			enabled : 1
		});
		userEditIndex = $('#dg-user').datagrid('getRows').length - 1;
		$('#dg-user').datagrid('selectRow', userEditIndex).datagrid('beginEdit', userEditIndex);
	}
}
function removeUser() {
	if (userEditIndex == undefined) {
		return;
	}
	var row = $('#dg-user').datagrid('getSelected');
	if (row.id) {
		$.messager.confirm('提示', '确定删除已选择的条目？', function(r) {
			if (r) {
				var empId = $('#empId-user').val();
				$.ajax({
					type : 'delete',
					url : 'api/auth/emp/' + empId + '/user/' + row.id,
					dataType : 'json',
					async : true,
					success : function(data) {
						if (data.success) {
							$('#dg-user').datagrid('reload');
							$('#dg-user').datagrid('clearSelections');
						} else {
							$.messager.show({
								title : '错误',
								msg : data.msg,
								showType : 'fade',
								style : {
									right : '',
									bottom : ''
								}
							});
						}
					},
					error : function() {
						$.messager.show({
							title : '错误',
							msg : '服务器正忙，请稍后再试！',
							showType : 'fade',
							style : {
								right : '',
								bottom : ''
							}
						});
					}
				});
			}
		});
	} else {
		$('#dg-user').datagrid('cancelEdit', userEditIndex).datagrid('deleteRow', userEditIndex);
	}
	userEditIndex = undefined;
}

function dlg_user() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要分配的条目！', 'info');
	} else if (rows.length == 1) {
		// 这个地方为了可以获取新的数据，再次获取
		$.ajax({
			type : 'get',
			url : 'api/auth/role/tree',
			dataType : 'json',
			async : false,
			success : function(data) {
				tree_roles = data;
			},
			error : function() {
				tree_roles = [];
			}
		});
		// 载入用户的信息
		var empId = rows[0].id;
		$('#dg-user').datagrid({
			striped : true,
			border : true,
			idField : 'id',
			rownumbers : true,
			fitColumns : true,
			singleSelect : true,
			url : 'api/auth/emp/' + empId + '/user/data',
			method : 'get',
			columns : [[{
						field : 'id',
						title : 'ID'
					}, {
						field : 'loginName',
						width : 50,
						title : '用户名',
						editor : {
							type : 'textbox',
							options : {
								required : true,
								validType : ['loginName']
							}
						}
					}, {
						field : 'loginPass',
						width : 80,
						title : '密码',
						editor : {
							type : 'textbox',
							options : {
								required : true,
								validType : ['loginPass']
							}
						}
					}, {
						field : 'enabled',
						width : 20,
						title : '启用',
						formatter : function(value, row, index) {
							if (value == 1) {
								return '是';
							} else {
								return '否';
							}
						},
						editor : {
							type : 'combobox',
							options : {
								required : true,
								editable : false,
								panelHeight : 'auto',
								data : [{
											text : '是',
											value : 1
										}, {
											text : '否',
											value : -1
										}]
							}
						}
					}, {
						field : 'roleIds',
						width : 80,
						title : '角色',
						formatter : function(value, row, index) {
							var roleNames = '';
							if (value) {
								var roleIds = value.split(",");
								for (var i = 0; i < roleIds.length; i++) {
									$.ajax({
										type : 'get',
										url : 'api/auth/role/' + roleIds[i],
										dataType : 'json',
										async : false,
										success : function(data) {
											if (data.name) {
												roleNames += data.name + ', ';
											}
										}
									});
								}
							}
							return roleNames.substring(0, roleNames.length - 2);
						},
						editor : {
							type : 'combotree',
							options : {
								data : tree_roles,
								required : true,
								editable : false,
								multiple : true,
								cascadeCheck : false,
								onBeforeSelect : function(node) {
									// 不允许select节点，会自动check
									return false;
								},
								onBeforeCheck : function(node) {
									// 机构类型不允许选择
									if (!$.isNumeric(node.id)) {
										$.messager.show({
											title : '提示',
											msg : '不允许选择机构！',
											showType : 'fade'
										});
										return false;
									}
								}
							}
						}
					}, {
						field : 'createDate',
						width : 80,
						title : '创建时间',
						formatter : function(value, row, index) {
							if (value) {
								var date = new Date(value);
								return date.format('yyyy-MM-dd');
							}
						}
					}, {
						field : 'loginRange',
						title : '范围',
						hidden : true
					}]],
			loadMsg : '数据载入中...',
			onLoadError : function() {
				// 该方法会在请求失败后执行
				// 这里使用测试数据填充DataGrid，便于调试页面
				var tmp = [{"id":1,"loginName":"zhangsan","loginPass":"123456","enabled":1,"createDate":1486533755000,"loginRange":null,"roleIds":"2"}];
				$(this).datagrid('loadData', tmp);
			},
			onClickRow : onClickUserRow
		});
		$('#empId-user').val(empId);
		$('#dlg-user').dialog({
			onResize : function() {
				$('#dg-user').datagrid('resize');
			}
		}).dialog('open');
	} else {
		$.messager.alert('提示', '分配时只可以选择一个！', 'info');
	}
}
function func_user() {
	if (endUserEditing()) {
		$('#dg-user').datagrid('acceptChanges');
		// 数据处理
		var data = $('#dg-user').datagrid('getData');
		var empId = $('#empId-user').val();
		$.ajax({
			type : 'post',
			url : 'api/auth/emp/' + empId + '/user',
			data : {
				objs : JSON.stringify(data.rows)
			},
			dataType : 'json',
			async : true,
			success : function(data) {
				if (data.success) {
					$('#dlg-user').dialog('close');
				} else {
					$.messager.show({
						title : '错误',
						msg : data.msg,
						showType : 'fade',
						style : {
							right : '',
							bottom : ''
						}
					});
				}
			},
			error : function() {
				$.messager.show({
					title : '错误',
					msg : '服务器正忙，请稍后再试！',
					showType : 'fade',
					style : {
						right : '',
						bottom : ''
					}
				});
			}
		});
	}
}