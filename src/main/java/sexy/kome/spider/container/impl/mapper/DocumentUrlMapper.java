package sexy.kome.spider.container.impl.mapper;

import sexy.kome.spider.model.DocumentUrl;

/**
 * Created by Hack on 2016/12/23.
 */
public interface DocumentUrlMapper {
    void save(DocumentUrl doc);
    void update(DocumentUrl doc);
    DocumentUrl lookupNextUnvisitedUrl();
}
