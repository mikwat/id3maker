/* ID3 Maker Utility
 *
 * Author: Michael Watts <mike@mikwat.com>
 *
 * Date: 10/16/2003
 *
 * Purpose: This utility exams the file name of an MP3 in order to 
 *  create appropriate ID3 tags.
 *
 * Notes: At this point the utility is very limited and specialized
 *  for a particular file naming convention.  It needs to be expanded.
 *  This utility is dependent upon the jd3Lib library which can
 *  be downloaded from SourceForge at:
 *  http://sourceforge.net/projects/jd3lib/
 *
 * Changes: 
 *  10/26/2003: 
 *    Added status messages.
 * 
 *  11/02/2003: 
 *    Added iTunes naming convention support (-itunes flag).  
 *    Added -c flag for defining an album-wide comment.
 *
 * Copyright (C) 2003 Michael Watts
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

import helliker.id3.MP3File;

import java.io.File;
import java.util.StringTokenizer;

public class ID3Maker {
   private static final String FLAG_DELIM = "-s";
   private static final String FLAG_GENRE = "-g";
   private static final String FLAG_COMMENT = "-c";
   private static final String FLAG_ITUNES = "-itunes";

   private static final String DEFAULT_COMMENT = "";

   private static final String DELIM = "-";
   private static final String CD = "CD";

   private static final int SUFFIX_LENGTH = 4;

   public static void main(String[] args) {
      // check argument count
      if (args.length < 1) {
         System.out.println(
            "usage: java ID3Maker directory [-itunes] [-s separator] [-g genre] [-c comment]\n");
         System.exit(1);
      }

      int argIdx = 0;
      String dirName = args[argIdx++];

      // make sure next argument is a directory
      File dir = new File(dirName);
      if (!dir.isDirectory()) {
         System.out.println(dirName + " is not a directory.");
         System.exit(1);
      }

      // check for flags
      boolean iTunes = false;
      if (argIdx < args.length && args[argIdx].equals(FLAG_ITUNES)) {
         argIdx++;
         iTunes = true;
      }

      String separator = DELIM;
      if (argIdx < args.length && args[argIdx].equals(FLAG_DELIM)) {
         argIdx++;
         separator = args[argIdx++];
      }

      String albumGenre = null;
      if (argIdx < args.length && args[argIdx].equals(FLAG_GENRE)) {
         argIdx++;
         albumGenre = args[argIdx++];
      }

      String comment = null;
      if (argIdx < args.length && args[argIdx].equals(FLAG_COMMENT)) {
         argIdx++;
         comment = args[argIdx++];
      }
      else {
         comment = DEFAULT_COMMENT;
      }

      // list of files in directory
      File file[] = dir.listFiles();

      // for each file in directory
      for (int i = 0; i < file.length; i++) {
         try {
            System.out.println("\nOpening file: " + file[i].getName());
            MP3File mp3File = new MP3File(file[i], MP3File.BOTH_TAGS);

            String fileName = mp3File.getFileName();
            fileName = removeSufix(fileName);

            String artistName = null;
            String trackNumber = null;
            String songTitle = null;
            String albumName = null;

            File parentFile = file[i].getParentFile();
            albumName = parentFile.getName();

            if (iTunes) {
               File grandParentFile = parentFile.getParentFile();

               artistName = grandParentFile.getName();
               trackNumber = fileName.substring(0, 2);
               songTitle = fileName.substring(3);
            }
            else {
               StringTokenizer token = new StringTokenizer(fileName, separator);

               artistName = token.nextToken().trim();
               trackNumber = token.nextToken().trim();
               songTitle = token.nextToken().trim();
            }

            mp3File.setAlbum(albumName);
            mp3File.setArtist(artistName);
            mp3File.setTitle(songTitle);
            mp3File.setTrack(trackNumber);
            mp3File.setComment(comment);
            if (albumGenre != null) {
               mp3File.setGenre(albumGenre);
            }

            float complete = (float)(i + 1) / (float)file.length;
            System.out.print(complete + "%..");
            mp3File.writeTags();
         }
         catch (Exception e) {
            throw new RuntimeException(e.toString());
         }
      }

      System.out.println("\nDone.");
   }

   private static String removeSufix(String current) {
      current = current.substring(0, current.length() - SUFFIX_LENGTH);

      return current;
   }
}
