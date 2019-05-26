package zlink.anno;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited  //可以继承
public @interface FromTbl {
  // String tbl() default ""; 
  // String onCol() default "";

  @AliasFor("value")
  String selectCol() default "";
  
  @AliasFor("selectCol")
  String value() default "";
}
