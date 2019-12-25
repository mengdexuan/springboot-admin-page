package com.boot.biz.index;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test ;

/**
 * @author mengdexuan on 2019/9/11 9:23.
 */
@Slf4j
public class TestClass {

	@Before
	public void setUp() throws Exception {
		log.info("setUp");
	}

	@After
	public void tearDown() throws Exception {
		log.info("tearDown");
	}

	@Test
	public void test() {
		log.info("test");
	}


	@Test
	public void test2() {
		log.info("test2");
	}
























}













































