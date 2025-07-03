package us.tx.state.dfps.service.business.context;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {

    @Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CustomJWTInterceptor());
	}
}
