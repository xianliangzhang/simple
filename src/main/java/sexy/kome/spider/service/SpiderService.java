package sexy.kome.spider.service;


import org.apache.ibatis.javassist.bytecode.CodeAttribute;
import sexy.kome.spider.mapper.SpiderFileMapper;
import sexy.kome.spider.mapper.SpiderURLMapper;
import sexy.kome.spider.model.SpiderFile;
import sexy.kome.spider.model.SpiderFileType;
import sexy.kome.spider.model.SpiderURL;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import sexy.kome.core.helper.CacheHelper;
import sexy.kome.spider.model.SpiderURLStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hack on 2016/12/2.
 */
public class SpiderService {
    private static final Logger RUN_LOG = Logger.getLogger(SpiderService.class);
    private static final  SqlSessionFactory SQL_SESSION_FACTORY = CacheHelper.getSqlSessionFactory(CacheHelper.DBIdentifier.KOME);

    public void saveFile(SpiderFile spiderFile) {
        try (SqlSession session = SQL_SESSION_FACTORY.openSession()) {
            SpiderFileMapper mapper = session.getMapper(SpiderFileMapper.class);

            List<SpiderFile> files = mapper.lookupBySimple(SpiderFile.newSpiderFile(spiderFile.getMd5()));
            if (files.isEmpty()) {
                mapper.save(spiderFile);
                session.commit();
                RUN_LOG.info(String.format("File-Saved [identifier=%s, size=%d]", spiderFile.getFileIdentifier(), spiderFile.getSize()));
            } else {
                RUN_LOG.info(String.format("File-Exists [identifier=%s, size=%d]", spiderFile.getFileIdentifier(), spiderFile.getSize()));
            }
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
    }

    public List<SpiderFile> lookupFilesByMd5(String md5) {
        try (SqlSession session = SQL_SESSION_FACTORY.openSession()) {
            SpiderFile simple = SpiderFile.newSpiderFile(md5);
            List<SpiderFile> files = session.getMapper(SpiderFileMapper.class).lookupBySimple(simple);
            return files;
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return new ArrayList<SpiderFile>();
    }

    public void saveURL(SpiderURL spiderURL) {
        try (SqlSession session = SQL_SESSION_FACTORY.openSession()) {
            SpiderURLMapper mapper = session.getMapper(SpiderURLMapper.class);
            if (mapper.lookupBySimple(SpiderURL.newSpiderURL(spiderURL.getUrl())).isEmpty()) {
                mapper.save(spiderURL);
                session.commit();
                RUN_LOG.info(String.format("URL-Saved [url=%s]", spiderURL.getUrl()));
            } else {
                RUN_LOG.warn(String.format("URL-Exists [url=%s]", spiderURL.getUrl()));
            }
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
    }

    public void updateURLStatus(SpiderURL spiderURL) {
        try (SqlSession session = SQL_SESSION_FACTORY.openSession()) {
            session.getMapper(SpiderURLMapper.class).updateStatus(spiderURL);
            session.commit();
        }
    }

    public List<SpiderURL> lookupUnvisitedURLs() {
        return lookupSpiderURLBySimple(SpiderURL.newSpiderURL(SpiderURLStatus.UNVISITED));
    }

    public SpiderURL lookupSpiderURLByURL(String url) {
        List<SpiderURL> urls = lookupSpiderURLBySimple(SpiderURL.newSpiderURL(url));
        return urls.isEmpty() ? null : urls.get(0);
    }

    private List<SpiderURL> lookupSpiderURLBySimple(SpiderURL simple) {
        try (SqlSession session = SQL_SESSION_FACTORY.openSession()) {
            return session.getMapper(SpiderURLMapper.class).lookupBySimple(simple);
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return new ArrayList<SpiderURL>();
    }

    public static void main(String[] args) {
        SpiderService spiderService = new SpiderService();
        SpiderFile file = new SpiderFile();
        file.setFileIdentifier("md5.jps");
        file.setMd5("md5");
        file.setSize(2000L);
        file.setType(SpiderFileType.IMAGE);
        file.setFromDocUrl("test-url");

        spiderService.saveFile(file);

        List<SpiderFile> files = spiderService.lookupFilesByMd5("md5");
        System.out.println(files.size());


    }
}
