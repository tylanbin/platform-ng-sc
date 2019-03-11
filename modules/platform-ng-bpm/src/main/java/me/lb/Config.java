package me.lb;

import javax.sql.DataSource;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import me.lb.support.system.filter.PaginationFilter;

@Configuration
public class Config {

	/**
	 * spring-boot 2.x 中支持cors（跨域）的全局设置
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowCredentials(true).allowedHeaders("*").allowedOrigins("*").allowedMethods("*");
			}
		};
	}

	/**
	 * 分页过滤器，用于获取分页参数
	 */
	@Bean
	public FilterRegistrationBean<PaginationFilter> paginationFilter() {
		FilterRegistrationBean<PaginationFilter> reg = new FilterRegistrationBean<PaginationFilter>();
		reg.setFilter(new PaginationFilter());
		reg.addUrlPatterns("/*");
		return reg;
	}

	// Activiti配置

	@Bean
	public SpringProcessEngineConfiguration processEngineConfiguration(DataSource dataSource, PlatformTransactionManager transactionManager) {
		SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();
		config.setDataSource(dataSource);
		config.setTransactionManager(transactionManager);
		config.setLabelFontName("黑体");
		config.setActivityFontName("黑体");
		config.setJobExecutorActivate(false);
		config.setDatabaseSchemaUpdate("true");// 这里设置为true，可以自动建表
		return config;
	}

	@Bean
	public ProcessEngineFactoryBean processEngine(SpringProcessEngineConfiguration configuration) {
		ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
		processEngineFactoryBean.setProcessEngineConfiguration(configuration);
		return processEngineFactoryBean;
	}

	@Bean
	public RuntimeService runtimeService(ProcessEngine processEngine) {
		return processEngine.getRuntimeService();
	}

	@Bean
	public RepositoryService repositoryService(ProcessEngine processEngine) {
		return processEngine.getRepositoryService();
	}

	@Bean
	public TaskService taskService(ProcessEngine processEngine) {
		return processEngine.getTaskService();
	}

	@Bean
	public HistoryService historyService(ProcessEngine processEngine) {
		return processEngine.getHistoryService();
	}

	@Bean
	public ManagementService managementService(ProcessEngine processEngine) {
		return processEngine.getManagementService();
	}

	@Bean
	public FormService formService(ProcessEngine processEngine) {
		return processEngine.getFormService();
	}

	@Bean
	public IdentityService identityService(ProcessEngine processEngine) {
		return processEngine.getIdentityService();
	}

}
