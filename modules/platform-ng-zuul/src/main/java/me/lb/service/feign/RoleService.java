package me.lb.service.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import me.lb.model.system.Role;

@FeignClient(name = "platform-ng-auth", path = "/feign/role")
public interface RoleService {

	@RequestMapping("/findByUserId")
	public List<Role> findByUserId(@RequestParam("userId") int userId);

}
