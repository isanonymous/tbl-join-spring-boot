package zlink.examples.entity;

import com.baomidou.mybatisplus.annotation.TableId;
// import lombok.Getter;
// import lombok.Setter;
// import lombok.ToString;
import zlink.anno.JoinOn;

// @Getter
// @Setter
// @ToString
public class Sheng {
  @TableId("shengId")
  private String shengId;
  private String shengName;
  @JoinOn(tbl = "shi",onCol = "sId")
  private Shi shi;

  public String getShengId() {
    return shengId;
  }

  public void setShengId(String shengId) {
    this.shengId = shengId;
  }

  public String getShengName() {
    return shengName;
  }

  public void setShengName(String shengName) {
    this.shengName = shengName;
  }

  public Shi getShi() {
    return shi;
  }

  public void setShi(Shi shi) {
    this.shi = shi;
  }
}
