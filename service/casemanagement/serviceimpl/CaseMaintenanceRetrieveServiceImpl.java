package us.tx.state.dfps.service.casemanagement.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.casemanagement.dao.FetchAllVictimsForCaseDao;
import us.tx.state.dfps.service.casemanagement.dao.FetchFacilityNameOfStageDao;
import us.tx.state.dfps.service.casemanagement.dao.FetchFullRowFromStageDao;
import us.tx.state.dfps.service.casemanagement.dao.FetchFullRowOfCapsCaseDao;
import us.tx.state.dfps.service.casemanagement.dao.FetchIncomingFacilityDao;
import us.tx.state.dfps.service.casemanagement.dao.FetchNameResourceDao;
import us.tx.state.dfps.service.casemanagement.dao.FetchNamesDao;
import us.tx.state.dfps.service.casemanagement.dao.FetchRecentlyClosedPreviousStageDao;
import us.tx.state.dfps.service.casemanagement.service.CaseMaintenanceRetrieveService;
import us.tx.state.dfps.service.casepackage.dto.FacilityNameInputDto;
import us.tx.state.dfps.service.casepackage.dto.FacilityNameOutputDto;
import us.tx.state.dfps.service.casepackage.dto.PreviousStageInputDto;
import us.tx.state.dfps.service.casepackage.dto.PreviousStageOutputDto;
import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityOutputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveAllVictimsInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveAllVictimsOutputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveCapsCaseInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveCapsCaseOutputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveFullRowStageInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveFullRowStageOutputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveNamesInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveNamesOutputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveNmResourceInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveNmResourceOutputDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CaseMaintenanceRetrieveReq;
import us.tx.state.dfps.service.common.response.CaseMaintenanceRetrieveRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:REST Service
 * IMPL class for display CaseMaintenance page Jan 22, 2018- 10:44:33 PM © 2017
 * Texas Department of Family and Protective Services
 */

// RetrieveCaseNameServiceImpl
@Service
@Transactional
public class CaseMaintenanceRetrieveServiceImpl implements CaseMaintenanceRetrieveService {

	@Autowired
	MessageSource messageSource;

	// Ccmna3d
	@Autowired
	FetchFacilityNameOfStageDao fetchFacilityNameOfStageDao;

	// Ccmni0d
	@Autowired
	FetchNameResourceDao fetchNameResourceDao;

	// Ccmnd7d
	@Autowired
	FetchNamesDao fetchNamesDao;

	// Ccmnb5d
	@Autowired
	FetchRecentlyClosedPreviousStageDao fetchRecentlyClosedPreviousStageDao;

	// Cint09d
	@Autowired
	FetchIncomingFacilityDao fetchIncomingFacilityDao;

	// Ccmnd9d
	@Autowired
	FetchFullRowOfCapsCaseDao fetchFullRowOfCapsCaseDao;

	// Cint40dD
	@Autowired
	FetchFullRowFromStageDao fetchFullRowFromStageDao;

	// Clsc90d
	@Autowired
	FetchAllVictimsForCaseDao fetchAllVictimsForCaseDao;

	public static final String CLASSIFICATION_LRC = "LRC";
	public static final String CLASSIFICATION_LCC = "LCC";
	public static final String CLASSIFICATION_RCL = "RCL";
	public static final String CLASSIFICATION_CCL = "CCL";
	public static final String SUBCARE_STAGE = "SUB";
	public static final String ADOPTION_STAGE = "ADO";
	public static final String PAL_STAGE = "PAL";
	public static final String POST_ADOPT_STAGE = "PAD";
	public static final String PERM_CARE_ASSIST_STAGE = "PCA";
	public static final int NM_PERSON_FULL_LEN = 26;
	public static final String CASE_NM_ET_AL = " et al";
	public static final int CASE_NM_ET_AL_LEN = 6;
	public static final String CLASSIFICATION_APS_FAC = "AFC";
	public static final String PERSON_ROLE = "PC";

	private static final Logger log = Logger.getLogger(CaseMaintenanceRetrieveServiceImpl.class);

	/**
	 * Method Description:This Method is used to retireve the information of the
	 * case
	 * 
	 * @param caseMaintenanceRetrieveReq
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CaseMaintenanceRetrieveRes callRetrieveCaseNameService(
			CaseMaintenanceRetrieveReq caseMaintenanceRetrieveReq) {
		log.debug("Entering method callRetrieveCaseNameService in RetrieveCaseNameServiceImpl");
		CaseMaintenanceRetrieveRes caseMaintenanceRetrieveRes = new CaseMaintenanceRetrieveRes();
		boolean bLicensingClassification = false;
		retrieveFacilityName(caseMaintenanceRetrieveReq, caseMaintenanceRetrieveRes);
		retrieveNmResource(caseMaintenanceRetrieveReq, caseMaintenanceRetrieveRes);
		retrieveNamesForChangeName(caseMaintenanceRetrieveReq, caseMaintenanceRetrieveRes);
		if (CLASSIFICATION_APS_FAC.equalsIgnoreCase(caseMaintenanceRetrieveRes.getCdStageProgram())) {

			retrieveAllVictims(caseMaintenanceRetrieveReq, caseMaintenanceRetrieveRes);
		}

		if ((CLASSIFICATION_LRC.equalsIgnoreCase(caseMaintenanceRetrieveRes.getCdStageProgram()))
				|| (CLASSIFICATION_LCC.equalsIgnoreCase(caseMaintenanceRetrieveRes.getCdStageProgram()))) {

			getMostRecentlyClosedPreviousIdStage(caseMaintenanceRetrieveReq, caseMaintenanceRetrieveRes);
			bLicensingClassification = true;
		}

		if (bLicensingClassification) {
			if (caseMaintenanceRetrieveRes.getIdPriorStage() == 0) {
				caseMaintenanceRetrieveRes.setIdPriorStage(caseMaintenanceRetrieveReq.getIdStage());
			}
			retrieveIncomingFacility(caseMaintenanceRetrieveReq, caseMaintenanceRetrieveRes);
		}

		if ((caseMaintenanceRetrieveReq.getCdStage().equalsIgnoreCase(SUBCARE_STAGE))
				|| (caseMaintenanceRetrieveReq.getCdStage().equalsIgnoreCase(PAL_STAGE))
				|| (caseMaintenanceRetrieveReq.getCdStage().equalsIgnoreCase(ADOPTION_STAGE))
				|| (caseMaintenanceRetrieveReq.getCdStage().equalsIgnoreCase(POST_ADOPT_STAGE))
				|| (caseMaintenanceRetrieveReq.getCdStage().equalsIgnoreCase(PERM_CARE_ASSIST_STAGE))) {
			retrieveFromStage(caseMaintenanceRetrieveReq, caseMaintenanceRetrieveRes);
		} else {
			retrieveCapsCase(caseMaintenanceRetrieveReq, caseMaintenanceRetrieveRes);
		}

		log.debug("Exiting method callRetrieveCaseNameService in RetrieveCaseNameServiceImpl");
		return caseMaintenanceRetrieveRes;
	}

	/**
	 * Method Name: Method Description:This Method is used to retireve the
	 * information of all the closure notices for a given EventId. CCMNA3D
	 * 
	 * @param pInputMsg
	 * @param pOutputMsg
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void retrieveFacilityName(CaseMaintenanceRetrieveReq pInputMsg, CaseMaintenanceRetrieveRes pOutputMsg) {
		log.debug("Entering method CCMNA3D in RetrieveCaseNameServiceImpl");
		FacilityNameInputDto pCCMNA3DInputRec = new FacilityNameInputDto();
		FacilityNameOutputDto pCCMNA3DOutputRec = new FacilityNameOutputDto();

		pCCMNA3DInputRec.setIdStage(pInputMsg.getIdStage());
		fetchFacilityNameOfStageDao.retrieveFacilityName(pCCMNA3DInputRec, pCCMNA3DOutputRec);
		if (pCCMNA3DOutputRec != null) {
			pOutputMsg.setNmFacilInvstFacility(pCCMNA3DOutputRec.getNmFacilInvstFacility());

		}

		log.debug("Exiting method CCMNA3D in RetrieveCaseNameServiceImpl");
	}

	/**
	 * Method Name: Method Description:This Method is used to retireve the
	 * information of all CCMNI0D
	 * 
	 * @param pInputMsg
	 * @param pOutputMsg
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void retrieveNmResource(CaseMaintenanceRetrieveReq pInputMsg, CaseMaintenanceRetrieveRes pOutputMsg) {
		log.debug("Entering method CCMNI0D in RetrieveCaseNameServiceImpl");
		RetrieveNmResourceInputDto pCCMNI0DInputRec = new RetrieveNmResourceInputDto();
		RetrieveNmResourceOutputDto pCCMNI0DOutputRec = new RetrieveNmResourceOutputDto();

		pCCMNI0DInputRec.setIdStage(pInputMsg.getIdStage());
		fetchNameResourceDao.fetchNameResource(pCCMNI0DInputRec, pCCMNI0DOutputRec);
		if (!ObjectUtils.isEmpty(pCCMNI0DOutputRec.getNmResourceInvstFacility())) {
			pOutputMsg.setNmFacilInvstFacility(pCCMNI0DOutputRec.getNmResourceInvstFacility());

		}

		log.debug("Exiting method CCMNI0D in RetrieveCaseNameServiceImpl");
	}

	/**
	 * * Method Name: Method Description:This Method is used to retireve the
	 * information of all CCMND7D
	 * 
	 * @param caseMaintenanceRetrieveReq
	 * @param caseMaintenanceRetrieveRes
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void retrieveNamesForChangeName(CaseMaintenanceRetrieveReq caseMaintenanceRetrieveReq,
			CaseMaintenanceRetrieveRes caseMaintenanceRetrieveRes) {
		log.debug("Entering method retrieveNamesForChangeName in RetrieveCaseNameServiceImpl");
		short i = 0;
		RetrieveNamesInputDto retrieveNamesInputDto = new RetrieveNamesInputDto();
		RetrieveNamesOutputDto retrieveNamesOutputDto = new RetrieveNamesOutputDto();

		retrieveNamesInputDto.setIdCase(caseMaintenanceRetrieveReq.getIdCase());
		retrieveNamesInputDto.setIdStage(caseMaintenanceRetrieveReq.getIdStage());
		fetchNamesDao.fetchNames(retrieveNamesInputDto, retrieveNamesOutputDto);
		if (retrieveNamesOutputDto != null) {
			for (i = 0; i < retrieveNamesOutputDto.getPrincipals().size(); i++) {
				if ((caseMaintenanceRetrieveReq.getCdStage().equalsIgnoreCase(SUBCARE_STAGE))
						|| (caseMaintenanceRetrieveReq.getCdStage().equalsIgnoreCase(PAL_STAGE))
						|| (caseMaintenanceRetrieveReq.getCdStage().equalsIgnoreCase(ADOPTION_STAGE))
						|| (caseMaintenanceRetrieveReq.getCdStage().equalsIgnoreCase(POST_ADOPT_STAGE))
						|| (caseMaintenanceRetrieveReq.getCdStage().equalsIgnoreCase(PERM_CARE_ASSIST_STAGE))) {
					if (retrieveNamesOutputDto.getPrincipals().get(i).getCdStagePersRole()
							.equalsIgnoreCase(PERSON_ROLE)) {
						caseMaintenanceRetrieveRes.getNmPersonFull()
								.add(retrieveNamesOutputDto.getPrincipals().get(i).getNmPersonFull());
						caseMaintenanceRetrieveRes
								.setCdStagePersRole(retrieveNamesOutputDto.getPrincipals().get(i).getCdStagePersRole());
						caseMaintenanceRetrieveRes.getUlIdNmPerson()
								.add(retrieveNamesOutputDto.getPrincipals().get(i).getIdNmPerson());
					}
				} else {
					caseMaintenanceRetrieveRes.getNmPersonFull()
							.add(retrieveNamesOutputDto.getPrincipals().get(i).getNmPersonFull());
					caseMaintenanceRetrieveRes
							.setCdStageProgram(retrieveNamesOutputDto.getPrincipals().get(i).getCdStageProgram());
					caseMaintenanceRetrieveRes
							.setCdStagePersRole(retrieveNamesOutputDto.getPrincipals().get(i).getCdStagePersRole());
					caseMaintenanceRetrieveRes.getUlIdNmPerson()
							.add(retrieveNamesOutputDto.getPrincipals().get(i).getIdNmPerson());
				}

			}

			caseMaintenanceRetrieveRes.setRowQty(retrieveNamesOutputDto.getRowQty());

		}

		log.debug("Exiting method CCMND7D in RetrieveCaseNameServiceImpl");
	}

	/**
	 * * Method Name: Method Description:This Method is used to retireve the
	 * information of all CallCCMNB5D
	 * 
	 * @param pInputMsg
	 * @param pOutputMsg
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void getMostRecentlyClosedPreviousIdStage(CaseMaintenanceRetrieveReq pInputMsg,
			CaseMaintenanceRetrieveRes pOutputMsg) {
		log.debug("Entering method CallCCMNB5D in RetrieveCaseNameServiceImpl");
		PreviousStageInputDto pCCMNB5DInputRec = new PreviousStageInputDto();
		PreviousStageOutputDto pCCMNB5DOutputRec = new PreviousStageOutputDto();

		pCCMNB5DInputRec.setIdStage(pInputMsg.getIdStage());
		fetchRecentlyClosedPreviousStageDao.fetchRecentlyClosedPreviousStage(pCCMNB5DInputRec, pCCMNB5DOutputRec);
		if (pCCMNB5DOutputRec != null) {
			pOutputMsg.setIdPriorStage(pCCMNB5DOutputRec.getIdPriorStage());
		}

		log.debug("Exiting method CallCCMNB5D in RetrieveCaseNameServiceImpl");
	}

	/**
	 * * Method Name: Method Description:This Method is used to retireve the
	 * information of all CallCINT09D
	 * 
	 * @param pInputMsg
	 * @param pOutputMsg
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void retrieveIncomingFacility(CaseMaintenanceRetrieveReq pInputMsg, CaseMaintenanceRetrieveRes pOutputMsg) {
		log.debug("Entering method CallCINT09D in RetrieveCaseNameServiceImpl");
		RetreiveIncomingFacilityInputDto pCINT09DInputRec = new RetreiveIncomingFacilityInputDto();
		RetreiveIncomingFacilityOutputDto pCINT09DOutputRec = new RetreiveIncomingFacilityOutputDto();

		pCINT09DInputRec.setIdStage(pOutputMsg.getIdPriorStage());
		fetchIncomingFacilityDao.fetchIncomingFacility(pCINT09DInputRec, pCINT09DOutputRec);
		if (pCINT09DOutputRec != null) {
			pOutputMsg.setNmIncmgFacilName(pCINT09DOutputRec.getNmIncmgFacilName());
		}

		log.debug("Exiting method CallCINT09D in RetrieveCaseNameServiceImpl");
	}

	/**
	 * * Method Name: Method Description:This Method is used to retireve the
	 * information of all CallCCMND9D
	 * 
	 * @param pInputMsg
	 * @param pOutputMsg
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void retrieveCapsCase(CaseMaintenanceRetrieveReq pInputMsg, CaseMaintenanceRetrieveRes pOutputMsg) {
		log.debug("Entering method CallCCMND9D in RetrieveCaseNameServiceImpl");
		RetrieveCapsCaseInputDto pCCMND9DInputRec = new RetrieveCapsCaseInputDto();
		RetrieveCapsCaseOutputDto pCCMND9DOutputRec = new RetrieveCapsCaseOutputDto();

		pCCMND9DInputRec.setIdCase(pInputMsg.getIdCase());
		fetchFullRowOfCapsCaseDao.retrieveCapsCase(pCCMND9DInputRec, pCCMND9DOutputRec);
		if (pCCMND9DOutputRec != null) {
			pOutputMsg.setNmPersonHistFull(pCCMND9DOutputRec.getNmCase());

		}

		log.debug("Exiting method CallCCMND9D in RetrieveCaseNameServiceImpl");
	}

	/**
	 * * Method Name: Method Description:This Method is used to retireve the
	 * information of all CallCINT40D
	 * 
	 * @param pInputMsg
	 * @param pOutputMsg
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void retrieveFromStage(CaseMaintenanceRetrieveReq pInputMsg, CaseMaintenanceRetrieveRes pOutputMsg) {
		log.debug("Entering method CallCINT40D in RetrieveCaseNameServiceImpl");
		RetrieveFullRowStageInputDto pCINT40DInputRec = new RetrieveFullRowStageInputDto();
		RetrieveFullRowStageOutputDto pCINT40DOutputRec = new RetrieveFullRowStageOutputDto();

		pCINT40DInputRec.setIdStage(pInputMsg.getIdStage());
		pCINT40DOutputRec = fetchFullRowFromStageDao.retrieveStage(pCINT40DInputRec);
		if (pCINT40DOutputRec != null) {
			pOutputMsg.setNmPersonHistFull(pCINT40DOutputRec.getNmStage());
		}

		log.debug("Exiting method CallCINT40D in RetrieveCaseNameServiceImpl");
	}

	/**
	 * Method Name: Method Description:This Method is used to retrieve the
	 * information of all victims
	 * 
	 * @param caseMaintenanceRetrieveReq
	 * @param caseMaintenanceRetrieveRes
	 */
	
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void retrieveAllVictims(CaseMaintenanceRetrieveReq caseMaintenanceRetrieveReq, CaseMaintenanceRetrieveRes caseMaintenanceRetrieveRes) {
		
		RetrieveAllVictimsInputDto victimsInputDto = new RetrieveAllVictimsInputDto();
		RetrieveAllVictimsOutputDto victimsOutputDto = new RetrieveAllVictimsOutputDto();

		//Defect 10800
		victimsInputDto.setIdStage(caseMaintenanceRetrieveReq.getIdStage());
		fetchAllVictimsForCaseDao.retrieveAllVictim(victimsInputDto, victimsOutputDto);
		if (!CollectionUtils.isEmpty(victimsOutputDto.getVictimList()) && victimsOutputDto.getVictimList().size() > 0) {
			victimsOutputDto.getVictimList().parallelStream().forEach(victim->{
				caseMaintenanceRetrieveRes.getNmPersonFull()
						.add(appendEtAlToName(victim.getNmPersonFull()));
				caseMaintenanceRetrieveRes.getUlIdNmPerson()
						.add(ServiceConstants.ZERO);
			});
		}

	}
	 

	
	/**
	 * Method Name: appendEtAlToName
	 * 
	 * Method Description: This method will append the string "et al" to the
	 * possible case name before it is placed into the output array.
	 * 
	 * @param nameToAppend
	 * @return void
	 */
	@Override
	public String appendEtAlToName(String nameToAppend) {
		//Defect 10800
		if (nameToAppend.length() < NM_PERSON_FULL_LEN - CASE_NM_ET_AL_LEN) {
			nameToAppend += CASE_NM_ET_AL;
		} else {
			nameToAppend = nameToAppend.substring(0, 18)+CASE_NM_ET_AL;
		}
		return nameToAppend;
	}

}
