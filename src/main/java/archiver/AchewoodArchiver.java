package archiver;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AchewoodArchiver {
	public static final String BASEURL = "http://www.achewood.com";

	private static final String LISTPAGESELECTOR = "dl dd a";
	private static final AtomicInteger comicsCounter = new AtomicInteger(0);
	private static final int THREADCOUNT = 30;

	private static LinkedBlockingQueue<Comic> queue;

	
	public static void main(String[] args) throws Exception {
		System.out.println(new Date());
		URL listPageUrl = new URL(BASEURL + "/list.php"); // achewood list page has a full list of comic urls and titles
		HtmlPage listPage = new HtmlPage(listPageUrl); // if this doesn't connect the whole thing blows up
		Elements listOfComicUrls = listPage.selectElements(LISTPAGESELECTOR);
		queue = new LinkedBlockingQueue<Comic>();
		for (Element element : listOfComicUrls) {
			URL url = new URL(BASEURL + "/" + element.attr("href"));
			Date date = parseComicDate(url.getQuery());
			String title = element.html();
			Comic comic = new Comic(url, title, date);
			queue.put(comic);
			comicsCounter().incrementAndGet();
		}
		
		ExecutorService pool = Executors.newFixedThreadPool(THREADCOUNT);
		do {
			Comic comic = queue.poll(100, TimeUnit.MILLISECONDS);
			if (comic == null) {
				continue;
			} else {
				PlistProcessor.getInstance().comics.add(comic);
				pool.execute(comic);
			}
		} while (comicsCounter().get() > 0);
		pool.shutdown();
		PlistProcessor.getInstance().saveDocument();
		System.out.println(new Date());
	}

	
	private static Date parseComicDate(String query) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("MMddyyyy");
		int beginIndex = query.indexOf("=") + 1;
		String queryDate = query.substring(beginIndex, query.length());

		return format.parse(queryDate);
	}
	
	public static synchronized AtomicInteger comicsCounter() {
		return comicsCounter;
	}
	
	public static synchronized LinkedBlockingQueue<Comic> getQueue() {
		return queue;
	}
}
