package us.tx.state.dfps.service.sscc.service;

import us.tx.state.dfps.service.common.response.SSCCEmployeeTrainingDetailRes;
import us.tx.state.dfps.web.sscc.bean.SSCCEmployeeTrainingDetailsBean;

import java.util.List;

public interface SSCCEmployeeTrainingDetailService {

    public SSCCEmployeeTrainingDetailRes getEmployeeTrainingDetailsList(Long personId);

    public SSCCEmployeeTrainingDetailRes getEmployeeTrainingDetailsByIdList(String fieldName, Long fieldId);

    public SSCCEmployeeTrainingDetailRes saveSSCCEmployeeTrainingDetails(SSCCEmployeeTrainingDetailsBean ssccEmployeeTrainingDetailsBean);

    public void deleteSSCCEmployeeDetailById(String fieldName, Long fieldId);
}
