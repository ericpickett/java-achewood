package archiver;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ComicText {
	public int textWidth;
	public int textHeight;
	private String[] texts;
	private static final String FONTNAME = "Verdana-Italic";
	private static final int FONTSTYLE = Font.PLAIN;
	private static final int FONTSIZE = 12;
	private static final Font FONT = new Font(FONTNAME, FONTSTYLE, FONTSIZE);
	private static final int WIDTHBUFFER = 5;
	private static final int HEIGHTBUFFER = 5;
	private static final Color TEXTCOLOR = Color.BLACK;

	public ComicText(String[] texts, BufferedImage img) {
		this.texts = texts;
		int[] textDimensions = this.getTextDimensions(texts, img);
		this.textWidth = textDimensions[0];
		this.textHeight = textDimensions[1];
	}
	
	private int[] getTextDimensions(String[] texts, BufferedImage img) {
		Graphics2D graphics = img.createGraphics();
		FontMetrics metrics = graphics.getFontMetrics(FONT);
		graphics.setFont(FONT);
		int width = WIDTHBUFFER;
		int height = HEIGHTBUFFER;
		for (String text : texts) {
			if (text.isEmpty()) continue;
			Rectangle2D rect = metrics.getStringBounds(text, graphics);
			int w = (int) rect.getWidth();
			if (w > width) width = w;
			height += (int) rect.getHeight();
		}
		
		return new int[] {width, height};
	}
	
	public BufferedImage writeText(BufferedImage img) {
		Graphics2D graphics = img.createGraphics();
		graphics.setColor(ComicText.TEXTCOLOR);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		FontMetrics metrics = graphics.getFontMetrics(FONT);
		int leader = 0;
		for (String text : texts) {
			Rectangle2D bounds = metrics.getStringBounds(text, graphics);
			graphics.drawString(text, img.getWidth() / 2 - (int) (bounds.getWidth() / 2), leader + (int) bounds.getHeight());
			leader += (int) bounds.getHeight();
		}
		return img;
	}
	

}
