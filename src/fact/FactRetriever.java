package fact;

import java.io.IOException;
import java.util.LinkedHashSet;

import org.apache.lucene.queryparser.classic.ParseException;

import chave.CHAVEDocumentIndex;
import index.InvalidIndexException;
import sentence.Sentence;
import sentence.SentenceIndex;

/**
 * This class ...
 * 
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 *
 */
public class FactRetriever {
  public static final int SENTENCE_THRESHOLD = Integer.MAX_VALUE;
  public static final int FACT_THRESHOLD = Integer.MAX_VALUE;

  private CHAVEDocumentIndex documentIndex = null;
  private SentenceIndex sentenceIndex = null;
  private FactIndex factIndex = null;

  /**
   * Creates a new ...
   * 
   * @param  documentIndex ...
   * @param  sentenceIndex ...
   * @param  factIndex ...
   * @throws InvalidIndexException ...
   * @throws IOException ...
   */
  public FactRetriever(CHAVEDocumentIndex documentIndex,
      SentenceIndex sentenceIndex, FactIndex factIndex)
          throws IOException, InvalidIndexException {
    this.documentIndex = documentIndex;
    this.sentenceIndex = sentenceIndex;
    this.factIndex = factIndex;

    if (this.documentIndex.isValid()) {
      this.documentIndex.open();
    }
    else {
      throw new InvalidIndexException("Invalid document index!");
    }
    if (this.sentenceIndex.isValid()) {
      this.sentenceIndex.open();
    }
    else {
      throw new InvalidIndexException("Invalid sentence index!");
    }
    if (this.factIndex.isValid()) {
      this.factIndex.open();
    }
    else {
      throw new InvalidIndexException("Invalid fact index!");
    }
  }

  /**
   * This method ...
   * 
   * @param  query ...
   * @return ...
   */
  public Fact[] retrieve(String query) {
    LinkedHashSet<Fact> facts = new LinkedHashSet<Fact>();
    query = query.trim();
    if (query.length() > 0) {
      Sentence[] sentenceResults = null;
      try {
        sentenceResults = sentenceIndex.query(query,
            SentenceIndex.LEMMATIZED_TEXT, SENTENCE_THRESHOLD);
      }
      catch (ParseException pe) {
        pe.printStackTrace();
      }
      catch (IOException ioe) {
        ioe.printStackTrace();
      }

      for (Sentence sentence : sentenceResults) {
        try {
          Fact[] factResults = factIndex.query("sentenceID:"
              + sentence.getID() +" AND documentID:" + sentence.getDocumentID(),
              FactIndex.SENTENCE_ID, FACT_THRESHOLD);

          for (Fact factResult : factResults) {
            facts.add(factResult);
          }
        }
        catch (ParseException pe) {
          pe.printStackTrace();
        }
        catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    }
    return facts.toArray(new Fact[facts.size()]);
  }
}
