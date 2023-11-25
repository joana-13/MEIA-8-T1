/* (C)2023 */
package isep.ipp.pt;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DroolsDemoApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder =
                new SpringApplicationBuilder(DroolsDemoApplication.class);
        builder.headless(false);
        ConfigurableApplicationContext context = builder.run(args);
    }
}
