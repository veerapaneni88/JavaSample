package us.tx.state.dfps.service.sscc.service;

import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.common.response.SSCCEmployeeDetailRes;
import us.tx.state.dfps.web.sscc.bean.SSCCEmployeeDetailsBean;

public interface SSCCEmployeeDetailService {

    public SSCCEmployeeDetailRes getWorkerCountyCodes(Long personId) throws Exception;

    public SSCCEmployeeDetailsBean saveSSCCEmployeeDetails(SSCCEmployeeDetailsBean ssccEmployeeDetailsBean) throws Exception;

    public SSCCEmployeeDetailRes getSSCCEmployeeDetailslUsingPersonId(Long personId);

    public SSCCEmployeeDetailRes getCBCAreaRegion(Long personId);
    public SSCCEmployeeDetailRes getEmployeeInfo(Long personId);

}
