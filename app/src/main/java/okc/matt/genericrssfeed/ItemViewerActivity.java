package okc.matt.genericrssfeed;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import MattsRSSUtils.RSSFetcher;
import MattsRSSUtils.RSSItem;
import MattsRSSUtils.RSSParser;


public class ItemViewerActivity extends AppCompatActivity {

    public ArrayList<RSSItem> feedItems;
    private RSSItemAdapter adapter;
    private RecyclerView itemsRecyclerView;
    Toolbar toolbar;

    @Override
    protected void onCreate(final Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_view_feed);
        toolbar = (Toolbar) findViewById(R.id.viewFeedToolbar);
        setSupportActionBar(toolbar);

        feedItems = new ArrayList<>();
        itemsRecyclerView = (RecyclerView) findViewById(R.id.feedRV);
        adapter = new RSSItemAdapter(this, feedItems);
        adapter.setOnArticleClickListener(new RSSItemAdapter.OnArticleClickListener() {
            @Override
            public void onArticleClick(View itemView, int position) {
                RSSItem clickedItem = feedItems.get(position);
                if (clickedItem.getLink() != null || !clickedItem.getLink().isEmpty()) {
                    Intent viewArticle = new Intent();
                    viewArticle.setAction(Intent.ACTION_VIEW);
                    viewArticle.setData(Uri.parse(feedItems.get(position).getLink()));
                    startActivity(viewArticle);
                } else {
                    Toast.makeText(ItemViewerActivity.this, "No page found for this article.", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        itemsRecyclerView.setAdapter(adapter);
        itemsRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        String feedUrl = this.getIntent().getExtras().getString("FEED_URL");
        String feedTitle = this.getIntent().getExtras().getString("FEED_TITLE");
        setTitle(feedTitle);
        try {
            Log.v("ItemViewerActivity", String.format("Starting download of \"%s\"", feedUrl));
            new AsyncFeedDownload().execute(new URL(feedUrl));
        } catch (MalformedURLException mfe) {
            Log.v("ItemViewerActivity", "Could not convert string into a valid URL");
            finish();
        }
    }

    private class AsyncFeedDownload extends AsyncTask<URL, Void, Void> {
        @Override
        protected Void doInBackground(URL... params) {
            try {
                RSSFetcher fetcher = new RSSFetcher(params[0]);
                if (fetcher.fetchDocument() == 0) {
                    RSSParser parser = new RSSParser(fetcher.getRssDocument());
                    Log.v("Document created?", String.valueOf(parser.docIsCreated()));
                    parser.parseDocument();
                    Log.v("AsyncFeedDownloader", String.format("Added items from \"%s\" to feed items list.", params[0]));
                    feedItems.addAll(parser.getFeedItems());
                    listFeedItems("AsyncFeedDownloader");
                }
            } catch (MalformedURLException mfe) {
                Log.v("AsyncFeedDownload", "Could not convert string into a valid URL");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }
    }

    private void listFeedItems(String logVTag) {
        for (RSSItem item : feedItems) {
            Log.v(logVTag, String.format("Item Title \"%s\"", item.getTitle()));
        }
    }
}
