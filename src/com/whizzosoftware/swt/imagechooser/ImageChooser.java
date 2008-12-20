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
import org.eclipse.swt.events.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;

import java.util.ResourceBundle;

/**
 * An image display and selection widget.
 *
 * @author Dan Noguerol
 */
public class ImageChooser extends Canvas implements PaintListener, MouseTrackListener, MouseListener {
    private Image image;
    private boolean isHover = false;
    private ResourceBundle resourceBundle;

    public ImageChooser(Composite parent, int i) {
        super(parent, i);
        
        addPaintListener(this);
        addMouseTrackListener(this);
        addMouseListener(this);

        DropTarget dropTarget = new DropTarget(this, DND.DROP_COPY | DND.DROP_DEFAULT);
        dropTarget.setTransfer(new Transfer[] {FileTransfer.getInstance()});

        resourceBundle = ResourceBundle.getBundle("Resources");
    }

    public void dispose() {
        if (image != null) {
            image.dispose();
        }
    }

    public void setImage(String path) {
        setImage(path, true);
    }

    public void setImage(String path, boolean setDirty) {
        if (this.image != null) {
            this.image.dispose();
            this.image = null;
        }
        if (path != null) {
            image = new Image(null, path);
        }
        if (setDirty) {
            notifyListeners(SWT.Modify, new Event());
            redraw();
        }
    }

    public void setImage(Image image) {
        setImage(image, true);
    }

    public void setImage(Image image, boolean setDirty) {
        if (this.image != null) {
            this.image.dispose();
            this.image = null;
        }
        if (image != null) {
            this.image = new Image(null, (ImageData)image.getImageData().clone());
        } else {
            this.image = null;
        }
        if (setDirty) {
            notifyListeners(SWT.Modify, new Event());
            redraw();
        }
    }

    public Image getImage() {
        return image;
    }

    public void paintControl(PaintEvent event) {
        Rectangle r = getClientArea();
        if (image != null) {
            Rectangle imageRect = image.getBounds();
            event.gc.drawImage(
                    image,
                    imageRect.x,
                    imageRect.y,
                    imageRect.width,
                    imageRect.height,
                    r.x,
                    r.y,
                    r.width,
                    r.height
            );
        } else {
            String text = resourceBundle.getString("NO_IMAGE_TEXT");
            event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_WHITE));
            event.gc.setBackground(event.display.getSystemColor(SWT.COLOR_DARK_GRAY));
            event.gc.fillRectangle(r.x, r.y, r.width, r.height);
            if (!isHover) {
                Font font = new Font(event.display, "Arial", 11, SWT.BOLD);
                event.gc.setFont(font);
                Point pt = event.gc.stringExtent(text);
                int x = (r.width - pt.x) / 2;
                int y = (r.height - pt.y) / 2;
                event.gc.drawText(text, x, y);
                font.dispose();
            }
        }

        if (isHover) {
            String text = resourceBundle.getString("CLICK_CHANGE_TEXT");
            event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_WHITE));
            event.gc.setBackground(event.display.getSystemColor(SWT.COLOR_BLUE));
            event.gc.setAlpha(175);
            event.gc.fillRectangle(r.x, r.y, r.width, r.height);
            Font font = new Font(event.display, "Arial", 10, SWT.BOLD);
            event.gc.setFont(font);
            Point pt = event.gc.stringExtent(text);
            int x = (r.width - pt.x) / 2;
            int y = (r.height - pt.y) / 2;
            event.gc.drawText(text, x, y, true);
            font.dispose();
        }
    }

    public void mouseDoubleClick(MouseEvent event) {
    }

    public void mouseDown(MouseEvent event) {
    }

    public void mouseUp(MouseEvent event) {
        try {
            ImageChooserDialog dialog = new ImageChooserDialog(getParent().getShell(), SWT.NONE);
            dialog.setImage(image);
            dialog.setText(resourceBundle.getString("SELECT_IMAGE_TEXT"));
            dialog.open();
            if (dialog.getImage() != null) {
                setImage(dialog.getImage());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    public void mouseEnter(MouseEvent event) {
        isHover = true;
        redraw();
    }

    public void mouseExit(MouseEvent event) {
        isHover = false;
        redraw();
    }

    public void mouseHover(MouseEvent event) {
    }
}
