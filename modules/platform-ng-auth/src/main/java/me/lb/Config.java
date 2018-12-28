package me.lb;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

}
