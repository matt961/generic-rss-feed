package okc.matt.genericrssfeed;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import MattsRSSUtils.RSSParser;
import MattsRSSUtils.RSSChannel;
import MattsRSSUtils.RSSFetcher;

public class ChannelSelectActivity extends AppCompatActivity {

    public ArrayList<RSSChannel> rssChannels;

    private static final String SP_FEED_URLS = "SP_FEED_URLS";
    private static final String SP_DELIMITER = ",";

    private ArrayList<String> feedUrlsList;

    private SharedPreferences sharedPreferences;

    private RSSChannelAdapter adapter;

    private RecyclerView channelRecyclerView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        feedUrlsList = new ArrayList<>();
        rssChannels = new ArrayList<>();

        // get saved list of feeds
        sharedPreferences =
                this.getSharedPreferences("okc.matt.genericrssfeed.USER_RSS_FEEDS", MODE_PRIVATE);
        final String[] feedsFromPrefs =
                sharedPreferences.getString(SP_FEED_URLS, "").split(SP_DELIMITER);
        Log.v("ChannelSelectActivity",
                String.valueOf(feedsFromPrefs.length));
        for (String pref : feedsFromPrefs)
            Log.v("ChannelSelectActivity", pref);

        Collections.addAll(feedUrlsList, feedsFromPrefs);


        channelRecyclerView = (RecyclerView) findViewById(R.id.channelRV);
        adapter = new RSSChannelAdapter(this, rssChannels);
        channelRecyclerView.setAdapter(adapter);
        channelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnLongClickListener(new RSSChannelAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, final int position) {
                final RSSChannel toDelete = rssChannels.remove(position);
                printRssChannels("onItemLongClick");
                adapter.notifyItemRemoved(position);
                updateSharedPrefs();
                View mainView = findViewById(R.id.activity_channel_select_id);
                if (mainView != null) {
                    Snackbar.make(mainView, String.format("Removed %s from your list.", toDelete.getTitle()), Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rssChannels.add(position, toDelete);
                                    adapter.notifyItemInserted(position);
                                    updateSharedPrefs();
                                }
                            })
                            .show();
                }
            }
        });
        adapter.setOnItemClickListener(new RSSChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent viewFeed = new Intent();
                viewFeed.putExtra("FEED_URL", rssChannels.get(position).getLink());
                viewFeed.putExtra("FEED_TITLE", rssChannels.get(position).getTitle());
                viewFeed.setClass(channelRecyclerView.getContext(), ItemViewerActivity.class);
                startActivity(viewFeed);
            }
        });
        AsyncFeedDownloader fd;
        if (feedsFromPrefs.length != 0) {
            // download the feeds and get the parsed RSSChannels and put them in rssChannels ArrayList
            fd = new AsyncFeedDownloader();
            fd.execute(feedsFromPrefs);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText txtUrl = new EditText(view.getContext());
                    txtUrl.setHint("Example: http://spectrum.ieee.org/rss");

                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Add a feed")
                            .setMessage("Paste a link to an RSS Feed here")
                            .setView(txtUrl)
                            .setPositiveButton("Add",
                                    new DialogInterface
                                            .OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            String newUrl = txtUrl
                                                    .getText()
                                                    .toString();
                                            // check if invalid URL before adding to shprefs
                                            try {
                                                new URL(newUrl);
                                            } catch (MalformedURLException mfe) {
                                                Log.v("ChannelSelectActivity",
                                                        String.format(
                                                                "Not adding to shared prefs URL \"%s\".",
                                                                newUrl));
                                                Toast.makeText(ChannelSelectActivity.this,
                                                        "Invalid URL, Try Again.",
                                                        Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            AsyncTask newFeed = new AsyncFeedDownloader()
                                                    .execute(newUrl);
                                            Log.v(String.format(
                                                    "AsyncFeedDownloader on \"%s\".",
                                                    newUrl),
                                                    String.format("STATUS = %s",
                                                            newFeed.getStatus()));
                                        }
                                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // do nothing
                                }
                            })
                            .show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_channel_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_clearlist) {
            clearFeedsFromSharedPrefs();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AsyncFeedDownloader extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            if (params.length == 0) return null;

            // init to default location if one new feed is to be added (for RSSChannelAdapter)
            // 0 for no items added yet for adapter.notifyItemRangeInserted
            Integer newFeedsCount = 0;

            for (String url : params) {
                if (url.isEmpty()) {
                    continue;
                }
                try {
                    Log.v("AsyncFeedDownloader", String.format("Downloading \"%s\"", url));
                    RSSFetcher f = new RSSFetcher(new URL(url));
                    int success = f.fetchDocument();
                    if (success == 0) {
                        RSSParser parser = new RSSParser(f.getRssDocument());
                        parser.parseDocument();
                        RSSChannel newChannel;
                        if ((newChannel = parser.getChannelMetaData()) != null) {
                            Log.v("AsyncFeedDownloader",
                                    String.format("Added new channel \"%s\"",
                                            newChannel.getTitle()));
                            newChannel.setLink(url);
                            rssChannels.add(newChannel);
                            updateSharedPrefs();
                            printRssChannels("AsyncFeedDownloader");
                            newFeedsCount++;
                        }
                    }
                } catch (MalformedURLException mue) {
                    Log.v("AsyncFeedDownloader", "Bad URL, did not download.");
                }
            }
            return newFeedsCount;
        }

        @Override
        protected void onPostExecute(Integer newFeedsCount) {
            super.onPostExecute(newFeedsCount);
            if (newFeedsCount > 0) {
                Log.v("AsyncFeedDownloader",
                        String.format(
                                "Updated RSSChannelAdapter with %d items."
                                , newFeedsCount));
                adapter.notifyDataSetChanged();
                channelRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void clearFeedsFromSharedPrefs() {
        sharedPreferences
                .edit()
                .putString(SP_FEED_URLS, "")
                .apply();
    }

    private void updateSharedPrefs() {
        StringBuilder newListForSP = new StringBuilder();
        for (RSSChannel rssChannel : rssChannels) {
            Log.v("updateSharedPrefs", String.format("URL is \"%s\"", rssChannel.getLink()));
            newListForSP
                    .append(rssChannel.getLink())
                    .append(SP_DELIMITER);
        }
        Log.v("updateSharedPrefs", newListForSP.toString());
        sharedPreferences
                .edit()
                .putString(SP_FEED_URLS, newListForSP.toString())
                .apply();
    }

    private void printRssChannels(String logVTag) {
        for (String feedUrl : feedUrlsList)
            Log.v(logVTag, String.format("URL is \"%s\"", feedUrl));
    }
}
