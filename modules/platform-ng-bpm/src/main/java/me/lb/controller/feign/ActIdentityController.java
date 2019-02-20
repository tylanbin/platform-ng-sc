package me.lb.controller.feign;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;

@RestController
@RequestMapping(value = "/identity", produces = "application/json;charset=UTF-8")
public class ActIdentityController {

	@Autowired
	private IdentityService identityService;

	// 分布式事务注解
	@LcnTransaction
	@RequestMapping(value = "/saveUser")
	public void saveActUser(String id, String lastName, String password) {
		User user = identityService.createUserQuery().userId(id).singleResult();
		if (user == null) {// 新用户
			user = identityService.newUser(id);
		}
		user.setLastName(lastName);
		user.setPassword(password);
		identityService.saveUser(user);
	}

	@LcnTransaction
	@RequestMapping(value = "/delUser")
	public void delActUser(String id, String lastName, String password) {
		identityService.deleteUser(id);
	}

	@LcnTransaction
	@RequestMapping(value = "/saveGroup")
	public void saveActGroup(String id, String name) {
		Group group = identityService.createGroupQuery().groupId(id).singleResult();
		if (group == null) {// 新用户组
			group = identityService.newGroup(id);
		}
		group.setName(name);
		identityService.saveGroup(group);
	}

	@LcnTransaction
	@RequestMapping(value = "/delGroup")
	public void delActGroup(String id, String name) {
		identityService.deleteGroup(id);
	}

	@LcnTransaction
	@RequestMapping(value = "/saveMembership")
	public void saveMembership(String userId, String groupId) {
		identityService.createMembership(userId, groupId);
	}

	@LcnTransaction
	@RequestMapping(value = "/delMembership")
	public void delMembership(String userId, String groupId) {
		identityService.deleteMembership(userId, groupId);
	}

}