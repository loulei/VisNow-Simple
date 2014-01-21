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

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import pl.edu.icm.visnow.application.area.AreaPanel;
import pl.edu.icm.visnow.application.area.Quad;
import pl.edu.icm.visnow.application.area.SelectableAreaItem;
import pl.edu.icm.visnow.engine.element.ElementSaturationListener;
import pl.edu.icm.visnow.engine.element.ElementState;
import pl.edu.icm.visnow.engine.element.ElementStateListener;
import pl.edu.icm.visnow.engine.error.Hubert;
import pl.edu.icm.visnow.engine.core.Input;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.main.ModuleBox;
import pl.edu.icm.visnow.engine.core.Output;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.swing.VNSwingUtils;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class ModulePanel extends JPanel implements SelectableAreaItem {

    //<editor-fold defaultstate="collapsed" desc=" [VAR] Dimensions ">
    protected final static int GLOWBORDER = 6;
    private final static int BUTTONSIZE = 10;
    private final static int BUTTONLEFT = 4;
    private final static int BUTTONTOP = 7;
    private final static int LABELHEIGHT = 10;
    private final static int LABELLEFT = 20;
    private final static int LABELTOP = 7;
    private final static int PROGRESSTOP = 15;
    private final static int PROGRESSHEIGHT = 4;
    private final static int PROGRESSMARGIN = 1;
    private final static int INPUTHEIGHT = 4;
    private final static int CENTERHEIGHT = 20;
    private final static int OUTPUTHEIGHT = 4;
    private final static int INITWIDTH = 150;
    private final static int PORTHEIGHT = 10;
    private final static int PORTWIDTH = 16;
    private final static int PORTSPACING = 24;
    private final static int PORTMARGIN = 8;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Rectangling ">
    private int getLx() {
        return this.getX() + GLOWBORDER;
    }

    private int getLy() {
        return this.getY() + GLOWBORDER;
    }

    private int getRx() {
        return this.getX() + this.getWidth() - GLOWBORDER;
    }

    private int getRy() {
        return this.getY() + this.getHeight() - GLOWBORDER;
    }

    public boolean isRectangled(Quad q) {
        if ((getLx() > q.getLx() && getLx() < q.getRx())
                || (getRx() > q.getLx() && getRx() < q.getRx())
                || (getLx() < q.getLx() && getRx() > q.getLx())
                || (getLx() < q.getRx() && getRx() > q.getRx())) {
            if ((getLy() > q.getLy() && getLy() < q.getRy())
                    || (getRy() > q.getLy() && getRy() < q.getRy())
                    || (getLy() < q.getLy() && getRy() > q.getLy())
                    || (getLy() < q.getRy() && getRy() > q.getRy())) {
                return true;
            }
        }
        return false;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" [VAR] Layers ">
    Integer glowI = new Integer(8);
    Integer backI = new Integer(10);
    Integer mainI = new Integer(20);
    Integer parametersI = new Integer(21);
    Integer portI = new Integer(30);
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" [VAR] Gui ">
    private javax.swing.JLayeredPane layeredPane;
    private JPanel button;
    private JLabel nameLabel;
    private JPanel inputPanel;
    private JPanel centerPanel;
    private JPanel outputPanel;
    private ModuleGlowPanel glow;
    private JProgressBar progress;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Scene - hierarchia ">
    private AreaPanel areaPanel;

    public AreaPanel getAreaPanel() {
        return areaPanel;
    }
    private HashMap<String, PortPanel> inputPanels;
    private HashMap<String, PortPanel> outputPanels;

    
    public PortPanel getInputPanel(String name) {
        return inputPanels.get(name);
    }

    public PortPanel[] getAllInputPanels() {
        Collection<PortPanel> tmp = inputPanels.values();
        PortPanel[] out = new PortPanel[tmp.size()];
        int i = 0;
        for(PortPanel pp : tmp) {
            out[i++] = pp;
        }
        return out;
    }
    
    public PortPanel getOutputPanel(String name) {
        return outputPanels.get(name);
    }

    public PortPanel[] getAllOutputPanels() {
        Collection<PortPanel> tmp = outputPanels.values();
        PortPanel[] out = new PortPanel[tmp.size()];
        int i = 0;
        for(PortPanel pp : tmp) {
            out[i++] = pp;
        }
        return out;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" [VAR] Module ">
    private ModuleBox module;

    public ModuleBox getModule() {
        return module;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" menu items ">
    private javax.swing.JMenuItem mbDelete;
    private javax.swing.JCheckBoxMenuItem mbParameters;
    private javax.swing.JMenuItem mbRename;
    private javax.swing.JMenuItem mbRun;
    private javax.swing.JMenuItem mbRefresh;
    private javax.swing.JMenuItem mbHelp;
    private javax.swing.JPopupMenu popupMenu;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [CONSTRUCTOR] ">
    public ModulePanel(AreaPanel area, ModuleBox module) {

        this.areaPanel = area;
        this.module = module;

        //<editor-fold defaultstate="collapsed" desc=" UI - layeredPane ">
        layeredPane = new javax.swing.JLayeredPane();
        setBackground(new java.awt.Color(0, 0, 0, 0));//TODO: przenies kolor do SwingUtils
        layeredPane.setBackground(new java.awt.Color(0, 0, 0, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(layeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(layeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 26, Short.MAX_VALUE));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc=" UI - button ">
        button = new JPanel();
        button.setBackground(new Color(153, 153, 153));
        button.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 0, 0)));
        button.setBounds(
                BUTTONLEFT + GLOWBORDER,
                BUTTONTOP + GLOWBORDER,
                BUTTONSIZE,
                BUTTONSIZE);
        layeredPane.add(button, mainI);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc=" UI - nameLabel ">
        nameLabel = new JLabel(module.getName());
        nameLabel.setFont(new java.awt.Font("Tahoma", 1, 9));
        nameLabel.setBounds(
                LABELLEFT + GLOWBORDER,
                LABELTOP + GLOWBORDER,
                INITWIDTH,
                LABELHEIGHT);
        layeredPane.add(nameLabel, mainI);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc=" UI - progress ">
        progress = new JProgressBar();
        progress.setMinimum(0);
        progress.setMaximum(1024);
        progress.setValue(0);
        progress.setBorder(null);
        progress.setBounds(
                GLOWBORDER + PROGRESSMARGIN,
                GLOWBORDER + INPUTHEIGHT + PROGRESSTOP,
                INITWIDTH - PROGRESSMARGIN - PROGRESSMARGIN,
                PROGRESSHEIGHT);
        progress.setForeground(VNSwingUtils.STATE_PROGRESS);
        layeredPane.add(progress, mainI);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc=" UI - input, center, outputPanel ">
        inputPanel = new JPanel();
        inputPanel.setBackground(new Color(238, 238, 238));
        inputPanel.setBounds(
                GLOWBORDER,
                GLOWBORDER,
                INITWIDTH,
                INPUTHEIGHT);
        layeredPane.add(inputPanel, backI);
        centerPanel = new JPanel();
        centerPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(153, 153, 153)));
        centerPanel.setBackground(new Color(255, 255, 255));
        centerPanel.setBounds(
                GLOWBORDER,
                GLOWBORDER + INPUTHEIGHT,
                INITWIDTH,
                CENTERHEIGHT);
        layeredPane.add(centerPanel, backI);
        outputPanel = new JPanel();
        outputPanel.setBackground(new Color(238, 238, 238));
        outputPanel.setBounds(
                GLOWBORDER,
                GLOWBORDER + INPUTHEIGHT + CENTERHEIGHT,
                INITWIDTH,
                OUTPUTHEIGHT);
        layeredPane.add(outputPanel, backI);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc=" Inputs, Outputs ">
        inputPanels = new HashMap<String, PortPanel>();
        outputPanels = new HashMap<String, PortPanel>();

        PortPanel port;


        Vector<Input> tab = getModule().getInputs().getSortedInputs();

        int left = GLOWBORDER + PORTMARGIN;
        //for(Input input: getModuleBoxEgg().getInputs().values()) {
        for (Input input : tab) {
            port = new PortPanel(this, input);
            inputPanels.put(port.getPort().getName(), port);
            //tab[i].addPortVisibilityChangeListener(pvcListener);

            if (!input.isVisible()) {
                continue;
            }

            port.setBounds(left, 0, PORTWIDTH, PORTHEIGHT);
            left += PORTSPACING;
            layeredPane.add(port, portI);


        }
        //}

        Vector<Output> tabb = getModule().getOutputs().getSortedOutputs();

        left = PORTMARGIN + GLOWBORDER;


        for (Output output : tabb) {

            //for(Output output: getModuleBoxEgg().getOutputs().values()) {
            port = new PortPanel(this, output);

            outputPanels.put(port.getPort().getName(), port);

            //tabb[i].addPortVisibilityChangeListener(pvcListener);

            if (!output.isVisible()) {
                continue;
            }

            port.setBounds(
                    left,
                    GLOWBORDER + INPUTHEIGHT + CENTERHEIGHT,
                    PORTWIDTH,
                    PORTHEIGHT);
            left += PORTSPACING;

            layeredPane.add(port, portI);

        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc=" Glow ">
        glow = new ModuleGlowPanel();
        glow.setBounds(
                0, 0,
                INITWIDTH + GLOWBORDER + GLOWBORDER,
                GLOWBORDER + INPUTHEIGHT + CENTERHEIGHT + OUTPUTHEIGHT + GLOWBORDER);
        //</editor-fold>

        setSelected(false);


        //<editor-fold defaultstate="collapsed" desc=" Menu ">
        popupMenu = new javax.swing.JPopupMenu();
        mbRun = new javax.swing.JMenuItem();
        mbRename = new javax.swing.JMenuItem();
        mbDelete = new javax.swing.JMenuItem();
        mbRefresh = new javax.swing.JMenuItem();
        mbHelp = new javax.swing.JMenuItem();

        mbRun.setText("Run");
        mbRun.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mbRunActionPerformed(evt);
            }
        });

        popupMenu.add(mbRun);

        mbRename.setText("Rename");
        mbRename.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mbRenameActionPerformed(evt);
            }
        });

        popupMenu.add(mbRename);

        mbDelete.setText("Delete");
        mbDelete.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mbDeleteActionPerformed(evt);
            }
        });

        popupMenu.add(mbDelete);

        mbRefresh.setText("Refresh");
        mbRefresh.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mbRefreshActionPerformed(evt);
            }
        });
        //popupMenu.add(mbRefresh);

        mbHelp.setText("Help");
        mbHelp.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mbHelpActionPerformed(evt);
            }
        });

        popupMenu.add(mbHelp);

        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc=" Button mouse listener ">
//        button.addMouseListener(new java.awt.event.MouseAdapter() {
//
//            @Override
//            public void mouseClicked(java.awt.event.MouseEvent evt) {
//                buttonPanelMouseClicked(evt);
//            }
//        });
        //</editor-fold>

        this.add(popupMenu);

        //<editor-fold defaultstate="collapsed" desc=" Saturation listener ">
        module.getElement().addSaturationListener(new ElementSaturationListener() {

            public void saturationChanged() {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        checkSaturation();
                    }
                });
            }
        });
        //</editor-fold>

        checkSaturation();

        //<editor-fold defaultstate="collapsed" desc=" State listener ">
        module.getElement().addElementStateListener(new ElementStateListener() {

            public void elementStateSet(final ElementState state) {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        progress.setValue(0);
                        switch (state) {
                            case passive:
                                progress.setBackground(VNSwingUtils.STATE_PASSIVE);
                                break;
                            case done:
                                progress.setBackground(VNSwingUtils.STATE_PASSIVE);
                                break;
                            case notifying:
                                progress.setBackground(VNSwingUtils.STATE_NOTIFYING);
                                break;
                            case ready:
                                progress.setBackground(VNSwingUtils.STATE_READY);
                                break;
                            case active:
                                progress.setBackground(VNSwingUtils.STATE_ACTIVE);
                                break;
                            case propagating:
                                progress.setBackground(VNSwingUtils.STATE_PROPAGATING);
                                break;
                        }
                    }
                });
            }
        });
        //</editor-fold>

        refresh();

    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Zaznaczenie ">
    public String getModuleForSelecting() {
        return module.getName();
    }
    protected boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean b) {
        this.selected = b;
        if (b) {
            layeredPane.remove(glow);
            layeredPane.add(glow, glowI);
        } else {
            layeredPane.remove(glow);
        }
        repaint();
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Check saturation ">
    private void checkSaturation() {
        switch (getModule().getElement().getSaturation()) {
            case notLinked:
                button.setBackground(VNSwingUtils.SATURATION_NOTLINKED);
                break;
            case noData:
                button.setBackground(VNSwingUtils.SATURATION_NODATA);
                break;
            case wrongData:
                button.setBackground(VNSwingUtils.SATURATION_WRONGDATA);
                break;
            case ok:
                button.setBackground(VNSwingUtils.SATURATION_OK);
                break;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Szerokosc ">
    public void refreshInputs() {
        Vector<Input> tab = getModule().getInputs().getSortedInputs();
        int k = 0;

        for (Input input : tab) {
            PortPanel port = inputPanels.get(input.getName());
            if (input.isVisible()) {
                if (port.getParent() == layeredPane) {
                } else {
                    layeredPane.add(port, portI);
                }
                port.setBounds(
                        k * PORTSPACING + PORTMARGIN + GLOWBORDER,
                        0,
                        PORTWIDTH,
                        PORTHEIGHT);
                ++k;
            } else {
                if (port.getParent() == layeredPane) {
                    if (port.getPort().isLinked() == false) {
                        layeredPane.remove(port);
                    }
                }
            }
        }
        correctWidth();
    }

    public void refreshOutputs() {
        outputPanel.setBounds(
                GLOWBORDER,
                GLOWBORDER + INPUTHEIGHT + CENTERHEIGHT + OUTPUTHEIGHT + GLOWBORDER,
                INITWIDTH,
                OUTPUTHEIGHT);
        Vector<Output> tab = getModule().getOutputs().getSortedOutputs();
        int k = 0;

        for (Output output : tab) {
            PortPanel port = outputPanels.get(output.getName());
            if (output.isVisible()) {
                if (port.getParent() == layeredPane) {
                } else {
                    layeredPane.add(port, portI);
                }
                port.setBounds(
                        k * PORTSPACING + PORTMARGIN + GLOWBORDER,
                        GLOWBORDER + INPUTHEIGHT + CENTERHEIGHT,
                        PORTWIDTH,
                        PORTHEIGHT);
                ++k;
            } else {
                if (port.getParent() == layeredPane) {
                    if (port.getPort().isLinked() == false) {
                        layeredPane.remove(port);
                    }
                }
            }
        }
        correctWidth();
    }

    public void refreshPorts() {
        refreshInputs();
        refreshOutputs();

    }

    public int getVisibleWidth() {
        int ins = 0;
        for (Input input : getModule().getInputs().getSortedInputs()) {
            if (input.isVisible()) {
                ins++;
            }
        }
        int outs = 0;
        for (Output output : getModule().getOutputs().getSortedOutputs()) {
            if (output.isVisible()) {
                outs++;
            }
        }

        //this.getModuleBoxEgg().getInputs().values().size();
        //int outs = this.getModuleBoxEgg().getOutputs().values().size();
        ins = (ins > outs) ? ins : outs;
        outs = (PORTMARGIN + PORTWIDTH) * ins + PORTMARGIN;

        int label = nameLabel.getFontMetrics(nameLabel.getFont()).charsWidth(nameLabel.getText().toCharArray(), 0, nameLabel.getText().length());

        label += 34;//TODO

        if (label > outs) {
            outs = label;
        }

        return (outs > INITWIDTH) ? outs : INITWIDTH;
    }

    public void setCorrectSize() {
        setCorrectSize((int) this.getBounds().getWidth());
    }

    public void setCorrectSize(int width) {

        this.setBounds(
                (int) this.getBounds().getX(),
                (int) this.getBounds().getY(),
                width,
                GLOWBORDER + INPUTHEIGHT + CENTERHEIGHT + OUTPUTHEIGHT + GLOWBORDER);

        glow.setBounds(
                0, 0,
                width,
                GLOWBORDER + INPUTHEIGHT + CENTERHEIGHT + OUTPUTHEIGHT + GLOWBORDER);

    }

    public void correctWidth() {

        int w = getVisibleWidth();

        inputPanel.setBounds(
                GLOWBORDER,
                GLOWBORDER,
                w,
                INPUTHEIGHT);
        centerPanel.setBounds(
                GLOWBORDER,
                GLOWBORDER + INPUTHEIGHT,
                w,
                CENTERHEIGHT);
        nameLabel.setBounds(
                GLOWBORDER + LABELLEFT,
                GLOWBORDER + LABELTOP,
                w - 26 - 8,//TODO
                LABELHEIGHT);
        progress.setBounds(
                GLOWBORDER + PROGRESSMARGIN,
                GLOWBORDER + INPUTHEIGHT + PROGRESSTOP,
                w - PROGRESSMARGIN - PROGRESSMARGIN,
                PROGRESSHEIGHT);
        outputPanel.setBounds(
                GLOWBORDER,
                GLOWBORDER + INPUTHEIGHT + CENTERHEIGHT,
                w,
                OUTPUTHEIGHT);
        glow.setBounds(
                0, 0,
                w + GLOWBORDER + GLOWBORDER,
                GLOWBORDER + INPUTHEIGHT + CENTERHEIGHT + OUTPUTHEIGHT + GLOWBORDER);

        setCorrectSize(w + GLOWBORDER + GLOWBORDER);

        if(getAreaPanel() != null)
            getAreaPanel().repaint();
        else 
            this.repaint();

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" refresh ">
    public void refresh() {
        nameLabel.setText(getModule().getName());
        centerPanel.setBackground(VNSwingUtils.color(15, 15, 15)); //break;

        correctWidth();

        repaint();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Menu actions ">

    private void buttonPanelMouseClicked(java.awt.event.MouseEvent evt) {
        if (!this.getAreaPanel().isLocked()) {
            popupMenu.show(button, 5, 5);
        }
    }

    public void showPopupMenu(int x, int y) {
        popupMenu.show(this, x, y);
    }

    public void showPopupMenu() {
        showPopupMenu(button.getX()+button.getWidth()/2, button.getY()+button.getHeight()/2);
    }
    
    private void mbDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        getAreaPanel().getArea().getOutput().deleteModule(this.getModule().getName(),
                this.getModule().getCore().getCoreName(),
                this.getLocation());
    }

    private void mbRefreshActionPerformed(java.awt.event.ActionEvent evt) {
        updateOutputs();
    }

    private void mbRenameActionPerformed(java.awt.event.ActionEvent evt) {
        repaint();

        String jop = (String) JOptionPane.showInputDialog(this, "Enter module name:\n", "Rename", JOptionPane.PLAIN_MESSAGE, null, null, getModule().getName());
        if (jop != null && !jop.equals("")) {
            getAreaPanel().getArea().getOutput().renameModule(
                    getModule().getName(),
                    jop);
            nameLabel.setText(getModule().getName());
            repaint();
        }
    }

    private void mbRunActionPerformed(java.awt.event.ActionEvent evt) {
        getAreaPanel().getArea().getOutput().startAction(module);
    }

    private void mbHelpActionPerformed(java.awt.event.ActionEvent evt) {
        VisNow.get().showHelp(ModuleCore.getHelpTopicID(getModule().getCore().getCoreName().getClassName()));
    }

    //</editor-fold>
    public void setProgress(float f) {
        this.progress.setValue((int) (f * 1024));
        if (f >= 1) {
            progress.setValue(0);
            progress.setBackground(VNSwingUtils.STATE_PASSIVE);
        }
    }

    public void updateOutputs() {
        Vector<Output> tabb = getModule().getOutputs().getSortedOutputs();

        int left = PORTMARGIN + GLOWBORDER;
        outputPanels.clear();

        for (Output output : tabb) {

            //for(Output output: getModuleBoxEgg().getOutputs().values()) {
            PortPanel port = new PortPanel(this, output);

            outputPanels.put(port.getPort().getName(), port);

            //tabb[i].addPortVisibilityChangeListener(pvcListener);

            if (!output.isVisible()) {
                continue;
            }

            port.setBounds(
                    left,
                    GLOWBORDER + INPUTHEIGHT + CENTERHEIGHT,
                    PORTWIDTH,
                    PORTHEIGHT);
            left += PORTSPACING;

            layeredPane.add(port, portI);

        }

    }

//    public boolean isHit(Point p) {
//        int x0 = this.getLocation().x;
//        int y0 = this.getLocation().y;
//        int x1 = x0 + this.getSize().width;
//        int y1 = y0 + this.getSize().height;
//        x0 += GLOWBORDER;
//        y0 += GLOWBORDER;
//        x1 -= GLOWBORDER;
//        y1 -= GLOWBORDER;
//        if(p.x < x0) return false;
//        if(p.x > x1) return false;
//        if(p.y < y0) return false;
//        if(p.y > y1) return false;
//        return true;
//    }
    public Iterator<PortPanel> getPortPanelsIterator() {
        return new PortPanelIterator(
                inputPanels.values().iterator(),
                outputPanels.values().iterator());
    }

    public void updateName() {
        this.nameLabel.setText(module.getName());
    }
}

class PortPanelIterator implements Iterator<PortPanel> {

    private Iterator<PortPanel> inputIterator;
    private Iterator<PortPanel> outputIterator;

    PortPanelIterator(Iterator<PortPanel> in, Iterator<PortPanel> out) {
        inputIterator = in;
        outputIterator = out;
    }

    public boolean hasNext() {
        return inputIterator.hasNext() || outputIterator.hasNext();
    }

    public PortPanel next() {
        if (inputIterator.hasNext()) {
            return inputIterator.next();
        }
        return outputIterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException("Operation not supported.");
    }
}
