package me.lb.controller.admin.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 专门用于整合流程设计器的控制器
 * ModelerController
 * @author lanbin
 */
@Controller
// 这个控制器路径以web开头，为的是与页面无缝集成，减少相对路径
@RequestMapping(value = "/web/admin/process/model")
public class ModelerController {

	@Autowired
	private RepositoryService repositoryService;

	/**
	 * 获取流程模型的json（设计器载入时使用）
	 */
	@ResponseBody
	@RequestMapping(value = "/{id}/json", method = RequestMethod.GET)
	public String get(@PathVariable String id) {
		try {
			ObjectMapper om = new ObjectMapper();
			Model model = repositoryService.getModel(id);
			if (model != null) {
				ObjectNode modelNode = null;
				if (StringUtils.isNotEmpty(model.getMetaInfo())) {
					modelNode = (ObjectNode) om.readTree(model.getMetaInfo());
				} else {
					modelNode = om.createObjectNode();
					modelNode.put(ModelDataJsonConstants.MODEL_NAME, model.getName());
				}
				modelNode.put(ModelDataJsonConstants.MODEL_ID, model.getId());
				ObjectNode jsonNode = (ObjectNode) om.readTree(new String(repositoryService.getModelEditorSource(model.getId()), "utf-8"));
				modelNode.set("model", jsonNode);
				return om.writeValueAsString(modelNode);
			} else {
				return "{}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}
	
	/**
	 * 存储流程模型
	 */
	@ResponseBody
	@RequestMapping(value = "/{id}", method = { RequestMethod.PUT, RequestMethod.POST })
	public String save(@PathVariable String id, @RequestBody MultiValueMap<String, String> params) {
		try {
			ObjectMapper om = new ObjectMapper();
			// 更新模型信息
			Model model = repositoryService.getModel(id);
			ObjectNode modelJson = (ObjectNode) om.readTree(model.getMetaInfo());
			modelJson.put(ModelDataJsonConstants.MODEL_NAME, params.getFirst("name"));
			modelJson.put(ModelDataJsonConstants.MODEL_DESCRIPTION, params.getFirst("description"));
			model.setName(params.getFirst("name"));
			model.setMetaInfo(modelJson.toString());
			repositoryService.saveModel(model);
			// 保留设计器使用的json
			repositoryService.addModelEditorSource(model.getId(), params.getFirst("json_xml").getBytes("utf-8"));
			// 生成流程图
			InputStream svgStream = new ByteArrayInputStream(params.getFirst("svg_xml").getBytes("utf-8"));
			TranscoderInput input = new TranscoderInput(svgStream);
			PNGTranscoder transcoder = new PNGTranscoder();
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			TranscoderOutput output = new TranscoderOutput(outStream);
			transcoder.transcode(input, output);
			final byte[] result = outStream.toByteArray();
			repositoryService.addModelEditorSourceExtra(model.getId(), result);
			outStream.close();
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"success\" : false }";
		}
	}

}
