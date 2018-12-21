package me.lb.service.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import me.lb.dao.system.RoleDao;
import me.lb.model.system.Role;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.system.RoleService;

@Service
public class RoleServiceImpl extends GenericServiceImpl<Role> implements RoleService {

	@Override
	public boolean validate(String name) {
		Role temp = findByName(name);
		if (temp == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Role findByName(String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		List<Role> temp = ((RoleDao) dao).findAll(params);
		if (!temp.isEmpty()) {
			return temp.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Role> findByUserId(int userId) {
		return ((RoleDao) dao).findByUserId(userId);
	}

	@Override
	public List<Map<Integer, Integer>> findRolePerm(Integer roleId, Integer permId) {
		return ((RoleDao) dao).findRolePerm(roleId, permId);
	}

	@Override
	public void saveRolePerm(int roleId, int permId) {
		((RoleDao) dao).saveRolePerm(roleId, permId);
	}

	@Override
	public void deleteRolePerm(Integer roleId, Integer permId) {
		((RoleDao) dao).deleteRolePerm(roleId, permId);
	}

	@Override
	public void auth(int roleId, List<Integer> permIds) {
		// 为角色授权，首先先将角色所有的旧权限删除
		((RoleDao) dao).deleteRolePerm(roleId, null);
		// 然后将新的权限循环存储即可
		for (int permId : permIds) {
			((RoleDao) dao).saveRolePerm(roleId, permId);
		}
	}

}