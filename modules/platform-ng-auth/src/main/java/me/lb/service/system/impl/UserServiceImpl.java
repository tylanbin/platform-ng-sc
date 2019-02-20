package me.lb.service.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;

import me.lb.dao.system.UserDao;
import me.lb.model.system.User;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.feign.ActIdentityService;
import me.lb.service.system.UserService;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends GenericServiceImpl<User> implements UserService {
	
	@Autowired
	private ActIdentityService actIdentityService;

	@Override
	@LcnTransaction
	public int save(User obj) {
		// 先存储系统的用户
		int id = super.save(obj);
		if (id > 0) {
			actIdentityService.saveActUser(String.valueOf(id), obj.getLoginName(), obj.getLoginPass());
		}
		return id;
	}

	@Override
	@LcnTransaction
	public void update(int id, User obj) {
		// 先更新系统的用户
		super.update(id, obj);
		actIdentityService.saveActUser(String.valueOf(id), obj.getLoginName(), obj.getLoginPass());
	}

	@Override
	@LcnTransaction
	public void delete(int id) {
		// 先删除Activiti的用户
		if (id > 0) {
			User user = ((UserDao) dao).findById(id);
			actIdentityService.delActUser(String.valueOf(id), user.getLoginName(), user.getLoginPass());
		}
		// 后处理系统用户
		super.delete(id);
	}

	@Override
	@LcnTransaction
	public void deleteAll() {
		List<User> users = findAll();
		for (User user : users) {
			// 先删除Activiti的用户
			if (user.getId() > 0) {
				actIdentityService.delActUser(String.valueOf(user.getId()), user.getLoginName(), user.getLoginPass());
			}
		}
		// 后处理系统用户
		super.deleteAll();
	}

	@Override
	@LcnTransaction
	public void deleteAll(List<Integer> ids) {
		for (int id : ids) {
			// 先删除Activiti的用户
			if (id > 0) {
				User user = ((UserDao) dao).findById(id);
				actIdentityService.delActUser(String.valueOf(id), user.getLoginName(), user.getLoginPass());
			}
		}
		// 后处理系统用户
		super.deleteAll(ids);
	}

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
	public List<Map<String, Integer>> findUserRole(Integer userId, Integer roleId) {
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