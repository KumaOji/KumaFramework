package com.kuma.boot.test;

import com.kuma.boot.common.utils.log.LogUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;

public class KmcBootTestBase {
   @Autowired
   protected ConfigurableEnvironment configurableEnvironment;

   public KmcBootTestBase() {
   }

   @BeforeAll
   public static void initAll() {
      System.setProperty("kmc.running.in.test", "true");
      LogUtils.info("-------------------开始测试-----------------", new Object[0]);
   }

   @BeforeEach
   public void setUp() {
      this.beforeSetUp();
   }

   protected void beforeSetUp() {
   }

   @AfterAll
   public static void tearDownAll() {
      LogUtils.info("-------------------测试结束-------------------", new Object[0]);
   }

   @AfterEach
   public void tearDown() {
      this.afterTearDown();
   }

   protected void afterTearDown() {
   }
}
