package me.lb.controller.admin.common;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.lb.model.system.User;
import me.lb.service.feign.UserService;
import me.lb.support.jackson.JsonWriter;
import me.lb.utils.MD5Util;
import me.lb.utils.UserUtil;

@RestController
@RequestMapping(value = "/api/zuul", produces = "application/json;charset=UTF-8")
public class LoginController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/login")
	public String login(String loginName, String loginPass, HttpSession session) {
		try {
			// 后台验证，避免恶意操作
			if (StringUtils.isEmpty(loginName.trim())) {
				return "{ \"success\" : false, \"msg\" : \"用户名不能为空！\" }";
			}
			if (StringUtils.isEmpty(loginPass.trim())) {
				return "{ \"success\" : false, \"msg\" : \"密码不能为空！\" }";
			}
			// 通过验证，设置Shiro登录信息
			UsernamePasswordToken token = new UsernamePasswordToken();
			token.setUsername(loginName.trim());
			// 使用自定义的MD5进行密码处理
			String md5Pass = MD5Util.getValue(MD5Util.PREFIX + loginPass.trim());
			token.setPassword(md5Pass.toCharArray());
			// token.setRememberMe(true);
			// 通过Shiro进行登录（使用Realm的doGetAuthenticationInfo方法）
			SecurityUtils.getSubject().login(token);
			// 如果失败会抛出异常
			// 如果成功，则记录用户的信息
			User user = userService.findByLoginName(loginName.trim());
			UserUtil.saveUserToSession(user, session);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"success\" : false, \"msg\" : \"用户名或密码错误！\" }";
		}
	}

	@RequestMapping(value = "/logout")
	public String logout() {
		try {
			SecurityUtils.getSubject().logout();
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"success\" : false }";
		}
	}

	@RequestMapping(value = "/getLoginInfo")
	public String getLoginInfo(HttpSession session) {
		try {
			User user = UserUtil.getUserFromSession(session);
			if (user != null) {
				return JsonWriter.getInstance().filter(User.class).getWriter().writeValueAsString(user);
			} else {
				return "{}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/noAuth")
	public String noAuth() {
		return "{ \"auth\" : false }";
	}

}