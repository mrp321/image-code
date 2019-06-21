package cn.sitedev.entity;

import java.util.Calendar;
import java.util.Date;

/**
 * 图形验证码
 * 
 * @author qchen
 * @version 2018年11月20日
 * @see ImageCode
 * @since
 */
public class ImageCode {
	/**
	 * 图形验证码base64编码字符串
	 */
	private String base64Src;

	/**
	 * 图形验证码内容
	 */
	private String code;

	/**
	 * 过期时间
	 */
	private Date expireTime;

	/**
	 * 图形验证码位数
	 */
	private int length;

	/**
	 * 图形验证码类型
	 */
	private int type;

	/**
	 * 无参构造器
	 */
	public ImageCode() {
		super();
	}

	/**
	 * 含参构造器
	 * 
	 * @author qchen
	 * @date 2018-11-20
	 * @param base64Src
	 *            图形验证码base64编码字符串
	 * @param code
	 *            图形验证码内容
	 * @param expireIn
	 *            有效期
	 * @param length
	 *            图形验证码位数
	 * @param type
	 *            图形验证码类型
	 */
	public ImageCode(String base64Src, String code, int expireIn, int length, int type) {
		super();
		this.base64Src = base64Src;
		this.code = code;
		// 指定xx秒后过期
		Calendar calc = Calendar.getInstance();
		calc.setTime(new Date());
		calc.add(Calendar.SECOND, expireIn);
		this.expireTime = calc.getTime();
		this.length = length;
		this.type = type;
	}

	public String getBase64Src() {
		return base64Src;
	}

	public void setBase64Src(String base64Src) {
		this.base64Src = base64Src;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ImageCode [base64Src=" + base64Src + ", code=" + code + ", expireTime=" + expireTime + ", length="
				+ length + ", type=" + type + "]";
	}

	/**
	 * 当前验证码是否过期
	 * 
	 * @author qchen
	 * @date 2018-11-21
	 * @return
	 */
	public boolean isExpired() {
		return this.expireTime.getTime() <= System.currentTimeMillis() ? true : false;
	}

}
