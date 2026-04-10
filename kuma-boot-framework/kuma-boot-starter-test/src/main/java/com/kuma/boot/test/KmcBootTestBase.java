package com.kuma.boot.test;

import com.kuma.boot.common.utils.log.LogUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@AutoConfigureMockMvc
public class KmcBootTestBase {
   @Autowired
   protected MockMvcTester mockMvcTester;
   @Autowired
   protected ConfigurableEnvironment configurableEnvironment;

   public KmcBootTestBase() {
   }

   @BeforeAll
   public static void initAll() {
      System.setProperty("kmc.running.in.test", "true");
      LogUtils.info("-------------------\u5f00\u59cb\u6d4b\u8bd5-----------------", new Object[0]);
   }

   @BeforeEach
   public void setUp() {
      this.beforeSetUp();
   }

   protected void beforeSetUp() {
   }

   @AfterAll
   public static void tearDownAll() {
      LogUtils.info("-------------------\u6d4b\u8bd5\u7ed3\u675f-------------------", new Object[0]);
   }

   @AfterEach
   public void tearDown() {
      this.afterTearDown();
   }

   protected void afterTearDown() {
   }
}
