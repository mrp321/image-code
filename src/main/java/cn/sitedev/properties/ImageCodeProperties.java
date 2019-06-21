package cn.sitedev.properties;

//import javax.validation.constraints.Min;
//import javax.validation.constraints.NotNull;

//import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 图形验证码属性类-如果配置文件中没有相关配置,则使用默认配置
 * 
 * @author qchen
 * @date 2019-3-11
 *
 */
// 注:只有将该组件注入到容器中才会起作用,因此需要@Component注解, 也可以使用@Configuration注解.
// 如果不标注@Component注解,可以通过在配置类(即标有@Configuration的类上标注@EnableConfigurationProperties(ImageCodeProperties.class)注解来启用该属性类)
@Component
// @ConfigurationPropertie告诉SpringBoot将本类中所有属性和配置文件中相关配置进行绑定
// prefix = "xx":将配置文件中哪个下面的所有属性和Java类中的属性进行绑定
@ConfigurationProperties(prefix = "imagecode")
// @PropertySource加载指定配置文件
@PropertySource(value = "classpath:imageCode.properties", encoding = "UTF-8")
@Validated
public class ImageCodeProperties {
	 // 图形验证码位数
	 private Integer length = 4;
	 // 图形验证码类型:1:纯英文+数字;2:纯中文
	 private Integer type = 1;
	 // 图形验证码有效期, 单位: s
	 private Integer expireIn = 60;
	 // 图形验证码高度
	 private Integer height = 60;
	 // 图形验证码宽度
	 private Integer width = 160;
	 // 图形验证码随机线条数
	 private Integer randomLineCnt = 5;
	 // 图形验证码来源:1:json文件;2:代码随机生成
	 private Integer source = 2;
	 // 图形验证码json文件名
	 private String jsonFileName = "char_set.json";
	 // 图形验证码字体大小
	 private Integer fontSize = 40;
	 // 英文字体名
	 private String fontEn = "Times New Roman";
	 // 中文字体名
	 private String fontCn = "思源宋体";

//	 // 图形验证码位数
//	 @NotNull(message = "图形验证码位数未配置")
//	 @Min(1)
//	 private Integer length;
//	 // 图形验证码类型:1:纯英文+数字;2:纯中文
//	 @NotNull(message = "图形验证码类型未配置")
//	 private Integer type;
//	 // 图形验证码有效期, 单位: s
//	 @NotNull(message = "图形验证码有效期未配置")
//	 @Min(1)
//	 private Integer expireIn;
//	 // 图形验证码高度
//	 @NotNull(message = "图形验证码高度未配置")
//	 @Min(1)
//	 private Integer height;
//	 // 图形验证码宽度
//	 @NotNull(message = "图形验证码宽度未配置")
//	 @Min(1)
//	 private Integer width;
//	 // 图形验证码随机线条数
//	 @NotNull(message = "图形验证码随机线条数未配置")
//	 private Integer randomLineCnt;
//	 // 图形验证码来源:1:json文件;2:代码随机生成
//	 @NotNull(message = "图形验证码来源未配置")
//	 private Integer source;
//	 // 图形验证码json文件名
//	 private String jsonFileName;
//	 // 图形验证码字体大小
//	 @NotNull(message = "图形验证码字体大小未配置")
//	 private Integer fontSize;
//	 // 英文字体名
//	 @NotBlank(message = "图形验证码英文字体名未配置")
//	 private String fontEn;
//	 // 中文字体名
//	 @NotBlank(message = "图形验证码中文字体名未配置")
//	 private String fontCn;

//	// 图形验证码位数
//	private Integer length = 4;
//	// 图形验证码类型:1:纯英文+数字;2:纯中文
//	private Integer type;
//	// 图形验证码有效期, 单位: s
//	private Integer expireIn;
//	// 图形验证码高度
//	private Integer height;
//	// 图形验证码宽度
//	private Integer width;
//	// 图形验证码随机线条数
//	private Integer randomLineCnt;
//	// 图形验证码来源:1:json文件;2:代码随机生成
//	@NotNull(message = "图形验证码来源未配置")
//	private Integer source;
//	// 图形验证码json文件名
//	private String jsonFileName;
//	// 图形验证码字体大小
//	private Integer fontSize;
//	// 英文字体名
//	private String fontEn;
//	// 中文字体名
//	private String fontCn;

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getExpireIn() {
		return expireIn;
	}

	public void setExpireIn(Integer expireIn) {
		this.expireIn = expireIn;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getRandomLineCnt() {
		return randomLineCnt;
	}

	public void setRandomLineCnt(Integer randomLineCnt) {
		this.randomLineCnt = randomLineCnt;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public String getJsonFileName() {
		return jsonFileName;
	}

	public void setJsonFileName(String jsonFileName) {
		this.jsonFileName = jsonFileName;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontEn() {
		return fontEn;
	}

	public void setFontEn(String fontEn) {
		this.fontEn = fontEn;
	}

	public String getFontCn() {
		return fontCn;
	}

	public void setFontCn(String fontCn) {
		this.fontCn = fontCn;
	}

	@Override
	public String toString() {
		return "\n----------当前图形验证码配置:----------\n位数=" + length + ", \n类型=" + type + ", \n有效期(s)=" + expireIn
				+ ", \n高度=" + height + ", \n宽度=" + width + ", \n随机干扰条纹数=" + randomLineCnt + ", \n来源=" + source
				+ ", \njson文件名=" + jsonFileName + ", \n字体大小=" + fontSize + ", \n英文字体名=" + fontEn + ", \n中文字体名=" + fontCn
				+ "\n--------------------";
	}

}