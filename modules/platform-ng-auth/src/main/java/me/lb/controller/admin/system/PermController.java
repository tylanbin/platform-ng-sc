package me.lb.controller.admin.system;

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

import me.lb.model.pagination.Pagination;
import me.lb.model.system.Perm;
import me.lb.service.system.PermService;
import me.lb.support.jackson.JsonWriter;

@RestController
// 需要增加produces，否则使用Feign调用时，会默认为text/html导致不能decode
@RequestMapping(value = "/perm", produces = "application/json;charset=UTF-8")
public class PermController {

	@Autowired
	private PermService permService;

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public String edit(@PathVariable int id, Perm temp) {
		try {
			Perm obj = permService.findById(id);
			obj.setName(temp.getName());
			obj.setToken(temp.getToken());
			obj.setUrl(temp.getUrl());
			// 修改父对象
			obj.setParentId(temp.getParentId());
			permService.update(id, obj);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable int id) {
		try {
			permService.delete(id);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String get(@PathVariable int id) {
		try {
			Perm temp = permService.findById(id);
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter.getInstance().filter(Perm.class, "children").getWriter().writeValueAsString(temp);
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/batch", method = RequestMethod.POST)
	public String batch_add(Integer parentId, String objs) {
		try {
			ObjectMapper om = new ObjectMapper();
			List<Perm> list = om.readValue(objs, new TypeReference<List<Perm>>() {
			});
			Iterator<Perm> it = list.iterator();
			while (it.hasNext()) {
				Perm obj = it.next();
				obj.setParentId(parentId);
				permService.save(obj);
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
				permService.delete(Integer.valueOf(id));
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@RequestMapping(value = "/tree", method = RequestMethod.GET)
	public String tree() {
		try {
			List<Perm> list = permService.findTopPerms();
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter.getInstance().filter(Perm.class).getWriter().writeValueAsString(list);
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		}
	}

	@RequestMapping(value = "/data", method = RequestMethod.GET)
	public String data(String params) {
		try {
			ObjectMapper om = new ObjectMapper();
			Pagination<Perm> pm = null;
			if (!StringUtils.isEmpty(params)) {
				// Perm vo = om.readValue(jsonParam, Perm.class);
				Map<String, Object> map = om.readValue(params, new TypeReference<Map<String, Object>>() {
				});
				pm = permService.pagingQuery(map);
			} else {
				pm = permService.pagingQuery();
			}
			// 序列化查询结果为JSON
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", pm.getTotal());
			result.put("rows", pm.getDatas());
			return JsonWriter.getInstance().filter(Perm.class, "children").getWriter().writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
	}

}
