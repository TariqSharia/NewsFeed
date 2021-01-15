package com.example.newsfeed;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class ContentAdapter  extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    List<Article> articles;


    public ContentAdapter(List<Article> articles) {
        this.articles=articles;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        ImageView imageView = (ImageView) cardView.findViewById(R.id.image);
        Glide.with(cardView).load(articles.get(position).getImage_url()).into(imageView);

        TextView title = (TextView) cardView.findViewById(R.id.title);
        title.setText(articles.get(position).getTitle());

        TextView description = (TextView) cardView.findViewById(R.id.description);
        description.setText(articles.get(position).getDescription());

        TextView source = (TextView) cardView.findViewById(R.id.source);
        source.setText(articles.get(position).getSource());

        TextView date = (TextView) cardView.findViewById(R.id.publishDate);
        date.setText(articles.get(position).getPublishDate().split("T")[0]);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO OPEN NEWS WEBPAGE
                Uri uriUrl = Uri.parse(articles.get(position).getUrl());
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                v.getContext().startActivity(launchBrowser);
            }
        });


    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;

        public ViewHolder(CardView cardView){
            super(cardView);
            this.cardView = cardView;
        }
    }
}
