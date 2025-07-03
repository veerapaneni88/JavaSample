package us.tx.state.dfps.service.dfpsautotransfer.service;

import us.tx.state.dfps.service.common.request.SSCCAutoTransferReq;
import us.tx.state.dfps.service.common.response.SSCCAutoTransferRes;
import us.tx.state.dfps.web.workload.bean.SSCCAutoTransferEvalBean;

import java.util.Date;
import java.util.List;

public interface DFPSAutoTrnsfrService {

    public SSCCAutoTransferRes getActiveRegions() throws Exception;

    public SSCCAutoTransferRes getVendorDtls(SSCCAutoTransferReq ssccAutoTransferBean) throws Exception;

    public Long insertTransferGroup(Long fromIdSsccParameter, Long staffId, Date transferDt) throws Exception;

    public void callEvaluation(Long ssccTrnsfrGrpId) throws Exception;

    public void callTransfer(Long ssccTrnsfrGrpId, Date transferDt) throws Exception;

    public List<SSCCAutoTransferEvalBean> getEvaluationRes(Long trnsfrGrpId) throws Exception;


    SSCCAutoTransferEvalBean checkEvalTransferStatus() throws Exception;

    public SSCCAutoTransferRes displayDtls(Long idTrnsfrGrp) throws Exception;

    void deleteEvalTrnsfr(Long idTrnsfrGrp) throws Exception;

    void createTrnsfrLock() throws Exception;

    SSCCAutoTransferRes getBpTrnsfrDt() throws Exception;

    SSCCAutoTransferRes caseAssignable(Long staffId);
}
