package zlink;

public enum JoinType {
  NONE(""),
  L("left join"),
  R("right join"),
  I("inner join");

  String val;
  JoinType(String s) {
    val=s;
  }

  public String getVal() {
    return val;
  }

  public void setVal(String val) {
    this.val = val;
  }
}
