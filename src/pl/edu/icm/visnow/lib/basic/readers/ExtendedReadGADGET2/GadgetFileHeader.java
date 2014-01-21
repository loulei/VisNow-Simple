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

package pl.edu.icm.visnow.lib.basic.readers.ExtendedReadGADGET2;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.stream.FileImageInputStream;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class GadgetFileHeader {

    private ByteOrder endian = ByteOrder.BIG_ENDIAN;
    private int[] npart = new int[6];
    private double[] mass = new double[6];
    private double time;
    private double redshift;
    private int flag_sfr;
    private int flag_feedback;
    private int[] npartTotal = new int[6];
    private int flag_cooling;
    private int num_files;
    private double boxSize;
    private double omega0;
    private double omegaLambda;
    private double hubbleParam;
    private String filePath = "";
    private int flagAge;
    private int flagMetals;
    private int[] nallhw = new int[6];
    
    private GadgetFileHeader() {        
    }
    
    public static GadgetFileHeader read(String filePath) {        
        GadgetFileHeader header = new GadgetFileHeader();
        header.filePath = filePath;
        File f = new File(filePath);
        FileImageInputStream in;
        try {
            in = new FileImageInputStream(f);
            in.setByteOrder(ByteOrder.BIG_ENDIAN);
            int test0 = in.readInt();
            if (test0 != 256) {
                in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                in.seek(0);
                test0 = in.readInt();
                if (test0 != 256) {
                    System.err.println("ERROR: cannot resolve endianness on file "+filePath);
                    return null;
                } else {
                    header.endian = ByteOrder.LITTLE_ENDIAN;
                }
            } else {
                header.endian = ByteOrder.BIG_ENDIAN;
            }

            //start read header
            int[] header_npart = new int[6];
            in.readFully(header_npart, 0, 6);
            header.npart = header_npart;
            
            double[] header_mass = new double[6];
            in.readFully(header_mass, 0, 6);
            header.mass = header_mass;
            
            header.time = in.readDouble();
            header.redshift = in.readDouble();
            header.flag_sfr = in.readInt();
            header.flag_feedback = in.readInt();
            
            int[] header_npartTotal = new int[6];
            in.readFully(header_npartTotal, 0, 6);
            header.npartTotal = header_npartTotal;
            
            header.flag_cooling = in.readInt();
            header.num_files = in.readInt();
            header.boxSize = in.readDouble();
            header.omega0 = in.readDouble();
            header.omegaLambda = in.readDouble();
            header.hubbleParam = in.readDouble();
            
            header.flagAge = in.readInt();
            header.flagMetals = in.readInt();
            header.nallhw = new int[6];
            in.readFully(header.nallhw, 0, 6);
            
            //end read header
            
            in.close();
        } catch(IOException ex) {
            return null;
        }        
        return header;
    }
    
    /**
     * @return the endian
     */
    public ByteOrder getEndian() {
        return endian;
    }

    /**
     * @return the npart
     */
    public int[] getNpart() {
        return npart;
    }

    /**
     * @return the header_mass
     */
    public double[] getMass() {
        return mass;
    }

    /**
     * @return the header_time
     */
    public double getTime() {
        return time;
    }

    /**
     * @return the header_redshift
     */
    public double getRedshift() {
        return redshift;
    }

    /**
     * @return the header_flag_sfr
     */
    public int getFlag_sfr() {
        return flag_sfr;
    }

    /**
     * @return the header_flag_feedback
     */
    public int getFlag_feedback() {
        return flag_feedback;
    }

    /**
     * @return the header_npartTotal
     */
    public int[] getNpartTotal() {
        return npartTotal;
    }

    /**
     * @return the header_flag_cooling
     */
    public int getFlag_cooling() {
        return flag_cooling;
    }

    /**
     * @return the header_num_files
     */
    public int getNum_files() {
        return num_files;
    }

    /**
     * @return the header_BoxSize
     */
    public double getBoxSize() {
        return boxSize;
    }

    /**
     * @return the header_Omega0
     */
    public double getOmega0() {
        return omega0;
    }

    /**
     * @return the header_OmegaLambda
     */
    public double getOmegaLambda() {
        return omegaLambda;
    }

    /**
     * @return the header_HubbleParam
     */
    public double getHubbleParam() {
        return hubbleParam;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @return the flagAge
     */
    public int getFlagAge() {
        return flagAge;
    }

    /**
     * @return the flagMetals
     */
    public int getFlagMetals() {
        return flagMetals;
    }

    /**
     * @return the nallhw
     */
    public int[] getNallhw() {
        return nallhw;
    }

}
