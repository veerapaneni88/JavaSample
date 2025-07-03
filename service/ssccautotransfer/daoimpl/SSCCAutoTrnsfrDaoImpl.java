package us.tx.state.dfps.service.ssccautotransfer.daoimpl;

import oracle.jdbc.OracleTypes;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.BatchParameters;
import us.tx.state.dfps.common.domain.BatchParametersId;
import us.tx.state.dfps.common.domain.SsccTransferGroup;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.ssccautotransfer.dao.SSCCAutoTrnsfrDao;
import us.tx.state.dfps.service.subcare.dao.BatchParametersDao;
import us.tx.state.dfps.web.workload.bean.SSCCAutoTransferBean;
import us.tx.state.dfps.web.workload.bean.SSCCAutoTransferEvalBean;
import us.tx.state.dfps.web.workload.bean.SSCCAutoTransferRegionsBean;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static us.tx.state.dfps.service.common.ServiceConstants.*;

@Repository
public class SSCCAutoTrnsfrDaoImpl implements SSCCAutoTrnsfrDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${SSCCAutoTrnsfrDaoImpl.getActiveRegions}")
    String getActiveRegionsSql;

    @Value("${SSCCAutoTrnsfrDaoImpl.getVendorDtls}")
    String getVendorDtls;

    @Value("${SSCCAutoTrnsfrDaoImpl.callEvaluation}")
    String callEvaluationProc;

    @Value("${SSCCAutoTrnsfrDaoImpl.callDfpsEvaluation}")
    String callEvaluationDfpsProc;

    @Value("${SSCCAutoTrnsfrDaoImpl.callTransfer}")
    String callTransferProc;

    @Value("${SSCCAutoTrnsfrDaoImpl.callDfpsTransfer}")
    String callDfpsTransferProc;

    @Value("${SSCCAutoTrnsfrDaoImpl.evaluationRes}")
    String evaluationRes;

    @Value("${SSCCAutoTrnsfrDaoImpl.getTrnsfrGrpId}")
    String getTrnsfrGrpId;

    @Value("${SSCCAutoTrnsfrDaoImpl.getSsccCatchment}")
    String getCachment;

    @Value("${SSCCAutoTrnsfrDaoImpl.getTransferGrpId}")
    String getEvalTrnsfrStatus;

    @Value("${SSCCAutoTrnsfrDaoImpl.getDfpsTransferGrpId}")
    String getDfpsTransferGrpId;

    @Value("${SSCCAutoTrnsfrDaoImpl.getDisplay}")
    String display;

    @Value("${SSCCAutoTrnsfrDaoImpl.getDfpsDisplay}")
    String dfpsDisplay;


    @Value("${SSCCAutoTrnsfrDaoImpl.updateRunMode}")
    String updateRunMode;

    @Value("${SSCCAutoTrnsfrDaoImpl.updateTrnsfrGrpId}")
    String updateTrnsfrGrpId;

    @Value("${SSCCAutoTrnsfrDaoImpl.updateTrnsfrDt}")
    String updateTrnsfrDt;


    @Value("${SSCCAutoTrnsfrDaoImpl.extTrnsfr}")
    String extTrnsfr;

    @Value("${SSCCAutoTrnsfrDaoImpl.deleteTrnsfr}")
    String deleteTrnsfr;

    @Value("${SSCCAutoTrnsfrDaoImpl.deleteTrnsfrGrp}")
    String deleteTrnsfrGrp;

    @Value("${SSCCAutoTrnsfrDaoImpl.lockBatchParm}")
    String lockBatchParm;

    @Value("${SSCCAutoTrnsfrDaoImpl.exstRun}")
    String exstRun;

    @Value("${SSCCAutoTrnsfrDaoImpl.trnsfrSchld}")
    String trnsfrSchld;

    @Value("${SSCCAutoTrnsfrDaoImpl.trnsfrException}")
    String trnsfrException;

    @Value("${SSCCAutoTrnsfrDaoImpl.trnsfrDate}")
    String trnsfrDt;

    @Value("${SSCCAutoTrnsfrDaoImpl.caseAssignable}")
    String caseAssign;



    @Autowired
    private BatchParametersDao batchParametersDao;

    private static final Logger logger = Logger.getLogger(SSCCAutoTrnsfrDaoImpl.class);

    /**
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<SSCCAutoTransferRegionsBean> getActiveRegions() throws Exception {

        List<SSCCAutoTransferRegionsBean> ssccRegionslist = new ArrayList<SSCCAutoTransferRegionsBean>();
        try {
            ssccRegionslist = (List<SSCCAutoTransferRegionsBean>) ((SQLQuery) sessionFactory.getCurrentSession()
                    .createSQLQuery(getActiveRegionsSql))
                    .addScalar("regionName", StandardBasicTypes.STRING)
                    .addScalar("contractId", StandardBasicTypes.LONG)
                    .addScalar("resourceId", StandardBasicTypes.LONG)
                    .addScalar("idSsccParameter", StandardBasicTypes.LONG)
                    .setResultTransformer(Transformers.aliasToBean(SSCCAutoTransferRegionsBean.class)).list();
        }catch(Exception e){
            logger.warn(e.getMessage());
            throw new Exception(e.getMessage());
        }

        return ssccRegionslist;
    }

    /**
     *
     * @param idSsccParameter
     * @return
     * @throws Exception
     */
    @Override
    public SSCCAutoTransferBean getVendorDtls(Long idSsccParameter) throws Exception {

        SSCCAutoTransferBean trnsfrBean = null;
        try{
            trnsfrBean = (SSCCAutoTransferBean) ((SQLQuery) sessionFactory.getCurrentSession()
                .createSQLQuery(getVendorDtls).setParameter("idSsccParameter", idSsccParameter))
                .addScalar("contractId", StandardBasicTypes.LONG)
                .addScalar("resourceId", StandardBasicTypes.LONG)
                .addScalar("startDt", StandardBasicTypes.DATE)
                .addScalar("endDt", StandardBasicTypes.DATE)
                .addScalar("resourceName", StandardBasicTypes.STRING)
                    .addScalar("idSsccParameter", StandardBasicTypes.LONG)
                    .addScalar("idCatchment", StandardBasicTypes.LONG)
                .setResultTransformer(Transformers.aliasToBean(SSCCAutoTransferBean.class)).uniqueResult();
        }catch(Exception e){
            logger.warn(e.getMessage());
            throw new Exception(e.getMessage());
        }

        if(!ObjectUtils.isEmpty(trnsfrBean)){
            return trnsfrBean;
        }else{
            return null;
        }
    }


    /**
     * @param fromIdSsccParameter
     * @param toIdSsccParameter
     * @param transferDt
     * @param
     * @return
     * 0210
0     */
    @Override
    public Long insertTransferGrp(Long fromIdSsccParameter, Long toIdSsccParameter, Long staffId, Date transferDt, String trnsfrType) throws Exception {
        Long trnsfrGrpId = null;
        Long maxid = 0l;
        Long catchement = 0l;
        try{
        maxid = (Long) ( sessionFactory.getCurrentSession().createSQLQuery(getTrnsfrGrpId))
                .addScalar("idSsccTransferGroup", StandardBasicTypes.LONG).uniqueResult();

            SSCCAutoTransferBean fromVendorDetails = getVendorDtls(fromIdSsccParameter);

            if("20".equalsIgnoreCase(trnsfrType)) {
                SSCCAutoTransferBean toVendorDetails = getVendorDtls(toIdSsccParameter);
                if (fromVendorDetails != null && toVendorDetails != null) {
                    SsccTransferGroup grpId = new SsccTransferGroup();
                    grpId.setIdSsccTransferGroup(maxid + 1);
                    grpId.setIdContractFrom(fromVendorDetails.getContractId());
                    grpId.setIdContractTo(toVendorDetails.getContractId());
                    grpId.setIdSsccRsrcFrom(fromVendorDetails.getResourceId());
                    grpId.setIdSsccRsrcTo(toVendorDetails.getResourceId());
                    grpId.setCdTransferType(trnsfrType);
                    grpId.setDtTransfer(transferDt);
                    grpId.setDtEvalRequested(new Date());
                    grpId.setDtCreated(new Date());
                    grpId.setDtLastUpdate(new Date());
                    grpId.setIndActive(ServiceConstants.STRING_IND_Y);
                    grpId.setDtRunEval(new Date());
                    grpId.setIdSsccCatchment(toVendorDetails.getIdCatchment());
                    grpId.setIdPerson(staffId);

                    trnsfrGrpId = (Long) sessionFactory.getCurrentSession().save(grpId);
                }
            }else if("30".equalsIgnoreCase(trnsfrType)){
                if (fromVendorDetails != null) {
                    SsccTransferGroup grpId = new SsccTransferGroup();
                    grpId.setIdSsccTransferGroup(maxid + 1);
                    grpId.setIdContractFrom(fromVendorDetails.getContractId());
                    grpId.setIdSsccRsrcFrom(fromVendorDetails.getResourceId());
                    grpId.setIdSsccRsrcTo(0l);
                    grpId.setCdTransferType(trnsfrType);
                    grpId.setDtTransfer(transferDt);
                    grpId.setDtEvalRequested(new Date());
                    grpId.setDtCreated(new Date());
                    grpId.setDtLastUpdate(new Date());
                    grpId.setIndActive(ServiceConstants.STRING_IND_Y);
                    grpId.setDtRunEval(new Date());
                    grpId.setIdSsccCatchment(0l);
                    grpId.setIdPerson(staffId);

                    trnsfrGrpId = (Long) sessionFactory.getCurrentSession().save(grpId);
                }
            }
        }catch(Exception e){
            logger.warn(e.getMessage());
            throw new Exception(e.getMessage());
        }

        return trnsfrGrpId;
    }

    /**
     *
     * @param ssccTrnsfrGrpId
     * @throws Exception
     */
    @Override
    public void callEvaluation(Long ssccTrnsfrGrpId, String source) throws Exception {

        SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
        Connection connection = null;
        CallableStatement callStatement = null;
        try {

            connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
            if(ServiceConstants.SSCC_AUTO_TRANSFER.equalsIgnoreCase(source)){
                callStatement = connection.prepareCall(callEvaluationProc);
            }else if(ServiceConstants.DFPS_AUTO_TRANSFER.equalsIgnoreCase(source)){
                callStatement = connection.prepareCall(callEvaluationDfpsProc);
            }


            if (!ObjectUtils.isEmpty(ssccTrnsfrGrpId)) {
                callStatement.setLong(1, ssccTrnsfrGrpId);
                callStatement.registerOutParameter(2, OracleTypes.BIGINT);
                callStatement.registerOutParameter(3, OracleTypes.VARCHAR);

                callStatement.execute();

                long errorCode = callStatement.getLong(2);
                String errorMsg = callStatement.getString(3);
                if (0 != errorCode && !ObjectUtils.isEmpty(errorCode) && !ObjectUtils.isEmpty(errorMsg)) {
                    throw new SQLException("Error occured in Evaluation stored proc :" + errorCode + errorMsg);
                }
            }

        } catch (SQLException e) {
            logger.warn(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     *
     * @param ssccTrnsfrGrpId
     * @throws Exception
     */
    @Override
    public void callTransfer(Long ssccTrnsfrGrpId, String source) throws Exception {

        SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
        Connection connection = null;
        CallableStatement callStatement = null;
        try {

            connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
            if(ServiceConstants.SSCC_AUTO_TRANSFER.equalsIgnoreCase(source)) {
                callStatement = connection.prepareCall(callTransferProc);
            }else if(ServiceConstants.DFPS_AUTO_TRANSFER.equalsIgnoreCase(source)) {
                callStatement = connection.prepareCall(callDfpsTransferProc);

            }
            callStatement.setLong(1, ssccTrnsfrGrpId);

            callStatement.execute();

        } catch (SQLException e) {
            logger.warn(e.getMessage());
            throw new Exception(e.getMessage());

        }
    }

    /**
     *
     * @param trnsfrGrpId
     * @return
     * @throws Exception
     */
    @Override
    public List<SSCCAutoTransferEvalBean> getEvaluationRes(Long trnsfrGrpId) throws Exception {
        List<SSCCAutoTransferEvalBean> evalLst = null;
        try {
            evalLst = (List<SSCCAutoTransferEvalBean>) ((SQLQuery) sessionFactory.getCurrentSession()
                    .createSQLQuery(evaluationRes).setParameter("trnsfrGrpId", trnsfrGrpId))
                    .addScalar("trnsfrGrpId", StandardBasicTypes.LONG)
                    .addScalar("primaryWorker", StandardBasicTypes.STRING)
                    .addScalar("stage", StandardBasicTypes.LONG)
                    .addScalar("stageName", StandardBasicTypes.STRING)
                    .addScalar("personId", StandardBasicTypes.LONG)
                    .addScalar("agencyId", StandardBasicTypes.LONG)
                    .addScalar("agencyName", StandardBasicTypes.STRING)
                    .addScalar("facilityName", StandardBasicTypes.STRING)
                    .addScalar("facilityId", StandardBasicTypes.LONG)
                    .addScalar("caseId", StandardBasicTypes.LONG)
                    .addScalar("ssccAuthException", StandardBasicTypes.STRING)
                    .setResultTransformer(Transformers.aliasToBean(SSCCAutoTransferEvalBean.class)).list();

        }catch(Exception e){
            logger.warn(e.getMessage());
            throw new Exception(e.getMessage());
        }

        return evalLst;
    }

    /**
     *
     * @param fromContractId
     * @param toContractId
     * @param fromResourceId
     * @param toResourceId
     * @return
     * @throws Exception
     */
    @Override
    public Long insertTransfer(Long fromContractId, Long toContractId, Long fromResourceId, Long toResourceId) throws Exception {
        Long trnsfrGrpId = null;
        Long maxid = 0l;
        Long catchement = 0l;
        try{
            maxid = (Long) ( sessionFactory.getCurrentSession().createSQLQuery(getTrnsfrGrpId))
                    .addScalar("idSsccTransferGroup", StandardBasicTypes.LONG).uniqueResult();

       catchement = (Long) ((SQLQuery)sessionFactory.getCurrentSession().createSQLQuery(getCachment)
                .setParameter("idContract",fromContractId))
                .addScalar("idSsccCatchment", StandardBasicTypes.LONG).uniqueResult();

            SsccTransferGroup grpId = new SsccTransferGroup();
            grpId.setIdSsccTransferGroup(maxid+1);
            grpId.setIdContractFrom(fromContractId);
            grpId.setIdContractTo(toContractId);
            grpId.setIdSsccRsrcFrom(fromResourceId);
            grpId.setIdSsccRsrcTo(toResourceId);
            grpId.setCdTransferType(ServiceConstants.STRING_TWENTY);
            grpId.setDtTransfer(new Date());
            grpId.setDtEvalRequested(new Date());
            grpId.setDtCreated(new Date());
            grpId.setDtLastUpdate(new Date());
            grpId.setIndActive(ServiceConstants.STRING_IND_Y);
            grpId.setIdSsccCatchment(catchement);

            trnsfrGrpId = (Long) sessionFactory.getCurrentSession().save(grpId);

        }catch(Exception e){
            logger.warn(e.getMessage());
            throw new Exception(e.getMessage());
        }

        return trnsfrGrpId;
    }

    /**
     *
     * @return
     * @throws Exception
     */

    @Override
    public SSCCAutoTransferEvalBean checkEvalTransferStatus(String source) throws Exception {

        SSCCAutoTransferEvalBean trnsfrBean = null;
        try {
            if(null != source && source.equalsIgnoreCase(ServiceConstants.SSCC_AUTO_TRANSFER)) {
                trnsfrBean = (SSCCAutoTransferEvalBean) ((SQLQuery) sessionFactory.getCurrentSession()
                        .createSQLQuery(getEvalTrnsfrStatus))
                        .addScalar("trnsfrGrpId", StandardBasicTypes.LONG)
                        .addScalar("eval", StandardBasicTypes.STRING)
                        .addScalar("transfer", StandardBasicTypes.STRING)
                        .setResultTransformer(Transformers.aliasToBean(SSCCAutoTransferEvalBean.class)).uniqueResult();
            }else if(null != source && source.equalsIgnoreCase(ServiceConstants.DFPS_AUTO_TRANSFER)){
                trnsfrBean = (SSCCAutoTransferEvalBean) ((SQLQuery) sessionFactory.getCurrentSession()
                        .createSQLQuery(getDfpsTransferGrpId))
                        .addScalar("trnsfrGrpId", StandardBasicTypes.LONG)
                        .addScalar("eval", StandardBasicTypes.STRING)
                        .addScalar("transfer", StandardBasicTypes.STRING)
                        .setResultTransformer(Transformers.aliasToBean(SSCCAutoTransferEvalBean.class)).uniqueResult();

            }
        }catch(Exception e){
            logger.warn(e.getMessage());
            throw new Exception(e.getMessage());
        }

        if(!ObjectUtils.isEmpty(trnsfrBean)){
            return trnsfrBean;
        }else{
            return null;
        }

    }


    /**
     *
     * @param idTrnsfrGrp
     * @return
     * @throws Exception
     */

    @Override
    public SSCCAutoTransferBean displayDtls(Long idTrnsfrGrp, String btchPrgm) throws Exception {
        SSCCAutoTransferBean trnsfrBean = null;
        try {

            if(ServiceConstants.SSCCT_BATCH_PROGRAM.equalsIgnoreCase(btchPrgm)) {
                trnsfrBean = (SSCCAutoTransferBean) ((SQLQuery) sessionFactory.getCurrentSession()
                        .createSQLQuery(display).setParameter("idTrnsfrGrp", idTrnsfrGrp))
                        .addScalar("transferDt", StandardBasicTypes.DATE)
                        .addScalar("toContractId", StandardBasicTypes.LONG)
                        .addScalar("fromContractId", StandardBasicTypes.LONG)
                        .addScalar("toResourceId", StandardBasicTypes.LONG)
                        .addScalar("fromResourceId", StandardBasicTypes.LONG)
                        .addScalar("staffId", StandardBasicTypes.LONG)
                        .addScalar("staffName", StandardBasicTypes.STRING)
                        .addScalar("toStartDt", StandardBasicTypes.DATE)
                        .addScalar("toEndDt", StandardBasicTypes.DATE)
                        .addScalar("toIdSsccParameter", StandardBasicTypes.LONG)
                        .addScalar("fromStartDt", StandardBasicTypes.DATE)
                        .addScalar("fromEndDt", StandardBasicTypes.DATE)
                        .addScalar("fromIdSsccParameter", StandardBasicTypes.LONG)
                        .addScalar("fromResourceName", StandardBasicTypes.STRING)
                        .addScalar("toResourceName", StandardBasicTypes.STRING)
                        .setResultTransformer(Transformers.aliasToBean(SSCCAutoTransferBean.class)).uniqueResult();
            }else if(ServiceConstants.DFPS_BATCH_PROGRAM.equalsIgnoreCase(btchPrgm)){
                trnsfrBean = (SSCCAutoTransferBean) ((SQLQuery) sessionFactory.getCurrentSession()
                        .createSQLQuery(dfpsDisplay).setParameter("idTrnsfrGrp", idTrnsfrGrp))
                        .addScalar("transferDt", StandardBasicTypes.DATE)
                        .addScalar("fromContractId", StandardBasicTypes.LONG)
                        .addScalar("fromResourceId", StandardBasicTypes.LONG)
                        .addScalar("staffId", StandardBasicTypes.LONG)
                        .addScalar("staffName", StandardBasicTypes.STRING)
                        .addScalar("fromStartDt", StandardBasicTypes.DATE)
                        .addScalar("fromEndDt", StandardBasicTypes.DATE)
                        .addScalar("fromIdSsccParameter", StandardBasicTypes.LONG)
                        .addScalar("fromResourceName", StandardBasicTypes.STRING)
                        .setResultTransformer(Transformers.aliasToBean(SSCCAutoTransferBean.class)).uniqueResult();
            }

            if(!ObjectUtils.isEmpty(trnsfrBean)) {
                Query query = ((SQLQuery) sessionFactory.getCurrentSession()
                        .createSQLQuery(trnsfrSchld).setParameter("idTrnsfrGrp", idTrnsfrGrp)
                        .setParameter("btchPrgm", btchPrgm))
                        .addScalar("trnsferScheduled", StandardBasicTypes.STRING);

                String trnsfrSchld = (String) query.uniqueResult();

                query = ((SQLQuery) sessionFactory.getCurrentSession()
                        .createSQLQuery(trnsfrException).setParameter("idTrnsfrGrp", idTrnsfrGrp))
                        .addScalar("trnsferExcep", StandardBasicTypes.STRING);
                String trnsferExcep = (String) query.uniqueResult();

                trnsfrBean.setTrnsferScheduled(trnsfrSchld);
                trnsfrBean.setTrnsferExcep(trnsferExcep);
            }


        }catch(Exception e){
            logger.warn(e.getMessage());
            throw new Exception(e.getMessage());
        }
        if(!ObjectUtils.isEmpty(trnsfrBean)){
            return trnsfrBean;
        }else{
            return null;
        }

    }

    /**
     * @param ssccTrnsfrGrpId
     * @param transferDt
     * @param btchPrgm
     * @throws Exception
     */
    @Override
    public void insertBatchParameter(Long ssccTrnsfrGrpId, Date transferDt, String btchPrgm) throws Exception {
        updateRunMode(btchPrgm);
        updateTransferGrpId(ssccTrnsfrGrpId,btchPrgm);
        updateTransferDt(transferDt,btchPrgm);
    }

    /**
     *
     * @param idTrnsfrGrp
     * @throws Exception
     */
    @Override
    public void deleteEvalTrnsfr(Long idTrnsfrGrp) throws Exception {

        try{
            Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteTrnsfr)
                    .setParameter("idTrnsfrGrp", idTrnsfrGrp);
            query.executeUpdate();

            query = sessionFactory.getCurrentSession().createSQLQuery(deleteTrnsfrGrp)
                    .setParameter("idTrnsfrGrp", idTrnsfrGrp);
            query.executeUpdate();

        }catch(Exception e){
            logger.warn(e.getMessage());
            throw new Exception(e.getMessage());
        }


    }

    /**
     *
     * @param transferDt
     * @throws Exception
     */

    private void updateTransferDt(Date transferDt,String btchPrgm) throws Exception {
        try{
            SimpleDateFormat formatter
                    = new SimpleDateFormat(ServiceConstants.DATE_FORMAT);
            String trnsfrDate = null;
            if(!ObjectUtils.isEmpty(btchPrgm) && ServiceConstants.SSCCT_BATCH_PROGRAM.equalsIgnoreCase(btchPrgm)) {
                trnsfrDate = formatter.format(transferDt) + " 01:00:00";
            }else{
                trnsfrDate = formatter.format(transferDt) + " 01:30:00";
            }
            Query query = sessionFactory.getCurrentSession().createSQLQuery(updateTrnsfrDt)
                    .setParameter("transferDt", trnsfrDate).setParameter("btchPrgm", btchPrgm)
                    ;
            query.executeUpdate();
        }catch(Exception e){
            logger.warn(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     *
     * @param ssccTrnsfrGrpId
     * @throws Exception
     */
    private void updateTransferGrpId(Long ssccTrnsfrGrpId,String btchPrgm) throws Exception {
        try {
            Query query = sessionFactory.getCurrentSession().createSQLQuery(updateTrnsfrGrpId)
                    .setParameter("ssccTrnsfrGrpId", String.valueOf(ssccTrnsfrGrpId))
                    .setParameter("btchPrgm", btchPrgm);
            query.executeUpdate();
        }catch(Exception e){
            logger.warn(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     *
     * @throws Exception
     */
    private void updateRunMode(String btchPrgm) throws Exception {
        try{
            Query query = sessionFactory.getCurrentSession().createSQLQuery(updateRunMode)
                    .setParameter("runMode", ServiceConstants.STR_ONE_VAL)
                    .setParameter("btchPrgm", btchPrgm);
            query.executeUpdate();
        }catch(Exception e){
            logger.warn(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void batchTransfer(String batchPrgm,String source) throws Exception {
        try{
            Date currentDt = new Date();
            currentDt.setSeconds(0);
            currentDt = DateUtils.stringToDateTime(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(currentDt),"MM/dd/yyyy HH:mm:ss");
            BatchParametersId bpId = new BatchParametersId();
            bpId.setNmBatchParameter(SSCCT_BATCH_RUN_MODE);
            bpId.setNmBatchProgram(batchPrgm);
            LockOptions lockOption = new LockOptions(LockMode.PESSIMISTIC_WRITE);
            BatchParameters batchParameters = (BatchParameters) sessionFactory.getCurrentSession().get(BatchParameters.class, bpId, lockOption.setTimeOut(0));
            if(ServiceConstants.ONE.equalsIgnoreCase(batchParameters.getTxtParameterValue())){
                if (currentDt.equals(DateUtils.stringToDateTime(batchParametersDao.getBatchParameters(batchPrgm, SSCCT_BATCH_RUN_DATE).getTxtParameterValue(),SQL_DATE_FORMAT))){
                    batchParameters.setTxtParameterValue(STR_ZERO_VAL);
                    sessionFactory.getCurrentSession().update(batchParameters);
                    transferProc(source);
                }
            }
        }catch(org.hibernate.exception.LockTimeoutException e){
            logger.error("Lock exception while locking Batch Parameters");
            throw new Exception(e.getMessage());
        }  catch(Exception e){
            logger.error("Exception occurred in lockBatchParams method",e);
            throw new Exception(e.getMessage());
        }

    }

    /**
     * @return
     */
    @Override
    public SSCCAutoTransferBean getBatchParameter(String btchPrgm) throws Exception {
        SSCCAutoTransferBean trnsfrBean = null;
        try {
            trnsfrBean = (SSCCAutoTransferBean) ((SQLQuery) sessionFactory.getCurrentSession()
                    .createSQLQuery(trnsfrDt).setParameter("btchPrgm", btchPrgm))
                    .addScalar("trnsfrDate", StandardBasicTypes.STRING)
                    .setResultTransformer(Transformers.aliasToBean(SSCCAutoTransferBean.class)).uniqueResult();
        }catch(Exception e){
            logger.error("Exception occurred while retrieving batch parameters",e);
            throw new Exception(e.getMessage());
        }
        return trnsfrBean;
    }

    /**
     * @param staffId
     * @return
     */
    @Override
    public SSCCAutoTransferBean caseAssignable(Long staffId) {
        Query query = ((SQLQuery) sessionFactory.getCurrentSession()
                .createSQLQuery(caseAssign).setParameter("staffId", staffId))
                .addScalar("caseAssignable", StandardBasicTypes.STRING);

        String caseAssign = (String) query.uniqueResult();

        SSCCAutoTransferBean bean = new SSCCAutoTransferBean();
        bean.setCaseAssignable(caseAssign);

        return bean;
    }

    /**
     *
     * @throws Exception
     */
    public void transferProc(String source) throws Exception {
        SSCCAutoTransferEvalBean evalBean = checkEvalTransferStatus(source);
        if(evalBean!=null && ServiceConstants.N.equalsIgnoreCase(evalBean.getTransfer()) && evalBean.getTrnsfrGrpId()!=0){
            logger.info("*************** Before Transfer ************" + new Date());
            callTransfer(evalBean.getTrnsfrGrpId(),source);
            logger.info("*************** After Transfer ************" + new Date());
        }
    }
}
