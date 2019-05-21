package sentence;

/**
 * This class ...
 * 
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 *
 */
public class Sentence {
  private String id = null;
  private String tokenizedText = null;
  private String lemmatizedText = null;
  private String documentID = null;

  /**
   * Creates a new ...
   * 
   * @param  id ...
   * @param  tokenizedText ...
   * @param  lemmatizedText ...
   * @param  docID ...
   */
  public Sentence(String id, String tokenizedText, String lemmatizedText,
      String docID) {
    this.id = id;
    this.tokenizedText = tokenizedText;
    this.lemmatizedText = lemmatizedText;
    this.documentID = docID;
  }

  /**
   * This method ...
   * 
   * @return ...
   */
  public String getID() {
    return id;
  }

  /**
   * This method ...
   * 
   * @param  id ...
   */
  public void setID(String id) {
    this.id = id;
  }

  /**
   * This method ...
   * 
   * @return ...
   */
  public String getTokenizedText() {
    return tokenizedText;
  }

  /**
   * This method ...
   * 
   * @param  tokenizedText ...
   */
  public void setTokenizedText(String tokenizedText) {
    this.tokenizedText = tokenizedText;
  }

  /**
   * This method ...
   * 
   * @return ...
   */
  public String getLemmatizedText() {
    return lemmatizedText;
  }

  /**
   * This method ...
   * 
   * @param  lemmatizedText ...
   */
  public void setLemmatizedText(String lemmatizedText) {
    this.lemmatizedText = lemmatizedText;
  }

  /**
   * This method ...
   * 
   * @return ...
   */
  public String getDocumentID() {
    return documentID;
  }

  /**
   * This method ...
   * 
   * @param  documentID ...
   */
  public void setDocumentID(String documentID) {
    this.documentID = documentID;
  }

  /**
   * This method ...
   * 
   * @return ...
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((documentID == null) ? 0 : documentID.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result
        + ((lemmatizedText == null) ? 0 : lemmatizedText.hashCode());
    result = prime * result
        + ((tokenizedText == null) ? 0 : tokenizedText.hashCode());
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
    Sentence other = (Sentence) obj;
    if (documentID == null) {
      if (other.documentID != null) {
        return false;
      }
    }
    else if (!documentID.equals(other.documentID)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    if (lemmatizedText == null) {
      if (other.lemmatizedText != null) {
        return false;
      }
    }
    else if (!lemmatizedText.equals(other.lemmatizedText)) {
      return false;
    }
    if (tokenizedText == null) {
      if (other.tokenizedText != null) {
        return false;
      }
    }
    else if (!tokenizedText.equals(other.tokenizedText)) {
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
    return "CHAVESentence [id=" + id + ", tokenizedText=" + tokenizedText
        + ", lemmatizedText=" + lemmatizedText + ", documentID=" + documentID
        + "]";
  }
}
