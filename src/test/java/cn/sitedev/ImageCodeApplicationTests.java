package cn.sitedev;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.sitedev.properties.ImageCodeProperties;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageCodeApplicationTests {

	@Autowired
	private ImageCodeProperties imageCodeProperties;

	// @Test
	public void contextLoads() {
		System.out.println("hello springboot test!");
	}

	@Test
	public void test() {
		System.out.println("测试属性类:" + imageCodeProperties);
	}

}
