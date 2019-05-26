package zlink.anno.slave;

import zlink.anno.Ljoin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})  // Error:(14, 1) java: 包含注释类型(cn.z.config.anno.Xxx)适用的目标多于可重复注释类型(cn.z.config.anno.Ljoin)
@Retention(RetentionPolicy.RUNTIME)  // Ljoin的保留期是RUNTIME, 所以这一行代码必须要加
@ForJoin(ForLjoin.class)
public @interface ForLjoin {
  Ljoin[] value();  // Error:(14, 1) java: 包含注释类型(cn.z.config.anno.Xxx)的保留期短于可重复注释类型(cn.z.config.anno.Ljoin)的保留期
}
