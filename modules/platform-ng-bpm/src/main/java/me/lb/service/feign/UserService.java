package me.lb.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import me.lb.model.system.User;

@FeignClient(name = "platform-ng-auth", path = "/feign/user")
public interface UserService {

	@RequestMapping("/findById")
	public User findById(@RequestParam("id") int id);

}
