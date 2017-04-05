package okc.matt.genericrssfeed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import MattsRSSUtils.RSSChannel;

public class RSSChannelAdapter extends RecyclerView.Adapter<RSSChannelAdapter.ViewHolder> {

    private ArrayList<RSSChannel> rssChannels;
    private Context channelSelectContext;

    private OnItemLongClickListener onLongClickListener;
    private OnItemClickListener onItemClickListener;

    // used for deleting a feed
    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnLongClickListener(OnItemLongClickListener listener) {
        this.onLongClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public RSSChannelAdapter(Context context, ArrayList<RSSChannel> rssChannels) {
        this.rssChannels = rssChannels;
        this.channelSelectContext = context;
    }

    private Context getContext() {
        return channelSelectContext;
    }

    @Override
    public RSSChannelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemChannel = inflater.inflate(R.layout.item_channel, parent, false);

        return new ViewHolder(itemChannel);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RSSChannel channel = rssChannels.get(position);

        TextView titleText = holder.titleText,
                lastpubdateText = holder.lastpubdateText,
                descriptionText = holder.descriptionText,
                categoryText = holder.categoryText;
        titleText.setText(channel.getTitle());
        if (channel.getLastBuildDate() != null) {
            lastpubdateText.setText(channel.getLastBuildDate().toString());
        } else
            lastpubdateText.setText(String.format("Downloaded %s", new Date().toString()));
        descriptionText.setText(channel.getDescription());
        categoryText.setText(channel.getCategory());
    }

    @Override
    public int getItemCount() {
        return rssChannels.size();
    }

    //===========================================================================
    // ============================== VIEWHOLDER ==============================
    //===========================================================================
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText,
                lastpubdateText,
                descriptionText,
                categoryText;

        public ViewHolder(final View itemView) {
            super(itemView);

            titleText = (TextView) itemView.findViewById(R.id.titleText);
            lastpubdateText = (TextView) itemView.findViewById(R.id.lastpubdateText);
            descriptionText = (TextView) itemView.findViewById(R.id.descriptionText);
            categoryText = (TextView) itemView.findViewById(R.id.categoryText);

            // for deleting a feed
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onLongClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onLongClickListener.onItemLongClick(itemView, position);
                            return true;
                        }
                    }
                    return false;
                }
            });

            // for viewing a feed's items
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }
}