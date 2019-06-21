package cn.sitedev.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.sitedev.entity.ImageCode;
import cn.sitedev.service.ImageCodeService;

/**
 * 图形验证码控制层
 * 
 * @author qchen
 *
 */
@RestController
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
	@GetMapping(value = "send")
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

	/**
	 * 校验图形验证码
	 * 
	 * @author qchen
	 * @date 2019-3-11
	 * @param request
	 *            请求
	 * @param session
	 *            会话
	 * @param imageCode
	 *            图形验证码
	 * @return
	 */
	@PostMapping(value = "verify")
	public Map<String, Object> verifyImageCode(HttpServletRequest request, HttpSession session, String imageCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean pass;
		try {
			pass = this.imageCodeService.verifyImageCode(session, imageCode);
			if (pass) {
				map.put("error_no", 0);
				map.put("error_info", "图形验证码校验通过");
			} else {
				map.put("error_no", -1);
				map.put("error_info", "图形验证码校验不通过");
			}
		} catch (Exception e) {
			map.put("error_no", -1);
			map.put("error_info", e.getMessage());
		}
		return map;
	}
}
