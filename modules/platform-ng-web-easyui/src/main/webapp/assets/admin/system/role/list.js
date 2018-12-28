var selected = null;
$(function() {
	$('#tree').tree({
		url : 'api/sys/org/tree',
		method : 'get',
		lines : true,
		formatter : function(node) {
			var text = node.text;
			$.ajax({
				type : 'get',
				url : 'api/auth/role/data',
				data : {
					page : 1,
					rows : 0,
					params : '{ "orgId" : ' + node.id + ' }'
				},
				dataType : 'json',
				async : false,
				success : function(data) {
					text += '&nbsp;<span style=\'color:blue\'>(' + data.total + ')</span>';
				},
				error : function() {
					text += '&nbsp;<span style=\'color:blue\'>(0)</span>';
 				}
			});
			return text;
		},
		onSelect : function(node) {
			var json = '{ "orgId" : ' + node.id + ' }';
			$('#dg-list').datagrid('clearSelections');
			$('#dg-list').datagrid('reload', {
				params : json
			});
			// 保留parentId信息
			$('#orgId-add').val(node.id);
			$('#orgId-edit').val(node.id);
			$('#orgName-edit').val(node.text);
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
				{"id":1,"name":"示例公司","text":"示例公司","serialNum":"demo","workPlace":"","contact":"","leader":"","children":[
					{"id":2,"name":"市场部","text":"市场部","serialNum":"demo-1","workPlace":"","contact":"","leader":"","children":[]},
					{"id":3,"name":"行政部","text":"行政部","serialNum":"demo-2","workPlace":"","contact":"","leader":"","children":[]},
					{"id":4,"name":"研发部","text":"研发部","serialNum":"demo-3","workPlace":"","contact":"","leader":"","children":[]}
				]}
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
		url : 'api/auth/role/data',
		queryParams : {
			params : '{ "orgId" : "isNull" }'
		},
		method : 'get',
		frozenColumns : [[{
					field : 'ck',
					checkbox : true
				}]],
		columns : [[{
					field : 'id',
					title : 'ID'
				}, {
					field : 'name',
					title : '角色名称'
				}, {
					field : 'description',
					title : '角色描述'
				}]],
		loadMsg : '数据载入中...',
		onLoadError : function() {
			// 该方法会在请求失败后执行
			// 这里使用测试数据填充DataGrid，便于调试页面
			var tmp = [
				{"id":2,"org":{"id":4,"name":"研发部","serialNum":"demo-3","workPlace":"","contact":"","leader":"","text":"研发部"},"name":"项目经理","description":"项目经理"},
				{"id":3,"org":{"id":4,"name":"研发部","serialNum":"demo-3","workPlace":"","contact":"","leader":"","text":"研发部"},"name":"软件工程师","description":"软件工程师"}
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
	if ($('#orgId-add').val()) {
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
						title : '角色名称',
						editor : {
							type : 'textbox',
							options : {
								required : true,
								validType : ['length[0, 10]']
							}
						}
					}, {
						field : 'description',
						width : 150,
						title : '角色描述',
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
	} else {
		$.messager.alert('提示', '请先选择机构！', 'info');
	}
}
function func_add() {
	if (endEditing()) {
		$('#dg-add').datagrid('acceptChanges');
		if ($('#orgId-add').val()) {
			// 数据处理
			var data = $('#dg-add').datagrid('getData');
			$.ajax({
				type : 'post',
				url : 'api/auth/role/batch',
				data : {
					orgId : $('#orgId-add').val(),
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
		} else {
			$.messager.alert('提示', '角色必须添加在机构下！', 'info');
		}
	}
}

function dlg_edit() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要修改的条目！', 'info');
	} else if (rows.length == 1) {
		if (rows[0].org) {
			$('#orgName-edit').val(rows[0].org.name);
		} else {
			$('#orgName-edit').val('无');
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
			url : 'api/auth/role/' + $('#id-edit').val(),
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
					url : 'api/auth/role/batch?ids=' + ids,
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
		params : '{ "orgId" : "isNull" }'
	});
}

function dlg_auth() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要授权的条目！', 'info');
	} else if (rows.length == 1) {
		// 载入权限树
		$('#tree-auth').tree({
			url : 'api/auth/perm/tree',
			method : 'get',
			lines : true,
			checkbox : true,
			onClick : function(node) {
				if (node.checked) {
					$(this).tree('uncheck', node.target);
				} else {
					$(this).tree('check', node.target);
				}
			},
			onLoadSuccess : function(node, data) {
				// 这里需要读取该角色已有的权限
				$.ajax({
					type : 'get',
					url : 'api/auth/role/' + rows[0].id + '/auth',
					dataType : 'json',
					async : false,
					success : function(data) {
						for (var i = 0; i < data.length; i++) {
							$('#tree-auth').tree('check', $('#tree-auth').tree('find', data[i].id).target);
						}
					}
				});
				$(this).tree('collapseAll');
			}
		});
		$('#id-auth').val(rows[0].id);
		$('#dlg-auth').dialog('open');
	} else {
		$.messager.alert('提示', '授权时只可以选择一个！', 'info');
	}
}
function func_auth() {
	var roleId = $('#id-auth').val();
	var perms = $('#tree-auth').tree('getChecked');
	var permIds = new Array();
	$.each(perms, function(i, perm) {
		permIds.push(perm.id);
	});
	$.ajax({
		type : 'post',
		url : 'api/auth/role/' + roleId + '/auth',
		data : {
			permIds : permIds.join(',')
		},
		dataType : 'json',
		async : true,
		success : function(data) {
			if (data.success) {
				// 授权不会影响页面数据，无需刷新
				$('#dlg-auth').dialog('close');
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