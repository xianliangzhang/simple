package sexy.kome.spider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;

/**
 * Created by Hack on 2016/12/3.
 */
public class Test {
    private static final String DEFAULT_URL = "http://www.baidu.com";

    public static void main(String[] args) throws Exception {
        Document document = Jsoup.connect(DEFAULT_URL).timeout(5000).get();
//        System.out.print(document);

        Elements links = document.select("a[href]"); //带有href属性的a元素
        Elements pngs = document.select("img[src$=.png]");

        printElement(document.getElementsByTag("a"));

//        System.out.print(document.body());

    }

    private static void printElement(Elements elements) {
        System.out.println(" ---------------------");
        elements.forEach(element -> {
            System.out.println(element);

            printElement(element.getElementsByTag("a"));
        });
    }
}
