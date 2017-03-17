package org.foree.bookreader.homepage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import org.foree.bookreader.R;
import org.foree.bookreader.bean.book.Book;
import org.foree.bookreader.bookinfopage.BookInfoActivity;
import org.foree.bookreader.net.NetCallback;
import org.foree.bookreader.parser.AbsWebParser;
import org.foree.bookreader.parser.WebParserManager;

import java.util.ArrayList;
import java.util.List;

public class BookStoreFragment extends Fragment {
    private ExpandableListView mExpandableListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ExpandableListAdapter mAdapter;
    private List<List<Book>> bookStoreList;
    private List<Book> categoryList;

    public static BookStoreFragment newInstance() {
        BookStoreFragment fragment = new BookStoreFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        syncBookStoreInfo();

    }

    private void syncBookStoreInfo() {
        // generate test data
        bookStoreList = new ArrayList<>();
//
//        for (int i = 0; i < 7; i++) {
//            categoryList = new ArrayList<>();
//            for (int j = 0; j < 4; j++) {
//                Book book = new Book("book" + j, "group" + i);
//                categoryList.add(book);
//            }
//            bookStoreList.add(categoryList);
//        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_store, container, false);

        //mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        //mSwipeRefreshLayout.setOnRefreshListener(this);

        mExpandableListView = (ExpandableListView) view.findViewById(R.id.el_book_store);
        initExpandableListView();

        return view;
    }

    private void initExpandableListView() {
        final Context context = this.getContext();
        AbsWebParser webParser = WebParserManager.getInstance().getWebParser("http://www.biquge.cn/xxxx");
        webParser.getHomePageInfo("http://www.biquge.cn", new NetCallback<List<List<Book>>>() {
            @Override
            public void onSuccess(List<List<Book>> data) {
                bookStoreList.addAll(data);
                mAdapter = new BookStoreExpandableListAdapter(context, bookStoreList);
                mExpandableListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                return true;
                            }
                        });
                        mExpandableListView.setGroupIndicator(null);

                        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                Intent intent = new Intent(getActivity(), BookInfoActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("book_url", bookStoreList.get(groupPosition).get(childPosition).getBookUrl());
                                intent.putExtras(bundle);

                                startActivity(intent);
                                Log.d("BookStoreFragment", "onChildClick");
                                return true;
                            }
                        });

                        mExpandableListView.setAdapter(mAdapter);
                        for (int i = 0; i < bookStoreList.size(); i++) {
                            mExpandableListView.expandGroup(i);
                        }
                    }
                }, 500);

            }

            @Override
            public void onFail(String msg) {

            }
        });

    }

}
