package archiver;

import org.jsoup.nodes.Element;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Comic implements Runnable {
	private URL comicUrl;
	private URL imageUrl;
	private String title;
	private String caption;
	private Date date;
	private String dateline;
	private BufferedImage img;

	private static final int minDelay = 1000;
	private static final int maxDelay = 5000;

	LinkedBlockingQueue<Comic> queue = AchewoodArchiver.getQueue();
	
	public Comic(URL url, String title, Date date) {
		this.comicUrl = url;
		this.title = title;
		this.date = date;
	}
	public void run() {
		if (imageUrl == null) {
			try {
				this.readComicPage();
			} catch (IOException e) {
				e.printStackTrace();
				imageUrl = null;
				int duration = randomSleepDuration(minDelay, maxDelay);
				sleepThread(duration);
			} finally {
				this.addToQueue();
			}
		} else if (img == null) {
			try {
				this.readImagePage();
			} catch (IOException e) {
				e.printStackTrace();
				img = null;
				int duration = randomSleepDuration(minDelay, maxDelay);
				sleepThread(duration);
			} finally {
				this.addToQueue();
			}
		} else {
			try {
				ImageProcessor processor = new ImageProcessor();
				processor.processImage(this);
				AchewoodArchiver.comicsCounter().decrementAndGet();
			} catch (Exception e) {
				img = null; // reset this and try again
				int duration = randomSleepDuration(minDelay, maxDelay);
				sleepThread(duration);
				this.addToQueue();
			}
		}
	}

	private void sleepThread(long milliseconds) {
		try {
			Thread.sleep(milliseconds); //give the server a chance to breath
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private int randomSleepDuration(int min, int max) {
		return ThreadLocalRandom.current().nextInt((max - min) + 1) + min;
	}
	private void readComicPage() throws IOException {
		HtmlPage comicPage = new HtmlPage(comicUrl);
		Element img = comicPage.selectElements(".comic").first();
		this.caption = img.attr("title");
		Element dateline = comicPage.selectElements("title").first();
		this.dateline = dateline.html();
		try {
			this.imageUrl = new URL(AchewoodArchiver.BASEURL + img.attr("src"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readImagePage() throws IOException {
		img = ImageIO.read(imageUrl);
	}
	
	private void addToQueue() {
		try {
			queue.put(this);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized BufferedImage getImg() {
		return img;
	}
	
	public synchronized String getTitle() {
		return title;
	}
	
	public synchronized String getCaption() {
		return caption;
	}
	
	public synchronized Date getDate() {
		return date;
	}
	
	public synchronized String getDateline() {
		return dateline;
	}
}
