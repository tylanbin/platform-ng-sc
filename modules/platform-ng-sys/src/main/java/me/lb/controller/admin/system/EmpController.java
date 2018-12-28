package me.lb.controller.admin.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.lb.model.pagination.Pagination;
import me.lb.model.system.Emp;
import me.lb.service.system.EmpService;
import me.lb.support.jackson.JsonWriter;

@RestController
// 需要增加produces，否则使用Feign调用时，会默认为text/html导致不能decode
@RequestMapping(value = "/emp", produces = "application/json;charset=UTF-8")
public class EmpController {

	@Autowired
	private EmpService empService;

	@InitBinder
	protected void initBinder(ServletRequestDataBinder binder) {
		// 对Date类型参数传递的处理
		// DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		CustomDateEditor dateEditor = new CustomDateEditor(format, true);
		binder.registerCustomEditor(Date.class, dateEditor);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public String edit(@PathVariable int id, Emp temp) {
		try {
			Emp obj = empService.findById(id);
			obj.setName(temp.getName());
			obj.setGender(temp.getGender());
			obj.setJob(temp.getJob());
			obj.setEducation(temp.getEducation());
			obj.setBirthday(temp.getBirthday());
			obj.setContact(temp.getContact());
			obj.setIdCard(temp.getIdCard());
			obj.setEmail(temp.getEmail());
			// obj.setIsOnJob(temp.getIsOnJob());
			// obj.setDateOfEntry(temp.getDateOfEntry());
			// obj.setDateOfConfirm(temp.getDateOfConfirm());
			// obj.setDateOfLeave(temp.getDateOfLeave());
			// 修改所属机构
			obj.setOrgId(temp.getOrgId());
			empService.update(id, obj);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable int id) {
		try {
			empService.delete(id);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String get(@PathVariable int id) {
		try {
			Emp temp = empService.findById(id);
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter.getInstance().filter(Emp.class).getWriter().writeValueAsString(temp);
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/batch", method = RequestMethod.POST)
	public String batch_add(int orgId, String objs) {
		try {
			ObjectMapper om = new ObjectMapper();
			List<Emp> list = om.readValue(objs, new TypeReference<List<Emp>>() {
			});
			Iterator<Emp> it = list.iterator();
			while (it.hasNext()) {
				Emp obj = it.next();
				obj.setOrgId(orgId);
				empService.save(obj);
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
				empService.delete(Integer.valueOf(id));
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@RequestMapping(value = "/data", method = RequestMethod.GET)
	public String data(String params) {
		try {
			ObjectMapper om = new ObjectMapper();
			Pagination<Emp> pm = null;
			if (!StringUtils.isEmpty(params)) {
				Map<String, Object> map = om.readValue(params, new TypeReference<Map<String, Object>>() {
				});
				pm = empService.pagingQuery(map);
			} else {
				pm = empService.pagingQuery();
			}
			// 序列化查询结果为JSON
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", pm.getTotal());
			result.put("rows", pm.getDatas());
			return JsonWriter.getInstance().filter(Emp.class).getWriter().writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
	}

}
