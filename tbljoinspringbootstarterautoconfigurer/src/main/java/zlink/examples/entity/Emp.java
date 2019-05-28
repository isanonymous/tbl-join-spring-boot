package zlink.examples.entity;

// import lombok.Getter;
// import lombok.Setter;
// import lombok.ToString;
import zlink.anno.JoinOn;

import java.util.Date;

// @Getter
// @Setter
// @ToString
public class Emp {
  private Integer id   ;//     int(11)
  private String name  ;//    varchar(13)
  private Integer age  ;//     int(3)
  private Date birthday;//  datetime

  //join哪张表, 查询这张表的哪些字段(会自动在前面加`d.`), 连接字段
  @JoinOn( tbl="dept", onCol="id")
  private Dept dept    ;//  int(11)

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  public Dept getDept() {
    return dept;
  }

  public void setDept(Dept dept) {
    this.dept = dept;
  }
}
