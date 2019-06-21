package cn.sitedev.service;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.sitedev.entity.ImageCode;
import cn.sitedev.properties.ImageCodeProperties;
import cn.sitedev.util.ImageCodeUtil;

/**
 * 图形验证码业务层
 * 
 * @author qchen
 * @version 2018年11月21日
 * @see ImageCodeService
 * @since
 */
@Service
public class ImageCodeService {

	private static final Logger log = LoggerFactory.getLogger(ImageCodeService.class);

	/**
	 * 图形验证码属性类
	 */
	private final ImageCodeProperties imageCodeProperties;

	/**
	 * 图形验证码位数
	 */
	// @Value("${imageCode.length}")
	private Integer IMAGE_CODE_LEN;
	/**
	 * 图形验证码类型:1:纯英文; 2: 纯中文
	 */
	// @Value("${imageCode.type}")
	private Integer IMAGE_CODE_TYPE;

	/**
	 * 图形验证码有效期: 单位:s
	 */
	// @Value("${imageCode.expireIn}")
	private Integer IMAGE_CODE_EXPIREIN;

	/**
	 * 图形验证码高度
	 */
	// @Value("${imageCode.height}")
	private Integer IMAGE_CODE_HEIGHT;

	/**
	 * 图形验证码宽度
	 */
	// @Value("${imageCode.width}")
	private Integer IMAGE_CODE_WIDTH;

	/**
	 * 图形验证码随机条纹数
	 */
	// @Value("${imageCode.randomLineCnt}")
	private Integer IMAGE_CODE_RANDOM_LINE_CNT;

	/**
	 * 图形验证码来源
	 */
	// @Value("${imageCode.source}")
	private Integer IMAGE_CODE_SOURCE;

	/**
	 * 图形验证码json文件名
	 */
	// @Value("${imageCode.jsonFileName}")
	public String IMAGE_CODE_JSON_FILE_NAME;

	/**
	 * 字体大小
	 */
	// @Value("${imageCode.fontSize}")
	private Integer IMAGE_CODE_FONT_SIZE;

	/**
	 * 英文字体名
	 */
	// @Value("${imageCode.fontEn}")
	private String IMAGE_CODE_FONT_EN;

	/**
	 * 中文字体名
	 */
	// @Value("${imageCode.fontCn}")
	private String IMAGE_CODE_FONT_CN;
	/**
	 * 图形验证码session保存时的key
	 */
	public static final String IMAGE_CODE_SESSION_ATTR_KEY_PREFIX = "image_code_";

	public ImageCodeService(ImageCodeProperties imageCodeProperties) {
		super();
		// 通过构造器直接注入ImageCodeProperties, 暂时无法直接通过@Autowired注解注入ImageCodeProperties组件
		this.imageCodeProperties = imageCodeProperties;
		log.info("获取图形验证码相关配置:" + imageCodeProperties);
		// 图形验证码位数
		IMAGE_CODE_LEN = imageCodeProperties.getLength();
		// 图形验证码类型:1:纯英文; 2: 纯中文
		IMAGE_CODE_TYPE = imageCodeProperties.getType();
		// 图形验证码有效期: 单位:s
		IMAGE_CODE_EXPIREIN = imageCodeProperties.getExpireIn();
		// 图形验证码高度
		IMAGE_CODE_HEIGHT = imageCodeProperties.getHeight();
		// 图形验证码宽度
		IMAGE_CODE_WIDTH = imageCodeProperties.getWidth();
		// 图形验证码随机条纹数
		IMAGE_CODE_RANDOM_LINE_CNT = imageCodeProperties.getRandomLineCnt();
		// 图形验证码来源
		IMAGE_CODE_SOURCE = imageCodeProperties.getSource();
		// 图形验证码json文件名
		IMAGE_CODE_JSON_FILE_NAME = imageCodeProperties.getJsonFileName();
		// 字体大小
		IMAGE_CODE_FONT_SIZE = imageCodeProperties.getFontSize();
		// 英文字体名
		IMAGE_CODE_FONT_EN = imageCodeProperties.getFontEn();
		// 中文字体名
		IMAGE_CODE_FONT_CN = imageCodeProperties.getFontCn();
	}

	/**
	 * 发送图形验证码
	 * 
	 * @author qchen
	 * @date 2018-11-22
	 * @param session
	 *            会话
	 * 
	 */
	public ImageCode sendImageCode(HttpSession session) throws IOException {

		// start modify on 2018-12-5 by qchen 添加防止重复生成图形验证码的逻辑;如果当前生成的验证码和上次的相同,则再次生成
		boolean notEqualToPre = false;
		// 如果当前生成的验证码和上次的相同
		ImageCode imageCode = null;
		ImageCode preImageCode = (ImageCode) session.getAttribute(IMAGE_CODE_SESSION_ATTR_KEY_PREFIX);
		String preImageCodeStr = preImageCode == null ? null : preImageCode.getCode();
		while (!notEqualToPre) {
			// 重新生成图形验证码
			imageCode = ImageCodeUtil.createImageCode(IMAGE_CODE_WIDTH, IMAGE_CODE_HEIGHT, IMAGE_CODE_EXPIREIN,
					IMAGE_CODE_LEN, IMAGE_CODE_TYPE, IMAGE_CODE_RANDOM_LINE_CNT, IMAGE_CODE_SOURCE,
					IMAGE_CODE_JSON_FILE_NAME, IMAGE_CODE_FONT_SIZE, IMAGE_CODE_FONT_EN, IMAGE_CODE_FONT_CN);
			if (!imageCode.getCode().equalsIgnoreCase(preImageCodeStr)) {
				notEqualToPre = true;
			} else {
				preImageCodeStr = imageCode.getCode();
			}
		}
		// end modify on 2018-12-5 by qchen 添加防止重复生成图形验证码的逻辑;如果当前生成的验证码和上次的相同,则再次生成
		session.setAttribute(IMAGE_CODE_SESSION_ATTR_KEY_PREFIX, imageCode);
		return imageCode;
	}

	/**
	 * 校验图形验证码
	 * 
	 * @author qchen
	 * @date 2018-11-22
	 * @param session
	 *            会话
	 * @param imageCode
	 *            图形验证码
	 * 
	 * @return
	 */
	public boolean verifyImageCode(HttpSession session, String imageCode) {
		boolean passFlag = false;
		// 从session中获取图形验证码对象
		Object imageCodeObjFromSession = session.getAttribute(IMAGE_CODE_SESSION_ATTR_KEY_PREFIX);
		if (imageCodeObjFromSession != null) {
			ImageCode imageCodeFromSession = (ImageCode) imageCodeObjFromSession;
			if (!imageCodeFromSession.isExpired()) {
				// 取得session中的图形验证码内容
				String codeFromSession = imageCodeFromSession.getCode();
				// start modify on 2018-12-6 by qchen 对于英文的验证码,验证的时候不需要进行大小写的判断
				if (codeFromSession.equalsIgnoreCase(imageCode)) {
					passFlag = true;
				}
				// end modify on 2018-12-6 by qchen 对于英文的验证码,验证的时候不需要进行大小写的判断
			} else {
				throw new RuntimeException("图形验证码已过期, 请重新点击刷新");
			}
		} else {
			throw new RuntimeException("会话已过期, 请重新点击刷新获取图形验证码");
		}
		return passFlag;
	}

	/**
	 * 图形验证码校验
	 * 
	 * @author qchen
	 * @date 2019-1-21
	 * @param session
	 *            会话
	 * @param imageCode
	 *            图形验证码
	 * 
	 */
	public void checkImageCode(HttpSession session, String imageCode) {
		// 图形验证码非空校验
		this.checkInputForImageCode(imageCode);
		// 非空校验通过, 则判断图形验证码是否过期, 或者图形验证码是否输入有误
		boolean passFlag = this.verifyImageCode(session, imageCode);
		if (!passFlag) {
			throw new RuntimeException("图形验证码输入有误, 请重新输入");
		}
	}

	/**
	 * 图形验证码非空校验
	 * 
	 * @author qchen
	 * @date 2019-1-21
	 * @param imageCode
	 *            图形验证码
	 */
	private void checkInputForImageCode(String imageCode) {
		// 图形验证码不为空
		if (StringUtils.isEmpty(imageCode)) {
			throw new RuntimeException("请输入图形验证码");
		}
	}
}
