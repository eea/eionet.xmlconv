package eionet.gdem.test;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 *
 * Default location of @WebAppConfiguration is /src/main/webapp
 */
//@Configuration
@ImportResource({"classpath:spring-app-context.xml",
        "classpath:test-datasource-context.xml", "classpath:test-runtime.xml", "classpath:test-spring-jpa.xml",
        "/WEB-INF/servlet-context.xml", "/WEB-INF/servlet-restapi.xml"})
//"/WEB-INF/servlet-thymeleaf.xml"
public class WebContextConfig {
}
