package us.tx.state.dfps.service.cciinvreport.service;


import us.tx.state.dfps.service.common.request.CciInvReportReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

public interface CciInvReportService {

    public PreFillDataServiceDto getCciInvReport(CciInvReportReq cciInvReportReq);
}
