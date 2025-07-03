package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdOutDto;
import us.tx.state.dfps.service.admin.dto.NamePrimayEndDateOutDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordOutDto;
import us.tx.state.dfps.service.admin.dto.StageSituationOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvAllegDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.person.dto.PersonEmailDto;
import us.tx.state.dfps.service.person.dto.PersonGenderSpanishDto;
import us.tx.state.dfps.service.person.dto.PopFormDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StageProgDto;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CINV38S
 * Class Description: This class is doing prefill data services for the
 * returning data from PopulateFormService. Jan 26, 2018 - 1:32:34 PM © 2017
 * Texas Department of Family and Protective Services
 * ********Change History**********
 * 11/07/2022 vaddih artf213657 ALM Defect# 18556- School INV RTB letter language - ENGLISH & SPANISH.
 * 01/02/2023 thompswa artf238090 PPM 73576 add ARIF address.
 */

@Component

/*
 * Form Name :CFIV3300(Letter to nonParents/nonParents Spanish)
 */
public class PopulateFormPrefillData extends DocumentServiceUtil {
	private static final String cfiv3200 = CodesConstant.CONNOTTY_CFIV3200.toLowerCase();
	private static final String cfiv3300 = CodesConstant.CONNOTTY_CFIV3300.toLowerCase();

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		PopFormDto popFormDto = (PopFormDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		// Initializing null DTOs
		if (ObjectUtils.isEmpty(popFormDto.getStageProgDto())) {
			popFormDto.setStageProgDto(new ArrayList<StageProgDto>());
		}
		if (ObjectUtils.isEmpty(popFormDto.getStageDetails())) {
			popFormDto.setStageDetails(new ArrayList<StageSituationOutDto>());
		}
		if (ObjectUtils.isEmpty(popFormDto.getEmployeePersPhNameDto())) {
			popFormDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (ObjectUtils.isEmpty(popFormDto.getAllegationDtoList())) {
			popFormDto.setAllegationDtoList(new ArrayList<AllegationDto>());
		}
		if (ObjectUtils.isEmpty(popFormDto.getAllegationWithVicDto())) {
			popFormDto.setAllegationWithVicDto(new ArrayList<AllegationWithVicDto>());
		}
		if (ObjectUtils.isEmpty(popFormDto.getCodesTablesDto())) {
			popFormDto.setCodesTablesDto(new CodesTablesDto());
		}
		if (ObjectUtils.isEmpty(popFormDto.getCpsInvstDetailStageIdOutDto())) {
			popFormDto.setCpsInvstDetailStageIdOutDto(new CpsInvstDetailStageIdOutDto());
		}
		if (ObjectUtils.isEmpty(popFormDto.getPersonEmailDto())) {
			popFormDto.setPersonEmailDto(new PersonEmailDto());
		}
		if (ObjectUtils.isEmpty(popFormDto.getPersonGenderSpanishDto())) {
			popFormDto.setPersonGenderSpanishDto(new PersonGenderSpanishDto());
		}
		if (ObjectUtils.isEmpty(popFormDto.getAllegationWithVicUqStageDto())) {
			popFormDto.setAllegationWithVicUqStageDto(new AllegationWithVicDto());
		}
		if (ObjectUtils.isEmpty(popFormDto.getNamePrimayEndDateOutDto())) {
			popFormDto.setNamePrimayEndDateOutDto(new ArrayList<NamePrimayEndDateOutDto>());
		}
		if (ObjectUtils.isEmpty(popFormDto.getAddressDto())) {
			popFormDto.setAddressDto(new AddressDto());
		}
		if (ObjectUtils.isEmpty(popFormDto.getCaseInfoDtoprincipal())) {
			popFormDto.setCaseInfoDtoprincipal(new CaseInfoDto());
		}
		if (ObjectUtils.isEmpty(popFormDto.getStagePersonLinkRecordOutDto())) {
			popFormDto.setStagePersonLinkRecordOutDto(new StagePersonLinkRecordOutDto());
		}
		if (ObjectUtils.isEmpty(popFormDto.getGenericCaseInfoDto())) {
			popFormDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}
		if (ObjectUtils.isEmpty(popFormDto.getStagePersonLinkDtoList())) {
			popFormDto.setStagePersonLinkDtoList(new ArrayList<StagePersonLinkDto>());
		}
		if (ObjectUtils.isEmpty(popFormDto.getAllegationsList())) {
			popFormDto.setAllegationsList(new ArrayList<CpsInvAllegDto>());
		}

		AllegationWithVicDto dtoStage = popFormDto.getAllegationWithVicUqStageDto();
		if (!TypeConvUtil.isNullOrEmpty(dtoStage.getAlgCdAllegDisposition())) {
			if (ServiceConstants.RULED_OUT.equalsIgnoreCase(dtoStage.getAlgCdAllegDisposition())) {

				/** Create and add the role removal paragraph primary group to the formDataGroupList */  // parent group cfiv3373/3273
				formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_ROLE_REMOVAL, FormConstants.EMPTY_STRING));
				/** Create and add the role removal letter primary group to the formDataGroupList */  // cfiv3380/cfiv3280
				FormDataGroupDto rrlGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RRL, FormConstants.EMPTY_STRING);
				formDataGroupList.add(rrlGroupDto);
				/** Create and set the bookmarks as list to the primary group */
				rrlGroupDto.setBookmarkDtoList(Arrays.asList(
						createBookmark(BookmarkConstants.RRL_INTAKE_DATE, ObjectUtils.isEmpty(popFormDto.getCpsInvstDetailStageIdOutDto().getDtCPSInvstDtlIntake())
								? ServiceConstants.EMPTY_STR : DateUtils.stringDt(popFormDto.getCpsInvstDetailStageIdOutDto().getDtCPSInvstDtlIntake())) // c sub groups cfiv3382/cfiv3282,
						, createBookmark(BookmarkConstants.RRL_CASE_NM, popFormDto.getGenericCaseInfoDto().getNmCase()) // cfiv3383/cfiv3283 are not needed in the java
						, createBookmark(BookmarkConstants.RRL_CASE_ID, popFormDto.getGenericCaseInfoDto().getIdCase())
						, createBookmark(BookmarkConstants.RRL_EMAIL_TXT, popFormDto.getPersonEmailDto().getTxtEmail()) // default email text in subgroup cfiv3385/cfiv3285
						, createBookmark(BookmarkConstants.UE_GROUPID, popFormDto.getUlIdPerson()) // field id for editable email input
				));

				/** Create and add the subgroup to the primary group */// sub group cfiv3384/cfiv3284
				List<FormDataGroupDto> rrlGroupDtoList = new ArrayList<>();
				for (CpsInvAllegDto dtoVic : popFormDto.getAllegationsList()) {
					FormDataGroupDto rrlListGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RRL_LIST, FormGroupsConstants.TMPLAT_RRL);
					rrlGroupDtoList.add(rrlListGroupDto);
					/** Create and set the bookmarks as list to the subgroup */// decode differently for english vs spanish
					rrlListGroupDto.setBookmarkDtoList(Arrays.asList(
							createBookmark(BookmarkConstants.RRL_LIST_AP, dtoVic.getNmPerpFull())
							, createBookmark(BookmarkConstants.RRL_LIST_VICTIM, dtoVic.getNmVicFull())
							, createBookmark(BookmarkConstants.RRL_LIST_ALLEG, dtoVic.getDecodeAllegType())
					));
				}
				rrlGroupDto.setFormDataGroupList(rrlGroupDtoList);
			}
		}



		// parent group cfiv3310/cfiv3210
		AddressDto dtoAddr = popFormDto.getAddressDto();
		FormDataGroupDto addresseeAddrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESSEE_ADDRESS,
				FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> addresseeAddrGroupDtoList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkaddresseeAddrList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkAddresseeAddrZip = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_ZIP,
				dtoAddr.getAddrZip());
		bookmarkaddresseeAddrList.add(bookmarkAddresseeAddrZip);
		BookmarkDto bookmarkAddresseeAddrLn1 = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_LINE_1,
				dtoAddr.getAddrPersAddrStLn1());
		bookmarkaddresseeAddrList.add(bookmarkAddresseeAddrLn1);
		BookmarkDto bookmarkAddresseeAddrCity = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_CITY,
				dtoAddr.getAddrCity());
		bookmarkaddresseeAddrList.add(bookmarkAddresseeAddrCity);
		BookmarkDto bookmarkAddresseeAddrState = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_STATE,
				dtoAddr.getCdAddrState());
		bookmarkaddresseeAddrList.add(bookmarkAddresseeAddrState);
		addresseeAddrGroupDto.setBookmarkDtoList(bookmarkaddresseeAddrList);

		// sub group cfzz3310/cfzz3210
		if (StringUtils.isNotBlank(dtoAddr.getAddrPersAddrStLn2())) {
			FormDataGroupDto street2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_STREET2,
					FormGroupsConstants.TMPLAT_ADDRESSEE_ADDRESS);
			List<BookmarkDto> bookmarkStreet2List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkStreetLn2 = createBookmark(BookmarkConstants.STREET_LN_2,
					dtoAddr.getAddrPersAddrStLn2());
			bookmarkStreet2List.add(bookmarkStreetLn2);
			street2GroupDto.setBookmarkDtoList(bookmarkStreet2List);

			addresseeAddrGroupDtoList.add(street2GroupDto);
		}

		addresseeAddrGroupDto.setFormDataGroupList(addresseeAddrGroupDtoList);
		formDataGroupList.add(addresseeAddrGroupDto);

		// parent group cfiv3320/cfiv3220 sub group cfiv3321/cfiv3221
		for (CpsInvAllegDto dto : popFormDto.getAllegationsList()) {
			FormDataGroupDto abuseListGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ABUSE_LIST, FormConstants.EMPTY_STRING);
			formDataGroupList.add(abuseListGroupDto);
			// decode differently for english/spanish versions
			abuseListGroupDto.setBookmarkDtoList(Arrays.asList(
					createBookmark(BookmarkConstants.ABUSE_LIST_FINDING, dto.getDecodeAllegDisp())
					, createBookmark(BookmarkConstants.ABUSE_LIST_PERP, dto.getNmPerpFull())
					, createBookmark(BookmarkConstants.ABUSE_LIST_VICTIM, dto.getNmVicFull())
					, createBookmark(BookmarkConstants.ABUSE_LIST_ABUSE, dto.getDecodeAllegType())
			));
		}

		// parent group cfiv3350/cfiv3250
		FormDataGroupDto workerAddrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER_ADDRESS,
				FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> workerAddrGroupDtoList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkWorkerAddrList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkWorkerPhone = createBookmark(BookmarkConstants.WORKER_PHONE,
				TypeConvUtil.formatPhone(popFormDto.getEmployeePersPhNameDto().getNbrPhone()));
		bookmarkWorkerAddrList.add(bookmarkWorkerPhone);
		BookmarkDto bookmarkWorkerAddrCity = createBookmark(BookmarkConstants.WORKER_ADDRESS_CITY,
				popFormDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
		bookmarkWorkerAddrList.add(bookmarkWorkerAddrCity);
		BookmarkDto bbokmarkWorkerAddrLn1 = createBookmark(BookmarkConstants.WORKER_ADDRESS_LINE_1,
				popFormDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
		bookmarkWorkerAddrList.add(bbokmarkWorkerAddrLn1);
		BookmarkDto bookmarkWorkerAddrZip = createBookmark(BookmarkConstants.WORKER_ADDRESS_ZIP,
				popFormDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
		bookmarkWorkerAddrList.add(bookmarkWorkerAddrZip);
		workerAddrGroupDto.setBookmarkDtoList(bookmarkWorkerAddrList);

		// sub group cfzz3350/cfzz3250
		if (StringUtils.isNotBlank(popFormDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2())) {
			FormDataGroupDto workerStreet2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER_STREET2,
					FormGroupsConstants.TMPLAT_WORKER_ADDRESS);
			List<BookmarkDto> bookmarkWorkerStreet2List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkWorkerStreet2 = createBookmark(BookmarkConstants.WORKER_STREET_LN_2,
					popFormDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
			bookmarkWorkerStreet2List.add(bookmarkWorkerStreet2);
			workerStreet2GroupDto.setBookmarkDtoList(bookmarkWorkerStreet2List);

			workerAddrGroupDtoList.add(workerStreet2GroupDto);
		}

		// sub group cfzz3351/cfzz3251
		if (StringUtils.isNotBlank(popFormDto.getEmployeePersPhNameDto().getNbrPhoneExtension())) {
			FormDataGroupDto workerExtGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EXT,
					FormGroupsConstants.TMPLAT_WORKER_ADDRESS);
			List<BookmarkDto> bookmarkWorkerExtList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkWorkerExt = createBookmark(BookmarkConstants.WORKER_PHONE_EXTENSION,
					popFormDto.getEmployeePersPhNameDto().getNbrPhoneExtension());
			bookmarkWorkerExtList.add(bookmarkWorkerExt);
			workerExtGroupDto.setBookmarkDtoList(bookmarkWorkerExtList);

			workerAddrGroupDtoList.add(workerExtGroupDto);
		}
		workerAddrGroupDto.setFormDataGroupList(workerAddrGroupDtoList);
		formDataGroupList.add(workerAddrGroupDto);

		/*
		 * Independencies in group
		 */

		// cfiv3303/cfiv3203
		FormDataGroupDto formdatagroupHeader = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_DIRECTOR,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkAddResourcfiv3303List = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkChildSuffix = createBookmark(BookmarkConstants.HEADER_DIRECTOR_TITLE,
				popFormDto.getCodesTablesDto().getaDecode());
		bookmarkAddResourcfiv3303List.add(bookmarkChildSuffix);
		BookmarkDto bookmarkChildSuffix2 = createBookmark(BookmarkConstants.HEADER_DIRECTOR_NAME,
				popFormDto.getCodesTablesDto().getbDecode());
		bookmarkAddResourcfiv3303List.add(bookmarkChildSuffix2);
		formdatagroupHeader.setBookmarkDtoList(bookmarkAddResourcfiv3303List);
		formDataGroupList.add(formdatagroupHeader);

		// cfzco00
		for (NamePrimayEndDateOutDto dto : popFormDto.getNamePrimayEndDateOutDto()) {
			if (StringUtils.isNotBlank(dto.getCdNameSuffix())) {
				FormDataGroupDto formdatagroupComma = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(formdatagroupComma);
				FormDataGroupDto formdatagroupComma2 = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(formdatagroupComma2);
			}
		}
		if (StringUtils.isNotBlank(popFormDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
			FormDataGroupDto formdatagroupComma3 = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_3,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(formdatagroupComma3);
		}

		for (StageProgDto stageProgDto : popFormDto.getStageProgDto()) {
			// cfiv3340/cfiv3240
			if (CodesConstant.CDISPSTN_UTD.equals(popFormDto.getCpsInvstDetailStageIdOutDto().getCdCpsOverallDisptn()) // artf246434,artf238090
					|| ServiceConstants.COMMA_LENGTH_MINUS.equalsIgnoreCase(stageProgDto.getIndStageProgClose())) {
				FormDataGroupDto willOfferGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WILL_OFFER,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(willOfferGroupDto);
			}
			// cfiv3341/cfiv3241
			else if (ServiceConstants.STR_ZERO_VAL.equalsIgnoreCase(stageProgDto.getIndStageProgClose())) {
				FormDataGroupDto wontOfferGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WILL_NOT,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(wontOfferGroupDto);
			}
		}

		// cfiv3312/cfiv3212
		CpsInvstDetailStageIdOutDto cpsInvstDetailStageIdOutDto = popFormDto.getCpsInvstDetailStageIdOutDto();
		if (!ServiceConstants.ADM_CODE.equals(cpsInvstDetailStageIdOutDto.getCdCpsOverallDisptn())) {
			FormDataGroupDto formdatagroupComma = createFormDataGroup(FormGroupsConstants.TMPLAT_NONPARENT_OPENING,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkAddResourcfiv3312List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkReportDate = createBookmark(BookmarkConstants.ABNEG_REPORTED_DATE,
					DateUtils.stringDt(cpsInvstDetailStageIdOutDto.getDtCPSInvstDtlIntake()));
			bookmarkAddResourcfiv3312List.add(bookmarkReportDate);
			formdatagroupComma.setBookmarkDtoList(bookmarkAddResourcfiv3312List);
			formDataGroupList.add(formdatagroupComma);
		}
		// cfiv3313/cfiv3213
		else {
			FormDataGroupDto formdatagroupComma = createFormDataGroup(FormGroupsConstants.TMPLAT_NONPARENT_ADM_OPENING,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkAddResourcfiv3313List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkReportDate = createBookmark(BookmarkConstants.ABNEG_REPORTED_DATE,
					cpsInvstDetailStageIdOutDto.getDtCPSInvstDtlIntake());
			bookmarkAddResourcfiv3313List.add(bookmarkReportDate);
			formdatagroupComma.setBookmarkDtoList(bookmarkAddResourcfiv3313List);
			formDataGroupList.add(formdatagroupComma);
		}
			//ALM Defect# 18556- School perp INV RTB language - ENGLISH & SPANISH letters
			boolean isPersonRtb = false;
			for (AllegationWithVicDto dispDto : popFormDto.getAllegationWithVicDto()) {
				// cfiv3359/cfiv3259 --CLSSB0D
				if (ServiceConstants.ADM_CODE.equalsIgnoreCase(dispDto.getaCdAllegDisposition())) {
					FormDataGroupDto findingAdmDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FINDING_ADM, FormConstants.EMPTY_STRING);
					formDataGroupList.add(findingAdmDto);
					findingAdmDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.ADMINISTRATIVE_CLOSURE, dispDto.getAlgCdAllegDisposition())));
				}
				// cfiv3358/cfiv3258 --CLSSB0D
				else if (ServiceConstants.RULED_OUT.equalsIgnoreCase(dispDto.getaCdAllegDisposition())) {
					FormDataGroupDto findingRoDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FINDING_RO, FormConstants.EMPTY_STRING);
					formDataGroupList.add(findingRoDto);
					findingRoDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.RULED_OUT, dispDto.getAlgCdAllegDisposition())));
				}

				// cfiv3357/cfiv3257 --CLSSB0D
				else if (ServiceConstants.REASON_TO_BELIEVE.equalsIgnoreCase(dispDto.getaCdAllegDisposition())) {

					//ALM Defect# 18556- School INV RTB letter language - ENGLISH & SPANISH
					//checks if the person selected to generate a letter is an AP or not
					isPersonRtb = true;

					FormDataGroupDto findingRtbDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FINDING_RTB, FormConstants.EMPTY_STRING);
					formDataGroupList.add(findingRtbDto);
					findingRtbDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.REASON_TO_BELIEVE, dispDto.getAlgCdAllegDisposition())));
				}

				// cfiv3356/cfiv3256 --CLSSB0D
				else if (ServiceConstants.UNABLE_TO_DETRMN_DISP.equalsIgnoreCase(dispDto.getaCdAllegDisposition())) {
					FormDataGroupDto findingUtdDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FINDING_UTD, FormConstants.EMPTY_STRING);
					formDataGroupList.add(findingUtdDto);
					findingUtdDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.UNABLE_TO_DETERMINE, dispDto.getAlgCdAllegDisposition())));
				}
			}
			if(isPersonRtb) {
				//ALM Defect# 16080- Closing LTR missing a paragraph
				//Added new bookmark for missing text as per the requirement
				FormDataGroupDto overallRTBGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ANY_RTB,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(overallRTBGroupDto);

				// parent group cfiv3375/cfiv3275
				FormDataGroupDto reviewRightGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REVIEW_RIGHT,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(reviewRightGroupDto);

				// sub group cfiv3391/cfiv3291 (c parent group cfiv3390/cfiv3290 not needed by java)
				FormDataGroupDto reqAriGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ARI,
						FormConstants.EMPTY_STRING);
				/** Create and set the list of fields to the subgroup */
				reqAriGroupDto.setBookmarkDtoList(Arrays.asList(
						createBookmark(BookmarkConstants.ARI_SYSDATE,
								DateUtils.stringDt(popFormDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate()))
						, createBookmark(BookmarkConstants.ARI_ADDR, ServiceConstants.ARI_ADDR_TEXT)
						, createBookmark(BookmarkConstants.ARI_CASENAME, popFormDto.getGenericCaseInfoDto().getNmCase())
						, createBookmark(BookmarkConstants.ARI_CASENUMBER, popFormDto.getGenericCaseInfoDto().getIdCase())
						, createBookmark(BookmarkConstants.ARI_STAGE_NUMBER, popFormDto.getGenericCaseInfoDto().getIdStage())
				));
				formDataGroupList.add(reqAriGroupDto);

				// parent group cfiv3372/cfiv3272
				StagePersonLinkRecordOutDto stagePersonLinkRecordOutDto = popFormDto.getStagePersonLinkRecordOutDto();
				if (ServiceConstants.SCHOOL_PERSONNEL.equalsIgnoreCase(stagePersonLinkRecordOutDto.getCdStagePersRelInt())) {
					FormDataGroupDto schoolInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SCHOOL_INVESTIGATION,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(schoolInvGroupDto);
				}
//			}
		}

		// only for spanish version
		if (cfiv3300.equalsIgnoreCase(popFormDto.getFormName())) {
			// parent group cfiv3316
			if (ServiceConstants.FEMALE.equalsIgnoreCase(popFormDto.getPersonGenderSpanishDto().getCdPersonSex())) {
				FormDataGroupDto salutation2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SALUTATION_2,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(salutation2GroupDto);
			}

			// parent group cfiv3315

			else if (ServiceConstants.MALE.equalsIgnoreCase(popFormDto.getPersonGenderSpanishDto().getCdPersonSex())) {
				FormDataGroupDto salutation1GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SALUTATION_1,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(salutation1GroupDto);
			}
		}

		for (AllegationDto dto : popFormDto.getAllegationDtoList()) {
			// parent group cfiv336a/cfiv326a
			if (ServiceConstants.SXTR.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto sxtrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SXTR,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSxtrList = new ArrayList<BookmarkDto>();
				// decode differently for spanish/english versions
				BookmarkDto bookmarkSxtrAlleg = createBookmarkWithCodesTable(BookmarkConstants.SXTR_ALLEGATION,
						dto.getCdAllegType(), cfiv3200.equalsIgnoreCase(popFormDto.getFormName())
								? CodesConstant.CCLICALT : CodesConstant.CABTPSP);
				bookmarkSxtrList.add(bookmarkSxtrAlleg);
				sxtrGroupDto.setBookmarkDtoList(bookmarkSxtrList);
				formDataGroupList.add(sxtrGroupDto);
			}

			// parent group cfiv3369/cfiv3269
			else if (ServiceConstants.LBTR.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto lbtrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_LBTR,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkLbtrList = new ArrayList<BookmarkDto>();

				// decode differently for spanish/english versions
				BookmarkDto bookmarkLbtrAlleg = createBookmarkWithCodesTable(BookmarkConstants.LBTR_ALLEGATION,
						dto.getCdAllegType(), cfiv3200.equalsIgnoreCase(popFormDto.getFormName())
								? CodesConstant.CCLICALT : CodesConstant.CABTPSP);
				bookmarkLbtrList.add(bookmarkLbtrAlleg);
				lbtrGroupDto.setBookmarkDtoList(bookmarkLbtrList);
				formDataGroupList.add(lbtrGroupDto);
			}

			// parent group cfiv3368/cfiv3268
			else if (ServiceConstants.SXAB.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto sxabGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SXAB,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSxabList = new ArrayList<BookmarkDto>();
				// decode differently for spanish/english versions
				BookmarkDto bookmarkSxabAlleg = createBookmarkWithCodesTable(BookmarkConstants.SXAB_ALLEGATION,
						dto.getCdAllegType(), cfiv3200.equalsIgnoreCase(popFormDto.getFormName())
								? CodesConstant.CCLICALT : CodesConstant.CABTPSP);
				bookmarkSxabList.add(bookmarkSxabAlleg);
				sxabGroupDto.setBookmarkDtoList(bookmarkSxabList);
				formDataGroupList.add(sxabGroupDto);
			}

			// parent group cfiv3367/cfiv3267
			else if (ServiceConstants.RAPR.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto raprGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RAPR,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRaprList = new ArrayList<BookmarkDto>();
				// decode differently for spanish/english versions
				BookmarkDto bookmarkRaprAlleg = createBookmarkWithCodesTable(BookmarkConstants.RAPR_ALLEGATION,
						dto.getCdAllegType(), cfiv3200.equalsIgnoreCase(popFormDto.getFormName())
								? CodesConstant.CCLICALT : CodesConstant.CABTPSP);
				bookmarkRaprList.add(bookmarkRaprAlleg);
				raprGroupDto.setBookmarkDtoList(bookmarkRaprList);
				formDataGroupList.add(raprGroupDto);
			}

			// parent group cfiv3366/cfiv3266
			else if (ServiceConstants.PHNG.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto phngGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PHNG,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPhngList = new ArrayList<BookmarkDto>();
				// decode differently for spanish/english versions
				BookmarkDto bookmarkPhngAlleg = createBookmarkWithCodesTable(BookmarkConstants.PHNG_ALLEGATION,
						dto.getCdAllegType(), cfiv3200.equalsIgnoreCase(popFormDto.getFormName())
								? CodesConstant.CCLICALT : CodesConstant.CABTPSP);
				bookmarkPhngList.add(bookmarkPhngAlleg);
				phngGroupDto.setBookmarkDtoList(bookmarkPhngList);
				formDataGroupList.add(phngGroupDto);
			}

			// parent group cfiv3365/cfiv3265
			else if (ServiceConstants.PHAB.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto phabGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PHAB,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPhabList = new ArrayList<BookmarkDto>();
				// decode differently for spanish/english versions
				BookmarkDto bookmarkPhabAlleg = createBookmarkWithCodesTable(BookmarkConstants.PHAB_ALLEGATION,
						dto.getCdAllegType(), cfiv3200.equalsIgnoreCase(popFormDto.getFormName())
								? CodesConstant.CCLICALT : CodesConstant.CABTPSP);
				bookmarkPhabList.add(bookmarkPhabAlleg);
				phabGroupDto.setBookmarkDtoList(bookmarkPhabList);
				formDataGroupList.add(phabGroupDto);
			}

			// parent group cfiv3364/cfiv3264
			else if (ServiceConstants.NSUP.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto nsupGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NSUP,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkNsupList = new ArrayList<BookmarkDto>();
				// decode differently for spanish/english versions
				BookmarkDto bookmarkNsupAlleg = createBookmarkWithCodesTable(BookmarkConstants.NSUP_ALLEGATION,
						dto.getCdAllegType(), cfiv3200.equalsIgnoreCase(popFormDto.getFormName())
								? CodesConstant.CCLICALT : CodesConstant.CABTPSP);
				bookmarkNsupList.add(bookmarkNsupAlleg);
				nsupGroupDto.setBookmarkDtoList(bookmarkNsupList);
				formDataGroupList.add(nsupGroupDto);
			}

			// parent group cfiv3363/cfiv3263
			else if (ServiceConstants.MDNG.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto mdngGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MDNG,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkMdngList = new ArrayList<BookmarkDto>();
				// decode differently for spanish/english versions
				BookmarkDto bookmarkMdngAlleg = createBookmarkWithCodesTable(BookmarkConstants.MDNG_ALLEGATION,
						dto.getCdAllegType(), cfiv3200.equalsIgnoreCase(popFormDto.getFormName())
								? CodesConstant.CCLICALT : CodesConstant.CABTPSP);
				bookmarkMdngList.add(bookmarkMdngAlleg);
				mdngGroupDto.setBookmarkDtoList(bookmarkMdngList);
				formDataGroupList.add(mdngGroupDto);
			}

			// parent group cfiv3362/cfiv3262
			else if (ServiceConstants.EMAB.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto emabGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EMAB,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkEmabList = new ArrayList<BookmarkDto>();
				// decode differently for spanish/english versions
				BookmarkDto bookmarkEmabAlleg = createBookmarkWithCodesTable(BookmarkConstants.EMAB_ALLEGATION,
						dto.getCdAllegType(), cfiv3200.equalsIgnoreCase(popFormDto.getFormName())
								? CodesConstant.CCLICALT : CodesConstant.CABTPSP);
				bookmarkEmabList.add(bookmarkEmabAlleg);
				emabGroupDto.setBookmarkDtoList(bookmarkEmabList);
				formDataGroupList.add(emabGroupDto);
			}

			// parent group cfiv3361/cfiv3261
			else if (ServiceConstants.ABAN.equalsIgnoreCase(dto.getCdAllegType())) {
				FormDataGroupDto abanGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ABAN,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkAbanList = new ArrayList<BookmarkDto>();
				// decode differently for spanish/english versions
				BookmarkDto bookmarkAbanAlleg = createBookmarkWithCodesTable(BookmarkConstants.ABAN_ALLEGATION,
						dto.getCdAllegType(), cfiv3200.equalsIgnoreCase(popFormDto.getFormName())
								? CodesConstant.CCLICALT : CodesConstant.CABTPSP);
				bookmarkAbanList.add(bookmarkAbanAlleg);
				abanGroupDto.setBookmarkDtoList(bookmarkAbanList);
				formDataGroupList.add(abanGroupDto);
			}
		}


		/*
		 * Populating the non form group data into prefill data. !!bookmarks
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// CSEC02D
		BookmarkDto bookmarkSysDate = createBookmark(BookmarkConstants.SYSTEM_DATE,
				DateUtils.stringDt(popFormDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate()));
		bookmarkNonFrmGrpList.add(bookmarkSysDate);

		BookmarkDto bookmarkCaseNum = createBookmark(BookmarkConstants.CASE_NUMBER,
				popFormDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkCaseNum);

		// CCMN40D
		//Fixed Warranty Defect#12004 Issue to fix duplicate bookmark creation
		if(!ObjectUtils.isEmpty(popFormDto.getNamePrimayEndDateOutDto())) {
			BookmarkDto bookmarkAddrNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.ADDRESSEE_NAME_SUFFIX,
					popFormDto.getNamePrimayEndDateOutDto().get(0).getCdNameSuffix(), CodesConstant.CSUFFIX2);
			bookmarkNonFrmGrpList.add(bookmarkAddrNameSuffix);

			BookmarkDto bookmarkDearNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.DEAR_NAME_SUFFIX,
					popFormDto.getNamePrimayEndDateOutDto().get(0).getCdNameSuffix(), CodesConstant.CSUFFIX2);
			bookmarkNonFrmGrpList.add(bookmarkDearNameSuffix);

			BookmarkDto bookmarkNameFirst = createBookmark(BookmarkConstants.DEAR_NAME_FIRST,
					popFormDto.getNamePrimayEndDateOutDto().get(0).getNmNameFirst());
			bookmarkNonFrmGrpList.add(bookmarkNameFirst);

			BookmarkDto bookmarkAddrNameFrist = createBookmark(BookmarkConstants.ADDRESSEE_NAME_FIRST,
					popFormDto.getNamePrimayEndDateOutDto().get(0).getNmNameFirst());
			bookmarkNonFrmGrpList.add(bookmarkAddrNameFrist);

			BookmarkDto bookmarkDearNameLast = createBookmark(BookmarkConstants.DEAR_NAME_LAST,
					popFormDto.getNamePrimayEndDateOutDto().get(0).getNmNameLast());
			bookmarkNonFrmGrpList.add(bookmarkDearNameLast);

			BookmarkDto bookmarkAddrNameLast = createBookmark(BookmarkConstants.ADDRESSEE_NAME_LAST,
					popFormDto.getNamePrimayEndDateOutDto().get(0).getNmNameLast());
			bookmarkNonFrmGrpList.add(bookmarkAddrNameLast);

			BookmarkDto bookmarkAddrNameMiddle = createBookmark(BookmarkConstants.ADDRESSEE_NAME_MIDDLE,
					popFormDto.getNamePrimayEndDateOutDto().get(0).getNmNameMiddle());
			bookmarkNonFrmGrpList.add(bookmarkAddrNameMiddle);

			BookmarkDto bookmarkDearNameMiddle = createBookmark(BookmarkConstants.DEAR_NAME_MIDDLE,
					popFormDto.getNamePrimayEndDateOutDto().get(0).getNmNameMiddle());
			bookmarkNonFrmGrpList.add(bookmarkDearNameMiddle);
		}
		// CSEC01D
		BookmarkDto bookmarkWorkerNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.WORKER_NAME_SUFFIX,
				popFormDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameSuffix);

		BookmarkDto bookmarkWorkerNameFirst = createBookmark(BookmarkConstants.WORKER_NAME_FIRST,
				popFormDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameFirst);

		BookmarkDto bookmarkWorkerNameLast = createBookmark(BookmarkConstants.WORKER_NAME_LAST,
				popFormDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameLast);

		BookmarkDto bookmarkWorkerNameMiddle = createBookmark(BookmarkConstants.WORKER_NAME_MIDDLE,
				popFormDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameMiddle);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}
}
