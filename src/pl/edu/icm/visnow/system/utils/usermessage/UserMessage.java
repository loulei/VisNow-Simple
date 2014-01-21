//<editor-fold defaultstate="collapsed" desc=" License ">

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
//</editor-fold>
package pl.edu.icm.visnow.system.utils.usermessage;

/**
 * User messages are messages presented to user; this should be the first choice for textual communication TO the user.
 * So (in production) this {@code UserMessage} functionality should be used instead of visible logger lines or {@code System.out.println}.
 *
 *
 * @author szpak
 */
public class UserMessage {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private String applicationName;
    private String sourceName;
    //title without any html-like tags
    private String title;
    //title possibliy with html tags apart from <html> and <html/> tags
    private String details;
    private Level level;
    private static final String allTagsRE = "<[^<>]+>";
    private static final String[] newLineTagsRE = {"(?i)<br>", "(?i)</tr>", "(?i)<li>"};

    /**
     * Creates new user message.
     * In
     * <code>title</code> all html-like tags and newLines are removed.
     * In
     * <code>details</code> &lt;html&gt; and &lt;/html&gt; tags are removed and all system line separators are replaced with &lt;br&gt;.
     */
    public UserMessage(String applicationName, String sourceName, String title, String details, Level level) {
        this.applicationName = applicationName.trim();
        this.sourceName = sourceName.trim();
        this.title = title.replaceAll(allTagsRE, " ").trim(); //remove all html-like tags
        //remove beginning/ending html tags + replace all line separators to <br>
        this.details = details.replaceAll("(?i)<html>", " ").replaceAll("(?i)</html>", " ").replaceAll(LINE_SEPARATOR, "<br>").trim();
        this.level = level;
    }

    /**
     * Returns message description in long or short format. Long format contains message title and details. Short format is just a message title.
     * In long format title and details are separated with single or double newline.
     * Html and standard version is supported.
     * In html mode description is wrapped in &lt;html&gt; and &lt;/html&gt; tags and &lt;br&gt; tag is used as line separator
     *
     * @param longFormat if true then returns title and details; if false then only title is returned
     * @param htmlMode if true then description is returned in html mode
     *
     */
    public String getDescription(boolean longFormat, boolean htmlMode) {
        //double newline in html mode
        String titleDetailsSep = htmlMode ? "<br><br>" : "<br>";
        String description = title + ((longFormat && !details.isEmpty()) ? titleDetailsSep + details : "");
        if (htmlMode)
            return "<html>" + description + "</html>";
        else {
            //replace newLineTagsRE tags to new line
            for (String nlTag : newLineTagsRE)
                description = description.replaceAll(nlTag, LINE_SEPARATOR);
            //TODO: unescape html entities (not only nbsp)
            //replace whitespaces
            description = description.replaceAll("&nbsp;", " ");
            //remove remaining tags 
            return description.replaceAll(allTagsRE, " ").trim();
        }
    }

    /**
     * Returns info in standard (non-html) mode which consists of sourceName and message description.
     */
    public String getInfo(boolean longFormat) {
        return sourceName + ": " + getDescription(longFormat, false);
    }

    /**
     * Same as {@link getInfo(false)}
     *
     * @return
     */
    @Override
    public String toString() {
        return getInfo(false);
    }

    /**
     * Returns message level.
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Returns application name.
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Returns source name.
     */
    public String getSourceName() {
        return sourceName;
    }

    /**
     * Getter to use for special cases. Typically {@link getDescription} should be used.
     * Returns raw title (no html/non-html processing).
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter to use for special cases. Typically {@link getDescription} should be used.
     * Returns raw details (no html/non-html processing).
     */
    public String getDetails() {
        return details;
    }
}
