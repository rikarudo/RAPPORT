package sentence;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;

import index.Index;

/**
 * This class ...
 * 
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 *
 */
public class SentenceIndex extends Index<Sentence> {
  public static final String ID = "id";
  public static final String TOKENIZED_TEXT = "tokenizedText";
  public static final String LEMMATIZED_TEXT = "lemmatizedText";
  public static final String DOCUMENT_ID = "documentID";

  /**
   * 
   */
  public static final String[] FIELDS = new String[] {
      ID, TOKENIZED_TEXT, LEMMATIZED_TEXT, DOCUMENT_ID
  };

  /**
   * Creates a new ...
   * 
   * @param  directory ...
   */
  public SentenceIndex(Directory directory) {
    super(directory, FIELDS);
  }

  /**
   * This method ...
   * 
   * @param  chaveSentence ...
   */
  public void add(Sentence chaveSentence) throws IOException {
    if (writer != null) {
      Document doc = new Document();
      doc.add(new StringField(ID,
          chaveSentence.getID(),
          Field.Store.YES));
      doc.add(new TextField(TOKENIZED_TEXT,
          chaveSentence.getTokenizedText(),
          Field.Store.YES));
      doc.add(new TextField(LEMMATIZED_TEXT,
          chaveSentence.getLemmatizedText(),
          Field.Store.YES));
      doc.add(new StringField(DOCUMENT_ID,
          chaveSentence.getDocumentID(),
          Field.Store.YES));
      writer.addDocument(doc);
    }
  }

  /**
   * This method ...
   * 
   * @param  terms ...
   * @param  field ...
   * @param  numHits ...
   * @return ...
   */
  public Sentence[] query(String terms, String field, int numHits)
      throws ParseException, IOException {
    if (searcher != null) {
      ArrayList<Sentence> chaveSentences = new ArrayList<Sentence>();
      QueryParser queryParser = new QueryParser(field, analyzer);
      Query query = queryParser.parse(terms);
      TopScoreDocCollector collector = TopScoreDocCollector.create(numHits,
          Integer.MAX_VALUE);
      searcher.search(query, collector);
      ScoreDoc[] hits = collector.topDocs().scoreDocs;
      Document doc = null;
      for (int i = 0; i < hits.length; i++) {
        if (hits[i].score > 0) {
          doc = searcher.doc(hits[i].doc);
          chaveSentences.add(new Sentence(doc.get(ID),
              doc.get(TOKENIZED_TEXT), doc.get(LEMMATIZED_TEXT),
              doc.get(DOCUMENT_ID)));
        }
      }
      return chaveSentences.toArray(new Sentence[chaveSentences.size()]);
    }
    else {
      return null;
    }
  }
}
