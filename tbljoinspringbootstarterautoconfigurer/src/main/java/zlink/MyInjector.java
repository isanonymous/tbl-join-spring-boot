package zlink;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.List;

public class MyInjector extends DefaultSqlInjector {
  public static String tablePrefix;
  @Value("${mybatis-plus.global-config.db-config.table-prefix:}")
  public void tablePrefix(String tablePrefix) {
    MyInjector.tablePrefix= !StringUtils.isEmpty(tablePrefix) ? tablePrefix : "";
  }
  @Override
  public List<AbstractMethod> getMethodList() {
    List<AbstractMethod> methodList = super.getMethodList();
    //增加自定义方法
    methodList.add(new JoinMethodHandler());
    return methodList;
  }
}
