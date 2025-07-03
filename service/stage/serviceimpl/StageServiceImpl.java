package us.tx.state.dfps.service.stage.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.VisitationPlanEnglishPrefillData;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.stage.service.StageService;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * has functions to access Stage related information. Oct 12, 2017- 3:13:32 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class StageServiceImpl implements StageService {

	@Autowired
	private StageDao stageDao;

	@Autowired
	private PcaDao pcaDao;

	@Autowired
	private VisitationPlanEnglishPrefillData visitationPlanEnglishPrefillData;

	private static final Logger log = Logger.getLogger(StageServiceImpl.class);

	/**
	 * Method Name: retrieveStageInfo Method Description: This method retrieves
	 * information from Stage table using idStage.
	 * 
	 * @param idStage
	 * @return StageValueBeanDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StageValueBeanDto retrieveStageInfo(Long idStage) {
		log.debug("Entering method retrieveStageInfo in StageUtilityService");
		return stageDao.retrieveStageInfo(idStage);
	}

	/**
	 * This method returns Corresponding Sub Stage Id for the Given Stage Id.
	 * 
	 * If idStage is of Type SUB Stage return idStage else // if idStage is of
	 * PCA Stage. find Corresponding Sub Stage Id and return it.
	 * 
	 * @param idStage
	 * 
	 * @return idSubStage
	 */
	public Long findPriorSubStageId(Long idStage) {

		Long idSubStage = idStage;
		StageDto stage = stageDao.getStageById(idStage);
		String stgType = stage.getCdStage();

		if (!ServiceConstants.CSTAGES_SUB.equals(stgType)) {
			idSubStage = stageDao.findLinkedStageId(idStage);
		}

		return idSubStage;
	}

	/**
	 * Service Name: CSUB61S & CSUB75S Method Name: getStageAndCaseDtls Method
	 * Description: This service will get forms populated by receiving idStage
	 * from controller, then retrieving data from caps_case, stage to get the
	 * forms populated.
	 *
	 * @param idStage
	 * @return StageCaseDtlDto @ the service exception
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)

	public PreFillDataServiceDto getStageAndCaseDtls(Long idStage) {
		// CSEC02D
		StageCaseDtlDto stageCaseDtlDto = pcaDao.getStageAndCaseDtls(idStage);
		return visitationPlanEnglishPrefillData.returnPrefillData(stageCaseDtlDto);
	}
}