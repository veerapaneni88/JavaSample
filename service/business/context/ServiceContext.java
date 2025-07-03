package us.tx.state.dfps.service.business.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
//import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.web.client.RestTemplate;

import gov.texas.dfps.api.notification.client.NotificationApi;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.JNDIUtil;

@Configuration
//@EnableResourceServer
//@EnableWebSecurity
//@Import({ us.tx.state.dfps.service.common.context.ApplicationContext.class, MethodSecurityConfig.class })
//public class ServiceContext extends ResourceServerConfigurerAdapter {
@Import({ us.tx.state.dfps.service.common.context.ApplicationContext.class })
public class ServiceContext {

	@Autowired
	NotificationApi notificationApi;

	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	 
	@Bean
	public NotificationApi notificationApi() {
		String baseUrl = JNDIUtil.lookUp(ServiceConstants.NOTIFICATION_URL);
		notificationApi.getApiClient().setBasePath(baseUrl);
		return notificationApi; 
	}

//    /**
//     * Configures the token store for oauth2 access tokens
//     */
//    @Override
//    public void configure(ResourceServerSecurityConfigurer config) {
//        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
//        defaultTokenServices.setTokenStore(tokenStore());
//        config.tokenServices(defaultTokenServices);
//        config.authenticationEntryPoint(new CustomHttp401UnauthorizedEntryPoint());
//    }
//
//    @Bean
//    public TokenStore tokenStore() {
//        return new JwtTokenStore(jwtAccessTokenConverter());
//    }
//
//    @Bean
//    public JwtAccessTokenConverter jwtAccessTokenConverter() {
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        CustomAccessTokenConverter customAccessTokenConverter = new CustomAccessTokenConverter();
//        converter.setAccessTokenConverter(customAccessTokenConverter);
//        converter.setSigningKey("123");
//        return converter;
//    }
//
//    @Bean
//    public ExceptionTranslationFilter exceptionTranslationFilter() {
//        ExceptionTranslationFilter exceptionTranslationFilter =
//                new ExceptionTranslationFilter(new CustomAuthenticationEntryPoint());
//        CustomAccessDeniedHandler accessDeniedHandlerImpl = new CustomAccessDeniedHandler();
//        exceptionTranslationFilter.setAccessDeniedHandler(accessDeniedHandlerImpl);
//        exceptionTranslationFilter.afterPropertiesSet();
//        return exceptionTranslationFilter;
//    }
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests().antMatchers("/lookup/**","/forms/getAllTemplates",
//                "/userSession/updateUserSession").permitAll()
//                .anyRequest().authenticated().and().sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and().csrf().disable().exceptionHandling()
//                .accessDeniedHandler(new CustomAccessDeniedHandler()).and().exceptionHandling()
//                .authenticationEntryPoint(new CustomHttp403ForbiddenEntryPoint());
//    }


}
