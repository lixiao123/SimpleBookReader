package org.foree.bookreader.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.foree.bookreader.R;
import org.foree.bookreader.book.Book;
import org.foree.bookreader.book.Chapter;
import org.foree.bookreader.dao.BookDao;
import org.foree.bookreader.data.event.PaginationEvent;
import org.foree.bookreader.pagination.PaginationArgs;
import org.foree.bookreader.pagination.PaginationLoader;
import org.foree.bookreader.ui.adapter.ItemListAdapter;
import org.foree.bookreader.ui.adapter.PageAdapter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by foree on 16-7-21.
 */
public class ArticleActivity extends AppCompatActivity {
    private static final String TAG = ArticleActivity.class.getSimpleName();
    FloatingActionButton turnNightMode;

    String chapterUrl, bookUrl;

    private List<Chapter> chapterList = new ArrayList<>();

    private BookDao bookDao;

    private int recentChapterId = -1;

    // view pager
    private ViewPager mViewPager;
    private PageAdapter pageAdapter;
    private TextView mTextView, mTvError, mTvLoading;

    // popWindow
    private PopupWindow popupWindow;
    private View rootView;
    private RecyclerView mRecyclerView;
    private ItemListAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_layout);

        // register EventBus
        EventBus.getDefault().register(this);

        // get chapterUrl and recentId
        bookDao = new BookDao(this);
        bookUrl = getIntent().getExtras().getString("book_url");
        openBook(bookUrl);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setUpLayoutViews();
        initTextView();

        notifyState(PaginationEvent.STATE_LOADING);

        /*turnNightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(turnFlag) {
                    // night Mode
                    tvContent.setTextColor(getResources().getColor(R.color.nightTextColor));
                    tvTitle.setTextColor(getResources().getColor(R.color.nightTextColor));
                    mSwipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.nightBackground));
                    turnFlag = false;
                }else{
                    // day Mode
                    tvContent.setTextColor(getResources().getColor(R.color.dayTextColor));
                    tvTitle.setTextColor(getResources().getColor(R.color.dayTextColor));
                    mSwipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.dayBackground));
                    turnFlag = true;
                }
            }
        });
*/

    }

    private void setUpLayoutViews() {
        //init textView
        mTvError = (TextView) findViewById(R.id.load_fail);
        mTvLoading = (TextView) findViewById(R.id.loading);

        mViewPager = (ViewPager) findViewById(R.id.book_pager);
        pageAdapter = new PageAdapter(getSupportFragmentManager());

        rootView = LayoutInflater.from(this).inflate(R.layout.activity_article, null);

        // get FloatActionButton
        turnNightMode = (FloatingActionButton) findViewById(R.id.fab);

        turnNightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow == null)
                    showPopup();
                else
                    popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
            }
        });

        mViewPager.setAdapter(pageAdapter);
    }

    private void initTextView() {
        mTextView = (TextView) findViewById(R.id.book_content);
        mTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Removing layout listener to avoid multiple calls
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                // init PaginationLoader
                PaginationLoader.getInstance().init(new PaginationArgs(mTextView.getWidth(),
                        mTextView.getHeight(),
                        mTextView.getLineSpacingMultiplier(),
                        mTextView.getLineSpacingExtra(),
                        mTextView.getPaint(),
                        mTextView.getIncludeFontPadding()));

                PaginationLoader.getInstance().loadPagination(chapterUrl);

            }
        });
    }

    private void notifyState(int state) {
        switch (state) {
            case PaginationEvent.STATE_FAILED:
                mTvLoading.setVisibility(View.GONE);
                mTvError.setVisibility(View.VISIBLE);
                break;
            case PaginationEvent.STATE_LOADING:
                mTvLoading.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.INVISIBLE);
                break;
            case PaginationEvent.STATE_SUCCESS:
                mTvLoading.setVisibility(View.GONE);
                mTvError.setVisibility(View.GONE);
                mViewPager.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PaginationEvent pageEvent) {
        notifyState(pageEvent.getState());
        Log.d("EventBus", "notifyState");
        if (pageEvent.getUrl().equals(chapterUrl))
            if( pageEvent.getPagination() != null) {
                pageAdapter.setPages(pageEvent.getPagination().getPages());
                mViewPager.setCurrentItem(0,false);
            }
    }

    @Override
    protected void onDestroy() {
        closeBook();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void showPopup() {
        View view = LayoutInflater.from(this).inflate(R.layout.popupwindow_layout, null);

        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);

        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(dp.heightPixels / 4 * 3);

        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_item_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));

        setUpRecyclerViewAdapter();
    }

    private void setUpRecyclerViewAdapter() {
        mAdapter = new ItemListAdapter(this, chapterList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ItemListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                chapterUrl = chapterList.get(position).getChapterUrl();
                recentChapterId = chapterList.get(position).getChapterId();
                popupWindow.dismiss();

                switchChapter();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void switchChapter() {
        notifyState(PaginationEvent.STATE_LOADING);
        PaginationLoader.getInstance().loadPagination(chapterUrl);
    }

    private void openBook(String bookUrl) {
        //get all chapter
        Book book = bookDao.findBookInfoByUrl(bookUrl);
        // get chapterList
        chapterList = book.getChapterList();
        // check init
        if (book.getRecentChapterId() == -1) {
            // get first chapter id
            setChapterId(bookUrl, chapterList.get(0).getChapterId());
            // update book object recentChapterId
            book.setRecentChapterId(chapterList.get(0).getChapterId());
        }

        // open by chapter id
        chapterUrl = bookDao.findChapterUrlById(book.getRecentChapterId());
    }

    private void setChapterId(String bookUrl, int newId) {
        bookDao.updateRecentChapterId(bookUrl, newId);
    }

    private void closeBook() {
        // set ChapterId
        if (recentChapterId != -1)
            bookDao.updateRecentChapterId(bookUrl, recentChapterId);
    }


}
