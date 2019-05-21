package wikipedia;

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
 * @version  0.9.9
 *
 */
public class WikipediaDocumentIndex extends Index<WikipediaDocument> {
  public static final String ID = "id";
  public static final String TITLE = "title";
  public static final String TEXT = "text";
  public static final String CATEGORIES = "categories";

  /**
   * 
   */
  public static final String[] FIELDS = new String[] {
      ID, TITLE, TEXT, CATEGORIES
  };

  /**
   * Creates a new ...
   * 
   * @param  directory ...
   */
  public WikipediaDocumentIndex(Directory directory) {
    super(directory, FIELDS);
  }

  /**
   * This method ...
   * 
   * @param wikipediaDocument ...
   */
  public void add(WikipediaDocument wikipediaDocument) throws IOException {
    if (writer != null) {
      Document doc = new Document();
      doc.add(new StringField(ID, wikipediaDocument.getID(),
          Field.Store.YES));
      doc.add(new StringField(TITLE, wikipediaDocument.getTitle(),
          Field.Store.YES));
      doc.add(new TextField(TEXT, wikipediaDocument.getText(),
          Field.Store.YES));
      doc.add(new StringField(CATEGORIES,
          wikipediaDocument.getCategories(), Field.Store.YES));
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
  public WikipediaDocument[] query(String terms, String field, int numHits)
      throws ParseException, IOException {
    if (searcher != null) {
      ArrayList<WikipediaDocument> wikiDocuments =
          new ArrayList<WikipediaDocument>();
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
          wikiDocuments.add(new WikipediaDocument(doc.get(ID),
              doc.get(TITLE), doc.get(TEXT), doc.get(CATEGORIES)));
        }
      }
      return wikiDocuments.toArray(
          new WikipediaDocument[wikiDocuments.size()]);
    }
    else {
      return null;
    }
  }
}
