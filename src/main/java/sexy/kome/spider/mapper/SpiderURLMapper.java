package sexy.kome.spider.mapper;

import sexy.kome.spider.model.SpiderURL;

import java.util.List;

/**
 * Created by Hack on 2016/12/2.
 */
public interface SpiderURLMapper {
    List<SpiderURL> lookupBySimple(SpiderURL spiderURL);

    void save(SpiderURL url);
    void updateStatus(SpiderURL url);
}
