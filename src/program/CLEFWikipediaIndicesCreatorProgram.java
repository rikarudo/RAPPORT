package program;

import chunk.Chunk;
import chunk.Chunker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;

import javax.xml.parsers.ParserConfigurationException;

import lemma.LemmatizeException;
import lemma.Lemmatizer;

import org.apache.lucene.store.FSDirectory;
import org.maltparser.core.exception.MaltChainedException;
import org.xml.sax.SAXException;

import wikipedia.WikipediaDocument;
import wikipedia.WikipediaDocumentIndex;
import dependency.CoNLLToken;
import dependency.DependencyChunk;
import dependency.DependencyChunker;
import dependency.DependencyParser;
import dependency.DependencyParsingException;
import dictionary.DictionaryLoadException;
import entity.Entity;
import entity.EntityFinder;
import fact.Fact;
import fact.FactExtractor;
import fact.FactIndex;
import pos.POSTagger;
import rank.WordRankingLoadException;
import sentence.Sentence;
import sentence.SentenceIndex;
import split.SentenceSplitter;
import token.Tokenizer;
import triple.Triple;
import util.GeneralTools;
import util.XMLTools;

/**
 * This class has in itself a <code>main</code> method for creating the
 * <em>indices</em> with the <code>WikipediaDocument</code>s...
 * 
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 *
 */
public class CLEFWikipediaIndicesCreatorProgram {
  // TODO: put this in an external configuration file or ask for it somewhere
  public static String ARTICLE_EXCLUSIONS = "(~|\\(desambiguação\\))";

  public static void main(String[] args) {
    LocalDateTime startTime = LocalDateTime.now();
    System.out.println("CLEFWikipediaIndicesCreatorProgram");
    System.out.println();
    System.out.println("Start Time: " + startTime);
    System.out.println();

    String wikipediaCorpusDirectory = null;
    String documentIndexDirectory = null;
    String sentenceIndexDirectory = null;
    String factIndexDirectory = null;

    WikipediaDocumentIndex documentIndex = null;
    SentenceIndex sentenceIndex = null;
    FactIndex factIndex = null;

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-c")) {
        if (i++ < args.length) {
          wikipediaCorpusDirectory = args[i];
        }
      }
      else if (args[i].equals("-d")) {
        if (i++ < args.length) {
          documentIndexDirectory = args[i];
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
    }

    String usage = "java program.CLEFWikipediaIndicesCreatorProgram "
        + "-c wikipediaCorpusDirectory\n-d documentIndexDirectory "
        + "-s sentenceIndexDirectory -f factIndexDirectory\n\n"
        + "You must specifiy the location of the HTML data files, that are "
        + "used to populate\nthe indices, and also the location where those "
        + "indices should be created (or appended).\n\n"
        + "The program's options are:\n"
        + "-c: the location of the Wikipedia HTML data files used in CLEF "
        + "(if a directory, it will be\n\trecursively listed)\n"
        + "-d: where the document index should be created\n"
        + "-s: where the sentence index should be created\n"
        + "-f: where the fact index should be created\n\n";
    if ((wikipediaCorpusDirectory == null) || documentIndexDirectory == null
        || sentenceIndexDirectory == null || factIndexDirectory == null) {
      System.out.println(usage);
      System.out.println();
      System.exit(0);
    }

    System.out.println("Starting...");
    System.out.println();
    System.out.println("Creating indices...");
    System.out.println();

    try {
      documentIndex = new WikipediaDocumentIndex(FSDirectory.open(
          FileSystems.getDefault().getPath(documentIndexDirectory)));
      sentenceIndex = new SentenceIndex(FSDirectory.open(
          FileSystems.getDefault().getPath(sentenceIndexDirectory)));
      factIndex = new FactIndex(FSDirectory.open(
          FileSystems.getDefault().getPath(factIndexDirectory)));
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }

    SentenceSplitter splitter =  null;
    Tokenizer tokenizer =  null;
    POSTagger tagger =  null;
    Lemmatizer lemmatizer =  null;
    Chunker chunker =  null;
    EntityFinder finder =  null;
    FactExtractor extractor = null;
    DependencyParser parser = null;

    try {
      splitter = new SentenceSplitter();
      tokenizer = new Tokenizer();
      tagger = new POSTagger();
      lemmatizer = new Lemmatizer();
      chunker = new Chunker();
      finder = new EntityFinder();
      extractor = new FactExtractor();
      parser = new DependencyParser();
    }
    catch (NumberFormatException nfe) {
      nfe.printStackTrace();
    }
    catch (DictionaryLoadException cle) {
      cle.printStackTrace();
    }
    catch (WordRankingLoadException wrle) {
      wrle.printStackTrace();
    }
    catch (InvalidPropertiesFormatException ipfe) {
      ipfe.printStackTrace();
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
    catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    }
    catch (SAXException saxe) {
      saxe.printStackTrace();
    }
    catch (MaltChainedException mce) {
      mce.printStackTrace();
    }

    try {
      documentIndex.create();
      sentenceIndex.create();
      factIndex.create();
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }

    System.out.println("Creating and populating indices...");
    System.out.println("(Depending on the contents, it may take quite a long "
        + "time...)");

    File[] editions = null;
    try {
      editions = GeneralTools.listFiles(new File(wikipediaCorpusDirectory),
          "html");
    }
    catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
    }

    System.out.println("Processing files (articles)...");
    if (editions.length > 0) {
      for (File edition : editions) {
        boolean sentencesWithFactsFound = false;
        System.out.println("input: " + edition.getAbsolutePath());
        if (!edition.getName().matches("(.*)" + ARTICLE_EXCLUSIONS + "(.*)")) {
          System.out.println("Processing: " + edition.getName());
          WikipediaDocument document = null;
          try {
            document = XMLTools.readCLEFWikipediaArticle(edition);
          }
          catch (IOException ioe) {
            ioe.printStackTrace();
          }

          String text = document.getText();
          if (document.getCategories().length() > 0) {
            String[] categories = document.getCategories().split("\n");
            for (String category : categories) {
              text = text.concat("\n" + document.getTitle() + " ser " + category
                  + ".");
            }
          }
          String[] sentences = splitter.split(text, true);
          for (int i = 0; i < sentences.length; i++) {
            boolean factsFound = false;
            HashSet<Fact> temporaryFacts = new HashSet<Fact>();
            String[] tokens = tokenizer.tokenize(sentences[i], false);
            HashSet<Entity> entities = new HashSet<Entity>(
                Arrays.asList(finder.find(tokens)));
            tokens = tokenizer.tokenize(finder.bindEntityTokens(tokens), true);
            String[] tags = tagger.tag(tokens);
            String[] lemmas = null;
            try {
              lemmas = lemmatizer.lemmatize(tokens, tags);
            }
            catch (LemmatizeException le) {
              le.printStackTrace();
            }
            HashSet<String> entitiesText = new HashSet<String>();
            for (Entity entity : entities) {
              entitiesText.add(entity.getText());
            }
            HashSet<String> properNouns = new HashSet<String>();
            for (int j = 0; j < tokens.length; j++) {
              if (tags[j].equals("prop") && !entitiesText.contains(tokens[j])) {
                properNouns.add(tokens[j]);
              }
            }

            StringBuffer tokenBuffer = new StringBuffer();
            for (String token : tokens) {
              tokenBuffer.append(token + " ");
            }
            StringBuffer lemmaBuffer = new StringBuffer();
            for (String lemma : lemmas) {
              lemmaBuffer.append(lemma + " ");
            }

            // temporaryFacts will have no id (null) -- beware!
            Triple[] facts = null;
            // for OpenNLP chunks // entities //////////////////////////////////
            Chunk[] chunks = chunker.chunkAsBlocks(tokens, tags, lemmas);
            facts = extractor.extract(chunks,
                entities.toArray(new Entity[entities.size()]));
            for (Triple fact : facts) {
              temporaryFacts.add(new Fact(null,
                  fact.getSubject().replace("_", " "),
                  fact.getPredicate().replace("_", " "),
                  fact.getObject().replace("_", " "),
                  Integer.toString(i), document.getID()));
            }

            // for MaltParser based chunks // entities /////////////////////////
            CoNLLToken[] conllTokens = null;
            try {
              conllTokens = parser.parseAsCoNLLToken(tokens, tags, lemmas);
            }
            catch (MaltChainedException mce) {
              mce.printStackTrace();
            }
            catch (DependencyParsingException dpe) {
              dpe.printStackTrace();
            }
            DependencyChunker dependency = null;
            try {
              dependency = new DependencyChunker(conllTokens);
            }
            catch (InvalidPropertiesFormatException ipfe) {
              ipfe.printStackTrace();
            }
            catch (IOException ioe) {
              ioe.printStackTrace();
            }

            DependencyChunk[] dependencyChunks = dependency.parseRootChunks();
            facts = extractor.extract(dependencyChunks,
                entities.toArray(new Entity[entities.size()]), true);
            for (Triple fact : facts) {
              temporaryFacts.add(new Fact(null,
                  fact.getSubject().replace("_", " "),
                  fact.getPredicate().replace("_", " "),
                  fact.getObject().replace("_", " "),
                  Integer.toString(i), document.getID()));
            }

            // USING PROPER NOUNS INSTEAD OF NAMED ENTITIES ////////////////////

            // for OpenNLP chunks // proper nouns //////////////////////////////
            facts = extractor.extract(chunks,
                properNouns.toArray(new String[properNouns.size()]));
            for (Triple fact : facts) {
              temporaryFacts.add(new Fact(null,
                  fact.getSubject().replace("_", " "),
                  fact.getPredicate().replace("_", " "),
                  fact.getObject().replace("_", " "),
                  Integer.toString(i), document.getID()));
            }

            // for MaltParser based chunks // proper nouns /////////////////////
            facts = extractor.extract(dependencyChunks,
                properNouns.toArray(new String[properNouns.size()]), true);
            for (int j = 0; j < facts.length; j++) {
              for (Triple fact : facts) {
                temporaryFacts.add(new Fact(null,
                    fact.getSubject().replace("_", " "),
                    fact.getPredicate().replace("_", " "),
                    fact.getObject().replace("_", " "),
                    Integer.toString(i), document.getID()));
              }
            }

            if (temporaryFacts.size() > 0) {
              factsFound = true;
            }

            // add facts to index
            int factID = 0;
            for (Fact temporaryFact : temporaryFacts) {
              try {
                factIndex.add(new Fact(Integer.toString(factID++),
                    temporaryFact.getSubject(),
                    temporaryFact.getPredicate(),
                    temporaryFact.getObject(),
                    temporaryFact.getSentenceID(),
                    temporaryFact.getDocumentID()));
              }
              catch (IOException ioe) {
                ioe.printStackTrace();
              }
            }

            if (factsFound) {
              sentencesWithFactsFound = true;
              try {
                sentenceIndex.add(new Sentence(Integer.toString(i),
                    tokenBuffer.toString().replace("_", " ").trim(),
                    lemmaBuffer.toString().replace("_", " ").trim(),
                    document.getID()));
              }
              catch (IOException ioe) {
                ioe.printStackTrace();
              }
            }
          }

          if (sentencesWithFactsFound) {
            try {
              documentIndex.add(new WikipediaDocument(document.getID(),
                  document.getTitle(), document.getText(),
                  document.getCategories()));
            }
            catch (IOException ioe) {
              ioe.printStackTrace();
            }
          }
        }
        else {
          System.out.println("File not processed: "
              + "not belonging to sanctioned list!");
        }
      }
      System.out.println("Processing complete...");
    }
    else {
      System.out.println("No files were found to be processed!");
    }

    System.out.println("Closing indices...");
    System.out.println();
    try {
      documentIndex.close();
      sentenceIndex.close();
      factIndex.close();
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }

    LocalDateTime endTime = LocalDateTime.now();
    System.out.println("Done!");
    System.out.println();
    System.out.println("End Time: " + endTime);
    System.out.println("Elapsed Time: " + Duration.between(startTime, endTime));
  }
}
