package sexy.kome.spider.container.impl;

import org.apache.commons.lang.StringUtils;
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
            session.getMapper(DocumentUrlMapper.class).save( new DocumentUrl(url) );
            session.commit();
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public String getAndUpdateNextUnvisitedDocumentURL() {
        try (SqlSession session = CacheHelper.getSqlSessionFactory().openSession()) {
            DocumentUrlMapper mapper = session.getMapper(DocumentUrlMapper.class);

            DocumentUrl documentUrl = mapper.lookupNextUnvisitedUrl();
            if (null != documentUrl && !StringUtils.isEmpty(documentUrl.getUrl())) {
                mapper.updateStatus(documentUrl.getUrl(), DocumentUrl.STATUS_VISITED);
            }

            session.commit();
            return documentUrl == null ? null : documentUrl.getUrl();
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public void updateDocumentURLStatus(String url, String status) {
        try (SqlSession session = CacheHelper.getSqlSessionFactory().openSession()) {
            session.getMapper(DocumentUrlMapper.class).updateStatus(url, status);
            session.commit();
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean hasVisitedImageURL(String url) {
        try (SqlSession session = CacheHelper.getSqlSessionFactory().openSession()) {
            FileUrl fileUrl = session.getMapper(FileUrlMapper.class).lookupByUrl(url);
            return fileUrl != null;
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void saveVisitedImageURL(String url) {
        try (SqlSession session = CacheHelper.getSqlSessionFactory().openSession()) {
            session.getMapper(FileUrlMapper.class).save( new FileUrl(url) );
            session.commit();
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        Container container = new DatabaseContainer();
        container.saveUnvisitedDocumentURL("xx");
        RUN_LOG.info("Saved-Document-URL: xx");

        String xx = container.getAndUpdateNextUnvisitedDocumentURL();
        RUN_LOG.info("Load-Unvisited-Document-URL: " + xx);
//
//        container.saveVisitedImageURL("yy");
//        RUN_LOG.info("Saved-Image-URL: yy");
//
//        boolean v = container.hasVisitedImageURL("yy");
//        RUN_LOG.info("Load-Visited-URL: " + v);

    }
}
