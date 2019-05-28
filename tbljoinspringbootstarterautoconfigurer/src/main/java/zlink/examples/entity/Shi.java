package zlink.examples.entity;
import com.baomidou.mybatisplus.annotation.TableId;
// import lombok.Getter;
// import lombok.Setter;
// import lombok.ToString;

// @Getter
// @Setter
// @ToString
public class Shi {
  @TableId("shiId")
  private String shiId;
  private String shiName;

  public String getShiId() {
    return shiId;
  }

  public void setShiId(String shiId) {
    this.shiId = shiId;
  }

  public String getShiName() {
    return shiName;
  }

  public void setShiName(String shiName) {
    this.shiName = shiName;
  }
}
