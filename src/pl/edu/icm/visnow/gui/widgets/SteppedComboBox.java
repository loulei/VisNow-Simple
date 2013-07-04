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

package pl.edu.icm.visnow.gui.widgets;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author Nobuo Tamemasa
 * @version 1.0 12/12/98
 */
public class SteppedComboBox extends JComboBox implements ComponentListener
{

   protected int popupWidth;

   public SteppedComboBox()
   {
      this(new String[]
              {
              });
      addComponentListener(this);
   }

   @SuppressWarnings("unchecked")
   public SteppedComboBox(ComboBoxModel aModel)
   {
      super(aModel);
      setUI(new SteppedComboBoxUI());
      updatePopupWidth();
      addComponentListener(this);
   }

   public SteppedComboBox(final Object[] items)
   {
      super(items);
      setUI(new SteppedComboBoxUI());
      updatePopupWidth();
      addComponentListener(this);
   }

   public SteppedComboBox(Vector items)
   {
      super(items);
      setUI(new SteppedComboBoxUI());
      updatePopupWidth();
      addComponentListener(this);
   }

   public void setPopupWidth(int width)
   {
      popupWidth = width;
   }

   private void updatePopupWidth()
   {

      SwingInstancer.swingRun(new Runnable()
      {
         public void run()
         {

            if (SteppedComboBox.this.getRenderer() != null && 
                SteppedComboBox.this.getModel() != null && 
                (SteppedComboBox.this.getUI() instanceof SteppedComboBoxUI))
            {

               JList list = ((SteppedComboBoxUI) SteppedComboBox.this.getUI()).createPopup().getList();

               popupWidth = 10;
               for (int i = 0; i < SteppedComboBox.this.getModel().getSize(); i++)
               {
                  int w = SteppedComboBox.this.getRenderer().getListCellRendererComponent(
                          list, SteppedComboBox.this.getModel().getElementAt(i), 
                          i, true, true).getPreferredSize().width;
                  if (w > popupWidth)
                  {
                     popupWidth = w;
                  }
               }
               popupWidth += 25;

            } else
            {

               if (SteppedComboBox.this.getFont() == null)
               {
                  return;
               }
               FontMetrics fm = SteppedComboBox.this.getFontMetrics(SteppedComboBox.this.getFont());
               if (fm == null)
               {
                  return;
               }
               popupWidth = 10;
               for (int i = 0; i < SteppedComboBox.this.getModel().getSize(); i++)
               {
                  int w = 0;
                  if (SteppedComboBox.this.getModel() != null && SteppedComboBox.this.getModel().getElementAt(i) != null)
                  {
                     w = fm.stringWidth(SteppedComboBox.this.getModel().getElementAt(i).toString());
                  }
                  if (w > popupWidth)
                  {
                     popupWidth = w;
                  }
               }
               popupWidth += 80;

            }

            if (popupWidth < getWidth())
            {
               popupWidth = getWidth();
            }

         }
      });

   }

   public Dimension getPopupSize()
   {
      Dimension size = getSize();
      if (popupWidth < 1)
      {
         popupWidth = size.width;
      }
      return new Dimension(popupWidth, size.height);
   }

   @Override
   public void setModel(ComboBoxModel aModel)
   {
      super.setModel(aModel);
      updatePopupWidth();
   }

   public void componentResized(ComponentEvent e)
   {
      updatePopupWidth();
   }

   public void componentMoved(ComponentEvent e)
   {
   }

   public void componentShown(ComponentEvent e)
   {
   }

   public void componentHidden(ComponentEvent e)
   {
   }

   @Override
   public void setRenderer(ListCellRenderer aRenderer)
   {
      super.setRenderer(aRenderer);
      updatePopupWidth();
   }
}

/**
 * @author Nobuo Tamemasa
 * @version 1.0 12/12/98
 */
class SteppedComboBoxUI extends MetalComboBoxUI
{

   protected ComboPopup createPopup()
   {
      BasicComboPopup popup = new BasicComboPopup(comboBox)
      {
         public void show()
         {
            Dimension popupSize = ((SteppedComboBox) comboBox).getPopupSize();
            popupSize.setSize(popupSize.width,
                    getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
            Rectangle popupBounds = computePopupBounds(0,
                    comboBox.getBounds().height, popupSize.width, popupSize.height);
            scroller.setMaximumSize(popupBounds.getSize());
            scroller.setPreferredSize(popupBounds.getSize());
            scroller.setMinimumSize(popupBounds.getSize());
            list.invalidate();
            int selectedIndex = comboBox.getSelectedIndex();
            if (selectedIndex == -1)
            {
               list.clearSelection();
            } else
            {
               list.setSelectedIndex(selectedIndex);
            }
            list.ensureIndexIsVisible(list.getSelectedIndex());
            setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());

            show(comboBox, popupBounds.x, popupBounds.y);
         }
      };
      popup.getAccessibleContext().setAccessibleParent(comboBox);
      return popup;
   }
}
