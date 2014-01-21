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
import java.awt.Component;
import java.util.HashMap;
import javax.swing.JSplitPane;
import org.w3c.dom.Node;

/**
 *
 * @author gacek
 */
public class FAreaSplit extends FAreaSplittable {





    protected boolean isMajor() {return false;}
    protected boolean isSplit() {return true;}
    protected boolean isSingle() {return false;}


    private FAreaSplittable son = new FAreaSingle(this);
    private FAreaSplittable daughter = new FAreaSingle(this);
    private JSplitPane pane = new JSplitPane();
    public FAreaSplittable getSon() {return son;}
    public FAreaSplittable getDaughter() {return daughter;}
    public FPlace getSomePlace() {return son.getSomePlace();}


//    public FAreaSplit(FArea parent) {
//        super(parent);
//        pane.setLeftComponent(son);
//        pane.setRightComponent(daughter);
//    }

    protected FAreaSplit(FArea parent, FAreaSplittable one, FAreaSplittable two, int direction) {
        super(parent);

        if(direction == topD || direction == bottomD) {
            pane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        } else {
            pane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        }
        pane.setDividerSize(9);
        pane.setOneTouchExpandable(true);

        if(direction == topD || direction == leftD) {
            son = two; daughter = one;
        } else {
            son = one; daughter = two;
        }



        pane.setLeftComponent(son);
        pane.setRightComponent(daughter);


        parent.replaceChild(one, this);
        one.setParentArea(this);
        two.setParentArea(this);

        getBottomLayer().add(pane, BorderLayout.CENTER);


        parent.validate();
        if(direction == topD || direction == bottomD) {
            pane.setDividerLocation(this.getHeight()/2);
        } else {
            pane.setDividerLocation(this.getWidth()/2);
        }

    }

    protected FAreaSplit(FArea parent, Node node, HashMap<String, Component> map) {
        super(parent);

   //     System.out.println("*\nSplit.new");
   //     System.out.println(node);

        boolean orientationH =
                node.getAttributes()
                .getNamedItem("dir").getNodeValue()
                .equalsIgnoreCase("horizontal");
        int position = Integer.parseInt(
                node.getAttributes().getNamedItem("position").getNodeValue()
                );


        if(orientationH) {
            pane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        } else {
            pane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        }

        pane.setDividerSize(9);
        pane.setOneTouchExpandable(true);

        Node n1=null;
        Node n2=null;

        for(int i=0; i<node.getChildNodes().getLength(); ++i) {
            Node tmp = node.getChildNodes().item(i);
            if(tmp.getNodeType()==tmp.ELEMENT_NODE) {
                if(n1==null) n1 = tmp; else n2 = tmp;
            }
        }

        if(n1.getNodeName().equalsIgnoreCase("single")) {
            son = new FAreaSingle(this,n1,map);
        } else {
            son = new FAreaSplit(this,n1,map);
        }

        if(n2.getNodeName().equalsIgnoreCase("single")) {
            daughter = new FAreaSingle(this,n2,map);
        } else {
            daughter = new FAreaSplit(this,n2,map);
        }

        pane.setLeftComponent(son);
        pane.setRightComponent(daughter);

        getBottomLayer().add(pane, BorderLayout.CENTER);


        parent.validate();
        pane.setDividerLocation(position);


    }


    protected void replaceChild(FAreaSplittable oldChild, FAreaSplittable newChild) {
        int div = pane.getDividerLocation();
        if(son.equals(oldChild)) {son = newChild; pane.setLeftComponent(son);}
        else {daughter = newChild; pane.setRightComponent(daughter);}
        pane.setDividerLocation(div);
    }


    public void setDividerLocation(int d) {
        this.pane.setDividerLocation(d);
    }

    @Override
    public void addBox(FBox box, int direction) {
        if(direction==centerD) {
            son.addBox(box);
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
        son.resize();
        daughter.resize();
        //TODO WHAT?
    }


    //protected void doDrop(FBox box, int dir) {
    //    addBox(box, dir);
    //}

    protected String writeXML(int i) {
        String d = "";
        for(int j=0; j<i; ++j) d+="  ";
        String ret = d+"<split dir=\"";
        ret += (pane.getOrientation()==JSplitPane.HORIZONTAL_SPLIT)?"horizontal":"vertical";
        ret += "\" position=\""+pane.getDividerLocation()+"\">\n";
        ret += this.getSon().writeXML(i+1);
        ret += this.getDaughter().writeXML(i+1);
        ret += d+"</split>\n";
        return ret;
    }

//    @Override
//    protected void initXML(Node node, HashMap<String, Component> map) {
//        throw new UnsupportedOperationException("Not supported yet.");
//   }
    protected void notifySplitListeners() {getParentArea().notifySplitListeners();}
    
    public JSplitPane getSplitPane() { return pane;}
}
