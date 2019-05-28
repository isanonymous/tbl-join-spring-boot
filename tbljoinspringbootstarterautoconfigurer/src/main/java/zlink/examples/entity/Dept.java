package zlink.examples.entity;

// import lombok.Getter;
// import lombok.Setter;
// import lombok.ToString;
import zlink.anno.JoinOn;

// @Getter
// @Setter
// @ToString
public class Dept {
  private Integer id ;//     int(11)
  private String name;//    varchar(13)
  private String addr;//    varchar(22)
  @JoinOn(tbl = "sheng",onCol = "shengId")
  private Sheng sheng;

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

  public String getAddr() {
    return addr;
  }

  public void setAddr(String addr) {
    this.addr = addr;
  }

  public Sheng getSheng() {
    return sheng;
  }

  public void setSheng(Sheng sheng) {
    this.sheng = sheng;
  }
}
