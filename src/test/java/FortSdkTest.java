import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhanghua on 5/18/16.
 */
@RestController
@ComponentScan(value = "com.boyuanitsm.fort.sdk")
@SpringBootApplication
public class FortSdkTest implements EmbeddedServletContainerCustomizer{

    @RequestMapping("/")
    String home() {
        return "fort sdk test!";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(FortSdkTest.class, args);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
        configurableEmbeddedServletContainer.setPort(8088);
    }
}
