package us.tx.state.dfps.service.lonnotinv.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.exception.FormsException;
import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.LONNotInvReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.LONNotInvPrefillData;
import us.tx.state.dfps.service.lonnotinv.dto.LONNotInvDto;
import us.tx.state.dfps.service.lonnotinv.service.LONNotInvService;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV87S
 * Letter of Notification - Not Investigated Mar 21, 2018- 3:33:13 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class LONNotInvServiceImpl implements LONNotInvService {

	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private NotifToLawEnforcementDao notifToLawEnforcementDao;

	@Autowired
	private LONNotInvPrefillData lONNotInvPrefillData;
	
	@Autowired
	MessageSource messageSource;

	public LONNotInvServiceImpl() {
		super();
	}

	/**
	 * Method Name: getLetter Method Description: Letter of Notification - Not
	 * Investigated - populating the letter
	 * 
	 * @param lONNotInvReq
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getLetter(LONNotInvReq lONNotInvReq) {

		Long ulIdPerson = 0L;
		List<MultiAddressDto> multiAddressDtoList = null;
		EmployeePersPhNameDto employeePersPhNameDto = null;

		LONNotInvDto lONNotInvDto = new LONNotInvDto();

		/* DAM call to retrieve letterhead information */

		CodesTablesDto codesTablesDto = populateLetterDao
				.getPersonInfoByCode(ServiceConstants.TITLE, ServiceConstants.NAME).get(0);

		// Call CCMN19D is used to retrieves primary worker for stage
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(lONNotInvReq.getIdStage(),
				ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);

		ulIdPerson = stagePersonDto.getIdTodoPersWorker();

		if (!ObjectUtils.isEmpty(ulIdPerson)) {
			/* retrieves worker info CallCSEC01D */
			employeePersPhNameDto = employeeDao.searchPersonPhoneName(ulIdPerson);
		}

		/*
		 * DAM retrieves date, time, and address facility inv Detail info
		 * CallCSES39D
		 */
		FacilInvDtlDto facilInvDtlDto = notifToLawEnforcementDao.getFacilityInvDtlbyId(lONNotInvReq.getIdStage());

		facilInvDtlDto.setDtFacilInvstBegun(DateUtils.getCurrentDate());

		/*
		 ** retrieves principal and reporter info CSEC18D
		 */

		List<CaseInfoDto> caseInfoDtoList = new ArrayList<CaseInfoDto>();
		caseInfoDtoList = populateLetterDao.getReporterInfoById(lONNotInvReq.getIdStage());

		for (CaseInfoDto caseInfoDto : caseInfoDtoList) {
			if (!ObjectUtils.isEmpty(caseInfoDto.getIdPerson())
					&& caseInfoDto.getIdPerson().equals(lONNotInvReq.getIdPerson())) {
				caseInfoDtoList.set(0, caseInfoDto);
				break;
			}
		}

		/*
		 ** Do not print report if reporter address is missing
		 */

		if (ObjectUtils.isEmpty(caseInfoDtoList.get(0).getAddrPersAddrStLn1())
				&& ObjectUtils.isEmpty(caseInfoDtoList.get(0).getAddrPersAddrStLn2())) {
			throw new FormsException(messageSource.getMessage("getlonnotinv.noreporteraddress", null, Locale.US));
		}

		/*
		 ** retrieves stage and caps_case table CSEC02D
		 */
		GenericCaseInfoDto genCaseInfoDto = disasterPlanDao.getGenericCaseInfo(lONNotInvReq.getIdStage());

		if (!ObjectUtils.isEmpty(genCaseInfoDto.getIdCase())) {
			// CallCLSCGCD This dam will retrieve multiple MHMR Facility
			// addresses.
			multiAddressDtoList = notifToLawEnforcementDao.getMultiAddress(lONNotInvReq.getIdStage(),
					genCaseInfoDto.getIdCase());
		}

		lONNotInvDto.setCaseInfoDto(caseInfoDtoList.get(0));
		lONNotInvDto.setCodesTablesDto(codesTablesDto);
		lONNotInvDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		lONNotInvDto.setFacilInvDtlDto(facilInvDtlDto);
		lONNotInvDto.setGenCaseInfoDto(genCaseInfoDto);
		lONNotInvDto.setMultiAddressDtoList(multiAddressDtoList);
		lONNotInvDto.setStagePersonDto(stagePersonDto);

		return lONNotInvPrefillData.returnPrefillData(lONNotInvDto);
	}

}
