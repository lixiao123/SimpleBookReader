package org.foree.bookreader.website;

import org.foree.bookreader.book.Article;
import org.foree.bookreader.book.Book;
import org.foree.bookreader.book.Chapter;
import org.foree.bookreader.net.NetCallback;
import org.foree.bookreader.parser.IWebParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

/**
 * Created by foree on 17-1-7.
 */

public abstract class WebInfo implements IWebParser{
    private static final String TAG = WebInfo.class.getSimpleName();

    protected String name;
    protected String web_char;
    protected String url;
    protected String searchApi;

    abstract List<Book> parseBookList(Document doc);
    abstract Book parseBookInfo(Document doc);
    abstract List<Chapter> parseChapterList(Document doc);
    abstract Article parseArticle(Document doc);

    @Override
    public void searchBook(final String keywords, final NetCallback<List<Book>> netCallback) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Document doc;
                try {
                    doc = Jsoup.connect(searchApi + keywords).get();
                    if( netCallback != null && doc != null){
                        netCallback.onSuccess(parseBookList(doc));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if( netCallback != null){
                        netCallback.onFail(e.toString());
                    }
                }
            }
        }.start();
    }

    @Override
    public void getBookInfo(final String bookUrl, final NetCallback<Book> netCallback) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Document doc;
                try {
                    doc = Jsoup.connect(bookUrl).get();
                    if( netCallback != null && doc != null){
                        netCallback.onSuccess(parseBookInfo(doc));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if( netCallback != null){
                        netCallback.onFail(e.toString());
                    }
                }
            }
        }.start();

    }

    @Override
    public void getChapterList(final String bookUrl, final NetCallback<List<Chapter>> netCallback) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Document doc;
                try {
                    doc = Jsoup.connect(bookUrl).get();
                    if( netCallback != null && doc != null){
                        netCallback.onSuccess(parseChapterList(doc));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if( netCallback != null){
                        netCallback.onFail(e.toString());
                    }
                }
            }
        }.start();
    }

    @Override
    public void getArticle(final String chapterUrl, final NetCallback<Article> netCallback) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Document doc;
                try {
                    doc = Jsoup.connect(chapterUrl).get();
                    if( netCallback != null && doc != null){
                        netCallback.onSuccess(parseArticle(doc));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if( netCallback != null){
                        netCallback.onFail(e.toString());
                    }
                }
            }
        }.start();
    }
}