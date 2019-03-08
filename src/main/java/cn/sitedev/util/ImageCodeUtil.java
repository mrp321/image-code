package cn.sitedev.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import sun.misc.BASE64Encoder;
import cn.sitedev.entity.ImageCode;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 图形验证码工具类
 * 
 * @author qchen
 * @version 2018年11月20日
 * @see ImageCodeUtil
 * @since
 */
public class ImageCodeUtil {

	private static final String IMAGE_FORMAT_JPG = "jpg";

	/**
	 * 图形验证码类型-英文+数字
	 */
	public static final int IMAGE_CODE_TYPE_EN = 1;

	/**
	 * 图形验证码类型-中文
	 */
	public static final int IMAGE_CODE_TYPE_CN = 2;

	/**
	 * 图形验证码类型描述-英文+数字
	 */
	public static final String IMAGE_CODE_TYPE_EN_DESC = "en";

	/**
	 * 图形验证码类型描述-中文
	 */
	public static final String IMAGE_CODE_TYPE_CN_DESC = "cn";

	/**
	 * 图形验证码base64编码的前缀
	 */
	private static final String IMAGE_BASE64_PREFIX = "data:image/" + IMAGE_FORMAT_JPG + ";base64,";

	/**
	 * 编码-UTF8
	 */
	public static final String CHARSET_UTF8 = "UTF-8";

	/**
	 * 编码-GBK
	 */
	public static final String CHARSET_GBK = "GBK";

	/**
	 * 默认中文字体
	 */
	public static final String DEFAULT_FONT_CN = "SimSun";

	/**
	 * 默认英文字体
	 */
	public static final String DEFAULT_FONT_EN = "Times New Roman";

	/**
	 * 默认图形验证码来源-json格式文件
	 */
	public static final int DEFAULT_SOURCE_JSON_FILE = 1;

	/**
	 * 默认图形验证码来源-代码随机生成
	 */
	public static final int DEFAULT_SOURCE_RANDOM_GENERATE = 2;

	/**
	 * 默认存储验证码的json文件名
	 */
	public static final String DEFAULT_JSON_FILE_NAME = "char_set.json";

	/**
	 * 默认字体大小
	 */
	public static final Integer DEFAULT_FONT_SIZE = 20;

	/**
	 * 从json文件中获取到的json内容
	 */
	public static JSONObject CHAR_SET_JSON = new JSONObject();

	// -------------------------------------------------------------
	// /**
	// * 图形验证码位数
	// */
	// private static int IMAGE_CODE_LEN = Configuration.getInt("imageCode.length",
	// 2);
	//
	// /**
	// * 图形验证码类型:1:纯英文; 2: 纯中文
	// */
	// private static int IMAGE_CODE_TYPE = Configuration.getInt("imageCode.type",
	// 2);
	//
	// /**
	// * 图形验证码有效期: 单位:s
	// */
	// private static int IMAGE_CODE_EXPIREIN =
	// Configuration.getInt("imageCode.expireIn", 60);
	//
	// /**
	// * 图形验证码高度
	// */
	// private static int IMAGE_CODE_HEIGHT =
	// Configuration.getInt("imageCode.height", 23);
	//
	// /**
	// * 图形验证码宽度
	// */
	// private static int IMAGE_CODE_WIDTH = Configuration.getInt("imageCode.width",
	// 67);
	//
	// /**
	// * 图形验证码随机条纹数
	// */
	// private static int IMAGE_CODE_RANDOM_LINE_CNT =
	// Configuration.getInt("imageCode.randomLineCnt",
	// 155);
	//
	// /**
	// * 图形验证码来源
	// */
	// private static int IMAGE_CODE_SOURCE =
	// Configuration.getInt("imageCode.source",
	// DEFAULT_SOURCE_RANDOM_GENERATE);
	//
	// /**
	// * 图形验证码json文件名
	// */
	// private static String IMAGE_CODE_JSON_FILE_NAME = Configuration.getString(
	// "imageCode.jsonFileName", DEFAULT_JSON_FILE_NAME);
	//
	// /**
	// * 字体大小
	// */
	// private static final Integer IMAGE_CODE_FONT_SIZE =
	// Configuration.getInt("imageCode.fontSize",
	// DEFAULT_FONT_SIZE);
	// ------------------------------------------------------------------

	/**
	 * 生成图片验证码
	 * 
	 * @author qchen
	 * @date 2018-11-20
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param expireIn
	 *            有效期
	 * @param length
	 *            长度
	 * @param type
	 *            类型
	 * @param randomLineCnt
	 *            随机干扰条纹数
	 * @param source
	 *            来源
	 * @param jsonFileName
	 *            json文件名
	 * @param fontSize
	 *            字体大小
	 * @param fontEn
	 *            英文字体名
	 * @param fontCn
	 *            中文字体名
	 * @return
	 * @throws IOException
	 */
	public static ImageCode createImageCode(Integer width, Integer height, Integer expireIn, Integer length,
			Integer type, Integer randomLineCnt, Integer source, String jsonFileName, Integer fontSize, String fontEn,
			String fontCn) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = commonSetForImageCode(image, width, height);
		// 生成随机干扰线条
		genRandomLines(graphics, width, height, randomLineCnt);
		// 如果没有指定字体名， 则使用默认字体名
		String fontName = getFontNameByImageCodeType(type, fontEn, fontCn);
		// 生成图形验证码
		String sRandCode = genImageCode((Graphics2D) graphics, length, type, source, jsonFileName, fontSize, fontName);
		// 图片流转为base64编码字符串
		String base64Img = image2Base64(image);
		return new ImageCode(base64Img, sRandCode, expireIn, length, type);
	}

	/**
	 * 共通设置
	 * 
	 * @author qchen
	 * @date 2018-11-20
	 * @param image
	 *            图像对象
	 * @param width
	 *            图形验证码宽度
	 * @param height
	 *            图形验证码高度
	 * @return
	 */
	private static Graphics commonSetForImageCode(BufferedImage image, Integer width, Integer height) {
		// 获取Graphics类的对象
		Graphics graphics = image.getGraphics();
		// 设置背景颜色
		graphics.setColor(getRandColor(240, 255));
		// 绘制背景颜色
		graphics.fillRect(0, 0, width, height);
		// 设置边框颜色
		graphics.setColor(Color.WHITE);
		// 绘制边框
		graphics.drawRect(0, 0, width - 1, height - 1);
		return graphics;
	}

	/**
	 * 生成随机干扰线条
	 * 
	 * @author qchen
	 * @date 2018-11-20
	 * @param graphics
	 *            Graphics对象
	 * @param width
	 *            图形验证码宽度
	 * @param height
	 *            图像验证码高度
	 * @param randomLineCnt
	 *            随机干扰线条数量
	 */
	private static void genRandomLines(Graphics graphics, Integer width, Integer height, Integer randomLineCnt) {
		// 实例化一个Random对象
		Random random = new Random();
		// 生成随机线条
		for (int i = 0; i < randomLineCnt; i++) {
			int x1 = random.nextInt(width);
			int y1 = random.nextInt(height);
			int x2 = random.nextInt(width);
			int y2 = random.nextInt(height);
			// 设置颜色
			graphics.setColor(getRandColor(0, 255));
			graphics.drawLine(x1, y1, x2, y2);
		}
	}

	/**
	 * Image对象转base64编码
	 * 
	 * @author qchen
	 * @date 2018-11-20
	 * @param image
	 *            Image对象
	 * @return
	 * @throws IOException
	 */
	private static String image2Base64(BufferedImage image) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(image, IMAGE_FORMAT_JPG, outputStream);
		BASE64Encoder encoder = new BASE64Encoder();
		String base64Img = encoder.encode(outputStream.toByteArray());
		// 去除base64编码后字符串中的"\r\n"子字符串(win系统)或"\n"(linux系统)
		String lineSep = System.getProperty("line.separator");
		base64Img = base64Img.replaceAll(lineSep, "");
		// 添加图片的base64前缀
		base64Img = IMAGE_BASE64_PREFIX + base64Img;
		return base64Img;
	}

	/**
	 * 生成图形验证码
	 * 
	 * @author qchen
	 * @date 2018-11-20
	 * @param graphics
	 *            Graphics对象
	 * @param length
	 *            图形验证码位数
	 * @param type
	 *            图形验证码类型
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String genImageCode(Graphics2D graphics, Integer length, Integer type)
			throws UnsupportedEncodingException {
		return genImageCode(graphics, length, type, DEFAULT_SOURCE_RANDOM_GENERATE, null, DEFAULT_FONT_SIZE, null);
	}

	/**
	 * 生成图形验证码
	 * 
	 * @author qchen
	 * @date 2018-11-27
	 * @param graphics
	 *            Graphics对象
	 * @param length
	 *            图形验证码位数
	 * @param type
	 *            图形验证码类型
	 * @param source
	 *            图形验证码来源
	 * @param jsonFileName
	 *            图形验证码json文件名
	 * @param fontSize
	 *            字体大小
	 * @param fontName
	 *            字体名
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String genImageCode(Graphics2D graphics, Integer length, Integer type, Integer source,
			String jsonFileName, Integer fontSize, String fontName) throws UnsupportedEncodingException {
		// 定义图形验证码字符串
		String sRandCode = "";
		String rand = "";
		if (source == DEFAULT_SOURCE_JSON_FILE) {
			// 图形验证码, 直接从json文件中取得词语
			sRandCode = getRandomCodeFromJson(length, type, jsonFileName);
			for (int i = 0; i < length; i++) {
				rand = sRandCode.substring(i, i + 1);
				setDegreeAndColorAndFontForEachCode(graphics, rand, i, fontSize, fontName);
			}
		} else {
			for (int i = 0; i < length; i++) {
				rand = genRandomCode(type);
				sRandCode += rand;
				setDegreeAndColorAndFontForEachCode(graphics, rand, i, fontSize, fontName);
			}
		}
		graphics.dispose();
		return sRandCode;
	}

	/**
	 * 为每位图形验证码设置角度,颜色和字体
	 * 
	 * @author qchen
	 * @date 2018-11-28
	 * @param graphics
	 *            图形
	 * @param type
	 *            类型
	 * @param rand
	 *            每一位验证码
	 * @param index
	 *            验证码的索引
	 * @param fontSize
	 *            字体大小
	 * @param fontName
	 *            字体名
	 */
	private static void setDegreeAndColorAndFontForEachCode(Graphics2D graphics, String rand, Integer index,
			Integer fontSize, String fontName) {
		Random random = new Random();
		// 设置字体旋转角度
		int degree = random.nextInt() % 30;
		// 设置字体颜色
		graphics.setColor(getRandColor(0, 200));
		// 正向角度
		graphics.rotate(degree * Math.PI / 180, fontSize * index + fontSize / 4, fontSize);
		// 设置字体
		graphics.setFont(new Font(fontName, Font.PLAIN, fontSize));
		// fontSize / 4 => 在每个文字间预留fontSize/4的空间
		graphics.drawString(rand, fontSize * index + fontSize / 4, fontSize);
		// 反向角度
		graphics.rotate(-degree * Math.PI / 180, fontSize * index + fontSize / 4, fontSize);
	}

	/**
	 * 从json文件中随机读取一个验证码
	 * 
	 * @author qchen
	 * @date 2018-11-28
	 * @param length
	 *            长度
	 * @param type
	 *            类型(1:纯英文+数字;2:纯中文)
	 * @param jsonFileName
	 *            json文件名
	 * @return
	 */
	private static String getRandomCodeFromJson(Integer length, Integer type, String jsonFileName) {
		String randomCode = null;
		if (StringUtils.isEmpty(jsonFileName)) {
			jsonFileName = DEFAULT_JSON_FILE_NAME;
		}
		try {
			JSONObject jsonObj = null;

			// JSON非空判断
			if (!CHAR_SET_JSON.isNullObject() && !CHAR_SET_JSON.isEmpty()) {
				jsonObj = CHAR_SET_JSON;
			} else {
				// 从类路径下读取json文件
				ClassPathResource clsPathRes = new ClassPathResource(jsonFileName);
				File file = clsPathRes.getFile();
				// json文件存在判断
				if (file.exists() && file.isFile()) {
					// 将json文件转为json字符串
					String jsonStr = FileUtils.readFileToString(file, CHARSET_UTF8);
					// 将读取到的数据转为JSONObject
					jsonObj = JSONObject.fromObject(jsonStr);
					// 将转换后的JSONOjbect存入内存,以供之后使用
					CHAR_SET_JSON = jsonObj;
				} else {
					throw new RuntimeException("文件[" + jsonFileName + "]不存在");
				}
			}
			if (jsonObj != null) {
				String typeDesc = getTypeDesc(type);
				String jsonKey = typeDesc + "-" + length;
				if (!jsonObj.containsKey(jsonKey)) {
					throw new RuntimeException("当前json文件中不包含指定key");
				}
				// 取出指定key对应的value
				JSONArray jsonArr = jsonObj.getJSONArray(jsonKey);
				// JSONArray非空判断
				if (!jsonArr.isEmpty() && jsonArr.isArray()) {
					Random random = new Random();
					// 随机生成一个索引
					int randomIndex = random.nextInt(jsonArr.size());
					// 获取指定索引对应的JSONObject对象
					JSONObject curObj = (JSONObject) jsonArr.get(randomIndex);
					// 获取JSONObject对象中的name属性值
					randomCode = curObj.getString("name");
				} else {
					throw new RuntimeException("当前json文件中不包含指定key[" + jsonKey + "]对应的内容");
				}

			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		// 获取随机字符串
		return randomCode;
	}

	/**
	 * 根据验证码类型获取中文描述
	 * 
	 * @author qchen
	 * @date 2018-11-28
	 * @param type
	 *            类型
	 * @return
	 */
	private static String getTypeDesc(Integer type) {
		String typeDesc = null;
		if (type == IMAGE_CODE_TYPE_CN) {
			typeDesc = IMAGE_CODE_TYPE_CN_DESC;
		} else if (type == IMAGE_CODE_TYPE_EN) {
			typeDesc = IMAGE_CODE_TYPE_EN_DESC;
		}
		return typeDesc;
	}

	/**
	 * 生成每一位随机码
	 * 
	 * @author qchen
	 * @date 2018-11-20
	 * @param type
	 *            图形验证码类型
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String genRandomCode(Integer type) throws UnsupportedEncodingException {
		String code = null;
		if (IMAGE_CODE_TYPE_CN == type) {
			code = genRandomCodeCn();
		} else if (IMAGE_CODE_TYPE_EN == type) {
			code = genRandomCodeEn();
		} else {
			code = genRandomCodeEn();
		}
		return code;
	}

	/**
	 * 生成随机英文字符串
	 * 
	 * @author qchen
	 * @date 2018-11-20
	 * @return
	 */
	private static String genRandomCodeEn() {
		// 实例化一个Random对象
		Random random = new Random();
		String codes = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String rand = codes.charAt(random.nextInt(codes.length())) + "";
		return rand;
	}

	/**
	 * 生成随机中文字符串
	 * 
	 * @author qchen
	 * @date 2018-11-21
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String genRandomCodeCn() throws UnsupportedEncodingException {
		// start modify on 2018-11-26 修改汉字的生成方式
		// String ch = genChineseCharacterByHanziSet(random);
		String ch = genChineseCharacterByRandomByteChar();
		// end modify on 2018-11-26 修改汉字的生成方式
		return ch;

	}

	/**
	 * 从常用中文汉字集中随机取出一个汉字
	 * 
	 * @author qchen
	 * @date 2018-11-27
	 * @return
	 */
	public static String genChineseCharacterByHanziSet() {
		// 实例化一个Random对象
		Random random = new Random();
		// 准备常用汉字集
		String base = "\u7684\u4e00\u4e86\u662f\u6211\u4e0d\u5728\u4eba\u4eec\u6709\u6765\u4ed6\u8fd9\u4e0a\u7740\u4e2a\u5730\u5230\u5927\u91cc\u8bf4\u5c31\u53bb\u5b50\u5f97\u4e5f\u548c\u90a3\u8981\u4e0b\u770b\u5929\u65f6\u8fc7\u51fa\u5c0f\u4e48\u8d77\u4f60\u90fd\u628a\u597d\u8fd8\u591a\u6ca1\u4e3a\u53c8\u53ef\u5bb6\u5b66\u53ea\u4ee5\u4e3b\u4f1a\u6837\u5e74\u60f3\u751f\u540c\u8001\u4e2d\u5341\u4ece\u81ea\u9762\u524d\u5934\u9053\u5b83\u540e\u7136\u8d70\u5f88\u50cf\u89c1\u4e24\u7528\u5979\u56fd\u52a8\u8fdb\u6210\u56de\u4ec0\u8fb9\u4f5c\u5bf9\u5f00\u800c\u5df1\u4e9b\u73b0\u5c71\u6c11\u5019\u7ecf\u53d1\u5de5\u5411\u4e8b\u547d\u7ed9\u957f\u6c34\u51e0\u4e49\u4e09\u58f0\u4e8e\u9ad8\u624b\u77e5\u7406\u773c\u5fd7\u70b9\u5fc3\u6218\u4e8c\u95ee\u4f46\u8eab\u65b9\u5b9e\u5403\u505a\u53eb\u5f53\u4f4f\u542c\u9769\u6253\u5462\u771f\u5168\u624d\u56db\u5df2\u6240\u654c\u4e4b\u6700\u5149\u4ea7\u60c5\u8def\u5206\u603b\u6761\u767d\u8bdd\u4e1c\u5e2d\u6b21\u4eb2\u5982\u88ab\u82b1\u53e3\u653e\u513f\u5e38\u6c14\u4e94\u7b2c\u4f7f\u5199\u519b\u5427\u6587\u8fd0\u518d\u679c\u600e\u5b9a\u8bb8\u5feb\u660e\u884c\u56e0\u522b\u98de\u5916\u6811\u7269\u6d3b\u90e8\u95e8\u65e0\u5f80\u8239\u671b\u65b0\u5e26\u961f\u5148\u529b\u5b8c\u5374\u7ad9\u4ee3\u5458\u673a\u66f4\u4e5d\u60a8\u6bcf\u98ce\u7ea7\u8ddf\u7b11\u554a\u5b69\u4e07\u5c11\u76f4\u610f\u591c\u6bd4\u9636\u8fde\u8f66\u91cd\u4fbf\u6597\u9a6c\u54ea\u5316\u592a\u6307\u53d8\u793e\u4f3c\u58eb\u8005\u5e72\u77f3\u6ee1\u65e5\u51b3\u767e\u539f\u62ff\u7fa4\u7a76\u5404\u516d\u672c\u601d\u89e3\u7acb\u6cb3\u6751\u516b\u96be\u65e9\u8bba\u5417\u6839\u5171\u8ba9\u76f8\u7814\u4eca\u5176\u4e66\u5750\u63a5\u5e94\u5173\u4fe1\u89c9\u6b65\u53cd\u5904\u8bb0\u5c06\u5343\u627e\u4e89\u9886\u6216\u5e08\u7ed3\u5757\u8dd1\u8c01\u8349\u8d8a\u5b57\u52a0\u811a\u7d27\u7231\u7b49\u4e60\u9635\u6015\u6708\u9752\u534a\u706b\u6cd5\u9898\u5efa\u8d76\u4f4d\u5531\u6d77\u4e03\u5973\u4efb\u4ef6\u611f\u51c6\u5f20\u56e2\u5c4b\u79bb\u8272\u8138\u7247\u79d1\u5012\u775b\u5229\u4e16\u521a\u4e14\u7531\u9001\u5207\u661f\u5bfc\u665a\u8868\u591f\u6574\u8ba4\u54cd\u96ea\u6d41\u672a\u573a\u8be5\u5e76\u5e95\u6df1\u523b\u5e73\u4f1f\u5fd9\u63d0\u786e\u8fd1\u4eae\u8f7b\u8bb2\u519c\u53e4\u9ed1\u544a\u754c\u62c9\u540d\u5440\u571f\u6e05\u9633\u7167\u529e\u53f2\u6539\u5386\u8f6c\u753b\u9020\u5634\u6b64\u6cbb\u5317\u5fc5\u670d\u96e8\u7a7f\u5185\u8bc6\u9a8c\u4f20\u4e1a\u83dc\u722c\u7761\u5174\u5f62\u91cf\u54b1\u89c2\u82e6\u4f53\u4f17\u901a\u51b2\u5408\u7834\u53cb\u5ea6\u672f\u996d\u516c\u65c1\u623f\u6781\u5357\u67aa\u8bfb\u6c99\u5c81\u7ebf\u91ce\u575a\u7a7a\u6536\u7b97\u81f3\u653f\u57ce\u52b3\u843d\u94b1\u7279\u56f4\u5f1f\u80dc\u6559\u70ed\u5c55\u5305\u6b4c\u7c7b\u6e10\u5f3a\u6570\u4e61\u547c\u6027\u97f3\u7b54\u54e5\u9645\u65e7\u795e\u5ea7\u7ae0\u5e2e\u5566\u53d7\u7cfb\u4ee4\u8df3\u975e\u4f55\u725b\u53d6\u5165\u5cb8\u6562\u6389\u5ffd\u79cd\u88c5\u9876\u6025\u6797\u505c\u606f\u53e5\u533a\u8863\u822c\u62a5\u53f6\u538b\u6162\u53d4\u80cc\u7ec6";
		// 截取汉字
		String ch = base.charAt(random.nextInt(base.length())) + "";
		return ch;
	}

	/**
	 * 随机生成一个汉字
	 * 
	 * @author qchen
	 * @date 2018-11-27
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String genChineseCharacterByRandomByteChar() throws UnsupportedEncodingException {
		// 实例化一个Random对象
		Random random = new Random();
		// 获取高位值
		int highPos = (176 + Math.abs(random.nextInt(39)));
		// 获取低位值
		int lowPos = (161 + Math.abs(random.nextInt(93)));
		byte[] chByteArr = new byte[2];
		chByteArr[0] = (new Integer(highPos).byteValue());
		chByteArr[1] = (new Integer(lowPos).byteValue());
		String ch = null;
		ch = new String(chByteArr, CHARSET_GBK);
		return ch;
	}

	/**
	 * 生成随机背景条纹
	 * 
	 * @author qchen
	 * @date 2018-11-20
	 * @param fc
	 *            产生颜色值下限
	 * @param bc
	 *            产生颜色值上限
	 * @return
	 */
	private static Color getRandColor(Integer fc, Integer bc) {
		// 实例化一个Random对象
		Random random = new Random();
		fc = fc > 255 ? 255 : fc < 0 ? 0 : fc;
		bc = bc > 255 ? 255 : bc < 0 ? 0 : bc;
		// 设置个0-255之间的随机颜色值
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	/**
	 * 根据验证码类型获取字体名称
	 * 
	 * @author qchen
	 * @date 201-11-30
	 * @param type
	 *            验证码类型
	 * @param fontEn
	 *            英文字体名
	 * @param fontCn
	 *            中文字体名
	 * @return
	 */
	private static String getFontNameByImageCodeType(Integer type, String fontEn, String fontCn) {
		// 没有获取到字体名的设置， 则使用默认字体设置
		if (StringUtils.isEmpty(fontCn)) {
			fontCn = DEFAULT_FONT_CN;
		}
		if (org.springframework.util.StringUtils.isEmpty(fontEn)) {
			fontEn = DEFAULT_FONT_EN;
		}
		String fontName = type == IMAGE_CODE_TYPE_CN ? fontCn : fontEn;
		return fontName;
	}
}
