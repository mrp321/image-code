package cn.sitedev.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.sitedev.entity.ImageCode;
import cn.sitedev.service.ImageCodeService;

@Controller
@RequestMapping("/imageCode")
public class ImageCodeController {
	private static final Logger logger = LoggerFactory.getLogger(ImageCodeController.class);
	@Autowired
	private ImageCodeService imageCodeService;

	/**
	 * 发送图形验证码
	 * 
	 * @author qchen
	 * @date 2018-11-20
	 * @param request
	 *            请求
	 * @param session
	 *            会话
	 * @return
	 */
	@RequestMapping(value = "send")
	@ResponseBody
	public Map<String, Object> sendImageCode(HttpServletRequest request, HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 发送图形验证码
			ImageCode imageCode = this.imageCodeService.sendImageCode(session);
			map.put("error_no", 0);
			map.put("error_info", "图形验证码发送成功");
			map.put("data", imageCode.getBase64Src());
		} catch (RuntimeException e) {
			map.put("error_no", -1);
			map.put("error_info", "图形验证码发送失败, 失败原因:[" + e.getMessage() + "]");
			logger.error("图形验证码发送失败：" + e.getMessage());
		} catch (Exception e) {
			map.put("error_no", -1);
			map.put("error_info", "图形验证码发送失败, 失败原因:[" + e.getMessage() + "]");
			logger.error("图形验证码发送失败：" + e.getMessage());
		}
		logger.info("图形验证码发送结束：" + map);
		return map;
	}

	@RequestMapping(value = "verify")
	@ResponseBody
	public Map<String, Object> verifyImageCode(HttpServletRequest request, HttpSession session, String imageCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean pass = this.imageCodeService.verifyImageCode(session, imageCode);
		if (pass) {
			map.put("error_no", 0);
			map.put("error_info", "图形验证码校验通过");
		} else {
			map.put("error_no", -1);
			map.put("error_info", "图形验证码校验不通过");
		}
		return map;
	}
}
