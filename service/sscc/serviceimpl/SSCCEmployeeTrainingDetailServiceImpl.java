package us.tx.state.dfps.service.sscc.serviceimpl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.SSCCEmployeeTrainingDetailRes;
import us.tx.state.dfps.service.sscc.dao.SSCCEmployeeTrainingDetailDao;
import us.tx.state.dfps.service.sscc.service.SSCCEmployeeTrainingDetailService;
import us.tx.state.dfps.web.sscc.bean.SSCCEmployeeTrainingDetailsBean;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class SSCCEmployeeTrainingDetailServiceImpl implements SSCCEmployeeTrainingDetailService {
    private static final Logger logger = Logger.getLogger(SSCCEmployeeTrainingDetailServiceImpl.class);

    @Autowired
    SSCCEmployeeTrainingDetailDao ssccEmployeeTrainingDetailDao;

    @Override
    public SSCCEmployeeTrainingDetailRes getEmployeeTrainingDetailsList(Long personId) {
        logger.info("Fetching employee training details List");
        SSCCEmployeeTrainingDetailRes response = new SSCCEmployeeTrainingDetailRes();
        List<SSCCEmployeeTrainingDetailsBean> ssccEmployeeTrainingDetailsList = ssccEmployeeTrainingDetailDao.getEmployeeTrainingDetailsList(personId);
        response.setEmployeeTrainingDetailsList((CollectionUtils.isNotEmpty(ssccEmployeeTrainingDetailsList) ? ssccEmployeeTrainingDetailsList : null));
        return response;
    }

    @Override
    public SSCCEmployeeTrainingDetailRes getEmployeeTrainingDetailsByIdList(String fieldName, Long fieldId) {
        logger.info("Fetching employee training details List By Id : " + fieldId + " , with fieldname : " + fieldName);
        SSCCEmployeeTrainingDetailRes response = new SSCCEmployeeTrainingDetailRes();
        List<SSCCEmployeeTrainingDetailsBean> ssccEmployeeTrainingDetailsList = ssccEmployeeTrainingDetailDao.getEmployeeTrainingDetailsByIdList(fieldName, fieldId);
        response.setEmployeeTrainingDetailsList((CollectionUtils.isNotEmpty(ssccEmployeeTrainingDetailsList) ? ssccEmployeeTrainingDetailsList : null));
        return response;
    }

    @Override
    public SSCCEmployeeTrainingDetailRes saveSSCCEmployeeTrainingDetails(SSCCEmployeeTrainingDetailsBean ssccEmployeeTrainingDetailsBean) {
        logger.info("Saving employee training details.");
        SSCCEmployeeTrainingDetailRes response = new SSCCEmployeeTrainingDetailRes();

        if ((ssccEmployeeTrainingDetailsBean.getCohortStartDate() != null && ssccEmployeeTrainingDetailsBean.getAnticipatedCohortEndDate() != null)
                && (ssccEmployeeTrainingDetailsBean.getCohortStartDate().compareTo(ssccEmployeeTrainingDetailsBean.getAnticipatedCohortEndDate()) > 0)) {
            response.setErrors(Arrays.asList(ServiceConstants.SSCC_EMPLOYEE_TRAINING_COHORT_DATES_VALIDATION_ERROR_MESSAGE));
            return response;
        }
        if (ssccEmployeeTrainingDetailsBean.getEmployeeTrainingId() != null) {
            List<SSCCEmployeeTrainingDetailsBean> savedBeanList = ssccEmployeeTrainingDetailDao.getEmployeeTrainingDetailsByIdList(ServiceConstants.SSCC_EMPLOYEE_TRAINING_ID_FIELD_NAME, ssccEmployeeTrainingDetailsBean.getEmployeeTrainingId());
            if( CollectionUtils.isNotEmpty(savedBeanList) && validateChangesBeforeSaving(ssccEmployeeTrainingDetailsBean, savedBeanList.get(0))) {
                ssccEmployeeTrainingDetailDao.updateSSCCEmployeeDetails(ssccEmployeeTrainingDetailsBean);
            }
        } else {
            ssccEmployeeTrainingDetailDao.saveSSCCEmployeeDetails(ssccEmployeeTrainingDetailsBean);
        }
        return response;
    }

    private boolean validateChangesBeforeSaving(SSCCEmployeeTrainingDetailsBean newTrainingDetailsBean, SSCCEmployeeTrainingDetailsBean existingTrainingDetailsBean) {
        EqualsBuilder dataChangeEqualsBuilder = new EqualsBuilder();
        dataChangeEqualsBuilder.append(newTrainingDetailsBean.getRequiredTrainingNote(), existingTrainingDetailsBean.getRequiredTrainingNote());
        dataChangeEqualsBuilder.append(newTrainingDetailsBean.getRequiredTraining(), existingTrainingDetailsBean.getRequiredTraining());
        dataChangeEqualsBuilder.append(convertDate(newTrainingDetailsBean.getCohortStartDate()), convertDate(existingTrainingDetailsBean.getCohortStartDate()));
        dataChangeEqualsBuilder.append(convertDate(newTrainingDetailsBean.getAnticipatedCohortEndDate()), convertDate(existingTrainingDetailsBean.getAnticipatedCohortEndDate()));
        dataChangeEqualsBuilder.append(convertDate(newTrainingDetailsBean.getCpdCoreCompletion()), convertDate(existingTrainingDetailsBean.getCpdCoreCompletion()));
        dataChangeEqualsBuilder.append(convertDate(newTrainingDetailsBean.getCsaCompletion()),convertDate(existingTrainingDetailsBean.getCsaCompletion()));
        dataChangeEqualsBuilder.append(convertDate(newTrainingDetailsBean.getPrimaryCaseAssignable()), convertDate(existingTrainingDetailsBean.getPrimaryCaseAssignable()));
        return !dataChangeEqualsBuilder.isEquals();
    }

    private Date convertDate(Date dateForConversion){
        return (!ObjectUtils.isEmpty(dateForConversion)) ? DateUtils.truncate(dateForConversion, Calendar.DAY_OF_MONTH) : null;
    }

    @Override
    public void deleteSSCCEmployeeDetailById(String fieldName, Long fieldId) {
        ssccEmployeeTrainingDetailDao.deleteSSCCTrainingDetailById(fieldName, fieldId);
    }
}
