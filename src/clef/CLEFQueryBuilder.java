package clef;

/*
 * TODO:
 * new indices (for avoiding duplicate facts)
 * give more weight to facts with more associated answers (through
 * post-processing of the query's results)
 * http://stackoverflow.com/questions/27114691/sorting-linkedhashmap-by-value
 */

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;

import entity.Entity;
import entity.EntityFinder;
import index.InvalidIndexException;
import lemma.LemmatizeException;
import lemma.Lemmatizer;
import nominalization.Nominalizer;
import ontology.LexicalOntology;
import pos.POSTagger;
import token.Tokenizer;
import toponym.ToponymDictionary;

/**
 * This class...
 * 
 * @author Ricardo
 * @version  0.9.9
 *
 */
public class CLEFQueryBuilder {
  public static final String[] NON_MANDATORY = {
      // non mandatory nouns (that are mostly omitted in a candidate answer)
      "abreviatura", "acrónimo", "cargo", "cidade", "cor", "dotação",
      "nacionalidade", "nome", "país", "pessoa",
      // pos tagger classification errors on nouns or proper nouns 
      "indique", "nomeie", "mencione", "onde"
  };

  /*
  public static final boolean USE_TOPONYM_DICTIONARY = true;
  public static final boolean USE_SYNONYMIZER = true;
  public static final boolean USE_NOMINALIZER = true;
   */

  public static final boolean USE_TOPONYM_DICTIONARY = false;
  public static final boolean USE_SYNONYMIZER = false;
  public static final boolean USE_NOMINALIZER = false;

  public static final String[] STOP_WORDS = {"a", "à", "ao", "aos", "aquela",
      "aquelas", "aquele", "aqueles", "aquilo", "as", "às", "até", "com",
      "como", "da", "das", "de", "dela", "delas", "dele", "deles", "depois",
      "do", "dos", "e", "ela", "elas", "ele", "eles", "em", "entre", "era",
      "eram", "éramos", "essa", "essas", "esse", "esses", "esta", "está",
      "estamos", "estão", "estas", "estava", "estavam", "estávamos", "este",
      "esteja", "estejam", "estejamos", "estes", "esteve", "estive",
      "estivemos", "estiver", "estivera", "estiveram", "estivéramos",
      "estiverem", "estivermos", "estivesse", "estivessem", "estivéssemos",
      "estou", "eu", "foi", "fomos", "for", "fora", "foram", "fôramos", "forem",
      "formos", "fosse", "fossem", "fôssemos", "fui", "há", "haja", "hajam",
      "hajamos", "hão", "havemos", "hei", "houve", "houvemos", "houver",
      "houvera", "houveram", "houvéramos", "houverem", "houvermos", "houvesse",
      "houvessem", "houvéssemos", "isso", "isto", "já", "lhe", "lhes", "mais",
      "mas", "me", "mesmo", "meu", "meus", "minha", "minhas", "muito", "na",
      "não", "nas", "nem", "no", "nos", "nós", "nossa", "nossas", "nosso",
      "nossos", "num", "numa", "o", "os", "ou", "para", "pela", "pelas", "pelo",
      "pelos", "por", "qual", "quando", "que", "quem", "são", "se", "seja",
      "sejam", "sejamos", "sem", "será", "serão", "serei", "seremos", "seria",
      "seriam", "seríamos", "seu", "seus", "só", "somos", "sou", "sua", "suas",
      "também", "te", "tem", "têm", "temos", "tenha", "tenham", "tenhamos",
      "tenho", "terá", "terão", "terei", "teremos", "teria", "teriam",
      "teríamos", "teu", "teus", "teve", "tinha", "tinham", "tínhamos", "tive",
      "tivemos", "tiver", "tivera", "tiveram", "tivéramos", "tiverem",
      "tivermos", "tivesse", "tivessem", "tivéssemos", "tu", "tua", "tuas",
      "um", "uma", "você", "vocês", "vos"};

  private Tokenizer tokenizer = null;
  private POSTagger tagger = null;
  private Lemmatizer lemmatizer = null;
  private EntityFinder finder = null;

  private ToponymDictionary toponymDictionary = null;
  private LexicalOntology ontology = null;
  private Nominalizer nominalizer = null;

  /**
   * Creates a new ...
   * 
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
  public CLEFQueryBuilder(Tokenizer tokenizer, POSTagger tagger,
      Lemmatizer lemmatizer, EntityFinder finder,
      ToponymDictionary toponymDictionary, LexicalOntology ontology,
      Nominalizer nominalizer)
          throws IOException, InvalidIndexException {

    this.tokenizer = tokenizer;
    this.tagger = tagger;
    this.lemmatizer = lemmatizer;
    this.finder = finder;

    this.toponymDictionary = toponymDictionary;
    this.ontology = ontology;
    this.nominalizer = nominalizer;
  }

  public String buildQuery(String question) {
    String query = new String();

    LinkedHashSet<String> notMandatory = new LinkedHashSet<String>();
    notMandatory.addAll(Arrays.asList(NON_MANDATORY));

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

    /*
    // identify adjectives
    HashSet<String> adjectives = new HashSet<String>();
    for (int i = 0; i < lemmas.length; i++) {
      if (tags[i].equals("adj")) {
        adjectives.add(lemmas[i]);
      }
    }
     */

    // make entities mandatory in the lucene queries
    for (Entity entity : entities) {
      String text = entity.getText().toLowerCase();
      if (USE_TOPONYM_DICTIONARY) {
        query += " +(";
        String[] keywords = text.split("\\s");
        for (String keyword : keywords) {
          if (!query.matches("(.*)\\b(" + keyword + ")\\b(.*)")) {
            query += " " + keyword + "~";
          }
        }
        // get demonyms
        String[] demonyms = toponymDictionary.retrieveDemonyms(
            entity.getText());
        if (demonyms.length > 0) {
          for (String demonym : demonyms) {
            if (!query.matches("(.*)\\b(" + demonym.toLowerCase()
            + ")\\b(.*)")) {
              query += " " + demonym.toLowerCase() + "~";
            }
          }
        }
        query += " )";
        //////////////
        if (query.endsWith("+( )")) {
          query = query.substring(0, query.length() - "+( )".length());
        }
        //////////////
      }
      else {
        String[] keywords = text.split("\\s");
        for (String keyword : keywords) {
          if (!query.matches("(.*)\\b(" + keyword + ")\\b(.*)")) {
            query += " +" + keyword + "~";
          }
        }
      }
    }

    // if the keyword list is empty at this point, make proper nouns mandatory
    if (query.length() == 0) {
      for (String properNoun : properNouns) {
        String text = properNoun.toLowerCase();
        if (USE_TOPONYM_DICTIONARY) {
          query += " +(";
          String[] keywords = text.split("\\s");
          for (String keyword : keywords) {
            if (!query.matches("(.*)\\b(" + keyword + ")\\b(.*)")) {
              query += " " + keyword + "~";
            }
          }
          // get demonyms
          String[] demonyms = toponymDictionary.retrieveDemonyms(properNoun);
          if (demonyms.length > 0) {
            for (String demonym : demonyms) {
              if (!query.matches("(.*)\\b(" + demonym.toLowerCase()
              + ")\\b(.*)")) {
                query += " " + demonym.toLowerCase() + "~";
              }
            }
          }
          query += " )";
          //////////////
          if (query.endsWith("+( )")) {
            query = query.substring(0, query.length() - "+( )".length());
          }
          //////////////
        }
        else {
          String[] keywords = text.split("\\s");
          for (String keyword : keywords) {
            if (!query.matches("(.*)\\b(" + keyword + ")\\b(.*)")) {
              query += " +" + keyword + "~";
            }
          }
        }
      }
    }

    // if the keyword list is also empty at this point, make nouns mandatory,
    // as long as they are not in the notMandatory list
    if (query.length() == 0) {
      for (String noun : nouns) {
        if (!notMandatory.contains(noun)) {
          if (!query.matches("(.*)\\b(" + noun + ")\\b(.*)")) {
            query += " +" + noun + "~";
          }
        }

        //////////////////////////////////////////////////////////////////////
        /*
        String[] toponyms = topoDict.retrieveToponyms(noun);
         for (String toponym : toponyms) {
          nounQuery += " " + toponym + "~";
        }

        String[] synonyms = synonymizer.synonymize(noun);
        for (String synonym : synonyms) {
          nounQuery += " " + synonym + "~";
        }
         */
        //////////////////////////////////////////////////////////////////////

      }
    }

    // if the keyword list still remains empty at this point, make nouns
    // mandatory even if they are in the notMandatory list
    if (query.length() == 0) {
      for (String noun : nouns) {
        if (!query.matches("(.*)\\b(" + noun + ")\\b(.*)")) {
          query += " +" + noun + "~";
        }
      }
    }

    /*
    for (String adjective : adjectives) {
      if (!query.matches("(.*)\\b(" + adjective + ")\\b(.*)")) {
        query += " +" + adjective + "~";
      }
    }
     */

    // add the remaining words as optional elements in the queries
    for (int i = 0; i < lemmas.length; i++) {
      if (!tags[i].equals("punc")) {
        String[] keywords = lemmas[i].replace("_", " ").split("\\s");
        for (String keyword : keywords) {
          if (!Arrays.asList(STOP_WORDS).contains(keyword)) {
            if (!query.matches("(.*)\\b(" + keyword + ")\\b(.*)")) {
              query += " " + keyword + "~";
            }
          }
        }
      }

      if (USE_SYNONYMIZER) {
        // use synonyms for the verbs
        if (tags[i].startsWith("v") && !lemmas[i].matches("ser|estar|ter")) {
          String[] synonyms = ontology.retrieveRelatedWords(lemmas[i],
              "SINONIMO_V_DE");
          for (String synonym : synonyms) {
            query += " " + synonym + "~";
          }
        }
      }
      if (USE_NOMINALIZER) {
        // create agents from the verbs
        if (tags[i].startsWith("v") && !lemmas[i].matches("ser|estar|ter")) {
          String[] agents = nominalizer.nominalize(lemmas[i], tags[i]);
          for (String agent : agents) {
            query += " " + agent + "~";
          }
        }
      }
    }

    return query.trim();
  }
}
