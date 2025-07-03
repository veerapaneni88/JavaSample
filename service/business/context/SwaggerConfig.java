package us.tx.state.dfps.service.business.context;

/**
 * This config enables swagger ui for the REST APIs in this app. This config is
 * only enabled with spring active profile "swagger". It can be activated during
 * development. See ./src/test/resource/jetty-test-config.xml  
 * 
 * The swagger ui and the openapi spec can be access at urls similar to:
 *    http://localhost:8081/service-business/swagger-ui.html 
 *    http://localhost:8081/service-business/v2/api-docs
 * 
 * When enabled, the openapi doc can be used to generate clients to access
 * the REST APIs from other applications. 
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Profile("swagger")
@Configuration
@EnableSwagger2
@EnableWebMvc
public class SwaggerConfig extends WebMvcConfigurerAdapter {

	private static final String TITLE = "IMPACT Service Business REST APIs";
	private static final String DESC = "A limited number of services exposed for use by "
			+ "internal DFPS applications. The swagger api-doc spec is used by client's "
			+ "to generate a java client to help invoke these services. ";

	@Bean
	public Docket api() {

		//
		// List of class to ignore during client generation. Swagger does not
		// circular dependency, this list remove those classes. Left here for
		// example. If any of services use these, swagger will crash.
		//
		@SuppressWarnings("unused")
		Class[] ignoreCls = { us.tx.state.dfps.common.domain.Event.class, us.tx.state.dfps.common.domain.Person.class,
				us.tx.state.dfps.common.domain.PrtActionPlan.class,
				us.tx.state.dfps.common.domain.ServiceAuthorization.class,
				us.tx.state.dfps.common.domain.ServicePlan.class, us.tx.state.dfps.common.domain.Invoice.class,
				us.tx.state.dfps.common.domain.Stage.class, us.tx.state.dfps.common.domain.FcePerson.class };

		// Swagger cannot handle the entire service-business due to circular
		// dependencies, so we using the 'withMethodAnnotation' to limit the
		// service end-points to select few we needed for external application
		// use. Luckily, these service do not have the dependency issue yet.
		//
		// Mark the interested service with the @APIOperation annotation. Use
		// this annotation only on services interested, limit it's use.
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("us.tx.state.dfps.service"))
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).build().apiInfo(apiInfo());
		// .ignoredParameterTypes(ignoreCls);
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title(TITLE).description(DESC).build();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}
