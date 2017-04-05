package okc.matt.genericrssfeed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import MattsRSSUtils.RSSItem;

public class RSSItemAdapter extends RecyclerView.Adapter<RSSItemAdapter.ViewHolder> {

    private ArrayList<RSSItem> channelItems;
    private Context context;

    private OnArticleClickListener listener;

    public interface OnArticleClickListener {
        void onArticleClick(View itemView, int position);
    }

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.listener = listener;
    }

    public RSSItemAdapter(Context context, ArrayList<RSSItem> channelItems) {
        this.channelItems = channelItems;
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public RSSItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemChannel = inflater.inflate(R.layout.item_feed_item, parent, false);

        return new ViewHolder(itemChannel);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RSSItem item = channelItems.get(position);

        TextView titleText = holder.titleText,
                authorText = holder.authorText,
                linkText = holder.linkText,
                pubDateText = holder.pubDateText;
        titleText.setText(item.getTitle());
        if (item.getPubDate() != null) {
            pubDateText.setText(item.getPubDate().toString());
        } else
            pubDateText.setText(String.format("Downloaded %s", new Date().toString()));
        linkText.setText(item.getLink());
        authorText.setText(item.getAuthor());
    }

    @Override
    public int getItemCount() {
        return channelItems.size();
    }

    //===========================================================================
    // ============================== VIEWHOLDER ==============================
    //===========================================================================
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText,
                authorText,
                linkText,
                pubDateText;

        public ViewHolder(final View itemView) {
            super(itemView);

            titleText = (TextView) itemView.findViewById(R.id.itemTitleText);
            authorText = (TextView) itemView.findViewById(R.id.itemAuthorText);
            linkText = (TextView) itemView.findViewById(R.id.itemLinkText);
            pubDateText = (TextView) itemView.findViewById(R.id.itemPubDateText);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onArticleClick(itemView, position);
                    }
                }
            });
        }
    }
}