package archiver;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

public class ImageProcessor {
	private static final String SAVEPATH = "src/main/resources/images/";
	private static final Color BACKGROUNDCOLOR = Color.WHITE;
	
	public void processImage(Comic comic) {
		System.out.println("in the process block");
		BufferedImage img = comic.getImg();
		String[] texts = {comic.getDateline(), comic.getTitle(), comic.getCaption()};
		ComicText comicText = new ComicText(texts, img);
		int textWidth = comicText.textWidth;
		int textHeight = comicText.textHeight;
		BufferedImage copy = this.createCopy(img, textWidth, textHeight);
		copy = this.resizeAndFill(copy);
		copy = comicText.writeText(copy);
		copy = this.writeImage(img, copy, textHeight);
		this.saveImage(comic, copy);
	}
	
	private void saveImage(Comic comic, BufferedImage img) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		File outfile = new File(SAVEPATH + format.format(comic.getDate()) + ".png");
		System.out.println("in the save block");
		try {
			ImageIO.write(img, "png", outfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private BufferedImage createCopy(BufferedImage img, int textWidth, int textHeight) {
		int copyHeight = img.getHeight() + textHeight + 5;
		int copyWidth = (img.getWidth() > textWidth) ? img.getWidth() : textWidth;
		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		return config.createCompatibleImage(copyWidth, copyHeight);
	}
	
	private BufferedImage resizeAndFill(BufferedImage img) {
		Graphics2D graphics = img.createGraphics();
		graphics.setColor(BACKGROUNDCOLOR);
		graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		return img;
	}
	
	private BufferedImage writeImage(BufferedImage original, BufferedImage copy, int textHeight) {
		Graphics2D graphics = copy.createGraphics();
		graphics.drawImage(original, null, (copy.getWidth() / 2) - (original.getWidth() / 2), textHeight);
		return copy;
	}
}
