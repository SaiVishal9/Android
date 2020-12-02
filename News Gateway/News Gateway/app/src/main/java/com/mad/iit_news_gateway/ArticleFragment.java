package com.mad.iit_news_gateway;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.squareup.picasso.Picasso;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ArticleFragment extends Fragment implements Serializable {


    public ArticleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment Article.
     */

    public static ArticleFragment newInstance(Article article) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putSerializable("article", article);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        assert getArguments() != null;
        outState.putSerializable("article", getArguments().getSerializable("article"));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final Article article;

        if (savedInstanceState == null) {
            assert getArguments() != null;
            article = (Article) getArguments().getSerializable("article");
        }
        else
            article = (Article) savedInstanceState.getSerializable("article");
        View v = inflater.inflate(R.layout.fragment_article, container, false);

        TextView titleTextView = (TextView) v.findViewById(R.id.title_TextView);
        TextView authorTexView = (TextView) v.findViewById(R.id.author_TextView);
        TextView dateTextView = (TextView) v.findViewById(R.id.date_TextView);
        TextView descriptionTextView = (TextView) v.findViewById(R.id.description);
        TextView indexTextView = (TextView) v.findViewById(R.id.index_TextView);
        final ImageButton imageButton = (ImageButton) v.findViewById(R.id.image);

        assert article != null;
        titleTextView.setText(article.getTitle());
        String author=article.getAuthor();
        if(author.equalsIgnoreCase("null"))
            authorTexView.setText("");
        else
            authorTexView.setText(article.getAuthor());
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM dd, yyyy hh:mm";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date da;
        String str = null;

        try {
            da = inputFormat.parse(article.getPublishedAt());
            assert da != null;
            str = outputFormat.format(da);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert str != null;
        if(str.equalsIgnoreCase("null"))
            dateTextView.setText("");
        else
            dateTextView.setText(str);

        descriptionTextView.setText(article.getDescription());
        indexTextView.setText(""+article.getIndex()+" of "+(article.getTotal()-90));

        if (article.getUrlToImage() != null){
            Picasso picasso = new Picasso.Builder(v.getContext()).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                    final String changedUrl = article.getUrlToImage().replace("http:", "https:");
                    picasso.load(changedUrl) .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder) .into(imageButton);
                }
            }).build();
            picasso.load(article.getUrlToImage()) .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder) .into(imageButton);
        } else {
            Picasso.get().load(article.getUrlToImage()).error(R.drawable.brokenimage).placeholder(R.drawable.missingimage);
        }

        final Intent i = new Intent((Intent.ACTION_VIEW));
        i.setData(Uri.parse(article.getUrl()));
        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });
        descriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });

        return v;
    }
}