package us.tx.state.dfps.service.pcaeligdetermination.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.basepcasession.service.BasePcaSessionService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.eventutility.service.EventUtilityService;
import us.tx.state.dfps.service.facility.dto.EligibilityDBDto;
import us.tx.state.dfps.service.pca.dto.PcaAppAndBackgroundDto;
import us.tx.state.dfps.service.pca.dto.PcaApplAndDetermDBDto;
import us.tx.state.dfps.service.pca.dto.PcaEligDeterminationDto;
import us.tx.state.dfps.service.pcaappandbackground.dao.PcaAppAndBackgroundDao;
import us.tx.state.dfps.service.pcaappandbackground.service.PcaAppAndBackgroundService;
import us.tx.state.dfps.service.pcaeligdetermination.dao.PcaEligDeterminationDao;
import us.tx.state.dfps.service.pcaeligdetermination.service.PcaEligDeterminationService;
import us.tx.state.dfps.service.pcaeligdetermination.utils.PcaEligDetermUtils;
import us.tx.state.dfps.service.person.dao.FceEligibilityDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PcaEligDeterminationServiceImpl Oct 16, 2017- 12:04:03 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class PcaEligDeterminationServiceImpl implements PcaEligDeterminationService {

	@Autowired
	PcaEligDeterminationDao pcaEligDeterminationDao;

	@Autowired
	EventUtilityService eventUtilityService;

	@Autowired
	PcaAppAndBackgroundDao pcaAppAndBackgroundDao;

	@Autowired
	BasePcaSessionService basePcaSessionService;

	@Autowired
	PcaAppAndBackgroundService pcaAppAndBackgroundService;

	@Autowired
	FceEligibilityDao fceEligibilityDao;

	@Autowired
	PcaEligDetermUtils pcaEligDetermUtils;

	private static final Logger log = Logger.getLogger(PcaEligDeterminationServiceImpl.class);

	/**
	 * Method Name: fetchEligibilityDetermination Method Description:This method
	 * returns Pca Eligibility Determination Record using Application Event.
	 * 
	 * @param idAppEvent
	 * @return PcaApplAndDetermDBDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PcaApplAndDetermDBDto fetchEligibilityDetermination(Long idAppEvent) {

		PcaApplAndDetermDBDto pcaAppDetermDBDto = new PcaApplAndDetermDBDto();
		log.debug("Entering method fetchEligibilityDetermination in PcaEligDeterminationService");
		if (idAppEvent != 0) {
			PcaEligDeterminationDto determDto = pcaEligDeterminationDao.selectPcaEligDeterminationFromEvent(idAppEvent);
			determDto.setNmInitialDetermPerson(determDto.getNmInitialDetermPerson());
			determDto.setNmFinalDetermPerson(determDto.getNmFinalDetermPerson());
			pcaAppDetermDBDto.setDetermValueBean(determDto);
			EventValueDto appEventValBean = eventUtilityService.fetchEventInfo(idAppEvent);
			pcaAppDetermDBDto.setAppEvent(appEventValBean);
			PcaAppAndBackgroundDto appDto = pcaAppAndBackgroundDao.selectPcaEligAppFromEvent(idAppEvent);
			pcaAppDetermDBDto.setAppValueBean(appDto);
			if (appDto.getIdPlcmtEvent() != 0) {
				EventValueDto plcmtEvent = eventUtilityService.fetchEventInfo(appDto.getIdPlcmtEvent());
				pcaAppDetermDBDto.setPlcmtEvent(plcmtEvent);
			}
		}
		log.debug("Exiting method fetchEligibilityDetermination in PcaEligDeterminationService");
		return pcaAppDetermDBDto;
	}

	/**
	 * Method Name: saveEligibilityDetermination Method Description:This method
	 * saves(updates) Pca Eligibility Determination information. Empty
	 * Eligibility Determination Record will be created when the worker submits
	 * Pca Application. So this function needs to handle updates only.
	 * 
	 * @param pcaAppDetermDB
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long saveEligibilityDetermination(PcaApplAndDetermDBDto pcaAppDetermDB) {

		PcaEligDeterminationDto determDto = pcaAppDetermDB.getDetermValueBean();
		Long idPcaEligDeterm = 0l;
		log.debug("Entering method saveEligibilityDetermination in PcaEligDeterminationService");
		{
			EventValueDto appEvent = pcaAppDetermDB.getAppEvent();

			idPcaEligDeterm = determDto.getIdPcaEligDeterm();
			determDto.setIdLastUpdatePerson(pcaAppDetermDB.getIdLastUpdatePerson());
			pcaEligDeterminationDao.updateEligibilityDetermination(determDto);

			PcaAppAndBackgroundDto appDto = pcaAppDetermDB.getAppValueBean();
			if (isValid(appDto.getCdWithdrawRsn())) {
				appDto.setIdLastUpdatePerson(pcaAppDetermDB.getIdLastUpdatePerson());
				pcaAppAndBackgroundDao.updatePcaEligApplication(appDto);
				basePcaSessionService.withdrawPCAApplication(pcaAppDetermDB,
						pcaAppDetermDB.getAppValueBean().getIdPcaEligApplication(), appEvent);

				pcaAppAndBackgroundDao.markTodoComplete(appEvent.getIdEvent());
			} else {
				pcaAppAndBackgroundService.updateAppEvent(appDto, pcaAppDetermDB.getAppEvent(), 0L, "", "");

				if (ServiceConstants.CEVTSTAT_APRV.equals(appEvent.getCdEventStatus())) {
					pcaAppAndBackgroundDao.markTodoComplete(appEvent.getIdEvent());
				}
			}
		}
		log.debug("Exiting method saveEligibilityDetermination in PcaEligDeterminationService");
		return idPcaEligDeterm;
	}

	/**
	 * Method Name: determinePrelimEligibility Method Description:This method
	 * determines Preliminary Eligibility and the saves the Determination to
	 * database.
	 * 
	 * @param pcaAppDetermDB
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long determinePrelimEligibility(PcaApplAndDetermDBDto pcaAppDetermDBDto){

		Long idAppEvent = 0l;

		log.debug("Entering method determinePrelimEligibility in PcaEligDeterminationService");
		{
			idAppEvent = pcaAppDetermDBDto.getAppEvent().getIdEvent();
			PcaAppAndBackgroundDto appDto = pcaAppAndBackgroundDao.selectPcaEligAppFromEvent(idAppEvent);
			EligibilityDBDto lastFCEligDb = fceEligibilityDao.selectLatestEligibility(appDto.getIdPerson());
			if (lastFCEligDb != null) {
				appDto.setIdEligEvent(lastFCEligDb.getIdEligEvent());
				appDto.setCdFceEligActual(lastFCEligDb.getCdEligActual());
			}
			if (ServiceConstants.N.equals(appDto.getIndChildSibling1())) {
				EligibilityDBDto fcEligDbSibling = fceEligibilityDao
						.selectLatestEligibility(appDto.getIdQualSibPerson());
				if (fcEligDbSibling != null) {
					pcaAppDetermDBDto.setCdQualSiblFceEligActual(fcEligDbSibling.getCdEligActual());
				}
			}
			pcaAppDetermDBDto.setAppValueBean(appDto);
			String cdPrelimEligibility = pcaEligDetermUtils.determinePrelimEligibility(pcaAppDetermDBDto);
			PcaEligDeterminationDto determDto = pcaAppDetermDBDto.getDetermValueBean();
			determDto.setIdLastUpdatePerson(pcaAppDetermDBDto.getIdLastUpdatePerson());
			determDto.setIdInitialDetermPerson(pcaAppDetermDBDto.getIdLastUpdatePerson());
			determDto.setDtInitialDeterm(new java.util.Date());
			determDto.setCdInitialDeterm(cdPrelimEligibility);
			if (determDto.getIdPcaEligDeterm() == 0) {
				determDto.setIdPcaEligApplication(appDto.getIdPcaEligApplication());
				determDto.setIdPlcmtEvent(appDto.getIdPlcmtEvent());
				determDto.setIdCreatedPerson(pcaAppDetermDBDto.getIdLastUpdatePerson());
				pcaEligDeterminationDao.insertPcaEligDetermination(determDto);
			} else {
				pcaEligDeterminationDao.updateEligibilityDetermination(determDto);
			}
			pcaAppAndBackgroundDao.updatePcaEligApplication(appDto);
		}
		log.debug("Exiting method determinePrelimEligibility in PcaEligDeterminationService");
		return idAppEvent;
	}

	/**
	 * Method Name: determineFinalEligibility Method Description:This method
	 * determines Final Eligibility and the saves the determination to database.
	 * 
	 * @param pcaAppDetermDB
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long determineFinalEligibility(PcaApplAndDetermDBDto pcaAppDetermDB){

		Long idAppEvent = 0l;
		log.debug("Entering method determineFinalEligibility in PcaEligDeterminationService");
		{
			PcaEligDeterminationDto determDto = pcaAppDetermDB.getDetermValueBean();
			if (isValid(determDto.getCdInitialDeterm()) == false) {
				determinePrelimEligibility(pcaAppDetermDB);
				determDto = pcaEligDeterminationDao.selectPcaEligDetermination(determDto.getIdPcaEligDeterm());
				pcaAppDetermDB.setDetermValueBean(determDto);
			}
			boolean isChildQualified = pcaEligDetermUtils.determineFinalEligibility(pcaAppDetermDB);
			if (isChildQualified) {
				determDto.setIndAsstDisqualified(ServiceConstants.N);
			} else {
				determDto.setIndAsstDisqualified(ServiceConstants.Y);
			}
			determDto.setCdFinalDeterm(determDto.getCdInitialDeterm());
			determDto.setIdLastUpdatePerson(pcaAppDetermDB.getIdLastUpdatePerson());
			determDto.setIdFinalDetermPerson(pcaAppDetermDB.getIdLastUpdatePerson());
			determDto.setDtFinalDeterm(new java.util.Date());
			pcaEligDeterminationDao.updateEligibilityDetermination(determDto);
			pcaAppAndBackgroundService.updateAppEvent(pcaAppDetermDB.getAppValueBean(), pcaAppDetermDB.getAppEvent(),
					pcaAppDetermDB.getIdLastUpdatePerson(), ServiceConstants.CEVTSTAT_COMP,
					ServiceConstants.APPLICATION_COMP_EVENT_DESC);
		}
		log.debug("Exiting method determineFinalEligibility in PcaEligDeterminationService");
		return idAppEvent;
	}

	/**
	 * Method Name: selectDetermFromIdPcaApp Method Description: This method
	 * returns Eligibility Determination Record for the given Application Id
	 * 
	 * @param idPcaEligApplicationReq
	 * @return PcaEligDeterminationDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PcaEligDeterminationDto selectDetermFromIdPcaApp(Long idPcaEligApplication) {

		PcaEligDeterminationDto determDto = new PcaEligDeterminationDto();
		log.debug("Entering method selectDetermFromIdPcaApp in PcaEligDeterminationService");
		determDto = pcaEligDeterminationDao.selectEligFromIdPcaApp(idPcaEligApplication);
		log.debug("Exiting method selectDetermFromIdPcaApp in PcaEligDeterminationService");
		return determDto;
	}

	/**
	 * Method Name: isValid Method Description:Checks whether the string is
	 * valid or not.
	 * 
	 * @param value
	 * @return boolean
	 */
	public boolean isValid(String value) {
		if (value == null) {
			return false;
		}
		String trimmedString = value.trim();
		return (trimmedString.length() > 0);
	}

}
