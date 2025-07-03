package us.tx.state.dfps.service.forms.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.notiftolawenforcement.dto.PriorStageDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.DateHelper;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstSctnTaskValueDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstValueDto;
import us.tx.state.dfps.service.admin.dto.RiskAssessmentFactorDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.cpsinvstsummary.dto.CpsInvstSummaryDto;
import us.tx.state.dfps.service.cpsinvstsummary.dto.RiskAssessmentInfoDto;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.intake.dto.IncomingDetailDto;
import us.tx.state.dfps.service.investigation.dto.CpsInvstDetailDto;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.stageutility.dao.StageUtilityDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Creates
 * prefill string to populate data on form Mar 29, 2018- 10:42:13 AM © 2017
 * Texas Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 05/06/2020 thompswa artf147748 : CPI June 2020 Project - adjustment for removal checklist data model change. Pre-release fixes artf152750
 */
@Component
public class CpsInvstSummaryPrefillData extends DocumentServiceUtil {

	@Autowired
	PersonUtil personUtil;

	@Autowired
	EventDao eventDao;

	@Autowired
	CaseUtils caseUtils;

	@Autowired
	StageUtilityDao stageUtilityDao;

	@Autowired
	private CodesDao codesDao;

	private static final String RMVL_CHCKLST_SCTNTASK_RSPNS_TEXT = "Date Completed";
	private static final String HOUR = "HOUR";
	private static final String DAY = "DAY";
	private static final String WEEK = "WEEK";
	private static final String RMVL = "RMVL";
	private static final String PLCMNT = "PLCMNT";
	private static final String COURT = "COURT";
	private static final String OTHR = "OTHR";
	private static final String DUE_DATE = "- DUE DATE:";
	private static final Logger logger = Logger.getLogger(CpsInvstSummaryPrefillData.class);

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		CpsInvstSummaryDto prefillDto = (CpsInvstSummaryDto) parentDtoobj;
		
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonGroupList = new ArrayList<BookmarkDto>(); // Non-group bookmarks
		
		// Initializing null DTOs
		if (ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
			prefillDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getAllegationWithVicList())) {
			prefillDto.setAllegationWithVicList(new ArrayList<AllegationWithVicDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getRiskAssessmentInfoDto())) {
			prefillDto.setRiskAssessmentInfoDto(new RiskAssessmentInfoDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getRiskAssessmentFactorDto())) {
			prefillDto.setRiskAssessmentFactorDto(new RiskAssessmentFactorDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getCaseInfoCollateralList())) {
			prefillDto.setCaseInfoCollateralList(new ArrayList<CaseInfoDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getCaseInfoPrincipalList())) {
			prefillDto.setCaseInfoPrincipalList(new ArrayList<CaseInfoDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getCpsInvstDetail())) {
			prefillDto.setCpsInvstDetail(new CpsInvstDetailDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getEmpSupvDto())) {
			prefillDto.setEmpSupvDto(new EmployeePersPhNameDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getEmpWorkerDto())) {
			prefillDto.setEmpWorkerDto(new EmployeePersPhNameDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getIncomingDetail())) {
			prefillDto.setIncomingDetail(new IncomingDetailDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getPriorStageDto())) {
			prefillDto.setPriorStageDto(new PriorStageDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getStagePersonDto())) {
			prefillDto.setStagePersonDto(new StagePersonDto());
		}

		// parent group cfzco00
		if (StringUtils.isNotBlank(prefillDto.getEmpWorkerDto().getCdNameSuffix())) {
			FormDataGroupDto commaFormGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(commaFormGroup);
		}

		for (CaseInfoDto principalDto : prefillDto.getCaseInfoPrincipalList()) {
			// parent group cfiv1001
			if (ServiceConstants.VIEW.equals(principalDto.getIndPersCancelHist())) {
				FormDataGroupDto victimGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_VICTIM,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> victimGroupDtoList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> bookmarkVictimList = new ArrayList<BookmarkDto>();
				if (!TypeConvUtil.isNullOrEmpty(principalDto.getIndPersonDobApprox()) && !ServiceConstants.N.equals(principalDto.getIndPersonDobApprox())) {
					BookmarkDto bookmarkVictimDobApprox = createBookmark(BookmarkConstants.VICTIM_DOB_APPROX,
							principalDto.getIndPersonDobApprox());
					bookmarkVictimList.add(bookmarkVictimDobApprox);
				}
				BookmarkDto bookmarkVictimSex = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_SEX,
						principalDto.getCdPersonSex(), CodesConstant.CSEX);
				bookmarkVictimList.add(bookmarkVictimSex);
				BookmarkDto bookmarkVictimDob = createBookmark(BookmarkConstants.VICTIM_DOB,
						DateUtils.stringDt(principalDto.getDtPersonBirth()));
				bookmarkVictimList.add(bookmarkVictimDob);
				BookmarkDto bookmarkVictimDod = createBookmark(BookmarkConstants.VICTIM_DOD,
						DateUtils.stringDt(principalDto.getDtPersonDeath()));
				bookmarkVictimList.add(bookmarkVictimDod);
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.ADDR_ZIP,
						principalDto.getAddrPersonAddrZip());
				bookmarkVictimList.add(bookmarkAddrZip);
				BookmarkDto bookmarkVictimAge = createBookmark(BookmarkConstants.VICTIM_AGE,
						principalDto.getNbrPersonAge());
				bookmarkVictimList.add(bookmarkVictimAge);
				BookmarkDto bookmarkPhoneNumber = createBookmark(BookmarkConstants.PHONE_NUMBER,
						TypeConvUtil.formatPhone(principalDto.getNbrPersonPhone()));
				bookmarkVictimList.add(bookmarkPhoneNumber);
				BookmarkDto bookmarkPhoneNumExt = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
						principalDto.getNbrPersonPhoneExtension());
				bookmarkVictimList.add(bookmarkPhoneNumExt);
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.ADDR_CITY,
						principalDto.getAddrPersonAddrCity());
				bookmarkVictimList.add(bookmarkAddrCity);
				BookmarkDto bookmarkAddrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
						principalDto.getAddrPersonAddrAttn());
				bookmarkVictimList.add(bookmarkAddrAttn);
				BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.ADDR_LN_1,
						principalDto.getAddrPersAddrStLn1());
				bookmarkVictimList.add(bookmarkAddrLn1);
				BookmarkDto bookmarkAddrLn2 = createBookmark(BookmarkConstants.ADDR_LN_2,
						principalDto.getAddrPersAddrStLn2());
				bookmarkVictimList.add(bookmarkAddrLn2);
				BookmarkDto bookmarkAddrCounty = createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY,
						principalDto.getCdPersonAddrCounty(), CodesConstant.CCOUNT);
				bookmarkVictimList.add(bookmarkAddrCounty);
				BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.ADDR_STATE,
						principalDto.getCdPersonAddrState());
				bookmarkVictimList.add(bookmarkAddrState);
				BookmarkDto bookmarkVictimNameSuffix = createBookmarkWithCodesTable(
						BookmarkConstants.VICTIM_NAME_SUFFIX, principalDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				bookmarkVictimList.add(bookmarkVictimNameSuffix);
				BookmarkDto bookmarkAddrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
						principalDto.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
				bookmarkVictimList.add(bookmarkAddrType);
				BookmarkDto bookmarkVictimRsn = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_RSN,
						principalDto.getCdPersonDeath(), CodesConstant.CRSNFDTH);
				bookmarkVictimList.add(bookmarkVictimRsn);
				BookmarkDto bookmarkVictimEthnicity = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_ETHNCTY,
						principalDto.getCdPersonEthnicGroup(), CodesConstant.CETHNIC);
				bookmarkVictimList.add(bookmarkVictimEthnicity);
				BookmarkDto bookmarkVictimLang = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_LANG,
						principalDto.getCdPersonLanguage(), CodesConstant.CLANG);
				bookmarkVictimList.add(bookmarkVictimLang);
				BookmarkDto bookmarkVictimMarital = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_MARITAL,
						principalDto.getCdPersonMaritalStatus(), CodesConstant.CMARSTAT);
				bookmarkVictimList.add(bookmarkVictimMarital);
				BookmarkDto bookmarkPhoneType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
						principalDto.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
				bookmarkVictimList.add(bookmarkPhoneType);
				BookmarkDto bookmarkVictimRelationship = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_RELTNSP,
						principalDto.getCdStagePersRelInt(), CodesConstant.CRELVICT);
				bookmarkVictimList.add(bookmarkVictimRelationship);
				BookmarkDto bookmarkVictimRole = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_ROLE,
						principalDto.getCdStagePersRole(), CodesConstant.CINVROLE);
				bookmarkVictimList.add(bookmarkVictimRole);
				BookmarkDto bookmarkVictimSsn = createBookmark(BookmarkConstants.VICTIM_SSN,
						principalDto.getPersonIdNumber());
				bookmarkVictimList.add(bookmarkVictimSsn);

				BookmarkDto bookmarkVictimName = createBookmark(BookmarkConstants.VICTIM_NAME, getFullName(
						principalDto.getNmNameLast(), principalDto.getNmNameFirst(), principalDto.getNmNameMiddle()));
				bookmarkVictimList.add(bookmarkVictimName);

				BookmarkDto bookmarkAddrNotes = createBookmark(BookmarkConstants.ADDR_NOTES,
						principalDto.getPersAddrCmnts());
				bookmarkVictimList.add(bookmarkAddrNotes);
				BookmarkDto bookmarkPhoneNotes = createBookmark(BookmarkConstants.PHONE_NOTES,
						principalDto.getTxtPersonPhoneComments());
				bookmarkVictimList.add(bookmarkPhoneNotes);
				BookmarkDto bookmarkVictimNotes = createBookmark(BookmarkConstants.VICTIM_NOTES,
						principalDto.getStagePersNotes());
				bookmarkVictimList.add(bookmarkVictimNotes);

				victimGroupDto.setBookmarkDtoList(bookmarkVictimList);
				victimGroupDto.setFormDataGroupList(victimGroupDtoList);
				formDataGroupList.add(victimGroupDto);
			}

			// parent group cfiv1002
			else if (ServiceConstants.PHONE.equals(principalDto.getIndPersCancelHist())) {
				FormDataGroupDto allegPerpGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEG_PERP,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> allegPerpGroupList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> bookmarkAllegPerpList = new ArrayList<BookmarkDto>();
				if (!principalDto.getIndPersonDobApprox().equals(ServiceConstants.N)) {
					BookmarkDto bookmarkApDobApprox = createBookmark(BookmarkConstants.AP_DOB_APPROX,
							principalDto.getIndPersonDobApprox());
					bookmarkAllegPerpList.add(bookmarkApDobApprox);
				}
				BookmarkDto bookmarkApSex = createBookmarkWithCodesTable(BookmarkConstants.AP_SEX,
						principalDto.getCdPersonSex(), CodesConstant.CSEX);
				bookmarkAllegPerpList.add(bookmarkApSex);
				BookmarkDto bookmarkApDob = createBookmark(BookmarkConstants.AP_DOB,
						DateUtils.stringDt(principalDto.getDtPersonBirth()));
				bookmarkAllegPerpList.add(bookmarkApDob);
				BookmarkDto bookmarkApDod = createBookmark(BookmarkConstants.AP_DOD,
						DateUtils.stringDt(principalDto.getDtPersonDeath()));
				bookmarkAllegPerpList.add(bookmarkApDod);
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.ADDR_ZIP,
						principalDto.getAddrPersonAddrZip());
				bookmarkAllegPerpList.add(bookmarkAddrZip);
				BookmarkDto bookmarkApAge = createBookmark(BookmarkConstants.AP_AGE, principalDto.getNbrPersonAge());
				bookmarkAllegPerpList.add(bookmarkApAge);
				BookmarkDto bookmarkPhoneNumber = createBookmark(BookmarkConstants.PHONE_NUMBER,
						TypeConvUtil.formatPhone(principalDto.getNbrPersonPhone()));
				bookmarkAllegPerpList.add(bookmarkPhoneNumber);
				BookmarkDto bookmarkPhoneNumExt = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
						principalDto.getNbrPersonPhoneExtension());
				bookmarkAllegPerpList.add(bookmarkPhoneNumExt);
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.ADDR_CITY,
						principalDto.getAddrPersonAddrCity());
				bookmarkAllegPerpList.add(bookmarkAddrCity);
				BookmarkDto bookmarkAddrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
						principalDto.getAddrPersonAddrAttn());
				bookmarkAllegPerpList.add(bookmarkAddrAttn);
				BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.ADDR_LN_1,
						principalDto.getAddrPersAddrStLn1());
				bookmarkAllegPerpList.add(bookmarkAddrLn1);
				BookmarkDto bookmarkAddrLn2 = createBookmark(BookmarkConstants.ADDR_LN_2,
						principalDto.getAddrPersAddrStLn2());
				bookmarkAllegPerpList.add(bookmarkAddrLn2);
				BookmarkDto bookmarkAddrCounty = createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY,
						principalDto.getCdPersonAddrCounty(), CodesConstant.CCOUNT);
				bookmarkAllegPerpList.add(bookmarkAddrCounty);
				BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.ADDR_STATE,
						principalDto.getCdPersonAddrState());
				bookmarkAllegPerpList.add(bookmarkAddrState);
				BookmarkDto bookmarkApNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.AP_NAME_SUFFIX,
						principalDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				bookmarkAllegPerpList.add(bookmarkApNameSuffix);
				BookmarkDto bookmarkAddrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
						principalDto.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
				bookmarkAllegPerpList.add(bookmarkAddrType);
				BookmarkDto bookmarkApRsn = createBookmarkWithCodesTable(BookmarkConstants.AP_RSN,
						principalDto.getCdPersonDeath(), CodesConstant.CRSNFDTH);
				bookmarkAllegPerpList.add(bookmarkApRsn);
				BookmarkDto bookmarkApEthnicity = createBookmarkWithCodesTable(BookmarkConstants.AP_ETHNCTY,
						principalDto.getCdPersonEthnicGroup(), CodesConstant.CETHNIC);
				bookmarkAllegPerpList.add(bookmarkApEthnicity);
				BookmarkDto bookmarkApLang = createBookmarkWithCodesTable(BookmarkConstants.AP_LANG,
						principalDto.getCdPersonLanguage(), CodesConstant.CLANG);
				bookmarkAllegPerpList.add(bookmarkApLang);
				BookmarkDto bookmarkApMarital = createBookmarkWithCodesTable(BookmarkConstants.AP_MARITAL,
						principalDto.getCdPersonMaritalStatus(), CodesConstant.CMARSTAT);
				bookmarkAllegPerpList.add(bookmarkApMarital);
				BookmarkDto bookmarkPhoneType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
						principalDto.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
				bookmarkAllegPerpList.add(bookmarkPhoneType);
				BookmarkDto bookmarkApRelationship = createBookmarkWithCodesTable(BookmarkConstants.AP_RELTNSP,
						principalDto.getCdStagePersRelInt(), CodesConstant.CRELVICT);
				bookmarkAllegPerpList.add(bookmarkApRelationship);
				BookmarkDto bookmarkApRole = createBookmarkWithCodesTable(BookmarkConstants.AP_ROLE,
						principalDto.getCdStagePersRole(), CodesConstant.CINVROLE);
				bookmarkAllegPerpList.add(bookmarkApRole);
				BookmarkDto bookmarkApSsn = createBookmark(BookmarkConstants.AP_SSN, principalDto.getPersonIdNumber());
				bookmarkAllegPerpList.add(bookmarkApSsn);

				BookmarkDto bookmarkApName = createBookmark(BookmarkConstants.AP_NAME, getFullName(
						principalDto.getNmNameLast(), principalDto.getNmNameFirst(), principalDto.getNmNameMiddle()));
				bookmarkAllegPerpList.add(bookmarkApName);

				BookmarkDto bookmarkAddrNotes = createBookmark(BookmarkConstants.ADDR_NOTES,
						principalDto.getPersAddrCmnts());
				bookmarkAllegPerpList.add(bookmarkAddrNotes);
				BookmarkDto bookmarkPhoneNotes = createBookmark(BookmarkConstants.PHONE_NOTES,
						principalDto.getTxtPersonPhoneComments());
				bookmarkAllegPerpList.add(bookmarkPhoneNotes);
				BookmarkDto bookmarkApNotes = createBookmark(BookmarkConstants.AP_NOTES,
						principalDto.getStagePersNotes());
				bookmarkAllegPerpList.add(bookmarkApNotes);

				// sub group cfzco00
				if (StringUtils.isNotBlank(principalDto.getCdNameSuffix())) {
					FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_B,
							FormGroupsConstants.TMPLAT_ALLEG_PERP);
					allegPerpGroupList.add(commaGroupDto);
				}

				allegPerpGroupDto.setBookmarkDtoList(bookmarkAllegPerpList);
				allegPerpGroupDto.setFormDataGroupList(allegPerpGroupList);
				formDataGroupList.add(allegPerpGroupDto);
			}

			// parent group cfiv1003
			else if (ServiceConstants.ON_CALL_VIEW.equals(principalDto.getIndPersCancelHist())) {
				FormDataGroupDto princOtherGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRINC_OTHER,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> princOtherGroupList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> bookmarkPrincOtherList = new ArrayList<BookmarkDto>();
				if (!principalDto.getIndPersonDobApprox().equals(ServiceConstants.N)) {
					BookmarkDto bookmarkPrincDobApprox = createBookmark(BookmarkConstants.PRINC_DOB_APPROX,
							principalDto.getIndPersonDobApprox());
					bookmarkPrincOtherList.add(bookmarkPrincDobApprox);
				}
				BookmarkDto bookmarkPrincSex = createBookmarkWithCodesTable(BookmarkConstants.PRINC_SEX,
						principalDto.getCdPersonSex(), CodesConstant.CSEX);
				bookmarkPrincOtherList.add(bookmarkPrincSex);
				BookmarkDto bookmarkPrincDob = createBookmark(BookmarkConstants.PRINC_DOB,
						DateUtils.stringDt(principalDto.getDtPersonBirth()));
				bookmarkPrincOtherList.add(bookmarkPrincDob);
				BookmarkDto bookmarkPrincDod = createBookmark(BookmarkConstants.PRINC_DOD,
						DateUtils.stringDt(principalDto.getDtPersonDeath()));
				bookmarkPrincOtherList.add(bookmarkPrincDod);
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.ADDR_ZIP,
						principalDto.getAddrPersonAddrZip());
				bookmarkPrincOtherList.add(bookmarkAddrZip);
				BookmarkDto bookmarkPrincAge = createBookmark(BookmarkConstants.PRINC_AGE,
						principalDto.getNbrPersonAge());
				bookmarkPrincOtherList.add(bookmarkPrincAge);
				BookmarkDto bookmarkPhoneNumber = createBookmark(BookmarkConstants.PHONE_NUMBER,
						TypeConvUtil.formatPhone(principalDto.getNbrPersonPhone()));
				bookmarkPrincOtherList.add(bookmarkPhoneNumber);
				BookmarkDto bookmarkPhoneNumExt = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
						principalDto.getNbrPersonPhoneExtension());
				bookmarkPrincOtherList.add(bookmarkPhoneNumExt);
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.ADDR_CITY,
						principalDto.getAddrPersonAddrCity());
				bookmarkPrincOtherList.add(bookmarkAddrCity);
				BookmarkDto bookmarkAddrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
						principalDto.getAddrPersonAddrAttn());
				bookmarkPrincOtherList.add(bookmarkAddrAttn);
				BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.ADDR_LN_1,
						principalDto.getAddrPersAddrStLn1());
				bookmarkPrincOtherList.add(bookmarkAddrLn1);
				BookmarkDto bookmarkAddrLn2 = createBookmark(BookmarkConstants.ADDR_LN_2,
						principalDto.getAddrPersAddrStLn2());
				bookmarkPrincOtherList.add(bookmarkAddrLn2);
				BookmarkDto bookmarkAddrCounty = createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY,
						principalDto.getCdPersonAddrCounty(), CodesConstant.CCOUNT);
				bookmarkPrincOtherList.add(bookmarkAddrCounty);
				BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.ADDR_STATE,
						principalDto.getCdPersonAddrState());
				bookmarkPrincOtherList.add(bookmarkAddrState);
				BookmarkDto bookmarkPrincNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.PRINC_NAME_SUFFIX,
						principalDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				bookmarkPrincOtherList.add(bookmarkPrincNameSuffix);
				BookmarkDto bookmarkAddrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
						principalDto.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
				bookmarkPrincOtherList.add(bookmarkAddrType);
				BookmarkDto bookmarkPrincRsn = createBookmarkWithCodesTable(BookmarkConstants.PRINC_RSN,
						principalDto.getCdPersonDeath(), CodesConstant.CRSNFDTH);
				bookmarkPrincOtherList.add(bookmarkPrincRsn);
				BookmarkDto bookmarkPrincEthnicity = createBookmarkWithCodesTable(BookmarkConstants.PRINC_ETHNCTY,
						principalDto.getCdPersonEthnicGroup(), CodesConstant.CETHNIC);
				bookmarkPrincOtherList.add(bookmarkPrincEthnicity);
				BookmarkDto bookmarkPrincLang = createBookmarkWithCodesTable(BookmarkConstants.PRINC_LANG,
						principalDto.getCdPersonLanguage(), CodesConstant.CLANG);
				bookmarkPrincOtherList.add(bookmarkPrincLang);
				BookmarkDto bookmarkPrincMarital = createBookmarkWithCodesTable(BookmarkConstants.PRINC_MARITAL,
						principalDto.getCdPersonMaritalStatus(), CodesConstant.CMARSTAT);
				bookmarkPrincOtherList.add(bookmarkPrincMarital);
				BookmarkDto bookmarkPhoneType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
						principalDto.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
				bookmarkPrincOtherList.add(bookmarkPhoneType);
				BookmarkDto bookmarkPrincRelationship = createBookmarkWithCodesTable(BookmarkConstants.PRINC_RELTNSP,
						principalDto.getCdStagePersRelInt(), CodesConstant.CRELVICT);
				bookmarkPrincOtherList.add(bookmarkPrincRelationship);
				BookmarkDto bookmarkPrincRole = createBookmarkWithCodesTable(BookmarkConstants.PRINC_ROLE,
						principalDto.getCdStagePersRole(), CodesConstant.CINVROLE);
				bookmarkPrincOtherList.add(bookmarkPrincRole);
				BookmarkDto bookmarkPrincSsn = createBookmark(BookmarkConstants.PRINC_SSN,
						principalDto.getPersonIdNumber());
				bookmarkPrincOtherList.add(bookmarkPrincSsn);

				BookmarkDto bookmarkPrincName = createBookmark(BookmarkConstants.PRINC_NAME, getFullName(
						principalDto.getNmNameLast(), principalDto.getNmNameFirst(), principalDto.getNmNameMiddle()));
				bookmarkPrincOtherList.add(bookmarkPrincName);

				BookmarkDto bookmarkAddrNotes = createBookmark(BookmarkConstants.ADDR_NOTES,
						principalDto.getPersAddrCmnts());
				bookmarkPrincOtherList.add(bookmarkAddrNotes);
				BookmarkDto bookmarkPhoneNotes = createBookmark(BookmarkConstants.PHONE_NOTES,
						principalDto.getTxtPersonPhoneComments());
				bookmarkPrincOtherList.add(bookmarkPhoneNotes);
				BookmarkDto bookmarkPrincNotes = createBookmark(BookmarkConstants.PRINC_NOTES,
						principalDto.getStagePersNotes());
				bookmarkPrincOtherList.add(bookmarkPrincNotes);

				princOtherGroupDto.setBookmarkDtoList(bookmarkPrincOtherList);
				princOtherGroupDto.setFormDataGroupList(princOtherGroupList);
				formDataGroupList.add(princOtherGroupDto);
			}

			// parent group cfiv1004
			if (null != principalDto.getIndStagePersReporter() && ServiceConstants.Y.equals(principalDto.getIndStagePersReporter())) {
				FormDataGroupDto reporterGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REPORTER_1,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> reporterGroupDtoList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> bookmarkReporterList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.ADDR_ZIP,
						principalDto.getAddrPersonAddrZip());
				bookmarkReporterList.add(bookmarkAddrZip);
				BookmarkDto bookmarkPhoneNumber = createBookmark(BookmarkConstants.PHONE_NUMBER,
						TypeConvUtil.formatPhone(principalDto.getNbrPersonPhone()));
				bookmarkReporterList.add(bookmarkPhoneNumber);
				BookmarkDto bookmarkPhoneNumExt = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
						principalDto.getNbrPersonPhoneExtension());
				bookmarkReporterList.add(bookmarkPhoneNumExt);
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.ADDR_CITY,
						principalDto.getAddrPersonAddrCity());
				bookmarkReporterList.add(bookmarkAddrCity);
				BookmarkDto bookmarkAddrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
						principalDto.getAddrPersonAddrAttn());
				bookmarkReporterList.add(bookmarkAddrAttn);
				BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.ADDR_LN_1,
						principalDto.getAddrPersAddrStLn1());
				bookmarkReporterList.add(bookmarkAddrLn1);
				BookmarkDto bookmarkAddrLn2 = createBookmark(BookmarkConstants.ADDR_LN_2,
						principalDto.getAddrPersAddrStLn2());
				bookmarkReporterList.add(bookmarkAddrLn2);
				BookmarkDto bookmarkAddrCounty = createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY,
						principalDto.getCdPersonAddrCounty(), CodesConstant.CCOUNT);
				bookmarkReporterList.add(bookmarkAddrCounty);
				BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.ADDR_STATE,
						principalDto.getCdPersonAddrState());
				bookmarkReporterList.add(bookmarkAddrState);
				BookmarkDto bookmarkRptNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.RPT_NAME_SUFFIX,
						principalDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				bookmarkReporterList.add(bookmarkRptNameSuffix);
				BookmarkDto bookmarkAddrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
						principalDto.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
				bookmarkReporterList.add(bookmarkAddrType);
				BookmarkDto bookmarkPhoneType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
						principalDto.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
				bookmarkReporterList.add(bookmarkPhoneType);
				BookmarkDto bookmarkRptRelationship = createBookmarkWithCodesTable(BookmarkConstants.RPT_RELTNSP,
						principalDto.getCdStagePersRelInt(), CodesConstant.CRELVICT);
				bookmarkReporterList.add(bookmarkRptRelationship);

				BookmarkDto bookmarkRptName = createBookmark(BookmarkConstants.RPT_NAME, getFullName(
						principalDto.getNmNameLast(), principalDto.getNmNameFirst(), principalDto.getNmNameMiddle()));
				bookmarkReporterList.add(bookmarkRptName);

				BookmarkDto bookmarkAddrNotes = createBookmark(BookmarkConstants.ADDR_NOTES,
						principalDto.getPersAddrCmnts());
				bookmarkReporterList.add(bookmarkAddrNotes);
				BookmarkDto bookmarkPhoneNotes = createBookmark(BookmarkConstants.PHONE_NOTES,
						principalDto.getTxtPersonPhoneComments());
				bookmarkReporterList.add(bookmarkPhoneNotes);
				BookmarkDto bookmarkRptNotes = createBookmark(BookmarkConstants.RPT_NOTES,
						principalDto.getStagePersNotes());
				bookmarkReporterList.add(bookmarkRptNotes);

				reporterGroupDto.setBookmarkDtoList(bookmarkReporterList);
				reporterGroupDto.setFormDataGroupList(reporterGroupDtoList);
				formDataGroupList.add(reporterGroupDto);
			}
		}

		// parent group cfiv1006
		for (AllegationWithVicDto allegationDto : prefillDto.getAllegationWithVicList()) {
			FormDataGroupDto allegationGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> allegationGroupDtoList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkAllegationList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkAllegDtlDisp = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_DISP,
					allegationDto.getaCdAllegDisposition(), CodesConstant.CCIVALDS);
			bookmarkAllegationList.add(bookmarkAllegDtlDisp);
			BookmarkDto bookmarkAllegDtlAlleg = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_ALLEG,
					allegationDto.getaCdAllegType(), CodesConstant.CABALTYP);
			bookmarkAllegationList.add(bookmarkAllegDtlAlleg);
			BookmarkDto bookmarkAllegDtlFatality = createBookmark(BookmarkConstants.ALLEG_DTL_FATALITY,
					allegationDto.getaIndFatality());
			bookmarkAllegationList.add(bookmarkAllegDtlFatality);
			BookmarkDto bookmarkAllegDtlAp = createBookmark(BookmarkConstants.ALLEG_DTL_AP,
					getFullName(allegationDto.getcNmPersonLast(), allegationDto.getcNmPersonFirst(),
							allegationDto.getcNmPersonMiddle()));

			bookmarkAllegationList.add(bookmarkAllegDtlAp);
			BookmarkDto bookmarkAllegDtlVictim = createBookmark(BookmarkConstants.ALLEG_DTL_VICTIM,
					getFullName(allegationDto.getbNmPersonLast(), allegationDto.getbNmPersonFirst(),
							allegationDto.getbNmPersonMiddle()));
			bookmarkAllegationList.add(bookmarkAllegDtlVictim);
			// sub group cfiv1013
			if (!ObjectUtils.isEmpty(allegationDto.getbDtPersonDeath())) {
				FormDataGroupDto algDodGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_ALG_CHILD_FATALITY_WITH_DOD, FormGroupsConstants.TMPLAT_ALLEGATION);
				List<BookmarkDto> bookmarkAlgDodList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAlgChildFatality = createBookmark(BookmarkConstants.ALG_CHILD_FATALITY,
						allegationDto.getaIndFatality());
				bookmarkAlgDodList.add(bookmarkAlgChildFatality);

				algDodGroupDto.setBookmarkDtoList(bookmarkAlgDodList);
				allegationGroupDtoList.add(algDodGroupDto);
			}

			// sub group cfiv1014
			else {
				FormDataGroupDto algNoDodGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_ALG_CHILD_FATALITY_WITHOUT_DOD,
						FormGroupsConstants.TMPLAT_ALLEGATION);
				allegationGroupDtoList.add(algNoDodGroupDto);
			}

			allegationGroupDto.setBookmarkDtoList(bookmarkAllegationList);
			allegationGroupDto.setFormDataGroupList(allegationGroupDtoList);
			formDataGroupList.add(allegationGroupDto);
		}

		// parent group cfiv1008
		if (StringUtils.isNotBlank(prefillDto.getCpsInvstDetail().getCdVictimTaped())) {
			FormDataGroupDto invSecAGroupDto = createFormDataGroup(FormGroupsConstants.INVESTIGATION_SECTION_A,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkInvstSecAList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkTapedVictimInd = createBookmarkWithCodesTable(BookmarkConstants.TAPED_VICTIM_IND,
					prefillDto.getCpsInvstDetail().getIndVictimTaped(), CodesConstant.CBJNTCNT);
			bookmarkInvstSecAList.add(bookmarkTapedVictimInd);
			BookmarkDto bookmarkTapedVictimCd = createBookmarkWithCodesTable(BookmarkConstants.TAPED_VICTIM_CD,
					prefillDto.getCpsInvstDetail().getCdVictimTaped(), CodesConstant.CVICTPD);
			bookmarkInvstSecAList.add(bookmarkTapedVictimCd);
			BookmarkDto bookmarkTapedVictimTxt = createBookmark(BookmarkConstants.TAPED_VICTIM_TXT,
					prefillDto.getCpsInvstDetail().getVictimTaped());
			bookmarkInvstSecAList.add(bookmarkTapedVictimTxt);

			invSecAGroupDto.setBookmarkDtoList(bookmarkInvstSecAList);
			formDataGroupList.add(invSecAGroupDto);
		}

		// parent group cfiv1011
		if (StringUtils.isNotBlank(prefillDto.getCpsInvstDetail().getIndMeth())
				&& 0 > ServiceConstants.N.compareTo(prefillDto.getCpsInvstDetail().getIndMeth())) {
			FormDataGroupDto invMethGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_METH,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkMethList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkIndInvMeth = createBookmarkWithCodesTable(BookmarkConstants.IND_INV_METH,
					prefillDto.getCpsInvstDetail().getIndMeth(), CodesConstant.CINVACAN);
			bookmarkMethList.add(bookmarkIndInvMeth);

			invMethGroupDto.setBookmarkDtoList(bookmarkMethList);
			formDataGroupList.add(invMethGroupDto);
		}

		// parent group cfiv1009
		if (StringUtils.isNotBlank(prefillDto.getCpsInvstDetail().getIndCpsLeJntCntct())
				&& 0 > ServiceConstants.N.compareTo(prefillDto.getCpsInvstDetail().getIndCpsLeJntCntct())) {
			FormDataGroupDto invSecBGroupDto = createFormDataGroup(FormGroupsConstants.INVESTIGATION_SECTION_B,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkInvSecBList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkFirstCntLeCps = createBookmarkWithCodesTable(BookmarkConstants.FIRST_CNT_LE_CPS,
					prefillDto.getCpsInvstDetail().getIndCpsLeJntCntct(), CodesConstant.CBJNTCNT);
			bookmarkInvSecBList.add(bookmarkFirstCntLeCps);
			BookmarkDto bookmarkFirstCntLeCpsWhyNot = createBookmarkWithCodesTable(
					BookmarkConstants.FIRST_CNT_LE_CPS_WHY_NOT, prefillDto.getCpsInvstDetail().getCdReasonNoJntCntct(),
					CodesConstant.CBJNTCNT);
			bookmarkInvSecBList.add(bookmarkFirstCntLeCpsWhyNot);
			BookmarkDto bookmarkFirstCntLeCpsComments = createBookmark(BookmarkConstants.FIRST_CNT_LE_CPS_COMMENTS,
					prefillDto.getCpsInvstDetail().getReasonNoJntCntct());
			bookmarkInvSecBList.add(bookmarkFirstCntLeCpsComments);

			invSecBGroupDto.setBookmarkDtoList(bookmarkInvSecBList);
			formDataGroupList.add(invSecBGroupDto);
		}

		// parent group cfiv1012
		FormDataGroupDto invFtmGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_FTM,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkInvFtmList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkIndInvFtmOccurred = createBookmarkWithCodesTable(BookmarkConstants.IND_INV_FTM_OCCURRED,
				prefillDto.getCpsInvstDetail().getIndFtmOccurred(), CodesConstant.CINVACAN);
		bookmarkInvFtmList.add(bookmarkIndInvFtmOccurred);
		BookmarkDto bookmarkIndInvFtmOffered = createBookmarkWithCodesTable(BookmarkConstants.IND_INV_FTM_OFFERED,
				prefillDto.getCpsInvstDetail().getIndFtmOffered(), CodesConstant.CINVACAN);
		bookmarkInvFtmList.add(bookmarkIndInvFtmOffered);

		invFtmGroupDto.setBookmarkDtoList(bookmarkInvFtmList);
		formDataGroupList.add(invFtmGroupDto);

		// parent group cfiv1016
		if (!ObjectUtils.isEmpty(prefillDto.getRiskAssessmentFactorDto().getCdRiskAssmtRiskFind())) {
			FormDataGroupDto riskFindingGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RISKFINDING,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRiskFindingList = new ArrayList<BookmarkDto>();
			BookmarkDto bookamrkRiskFinding = createBookmarkWithCodesTable(BookmarkConstants.RISKFINDING,
					prefillDto.getRiskAssessmentFactorDto().getCdRiskAssmtRiskFind(), CodesConstant.CCRSKFND);
			bookmarkRiskFindingList.add(bookamrkRiskFinding);

			riskFindingGroupDto.setBookmarkDtoList(bookmarkRiskFindingList);
			formDataGroupList.add(riskFindingGroupDto);
		}
		// parent group cfzco00
		if (StringUtils.isNotBlank(prefillDto.getEmpSupvDto().getCdNameSuffix())) {
			FormDataGroupDto comma2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(comma2GroupDto);
		}

		for (CaseInfoDto collateralDto : prefillDto.getCaseInfoCollateralList()) {
			// parent group cfiv1005
			if (null != collateralDto.getIndStagePersReporter() && ServiceConstants.N.equals(collateralDto.getIndStagePersReporter())) {
				FormDataGroupDto collateralGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COLLATERAL,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> collateralGroupDtoList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> bookmarkCollateralList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkColSex = createBookmarkWithCodesTable(BookmarkConstants.COL_SEX,
						collateralDto.getCdPersonSex(), CodesConstant.CSEX);
				bookmarkCollateralList.add(bookmarkColSex);
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.ADDR_ZIP,
						collateralDto.getAddrPersonAddrZip());
				bookmarkCollateralList.add(bookmarkAddrZip);
				BookmarkDto bookmarkPhoneNumber = createBookmark(BookmarkConstants.PHONE_NUMBER,
						TypeConvUtil.formatPhone(collateralDto.getNbrPersonPhone()));
				bookmarkCollateralList.add(bookmarkPhoneNumber);
				BookmarkDto bookmarkPhoneNumExt = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
						collateralDto.getNbrPersonPhoneExtension());
				bookmarkCollateralList.add(bookmarkPhoneNumExt);
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.ADDR_CITY,
						collateralDto.getAddrPersonAddrCity());
				bookmarkCollateralList.add(bookmarkAddrCity);
				BookmarkDto bookmarkAddrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
						collateralDto.getAddrPersonAddrAttn());
				bookmarkCollateralList.add(bookmarkAddrAttn);
				BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.ADDR_LN_1,
						collateralDto.getAddrPersAddrStLn1());
				bookmarkCollateralList.add(bookmarkAddrLn1);
				BookmarkDto bookmarkAddrLn2 = createBookmark(BookmarkConstants.ADDR_LN_2,
						collateralDto.getAddrPersAddrStLn2());
				bookmarkCollateralList.add(bookmarkAddrLn2);
				BookmarkDto bookmarkAddrCounty = createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY,
						collateralDto.getCdPersonAddrCounty(), CodesConstant.CCOUNT);
				bookmarkCollateralList.add(bookmarkAddrCounty);
				BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.ADDR_STATE,
						collateralDto.getCdPersonAddrState());
				bookmarkCollateralList.add(bookmarkAddrState);
				BookmarkDto bookmarkColNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.COL_NAME_SUFFIX,
						collateralDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				bookmarkCollateralList.add(bookmarkColNameSuffix);
				BookmarkDto bookmarkAddrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
						collateralDto.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
				bookmarkCollateralList.add(bookmarkAddrType);
				BookmarkDto bookmarkPhoneType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
						collateralDto.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
				bookmarkCollateralList.add(bookmarkPhoneType);
				BookmarkDto bookmarkColRelationship = createBookmarkWithCodesTable(BookmarkConstants.COL_RELTNSP,
						collateralDto.getCdStagePersRelInt(), CodesConstant.CRELVICT);
				bookmarkCollateralList.add(bookmarkColRelationship);
				BookmarkDto bookmarkColName = createBookmark(BookmarkConstants.COL_NAME,
						getFullName(collateralDto.getNmNameLast(), collateralDto.getNmNameFirst(),
								collateralDto.getNmNameMiddle()));
				bookmarkCollateralList.add(bookmarkColName);

				BookmarkDto bookmarkAddrNotes = createBookmark(BookmarkConstants.ADDR_NOTES,
						collateralDto.getPersAddrCmnts());
				bookmarkCollateralList.add(bookmarkAddrNotes);
				BookmarkDto bookmarkPhoneNotes = createBookmark(BookmarkConstants.PHONE_NOTES,
						collateralDto.getTxtPersonPhoneComments());
				bookmarkCollateralList.add(bookmarkPhoneNotes);
				BookmarkDto bookmarkColNotes = createBookmark(BookmarkConstants.COL_NOTES,
						collateralDto.getStagePersNotes());
				bookmarkCollateralList.add(bookmarkColNotes);

				collateralGroupDto.setBookmarkDtoList(bookmarkCollateralList);
				collateralGroupDto.setFormDataGroupList(collateralGroupDtoList);
				formDataGroupList.add(collateralGroupDto);
			}

			// parent group cfiv1007
			else if (null != collateralDto.getIndStagePersReporter() && ServiceConstants.Y.equals(collateralDto.getIndStagePersReporter())) {
				FormDataGroupDto reporterGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REPORTER_2,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> reporterGroupDtoList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> bookmarkReporterList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.ADDR_ZIP,
						collateralDto.getAddrPersonAddrZip());
				bookmarkReporterList.add(bookmarkAddrZip);
				BookmarkDto bookmarkPhoneNumber = createBookmark(BookmarkConstants.PHONE_NUMBER,
						TypeConvUtil.formatPhone(collateralDto.getNbrPersonPhone()));
				bookmarkReporterList.add(bookmarkPhoneNumber);
				BookmarkDto bookmarkPhoneNumExt = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
						collateralDto.getNbrPersonPhoneExtension());
				bookmarkReporterList.add(bookmarkPhoneNumExt);
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.ADDR_CITY,
						collateralDto.getAddrPersonAddrCity());
				bookmarkReporterList.add(bookmarkAddrCity);
				BookmarkDto bookmarkAddrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
						collateralDto.getAddrPersonAddrAttn());
				bookmarkReporterList.add(bookmarkAddrAttn);
				BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.ADDR_LN_1,
						collateralDto.getAddrPersAddrStLn1());
				bookmarkReporterList.add(bookmarkAddrLn1);
				BookmarkDto bookmarkAddrLn2 = createBookmark(BookmarkConstants.ADDR_LN_2,
						collateralDto.getAddrPersAddrStLn2());
				bookmarkReporterList.add(bookmarkAddrLn2);
				BookmarkDto bookmarkAddrCounty = createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY,
						collateralDto.getCdPersonAddrCounty(), CodesConstant.CCOUNT);
				bookmarkReporterList.add(bookmarkAddrCounty);
				BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.ADDR_STATE,
						collateralDto.getCdPersonAddrState());
				bookmarkReporterList.add(bookmarkAddrState);
				BookmarkDto bookmarkRptNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.RPT_NAME_SUFFIX,
						collateralDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				bookmarkReporterList.add(bookmarkRptNameSuffix);
				BookmarkDto bookmarkAddrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
						collateralDto.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
				bookmarkReporterList.add(bookmarkAddrType);
				BookmarkDto bookmarkPhoneType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
						collateralDto.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
				bookmarkReporterList.add(bookmarkPhoneType);
				BookmarkDto bookmarkRptRelationship = createBookmarkWithCodesTable(BookmarkConstants.RPT_RELTNSP,
						collateralDto.getCdStagePersRelInt(), CodesConstant.CRELVICT);
				bookmarkReporterList.add(bookmarkRptRelationship);

				BookmarkDto bookmarkRptName = createBookmark(BookmarkConstants.RPT_NAME,
						getFullName(collateralDto.getNmNameLast(), collateralDto.getNmNameFirst(),
								collateralDto.getNmNameMiddle()));
				bookmarkReporterList.add(bookmarkRptName);

				BookmarkDto bookmarkAddrNotes = createBookmark(BookmarkConstants.ADDR_NOTES,
						collateralDto.getPersAddrCmnts());
				bookmarkReporterList.add(bookmarkAddrNotes);
				BookmarkDto bookmarkPhoneNotes = createBookmark(BookmarkConstants.PHONE_NOTES,
						collateralDto.getTxtPersonPhoneComments());
				bookmarkReporterList.add(bookmarkPhoneNotes);
				BookmarkDto bookmarkRptNotes = createBookmark(BookmarkConstants.RPT_NOTES,
						collateralDto.getStagePersNotes());
				bookmarkReporterList.add(bookmarkRptNotes);

				// sub group cfzco00
				if (StringUtils.isNotBlank(collateralDto.getCdNameSuffix())) {
					FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_F,
							FormGroupsConstants.TMPLAT_REPORTER);
					reporterGroupDtoList.add(commaGroupDto);
				}

				reporterGroupDto.setBookmarkDtoList(bookmarkReporterList);
				reporterGroupDto.setFormDataGroupList(reporterGroupDtoList);
				formDataGroupList.add(reporterGroupDto);
			}
		}

		// parent group cfiv1010
		if (StringUtils.isNotBlank(prefillDto.getIncomingDetail().getIndIncmgSuspMeth())) {
			FormDataGroupDto intakeMethGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_INTAKE_METH,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkIntakeMethList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkIndIncmgSuspMeth = createBookmarkWithCodesTable(BookmarkConstants.IND_INCMG_SUSP_METH,
					prefillDto.getIncomingDetail().getIndIncmgSuspMeth(), CodesConstant.CINVACAN);
			bookmarkIntakeMethList.add(bookmarkIndIncmgSuspMeth);

			intakeMethGroupDto.setBookmarkDtoList(bookmarkIntakeMethList);
			formDataGroupList.add(intakeMethGroupDto);
		}

		// parent group cfiv1015
		if (!ObjectUtils.isEmpty(prefillDto.getRiskAssessmentInfoDto().getCdFinalRiskLevel())) {
			FormDataGroupDto sdmRiskFinalGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SDMRISKFINAL,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkSdmRiskFinalList = new ArrayList<BookmarkDto>();

			/**QCR 62702
			 * Change of sub title depending on Date of Assessment completed
			 * Add "SDM" if the Date of Assessment completed before 9/1/2020
			 *
			 **/
			BookmarkDto bookmarkSDMSubTitle = null;
			Date dtSDMRemoval=codesDao.getAppRelDate(ServiceConstants.CRELDATE_SDM_REMOVAL_2020);
			if (!ObjectUtils.isEmpty(prefillDto.getRiskAssessmentInfoDto())) {
				if(!ObjectUtils.isEmpty(prefillDto.getRiskAssessmentInfoDto().getDtAssmtCompleted())
						&& prefillDto.getRiskAssessmentInfoDto().getDtAssmtCompleted().compareTo(dtSDMRemoval)<0){
					bookmarkSDMSubTitle = createBookmark(BookmarkConstants.TXT_SDM_SUB_TITLE, "SDM");
				} else {
					bookmarkSDMSubTitle = createBookmark(BookmarkConstants.TXT_SDM_SUB_TITLE,FormConstants.EMPTY_STRING);
				}

			}
			bookmarkSdmRiskFinalList.add(bookmarkSDMSubTitle);
			/**End of QCR 62702**/
			BookmarkDto bookmarkSdmRiskFinal = createBookmarkWithCodesTable(BookmarkConstants.SDMRISKFINAL,
					prefillDto.getRiskAssessmentInfoDto().getCdFinalRiskLevel(), "CSDMRLVL");

			bookmarkSdmRiskFinalList.add(bookmarkSdmRiskFinal);
			sdmRiskFinalGroupDto.setBookmarkDtoList(bookmarkSdmRiskFinalList);
			formDataGroupList.add(sdmRiskFinalGroupDto);
		}

		// CSEC01 - Worker
		BookmarkDto bookmarkSumOfficeAddrCity = createBookmark(BookmarkConstants.SUM_OFFICE_ADDR_CITY,
				prefillDto.getEmpWorkerDto().getAddrMailCodeCity());
		bookmarkNonGroupList.add(bookmarkSumOfficeAddrCity);
		BookmarkDto bookmarkSumOfficeAddrCounty = createBookmarkWithCodesTable(BookmarkConstants.SUM_OFFICE_ADDR_COUNTY,
				prefillDto.getEmpWorkerDto().getAddrMailCodeCounty(), CodesConstant.CCOUNT);
		bookmarkNonGroupList.add(bookmarkSumOfficeAddrCounty);
		BookmarkDto bookmarkSumOfficeAddrLn1 = createBookmark(BookmarkConstants.SUM_OFFICE_ADDR_LINE_1,
				prefillDto.getEmpWorkerDto().getAddrMailCodeStLn1());
		bookmarkNonGroupList.add(bookmarkSumOfficeAddrLn1);
		BookmarkDto bookmarkSumOfficeAddrLn2 = createBookmark(BookmarkConstants.SUM_OFFICE_ADDR_LINE_2,
				prefillDto.getEmpWorkerDto().getAddrMailCodeStLn2());
		bookmarkNonGroupList.add(bookmarkSumOfficeAddrLn2);
		BookmarkDto bookmarkSumOfficeAddrZip = createBookmark(BookmarkConstants.SUM_OFFICE_ADDR_ZIP,
				prefillDto.getEmpWorkerDto().getAddrMailCodeZip());
		bookmarkNonGroupList.add(bookmarkSumOfficeAddrZip);

		BookmarkDto bookmarkSumWorkerNm = createBookmark(BookmarkConstants.SUM_WORKER_NAME,
				personUtil.getPersonFullName(prefillDto.getEmpWorkerDto().getIdPerson()));
		bookmarkNonGroupList.add(bookmarkSumWorkerNm);

		// CSEC02D
		BookmarkDto bookmarkSumDtInvestAppr = createBookmark(BookmarkConstants.SUM_DT_INVEST_APPR,
				DateUtils.stringDt(prefillDto.getGenericCaseInfoDto().getDtStageClose()));
		bookmarkNonGroupList.add(bookmarkSumDtInvestAppr);
		BookmarkDto bookmarkSumReccomAction = createBookmarkWithCodesTable(BookmarkConstants.SUM_RECCOM_ACTION,
				prefillDto.getGenericCaseInfoDto().getCdStageReasonClosed(), CodesConstant.CCINVCLS);
		bookmarkNonGroupList.add(bookmarkSumReccomAction);
		BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				prefillDto.getGenericCaseInfoDto().getNmCase());
		bookmarkNonGroupList.add(bookmarkTitleCaseName);
		BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				prefillDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonGroupList.add(bookmarkTitleCaseNumber);

		// CINV95D
		BookmarkDto bookmarkSumOverallDisp = createBookmarkWithCodesTable(BookmarkConstants.SUM_OVERALL_DISP,
				prefillDto.getCpsInvstDetail().getCdCpsOverallDisptn(), CodesConstant.CCIVALDS);
		bookmarkNonGroupList.add(bookmarkSumOverallDisp);
		if (ServiceConstants.CITIZENSHIP_STATUS_TMR.equalsIgnoreCase(prefillDto.getCitizenshipStatus())) {
			BookmarkDto bookmarkSumEaEligibility = createBookmarkWithCodesTable(BookmarkConstants.SUM_EA_ELIGIBILITY,
					ServiceConstants.U, CodesConstant.CEACONCL);
			bookmarkNonGroupList.add(bookmarkSumEaEligibility);
		} else {
			BookmarkDto bookmarkSumEaEligibility = createBookmarkWithCodesTable(BookmarkConstants.SUM_EA_ELIGIBILITY,
					prefillDto.getCpsInvstDetail().getIndCpsInvstDtlEaConcl(), CodesConstant.CAFCTRSP);
			bookmarkNonGroupList.add(bookmarkSumEaEligibility);
		}
		BookmarkDto bookmarkSumSafetyPlan = createBookmarkWithCodesTable(BookmarkConstants.SUM_SAFETY_PLAN,
				prefillDto.getCpsInvstDetail().getIndCpsInvstSafetyPln(), CodesConstant.CAFCTRSP);
		bookmarkNonGroupList.add(bookmarkSumSafetyPlan);
		BookmarkDto bookmarkSumDtIntakeAssigned = createBookmark(BookmarkConstants.SUM_DT_INTAKE_ASSIGNED,
				DateUtils.stringDt(prefillDto.getCpsInvstDetail().getDtCpsInvstDtlAssigned()));
		bookmarkNonGroupList.add(bookmarkSumDtIntakeAssigned);
		BookmarkDto bookmarkSumDtInvestBegun = createBookmark(BookmarkConstants.SUM_DT_INVEST_BEGUN,
				DateUtils.stringDt(prefillDto.getCpsInvstDetail().getDtCpsInvstDtlBegun()));
		bookmarkNonGroupList.add(bookmarkSumDtInvestBegun);
		BookmarkDto bookmarkSumDtIntakeReceived = createBookmark(BookmarkConstants.SUM_DT_INTAKE_RECEIVED,
				DateUtils.stringDt(prefillDto.getCpsInvstDetail().getDtCpsInvstDtlIntake()));
		bookmarkNonGroupList.add(bookmarkSumDtIntakeReceived);
		BookmarkDto bookmarkDtInvestActions = createBookmark(BookmarkConstants.SUM_DT_INVEST_ACTIONS,
				DateUtils.stringDt(prefillDto.getCpsInvstDetail().getDtCpsInvstDtlComplt()));
		bookmarkNonGroupList.add(bookmarkDtInvestActions);
		
		// CINV95D CpsInvstDetail Investigation details will get populate from here.
		if (!ObjectUtils.isEmpty(prefillDto.getCpsInvstDetail())) {
			BookmarkDto bookmarkOverallDisptn = createBookmarkWithCodesTable(BookmarkConstants.OVERALL_DISP,
					prefillDto.getCpsInvstDetail().getCdCpsOverallDisptn(), CodesConstant.CDISPSTN);
			bookmarkNonGroupList.add(bookmarkOverallDisptn);
			BookmarkDto bookmarkChildLaborTraffic = createBookmarkWithCodesTable(BookmarkConstants.INV_LABOR_TRAFFIC,
					prefillDto.getCpsInvstDetail().getIndChildLaborTraffic(), CodesConstant.CINVACAN);
			bookmarkNonGroupList.add(bookmarkChildLaborTraffic);
			BookmarkDto bookmarkChildSexTraffic = createBookmarkWithCodesTable(BookmarkConstants.INV_SEX_TRAFFIC,
					prefillDto.getCpsInvstDetail().getIndChildLaborTraffic(), CodesConstant.CINVACAN);
			bookmarkNonGroupList.add(bookmarkChildSexTraffic);
			BookmarkDto bookmarkCpsLeJointContact = createBookmarkWithCodesTable(BookmarkConstants.INV_JOINT_CNT,
					prefillDto.getCpsInvstDetail().getIndCpsLeJntCntct(), CodesConstant.CBJNTCNT);
			bookmarkNonGroupList.add(bookmarkCpsLeJointContact);

			BookmarkDto bookmarkCpsInvstSafetyPln = createBookmarkWithCodesTable(BookmarkConstants.SAFETY_PLAN_COMPLETE,
					prefillDto.getCpsInvstDetail().getIndCpsInvstSafetyPln(), CodesConstant.CINVACAN);
			bookmarkNonGroupList.add(bookmarkCpsInvstSafetyPln);
			BookmarkDto bookmarkFTMOccurred = createBookmarkWithCodesTable(BookmarkConstants.INV_FTM_OCCURRED,
					prefillDto.getCpsInvstDetail().getIndFtmOccurred(), CodesConstant.CINVACAN);
			bookmarkNonGroupList.add(bookmarkFTMOccurred);
			BookmarkDto bookmarkFTMOffered = createBookmarkWithCodesTable(BookmarkConstants.INV_FTM_OFFERED,
					prefillDto.getCpsInvstDetail().getIndFtmOffered(), CodesConstant.CINVACAN);
			bookmarkNonGroupList.add(bookmarkFTMOffered);
			BookmarkDto bookmarkMultiPersFound = createBookmarkWithCodesTable(BookmarkConstants.INV_MULTI_FOUND,
					prefillDto.getCpsInvstDetail().getIndMultPersFound(), CodesConstant.CINVACAN);
			bookmarkNonGroupList.add(bookmarkMultiPersFound);
			BookmarkDto bookmarkMultiPersMerged = createBookmarkWithCodesTable(BookmarkConstants.INV_MULTI_MERGE,
					prefillDto.getCpsInvstDetail().getIndMultPersMerged(), CodesConstant.CINVACAN);
			bookmarkNonGroupList.add(bookmarkMultiPersMerged);
			BookmarkDto bookmarkParentGivenGuide = createBookmarkWithCodesTable(BookmarkConstants.INV_PARENT_GUIDE,
					prefillDto.getCpsInvstDetail().getIndParentGivenGuide(), CodesConstant.CINVACAN);
			bookmarkNonGroupList.add(bookmarkParentGivenGuide);
			BookmarkDto bookmarkParentNotify = createBookmarkWithCodesTable(BookmarkConstants.INV_PARENT_24HR,
					prefillDto.getCpsInvstDetail().getIndParentNotify24h(), CodesConstant.CINVACAN);
			bookmarkNonGroupList.add(bookmarkParentNotify);
			BookmarkDto bookmarkVictimPhoto = createBookmarkWithCodesTable(BookmarkConstants.INV_VIC_PHOTO,
					prefillDto.getCpsInvstDetail().getIndVictimPhoto(), CodesConstant.CINVACAN);
			bookmarkNonGroupList.add(bookmarkVictimPhoto);

			Date crelNotifRightsDate = null;
            try {
				crelNotifRightsDate = DateUtils
                        .toJavaDateFromInput(lookupDao.simpleDecode(ServiceConstants.CRELDATE, CodesConstant.CRELDATE_JUNE_24_NTFCTN_RGHTS));
            } catch (ParseException e) {
                logger.error(e.getMessage());
            }

			if(!ObjectUtils.isEmpty(crelNotifRightsDate) && (ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto().getDtStageClose())
					|| DateUtils.isAfter(prefillDto.getGenericCaseInfoDto().getDtStageClose(), crelNotifRightsDate)))
			{
				FormDataGroupDto notificationRightsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NOTIFICATION_RIGHTS,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkNotificationRightsList = new ArrayList<BookmarkDto>();
				BookmarkDto indVrdWrtNotificationRights = createBookmarkWithCodesTable(BookmarkConstants.INV_VRB_WRT_NOTIF,
						prefillDto.getCpsInvstDetail().getIndVrblWrtnNotifRights(), CodesConstant.CINVACAN);
				bookmarkNotificationRightsList.add(indVrdWrtNotificationRights);
				BookmarkDto indNotificationUpload = createBookmarkWithCodesTable(BookmarkConstants.INV_NOTIF_FRM_UPD,
						prefillDto.getCpsInvstDetail().getIndNotifRightsUpld(), CodesConstant.CINVACAN);
				bookmarkNotificationRightsList.add(indNotificationUpload);
				notificationRightsGroupDto.setBookmarkDtoList(bookmarkNotificationRightsList);
				formDataGroupList.add(notificationRightsGroupDto);
			}

			BookmarkDto bookmarkVictimTaped = createBookmarkWithCodesTable(BookmarkConstants.INV_VIC_TAPED,
					prefillDto.getCpsInvstDetail().getIndVictimTaped(), CodesConstant.CINVACAN);
			bookmarkNonGroupList.add(bookmarkVictimTaped);
			BookmarkDto bookmarkCpsInvstAbbrv = createBookmarkWithCodesTable(BookmarkConstants.INV_ABBREVIATED,
					prefillDto.getCpsInvstDetail().getIndCpsInvstDtlAbbrv(), CodesConstant.CINVACAN);
			bookmarkNonGroupList.add(bookmarkCpsInvstAbbrv);

			BookmarkDto bookmarkCdCpsInvstCpsLeJointContact = createBookmarkWithCodesTable(
					BookmarkConstants.INV_NOJOINT,
					prefillDto.getCpsInvstDetail().getCdReasonNoJntCntct(), CodesConstant.CSJNTCNT);
			bookmarkNonGroupList.add(bookmarkCdCpsInvstCpsLeJointContact);
			BookmarkDto bookmarkCdVictimPhoto = createBookmarkWithCodesTable(BookmarkConstants.INV_NO_PHOTO_REASON,
					prefillDto.getCpsInvstDetail().getCdVictimNoPhotoRsn(), CodesConstant.CINVNPHT);
			bookmarkNonGroupList.add(bookmarkCdVictimPhoto);
			BookmarkDto bookmarkCdVictimTaped = createBookmarkWithCodesTable(BookmarkConstants.INV_VIC_NO_TAPED_REASON,
					prefillDto.getCpsInvstDetail().getCdVictimTaped(), CodesConstant.CVICTPD);
			bookmarkNonGroupList.add(bookmarkCdVictimTaped);
			BookmarkDto bookmarkDtCPSInvstDtlBegun = createBookmark(BookmarkConstants.DT_INV_BEGUN,
					DateUtils.stringDt(prefillDto.getCpsInvstDetail().getDtCpsInvstDtlBegun()));
			bookmarkNonGroupList.add(bookmarkDtCPSInvstDtlBegun);
			BookmarkDto bookmarkCPSInvstDtlIntake = createBookmark(BookmarkConstants.DT_INT_REC,
					DateUtils.stringDt(prefillDto.getCpsInvstDetail().getDtCpsInvstDtlIntake()));
			bookmarkNonGroupList.add(bookmarkCPSInvstDtlIntake);
			BookmarkDto bookmarkCpsInvstDtlComplt = createBookmark(BookmarkConstants.DT_INV_COMPLETED,
					DateUtils.stringDt(prefillDto.getCpsInvstDetail().getDtCpsInvstDtlComplt()));
			bookmarkNonGroupList.add(bookmarkCpsInvstDtlComplt);
			BookmarkDto bookmarkCpsInvstCpsLeJointContact = createBookmark(BookmarkConstants.INV_JOINT_CNT_TXT,
					prefillDto.getCpsInvstDetail().getReasonNoJntCntct());
			bookmarkNonGroupList.add(bookmarkCpsInvstCpsLeJointContact);
			BookmarkDto bookmarkTxtVictimPhoto = createBookmark(BookmarkConstants.INV_VIC_PHOTO_TXT,
					prefillDto.getCpsInvstDetail().getVictimPhoto());
			bookmarkNonGroupList.add(bookmarkTxtVictimPhoto);
			BookmarkDto bookmarkTxtVictimTaped = createBookmark(BookmarkConstants.INV_VIC_TAPED_TXT,
					prefillDto.getCpsInvstDetail().getVictimTaped());
			bookmarkNonGroupList.add(bookmarkTxtVictimTaped);

		}
		// getMethIndicator
		if(!ObjectUtils.isEmpty(prefillDto.getIdMethSusp())){
			BookmarkDto bookmarkSuspMeth = createBookmarkWithCodesTable(BookmarkConstants.INV_METH_SUSP,
					prefillDto.getIdMethSusp(), CodesConstant.CINVACAN);
			bookmarkNonGroupList.add(bookmarkSuspMeth);
			
			BookmarkDto bookmarkIndMeth = createBookmark(BookmarkConstants.INV_METH_DISC,
					prefillDto.getIdMethSusp().equals(ServiceConstants.N)?ServiceConstants.ARNO:ServiceConstants.ARYES);
			bookmarkNonGroupList.add(bookmarkIndMeth);
		}	
		// CSEC01D - Supervisor
		BookmarkDto bookmarkSumSupervisorNm = createBookmark(BookmarkConstants.SUM_SUPERVISOR_NAME,
				personUtil.getPersonFullName(prefillDto.getEmpSupvDto().getIdPerson()));
		bookmarkNonGroupList.add(bookmarkSumSupervisorNm);

		// Populate the Template Removal CheckList

		if (!ObjectUtils.isEmpty(prefillDto.getChecklists())) {
			FormDataGroupDto rmvlGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RMVL,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> rmvlChecklistGroupDtoList = new ArrayList<FormDataGroupDto>();
			
			for (RmvlChcklstValueDto rmvlChcklstValueDto : prefillDto.getChecklists()) {
				FormDataGroupDto rmvlChkLstGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RMVL_CHCKLST,
						FormGroupsConstants.TMPLAT_RMVL);
				List<BookmarkDto> bookmarkrmvlChkLst = new ArrayList<BookmarkDto>();
				bookmarkrmvlChkLst.add(createBookmark(BookmarkConstants.RMVL_CHCKLSTCHILDLIST,
						rmvlChcklstValueDto.getTxtChildList()));
				bookmarkrmvlChkLst.add(createBookmark(BookmarkConstants.RMVL_CHCKLSTLABEL,
						rmvlChcklstValueDto.getNmChklst()));
				bookmarkrmvlChkLst.add(createBookmark(BookmarkConstants.RMVL_CHCKLST_PURPOSE,
						rmvlChcklstValueDto.getTxtPurps()));
				bookmarkrmvlChkLst.add(createBookmark(BookmarkConstants.RMVL_CHCKLST_INFO,
						rmvlChcklstValueDto.getTxtInstrctns()));
				bookmarkrmvlChkLst.add(createBookmark(BookmarkConstants.RMVL_CHCKLST_NOTE,
						rmvlChcklstValueDto.getTxtNote()));
				bookmarkrmvlChkLst.add(createBookmark(BookmarkConstants.RMVL_CHCKLST_DATE,
						DateUtils.stringDt(rmvlChcklstValueDto.getDtRemoval())));
				bookmarkrmvlChkLst.add(createBookmark(BookmarkConstants.RMVL_CHCKLST_CASENAME,
						caseUtils.getNmCase(prefillDto.getCpsInvstDetail().getIdCase())));
				bookmarkrmvlChkLst.add(createBookmark(BookmarkConstants.RMVL_CHCKLST_CASEID,
						prefillDto.getCpsInvstDetail().getIdCase()));
				rmvlChkLstGroupDto.setBookmarkDtoList(bookmarkrmvlChkLst);
				
				// display the stage ID if closed-to-merge
				List<FormDataGroupDto> checklistMergeFormDataGroupDtoList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> checklistMergeBkmkDtoList = new ArrayList<BookmarkDto>();
				if (!ObjectUtils.isEmpty(rmvlChcklstValueDto.getCdStageReasonClosed())
						&& rmvlChcklstValueDto.getCdStageReasonClosed().equals(ServiceConstants.Close_To_Merge)) {
					FormDataGroupDto chkLstMergeGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_RMVL_CHCKLST_MERGE, FormGroupsConstants.TMPLAT_RMVL_CHCKLST);
					checklistMergeBkmkDtoList.add(createBookmark(BookmarkConstants.RMVL_CHCKLST_MERGE,
							rmvlChcklstValueDto.getIdStage()));
					chkLstMergeGroupDto.setBookmarkDtoList(checklistMergeBkmkDtoList);
					checklistMergeFormDataGroupDtoList.add(chkLstMergeGroupDto);
					rmvlChkLstGroupDto.setFormDataGroupList(checklistMergeFormDataGroupDtoList);
				}

				populateTaskSection(rmvlChkLstGroupDto, rmvlChcklstValueDto, prefillDto.getSctnTasks());

				rmvlChecklistGroupDtoList.add(rmvlChkLstGroupDto);

			}
			rmvlGroupDto.setFormDataGroupList(rmvlChecklistGroupDtoList);
			formDataGroupList.add(rmvlGroupDto);
		}

		PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
		prefillData.setBookmarkDtoList(bookmarkNonGroupList);
		prefillData.setFormDataGroupList(formDataGroupList);

		return prefillData;
	}

	public String getFullName(String lastName, String firstName, String middleName) {
		StringBuilder fullName = new StringBuilder();
		if (!ObjectUtils.isEmpty(lastName)) {
			fullName.append(lastName);
			fullName.append(ServiceConstants.COMMA);

		}
		if (!ObjectUtils.isEmpty(firstName)) {
			fullName.append(firstName);
			fullName.append(ServiceConstants.SPACE);

		}
		if (!ObjectUtils.isEmpty(middleName)) {
			fullName.append(middleName);

		}

		return fullName.toString();

	}

	public String removeSpecialChar(String input) {
		Pattern pt = Pattern.compile("[^a-zA-Z0-9 /:/-/(/)/*/.]");
		Matcher match = pt.matcher(input);
		while (match.find()) {
			String s = match.group();
			input = input.replaceAll("\\" + s, "");
		}

		return input;
	}

	public void populateTaskSection(FormDataGroupDto rmvlChkLstGroupDto, RmvlChcklstValueDto rmvlChcklstValueDto,
			List<RmvlChcklstSctnTaskValueDto> rmvlChcklstSctnTaskList) {
		List<FormDataGroupDto> taskRspnsGroupList = new ArrayList<FormDataGroupDto>();
		for (RmvlChcklstSctnTaskValueDto rmvlChcklstSctnTaskValueDto : rmvlChcklstSctnTaskList) {
			// artf147748 if after cpi release date, check IdRmvlChcklstLink ...
			if(( Boolean.TRUE.equals(rmvlChcklstSctnTaskValueDto.getIsAfterCreldateJun2020())
					&& null != rmvlChcklstSctnTaskValueDto.getIdRmvlChcklstLink()
					&& rmvlChcklstSctnTaskValueDto.getIdRmvlChcklstLink().equals(rmvlChcklstValueDto.getIdRmvlChcklstLink())
					&& rmvlChcklstSctnTaskValueDto.getIdStage().equals(rmvlChcklstValueDto.getIdStage()) 
		        	&& rmvlChcklstSctnTaskValueDto.getIdRmvlGroup().equals(rmvlChcklstValueDto.getIdRmvlGroup()))
				|| // end artf147748 ... or as before ...
		         ( Boolean.FALSE.equals(rmvlChcklstSctnTaskValueDto.getIsAfterCreldateJun2020()) 
		        	&& rmvlChcklstSctnTaskValueDto.getIdStage().equals(rmvlChcklstValueDto.getIdStage()) 
		        	&& rmvlChcklstSctnTaskValueDto.getIdRmvlGroup().equals(rmvlChcklstValueDto.getIdRmvlGroup()))) {
				FormDataGroupDto taskRspnsGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_RMVL_CHCKLST_SCTNTASK, FormGroupsConstants.TMPLAT_RMVL_CHCKLST);
				List<BookmarkDto> taskRspnsBookmarkList = new ArrayList<BookmarkDto>();
				String header = getDueDate(rmvlChcklstValueDto, rmvlChcklstSctnTaskValueDto);

				List<FormDataGroupDto> headerStyleList = new ArrayList<FormDataGroupDto>();

				if (!ObjectUtils.isEmpty(rmvlChcklstSctnTaskValueDto.getIndHeader())
						&& rmvlChcklstSctnTaskValueDto.getIndHeader().equals(ServiceConstants.Y)) {
					// if Header, the response field will display "Date Completed" as the header text, add bold header groups 1 & 3
					headerStyleList.add(createFormDataGroup(
							FormGroupsConstants.TMPLAT_RMVL_CHCKLST_SCTNTASK_HDR1, taskRspnsGroupDto.getFormDataGroupBookmark()));
					headerStyleList.add(createFormDataGroup(
							FormGroupsConstants.TMPLAT_RMVL_CHCKLST_SCTNTASK_HDR3, taskRspnsGroupDto.getFormDataGroupBookmark()));
					taskRspnsBookmarkList.add(createBookmark(
							BookmarkConstants.RMVL_CHCKLST_SCTNTASK_RSPNS, RMVL_CHCKLST_SCTNTASK_RSPNS_TEXT));

				} else {  // add normal test groups 2 & 4
					headerStyleList.add(createFormDataGroup(
							FormGroupsConstants.TMPLAT_RMVL_CHCKLST_SCTNTASK_HDR2, taskRspnsGroupDto.getFormDataGroupBookmark()));
					headerStyleList.add(createFormDataGroup(
							FormGroupsConstants.TMPLAT_RMVL_CHCKLST_SCTNTASK_HDR4, taskRspnsGroupDto.getFormDataGroupBookmark()));
					if (!ObjectUtils.isEmpty(rmvlChcklstSctnTaskValueDto.getTxtRspn())) {
						taskRspnsBookmarkList.add(createBookmark(
								BookmarkConstants.RMVL_CHCKLST_SCTNTASK_RSPNS, rmvlChcklstSctnTaskValueDto.getTxtRspn()));
					}
				}
				if (!ObjectUtils.isEmpty(rmvlChcklstSctnTaskValueDto.getTxtDesc())) {
					// if not a Header the replace text is not there
					taskRspnsBookmarkList.add(createBookmark( BookmarkConstants.RMVL_CHCKLST_SCTNTASK_DESC, 
							rmvlChcklstSctnTaskValueDto.getTxtDesc().replace(DUE_DATE, header)));
				}
				taskRspnsGroupDto.setBookmarkDtoList(taskRspnsBookmarkList);
				taskRspnsGroupDto.setFormDataGroupList(headerStyleList);
				taskRspnsGroupList.add(taskRspnsGroupDto);
			}
		}
		rmvlChkLstGroupDto.setFormDataGroupList(taskRspnsGroupList);
	}

	private String getDueDate(RmvlChcklstValueDto checklist, RmvlChcklstSctnTaskValueDto sctnTask) {
		String dueDateString = "";
		int numberOfDays = ServiceConstants.Zero_INT;
		Date triggerDate = null;

		if ( null != sctnTask.getCdTrigger()) {
			if (PLCMNT.equals(sctnTask.getCdTrigger())) {
				// TODO what date is trigger
				triggerDate = checklist.getDtRemoval();
				numberOfDays = 1;
			} else if (COURT.equals(sctnTask.getCdTrigger())) {
				// TODO what date is trigger
				triggerDate = checklist.getDtRemoval();
			} else if (OTHR.equals(sctnTask.getCdTrigger())) {
				// TODO what date is trigger
				;
			} else if (RMVL.equals(sctnTask.getCdTrigger())) {
				triggerDate = checklist.getDtRemoval();
				if (HOUR.equals(sctnTask.getCdTriggerInterval())) {
					numberOfDays = (int) (sctnTask.getNbrTriggerValue() / 24);
				} else if (DAY.equals(sctnTask.getCdTriggerInterval())) {
					numberOfDays = sctnTask.getNbrTriggerValue().intValue();
				} else if (WEEK.equals(sctnTask.getCdTriggerInterval())) {
					numberOfDays = 7 * sctnTask.getNbrTriggerValue().intValue();
				}
			}
		}
		if (null != triggerDate) {
			dueDateString = DUE_DATE + ServiceConstants.SPACE
					+ DateUtils.stringDt(DateHelper.addToDate(triggerDate, 0, 0, numberOfDays));
		}
		return dueDateString;
	}/* end getDueDate */
}