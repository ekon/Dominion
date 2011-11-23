package ekon.dominion;

public class GameException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public enum Type {
		INVALID_MOVE(""),
		CODE_ISSUE(""),
		TO_REMOVE("");
		
		private String message;
		private Type(String message) {
			this.message = message;
		}
		
		public String getMessage() { return message; }
	};
	
	public GameException(Type type) {
		super(type.getMessage());
	}
	
	public GameException(Type type, String message) {
		super(type.name() + " " + message);
	}
}
