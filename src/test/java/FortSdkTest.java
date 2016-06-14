import com.boyuanitsm.fort.sdk.client.FortCrudClient;
import com.boyuanitsm.fort.sdk.context.FortContext;
import com.boyuanitsm.fort.sdk.context.FortContextHolder;
import com.boyuanitsm.fort.sdk.domain.SecurityGroup;
import com.boyuanitsm.fort.sdk.domain.SecurityUser;
import com.boyuanitsm.fort.sdk.exception.FortCrudException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.PathVariable;
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
    private FortCrudClient crudClient;

    @RequestMapping("/api/profile")
    FortContext product() {
        return FortContextHolder.getContext();
    }

    @RequestMapping("/api/signup")
    void signup(SecurityUser user, HttpServletResponse response) throws FortCrudException, IOException {
        user = crudClient.signUp(user);
        System.out.println(user);
        response.sendRedirect("/login.html");
    }

    @RequestMapping("/api/update-group/{id}")
    void updateGroup(@PathVariable("id") Long id) throws FortCrudException {
        SecurityGroup group = crudClient.getSecurityGroup(id);
        group.setAllowDeleting(false);
        crudClient.updateSecurityGroup(group);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(FortSdkTest.class, args);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
        configurableEmbeddedServletContainer.setPort(8088);
    }
}
