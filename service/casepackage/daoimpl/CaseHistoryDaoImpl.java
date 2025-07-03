/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jul 17, 2017- 10:54:03 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casepackage.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casepackage.dao.CaseHistoryDao;
import us.tx.state.dfps.service.casepackage.dto.CaseHistoryCaseDto;
import us.tx.state.dfps.service.casepackage.dto.CaseHistoryCommonDto;
import us.tx.state.dfps.service.casepackage.dto.CaseHistoryStagePCSPDto;
import us.tx.state.dfps.service.casepackage.dto.CaseHistoryUtcStageDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.RetrvCaseHistoryReq;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 17, 2017- 10:54:03 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class CaseHistoryDaoImpl implements CaseHistoryDao {

	public CaseHistoryDaoImpl() {

	}

	@Autowired
	MessageSource messageSource;

	@Value("${CaseHistoryDaoImpl.getCaseHistory}")
	private String getCaseHistory;

	@Value("${CaseHistoryDaoImpl.getCaseStageHstrynew}")
	private String getCaseStageHistory;

	@Value("${CaseHistoryDaoImpl.getCaseStageHstryUtc}")
	private String getCaseStageHistoryUtc;

	@Value("${CaseHistoryDaoImpl.getPCSPDetails}")
	private String getPCSPDetails;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(CaseHistoryDaoImpl.class);

	/**
	 * Method Description: get the Case History List for the passed Case Id
	 * 
	 * @param: retrvCaseHistoryReq
	 * @return: List<CaseHistoryCaseDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CaseHistoryCaseDto> getCaseHistoryCaseList(RetrvCaseHistoryReq retrvCaseHistoryReq) {

		log.info("Transaction Id in method getCaseHistoryCaseList: " + retrvCaseHistoryReq.getTransactionId());
		List<CaseHistoryCaseDto> caseHistoryCaseList = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();
		// Same Query as in Principal Case History screen. A few more fields
		// have been added which are required for the new Case History page
		Query retrieveCaseHistCaseList = session.createSQLQuery(getCaseHistory)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdCounty", StandardBasicTypes.STRING)
				.addScalar("cdCaseProgram", StandardBasicTypes.STRING)
				.addScalar("indSensitive", StandardBasicTypes.STRING)
				.addScalar("dtCaseClosed", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCaseOpened", StandardBasicTypes.TIMESTAMP).addScalar("nmCase", StandardBasicTypes.STRING)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("indCaseLink", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("nmPrimary", StandardBasicTypes.STRING)
				.addScalar("idCaseMergeTo", StandardBasicTypes.LONG)
				.addScalar("indSafetyCheckList", StandardBasicTypes.STRING)
				.addScalar("indWorkerSafety", StandardBasicTypes.STRING)
				.addScalar("workerSafety", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(CaseHistoryCaseDto.class));
		retrieveCaseHistCaseList.setParameter("idCase", retrvCaseHistoryReq.getIdCase());
		caseHistoryCaseList = (List<CaseHistoryCaseDto>) retrieveCaseHistCaseList.list();
		return caseHistoryCaseList;
	}

	/**
	 * Method Description: Get the stages for the passed case id
	 * 
	 * @param: idCase
	 * @param: idTransaction
	 * @return: List<CaseHistoryCommonDto>
	 */
	public List<CaseHistoryCommonDto> getCaseHistoryStageCommonList(Long idCase, String idTransaction) {
		log.info("Transaction Id in method getCaseHistoryStageCommonList: " + idTransaction);
		Session session = sessionFactory.getCurrentSession();

		// Query for fetching the Stage and Allegation details for the cases
		// which are fetched in above query. Only the query is formed here, the
		// execution happens in the below for loop
		Query retrCaseStageHistoryList = session.createSQLQuery(getCaseStageHistory)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStageReasClosed", StandardBasicTypes.STRING)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("indFatality", StandardBasicTypes.STRING)
				.addScalar("cdAllegType", StandardBasicTypes.STRING)
				.addScalar("cdIntAllegType", StandardBasicTypes.STRING)
				.addScalar("cdCpsOverallDisp", StandardBasicTypes.STRING)
				.addScalar("nmAllegVictim", StandardBasicTypes.STRING)
				.addScalar("nmAllegPerp", StandardBasicTypes.STRING).addScalar("cdAllegDisp", StandardBasicTypes.STRING)
				.addScalar("nmIntAllegVictim", StandardBasicTypes.STRING)
				.addScalar("nmIntAllegPerp", StandardBasicTypes.STRING)
				.addScalar("reasonInvClosed", StandardBasicTypes.STRING)
				.addScalar("reasOpenSrvcs", StandardBasicTypes.STRING)
				.addScalar("dispSeverity", StandardBasicTypes.STRING)
				.addScalar("cdSafetyDecision", StandardBasicTypes.STRING)
				.addScalar("cdFinalRiskLvl", StandardBasicTypes.STRING)
				.addScalar("cdRiskAssmtFind", StandardBasicTypes.STRING)
				.addScalar("arTxtFactor76", StandardBasicTypes.STRING)
				.addScalar("arTxtFactor77", StandardBasicTypes.STRING)
				.addScalar("arTxtFactor78", StandardBasicTypes.STRING)
				.addScalar("arTxtFactor79", StandardBasicTypes.STRING)
				.addScalar("assmtDiscussion", StandardBasicTypes.STRING)
				.addScalar("cdSafetyDecsnNew", StandardBasicTypes.STRING)
				.addScalar("cdFinalRiskNew", StandardBasicTypes.STRING)
				.addScalar("nmHousehold", StandardBasicTypes.STRING)
				.addScalar("nmHouseholdAR", StandardBasicTypes.STRING)
				.addScalar("cdInvCnclsnStatus", StandardBasicTypes.STRING)
				.addScalar("cdArCnclsnStatus", StandardBasicTypes.STRING)
				.addScalar("cdArClosureReason", StandardBasicTypes.STRING)
				.addScalar("cdStagePriority", StandardBasicTypes.STRING)
				.addScalar("cdStageType", StandardBasicTypes.STRING)
				.addScalar("cdSdmRiskStatus", StandardBasicTypes.STRING)
				.addScalar("intakeNarrative", StandardBasicTypes.BINARY)
				.addScalar("dtLegalStatusEff", StandardBasicTypes.DATE)
				.addScalar("cdLegalCounty", StandardBasicTypes.STRING)
				.addScalar("txtCauseNumber", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatus", StandardBasicTypes.STRING)
				.addScalar("cdDischargeReas", StandardBasicTypes.STRING)
				.addScalar("cdPlacementType", StandardBasicTypes.STRING)
				.addScalar("cdLivingArrangeType", StandardBasicTypes.STRING)
				.addScalar("dtPlacementStart", StandardBasicTypes.DATE)
				.addScalar("dtPlacementEnd", StandardBasicTypes.DATE)
				.addScalar("nmPlacementPerson", StandardBasicTypes.STRING)
				.addScalar("nmPlacementFacility", StandardBasicTypes.STRING)
				.addScalar("contactNarrative", StandardBasicTypes.BINARY)
				.addScalar("idContactEvent", StandardBasicTypes.LONG)
				.addScalar("cdPALLivingArrange", StandardBasicTypes.STRING)
				// Uncommented as part of R1.1 to R2 code Merge
				.addScalar("txtReasonArClosed", StandardBasicTypes.STRING)
				.addScalar("txtReasonArOpenServ", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(CaseHistoryCommonDto.class));

		List<CaseHistoryCommonDto> caseHistoryCommonDtlsList;
		retrCaseStageHistoryList.setParameter("idCase", idCase);
		// Currently only the INT, INV and AR stages are to be pulled from
		// the database to be displayed in Case History. This would have to
		// be changed as part of future releases
		retrCaseStageHistoryList.setParameterList("cdStgList", ServiceConstants.CASE_HIST_STAGES);
		caseHistoryCommonDtlsList = (List<CaseHistoryCommonDto>) retrCaseStageHistoryList.list();
		return caseHistoryCommonDtlsList;
	}

	/**
	 * Method Description: Get the stages closed details for the passed case id
	 * 
	 * @param: List<idCase>
	 * @param: idTransaction
	 * @return: List<CaseHistoryUtcStageDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CaseHistoryUtcStageDto> getCaseHistoryStageClosedList(List<Long> idCaseList, String idTransaction) {

		log.info("Transaction Id in method getCaseHistoryStageClosedList: " + idTransaction);
		Session session = sessionFactory.getCurrentSession();
		Query retrCaseStageHistoryList = session.createSQLQuery(getCaseStageHistoryUtc)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("dtStageClosed", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdReasonClosed", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(CaseHistoryUtcStageDto.class));
		List<CaseHistoryUtcStageDto> caseHistoryUtcDtlsList;
		retrCaseStageHistoryList.setParameterList("idCaseList", idCaseList);
		retrCaseStageHistoryList.setParameterList("cdStgList", ServiceConstants.CASE_HIST_STAGES);
		caseHistoryUtcDtlsList = (List<CaseHistoryUtcStageDto>) retrCaseStageHistoryList.list();
		return caseHistoryUtcDtlsList;
	}

	/**
	 * Method Description: This method is used to get the PCSP details for the
	 * FPR stage to be displayed in the Case History Page
	 * 
	 * @param idCase
	 *            - Case ID for which the records should be fetched
	 * @return List<CaseHistoryStagePCSPDto> - List of PCSP records for the Case
	 */
	public List<CaseHistoryStagePCSPDto> getPCSPDetails(Long idCase) {

		List<CaseHistoryStagePCSPDto> pcspDetailsList = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();
		Query retrCaseStageHistoryList = session.createSQLQuery(getPCSPDetails)
				.addScalar("dtPCSPStart", StandardBasicTypes.DATE).addScalar("dtPCSPEnd", StandardBasicTypes.DATE)
				.addScalar("nmPrimaryCaregiver", StandardBasicTypes.STRING)
				.addScalar("nmChild", StandardBasicTypes.STRING).addScalar("cdRelToChild", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(CaseHistoryStagePCSPDto.class));
		retrCaseStageHistoryList.setParameter("idCase", idCase);
		pcspDetailsList = (List<CaseHistoryStagePCSPDto>) retrCaseStageHistoryList.list();
		return pcspDetailsList;
	}
}
