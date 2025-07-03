package us.tx.state.dfps.service.mobile.daoImpl;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.domain.SyncError;
import us.tx.state.dfps.service.mobile.dao.MobileWorkloadDao;
import us.tx.state.dfps.service.workload.dto.SyncErrorDto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MobileWorkloadDaoImpl implements MobileWorkloadDao {

    public static final Logger log = Logger.getLogger(MobileWorkloadDaoImpl.class);
    @Value("${WorkloadDao.getCheckOutMobileWorkloadSql}")
    String getCheckOutMobileWorkloadSql;
    @Value("${WorkloadDao.getCheckInMobileWorkloadSql}")
    String getCheckInMobileWorkloadSql;
    @Value("${WorkloadDao.getResetInMobileWorkloadSql}")
    String getResetInMobileWorkloadSql;
    @Value("${WorkloadDao.getResetOutMobileWorkloadSql}")
    String getResetOutMobileWorkloadSql;
    @Value("${WorkloadDao.getcheckinNullMobileWorkloadSql}")
    String getcheckinNullMobileWorkloadSql;
    @Value("${WorkloadDao.deleteSyncErrorsByType}")
    String deleteSyncErrorsByTypeSql;
    @Value("${WorkloadDao.deleteSyncErrorsForUser}")
    String deleteSyncErrorsForUserSql;
    @Value("${WorkloadDao.updateWorkloadErrorsForUser}")
    String updateWorkloadErrorsForUserSql;
    @Value("${WorkloadDao.updateVersionForUser}")
    String updateVersionForUserSql;
    @Value("${WorkloadDao.insertVersionForUser}")
    String insertVersionForUserSql;
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void saveConfirm(Long idPerson, List<String> idStages) {

        Query query = sessionFactory.getCurrentSession().createSQLQuery(getCheckOutMobileWorkloadSql);
        query.setParameter("idPerson", idPerson);
        if (idStages != null && idStages.size() > 0) {
            query.setParameterList("idStages", idStages);
        } else {
            query.setParameter("idStages", null);
        }
        query.executeUpdate();

        String strQuery = getQuery(getCheckInMobileWorkloadSql, idStages);
        query = sessionFactory.getCurrentSession().createSQLQuery(strQuery);
        query.setParameter("idPerson", idPerson);
        if (idStages != null && idStages.size() > 0) {
            query.setParameterList("idStages", idStages);
        }
        query.executeUpdate();

        query = sessionFactory.getCurrentSession().createSQLQuery(getResetOutMobileWorkloadSql);
        query.setParameter("idPerson", idPerson);
        if (idStages != null && idStages.size() > 0) {
            query.setParameterList("idStages", idStages);
        } else {
            query.setParameter("idStages", null);
        }
        query.executeUpdate();

        strQuery = getQuery(getResetInMobileWorkloadSql, idStages);
        query = sessionFactory.getCurrentSession().createSQLQuery(strQuery);
        query.setParameter("idPerson", idPerson);
        if (idStages != null && idStages.size() > 0) {
            query.setParameterList("idStages", idStages);
        }
        query.executeUpdate();

        query = sessionFactory.getCurrentSession().createSQLQuery(getcheckinNullMobileWorkloadSql);
        query.setParameter("idPerson", idPerson);
        if (idStages != null && idStages.size() > 0) {
            query.setParameterList("idStages", idStages);
        } else {
            query.setParameter("idStages", null);
        }
        query.executeUpdate();
    }

    private String getQuery(String query, List<String> idStages) {
        if (idStages != null && idStages.size() > 0) {
            query = query.concat(" NOT IN (:idStages)");
        } else {
            query = query.concat(" IS NOT NULL");
        }
        return query;
    }


    public List<SyncErrorDto> getSyncErrorSql(Long idPerson) {

        List<SyncErrorDto> syncErrors = sessionFactory.getCurrentSession()
                .createCriteria(SyncError.class)
                .setProjection(Projections.projectionList()
                        .add(Projections.property("idSyncError"), "idSyncError")
                        .add(Projections.property("dtLastUpdate"), "dtLastUpdate")
                        .add(Projections.property("idCase"), "idCase")
                        .add(Projections.property("idStage"), "idStage")
                        .add(Projections.property("idEvent"), "idEvent")
                        .add(Projections.property("txtMessage"), "txtMessage")
                        .add(Projections.property("idPerson"), "idPerson")
                        .add(Projections.property("cdMsgType"), "cdMsgType"))
                .add(Restrictions.eq("idPerson", idPerson))
                .setResultTransformer(Transformers.aliasToBean(SyncErrorDto.class))
                .list();

        return syncErrors;
    }

    public void deleteSyncError(List<String> idSyncErrors) {
        try {
            List<SyncError> syncErrors = sessionFactory.getCurrentSession()
                    .createCriteria(SyncError.class)
                    .add(Restrictions.in("idSyncError", idSyncErrors.stream()
                            .map(s -> Long.valueOf(s))
                            .collect(Collectors.toList())))
                    .list();
            for (SyncError syncError : syncErrors) {
                sessionFactory.getCurrentSession().delete(syncError);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSyncErrorsByType(Long idUser, String cdMsgType) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteSyncErrorsByTypeSql);
        query.setParameter("idPerson", idUser);
        query.setParameter("cdMsgType", cdMsgType);
        int rowsDeleted = query.executeUpdate();
    }

    @Override
    public void deleteSyncErrorsForUser(Long userID) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteSyncErrorsForUserSql);
        query.setParameter("idPerson", userID);
        int rowsDeleted = query.executeUpdate();
    }

    @Override
    public void updateWorkloadErrorsForUser(Long userID) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(updateWorkloadErrorsForUserSql);
        query.setParameter("idPerson", userID);
        int rowsDeleted = query.executeUpdate();
    }

    @Override
    public void updateVersionForUser(Long userID, String nbrVersion, String latestVersion) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(updateVersionForUserSql);
        query.setParameter("idPerson", userID);
        query.setParameter("version", nbrVersion);
        query.setParameter("latestVersion", latestVersion);
        int rowsUpdated = query.executeUpdate();
        if (rowsUpdated == 0) {
            query = sessionFactory.getCurrentSession().createSQLQuery(insertVersionForUserSql);
            query.setParameter("idPerson", userID);
            query.setParameter("version", nbrVersion);
            query.setParameter("latestVersion", latestVersion);
            query.setParameter("updateDate", new Date());
            int rowsInserted = query.executeUpdate();
        }

    }
}
