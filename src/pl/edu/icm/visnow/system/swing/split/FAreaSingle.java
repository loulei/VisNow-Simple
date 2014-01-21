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

package pl.edu.icm.visnow.system.swing.split;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import org.w3c.dom.Node;

/**
 *
 * @author gacek
 */
public class FAreaSingle extends FAreaSplittable {

    
    protected boolean isMajor() {return false;}
    protected boolean isSplit() {return false;}
    protected boolean isSingle() {return true;}

    private FPlace place = new FPlace(this);
    public FPlace getPlace() {return place;}
    public FPlace getSomePlace() {return place;}


    private boolean remove = false;
    public boolean isRemoved() {return remove;}

    public FAreaSingle(FArea parent) {
        super(parent);
       // if(parent == null) System.out.println("SINGLE: PIERWSZY ERROR");
        getBottomLayer().add(place, BorderLayout.CENTER);
        this.setBackground(Color.red);
    }

    protected FAreaSingle(FArea parent, Node xml, HashMap<String, Component> map) {
        this(parent);
        //if(parent == null) System.out.println("SINGLE: DRUGI ERROR");
//        System.out.println("*\nSingle.new");
//        System.out.println(xml);
        for(int i=0; i<xml.getChildNodes().getLength(); ++i) {
            Node tmp = xml.getChildNodes().item(i);
            if(tmp.getNodeName().equalsIgnoreCase("box")) {
                String name = tmp.getAttributes().getNamedItem("name").getNodeValue();
                if(map.containsKey(name)) {
                    this.addBox(new FBox(name, map.get(name)));
                }
            }
        }
    }



    void markForRemoval() {
        remove = true;
        getMajor().markPlaceForRemoval(this);

    }
        
    protected void replaceChild(FAreaSplittable oldChild, FAreaSplittable newChild) {}


    @Override
    public void addBox(FBox box, int direction) {
        if(direction==centerD) {
            place.addBox(box);
            //notifySplitListeners();
            return;
        }


        FAreaSingle brother = new FAreaSingle(null);
        brother.addBox(box);
        new FAreaSplit(getParentArea(), this, brother, direction);
        //notifySplitListeners();
    }


    @Override
    protected void resize() {
        super.resize();
        place.resize();
        //TODO WHAT?
    }

   // protected void doDrop(FBox box, int dir) {
   //     addBox(box, dir);
   // }

    protected void unmarkForRemoval() {
        if(this.remove) {
            this.remove = false;
            this.getMajor().markPlaceForRemoval(null);
        }
    }

    protected void performRemoval() {
        //System.out.println("Perform removal");
        FArea par = this.getParentArea();
        if(!par.isSplit()) return;
        FAreaSplit parent = (FAreaSplit)par;
        FArea grand = parent.getParentArea();

        FAreaSplittable brother;
        if(parent.getSon().equals(this)) brother = parent.getDaughter();
        else brother = parent.getSon();

        brother.setParentArea(grand);
        grand.replaceChild(parent, brother);
        grand.validate();
    }

    protected String writeXML(int i) {
        String d = "";
        for(int j=0; j<i; ++j) d+="  ";
        String ret = d+"<single>\n";
        ret += this.getPlace().writeInnerXML(i+1);
        ret += d+"</single>\n";
        return ret;
    }

    protected void notifySplitListeners() {getParentArea().notifySplitListeners();}
}
