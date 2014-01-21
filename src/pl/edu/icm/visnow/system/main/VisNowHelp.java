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

package pl.edu.icm.visnow.system.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.application.area.Area;
import pl.edu.icm.visnow.application.area.widgets.ModulePanel;
import pl.edu.icm.visnow.engine.core.CoreName;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.exception.VNException;
import pl.edu.icm.visnow.engine.main.ModuleBox;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class VisNowHelp {

    public static void main(String[] args) {
        //String moduleClass = "pl.edu.icm.visnow.lib.basic.mappers.Isosurface.Isosurface";
        //String moduleName = "isosurface [0]";

        String moduleClass = "pl.edu.icm.visnow.lib.basic.viewers.Viewer3D.Viewer3D";
        String moduleName = "viewer 3d [0]";        

        BufferedImage img = null;
        HashMap<String, Point> inputHandles = new HashMap<String, Point>();
        HashMap<String, Point> outputHandles = new HashMap<String, Point>();

        img = renderModuleBox(moduleName, moduleClass, inputHandles, outputHandles);

        showImage(img, inputHandles, outputHandles);


    }

    private static void showImage(final BufferedImage img, final HashMap<String, Point> inputHandles, final HashMap<String, Point> outputHandles) {
        JFrame frame = new JFrame("image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0, 0, 640, 480);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel() {

            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                if(img == null)
                    return;
                
                int xpos = (getWidth() - img.getWidth()) / 2;
                int ypos = (getHeight() - img.getHeight()) / 2;
                g2d.drawImage(img, null, xpos, ypos);

                String[] inputPorts = inputHandles.keySet().toArray(new String[0]);
                for (int i = 0; i < inputHandles.size(); i++) {
                    Point p = (Point) inputHandles.get(inputPorts[i]);
                    g2d.setPaint(Color.MAGENTA);
                    //g2d.fillOval((int) (xpos + p.getX() - 2), (int) (ypos + p.getY() - 2), 4, 4);                    
                    g2d.fillRect((int) (xpos + p.getX()), (int) (ypos + p.getY()), 1, 1);
                }

                String[] outputPorts = outputHandles.keySet().toArray(new String[0]);
                for (int i = 0; i < outputHandles.size(); i++) {
                    Point p = (Point) outputHandles.get(outputPorts[i]);
                    g2d.setPaint(Color.MAGENTA);
                    //g2d.fillOval((int) (xpos + p.getX() - 2), (int) (ypos + p.getY() - 2), 4, 4);
                    g2d.fillRect((int) (xpos + p.getX()), (int) (ypos + p.getY()), 1, 1);
                }
            }
        };
        panel.setPreferredSize(new Dimension(640, 480));
        
        
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }
    
    public static BufferedImage renderModuleBox(String moduleName, String moduleClass, HashMap<String, Point> inputHandles, HashMap<String, Point> outputHandles) {
        if (moduleClass == null) {
            return null;
        }

        String[] args = new String[0];
        VisNow.mainBlocking(args, false);
        VisNow vn = VisNow.get();

        BufferedImage img = null;
        Application app = vn.getMainWindow().getApplicationsPanel().getCurrentApplication();
        Area ai = app.getArea().getInput().getArea();
        ModuleCore core = null;
        ModulePanel mp = null;
        String[] inputPorts;
        String[] outputPorts;
        
        try {
            core = app.getLibraries().generateCore(new CoreName("internal", moduleClass));
        
            inputPorts = core.getInputs().getInputs().keySet().toArray(new String[0]);
            outputPorts = core.getOutputs().getOutputs().keySet().toArray(new String[0]);
            
            
        } catch (VNException ex) {
            System.err.println("ERROR: cannot create module "+moduleClass);
            return null;
        }
        
        ModuleBox mb = new ModuleBox(app.getEngine(), moduleName, core);
        mp = new ModulePanel(ai.getPanel(), mb);
        
        int w = mp.getWidth();
        int h = mp.getHeight();
        
        mp.setPreferredSize(new Dimension(w, h));        
        JPanel tmpPanel = new JPanel(new BorderLayout());
        tmpPanel.setPreferredSize(mp.getPreferredSize());
        tmpPanel.add(mp,BorderLayout.CENTER);               
        
        JFrame tmpFrame = new JFrame("tmp");
        tmpFrame.setLayout(new BorderLayout());        
        tmpFrame.add(tmpPanel,BorderLayout.CENTER);
        tmpFrame.setBounds(0, 0, 480, 320);
        tmpFrame.setVisible(true);
        
        img = new BufferedImage(w-12, h-12, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.translate(-6, -6);
        g2d.setPaint(Color.BLACK);
        g2d.fillRect(0,0,w-12,h-12);
        mp.paintAll(g2d);
        img.flush();
      
        tmpFrame.dispose();
        
        if (inputPorts != null) {
            for (int i = 0; i < inputPorts.length; i++) {
                inputHandles.put(inputPorts[i], new Point(16 + 24 * i, 0));
            }
        }

        if (outputPorts != null) {
            for (int i = 0; i < outputPorts.length; i++) {
                outputHandles.put(outputPorts[i], new Point(16 + 24 * i, h-13));
            }
        }

        return img;
    }

   private VisNowHelp()
   {
   }
}
