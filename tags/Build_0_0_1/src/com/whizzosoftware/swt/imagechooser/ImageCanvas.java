/************************************************************************************************************
 * Copyright (c) 2008 Whizzo Software, LLC. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Whizzo Software, LLC (initial design and implementation)
 ***********************************************************************************************************/
package com.whizzosoftware.swt.imagechooser;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.SWT;

/**
 * Canvas that displays an image and allows its manipulation (e.g. rotate, crop, flip, etc.)
 *
 * @author Dan Noguerol
 */
public class ImageCanvas extends Canvas implements Listener {
    private Image image;
    private ImageCanvasListener listener;
    private Rectangle trackRect;
    private boolean isTracking = false;
    
    public ImageCanvas(Composite composite, int i) {
        super(composite, i);

        addListener(SWT.MouseDown, this);
        addListener(SWT.MouseMove, this);
        addListener(SWT.MouseUp, this);
        addListener(SWT.Paint, this);

        setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    }

    public void dispose() {
        if (image != null) {
            image.dispose();
        }
        super.dispose();
    }

    public void setListener(ImageCanvasListener listener) {
        this.listener = listener;
    }

    /**
     * Sets the image based on a file path.
     *
     * @param path the image's file path
     */
    public void setImagePath(String path) {
        if (this.image != null) {
            this.image.dispose();
        }
        if (path != null) {
            this.image = new Image(getParent().getDisplay(), path);
            setSize(getImageSize());
        } else {
            this.image = null;
        }
        redraw();
        fireImageChanged();
    }

    /**
     * Sets the image based on an Image object.
     *
     * @param image the Image object
     */
    public void setImage(Image image) {
        if (this.image != null) {
            this.image.dispose();
        }
        if (image != null) {
            this.image = new Image(getParent().getDisplay(), image, SWT.IMAGE_COPY);
            setSize(getImageSize());
        } else {
            this.image = null;
        }
        redraw();
        fireImageChanged();
    }

    /**
     * Returns the current image.
     *
     * @return an Image instance
     */
    public Image getImage() {
        return image;
    }

    /**
     * Returns the size of the current image.
     *
     * @return a Point instance
     */
    public Point getImageSize() {
        if (image != null) {
            Rectangle r = image.getBounds();
            return new Point(r.width, r.height);
        } else {
            return new Point(400, 400);
        }
    }

    /**
     * Returns the tracking rectangle.
     *
     * @return a Rectangle instance
     */
    public Rectangle getTrackingRect() {
        return trackRect;
    }

    /**
     * Clears the current image.
     */
    public void clear() {
        setImage(null);
    }

    /**
     * Crops the current image.
     *
     * @param cropRect a Rectangle defining the crop
     */
    public void crop(Rectangle cropRect) {
	    int x = cropRect.x;
        int y = cropRect.y;
        int w = cropRect.width;
        int h = cropRect.height;

		Image cropImage = new Image(getShell().getDisplay(), w, h);

		if (x+w > image.getBounds().width) {
			w = image.getBounds().width - x;
		}
		if (y+h > image.getBounds().height) {
			h = image.getBounds().height - y;
		}

		GC cropGC = new GC(cropImage);
		cropGC.drawImage(image, x, y, w, h, 0, 0, w, h);
		cropGC.dispose();

		ImageData cropImageData = cropImage.getImageData();
		cropImage.dispose();

        setImage(new Image(getParent().getDisplay(), cropImageData));
        
        fireTrackerStopped();
    }

    /**
     * Rotates the current image.
     * 
     * @param direction SWT.LEFT, SWT.RIGHT or SWT.DOWN
     */
    public void rotate(int direction) {
        ImageData srcData = image.getImageData();
        int bytesPerPixel = srcData.bytesPerLine / srcData.width;
        int destBytesPerLine = (direction == SWT.DOWN) ? srcData.width * bytesPerPixel : srcData.height * bytesPerPixel;
        byte[] newData = new byte[srcData.data.length];
        int width = 0, height = 0;
        for (int srcY = 0; srcY < srcData.height; srcY++) {
            for (int srcX = 0; srcX < srcData.width; srcX++) {
                int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
                switch (direction) {
                    case SWT.LEFT: // left 90 degrees
                        destX = srcY;
                        destY = srcData.width - srcX - 1;
                        width = srcData.height;
                        height = srcData.width;
                        break;
                    case SWT.RIGHT: // right 90 degrees
                        destX = srcData.height - srcY - 1;
                        destY = srcX;
                        width = srcData.height;
                        height = srcData.width;
                        break;
                    case SWT.DOWN: // 180 degrees
                        destX = srcData.width - srcX - 1;
                        destY = srcData.height - srcY - 1;
                        width = srcData.width;
                        height = srcData.height;
                        break;
                }
                destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
                srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);
                System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
            }
        }
        setImage(new Image(getParent().getDisplay(), new ImageData(width, height, srcData.depth, srcData.palette, destBytesPerLine, newData)));
    }

    /**
     * Flips the current image.
     *
     * @param vertical a vertical flip?
     */
    public void flip(boolean vertical) {
        ImageData srcData = image.getImageData();
        int bytesPerPixel = srcData.bytesPerLine / srcData.width;
        int destBytesPerLine = srcData.width * bytesPerPixel;
        byte[] newData = new byte[srcData.data.length];
        for (int srcY = 0; srcY < srcData.height; srcY++) {
            for (int srcX = 0; srcX < srcData.width; srcX++) {
                int destX, destY, destIndex, srcIndex;
                if (vertical) {
                    destX = srcX;
                    destY = srcData.height - srcY - 1;
                } else {
                    destX = srcData.width - srcX - 1;
                    destY = srcY;
                }
                destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
                srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);
                System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
            }
        }
        // destBytesPerLine is used as scanlinePad to ensure that no padding is required
        setImage(new Image(getParent().getDisplay(), new ImageData(srcData.width, srcData.height, srcData.depth,
                srcData.palette, destBytesPerLine, newData)));
    }

    public void handleEvent(Event event) {
        switch (event.type) {
            case SWT.Paint:
                if (image != null) {
                    event.gc.setBackground(event.display.getSystemColor(SWT.COLOR_BLACK));
                    Rectangle imageRect = image.getBounds();
                    event.gc.drawImage(
                            image,
                            imageRect.x,
                            imageRect.y,
                            imageRect.width,
                            imageRect.height,
                            imageRect.x,
                            imageRect.y,
                            imageRect.width,
                            imageRect.height
                    );
                    if (trackRect != null) {
                        event.gc.setAlpha(100);
                        event.gc.setBackground(event.display.getSystemColor(SWT.COLOR_BLUE));
                        event.gc.fillRectangle(trackRect);
                    }
                }
                break;
            case SWT.MouseDown:
                if (image != null) {
                    trackRect = new Rectangle(event.x, event.y, 1, 1);
                    fireTrackerStarted();
                }
                break;
            case SWT.MouseMove:
                if (isTracking) {
                    trackRect.width = event.x - trackRect.x;
                    trackRect.height = event.y - trackRect.y;
                    redraw();
                }
                break;
            case SWT.MouseUp:
                if (image != null) {
                    if (Math.abs(event.x - trackRect.x) < 3 && Math.abs(event.y - trackRect.y) < 3) {
                        fireTrackerStopped();
                    }
                    isTracking = false;
                }
                break;
        }
    }

    protected void fireImageChanged() {
        trackRect = null;
        if (listener != null) {
            listener.imageChanged(this);
        }
    }

    protected void fireTrackerStarted() {
        isTracking = true;
        if (listener != null) {
            listener.trackerStarted();
        }
    }

    protected void fireTrackerStopped() {
        trackRect = null;
        redraw();        
        if (listener != null) {
            listener.trackerStopped();
        }
    }
}
