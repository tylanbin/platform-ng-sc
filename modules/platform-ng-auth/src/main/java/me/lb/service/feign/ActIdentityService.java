package me.lb.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "platform-ng-bpm", path = "/identity")
public interface ActIdentityService {

	@RequestMapping("/saveUser")
	public void saveActUser(@RequestParam("id") String id, @RequestParam("lastName") String lastName, @RequestParam("password") String password);

	@RequestMapping("/delUser")
	public void delActUser(@RequestParam("id") String id, @RequestParam("lastName") String lastName, @RequestParam("password") String password);

	@RequestMapping("/saveGroup")
	public void saveActGroup(@RequestParam("id") String id, @RequestParam("name") String name);

	@RequestMapping("/delGroup")
	public void delActGroup(@RequestParam("id") String id, @RequestParam("name") String name);

	@RequestMapping("/saveMembership")
	public void saveMembership(@RequestParam("userId") String userId, @RequestParam("groupId") String groupId);

	@RequestMapping("/delMembership")
	public void delMembership(@RequestParam("userId") String userId, @RequestParam("groupId") String groupId);

}