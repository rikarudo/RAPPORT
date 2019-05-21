package clef;

import java.util.Arrays;

/**
 * This class represents the CLEF questions made available through
 * Linguateca's CHAVE resources.
 * 
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 * 
 */
public class CLEFQuestion {
  private String year = null;
  private String id = null;
  private String category = null;
  private String type = null;
  private String constraints = null;
  private String language = null;
  private String task = null;
  private String text = null;
  private CLEFAnswer[] answers = null;

  /**
   * Creates a new ...
   *  
   * @param  year ...
   * @param  id ...
   * @param  category ...
   * @param  type ...
   * @param  constraints ...
   * @param  language ...
   * @param  task ...
   * @param  text ...
   * @param  answers ...
   */
  public CLEFQuestion(String year, String id, String category, String type,
      String constraints, String language, String task, String text,
      CLEFAnswer[] answers) {
    this.year = year;
    this.id = id;
    this.category = category;
    this.type = type;
    this.constraints = constraints;
    this.language = language;
    this.task = task;
    this.text = text;
    this.answers = answers;
  }

  /**
   * This method ..
   * 
   * @return the year
   */
  public String getYear() {
    return year;
  }

  /**
   * This method ...
   * 
   * @param  year the year to set ...
   */
  public void setYear(String year) {
    this.year = year;
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
   * @return the type ...
   */
  public String getType() {
    return type;
  }

  /**
   * This method ...
   * 
   * @param  type the type to set ...
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * This method ...
   * 
   * @return the constraints ...
   */
  public String getConstraints() {
    return constraints;
  }

  /**
   * 
   * @param  constraints the constraints to set ...
   */
  public void setConstraints(String constraints) {
    this.constraints = constraints;
  }

  /**
   * This method ...
   * 
   * @return the language ...
   */
  public String getLanguage() {
    return language;
  }

  /**
   * This method ...
   * 
   * @param  language the language to set ...
   */
  public void setLanguage(String language) {
    this.language = language;
  }

  /**
   * This method ...
   * 
   * @return the task ...
   */
  public String getTask() {
    return task;
  }

  /**
   * This method ...
   * 
   * @param  task the task to set ...
   */
  public void setTask(String task) {
    this.task = task;
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
   * @param text the text to set ...
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * This method ...
   * 
   * @return ...
   */
  public CLEFAnswer[] getAnswers() {
    return answers;
  }

  /**
   * This method ...
   * 
   * @param  answers ...
   */
  public void setAnswers(CLEFAnswer[] answers) {
    this.answers = answers;
  }

  /**
   * This method ...
   * 
   * @return an hashcode value for this object ...
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(answers);
    result = prime * result + ((category == null) ? 0 : category.hashCode());
    result = prime * result
        + ((constraints == null) ? 0 : constraints.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((language == null) ? 0 : language.hashCode());
    result = prime * result + ((task == null) ? 0 : task.hashCode());
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((year == null) ? 0 : year.hashCode());
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
    if (getClass() != obj.getClass()) {
      return false;
    }
    CLEFQuestion other = (CLEFQuestion) obj;
    if (!Arrays.equals(answers, other.answers)) {
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
    if (constraints == null) {
      if (other.constraints != null) {
        return false;
      }
    }
    else if (!constraints.equals(other.constraints)) {
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
    if (language == null) {
      if (other.language != null) {
        return false;
      }
    }
    else if (!language.equals(other.language)) {
      return false;
    }
    if (task == null) {
      if (other.task != null) {
        return false;
      }
    }
    else if (!task.equals(other.task)) {
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
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    }
    else if (!type.equals(other.type)) {
      return false;
    }
    if (year == null) {
      if (other.year != null) {
        return false;
      }
    }
    else if (!year.equals(other.year)) {
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
    return "CLEFQuestion [year=" + year + ", id=" + id + ", category="
        + category + ", type=" + type + ", constraints=" + constraints
        + ", language=" + language + ", task=" + task + ", text=" + text
        + ", answers=" + Arrays.toString(answers) + "]";
  }

}
