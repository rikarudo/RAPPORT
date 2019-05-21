package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import wikipedia.WikipediaDocument;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import chave.CHAVEDocument;
import clef.CLEFAnswer;
import clef.CLEFAnswerExtract;
import clef.CLEFQuestion;

/**
 * This class encloses methods for reading XML files, and also manipulating
 * XML text, related to <em>Question Answering</em>.
 *
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 */
public class XMLTools {

  private XMLTools() {
    // Disable external instantiation
  }

  /**
   * This method simply discards the tags identifying spans of text (e.g.,
   * named entities) in the annotated text, producing an unannotated version
   * of the text. The tag contents are kept.
   *
   * @param  annotatedText the text to be ripped of tags
   * @return an unannotated version of the text
   */
  public static String discardTags(String annotatedText) {
    return annotatedText.replaceAll("<[^>]+>", "");
  }

  /**
   * This method removes the specified tags (including theirs contents) from
   * the given text. It is case sensitive.
   *
   * @param  tags a <code>String</code> array with the tags to be removed,
   *         along with theirs contents
   * @param  text the text to be ripped of tags
   * @return an tag stripped version of the text
   */
  public static String removeTags(String[] tags, String text) {
    for (int i = 0; i < tags.length; i++) {
      String pattern = "(((<" + tags[i] + "[\\s\\w=\"]*>(.*?)</" + tags[i] +
          ">)|(<" + tags[i] + "\\s?/>))[\\r\\n]*)";
      text = text.replaceAll(pattern, "");
    }
    return text;
  }

  /**
   * This method simply escapes some XML special characters.
   *
   * @param  text the text to be escaped
   * @return an escaped version of the text
   */
  public static String escapeReservedCharacters(String text) {
    return text.replace("&", "&amp;").replace("\"", "&quot;").replace(
        "'", "&apos;").replace("<", "&lt;").replace(">", "&gt;");
  }

  /**
   * This method simply reverts XML special characters to their unescaped
   * counterpart.
   *
   * @param  text the text to be unescaped
   * @return an unescaped version of the text
   */
  public static String unescapeReservedCharacters(String text) {
    return text.replace("&amp;", "&").replace("&quot;", "\"").replace(
        "&apos;", "'").replace("&lt;", "<").replace("&gt;", ">");
  }

  /**
   * This method retrieves the contents of a given XML tag on an XML document.
   *
   * @param  taggedContents the XML contents to be processed
   * @param  tagName the name of the tag whose contents are to be retrieved
   * @return the contents of the specified tag
   */
  public static String[] retrieveTagContents(String taggedContents,
      String tagName) {
    Matcher matcher = Pattern.compile("(?<=(<" + tagName +
        ">\n?))(.*?)(?=(\n?</" + tagName + ">))",
        Pattern.DOTALL | Pattern.CASE_INSENSITIVE |
        Pattern.UNICODE_CASE).matcher(taggedContents);
    ArrayList<String> tagContents = new ArrayList<String>();
    while (matcher.find()) {
      tagContents.add(matcher.group().trim());
    }
    return tagContents.toArray(new String[tagContents.size()]);
  }

  /**
   * This method generates a random text <em>string</em>, with mixed case
   * letters, and also digits. Nevertheless, the first character of the
   * generated <em>string</em> is always a lower case letter.
   *
   * @param  length the length of the <em>string</em> to be generated
   * @return a random text <em>string</em>
   */
  public static String generateRandomTag(int length) {
    Random random = new Random();
    String digits = "0123456789";
    String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
    String characters = digits.concat(lowerCaseLetters.concat(
        lowerCaseLetters.toUpperCase()));
    StringBuilder randomTag = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      if (i == 0) {
        // an XML tag must start with a letter (preferably a lower case one)
        randomTag.append(lowerCaseLetters.charAt(random.nextInt(
            lowerCaseLetters.length())));
      }
      else {
        randomTag.append(characters.charAt(random.nextInt(
            characters.length())));
      }
    }
    return randomTag.toString();
  }

  /**
   * This method reads the contents from Wikipedia articles (Linguateca dumps).
   * It parses of of the articles and retrieves an object with most of its
   * contents, namely the <em>id</em> (path), <em>title</em>, <em>text</em>,
   * and <em>categories</em>.
   * 
   * @param  article the file to be processed
   * @return a <code>WikipediaDocument</code> object
   * @throws FileNotFoundException ...
   * @throws IOException ...
   */
  public static WikipediaDocument readPagicoWikipediaArticle(File article)
      throws FileNotFoundException, IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(
        new FileInputStream(article)));
    StringBuffer contents = new StringBuffer();
    String id = article.getName();
    String title = new String();
    if (id.contains(".xml")) {
      title = id.substring(0, id.lastIndexOf(".xml"));
    }
    else if (id.contains(".")) {
      title = id.substring(0, id.lastIndexOf("."));
    }
    else {
      title = article.getName();
    }
    String[] contentLines = null;
    String line = null;
    while ((line = reader.readLine()) != null) {
      // links to other languages (at the end of the article) -- stop
      if (line.contains("<ol class=\"mwx.languagelinks\">")) {
        break;
      }
      contents.append(line.trim() + "\n");
    }
    reader.close();

    contentLines = contents.toString().replaceAll(
        ">thumb\\|((\\w+|(\\d+px\\.?))\\|)+", "").replaceAll(
            "<style\\b[^>]*>[\\w\\s\\-\".,:;!?/@]*</style>", "").replaceAll(
                "<[^>]+>", "").replaceAll("\\n+", "\n").replaceAll(
                    "&amp;", "&").replaceAll("…", "...").trim().split("\n");

    String text = new String();
    String categories = new String();
    boolean endText = false;

    for (String contentLine : contentLines) {
      contentLine = contentLine.trim();      
      if (contentLine.length() > 0) {
        if (contentLine.startsWith("Categoria:")) {
          categories += contentLine.substring("Categoria:".length()) + "\n";
          endText = true;
        }
        if (!endText) {
          text += contentLine + "\n";
        }
      }
    }
    return new WikipediaDocument(id.trim(), title.trim(), text.trim(),
        categories.trim());
  }

  /**
   * This method reads the contents from Wikipedia articles (CLEF dumps).
   * It parses of of the articles and retrieves an object with most of its
   * contents, namely the <em>id</em> (path), <em>title</em>, <em>text</em>,
   * and <em>categories</em>.
   * 
   * @param  article ...
   * @return ...
   * @throws FileNotFoundException ...
   * @throws IOException ...
   */
  public static WikipediaDocument readCLEFWikipediaArticle(File article)
      throws FileNotFoundException, IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(
        new FileInputStream(article)));
    StringBuffer contents = new StringBuffer();
    String id = article.getName();
    String title = new String();
    if (id.contains("_")) {
      title = id.substring(0, id.lastIndexOf("_"));
    }
    else if (id.contains(".")){
      title = id.substring(0, id.lastIndexOf("."));
    }
    else {
      title = article.getName();
    }
    String text = new String();
    String categories = new String();
    String line = null;
    boolean copy = false;
    while ((line = reader.readLine()) != null) {
      if (copy) {
        contents.append(line.trim() + "\n");
        if (line.contains("<!-- end content -->")) {
          break;
        }
      }
      else {
        if (line.contains("<!-- start content -->")) {
          contents.append(line.trim() + "\n");
          copy = true;
        }
      }
    }
    reader.close();

    String fullContents = contents.toString();

    if (fullContents.contains(
        "<a name=\"Refer.C3.AAncias\" id=\"Refer.C3.AAncias\"></a>")) {
      text = fullContents.substring(0, fullContents.indexOf(
          "<a name=\"Refer.C3.AAncias\" id=\"Refer.C3.AAncias\"></a>"));
    }
    else if (fullContents.contains("<div id=\"mw-normal-catlinks\">")) {
      text = fullContents.substring(0, fullContents.indexOf(
          "<div id=\"mw-normal-catlinks\">"));      
    }
    else {
      text = new String(fullContents);
    }
    text = text.replaceAll(
        "<style\\b[^>]*>[\\w\\s\\-\".,:;!?/@]*</style>", "").replaceAll(
            "<[^>]+>", "").replaceAll(
                "\\[.*?\\]", "").replaceAll("\\n+", "\n").replaceAll(
                    "&amp;", "&").replaceAll("…", "...").replaceAll(
                        "&#\\d+;", "").trim();

    if (fullContents.contains("<div id=\"mw-normal-catlinks\">")
        && fullContents.substring(fullContents.indexOf(
            "<div id=\"mw-normal-catlinks\">")).indexOf("</div>") >= 0) {
      String categoryContents = fullContents.substring(fullContents.indexOf(
          "<div id=\"mw-normal-catlinks\">")).substring(0,
              fullContents.substring(fullContents.indexOf(
                  "<div id=\"mw-normal-catlinks\">")).indexOf("</div>")
              + "</div>".length());
      String pattern = "title=\"Categoria:.*?\"";
      Matcher matcher = Pattern.compile(pattern).matcher(categoryContents);
      while (matcher.find()) {
        String categoryText = matcher.group();
        if (categoryText.indexOf(":") + 1 < fullContents.length()) {
          if (!categoryText.substring(
              categoryText.indexOf(":") + 1).startsWith("!")) {
            categories += categoryText.substring(categoryText.indexOf(":") + 1,
                categoryText.length() - 1) + "\n";
          }
        }
      }
    }

    return new WikipediaDocument(id.trim(), title.trim(), text.trim(),
        categories.trim());
  }

  /**
   * This method reads the contents from files with the CHAVE editions of
   * <em>P&uacute;blico</em> and <em>Folha de S&atilde;o Paulo</em>
   * newspapers. It parses of of the news articles and retrieves an array
   * of objects representing those same articles.
   * 
   * @param  edition the file with the newspaper edition
   * @param  encoding the character encoding of the file
   * @return an array of <code>CHAVEDocument</code>s
   * @throws ParserConfigurationException ...
   * @throws SAXException ...
   * @throws FileNotFoundException ...
   * @throws IOException ...
   */
  public static CHAVEDocument[] readCHAVEEdition(File edition, String encoding)
      throws ParserConfigurationException, SAXException, FileNotFoundException,
      IOException {
    CHAVEDocument[] documents = null;
    BufferedReader reader = new BufferedReader(new InputStreamReader(
        new FileInputStream(edition), encoding));
    StringBuffer contents = new StringBuffer();
    String line = null;
    while ((line = reader.readLine()) != null) {
      contents.append(line.trim() + "\n");
    }
    reader.close();

    String xmlContents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<chave>" + contents.toString() + "</chave>";

    // as FSP CHAVE files don't have the ampersand escaped, it has to be done
    xmlContents = xmlContents.replaceAll("&(?!(amp;))", "&amp;");

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(new InputSource(new StringReader(
        xmlContents)));
    doc.getDocumentElement().normalize();
    NodeList docList = doc.getElementsByTagName("DOC");

    Element docElement = null;
    NodeList docElementList = null;
    String number = null;
    String id = null;
    String date = null;
    String category = null;
    String author = null;
    String text = null;

    documents = new CHAVEDocument[docList.getLength()];
    for (int i = 0; i < docList.getLength(); i++) {
      docElement = (Element) docList.item(i);
      docElementList = docElement.getElementsByTagName("DOCNO");
      if ((docElementList != null) && (docElementList.getLength() > 0)) {
        number = docElementList.item(0).getTextContent().trim();
      }
      else {
        number = new String();
      }
      docElementList = docElement.getElementsByTagName("DOCID");
      if ((docElementList != null) && (docElementList.getLength() > 0)) {
        id = docElementList.item(0).getTextContent().trim();
      }
      else {
        id = new String();
      }
      docElementList = docElement.getElementsByTagName("DATE");
      if ((docElementList != null) && (docElementList.getLength() > 0)) {
        date = docElementList.item(0).getTextContent().trim();
      }
      else {
        date = new String();
      }
      docElementList = docElement.getElementsByTagName("CATEGORY");
      if ((docElementList != null) && (docElementList.getLength() > 0)) {
        category = docElementList.item(0).getTextContent().trim();
      }
      else {
        category = new String();
      }
      docElementList = docElement.getElementsByTagName("AUTHOR");
      if ((docElementList != null) && (docElementList.getLength() > 0)) {
        author = docElementList.item(0).getTextContent().trim();
      }
      else {
        author = new String();
      }
      docElementList = docElement.getElementsByTagName("TEXT");
      if ((docElementList != null) && (docElementList.getLength() > 0)) {
        text = docElementList.item(0).getTextContent().trim();
      }
      else {
        text = new String();
      }
      documents[i] = new CHAVEDocument(
          number, id, date, category, author, text);
    }
    return documents;
  }

  /**
   * This method ...
   *
   * @param  filename the filename of the file with the <em>CLEF questions</em>
   * @param  taskLanguage the language the <em>CLEF questions</em>
   *         are related to
   * @return a <code>CLEFQuestion</code> array with <em>CLEF questions</em>
   *         specified in the file
   * @throws ParserConfigurationException ...
   * @throws IOException ...
   * @throws SAXException ...
   */
  public static CLEFQuestion[] readCLEFQuestions(String filename,
      String taskLanguage)
          throws ParserConfigurationException, SAXException, IOException {
    File file = new File(filename);
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.parse(file);
    doc.getDocumentElement().normalize();
    NodeList questionList = doc.getElementsByTagName("pergunta");
    Node question = null;
    String year = null;
    String id = null;
    String category = null;
    String type = null;
    String restriction = null;
    String language = null;
    String task = null;
    String text = null;
    ArrayList<CLEFQuestion> questions = new ArrayList<CLEFQuestion>();
    for (int i = 0; i < questionList.getLength(); i++) {
      question = (Element) questionList.item(i);
      if (((Element) question).hasAttribute("tarefa_" + taskLanguage)) {
        year = ((Element) question).getAttribute("ano");
        id = ((Element) question).getAttribute("id_org");
        category = ((Element) question).getAttribute("categoria");
        type = ((Element) question).getAttribute("tipo");
        restriction = ((Element) question).getAttribute("restrição");
        language = ((Element) question).getAttribute("ling_orig");
        task = ((Element) question).getAttribute("tarefa_" + taskLanguage);
        text = ((Element) question).getElementsByTagName(
            "texto").item(0).getTextContent();

        NodeList extractNodes = ((Element) question).getElementsByTagName(
            "extracto");
        ArrayList<CLEFAnswerExtract> extracts
        = new ArrayList<CLEFAnswerExtract>();
        if (extractNodes.getLength() > 0) {
          Node extract = null;
          String extractNumber = null;
          String answerNumber = null;
          String extractText = null;
          for (int j = 0; j < extractNodes.getLength(); j++) {
            extract = (Element) extractNodes.item(j);
            extractNumber = ((Element) extract).getAttribute("n");
            answerNumber = ((Element) extract).getAttribute("resposta_n");
            extractText = extract.getTextContent();
            extracts.add(new CLEFAnswerExtract(extractNumber, answerNumber,
                extractText.trim()));
          }
        }

        NodeList answerNodes = ((Element) question).getElementsByTagName(
            "resposta");
        ArrayList<CLEFAnswer> answers = new ArrayList<CLEFAnswer>();
        if (answerNodes.getLength() > 0) {
          Node answer = null;
          String answerNumber = null;
          String answerDocID = null;
          String answerText = null;
          CLEFAnswerExtract answerExtract = null;
          for (int j = 0; j < answerNodes.getLength(); j++) {
            answer = (Element) answerNodes.item(j);
            answerNumber = ((Element) answer).getAttribute("n");
            answerDocID = ((Element) answer).getAttribute("docid");
            answerText = answer.getTextContent();
            for (CLEFAnswerExtract extract : extracts) {
              if (extract.getAnswerNumber().equals(answerNumber)) {
                answerExtract = extract;
                break;
              }
            }
            answers.add(new CLEFAnswer(answerNumber, answerDocID,
                answerText.trim(), answerExtract));
          }
        }
        questions.add(new CLEFQuestion(year, id, category, type, restriction,
            language, task, text.trim(), answers.toArray(
                new CLEFAnswer[answers.size()])));
      }
    }
    return questions.toArray(new CLEFQuestion[questions.size()]);
  }
}
