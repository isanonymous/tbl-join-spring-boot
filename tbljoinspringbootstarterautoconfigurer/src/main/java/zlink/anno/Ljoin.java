package zlink.anno;

import zlink.JoinType;
import zlink.anno.slave.ForLjoin;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ForLjoin.class)

@JoinAndSelect(joinType = JoinType.L, freeJoinType = "")
public @interface Ljoin {
  // JoinType joinType() default JoinType.L;

  @AliasFor(annotation = JoinAndSelect.class, attribute ="value" )
  String value() default "";

  String tblName() default "";  //用表名, 还是用实体类的属性名?

  int order() default 1;
}
