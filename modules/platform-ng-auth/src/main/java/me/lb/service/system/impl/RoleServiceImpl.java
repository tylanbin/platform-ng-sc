package me.lb.service.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;

import me.lb.dao.system.RoleDao;
import me.lb.model.system.Role;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.feign.ActIdentityService;
import me.lb.service.system.RoleService;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl extends GenericServiceImpl<Role> implements RoleService {
	
	@Autowired
	private ActIdentityService actIdentityService;

	@Override
	@LcnTransaction
	public int save(Role obj) {
		// 先存储系统的角色
		int id = super.save(obj);
		if (id > 0) {
			actIdentityService.saveActGroup(String.valueOf(id), obj.getName());
		}
		return id;
	}

	@Override
	@LcnTransaction
	public void update(int id, Role obj) {
		// 先更新系统的角色
		super.update(id, obj);
		actIdentityService.saveActGroup(String.valueOf(id), obj.getName());
	}

	@Override
	@LcnTransaction
	public void delete(int id) {
		// 先删除Activiti的组
		if (id > 0) {
			Role role = ((RoleDao) dao).findById(id);
			actIdentityService.delActGroup(String.valueOf(id), role.getName());
		}
		// 后处理系统角色
		super.delete(id);
	}

	@Override
	@LcnTransaction
	public void deleteAll() {
		List<Role> roles = findAll();
		for (Role role : roles) {
			// 先删除Activiti的组
			if (role.getId() > 0) {
				actIdentityService.delActGroup(String.valueOf(role.getId()), role.getName());
			}
		}
		// 后处理系统角色
		super.deleteAll();
	}

	@Override
	@LcnTransaction
	public void deleteAll(List<Integer> ids) {
		for (int id : ids) {
			// 先删除Activiti的组
			if (id > 0) {
				Role role = ((RoleDao) dao).findById(id);
				actIdentityService.delActGroup(String.valueOf(id), role.getName());
			}
		}
		// 后处理系统角色
		super.deleteAll(ids);
	}

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
	public List<Map<String, Integer>> findRolePerm(Integer roleId, Integer permId) {
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