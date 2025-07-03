package us.tx.state.dfps.service.sscc.daoimpl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import us.tx.state.dfps.common.domain.SSCCEmployeeTrainingDetail;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.sscc.dao.SSCCEmployeeTrainingDetailDao;
import us.tx.state.dfps.web.sscc.bean.SSCCEmployeeTrainingDetailsBean;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Repository
public class SSCCEmployeeTrainingDetailDaoImpl implements SSCCEmployeeTrainingDetailDao {
    private static final Logger logger = Logger.getLogger(SSCCEmployeeTrainingDetailDaoImpl.class);

    @Autowired
    MessageSource messageSource;
    @Autowired
    private SessionFactory sessionFactory;
    @Override
    public List<SSCCEmployeeTrainingDetailsBean> getEmployeeTrainingDetailsList(Long personId){
        logger.info("getting employee training details list.");
        List<SSCCEmployeeTrainingDetailsBean> ssccEmployeeTrainingDetailsList = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SSCCEmployeeTrainingDetail.class);
        criteria.add(Restrictions.eq("personId", personId));
        List<SSCCEmployeeTrainingDetail> employeeTrainingDetails = criteria.list();
        for (SSCCEmployeeTrainingDetail detail : employeeTrainingDetails) {
            logger.info("Employee Training Detail ID: " + detail);
            SSCCEmployeeTrainingDetailsBean ssccEmployeeTrainingDetailsBean = new SSCCEmployeeTrainingDetailsBean();
            ssccEmployeeTrainingDetailsBean.setEmployeeTrainingId(detail.getEmployeeTrainingDetailId());
            ssccEmployeeTrainingDetailsBean.setCohortStartDate(detail.getCohortStartDt());
            ssccEmployeeTrainingDetailsBean.setAnticipatedCohortEndDate(detail.getCohortEndDt());
            ssccEmployeeTrainingDetailsBean.setRequiredTraining(detail.getReqTrainingAtEntryCd());
            ssccEmployeeTrainingDetailsBean.setRequiredTrainingNote(detail.getReqTrainingNoteCd());
            ssccEmployeeTrainingDetailsBean.setCsaCompletion(detail.getCsaCompletionDt());
            ssccEmployeeTrainingDetailsBean.setCpdCoreCompletion(detail.getCpdCompletionDt());
            ssccEmployeeTrainingDetailsBean.setPrimaryCaseAssignable(detail.getPrimaryAssignDt());
            ssccEmployeeTrainingDetailsList.add(ssccEmployeeTrainingDetailsBean);
        }
        return ssccEmployeeTrainingDetailsList;
    }

    @Override
    public List<SSCCEmployeeTrainingDetailsBean> getEmployeeTrainingDetailsByIdList(String fieldName,Long fieldId) {
        logger.info("getting employee training details list by Id."+fieldId);
        List<SSCCEmployeeTrainingDetailsBean> ssccEmployeeTrainingDetailsList = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SSCCEmployeeTrainingDetail.class);
        criteria.add(Restrictions.eq(fieldName, fieldId));
        List<SSCCEmployeeTrainingDetail> employeeTrainingDetails = criteria.list();
        for (SSCCEmployeeTrainingDetail detail : employeeTrainingDetails) {
            logger.info("Employee Training Detail ID: " + detail);
            SSCCEmployeeTrainingDetailsBean ssccEmployeeTrainingDetailsBean = new SSCCEmployeeTrainingDetailsBean();
            ssccEmployeeTrainingDetailsBean.setEmployeeTrainingId(detail.getEmployeeTrainingDetailId());
            ssccEmployeeTrainingDetailsBean.setCohortStartDate(detail.getCohortStartDt());
            ssccEmployeeTrainingDetailsBean.setAnticipatedCohortEndDate(detail.getCohortEndDt());
            ssccEmployeeTrainingDetailsBean.setRequiredTraining(detail.getReqTrainingAtEntryCd());
            ssccEmployeeTrainingDetailsBean.setRequiredTrainingNote(detail.getReqTrainingNoteCd());
            ssccEmployeeTrainingDetailsBean.setCsaCompletion(detail.getCsaCompletionDt());
            ssccEmployeeTrainingDetailsBean.setCpdCoreCompletion(detail.getCpdCompletionDt());
            ssccEmployeeTrainingDetailsBean.setPrimaryCaseAssignable(detail.getPrimaryAssignDt());
            ssccEmployeeTrainingDetailsList.add(ssccEmployeeTrainingDetailsBean);
        }
        return ssccEmployeeTrainingDetailsList;
    }

    @Override
    public Long saveSSCCEmployeeDetails(SSCCEmployeeTrainingDetailsBean ssccEmployeeTrainingDetailsBean){
        logger.info("Saving SSCC employee details for person : "+ssccEmployeeTrainingDetailsBean.getPersonId());
        Long primaryKey = ServiceConstants.LongZero;
        SSCCEmployeeTrainingDetail ssccEmployeeTrainingDao = new SSCCEmployeeTrainingDetail();
        ssccEmployeeTrainingDao.setPersonId(ssccEmployeeTrainingDetailsBean.getPersonId());
        ssccEmployeeTrainingDao.setReqTrainingAtEntryCd(ssccEmployeeTrainingDetailsBean.getRequiredTraining());
        ssccEmployeeTrainingDao.setReqTrainingNoteCd(ssccEmployeeTrainingDetailsBean.getRequiredTrainingNote());
        ssccEmployeeTrainingDao.setCohortStartDt(ssccEmployeeTrainingDetailsBean.getCohortStartDate());
        ssccEmployeeTrainingDao.setCohortEndDt(ssccEmployeeTrainingDetailsBean.getAnticipatedCohortEndDate());
        ssccEmployeeTrainingDao.setCpdCompletionDt(ssccEmployeeTrainingDetailsBean.getCpdCoreCompletion());
        ssccEmployeeTrainingDao.setCsaCompletionDt(ssccEmployeeTrainingDetailsBean.getCsaCompletion());
        ssccEmployeeTrainingDao.setPrimaryAssignDt(ssccEmployeeTrainingDetailsBean.getPrimaryCaseAssignable());
        ssccEmployeeTrainingDao.setCreatedDt(new Date());
        ssccEmployeeTrainingDao.setLastUpdateDt(new Date());
        ssccEmployeeTrainingDao.setCreatedId(ssccEmployeeTrainingDetailsBean.getIdCreated());
        ssccEmployeeTrainingDao.setLastUpdatePersonId(ssccEmployeeTrainingDetailsBean.getIdLastUpdated());
        primaryKey = (Long) sessionFactory.getCurrentSession().save(ssccEmployeeTrainingDao);
        if (TypeConvUtil.isNullOrEmpty(primaryKey)) {
            throw new DataNotFoundException(
                    messageSource.getMessage("Error while saving sscc employee details.", null, Locale.US));
        }

        return primaryKey;
    }

    @Override
    public void updateSSCCEmployeeDetails(SSCCEmployeeTrainingDetailsBean ssccEmployeeTrainingDetailsBean) {
        logger.info("Updating SSCC employee details for person: " + ssccEmployeeTrainingDetailsBean.getPersonId());

        SSCCEmployeeTrainingDetail ssccEmployeeTrainingDao = (SSCCEmployeeTrainingDetail) sessionFactory.getCurrentSession().get(SSCCEmployeeTrainingDetail.class, ssccEmployeeTrainingDetailsBean.getEmployeeTrainingId());

        if (ssccEmployeeTrainingDao == null) {
            throw new DataNotFoundException("SSCC employee details not found for id: " + ssccEmployeeTrainingDetailsBean.getEmployeeTrainingId());
        }

        ssccEmployeeTrainingDao.setReqTrainingAtEntryCd(ssccEmployeeTrainingDetailsBean.getRequiredTraining());
        ssccEmployeeTrainingDao.setReqTrainingNoteCd(ssccEmployeeTrainingDetailsBean.getRequiredTrainingNote());
        ssccEmployeeTrainingDao.setCohortStartDt(ssccEmployeeTrainingDetailsBean.getCohortStartDate());
        ssccEmployeeTrainingDao.setCohortEndDt(ssccEmployeeTrainingDetailsBean.getAnticipatedCohortEndDate());
        ssccEmployeeTrainingDao.setCpdCompletionDt(ssccEmployeeTrainingDetailsBean.getCpdCoreCompletion());
        ssccEmployeeTrainingDao.setCsaCompletionDt(ssccEmployeeTrainingDetailsBean.getCsaCompletion());
        ssccEmployeeTrainingDao.setPrimaryAssignDt(ssccEmployeeTrainingDetailsBean.getPrimaryCaseAssignable());
        ssccEmployeeTrainingDao.setLastUpdateDt(new Date());
        ssccEmployeeTrainingDao.setLastUpdatePersonId(ssccEmployeeTrainingDetailsBean.getIdLastUpdated());

        sessionFactory.getCurrentSession().update(ssccEmployeeTrainingDao);
    }


    @Override
    public void deleteSSCCTrainingDetailById(String fieldName,Long fieldId){
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SSCCEmployeeTrainingDetail.class);
        criteria.add(Restrictions.eq(fieldName, fieldId));
        criteria.setMaxResults(1); // Limit the result to one record
        List<SSCCEmployeeTrainingDetail> employeeTrainingDetails = criteria.list();
        if(CollectionUtils.isNotEmpty(employeeTrainingDetails)) {
            SSCCEmployeeTrainingDetail employeeTrainingDetail = employeeTrainingDetails.get(0);
            sessionFactory.getCurrentSession().delete(employeeTrainingDetail);
        }else{
            throw new DataNotFoundException("SSCC employee details not found for field : "+fieldName+" - "+ fieldId);
        }
    }
}
