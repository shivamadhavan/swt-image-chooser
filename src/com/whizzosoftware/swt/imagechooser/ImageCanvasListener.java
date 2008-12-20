/************************************************************************************************************
 * Copyright (c) 2008 Whizzo Software, LLC. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Whizzo Software, LLC (initial design and implementation)
 ***********************************************************************************************************/
package com.whizzosoftware.swt.imagechooser;

/**
 * Interface for objects that want to listen for ImageCanvas events.
 *
 * @author Dan Noguerol
 */
public interface ImageCanvasListener {
    /**
     * Invoked when the image changes.
     *
     * @param canvas the ImageCanvas for which the image changed
     */
    public void imageChanged(ImageCanvas canvas);

    /**
     * Invoked when a drag tracker starts.
     */
    public void trackerStarted();

    /**
     * Invoked when a drag tracker stops.
     */
    public void trackerStopped();
}
