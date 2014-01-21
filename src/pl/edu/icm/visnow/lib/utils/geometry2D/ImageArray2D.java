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

package pl.edu.icm.visnow.lib.utils.geometry2D;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.BitArray;
import pl.edu.icm.visnow.datasets.dataarrays.ComplexDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.dataarrays.LogicDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.StringDataArray;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class ImageArray2D extends Array2D {

    private BufferedImage img = null;

    public ImageArray2D() {
        this("image 2D");
    }

    public ImageArray2D(String name) {
        this(null, name);
    }

    public ImageArray2D(RegularField field) {
        this(field, "image 2D");
    }

    public ImageArray2D(RegularField field, String name) {
        super(field, name);
        updateImage();
    }

    @Override
    public void drawLocal2D(Graphics2D g, AffineTransform tr) {
        try {
            AffineTransform ltr = (AffineTransform) tr.clone();
            g.transform(ltr);
            //--------------draw here----------------------
            if (img != null) {
                if(g.getComposite() != null && g.getComposite() instanceof AlphaComposite && renderingParams.getTransparency() > 0) {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
                            ((AlphaComposite) g.getComposite()).getAlpha() * (1.0f - renderingParams.getTransparency())));                    
                } else if (renderingParams.getTransparency() > 0) {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
                            1.0f - renderingParams.getTransparency()));
                }
                g.drawImage(img, 0, 0, null);
            }
            //---------------------------------------------            
            ltr.invert();
            g.transform(ltr);
        } catch (NoninvertibleTransformException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setField(RegularField field) {
        super.setField(field);
        updateImage();
    }
    private boolean grayscale = false;

    private void updateImage() {
        if(colors == null)
            return;
        
        //check colors for grayscale
        grayscale = true;
        for (int i = 0; i < width * height; i++) {
            if (colors[4 * i + 0] != colors[4 * i + 1] || colors[4 * i + 0] != colors[4 * i + 2]) {
                grayscale = false;
                break;
            }
        }

        if (field != null) {
            if (grayscale) {
                img = new BufferedImage(field.getDims()[0], field.getDims()[1], BufferedImage.TYPE_BYTE_GRAY);
            } else {
                img = new BufferedImage(field.getDims()[0], field.getDims()[1], BufferedImage.TYPE_INT_ARGB);
            }
            updateColors();
        } else {
            img = null;
        }
    }

    @Override
    public void setColors(byte[] colors) {
        if (colors != null && colors.length == 4 * width * height) {
            this.colors = colors;
            updateImage();
        }
    }

    private void updateColors() {
        if (img == null || colors == null) {
            return;
        }

        WritableRaster raster = img.getRaster();
        int w = img.getWidth();
        int h = img.getHeight();
        if (colors.length != 4 * w * h) {
            return;
        }
        
        if(grayscale) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    raster.setSample(i, h - j - 1, 0, colors[(j * w + i)*4] & 0xff);
                }
            }
        } else {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    for (int b = 0; b < 4; b++) {
                        raster.setSample(i, h - j - 1, b, colors[(j * w + i)*4 + b] & 0xff);
                    }
                }
            }
        }
        fireStateChanged();
    }

    @Override
    public String getLocalInfoAt(float x, float y) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height || img == null) {
            return "";
        }

        int[] pixel = null;
        pixel = img.getRaster().getPixel((int) Math.floor(x), height - 1 - (int) Math.floor(y), pixel);

        switch (img.getType()) {
            case BufferedImage.TYPE_BYTE_GRAY:
                return "V = " + pixel[0];
            case BufferedImage.TYPE_INT_RGB:
            case BufferedImage.TYPE_INT_ARGB:
                return "R:G:B = " + pixel[0] + ":" + pixel[1] + ":" + pixel[2];
            default:
                return "";
        }
    }

    @Override
    public String getDetailedLocalInfoAt(float x, float y) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height || img == null) {
            return "";
        }

        int[] pixel = null;
        int u = (int) Math.floor(x);
        int v = (int) Math.floor(y);
        pixel = img.getRaster().getPixel(u, height - 1 - v, pixel);

        StringBuilder s = new StringBuilder();
        s.append("<html><body>");
        s.append("<p>");
        s.append("Color value at ["+u+","+v+"]:<br>");
        switch (img.getType()) {
            case BufferedImage.TYPE_BYTE_GRAY:
                s.append(" V: " + pixel[0] + "<br>");
                break;
            case BufferedImage.TYPE_INT_RGB:
                s.append(" R: " + pixel[0] + "<br>");
                s.append(" G: " + pixel[1] + "<br>");
                s.append(" B: " + pixel[2] + "<br>");
                break;
            case BufferedImage.TYPE_INT_ARGB:
                s.append(" R: " + pixel[0] + "<br>");
                s.append(" G: " + pixel[1] + "<br>");
                s.append(" B: " + pixel[2] + "<br>");
                s.append(" A: " + pixel[3] + "<br>");
                break;
        }
        s.append("</p>");
        
        s.append("<p><br>");
        s.append("Field component values at ["+u+","+v+"]:");
        s.append("<table>");
        for (int i = 0; i < field.getNData(); i++) {
            DataArray da = field.getData(i);
            int veclen = da.getVeclen();
            s.append("<tr>");
            s.append("<td>");
            s.append(" "+da.getName()+": ");
            s.append("</td><td>");
            switch(da.getType()) {
                case DataArray.FIELD_DATA_BYTE:
                    byte[] bData = da.getBData();
                    if(veclen == 1) {
                        s.append(""+(bData[v*width*veclen + u*veclen]&0xFF));
                    } else {
                        s.append("["+(bData[v*width*veclen + u*veclen]&0xFF));
                        for (int l = 1; l < veclen; l++) {
                            s.append(", "+(bData[v*width*veclen + u*veclen + l]&0xFF));   
                        }                        
                        s.append("]");
                    }
                    break;
                case DataArray.FIELD_DATA_SHORT:
                    short[] sData = da.getSData();
                    if(veclen == 1) {
                        s.append(""+sData[v*width*veclen + u*veclen]);
                    } else {
                        s.append("["+sData[v*width*veclen + u*veclen]);
                        for (int l = 1; l < veclen; l++) {
                            s.append(", "+sData[v*width*veclen + u*veclen + l]);   
                        }                        
                        s.append("]");
                    }
                    break;
                case DataArray.FIELD_DATA_INT:
                    int[] iData = da.getIData();
                    if(veclen == 1) {
                        s.append(""+iData[v*width*veclen + u*veclen]);
                    } else {
                        s.append("["+iData[v*width*veclen + u*veclen]);
                        for (int l = 1; l < veclen; l++) {
                            s.append(", "+iData[v*width*veclen + u*veclen + l]);   
                        }                        
                        s.append("]");
                    }
                    break;
                case DataArray.FIELD_DATA_COMPLEX:
                    float[] fReData = ((ComplexDataArray)da).getFRealData();
                    float[] fImData = ((ComplexDataArray)da).getFImagData();
                    int idx = v*width*veclen + u*veclen;
                    if(veclen == 1) {
                        s.append(""+fReData[idx]+" + i*"+fImData[idx]);
                    } else {
                        s.append("["+fReData[idx]+" + i*"+fImData[idx]);
                        for (int l = 1; l < veclen; l++) {
                            s.append(", "+fReData[idx + l]+" + i*"+fImData[idx + l]);   
                        }
                        s.append("]");
                    }
                    break;
                case DataArray.FIELD_DATA_STRING:
                    String[] strData = ((StringDataArray)da).getStringData();
                    if(veclen == 1) {
                        s.append(""+strData[v*width*veclen + u*veclen]);
                    } else {
                        s.append("["+strData[v*width*veclen + u*veclen]);
                        for (int l = 1; l < veclen; l++) {
                            s.append(", "+strData[v*width*veclen + u*veclen + l]);   
                        }                        
                        s.append("]");
                    }
                    break;
                case DataArray.FIELD_DATA_LOGIC:
                    BitArray bitData = ((LogicDataArray)da).getBitArray();
                    if(veclen == 1) {
                        s.append(""+bitData.getValueAtIndex(v*width*veclen + u*veclen));
                    } else {
                        s.append("["+bitData.getValueAtIndex(v*width*veclen + u*veclen));
                        for (int l = 1; l < veclen; l++) {
                            s.append(", "+bitData.getValueAtIndex(v*width*veclen + u*veclen + l));   
                        }                        
                        s.append("]");
                    }
                    break;
                default:
                    float[] fData = da.getFData();
                    if(veclen == 1) {
                        s.append(""+fData[v*width*veclen + u*veclen]);
                    } else {
                        s.append("["+fData[v*width*veclen + u*veclen]);
                        for (int l = 1; l < veclen; l++) {
                            s.append(", "+fData[v*width*veclen + u*veclen + l]);   
                        }
                        s.append("]");
                    }
                    break;
            }
            s.append("</td>");
            s.append("</tr>");            
        }
        s.append("</table>");
        s.append("</p>");

        s.append("</body></html>");
        return s.toString();
    }
}
