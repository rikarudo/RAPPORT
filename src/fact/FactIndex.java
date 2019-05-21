package fact;

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
public class FactIndex extends Index<Fact> {
  public static final String ID = "id";
  public static final String SUBJECT = "subject";
  public static final String PREDICATE = "predicate";
  public static final String OBJECT = "object";
  public static final String SENTENCE_ID = "sentenceID";
  public static final String DOCUMENT_ID = "documentID";

  /**
   * 
   */
  public static final String[] FIELDS = new String[] {
      ID, SUBJECT, PREDICATE, OBJECT, SENTENCE_ID, DOCUMENT_ID
  };

  /**
   * Creates a new ...
   * 
   * @param  directory ...
   */
  public FactIndex(Directory directory) {
    super(directory, FIELDS);
  }

  /**
   * This method ...
   * 
   * @param  fact ...
   */
  public void add(Fact fact) throws IOException {
    if (writer != null) {
      Document doc = new Document();
      doc.add(new StringField(ID,
          fact.getID(),
          Field.Store.YES));
      doc.add(new TextField(SUBJECT,
          fact.getSubject(),
          Field.Store.YES));
      doc.add(new TextField(PREDICATE,
          fact.getPredicate(),
          Field.Store.YES));
      doc.add(new StringField(OBJECT,
          fact.getObject(),
          Field.Store.YES));
      doc.add(new StringField(SENTENCE_ID,
          fact.getSentenceID(),
          Field.Store.YES));
      doc.add(new StringField(DOCUMENT_ID,
          fact.getDocumentID(),
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
  public Fact[] query(String terms, String field, int numHits)
      throws ParseException, IOException {
    if (searcher != null) {
      ArrayList<Fact> chaveChunks = new ArrayList<Fact>();
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
          chaveChunks.add(new Fact(doc.get(ID),
              doc.get(SUBJECT), doc.get(PREDICATE), doc.get(OBJECT),
              doc.get(SENTENCE_ID), doc.get(DOCUMENT_ID)));
        }
      }
      return chaveChunks.toArray(new Fact[chaveChunks.size()]);
    }
    else {
      return null;
    }
  }
}
