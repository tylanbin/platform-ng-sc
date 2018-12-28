package me.lb.controller.feign;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import me.lb.model.system.Role;
import me.lb.model.system.User;
import me.lb.service.system.RoleService;
import me.lb.service.system.UserService;
import me.lb.support.jackson.JsonWriter;

@RestController
@RequestMapping(value = "/feign", produces = "application/json;charset=UTF-8")
public class FeignController {

	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;

	@RequestMapping(value = "/user/findByLoginName", method = RequestMethod.GET)
	public String findUserByLoginName(String loginName) {
		try {
			if (!StringUtils.isEmpty(loginName)) {
				User obj = userService.findByLoginName(loginName);
				return JsonWriter.getInstance().filter(User.class).getWriter().writeValueAsString(obj);
			} else {
				return "{}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/role/findByUserId", method = RequestMethod.GET)
	public String findRoleByUserId(int userId) {
		try {
			List<Role> roles = roleService.findByUserId(userId);
			return JsonWriter.getInstance().filter(Role.class).getWriter().writeValueAsString(roles);
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

}
