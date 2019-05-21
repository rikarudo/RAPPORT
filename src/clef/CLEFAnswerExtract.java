package clef;

/**
 * This class ...
 * 
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 *
 */
public class CLEFAnswerExtract {
  private String number = null;
  private String answerNumber = null;
  private String text = null;

  /**
   * Creates a new ...
   * 
   * @param  number ...
   * @param  answerNumber ...
   * @param  text ...
   */
  public CLEFAnswerExtract(String number, String answerNumber, String text) {
    this.number = number;
    this.answerNumber = answerNumber;
    this.text = text;
  }

  /**
   * This method ...
   * 
   * @return the number ...
   */
  public String getNumber() {
    return number;
  }

  /**
   * This method ...
   * 
   * @param  number the number to set ...
   */
  public void setNumber(String number) {
    this.number = number;
  }

  /**
   * This method ...
   * 
   * @return the answerNumber ...
   */
  public String getAnswerNumber() {
    return answerNumber;
  }

  /**
   * This method ...
   * 
   * @param  answerNumber the answerNumber to set ...
   */
  public void setAnswerNumber(String answerNumber) {
    this.answerNumber = answerNumber;
  }

  /**
   * This method ...
   * 
   * @return the text ...
   */
  public String getText() {
    return text;
  }

  /**
   * This method ...
   * 
   * @param  text the text to set ...
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * This method ...
   * 
   * @return ...
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((answerNumber == null) ? 0 : answerNumber.hashCode());
    result = prime * result + ((number == null) ? 0 : number.hashCode());
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    return result;
  }

  /**
   * This method ...
   * 
   * @param  obj ...
   * @return ...
   */
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CLEFAnswerExtract)) {
      return false;
    }
    CLEFAnswerExtract other = (CLEFAnswerExtract) obj;
    if (answerNumber == null) {
      if (other.answerNumber != null) {
        return false;
      }
    }
    else if (!answerNumber.equals(other.answerNumber)) {
      return false;
    }
    if (number == null) {
      if (other.number != null) {
        return false;
      }
    }
    else if (!number.equals(other.number)) {
      return false;
    }
    if (text == null) {
      if (other.text != null) {
        return false;
      }
    }
    else if (!text.equals(other.text)) {
      return false;
    }
    return true;
  }

  /**
   * This method ...
   * 
   * @return ...
   */
  public String toString() {
    return "CLEFAnswerExtract [number=" + number + ", answerNumber="
        + answerNumber + ", text=" + text + "]";
  }
}
