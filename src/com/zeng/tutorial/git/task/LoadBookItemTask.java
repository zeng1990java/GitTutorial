package com.zeng.tutorial.git.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zeng.tutorial.git.bo.Book;
import com.zeng.tutorial.git.bo.BookItem;

import android.os.AsyncTask;

public class LoadBookItemTask extends AsyncTask<Void,Void,Book> {

	private static final String BASE_URL = "http://www.liaoxuefeng.com";
	private static final String GIT_URL = BASE_URL+"/wiki/0013739516305929606dd18361248578c67b8067c8c017b000";
	
	@Override
	protected Book doInBackground(Void... params) {
		try {
			Book book = new Book();
			Document doc = Jsoup.connect(GIT_URL).get();
			book.title = doc.title();
			Elements xSidebarLeft = doc.getElementsByClass("x-sidebar-left");
			Elements liElements = xSidebarLeft.select("li");
			ListIterator<Element> iterator = liElements.listIterator();
			List<BookItem> bookItems = new ArrayList<BookItem>();
			while(iterator.hasNext()){
				Element element = iterator.next();
				Element aElement = element.getElementsByTag("a").get(0);
				BookItem item = new BookItem(aElement.text(),BASE_URL+aElement.attr("href"));
				bookItems.add(item);
			}
			book.bookItems = bookItems;
			return book;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
