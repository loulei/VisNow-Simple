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
package pl.edu.icm.visnow.lib.basic.readers.ReadCSV;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class CsvFieldReader {

    public static RegularField readCsvFloatField1D(String filePath, String fieldDelimiter, boolean headersLine) {
        return readCsvFloatField1D(filePath, fieldDelimiter, headersLine, 0, 0);
    }

    public static RegularField readCsvFloatField1D(String filePath, String fieldDelimiter, boolean headersLine, int linesToSkip, int linesToSkipAtTheEnd) {
        return readCsvField1D(filePath, fieldDelimiter, headersLine, linesToSkip, linesToSkipAtTheEnd, false);
    }

    public static RegularField readCsvField1D(String filePath, String fieldDelimiter, boolean headersLine) {
        return readCsvField1D(filePath, fieldDelimiter, headersLine, 0, 0, true);
    }
    
    public static RegularField readCsvField1D(String filePath, String fieldDelimiter, boolean headersLine, int linesToSkip, int linesToSkipAtTheEnd, boolean allowStrings) {
        if (filePath == null) {
            System.out.println("[readCsvFloatField1D] filePath is null");
            return null;
        }

        RegularField field = null;

        String line = null;
        int[] dims = new int[1];
        FileReader in;
        ArrayList<String> lines = new ArrayList<String>();

        try {
            in = new FileReader(filePath);
            LineNumberReader lr = new LineNumberReader(in);
            try {
                if (linesToSkip > 0) {
                    while (linesToSkip-- != 0) {
                        lr.readLine();
                    }
                }
                while ((line = lr.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }

        int nLines = lines.size();
        if (headersLine) {
            nLines--;
        }
        nLines -= linesToSkipAtTheEnd;

        dims[0] = nLines;
        field = new RegularField(dims);

        String[] tmp = lines.get(0).split(fieldDelimiter);
        int nFields = tmp.length;
        String[] headers = new String[nFields];
        //float[][] data = new float[nFields][nLines];
        ArrayList data = new ArrayList();

        if (headersLine) {
            //treat first line as headers
            for (int i = 0; i < nFields; i++) {
                headers[i] = tmp[i];
            }
        } else {
            for (int i = 0; i < nFields; i++) {
                headers[i] = "field" + i;
            }
        }

        float v;
        for (int l = 0; l < nLines; l++) {
            if (headersLine) {
                tmp = lines.get(l + 1).split(fieldDelimiter);
            } else {
                tmp = lines.get(l).split(fieldDelimiter);
            }

            if (l == 0) {
                if(!allowStrings) {
                    for (int i = 0; i < nFields; i++) {
                        float[] fData = new float[nLines];
                        data.add(fData);
                    }                    
                } else {
                    for (int i = 0; i < nFields; i++) {
                        try {
                            v = Float.parseFloat(tmp[i]);
                            float[] fData = new float[nLines];
                            data.add(fData);
                        } catch (NumberFormatException ex) {
                            String[] sData = new String[nLines];
                            data.add(sData);
                        }
                    }
                }
            }

            for (int i = 0; i < nFields; i++) {
                if(data.get(i) instanceof String[]) {
                    ((String[])data.get(i))[l] = tmp[i];                    
                } else {
                    try {
                        v = Float.parseFloat(tmp[i]);
                    } catch (NumberFormatException ex) {
                        v = 0.0f;
                    }
                    ((float[])data.get(i))[l] = v;
                }                
            }
        }


        for (int i = 0; i < nFields; i++) {
            if(data.get(i) instanceof String[]) {
                field.addData(DataArray.create((String[])data.get(i), 1, headers[i]));
            } else {
                field.addData(DataArray.create((float[])data.get(i), 1, headers[i]));
            }            
        }
        return field;
    }

    public static RegularField readCsvQuotedFloatField1D(String filePath, String quoteString, char comma, boolean headersLine) {
        if (filePath == null) {
            System.out.println("[readCsvQuotedFloatField1D] filePath is null");
            return null;
        }

        RegularField field = null;

        String line = null;
        int[] dims = new int[1];
        FileReader in;
        ArrayList<String> lines = new ArrayList<String>();

        try {
            in = new FileReader(filePath);
            LineNumberReader lr = new LineNumberReader(in);
            try {
                while ((line = lr.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }

        int nLines = lines.size();
        if (headersLine) {
            nLines--;
        }

        dims[0] = nLines;
        field = new RegularField(dims);

        String[] tmp = Decode(lines.get(0), quoteString);
        int nFields = tmp.length;
        String[] headers = new String[nFields];
        float[][] data = new float[nFields][nLines];

        if (headersLine) {
            //treat first line as headers
            for (int i = 0; i < nFields; i++) {
                headers[i] = tmp[i];
            }
        } else {
            for (int i = 0; i < nFields; i++) {
                headers[i] = "field" + i;
            }
        }

        float v;
        for (int l = 0; l < nLines; l++) {
            if (headersLine) {
                tmp = Decode(lines.get(l + 1), quoteString);
            } else {
                tmp = Decode(lines.get(l), quoteString);
            }

            for (int i = 0; i < nFields; i++) {
                try {
                    String dotSeparatedLine = tmp[i].replace(comma, '.');
                    v = Float.parseFloat(dotSeparatedLine);

                } catch (NumberFormatException ex) {
                    v = 0.0f;
                }
                data[i][l] = v;
            }
        }


        for (int i = 0; i < nFields; i++) {
            field.addData(DataArray.create(data[i], 1, headers[i]));
        }
        return field;
    }

    private static String[] Decode(String line, String quoteString) {
        String[] tmp = line.split(quoteString);

        ArrayList<String> ret = new ArrayList<String>();

        int i = 1;
        do {
            ret.add(tmp[i]);
            i += 2;
        } while (i < tmp.length);

        String[] retA = new String[ret.size()];
        ret.toArray(retA);

        return retA;
    }

    private CsvFieldReader() {
    }
}
