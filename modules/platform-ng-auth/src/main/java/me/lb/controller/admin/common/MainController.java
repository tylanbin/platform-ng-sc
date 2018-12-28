package me.lb.controller.admin.common;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.lb.model.system.User;
import me.lb.service.system.UserService;
import me.lb.utils.MD5Util;
import me.lb.utils.UserUtil;

@RestController
public class MainController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/change")
	public String change(String oldPass, String newPass, String rePass, HttpSession session) {
		try {
			// 读取登录用户信息
			User user = UserUtil.getUserFromSession(session);
			if (user != null) {
				if (user.getLoginPass().equals(MD5Util.getValue(MD5Util.PREFIX + oldPass.trim()))) {
					// 旧密码正确才能继续操作
					if (newPass.trim().equals(rePass.trim())) {
						// 两次输入的密码一致
						user.setLoginPass(MD5Util.getValue(MD5Util.PREFIX + newPass.trim()));
						userService.update(user.getId(), user);
						return "{ \"success\" : true }";
					} else {
						return "{ \"msg\" : \"两次输入的密码不一致！\" }";
					}
				} else {
					return "{ \"msg\" : \"旧密码输入错误！\" }";
				}
			} else {
				return "{ \"msg\" : \"您没有登录！\" }";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"修改失败！\" }";
		}
	}

	@RequestMapping(value = "/forceChange")
	public String forceChange(String newPass, HttpSession session) {
		try {
			// 预留一个强制修改的路径，避免出现问题
			User user = UserUtil.getUserFromSession(session);
			if (user != null) {
				user.setLoginPass(MD5Util.getValue(MD5Util.PREFIX + newPass.trim()));
				userService.update(user.getId(), user);
				return "{ \"success\" : true }";
			} else {
				return "{ \"msg\" : \"您没有登录！\" }";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"修改失败！\" }";
		}
	}

}