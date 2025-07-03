package us.tx.state.dfps.service.ssccautotransfer.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.SSCCAutoTransferReq;
import us.tx.state.dfps.service.common.response.SSCCAutoTransferRes;
import us.tx.state.dfps.service.ssccautotransfer.dao.SSCCAutoTrnsfrDao;
import us.tx.state.dfps.service.ssccautotransfer.service.SSCCAutoTrnsfrService;
import us.tx.state.dfps.web.workload.bean.SSCCAutoTransferEvalBean;

import java.util.Date;
import java.util.List;
@Service
@Transactional
public class SSCCAutoTrnsfrServiceImpl implements SSCCAutoTrnsfrService {

    @Autowired
    SSCCAutoTrnsfrDao ssccAutoTrnsfrDao;

    /**
     *
     * @return
     * @throws Exception
     */
    @Override
    public SSCCAutoTransferRes getActiveRegions() throws Exception {
        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        response.setAutoTransfer(ssccAutoTrnsfrDao.getActiveRegions());
        return response;
    }

    /**
     *
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public SSCCAutoTransferRes getVendorDtls(SSCCAutoTransferReq request) throws Exception {
        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        response.setAutoTrnfrBean(ssccAutoTrnsfrDao.getVendorDtls(request.getIdSsccParameter()));
        return response;
    }


    /**
     *
     * @param fromIdSsccParameter
     * @param toIdSsccParameter
     * @param transferDt
     * @return
     * @throws Exception
     */
    @Override
    public Long insertTransferGroup(Long fromIdSsccParameter, Long toIdSsccParameter, Long staffId, Date transferDt) throws Exception {

        return ssccAutoTrnsfrDao.insertTransferGrp(fromIdSsccParameter, toIdSsccParameter,staffId, transferDt, ServiceConstants.SSCC_TRANSFER);
    }

    /**
     *
     * @param ssccTrnsfrGrpId
     * @throws Exception
     */
    public void callEvaluation(Long ssccTrnsfrGrpId) throws Exception {
        ssccAutoTrnsfrDao.callEvaluation(ssccTrnsfrGrpId,ServiceConstants.SSCC_AUTO_TRANSFER);
    }

    /**
     *
     * @param ssccTrnsfrGrpId
     * @param transferDt
     * @throws Exception
     */
    public void callTransfer(Long ssccTrnsfrGrpId, Date transferDt) throws Exception {
        ssccAutoTrnsfrDao.insertBatchParameter(ssccTrnsfrGrpId,transferDt, ServiceConstants.SSCCT_BATCH_PROGRAM);
    }


    /**
     *
     * @param trnsfrGrpId
     * @return
     * @throws Exception
     */
    @Override
    public List<SSCCAutoTransferEvalBean> getEvaluationRes(Long trnsfrGrpId) throws Exception {
        return ssccAutoTrnsfrDao.getEvaluationRes(trnsfrGrpId);
    }

    /**
     *
     * @return
     * @throws Exception
     */

    @Override
    public SSCCAutoTransferEvalBean checkEvalTransferStatus() throws Exception {
        return ssccAutoTrnsfrDao.checkEvalTransferStatus(ServiceConstants.SSCC_AUTO_TRANSFER);
    }

    /**
     *
     * @param idTrnsfrGrp
     * @return
     * @throws Exception
     */
    @Override
    public SSCCAutoTransferRes displayDtls(Long idTrnsfrGrp) throws Exception {
        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        response.setAutoTrnfrBean(ssccAutoTrnsfrDao.displayDtls(idTrnsfrGrp,ServiceConstants.SSCCT_BATCH_PROGRAM));
        return response;
    }

    /**
     *
     * @param idTrnsfrGrp
     * @throws Exception
     */
    @Override
    public void deleteEvalTrnsfr(Long idTrnsfrGrp) throws Exception {
        ssccAutoTrnsfrDao.deleteEvalTrnsfr(idTrnsfrGrp);
    }

    /**
     *
     * @throws Exception
     */
    @Override
    @Transactional
    public void createTrnsfrLock() throws Exception {
        ssccAutoTrnsfrDao.batchTransfer(ServiceConstants.SSCCT_BATCH_PROGRAM,ServiceConstants.SSCC_AUTO_TRANSFER);
    }

    /**
     * @return
     */
    @Override
    public SSCCAutoTransferRes getBpTrnsfrDt() throws Exception {
        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        response.setAutoTrnfrBean(ssccAutoTrnsfrDao.getBatchParameter(ServiceConstants.SSCCT_BATCH_PROGRAM));
        return response;
    }

    /**
     * @param staffId
     * @return
     */
    @Override
    public SSCCAutoTransferRes caseAssignable(Long staffId) {
        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        response.setAutoTrnfrBean(ssccAutoTrnsfrDao.caseAssignable(staffId));
        return response;
    }
}
