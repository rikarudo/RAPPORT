package wikipedia;

/**
 * This class represents Wikipedia articles as used in CLEF.
 * 
 * @author  Ricardo
 * @version 0.9.9.9
 * 
 */
public class WikipediaDocument {
  private String id = null;
  private String title = null;
  private String text = null;
  private String categories = null;

  /**
   * Creates a new ...
   * 
   * @param  id ...
   * @param  title ...
   * @param  text ...
   * @param  categories ...
   */
  public WikipediaDocument(String id, String title, String text,
      String categories) {
    this.id = id;
    this.title = title;
    this.text = text;
    this.categories = categories;
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
   * @return the title ...
   */
  public String getTitle() {
    return title;
  }

  /**
   * This method ...
   * 
   * @param  title the title to set ...
   */
  public void setTitle(String title) {
    this.title = title;
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
   * @param  text the text to set
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * This method ...
   * 
   * @return the categories ...
   */
  public String getCategories() {
    return categories;
  }

  /**
   * This method ...
   * 
   * @param  categories the categories to set ...
   */
  public void setCategories(String categories) {
    this.categories = categories;
  }

  /**
   * This method ...
   * 
   * @return an hashcode value for this object ...
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    result = prime * result
        + ((categories == null) ? 0 : categories.hashCode());
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
    if (!(obj instanceof WikipediaDocument)) {
      return false;
    }
    WikipediaDocument other = (WikipediaDocument) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    if (title == null) {
      if (other.title != null) {
        return false;
      }
    }
    else if (!title.equals(other.title)) {
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
    if (categories == null) {
      if (other.categories != null) {
        return false;
      }
    }
    else if (!categories.equals(other.categories)) {
      return false;
    }
    return true;
  }
}
