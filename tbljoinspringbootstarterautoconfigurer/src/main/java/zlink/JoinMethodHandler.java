package zlink;

import org.apache.ibatis.annotations.Param;
import zlink.ex.RepeatedOrderException;
import zlink.TblRegister;
import zlink.anno.FromTbl;
import zlink.anno.JoinAndSelect;
import zlink.anno.slave.ForJoin;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 把继承了com.baomidou.mybatisplus.core.mapper.BaseMapper的类中的方法生成MappedStatement
 */
public class JoinMethodHandler extends AbstractMethod {

  // 1.获取mapper上加了@Ljoin注解的方法
  // 2.获取实体类上加了@JoinOn的属性(如果只有一个,表名就可以确定了)
  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
    // 配置文件的table-prefix + entity类名
    // System.out.println(tableInfo.getTableName());  //dept; emp; mmyy_dept; mmyy_emp
    // System.out.println(tableInfo.getAllSqlSelect());  //id,name,addr; id,name,age,birthday,dept; wz_id,title,content,late_upd_date

    // 1. sql
    StringBuffer sql = new StringBuffer();  // String sql = "select * from dept where id<3";
    //获取{类|接口}实现的所有接口
    // ArrUtil.outln(mapperClass.getInterfaces());  //com.baomidou.mybatisplus.core.mapper.BaseMapper
    // 此类所实现的接口的一个数组
    // ArrUtil.outln(mapperClass.getGenericInterfaces());  // [com.baomidou.mybatisplus.core.mapper.BaseMapper<cn.z.entity.Dept>]

    // ParameterizedType pt = (ParameterizedType) t;  // 强制转换为`参数化类型`【BaseMapper<cn.z.entity.Dept>】
    // Type types[] =  ((ParameterizedType) t).getActualTypeArguments();  // 获取参数化类型中，实际类型的定义 【new Type[]{Dept.class}】
    // ArrUtil.outln(types);  //[class cn.z.entity.Dept]
    TblRegister.addMap(modelClass);
    // Map<String, TblRegister.Tbl> registInfo = TblRegister.registInfo;
    String prevTbl = null;

    StringBuffer selectCol=new StringBuffer();
    Map<String, String> methodNameSql = new HashMap<>();
    TreeMap<Annotation,String> splitResult = new TreeMap<>(Comparator.comparingInt(
            value -> {
              try {
                return (int)value.annotationType().getMethod("order").invoke(value);
              } catch (Exception e) {
                e.printStackTrace();
                return -1;
              }
            }
    ));
    Map<String,String> joinTypeMap = new LinkedHashMap<>();  //k:表名, v:join类型

              // String currMethod=null;
    List<Integer> orderLi = new ArrayList<>();
    Method[] methods = mapperClass.getDeclaredMethods();  //如:selectList()...
    for (Method method : methods) {
      selectCol.setLength(0);  //清空
      sql.setLength(0);  //清空
      orderLi.clear();
      joinTypeMap.clear();
      // currMethod =method.getName();
      // if (!currMethod.equalsIgnoreCase("selectListPage")) {
      //   continue;
      // }
      // ArrUtil.outln(method.getDeclaredAnnotations());  //[@...ForLjoin(value=[@...Ljoin(tblName=, value=[233]), @...Ljoin(tblName=, value=[2334])]), @...Rjoin(tblName=, value=555)]
      for (Annotation anno : method.getDeclaredAnnotations()) {
        ForJoin fj;
        JoinAndSelect jas;

        if (anno.annotationType()== FromTbl.class) {
          String value = ((FromTbl)anno).value();
          selectCol.append(",").append(value);
          // splitResult.add(anno);

          // 多个相同{LIR}join注解的情况  
        } else if ((fj = getAnnotation(anno, ForJoin.class)) != null) {
          try {
            Object invoke = anno.getClass().getMethod("value").invoke(anno);  //{LIR}join[] value();
            if (invoke.getClass().isArray()) {
              for (Object join : (Object[]) invoke) {  //{LIR}join

                // String joinType = join.getClass().getDeclaredAnnotation(JoinAndSelect.class).joinType().getVal();
                // 两张表的join类型
                          // String joinType = ((Annotation)join).annotationType().getDeclaredAnnotation(JoinAndSelect.class).joinType().getVal();
                // @{LIR}join的tblName()
                String tblName = join.getClass().getMethod("tblName").invoke(join).toString();
                // Map<String, TblRegister.Tbl> registInfo = TblRegister.registInfo;
                if (StringUtils.isEmpty(tblName)) {  //@{LIR}join没有指定表名属性, 只有一个外键字段
                  //上一个表                                                 实体类     mp的表类
                  String getTblName = prevTbl != null ? prevTbl : getTblName(modelClass, tableInfo);
                  //获取当前modelClass的表名, 通过表名再获取join的表
                  TblRegister.Tbl tblByFk = TblRegister.getTbl(getTblName).getUniqueForeignTbl();
                  String as = StringUtils.isEmpty(tblByFk.getAlias()) ? "" : " AS " + tblByFk.getAlias();
                  tblName = tblByFk.getName() + as;
                }
                prevTbl = tblName;
                Integer order = invokeByName((Annotation) join, "order", int.class);
                if (orderLi.size() > 0 && orderLi.contains(order)) {
                  throw new RepeatedOrderException("在"+method.getDeclaringClass()+"的"+method.getName()+"方法中存在重复的顺序("+order+"号)!" );
                }
                orderLi.add(order);
                splitResult.put((Annotation) join, tblName);
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }

          // {LIR}join只有一个的情况  
        } else if ((jas = getAnnotation(anno, JoinAndSelect.class)) != null || (anno.annotationType()== JoinAndSelect.class)) {  //{LIR}join;
                    // String joinType = jas != null ? jas.joinType().getVal() : ((JoinAndSelect)anno).freeJoinType();
          String tblName = null;
          try {
            tblName = anno.getClass().getMethod("tblName").invoke(anno).toString();
            if (StringUtils.isEmpty(tblName)) {  //{LIR}join的`tblName`属性为默认值;
              //获取表名, 通过表名再获取join的表
              String getTblName = prevTbl != null?prevTbl:getTblName(modelClass, tableInfo);
              TblRegister.Tbl tblByFk = TblRegister.getTbl(getTblName).getUniqueForeignTbl();
              if (tblByFk!=null) {
                String as = StringUtils.isEmpty(tblByFk.getAlias()) ? "" : " AS " + tblByFk.getAlias();
                tblName = tblByFk.getName() + as;
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
          Integer order = invokeByName(anno, "order", int.class);
          if (orderLi.size() > 0 && orderLi.contains(order)) {
            try {
              throw new RepeatedOrderException("在"+method.getDeclaringClass()+"的"+method.getName()+"方法中存在重复的顺序("+order+"号)!" );
            } catch (RepeatedOrderException e) {
              e.printStackTrace();
            }
          }
          orderLi.add(order);
          splitResult.put(anno,tblName);
        }
      }
      fill(splitResult, selectCol, joinTypeMap);
      if (selectCol.length()>0) {
        sql.append("<script>select ");
        sql.append(selectCol.deleteCharAt(0));
        sql.append("\n from ");
        // TableName tableName = modelClass.getDeclaredAnnotation(TableName.class);
        // selectCol.append(tableName !=null?tableName.value():tableInfo.getTableName());
        sql.append(getTblName(modelClass, tableInfo));

        StringBuffer joinOn = produceJoinOn(joinTypeMap, getTblName(modelClass, tableInfo));
        // System.out.println("\n        joinOn:  " + sql.append(joinOn));
        sql.append(joinOn);
        // sql.append(" <where> ${ew.sqlSegment} </where></script>");
        String annoValue = null;
        Class<?>[] methodParamTypes = method.getParameterTypes();  //方法的所有参数的类型
        for (int i = 0; i < methodParamTypes.length; i++) {
          Class<?> methodParamType = methodParamTypes[i];
          if (Wrapper.class.isAssignableFrom(methodParamType)) {
            Annotation[] annotations = method.getParameterAnnotations()[i];
            for (Annotation anno : annotations) {
              if (anno.annotationType() == Param.class) {
                annoValue = ((Param)anno).value();
              }
            }
          }
        }
        if (annoValue != null) {
          sql.append(" <if test=\"" +annoValue+ "!=null\">${" +annoValue+ ".customSqlSegment}</if> </script>");
        } else {
          sql.append(" </script>");
        }

        methodNameSql.put(method.getName(), sql.toString());
      }
      // if (sql.length()>0)break;  //common

      // selectCol.setLength(0);
      // sql.setLength(0);
      // prevTbl=null;
      // if (modelClass.getSimpleName().equalsIgnoreCase("dept") || mapperClass.getSimpleName().equalsIgnoreCase("emp"))
      //   System.out.println(modelClass.getSimpleName()+"\n-------joinTypeMap: " + joinTypeMap);
      // joinTypeMap.clear();
    }
//          
//          // System.out.println(fieldAno.annotationType());  //interface cn.z.config.Join
//          // System.out.println(fieldAno.toString());  //@cn.z.config.Join(col=id, joinType=L, tbl=dept)
//        }
//      }
    /*System.out.println("\n-------sql-------: " + sql+"\n    ---sqlend");  //select id,name,age,birthday,dept from emp left join dept on emp.dept = dept.id
    if (sql == null || sql.toString().trim().length()==0) {
      return null;
    }*/
    for (String methodName : methodNameSql.keySet()) {
      // }
      // 2. 不知道什么意思,照着抄就行了
      SqlSource sqlSource = languageDriver.createSqlSource(configuration, methodNameSql.get(methodName), Wrapper.class);
      // String method = currMethod;  // String method = "selectDept3";  // mapper 接口方法名一致
      // 3. addMappedStatement
      this.addSelectMappedStatement(mapperClass, methodName, sqlSource, modelClass, tableInfo);
    }
    return null;
  }
  //                                                                                //k:表名, v:join类型
  private void fill(TreeMap<Annotation, String> splitResult, StringBuffer selectCol, Map<String, String> joinTypeMap) {
    // splitResult.sort(Comparator.comparingInt(
    //   value -> {
    //     try {
    //       return Integer.parseInt(value.annotationType().getMethod("order").invoke(value).toString());
    //     } catch (Exception e) {
    //       e.printStackTrace();
    //       return -1;
    //     }
    //   }
    // ));
    for (Annotation anno : splitResult.keySet()) {
      String tblName = splitResult.get(anno);
      if (!(anno instanceof JoinAndSelect)) {
        joinTypeMap.put(tblName
                , anno.annotationType().getDeclaredAnnotation(JoinAndSelect.class).joinType().getVal());
      } else {
        joinTypeMap.put(tblName, ((JoinAndSelect)anno).freeJoinType());
      }
      try {
        selectCol.append(",")
                .append(anno.annotationType().getMethod("value").invoke(anno));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  //                                    k:表名, v:join类型
  private StringBuffer produceJoinOn(Map<String, String> joinTypeMap, String tblName) {
    StringBuffer sb = new StringBuffer();
    for (Iterator<String> it = joinTypeMap.keySet().iterator(); it.hasNext(); ) {
      String next = it.next();  //tbl_shi
      String joinType = joinTypeMap.get(next);  //tbl_shi

      // Map<String, TblRegister.Tbl> registInfo = TblRegister.registInfo;
      TblRegister.Tbl tbl = TblRegister.getTbl(tblName);  //tbl_sheng
      // 外键字段名, 表信息
      Map<String, TblRegister.Tbl> mapInfo = tbl.getMapInfo();  //tbl_sheng的所有外键关系
      for(Iterator<Map.Entry<String, TblRegister.Tbl>> iterator = mapInfo.entrySet().iterator();iterator.hasNext();) {
        Map.Entry<String, TblRegister.Tbl> entry = iterator.next();
        if (entry.getValue().getName().equalsIgnoreCase(next)) {  // 找到一样的表名
          sb.append("\n"+joinType+" "+next+" on "+tbl.getName()+"."+entry.getKey()+"="+next+"."+entry.getValue().getPrimaryKey());
          tblName = entry.getValue().getName();
        }
      }
    }
    return sb;
  }

  private static String getTblName(Class<?> obj, TableInfo tableInfo) {
    TableName tableName = obj.getDeclaredAnnotation(TableName.class);
    // 注解上的表名, 优先于TableInfo的
    return tableName !=null?tableName.value():tableInfo.getTableName();
  }
  private static <A extends Annotation> A getAnnotation(Annotation target, Class<A> anno) {
    return (A)target.annotationType().getAnnotation(anno);
  }

  public static <T> T invokeByName(Class<?> clazz, String methodName, Class<T> returnType) {
    try {
      return (T)clazz.getMethod(methodName).invoke(clazz);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }
  public static <T> T invokeByName(Annotation clazz, String methodName, Class<T> returnType) {
    try {
      return (T)clazz.annotationType().getMethod(methodName).invoke(clazz);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }
}


