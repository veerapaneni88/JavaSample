package us.tx.state.dfps.service.sscc.service;

import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.common.response.SSCCEmployeeDetailRes;
import us.tx.state.dfps.web.sscc.bean.SSCCEmployeeDetailsBean;
import us.tx.state.dfps.service.common.response.SSCCCatchmentsRes;

import java.util.TreeMap;
import java.util.regex.PatternSyntaxException;

public interface SSCCCatchmentsService {

    public String getCatchmentsForRegion(String region) throws Exception;

    TreeMap<String, String> getCatchmentsByRegionMap(String region) throws PatternSyntaxException;

}
