package processing.blinkenlights;

/**
 Copyright (C) 2008  Robin Senior

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 For more info, please contact me at senior@gmail.com
 */

import java.io.PrintWriter;

import processing.core.PApplet;
import processing.core.PConstants;

public class BMLWriter implements PConstants
{
	PApplet _parent;
	PacketSender _mySender;

	boolean _dump = false;

	PrintWriter _output;

	int _countX = 96;
	int _countY = 32;
	int _bits = 4, _channels = 1, _duration = 41;
	int _pixelWidth, _pixelHeight, _spanProduct;

	String _title = "", _description = "", _author = "", _email = "",
			_url = "";
	String _creator = "Blinkelights Processing Exporter";

	boolean _showGrid = false;
	boolean _showPreview = false;

	final int _numColors = (int) Math.pow(2, _bits);
	
	final private String _version = "1.5.1";

	public BMLWriter(PApplet parent, int frameDuration)
	{
		_parent = parent;
		_duration = frameDuration;

		_parent.registerDraw(this);
		_parent.registerDispose(this);
	}

	public BMLWriter(PApplet parent, int frameDuration,
			PacketSender sender)
	{
		this(parent, frameDuration);
		_mySender = sender;
	}

	// call to begin writing
	public void startWriting(String fileName)
	{
		_dump = true;

		System.out.println("dumping to file: " + fileName);

		_output = _parent.createWriter(fileName);
		dumpHeader();

	}

	private void dumpHeader()
	{
		_output.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		_output.println("<blm width=\"" + _countX + "\" height=\"" + _countY
				+ "\" bits=\"" + _bits + "\" channels=\"" + _channels + "\">");

		_output.println("<header>");
		_output.println("<title>" + _title + "</title>");
		_output.println("<description>" + _description + "</description>");
		_output.println("<creator>" + _creator + "</creator>");
		_output.println("<author>" + _author + "</author>");
		_output.println("<email>" + _email + "</email>");
		_output.println("<url>" + _url + "</url>");
		_output.println("</header>");
	}

	private void dumpFooter()
	{
		_output.println("</blm>");
		_output.flush();
		_output.close();
	}

	public void draw()
	{

		// compute the size of the pixelation blocks
		_pixelWidth = _parent.width / _countX;
		_pixelHeight = _parent.height / _countY;

		int numPixels = _countX * _countY;
		byte[] pixelData = new byte[numPixels];

		// the number of pixels to average
		_spanProduct = _pixelWidth * _pixelHeight;

		if (_dump)
			_output.println("<frame duration=\"" + _duration + "\">");

		// put the pixels into the data structure
		_parent.loadPixels();

		int k = 0;
		for (int j = 0; j < _countY; j++)
		{
			if (_dump)
				_output.print("<row>");

			for (int i = 0; i < _countX; i++)
			{

				int rSum = 0;
				int gSum = 0;
				int bSum = 0;

				for (int x = i * _pixelWidth; x < (i + 1) * _pixelWidth; x++)
				{
					for (int y = j * _pixelHeight; y < (j + 1) * _pixelHeight; y++)
					{
						// compute the stride length for acessing the 1D pixel
						// array
						int pixelPoint = y * _parent.width + x;

						int color = _parent.pixels[pixelPoint];

						rSum += getRed(color);
						gSum += getGreen(color);
						bSum += getBlue(color);
					}
				}

				// average the pixels
				rSum /= _spanProduct;
				gSum /= _spanProduct;
				bSum /= _spanProduct;

				// convert to grayscale (2nd way is weighted for human colour
				// perception)
				int avg = (rSum + gSum + bSum) / 3;
				int avgColor = createColor(avg);
				// float avg = (.3f * rSum) + (.59f * gSum) + (.11f * bSum);

				// write the pixelated square to bml
				if (_dump)
				{
					int value = avg/_numColors;
					//String hex = Integer.toHexString(bucketedColor);
					String hex = StringTools.toLzHexStringPost( value, _bits / 4 );
					
					_output.print(hex);
				}

				// write the pixel to the byte array
				pixelData[k++] = (byte) (avg & 0x0000ff);

				// paint the pixelated image
				if(showPreview())
				{
					for (int y = j * _pixelHeight; y < (j + 1) * _pixelHeight; y++)
						for (int x = i * _pixelWidth; x < (i + 1) * _pixelWidth; x++)
							_parent.pixels[y * _parent.width + x] = avgColor;
				}
			}
			if (_dump)
				_output.println("</row>");
		}

		if (_dump)
			_output.println("</frame>");

		// send the packets
		if (null != _mySender)
			_mySender.sendFrame(pixelData, _countX, _countY);
		
		if (_showGrid && _countX == 96 && _countY == 32)
		{

			// draw the grid in blue
			for (int x = 0; x < _countX; x++)
				for (int y = 0; y < _parent.height; y++)
					_parent.pixels[y * _parent.width + (x * _pixelWidth)] = 0xFF000099;

			for (int y = 0; y < _countY; y++)
				for (int x = 0; x < _parent.width; x++)
					_parent.pixels[(y * _pixelHeight) * _parent.width + x] = 0xFF000099;

			// draw the visible regions in red
			drawRect(15, 11, 22, 8);
			drawRect(15, 21, 22, 7);
			drawRect(49, 5, 30, 12);
			drawRect(49, 19, 30, 9);
		}
		

		// update the pixels in the Processing display
		_parent.updatePixels();
		
	}

	private void drawRect(int x, int y, int width, int height)
	{
		int c = 0xFFFF0000;

		// left side & right sides
		int startY = y * _pixelHeight;
		for (int dY = startY; dY < startY + (height * _pixelHeight); dY++)
		{
			_parent.pixels[dY * _parent.width + (x * _pixelWidth)] = c;
			_parent.pixels[dY * _parent.width + ((x + width) * _pixelWidth)] = c;
		}

		// top & bottom sides
		int startX = x * _pixelWidth;
		for (int dX = startX; dX < startX + (width * _pixelWidth); dX++)
		{
			_parent.pixels[(y * _pixelHeight) * _parent.width + dX] = c;
			_parent.pixels[((y + height) * _pixelHeight) * _parent.width + dX] = c;
		}
	}

	// should get called when the applet exits
	public void dispose()
	{
		if (_dump)
			dumpFooter();
		System.out.println("Finished writing file.");
	}
	
	// ------------------------------------------------------------------------
	// color operations
	// ------------------------------------------------------------------------
	private int createColor(int avg)
	{
		return (255 << 24) | (avg << 16) | (avg << 8) | avg;
	}

	private int getRed(int color)
	{
		return color >> 16 & 0xFF;
	}

	private int getGreen(int color)
	{
		return color >> 8 & 0xFF;
	}

	private int getBlue(int color)
	{
		return color & 0xFF;
	}

	// ------------------------------------------------------------------------
	// setters
	// ------------------------------------------------------------------------
	public void setTitle(String title)
	{
		_title = title;
	}

	public void setDescription(String description)
	{
		_description = description;
	}

	public void setAuthor(String author)
	{
		_author = author;
	}

	public void setEmail(String email)
	{
		_email = email;
	}

	public void setURL(String url)
	{
		_url = url;
	}

	public void showGrid(boolean show)
	{
		_showGrid = show;
	}

	public void showPreview(boolean show)
	{
		_showPreview = show;
	}
	
	public boolean showPreview()
	{
		return _showPreview;
	}
	
	private boolean showGrid()
	{
		return _showGrid;
	}
	
	public void setCountX(int count)
	{
		_countX = count;
	}
	
	public void setCountY(int count)
	{
		_countY = count;
	}
	
	public int getCountX()
	{
		return _countX;
	}
	
	public int getCountY()
	{
		return _countY;
	}
	
	public String getVersion()
	{
		return _version;
	}
	
	public void setColorDepth(int depthInBits)
	{
		_bits = depthInBits;
	}
	
	public int getColorDepth()
	{
		return _bits;
	}
}
