package sexy.kome.spider.container.impl.mapper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import sexy.kome.core.helper.CacheHelper;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by Hack on 2016/12/18.
 */
public class SpiderURLMapper {
    private static final Logger RUN_LOG = Logger.getLogger(SpiderURLMapper.class);
    private static final Connection CONNECTION = CacheHelper.newConnection(CacheHelper.DBIdentifier.KOME);

    private static final String INSERT_MAPPER =
            "insert into spider_url(type, status, url, create_time, update_time) " +
                    "select '%s', '%s', '%s', now(), now() from dual where not exists (select * from spider_url where url = '%s')";

    public boolean save(SpiderURL spiderURL) {
        boolean result = false;
        try {
            String sql = String.format(INSERT_MAPPER, spiderURL.getType(), spiderURL.getStatus(), spiderURL.getUrl(), spiderURL.getUrl());
            synchronized (CONNECTION) {
                result = CONNECTION.createStatement().execute(sql);
                CONNECTION.commit();
            }
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return result;
    }

    public boolean isURLExists(String url) {
        try {
            String sql = String.format("select count(*) count from spider_url where url='%s'", url);
            ResultSet rs = null;
            synchronized (CONNECTION) {
                rs = CONNECTION.createStatement().executeQuery(sql);
            }
            rs.next();
            return rs.getInt("count") > 0;
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return false;
    }

    public String lookupUnvisitedDocumentURL() {
        try {
            String sql = "select url from spider_url where status = 'UNVISITED' and type = 'DOCUMENT_URL' AND length(url)>1 limit 1";
            String update = "update spider_url set status = 'VISITED' where url='%s' and type='DOCUMENT_URL'";
            ResultSet rs = null;
            synchronized (CONNECTION) {
                rs = CONNECTION.createStatement().executeQuery(sql);

                if (rs.next()) {
                    String targetURL = rs.getString("url");
                    if (!StringUtils.isEmpty(targetURL)) {
                        CONNECTION.createStatement().executeUpdate(String.format(update, targetURL));
                        CONNECTION.commit();
                    }
                    return targetURL;
                }
            }
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }
}
