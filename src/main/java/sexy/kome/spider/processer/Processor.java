package sexy.kome.spider.processer;

import org.jsoup.nodes.Document;

/**
 * Created by Hack on 2016/11/27.
 */
public interface Processor {
    void process(Document document);
}
