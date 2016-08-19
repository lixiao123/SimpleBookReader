package org.foree.zetianji.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.foree.zetianji.R;
import org.foree.zetianji.book.Chapter;
import org.foree.zetianji.helper.BQGWebSiteHelper;
import org.foree.zetianji.net.NetCallback;

/**
 * Created by foree on 16-7-21.
 */
public class ArticleActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    BQGWebSiteHelper apiHelper;
    private static final String TAG = ArticleActivity.class.getSimpleName();
    TextView tv;
    Chapter chapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    String webChar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        tv = (TextView)findViewById(R.id.tv_content);
        Bundle bundle = getIntent().getExtras();
        chapter = (Chapter)bundle.getSerializable("chapter");
        webChar = bundle.getString("web_char");
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_ly);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(chapter.getTitle());
        }

        syncArticleContent();

    }
    private void syncArticleContent(){
        mSwipeRefreshLayout.setRefreshing(true);
        apiHelper = new BQGWebSiteHelper();
        apiHelper.getChapterContent(chapter.getUrl(), webChar, new NetCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if(data != null) {
                //    Log.d(TAG, data);
                    updateUI(data);
                }else{
                    errorUI();
                }
            }

            @Override
            public void onFail(String msg) {
                errorUI();
            }
        });
    }

    private void errorUI(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // use textView format
                tv.setText("no data");
                mSwipeRefreshLayout.setRefreshing(false);
                Snackbar.make(mSwipeRefreshLayout, "获取数据失败，请下拉刷新重新获取", Snackbar.LENGTH_LONG).show();
            }
        }, 0);

    }
    private void updateUI(final String data){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // use textView format
                tv.setText(Html.fromHtml(data));
                mSwipeRefreshLayout.setRefreshing(false);
                Snackbar.make(mSwipeRefreshLayout, "加载成功", Snackbar.LENGTH_SHORT).show();
            }
        }, 0);


    }

    @Override
    public void onRefresh() {
        syncArticleContent();
    }
}
