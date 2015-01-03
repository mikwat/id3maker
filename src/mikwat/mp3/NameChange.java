/* Name Change Utility
 *
 * Author: Michael Watts <mike@mikwat.com>
 *
 * Date: 11/08/2002
 *
 * Purpose: To rename a directory of files based on a specific format.
 *
 * Notes:   This class has been written specifically to rename mp3 files,
 *    but it can easily be adapted or generalized to rename any type and
 *    format of file.
 *
 * Changes:
 *    11/09/2002
 *       - Now removes underscore characters from file name.
 *       - Added upperCaseFirst() method.
 *       - Now converts first character of each word in file
 *       name to upper case.
 * 
 *    6/28/2003
 *       - Added -u flag for removing underscore characters.
 * 
 *    7/12/2003
 *       - Added logic to upperCaseFirst() method to ignore open paren char '('
 *       and to convert following char instead.
 *
 * Copyright (C) 2002-2003 Michael Watts
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 */

package mikwat.mp3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class NameChange {
   public static final int MAX_FMT = 10;
   public static final String ARTIST = "n";
   public static final String UNDERSCORE = "u";
   public static final String SEPARATOR = "s";
   public static final String FORMAT = "f";
   public static final String NEW_FORMAT = "nf";
   public static final String NUMBER = "#";
   public static final String TITLE = "t";
   public static final String LOWER_CASE = "l";
   //private static String ALBUM = "a";
   //private static String DONTCARE = "!";

   public static final String[] SUFFIX = { ".mp3", ".Mp3", ".MP3" };
   public static final int SUFFIX_LENGTH = 4;
   private static BufferedReader in; // input stream

   public static void main(String args[]) {
      boolean removeUnderscore = false;
      boolean makeLower = false;
      in = new BufferedReader(new InputStreamReader(System.in));
      String artist = ""; // set if artist name needs to be defined
      //String album;

      // check argument count
      if (args.length < 3) {
         System.out.println(
            "usage: java NameChange directory [-n artist] [-u] [-l] separator "
               + "element [more-elements] [-nf element [more-elements]]\n");
         System.exit(1);
      }

      int index = 0;

      // make sure next argument is a directory
      File dir = new File(args[index]);
      if (!dir.isDirectory()) {
         System.out.println(args[index] + " is not a directory.");
         System.exit(1);
      }

      index++;

      // check for flags
      if (args[index].equals("-" + ARTIST)) {
         artist = args[index + 1];
         index += 2;
      }

      if (args[index].equals("-" + UNDERSCORE)) {
         removeUnderscore = true;
         index++;
      }

      if (args[index].equals("-" + LOWER_CASE)) {
         makeLower = true;
         index++;
      }

      ArrayList separator = new ArrayList();
      if (args[index].equals("-" + SEPARATOR)) {
         while (!args[++index].equals("-" + FORMAT)) {
            separator.add(args[index]);
         }
      }
      else {
         separator.add(args[index]);
      }

      // create array with elements of file name format
      ArrayList fmt = new ArrayList();
      index++;
      while (index < args.length && !args[index].equals("-" + NEW_FORMAT)) {
         fmt.add(args[index]);
         index++;
      }

      // create array with elements of new file name format
      ArrayList newFmt = new ArrayList();
      while (index < args.length) {
         newFmt.add(args[index]);
         index++;
      }

      // list of files in directory
      File file[] = dir.listFiles();

      // for each file in directory
      for (int i = 0; i < file.length; i++) {
         // get current file name
         String current = file[i].getName();

         FileName fileName = new FileName(current);

         if (!fileName.isValidSufix()) {
            continue;
         }

         fileName.setFormat(fmt);
         fileName.setNewFormat(newFmt);
         fileName.setSeparator(separator);
         fileName.setRemoveUnderscore(removeUnderscore);
         fileName.setMakeLower(makeLower);
         fileName.setArtistName(artist);

         String newName = fileName.rename();

         // create new file
         File newFile = new File(dir + File.separator + newName);

         System.out.println(newName);
         System.out.print("Change file name? (y/n)");
         try {
            if (in.readLine().equals("y")) {
               // rename file
               if (file[i].renameTo(newFile))
                  System.out.println("Success.");
               else
                  System.out.println("Cannot change file name to: " + newFile);
            }
         }
         catch (IOException e) {}
      }
   }

   public static int sequentialSearch(List array, String key) {
      int i;
      for (i = 0; i < array.size(); i++) {
         String elm = (String) array.get(i);
         if (elm != null && elm.equals(key)) {
            break;
         }
      }
      if (i == array.size()) {
         i = -1;
      }

      return i;
   }

   public static String upperCaseFirst(String str, boolean makeLower) {
      if (str == null) {
         return null;
      }
      String rval = "";

      // tokenize on the space character
      StringTokenizer token = new StringTokenizer(str);

      while (token.hasMoreTokens()) {
         String current = token.nextToken();

         if (makeLower) {
            current = current.toLowerCase();
         }

         // determine first alpha character (ignore open paren '(')
         int charIdx = 0;
         Character leadChar = null;
         if (current.charAt(charIdx) == '(') {
            charIdx++;
            leadChar = new Character('(');
         }

         if (current.length() <= charIdx) {
            rval += leadChar.toString();
         }
         else {
            // grab character and convert to upper case
            char c = Character.toUpperCase(current.charAt(charIdx));

            // concat string back together
            rval += ((leadChar != null) ? leadChar.toString() : "")
               + c
               + current.substring(charIdx + 1)
               + " ";
         }
      }
      // remove trail space
      rval = rval.trim();

      return rval;
   }
}