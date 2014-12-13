package com.zeng.tutorial.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.zeng.tutorial.git.R;


public class MainActivity extends Activity {

	private static final String TAG = "SampleActivity";
	
	private static final String HTML_START = "<!DOCTYPE HTML><html><body>";
	private static final String HTML_END = "</body></html>";
	
	private static final String HTML_DIV_START = "<div>";
	private static final String HTML_DIV_END = "</div>";
	
	private static final String BASE_URL = "http://www.liaoxuefeng.com";
	
	String url = BASE_URL + "/wiki/0013739516305929606dd18361248578c67b8067c8c017b000/0013743256916071d599b3aed534aaab22a0db6c4e07fd0000";
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private boolean drawerArrowColor;

	private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        
        webView = (WebView) findViewById(R.id.webview);
		WebSettings ws = webView.getSettings();
		ws.setJavaScriptEnabled(false);
		ws.setAllowFileAccess(true); 
		ws.setBuiltInZoomControls(false);
		ws.setSupportZoom(false);  
		ws.setTextZoom(120);


        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
            drawerArrow, R.string.drawer_open,
            R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        String[] values = new String[]{
            "Stop Animation (Back icon)",
            "Stop Animation (Home icon)",
            "Start Animation",
            "Change Color",
            "GitHub Page",
            "Share",
            "Rate"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceAsColor")
			@Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        mDrawerToggle.setAnimateEnabled(false);
                        drawerArrow.setProgress(1f);
                        break;
                    case 1:
                        mDrawerToggle.setAnimateEnabled(false);
                        drawerArrow.setProgress(0f);
                        break;
                    case 2:
                        mDrawerToggle.setAnimateEnabled(true);
                        mDrawerToggle.syncState();
                        break;
                    case 3:
                        if (drawerArrowColor) {
                            drawerArrowColor = false;
                            drawerArrow.setColor(R.color.ldrawer_color);
                        } else {
                            drawerArrowColor = true;
                            drawerArrow.setColor(R.color.drawer_arrow_second_color);
                        }
                        mDrawerToggle.syncState();
                        break;
                    case 4:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/IkiMuhendis/LDrawer"));
                        startActivity(browserIntent);
                        break;
                    case 5:
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        share.putExtra(Intent.EXTRA_SUBJECT,
                            getString(R.string.app_name));
                        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_description) + "\n" +
                            "GitHub Page :  https://github.com/IkiMuhendis/LDrawer\n" +
                            "Sample App : https://play.google.com/store/apps/details?id=" +
                            getPackageName());
                        startActivity(Intent.createChooser(share,
                            getString(R.string.app_name)));
                        break;
                    case 6:
                        String appUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();
                        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));
                        startActivity(rateIntent);
                        break;
                }

            }
        });
        
        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    protected <Params, Progress, Result> void executeTask(
			AsyncTask<Params, Progress, Result> task, Params... params) {
		if (Build.VERSION.SDK_INT >= 11) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		} else {
			task.execute(params);
		}
	}
    
    public void loadData(){
		
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					Document doc = Jsoup.connect(url).get();
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
							System.out.println(element.baseUri()+"==="+index+"==="+element.attr("data-src"));
						}
						index++;
					}
					
					for(int i = 0; i < videos.size(); i++){
						Element e = new Element(Tag.valueOf("video"), url);
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
			
			protected void onPostExecute(String result) {
				Log.d(TAG, result);
				webView.loadDataWithBaseURL(null, result, "text/html", "UTF-8", null);
			};
		}.execute();
    }
}
