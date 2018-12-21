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
import me.lb.model.system.Org;
import me.lb.service.system.OrgService;
import me.lb.support.jackson.JsonWriter;

@RestController
// 需要增加produces，否则使用Feign调用时，会默认为text/html导致不能decode
@RequestMapping(value = "/org", produces = "application/json;charset=UTF-8")
public class OrgController {

	@Autowired
	private OrgService orgService;

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String edit(@PathVariable int id, Org temp) {
		try {
			Org obj = orgService.findById(id);
			obj.setName(temp.getName());
			obj.setSerialNum(temp.getSerialNum());
			obj.setWorkPlace(temp.getWorkPlace());
			obj.setContact(temp.getContact());
			obj.setLeader(temp.getLeader());
			// 修改父对象
			obj.setParentId(temp.getParentId());
			orgService.update(id, obj);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable int id) {
		try {
			orgService.delete(id);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String get(@PathVariable int id) {
		try {
			Org temp = orgService.findById(id);
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter.getInstance().filter(Org.class, "children").getWriter().writeValueAsString(temp);
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/batch", method = RequestMethod.POST)
	public String batch_add(Integer parentId, String objs) {
		try {
			ObjectMapper om = new ObjectMapper();
			List<Org> list = om.readValue(objs, new TypeReference<List<Org>>() {
			});
			Iterator<Org> it = list.iterator();
			while (it.hasNext()) {
				Org obj = it.next();
				obj.setParentId(parentId);
				orgService.save(obj);
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
				orgService.delete(Integer.valueOf(id));
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
			List<Org> list = orgService.findTopOrgs();
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter.getInstance().filter(Org.class).getWriter().writeValueAsString(list);
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		}
	}

	@RequestMapping(value = "/data", method = RequestMethod.GET)
	public String data(String params) {
		try {
			ObjectMapper om = new ObjectMapper();
			Pagination<Org> pm = null;
			if (!StringUtils.isEmpty(params)) {
				Map<String, Object> map = om.readValue(params, new TypeReference<Map<String, Object>>() {
				});
				pm = orgService.pagingQuery(map);
			} else {
				pm = orgService.pagingQuery();
			}
			// 序列化查询结果为JSON
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", pm.getTotal());
			result.put("rows", pm.getDatas());
			return JsonWriter.getInstance().filter(Org.class, "children").getWriter().writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
	}

}
