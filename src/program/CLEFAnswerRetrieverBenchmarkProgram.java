package program;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import nominalization.Nominalizer;
import ontology.LexicalOntology;
import opennlp.tools.util.InvalidFormatException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.SAXException;

import chave.CHAVEDocumentIndex;
import clef.CLEFAnswer;
import clef.CLEFAnswerRetriever;
import clef.CLEFQuestion;
import dictionary.DictionaryLoadException;
import entity.Entity;
import entity.EntityFinder;
import fact.Fact;
import fact.FactIndex;
import index.InvalidIndexException;
import lemma.Lemmatizer;
import lexicon.LexiconLoadException;
import pos.POSTagger;
import rank.WordRankingLoadException;
import sentence.Sentence;
import sentence.SentenceIndex;
import token.Tokenizer;
import toponym.ToponymDictionary;
import toponym.ToponymDictionaryLoadException;
import triple.TripleLoadException;
import util.XMLTools;
import wikipedia.WikipediaDocumentIndex;

/*
  TODO: determine what to do with the document indices;
  right now, they are just open, nothing more, as the data needed for the
  benchmark are all found in the sentence and fact indices...
 */

/**
 * This class has in itself a <code>main</code> method for retrieving the
 * <em>answers</em> to the <em>CLEF questons</em>, providing a benchamark...
 * 
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 *
 */
public class CLEFAnswerRetrieverBenchmarkProgram {

  // TODO: put these in an external configuration
  public static boolean USE_TYPES = true;
  //public static boolean ORDER_KEYS = true;
  public static boolean ORDER_KEYS = false;
  public static boolean LIMIT_TO_KNOWN_ANSWERS = false;

  public static void main(String[] args) {
    LocalDateTime startTime = LocalDateTime.now();
    System.out.println("CLEFAnswerRetrieverBenchmarkProgram");
    System.out.println();
    System.out.println("Start Time: " + startTime);
    System.out.println();

    String clefQuestionsFile = null;
    String chaveDocumentIndexDirectory = null;
    String wikipediaDocumentIndexDirectory = null;
    String sentenceIndexDirectory = null;
    String factIndexDirectory = null;

    String[] clefYears = null;
    String category = null;
    String type = null;
    int answerLimit = 1024;

    WikipediaDocumentIndex wikipediaDocumentIndex = null;
    CHAVEDocumentIndex chaveDocumentIndex = null;
    SentenceIndex sentenceIndex = null;
    FactIndex factIndex = null;

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-q")) {
        if (i++ < args.length) {
          clefQuestionsFile = args[i];
        }
      }
      else if (args[i].equals("-cd")) {
        if (i++ < args.length) {
          chaveDocumentIndexDirectory = args[i];
        }
      }
      else if (args[i].equals("-wd")) {
        if (i++ < args.length) {
          wikipediaDocumentIndexDirectory = args[i];
        }
      }
      else if (args[i].equals("-s")) {
        if (i++ < args.length) {
          sentenceIndexDirectory = args[i];
        }
      }
      else if (args[i].equals("-f")) {
        if (i++ < args.length) {
          factIndexDirectory = args[i];
        }
      }
      else if (args[i].equals("-y")) {
        if (i++ < args.length) {
          clefYears = args[i].split(",");
        }
      }
      else if (args[i].equals("-c")) {
        if (i++ < args.length) {
          category = args[i];
        }
      }
      else if (args[i].equals("-t")) {
        if (i++ < args.length) {
          type = args[i];
        }
      }
      else if (args[i].equals("-l")) {
        if (i++ < args.length) {
          answerLimit = Integer.parseInt(args[i]);
        }
      }
    }

    String usage = "java program.CLEFAnswerRetrieverBenchmarkProgram "
        + "-q clefQuestionsFile\n-cd chaveDocumentIndexDirectory "
        + "-wd wikipediaDocumentIndexDirectory"
        + "-s sentenceIndexDirectory -f factIndexDirectory\n"
        + "-y clefYears -l answerLimit\n\n"
        + "You must specifiy the file with the QA@CLEF questions, that are "
        + "used to query\nthe system, and also the location where the "
        + "indices are located.\n\n"
        + "The program's options are:\n"
        + "-q: the location of the CLEF questions file\n"
        + "-cd: where the CHAVE document index is located"
        + "(optional only if a Wikipedia document index is specified)\n"
        + "-wd: where the Wikipedia document index is located"
        + "(optional only if a CHAVE document index is specified)\n"
        + "-s: where the sentence index is located\n"
        + "-f: where the fact index is located\n"
        + "-y: the question years to be addresses (optional, multiple years"
        + " must be separated by commas without spaces)\n"
        + "-c: the category filter for questions (optional)\n"
        + "-t: the type filter for questions (optional)\n"
        + "-l: the maximum number of answers per question\n\n";
    if ((clefQuestionsFile == null) || ((chaveDocumentIndexDirectory == null)
        && (wikipediaDocumentIndexDirectory == null))
        || (sentenceIndexDirectory == null) || (factIndexDirectory == null)) {
      System.out.println(usage);
      System.out.println();
      System.exit(0);
    }

    System.out.println("Command line parameters passed:");
    System.out.println("-q: " + clefQuestionsFile);
    if (chaveDocumentIndexDirectory == null) {
      System.out.println("-cd: [not set]");
    }
    else {
      System.out.println("-cd: " + chaveDocumentIndexDirectory);
    }
    if (wikipediaDocumentIndexDirectory == null) {
      System.out.println("-wd: [not set]");      
    }
    else {
      System.out.println("-wd: " + wikipediaDocumentIndexDirectory);
    }
    System.out.println("-s: " + sentenceIndexDirectory);
    System.out.println("-f: " + factIndexDirectory);
    if (clefYears != null) {
      System.out.println("-y: " + Arrays.toString(clefYears));
    }
    else {
      System.out.println("-y: [all years]");      
    }
    if (category != null) {
      System.out.println("-c: " + category);
    }
    else {
      System.out.println("-c: [all categories]");      
    }
    if (type != null) {
      System.out.println("-t: " + type);
    }
    else {
      System.out.println("-t: [all types]");      
    }
    System.out.println("-l: " + answerLimit);
    System.out.println();

    try {
      chaveDocumentIndex = new CHAVEDocumentIndex(FSDirectory.open(
          FileSystems.getDefault().getPath(chaveDocumentIndexDirectory)));
      wikipediaDocumentIndex = new WikipediaDocumentIndex(FSDirectory.open(
          FileSystems.getDefault().getPath(chaveDocumentIndexDirectory)));
      sentenceIndex = new SentenceIndex(FSDirectory.open(
          FileSystems.getDefault().getPath(sentenceIndexDirectory)));
      factIndex = new FactIndex(FSDirectory.open(
          FileSystems.getDefault().getPath(factIndexDirectory)));
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }

    Tokenizer tokenizer = null;
    POSTagger tagger = null;
    Lemmatizer lemmatizer = null;
    EntityFinder finder = null;
    ToponymDictionary toponymDictionary = null;
    LexicalOntology ontology = null;
    Nominalizer nominalizer = null;

    try {
      tokenizer = new Tokenizer();
      tagger = new POSTagger();
      lemmatizer = new Lemmatizer();
      finder = new EntityFinder();
      toponymDictionary = new ToponymDictionary();
      ontology = new LexicalOntology();
      nominalizer = new Nominalizer();
    }
    catch (NumberFormatException nfe) {
      nfe.printStackTrace();
    }
    catch (DictionaryLoadException dle) {
      dle.printStackTrace();
    }
    catch (WordRankingLoadException wrle) {
      wrle.printStackTrace();
    }
    catch (ToponymDictionaryLoadException tdle) {
      tdle.printStackTrace();
    }
    catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
    }
    catch (InvalidPropertiesFormatException ipfe) {
      ipfe.printStackTrace();
    }
    catch (InvalidFormatException ife) {
      ife.printStackTrace();
    }
    catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    }
    catch (SAXException saxe) {
      saxe.printStackTrace();
    }
    catch (LexiconLoadException lle) {
      lle.printStackTrace();
    }
    catch (TripleLoadException tle) {
      tle.printStackTrace();
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }

    CLEFQuestion[] clefQuestions = null;
    try {
      clefQuestions = XMLTools.readCLEFQuestions(clefQuestionsFile, "pt");
    }
    catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
    }
    catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    }
    catch (SAXException saxe) {
      saxe.printStackTrace();
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    CLEFAnswerRetriever retriever = null;
    try {
      retriever = new CLEFAnswerRetriever(sentenceIndex, factIndex, tokenizer,
          tagger, lemmatizer, finder, toponymDictionary, ontology, nominalizer);
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
    catch (InvalidIndexException chaveie) {
      chaveie.printStackTrace();
    }

    // HASHMAP mapping question types to fact entity types
    // TODO: put this in an external file
    HashMap<String, String[]> questionToEntityTypes
    = new HashMap<String, String[]>();
    questionToEntityTypes.put("COUNT", new String[]{"numeric"});
    questionToEntityTypes.put("LOCATION", new String[]{"place"});
    questionToEntityTypes.put("MANNER", new String[]{});
    questionToEntityTypes.put("MEASURE", new String[]{"numeric"});
    questionToEntityTypes.put("OBJECT", new String[]{"thing", "artprod"});
    questionToEntityTypes.put("ORGANIZATION", new String[]{"organization"});
    questionToEntityTypes.put("OTHER", new String[]{"event", "abstract"});
    questionToEntityTypes.put("PERSON", new String[]{"person"});
    questionToEntityTypes.put("TIME", new String[]{"time"});

    System.out.println("Processing CLEF questions...");
    System.out.println();

    int questions = 0;
    int rightFacts = 0;
    int answeredQuestions = 0;
    //int totalKnownAnswers = 0;
    //int totalRetrievedAnswers = 0;
    int nilAnswers = 0;
    int nilAnswersRight = 0;

    ArrayList<String> clefYearsList = null;
    if (clefYears != null) {
      clefYearsList = new ArrayList<String>(Arrays.asList(clefYears));
    }

    for (CLEFQuestion clefQuestion : clefQuestions) {
      if (((clefYearsList != null)
          && (!clefYearsList.contains(clefQuestion.getYear())))
          || ((category != null)
              && (!category.equalsIgnoreCase(clefQuestion.getCategory())))
          || ((type != null)
              && (!type.equalsIgnoreCase(clefQuestion.getType())))) {
        System.out.println("Question not in the specified years list, "
            + "or of a different category or type...");
        continue;
      }

      questions++;
      boolean alreadyAnsweredQuestion = false;

      String question = clefQuestion.getText();

      System.out.println("---------------------------------------------------");
      System.out.println(question);
      System.out.println("[TYPE: " + clefQuestion.getType()
      + " | CATEGORY: " + clefQuestion.getCategory() + "]");
      System.out.println("---------------------------------------------------");      

      CLEFAnswer[] clefKnownAnswers = clefQuestion.getAnswers();
      LinkedHashSet<String> clefKnownAnswersDocIDs =
          new LinkedHashSet<String>();
      LinkedHashSet<String> clefKnownAnswersTexts = new LinkedHashSet<String>();

      //totalKnownAnswers += clefKnownAnswers.length;

      if (clefKnownAnswers.length == 1) {
        if (clefKnownAnswers[0].getText().equals("NIL")) {
          nilAnswers++;
        }
      }
      if (clefKnownAnswers.length == 0) {
        nilAnswers++;
      }

      for (int i = 0; i < clefKnownAnswers.length; i++) {
        clefKnownAnswersDocIDs.add(clefKnownAnswers[i].getDocID());
        clefKnownAnswersTexts.add(clefKnownAnswers[i].getText());
      }

      LinkedHashMap<String, LinkedHashSet<Fact>> facts =
          retriever.retrieve(question);

      System.out.println();
      System.out.println("Answer(s) found:");
      Set<String> keys = facts.keySet();
      if (keys.size() == 0) {
        System.out.println("NONE (NIL)!");
        if (clefKnownAnswers.length == 1) {
          if (clefKnownAnswers[0].getText().equals("NIL")) {
            nilAnswersRight++;
            rightFacts++;
            if (!alreadyAnsweredQuestion) {
              answeredQuestions++;
              alreadyAnsweredQuestion = true;
            }
          }
        }
        if (clefKnownAnswers.length == 0) {
          nilAnswersRight++;
          rightFacts++;
          if (!alreadyAnsweredQuestion) {
            answeredQuestions++;
            alreadyAnsweredQuestion = true;
          }
        }
        continue;
      }

      // order the answers based on the number of facts having the keys
      Vector<String> orderedFactKeys = new Vector<String>();

      if (ORDER_KEYS) {
        int maxFacts = 0;
        for (String key : keys) {
          if (facts.get(key).size() > maxFacts) {
            maxFacts = facts.get(key).size();
          }
        }
        for (int i = maxFacts; i > 0; i--) {
          for (String key : keys) {
            if (facts.get(key).size() == i) {
              orderedFactKeys.add(key);
            }
          }
        }
      }
      else {
        orderedFactKeys.clear();
        for (String key : keys) {
          orderedFactKeys.add(key);
        }      
      }

      ////

      Vector<String> sameTypeAnswers = new Vector<String>();
      Vector<String> otherTypeAnswers = new Vector<String>();

      for (String key : orderedFactKeys) {
        ArrayList<String> expectedTypes = new ArrayList<String>(Arrays.asList(
            questionToEntityTypes.get(clefQuestion.getType())));

        Entity[] entities = finder.find(key.split("\\s"));
        for (Entity entity : entities) {
          if (expectedTypes.contains(entity.getType())) {
            sameTypeAnswers.add(key);
          }
          else {
            otherTypeAnswers.add(key);
          }
        }
      }

      int currentAnswer = 0;

      ////

      if (USE_TYPES) {
        for (String key: sameTypeAnswers) {
          boolean match = false;
          // only accept candidate answers if they contain 3 or more
          // characters or if they are numbers
          if ((key.length() >= 3) || (key.matches("^[+-]?\\d+$"))) {
            currentAnswer++;
            if (currentAnswer > answerLimit) {
              break;
            }
            //totalRetrievedAnswers++;

            for (String clefKnownAnswerText : clefKnownAnswersTexts) {
              String[] clefTokens = tokenizer.tokenize(
                  clefKnownAnswerText, true);
              String clefText = new String();
              for (String clefToken : clefTokens) {
                clefText += clefToken + " ";
              }
              clefText = clefText.trim().toLowerCase();
              if (clefText.length() > 0) {
                if (key.toLowerCase().contains(clefText)
                    || clefText.contains(key.toLowerCase())) {
                  if (LIMIT_TO_KNOWN_ANSWERS) {
                    LinkedHashSet<Fact> answerFacts = facts.get(key);
                    for (Fact answerFact : answerFacts) {
                      if (clefKnownAnswersDocIDs.contains(
                          answerFact.getDocumentID())) {
                        match = true;
                        break;
                      }
                    }
                  }
                  else {
                    match = true;
                  }
                }
              }
            }

            if (match) {
              rightFacts++;
              if (!alreadyAnsweredQuestion) {
                answeredQuestions++;
                alreadyAnsweredQuestion = true;
              }

              System.out.println();
              System.out.println("> Short answer: " + key);
              /*
              System.out.println("> Short answer: " + key
                  + " [ " + clefQuestion.getType() + " || "
                  + finder.annotate(key.split("\\s")) + " ]");
               */
              System.out.println("-------------------------------------------");

              LinkedHashSet<Fact> answerFacts = facts.get(key);
              for (Fact answerFact : answerFacts) {
                System.out.println(answerFact);
                Sentence[] sentenceResults = null;
                try {
                  sentenceResults = sentenceIndex.query("id:"
                      + answerFact.getSentenceID() + " AND documentID:"
                      + answerFact.getDocumentID(),
                      SentenceIndex.LEMMATIZED_TEXT, 1000);
                }
                catch (ParseException pe) {
                  pe.printStackTrace();
                }
                catch (IOException ioe) {
                  ioe.printStackTrace();
                }
                for (int i = 0; i < sentenceResults.length;i++) {
                  System.out.println(sentenceResults[i].getTokenizedText());
                }
                System.out.println(answerFact.getDocumentID());
                System.out.println();
              }
            }
          }
        }

        for (String key: otherTypeAnswers) {
          boolean match = false;
          // only accept candidate answers if they contain 3 or more
          // characters or if they are numbers
          if ((key.length() >= 3) || (key.matches("^[+-]?\\d+$"))) {
            currentAnswer++;
            if (currentAnswer > answerLimit) {
              break;
            }
            //totalRetrievedAnswers++;

            for (String clefKnownAnswerText : clefKnownAnswersTexts) {
              String[] clefTokens = tokenizer.tokenize(
                  clefKnownAnswerText, true);
              String clefText = new String();
              for (String clefToken : clefTokens) {
                clefText += clefToken + " ";
              }
              clefText = clefText.trim().toLowerCase();
              if (clefText.length() > 0) {
                if (key.toLowerCase().contains(clefText)
                    || clefText.contains(key.toLowerCase())) {
                  if (LIMIT_TO_KNOWN_ANSWERS) {
                    LinkedHashSet<Fact> answerFacts = facts.get(key);
                    for (Fact answerFact : answerFacts) {
                      if (clefKnownAnswersDocIDs.contains(
                          answerFact.getDocumentID())) {
                        match = true;
                        break;
                      }
                    }
                  }
                  else {
                    match = true;
                  }
                }
              }
            }

            if (match) {
              rightFacts++;
              if (!alreadyAnsweredQuestion) {
                answeredQuestions++;
                alreadyAnsweredQuestion = true;
              }

              System.out.println();
              System.out.println("> Short answer: " + key);
              /*
              System.out.println("> Short answer: " + key
                  + " [ " + clefQuestion.getType() + " || "
                  + finder.annotate(key.split("\\s")) + " ]");
               */
              System.out.println("-------------------------------------------");

              LinkedHashSet<Fact> answerFacts = facts.get(key);
              for (Fact answerFact : answerFacts) {
                System.out.println(answerFact);
                Sentence[] sentenceResults = null;
                try {
                  sentenceResults = sentenceIndex.query("id:"
                      + answerFact.getSentenceID() + " AND documentID:"
                      + answerFact.getDocumentID(),
                      SentenceIndex.LEMMATIZED_TEXT, 1000);
                }
                catch (ParseException pe) {
                  pe.printStackTrace();
                }
                catch (IOException ioe) {
                  ioe.printStackTrace();
                }
                for (int i = 0; i < sentenceResults.length;i++) {
                  System.out.println(sentenceResults[i].getTokenizedText());
                }
                System.out.println(answerFact.getDocumentID());
                System.out.println();
              }
            }
          }
        }
      }
      else {
        for (String key : orderedFactKeys) {
          boolean match = false;
          // only accept candidate answers if they contain 3 or more
          // characters or if they are numbers
          if ((key.length() >= 3) || (key.matches("^[+-]?\\d+$"))) {
            currentAnswer++;
            if (currentAnswer > answerLimit) {
              break;
            }
            //totalRetrievedAnswers++;

            for (String clefKnownAnswerText : clefKnownAnswersTexts) {
              String[] clefTokens = tokenizer.tokenize(
                  clefKnownAnswerText, true);
              String clefText = new String();
              for (String clefToken : clefTokens) {
                clefText += clefToken + " ";
              }
              clefText = clefText.trim().toLowerCase();
              if (clefText.length() > 0) {
                if (key.toLowerCase().contains(clefText)
                    || clefText.contains(key.toLowerCase())) {
                  if (LIMIT_TO_KNOWN_ANSWERS) {
                    LinkedHashSet<Fact> answerFacts = facts.get(key);
                    for (Fact answerFact : answerFacts) {
                      if (clefKnownAnswersDocIDs.contains(
                          answerFact.getDocumentID())) {
                        match = true;
                        break;
                      }
                    }
                  }
                  else {
                    match = true;
                  }
                }
              }
            }

            if (match) {
              rightFacts++;
              if (!alreadyAnsweredQuestion) {
                answeredQuestions++;
                alreadyAnsweredQuestion = true;
              }

              System.out.println();
              System.out.println("> Short answer: " + key);
              /*
              System.out.println("> Short answer: " + key
                  + " [ " + clefQuestion.getType() + " || "
                  + finder.annotate(key.split("\\s")) + " ]");
               */
              System.out.println("-------------------------------------------");

              LinkedHashSet<Fact> answerFacts = facts.get(key);
              for (Fact answerFact : answerFacts) {
                System.out.println(answerFact);
                Sentence[] sentenceResults = null;
                try {
                  sentenceResults = sentenceIndex.query("id:"
                      + answerFact.getSentenceID() + " AND documentID:"
                      + answerFact.getDocumentID(),
                      SentenceIndex.LEMMATIZED_TEXT, 1000);
                }
                catch (ParseException pe) {
                  pe.printStackTrace();
                }
                catch (IOException ioe) {
                  ioe.printStackTrace();
                }
                for (int i = 0; i < sentenceResults.length;i++) {
                  System.out.println(sentenceResults[i].getTokenizedText());
                }
                System.out.println(answerFact.getDocumentID());
                System.out.println();
              }
            }
          }
        }
      }
      System.out.println();
    }

    System.out.println("Closing indices...");
    System.out.println();
    try {
      chaveDocumentIndex.close();
      wikipediaDocumentIndex.close();
      sentenceIndex.close();
      factIndex.close();
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }

    System.out.println();
    System.out.println(">> QUESTIONS*:    " + questions + " <<");
    System.out.println(">> ANSWERS*:      " + answeredQuestions + " <<");
    System.out.println(">> ACCURACY*:     "
        + ((float) answeredQuestions / questions * 100) + "% <<");
    System.out.println(">> NIL:           " + nilAnswersRight + "/"
        + nilAnswers + "=" + ((float) nilAnswersRight / nilAnswers));
    System.out.println(">> FACTS (RIGHT): " + rightFacts + " <<");
    /*
    // TODO: these values are inaccurate when not limiting the correct answers
    // to the documents already known, as the number of facts easily go over
    // the number of known answers -- something other that rightFacts should
    // be used
    System.out.println(">> PRECISION:     "
        + ((float) rightFacts / totalKnownAnswers * 100) + "% <<");
    System.out.println(">> RECALL:        "
        + ((float) rightFacts / totalRetrievedAnswers * 100) + "% <<");
     */
    System.out.println();

    LocalDateTime endTime = LocalDateTime.now();
    System.out.println("Done!");
    System.out.println();
    System.out.println("End Time: " + endTime);
    System.out.println("Elapsed Time: " + Duration.between(startTime, endTime));
  }
}

/*
COUNT => numeric
LOCATION => place
MANNER => ?
MEASURE => numeric
OBJECT => thing, artprod
ORGANIZATION => organization
OTHER => event, abstract
PERSON => person
TIME => time
 */
