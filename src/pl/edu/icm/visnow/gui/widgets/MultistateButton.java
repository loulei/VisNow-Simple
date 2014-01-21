//<editor-fold defaultstate="collapsed" desc=" COPYRIGHT AND LICENSE ">
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
//</editor-fold>

package pl.edu.icm.visnow.gui.widgets;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventListener;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ButtonUI;
import pl.edu.icm.visnow.gui.icons.IconsContainer;

public class MultistateButton extends AbstractButton
{

   /**
    * @see #getUIClassID
    * @see #readObject
    */
   private static final String uiClassID = "ToggleButtonUI";
   private static final Icon[] defaultIcons = new Icon[]
   {
      new ImageIcon(IconsContainer.getBallGray()),
      new ImageIcon(IconsContainer.getBallBlue1()),
      new ImageIcon(IconsContainer.getBallLight())
   };
   private static final String[] defaultTexts = new String[]
   {
      "", "", ""
   };
   private MultistateModel model;
   private Icon[] icons = defaultIcons;
   private String[] texts = defaultTexts;

   /**
    * Creates an initially unselected toggle button without setting the text or
    * image.
    */
   public MultistateButton()
   {
      this(defaultTexts, defaultIcons, 0);
   }

   /**
    * Creates an initially unselected toggle button with the specified image but
    * no text.
    *
    * @param icon the image that the button should display
    */
   public MultistateButton(Icon[] icons)
   {
      this(defaultTexts, icons, 0);
   }

   /**
    * Creates a multistate button with the specified image and selection state,
    * but no text.
    *
    * @param icons the images that the button should display (its length will be
    * used as the number of states)
    * @param state initial state of the button
    */
   public MultistateButton(Icon[] icons, int state)
   {
      this(defaultTexts, icons, state);
   }

   /**
    * Creates a button with the specified texts, no icons and 0 as selected
    * state.
    *
    * @param texts the strings displayed on the button
    */
   public MultistateButton(String[] texts)
   {
      this(texts, defaultIcons, 0);
   }

   /**
    * Creates a toggle button with the specified texts and initial state.
    *
    * @param texts the strings displayed on the toggle button (its length will be
    * used as the number of states)
    * @param state initial state of the button
    */
   public MultistateButton(String[] texts, int state)
   {
      this(texts, defaultIcons, state);
   }

   /**
    * Creates a toggle button that has the specified texts and images, and 0 as
    * selected state.
    *
    * @param texts the strings displayed on the button
    * @param icons the image that the button should display (lengths of texts and
    * icons should be equal)
    */
   public MultistateButton(String[] texts, Icon[] icons)
   {
      this(texts, icons, 0);
   }

   /**
    * Creates a toggle button with the specified text, image, and initial state.
    *
    * @param texts the texts of the toggle button
    * @param icons the images that the button should display
    * @param state initial state of the button
    */
   public MultistateButton(String[] texts, Icon[] icons, int state)
   {
      // Create the model
      init(texts, icons, state);
   }

   public void init(String[] texts, Icon[] icons, int state)
   {
      int nStates = texts.length;
      if (icons != null && nStates > icons.length)
         nStates = icons.length;
      if (state >= nStates)
         state = 0;
      model = new MultistateModel(nStates, state);
      setModel(model);
      this.texts = texts;
      setText(texts[model.getState()]);
      this.icons = icons;
      if (icons != null)
         setIcon(icons[model.getState()]);
      else
         setIcon(null);
      updateUI();
      setAlignmentX(LEFT_ALIGNMENT);
      setAlignmentY(CENTER_ALIGNMENT);
   }

   public void setInit(String[] texts, Icon[] icons)
   {
      init(texts, icons, 0);
   }

   public void setTexts(String[] texts)
   {
      if (icons != null && icons.length != texts.length)
         icons = new Icon[texts.length];
      init(texts, icons, 0);
   }

   public void setIcons(Icon[] icons)
   {
      if (icons != null && icons.length != texts.length)
         texts = new String[icons.length];
      init(texts, icons, 0);
   }

   public void setState(int state)
   {
      model.setState(state);
   }

   @Override
   public Icon getIcon()
   {
      if (icons == null)
         return null;

      if (model == null)
         return defaultIcons[0];
      return icons[model.getState()];
   }

   @Override
   public String getText()
   {
      if (model == null)
         return defaultTexts[0];
      return texts[model.getState()];
   }

   /**
    * Resets the UI property to a value from the current look and feel.
    *
    * @see JComponent#updateUI
    */
   @Override
   public void updateUI()
   {
      setUI((ButtonUI) UIManager.getUI(this));
   }

   /**
    * Returns a string that specifies the name of the l&f class that renders
    * this component.
    *
    * @return String "ToggleButtonUI"
    * @see JComponent#getUIClassID
    * @see UIDefaults#getUI
    * @beaninfo description: A string that specifies the name of the L&F class
    */
   @Override
   public String getUIClassID()
   {
      return uiClassID;
   }

   /**
    * Overriden to return true, MultistateButton supports the selected state.
    */
   boolean shouldUpdateSelectedStateFromAction()
   {
      return true;
   }

   class MultistateModel implements ButtonModel, Serializable
   {

      protected int nStates = 3;
      protected int state = 0;
      /**
       * The bitmask used to store the state of the button.
       */
      protected int stateMask = 0;
      /**
       * The action command string fired by the button.
       */
      protected String actionCommand = null;
      /**
       * The button group that the button belongs to.
       */
      protected ButtonGroup group = null;
      /**
       * The button's mnemonic.
       */
      protected int mnemonic = 0;
      /**
       * Only one
       * <code>ChangeEvent</code> is needed per button model instance since the
       * event's only state is the source property. The source of events
       * generated is always "this".
       */
      protected transient ChangeEvent changeEvent = null;
      /**
       * Stores the listeners on this model.
       */
      protected EventListenerList listenerList = new EventListenerList();
      // controls the usage of the MenuItem.disabledAreNavigable UIDefaults
      // property in the setArmed() method
      private boolean menuItem = false;

      /**
       * Constructs a
       * <code>MultistateModel</code> setting maximum allowed number of states
       * to
       * <code>nStates</code>
       *
       */
      public MultistateModel(int nStates, int state)
      {
         this.nStates = nStates;
         stateMask = 0;
         this.state = state;
         setEnabled(true);
      }

      public MultistateModel(int nStates)
      {
         this(nStates, 0);
      }

      /**
       * Constructs a tristate model:
       * <code>MultistateModel</code> with 3 allowed states
       *
       */
      public MultistateModel()
      {
         this(3);
      }
      /**
       * Identifies the "armed" bit in the bitmask, which indicates partial
       * commitment towards choosing/triggering the button.
       */
      public final static int ARMED = 1 << 0;
      /**
       * Identifies the state value
       */
      public final static int SELECTED = 1 << 1;
      /**
       * Identifies the "pressed" bit in the bitmask, which indicates that the
       * button is pressed.
       */
      public final static int PRESSED = 1 << 2;
      /**
       * Identifies the "enabled" bit in the bitmask, which indicates that the
       * button can be selected by an input device (such as a mouse pointer).
       */
      public final static int ENABLED = 1 << 3;
      /**
       * Identifies the "rollover" bit in the bitmask, which indicates that the
       * mouse is over the button.
       */
      public final static int ROLLOVER = 1 << 4;

      /**
       * {
       *
       * @inheritDoc}
       */
      public void setActionCommand(String actionCommand)
      {
         this.actionCommand = actionCommand;
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public String getActionCommand()
      {
         return actionCommand;
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public boolean isArmed()
      {
         return (stateMask & ARMED) != 0;
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public boolean isEnabled()
      {
         return (stateMask & ENABLED) != 0;
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public boolean isPressed()
      {
         return (stateMask & PRESSED) != 0;
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public boolean isRollover()
      {
         return (stateMask & ROLLOVER) != 0;
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public void setArmed(boolean b)
      {
         if (isMenuItem()
                 && UIManager.getBoolean("MenuItem.disabledAreNavigable"))
         {
            if ((isArmed() == b))
               return;
         } else if ((isArmed() == b) || !isEnabled())
            return;

         if (b)
            stateMask |= ARMED;
         else
            stateMask &= ~ARMED;
         fireStateChanged();
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public void setEnabled(boolean b)
      {
         if (isEnabled() == b)
            return;
         if (b)
            stateMask |= ENABLED;
         else
         {
            stateMask &= ~ENABLED;
            // unarm and unpress, just in case
            stateMask &= ~ARMED;
            stateMask &= ~PRESSED;
         }
         fireStateChanged();
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public void setRollover(boolean b)
      {
         if ((isRollover() == b) || !isEnabled())
            return;
         if (b)
            stateMask |= ROLLOVER;
         else
            stateMask &= ~ROLLOVER;
         fireStateChanged();
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public void setMnemonic(int key)
      {
         mnemonic = key;
         fireStateChanged();
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public int getMnemonic()
      {
         return mnemonic;
      }

      public int getnStates()
      {
         return nStates;
      }

      public void setnStates(int nStates)
      {
         this.nStates = nStates;
      }

      public int getState()
      {
         return state;
      }

      public void setState(int state)
      {
         if (state < 0)
            this.state = nStates - 1;
         else if (state >= nStates)
            this.state = 0;
         else
            this.state = state;
         fireStateChanged();
      }

      public void stateUp()
      {
         state = (state + 1) % nStates;
         fireStateChanged();
      }

      public void stateDown()
      {
         state = state - 1;
         if (state < 0)
            state = nStates - 1;
         fireStateChanged();
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public void addChangeListener(ChangeListener l)
      {
         listenerList.add(ChangeListener.class, l);
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public void removeChangeListener(ChangeListener l)
      {
         listenerList.remove(ChangeListener.class, l);
      }

      /**
       * Returns an array of all the change listeners registered on this
       * <code>MultistateModel</code>.
       *
       * @return all of this model's <code>ChangeListener</code>s or an empty
       * array if no change listeners are currently registered
       *
       * @see #addChangeListener
       * @see #removeChangeListener
       *
       * @since 1.4
       */
      public ChangeListener[] getChangeListeners()
      {
         return (ChangeListener[]) listenerList.getListeners(
                 ChangeListener.class);
      }

      /**
       * Notifies all listeners that have registered interest for notification
       * on this event type. The event instance is created lazily.
       *
       * @see EventListenerList
       */
      protected void fireStateChanged()
      {
         // Guaranteed to return a non-null array
         Object[] listeners = listenerList.getListenerList();
         // Process the listeners last to first, notifying
         // those that are interested in this event
         for (int i = listeners.length - 2; i >= 0; i -= 2)
         {
            if (listeners[i] == ChangeListener.class)
            {
               // Lazily create the event:
               if (changeEvent == null)
                  changeEvent = new ChangeEvent(this);
               ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
         }
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public void addActionListener(ActionListener l)
      {
         listenerList.add(ActionListener.class, l);
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public void removeActionListener(ActionListener l)
      {
         listenerList.remove(ActionListener.class, l);
      }

      /**
       * Returns an array of all the action listeners registered on this
       * <code>MultistateModel</code>.
       *
       * @return all of this model's <code>ActionListener</code>s or an empty
       * array if no action listeners are currently registered
       *
       * @see #addActionListener
       * @see #removeActionListener
       *
       * @since 1.4
       */
      public ActionListener[] getActionListeners()
      {
         return (ActionListener[]) listenerList.getListeners(
                 ActionListener.class);
      }

      /**
       * Notifies all listeners that have registered interest for notification
       * on this event type.
       *
       * @param e the <code>ActionEvent</code> to deliver to listeners
       * @see EventListenerList
       */
      protected void fireActionPerformed(ActionEvent e)
      {
         // Guaranteed to return a non-null array
         Object[] listeners = listenerList.getListenerList();
         // Process the listeners last to first, notifying
         // those that are interested in this event
         for (int i = listeners.length - 2; i >= 0; i -= 2)
         {
            if (listeners[i] == ActionListener.class)
            {
               // Lazily create the event:
               // if (changeEvent == null)
               // changeEvent = new ChangeEvent(this);
               ((ActionListener) listeners[i + 1]).actionPerformed(e);
            }
         }
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public void addItemListener(ItemListener l)
      {
         listenerList.add(ItemListener.class, l);
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public void removeItemListener(ItemListener l)
      {
         listenerList.remove(ItemListener.class, l);
      }

      /**
       * Returns an array of all the item listeners registered on this
       * <code>MultistateModel</code>.
       *
       * @return all of this model's <code>ItemListener</code>s or an empty
       * array if no item listeners are currently registered
       *
       * @see #addItemListener
       * @see #removeItemListener
       *
       * @since 1.4
       */
      public ItemListener[] getItemListeners()
      {
         return (ItemListener[]) listenerList.getListeners(ItemListener.class);
      }

      /**
       * Notifies all listeners that have registered interest for notification
       * on this event type.
       *
       * @param e the <code>ItemEvent</code> to deliver to listeners
       * @see EventListenerList
       */
      protected void fireItemStateChanged(ItemEvent e)
      {
         // Guaranteed to return a non-null array
         Object[] listeners = listenerList.getListenerList();
         // Process the listeners last to first, notifying
         // those that are interested in this event
         for (int i = listeners.length - 2; i >= 0; i -= 2)
         {
            if (listeners[i] == ItemListener.class)
            {
               // Lazily create the event:
               // if (changeEvent == null)
               // changeEvent = new ChangeEvent(this);
               ((ItemListener) listeners[i + 1]).itemStateChanged(e);
            }
         }
      }

      /**
       * Returns an array of all the objects currently registered as
       * <code><em>Foo</em>Listener</code>s upon this model.
       * <code><em>Foo</em>Listener</code>s are registered using the
       * <code>add<em>Foo</em>Listener</code> method. <p> You can specify the
       * <code>listenerType</code> argument with a class literal, such as
       * <code><em>Foo</em>Listener.class</code>. For example, you can query a
       * <code>MultistateModel</code> instance
       * <code>m</code> for its action listeners with the following code:
       *
       * <pre>ActionListener[] als = (ActionListener[])(m.getListeners(ActionListener.class));</pre>
       *
       * If no such listeners exist, this method returns an empty array.
       *
       * @param listenerType the type of listeners requested; this parameter
       * should specify an interface that descends        * from <code>java.util.EventListener</code>
       * @return an array of all objects registered as
       * <code><em>Foo</em>Listener</code>s on this model, or an empty array if
       * no such listeners have been added
       * @exception ClassCastException if <code>listenerType</code> doesn't
       * specify a class or interface that implements
       * <code>java.util.EventListener</code>
       *
       * @see #getActionListeners
       * @see #getChangeListeners
       * @see #getItemListeners
       *
       * @since 1.3
       */
      public <T extends EventListener> T[] getListeners(Class<T> listenerType)
      {
         return listenerList.getListeners(listenerType);
      }

      /**
       * Overridden to return
       * <code>null</code>.
       */
      public Object[] getSelectedObjects()
      {
         return null;
      }

      /**
       * {
       *
       * @inheritDoc}
       */
      public void setGroup(ButtonGroup group)
      {
         this.group = group;
      }

      /**
       * Returns the group that the button belongs to. Normally used with radio
       * buttons, which are mutually exclusive within their group.
       *
       * @return the <code>ButtonGroup</code> that the button belongs to
       *
       * @since 1.3
       */
      public ButtonGroup getGroup()
      {
         return group;
      }

      boolean isMenuItem()
      {
         return menuItem;
      }

      void setMenuItem(boolean menuItem)
      {
         this.menuItem = menuItem;
      }

      public boolean isSelected()
      {
//              if(getGroup() != null) {
//                  return getGroup().isSelected(this);
//              } else {
         return (stateMask & SELECTED) != 0;
//              }
      }

      /**
       * Sets the selected state of the button.
       *
       * @param b true selects the toggle button, false deselects the toggle
       * button.
       */
      public void setSelected(boolean b)
      {
         ButtonGroup group = getGroup();
         if (group != null)
         {
            // use the group model instead
            group.setSelected(this, b);
            b = group.isSelected(this);
         }

         if (isSelected() == b)
            return;
         if (b)
            stateMask |= SELECTED;
         else
            stateMask &= ~SELECTED;

         // Send ChangeEvent
         fireStateChanged();

         // Send ItemEvent
         fireItemStateChanged(
                 new ItemEvent(this,
                 ItemEvent.ITEM_STATE_CHANGED,
                 this,
                 this.isSelected() ? ItemEvent.SELECTED : ItemEvent.DESELECTED));

      }

      /**
       * Sets the pressed state of the toggle button.
       */
      public void setPressed(boolean b)
      {
         if ((isPressed() == b) || !isEnabled())
            return;

         if (!b && isArmed())
            setSelected(!this.isSelected());

         if (b)
            stateMask |= PRESSED;
         else
            stateMask &= ~PRESSED;

         fireStateChanged();

         if (!isPressed() && isArmed())
         {
            int modifiers = 0;
            AWTEvent currentEvent = EventQueue.getCurrentEvent();
            if (currentEvent instanceof InputEvent)
            {
               modifiers = ((InputEvent) currentEvent).getModifiers();
            } else if (currentEvent instanceof ActionEvent)
            {
               modifiers = ((ActionEvent) currentEvent).getModifiers();
            }
            fireActionPerformed(
                    new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                    getActionCommand(),
                    EventQueue.getMostRecentEventTime(),
                    modifiers));
         }
      }
   }

   @Override
   protected void processMouseEvent(MouseEvent e)
    {
        if (super.isEnabled()) {
            if (e.getID() != MouseEvent.MOUSE_CLICKED)
                return;
            if (e.getButton() == MouseEvent.BUTTON1) model.stateUp();
            else if (e.getButton() == MouseEvent.BUTTON3) model.stateDown();
            else fireStateChanged();
            repaint();            
        }
   }

   public int getState()
   {
      return model.getState();
   }

   /**
    * Returns a string representation of this MultistateButton. This method is
    * intended to be used only for debugging purposes, and the content and
    * format of the returned string may vary between implementations. The
    * returned string may be empty but may not be
    * <code>null</code>.
    *
    * @return a string representation of this MultistateButton.
    */
   protected String paramString()
   {
      return super.paramString();
   }

/////////////////
// Accessibility support
////////////////
   /**
    * Gets the AccessibleContext associated with this MultistateButton. For
    * toggle buttons, the AccessibleContext takes the form of an
    * AccessibleMultistateButton. A new AccessibleMultistateButton instance is
    * created if necessary.
    *
    * @return an AccessibleMultistateButton that serves as the AccessibleContext
    * of this MultistateButton
    * @beaninfo expert: true description: The AccessibleContext associated with
    * this ToggleButton.
    */
   public AccessibleContext getAccessibleContext()
   {
      if (accessibleContext == null)
      {
         accessibleContext = new AccessibleMultistateButton();
      }
      return accessibleContext;
   }

   /**
    * This class implements accessibility support for the
    * <code>MultistateButton</code> class. It provides an implementation of the
    * Java Accessibility API appropriate to toggle button user-interface
    * elements. <p> <strong>Warning:</strong> Serialized objects of this class
    * will not be compatible with future Swing releases. The current
    * serialization support is appropriate for short term storage or RMI between
    * applications running the same version of Swing. As of 1.4, support for
    * long term storage of all JavaBeans<sup><font size="-2">TM</font></sup> has
    * been added to the
    * <code>java.beans</code> package. Please see {@link java.beans.XMLEncoder}.
    */
   protected class AccessibleMultistateButton extends AccessibleAbstractButton
           implements ItemListener
   {

      public AccessibleMultistateButton()
      {
         super();
         MultistateButton.this.addItemListener(this);
      }

      /**
       * Fire accessible property change events when the state of the toggle
       * button changes.
       */
      public void itemStateChanged(ItemEvent e)
      {
         MultistateButton tb = (MultistateButton) e.getSource();
         if (MultistateButton.this.accessibleContext != null)
         {
            if (tb.isSelected())
            {
               MultistateButton.this.accessibleContext.firePropertyChange(
                       AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                       null, AccessibleState.CHECKED);
            } else
            {
               MultistateButton.this.accessibleContext.firePropertyChange(
                       AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                       AccessibleState.CHECKED, null);
            }
         }
      }

      /**
       * Get the role of this object.
       *
       * @return an instance of AccessibleRole describing the role of the object
       */
      public AccessibleRole getAccessibleRole()
      {
         return AccessibleRole.TOGGLE_BUTTON;
      }
   } // inner class AccessibleMultistateButton
//   @Override
//   public void addChangeListener(javax.swing.event.ChangeListener listener)
//   {
//      model.addChangeListener(listener);
//   }
//   
   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    *
    * @param listener The listener to register.
    */
   @Override
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    *
    * @param listener The listener to remove.
    */
   @Override
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      if (changeListenerList != null)
      {
         changeListenerList.remove(listener);
      }
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   @Override
   protected void fireStateChanged()
   {
      ChangeEvent e = new ChangeEvent(this);
      synchronized (this)
      {
         if (changeListenerList == null)
            return;
         for (int i = 0; i < changeListenerList.size(); i++)
         {
            ((ChangeListener) changeListenerList.get(i)).stateChanged(e);
         }
      }
   }
}
