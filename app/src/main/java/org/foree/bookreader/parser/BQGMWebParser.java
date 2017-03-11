package org.foree.bookreader.parser;

import android.text.Html;
import android.util.Log;

import org.foree.bookreader.bean.book.Book;
import org.foree.bookreader.bean.book.Chapter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by foree on 17-1-18.
 */

public class BQGMWebParser extends AbsWebParser {
    private final static String TAG = BQGMWebParser.class.getSimpleName();

    @Override
    String getHostName() {
        return "笔趣阁移动版";
    }

    @Override
    String getWebChar() {
        return "utf-8";
    }

    @Override
    String getHostUrl() {
        return "http://m.biquge.com";
    }

    @Override
    String getSearchApi() {
        return "http://zhannei.baidu.com/cse/search?s=287293036948159515&q=";
    }

    @Override
    List<Book> parseBookList(Document doc) {
        List<Book> bookList = new ArrayList<>();
        Elements resultList = doc.getElementsByClass("result-game-item");
        for (Element result : resultList) {
            Book book = new Book();
            //Log.d(TAG, result.toString());
            Elements titles = result.getElementsByClass("result-game-item-title-link");
            //Log.d(TAG, titles.toString());
            Element title = titles.get(0);

            Log.d(TAG, title.attr("href").replace("www", "m"));
            Log.d(TAG, title.attr("title"));

            book.setBookName(title.attr("title"));
            book.setBookUrl(title.attr("href").replace("www", "m"));
            bookList.add(book);

        }
        return bookList;
    }

    @Override
    Book parseBookInfo(String bookUrl, Document doc) {
        Book book = new Book();
        Chapter newestChapter = new Chapter();

        Elements updates = doc.getElementsByClass("block_txt2");
        if (updates != null && !updates.isEmpty()) {
            Element bookInfo = updates.get(0);
            Elements bookNames = bookInfo.getElementsByTag("h2");
            if (bookNames != null && !bookNames.isEmpty()) {
                book.setBookName(bookNames.get(0).text());
            }

            Elements bookOthers = bookInfo.getElementsByTag("p");
            if (bookOthers != null && !bookOthers.isEmpty()) {
                for (Element otherInfo : bookOthers) {
                    String otherInfoString = otherInfo.text();
                    Log.d(TAG, otherInfo.toString());
                    if (otherInfoString.contains("：")) {
                        switch (otherInfoString.split("：")[0]) {
                            case "作者":
                                book.setAuthor(otherInfoString.split("：")[1]);
                                break;
                            case "分类":
                                book.setCategory(otherInfoString.split("：")[1]);
                                break;
                            case "更新":
                                book.setUpdateTime(otherInfoString.split("：")[1]);
                                break;
                            case "最新":
                                Elements newest_ele = otherInfo.getElementsByTag("a");
                                if (newest_ele != null && !newest_ele.isEmpty()) {
                                    newestChapter.setChapterTitle(otherInfo.text().split("：")[1]);
                                    newestChapter.setChapterUrl(getHostUrl() + newest_ele.get(0).attr("href"));
                                    newestChapter.setChapterId(getChapterId(newest_ele.get(0).attr("href")));
                                    newestChapter.setBookUrl(bookUrl);
                                }
                                break;
                        }

                    }
                }
            }
        }

        book.setNewestChapter(newestChapter);
        book.setBookUrl(bookUrl);

        Elements descrptions = doc.getElementsByClass("intro_info");
        if (descrptions != null && !descrptions.isEmpty()) {
            Elements description = descrptions.get(0).getElementsByTag("p");
            if (description != null && !description.isEmpty()) {
                book.setDescription(description.get(0).text());
            }
        }

        String chapterListUrl = null;

        // get chapterListUrl by book website
        Elements chapterElements = doc.getElementsByClass("intro");
        for (Element infoNode : chapterElements) {
            if (infoNode.toString().contains("完整目录")) {
                if (infoNode.child(0) != null) {
                    chapterListUrl = getHostUrl() + infoNode.child(0).attr("href");
                    Log.d(TAG, "chapterListUrl = " + chapterListUrl);
                }
            }
        }

        book.setContentUrl(chapterListUrl);

        return book;
    }

    @Override
    List<Chapter> parseChapterList(String bookUrl, String contentUrl, Document doc) {
        List<Chapter> chapterList = new ArrayList<>();

        if (doc != null) {
            Elements chapters = doc.getElementsByClass("chapter");
            if (chapters != null && !chapters.isEmpty()) {
                Elements li = chapters.select("li");
                for (Element chapterNode : li) {
                    Chapter chapter = new Chapter();

                    Element chapterLink = chapterNode.child(0);
                    if (chapterLink != null) {
                        chapter.setBookUrl(bookUrl);
                        chapter.setChapterTitle(chapterLink.text());
                        chapter.setChapterUrl(getHostUrl() + chapterLink.attr("href"));
                        chapter.setChapterId(getChapterId(chapterLink.attr("href")));
                        Log.d(TAG, getHostUrl() + chapterLink.attr("href"));
                        Log.d(TAG, chapterLink.text());
                    }
                    chapterList.add(chapter);
                }
            }
        }

        return chapterList;
    }

    @Override
    Chapter parseChapterContents(String chapterUrl, Document doc) {
        Chapter chapter = new Chapter();

        // set ulr
        chapter.setChapterUrl(chapterUrl);

        // get chapter title
        Element titleElement = doc.getElementById("nr_title");
        if (titleElement != null) {
            Log.d(TAG, "Title" + titleElement.text());
            chapter.setChapterTitle(titleElement.text());
        }


        // get contents
        Element contentElement = doc.getElementById("nr1");
        if (contentElement != null) {
            Log.d(TAG, contentElement.text());
            chapter.setContents(Html.fromHtml(contentElement.toString()).toString());
        }

        return chapter;
    }

    @Override
    List<List<Book>> parseHostUrl(String hostUrl, Document doc) {
        return null;
    }
}
