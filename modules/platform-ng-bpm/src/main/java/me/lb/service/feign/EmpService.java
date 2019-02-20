package me.lb.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import me.lb.model.system.Emp;

@FeignClient(name = "platform-ng-sys", path = "/emp")
public interface EmpService {

	@RequestMapping("/{id}")
	public Emp findById(@PathVariable("id") int id);

}
