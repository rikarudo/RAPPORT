package index;

/**
 * This class ...
 *
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 */
public class InvalidIndexException extends Exception{
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new ...
   * 
   */
  public InvalidIndexException() {
    super();
  }

  /**
   * Creates a new ...
   * 
   * @param  message ...
   */
  public InvalidIndexException(String message) {
    super(message);
  }
}
