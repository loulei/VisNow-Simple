/* VisNow
   Copyright (C) 2006-2013 University of Warsaw, ICM

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the 
University of Warsaw, Interdisciplinary Centre for Mathematical and 
Computational Modelling, Pawinskiego 5a, 02-106 Warsaw, Poland. 

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package pl.edu.icm.visnow.system.utils.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * LogReader reads log file on change; It provides log data with additional log Level flag 
 * (which can be used later on to display lines in color); It gives next line of log only if it's fully loaded 
 * (followed by newline).
 * 
 * This is LogReader factory, so LogReaders (identified by filepath) are cached (with its log buffer) - 
 * this is usefull if there are more logs in log directory - typical case: log_date1.log, log_date2.log, etc..., log.log
 * 
 * There are two main cases:
 * 1. File is appended (if length>=lastLength)
 * 2. File is new/empty (if not exists, can't open, lastLength>length)
 * 
 * @author szpak
 */
public class LogReader {
    private final static Logger LOGGER = Logger.getLogger(LogReader.class);    

    //indicates if log file has been changed
    private boolean isNewLog;
    private long lastLength;
    private LogLine.Level lastLevel = LogLine.Level.UNKNOWN_LEVEL;
    //buffer to keep readed log
    private StringBuffer buffer;
    
    /** position in buffer just after last returned char (which has to be newline). */
    private int bufferPosition;
    //char buffer to read from file
    private char[] charBuffer;
    private String filePath;
    
    
    private File file;
    private String path;
    private BufferedReader bufferedReader;    
    
    //this is factory of logBuffers, and this is a map to keep all constructed logBuffers.
    private static Map<String, LogReader> logReaders = new HashMap<String, LogReader>();


    /**
     * @param filePath path that poinst to log file as in File(path) constructor.
     */
    private LogReader(String filePath) {
        this.filePath = filePath;
        buffer = new StringBuffer();
        charBuffer = new char[10000];
        resetReader(true);
    }
    
    /**
     * Resets reader buffer (like it was a new log file) and additionally (if full is true)
     * resets path/file/bufferedReader objects - so need to be reinitialized.
     */
    private void resetReader(boolean fullReset) {
        buffer.setLength(0);
        isNewLog = true;
        lastLength = 0;
        bufferPosition = 0;

        lastLevel = LogLine.Level.UNKNOWN_LEVEL;

        if (fullReset) {
            path = null;
            file = null;
            bufferedReader = null;            
        }
    }
        
    /**
     * Reads next characters from file and updates flag (lastLength)
     * or resets file and resets flag.
     */
    public void updateReader() {
        if (filePath.equals("")) 
        {
            resetReader(true); //empty reader (always new/empty)
            return;
        }
        if (path == null) path = filePath;
        if (file == null) file = new File(path);
        if (!file.exists()) { //means new log
            resetReader(true);
            return;
        }               
            
        try {
            //means new log
            if (bufferedReader == null || file.length() < lastLength) {
                resetReader(false);
                bufferedReader =  new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            }
        } catch (IOException ex) {
            return; //if can't read file than it's marked as new/empty log
        }

        
        if (file.length() > lastLength) {//read file only if its size increased
            int read = 0;
            try {
                while (read>=0) {
                    read = bufferedReader.read(charBuffer,0,charBuffer.length);
                    if (read>0) buffer.append(charBuffer,0,read);
                } 
            } catch (IOException ex) {
                resetReader(true);
                return;
            }
        }
        
        //update length
        lastLength = file.length();
    }

    /**
     * Indicates if log is new/empty (if not exists, cant open, previousSize>size)
     * @return 
     */
    public boolean isNew() {
        return isNewLog;
    }    

    /**
     * Returns latest lines or empty list (if no new lines); sets new flag to false; updates buffer position.
     * Line in file has to be ended with newline (so last line is always not returned - this is to avoid situation 
     * when log4j logger's written to the file only first part of the line). This assumes that log4j logger always 
     * writes every line into file with newline.
     */
    public List<LogLine> getLines() {
        isNewLog = false;
        List<LogLine> lines = new ArrayList<LogLine>();
        
        
        String subBuffer = buffer.substring(bufferPosition);
        
        //never include last line (because it's always without newline - so it will be as a last element of split)

        //avoid trailing whitespace
        String[] subBufferLines = subBuffer.split("\r\n|\n|\r",-1);
        //do not include last line (without newline)
        for (int i = 0; i < subBufferLines.length-1; i++) {
            //replace tabs with spaces
            LogLine line = new LogLine(subBufferLines[i].replaceAll("\t","    "),lastLevel);
            lastLevel = line.getLevel();
            lines.add(line);
        }
        
        //update buffer position (do not include last line - if it's without newline)
        if (subBuffer.length()>0)
            bufferPosition = buffer.length()- subBufferLines[subBufferLines.length-1].length();
        
        return lines;
    }

    
    /**
     * Log buffer factory; item is recognized by filepath.
     * 
     * If logger does not exist then new is constructed; if it exists than it's returned (bufferPosition is set 
     * to 0 - like a new file was read).
     * @param filepath path to logfile
     */
    public static LogReader getLogReader(String filepath) {
        LogReader reader = logReaders.get(filepath);
        if (reader == null) {
            reader = new LogReader(filepath);
            logReaders.put(filepath, reader);
        }
        else {
            //serve buffer from the beginning
            reader.bufferPosition = 0; 
            reader.isNewLog = true;
        }
        return reader;
    }    
}
