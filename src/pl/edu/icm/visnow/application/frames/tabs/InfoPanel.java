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

package pl.edu.icm.visnow.application.frames.tabs;

import java.awt.Color;
import javax.swing.JLabel;
import pl.edu.icm.visnow.application.frames.portPanels.InfoInputPanel;
import pl.edu.icm.visnow.application.frames.portPanels.InfoOutputPanel;
import pl.edu.icm.visnow.application.frames.portPanels.InfoParameterPanel;
import pl.edu.icm.visnow.engine.core.Input;
import pl.edu.icm.visnow.engine.main.ModuleBox;
import pl.edu.icm.visnow.engine.core.Output;
import pl.edu.icm.visnow.engine.core.Parameter;
import pl.edu.icm.visnow.system.swing.BigPanel;
import pl.edu.icm.visnow.system.swing.JExpandableList;
import pl.edu.icm.visnow.system.swing.SmallPanel;
import pl.edu.icm.visnow.system.swing.VNSwingUtils;


/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class InfoPanel extends JExpandableList {

    private final static Color ONE = new Color(165,165,165);//new Color(255,255,255);
    private final static Color TWO = new Color(205,205,205);//new Color(165,165,165);
    private final static Color THREE = new Color(255,255,255);//new Color(205,205,205);

    private ModuleBox module;

    JExpandableList inPanels;
    JExpandableList outPanels;
    JExpandableList paramPanels;


    public InfoPanel(ModuleBox module) {
        super(ONE);
        this.module = module;
        this.setBackground(VNSwingUtils.randomBrown());

        this.inPanels = new JExpandableList(TWO);
        this.outPanels = new JExpandableList(TWO);
        this.paramPanels = new JExpandableList(TWO);

        inPanels.setParentList(this);
        outPanels.setParentList(this);
        paramPanels.setParentList(this);

        SmallPanel inputSmall = new SmallPanel(ONE, TWO,"inputs");
        BigPanel inputBig = new BigPanel(ONE, TWO, "inputs");
        VNSwingUtils.setFillerComponent(inputBig.getPanel(), inPanels);

        SmallPanel outputSmall = new SmallPanel(ONE, TWO,"outputs");
        BigPanel outputBig = new BigPanel(ONE, TWO,"outputs");
        VNSwingUtils.setFillerComponent(outputBig.getPanel(), outPanels);

        SmallPanel paramSmall = new SmallPanel(ONE, TWO,"params");
        BigPanel paramBig = new BigPanel(ONE, TWO,"params");
        VNSwingUtils.setFillerComponent(paramBig.getPanel(), paramPanels);

        this.addPair(inputSmall, inputSmall.getButton(), inputBig, inputBig.getButton());
        this.addPair(outputSmall, outputSmall.getButton(), outputBig, outputBig.getButton());
        this.addPair(paramSmall, paramSmall.getButton(), paramBig, paramBig.getButton());


        if(module.getInputs().getInputs().isEmpty()) {
            inPanels.addEl(new JLabel("no inputs"));
        } else
        for(Input input: module.getInputs()) {
            SmallPanel sm = new SmallPanel(TWO, THREE, input.getName());
            InfoInputPanel bg = new InfoInputPanel(TWO, THREE, input);
            inPanels.addPair(sm, sm.getButton(), bg, bg.getButton());
        }

        if(module.getOutputs().getOutputs().isEmpty()) {
            outPanels.addEl(new JLabel("no outputs"));
        } else
        for(Output output: module.getOutputs()) {
            SmallPanel sm = new SmallPanel(TWO, THREE, output.getName());
            InfoOutputPanel bg = new InfoOutputPanel(TWO, THREE, output);
            outPanels.addPair(sm, sm.getButton(), bg, bg.getButton());
        }

        if(module.getParameters().getParameters().isEmpty()) {
            paramPanels.addEl(new JLabel("no parameters"));
        } else
        for(Parameter parameter: module.getParameters()) {
            SmallPanel sm = new SmallPanel(TWO, THREE, parameter.getName());
            InfoParameterPanel bg = new InfoParameterPanel(TWO, THREE, parameter);
            paramPanels.addPair(sm, sm.getButton(), bg, bg.getButton());
        }



    }
}
