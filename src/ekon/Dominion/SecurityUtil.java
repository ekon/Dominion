package ekon.dominion;

public class SecurityUtil extends SecurityManager {
  public void verifyCallingClassIsTest() {
	if (!(getClassContext()[4].getName().contains("Test") || getClassContext()[5].getName().contains("Test"))) { 
	  throw new SecurityException("You are calling a test-only method from a non-test class.");
	}
  }
}
