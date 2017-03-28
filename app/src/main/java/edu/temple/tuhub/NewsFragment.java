package edu.temple.tuhub;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by mangaramu on 3/12/2017.
 */

public class NewsFragment extends Fragment {
    WebView newsview;
     String news;
     String newsurl;

    public void onAttach(Context context) {


        super.onAttach(context);
    }

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.newsfrag,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        newsview = (WebView) getActivity().findViewById(R.id.newsview);
        newsview.loadDataWithBaseURL(newsurl,news,"html/text","UTF-8",newsurl);
        WebSettings webSettings = newsview.getSettings();
        newsview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void Setnews(String x)
    {
        news=x;
    }
}
