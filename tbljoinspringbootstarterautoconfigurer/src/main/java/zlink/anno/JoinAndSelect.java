package zlink.anno;

import zlink.JoinType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited  //可以继承
public @interface JoinAndSelect {
  // String tbl() default ""; 
  // String onCol() default "";
  JoinType joinType() default JoinType.NONE;
  
  String freeJoinType();
  String tblName() default "";  //用表名, 还是用实体类的属性名?

  // @AliasFor("value")
  // String[] selectCol() default {};
  
  // @AliasFor("selectCol")
  String value() default "";

  int order() default 1;
}
