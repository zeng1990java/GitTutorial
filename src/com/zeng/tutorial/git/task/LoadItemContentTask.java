package com.zeng.tutorial.git.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import android.os.AsyncTask;

public class LoadItemContentTask extends AsyncTask<Void,Void,String> {

	private static final String BASE_URL = "http://www.liaoxuefeng.com";
	
	private static final String HTML_START = "<!DOCTYPE HTML><html><body>";
	private static final String HTML_END = "</body></html>";
	
	private static final String HTML_DIV_START = "<div>";
	private static final String HTML_DIV_END = "</div>";
	
	private String mUrl;
	
	public LoadItemContentTask(String url){
		this.mUrl = url;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		try {
			Document doc = Jsoup.connect(mUrl).get();
			Elements elements = doc.getElementsByClass("x-wiki-content");
			Elements allElements = elements.get(0).getAllElements();
			Iterator<Element> iter = allElements.iterator();
			List<Integer> videos = new ArrayList<Integer>();
			List<String> videoUrls = new ArrayList<String>();
			int index = 0;
			while(iter.hasNext()){
				Element element = iter.next();
				if(element.attr("class").equals("html5-video")){
					videos.add(index);
					videoUrls.add(element.attr("data-src"));
				}
				index++;
			}
			
			for(int i = 0; i < videos.size(); i++){
				Element e = new Element(Tag.valueOf("video"), mUrl);
				e.attr("src", videoUrls.get(i));
				e.attr("controls", "controls");
				e.attr("width", "320");
				e.attr("height", "240");
				allElements.remove(videos.get(i));
				allElements.add(videos.get(i), e);
			}
			
			Elements imgElements = elements.select("img");
			List<String> imgs = new ArrayList<String>();
			if(imgElements!=null&&imgElements.size()>0){
				Iterator<Element> iterator = imgElements.iterator();
				while(iterator.hasNext()){
					Element element = iterator.next();
					String imageUrl = element.attr("src");
					imgs.add(imageUrl);
					System.out.println(imageUrl);
				}
			}
			String result = allElements.toString();
			for(String img : imgs){
				result = result.replaceAll(img, BASE_URL+img);
			}
			return HTML_START+HTML_DIV_START+result+HTML_DIV_END+HTML_END;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
