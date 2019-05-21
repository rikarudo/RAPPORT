package clef;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

import clef.CLEFQueryBuilder;
import entity.Entity;
import entity.EntityFinder;
import fact.FactAnswer;
import fact.Fact;
import fact.FactIndex;
import index.InvalidIndexException;
import lemma.LemmatizeException;
import lemma.Lemmatizer;
import nominalization.Nominalizer;
import ontology.LexicalOntology;
import pos.POSTagger;
import sentence.Sentence;
import sentence.SentenceIndex;
import token.Tokenizer;
import toponym.ToponymDictionary;

/**
 * This class ...
 * 
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 *
 */
public class CLEFAnswerRetriever {
  public static final int SENTENCE_THRESHOLD = 1024;

  private SentenceIndex sentenceIndex = null;
  private FactIndex factIndex = null;
  private Tokenizer tokenizer = null;
  private POSTagger tagger = null;
  private Lemmatizer lemmatizer = null;
  private EntityFinder finder = null;
  private CLEFQueryBuilder builder = null;

  /**
   * Creates a new ...
   * 
   * @param  sentenceIndex ...
   * @param  factIndex ...
   * @param  tokenizer ...
   * @param  tagger ...
   * @param  lemmatizer ...
   * @param  finder ...
   * @param  toponymDictionary ...
   * @param  ontology ...
   * @param  nominalizer ...
   * @throws InvalidIndexException ...
   * @throws IOException ...
   */
  public CLEFAnswerRetriever(SentenceIndex sentenceIndex, FactIndex factIndex,
      Tokenizer tokenizer, POSTagger tagger, Lemmatizer lemmatizer,
      EntityFinder finder, ToponymDictionary toponymDictionary,
      LexicalOntology ontology, Nominalizer nominalizer)
          throws IOException, InvalidIndexException {
    this.sentenceIndex = sentenceIndex;
    this.factIndex = factIndex;

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

    this.tokenizer = tokenizer;
    this.tagger = tagger;
    this.lemmatizer = lemmatizer;
    this.finder = finder;

    builder = new CLEFQueryBuilder(tokenizer, tagger, lemmatizer, finder,
        toponymDictionary, ontology, nominalizer);
  }

  public LinkedHashMap<String, LinkedHashSet<Fact>> retrieve(String question) {
    LinkedHashMap<String, LinkedHashSet<Fact>> groupedAnswers =
        new LinkedHashMap<String, LinkedHashSet<Fact>>();

    LinkedHashSet<FactAnswer> answers = new LinkedHashSet<FactAnswer>();
    LinkedHashSet<Fact> factList = new LinkedHashSet<Fact>();

    String query = builder.buildQuery(question);

    String[] tokens = tokenizer.tokenize(question, false);
    Entity[] questionEntities = finder.find(tokens);

    LinkedHashSet<Entity> entities = new LinkedHashSet<Entity>();
    LinkedHashSet<String> entitiesText = new LinkedHashSet<String>();

    for (Entity questionEntity : questionEntities) {
      if (!questionEntity.getText().matches("(.*)\\sd[aeo]s?")) {
        entities.add(questionEntity);
        entitiesText.add(questionEntity.getText());
      }
    }

    tokens = tokenizer.tokenize(tokenizer.groupTokens(tokens,
        entitiesText.toArray(new String[entitiesText.size()])), true);

    String[] tags = tagger.tag(tokens);
    String[] lemmas = null;      
    try {
      lemmas = lemmatizer.lemmatize(tokens, tags);
    }
    catch (LemmatizeException le) {
      le.printStackTrace();
    }

    // run the query
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
              + QueryParser.escape(sentence.getID()) +" AND documentID:"
              + QueryParser.escape(sentence.getDocumentID()),
              FactIndex.SENTENCE_ID, SENTENCE_THRESHOLD);

          for (Fact factResult : factResults) {
            factList.add(factResult);
          }
        }
        catch (ParseException pe) {
          pe.printStackTrace();
        }
        catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }

      // identify proper nouns (that are not already classified as entities)
      LinkedHashSet<String> properNouns = new LinkedHashSet<String>();
      for (int i = 0; i < tokens.length; i++) {
        if (tags[i].equals("prop")
            && !entitiesText.contains(tokens[i].replace("_", " "))) {
          properNouns.add(tokens[i].replace("_", " "));
        }
      }

      // identify nouns
      LinkedHashSet<String> nouns = new LinkedHashSet<String>();
      for (int i = 0; i < lemmas.length; i++) {
        if (tags[i].equals("n")) {
          nouns.add(lemmas[i]);
        }
      }

      // add entities and proper nouns to the mandatory keywords
      LinkedHashSet<String> mandatoryKeywords = new LinkedHashSet<String>();
      for (Entity entity : entities) {
        mandatoryKeywords.add(
            entity.getText().toLowerCase());
      }
      // add the proper nouns to the mandatory keywords if the list is empty
      //if (mandatoryKeywords.size() == 0) {
      for (String properNoun : properNouns) {
        mandatoryKeywords.add(properNoun.toLowerCase().replace("_", " "));
      }
      //}
      // add the nouns to the mandatory keywords if the list is empty
      if (mandatoryKeywords.size() == 0) {
        mandatoryKeywords.addAll(nouns);
      }

      String questionVerb = null;
      for (int i = 0; i < lemmas.length; i++) {
        if (tags[i].startsWith("v")) {
          questionVerb = lemmas[i];
        }
      }

      for (Fact fact : factList) {
        if ((questionVerb != null)
            && (fact.getPredicate().equals(questionVerb))) {
          for (String mandatoryKeyword : mandatoryKeywords) {
            // check if the mandatory keywords are present in the triples
            if (fact.getSubject().toLowerCase().contains(
                mandatoryKeyword)) {
              answers.add(new FactAnswer(fact.getObject(), fact));
            }
            else if (fact.getObject().toLowerCase().contains(
                mandatoryKeyword)) {
              answers.add(new FactAnswer(fact.getSubject(), fact));
            }
          }
        }
        else {
          //////////////////////////////////////////////////////////////////
          for (String mandatoryKeyword : mandatoryKeywords) {
            // check if the mandatory keywords are present in the triples
            if (fact.getSubject().toLowerCase().contains(
                mandatoryKeyword)) {
              answers.add(new FactAnswer(fact.getObject(), fact));
            }
            else if (fact.getObject().toLowerCase().contains(
                mandatoryKeyword)) {
              answers.add(new FactAnswer(fact.getSubject(), fact));
            }
          }
        }
      }
    }

    for (FactAnswer answer : answers) {
      if (groupedAnswers.containsKey(answer.getAnswer())) {
        LinkedHashSet<Fact> facts = groupedAnswers.get(answer.getAnswer());
        facts.add(answer.getFact());
        groupedAnswers.put(answer.getAnswer(), facts);        
      }
      else {
        LinkedHashSet<Fact> facts = new LinkedHashSet<Fact>();
        facts.add(answer.getFact());
        groupedAnswers.put(answer.getAnswer(), facts);
      }
    }

    return groupedAnswers;
  }
}
