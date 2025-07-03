package us.tx.state.dfps.service.sscc.serviceimpl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.SSCCEmployeeDetailRes;
import us.tx.state.dfps.service.sscc.dao.SSCCEmployeeDetailDao;
import us.tx.state.dfps.service.sscc.service.SSCCEmployeeDetailService;
import us.tx.state.dfps.web.sscc.bean.SSCCEmployeeDetailsBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SSCCEmployeeDetailServiceImpl implements SSCCEmployeeDetailService {

    private static final Logger log = Logger.getLogger(SSCCEmployeeDetailServiceImpl.class);

    @Autowired
    SSCCEmployeeDetailDao ssccEmployeeDetailDao;

    @Autowired
    EmployeeDao employeeDao;

    @Override
    public SSCCEmployeeDetailRes getWorkerCountyCodes(Long personId) {
        SSCCEmployeeDetailRes res = new SSCCEmployeeDetailRes();
        res.setWorkerCountyCodesList(ssccEmployeeDetailDao.getWorkerCountyCodes(personId));
        return res;
    }

    @Override
    public SSCCEmployeeDetailRes getCBCAreaRegion(Long personId) {
        SSCCEmployeeDetailRes res = new SSCCEmployeeDetailRes();
        res.setCbcAreaRegion(ssccEmployeeDetailDao.getGetCBCAreaRegion(personId));
        return res;
    }

    @Override
    public SSCCEmployeeDetailsBean saveSSCCEmployeeDetails(SSCCEmployeeDetailsBean ssccEmployeeDetailsBean) {
        log.info("Entered saving/updating SSCC employee details.");
        List<String> errorList = new ArrayList<String>();
        SSCCEmployeeDetailsBean ssccEmployeeDetailsExistingBean = null;
        SSCCEmployeeDetailsBean ssccEmployeeDetailsSavedBean = null;

        ssccEmployeeDetailsExistingBean = ssccEmployeeDetailDao.getSSCCEmployeeDetailByPersonId(ssccEmployeeDetailsBean.getPersonId());
        if ((!ObjectUtils.isEmpty(ssccEmployeeDetailsExistingBean)) && ObjectUtils.isEmpty(ssccEmployeeDetailsExistingBean .getEffectiveDateOfChangeEnd())) {
            //These values has to be assigned from previous data as these fields are disabled for user on UI and can only enter them once.
            ssccEmployeeDetailsBean.setRehire(ssccEmployeeDetailsExistingBean.getRehire());
            ssccEmployeeDetailsBean.setStipendStudent(ssccEmployeeDetailsExistingBean.getStipendStudent());
            ssccEmployeeDetailsBean.setHireDate(ssccEmployeeDetailsExistingBean.getHireDate());
            ssccEmployeeDetailsSavedBean = saveNewAndUpdateExistingSSCCEmployeeDetails(ssccEmployeeDetailsBean, ssccEmployeeDetailsExistingBean);
        } else {
            ssccEmployeeDetailsSavedBean = saveNewSSCCEmployeeDetails(ssccEmployeeDetailsBean);
        }
        return ssccEmployeeDetailsSavedBean;
    }

    private SSCCEmployeeDetailsBean saveNewAndUpdateExistingSSCCEmployeeDetails(SSCCEmployeeDetailsBean ssccEmployeeDetailsBean, SSCCEmployeeDetailsBean ssccEmployeeDetailsExistingBean) {
        log.info("Started Updating the SSCC employee details.");
        ssccEmployeeDetailsBean.setIsExsistingSSCCStaff(ServiceConstants.TRUE);
        List<String> errorList = new ArrayList<String>();
        StringBuilder errorMessage = new StringBuilder();
        String initialValidationMessage = validateChangesBeforeSaving(ssccEmployeeDetailsBean, ssccEmployeeDetailsExistingBean);
        if (StringUtils.isNotEmpty(initialValidationMessage)) {
            if(ServiceConstants.SSCC_EMPLOYEE_DETAILS_NO_CHANGE_VALIDATION_ERROR_MESSAGE.equalsIgnoreCase(initialValidationMessage)){
                return ssccEmployeeDetailsBean;
            }else {
                errorMessage.append(initialValidationMessage);
            }
        } else if (!ssccEmployeeDetailsExistingBean.getEffectiveDateOfChange().before(ssccEmployeeDetailsBean.getEffectiveDateOfChange())) {
            errorMessage.append(ServiceConstants.SSCC_EMPLOYEE_DETAILS_NEW_EFFECTIVE_DT_VALIDATION_ERROR_MESSAGE);
        }
        if (ObjectUtils.isEmpty(errorMessage)) {
            ssccEmployeeDetailDao.saveSSCCEmployeeDetails(ssccEmployeeDetailsBean);
            updateEffectiveChangeEndDate(ssccEmployeeDetailsExistingBean, ssccEmployeeDetailsBean.getEffectiveDateOfChange(), ssccEmployeeDetailsBean.getIdlastUpdatePerson());
        } else {
            errorList.add(errorMessage.toString());
            ssccEmployeeDetailsBean.setErrors(errorList);
        }
        return ssccEmployeeDetailsBean;
    }

    private SSCCEmployeeDetailsBean saveNewSSCCEmployeeDetails(SSCCEmployeeDetailsBean ssccEmployeeDetailsBean) {
        log.info("Started Saving new SSCC employee details.");
        if (ssccEmployeeDetailsBean.getHireDate().after(ssccEmployeeDetailsBean.getEffectiveDateOfChange())) {
            log.error("HireDate can't be after the EffectiveDateOfChange");
            List<String> errorList = new ArrayList<String>();
            errorList.add(ServiceConstants.SSCC_EMPLOYEE_DETAILS_HIRE_DT_VALIDATION_ERROR_MESSAGE);
            ssccEmployeeDetailsBean.setErrors(errorList);
        } else {
            ssccEmployeeDetailDao.saveSSCCEmployeeDetails(ssccEmployeeDetailsBean);
            ssccEmployeeDetailsBean.setIsExsistingSSCCStaff(ServiceConstants.TRUE);
        }
        return ssccEmployeeDetailsBean;
    }

    private String validateChangesBeforeSaving(SSCCEmployeeDetailsBean ssccEmployeeDetailsBean, SSCCEmployeeDetailsBean ssccEmployeeDetailsExistingBean) {
        EqualsBuilder dataChangeEqualsBuilder = new EqualsBuilder();
        dataChangeEqualsBuilder.append(ssccEmployeeDetailsBean.getWorkerCounty(), ssccEmployeeDetailsExistingBean.getWorkerCounty());
        dataChangeEqualsBuilder.append(ssccEmployeeDetailsBean.getJobTitle(), ssccEmployeeDetailsExistingBean.getJobTitle());
        dataChangeEqualsBuilder.append(ssccEmployeeDetailsBean.getSupervisorPid(), ssccEmployeeDetailsExistingBean.getSupervisorPid());
        log.info("is SSCC employee details updated ? " + !dataChangeEqualsBuilder.isEquals());
        EqualsBuilder effectiveDateChangeEqualsBuilder = new EqualsBuilder();
        effectiveDateChangeEqualsBuilder.append(ssccEmployeeDetailsBean.getEffectiveDateOfChange(), ssccEmployeeDetailsExistingBean.getEffectiveDateOfChange());
        log.info("is Effective date of change is updated ?" + !effectiveDateChangeEqualsBuilder.isEquals());
        String errorMessage = null;
        if (dataChangeEqualsBuilder.isEquals() && effectiveDateChangeEqualsBuilder.isEquals()) {
            // Both true - show error message
            errorMessage = ServiceConstants.SSCC_EMPLOYEE_DETAILS_NO_CHANGE_VALIDATION_ERROR_MESSAGE;
        } else if (!dataChangeEqualsBuilder.isEquals() && !effectiveDateChangeEqualsBuilder.isEquals()) {
            // Both false - This is a valid scenario
            errorMessage = null;
        } else {
            // One true, one false - show different message
            errorMessage = ServiceConstants.SSCC_EMPLOYEE_DETAILS_EFFECTIVE_DT_VALIDATION_ERROR_MESSAGE;
        }
        return errorMessage;
    }

    private void updateEffectiveChangeEndDate(SSCCEmployeeDetailsBean ssccEmployeeDetailsSavedBean, Date NewRoleEffectiveChangeStartDate, Long lastUpdatedUserId) {
        log.info("Started updating effective end date for old record.");
        Calendar c = Calendar.getInstance();
        c.setTime(NewRoleEffectiveChangeStartDate);
        c.add(Calendar.DATE, -1);
        Date previousRoleEndDate = c.getTime();
        log.info("Previous role end date : " + previousRoleEndDate);
        ssccEmployeeDetailDao.updateSSCCEmployeeDetails(ssccEmployeeDetailsSavedBean.getSsccEmployeeDetailId(), previousRoleEndDate, lastUpdatedUserId);
    }

    @Override
    public SSCCEmployeeDetailRes getSSCCEmployeeDetailslUsingPersonId(Long personId) {
        SSCCEmployeeDetailRes response = new SSCCEmployeeDetailRes();
        SSCCEmployeeDetailsBean ssccEmployeeDetailsBean = ssccEmployeeDetailDao.getSSCCEmployeeDetailByPersonId(personId);
        response.setSsccEmployeeDetails(ssccEmployeeDetailsBean);
        return response;

    }

    @Override
    public SSCCEmployeeDetailRes getEmployeeInfo(Long personId) {
        SSCCEmployeeDetailRes response = new SSCCEmployeeDetailRes();
        SSCCEmployeeDetailsBean ssccEmployeeDetailsBean = new SSCCEmployeeDetailsBean();
        ssccEmployeeDetailsBean.setEmpDetailDto(employeeDao.getEmployeeById(personId));
        response.setSsccEmployeeDetails(ssccEmployeeDetailsBean);
        return response;
    }

}
