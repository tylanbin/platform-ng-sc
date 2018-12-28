var selected = null;
$(function() {
	$('#tree').tree({
		url : 'api/auth/perm/tree',
		method : 'get',
		lines : true,
		onSelect : function(node) {
			var json = '{ "parentId" : ' + node.id + ' }';
			$('#dg-list').datagrid('clearSelections');
			$('#dg-list').datagrid('reload', {
				params : json
			});
			// 保留parentId信息
			$('#pid-add').val(node.id);
			$('#pid-edit').val(node.id);
			$('#pname-edit').val(node.text);
		},
		onLoadSuccess : function(node, data) {
			$(this).tree('collapseAll');
			if (selected) {
				$(this).tree('expandTo', $(this).tree('find', selected).target);
			}
		},
		onLoadError : function() {
			// 该方法会在请求失败后执行
			// 这里使用测试数据填充Tree，便于调试页面
			var tmp = [
			];
			$(this).tree('loadData', tmp);
 		}
	});
	$('#dg-list').datagrid({
		fit : true,
		striped : true,
		border : true,
		idField : 'id',
		rownumbers : true,
		fitColumns : true,
		singleSelect : false,
		pagination : true,
		pageSize : 15,
		pageList : [10, 15, 20],
		url : 'api/auth/perm/data',
		queryParams : {
			params : '{ "parentId" : "isNull" }'
		},
		method : 'get',
		frozenColumns : [[{
					field : 'ck',
					checkbox : true
				}]],
		columns : [[{
					field : 'name',
					title : '权限名称'
				}, {
					field : 'token',
					title : '权限标识'
				}, {
					field : 'url',
					title : '链接地址'
				}]],
		loadMsg : '数据载入中...',
		onLoadError : function() {
			// 该方法会在请求失败后执行
			// 这里使用测试数据填充DataGrid，便于调试页面
			var tmp = [
			];
			$(this).datagrid('loadData', tmp);
		}
	});
	$('#dg-list').datagrid('getPager').pagination({
		beforePageText : '第',
		afterPageText : '页    共 {pages} 页',
		displayMsg : '当前显示 {from} - {to} 条记录    共 {total} 条记录'
	});
});

function search(value, name) {
	$('#dg-list').datagrid('clearSelections');
	$('#dg-list').datagrid('reload', {
		params : '{ "' + name + '" : "' + value + '" }'
	});
}

// 多行添加代码
var editIndex = undefined;
function endEditing() {
	if (editIndex == undefined) {
		return true;
	}
	if ($('#dg-add').datagrid('validateRow', editIndex)) {
		$('#dg-add').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}
function onClickRow(index) {
	if (editIndex != index) {
		if (endEditing()) {
			$('#dg-add').datagrid('selectRow', index).datagrid('beginEdit', index);
			editIndex = index;
		} else {
			$('#dg-add').datagrid('selectRow', editIndex);
		}
	}
}
function appendLine() {
	if (endEditing()) {
		$('#dg-add').datagrid('appendRow', {});
		editIndex = $('#dg-add').datagrid('getRows').length - 1;
		$('#dg-add').datagrid('selectRow', editIndex).datagrid('beginEdit', editIndex);
	}
}
function removeLine() {
	if (editIndex == undefined) {
		return;
	}
	$('#dg-add').datagrid('cancelEdit', editIndex).datagrid('deleteRow', editIndex);
	editIndex = undefined;
}

function dlg_add() {
	$('#dg-add').datagrid({
		striped : true,
		border : true,
		idField : 'id',
		rownumbers : true,
		fitColumns : true,
		singleSelect : true,
		columns : [[{
					field : 'name',
					width : 80,
					title : '权限名称',
					editor : {
						type : 'textbox',
						options : {
							required : true,
							validType : ['length[0, 10]']
						}
					}
				}, {
					field : 'token',
					width : 100,
					title : '权限标识',
					editor : {
						type : 'textbox',
						options : {
							required : true,
							validType : ['length[0, 10]']
						}
					}
				}, {
					field : 'url',
					width : 150,
					title : '链接地址',
					editor : {
						type : 'textbox',
						options : {
							validType : ['length[0, 100]']
						}
					}
				}]],
		loadMsg : '数据载入中...',
		onClickRow : onClickRow
	});
	$('#dlg-add').dialog({
		onResize : function() {
			$('#dg-add').datagrid('resize');
		},
		onClose : function() {
			editIndex = undefined;
			$('#dg-add').datagrid('loadData', {
				total : 0,
				rows : []
			});
		}
	}).dialog('open');
}
function func_add() {
	if (endEditing()) {
		$('#dg-add').datagrid('acceptChanges');
		// 数据处理
		var data = $('#dg-add').datagrid('getData');
		$.ajax({
			type : 'post',
			url : 'api/auth/perm/batch',
			data : {
				parentId : $('#pid-add').val(),
				objs : JSON.stringify(data.rows)
			},
			dataType : 'json',
			async : true,
			success : function(data) {
				if (data.success) {
					var node = $('#tree').tree('getSelected');
					if (node) {
						selected = node.id;
					}
					$('#tree').tree('reload');
					$('#dg-list').datagrid('reload');
					$('#dg-list').datagrid('clearSelections');
					$('#dlg-add').dialog('close');
				} else {
					// 出错也需要重载，避免重复数据
					$('#tree').tree('reload');
					$('#dg-list').datagrid('reload');
					$('#dg-list').datagrid('clearSelections');
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
				// 出错也需要重载，避免重复数据
				$('#tree').tree('reload');
				$('#dg-list').datagrid('reload');
				$('#dg-list').datagrid('clearSelections');
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

function dlg_edit() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要修改的条目！', 'info');
	} else if (rows.length == 1) {
		if (rows[0].perm) {
			$('#pname-edit').val(rows[0].perm.name);
		} else {
			$('#pname-edit').val('无');
		}
		$('#id-edit').val(rows[0].id);
		$('#fm-edit').form('load', rows[0]);
		$('#dlg-edit').dialog('open');
	} else {
		$.messager.alert('提示', '修改条目时只可以选择一个！', 'info');
	}
}
function func_edit() {
	// 非spring boot版本不需要跨域，可以使用easyui form插件
	// 一旦跨域，easyui form原理是提交到一个iframe中，再获取其html，跨域将导致出错
	if ($('#fm-edit').form('validate')) {
		$.ajax({
			type : 'put',
			url : 'api/auth/perm/' + $('#id-edit').val(),
			data : $('#fm-edit').serializeObj(),
			dataType : 'json',
			async : true,
			success : function(data) {
				if (data.success) {
					var node = $('#tree').tree('getSelected');
					if (node) {
						selected = node.id;
					}
					$('#tree').tree('reload');
					$('#dg-list').datagrid('reload');
					$('#dg-list').datagrid('clearSelections');
					$('#dlg-edit').dialog('close');
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

function func_del() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length > 0) {
		$.messager.confirm('提示', '确定删除已选择的条目？', function(r) {
			if (r) {
				var ids = new Array();
				$.each(rows, function(i, row) {
					ids.push(row.id);
				});
				$.ajax({
					type : 'delete',
					url : 'api/auth/perm/batch?ids=' + ids,
					dataType : 'json',
					async : true,
					success : function(data) {
						if (data.success) {
							var node = $('#tree').tree('getSelected');
							if (node) {
								selected = node.id;
							}
							$('#tree').tree('reload');
							$('#dg-list').datagrid('reload');
							$('#dg-list').datagrid('clearSelections');
						} else {
							// 出错也需要重载
							$('#dg-list').datagrid('reload');
							$('#dg-list').datagrid('clearSelections');
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
						// 出错也需要重载
						$('#dg-list').datagrid('reload');
						$('#dg-list').datagrid('clearSelections');
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
		$.messager.alert('提示', '请选择要删除的条目！', 'info');
	}
}

function func_reload() {
	$('#tree').tree('reload');
	$('#searchbox').searchbox('setValue', '');
	$('#dg-list').datagrid('clearSelections');
	$('#dg-list').datagrid('reload', {
		params : '{ "parentId" : "isNull" }'
	});
}