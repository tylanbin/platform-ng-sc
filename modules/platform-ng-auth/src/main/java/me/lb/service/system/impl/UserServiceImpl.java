package me.lb.service.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import me.lb.dao.system.UserDao;
import me.lb.model.system.User;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.system.UserService;

@Service
public class UserServiceImpl extends GenericServiceImpl<User> implements UserService {

	@Override
	public boolean validate(String loginName) {
		User temp = findByLoginName(loginName);
		if (temp == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public User findByLoginName(String loginName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginName", loginName);
		List<User> temp = ((UserDao) dao).findAll(params);
		if (!temp.isEmpty()) {
			return temp.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Map<Integer, Integer>> findUserRole(Integer userId, Integer roleId) {
		return ((UserDao) dao).findUserRole(userId, roleId);
	}

	@Override
	public void saveUserRole(int userId, int roleId) {
		((UserDao) dao).saveUserRole(userId, roleId);
	}

	@Override
	public void deleteUserRole(Integer userId, Integer roleId) {
		((UserDao) dao).deleteUserRole(userId, roleId);
	}

	@Override
	public void auth(int userId, List<Integer> roleIds) {
		// 为用户分配角色，首先先将用户所有的旧角色删除
		((UserDao) dao).deleteUserRole(userId, null);
		// 然后将新的角色循环存储即可
		for (int roleId : roleIds) {
			((UserDao) dao).saveUserRole(userId, roleId);
		}
	}

}