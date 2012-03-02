package ray;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferFloat;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

import ray.math.Color;

/**
 * Basic image class packs all the image data into a single array of floats.
 *
 * @author arbree
 * Aug 18, 2005
 * FloatRGBImage.java
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class Image {
	
	/** Image width * */
	protected int width;
	
	/** Image height * */
	protected int height;
	
	/** Data array* */
	protected float data[];
	
	/**
	 * Create an empty image
	 *
	 * @param inW input width
	 * @param inH input height
	 */
	public Image(int inW, int inH) {
		
		setSize(inW, inH);
	}
	
	/**
	 * Copy constructor
	 *
	 * @param oldImage oldImage
	 */
	public Image(Image oldImage) {
		
		setSize(oldImage.getWidth(), oldImage.getHeight());
		for (int i = 0; i < width * height * 3; i++)
			data[i] = oldImage.data[i];
	}
	
	/**
	 * Set the image to black
	 */
	public void clear() {
		
		for (int i = 0; i < data.length; i++) {
			data[i] = 0;
		}
	}
	
	/**
	 * @return the width of the image
	 */
	public int getWidth() {
		
		return width;
	}
	
	/**
	 * @return the height of the image
	 */
	public int getHeight() {
		
		return height;
	}
	
	/**
	 * Set the size of the image by recreating it.  Destorys all current image data.
	 * @param newWidth width
	 * @param newHeight height
	 */
	public void setSize(int newWidth, int newHeight) {
		
		width = newWidth;
		height = newHeight;
		data = new float[width * height * 3];
	}
	
	/**
	 * Get the color of a pixel.
	 *
	 * @param outPixel Color value of pixel (inX,inY)
	 * @param inX inX coordinate
	 * @param inY inY Coordinate
	 */
	public void getPixelColor(Color outPixel, int inX, int inY) {
		
		int idx = calcIdx(inX, inY);
		outPixel.set(data[idx + 0], data[idx + 1], data[idx + 2]);
	}
	
	/**
	 * Set the color of a pixel.
	 * @param inPixel Color value of pixel (inX,inY)
	 * @param inX inX coordinate
	 * @param inY inY Coordinate
	 */
	public void setPixelColor(Color inPixel, int inX, int inY) {
		
		int idx = calcIdx(inX, inY);
		data[idx + 0] = (float) inPixel.r;
		data[idx + 1] = (float) inPixel.g;
		data[idx + 2] = (float) inPixel.b;
		
	}
	
	/**
	 * Set the color of a pixel using rgb data.
	 * @param inX inX coordinate
	 * @param inY inY Coordinate
	 * @param inR red component
	 * @param inG green component
	 * @param inB blue component
	 */
	public void setPixelRGB(double inR, double inG, double inB, int inX, int inY) {
		
		int idx = calcIdx(inX, inY);
		data[idx + 0] = (float) inR;
		data[idx + 1] = (float) inG;
		data[idx + 2] = (float) inB;
	}
	
	/**
	 * Computes the index in the data array.
	 * @param inX inX
	 * @param inY inY
	 * @return Array index.
	 */
	protected final int calcIdx(int inX, int inY) {
		
		return ((height - 1 - inY) * width + inX) * 3;
	}
	
	/** Return a BufferedImage (Java's internal image format) version of this image.
	 * The returned image is backed by the same data as this image, so changes to that image
	 * will affect this image.  This is needed to make the updates to the display window fast
	 * and for image IO.  It is not recommended for other uses.
	 *
	 * @return BufferedImage representing the data in this image
	 */
	public BufferedImage asBufferedImage() {
		
		int pixelFloats = 3;
		int bandOffsets[] = new int[]{0,1,2};
		
		// create a data buffer that shares the same backing array as this image
		DataBufferFloat db = new DataBufferFloat(data,data.length);
		
		// sample model describes how to find pixels and pixel components in the data buffer
		PixelInterleavedSampleModel sm = new PixelInterleavedSampleModel(db.getDataType(),width,height,
				pixelFloats,width*pixelFloats,bandOffsets);
		
		// raster wraps the data buffer and sample model
		WritableRaster raster = Raster.createWritableRaster(sm,db, null);
		
		// sRGB is java's default color space, may not match ours but prevents java from mucking with pixel values
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		
		// color model which matches our sample model
		ComponentColorModel cm = new ComponentColorModel(cs,false,true,Transparency.OPAQUE,db.getDataType());
		
		return new BufferedImage(cm,raster,true,null);
	}
	
	/**
	 * Write this image to the filename.  The output is always written as a PNG regardless
	 * of the extension on the filename given.
	 * @param fileName the output filename
	 */
	public void write(String fileName) {
		
		// For some reason java can't write the BufferedImages produced directly from the
		// image data, so we have to recreate the image to produce one for output.
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Color pixelColor = new Color();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				this.getPixelColor(pixelColor, x, y);
				pixelColor.gammaCorrect(1.0);
				int rgb = pixelColor.toInt();
				bufferedImage.setRGB(x, (height - 1 - y), rgb);
			}
		}
		
		try {
			ImageIO.write(bufferedImage, "PNG", new File(fileName));
		}
		catch (Exception e) {
			System.out.println("Error occured while attempting to write file: "+fileName);
			System.err.println(e);
			e.printStackTrace();
		}
	}
}
