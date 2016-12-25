package sexy.kome.spider.container.impl.mapper;

import sexy.kome.spider.model.FileUrl;

/**
 * Created by Hack on 2016/12/23.
 */
public interface FileUrlMapper {
    void save(FileUrl url);
    FileUrl lookupByUrl(String url);
}
