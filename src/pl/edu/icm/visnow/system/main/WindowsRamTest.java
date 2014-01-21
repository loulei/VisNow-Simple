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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author babor
 */


public class WindowsRamTest {
    private static void printFullUsage() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        System.out.print("\n");
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("get") && Modifier.isPublic(method.getModifiers())) {
                Object value;
                try {
                    value = method.invoke(operatingSystemMXBean);
                } catch (Exception e) {
                    value = e;
                    e.printStackTrace();
                }
                System.out.println("\t" + method.getName() + " = " + value);
            }
        }
    }

    private static void printTotalMemory() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        long totalMemoryB = -1;
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("getTotalPhysicalMemorySize") && Modifier.isPublic(method.getModifiers())) {
                Object value;
                try {
                    value = method.invoke(operatingSystemMXBean);
                } catch (Exception e) {
                    value = e;
                    e.printStackTrace();
                }
                
                if(value instanceof Long) {
                    totalMemoryB = (Long)value;
                }
                break;
            }
        }
        
        if(totalMemoryB == -1) {
            System.out.println("1024");
        } else {
            totalMemoryB = totalMemoryB*4/5;                
            long mem32limit = 1400L*1024L*1024L;
            if(!VisNow.isCpuArch64() && totalMemoryB > mem32limit) {
                totalMemoryB = mem32limit;
            }
            System.out.println(""+(totalMemoryB/(1024L*1024L)));
        }        
    }
    
    public static void main(String[] args) {
        if (args != null && args.length == 1 && args[0].equals("-full")) {
            System.out.println("\n\n\t---------------JVM Runtime Details-------------");
            System.out.println("\tAvailable processors (Cores): " + Runtime.getRuntime().availableProcessors());
            System.out.println("\tInitial Memory (-Xms)       : " + (Runtime.getRuntime().freeMemory() / (1024 * 1024)) + " MB");
            long maxMemory = Runtime.getRuntime().maxMemory();
            System.out.println("\tMaximum JVM Memory (-Xmx)   : " + (maxMemory / (1024 * 1024)) + " MB");
            System.out.println("\tTotal Used JVM Memory       : " + (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " MB");

            File[] roots = File.listRoots();
            System.out.println("\n\n\t---------------FileSystem Details-------------");
            for (File root : roots) {
                System.out.println("\n\tFileSystem Root Details: " + root.getAbsolutePath());
                System.out.println("\tTotal Space              : " + (root.getTotalSpace() / (1024 * 1024)) + " MB");
                System.out.println("\tFree Space               : " + (root.getFreeSpace() / (1024 * 1024)) + " MB");
                System.out.println("\tUsable Space             : " + (root.getUsableSpace() / (1024 * 1024)) + " MB");
            }
            System.out.println("\n\n\t---------------CPU USAGES-------------");
            printFullUsage();
        } else {
            try {
                printTotalMemory();
            } catch(Exception ex) {
                System.out.println("1024");
            }
        }

    }    
}
