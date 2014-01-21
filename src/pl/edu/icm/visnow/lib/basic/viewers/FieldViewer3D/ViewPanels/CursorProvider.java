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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author babor
 */
public class CursorProvider {

    private static Cursor customMoveCursor;
    private static Image customMoveCursorImage;
    private static Point customMoveCursorHotSpot;
    
    private static Cursor customMoveHorizCursor;
    private static Image customMoveHorizCursorImage;
    private static Point customMoveHorizCursorHotSpot;
    
    private static Cursor customMoveVertCursor;
    private static Image customMoveVertCursorImage;
    private static Point customMoveVertCursorHotSpot;
    
    private static Cursor customRotateCursor;
    private static Image customRotateCursorImage;
    private static Point customRotateCursorHotSpot;
    
    private static boolean cursorsInitialized = false;

    public CursorProvider() {
        initCursors();
    }
    
    /**
     * @return the customMoveCursor
     */
    public Cursor getCustomMoveCursor() {
        return customMoveCursor;
    }

    /**
     * @return the customMoveHorizCursor
     */
    public Cursor getCustomMoveHorizCursor() {
        return customMoveHorizCursor;
    }

    /**
     * @return the customMoveVertCursor
     */
    public Cursor getCustomMoveVertCursor() {
        return customMoveVertCursor;
    }

    public Cursor getCustomMoveVertCursorRotated(float angle) {
        Image cImage;
        AffineTransform tr = new AffineTransform();
        tr.rotate(angle, customMoveVertCursorImage.getWidth(null)/2, customMoveVertCursorImage.getHeight(null)/2);
        BufferedImageOp op = new AffineTransformOp(tr, AffineTransformOp.TYPE_BICUBIC);
        cImage = op.filter((BufferedImage)customMoveVertCursorImage, null);        
        Point p = new Point(0,0);
        p.x = cImage.getWidth(null)/2;
        p.y = cImage.getHeight(null)/2;        
        Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(cImage, p, "MoveVertCursorRotated");
        return c;
    }
    
    /**
     * @return the customRotateCursor
     */
    public Cursor getCustomRotateCursor() {
        return customRotateCursor;
    }
    
    private void initCursors() {
        if(cursorsInitialized)
            return;
        
        try {
           Toolkit toolkit = Toolkit.getDefaultToolkit();


           customMoveCursorImage = ImageIO.read(getClass().getResourceAsStream("/pl/edu/icm/visnow/gui/icons/cursor_move.gif"));
           customMoveCursorHotSpot = new Point(0,0);
           customMoveCursorHotSpot.x = 14;
           customMoveCursorHotSpot.y = 14;
           customMoveCursor = toolkit.createCustomCursor(customMoveCursorImage, customMoveCursorHotSpot, "MoveCursor");

           customMoveHorizCursorImage = ImageIO.read(getClass().getResourceAsStream("/pl/edu/icm/visnow/gui/icons/cursor_move_horiz.gif"));
           customMoveHorizCursorHotSpot = new Point(0,0);
           customMoveHorizCursorHotSpot.x = 10;
           customMoveHorizCursorHotSpot.y = 6;
           customMoveHorizCursor = toolkit.createCustomCursor(customMoveHorizCursorImage, customMoveHorizCursorHotSpot, "MoveHorizCursor");

           customMoveVertCursorImage = ImageIO.read(getClass().getResourceAsStream("/pl/edu/icm/visnow/gui/icons/cursor_move_vert.gif"));
           customMoveVertCursorHotSpot = new Point(0,0);
           customMoveVertCursorHotSpot.x = 6;
           customMoveVertCursorHotSpot.y = 10;
           customMoveVertCursor = toolkit.createCustomCursor(customMoveVertCursorImage, customMoveVertCursorHotSpot, "MoveVertCursor");

           customRotateCursorImage = ImageIO.read(getClass().getResourceAsStream("/pl/edu/icm/visnow/gui/icons/cursor_rotate.gif"));
           customRotateCursorHotSpot = new Point(0,0);
           customRotateCursorHotSpot.x = 11;
           customRotateCursorHotSpot.y = 8;
           customRotateCursor = toolkit.createCustomCursor(customRotateCursorImage, customRotateCursorHotSpot, "RotateCursor");

        } catch (IOException e) {
            customMoveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
            customMoveHorizCursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
            customMoveVertCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
        }
        cursorsInitialized = true;
    }
    
    
}
