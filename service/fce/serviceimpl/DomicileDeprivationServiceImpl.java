package us.tx.state.dfps.service.fce.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.utils.EventUtil;
import us.tx.state.dfps.service.common.utils.FceInitUtil;
import us.tx.state.dfps.service.domiciledeprivation.dto.DomicileDeprivationDto;
import us.tx.state.dfps.service.domiciledeprivation.dto.PrinciplesDto;
import us.tx.state.dfps.service.domiciledeprivation.dto.SelectPersonDomicileDto;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.fce.dao.DomicileDeprivationDao;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceContextDto;
import us.tx.state.dfps.service.fce.dto.FceDomicilePersonWelfDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.service.DomicileDeprivationService;
import us.tx.state.dfps.service.fce.service.FceService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * implements the DomicileDeprivation Service Mar 15, 2018- 12:13:48 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class DomicileDeprivationServiceImpl implements DomicileDeprivationService {

	@Autowired
	private DomicileDeprivationDao domicileDeprivationDao;

	@Autowired
	private FceService fceService;

	@Autowired
	private FceDao fceDao;

	@Autowired
	private EventService eventService;
	
	@Autowired
	private EventUtil eventUtil;

	@Autowired
	FceInitUtil fceInitUtil;

	/**
	 * Method Name: read Method Description: This method fetches the
	 * DomicileDeprivation details
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idLastUpdatePerson
	 * @return DomicileDeprivationDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DomicileDeprivationDto fetchDomicileDeprivation(Long idStage, Long idEvent, Long idLastUpdatePerson) {

		FceContextDto fceContextDto = fceService.initializeFceApplication(idStage, idEvent, idLastUpdatePerson);

		DomicileDeprivationDto domicileDeprivationDto = new DomicileDeprivationDto();
		domicileDeprivationDto.setFceApplicationDto(fceContextDto.getFceApplicationDto());
		domicileDeprivationDto.setFceEligibilityDto(fceContextDto.getFceEligibilityDto());

		domicileDeprivationDto.setCdEventStatus(fceContextDto.getCdEventStatus());

		List<PrinciplesDto> principles = domicileDeprivationDao
				.findPrinciples(fceContextDto.getFceEligibilityDto().getIdFceEligibility());
		List<FceDomicilePersonWelfDto> fceDomicilePersonWelfDtoList = fceDao
				.getFceDomicilePersonWelf(fceContextDto.getFceApplicationDto());

		domicileDeprivationDto.setPrinciples(principles);
		domicileDeprivationDto.setFceDomicilePersonWelfDto(fceDomicilePersonWelfDtoList);

		return domicileDeprivationDto;
	}

	/**
	 * Method Name: saveDomicileDeprivation Method Description: This method
	 * saves the new DomicileDeprivation details
	 * 
	 * @param domicileDeprivationDto
	 * @return CommonHelperRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes saveDomicileDeprivation(DomicileDeprivationDto domicileDeprivationDto) {

		FceApplicationDto fceApplicationDto = domicileDeprivationDto.getFceApplicationDto();
		FceEligibilityDto fceEligibilityDto = domicileDeprivationDto.getFceEligibilityDto();
		List<SelectPersonDomicileDto> selectPersondtoList = domicileDeprivationDto.getDomicileSelectPersonList();
		fceService.verifyCanSave(fceEligibilityDto.getIdStage(), fceEligibilityDto.getIdLastUpdatePerson());
		Long idEvent = fceApplicationDto.getIdEvent();
		String eventType = domicileDeprivationDto.getCdEventStatus();
		if ((ServiceConstants.PENDING_EVENT.equals(eventType)) || (ServiceConstants.COMPLETE_EVENT.equals(eventType))) {
			calculateParentalDeprivation(fceApplicationDto.getCdLivingMonthRemoval(), fceApplicationDto,
					fceEligibilityDto);
		}
		// ADS changes for R2 the nameRelative and the Relationship fields are
		// saved or deleted
		// in to FCE_DOMCL_PRSN_WELF table based on the IDs for the Selected
		// Records
		// in web.

		CommonHelperRes commonHelperRes = fceDao.updateFcEligibilityAndApp(fceApplicationDto, fceEligibilityDto);
		if (!ObjectUtils.isEmpty(selectPersondtoList)) {
			for (SelectPersonDomicileDto selectPersonWelfaredto : selectPersondtoList) {
				if ((ObjectUtils.isEmpty(selectPersonWelfaredto.getIndSelectedForDelete()))
						&& (selectPersonWelfaredto.getIndSelected() == true)) {
					FceDomicilePersonWelfDto fceWelfaredto = new FceDomicilePersonWelfDto();
					fceWelfaredto.setIdFceApplication(fceApplicationDto.getIdFceApplication());
					fceWelfaredto.setIdCreatedPerson(fceApplicationDto.getIdPerson());
					fceWelfaredto.setIdLastUpdatePerson(fceApplicationDto.getIdLastUpdatePerson());
					fceWelfaredto.setIdPerson(selectPersonWelfaredto.getIdPerson());
					fceDao.updateFceDomicilePersonWelf(fceWelfaredto);
				}
				// When Indicator for selectedForDelete is true then its move
				// through the loop
				// and matches the IDPerson and IDFceApplication and deletes
				// records.
				else if (!(ObjectUtils.isEmpty(selectPersonWelfaredto.getIndSelectedForDelete()))
						&& (true == selectPersonWelfaredto.getIndSelectedForDelete())) {
					FceDomicilePersonWelfDto fceWelfaredtoDel = new FceDomicilePersonWelfDto();
					fceWelfaredtoDel.setIdFceApplication(fceApplicationDto.getIdFceApplication());
					fceWelfaredtoDel.setIdPerson(selectPersonWelfaredto.getIdPerson());
					fceDao.deleteFceDomicilePersonWelf(fceWelfaredtoDel);
				}
			}
		}
		if (ObjectUtils.isEmpty(commonHelperRes.getErrorDto())) {
			if (ServiceConstants.NEW.equals(eventType)) {
				eventUtil.changeEventStatus(idEvent, ServiceConstants.NEW, ServiceConstants.PROCESS_EVENT);
			} else if (ServiceConstants.COMPLETE.equals(eventType)) {
				eventUtil.changeEventStatus(idEvent, ServiceConstants.COMPLETE, ServiceConstants.PENDING_EVENT);
			}
		}
		return commonHelperRes;
	}

	/**
	 * Method Name: calculateParentalDeprivation Method Description: This method
	 * calculates the SystemDerivedParentalDeprivation
	 * 
	 * @param cdLivingCondition
	 * @param fceApplication
	 * @param fceEligibility
	 * @return
	 */
	private void calculateParentalDeprivation(String cdLivingCondition, FceApplicationDto fceApplicationDto,
			FceEligibilityDto fceEligibilityDto) {
		fceEligibilityDto.setIndMeetsDpOrNotSystem(ServiceConstants.Y);
		fceEligibilityDto.setIndMeetsDpOrNotEs(ServiceConstants.Y);
		Boolean noneBothPrnt = Boolean.FALSE;
		Boolean noneOnePrnt = Boolean.FALSE;
		if (ServiceConstants.CFCELIV_N.equals(cdLivingCondition)) {
			if (!ObjectUtils.isEmpty(fceEligibilityDto.getIndChildLivingPrnt6Mnths())
					&& (ServiceConstants.Y.equals(fceEligibilityDto.getIndChildLivingPrnt6Mnths()))) {
				if (ServiceConstants.CFCELIV_B.equals(fceApplicationDto.getCdNotaMostRecent()))
					noneBothPrnt = Boolean.TRUE;

				else if (ServiceConstants.CFCELIV_O.equals(fceApplicationDto.getCdNotaMostRecent()))
					noneOnePrnt = Boolean.TRUE;
			}
		}
		Boolean isValid = Boolean.FALSE;

		if (checkCfcelivB(fceApplicationDto, fceEligibilityDto)
				&& (ServiceConstants.CFCELIV_B.equals(cdLivingCondition) || noneBothPrnt)) {
			isValid = Boolean.TRUE;
		}
		if (!ServiceConstants.Y.equals(fceEligibilityDto.getIndAbsentMilitaryWork())
				&& (ServiceConstants.CFCELIV_O.equals(cdLivingCondition) || noneOnePrnt)) {
			isValid = Boolean.TRUE;
		}
		if (!isValid) {
			fceEligibilityDto.setIndMeetsDpOrNotSystem(ServiceConstants.N);
			fceEligibilityDto.setIndMeetsDpOrNotEs(ServiceConstants.N);
		}
	}

	/**
	 * Method Name: checkCfcelivB Method Description: check condition for
	 * CFCELiv='B'
	 * 
	 * @param fceApplication
	 * @param fceEligibility
	 * @ @return boolean
	 */
	private boolean checkCfcelivB(FceApplicationDto fceApplication, FceEligibilityDto fceEligibility) {
		int nbrCertifiedGroup = ServiceConstants.Zero;
		if (!ObjectUtils.isEmpty(fceEligibility.getNbrCertifiedGroup())) {
			nbrCertifiedGroup = fceEligibility.getNbrCertifiedGroup().intValue();
		}
		if ((nbrCertifiedGroup == ServiceConstants.Zero)
				&& ((ServiceConstants.CAPS_FCE.equals(fceApplication.getCdApplication()))
						|| (ServiceConstants.IMP.equals(fceApplication.getCdApplication())))) {
			nbrCertifiedGroup = ServiceConstants.Three;
		}
		double fcePweUnderemployed = ServiceConstants.DoubleZero;
		if (nbrCertifiedGroup > ServiceConstants.Zero) {
			fcePweUnderemployed = getFcePweUnderemployed(nbrCertifiedGroup);
		}
		double fceAmtPweIncome = ObjectUtils.isEmpty(fceEligibility.getAmtPweIncome()) ? ServiceConstants.DoubleZero
				: fceEligibility.getAmtPweIncome();
		boolean pweIsUnderEmployed = (fceAmtPweIncome <= fcePweUnderemployed);
		if (ServiceConstants.Y.equals(fceEligibility.getIndParentDisabled())
				|| ServiceConstants.Y.equals(fceEligibility.getCdPweIrregularUnder100())
				|| ServiceConstants.Y.equals(fceEligibility.getCdPweSteadyUnder100()) || pweIsUnderEmployed) {
			return true;
		}
		return false;
	}

	/**
	 * Method Name: getFcePweUnderemployed Method Description: This method finds
	 * FcePwe Under Employer
	 * 
	 * @param nbrCertifiedGroup
	 * @return double @
	 */
	private double getFcePweUnderemployed(int nbrCertifiedGroup) {
		if (nbrCertifiedGroup > ServiceConstants.MAX_NBR_CERTIFIED) {
			nbrCertifiedGroup = ServiceConstants.MAX_NBR_CERTIFIED;
		}
		//artf133213: Get the income limit for the number of people in a group from table FCE_PWE_UNDEREMPLOYED
		return (double) fceInitUtil.getFcePweUnderemployed(nbrCertifiedGroup );
	}
}
