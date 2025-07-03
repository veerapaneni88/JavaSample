package us.tx.state.dfps.service.sscc.daoimpl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.domain.SSCCEmployeeDetail;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.sscc.dao.SSCCEmployeeDetailDao;
import us.tx.state.dfps.web.sscc.bean.SSCCEmployeeDetailsBean;

import java.util.*;

@Repository
public class SSCCEmployeeDetailDaoImpl implements SSCCEmployeeDetailDao {

    private static final Logger logger = Logger.getLogger(SSCCEmployeeDetailDaoImpl.class);

    @Autowired
    MessageSource messageSource;
    @Value("${SSCCEmployeeDetailDaoImpl.getWorkerCounties}")
    String getWorkerCountiesSql;
    @Value("${SSCCEmployeeDetailDaoImpl.getSSCCEmployeeDetailsByPersonId}")
    String getSSCCEmployeeDetailsByPersonId;
    @Value("${SSCCEmployeeDetailDaoImpl.updateEffectiveChangeEndDt}")
    String updateEffectiveDateEndById;
    @Value("${SSCCEmployeeDetailDaoImpl.getCBCAreaRegion}")
    String getCBCAreaRegion;
    @Value("${SSCCEmployeeDetailDaoImpl.updateEmpEndDtByPersonId}")
    String updateEmpEndDtByPersonId;
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<String> getWorkerCountyCodes(Long personId) {
        logger.info("Fetching worker county codes for person: " + personId);
        List<String> workerCountyCodesList = new ArrayList<>();
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getWorkerCountiesSql);
        query.setParameter("personId", personId);
        query.addScalar("cd_catchment_county", StandardBasicTypes.STRING);
        List<String> results = query.list();
        Optional.ofNullable(results).ifPresent(list -> list.forEach(workerCountyCodesList::add));
        return workerCountyCodesList;
    }

    @Override
    public Long saveSSCCEmployeeDetails(SSCCEmployeeDetailsBean ssccEmployeeDetailsBean) {
        logger.info("Saving SSCC employee details for person : " + ssccEmployeeDetailsBean.getPersonId());
        Long primaryKey = ServiceConstants.LongZero;
        SSCCEmployeeDetail ssccEmployeeDetailDao = new SSCCEmployeeDetail();
        ssccEmployeeDetailDao.setCdCbcArea(ssccEmployeeDetailsBean.getCbcAreaRegion());
        ssccEmployeeDetailDao.setJobTitle(ssccEmployeeDetailsBean.getJobTitle());
        ssccEmployeeDetailDao.setCdWorkerCounty(ssccEmployeeDetailsBean.getWorkerCounty());
        ssccEmployeeDetailDao.setDtEffectiveEnd(null);
        ssccEmployeeDetailDao.setDtEffectiveStart(ssccEmployeeDetailsBean.getEffectiveDateOfChange());
        ssccEmployeeDetailDao.setDtHire(ssccEmployeeDetailsBean.getHireDate());
        ssccEmployeeDetailDao.setIdPerson(ssccEmployeeDetailsBean.getPersonId());
        ssccEmployeeDetailDao.setIdSupervisorPerson(ssccEmployeeDetailsBean.getSupervisorPid());
        ssccEmployeeDetailDao.setIndRehire((ssccEmployeeDetailsBean.getRehire() != null) ? ssccEmployeeDetailsBean.getRehire() : "N");
        ssccEmployeeDetailDao.setIndStipendStudent((ssccEmployeeDetailsBean.getStipendStudent() != null) ? ssccEmployeeDetailsBean.getStipendStudent() : "N");
        ssccEmployeeDetailDao.setDtCreated(new Date());
        ssccEmployeeDetailDao.setDtLastUpdate(new Date());
        ssccEmployeeDetailDao.setIdCreatedPerson(ssccEmployeeDetailsBean.getIdCreatedPerson());
        ssccEmployeeDetailDao.setIdLastUpdatePerson(ssccEmployeeDetailsBean.getIdlastUpdatePerson());
        primaryKey = (Long) sessionFactory.getCurrentSession().save(ssccEmployeeDetailDao);
        if (TypeConvUtil.isNullOrEmpty(primaryKey)) {
            throw new DataNotFoundException(
                    messageSource.getMessage("Error while saving sscc employee details.", null, Locale.US));
        }

        return primaryKey;
    }

    @Override
    public SSCCEmployeeDetailsBean getSSCCEmployeeDetailByPersonId(Long personId) {
        logger.info("Fetching SSCC employee details by person id : " + personId);
        SSCCEmployeeDetailsBean ssccEmployeeDetailsBean = new SSCCEmployeeDetailsBean();
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getSSCCEmployeeDetailsByPersonId)
                .addScalar("cbcAreaRegion", StandardBasicTypes.STRING).addScalar("hireDate", StandardBasicTypes.DATE)
                .addScalar("stipendStudent", StandardBasicTypes.STRING).addScalar("workerCounty", StandardBasicTypes.STRING)
                .addScalar("jobTitle", StandardBasicTypes.STRING).addScalar("rehire", StandardBasicTypes.STRING).addScalar("supervisorPid", StandardBasicTypes.LONG)
                .addScalar("effectiveDateOfChange", StandardBasicTypes.DATE).addScalar("effectiveDateOfChangeEnd", StandardBasicTypes.DATE)
                .addScalar("personId", StandardBasicTypes.LONG).addScalar("ssccEmployeeDetailId", StandardBasicTypes.LONG)
                .addScalar("supervisorName", StandardBasicTypes.STRING).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
                .addScalar("dtCreated", StandardBasicTypes.DATE).setParameter("personId", personId);
        List<Object[]> results = query.list();
        if (CollectionUtils.isNotEmpty(results)) {
            for (Object[] row : results) {
                ssccEmployeeDetailsBean.setCbcAreaRegion((row[0] != null) ? (String) row[0] : null);
                ssccEmployeeDetailsBean.setHireDate((row[1] != null) ? (Date) row[1] : null);
                ssccEmployeeDetailsBean.setStipendStudent((row[2] != null) ? (String) row[2] : null);
                ssccEmployeeDetailsBean.setWorkerCounty((row[3] != null) ? (String) row[3] : null);
                ssccEmployeeDetailsBean.setJobTitle((row[4] != null) ? (String) row[4] : null);
                ssccEmployeeDetailsBean.setRehire((row[5] != null) ? (String) row[5] : null);
                ssccEmployeeDetailsBean.setSupervisorPid((row[6] != null) ? (Long) row[6] : null);
                ssccEmployeeDetailsBean.setEffectiveDateOfChange((row[7] != null) ? (Date) row[7] : null);
                ssccEmployeeDetailsBean.setEffectiveDateOfChangeEnd((row[8] != null) ? (Date) row[8] : null);
                ssccEmployeeDetailsBean.setPersonId((row[9] != null) ? (Long) row[9] : null);
                ssccEmployeeDetailsBean.setSsccEmployeeDetailId((row[10] != null) ? (Long) row[10] : null);
                ssccEmployeeDetailsBean.setSupervisorName((row[11] != null) ? (String) row[11] : null);
                ssccEmployeeDetailsBean.setIdCreatedPerson((row[12] != null) ? (Long) row[12] : null);
                ssccEmployeeDetailsBean.setDtCreated((row[13] != null) ? (Date) row[13] : null);
                ssccEmployeeDetailsBean.setIsExsistingSSCCStaff("true");
                System.out.println(ssccEmployeeDetailsBean);
            }
            return ssccEmployeeDetailsBean;
        } else {
            return null;
        }
    }

    @Override
    public void updateSSCCEmployeeDetails(Long ssccEmployeeDetailId, Date roleEndDate, Long lastUpdatedUserId) {
        logger.info("Updating SSCC employee details for, EmployeeDetailId: " + ssccEmployeeDetailId);
        Query query = sessionFactory.getCurrentSession().createSQLQuery(updateEffectiveDateEndById);
        query.setParameter("dtChangeEffectiveEnd", roleEndDate);
        query.setParameter("idSsccEmployeeDetail", ssccEmployeeDetailId);
        query.setParameter("idLastUpdatePerson", lastUpdatedUserId);
        query.setParameter("dtLastUpdate", new Date());
        query.executeUpdate();
    }

    @Override
    public void updateSSCCEmployeeEndDate(Long personId, Date empEndDt, Long lastUpdatedUserId) {
        logger.info("Updating SSCC employee end date by person id");
        Query query = sessionFactory.getCurrentSession().createSQLQuery(updateEmpEndDtByPersonId);
        query.setParameter("dtChangeEffectiveEnd", empEndDt);
        query.setParameter("idLastUpdatePerson", lastUpdatedUserId);
        query.setParameter("personId", personId);
        query.setParameter("dtLastUpdate", new Date());
        query.executeUpdate();
    }

    @Override
    public String getGetCBCAreaRegion(Long personId) {
        logger.info("Fetching CDC Region code for person : " + personId);
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getCBCAreaRegion).setParameter("idPerson", personId);
        Object result = query.uniqueResult();
        String cbcCode = (result != null) ? result.toString() : null;
        logger.info("CBC Region : " + cbcCode);
        return cbcCode;
    }

}
