package com.dfour;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

public class Piled {
	private BufferedImage inImage;
	private boolean debug = true;
	public Piled(File in, String out, int size){
		if(debug){
			System.out.println("infile:"+in);
			System.out.println("outfile:"+out);
			System.out.println("tilesize:"+size);
		}
		try {
			inImage = ImageIO.read(in);
			if(debug){
				System.out.println("Read image in.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int width = inImage.getWidth();
		int height = inImage.getHeight();
		
		if(width % size > 0){
			throw new RuntimeException("Width not a multiple of tile size.");
		}
		if(height % size > 0){
			throw new RuntimeException("Height not a multiple of tile size.");
		}
		
		int rows = width / size;
		int cols = height / size ;
		if(debug){
			System.out.println("Rows"+rows+" Cols"+cols);
		}
		
		int widthWithPadding = size +(size /8);
		int heightWithPadding = size + (size /8);
		
		if(debug){
			System.out.println("widthWithPadding"+widthWithPadding+" heightWithPadding"+heightWithPadding);
		}
		
		BufferedImage outputCanvas = new BufferedImage(widthWithPadding* rows, heightWithPadding * cols,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)outputCanvas.getGraphics();
		
		for(int r = 0; r < rows ; r++){
			for(int c = 0; c < cols ; c++){
				// get the tiles
				BufferedImage tile = inImage.getSubimage(r * size, c * size, size, size);
				drawImage(outputCanvas, tile, size/8 , size/8,r, c, size);
				if(debug){
					System.out.println("Drawing image to camvas   wwp:"+widthWithPadding * r+" hwp:"+
							heightWithPadding * c+ " r:"+r+" c:"+c+" size:"+size);
				}
				
			}
		}
		
		ImageOutputStream ios = null;
		File outfile = new File(out);
		try {
			ios = ImageIO.createImageOutputStream(outfile);
			ImageIO.write(outputCanvas, "png", ios);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * @param canvas	output image
	 * @param image		imput image
	 * @param amountX 	x padding
	 * @param amountY 	y padding
	 * @param row		row
	 * @param col		col
	 * @param size		size
	 */
	private void drawImage(BufferedImage canvas, BufferedImage image, int amountX, int amountY,int row, int col, int size){
		// Copy corner pixels to fill corners of the padding.
		int iw = image.getWidth();
		int ih = image.getHeight();
		int rectX = (row * size) + (row * amountX) + amountX/2;
		int rectY = (col * size) + (col * amountY) + amountY/2;
		if(debug){
			System.out.println("rectX"+rectX+" rectY"+rectY);

		}
		for (int i = 1; i <= amountX/2; i++) {
			for (int j = 1; j <= amountY/2; j++) {
				//dst output ,imagex xpos to draw at,y ypos to draw at, argb colour format
				plot(canvas, rectX - i, rectY - j, image.getRGB(0, 0));
				plot(canvas, rectX - i, rectY + ih - 1 + j, image.getRGB(0, ih - 1));
				plot(canvas, rectX + iw - 1 + i, rectY - j, image.getRGB(iw - 1, 0));
				plot(canvas, rectX + iw - 1 + i, rectY + ih - 1 + j, image.getRGB(iw - 1, ih - 1));
			}
		}
		// Copy edge pixels into padding.
		for (int i = 1; i <= amountY/2; i++) {
			copy(image, 0, 0, iw, 1, canvas, rectX, rectY - i);
			copy(image, 0, ih - 1, iw, 1, canvas, rectX, rectY + ih - 1 + i);
		}
		for (int i = 1; i <= amountX/2; i++) {
			//copy from 
			copy(image, 0, 0, 1, ih, canvas, rectX - i, rectY);
			copy(image, iw - 1, 0, 1, ih, canvas, rectX + iw - 1 + i, rectY);
		}
		copy(image, 0, 0, size, size, canvas, rectX, rectY);
	}
	
	/**
	 * @param dst output image
	 * @param x	xpos to draw at
	 * @param y	ypos to draw at
	 * @param argb	colour format
	 */
	static private void plot (BufferedImage dst, int x, int y, int argb) {
		if (0 <= x && x < dst.getWidth() 
				&& 0 <= y && y < dst.getHeight()) {
			dst.setRGB(x, y, argb);  // draw argb to x y
		}
	}
	
	/**
	 * Draws from source image to output image
	 * @param src image source
	 * @param x xpos of src image
	 * @param y ypos of source image
	 * @param w width to draw
	 * @param h height to draw
	 * @param dst	output image
	 * @param dx 	output image x
	 * @param dy	output image y
	 */
	static private void copy (BufferedImage src, int x, int y, int w, int h, BufferedImage dst, int dx, int dy ) {
		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++)
				plot(dst, dx + i, dy + j, src.getRGB(x + i, y + j));
	}

	
	static public void main (String[] args) throws Exception {
		String input = "../image.png",output = "outputImage.png";
		int tileSize = 32;
		switch (args.length) {
		case 3:
			tileSize = Integer.valueOf(args[2]);
		case 2:
			output = args[1];
		case 1:
			input = args[0];
		}
		
		File inputFile = new File(input);
		output = new File(inputFile.getParentFile(), "outPut.png").getAbsolutePath();
		
		new Piled(inputFile,output,tileSize);
	}
	
}
