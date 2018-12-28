package me.lb.service.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import me.lb.model.system.Org;

@FeignClient(name = "platform-ng-sys", path = "/org")
public interface OrgService {

	@RequestMapping("/tree")
	public List<Org> findTopOrgs();

}
