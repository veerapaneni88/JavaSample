package us.tx.state.dfps.service.sscc.daoimpl;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.common.response.SSCCCatchmentsRes;
import us.tx.state.dfps.service.sscc.dao.SSCCCatchmentsDao;
import us.tx.state.dfps.service.sscc.dao.SSCCEmployeeDetailDao;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Repository
public class SSCCCatchmentsDaoImpl implements SSCCCatchmentsDao {

    @Autowired
    SessionFactory sessionFactory;

    private static final Logger logger = Logger.getLogger(SSCCCatchmentsDaoImpl.class);
    @Value("${SSCCCathmentService.getCatchmentsByregionJSON}")
    private String getSSCCCatchmentsJSONSql ;

    @Value("${SSCCCathmentService.getCatchmentsByregion}")
    private String getSSCCCatchmentsListSql ;

    @Override
    public String getCatchmentsByRegion(String region) {

        List<String> results = new ArrayList<>();

        logger.info("Fetching SSCC Catchments for region : " + region);
        Session session = sessionFactory.getCurrentSession();
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getSSCCCatchmentsJSONSql);
        query.addScalar("obj", StandardBasicTypes.STRING);
        query.setParameter("region", region);
        try {
            results = query.list();
        } catch (Exception ex) {
            logger.log(Priority.toPriority(Priority.FATAL_INT),ex.getLocalizedMessage());
        }
        SSCCCatchmentsRes response= new SSCCCatchmentsRes();
        return "[" + results.stream().map(e -> e).collect(Collectors.joining(","))+"]";
    }

    @Override
    public TreeMap<String, String> getCatchmentsByRegionMap(String region) {

        TreeMap<String, String> results = new TreeMap<>();
        List<Object[]> queryResults= new ArrayList<>();
        logger.info("Fetching SSCC Catchments for region : " + region);
        Session session = sessionFactory.getCurrentSession();
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getSSCCCatchmentsListSql);
        query.addScalar("value", StandardBasicTypes.STRING);
        query.addScalar("displayText", StandardBasicTypes.STRING);
        query.setParameter("region", region);
        try {
            queryResults = query.list();
        } catch (Exception ex) {
            logger.log(Priority.toPriority(Priority.FATAL_INT),ex.getLocalizedMessage());
            queryResults= new ArrayList<>();
        }
        SSCCCatchmentsRes response= new SSCCCatchmentsRes();
        queryResults.stream().forEach(e->results.put((String)e[0],(String)e[1]));
        return results;
    }

}
