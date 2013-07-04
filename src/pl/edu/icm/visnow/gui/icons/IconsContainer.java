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

package pl.edu.icm.visnow.gui.icons;


import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class IconsContainer
{   
   private static BufferedImage lightColorTable;
   private static BufferedImage indexL, indexR, indexLDisabled, indexRDisabled;
   private static BufferedImage ballBlack, ballGray, ballBlue1, ballBlue2, ballLight, ballRed, ballGreen, ballYellow, ballMagenta;
   private static BufferedImage gouraud, flat, unshaded, background;
   private static void input()
   {
      try
      {
         lightColorTable = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/lightChooser.png"));
         indexL          = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/index_l.png"));
         indexR          = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/index_r.png"));
         indexLDisabled  = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/index_l_grey.png"));
         indexRDisabled  = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/index_r_grey.png"));
         ballBlack       = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/bbs.png"));
         ballGray        = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b0s.png"));
         ballBlue1       = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b1s.png"));
         ballBlue2       = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b12s.png"));
         ballLight       = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b2s.png"));
         ballRed         = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b3s.png"));
         ballGreen       = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b4s.png"));
         ballYellow      = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b5s.png"));
         ballMagenta     = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b6s.png"));
         gouraud         = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/gouraud.png"));
         flat            = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/flat.png"));
         unshaded        = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/unshaded.png"));
         background      = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/background.png"));
      } catch (Exception e)
      {
      }
   }
   
   public static BufferedImage getGouraud()
   {
      if (gouraud == null)
      {
         try { gouraud = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/gouraud.png"));} 
         catch (Exception e) {}
      }
      return gouraud;
  }

   public static BufferedImage getFlat()
   {
      if (flat == null)
      {
         try { flat = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/flat.png"));} 
         catch (Exception e) {}
      }
      return flat;
   }

    public static BufferedImage getUnshaded()
   {
      if (unshaded == null)
      {
         try { unshaded = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/unshaded.png"));}
         catch (Exception e) {}
      }
      return unshaded;
  }

   public static BufferedImage getBackground()
   {
      if (background == null)
      {
         try {background = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/background.png")); }
         catch (Exception e) {}
      }
      return background;
   }
   

   public static BufferedImage getIndexL()
   {
      if (indexL == null)
      {
         try { indexL = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/index_l.png"));} 
         catch (Exception e) {
         }
      }
      return indexL;
  }

   public static BufferedImage getIndexR()
   {
      if (indexR == null)
      {
         try { indexR = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/index_r.png"));} 
         catch (Exception e) {}
      }
      return indexR;
   }

    public static BufferedImage getIndexLDisabled()
   {
      if (indexL == null)
      {
         try { indexLDisabled = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/index_l_grey.png"));}
         catch (Exception e) {}
      }
      return indexLDisabled;
  }

   public static BufferedImage getIndexRDisabled()
   {
      if (indexRDisabled == null)
      {
         try {indexRDisabled = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/index_r_grey.png")); }
         catch (Exception e) {}
      }
      return indexRDisabled;
   }

  public static BufferedImage getLightColorTable()
   {
      if (lightColorTable == null)
      {
         try { lightColorTable = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/lightChooser.png"));} 
         catch (Exception e) {}
      }
     return lightColorTable;
   }
   
   public static BufferedImage getBallBlack()
   {
      if (ballBlack == null)
      {
         try { ballBlack       = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/bbs.png"));}
         catch (Exception e) {
            System.out.println("getBallBlack()");}
      }
      return ballBlack;
   }
   
   public static BufferedImage getBallGray()
   {
      if (ballGray == null)
      {
         try { ballGray       = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b0s.png"));}
         catch (Exception e) {
            System.out.println("getBallGray()");}
      }
      return ballGray;
   }

   public static BufferedImage getBallBlue1()
   {
      if (ballBlue1 == null)
      {
         try { ballBlue1      = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b1s.png"));}
         catch (Exception e) {
            System.out.println("getBallBlue1()");}
      }
      return ballBlue1;
   }
   
   public static BufferedImage getBallBlue2()
   {
      if (ballBlue2 == null)
      {
         try { ballBlue2      = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b12s.png"));}
         catch (Exception e) {
            System.out.println("getBallBlue2()");}
      }
      return ballBlue2;
   }
   
   public static BufferedImage getBallLight()
   {
      if (ballLight == null)
      {
         try { ballLight     = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b2s.png"));}
         catch (Exception e) {
            System.out.println("getBallLight()");}
      }
      return ballLight;
   }
   
   public static BufferedImage getBallRed()
   {
      if (ballRed == null)
      {
         try { ballRed     = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b3s.png"));}
         catch (Exception e) {
            System.out.println("getBallRed()");}
      }
      return ballRed;
   }
   
   public static BufferedImage getBallGreen()
   {
      if (ballGreen == null)
      {
         try { ballGreen     = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b4s.png"));}
         catch (Exception e) {
            System.out.println("getBallGreen()");}
      }
      return ballGreen;
   }
    
   public static BufferedImage getBallYellow()
   {
      if (ballYellow == null)
      {
         try { ballYellow     = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b5s.png"));}
         catch (Exception e) {
            System.out.println("getBallYellow()");}
      }
      return ballYellow;
   }
  
   public static BufferedImage getBallMagenta()
   {
      if (ballMagenta == null)
      {
         try { ballMagenta     = ImageIO.read(IconsContainer.class.getResource("/pl/edu/icm/visnow/gui/icons/b6s.png"));}
         catch (Exception e) {
            System.out.println("getBallMagenta()");}
      }
      return ballMagenta;
   }

   private IconsContainer()
   {
   }

}
