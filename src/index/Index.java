package index;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

/**
 * This class ...
 * 
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 *
 * @param  <D> ...
 */
public abstract class Index<D> {
  private String[] fields = null;
  private Directory directory = null;
  protected Analyzer analyzer = null;
  protected IndexWriter writer = null;
  protected IndexSearcher searcher = null;

  protected Index(Directory directory, String[] indexFields) {
    analyzer = new WhitespaceAnalyzer();
    this.directory = directory;
    this.fields = indexFields;
  }

  /**
   * This method ...
   * 
   * @throws IOException ...
   */
  public void create() throws IOException {
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    if (writer == null) {
      writer = new IndexWriter(directory, config);
    }
  }

  /**
   * This method ...
   * 
   * @param  document ...
   * @throws IOException ...
   */
  public abstract void add(D document) throws IOException;

  /**
   * This method ...
   * 
   * @throws IOException ...
   */
  public void close() throws IOException  {
    if (writer != null) {
      writer.close();
    }
  }

  /**
   * This method ...
   * 
   * @param  terms ...
   * @param  field ...
   * @return ...
   * @throws ParseException ...
   */
  public String analyze(String terms, String field) throws ParseException {
    Query query = new QueryParser(field, analyzer).parse(terms);
    return query.toString();
  }

  /**
   * This method ...
   * 
   * @throws IOException ...
   */
  public void open() throws IOException {
    if (searcher == null) {
      searcher = new IndexSearcher(DirectoryReader.open(directory));
    }    
  }

  /**
   * This method ...
   * 
   * @param  terms ...
   * @param  field ...
   * @param  numHits ...
   * @return ...
   * @throws ParseException ...
   * @throws IOException ...
   */
  public abstract D[] query(String terms, String field, int numHits)
      throws ParseException, IOException;

  /**
   * This method ...
   * 
   * @return ...
   * @throws IOException ...
   */
  public boolean exists() throws IOException{
    return DirectoryReader.indexExists(directory);
  }

  /**
   * This method ...
   * 
   * @return ...
   * @throws IOException ...
   */
  public boolean isValid() throws IOException {
    if (this.exists()) {
      Set<String> fieldSet = new TreeSet<String>();
      List<LeafReaderContext> leaves =
          DirectoryReader.open(directory).leaves();
      for (LeafReaderContext context : leaves) {
        LeafReader atomicReader = context.reader();
        FieldInfos infos = atomicReader.getFieldInfos();
        for (FieldInfo info : infos) {
          fieldSet.add(info.name);
        }
      }
      if (fieldSet.size() == fields.length) {
        for (String indexField : fields) {
          if (!fieldSet.contains(indexField)) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
}
