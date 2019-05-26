package zlink.anno;

import zlink.JoinType;
import zlink.anno.slave.ForIjoin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ForIjoin.class)

@JoinAndSelect(joinType = JoinType.I, freeJoinType = "")
public @interface Ijoin {
  // JoinType joinType() default JoinType.L;

  // @AliasFor(annotation = JoinAndSelect.class, attribute ="value" )
  String value() default "";
  
  String tblName() default "";

  int order() default 1;
}
