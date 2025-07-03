package us.tx.state.dfps.service.business.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import us.tx.state.dfps.service.common.serviceimpl.UserDetailsServiceImpl;

/**
 * service-common- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 11, 2017- 5:34:24 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public SSORequestHeaderAuthenticationFilter ssoFilter() throws Exception {
		SSORequestHeaderAuthenticationFilter filter = new SSORequestHeaderAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManager());
		return filter;
	}

	@Bean
	public AffirmativeBased accessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> voters = new ArrayList<>(2);
		voters.add(new RoleVoter());
		voters.add(new WebExpressionVoter());
		AffirmativeBased decisionManager = new AffirmativeBased(voters);
		decisionManager.setAllowIfAllAbstainDecisions(false);
		return decisionManager;
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		final List<AuthenticationProvider> providers = new ArrayList<>(1);
		providers.add(preauthAuthProvider());
		return new ProviderManager(providers);
	}

	@Bean
	public PreAuthenticatedAuthenticationProvider preauthAuthProvider() throws Exception {
		PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
		provider.setPreAuthenticatedUserDetailsService(userDetailsServiceWrapper());
		return provider;
	}

	@Bean
    UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> userDetailsServiceWrapper() throws Exception {
		UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken>
                wrapper = new UserDetailsByNameServiceWrapper<>();
		wrapper.setUserDetailsService(userSecurityService());
		return wrapper;
	}

	@Bean
    UserDetailsService userSecurityService() {
		return new UserDetailsServiceImpl();
	}

	@Bean(name = "springSecurityFilterChain")
	public FilterChainProxy getFilterChainProxy() throws ServletException, Exception {
		List<SecurityFilterChain> listOfFilterChains = new ArrayList<SecurityFilterChain>();

		listOfFilterChains.add(
				new DefaultSecurityFilterChain(new AntPathRequestMatcher("/**"), securityContextPersistenceFilter(),
						ssoFilter(), exceptionTranslationFilter(), filterSecurityInterceptor()));
		return new FilterChainProxy(listOfFilterChains);
	}

	@Bean
	public SecurityContextPersistenceFilter securityContextPersistenceFilter() {
		return new SecurityContextPersistenceFilter(new HttpSessionSecurityContextRepository());
	}

	@Bean
	public ExceptionTranslationFilter exceptionTranslationFilter() {
		ExceptionTranslationFilter exceptionTranslationFilter = new ExceptionTranslationFilter(
				new CustomAuthenticationEntryPoint());
		CustomAccessDeniedHandler accessDeniedHandlerImpl = new CustomAccessDeniedHandler();
		exceptionTranslationFilter.setAccessDeniedHandler(accessDeniedHandlerImpl);
		exceptionTranslationFilter.afterPropertiesSet();
		return exceptionTranslationFilter;
	}

	@Bean
	public FilterSecurityInterceptor filterSecurityInterceptor() throws Exception {
		FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
		filterSecurityInterceptor.setAuthenticationManager(authenticationManager());
		filterSecurityInterceptor.setAccessDecisionManager(accessDecisionManager());

		LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();

		requestMap.put(new AntPathRequestMatcher("/**"),
				org.springframework.security.access.SecurityConfig.createList("hasRole('INTERNAL')"));
		FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource = new org.springframework.security.web.access.expression.ExpressionBasedFilterInvocationSecurityMetadataSource(
				requestMap, new DefaultWebSecurityExpressionHandler());
		filterSecurityInterceptor.setSecurityMetadataSource(filterInvocationSecurityMetadataSource);
		filterSecurityInterceptor.afterPropertiesSet();

		return filterSecurityInterceptor;
	}

	@Bean
	public SecurityContextHolderAwareRequestFilter securityContextHolderAwareRequestFilter() {
		return new SecurityContextHolderAwareRequestFilter();

	}
}
