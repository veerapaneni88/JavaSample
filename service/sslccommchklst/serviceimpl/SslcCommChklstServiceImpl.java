package us.tx.state.dfps.service.sslccommchklst.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.SslcCommChklstReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.SslcCommChklstPrefillData;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;
import us.tx.state.dfps.service.sslccommchklst.dto.SslcCommChklstDto;
import us.tx.state.dfps.service.sslccommchklst.service.SslcCommChklstService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service for
 * form CFIV4400 Mar 15, 2018- 5:04:48 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Service
@Transactional
public class SslcCommChklstServiceImpl implements SslcCommChklstService {

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private NotifToLawEnforcementDao notifToLawEnforcementDao;

	@Autowired
	private SslcCommChklstPrefillData prefillData;

	/**
	 * Method Name: getChecklistData Method Description: Gets data for checklist
	 * and returns prefill data
	 * 
	 * @param sslcCommChklstReq
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getChecklistData(SslcCommChklstReq sslcCommChklstReq, boolean isApsReferral) {
		// Declare constants and prefill dto
		Long idCase = ServiceConstants.LONG_ZERO_VAL;
		SslcCommChklstDto prefillDto = new SslcCommChklstDto();

		// Determines whether form is cfiv4400 or cfiv2300
		prefillDto.setIsApsReferral(isApsReferral);

		// CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(sslcCommChklstReq.getIdStage());
		if (!ObjectUtils.isEmpty(genericCaseInfoDto)) {
			idCase = genericCaseInfoDto.getIdCase();
			prefillDto.setGenericCaseInfoDto(genericCaseInfoDto);
		}

		// CSES39D
		FacilInvDtlDto facilInvDtlDto = notifToLawEnforcementDao.getFacilityInvDtlbyId(sslcCommChklstReq.getIdStage());

		// CLSCGCD
		if (ObjectUtils.isEmpty(facilInvDtlDto)) {
			facilInvDtlDto = new FacilInvDtlDto();
		}
		List<MultiAddressDto> multiAddressDtoList = notifToLawEnforcementDao
				.getMultiAddress(sslcCommChklstReq.getIdStage(), idCase);
		if (!ObjectUtils.isEmpty(multiAddressDtoList)) {
			facilInvDtlDto.setNmFacilinvstFacility(multiAddressDtoList.get(0).getaCpNmResource());
			facilInvDtlDto.setTxtFacilInvstComments(multiAddressDtoList.get(0).getaFilCdMhmrCode());
		}
		prefillDto.setFacilInvDtlDto(facilInvDtlDto);
		prefillDto.setMultiAddressDtoList(multiAddressDtoList);

		return prefillData.returnPrefillData(prefillDto);
	}

	/**
	 * Method Name: getChecklistData Method Description: Gets data for checklist
	 * and returns prefill data
	 * 
	 * @param sslcCommChklstReq
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getApsReferralData(SslcCommChklstReq sslcCommChklstReq) {
		// Declare constants and prefill dto
		Long idCase = ServiceConstants.LONG_ZERO_VAL;
		SslcCommChklstDto prefillDto = new SslcCommChklstDto();

		// To set form cfiv4300
		prefillDto.setIsApsReferral(ServiceConstants.FALSEVAL);
		prefillDto.setApsRefForm(ServiceConstants.ADULT_PROTECTIVE_SERVICES);
		;

		// CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(sslcCommChklstReq.getIdStage());
		if (!ObjectUtils.isEmpty(genericCaseInfoDto)) {
			idCase = genericCaseInfoDto.getIdCase();
			prefillDto.setGenericCaseInfoDto(genericCaseInfoDto);
		}

		// CSES39D
		FacilInvDtlDto facilInvDtlDto = notifToLawEnforcementDao.getFacilityInvDtlbyId(sslcCommChklstReq.getIdStage());

		// CLSCGCD
		if (ObjectUtils.isEmpty(facilInvDtlDto)) {
			facilInvDtlDto = new FacilInvDtlDto();
		}
		List<MultiAddressDto> multiAddressDtoList = notifToLawEnforcementDao
				.getMultiAddress(sslcCommChklstReq.getIdStage(), idCase);
		if (!ObjectUtils.isEmpty(multiAddressDtoList)) {
			facilInvDtlDto.setNmFacilinvstFacility(multiAddressDtoList.get(0).getaCpNmResource());
			facilInvDtlDto.setTxtFacilInvstComments(multiAddressDtoList.get(0).getaFilCdMhmrCode());
		}
		prefillDto.setFacilInvDtlDto(facilInvDtlDto);
		prefillDto.setMultiAddressDtoList(multiAddressDtoList);

		return prefillData.returnPrefillData(prefillDto);
	}

}
