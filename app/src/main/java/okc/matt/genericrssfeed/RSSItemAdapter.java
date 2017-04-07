package okc.matt.genericrssfeed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import MattsRSSUtils.RSSItem;

public class RSSItemAdapter extends RecyclerView.Adapter<RSSItemAdapter.ViewHolder> {

    // this should be in MattsRSSUtils.RSSParser but I had issues with the classes not being
    // loaded at RUNTIME. Could not resolve the issue.
    private final Pattern descriptionPuller = Pattern.compile("<p>((.|\\n)*?)<");

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
        titleText.setText(item.getTitle().trim());
        if (item.getPubDate() != null) {
            pubDateText.setText(item.getPubDate().toString());
        } else
            pubDateText.setText(String.format("Downloaded %s", new Date().toString()));

        if (!item.getDescription().trim().isEmpty()) {
            final Matcher descriptionContents = descriptionPuller.matcher(item.getDescription());
            StringBuilder description = new StringBuilder();
            if (descriptionContents.find()) {
                if (!descriptionContents.group(1).trim().isEmpty()) {
                    description
                            .append(" ")
                            .append(descriptionContents.group(1).trim());
                    if (description.toString().endsWith(".")) {
                        description.append("..");
                    } else
                        description.append("...");
                    String descText = description.toString().replaceAll("(\\r|\\n|\\r\\n)+", " ").trim();
                    if (descText.length() > 1024)
                        linkText.setText(item.getLink());
                    else
                        linkText.setText(descText);
                } else {
                    linkText.setText(item.getLink());
                }
            } else {
                if (item.getDescription().length() > 512)
                    linkText.setText(item.getLink());
                else
                    linkText.setText(item.getDescription());
            }
        } else {
            linkText.setText(item.getLink());
        }

        if (item.getAuthor().trim().isEmpty()) {
            authorText.setVisibility(View.GONE);
        } else {
            authorText.setVisibility(View.VISIBLE);
            authorText.setText(item.getAuthor());
        }
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