/************************************************************************************************************
 * Copyright (c) 2008 Whizzo Software, LLC. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Whizzo Software, LLC (initial design and implementation)
 ***********************************************************************************************************/
package com.whizzosoftware.swt.imagechooser.util;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ImageUtil {
    static public Image getBrowseIcon(Display display) {
        return new Image(display, new ImageData(ImageUtil.class.getClassLoader().getResourceAsStream("icons/browse.png")));
    }

    static public Image getCaptureIcon(Display display) {
        return new Image(display, new ImageData(ImageUtil.class.getClassLoader().getResourceAsStream("icons/capture.png")));
    }

    static public Image getImportIcon(Display display) {
        return new Image(display, new ImageData(ImageUtil.class.getClassLoader().getResourceAsStream("icons/import.png")));
    }

    static public Image getFlipIcon(Display display) {
        return new Image(display, new ImageData(ImageUtil.class.getClassLoader().getResourceAsStream("icons/flip.png")));
    }

    static public Image getRotateIcon(Display display) {
        return new Image(display, new ImageData(ImageUtil.class.getClassLoader().getResourceAsStream("icons/rotate.png")));
    }

    static public Image getCropIcon(Display display) {
        return new Image(display, new ImageData(ImageUtil.class.getClassLoader().getResourceAsStream("icons/crop.png")));
    }

    static public Image getClearIcon(Display display) {
        return new Image(display, new ImageData(ImageUtil.class.getClassLoader().getResourceAsStream("icons/clear.png")));
    }
}