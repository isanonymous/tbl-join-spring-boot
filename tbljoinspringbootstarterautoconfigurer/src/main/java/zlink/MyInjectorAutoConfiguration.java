package zlink;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyInjectorAutoConfiguration {
  @Bean
  public MyInjector myInjector() {
    MyInjector myInjector = new MyInjector();
    return myInjector;
  }
}
