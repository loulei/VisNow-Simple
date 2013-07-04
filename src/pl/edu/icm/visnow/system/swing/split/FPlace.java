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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author gacek
 */
public class FPlace extends Container {


    //
    private HashMap<String, FBox> boxes;
    private HashMap<String, FSplitButton> buttons;
    private Vector<String> labels;
    protected int getBoxCount() {return labels.size();}

    //
    private FAreaSingle parent;
    public FAreaSingle getParentArea() {return parent;}
    protected void setParent(FAreaSingle parent) {this.parent = parent;}

    //
    private JPanel center;

    private JPanel top1;
    private JPanel top2;
    private JPanel bar;

    //
    private JButton closeButton;
    private JButton leftButton;
    private JButton rightButton;


    //
    private int currentLabel = -1;
    private int firstLabel = -1;
    private int lastLabel = -1;


    //<editor-fold defaultstate="collapsed" desc=" [CONSTRUCTOR] ">
    public FPlace(FAreaSingle parent) {

        //
        boxes = new HashMap<String, FBox>();
        buttons = new HashMap<String, FSplitButton>();
        labels = new Vector<String>();

        //
        this.parent = parent;


        //
        center = new JPanel();
        top1 = new JPanel();
        top2 = new JPanel();
        bar = new JPanel();
        
        this.setLayout(new BorderLayout());
        top1.setLayout(new BorderLayout());
        top1.setPreferredSize(new Dimension(1000,FSplitUI.header));
        top2.setLayout(new BorderLayout());
        bar.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        center.setLayout(new CardLayout());
        bar.setBackground(FSplitUI.normalColor);

        //RELEASE-OFF
        //this.add(top1, BorderLayout.NORTH);
        this.add(center, BorderLayout.CENTER);
        top1.add(top2, BorderLayout.CENTER);
        top2.add(bar, BorderLayout.CENTER);


        //
        closeButton = new JButton("\u2716");//\u2327
        leftButton = new JButton("\u22B2");//("\u2190");
        rightButton = new JButton("\u22B3");//("\u2192");
        top1.add(closeButton, BorderLayout.EAST);
        top2.add(leftButton, BorderLayout.WEST);
        top2.add(rightButton, BorderLayout.EAST);

        closeButton.setMargin(new Insets(0,10,0,10));
        leftButton.setMargin(new Insets(0,2,0,2));
        rightButton.setMargin(new Insets(0,2,0,2));
        closeButton.setBackground(new Color(200,200,200));
        leftButton.setBackground(new Color(200,200,200));
        rightButton.setBackground(new Color(200,200,200));


        leftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                minusLabel();
            }
        });
        rightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                plusLabel();
            }
        });
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //RELEASE-OFF
                //closeCurrent();
            }
        });

 }

    //</editor-fold>

 
 

    //<editor-fold defaultstate="collapsed" desc=" Renew Labels ">
    protected void renewLabels() {
        //Main.Debug1("RENEW LABELS, "+labels.size()+"="+buttons.size()+"="+boxes.size()+" items");
        //String str = "";
        //for(String l: labels) str+="["+l+"]";
        //M//ain.Debug1(str);

        bar.removeAll();
        if(labels.isEmpty()) return;
        int wmax = bar.getWidth();
        int wcur = 0;
        
        firstLabel = currentLabel;
        lastLabel = currentLabel;


        //System.out.println("FIRST FOR");
        for(int i=currentLabel; i<labels.size(); ++i) {
          //  System.out.println("<"+firstLabel+"["+currentLabel+"]"+lastLabel+")");
            int wi = buttons.get(labels.elementAt(i)).getWidth();
            if(wi + wcur >= wmax) {break;}
            bar.add(buttons.get(labels.elementAt(i)));
            wcur += wi;
            lastLabel++;
        }

        //System.out.println("SECOND FOR");
        for(int i=currentLabel-1; i>=0; --i) {
          //  System.out.println("<"+firstLabel+"["+currentLabel+"]"+lastLabel+")");
            int wi = buttons.get(labels.elementAt(i)).getWidth();
            if(wi + wcur >= wmax) {break;}
            bar.add(buttons.get(labels.elementAt(i)),0);
            wcur += wi;
            firstLabel=i;
        }

        rightButton.setEnabled(lastLabel!=labels.size());
        leftButton.setEnabled(firstLabel!=0);
        top1.validate();
        bar.validate();
        repaint();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Correct Labels ">
    protected void correctLabels() {
        //if(firstLabel == -1 || lastLabel == -1) return;
        if(labels.isEmpty()) return;
        int wmax = bar.getWidth();

        
        if(currentLabel == -1) {
            bar.removeAll();
            return;
        }
        if(firstLabel == -1 || lastLabel == -1) {
            firstLabel = currentLabel;
            lastLabel = currentLabel+1;
            bar.removeAll();
            bar.add(buttons.get(labels.elementAt(currentLabel)));
        }
        /*if(wmax -50 < buttons.get(labels.elementAt(currentLabel)).getWidth()) {
            rightButton.setEnabled(lastLabel!=labels.size());
            leftButton.setEnabled(firstLabel!=0);
            top1.validate();
            return;
        }*/


        int wsum = 0;

        for(int i=firstLabel; i<lastLabel; ++i)
            wsum += buttons.get(labels.elementAt(i)).getWidth();

        if(wsum > wmax) {
            //deleting
            while(wsum > wmax) {
                --lastLabel; //if(lastLabel<0) return;
                if(lastLabel==currentLabel) {++lastLabel; break;}
                bar.remove(buttons.get(labels.elementAt(lastLabel)));
                wsum -= buttons.get(labels.elementAt(lastLabel)).getWidth();
            }
            while(wsum > wmax) {
                if(firstLabel==currentLabel) break; //if(firstLabel == labels.size()) {firstLabel = -1; return;}
                bar.remove(buttons.get(labels.elementAt(firstLabel)));
                wsum -= buttons.get(labels.elementAt(firstLabel)).getWidth();
                ++firstLabel;
            }
        } else {
            while(lastLabel != labels.size()) {
                int wi = buttons.get(labels.elementAt(lastLabel)).getWidth();
                if(wsum+wi>wmax) break;
                wsum+=wi;
                bar.add(buttons.get(labels.elementAt(lastLabel)));
                lastLabel++;
            }
            while(firstLabel != 0) {
                int wi = buttons.get(labels.elementAt(firstLabel-1)).getWidth();
                if(wsum+wi>wmax) break;
                wsum+=wi;
                --firstLabel;
                bar.add(buttons.get(labels.elementAt(firstLabel)),0);
            }
        }

        rightButton.setEnabled(lastLabel!=labels.size());
        leftButton.setEnabled(firstLabel!=0);
        top1.validate();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Add Box ">
    public void addBox(String name, Component comp) {
        addBox(new FBox(name, comp));
    }

    public void addBox(final FBox box) {

        FSplitButton jb = new FSplitButton(box.getName(),box);
        jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {selectBox(box.getName());}
        });

        boxes.put(box.getName(),box);
        buttons.put(box.getName(), jb);
        
        int i=0;
        while(i<labels.size()) {
            if(labels.elementAt(i).compareTo(box.getName())<0)
                ++i;
            else
                break;
        }
        labels.add(i,box.getName());

        center.add(box.getComponent(), box.getName());
        box.setPlace(this);

        selectBox(i);
        renewLabels();
        parent.unmarkForRemoval();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Select Box ">
    private void selectBox(String label) {
        selectBox(labels.indexOf(label));
    }

    private void selectBox(int i) {
        for(FSplitButton f: buttons.values()) {
            f.setActive(false);
        }
        //if(currentLabel!=-1)
        //    buttons.get(labels.elementAt(currentLabel)).setActive(false);
        currentLabel = i;
        buttons.get(labels.elementAt(i)).setActive(true);
        ((CardLayout)center.getLayout()).show(center,labels.elementAt(i));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Remove Box ">
    public void removeBox(FBox box) {

       // System.out.println("Remove box");

        int id = labels.indexOf(box.getName());
        
       // Main.Debug1("  remove box");
        boxes.remove(box.getName());
        buttons.remove(box.getName());
        labels.remove(box.getName());
        
        if(labels.isEmpty()) {
            setNull();
            parent.markForRemoval();
            return;
        }

        boolean change = (currentLabel == id);
        if(firstLabel > id) --firstLabel;
        if(currentLabel > id) --currentLabel;
        if(lastLabel > id) --lastLabel;

        if(change) {
            currentLabel = -1;
            selectBox(0);
        }


        //System.out.println("  remove panel");
        center.remove(box.getComponent());
        renewLabels();
        validate();
        //System.out.println("  remove done");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Set null ">
    void setNull() {
        this.bar.removeAll();
        this.center.removeAll();
        boxes.clear();
        buttons.clear();
        labels.clear();
        leftButton.setEnabled(false);
        rightButton.setEnabled(false);
        parent.validate();
    }

    //</editor-fold>

    protected void resize() {
        correctLabels();
//        renewLabels();
        //TODO WHAT?
    }


    private void printBounds() {
        System.out.println("");
        System.out.println(labels.size()+" labels, ["+firstLabel+"["+currentLabel+"]"+lastLabel+"]");
        System.out.println("width: "+this.getWidth()+" > "+bar.getWidth());
        String str = "buttons: ";
        for(int i=0; i<labels.size(); ++i)
            str += "["+buttons.get(labels.elementAt(i)).getWidth()+"]";
        System.out.println(str);
    }

    private void closeCurrent() {
        this.removeBox(boxes.get(labels.elementAt(currentLabel)));
        this.getParentArea().getMajor().checkRemoval();
    }













    //<editor-fold defaultstate="collapsed" desc=" Minus label ">
    private void minusLabel() {
        if(firstLabel <= 0) return;
        --firstLabel;

        int wmax = bar.getWidth();

        int wsum = 0;

        bar.removeAll();

        int current = firstLabel;
        while(current < labels.size()) {
            int wi = buttons.get(labels.elementAt(current)).getWidth();
            if(wsum+wi>wmax) break;
            wsum+=wi;
            bar.add(buttons.get(labels.elementAt(current)));
            ++current;
        }

        lastLabel = current;

        rightButton.setEnabled(lastLabel!=labels.size());
        leftButton.setEnabled(firstLabel!=0);
        top1.validate();
        repaint();
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Plus label ">
    private void plusLabel() {
        if(lastLabel > labels.size()) return;

        ++lastLabel;

        int wmax = bar.getWidth();

        int wsum = 0;

        bar.removeAll();

        int current = lastLabel;
        while(current > 0 ) {
            --current;
            int wi = buttons.get(labels.elementAt(current)).getWidth();
            if(wsum+wi>wmax) break;
            wsum+=wi;
            bar.add(buttons.get(labels.elementAt(current)),0);
        }

        firstLabel = current+1;
        rightButton.setEnabled(lastLabel!=labels.size());
        leftButton.setEnabled(firstLabel!=0);
        top1.validate();
        repaint();
    }

    //</editor-fold>

    protected String writeInnerXML(int i) {
        String d = "";
        for(int j=0; j<i; ++j) d+="  ";
        String ret = "";
        for(FBox box: this.boxes.values()) {
            ret += d+"<box name=\""+box.getName()+"\"/>\n";
        }
        return ret;
    }
        
}
