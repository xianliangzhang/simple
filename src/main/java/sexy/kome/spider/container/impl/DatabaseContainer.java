package sexy.kome.spider.container.impl;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import sexy.kome.core.helper.CacheHelper;
import sexy.kome.spider.container.Container;
import sexy.kome.spider.container.impl.mapper.DocumentUrlMapper;
import sexy.kome.spider.container.impl.mapper.FileUrlMapper;
import sexy.kome.spider.model.DocumentUrl;
import sexy.kome.spider.model.FileUrl;

/**
 * Created by Hack on 2016/12/24.
 */
public class DatabaseContainer implements Container {
    private static final Logger RUN_LOG = Logger.getLogger(DatabaseContainer.class);

    @Override
    public void saveUnvisitedDocumentURL(String url) {
        try (SqlSession session = CacheHelper.getSqlSessionFactory().openSession()) {
            DocumentUrlMapper mapper = session.getMapper(DocumentUrlMapper.class);
            mapper.save( new DocumentUrl(url) );
            session.commit();
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public String getUnvisitedDocumentURL() {
        try (SqlSession session = CacheHelper.getSqlSessionFactory().openSession()) {
            DocumentUrlMapper mapper = session.getMapper(DocumentUrlMapper.class);
            DocumentUrl documentUrl = mapper.lookupNextUnvisitedUrl();
            return documentUrl == null ? null : documentUrl.getUrl();
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean hasVisitedImageURL(String url) {
        try (SqlSession session = CacheHelper.getSqlSessionFactory().openSession()) {
            FileUrlMapper mapper = session.getMapper(FileUrlMapper.class);
            FileUrl fileUrl = mapper.lookupByUrl(url);
            return fileUrl != null;
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void saveVisitedImageURL(String url) {
        try (SqlSession session = CacheHelper.getSqlSessionFactory().openSession()) {
            FileUrlMapper mapper = session.getMapper(FileUrlMapper.class);
            mapper.save( new FileUrl(url) );
            session.commit();
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        Container container = new DatabaseContainer();
        container.saveUnvisitedDocumentURL("xx");
        RUN_LOG.info("Saved-Document-URL: xx");

        String xx = container.getUnvisitedDocumentURL();
        RUN_LOG.info("Load-Unvisited-Document-URL: " + xx);

//        container.saveVisitedImageURL("yy");
//        RUN_LOG.info("Saved-Image-URL: yy");
//
//        boolean v = container.hasVisitedImageURL("yy");
//        RUN_LOG.info("Load-Visited-URL: " + v);

    }
}
