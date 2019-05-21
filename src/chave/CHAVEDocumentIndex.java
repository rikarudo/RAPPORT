package chave;

import index.Index;

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

/**
 * This class ...
 * 
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 *
 */
public class CHAVEDocumentIndex extends Index<CHAVEDocument> {
  public static final String NUMBER = "number";
  public static final String ID = "id";
  public static final String DATE = "date";
  public static final String CATEGORY = "category";
  public static final String AUTHOR = "author";
  public static final String TEXT = "text";

  /**
   * 
   */
  public static final String[] FIELDS = new String[] {
      NUMBER, ID, DATE, CATEGORY, AUTHOR, TEXT
  };

  /**
   * Creates a new ...
   * 
   * @param  directory ...
   */
  public CHAVEDocumentIndex(Directory directory) {
    super(directory, FIELDS);
  }

  /**
   * This method ...
   * 
   * @param chaveDocument ...
   */
  public void add(CHAVEDocument chaveDocument) throws IOException {
    if (writer != null) {
      Document doc = new Document();
      doc.add(new StringField(NUMBER, chaveDocument.getNumber(),
          Field.Store.YES));
      doc.add(new StringField(ID, chaveDocument.getID(),
          Field.Store.YES));
      doc.add(new StringField(DATE, chaveDocument.getDate(),
          Field.Store.YES));
      doc.add(new StringField(CATEGORY, chaveDocument.getCategory(),
          Field.Store.YES));
      doc.add(new StringField(AUTHOR, chaveDocument.getAuthor(),
          Field.Store.YES));
      doc.add(new TextField(TEXT, chaveDocument.getText(),
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
  public CHAVEDocument[] query(String terms, String field, int numHits)
      throws ParseException, IOException {
    if (searcher != null) {
      ArrayList<CHAVEDocument> chaveDocuments = new ArrayList<CHAVEDocument>();
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
          chaveDocuments.add(new CHAVEDocument(doc.get(NUMBER), doc.get(ID),
              doc.get(DATE), doc.get(CATEGORY), doc.get(AUTHOR),
              doc.get(TEXT)));
        }
      }
      return chaveDocuments.toArray(new CHAVEDocument[chaveDocuments.size()]);
    }
    else {
      return null;
    }
  }
}
