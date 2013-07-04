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

package pl.edu.icm.visnow.lib.utils.field;

/**
 *
 * @author know
 */
public class Tile
{
   public static final int TILE_OK            = 0;
   public static final int TILE_TYPE_MISMATCH = 1;
   public static final int TILE_DIMS_MISMATCH = 2;
   public static int putTile(int[][] tile, int[] dims, Object target, Object content, int vlen, int coord) 
   {
      if (dims == null || tile == null || dims.length != tile.length)
         return TILE_DIMS_MISMATCH;
      if (content instanceof byte[] && target instanceof boolean[])
         return putTile(tile, dims, (boolean[])target, (byte[])content, vlen, coord);
      if (content instanceof byte[] && target instanceof byte[])
         return putTile(tile, dims, (byte[])target, (byte[])content, vlen, coord);
      if (content instanceof short[] && target instanceof short[])
         return putTile(tile, dims, (short[])target, (short[])content, vlen, coord);
      if (content instanceof int[] && target instanceof int[])
         return putTile(tile, dims, (int[])target, (int[])content, vlen, coord);
      if (content instanceof float[] && target instanceof float[])
         return putTile(tile, dims, (float[])target, (float[])content, vlen, coord);
      if (content instanceof double[] && target instanceof double[])
         return putTile(tile, dims, (double[])target, (double[])content, vlen, coord);
      if (content instanceof String[] && target instanceof String[])
         return putTile(tile, dims, (String[])target, (String[])content, vlen, coord);
      return TILE_TYPE_MISMATCH;
   }  
   
   private static int putTile(int[][] tile, int[] dims, boolean[]target, byte[] cont, int vlen)
   {
      int k = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         k *= tileDims[i];
      }
      if (cont.length != vlen * k)
         return TILE_DIMS_MISMATCH;
      int nCopied = vlen * (Math.min(tile[0][1] + 1, dims[0]) - Math.max(tile[0][0], 0));
      switch (dims.length)
      {
         case 3:
            for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0);
                    ii < tileDims[2] && i < dims[2];
                    i++, ii++)
               for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                       jj < tileDims[1] && j < dims[1]; j++, jj++)
               {
                  int startCont = vlen * ((tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0));
                  int startTarg = vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0));
                  for (int l = 0; l < nCopied; l++)
                     target[startTarg + l] = cont[startCont + l] != 0;
               }
            break;
         case 2:
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                    j < tile[1][1] && jj < dims[1]; j++, jj++)
            {
               int startCont = vlen * (jj * tileDims[0] + Math.max(-tile[0][0], 0));
               int startTarg = vlen * (j * dims[0] + Math.max(tile[0][0], 0));
               for (int l = 0; l < nCopied; l++)
                  target[startTarg + l] = cont[startCont + l] != 0;
            }
            break;
         case 1:
            int startCont = vlen * Math.max(-tile[0][0], 0);
            int startTarg = vlen * Math.max(tile[0][0], 0);
            for (int l = 0; l < nCopied; l++)
               target[startTarg + l] = cont[startCont + l] != 0;
            break;
      }
      return TILE_OK;
   }  
   
   private static int putTile(int[][] tile, int[] dims, boolean[]target, byte[] cont, int vlen, int coord) 
   {
      if (coord == -1)
         return putTile(tile, dims, target, cont, vlen);
      int tile_size = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         tile_size *= tileDims[i];
      }
      if (cont.length != tile_size)
         return TILE_DIMS_MISMATCH;
      switch (dims.length)
      {
      case 3:
         for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0); 
                  i < dims[2] &&               ii < tileDims[2]; 
                  i++,                         ii++)
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                     j < dims[1] &&               jj < tileDims[1]; 
                     j++,                         jj ++)
               for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                        ks = (tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0),
                        kt = vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                        k < dims[0] &&                   kk < tileDims[0]; 
                        k++,                             kk++, ks ++, kt += vlen)
                     target[kt] = cont[ks] != 0;
                  
         break;
      case 2:
         for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                  j < dims[1] &&               jj < tileDims[1]; 
                  j++,                         jj ++)
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                     ks = jj * tileDims[0] + Math.max(-tile[0][0], 0),
                     kt = vlen * (j * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                     k < dims[0] &&                   kk < tileDims[0]; 
                     k++,                             kk++, ks ++, kt += vlen)
                  target[kt] = cont[ks] != 0;
         break;
      case 1:
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                  kt = vlen * (Math.max(tile[0][0], 0)) + coord; 
                  k < dims[0] &&                   kk < tileDims[0]; 
                  k++,                             kk++, kt += vlen)
                  target[kt] = cont[kk] != 0;
         break;
      }
      return TILE_OK;
   }  
   
   private static int putTile(int[][] tile, int[] dims, byte[]target, byte[] cont, int vlen)
   {
      int k = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         k *= tileDims[i];
      }
      if (cont.length != vlen * k)
         return TILE_DIMS_MISMATCH;
      int nCopied = vlen * (Math.min(tile[0][1] + 1, dims[0]) - Math.max(tile[0][0], 0));
      switch (dims.length)
      {
      case 3:
         for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0); 
                  ii < tileDims[2] && i < dims[2]; 
                  i++, ii++)
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                     jj < tileDims[1] && j < dims[1]; j++, jj ++)
               System.arraycopy(
                     cont, vlen * ((tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0)), 
                     target, vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0)), nCopied);
         break;
      case 2:
         for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                  j < tile[1][1] && jj < dims[1]; j++, jj ++)
            System.arraycopy(
                     cont, vlen * (jj * tileDims[0] + Math.max(-tile[0][0], 0)), 
                     target, vlen * (j * dims[0] + Math.max(tile[0][0], 0)), nCopied);
         break;
      case 1:
            System.arraycopy(cont, 0, target, vlen * Math.max(tile[0][0], 0), nCopied);
         break;
      }
      return TILE_OK;
   }   
   
   private static int putTile(int[][] tile, int[] dims, byte[]target, byte[] cont, int vlen, int coord) 
   {
      if (coord == -1)
         return putTile(tile, dims, target, cont, vlen);
      int tile_size = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         tile_size *= tileDims[i];
      }
      if (cont.length != tile_size)
         return TILE_DIMS_MISMATCH;
      switch (dims.length)
      {
      case 3:
         for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0); 
                  i < dims[2] &&               ii < tileDims[2]; 
                  i++,                         ii++)
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                     j < dims[1] &&               jj < tileDims[1]; 
                     j++,                         jj ++)
               for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                        ks = (tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0),
                        kt = vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                        k < dims[0] &&                   kk < tileDims[0]; 
                        k++,                             kk++, ks ++, kt += vlen)
                     target[kt] = cont[ks];
                  
         break;
      case 2:
         for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                  j < dims[1] &&               jj < tileDims[1]; 
                  j++,                         jj ++)
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                     ks = jj * tileDims[0] + Math.max(-tile[0][0], 0),
                     kt = vlen * (j * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                     k < dims[0] &&                   kk < tileDims[0]; 
                     k++,                             kk++, ks ++, kt += vlen)
                  target[kt] = cont[ks];
         break;
      case 1:
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                  kt = vlen * (Math.max(tile[0][0], 0)) + coord; 
                  k < dims[0] &&                   kk < tileDims[0]; 
                  k++,                             kk++, kt += vlen)
                  target[kt] = cont[kk];
         break;
      }
      return TILE_OK;
   }  
   
   private static int putTile(int[][] tile, int[] dims, short[]target, short[] cont, int vlen) 
   {
      int k = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         k *= tileDims[i];
      }
      if (cont.length != vlen * k)
         return TILE_DIMS_MISMATCH;
      int nCopied = vlen * (Math.min(tile[0][1] + 1, dims[0]) - Math.max(tile[0][0], 0));
      switch (dims.length)
      {
      case 3:
         for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0); 
                  ii < tileDims[2] && i < dims[2]; 
                  i++, ii++)
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                     jj < tileDims[1] && j < dims[1]; j++, jj ++)
               System.arraycopy(
                     cont, vlen * ((tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0)), 
                     target, vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0)), nCopied);
         break;
      case 2:
         for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                  j < tile[1][1] && jj < dims[1]; j++, jj ++)
            System.arraycopy(
                     cont, vlen * (jj * tileDims[0] + Math.max(-tile[0][0], 0)), 
                     target, vlen * (j * dims[0] + Math.max(tile[0][0], 0)), nCopied);
         break;
      case 1:
            System.arraycopy(cont, 0, target, vlen * Math.max(tile[0][0], 0), nCopied);
         break;
      }
      return TILE_OK;
   }   
   
   private static int putTile(int[][] tile, int[] dims, short[]target, short[] cont, int vlen, int coord) 
   {
      if (coord == -1)
         return putTile(tile, dims, target, cont, vlen);
      int tile_size = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         tile_size *= tileDims[i];
      }
      if (cont.length != tile_size)
         return TILE_DIMS_MISMATCH;
      switch (dims.length)
      {
      case 3:
         for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0); 
                  i < dims[2] &&               ii < tileDims[2]; 
                  i++,                         ii++)
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                     j < dims[1] &&               jj < tileDims[1]; 
                     j++,                         jj ++)
               for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                        ks = (tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0),
                        kt = vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                        k < dims[0] &&                   kk < tileDims[0]; 
                        k++,                             kk++, ks ++, kt += vlen)
                     target[kt] = cont[ks];
                  
         break;
      case 2:
         for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                  j < dims[1] &&               jj < tileDims[1]; 
                  j++,                         jj ++)
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                     ks = jj * tileDims[0] + Math.max(-tile[0][0], 0),
                     kt = vlen * (j * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                     k < dims[0] &&                   kk < tileDims[0]; 
                     k++,                             kk++, ks ++, kt += vlen)
                  target[kt] = cont[kk];
         break;
      case 1:
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                  kt = vlen * (Math.max(tile[0][0], 0)) + coord; 
                  k < dims[0] &&                   kk < tileDims[0]; 
                  k++,                             kk++, kt += vlen)
         break;
      }
      return TILE_OK;
   }  
   
   private static int putTile(int[][] tile, int[] dims, int[]target, int[] cont, int vlen) 
   {
      int k = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         k *= tileDims[i];
      }
      if (cont.length != vlen * k)
         return TILE_DIMS_MISMATCH;
      int nCopied = vlen * (Math.min(tile[0][1] + 1, dims[0]) - Math.max(tile[0][0], 0));
      switch (dims.length)
      {
      case 3:
         for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0); 
                  ii < tileDims[2] && i < dims[2]; 
                  i++, ii++)
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                     jj < tileDims[1] && j < dims[1]; j++, jj ++)
               System.arraycopy(
                     cont, vlen * ((tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0)), 
                     target, vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0)), nCopied);
         break;
      case 2:
         for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                  j < tile[1][1] && jj < dims[1]; j++, jj ++)
            System.arraycopy(
                     cont, vlen * (jj * tileDims[0] + Math.max(-tile[0][0], 0)), 
                     target, vlen * (j * dims[0] + Math.max(tile[0][0], 0)), nCopied);
         break;
      case 1:
            System.arraycopy(cont, 0, target, vlen * Math.max(tile[0][0], 0), nCopied);
         break;
      }
      return TILE_OK;
   }   
   
   private static int putTile(int[][] tile, int[] dims, int[]target, int[] cont, int vlen, int coord) 
   {
      if (coord == -1)
         return putTile(tile, dims, target, cont, vlen);
      int tile_size = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         tile_size *= tileDims[i];
      }
      if (cont.length != tile_size)
         return TILE_DIMS_MISMATCH;
      switch (dims.length)
      {
      case 3:
         for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0); 
                  i < dims[2] &&               ii < tileDims[2]; 
                  i++,                         ii++)
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                     j < dims[1] &&               jj < tileDims[1]; 
                     j++,                         jj ++)
               for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                        ks = (tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0),
                        kt = vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                        k < dims[0] &&                   kk < tileDims[0]; 
                        k++,                             kk++, ks ++, kt += vlen)
                     target[kt] = cont[ks];
                  
         break;
      case 2:
         for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                  j < dims[1] &&               jj < tileDims[1]; 
                  j++,                         jj ++)
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                     ks = jj * tileDims[0] + Math.max(-tile[0][0], 0),
                     kt = vlen * (j * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                     k < dims[0] &&                   kk < tileDims[0]; 
                     k++,                             kk++, ks ++, kt += vlen)
                  target[kt] = cont[ks];
         break;
      case 1:
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                  kt = vlen * (Math.max(tile[0][0], 0)) + coord; 
                  k < dims[0] &&                   kk < tileDims[0]; 
                  k++,                             kk++, kt += vlen)
                  target[kt] = cont[kk];
         break;
      }
      return TILE_OK;
   }  
   private static int putTile(int[][] tile, int[] dims, float[]target, float[] cont, int vlen)
   {
      int k = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         k *= tileDims[i];
      }
      if (cont.length != vlen * k)
         return TILE_DIMS_MISMATCH;
      int nCopied = vlen * (Math.min(tile[0][1] + 1, dims[0]) - Math.max(tile[0][0], 0));
      switch (dims.length)
      {
      case 3:
         for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0); 
                  ii < tileDims[2] && i < dims[2]; 
                  i++, ii++)
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                     jj < tileDims[1] && j < dims[1]; j++, jj ++)
               System.arraycopy(
                     cont, vlen * ((tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0)), 
                     target, vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0)), nCopied);
         break;
      case 2:
         for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                  j < tile[1][1] && jj < dims[1]; j++, jj ++)
            System.arraycopy(
                     cont, vlen * (jj * tileDims[0] + Math.max(-tile[0][0], 0)), 
                     target, vlen * (j * dims[0] + Math.max(tile[0][0], 0)), nCopied);
         break;
      case 1:
            System.arraycopy(cont, 0, target, vlen * Math.max(tile[0][0], 0), nCopied);
         break;
      }
      return TILE_OK;
   }   
   
   private static int putTile(int[][] tile, int[] dims, float[]target, float[] cont, int vlen, int coord) 
   {
      if (coord == -1)
         return putTile(tile, dims, target, cont, vlen);
      int tile_size = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         tile_size *= tileDims[i];
      }
      if (cont.length != tile_size)
         return TILE_DIMS_MISMATCH;
      switch (dims.length)
      {
      case 3:
         for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0); 
                  i < dims[2] &&               ii < tileDims[2]; 
                  i++,                         ii++)
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                     j < dims[1] &&               jj < tileDims[1]; 
                     j++,                         jj ++)
               for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                        ks = (tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0),
                        kt = vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                        k < dims[0] &&                   kk < tileDims[0]; 
                        k++,                             kk++, ks ++, kt += vlen)
                     target[kt] = cont[ks];
                  
         break;
      case 2:
         for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                  j < dims[1] &&               jj < tileDims[1]; 
                  j++,                         jj ++)
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                     ks = jj * tileDims[0] + Math.max(-tile[0][0], 0),
                     kt = vlen * (j * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                     k < dims[0] &&                   kk < tileDims[0]; 
                     k++,                             kk++, ks ++, kt += vlen)
                  target[kt] = cont[ks];
         break;
      case 1:
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                  kt = vlen * (Math.max(tile[0][0], 0)) + coord; 
                  k < dims[0] &&                   kk < tileDims[0]; 
                  k++,                             kk++, kt += vlen)
                  target[kt] = cont[kk];
         break;
      }
      return TILE_OK;
   }  
   
   private static int putTile(int[][] tile, int[] dims, double[]target, double[] cont, int vlen) 
   {
      int k = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         k *= tileDims[i];
      }
      if (cont.length != vlen * k)
         return TILE_DIMS_MISMATCH;
      int nCopied = vlen * (Math.min(tile[0][1] + 1, dims[0]) - Math.max(tile[0][0], 0));
      switch (dims.length)
      {
      case 3:
         for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0); 
                  ii < tileDims[2] && i < dims[2]; 
                  i++, ii++)
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                     jj < tileDims[1] && j < dims[1]; j++, jj ++)
               System.arraycopy(
                     cont, vlen * ((tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0)), 
                     target, vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0)), nCopied);
         break;
      case 2:
         for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                  j < tile[1][1] && jj < dims[1]; j++, jj ++)
            System.arraycopy(
                     cont, vlen * (jj * tileDims[0] + Math.max(-tile[0][0], 0)), 
                     target, vlen * (j * dims[0] + Math.max(tile[0][0], 0)), nCopied);
         break;
      case 1:
            System.arraycopy(cont, 0, target, vlen * Math.max(tile[0][0], 0), nCopied);
         break;
      }
      return TILE_OK;
   }   
      
   private static int putTile(int[][] tile, int[] dims, double[]target, double[] cont, int vlen, int coord) 
   {
      if (coord == -1)
         return putTile(tile, dims, target, cont, vlen);
      int tile_size = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         tile_size *= tileDims[i];
      }
      if (cont.length != tile_size)
         return TILE_DIMS_MISMATCH;
      switch (dims.length)
      {
      case 3:
         for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0); 
                  i < dims[2] &&               ii < tileDims[2]; 
                  i++,                         ii++)
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                     j < dims[1] &&               jj < tileDims[1]; 
                     j++,                         jj ++)
               for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                        ks = (tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0),
                        kt = vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                        k < dims[0] &&                   kk < tileDims[0]; 
                        k++,                             kk++, ks ++, kt += vlen)
                     target[kt] = cont[ks];
                  
         break;
      case 2:
         for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                  j < dims[1] &&               jj < tileDims[1]; 
                  j++,                         jj ++)
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                     ks = jj * tileDims[0] + Math.max(-tile[0][0], 0),
                     kt = vlen * (j * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                     k < dims[0] &&                   kk < tileDims[0]; 
                     k++,                             kk++, ks ++, kt += vlen)
                  target[kt] = cont[ks];
         break;
      case 1:
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                  kt = vlen * (Math.max(tile[0][0], 0)) + coord; 
                  k < dims[0] &&                   kk < tileDims[0]; 
                  k++,                             kk++, kt += vlen)
                  target[kt] = cont[kk];
         break;
      }
      return TILE_OK;
   }  
   private static int putTile(int[][] tile, int[] dims, String[]target, String[] cont, int vlen) 
   {
      int k = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         k *= tileDims[i];
      }
      if (cont.length != vlen * k)
         return TILE_DIMS_MISMATCH;
      int nCopied = vlen * (Math.min(tile[0][1] + 1, dims[0]) - Math.max(tile[0][0], 0));
      switch (dims.length)
      {
      case 3:
         for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0); 
                  ii < tileDims[2] && i < dims[2]; 
                  i++, ii++)
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                     jj < tileDims[1] && j < dims[1]; j++, jj ++)
               System.arraycopy(
                     cont, vlen * ((tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0)), 
                     target, vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0)), nCopied);
         break;
      case 2:
         for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                  j < tile[1][1] && jj < dims[1]; j++, jj ++)
            System.arraycopy(
                     cont, vlen * (jj * tileDims[0] + Math.max(-tile[0][0], 0)), 
                     target, vlen * (j * dims[0] + Math.max(tile[0][0], 0)), nCopied);
         break;
      case 1:
            System.arraycopy(cont, 0, target, vlen * Math.max(tile[0][0], 0), nCopied);
         break;
      }
      return TILE_OK;
   }   
   
   private static int putTile(int[][] tile, int[] dims, String[]target, String[] cont, int vlen, int coord) 
   {
      if (coord == -1)
         return putTile(tile, dims, target, cont, vlen);
      int tile_size = 1;
      int[] tileDims = new int[dims.length];
      for (int i = 0; i < dims.length; i++)
      {
         if (tile[i].length != 2 || tile[i][0] > tile[i][1])
            return TILE_DIMS_MISMATCH;
         tileDims[i] = tile[i][1] - tile[i][0] + 1;
         tile_size *= tileDims[i];
      }
      if (cont.length != tile_size)
         return TILE_DIMS_MISMATCH;
      switch (dims.length)
      {
      case 3:
         for (int i = Math.max(tile[2][0], 0), ii = Math.max(-tile[2][0], 0); 
                  i < dims[2] &&               ii < tileDims[2]; 
                  i++,                         ii++)
            for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                     j < dims[1] &&               jj < tileDims[1]; 
                     j++,                         jj ++)
               for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                        ks = (tileDims[1] * ii + jj) * tileDims[0] + Math.max(-tile[0][0], 0),
                        kt = vlen * ((dims[1] * i + j) * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                        k < dims[0] &&                   kk < tileDims[0]; 
                        k++,                             kk++, ks ++, kt += vlen)
                     target[kt] = cont[ks];
                  
         break;
      case 2:
         for (int j = Math.max(tile[1][0], 0), jj = Math.max(-tile[1][0], 0);
                  j < dims[1] &&               jj < tileDims[1]; 
                  j++,                         jj ++)
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                     ks = jj * tileDims[0] + Math.max(-tile[0][0], 0),
                     kt = vlen * (j * dims[0] + Math.max(tile[0][0], 0)) +coord; 
                     k < dims[0] &&                   kk < tileDims[0]; 
                     k++,                             kk++, ks ++, kt += vlen)
                  target[kt] = cont[ks];
         break;
      case 1:
            for (int k = Math.max(tile[0][0], 0), kk = Math.max(-tile[0][0], 0),
                  kt = vlen * (Math.max(tile[0][0], 0)) + coord; 
                  k < dims[0] &&                   kk < tileDims[0]; 
                  k++,                             kk++, kt += vlen)
                  target[kt] = cont[kk];
         break;
      }
      return TILE_OK;
   }  
}
