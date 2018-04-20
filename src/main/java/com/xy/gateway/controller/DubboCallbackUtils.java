package com.xy.gateway.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.CollectionUtils;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by on 2017/10/23.
 */
@Slf4j
public class DubboCallbackUtils {

	// 当前应用的信息
	private static ApplicationConfig application = new ApplicationConfig();

	// 注册中心信息缓存
	private static Map<String, RegistryConfig> registryConfigCache = new ConcurrentHashMap<>();

	// 各个业务方的ReferenceConfig缓存
	private static Map<String, ReferenceConfig<?>> referenceCache = new ConcurrentHashMap<>();

	static {
		application.setName("consumer-test");
	}

	/**
	 * 获取注册中心信息
	 *
	 * @param address
	 *            zk注册地址
	 * @param group
	 *            dubbo服务所在的组
	 * @return
	 */
	private static RegistryConfig getRegistryConfig(String address, String group, String version) {
		String key = address + "-" + group + "-" + version;
		RegistryConfig registryConfig = registryConfigCache.get(key);
		if (null == registryConfig) {
			registryConfig = new RegistryConfig();
			if (StringUtils.isNotEmpty(address)) {
				registryConfig.setAddress(address);
			}
			if (StringUtils.isNotEmpty(version)) {
				registryConfig.setVersion(version);
			}
			if (StringUtils.isNotEmpty(group)) {
				registryConfig.setGroup(group);
			}
			registryConfigCache.put(key, registryConfig);
		}
		return registryConfig;
	}

	private static ReferenceConfig<?> getReferenceConfig(String interfaceName, String address, String group, String version) {
		String referenceKey = interfaceName;

		ReferenceConfig<?> referenceConfig = referenceCache.get(referenceKey);
		if (null == referenceConfig) {
			referenceConfig = new ReferenceConfig<>();
			referenceConfig.setApplication(application);
			referenceConfig.setRegistry(getRegistryConfig(address, group, version));
			referenceConfig.setInterface(interfaceName);
			if (StringUtils.isNotEmpty(version)) {
				referenceConfig.setVersion(version);
			}
			referenceConfig.setGeneric(true);
			referenceCache.put(referenceKey, referenceConfig);
		}
		return referenceConfig;
	}

	public static Object invoke(String interfaceName, String methodName, List<String> parameterTypes, List<Object> paramList, String address, String version) {
		ReferenceConfig<?> reference = getReferenceConfig(interfaceName, address, null, version);
		GenericService genericService = (GenericService) reference.get();
		if (genericService == null) {
			log.debug("GenericService 不存在:{}", interfaceName);
			return null;
		}

		Object[] paramObject = null;
		if (!CollectionUtils.isEmpty(paramList)) {
			paramObject = new Object[paramList.size()];
			for (int i = 0; i < paramList.size(); i++) {
				paramObject[i] = paramList.get(i);
			}
		}

		String[] types = null;
		if (!CollectionUtils.isEmpty(parameterTypes)) {
			types = new String[parameterTypes.size()];
			parameterTypes.toArray(types);
		}
		return genericService.$invoke(methodName, types, paramObject);
	}

	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<>();
		map.put("keyword", "史月波");

		Object obj = DubboCallbackUtils.invoke("com.hexun.es.service.SearchArticleService", 
				"searchList", 
				Arrays.asList("com.hexun.es.pojo.ArticleQueryRequest"), 
				Arrays.asList(map),
				"zookeeper://127.0.0.1:2181", 
				null);
		System.out.println(obj);
	}
}
