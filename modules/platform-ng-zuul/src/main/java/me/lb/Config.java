package me.lb;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import me.lb.support.shiro.realm.SystemRealm;

@Configuration
public class Config {

	// spring-boot 2.x 中支持cors（跨域）的全局设置

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowCredentials(true).allowedHeaders("*").allowedOrigins("*").allowedMethods("*");
			}
		};
	}

	// shiro配置

	@Bean
	public SystemRealm systemRealm() {
		return new SystemRealm();
	}

	@Bean
	public SecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(systemRealm());
		return securityManager;
	}

	@Bean
	public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
		shiroFilter.setSecurityManager(securityManager);
		shiroFilter.setLoginUrl("/api/zuul/noAuth");
		shiroFilter.setUnauthorizedUrl("/api/zuul/noAuth");
		// 配置需要过滤的路径
		Map<String, String> map = new HashMap<String, String>();
		map.put("/api/zuul/**", "anon");
		map.put("/**", "authc");
		shiroFilter.setFilterChainDefinitionMap(map);
		return shiroFilter;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor advisor(SecurityManager securityManager) {
		// 启用shiro的注解
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}

}
