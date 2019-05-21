package clef;

/**
 * This class ...
 * 
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 *
 */
public class CLEFAnswer {
  private String number = null;
  private String docID = null;
  private String text = null;
  private CLEFAnswerExtract extract = null;

  /**
   * Creates a new ...
   * 
   * @param  number ...
   * @param  docID ...
   * @param  text ...
   */
  public CLEFAnswer(String number, String docID, String text) {
    this(number, docID, text, null);
  }

  /**
   * Creates a new ...
   * 
   * @param  number ...
   * @param  docID ...
   * @param  text ...
   * @param  extract ...
   */
  public CLEFAnswer(String number, String docID, String text,
      CLEFAnswerExtract extract) {
    this.number = number;
    this.docID = docID;
    this.text = text;
    this.extract = extract;
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
   * @return the docID ...
   */
  public String getDocID() {
    return docID;
  }

  /**
   * This method ...
   * 
   * @param  docID the docID to set ...
   */
  public void setDocID(String docID) {
    this.docID = docID;
  }

  /**
   * This method ...
   * 
   * @return the extract ...
   */
  public CLEFAnswerExtract getExtract() {
    return extract;
  }

  /**
   * This method ...
   * 
   * @param  extract the extract to set ...
   */
  public void setExtract(CLEFAnswerExtract extract) {
    this.extract = extract;
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
    result = prime * result + ((docID == null) ? 0 : docID.hashCode());
    result = prime * result + ((extract == null) ? 0 : extract.hashCode());
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
    if (getClass() != obj.getClass()) {
      return false;
    }
    CLEFAnswer other = (CLEFAnswer) obj;
    if (docID == null) {
      if (other.docID != null) {
        return false;
      }
    }
    else if (!docID.equals(other.docID)) {
      return false;
    }
    if (extract == null) {
      if (other.extract != null) {
        return false;
      }
    }
    else if (!extract.equals(other.extract)) {
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
    return "CLEFAnswer [number=" + number + ", docID=" + docID + ", text="
        + text + ", extract=" + extract + "]";
  }
}
