package sexy.kome.spider.mapper;

import sexy.kome.spider.model.SpiderFile;

import java.util.List;

/**
 * Created by Hack on 2016/12/2.
 */
public interface SpiderFileMapper {
    void save(SpiderFile spiderFile);
    List<SpiderFile> lookupBySimple(SpiderFile spiderFile);
}
