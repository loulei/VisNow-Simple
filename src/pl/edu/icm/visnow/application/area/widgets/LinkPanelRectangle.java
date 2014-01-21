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

package pl.edu.icm.visnow.application.area.widgets;


import java.awt.Point;
import pl.edu.icm.visnow.application.area.Quad;
/**
 *
 * @author  Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class LinkPanelRectangle {
        private int fromX;
        private int fromY;
        private int toX;
        private int toY;
        private int getLx() {return (fromX<toX)?fromX:toX;}
        private int getLy() {return (fromY<toY)?fromY:toY;}
        private int getRx() {return (fromX>toX)?fromX:toX;}
        private int getRy() {return (fromY>toY)?fromY:toY;}
        public LinkPanelRectangle(int fromX, int fromY, int toX, int toY, int d) {
            int minX = (fromX<toX)?fromX:toX;
            int minY = (fromY<toY)?fromY:toY;
            int maxX = (fromX>toX)?fromX:toX;
            int maxY = (fromY>toY)?fromY:toY;
            this.fromX = minX-d;
            this.fromY = minY-d;
            this.toX = maxX+d;
            this.toY = maxY+d;
        }
        public boolean contains(Point p) {
            if(p.getX()>=fromX && p.getX()<=toX)
            if(p.getY()>=fromY && p.getY()<=toY)
                return true;
            return false;
        }
        public boolean isRectangled(Quad q) {
           if((getLx() > q.getLx() && getLx() < q.getRx()) ||
              (getRx() > q.getLx() && getRx() < q.getRx()) ||
              (getLx() < q.getLx() && getRx() > q.getLx()) ||
              (getLx() < q.getRx() && getRx() > q.getRx())
           )
           if((getLy() > q.getLy() && getLy() < q.getRy()) ||
              (getRy() > q.getLy() && getRy() < q.getRy()) ||
              (getLy() < q.getLy() && getRy() > q.getLy()) ||
              (getLy() < q.getRy() && getRy() > q.getRy())
           )
               return true;
            return false;
        }

    }