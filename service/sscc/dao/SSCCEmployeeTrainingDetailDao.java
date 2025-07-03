package us.tx.state.dfps.service.sscc.dao;

import us.tx.state.dfps.web.sscc.bean.SSCCEmployeeTrainingDetailsBean;

import java.util.List;

public interface SSCCEmployeeTrainingDetailDao {

    public List<SSCCEmployeeTrainingDetailsBean> getEmployeeTrainingDetailsList(Long personId);

    public List<SSCCEmployeeTrainingDetailsBean> getEmployeeTrainingDetailsByIdList(String fieldName,Long fieldId);


    public Long saveSSCCEmployeeDetails(SSCCEmployeeTrainingDetailsBean ssccEmployeeTrainingDetailsBean);

    public void updateSSCCEmployeeDetails(SSCCEmployeeTrainingDetailsBean ssccEmployeeTrainingDetailsBean);

    public void deleteSSCCTrainingDetailById(String fieldName,Long fieldId);
}
