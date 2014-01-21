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
package pl.edu.icm.visnow.lib.basic.readers.ReadImage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.engine.core.ParameterChangeListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.RegularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.ImageUtilities;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.io.InputSource;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.utils.usermessage.Level;
import pl.edu.icm.visnow.system.utils.usermessage.UserMessage;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class ReadImage extends RegularOutFieldVisualizationModule {

    private GUI computeUI = null;
    private Params params;
    private BufferedImage img = null;
    private String imageName = "image";
    public static OutputEgg[] outputEggs = null;
    private static final Logger log = Logger.getLogger(ReadImage.class);

    /**
     * Creates a new instance of ReadImage
     */
    public ReadImage() {
        parameters = params = new Params();
        params.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                startAction();
            }
        });

        params.addParameterChangelistener(new ParameterChangeListener() {
            @Override
            public void parameterChanged(String name) {
                if (name.equals("show")) {
                    if (!params.isShow() && outField != null) {
                        outObj.clearAllGeometry();
                        return;
                    }
                    if (!prepareOutputGeometry()) {
                        return;
                    }
                    show();
                }
            }
        });

        SwingInstancer.swingRunAndWait(new Runnable() {
            @Override
            public void run() {
                computeUI = new GUI();
                computeUI.setParams(params);
                ui.addComputeGUI(computeUI);
                setPanel(ui);
            }
        });
    }

    @Override
    public void onActive() {
        if (params.getFiles() == null || params.getFiles().length < 1) {
            computeUI.setInfo("", Color.BLACK);
            computeUI.setImageDescription("");
            computeUI.imagesListClear();
            setOutputValue("outRegularField", null);
            return;
        }

        if (params.getFiles().length == 1 && !params.isSequenceMode()) {
            switch (params.getSource()) {
                case InputSource.FILE:
                case InputSource.GRID:
                    readSingleImage(params.getFiles()[0]);
                    break;
                case InputSource.URL:
                    readSingleImageFromURL(params.getFiles()[0]);
            }
            
            
            if (!params.isGrayscale() && img != null && img.getType() != BufferedImage.TYPE_BYTE_GRAY && img.getType() != BufferedImage.TYPE_USHORT_GRAY) {
                //check if image bands are equal
                WritableRaster raster = img.getRaster();
                int nBands = raster.getNumBands();
                if(nBands > 1) {
                    boolean equalBands = true;
                    int[] pixel = new int[nBands];
                    int width = img.getWidth();
                    int height = img.getHeight();
                    for (int j = 0; j < height; j++) {
                        for (int i = 0; i < width; i++) {
                            pixel = raster.getPixel(i, j, pixel);
                            for (int k = 0; k < pixel.length; k++) {
                                if(pixel[k] != pixel[0]) {
                                    equalBands = false;
                                    break;
                                }                                
                            }                            
                            if(!equalBands) {
                                break;
                            }
                        }                    
                        if(!equalBands) {
                            break;
                        }
                    }
                    
                    if(equalBands) {
                        BufferedImage imgGray = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
                        switch (img.getType()) {
                            case BufferedImage.TYPE_INT_RGB:
                            case BufferedImage.TYPE_INT_ARGB:
                                WritableRaster rasterRGB = img.getRaster();
                                WritableRaster rasterGray = imgGray.getRaster();
                                for (int y = 0; y < height; y++) {
                                    for (int x = 0; x < width; x++) {
                                        rasterGray.setSample(x, y, 0, rasterRGB.getSample(x, y, 0));
                                    }
                                }
                                break;
                            default:
                                imgGray.getGraphics().drawImage(img, 0, 0, null);
                                break;
                        }
                        img = imgGray;                        
                    }
                }
            } else if (params.isGrayscale() && img != null && img.getType() != BufferedImage.TYPE_BYTE_GRAY && img.getType() != BufferedImage.TYPE_USHORT_GRAY) {
                int w = img.getWidth();
                int h = img.getHeight();
                BufferedImage imgGray = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                switch (img.getType()) {
                    case BufferedImage.TYPE_INT_RGB:
                    case BufferedImage.TYPE_INT_ARGB:
                        WritableRaster rasterRGB = img.getRaster();
                        WritableRaster rasterGray = imgGray.getRaster();
                        float[] wght = params.getRGBWeights();
                        for (int y = 0; y < h; y++) {
                            for (int x = 0; x < w; x++) {
                                rasterGray.setSample(x, y, 0,
                                        wght[0] * rasterRGB.getSampleFloat(x, y, 0)
                                        + wght[1] * rasterRGB.getSampleFloat(x, y, 1)
                                        + wght[2] * rasterRGB.getSampleFloat(x, y, 2));

                            }
                        }
                        break;
                    default:
                        log.warn("WARNING: Image type " + img.getType() + " does not support weighted grayscale conversion");
                        imgGray.getGraphics().drawImage(img, 0, 0, null);
                        break;
                }
                img = imgGray;
            }
            outField = ImageUtilities.bufferedImage2RegularField(img, true);
            setOutputValue("outRegularField", new VNRegularField(outField));
            if (outField != null) {
                computeUI.setImageDescription(outField.toMultilineString());
            }
            //send user message
            if (img != null)
                VisNow.get().userMessageSend(this, "Image successfully loaded", outField != null ? outField.toMultilineString() : "", Level.INFO);
            else
                VisNow.get().userMessageSend(this, "Error loading image", "", Level.ERROR);

            outObj2DStruct.setName(imageName);
            computeUI.setImage(img);
        } else {
            outField = readMultipleImages(params.getFiles(), computeUI);
            setOutputValue("outRegularField", new VNRegularField(outField));
        }

        if (!params.isShow()) {
            return;
        }
        if (!prepareOutputGeometry()) {
            return;
        }
        show();

    }

    private void readSingleImage(String path) {
        img = ImageUtilities.loadImage(path);
        if (img != null) {
            computeUI.setInfo("Image successfully loaded", Color.BLACK);
        } else {
            computeUI.setInfo("Error loading image", Color.RED);
        }

        int last = path.lastIndexOf('.');
        if (last < 1 || last >= path.length()) {
            last = path.length();
        }

        if (path.contains("/")) {
            imageName = path.substring(path.lastIndexOf('/') + 1, last);
        } else if (path.contains("\\")) {
            imageName = path.substring(path.lastIndexOf("\\") + 1, last);
        } else {
            imageName = path.substring(0, last);
        }
    }

    private void readSingleImageFromURL(String path) {
        try {
            URL url = new URL(path);
            img = ImageUtilities.loadImage(url);
        } catch (IOException e) {
            computeUI.setInfo("Error loading image", Color.RED);
        }

        if (img != null) {
            computeUI.setInfo("Image successfully loaded", Color.BLACK);
        } else {
            computeUI.setInfo("Error loading image", Color.RED);
        }

        int last = path.lastIndexOf('.');
        if (last < 1 || last >= path.length()) {
            last = path.length();
        }

        if (path.contains("/")) {
            imageName = path.substring(path.lastIndexOf('/') + 1, last);
        } else if (path.contains("\\")) {
            imageName = path.substring(path.lastIndexOf("\\") + 1, last);
        } else {
            imageName = path.substring(0, last);
        }
    }

    private RegularField readMultipleImages(String[] files, GUI ui) {
        if (files == null || files.length < 1) {
            return null;
        }

        setProgress(0.0f);
        ui.imagesListClear();
        int w = 0, h = 0;
        int type = BufferedImage.TYPE_BYTE_GRAY;
        Vector<BufferedImage> images = new Vector<BufferedImage>();
        for (int i = 0; i < files.length; i++) {
            setProgress((float) i / (float) files.length);
            BufferedImage im = ImageUtilities.loadImage(files[i]);
            if (im == null) {
                ui.imagesListAddInfo("Cannot read image: " + files[i]);
                continue;
            }

            if (i == 0) {
                w = im.getWidth();
                h = im.getHeight();
            }

            if (im.getType() != BufferedImage.TYPE_BYTE_GRAY) {
                BufferedImage imgGray = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                WritableRaster rasterRGB = im.getRaster();
                WritableRaster rasterGray = imgGray.getRaster();
                float[] wght = params.getRGBWeights();
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        rasterGray.setSample(x, y, 0,
                                wght[0] * rasterRGB.getSampleFloat(x, y, 0)
                                + wght[1] * rasterRGB.getSampleFloat(x, y, 1)
                                + wght[2] * rasterRGB.getSampleFloat(x, y, 2));

                    }
                }
                im = imgGray;
            }

            if (i == 0) {
                type = im.getType();
                ui.imagesListAddInfo("Image " + (i + 1) + " successfully read");
                images.add(im);
                continue;
            }

            if (im.getWidth() != w || im.getHeight() != h) {
                ui.imagesListAddInfo("Wrong size. Skipping image: " + files[i]);
                continue;
            }

            if (im.getType() != type) {
                ui.imagesListAddInfo("Wrong image type. Skipping image: " + files[i]);
                continue;
            }

            ui.imagesListAddInfo("Image " + (i + 1) + " successfully read");
            images.add(im);
        }

        
        int[] dims = new int[3];
        dims[0] = w;
        dims[1] = h;
        dims[2] = images.size();

        int[] dims2 = null;
        if (images.size() == 1) {
            dims2 = new int[]{w, h};
        } else {
            dims2 = new int[]{w, h, images.size()};
        }        
   
        RegularField out = new RegularField(dims2);
        float[][] affine = new float[4][3];
        affine[0][0] = 1;
        affine[0][1] = 0;
        affine[0][2] = 0;
        affine[1][0] = 0;
        affine[1][1] = 1;
        affine[1][2] = 0;
        affine[2][0] = 0;
        affine[2][1] = 0;
        affine[2][2] = 1;
        affine[3][0] = -dims[0] / 2;
        affine[3][1] = -dims[1] / 2;
        affine[3][2] = -dims[2] / 2;
        out.setAffine(affine);


        if (type == BufferedImage.TYPE_BYTE_GRAY) {
            byte[] data = new byte[dims[0] * dims[1] * dims[2]];
            for (int k = 0; k < dims[2]; k++) {
                WritableRaster raster = images.get(k).getRaster();
                for (int j = 0; j < dims[1]; j++) {
                    for (int i = 0; i < dims[0]; i++) {
                        data[k * dims[0] * dims[1] + j * dims[0] + i] = (byte) raster.getSample(i, j, 0);
                    }
                }
            }
            out.addData(DataArray.create(data, 1, "data"));
        } else if (type == BufferedImage.TYPE_INT_RGB) {
            byte[] rData = new byte[dims[0] * dims[1] * dims[2]];
            byte[] gData = new byte[dims[0] * dims[1] * dims[2]];
            byte[] bData = new byte[dims[0] * dims[1] * dims[2]];
            for (int k = 0; k < dims[2]; k++) {
                WritableRaster raster = images.get(k).getRaster();
                for (int j = 0; j < dims[1]; j++) {
                    for (int i = 0; i < dims[0]; i++) {
                        int l = k * dims[0] * dims[1] + j * dims[0] + i;
                        rData[l] = (byte) raster.getSample(i, j, 0);
                        gData[l] = (byte) raster.getSample(i, j, 1);
                        bData[l] = (byte) raster.getSample(i, j, 2);
                    }
                }
            }
            out.addData(DataArray.create(rData, 1, "redData"));
            out.addData(DataArray.create(gData, 1, "greenData"));
            out.addData(DataArray.create(bData, 1, "blueData"));
        } else {
            ui.imagesListClear();
            ui.imagesListAddInfo("Read cancelled - unsupported images type");
            return null;
        }

        setProgress(1.0f);
        return out;
    }

    @Override
    public void onInitFinishedLocal() {
        if (isForceFlag()) {
            computeUI.activateOpenDialog();
        }
    }
    
   @Override
   public boolean isGenerator() {
      return true;
   }
    
}
