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
