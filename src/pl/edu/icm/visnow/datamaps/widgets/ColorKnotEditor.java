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

package pl.edu.icm.visnow.datamaps.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import pl.edu.icm.visnow.datamaps.colormap1d.ColorMap1D;
import pl.edu.icm.visnow.datamaps.colormap1d.RGBChannelColorMap1D;
import pl.edu.icm.visnow.datamaps.colormap1d.RGBChannelColorMap1D.ColorKnot;
import pl.edu.icm.visnow.datamaps.utils.Orientation;

/**
 * @author  Michał Łyczek (lyczek@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ColorKnotEditor extends javax.swing.JPanel {

    final int PICKER_WIDTH = 17;
    protected int padding = 0,
            pickerHeight = 31,
            colorStripHeight = 18,
            padding2 = 0;
    private ColorKnot ghostKnot;
    private BufferedImage fontImage;
    private BufferedImage knotImageN, knotImageS, knotImageD;
    private BufferedImage midpointImageN, midpointImageS, midpointImageD;
    protected ColorKnot selectedKnot;
    protected RGBChannelColorMap1D colorMap;
    protected Orientation orientation = Orientation.VERTICAL;
    private boolean showRuler = false, showColorKnots = false;
    protected final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            repaint();
        }
    };

    public boolean isHorizontal() {
        return orientation == Orientation.HORIZONTAL;
    }

    public boolean isShowColorKnot() {
        return showColorKnots;
    }

    public void setShowColorKnot(boolean showColorKnot) {
        this.showColorKnots = showColorKnot;
    }

    public int getPadding2() {
        return padding2;
    }

    public void setPadding2(int padding2) {
        this.padding2 = padding2;
    }

    public boolean isShowRuler() {
        return showRuler;
    }

    public void setShowRuler(boolean showRuler) {
        this.showRuler = showRuler;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        colorMapPreview1.setOrientation(orientation);
        if (isHorizontal()) {
//            colorMapPreview1.setPreferredSize(new Dimension(getWidth(), 21));
//            colorMapPreview1.setSize(new Dimension(getWidth(), 21));
            colorMapPreview1.setBounds(0, padding2, getWidth(), 21);
            repaint();
        } else {
            colorMapPreview1.setPreferredSize(new Dimension(21, getHeight()));
            colorMapPreview1.setSize(new Dimension(21, getHeight()));
        }

    }

    private Point2D toModelSpace(Point p) {
        Insets insets = getInsets();
        Dimension dimension = new Dimension(getWidth() - insets.left - insets.right, getHeight() - insets.top - insets.bottom);
        if (isHorizontal()) {
            return new Point2D.Float((float) (p.x - insets.left) / dimension.width, 1 - (float) (p.y - insets.top) / dimension.height);
        } else {
            return new Point2D.Float((float) (p.y - insets.top) / dimension.height, (float) (p.x - insets.left) / dimension.width);
        }
    }

    /** Creates new form ColorKnotEditor */
    public ColorKnotEditor() {
        try {
            ClassLoader cl = getClass().getClassLoader();
            fontImage = ImageIO.read(cl.getResource("pl/edu/icm/visnow/datamaps/widgets/resources/font.png"));
            knotImageN = ImageIO.read(cl.getResource("pl/edu/icm/visnow/datamaps/widgets/resources/knot1.png"));
            knotImageS = ImageIO.read(cl.getResource("pl/edu/icm/visnow/datamaps/widgets/resources/knot2.png"));
            knotImageD = ImageIO.read(cl.getResource("pl/edu/icm/visnow/datamaps/widgets/resources/knot3.png"));
            midpointImageN = ImageIO.read(cl.getResource("pl/edu/icm/visnow/datamaps/widgets/resources/midpoint1.png"));
            midpointImageS = ImageIO.read(cl.getResource("pl/edu/icm/visnow/datamaps/widgets/resources/midpoint2.png"));
            midpointImageD = ImageIO.read(cl.getResource("pl/edu/icm/visnow/datamaps/widgets/resources/midpoint3.png"));
        } catch (IOException ex) {
            Logger.getLogger(ColorKnotEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        initComponents();

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (colorMap != null && !colorMap.isBuildin()) {
                    if (e.getClickCount() >= 2) {
                        colorMap.addColorKnot(new RGBChannelColorMap1D.ColorKnot((float) toModelSpace(e.getPoint()).getX(), colorMap.getColor((float) toModelSpace(e.getPoint()).getX())));
                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        ColorKnot colorKnot = colorMap.getClosestColorKnot((float) toModelSpace(e.getPoint()).getX());

                        Color newColor = JColorChooser.showDialog(null, "Choose color", new Color(colorKnot.getColor()));
                        if (newColor != null) {
                            colorMap.setColor(colorKnot, newColor.getRGB());
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (colorMap != null && !colorMap.isBuildin()) {
                    selectedKnot = colorMap.getClosestColorKnot((float) toModelSpace(e.getPoint()).getX());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!ColorKnotEditor.this.contains(e.getPoint()) && !colorMap.isBuildin()) {
                    colorMap.removeColorKnot(selectedKnot);
                }
                selectedKnot = null;
            }
        });

        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (colorMap != null && !colorMap.isBuildin()) {
                    colorMap.moveColorKnot(selectedKnot, (float) toModelSpace(e.getPoint()).getX());
                }
            }
        });
    }

    public RGBChannelColorMap1D getColorMap() {
        return colorMap;
    }

    public void setColorMap(ColorMap1D colorMap) {
        if (colorMap instanceof RGBChannelColorMap1D) {
            if (this.colorMap != null) {
                this.colorMap.removePropertyChangeListener(propertyChangeListener);
            }
            this.colorMap = (RGBChannelColorMap1D) colorMap;
            this.colorMap.addPropertyChangeListener(propertyChangeListener);
            colorMapPreview1.setColorMap(colorMap);
            repaint();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        colorMapPreview1 = new pl.edu.icm.visnow.datamaps.widgets.ColorMapPreview();

        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        colorMapPreview1.setMinimumSize(new java.awt.Dimension(200, 21));

        javax.swing.GroupLayout colorMapPreview1Layout = new javax.swing.GroupLayout(colorMapPreview1);
        colorMapPreview1.setLayout(colorMapPreview1Layout);
        colorMapPreview1Layout.setHorizontalGroup(
            colorMapPreview1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 396, Short.MAX_VALUE)
        );
        colorMapPreview1Layout.setVerticalGroup(
            colorMapPreview1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 17, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(31, 0, 4, 0);
        add(colorMapPreview1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
    }//GEN-LAST:event_formKeyReleased

    private int getInt(float v) {
        return (int) (colorMapPreview1.getWidth() * v);
    }

    private Rectangle getColorStripRect() {
        return new Rectangle(0, 0, getWidth(), getHeight());
    }

    private void paintRuler(Graphics g) {
        Rectangle r = getColorStripRect();

        g.setColor(new Color(214, 214, 214));
        for (float f = 0; f <= 1; f += 0.025f) {
            g.drawLine(getInt(f), r.y - 6, getInt(f), r.y - 10);
        }
        for (float f = 0; f <= 1; f += 0.10f) {
            g.drawLine(getInt(f), r.y - 6, getInt(f), r.y - 14);
        }

    }

    private static Color getLighterColor(Color c) {
        float[] comps = new float[]{
            c.getColorComponents(null)[0] + 0.2f,
            c.getColorComponents(null)[1] + 0.2f,
            c.getColorComponents(null)[2] + 0.2f
        };
        for (int i = 0; i < 3; i++) {
            if (comps[i] > 1.f) {
                comps[i] = 1.f;
            }
        }
        return new Color(comps[0], comps[1], comps[2]);
    }

    private static Color getDarkerColor(Color c) {
        float[] comps = new float[]{
            c.getColorComponents(null)[0] - 0.2f,
            c.getColorComponents(null)[1] - 0.2f,
            c.getColorComponents(null)[2] - 0.2f
        };
        for (int i = 0; i < 3; i++) {
            if (comps[i] < 0.f) {
                comps[i] = 0.f;
            }
        }
        return new Color(comps[0], comps[1], comps[2]);
    }

    public int getPadding() {
        return padding;
    }

    private void paintColorKnot(Graphics g, ColorKnot k, boolean isGhost) {
        Rectangle r = getColorStripRect();
        int x = (int) (k.getPosition() * r.width);
        BufferedImage img = null;
        if (isGhost) {
            img = knotImageD;
        } else {
            if (k == selectedKnot) {
                img = knotImageS;
            } else {
                img = knotImageN;
            }
        }

        int px = r.x + x - PICKER_WIDTH / 2;
        int py = getPadding() - 2;
        g.drawImage(img, px, py, this);

        if (isGhost) {
            Color c = new Color(195, 195, 195);
            // color pentagon
            int[] xPts = new int[]{px + 4, px + 13, px + 13, px + 8, px + 4};
            int[] yPts = new int[]{py + 12, py + 12, py + 22, py + 27, py + 22};
            g.setColor(getBackground());
            g.fillPolygon(xPts, yPts, 5);

            // light+shadow on pentagon
            g.setColor(c);
            g.drawLine(px + 4, py + 12, px + 11, py + 12);
            g.drawLine(px + 4, py + 12, px + 4, py + 22);
            g.drawLine(px + 4, py + 22, px + 7, py + 25);
            g.setColor(c);
            g.drawLine(px + 12, py + 12, px + 12, py + 22);
            g.drawLine(px + 12, py + 22, px + 8, py + 26);

        } else {
            // color pentagon
            int[] xPts = new int[]{px + 4, px + 13, px + 13, px + 8, px + 4};
            int[] yPts = new int[]{py + 12, py + 12, py + 22, py + 27, py + 22};
            Color knotColor = new Color(k.getColor());
            g.setColor(knotColor);
            g.fillPolygon(xPts, yPts, 5);

            // light+shadow on pentagon
            g.setColor(getLighterColor(knotColor));
            g.drawLine(px + 4, py + 12, px + 11, py + 12);
            g.drawLine(px + 4, py + 12, px + 4, py + 22);
            g.drawLine(px + 4, py + 22, px + 7, py + 25);
            g.setColor(getDarkerColor(knotColor));
            g.drawLine(px + 12, py + 12, px + 12, py + 22);
            g.drawLine(px + 12, py + 22, px + 8, py + 26);
        }
        paintColorKnotLabel(g, k, isGhost);
    }

    private BufferedImage getDigitImage(int i, boolean isGhost) {
        int fix = (isGhost) ? 6 : 0;
        switch (i) {
            case 0:
                return fontImage.getSubimage(0, fix, 5, 7);
            case 1:
                return fontImage.getSubimage(45, fix, 4, 7);
            case 100:
                return fontImage.getSubimage(48, fix, 11, 7);
            default:
                return fontImage.getSubimage(5 + 5 * (i - 2), fix, 5, 7);
        }
    }

    private void paintColorKnotLabel(Graphics g, ColorKnot k, boolean isGhost) {
        Rectangle r = getColorStripRect();
        int i = (int) (k.getPosition() * 100);
        if (i < 0 || i > 100) {
            return;
        }
        int x = (int) (k.getPosition() * r.width);
        int px = r.x + x - PICKER_WIDTH / 2;
        int py = getPadding() - 2;

        if (i == 100) {
            g.drawImage(getDigitImage(i, isGhost), px + 3, py + 5, null);
        } else if (i > 9) {
            BufferedImage d1 = getDigitImage(i / 10, isGhost);
            g.drawImage(d1, px + 3, py + 5, null);
            g.drawImage(getDigitImage(i % 10, isGhost), px + 3 + d1.getWidth(), py + 5, null);
        } else {
            g.drawImage(getDigitImage(i, isGhost), px + 5, py + 5, null);
        }
    }

    private void paintColorKnots(Graphics g) {
        for (ColorKnot k : colorMap.getColorKnots()) {
            if (k != selectedKnot) {
                paintColorKnot(g, k, false);
            }
        }
        if (selectedKnot != null && colorMap.getKnotIndex(selectedKnot) != -1) {
            paintColorKnot(g, selectedKnot, false);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (colorMap != null) {

            if (showRuler) {
                paintRuler(g);
            }
            if (showColorKnots) {
                paintColorKnots(g);
            }


        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.edu.icm.visnow.datamaps.widgets.ColorMapPreview colorMapPreview1;
    // End of variables declaration//GEN-END:variables
}
