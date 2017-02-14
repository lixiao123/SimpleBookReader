package org.foree.bookreader.pagination;

import org.foree.bookreader.data.book.Article;
import org.foree.bookreader.data.cache.PaginationCache;
import org.foree.bookreader.data.event.PaginationEvent;
import org.foree.bookreader.net.NetCallback;
import org.foree.bookreader.parser.AbsWebParser;
import org.foree.bookreader.parser.WebParserManager;
import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by foree on 17-2-7.
 */

public class RequestDispatcher extends Thread {
    private PriorityBlockingQueue<ArticleRequest> mPriorityQueue;

    public RequestDispatcher(PriorityBlockingQueue<ArticleRequest> priorityQueue) {
        this.mPriorityQueue = priorityQueue;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                ArticleRequest request = mPriorityQueue.take();
                String url = request.getUrl();

                Article article = PaginationCache.getInstance().get(url);

                if (article == null) {
                    downloadArticle(request, url);
                } else {
                    EventBus.getDefault().post(new PaginationEvent(article, PaginationEvent.STATE_SUCCESS));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadArticle(final ArticleRequest request, final String url) {
        if (url != null && !url.isEmpty()) {
            AbsWebParser absWebParser = WebParserManager.getInstance().getWebParser(url);
            absWebParser.getArticle(url, new NetCallback<Article>() {
                @Override
                public void onSuccess(Article data) {
                    if (data.getContents() != null) {
                        PaginateCore.splitPage(request.getPaginationArgs(), data);

                        // put cache
                        PaginationCache.getInstance().put(url, data);

                        // post
                        EventBus.getDefault().post(new PaginationEvent(data, PaginationEvent.STATE_SUCCESS));
                    } else {
                        EventBus.getDefault().post(new PaginationEvent(null, PaginationEvent.STATE_FAILED));
                    }
                }

                @Override
                public void onFail(String msg) {
                    EventBus.getDefault().post(new PaginationEvent(null, PaginationEvent.STATE_FAILED));
                }
            });
        }
    }

}
