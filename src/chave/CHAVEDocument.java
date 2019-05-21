package chave;

/**
 * This class represents the documents (newspaper articles) found in
 * Linguateca's CHAVE collection of newspaper articles from
 * <em>P&uacute;blico</em> and from <em>Folha de S&atilde;o Paulo</em>, for the
 * years of 1994 and 1995.
 * 
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 * 
 */
public class CHAVEDocument {
  private String number = null;
  private String id = null;
  private String date = null;
  private String category = null;
  private String author = null;
  private String text = null;

  /**
   * Creates a new ...
   * 
   * @param  number ...
   * @param  id ...
   * @param  date ...
   * @param  category ...
   * @param  author ...
   * @param  text ...
   */
  public CHAVEDocument(String number, String id, String date, String category,
      String author, String text) {
    this.number = number;
    this.id = id;
    this.date = date;
    this.category = category;
    this.author = author;
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
   * @return the id ...
   */
  public String getID() {
    return id;
  }

  /**
   * This method ...
   * 
   * @param  id the id to set ...
   */
  public void setID(String id) {
    this.id = id;
  }

  /**
   * This method ...
   * 
   * @return the date ...
   */
  public String getDate() {
    return date;
  }

  /**
   * This method ...
   * 
   * @param  date the date to set ...
   */
  public void setDate(String date) {
    this.date = date;
  }

  /**
   * This method ...
   * 
   * @return the category ...
   */
  public String getCategory() {
    return category;
  }

  /**
   * This method ...
   * 
   * @param  category the category to set ...
   */
  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * This method ...
   * 
   * @return the author ...
   */
  public String getAuthor() {
    return author;
  }

  /**
   * This method ...
   * 
   * @param  author the author to set ...
   */
  public void setAuthor(String author) {
    this.author = author;
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
   * @return an hashcode value for this object ...
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((author == null) ? 0 : author.hashCode());
    result = prime * result + ((category == null) ? 0 : category.hashCode());
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((number == null) ? 0 : number.hashCode());
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    return result;
  }

  /**
   * This method ...
   * 
   * @param  obj ...
   * @return <em>true</em>, if the objects are equal, <em>false</em>,
   *         otherwise ...
   */
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CHAVEDocument)) {
      return false;
    }
    CHAVEDocument other = (CHAVEDocument) obj;
    if (author == null) {
      if (other.author != null) {
        return false;
      }
    }
    else if (!author.equals(other.author)) {
      return false;
    }
    if (category == null) {
      if (other.category != null) {
        return false;
      }
    }
    else if (!category.equals(other.category)) {
      return false;
    }
    if (date == null) {
      if (other.date != null) {
        return false;
      }
    }
    else if (!date.equals(other.date)) {
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
}
