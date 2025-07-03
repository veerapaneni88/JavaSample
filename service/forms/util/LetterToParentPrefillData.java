package us.tx.state.dfps.service.forms.util;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.admin.dto.EmpNameDto;
import us.tx.state.dfps.service.admin.dto.StageSituationOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvAllegDto;
import us.tx.state.dfps.service.cpsinv.dto.StagePersonValueDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.CpsInvstDetailDto;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.person.dto.LetterToParentDto;
import us.tx.state.dfps.service.person.dto.PersonEmailDto;
import us.tx.state.dfps.service.person.dto.PersonGenderSpanishDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StageProgDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Generates
 * prefill string using data passed from data; used for both cfiv3000 and
 * cfiv3100 May 29, 2018- 4:09:58 PM © 2017 Texas Department of Family and
 * Protective Services
 * ********Change History**********
 * 06/28/2019 chrisf artf118334 ppm39838 hpalm12277 RRL- All AP's are displayed regardless of disposition
 * 08/16/2019 paniwa2 artf120231 ppm39838 hpalm12642 NullPointer launches 2 blue lines
 * 11/07/2022 vaddih artf213657 ALM Defect# 18556- letter language when School INV is RTB - ENGLISH & SPANISH.
 * 01/02/2023 thompswa artf238090 PPM 73576 add ARIF address.
 *
 */
@Component
public class LetterToParentPrefillData extends DocumentServiceUtil {

	private static final Logger logger = Logger.getLogger(LetterToParentPrefillData.class.getName());

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		LetterToParentDto prefillDto = (LetterToParentDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonGroupList = new ArrayList<BookmarkDto>();

		// Initialize null DTOs
		if (ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
			prefillDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getAddressDto())) {
			prefillDto.setAddressDto(new AddressDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getAllegationsList())) {
			prefillDto.setAllegationsList(new ArrayList<CpsInvAllegDto>()); // artf238090
		}
		if (ObjectUtils.isEmpty(prefillDto.getAllegTypesList())) {
			prefillDto.setAllegTypesList(new ArrayList<FacilityAllegationInfoDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getEmpNameDto())) {
			prefillDto.setEmpNameDto(new EmpNameDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getCodesTablesList())) {
			prefillDto.setCodesTablesList(new ArrayList<CodesTablesDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getCpsInvstDetailDto())) {
			prefillDto.setCpsInvstDetailDto(new CpsInvstDetailDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getPersonGenderSpanishDto())) {
			prefillDto.setPersonGenderSpanishDto(new PersonGenderSpanishDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getDispositionsList())) {
			prefillDto.setDispositionsList(new ArrayList<FacilityAllegationInfoDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getEmployeePersPhNameDto())) {
			prefillDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getRuledOutStageList())) {
			prefillDto.setRuledOutStageList(new ArrayList<AllegationWithVicDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getPerpsList())) {
			prefillDto.setPerpsList(new ArrayList<FacilityAllegationInfoDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getPerpsRemovalList())) {
			prefillDto.setPerpsRemovalList(new ArrayList<FacilityAllegationInfoDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getPersonEmailList())) {
			prefillDto.setPersonEmailList(new ArrayList<PersonEmailDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getStageDetailsList())) {
			prefillDto.setStageDetailsList(new ArrayList<StageSituationOutDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getStagePersonLinkList())) {
			prefillDto.setStagePersonLinkList(new ArrayList<StagePersonLinkDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getStagePersonValueDto())) {
			prefillDto.setStagePersonValueDto(new StagePersonValueDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getStageProgList())) {
			prefillDto.setStageProgList(new ArrayList<StageProgDto>());
		}

		// parent groups cfzco00
		if (StringUtils.isNotBlank(prefillDto.getEmpNameDto().getCdNameSuffix())) {
			FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(commaGroupDto);
			FormDataGroupDto comma2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(comma2GroupDto);
		}
		if (StringUtils.isNotBlank(prefillDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
			FormDataGroupDto comma3GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_3,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(comma3GroupDto);
		}

		// parent group cfiv3010/cfiv3110
		FormDataGroupDto addresseeAddrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESSEE_ADDRESS,
				FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> addresseeAddrGroupDtoList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkaddresseeAddrList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkAddresseeAddrZip = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_ZIP,
				prefillDto.getAddressDto().getAddrZip());
		bookmarkaddresseeAddrList.add(bookmarkAddresseeAddrZip);
		BookmarkDto bookmarkAddresseeAddrLn1 = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_LINE_1,
				prefillDto.getAddressDto().getAddrPersAddrStLn1());
		bookmarkaddresseeAddrList.add(bookmarkAddresseeAddrLn1);
		BookmarkDto bookmarkAddresseeAddrCity = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_CITY,
				prefillDto.getAddressDto().getAddrCity());
		bookmarkaddresseeAddrList.add(bookmarkAddresseeAddrCity);
		BookmarkDto bookmarkAddresseeAddrState = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_STATE,
				prefillDto.getAddressDto().getCdAddrState());
		bookmarkaddresseeAddrList.add(bookmarkAddresseeAddrState);
		addresseeAddrGroupDto.setBookmarkDtoList(bookmarkaddresseeAddrList);

		// sub group cfzz3010/cfzz3110
		if (StringUtils.isNotBlank(prefillDto.getAddressDto().getAddrPersAddrStLn2())) {
			FormDataGroupDto street2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_STREET2,
					FormGroupsConstants.TMPLAT_ADDRESSEE_ADDRESS);
			List<BookmarkDto> bookmarkStreet2List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkStreetLn2 = createBookmark(BookmarkConstants.STREET_LN_2,
					prefillDto.getAddressDto().getAddrPersAddrStLn2());
			bookmarkStreet2List.add(bookmarkStreetLn2);
			street2GroupDto.setBookmarkDtoList(bookmarkStreet2List);

			addresseeAddrGroupDtoList.add(street2GroupDto);
		}

		addresseeAddrGroupDto.setFormDataGroupList(addresseeAddrGroupDtoList);
		formDataGroupList.add(addresseeAddrGroupDto);

		// parent group cfiv3050/cfiv3150
		FormDataGroupDto workerAddrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER_ADDRESS,
				FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> workerAddrGroupDtoList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkWorkerAddrList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkWorkerPhone = createBookmark(BookmarkConstants.WORKER_PHONE,
				TypeConvUtil.formatPhone(prefillDto.getEmployeePersPhNameDto().getNbrPhone()));
		bookmarkWorkerAddrList.add(bookmarkWorkerPhone);
		BookmarkDto bookmarkWorkerAddrCity = createBookmark(BookmarkConstants.WORKER_ADDRESS_CITY,
				prefillDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
		bookmarkWorkerAddrList.add(bookmarkWorkerAddrCity);
		BookmarkDto bbokmarkWorkerAddrLn1 = createBookmark(BookmarkConstants.WORKER_ADDRESS_LINE_1,
				prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
		bookmarkWorkerAddrList.add(bbokmarkWorkerAddrLn1);
		BookmarkDto bookmarkWorkerAddrZip = createBookmark(BookmarkConstants.WORKER_ADDRESS_ZIP,
				prefillDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
		bookmarkWorkerAddrList.add(bookmarkWorkerAddrZip);
		workerAddrGroupDto.setBookmarkDtoList(bookmarkWorkerAddrList);

		// sub group cfzz3050/cfzz3150
		if (StringUtils.isNotBlank(prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2())) {
			FormDataGroupDto workerStreet2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER_STREET2,
					FormGroupsConstants.TMPLAT_WORKER_ADDRESS);
			List<BookmarkDto> bookmarkWorkerStreet2List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkWorkerStreet2 = createBookmark(BookmarkConstants.WORKER_STREET_LN_2,
					prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
			bookmarkWorkerStreet2List.add(bookmarkWorkerStreet2);
			workerStreet2GroupDto.setBookmarkDtoList(bookmarkWorkerStreet2List);

			workerAddrGroupDtoList.add(workerStreet2GroupDto);
		}

		// sub group cfzz3051/cfzz3151
		if (StringUtils.isNotBlank(prefillDto.getEmployeePersPhNameDto().getNbrPhoneExtension())) {
			FormDataGroupDto workerExtGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EXT,
					FormGroupsConstants.TMPLAT_WORKER_ADDRESS);
			List<BookmarkDto> bookmarkWorkerExtList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkWorkerExt = createBookmark(BookmarkConstants.WORKER_PHONE_EXTENSION,
					prefillDto.getEmployeePersPhNameDto().getNbrPhoneExtension());
			bookmarkWorkerExtList.add(bookmarkWorkerExt);
			workerExtGroupDto.setBookmarkDtoList(bookmarkWorkerExtList);

			workerAddrGroupDtoList.add(workerExtGroupDto);
		}
		workerAddrGroupDto.setFormDataGroupList(workerAddrGroupDtoList);
		formDataGroupList.add(workerAddrGroupDto);

		// parent group cfiv3003/cfiv3103
		for (CodesTablesDto codeDto : prefillDto.getCodesTablesList()) {
			FormDataGroupDto headerDirGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_DIRECTOR,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkHeaderDirList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDirTitle = createBookmark(BookmarkConstants.HEADER_DIRECTOR_TITLE,
					codeDto.getaDecode());
			bookmarkHeaderDirList.add(bookmarkDirTitle);
			BookmarkDto bookmarkDirName = createBookmark(BookmarkConstants.HEADER_DIRECTOR_NAME, codeDto.getbDecode());
			bookmarkHeaderDirList.add(bookmarkDirName);
			headerDirGroupDto.setBookmarkDtoList(bookmarkHeaderDirList);
			formDataGroupList.add(headerDirGroupDto);
		}

		//artf213657 - checks if the person selected to generate a letter is an AP or not and the disposition is RTB
		boolean isPersonRtb = false;
		for (CpsInvAllegDto dto : prefillDto.getAllegationsList()) {
			if (dto.getIdAllegPrep() != null && dto.getIdAllegPrep().equals(prefillDto.getEmpNameDto().getIdPerson())
						&& ServiceConstants.REASON_TO_BELIEVE.equals(dto.getCdAllegDisp())) {
					isPersonRtb = true;
			}
			// parent group cfiv3020/cfiv3120 collapsed into sub group cfiv3121
			FormDataGroupDto abuseGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ABUSE_LIST, FormConstants.EMPTY_STRING);
			formDataGroupList.add(abuseGroupDto);
			// decode differently for english/spanish versions - passed in from serviceImpl
			abuseGroupDto.setBookmarkDtoList(Arrays.asList(
					createBookmark(BookmarkConstants.ABUSE_LIST_FINDING, dto.getDecodeAllegDisp())
					, createBookmark(BookmarkConstants.ABUSE_LIST_PERP, dto.getNmPerpFull())
					, createBookmark(BookmarkConstants.ABUSE_LIST_VICTIM, dto.getNmVicFull())
					, createBookmark(BookmarkConstants.ABUSE_LIST_ABUSE, dto.getDecodeAllegType())// display any abuse/neglect definitions
			));
		}

		// parent group cfiv3070/cfiv3170
		if (StringUtils.isNotBlank(prefillDto.getIndVictim()) && ServiceConstants.A.equals(prefillDto.getIndVictim())) {
			FormDataGroupDto eciFprGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ECI_FPR,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(eciFprGroupDto);
		}

		// parent group cfiv3071/cfiv3171
		else if (StringUtils.isNotBlank(prefillDto.getIndVictim())
				&& ServiceConstants.CPS.equals(prefillDto.getIndVictim())) {
			FormDataGroupDto eciAllGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ECI_ALL,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(eciAllGroupDto);
		}

		// parent group cfiv3072/cfiv3172
		else if (StringUtils.isNotBlank(prefillDto.getIndVictim())
				&& ServiceConstants.BOTH.equals(prefillDto.getIndVictim())) {
			FormDataGroupDto eciCloseGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ECI_CLOSE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(eciCloseGroupDto);
		}

        for (AllegationWithVicDto vicDto : prefillDto.getRuledOutStageList()) {
			//artf189470: Right to Request Role Removal in letter to Parent/Guardian: the Right to Request Role description will only be
			// displayed when all allegations against the person for which the letter is being generated are Ruled Out on the Allegation List
			// of the stage.
			if (ServiceConstants.RULED_OUT.equals(vicDto.getCdOverAllDisposition())) {
                formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_ROLE_REMOVAL, FormConstants.EMPTY_STRING));
                break;
			}
		}

		// parent group cfiv3011/cfiv3111
		FormDataGroupDto parentOpeningGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PARENT_OPENING,
				FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> parentOpeningGroupList = new ArrayList<FormDataGroupDto>();

		// sub group cfiv3012/cfiv3112
		if (!ServiceConstants.ADM_CODE.equals(prefillDto.getCpsInvstDetailDto().getCdCpsOverallDisptn())) {
			FormDataGroupDto normOpeningGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NORM_OPENING,
					FormGroupsConstants.TMPLAT_PARENT_OPENING);
			List<BookmarkDto> bookmarkNormOpeningList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkAbnegDate = createBookmark(BookmarkConstants.ABNEG_REPORTED_DATE,
					DateUtils.stringDt(prefillDto.getCpsInvstDetailDto().getDtCpsInvstDtlIntake()));
			bookmarkNormOpeningList.add(bookmarkAbnegDate);
			normOpeningGroupDto.setBookmarkDtoList(bookmarkNormOpeningList);
			parentOpeningGroupList.add(normOpeningGroupDto);
		}

		// sub group cfiv3013/cfiv3113
		else {
			FormDataGroupDto admOpeningGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADM_OPENING,
					FormGroupsConstants.TMPLAT_PARENT_OPENING);
			// bookmark only for english version
			if (CodesConstant.CONNOTTY_CFIV3000.equalsIgnoreCase(prefillDto.getFormName())
			|| CodesConstant.CCNTCTYP_APGN.equalsIgnoreCase(prefillDto.getFormName())) {
				List<BookmarkDto> bookmarkAdmOpeningList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAbnegDate = createBookmark(BookmarkConstants.ABNEG_REPORTED_DATE,
						DateUtils.stringDt(prefillDto.getCpsInvstDetailDto().getDtCpsInvstDtlIntake()));
				bookmarkAdmOpeningList.add(bookmarkAbnegDate);
				admOpeningGroupDto.setBookmarkDtoList(bookmarkAdmOpeningList);
			}
			parentOpeningGroupList.add(admOpeningGroupDto);
		}

		parentOpeningGroupDto.setFormDataGroupList(parentOpeningGroupList);
		formDataGroupList.add(parentOpeningGroupDto);

		//pulling the alleged perpetrator from the allegation list and checking if the person selected to generate a letter is an AP or not
		boolean bRtbFound = false;
		for (FacilityAllegationInfoDto dispDto : prefillDto.getDispositionsList()) {
			if (ServiceConstants.ADM_CODE.equals(dispDto.getCdAllegDisposition())) {
				/** Create and add the parent group cfiv3059/cfiv3159 */
				FormDataGroupDto findingAdmGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FINDING_ADM, FormConstants.EMPTY_STRING);
				formDataGroupList.add(findingAdmGroupDto);
				/** Create and set the list of fields to the group (here only one) */
				findingAdmGroupDto.setBookmarkDtoList(Arrays.asList(
						createBookmark(BookmarkConstants.ADMINISTRATIVE_CLOSURE, dispDto.getDecodeAllegDisp())
				));
			}
			else if (ServiceConstants.RULED_OUT.equals(dispDto.getCdAllegDisposition())) {
				/** Create and add the parent group cfiv3058/cfiv3158 */
				FormDataGroupDto findingRoGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FINDING_RO, FormConstants.EMPTY_STRING);
				formDataGroupList.add(findingRoGroupDto);
				/** Create and set the list of fields to the group (here only one) */
				findingRoGroupDto.setBookmarkDtoList(Arrays.asList(
						createBookmark(BookmarkConstants.RULED_OUT, dispDto.getDecodeAllegDisp())
				));
			}
			else if (ServiceConstants.REASON_TO_BELIEVE.equals(dispDto.getCdAllegDisposition()) && !bRtbFound ) {
				/** Create and add the parent group cfiv3057/cfiv3157 */
				FormDataGroupDto findingRtbGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FINDING_RTB, FormConstants.EMPTY_STRING);
				formDataGroupList.add(findingRtbGroupDto);
				/** Create and set the list of fields to the group (here only one) */
				findingRtbGroupDto.setBookmarkDtoList(Arrays.asList(
						createBookmark(BookmarkConstants.REASON_TO_BELIEVE, dispDto.getDecodeAllegDisp())
				));
				if (isPersonRtb) {
					/** Create and add the parent group cfiv3075/cfiv3175 */
					FormDataGroupDto reviewRightGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REVIEW_RIGHT, FormConstants.EMPTY_STRING);
					formDataGroupList.add(reviewRightGroupDto);

					/** Ignore the c parent group cfiv3090/cfiv3190 is not needed by java */
					/** Create and set the list of the subgroups (cfiv3091/cfiv3191) and add to the overall list*/
					FormDataGroupDto reqAriGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ARI, FormConstants.EMPTY_STRING);
					formDataGroupList.add(reqAriGroupDto);
					/** Create and set the list of fields to the subgroup */
					reqAriGroupDto.setBookmarkDtoList(Arrays.asList(
							createBookmark(BookmarkConstants.ARI_SYSDATE,
									DateUtils.stringDt(prefillDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate()))
							, createBookmark(BookmarkConstants.ARI_ADDR, ServiceConstants.ARI_ADDR_TEXT)
							, createBookmark(BookmarkConstants.ARI_CASENAME, prefillDto.getGenericCaseInfoDto().getNmCase())
							, createBookmark(BookmarkConstants.ARI_CASENUMBER, prefillDto.getGenericCaseInfoDto().getIdCase())
							, createBookmark(BookmarkConstants.ARI_STAGE_NUMBER, prefillDto.getGenericCaseInfoDto().getIdStage())
					));
				}
				bRtbFound = true;
			}
			else if (ServiceConstants.UNABLE_TO_DETRMN_DISP.equals(dispDto.getCdAllegDisposition())) {
				/** Create and add the parent group cfiv3056/cfiv3156 */
				FormDataGroupDto findingUtdGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FINDING_UTD,
						FormConstants.EMPTY_STRING);
				/** Create and set the list of fields to the group (here only one) */
				findingUtdGroupDto.setBookmarkDtoList(Arrays.asList(
						createBookmark(BookmarkConstants.UNABLE_TO_DETERMINE, dispDto.getDecodeAllegDisp())
				));
				formDataGroupList.add(findingUtdGroupDto);
			}
		}

		//artf213657 - if the person selected to generate a letter is an AP and the disposition is RTB, then a certain text is additionally displayed in the RTB letter		if(isPersonRtb){
		if(bRtbFound && isPersonRtb){
			FormDataGroupDto overallRTBGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ANY_RTB,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(overallRTBGroupDto);
		}

		for (FacilityAllegationInfoDto dto : prefillDto.getAllegTypesList()) {
			// parent group cfiv306a/cfiv316a
			if (ServiceConstants.SXTR.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto sxtrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SXTR, FormConstants.EMPTY_STRING);
				sxtrGroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.SXTR_ALLEGATION, dto.getDecodeAllegType())));
				formDataGroupList.add(sxtrGroupDto);
			}
			// parent group cfiv3069/cfiv3169
			else if (ServiceConstants.LBTR.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto lbtrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_LBTR, FormConstants.EMPTY_STRING);
				lbtrGroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.LBTR_ALLEGATION, dto.getDecodeAllegType())));
				formDataGroupList.add(lbtrGroupDto);
			}

			// parent group cfiv3068/cfiv3168
			else if (ServiceConstants.SXAB.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto sxabGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SXAB, FormConstants.EMPTY_STRING);
				sxabGroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.SXAB_ALLEGATION, dto.getDecodeAllegType())));
				formDataGroupList.add(sxabGroupDto);
				logger.info("dto.getDecodeAllegType() = " + dto.getDecodeAllegType());
			}

			// parent group cfiv3067/cfiv3167
			else if (ServiceConstants.RAPR.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto raprGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RAPR, FormConstants.EMPTY_STRING);
				raprGroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.RAPR_ALLEGATION, dto.getDecodeAllegType())));
				formDataGroupList.add(raprGroupDto);
			}

			// parent group cfiv3066/cfiv3166
			else if (ServiceConstants.PHNG.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto phngGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PHNG, FormConstants.EMPTY_STRING);
				phngGroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.PHNG_ALLEGATION, dto.getDecodeAllegType())));
				formDataGroupList.add(phngGroupDto);
			}

			// parent group cfiv3065/cfiv3165
			else if (ServiceConstants.PHAB.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto phabGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PHAB, FormConstants.EMPTY_STRING);
				phabGroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.PHAB_ALLEGATION, dto.getDecodeAllegType())));
				formDataGroupList.add(phabGroupDto);
			}

			// parent group cfiv3064/cfiv3164
			else if (ServiceConstants.NSUP.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto nsupGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NSUP, FormConstants.EMPTY_STRING);
				nsupGroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.NSUP_ALLEGATION, dto.getDecodeAllegType())));
				formDataGroupList.add(nsupGroupDto);
			}

			// parent group cfiv3063/cfiv3163
			else if (ServiceConstants.MDNG.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto mdngGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MDNG, FormConstants.EMPTY_STRING);
				mdngGroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.MDNG_ALLEGATION, dto.getDecodeAllegType())));
				formDataGroupList.add(mdngGroupDto);
			}

			// parent group cfiv3062/cfiv3162
			else if (ServiceConstants.EMAB.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto emabGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EMAB, FormConstants.EMPTY_STRING);
				emabGroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.EMAB_ALLEGATION, dto.getDecodeAllegType())));
				formDataGroupList.add(emabGroupDto);
			}

			// parent group cfiv3061/cfiv3161
			else if (ServiceConstants.ABAN.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto abanGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ABAN, FormConstants.EMPTY_STRING);
				abanGroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.ABAN_ALLEGATION, dto.getDecodeAllegType())));
				formDataGroupList.add(abanGroupDto);
			}
		}

		for (StageProgDto stageProgDto : prefillDto.getStageProgList()) {
			// parent group cfiv3040/cfiv3140
			if(CodesConstant.CDISPSTN_UTD.equals(prefillDto.getCpsInvstDetailDto().getCdCpsOverallDisptn())){
				formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_WILL_OFFER_UTD, FormConstants.EMPTY_STRING));
			}
			else if (ServiceConstants.STR_ONE_VAL.equalsIgnoreCase(stageProgDto.getIndStageProgClose()))
			{
				formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_WILL_OFFER, FormConstants.EMPTY_STRING));
			}
			// cfiv3041/cfiv3141
			else if (ServiceConstants.STR_ZERO_VAL.equalsIgnoreCase(stageProgDto.getIndStageProgClose())) {
				formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_WILL_NOT, FormConstants.EMPTY_STRING));
			}
		}

		//artf251969 : Template RRL (role removal form) should be shown when all allegations are ruled out for that perpetrator
			for (FacilityAllegationInfoDto perpDto : prefillDto.getPerpsList()) {
			/** Create and add a primary group(parent group cfiv3080/cfiv3180) to the formDataGroupList */
				if (ServiceConstants.RULED_OUT.equals(perpDto.getCdAllegDisposition())) {
				FormDataGroupDto rrlGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RRL, FormConstants.EMPTY_STRING);
				formDataGroupList.add(rrlGroupDto);
				rrlGroupDto.setBookmarkDtoList(Arrays.asList(
						createBookmark(BookmarkConstants.RRL_INTAKE_DATE,
								DateUtils.stringDt(prefillDto.getCpsInvstDetailDto().getDtCpsInvstDtlIntake())) // sub group cfiv3082/cfiv3182
						, createBookmark(BookmarkConstants.RRL_CASE_NM, prefillDto.getGenericCaseInfoDto().getNmCase()) // sub group cfiv3083/cfiv3183
						, createBookmark(BookmarkConstants.RRL_CASE_ID, prefillDto.getGenericCaseInfoDto().getIdCase())
						, createBookmark(BookmarkConstants.RRL_EMAIL_TXT, getRrlEmailTxt(perpDto, prefillDto.getPersonEmailList())) // sub group cfiv3385/cfiv3285
						, createBookmark(BookmarkConstants.UE_GROUPID, 0l < perpDto.getIdPerson() ? perpDto.getIdPerson() : 0l)
				));
				// sub group cfiv3084/cfiv3184
				List<FormDataGroupDto> rrlGroupDtoList = new ArrayList<FormDataGroupDto>();
				for (CpsInvAllegDto dtoVic : prefillDto.getAllegationsList()) {
					if (dtoVic.getCdAllegDisp().equals(ServiceConstants.RULED_OUT) // artf118334
							&& (!ObjectUtils.isEmpty(dtoVic.getIdAllegPrep()) // artf120231
									&& dtoVic.getIdAllegPrep().equals(perpDto.getIdPerson()))) {
						FormDataGroupDto rrlListGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RRL_LIST,
								FormGroupsConstants.TMPLAT_RRL);
						rrlListGroupDto.setBookmarkDtoList(Arrays.asList(
								createBookmark(BookmarkConstants.RRL_LIST_AP, dtoVic.getNmPerpFull())
								, createBookmark(BookmarkConstants.RRL_LIST_VICTIM, dtoVic.getNmVicFull())
								, createBookmark( BookmarkConstants.RRL_LIST_ALLEG, dtoVic.getDecodeAllegType())
						));
						rrlGroupDtoList.add(rrlListGroupDto);
					}
				}
				rrlGroupDto.setFormDataGroupList(rrlGroupDtoList);
			}
		}

		// only for spanish version
		if (CodesConstant.CONNOTTY_CFIV3100.equalsIgnoreCase(prefillDto.getFormName())
		|| CodesConstant.CCNTCTYP_APGS.equalsIgnoreCase(prefillDto.getFormName())) {
			// parent group cfiv3316
			if (ServiceConstants.FEMALE.equalsIgnoreCase(prefillDto.getPersonGenderSpanishDto().getCdPersonSex())) {
				FormDataGroupDto salutation2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SALUTATION_2,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(salutation2GroupDto);
			}

			// parent group cfiv3315
			else if (ServiceConstants.MALE.equalsIgnoreCase(prefillDto.getPersonGenderSpanishDto().getCdPersonSex())) {
				FormDataGroupDto salutation1GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SALUTATION_1,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(salutation1GroupDto);
			}
		}

		// non group bookmarks
		// CSEC02D
		bookmarkNonGroupList.add(createBookmark(BookmarkConstants.SYSTEM_DATE,
				DateUtils.stringDt(prefillDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate())));
		bookmarkNonGroupList.add( createBookmark(BookmarkConstants.CASE_NAME,
				prefillDto.getGenericCaseInfoDto().getNmCase()));
		bookmarkNonGroupList.add(createBookmark(BookmarkConstants.CASE_NUMBER,
				prefillDto.getGenericCaseInfoDto().getIdCase()));

		// CCMN40D
		BookmarkDto bookmarkAddrNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.ADDRESSEE_NAME_SUFFIX,
				prefillDto.getEmpNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonGroupList.add(bookmarkAddrNameSuffix);

		BookmarkDto bookmarkDearNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.DEAR_NAME_SUFFIX,
				prefillDto.getEmpNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonGroupList.add(bookmarkDearNameSuffix);

		BookmarkDto bookmarkNameFirst = createBookmark(BookmarkConstants.DEAR_NAME_FIRST,
				prefillDto.getEmpNameDto().getNmNameFirst());
		bookmarkNonGroupList.add(bookmarkNameFirst);

		BookmarkDto bookmarkAddrNameFrist = createBookmark(BookmarkConstants.ADDRESSEE_NAME_FIRST,
				prefillDto.getEmpNameDto().getNmNameFirst());
		bookmarkNonGroupList.add(bookmarkAddrNameFrist);

		BookmarkDto bookmarkDearNameLast = createBookmark(BookmarkConstants.DEAR_NAME_LAST,
				prefillDto.getEmpNameDto().getNmNameLast());
		bookmarkNonGroupList.add(bookmarkDearNameLast);

		BookmarkDto bookmarkAddrNameLast = createBookmark(BookmarkConstants.ADDRESSEE_NAME_LAST,
				prefillDto.getEmpNameDto().getNmNameLast());
		bookmarkNonGroupList.add(bookmarkAddrNameLast);

		BookmarkDto bookmarkAddrNameMiddle = createBookmark(BookmarkConstants.ADDRESSEE_NAME_MIDDLE,
				prefillDto.getEmpNameDto().getNmNameMiddle());
		bookmarkNonGroupList.add(bookmarkAddrNameMiddle);

		BookmarkDto bookmarkDearNameMiddle = createBookmark(BookmarkConstants.DEAR_NAME_MIDDLE,
				prefillDto.getEmpNameDto().getNmNameMiddle());
		bookmarkNonGroupList.add(bookmarkDearNameMiddle);
		// CSEC01D
		BookmarkDto bookmarkWorkerNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.WORKER_NAME_SUFFIX,
				prefillDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonGroupList.add(bookmarkWorkerNameSuffix);

		BookmarkDto bookmarkWorkerNameFirst = createBookmark(BookmarkConstants.WORKER_NAME_FIRST,
				prefillDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonGroupList.add(bookmarkWorkerNameFirst);

		BookmarkDto bookmarkWorkerNameLast = createBookmark(BookmarkConstants.WORKER_NAME_LAST,
				prefillDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonGroupList.add(bookmarkWorkerNameLast);

		BookmarkDto bookmarkWorkerNameMiddle = createBookmark(BookmarkConstants.WORKER_NAME_MIDDLE,
				prefillDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonGroupList.add(bookmarkWorkerNameMiddle);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonGroupList);
		return preFillData;
	}

	public static String getRrlEmailTxt(FacilityAllegationInfoDto perpDto, List<PersonEmailDto> personEmailList) {

		String txtEmail = ServiceConstants.EMPTY_STRING;
		for (PersonEmailDto personEmail : personEmailList) {
			if (!ObjectUtils.isEmpty(personEmail.getIdPerson()) && personEmail.getIdPerson().equals(perpDto.getIdPerson())) {
				txtEmail = personEmail.getTxtEmail();
				break;
			}
		}
		return txtEmail;
	}

}
