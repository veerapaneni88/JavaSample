package us.tx.state.dfps.service.mobile.daoImpl;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.domain.MpsSyncStatistics;
import us.tx.state.dfps.mobile.SyncStatisticsDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.mobile.dao.MobileSetupDao;

import java.sql.Timestamp;
import java.util.Date;

@Repository
public class MobileSetupDaoImpl implements MobileSetupDao {
    private static final Logger log = Logger.getLogger(MobileSetupDaoImpl.class);
    @Autowired
    MessageSource messageSource;
    @Autowired
    private SessionFactory sessionFactory;
    @Value("${MobileSetupDaoImpl.getTokenBody}")
    private String getTokenBodySql;
    @Value("${MobileSetupDaoImpl.saveTokenBody}")
    private String saveTokenBodySql;
    @Value("${MobileSetupDaoImpl.deleteTokens}")
    private String deleteTokensSql;
    @Value("${MobileSetupDaoImpl.getSetupSyncUserSql}")
    private String getSetupSyncUserSql;
    @Value("${MobileSetupDaoImpl.getImpactPubSubscriptionSql}")
    private String getImpactPubSubscriptionSql;
    @Value("${MobileSetupDaoImpl.getStaticPubSubscriptionSql}")
    private String getStaticPubSubscriptionSql;
    @Value("${MobileSetupDaoImpl.getResourcePubSubscriptionSql}")
    private String getResourcePubSubscriptionSql;
    @Value("${MobileSetupDaoImpl.getInsertForMobileWorkerSql}")
    private String getInsertForMobileWorkerSql;

    @Value("${MobileSetupDaoImpl.getLastDownloadTimeSql}")
    private String getLastDownloadTimeSql;

    @Value("${MobileSetupDaoImpl.getCodesTableRowCountSql}")
    private String getCodesTableRowCountSql;

    @Value("${MobileSetupDaoImpl.getCodesTableEndDatedRowCountSql}")
    private String getCodesTableEndDatedRowCountSql;

    @Value("${MobileSetupDaoImpl.getMessageLastUpdateSql}")
    private String getMessageLastUpdateSql;


    public MobileSetupDaoImpl() {
    }

    @Override
    @SuppressWarnings("unchecked")

    public void completeSetup(CommonHelperReq commonHelperReq) {

        StringBuffer sql = new StringBuffer();
        Query query;
        try {
            // Create the synchronization user
            String strQuery = getSetupSyncUserSql;
            strQuery = strQuery.replace(":host", commonHelperReq.getHostName());
            strQuery = strQuery.replace(":port", commonHelperReq.getDataAcsInd());
            strQuery = strQuery.replace(":idPerson", commonHelperReq.getIdPerson().toString());
            query = sessionFactory.getCurrentSession().createSQLQuery(strQuery);
            query.uniqueResult();

            log.info(" Executed CREATE SYNCHRONIZATION USER");
        } catch (Exception e) {
            // if we get here it just means that it already exists
        }
        // Create the subscription to the IMPACT publication
        try {
            String strQuery = getImpactPubSubscriptionSql;
            strQuery = strQuery.replace(":idPerson", commonHelperReq.getIdPerson().toString());
            query = sessionFactory.getCurrentSession().createSQLQuery(strQuery);
            query.uniqueResult();
            log.info(" Executed CREATE SYNCHRONIZATION SUBSCRIPTION");
        } catch (Exception e) {
            // if we get here it just means that it already exists
        }

        // Create the subscription to the static publication
        try {
            String strQuery = getStaticPubSubscriptionSql;
            strQuery = strQuery.replace(":idPerson", commonHelperReq.getIdPerson().toString());
            query = sessionFactory.getCurrentSession().createSQLQuery(strQuery);
            query.uniqueResult();
            log.info(" Executed CREATE SYNCHRONIZATION SUBSCRIPTION TO static_pub");
        } catch (Exception e) {
            // if we get here it just means that it already exists
        }

        try {
            query = sessionFactory.getCurrentSession().createSQLQuery(getInsertForMobileWorkerSql);
            query.setParameter(0, commonHelperReq.getIdPerson());
            query.executeUpdate();

            log.info(" Executed insert into MOBILE_WORKER_TYPE");
        } catch (Exception e) {
            e.printStackTrace();
            //
        }

    }

    @Override
    public void saveTokenBody(String body) {
        sessionFactory.getCurrentSession().createSQLQuery(deleteTokensSql).executeUpdate();
        Query query = sessionFactory.getCurrentSession().createSQLQuery(saveTokenBodySql)
                .setParameter("tokenBody", body);
        query.executeUpdate();
    }

    @Override
    public String getTokenBody() {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getTokenBodySql);
        String tokenBody = (String) query.uniqueResult();
        return tokenBody;
    }

    /**
     * This method saves the gathered sync statistics info into
     * database.
     *
     * @param syncStatsDto
     */
    @Override
    public void populateSyncStats(SyncStatisticsDto syncStatsDto) {
        try {

            MpsSyncStatistics mpsSyncStatistics = new MpsSyncStatistics();
            //mpsSyncStatistics.setIdMpsSyncStatistics(idMpsSyncStatiscs.longValue());
            mpsSyncStatistics.setIdWkldPerson(syncStatsDto.getIdWkldPerson());
            mpsSyncStatistics.setDtSyncStart(syncStatsDto.getSyncStartTime());
            mpsSyncStatistics.setDtSyncEnd(syncStatsDto.getSyncEndTime());
            mpsSyncStatistics.setCdSyncType(syncStatsDto.getSzCdSyncType());
            mpsSyncStatistics.setTxtHostName(syncStatsDto.getSzHostName());
            mpsSyncStatistics.setTxtMpsVersion(syncStatsDto.getSzMpsVersion());
            mpsSyncStatistics.setTxtConnType(syncStatsDto.getSzCdConnType());
            mpsSyncStatistics.setStaticPubStart(syncStatsDto.getStPubStartTime());
            mpsSyncStatistics.setStaticPubEnd(syncStatsDto.getStPubEndTime());
            mpsSyncStatistics.setStaticPubBytes(syncStatsDto.getStPubBytes());
            mpsSyncStatistics.setImpactPubStart(syncStatsDto.getImpPubStartTime());
            mpsSyncStatistics.setImpactPubEnd(syncStatsDto.getImpPubEndTime());
            mpsSyncStatistics.setImpactPubBytes(syncStatsDto.getImpPubBytes());
            mpsSyncStatistics.setNetworkSpeed(syncStatsDto.getNtwrkSpeed());
            mpsSyncStatistics.setDtLastUpdate(new Date());
            sessionFactory.getCurrentSession().save(mpsSyncStatistics);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Date getLastSynchTime() {
        Date tsLastdownloadtime = null;
        SQLQuery sQLQuery = sessionFactory.getCurrentSession()
                .createSQLQuery(getLastDownloadTimeSql);
        tsLastdownloadtime = (Date) sQLQuery.uniqueResult();
        return tsLastdownloadtime;
    }


    public int getCodesTablesRowCount() {
        Integer count = (Integer) sessionFactory.getCurrentSession()
                .createSQLQuery(getCodesTableRowCountSql)
                .addScalar("count", StandardBasicTypes.INTEGER).uniqueResult();
        return count;
    }


    public int getCodesTablesEndDatedRowCount() {
        Integer count = (Integer) sessionFactory.getCurrentSession()
                .createSQLQuery(getCodesTableEndDatedRowCountSql)
                .addScalar("count", StandardBasicTypes.INTEGER).uniqueResult();
        return count;
    }


    public Timestamp getLastMessageUpdateTimestamp() {
        Timestamp lastMessageUpdateTimestamp = (Timestamp) sessionFactory.getCurrentSession()
                .createSQLQuery(getMessageLastUpdateSql)
                .addScalar("lastUpdateDt", StandardBasicTypes.TIMESTAMP).uniqueResult();
        return lastMessageUpdateTimestamp;
    }
}
