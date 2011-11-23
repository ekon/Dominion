package ekon.dominion;

public class SecurityUtil extends SecurityManager {
  
  private String callingClassName;
  
  public String getCallingClassName() {
	callingClassName = getClassContext()[4].getName();
	return callingClassName;
  }
  
  public boolean isCallingClass(Class<?> callingClass) {
	return getClassContext()[4].getClass().equals(callingClass);
  }
  
  public boolean isCallingClassTest() {
	return (getCallingClassName().contains("Test"));
  }
  
  public void verifyCallingClassIsTest() {
	if (!isCallingClassTest()) { throw new SecurityException(
		"You are calling a test-only method from a non-test class."); }
  }
}
