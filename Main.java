package excelbb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import java.io.File;

@ComponentScan(basePackages = {"excelbb/controller"})
@SpringBootApplication
@EnableAutoConfiguration(exclude={ThymeleafAutoConfiguration.class})
public class Main extends SpringBootServletInitializer{

	public Main() {
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(Main.class);
	}

	public static void main(String[] args){
		File root = new File("src/main/webapp");
		boolean b = root.exists();
		b = root.isDirectory();
		String absPath = root.getAbsolutePath();
		File file2=root.getAbsoluteFile();
		ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
	}
}
