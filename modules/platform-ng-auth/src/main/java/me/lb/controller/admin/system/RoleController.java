package me.lb.controller.admin.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.lb.feign.OrgService;
import me.lb.model.pagination.Pagination;
import me.lb.model.system.Org;
import me.lb.model.system.Perm;
import me.lb.model.system.Role;
import me.lb.service.system.PermService;
import me.lb.service.system.RoleService;
import me.lb.support.jackson.JsonWriter;

@RestController
// 需要增加produces，否则使用Feign调用时，会默认为text/html导致不能decode
@RequestMapping(value = "/role", produces = "application/json;charset=UTF-8")
public class RoleController {

	@Autowired
	private RoleService roleService;
	@Autowired
	private PermService permService;
	@Autowired
	private OrgService orgService;

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String edit(@PathVariable int id, Role temp) {
		try {
			Role validate = roleService.findByName(temp.getName());
			if (validate != null && validate.getId() != id) {
				return "{ \"msg\" : \"" + temp.getName() + "与已有角色名冲突，请更换后重试！\" }";
			}
			Role obj = roleService.findById(id);
			obj.setName(temp.getName());
			obj.setDescription(temp.getDescription());
			obj.setOrgId(temp.getOrgId());
			roleService.update(id, obj);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable int id) {
		try {
			roleService.delete(id);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String get(@PathVariable int id) {
		try {
			Role temp = roleService.findById(id);
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter.getInstance().filter(Role.class).getWriter().writeValueAsString(temp);
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/batch", method = RequestMethod.POST)
	public String batch_add(int orgId, String objs) {
		try {
			ObjectMapper om = new ObjectMapper();
			List<Role> list = om.readValue(objs, new TypeReference<List<Role>>() {
			});
			Iterator<Role> it = list.iterator();
			while (it.hasNext()) {
				Role obj = it.next();
				if (!roleService.validate(obj.getName())) {
					return "{ \"msg\" : \"" + obj.getName() + "与已有角色名冲突，请更换后重试！\" }";
				}
				obj.setOrgId(orgId);
				roleService.save(obj);
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@RequestMapping(value = "/batch", method = RequestMethod.DELETE)
	public String batch_delete(String ids) {
		try {
			String[] temp = ids.split(",");
			for (String id : temp) {
				roleService.delete(Integer.valueOf(id));
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@RequestMapping(value = "/tree", method = RequestMethod.GET)
	public String tree() {
		// 角色的树需要经过特殊处理，将机构和角色合并显示，并进行区分
		try {
			ObjectMapper om = new ObjectMapper();
			List<Org> list = orgService.findTopOrgs();
			List<Object> result = genRoleTree(list);
			// 将查询出的结果序列化为JSON并返回
			return om.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		}
	}

	@RequestMapping(value = "/data", method = RequestMethod.GET)
	public String data(String params) {
		try {
			ObjectMapper om = new ObjectMapper();
			Pagination<Role> pm = null;
			if (!StringUtils.isEmpty(params)) {
				Map<String, Object> map = om.readValue(params, new TypeReference<Map<String, Object>>() {
				});
				pm = roleService.pagingQuery(map);
			} else {
				pm = roleService.pagingQuery();
			}
			// 序列化查询结果为JSON
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", pm.getTotal());
			result.put("rows", pm.getDatas());
			return JsonWriter.getInstance().filter(Role.class).getWriter().writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
	}

	// 授权的方法

	@RequestMapping(value = "/{id}/auth", method = RequestMethod.POST)
	public String auth_post(@PathVariable int id, String permIds) {
		try {
			List<Integer> list = new ArrayList<Integer>();
			String[] temp = permIds.split(",");
			for (String permId : temp) {
				list.add(Integer.parseInt(permId.trim()));
			}
			roleService.auth(id, list);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@RequestMapping(value = "/{id}/auth", method = RequestMethod.GET)
	public String auth_get(@PathVariable int id) {
		try {
			List<Perm> perms = permService.findByRoleId(id);
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter.getInstance().filter(Perm.class, "children").getWriter().writeValueAsString(perms);
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		}
	}

	/**
	 * 递归机构集合，取出角色信息，生成角色树
	 * @param orgs 机构集合（包含父子关系）
	 * @param result 结果集合
	 */
	private List<Object> genRoleTree(Collection<Org> orgs) {
		List<Object> result = new ArrayList<Object>();
		for (Org org : orgs) {
			// 对每个机构进行封装Map的操作
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("id", "org_" + org.getId());
			obj.put("text", org.getName());
			obj.put("iconCls", "icon-org");
			// 关键是对其children的操作
			List<Object> children = new ArrayList<Object>();
			// 首先先加入递归的结果
			children.addAll(genRoleTree(org.getChildren()));
			// 其次添加角色的信息
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orgId", org.getId());
			for (Role role : roleService.findAll(params)) {
				// 每个角色都是一个独立的叶子节点
				Map<String, Object> obj_role = new HashMap<String, Object>();
				obj_role.put("id", role.getId());
				obj_role.put("text", role.getName());
				obj_role.put("iconCls", "icon-group");
				children.add(obj_role);
			}
			if (!children.isEmpty()) {
				obj.put("children", children);
			}
			result.add(obj);
		}
		return result;
	}

}
