package fact;

import fact.Fact;

/**
 * This class ...
 * 
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 *
 */
public class FactAnswer {
  private String answer = null;
  private Fact fact = null;

  /**
   * Creates a new ...
   * 
   * @param  answer ...
   * @param  fact ...
   */
  public FactAnswer(String answer, Fact fact) {
    this.answer = answer;
    this.fact = fact;
  }

  /**
   * This method ...
   * 
   * @return ...
   */
  public String getAnswer() {
    return answer;
  }

  /**
   * This method ...
   * 
   * @param  answer ...
   */
  public void setAnswer(String answer) {
    this.answer = answer;
  }

  /**
   * This method ...
   * 
   * @return ...
   */
  public Fact getFact() {
    return fact;
  }

  /**
   * This method ...
   * 
   * @param  fact ...
   */
  public void setFact(Fact fact) {
    this.fact = fact;
  }

  /**
   * This method ...
   * 
   * @return ...
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((answer == null) ? 0 : answer.hashCode());
    result = prime * result + ((fact == null) ? 0 : fact.hashCode());
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
    FactAnswer other = (FactAnswer) obj;
    if (answer == null) {
      if (other.answer != null) {
        return false;
      }
    }
    else if (!answer.equals(other.answer)) {
      return false;
    }
    if (fact == null) {
      if (other.fact != null) {
        return false;
      }
    }
    else if (!fact.equals(other.fact)) {
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
    return "FactAnswer [answer=" + answer + ", fact=" + fact + "]";
  }  
}
