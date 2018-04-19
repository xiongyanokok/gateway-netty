package com.xy.gateway.controller;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

@Component
public class InitDubbo implements ApplicationListener<ContextRefreshedEvent> {

	@SuppressWarnings("resource")
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// 当前应用名称
		ApplicationConfig application = new ApplicationConfig();
		application.setName("consumer");

		// 连接注册中心配置
		RegistryConfig registry = new RegistryConfig();
		registry.setProtocol("zookeeper"); // 必须
		registry.setAddress("10.0.202.63:2181,10.0.202.63:2182"); // 必须

		// 引用远程服务
		ReferenceConfig<?> reference = new ReferenceConfig<>();
		reference.setApplication(application); // 必须
		reference.setRegistry(registry); // 必须
		//reference.setId("searchArticleService");
		try {
			reference.setInterface(Class.forName("com.hexun.es.service.SearchArticleService")); // 必须
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		reference.setProtocol("dubbo"); // 必须
		reference.setCheck(true);
		reference.setTimeout(5000);
		
		// 获取ApplicationContext
		ApplicationContext applicationContext = event.getApplicationContext();
		
		// 将applicationContext转换为ConfigurableApplicationContext
		ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;

		// 获取bean工厂并转换为DefaultListableBeanFactory
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

		// 注册单例bean
		defaultListableBeanFactory.registerSingleton("searchArticleService", reference.get());
	}

}
