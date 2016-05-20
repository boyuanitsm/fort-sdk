import com.boyuanitsm.fort.sdk.client.FortClient;
import com.boyuanitsm.fort.sdk.context.FortContext;
import com.boyuanitsm.fort.sdk.context.FortContextHolder;
import com.boyuanitsm.fort.sdk.domain.SecurityUser;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * fort sdk test. using Spring Boot.
 *
 * @author zhanghua on 5/18/16.
 */
@RestController
@ComponentScan(value = "com.boyuanitsm.fort.sdk")
@SpringBootApplication
public class FortSdkTest implements EmbeddedServletContainerCustomizer{

    @Autowired
    private FortClient fortClient;

    @RequestMapping("/api/profile")
    FortContext product() {
        return FortContextHolder.getContext();
    }

    @RequestMapping("/api/signup")
    void signup(SecurityUser user, HttpServletResponse response) throws IOException, HttpException {
        fortClient.registerUser(user);
        response.sendRedirect("/login.html");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(FortSdkTest.class, args);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
        configurableEmbeddedServletContainer.setPort(8088);
    }
}
