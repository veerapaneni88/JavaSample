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
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.DPSCrimHistResReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.dpscrimhistres.dto.DPSCrimHistResDto;
import us.tx.state.dfps.service.dpscrimhistres.service.DPSCrimHistResService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.DPSCrimHistResPrefillData;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.NameDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: service
 * implementation for form ccf12o00 Apr 30, 2018- 2:46:30 PM © 2017 Texas
 * Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 08/24/2023 thompswa artf251083 add getNameWithSuffix.
 */

@Service
@Transactional
public class DPSCrimHistResServiceImpl implements DPSCrimHistResService {

	@Autowired
	SpecialHandlingCaseDetailFetchDao specialHandlingCaseDetailFetchDao;

	@Autowired
	DPSCrimHistResPrefillData dPSCrimHistResPrefillData;

	@Autowired
	NameDao nameDao;

	@Autowired
	LookupDao lookupDao;

	private static final String BATCH_PROCESS = "Batch Process";
	private static final Long BATCH_PROCESS_ID = 999999996L;


	/**
	 * Service Name: CCFC34S Description: This service will populate the F/A
	 * Home Reverification Document. This form will be used by the F/A Home
	 * worker to reverify the F/A Home is meeting compliance standards.
	 * 
	 * @param DPSCrimHistResReq
	 * @return PreFillDataServiceDto
	 */

	@Override
	public PreFillDataServiceDto getCrimHistRes(DPSCrimHistResReq dPSCrimHistResReq) {

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
				 ** First Call to CSEC49D to retrieve Person information
				 */
				empNameDtoPerson = nameDao.getInfoByPersonId(crimHistDto.getIdRecCheckPerson(),
						crimHistDto.getDtRecCheckReq());
				empNameDtoPerson.setEmpNameAction(getNameWithSuffix( empNameDtoPerson ));

			}

			if (!ObjectUtils.isEmpty(crimHistDto.getIdRecCheckReq())) {
				/*
				 ** Second Call to CSEC49D to retrieve Requestor information
				 */
				if((crimHistDto.getIdRecCheckReq().equals(BATCH_PROCESS_ID))){
					empNameDtoReq.setEmpNameAction(BATCH_PROCESS);
				}else{
					empNameDtoReq = nameDao.getInfoByPersonId(crimHistDto.getIdRecCheckReq(),
							crimHistDto.getDtRecCheckReq());
					empNameDtoReq.setEmpNameAction(getNameWithSuffix( empNameDtoReq ));
				}

			}
		}

		dPSCrimHistResDto.setCrimHistDto(crimHistDto);
		dPSCrimHistResDto.setEmpNameDtoPerson(empNameDtoPerson);
		dPSCrimHistResDto.setEmpNameDtoReq(empNameDtoReq);
		dPSCrimHistResDto.setSpecialHandlingCaseDetailOutDto(specialHandlingCaseDetailOutDto);

		return dPSCrimHistResPrefillData.returnPrefillData(dPSCrimHistResDto);
	}


	/**
	 * return full name as last, first, middle ( middle initial false ) string(or "Unknown").
	 * Code changes to fix defect artf179500
	 */
	public String getNameWithSuffix( EmpNameDto empNameDto )
	{
		String empName = ServiceConstants.EMPTY_STRING;

		String suffix = null;

		if( !ObjectUtils.isEmpty( empNameDto.getCdNameSuffix())){
			suffix = lookupDao.decode( ServiceConstants.CSUFFIX, empNameDto.getCdNameSuffix()) ;
		}
		empName = TypeConvUtil.getNameWithSuffix(
				empNameDto.getNmNameFirst(),
				empNameDto.getNmNameMiddle(),
				empNameDto.getNmNameLast(),
				suffix,
				false);
		/* End of code changes to fix defect artf179500 */

		return empName;
	}/* end getNameWithSuffix */

}
