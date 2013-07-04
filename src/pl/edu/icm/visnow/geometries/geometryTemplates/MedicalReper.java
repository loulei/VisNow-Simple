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

package pl.edu.icm.visnow.geometries.geometryTemplates;

import com.sun.j3d.utils.image.TextureLoader;
import java.awt.Color;
import javax.media.j3d.*;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.objects.generics.*;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class MedicalReper extends GeometryObject
{

   public MedicalReper()
   {
      //ImageIcon texture = new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/geometries/geometryTemplates/medical_reper.tiff"));
      //TextureLoader myLoader = new TextureLoader(texture.getImage(), null);
      //TextureLoader myLoader = new TextureLoader(getClass().getResource("/pl/edu/icm/visnow/geometries/geometryTemplates/medical_reper.tiff"), null);
      TextureLoader myLoader = new TextureLoader(getClass().getResource("/pl/edu/icm/visnow/geometries/geometryTemplates/medical_reper.png"), null);

      ImageComponent2D img = myLoader.getImage();
      Texture2D txt = new Texture2D(Texture2D.BASE_LEVEL, Texture2D.RGB, img.getWidth(), img.getHeight());
      txt.setImage(0, img);
      txt.setEnable(true);

      OpenAppearance app = new OpenAppearance();
      //app.getPolygonAttributes().setCullFace(PolygonAttributes.CULL_NONE);
      //app.getPolygonAttributes().setBackFaceNormalFlip(true);
      //app.getPolygonAttributes().setPolygonMode(PolygonAttributes.POLYGON_LINE);
      OpenMaterial mat = new OpenMaterial();
      mat.setSpecularColor(0.1f, 0.1f, 0.1f);
      app.setMaterial(mat);
      app.setTexture(txt);

      float scale = 0.5f;
      float d = 0.125f;

      //OUTLINE
      Shape3D outlineShape = new Shape3D();
      float[] boxVerts = new float[24];
      IndexedLineStripArray box = new IndexedLineStripArray(8,
              GeometryArray.COORDINATES | GeometryArray.BY_REFERENCE
              | GeometryArray.BY_REFERENCE_INDICES | GeometryArray.USE_COORD_INDEX_ONLY,
              24, new int[]
              {
                 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2
              });
      box.setCoordIndicesRef(new int[]
              {
                 0, 1, 2, 3, 4, 5, 6, 7, 0, 2, 1, 3, 4, 6, 5, 7, 0, 4, 1, 5, 2, 6, 3, 7
              });
      box.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);

      float[][] af = new float[][]
      {
         {
            2.0f, 0.0f, 0.0f
         },
         {
            0.0f, 2.0f, 0.0f
         },
         {
            0.0f, 0.0f, 2.0f
         },
         {
            -1.0f, -1.0f, -1.0f
         },
      };
      for (int i = 0; i < 4; i++)
      {
         for (int j = 0; j < 3; j++)
         {
            af[i][j] *= scale;
         }
      }
      for (int i = 0; i < 3; i++)
      {
         boxVerts[i] = af[3][i];
         boxVerts[i + 3] = af[3][i] + af[0][i];
         boxVerts[i + 6] = af[3][i] + af[1][i];
         boxVerts[i + 9] = af[3][i] + af[1][i] + af[0][i];
         boxVerts[i + 12] = af[3][i] + af[2][i];
         boxVerts[i + 15] = af[3][i] + af[2][i] + af[0][i];
         boxVerts[i + 18] = af[3][i] + af[2][i] + af[1][i];
         boxVerts[i + 21] = af[3][i] + af[2][i] + af[1][i] + af[0][i];
      }
      box.setCoordRefFloat(boxVerts);

      OpenLineAttributes boxLineAttr = new OpenLineAttributes();
      boxLineAttr.setLineWidth(4.0f);
      OpenColoringAttributes boxColorAttr = new OpenColoringAttributes();
      boxColorAttr.setColor(new Color3f(Color.BLACK));

      OpenAppearance boxApp = new OpenAppearance();
      boxApp.setLineAttributes(boxLineAttr);
      boxApp.setColoringAttributes(boxColorAttr);
      outlineShape.addGeometry(box);
      outlineShape.setAppearance(boxApp);
      //this.addChild(outlineShape);


      //FRONT - Head
      float[] frontCoords = new float[]
      {
         -0.99f, -0.99f, 0.99f,
         0.99f, -0.99f, 0.99f,
         0.99f, 0.99f, 0.99f,
         -0.99f, 0.99f, 0.99f
      };
      for (int i = 0; i < frontCoords.length; i++)
      {
         frontCoords[i] *= scale;
      }
      float[] frontNormals = new float[]
      {
         0.0f, 0.0f, 1.0f,
         0.0f, 0.0f, 1.0f,
         0.0f, 0.0f, 1.0f,
         0.0f, 0.0f, 1.0f,
      };
      float[] frontTexCoords = new float[]
      {
         4 * d, 0.0f,
         5 * d, 0.0f,
         5 * d, 1.0f,
         4 * d, 1.0f,
      };
      QuadArray front = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.BY_REFERENCE);
      front.setCoordRefFloat(frontCoords);
      front.setNormalRefFloat(frontNormals);
      front.setTexCoordRefFloat(0, frontTexCoords);
      Shape3D frontObj = new Shape3D(front, app);
      geometryObj.removeAllChildren();
      geometryObj.addChild(frontObj);

      //BACK - Feet
      float[] backCoords = new float[]
      {
         0.99f, 0.99f, -0.99f,
         -0.99f, 0.99f, -0.99f,
         -0.99f, -0.99f, -0.99f,
         0.99f, -0.99f, -0.99f
      };
      for (int i = 0; i < frontCoords.length; i++)
      {
         backCoords[i] *= scale;
      }
      float[] backNormals = new float[]
      {
         0.0f, 0.0f, -1.0f,
         0.0f, 0.0f, -1.0f,
         0.0f, 0.0f, -1.0f,
         0.0f, 0.0f, -1.0f,
      };
      float[] backTexCoords = new float[]
      {
         6 * d, 0.0f,
         5 * d, 0.0f,
         5 * d, 1.0f,
         6 * d, 1.0f,
      };
      QuadArray back = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.BY_REFERENCE);
      back.setCoordRefFloat(backCoords);
      back.setNormalRefFloat(backNormals);
      back.setTexCoordRefFloat(0, backTexCoords);
      Shape3D backObj = new Shape3D(back, app);
      geometryObj.addChild(backObj);

      //TOP - Posterior
      float[] topCoords = new float[]
      {
         0.99f, 0.99f, -0.99f,
         -0.99f, 0.99f, -0.99f,
         -0.99f, 0.99f, 0.99f,
         0.99f, 0.99f, 0.99f
      };
      for (int i = 0; i < topCoords.length; i++)
      {
         topCoords[i] *= scale;
      }
      float[] topNormals = new float[]
      {
         0.0f, 1.0f, 0.0f,
         0.0f, 1.0f, 0.0f,
         0.0f, 1.0f, 0.0f,
         0.0f, 1.0f, 0.0f,
      };
      float[] topTexCoords = new float[]
      {
         3 * d, 0.0f,
         4 * d, 0.0f,
         4 * d, 1.0f,
         3 * d, 1.0f,
      };
      QuadArray top = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.BY_REFERENCE);
      top.setCoordRefFloat(topCoords);
      top.setNormalRefFloat(topNormals);
      top.setTexCoordRefFloat(0, topTexCoords);
      Shape3D topObj = new Shape3D(top, app);
      geometryObj.addChild(topObj);

      //BOTTOM - Anterior
      float[] bottomCoords = new float[]
      {
         -0.99f, -0.99f, -0.99f,
         0.99f, -0.99f, -0.99f,
         0.99f, -0.99f, 0.99f,
         -0.99f, -0.99f, 0.99f
      };
      for (int i = 0; i < bottomCoords.length; i++)
      {
         bottomCoords[i] *= scale;
      }
      float[] bottomNormals = new float[]
      {
         0.0f, -1.0f, 0.0f,
         0.0f, -1.0f, 0.0f,
         0.0f, -1.0f, 0.0f,
         0.0f, -1.0f, 0.0f,
      };
      float[] bottomTexCoords = new float[]
      {
         2 * d, 0.0f,
         3 * d, 0.0f,
         3 * d, 1.0f,
         2 * d, 1.0f,
      };
      QuadArray bottom = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.BY_REFERENCE);
      bottom.setCoordRefFloat(bottomCoords);
      bottom.setNormalRefFloat(bottomNormals);
      bottom.setTexCoordRefFloat(0, bottomTexCoords);
      Shape3D bottomObj = new Shape3D(bottom, app);
      geometryObj.addChild(bottomObj);

      //LEFT - Right
      float[] leftCoords = new float[]
      {
         -0.99f, 0.99f, -0.99f,
         -0.99f, -0.99f, -0.99f,
         -0.99f, -0.99f, 0.99f,
         -0.99f, 0.99f, 0.99f,
      };
      for (int i = 0; i < leftCoords.length; i++)
      {
         leftCoords[i] *= scale;
      }
      float[] leftNormals = new float[]
      {
         -1.0f, 0.0f, 0.0f,
         -1.0f, 0.0f, 0.0f,
         -1.0f, 0.0f, 0.0f,
         -1.0f, 0.0f, 0.0f,
      };
      float[] leftTexCoords = new float[]
      {
         0 * d, 0.0f,
         1 * d, 0.0f,
         1 * d, 1.0f,
         0 * d, 1.0f,
      };
      QuadArray left = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.BY_REFERENCE);
      left.setCoordRefFloat(leftCoords);
      left.setNormalRefFloat(leftNormals);
      left.setTexCoordRefFloat(0, leftTexCoords);
      Shape3D leftObj = new Shape3D(left, app);
      geometryObj.addChild(leftObj);

      //RIGHT - Left
      float[] rightCoords = new float[]
      {
         0.99f, -0.99f, -0.99f,
         0.99f, 0.99f, -0.99f,
         0.99f, 0.99f, 0.99f,
         0.99f, -0.99f, 0.99f,
      };
      for (int i = 0; i < rightCoords.length; i++)
      {
         rightCoords[i] *= scale;
      }
      float[] rightNormals = new float[]
      {
         1.0f, 0.0f, 0.0f,
         1.0f, 0.0f, 0.0f,
         1.0f, 0.0f, 0.0f,
         1.0f, 0.0f, 0.0f,
      };
      float[] rightTexCoords = new float[]
      {
         1 * d, 0.0f,
         2 * d, 0.0f,
         2 * d, 1.0f,
         1 * d, 1.0f,
      };
      QuadArray right = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.BY_REFERENCE);
      right.setCoordRefFloat(rightCoords);
      right.setNormalRefFloat(rightNormals);
      right.setTexCoordRefFloat(0, rightTexCoords);
      Shape3D rightObj = new Shape3D(right, app);
      geometryObj.addChild(rightObj);


//        float shininess = 0.0f;
//        Color3f rlColor = new Color3f(1.0f, 0.0f, 0.0f);
//        Color3f apColor = new Color3f(0.0f, 1.0f, 0.0f);
//        Color3f fhColor = new Color3f(0.0f, 0.0f, 1.0f);
//        
//        OpenAppearance appearance = new OpenAppearance();
//        appearance.getPolygonAttributes().setCullFace(PolygonAttributes.CULL_NONE);
//        appearance.getPolygonAttributes().setBackFaceNormalFlip(true);
//        Box b = new Box(.5f, .5f, .5f, appearance);
//        
//        //FH - Feet/Head
//        OpenAppearance fhApp = new OpenAppearance();
//        OpenMaterial fhMat = new OpenMaterial();   
//        fhMat.setShininess(shininess);
//        //hfMat.setColorTarget(OpenMaterial.AMBIENT_AND_DIFFUSE);
//        fhMat.setColorTarget(OpenMaterial.AMBIENT_AND_DIFFUSE);
//        fhMat.setAmbientColor(fhColor);        
//        fhMat.setDiffuseColor(fhColor);
//        fhMat.setSpecularColor(fhColor);
//        fhApp.setMaterial(fhMat);
//        fhApp.setColoringAttributes(new ColoringAttributes(0.0f, 0.0f, 0.0f, ColoringAttributes.NICEST));        
//        //FRONT->Head
//        Shape3D front = b.getShape(Box.FRONT);
//        front.setAppearance(fhApp);
//        //BACK->Feet
//        Shape3D back = b.getShape(Box.BACK);
//        back.setAppearance(fhApp);
//
//        //AP - Anterior/Posterior
//        OpenAppearance apApp = new OpenAppearance();
//        OpenMaterial apMat = new OpenMaterial();   
//        apMat.setShininess(shininess);
//        apMat.setColorTarget(OpenMaterial.AMBIENT_AND_DIFFUSE);
//        apMat.setAmbientColor(apColor);        
//        apMat.setDiffuseColor(apColor);
//        apMat.setSpecularColor(apColor);
//        apApp.setMaterial(apMat);
//        apApp.setColoringAttributes(new ColoringAttributes(0.0f, 0.0f, 0.0f, ColoringAttributes.NICEST));        
//        //TOP->Posterior
//        Shape3D top = b.getShape(Box.TOP);
//        top.setAppearance(apApp);
//        //BOTTOM->Anterior
//        Shape3D bottom = b.getShape(Box.BOTTOM);
//        bottom.setAppearance(apApp);
//        
//        //RL - Right/Left
//        OpenAppearance rlApp = new OpenAppearance();
//        OpenMaterial rlMat = new OpenMaterial();   
//        rlMat.setShininess(shininess);
//        rlMat.setColorTarget(OpenMaterial.AMBIENT_AND_DIFFUSE);
//        rlMat.setAmbientColor(rlColor);        
//        rlMat.setDiffuseColor(rlColor);
//        rlMat.setSpecularColor(rlColor);
//        rlApp.setMaterial(rlMat);
//        rlApp.setColoringAttributes(new ColoringAttributes(0.0f, 0.0f, 0.0f, ColoringAttributes.NICEST));        
//        //LEFT->Right
//        Shape3D left = b.getShape(Box.LEFT);
//        left.setAppearance(rlApp);
//        //RIGHT->Left
//        Shape3D right = b.getShape(Box.RIGHT);
//        right.setAppearance(rlApp);
//        this.addChild(b);

   }

}
