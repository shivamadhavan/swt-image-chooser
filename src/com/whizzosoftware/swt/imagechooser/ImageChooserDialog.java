/************************************************************************************************************
 * Copyright (c) 2008 Whizzo Software, LLC. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Whizzo Software, LLC (initial design and implementation)
 ***********************************************************************************************************/
package com.whizzosoftware.swt.imagechooser;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import com.whizzosoftware.swt.imagechooser.util.ImageUtil;

import java.util.ResourceBundle;

/**
 * An image selection and manipulation dialog.
 *
 * @author Dan Noguerol
 */
public class ImageChooserDialog extends Dialog implements SelectionListener, ImageCanvasListener {
    private Shell shell;
    private ToolItem browseItem;
    private ToolItem captureItem;
    private ToolItem importItem;
    private ToolItem cropItem;
    private ToolItem rotateItem;
    private ToolItem flipItem;
    private ToolItem clearItem;
    private ScrolledComposite scrollComposite;
    private Image image;
    private ImageCanvas imageCanvas;
    private Button cancelButton;
    private Button okButton;
    private ResourceBundle resourceBundle;

    protected ImageChooserDialog(Shell parent, int style) {
        super(parent, style);
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL |SWT.RESIZE);
        resourceBundle = ResourceBundle.getBundle("Resources");
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setText(String title) {
        shell.setText(title);
    }

    public void open() {
        shell.setLayout(new GridLayout(1, true));

        createToolbar(shell);
        createImageArea(shell);
        createButtonBar(shell);

        shell.pack();
        shell.open();

        while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch()) {
                shell.getDisplay().sleep();
            }
        }
    }

    public void widgetSelected(SelectionEvent event) {
        if (event.widget == browseItem) {
            FileDialog fd = new FileDialog(event.display.getActiveShell());
            fd.setText(resourceBundle.getString("OPEN_TEXT"));
            fd.setFilterExtensions(new String[] {"*.gif;*.png;*.jpg;*.jpeg;*.bmp", "*.*"});
            String selected = fd.open();
            if (selected != null) {
                imageCanvas.setImagePath(selected);
            }
        } else if (event.widget == captureItem) {
            GC gc = new GC(event.display);
            Image image = new Image(event.display, event.display.getBounds());
            gc.copyArea(image, 0, 0);
            gc.dispose();
            imageCanvas.setImage(image);
            image.dispose();
        } else if (event.widget == importItem) {
        } else if (event.widget == cropItem) {
            imageCanvas.crop(imageCanvas.getTrackingRect());
        } else if (event.widget == rotateItem) {
            imageCanvas.rotate(SWT.RIGHT);
        } else if (event.widget == flipItem) {
            imageCanvas.flip(false);
        } else if (event.widget == clearItem) {
            imageCanvas.clear();
        } else if (event.widget == okButton) {
            shell.dispose();
        } else if (event.widget == cancelButton) {
            image = null;
            shell.dispose();
        }
    }

    public void widgetDefaultSelected(SelectionEvent event) {
    }

    protected void createToolbar(Shell parent) {
        ToolBar toolbar = new ToolBar(parent, SWT.HORIZONTAL);
        browseItem = new ToolItem(toolbar, SWT.PUSH);
        browseItem.setText(resourceBundle.getString("BROWSE_BUTTON_TEXT"));
        browseItem.setToolTipText(resourceBundle.getString("BROWSE_BUTTON_TOOLTIP"));
        browseItem.setImage(ImageUtil.getBrowseIcon(parent.getDisplay()));
        browseItem.addSelectionListener(this);
        captureItem = new ToolItem(toolbar, SWT.PUSH);
        captureItem.setText(resourceBundle.getString("CAPTURE_BUTTON_TEXT"));
        captureItem.setToolTipText(resourceBundle.getString("CAPTURE_BUTTON_TOOLTIP"));
        captureItem.setImage(ImageUtil.getCaptureIcon(parent.getDisplay()));
        captureItem.addSelectionListener(this);
        importItem = new ToolItem(toolbar, SWT.PUSH);
        importItem.setText(resourceBundle.getString("IMPORT_BUTTON_TEXT"));
        importItem.setToolTipText(resourceBundle.getString("IMPORT_BUTTON_TOOLTIP"));
        importItem.setImage(ImageUtil.getImportIcon(parent.getDisplay()));
        importItem.addSelectionListener(this);
        new ToolItem(toolbar, SWT.SEPARATOR);
        cropItem = new ToolItem(toolbar, SWT.PUSH);
        cropItem.setText(resourceBundle.getString("CROP_BUTTON_TEXT"));
        cropItem.setToolTipText(resourceBundle.getString("CROP_BUTTON_TOOLTIP"));
        cropItem.setImage(ImageUtil.getCropIcon(parent.getDisplay()));
        cropItem.addSelectionListener(this);
        cropItem.setEnabled(false);
        rotateItem = new ToolItem(toolbar, SWT.PUSH);
        rotateItem.setText(resourceBundle.getString("ROTATE_BUTTON_TEXT"));
        rotateItem.setToolTipText(resourceBundle.getString("ROTATE_BUTTON_TOOLTIP"));
        rotateItem.setImage(ImageUtil.getRotateIcon(parent.getDisplay()));
        rotateItem.addSelectionListener(this);
        flipItem = new ToolItem(toolbar, SWT.PUSH);
        flipItem.setText(resourceBundle.getString("FLIP_BUTTON_TEXT"));
        flipItem.setToolTipText(resourceBundle.getString("FLIP_BUTTON_TOOLTIP"));
        flipItem.setImage(ImageUtil.getFlipIcon(parent.getDisplay()));
        flipItem.addSelectionListener(this);
        clearItem = new ToolItem(toolbar, SWT.PUSH);
        clearItem.setText(resourceBundle.getString("CLEAR_BUTTON_TEXT"));
        clearItem.setToolTipText(resourceBundle.getString("CLEAR_BUTTON_TOOLTIP"));
        clearItem.setImage(ImageUtil.getClearIcon(parent.getDisplay()));
        clearItem.addSelectionListener(this);
    }

    protected void createImageArea(Shell parent) {
        scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        GridData data = new GridData();
        data.horizontalAlignment = SWT.FILL;
        data.grabExcessHorizontalSpace = true;
        data.verticalAlignment = SWT.FILL;
        data.grabExcessVerticalSpace = true;
        if (image != null) {
            Rectangle ir = image.getBounds();
            int w = ir.width - ir.x;
            int h = ir.height - ir.y;
            data.widthHint = w > 640 ? 640 : w;
            data.heightHint = h > 480 ? 480 : h;
        } else {
            data.widthHint = 640;
            data.heightHint = 480;
        }
        scrollComposite.setLayoutData(data);

        imageCanvas = new ImageCanvas(scrollComposite, SWT.NONE);
        imageCanvas.setLayout(new FillLayout());
        imageCanvas.setListener(this);
        imageCanvas.setImage(image);
        scrollComposite.setContent(imageCanvas);
        scrollComposite.setExpandHorizontal(true);
        scrollComposite.setExpandVertical(true);
        scrollComposite.setMinSize(imageCanvas.getImageSize());
    }

    protected void createButtonBar(Shell parent) {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(new FillLayout());
        cancelButton = new Button(c, SWT.NONE);
        cancelButton.setText(resourceBundle.getString("CANCEL_BUTTON_TEXT"));
        cancelButton.addSelectionListener(this);
        okButton = new Button(c, SWT.NONE);
        okButton.setText(resourceBundle.getString("OK_BUTTON_TEXT"));
        okButton.addSelectionListener(this);
    }

    public void imageChanged(ImageCanvas canvas) {
        setImage(canvas.getImage());        
        scrollComposite.setMinSize(canvas.getImageSize());
    }

    public void trackerStarted() {
        cropItem.setEnabled(true);
    }

    public void trackerStopped() {
        cropItem.setEnabled(false);
    }
}
