/* Name Change Utility
 *
 * Author: Michael Watts <mike@mikwat.com>
 *
 * Date: 7/12/2003
 *
 * Purpose:
 *
 * Notes:
 *
 * Changes:
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Michael Watts
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mikwat.mp3;

import java.util.List;
import java.util.StringTokenizer;

public class FileName {
   private String originalName = null;
   private String artistName = null;
   private boolean removeUnderscore = false;
   private List separator = null;
   private List fmt = null;
   private List newFmt = null;
   private boolean makeLower = false;

   public void setMakeLower(boolean makeLower) {
      this.makeLower = makeLower;
   }

   public FileName(String originalName) {
      this.originalName = originalName;
   }

   public void setArtistName(String artistName) {
      this.artistName = artistName;
   }

   public void setFormat(List fmt) {
      this.fmt = fmt;
   }

   public void setNewFormat(List newFmt) {
      this.newFmt = newFmt;
   }

   public void setRemoveUnderscore(boolean removeUnderscore) {
      this.removeUnderscore = removeUnderscore;
   }

   public void setSeparator(List separator) {
      this.separator = separator;
   }

   public boolean isValidSufix() {
      boolean isValid = false;
      for (int i = 0; i < NameChange.SUFFIX.length; i++) {
         if (originalName.endsWith(NameChange.SUFFIX[i])) {
            isValid = true;
            break;
         }
      }

      return isValid;
   }

   public String rename() {

      if (!isValidSufix()) {
         return null;
      }

      String name = originalName;

      // remove _ from file name
      if (removeUnderscore) {
         name = removeUnderscore(name);
      }

      // remove sufix from file name
      name = removeSufix(name);

      StringTokenizer token =
         new StringTokenizer(name, (String) separator.get(0));

      // fill array with elements of file name
      String elm[] = new String[NameChange.MAX_FMT];
      int j = 0;
      while (token.hasMoreTokens()) {
         String t = token.nextToken();
         t = t.trim(); // remove whitespace from front/back
         elm[j++] = t;
      }

      // create new file name: "ARTIST - NUMBER - TITLE.mp3"
      String newName = new String("");

      if (newFmt == null || newFmt.size() == 0) {
         // artist name
         newName = addArtistName(newName, elm);
         newName = addSeparator(newName);

         // track number
         newName = addTrackNumber(newName, elm);
         newName = addSeparator(newName);

         // song title
         newName = addTitle(newName, elm);
      }
      else {
         for (int i = 0; i < newFmt.size(); i++) {
            String format = (String) newFmt.get(i);

            if (format.equals(NameChange.ARTIST)) {
               newName = addArtistName(newName, elm);
            }
            else if (format.equals(NameChange.NUMBER)) {
               newName = addTrackNumber(newName, elm);
            }
            else if (format.equals(NameChange.TITLE)) {
               newName = addTitle(newName, elm);
            }
            else if (format.equals(NameChange.SEPARATOR)) {
               newName = addSeparator(newName);
            }
         }
      }

      // add sufix
      newName = addSufix(newName);

      return newName;
   }

   private String addArtistName(String current, String[] originalElm) {
      int c = NameChange.sequentialSearch(fmt, NameChange.ARTIST);
      if (c < 0)
         current += artistName;
      else {
         originalElm[c] = NameChange.upperCaseFirst(originalElm[c], makeLower);
         current += originalElm[c];
      }

      return current;
   }

   private String addTrackNumber(String current, String[] originalElm) {
      int c = NameChange.sequentialSearch(fmt, NameChange.NUMBER);
      current
         += ((originalElm[c].length() == 1)
            ? "0" + originalElm[c]
            : originalElm[c]);

      return current;
   }

   private String addTitle(String current, String[] originalElm) {
      int c = NameChange.sequentialSearch(fmt, NameChange.TITLE);
      originalElm[c] = NameChange.upperCaseFirst(originalElm[c], makeLower);
      current += originalElm[c];

      return current;
   }

   private String addSeparator(String current) {
      current += " - ";

      return current;
   }

   private String addSufix(String current) {
      current += ".mp3";

      return current;
   }

   private String removeSufix(String current) {
      current =
         current.substring(0, current.length() - NameChange.SUFFIX_LENGTH);

      return current;
   }

   private String removeUnderscore(String current) {
      current = current.replace('_', ' ');

      return current;
   }

}
