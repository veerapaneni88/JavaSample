package us.tx.state.dfps.service.ssccautotransfer.dao;

import us.tx.state.dfps.web.workload.bean.SSCCAutoTransferBean;
import us.tx.state.dfps.web.workload.bean.SSCCAutoTransferEvalBean;
import us.tx.state.dfps.web.workload.bean.SSCCAutoTransferRegionsBean;

import java.util.Date;
import java.util.List;

public interface SSCCAutoTrnsfrDao {

    public List<SSCCAutoTransferRegionsBean> getActiveRegions() throws Exception;

    public SSCCAutoTransferBean getVendorDtls(Long contractId) throws Exception;

    public Long insertTransferGrp(Long fromIdSsccParameter, Long toIdSsccParameter,Long staffId, Date transferDt, String trnsfrType) throws Exception;

    public void callEvaluation(Long ssccTrnsfrGrpId, String source) throws Exception;

    public void callTransfer(Long ssccTrnsfrGrpId,String source) throws Exception;

    public List<SSCCAutoTransferEvalBean> getEvaluationRes(Long trnsfrGrpId) throws Exception;

    public Long insertTransfer(Long fromContractId, Long toContractId, Long fromResourceId, Long toResourceId) throws Exception;

    public SSCCAutoTransferEvalBean checkEvalTransferStatus(String source) throws Exception;

    public SSCCAutoTransferBean displayDtls(Long idTrnsfrGrp,String btchPrgm) throws Exception;

    void insertBatchParameter(Long ssccTrnsfrGrpId, Date transferDt, String btchParm) throws Exception;

    void deleteEvalTrnsfr(Long idTrnsfrGrp) throws Exception;

    void batchTransfer(String batchPrgm,String source) throws Exception;

    SSCCAutoTransferBean getBatchParameter(String btchPrgm) throws Exception;

    SSCCAutoTransferBean caseAssignable(Long staffId);
}
