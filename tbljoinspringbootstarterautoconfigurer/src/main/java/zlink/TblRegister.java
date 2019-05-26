package zlink;

import zlink.ex.TblNotFoundException;
import zlink.anno.JoinOn;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TblRegister {
  public static Map<String,Tbl> registInfo=new TreeMap<>();
  private static String tablePrefix = MyInjector.tablePrefix;

  public static void addRegistInfo(String tblName, String pk) {
    Tbl tbl = getTbl(tblName);
    if (tbl==null) {
      tbl = new Tbl();
      tbl.setName(tblName);
      tbl.setPrimaryKey(pk);
      registInfo.put(tblName, tbl);
    }
  }

  public static void addMap(Class<?> clazz) {
    System.out.println("\n        clazz:  " + clazz);
    // if (TblRegister.isExist(clazz)){
    //   return;
    // }
    for (Field f : clazz.getDeclaredFields()) {  //表的所有字段
      JoinOn joinOn;
      TableId tableId;
      TableName tn;
      String tableName = null;
      if ((tn = clazz.getDeclaredAnnotation(TableName.class)) != null) {  //实体类上有@TableName
        tableName=tn.value();
      }else {
        tableName = tablePrefix + clazz.getSimpleName();
      }
      if ((joinOn = f.getDeclaredAnnotation(JoinOn.class)) != null) {  //字段上有@JoinOn
        // String currentNamespace = builderAssistant.getCurrentNamespace();
        // String tableName = tableInfo.getTableName();
        // String tablePrefix = tableName.substring(0, tableName.indexOf("_")+1);

        // String colName=f.getName();

        String[] refTblInfo = joinOn.tbl().split("(?i)AS");
        Class<?> fieldType = f.getType();
        TableName declaredAnnotation = fieldType.getDeclaredAnnotation(TableName.class);
        // 有@TableName则取@TableName的值
        String simpleName = declaredAnnotation != null ? declaredAnnotation.value() : fieldType.getSimpleName();
        String refTbl = !StringUtils.isEmpty(joinOn.tbl().trim()) ? tablePrefix + refTblInfo[0].trim() : tablePrefix + simpleName;
        String refCol = joinOn.onCol();
        // String foreignKeyCol =f.getName();
        // String allCol = tableInfo.getAllSqlSelect();

        TblRegister.addMap(tableName, StringUtils.camelToUnderline(f.getName()), refTbl, refCol);  //添加两张表的关系

        TblRegister.addMap(fieldType);
        // TblRegister.addMap(f.getType());
        // System.out.println(sql);  //select id,name,age,birthday,dept from emp left join dept on emp.dept = dept.id
      }
      if ((tableId = f.getDeclaredAnnotation(TableId.class)) != null) {  //字段上有@TableId
        // Tbl tbl = TblRegister.getTbl(tableName);
        // if (tbl == null) {
        TblRegister.addRegistInfo(tableName, "".equals(tableId.value())?f.getName():tableId.value());
        // }
        // tbl.setPrimaryKey("".equals(tableId.value())?f.getName():tableId.value());
      }
      if ("id".equals(f.getName())) {
        TblRegister.addRegistInfo(tableName, "id");
      }
    }
  }

  private static boolean isExist(Class<?> clazz) {
    if (TblRegister.registInfo.isEmpty()) {
      return false;
    }
    TableName annotation = clazz.getAnnotation(TableName.class);
    if (annotation != null) {
      // return TblRegister.registInfo.keySet().contains(tablePrefix + annotation.value());
      for(Iterator<String> it = TblRegister.registInfo.keySet().iterator(); it.hasNext();) {
        if (it.next().equalsIgnoreCase(tablePrefix + annotation.value())) {
          return true;
        }
      }
      return false;
    } else {
      // return TblRegister.registInfo.keySet().contains(tablePrefix + clazz.getSimpleName());
      for(Iterator<String> it = TblRegister.registInfo.keySet().iterator(); it.hasNext();) {
        if (it.next().equalsIgnoreCase(tablePrefix + clazz.getSimpleName())) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * sheng.shiId = shi.shiId
   * sheng.detailId = detail.dId 
   * shi.quId = qu.quId
   */
  public static class Tbl{
    //     sheng
    String name;
    String primaryKey;
    String alias;
    byte foreignKeyTotal;
    //      shiId   shi.shiId
    Map<String,Tbl> mapInfo=new HashMap<>();

    public Tbl getForeignTblByTblName(String tblName) {
      for (Tbl tbl : mapInfo.values()) {
        if (tbl.name.equalsIgnoreCase(tblName)) {
          return tbl;
        }
      }
      return null;
    }

    public Tbl getUniqueForeignTbl() throws TblNotFoundException {
      if (foreignKeyTotal == 1) {
        return mapInfo.entrySet().iterator().next().getValue();
      }
      // throw new TblNotFoundException("没有找到对应的表");
      return null;
    }

    public Tbl getTblByFk(String fk)  {
      if (foreignKeyTotal == 1) {
        // return mapInfo.get(0);
        return mapInfo.entrySet().iterator().next().getValue();
      }
      for(Iterator<String> keyIt = mapInfo.keySet().iterator(); keyIt.hasNext();) {
        String next = keyIt.next();
        if (next.equalsIgnoreCase(fk)) {
          return mapInfo.get(next);
        }
      }
      // throw new TblNotFoundException("没有和"+fk+"对应的表");
      return null;
    }

    @Override
    public String toString() {
      return "Tbl{" +
              "name='" + name + '\'' +
              "primaryKey='" + primaryKey + '\'' +
              ", alias='" + alias + '\'' +
              ", mapInfo=" + mapInfo +
              '}';
    }

    public byte getForeignKeyTotal() {
      return foreignKeyTotal;
    }

    public void setForeignKeyTotal(byte foreignKeyTotal) {
      this.foreignKeyTotal = foreignKeyTotal;
    }

    public Map<String, Tbl> getMapInfo() {
      return mapInfo;
    }

    public void setMapInfo(Map<String, Tbl> mapInfo) {
      this.mapInfo = mapInfo;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getAlias() {
      return alias;
    }

    public void setAlias(String alias) {
      this.alias = alias;
    }

    public String getPrimaryKey() {
      return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
      this.primaryKey = primaryKey;
    }

    /** 把外键字段和ref的表关联*/
    void addMap(String foreignKeyCol, Tbl tbl) {
      // for (Iterator<Map<String, Tbl>> it = mapInfo.iterator(); it.hasNext(); ) {
      //   Map<String, Tbl> next =  it.next();
        /*if (next.keySet().contains(foreignKeyCol)) {
          return;
        }*/
      for(Iterator<String> keyIt = mapInfo.keySet().iterator(); keyIt.hasNext();) {
        if (keyIt.next().equalsIgnoreCase(foreignKeyCol)) {
          return;
        }
      }
      // }
      // HashMap<String,Tbl> map = new HashMap<>();  //外键字段不会很多,直接hash
      // map.put(foreignKeyCol, tbl);
      // mapInfo.add(map);
      mapInfo.put(foreignKeyCol, tbl);
    }
  }

  // public static Tbl getTbl(String tblName) {
  //   return registInfo.get(tblName);
  // }

  // dept.shengId = sheng.sId
  public static void addMap(String tblName,String foreignKeyCol, String refTbl,String refCol) {
    Tbl tbl = getTbl(refTbl);
    if (tbl == null) {
      tbl = new Tbl();
      tbl.primaryKey=refCol;
      tbl.name=refTbl;
      registInfo.put(refTbl, tbl);  //注册ref表
    }
    Tbl primaryTbl = getTbl(tblName);
    if (primaryTbl == null) {
      primaryTbl = new Tbl();
      primaryTbl.name = tblName;
      registInfo.put(tblName, primaryTbl);  //注册主表
    }
    Tbl tblByFk=null;
    tblByFk = primaryTbl.getTblByFk(foreignKeyCol);  //获取外键字段对应的表
    if (tblByFk==null) {
      primaryTbl.foreignKeyTotal++;  //外键字段数量+1
      primaryTbl.addMap(foreignKeyCol, tbl);  //把外键字段和对应的表关联
    }
  }

  public static Tbl getTbl(String target) {
    Set<String> tblNameSet = registInfo.keySet();
    if (tblNameSet.size() > 0) {
      for (String tblName : tblNameSet) {
        if (tblName.equalsIgnoreCase(target)) {
          return registInfo.get(tblName);
        }
      }
    }/* else {
      return registInfo.put(target, new Tbl());
    }*/
    // Tbl tbl = new Tbl();
    // registInfo.put(target,tbl);
    // return tbl;
    return null;
  }
}

