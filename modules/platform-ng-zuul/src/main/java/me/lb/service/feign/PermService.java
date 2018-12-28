package me.lb.service.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import me.lb.model.system.Perm;

@FeignClient(name = "platform-ng-auth")
public interface PermService {

	@RequestMapping(value = "/role/{id}/auth")
	public List<Perm> findByRoleId(@PathVariable("id") int id);

}
