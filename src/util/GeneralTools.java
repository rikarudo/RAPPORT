package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class encloses a set of general purpose methods that can be used across
 * multiple scenarios.
 *
 * @author   Ricardo Rodrigues
 * @version  0.9.9.9
 */
public class GeneralTools {

  /**
   * Comma as a decimal mark for use as an argument in the
   * <code>isNumber()</code> method.
   */
  public static final char COMMA_DECIMAL_MARK = ',';

  /**
   * Period as a decimal mark for use as an argument in the
   * <code>isNumber()</code> method.
   */
  public static final char PERIOD_DECIMAL_MARK = '.';

  private GeneralTools() {
    // Disable external instantiation
  }

  /**
   * Formats a time given in milliseconds, dividing it in milliseconds,
   * seconds, minutes and hours, using the format <code>hh:mm:ss.ms</code> (for
   * instance, <code>12:34:56.789</code>). Take notice that hours can have a
   * value greater than or equal to <code>24</code>, as days are not accounted
   * for.
   *
   * @param  milliseconds the time to be formatted
   * @return the formatted time
   */
  public static String formatTime(long milliseconds) {
    int ms = (int) milliseconds % 1000;
    int s = (int) milliseconds / 1000 % 60;
    int m = (int) milliseconds / 1000 / 60 % 60;
    int h = (int) milliseconds / 1000 / 60 / 60;
    String formattedTime = new String();
    formattedTime += h;
    formattedTime += ":";
    if (m < 10) {
      formattedTime += "0";
    }
    formattedTime += m;
    formattedTime += ":";
    if (s < 10) {
      formattedTime += "0";
    }
    formattedTime += s;
    formattedTime += ".";
    if (ms < 100) {
      formattedTime += "0";
    }
    if (ms < 10) {
      formattedTime += "0";
    }
    formattedTime += ms;
    return formattedTime;
  }

  /**
   * Verifies if the given argument is a number. It handles positive, negative,
   * and decimal values.
   *
   * @param  arg the argument to be tested if it is a number or not
   * @param  decimalMark the character used as a decimal mark
   * @return the result of the test
   */
  public static boolean isNumber(String arg, char decimalMark) {
    String pattern = null;
    if (decimalMark == COMMA_DECIMAL_MARK) {
      pattern = "^(\\d|-)?(\\d|\\.)*\\,?\\d*$";
    }
    else {
      pattern = "^(\\d|-)?(\\d|\\,)*\\.?\\d*$";
    }
    Matcher matcher = Pattern.compile(pattern).matcher(arg);
    return matcher.matches();
  }

  /**
   * The array returned by this method contains each substring of the given
   * text that is terminated by another substring that matches the given
   * expression or is terminated by the end of the text. The substrings in the
   * array are in the order in which they occur in the text. If the expression
   * does not match any part of the input then the resulting array has just one
   * element, namely the <code>String</code> that represents the text. (This
   * method is provided here just for convenience, providing an alternative to
   * the same method in class <code>String</code>, being now possible the
   * inclusion of the delimiters. It can be specially useful in parsing
   * sentences in a text.)
   *
   * @param  text the text to be split
   * @param  pattern the pattern (a <em>regular expression</em> used to split
   *         the text
   * @param  appendDelimiters whether delimiters should or be not also included
   *         in the retrieved array whit all the tokens resulting from the
   *         split
   * @return a <code>String</code> array computed by splitting the text around
   *         matches of the given <em>regular expression</em>
   */
  public static String[] split(String text, Pattern pattern,
      boolean appendDelimiters) {
    String[] tokens = null;
    Matcher matcher = pattern.matcher(text);
    ArrayList<String> matches = new ArrayList<String>();
    int start = 0;
    if (appendDelimiters) {
      while (matcher.find()) {
        matches.add(text.substring(start, matcher.start()) +
            matcher.group().trim());
        start = matcher.end();
      }
    }
    else {
      while (matcher.find()) {
        matches.add(text.substring(start, matcher.start()));
        matches.add(matcher.group().trim());
        start = matcher.end();
      }
    }
    if (start < text.length()) {
      matches.add(text.substring(start).trim());
    }
    tokens = matches.toArray(new String[matches.size()]);
    return tokens;
  }

  /**
   * Replaces each of the so called metacharacters in a <em>regular
   * expression</em> by their escaped counterparts. An escaped version of the
   * text is returned. The metacharacters are
   * <em><code>[\&circ;$.|?*+()]</code></em>.
   *
   * @param  text the text to be &quot;escaped&quot;
   * @return a <code>String</code> with the &quot;escaped&quot; text
   */
  public static String escapeRegExMetacharacters(String text) {
    return text.replace("\\", "\\\\").replace("^", "\\^").replace(
        "$", "\\$").replace(".", "\\.").replace("|", "\\|").replace(
            "?", "\\?").replace("*", "\\*").replace("+", "\\+").replace(
                "(", "\\(").replace(")", "\\)").replace("[", "\\[").replace(
                    "]", "\\]");
  }

  /**
   * This method facilitates the merging of two arrays of <em>strings</em>. It
   * receives two arrays and returns another array resulting from the merging
   * of the given arrays. Repeated elements on both arrays will have just one
   * copy in the new array.
   *
   * @param  oneStringArray one of the arrays to be merged
   * @param  anotherStringArray the other array
   * @return an array resulting from the merging of the two given arrays
   */
  public static String[] mergeStringArrays(String[] oneStringArray,
      String[] anotherStringArray) {
    ArrayList<String> mergedStrings = new ArrayList<String>();
    for (int i = 0; i < oneStringArray.length; i++) {
      if (!mergedStrings.contains(oneStringArray[i])) {
        mergedStrings.add(oneStringArray[i]);
      }
    }
    for (int i = 0; i < anotherStringArray.length; i++) {
      if (!mergedStrings.contains(anotherStringArray[i])) {
        mergedStrings.add(anotherStringArray[i]);
      }
    }
    return mergedStrings.toArray(new String[mergedStrings.size()]);
  }

  /**
   * This method checks whether all the characters in a given <em>string</em>
   * are capitals.
   *
   * @param  word the <em>string</em> to be checked
   * @return <em>true</em> if all the characters are capitals, or
   *         <em>false</em>, otherwise
   *
   */
  public static boolean isAllCaps(String word) {
    for (int i = 0; i < word.length(); i++) {
      if (!Character.isUpperCase(word.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * This method checks whether the first character in a given <em>string</em>
   * is capital.
   *
   * @param  word the <em>string</em> to be checked
   * @return <em>true</em> if the first character is capital, or
   *         <em>false</em>, otherwise or if the <em>string</em> is empty
   *
   */
  public static boolean isCapitalized(String word) {
    if (word.length() > 0) {
      if (!Character.isUpperCase(word.charAt(0))) {
        return false;
      }
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * This method capitalizes the first character of a given <em>string</em>,
   * returning the new <em>string</em> obtained this way. If the <em>string</em>
   * is empty, or its first character is already capitalized or a digit, it
   * is returned the same <em>string</em>.
   *
   * @param  word the <em>string</em> to have its first character capitalizes
   * @return a new <em>string</em> with its first character capitalized
   *
   */
  public static String capitalize(String word) {
    if (word.length() > 0) {
      String capitalizedWord = word.substring(0, 1).toUpperCase();
      if (word.length() > 1) {
        capitalizedWord = capitalizedWord.concat(word.substring(1));
      }
      return capitalizedWord;
    }
    else {
      return word;
    }
  }

  /**
   * This method counts the occurrences of a <em>character</em> in a given
   * <em>string</em>.
   * 
   * @param  haystack the <em>string</em> to be analyzed
   * @param  needle the <em>character</em> to be searched for
   * @return the number of occurrences
   */
  public static int countOccurrences(String haystack, char needle) {
    int count = 0;
    for (int i=0; i < haystack.length(); i++) {
      if (haystack.charAt(i) == needle) {
        count++;
      }
    }
    return count;
  }

  /**
   * This method lists recursively all files in a given directory and in its
   * subdirectories.
   *
   * @param  directory the directory to list the files from
   * @return an <em>array</em> with the files found
   *
   */
  public static File[] listFiles(File directory) {
    ArrayList<File> fileList = listFilesAsArrayList(directory, true);
    return fileList.toArray(new File[fileList.size()]);
  }

  /**
   * This method lists all files in a given directory and, optionally, in its
   * subdirectories.
   *
   * @param  directory the directory to list the files from
   * @param  recursive whether the listing should be recursive or not
   * @return an <em>array</em> with the files found
   * @throws FileNotFoundException ...
   *
   */
  public static File[] listFiles(File directory, boolean recursive)
      throws FileNotFoundException {
    if (directory.exists()) {
      ArrayList<File> fileList = listFilesAsArrayList(directory, recursive);
      return fileList.toArray(new File[fileList.size()]);
    }
    else {
      throw new FileNotFoundException();
    }
  }

  /**
   * This method lists recursively all files in a given directory and its
   * subdirectories, that end with the given extension.
   *
   * @param  directory the directory to list the files from
   * @param  extension the extension the files should end with
   * @return an <em>array</em> with the files found
   * @throws FileNotFoundException ...
   *
   */
  public static File[] listFiles(File directory, String extension)
      throws FileNotFoundException {
    if (directory.exists()) {
      FileExtensionFilter filter = new FileExtensionFilter(extension);
      ArrayList<File> fileList = listFilesAsArrayList(directory, filter, true);
      return fileList.toArray(new File[fileList.size()]);
    }
    else {
      throw new FileNotFoundException();
    }
  }

  /**
   * This method lists recursively all files in a given directory and its
   * subdirectories, that comply to the given filter.
   *
   * @param  directory the directory to list the files from
   * @param  extension the extension the files should end with
   * @param  recursive whether the listing should be recursive or not
   * @return an <em>array</em> with the files found
   * @throws FileNotFoundException ...
   *
   */
  public static File[] listFiles(File directory, String extension,
      boolean recursive) throws FileNotFoundException {
    if (directory.exists()) {
      FileExtensionFilter filter = new FileExtensionFilter(extension);
      ArrayList<File> fileList = listFilesAsArrayList(directory, filter,
          recursive);
      return fileList.toArray(new File[fileList.size()]);
    }
    else {
      throw new FileNotFoundException();
    }

  }

  private static ArrayList<File> listFilesAsArrayList(File directory,
      boolean recursive) {
    ArrayList<File> fileList = new ArrayList<File>();
    // checks whether the directory is really a directory
    if (directory.isDirectory()) {
      List<File> currentList = Arrays.asList(directory.listFiles());
      for (File file : currentList) {
        if (!file.isDirectory()) {
          fileList.add(file);
        }
        else if (recursive) {
          fileList.addAll(listFilesAsArrayList(file, recursive));
        }
      }
    }
    else {
      // otherwise, the directory is actually a file
      File file = directory;
      fileList.add(file);
    }
    return fileList;
  }

  private static ArrayList<File> listFilesAsArrayList(File directory,
      FilenameFilter filter, boolean recursive) {
    ArrayList<File> fileList = new ArrayList<File>();
    // checks whether the directory is really a directory
    if (directory.isDirectory()) {
      List<File> currentList = Arrays.asList(directory.listFiles(filter));
      for (File file : currentList) {
        if (!file.isDirectory()) {
          fileList.add(file);
        }
        else if (recursive) {
          fileList.addAll(listFilesAsArrayList(file, filter, recursive));
        }
      }
    }
    else {
      // otherwise, the directory is really a file
      File file = directory;
      if (filter.accept(directory.getParentFile(), file.getName())) {
        fileList.add(file);
      }
    }
    return fileList;
  }

  // an inner class ////////////////////////////////////////////////////////////

  private static class FileExtensionFilter implements FilenameFilter {
    private String extension = null;

    private FileExtensionFilter(String extension) {
      this.extension = new String("." + extension);
    }

    public boolean accept(File directory, String filename) {
      if (filename.endsWith(extension) || new File(directory.getAbsolutePath() +
          File.separator + filename).isDirectory()) {
        return true;
      }
      else {
        return false;
      }
    }
  }

  /**
   * This method reads typical configuration files (or other resources),
   * with commented out lines.
   * 
   * @param  configData the configuration data
   * @param  comment the symbol used to comment out lines
   * @return an <em>array</em> with the configuration lines
   * @throws IOException ...
   */
  public static String[] readConfigurationData(InputStream configData,
      String comment)
          throws IOException {
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(configData));
    ArrayList<String> data = new ArrayList<String>();
    String line = null;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.length() > 0) {
        if (!line.startsWith(comment)) {
          if (line.contains(comment)) {
            data.add(line.substring(0, line.indexOf(comment)).trim());
          }
          else {
            data.add(line);
          }
        }
      }
    }
    return data.toArray(new String[data.size()]);
  }
}
