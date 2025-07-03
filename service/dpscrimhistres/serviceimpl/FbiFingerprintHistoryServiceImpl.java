package us.tx.state.dfps.service.dpscrimhistres.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dto.CrimHistDto;
import us.tx.state.dfps.service.admin.dto.EmpNameDto;
import us.tx.state.dfps.service.casepackage.dao.SpecialHandlingCaseDetailFetchDao;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingCaseDetailInDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingCaseDetailOutDto;
import us.tx.state.dfps.service.common.request.DPSCrimHistResReq;
import us.tx.state.dfps.service.dpscrimhistres.dto.DPSCrimHistResDto;
import us.tx.state.dfps.service.dpscrimhistres.service.FbiFingerprintHistoryService;
import us.tx.state.dfps.service.dpscrimhistres.service.DPSCrimHistResService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.FbiFingerprintHistoryPrefillData;
import us.tx.state.dfps.service.person.dao.NameDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: service
 * implementation for form fbifingerprinthistory Feb 5, 2021- 2:46:30 PM © 2017 Texas
 * Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 02/05/2021 thompswa artf172715 initial.
 * 08/24/2023 thompswa artf251083 add getNameWithSuffix.
 */
@Service
@Transactional
public class FbiFingerprintHistoryServiceImpl implements FbiFingerprintHistoryService {

	@Autowired
	SpecialHandlingCaseDetailFetchDao specialHandlingCaseDetailFetchDao;

	@Autowired
	FbiFingerprintHistoryPrefillData fbiFingerprintHistoryPrefillData;

	@Autowired
	NameDao nameDao;
	@Autowired
	DPSCrimHistResService dpsCrimHistResService;

	/**
	 * Service Name: CCFC34S Description: This service will populate the FBI
	 * Fingerprint History manual use case. This form will be used by the 
	 * records check requestor to verify the onboarding worker for fingerprinting.
	 * 
	 * @param DPSCrimHistResReq
	 * @return PreFillDataServiceDto
	 */

	@Override
	public PreFillDataServiceDto getFbiFingerprintHistRes(DPSCrimHistResReq dPSCrimHistResReq) {

		DPSCrimHistResDto dPSCrimHistResDto = new DPSCrimHistResDto();

		EmpNameDto empNameDtoPerson = new EmpNameDto();
		EmpNameDto empNameDtoReq = new EmpNameDto();

		// CallCCMNB1D
		SpecialHandlingCaseDetailInDto specialHandlingCaseDetailInDto = new SpecialHandlingCaseDetailInDto();
		SpecialHandlingCaseDetailOutDto specialHandlingCaseDetailOutDto = new SpecialHandlingCaseDetailOutDto();
		specialHandlingCaseDetailInDto.setIdCase(dPSCrimHistResReq.getIdCase());
		specialHandlingCaseDetailOutDto = specialHandlingCaseDetailFetchDao
				.specialHandlingCaseDetailFetch(specialHandlingCaseDetailInDto);

		// CallCSEC59D
		CrimHistDto crimHistDto = new CrimHistDto();
		crimHistDto = nameDao.getCriminalHistById(dPSCrimHistResReq.getIdCrimHist());

		if (!ObjectUtils.isEmpty(crimHistDto.getDtRecCheckReq())) {

			if (!ObjectUtils.isEmpty(crimHistDto.getIdRecCheckPerson())) {
				/*
				 ** First Call to nameDao to retrieve Person information
				 */
				empNameDtoPerson = nameDao.getNameByPersonId(crimHistDto.getIdRecCheckPerson());
				empNameDtoPerson.setEmpNameAction(dpsCrimHistResService.getNameWithSuffix(empNameDtoPerson));

			}

			if (!ObjectUtils.isEmpty(crimHistDto.getIdRecCheckReq())) {
				/*
				 ** Second Call to nameDao to retrieve Requestor information
				 */
				empNameDtoReq = nameDao.getNameByPersonId(crimHistDto.getIdRecCheckReq());
				empNameDtoReq.setEmpNameAction(dpsCrimHistResService.getNameWithSuffix(empNameDtoReq));
			}
		}

		dPSCrimHistResDto.setCrimHistDto(crimHistDto);
		dPSCrimHistResDto.setEmpNameDtoPerson(empNameDtoPerson);
		dPSCrimHistResDto.setEmpNameDtoReq(empNameDtoReq);
		dPSCrimHistResDto.setSpecialHandlingCaseDetailOutDto(specialHandlingCaseDetailOutDto);

		return fbiFingerprintHistoryPrefillData.returnPrefillData(dPSCrimHistResDto);
	}

}
