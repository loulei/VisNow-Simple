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

/**
 * Class to represent single line of Log file with corresponding log Level.
 * It's based on org.apache.log4j.Level and additionally Unknown_level is added.
 * 
 * @author szpak
 */
public class LogLine {
    private Level level;
    private String line;

    /**
     * Tries to read level information from the line; If no level string is found than defaultLevel is taken.
     * @param line 
     */
    public LogLine(String line, Level defaultLevel) {
        this.line = line;
        level = defaultLevel;
        for (Level level:Level.values()) {
            if (line.indexOf(level.name())>=0) {
                this.level = level;
                break;
            }
        }
    }

    public String getLine() {
        return line;
    }

    public Level getLevel() {
        return level;
    }
    
    
    /**
     * Levels based on log4j levels + unknown level. 
     * This class describes level of the line (not every line includes Level string and
     * there are entries with multiple lines - like printStackTrace lines)
     * 
     * !! Used also as a name for string comparision !!
     */
    public enum Level {
        FATAL, ERROR, WARN, INFO, DEBUG, TRACE, UNKNOWN_LEVEL;
    }
}
