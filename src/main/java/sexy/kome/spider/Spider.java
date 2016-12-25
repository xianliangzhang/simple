package sexy.kome.spider;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sexy.kome.core.helper.ConfigHelper;
import sexy.kome.spider.container.Container;
import sexy.kome.spider.container.impl.DatabaseContainer;
import sexy.kome.spider.processer.Processor;
import sexy.kome.spider.processer.impl.ImageProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Hack on 2016/11/27.
 */
public class Spider {
    private static final Logger RUN_LOG = Logger.getLogger(Spider.class);
    public static final int MAX_URL_LENGTH = 100;

    private Set<Processor> PROCESSORS = new HashSet<Processor>();
    public Container container;
    public boolean running = true;  // 爬虫先生继续爬

    private Spider(String originURL, Container container, Class<? extends Processor>... processors) {
        this.container = container;
        container.saveUnvisitedDocumentURL(originURL);

        for (int i = 0; i < processors.length; i++) {
            try {
                Processor processor = processors[i].getConstructor(Spider.class).newInstance(this);
                PROCESSORS.add(processor);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void start() {
        while (running) {
            try {
                String documentURL = container.getUnvisitedDocumentURL();
                if (StringUtils.isEmpty(documentURL)) {
                    running = false;
                    throw new RuntimeException("No-More-Unvisited-Document-URL........");
                }
                RUN_LOG.info(String.format("Start to process Document-URL: " + documentURL));
                Document document = Jsoup.connect(documentURL).timeout(3000).get();

                // 处理文档中感兴趣的元素
                PROCESSORS.forEach(processor -> {
                    processor.process(document);
                });

                // 处理文档中的链接
                document.select("a[href]").forEach(link -> {
                    String targetURL = link.attr("abs:href");
                    RUN_LOG.info("");
                    if (targetURL.length() <= MAX_URL_LENGTH && !targetURL.endsWith(".exe")) {
                        container.saveUnvisitedDocumentURL(targetURL);
                    }
                });
            } catch (Exception e) {
                RUN_LOG.error(e.getMessage(), e);
            }
        }
    }

    public Container getContainer() {
        return container;
    }

    public static void main(String[] args) throws Exception {
        String originURL = args.length > 0 ? args[0] : ConfigHelper.get("spider.source.url");
        new Spider(originURL, new DatabaseContainer(), ImageProcessor.class).start();
    }

}
