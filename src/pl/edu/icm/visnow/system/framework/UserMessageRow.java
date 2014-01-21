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
package pl.edu.icm.visnow.system.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.edu.icm.visnow.system.utils.usermessage.UserMessage;

/**
 * User message extension used in UserMessagePanel. 
 * This includes: column order, expanded flag.
 * 
 * @author szpak
 */
public class UserMessageRow extends UserMessage{
    private boolean expanded = false;
    
    /**
     * Just a wrapper around UserMessage constructor. Constructs new UserMessageRow with the same values as in <code>userMessage</code>.
     * By default userMessageRow is collapsed (<code>expanded == false</code>).
     */
    public UserMessageRow(UserMessage userMessage) {
        super(userMessage.getApplicationName(), userMessage.getSourceName(), userMessage.getTitle(), userMessage.getDetails(), userMessage.getLevel());
    }
    
    public static enum ColumnName {

        LEVEL, APPLICATION, SOURCE, DESCRIPTION
    }
    //Column order to display; 
    public static final ColumnName[] COLUMN_ORDER = {ColumnName.LEVEL, ColumnName.APPLICATION, ColumnName.SOURCE, ColumnName.DESCRIPTION};

    /**
     * Returns
     * <code>columnName</code> column index according to
     * <code>COLUMN_ORDER</code>. Throws {@code IllegalArgumentException} if
     * <code>rowName</code> is not on the list.
     */
    public static int getColumnIndex(ColumnName columnName) {
        for (int i = 0; i < COLUMN_ORDER.length; i++)
            if (COLUMN_ORDER[i] == columnName) return i;
        throw new IllegalArgumentException("Column name: " + columnName + " is not specified in COLUMN_ORDER: " + Arrays.toString(COLUMN_ORDER));
    }

    /**
     * Returns column name at selected
     * <code>index</code>
     */
    public static ColumnName getColumnName(int index) {
        return COLUMN_ORDER[index];
    }

//    /**
//     * Returns this message as an array of Strings in order
//     * <code>COLUMN_ORDER</code>
//     *
//     * @param longFormat true for long format
//     */
//    public String[] toRow(boolean longFormat) {
//        List<String> row = new ArrayList<String>();
//
//        for (ColumnName columnName : COLUMN_ORDER)
//            switch (columnName) {
//                case LEVEL:
//                    row.add(level.name());
//                    break;
//                case APPLICATION:
//                    row.add(applicationName);
//                    break;
//                case SOURCE:
//                    row.add(sourceName);
//                    break;
//                case DESCRIPTION:
//                    row.add(getDescription(longFormat, true));
//                    break;
//            }
//
//        return row.toArray(new String[row.size()]);
//    }

    /**
     * Returns fake row. This method gives table of length dependent on {@link COLUMN_ORDER} of same copy this UserMessageRow. 
     * It's cell renderer that gets proper cell value from the row.
     */
    public UserMessageRow[] toFakeRow() {
        List<UserMessageRow> fakeRow = new ArrayList<UserMessageRow>(COLUMN_ORDER.length);
        for (int i = 0; i < COLUMN_ORDER.length; i++) fakeRow.add(this);
        
        return fakeRow.toArray(new UserMessageRow[fakeRow.size()]);
    }
    
    /**
     * Toggles expanded.
     */
    public void toggleExpanded() {
        setExpanded(!expanded);
    }

    /**
     * Returns expanded flag
     */
    public boolean isExpanded() {
        return expanded;
    }
    
    /**
     * Sets expanded flag to <code>expanded</code>
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
