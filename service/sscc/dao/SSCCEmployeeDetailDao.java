package us.tx.state.dfps.service.sscc.dao;

import us.tx.state.dfps.common.domain.SSCCEmployeeDetail;
import us.tx.state.dfps.web.sscc.bean.SSCCEmployeeDetailsBean;
import us.tx.state.dfps.web.workload.bean.SSCCAutoTransferRegionsBean;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SSCCEmployeeDetailDao {

    public List<String> getWorkerCountyCodes(Long personId);

    public Long saveSSCCEmployeeDetails(SSCCEmployeeDetailsBean ssccEmployeeDetailsBean);

    public SSCCEmployeeDetailsBean getSSCCEmployeeDetailByPersonId (Long personId);

    public void updateSSCCEmployeeDetails(Long ssccEmployeeDetailId,Date roleEndDate,Long lastUpdatedUserId);

    public void updateSSCCEmployeeEndDate(Long personId,Date empEndDt,Long lastUpdatedUserId);

    public String getGetCBCAreaRegion(Long personId);

}
