package zlink.anno;

import zlink.JoinType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited  //可以继承
public @interface JoinOn {
  String tbl() default ""; 
  String onCol() default "";
  JoinType joinType() default JoinType.L;

  // String[] selectCol() default {};
}
