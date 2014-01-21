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
package pl.edu.icm.visnow.geometries.objects.generics;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.TransparencyAttributes;
import pl.edu.icm.visnow.geometries.viewer3d.Display3DPanel;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class OpenBranchGroup extends BranchGroup {

    /**
     * Creates a new instance of OpenBranchGroup
     */
    public OpenBranchGroup() {
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(BranchGroup.ALLOW_LOCAL_TO_VWORLD_READ);
    }

    public OpenBranchGroup(String name) {
        super.setName(name);
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(BranchGroup.ALLOW_LOCAL_TO_VWORLD_READ);
    }

    public void printDebugInfo() {
        System.out.println(getName() + ": " + this);
        int n = this.numChildren();
        System.out.println("group: " + n);
        for (int i = 0; i < n; i++) {
            System.out.println("obj " + i);
            Node child = this.getChild(i);
            if (child instanceof OpenBranchGroup) {
                ((OpenBranchGroup) child).printDebugInfo();
            }
            if (child instanceof OpenBranchGroup) {
                ((OpenBranchGroup) child).printDebugInfo();
            }
        }
    }

    @Override
    public Node cloneNode(boolean forceDuplicate) {
        OpenBranchGroup openBranchGroup = new OpenBranchGroup();
        openBranchGroup.duplicateNode(this, forceDuplicate);
        return openBranchGroup;
    }
    private Node postparent = null;

    public boolean postdetach() {
        //System.out.println("object "+getName()+" postdetach @"+System.currentTimeMillis());
        if (this.postparent != null) {
            return false;
        }

        if (!isNodeAttached(this)) {
            return false;
        }

        if (this.getParent() == null) {
            return false;
        }

        if (myViewer != null && !myViewer.isStoringFrames()) {
            return false;
        }

        if (myViewer != null && myViewer.isWaitingForExternalTrigger()) {
            return false;
        }

        if (myViewer != null) {
            myViewer.setPostRenderSilent(true);
        }

        this.postparent = this.getParent();
        if (postparent != null)
            this.detach();
        return true;
    }

    public void postattach() {
        if (this.postparent == null) {
            return;
        }

        //System.out.println("object "+getName()+" postattach @"+System.currentTimeMillis());
        if (myViewer != null) {
            myViewer.setPostRenderSilent(false);
        }

        if (postparent instanceof OpenBranchGroup) {
            ((OpenBranchGroup) postparent).addChild(this);
        } else if (postparent instanceof OpenTransformGroup) {
            ((OpenTransformGroup) postparent).addChild(this);
        }
        this.postparent = null;
    }
    protected Display3DPanel myViewer = null;

    public void setCurrentViewer(Display3DPanel panel) {
        this.myViewer = panel;
//        if (panel != null) {
//            System.out.println("object " + getName() + " set viewer to " + panel.getName());
//        } else {
//            System.out.println("object " + getName() + " set viewer to NULL");
//        }


        for (int i = 0; i < this.numChildren(); i++) {
            Node n = this.getChild(i);
            if (n instanceof OpenBranchGroup) {
                ((OpenBranchGroup) n).setCurrentViewer(panel);
            } else if (n instanceof OpenTransformGroup) {
                ((OpenTransformGroup) n).setCurrentViewer(panel);
            }
        }
    }

    public Display3DPanel getCurrentViewer() {
        return myViewer;
    }

    public static boolean isNodeAttached(Node n) {
        if (n == null)
            return false;

        if ("root_object".equals(n.getName()))
            return true;

        return isNodeAttached(n.getParent());
    }

    /**
     * Factory for OpenAppearance. Creates appearance that have lighting set and transparency (if
     * <code>transparent</code> is true)
     *
     * @return a new Appearance object
     */
    public static OpenAppearance createAppearance(boolean transparent) {

        OpenAppearance a = new OpenAppearance();
        a.getMaterial().setLightingEnable(true);
        if (transparent)
            a.getTransparencyAttributes().setTransparencyMode(TransparencyAttributes.NICEST);
        return a;

    }
}
