package zlink.ex;

public class TblNotFoundException extends Exception {
  private static final long serialVersionUID = -8287042655728387076L;

  private Throwable ex;

  public TblNotFoundException() {
    super((Throwable)null);  // Disallow initCause
  }

  public TblNotFoundException(String s) {
    super(s, null);  //  Disallow initCause
  }

  public TblNotFoundException(String s, Throwable ex) {
    super(s, null);  //  Disallow initCause
    this.ex = ex;
  }

  public Throwable getException() {
    return ex;
  }

  public Throwable getCause() {
    return ex;
  }
}
