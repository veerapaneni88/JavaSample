/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jul 26, 2018- 11:50:41 AM
 *
 */
package us.tx.state.dfps.service.forms.util;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.FamilyPlanReqrdActn;
import us.tx.state.dfps.familyplan.dto.FamilyPlanActnRsrcDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanDtlEvalDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanNeedsDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanPartcpntDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanReqrdActnsDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanStrengthsDto;
import us.tx.state.dfps.familyplan.response.FamilyPlanDtlEvalRes;
import us.tx.state.dfps.fsna.dto.CpsFsnaDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.dto.CodeAttributes;
import us.tx.state.dfps.service.lookup.service.LookupService;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.stageutility.service.StageUtilityService;

@Component
public class FamilyPlanFormPrefillData extends DocumentServiceUtil {

	public static final String YES = "Yes";
	public static final String NO = "No";
	public static final String FAMILY_REUNIFICATION = "Family Reunification";

	@Autowired
	LookupService lookupService;


	public String getTffEvidenceBasedServices(FamilyPlanReqrdActnsDto familyPlanReqrdActn) {
		List<String> tffEvidenceBasedServicesList = new ArrayList<String>();
		if (Objects.nonNull(familyPlanReqrdActn) && Objects.nonNull(familyPlanReqrdActn.getTffevidenceBasedSvcs())) {
			tffEvidenceBasedServicesList.addAll(Objects.nonNull(familyPlanReqrdActn.getTffevidenceBasedSvcs()) ? Arrays.asList(familyPlanReqrdActn.getTffevidenceBasedSvcs().split(",", -1)) : new ArrayList<String>());

		}
		List<String> selectedTffEvidenceSvcsValueList = new ArrayList();
		if (CollectionUtils.isNotEmpty(tffEvidenceBasedServicesList)) {
			HashMap<String, TreeMap<String, CodeAttributes>> codesMap = lookupService.getCodes();
			if (Objects.nonNull(codesMap)) {
				TreeMap<String, CodeAttributes> codeAttributesTreeMap = codesMap.get(us.tx.state.dfps.common.web.CodesConstant.CSVCCODE);
				selectedTffEvidenceSvcsValueList = tffEvidenceBasedServicesList.stream().map(x ->{
							return codeAttributesTreeMap.get(x).getDecode();
						}
				).collect(Collectors.toList());

			}
			return String.join(",", selectedTffEvidenceSvcsValueList);
		}
		return "";
	}
	
	/** The stage utility service. */
	@Autowired
	StageUtilityService stageUtilityService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.forms.util.DocumentServiceUtil#
	 * returnPrefillData( java.lang.Object)
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		FamilyPlanDtlEvalRes familyPlanDtlEvalRes = (FamilyPlanDtlEvalRes) parentDtoobj;
		if (ObjectUtils.isEmpty(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto())) {
			familyPlanDtlEvalRes.setFamilyPlanDtlEvalDto(new FamilyPlanDtlEvalDto());
		}

		if (ObjectUtils.isEmpty(familyPlanDtlEvalRes.getFamilyPlanPartcpntDto())) {
			familyPlanDtlEvalRes.setFamilyPlanPartcpntDto(new ArrayList<FamilyPlanPartcpntDto>());
		}

		if (ObjectUtils.isEmpty(familyPlanDtlEvalRes.getCpsFsnaDto())) {
			familyPlanDtlEvalRes.setCpsFsnaDto(new CpsFsnaDto());
		}

		if (ObjectUtils.isEmpty(familyPlanDtlEvalRes.getPersons())) {
			familyPlanDtlEvalRes.setPersons(new ArrayList<PersonListDto>());
		}

		if (ObjectUtils.isEmpty(familyPlanDtlEvalRes.getChildDomainCodes())) {
			familyPlanDtlEvalRes.setChildDomainCodes(new ArrayList<>());
		}
		if (ObjectUtils.isEmpty(familyPlanDtlEvalRes.getParentDomainCodes())) {
			familyPlanDtlEvalRes.setParentDomainCodes(new ArrayList<>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		String cdFgdmConference = familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getCdFgdmConference();

		if (CodesConstant.CFGDMCON_20.equals(cdFgdmConference) || CodesConstant.CFGDMCON_30.equals(cdFgdmConference)) {
			FormDataGroupDto templateHeader = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(templateHeader);
		}
		;
		// create the permanency goal group and set prefill data
		for (FamilyPlanPartcpntDto familyPlanPartcpntDto : familyPlanDtlEvalRes.getFamilyPlanPartcpntDto()) {
			if (familyPlanPartcpntDto.getIndPartcpntType().equalsIgnoreCase("C")) {
				FormDataGroupDto tmPermGoalFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PERMANENCY_GOAL, FormConstants.EMPTY_STRING);
				List<BookmarkDto> permGoalBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto addBookmarkChildName = createBookmark(BookmarkConstants.CHILD_FULL_NAME,
						familyPlanPartcpntDto.getNmParticipant());

				BookmarkDto addBookmarkCdPermGoal = createBookmarkWithCodesTable(BookmarkConstants.PRIMARY_PERM_GOAL,
						familyPlanPartcpntDto.getCdPrmryPermncyGoal(), CodesConstant.CCPPRMGL);
				
				if(ObjectUtils.isEmpty(familyPlanPartcpntDto.getCdPrmryPermncyGoal())) {
					//Fix DEFECT 13134
					if (!ObjectUtils.isEmpty(familyPlanPartcpntDto.getIdPerson())
							&& familyPlanDtlEvalRes.getCdStage().equalsIgnoreCase("FRE")
							&& stageUtilityService.isChildInSubStage(familyPlanPartcpntDto.getIdPerson())) {
						addBookmarkCdPermGoal = createBookmark(BookmarkConstants.PRIMARY_PERM_GOAL,
								FAMILY_REUNIFICATION);
					} else {
						addBookmarkCdPermGoal = createBookmark(BookmarkConstants.PRIMARY_PERM_GOAL, "Not Applicable");
					}
				}
				BookmarkDto addBookmarkDtAchieve = createBookmark(BookmarkConstants.DT_ACHIEVE,
						TypeConvUtil.formDateFormat(familyPlanPartcpntDto.getDtAchevPrmryPermncyGoal()));

				if (familyPlanDtlEvalRes.getCdStage().equalsIgnoreCase("FSU")) {
					BookmarkDto addBookmarkConGoal = createBookmarkWithCodesTable(BookmarkConstants.CONCURRENT_PERM_GOAL,
							familyPlanPartcpntDto.getCdCncrntPermncyGoal(), CodesConstant.CCPPRMGL);
					permGoalBookmarkList.add(addBookmarkConGoal);
					BookmarkDto addBookmarkDtCon = createBookmark(BookmarkConstants.DT_CON_ACHIEVE,
							TypeConvUtil.formDateFormat(familyPlanPartcpntDto.getDtAchevCncrntPermncyGoal()));
					permGoalBookmarkList.add(addBookmarkDtCon);
				}
				permGoalBookmarkList.add(addBookmarkDtAchieve);
				permGoalBookmarkList.add(addBookmarkCdPermGoal);
				permGoalBookmarkList.add(addBookmarkChildName);

				tmPermGoalFrmDataGrpDto.setBookmarkDtoList(permGoalBookmarkList);
				formDataGroupList.add(tmPermGoalFrmDataGrpDto);
			}
		}

		// create the additional participant form group and set prefill data
		boolean addPartExists = false;
		List<FormDataGroupDto> tempList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto tmplatAddSection = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDITIONAL_PART_SECTION,
				FormConstants.EMPTY_STRING);
		if (!ObjectUtils.isEmpty(familyPlanDtlEvalRes.getFamilyPlanPartcpntDto())) {
			addPartExists = !ObjectUtils.isEmpty(familyPlanDtlEvalRes.getFamilyPlanPartcpntDto().stream()
					.filter(x -> x.getIndPartcpntType().equals("A")).findAny().orElse(null));
		}
		for (FamilyPlanPartcpntDto familyPlanPartcpntDto : familyPlanDtlEvalRes.getFamilyPlanPartcpntDto()) {
			if (familyPlanPartcpntDto.getIndPartcpntType().equalsIgnoreCase("A")) {

				FormDataGroupDto tmplatAddPartFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_ADDITIONAL_PART, FormGroupsConstants.TMPLAT_ADDITIONAL_PART_SECTION);
				List<BookmarkDto> addPartcpnBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto addBookmarkPartcpnName = createBookmark(BookmarkConstants.PART_FULL_NAME,
						familyPlanPartcpntDto.getNmParticipant());

				BookmarkDto addBookmarkReltionship = createBookmarkWithCodesTable(BookmarkConstants.RELATIONSHIP,
						familyPlanPartcpntDto.getRelationship(), CodesConstant.CRPTRINT);

				addPartcpnBookmarkList.add(addBookmarkReltionship);
				addPartcpnBookmarkList.add(addBookmarkPartcpnName);
				tmplatAddPartFrmDataGrpDto.setBookmarkDtoList(addPartcpnBookmarkList);
				tempList.add(tmplatAddPartFrmDataGrpDto);
			}
		}
		if (addPartExists) {
			tmplatAddSection.setFormDataGroupList(tempList);
			formDataGroupList.add(tmplatAddSection);
		}

		// create the child strength and needs form group and set prefill data
		for (FamilyPlanPartcpntDto familyPlanPartcpntDto : familyPlanDtlEvalRes.getFamilyPlanPartcpntDto()) {
			if (familyPlanPartcpntDto.getIndPartcpntType().equalsIgnoreCase("C")) {
				FormDataGroupDto tmplatChildStrengthFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CHILD_STRENGTH, FormConstants.EMPTY_STRING);
				List<BookmarkDto> childStrNeedBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto addBookmarkChildName = createBookmark(BookmarkConstants.CHILD_FULL_NAME,
						familyPlanPartcpntDto.getNmParticipant());

				FormDataGroupDto tmplatAdditionalInfoDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_ADDITIONAL_INFO, FormGroupsConstants.TMPLAT_CHILD_STRENGTH);

				List<BookmarkDto> additionalInfoBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto addBookmarkAddInfo = createBookmark(BookmarkConstants.ADDITIONAL_INFO,
						formatTextValue(familyPlanPartcpntDto.getTxtAdtnlInfo()));
				additionalInfoBookmarkList.add(addBookmarkAddInfo);

				List<FormDataGroupDto> childStrGrpLi = new ArrayList<FormDataGroupDto>();

				for (FamilyPlanStrengthsDto familyPlanStrengthsDto : familyPlanPartcpntDto
						.getFamilyPlanStrengthsDtoList()) {
					FormDataGroupDto tmplatChildStrQuesFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CHILD_QUES_ANS, FormGroupsConstants.TMPLAT_CHILD_STRENGTH);
					List<BookmarkDto> childStrQuesBookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto addBookmarkQuesOrder = createBookmark(BookmarkConstants.QUESTION_ORDER,
							familyPlanStrengthsDto.getTxtDmnDisplayOrder());
					BookmarkDto addBookmarkQuestion = createBookmarkWithCodesTable(BookmarkConstants.QUESTION,
							familyPlanStrengthsDto.getCdQuestion(), CodesConstant.FSNADOMN);
					BookmarkDto addBookmarkAnswer = createBookmarkWithCodesTable(BookmarkConstants.ANSWER,
							familyPlanStrengthsDto.getCdAnswr(), CodesConstant.FSNANSWR);
					BookmarkDto addBookmarkDesc = createBookmark(BookmarkConstants.DESCRIPTION,
							formatTextValue(familyPlanStrengthsDto.getTxtDesc()));
					childStrQuesBookmarkList.add(addBookmarkDesc);
					childStrQuesBookmarkList.add(addBookmarkAnswer);
					childStrQuesBookmarkList.add(addBookmarkQuestion);
					childStrQuesBookmarkList.add(addBookmarkQuesOrder);
					tmplatChildStrQuesFrmDataGrpDto.setBookmarkDtoList(childStrQuesBookmarkList);
					childStrGrpLi.add(tmplatChildStrQuesFrmDataGrpDto);
				}
				childStrNeedBookmarkList.add(addBookmarkChildName);

				if (!ObjectUtils.isEmpty(familyPlanPartcpntDto.getTxtAdtnlInfo())) {
					tmplatAdditionalInfoDto.setBookmarkDtoList(additionalInfoBookmarkList);
					childStrGrpLi.add(tmplatAdditionalInfoDto);
				}
				tmplatChildStrengthFrmDataGrpDto.setBookmarkDtoList(childStrNeedBookmarkList);
				tmplatChildStrengthFrmDataGrpDto.setFormDataGroupList(childStrGrpLi);
				formDataGroupList.add(tmplatChildStrengthFrmDataGrpDto);
			}
		}

		// create the parent strength and needs form group and set prefill data
		for (FamilyPlanPartcpntDto familyPlanPartcpntDto : familyPlanDtlEvalRes.getFamilyPlanPartcpntDto()) {
			if (familyPlanPartcpntDto.getIndPartcpntType().equalsIgnoreCase("P")) {
				FormDataGroupDto tmplatParentStrengthFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PARENT_STRENGTH, FormConstants.EMPTY_STRING);
				List<BookmarkDto> ParentStrNeedBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto addBookmarkParentName = createBookmark(BookmarkConstants.PARENT_FULL_NAME,
						familyPlanPartcpntDto.getNmParticipant());

				FormDataGroupDto tmplatAdditionalInfoDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_ADDITIONAL_INFO_2, FormGroupsConstants.TMPLAT_PARENT_STRENGTH);

				List<BookmarkDto> additionalInfoBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto addBookmarkAddInfo = createBookmark(BookmarkConstants.ADDITIONAL_INFO,
						formatTextValue(familyPlanPartcpntDto.getTxtAdtnlInfo()));
				additionalInfoBookmarkList.add(addBookmarkAddInfo);

				List<FormDataGroupDto> ParentStrGrpLi = new ArrayList<FormDataGroupDto>();

				for (FamilyPlanStrengthsDto familyPlanStrengthsDto : familyPlanPartcpntDto
						.getFamilyPlanStrengthsDtoList()) {
					FormDataGroupDto tmplatParentStrQuesFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_PARENT_QUES_ANS, FormGroupsConstants.TMPLAT_PARENT_STRENGTH);
					List<BookmarkDto> ParentStrQuesBookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto addBookmarkQuesOrder = createBookmark(BookmarkConstants.QUESTION_ORDER,
							familyPlanStrengthsDto.getTxtDmnDisplayOrder());
					BookmarkDto addBookmarkQuestion = createBookmarkWithCodesTable(BookmarkConstants.QUESTION,
							familyPlanStrengthsDto.getCdQuestion(), CodesConstant.FSNADOMN);
					BookmarkDto addBookmarkAnswer = createBookmarkWithCodesTable(BookmarkConstants.ANSWER,
							familyPlanStrengthsDto.getCdAnswr(), CodesConstant.FSNANSWR);
					BookmarkDto addBookmarkDesc = createBookmark(BookmarkConstants.DESCRIPTION,
							formatTextValue(familyPlanStrengthsDto.getTxtDesc()));
					ParentStrQuesBookmarkList.add(addBookmarkDesc);
					ParentStrQuesBookmarkList.add(addBookmarkAnswer);
					ParentStrQuesBookmarkList.add(addBookmarkQuestion);
					ParentStrQuesBookmarkList.add(addBookmarkQuesOrder);
					tmplatParentStrQuesFrmDataGrpDto.setBookmarkDtoList(ParentStrQuesBookmarkList);
					ParentStrGrpLi.add(tmplatParentStrQuesFrmDataGrpDto);
				}

				if (!ObjectUtils.isEmpty(familyPlanPartcpntDto.getTxtAdtnlInfo())) {
					tmplatAdditionalInfoDto.setBookmarkDtoList(additionalInfoBookmarkList);
					ParentStrGrpLi.add(tmplatAdditionalInfoDto);
				}
				ParentStrNeedBookmarkList.add(addBookmarkParentName);
				ParentStrNeedBookmarkList.add(addBookmarkAddInfo);
				tmplatParentStrengthFrmDataGrpDto.setBookmarkDtoList(ParentStrNeedBookmarkList);
				tmplatParentStrengthFrmDataGrpDto.setFormDataGroupList(ParentStrGrpLi);
				formDataGroupList.add(tmplatParentStrengthFrmDataGrpDto);
			}
		}

		// create the parent needs form group and set prefill data
		for (FamilyPlanPartcpntDto familyPlanPartcpntDto : familyPlanDtlEvalRes.getFamilyPlanPartcpntDto()) {
			if (familyPlanPartcpntDto.getIndPartcpntType().equalsIgnoreCase("P")) {
				FormDataGroupDto tmplatParentNeedFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PARENT_NEED, FormConstants.EMPTY_STRING);
				List<BookmarkDto> ParentStrNeedBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto addBookmarkParentName = createBookmark(BookmarkConstants.PARENT_FULL_NAME,
						familyPlanPartcpntDto.getNmParticipant());
				// BookmarkDto addBookmarkAddInfo =
				// createBookmark(BookmarkConstants.ADDITIONAL_INFO,
				// familyPlanPartcpntDto.getTxtAdtnlInfo());
				List<FormDataGroupDto> ParentStrGrpLi = new ArrayList<FormDataGroupDto>();

				for (FamilyPlanNeedsDto familyPlanNeedsDto : familyPlanPartcpntDto.getFamilyPlanNeedsDtoList()) {
					FormDataGroupDto tmplatParentStrQuesFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_PARENT_NEED_QUES, FormGroupsConstants.TMPLAT_PARENT_NEED);
					List<BookmarkDto> ParentStrQuesBookmarkList = new ArrayList<BookmarkDto>();
					List<FormDataGroupDto> tmpParentReqNeedGrpLi = new ArrayList<FormDataGroupDto>();
					BookmarkDto addBookmarkNeedDomain = createBookmarkWithCodesTable(BookmarkConstants.NEED_DOMAIN,
							familyPlanNeedsDto.getCdDomain(), CodesConstant.FSNADOMN);	
					// Added to Show Reason for Addition Label Only if Value is Present
					if(!ObjectUtils.isEmpty(familyPlanNeedsDto.getCdRsnForAdtn()))
					{
				
					FormDataGroupDto tmplatReasonForAddtnFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_REASON_FOR_ADDITION, FormGroupsConstants.TMPLAT_PARENT_NEED_QUES);
					List<BookmarkDto> bookmarkRsnAddtnBookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto addBookmarkRsnAddtn = createBookmarkWithCodesTable(
							BookmarkConstants.REASON_FOR_ADDITION, familyPlanNeedsDto.getCdRsnForAdtn(),
							CodesConstant.CFPRFADD);
					bookmarkRsnAddtnBookmarkList.add(addBookmarkRsnAddtn);
					tmplatReasonForAddtnFrmDataGrpDto.setBookmarkDtoList(bookmarkRsnAddtnBookmarkList);
					tmpParentReqNeedGrpLi.add(tmplatReasonForAddtnFrmDataGrpDto);
					
					}					
					BookmarkDto addBookmarkNeeds = createBookmark(BookmarkConstants.NEED,
							formatTextValue(familyPlanNeedsDto.getTxtNeedDescription()));
					ParentStrQuesBookmarkList.add(addBookmarkNeeds);
					ParentStrQuesBookmarkList.add(addBookmarkNeedDomain);

					for (FamilyPlanReqrdActnsDto familyPlanReqrdActnsDto : familyPlanNeedsDto
							.getFamilyPlanReqrdActnsDtoList()) {
						List<FormDataGroupDto> tmplatRsrcLi = new ArrayList<FormDataGroupDto>();
						FormDataGroupDto tmplatReqActionFrmDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_REQ_ACTION, FormGroupsConstants.TMPLAT_PARENT_NEED_QUES);
						List<BookmarkDto> reqActBookmarkLi = new ArrayList<BookmarkDto>();
						BookmarkDto addBookmarkRequiredAction = createBookmark(BookmarkConstants.REQUIRED_ACTION,
								formatTextValue(familyPlanReqrdActnsDto.getTxtReqrdActn()));
						if (familyPlanDtlEvalRes.getCdStage().equalsIgnoreCase("FPR")) {
							// TFF Evidence Based Services - START
							if(Objects.nonNull(familyPlanReqrdActnsDto.getIndTffService())) {
								FormDataGroupDto tempFamilyPlanReqdActnTFFEvidenceSvcsFrmDataGrpDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_EVIDENCE_BASED_SVCS,
										FormGroupsConstants.TMPLAT_REQ_ACTN);
								List<BookmarkDto> bookmarkFamilyPlanReqdActnTffEvidenceSvcsList = new ArrayList<BookmarkDto>();
								BookmarkDto bookmarkIsTffEvidenceSvc = createBookmark(BookmarkConstants.IS_TFF_EVDNC_SVC,
										familyPlanReqrdActnsDto.getIndTffService());
								BookmarkDto bookmarkTffEvidenceBasedSvcs = createBookmark(BookmarkConstants.TFF_EVDNC_BASED_SVCS,
										getTffEvidenceBasedServices(familyPlanReqrdActnsDto));

								bookmarkFamilyPlanReqdActnTffEvidenceSvcsList.add(bookmarkIsTffEvidenceSvc);
								bookmarkFamilyPlanReqdActnTffEvidenceSvcsList.add(bookmarkTffEvidenceBasedSvcs);
								tempFamilyPlanReqdActnTFFEvidenceSvcsFrmDataGrpDto.setBookmarkDtoList(bookmarkFamilyPlanReqdActnTffEvidenceSvcsList);
								tmplatRsrcLi.add(tempFamilyPlanReqdActnTFFEvidenceSvcsFrmDataGrpDto);
							}
							// TFF Evidence Based Services - END

							BookmarkDto addBookmarkCommRsrc = new BookmarkDto();
							if (FormConstants.Y.equalsIgnoreCase(familyPlanReqrdActnsDto.getIndRsrcUtlzd())) {
								addBookmarkCommRsrc = createBookmark(BookmarkConstants.COMMUNITY_RESOURCE_CARE, YES);
							} else {
								addBookmarkCommRsrc = createBookmark(BookmarkConstants.COMMUNITY_RESOURCE_CARE, NO);
							}
							reqActBookmarkLi.add(addBookmarkCommRsrc);
						}
						BookmarkDto dtCmpltdBookMark = createBookmark(BookmarkConstants.DT_CMPLT,
								TypeConvUtil.formDateFormat(familyPlanReqrdActnsDto.getDtTrgtCmpltn()));

						BookmarkDto addBookmarkPriorityStatus = createBookmarkWithCodesTable(
								BookmarkConstants.PRIORITY_STATUS, familyPlanReqrdActnsDto.getCdPriorty(),
								CodesConstant.CFPRQACP);

						BookmarkDto addBookmarkCourt = new BookmarkDto();
						if (FormConstants.Y.equalsIgnoreCase(familyPlanReqrdActnsDto.getIndCourtOrdrd())) {
							addBookmarkCourt = createBookmark(BookmarkConstants.COURT_ORDERED, YES);
						} else {
							addBookmarkCourt = createBookmark(BookmarkConstants.COURT_ORDERED, FormConstants.NO);
						}


						for (FamilyPlanActnRsrcDto familyPlanActnRsrcDto : familyPlanReqrdActnsDto
								.getFamilyPlanActnRsrcDtoList()) {

							FormDataGroupDto tmplatRrscFrmDataGrpDto = createFormDataGroup(
									FormGroupsConstants.TMPLAT_PARENT_RSRC, FormGroupsConstants.TMPLAT_REQ_ACTION);
							BookmarkDto addBookmarkNmRrsc = createBookmark(BookmarkConstants.NM_RESOURCE,
									familyPlanActnRsrcDto.getNmRsrc());
							BookmarkDto addBookmarkRsrcPhone = createBookmark(BookmarkConstants.RESOURCE_PHONE,
									TypeConvUtil.formatPhone(familyPlanActnRsrcDto.getNbrRsrcPhone()));

							BookmarkDto addBookmarkRsrcExt = createBookmark(BookmarkConstants.RESOURCE_PHONE_EXT,
									familyPlanActnRsrcDto.getNbrRsrcPhoneExt());

							BookmarkDto addBookmarkRsrcAddr1 = createBookmark(BookmarkConstants.RESOURCE_ADDRESS_1,
									familyPlanActnRsrcDto.getAddrRsrcStLn1());

							BookmarkDto addBookmarkRsrcAddr2 = createBookmark(BookmarkConstants.RESOURCE_ADDRESS_2,
									familyPlanActnRsrcDto.getAddrRsrcStLn2());

							BookmarkDto addBookmarkRsrcCity = createBookmark(BookmarkConstants.RESOURCE_CITY,
									familyPlanActnRsrcDto.getAddrRsrcCity());

							BookmarkDto addBookmarkRsrcZip = createBookmark(BookmarkConstants.RESOURCE_ZIP,
									familyPlanActnRsrcDto.getAddrRsrcZip());
							List<BookmarkDto> rsrcBookmarkLi = new ArrayList<BookmarkDto>();
							rsrcBookmarkLi.add(addBookmarkRsrcExt);
							rsrcBookmarkLi.add(addBookmarkRsrcPhone);
							rsrcBookmarkLi.add(addBookmarkRsrcZip);
							rsrcBookmarkLi.add(addBookmarkRsrcCity);
							rsrcBookmarkLi.add(addBookmarkRsrcAddr2);
							rsrcBookmarkLi.add(addBookmarkRsrcAddr1);
							rsrcBookmarkLi.add(addBookmarkNmRrsc);
							tmplatRrscFrmDataGrpDto.setBookmarkDtoList(rsrcBookmarkLi);

							tmplatRsrcLi.add(tmplatRrscFrmDataGrpDto);
						}

						reqActBookmarkLi.add(addBookmarkRequiredAction);
						reqActBookmarkLi.add(addBookmarkCourt);
						reqActBookmarkLi.add(addBookmarkPriorityStatus);
						reqActBookmarkLi.add(dtCmpltdBookMark);
						tmplatReqActionFrmDataGrpDto.setBookmarkDtoList(reqActBookmarkLi);
						tmplatReqActionFrmDataGrpDto.setFormDataGroupList(tmplatRsrcLi);
						tmpParentReqNeedGrpLi.add(tmplatReqActionFrmDataGrpDto);
					}

					tmplatParentStrQuesFrmDataGrpDto.setBookmarkDtoList(ParentStrQuesBookmarkList);
					tmplatParentStrQuesFrmDataGrpDto.setFormDataGroupList(tmpParentReqNeedGrpLi);
					ParentStrGrpLi.add(tmplatParentStrQuesFrmDataGrpDto);

				}
				ParentStrNeedBookmarkList.add(addBookmarkParentName);
				tmplatParentNeedFrmDataGrpDto.setBookmarkDtoList(ParentStrNeedBookmarkList);
				tmplatParentNeedFrmDataGrpDto.setFormDataGroupList(ParentStrGrpLi);
				formDataGroupList.add(tmplatParentNeedFrmDataGrpDto);
			}

		}

		if (!familyPlanDtlEvalRes.getCdStage().equalsIgnoreCase("FSU")) {
			// create the children needs form group and set prefill data
			for (FamilyPlanPartcpntDto familyPlanPartcpntDto : familyPlanDtlEvalRes.getFamilyPlanPartcpntDto()) {
				if (familyPlanPartcpntDto.getIndPartcpntType().equalsIgnoreCase("C")) {
					FormDataGroupDto tmplatChildNeedFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CHILD_NEED, FormConstants.EMPTY_STRING);
					List<BookmarkDto> childStrNeedBookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto addBookmarkParentName = createBookmark(BookmarkConstants.CHILD_FULL_NAME,
							familyPlanPartcpntDto.getNmParticipant());
					// BookmarkDto addBookmarkAddInfo =
					// createBookmark(BookmarkConstants.ADDITIONAL_INFO,
					// familyPlanPartcpntDto.getTxtAdtnlInfo());
					List<FormDataGroupDto> childStrGrpLi = new ArrayList<FormDataGroupDto>();

					for (FamilyPlanNeedsDto familyPlanNeedsDto : familyPlanPartcpntDto.getFamilyPlanNeedsDtoList()) {
						FormDataGroupDto tmplatChildStrQuesFrmDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_CHILD_NEED_QUES, FormGroupsConstants.TMPLAT_CHILD_NEED);
						List<BookmarkDto> ChildStrQuesBookmarkList = new ArrayList<BookmarkDto>();
						BookmarkDto addBookmarkNeedDomain = createBookmarkWithCodesTable(
								BookmarkConstants.NEED_DOMAIN_CHILD, familyPlanNeedsDto.getCdDomain(),
								CodesConstant.FSNADOMN);	
						List<FormDataGroupDto> tmpParentReqNeedGrpLi = new ArrayList<FormDataGroupDto>();
						// Added to Show Reason for Addition Label Only if Value is Present
						if(!ObjectUtils.isEmpty(familyPlanNeedsDto.getCdRsnForAdtn()))
						{
						
						FormDataGroupDto tmplatReasonForAddtnFrmDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_REASON_FOR_ADDITION_CHILD, FormGroupsConstants.TMPLAT_CHILD_NEED_QUES);
						List<BookmarkDto> bookmarkRsnAddtnBookmarkList = new ArrayList<BookmarkDto>();
						BookmarkDto addBookmarkRsnAddtn = createBookmarkWithCodesTable(
								BookmarkConstants.REASON_FOR_ADDITION_CHILD, familyPlanNeedsDto.getCdRsnForAdtn(),
								CodesConstant.CFPRFADD);
						bookmarkRsnAddtnBookmarkList.add(addBookmarkRsnAddtn);
						tmplatReasonForAddtnFrmDataGrpDto.setBookmarkDtoList(bookmarkRsnAddtnBookmarkList);
						tmpParentReqNeedGrpLi.add(tmplatReasonForAddtnFrmDataGrpDto);
						
						}						
						BookmarkDto addBookmarkNeeds = createBookmark(BookmarkConstants.NEED_CHILD,
								formatTextValue(familyPlanNeedsDto.getTxtNeedDescription()));
						ChildStrQuesBookmarkList.add(addBookmarkNeeds);
						ChildStrQuesBookmarkList.add(addBookmarkNeedDomain);

						for (FamilyPlanReqrdActnsDto familyPlanReqrdActnsDto : familyPlanNeedsDto
								.getFamilyPlanReqrdActnsDtoList()) {
							List<FormDataGroupDto> tmplatRsrcLi = new ArrayList<FormDataGroupDto>();
							FormDataGroupDto tmplatReqActionFrmDataGrpDto = createFormDataGroup(
									FormGroupsConstants.TMPLAT_CHILD_REQ_ACTION,
									FormGroupsConstants.TMPLAT_CHILD_NEED_QUES);
							List<BookmarkDto> reqActBookmarkLi = new ArrayList<BookmarkDto>();
							BookmarkDto addBookmarkRequiredAction = createBookmark(
									BookmarkConstants.REQUIRED_ACTION_CHILD, familyPlanReqrdActnsDto.getTxtReqrdActn());
							if (familyPlanDtlEvalRes.getCdStage().equalsIgnoreCase("FPR")) {
								// TFF Evidence Based Services - START
								if(Objects.nonNull(familyPlanReqrdActnsDto.getIndTffService())) {
									FormDataGroupDto tempFamilyPlanReqdActnTFFEvidenceSvcsFrmDataGrpDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_EVIDENCE_BASED_SVCS,
											FormGroupsConstants.TMPLAT_REQ_ACTN);
									List<BookmarkDto> bookmarkFamilyPlanReqdActnTffEvidenceSvcsList = new ArrayList<BookmarkDto>();
									BookmarkDto bookmarkIsTffEvidenceSvc = createBookmark(BookmarkConstants.IS_TFF_EVDNC_SVC,
											familyPlanReqrdActnsDto.getIndTffService());
									BookmarkDto bookmarkTffEvidenceBasedSvcs = createBookmark(BookmarkConstants.TFF_EVDNC_BASED_SVCS,
											getTffEvidenceBasedServices(familyPlanReqrdActnsDto));

									bookmarkFamilyPlanReqdActnTffEvidenceSvcsList.add(bookmarkIsTffEvidenceSvc);
									bookmarkFamilyPlanReqdActnTffEvidenceSvcsList.add(bookmarkTffEvidenceBasedSvcs);
									tempFamilyPlanReqdActnTFFEvidenceSvcsFrmDataGrpDto.setBookmarkDtoList(bookmarkFamilyPlanReqdActnTffEvidenceSvcsList);
									tmplatRsrcLi.add(tempFamilyPlanReqdActnTFFEvidenceSvcsFrmDataGrpDto);
								}
								// TFF Evidence Based Services - END
								BookmarkDto addBookmarkCommRsrc = new BookmarkDto();
								if (FormConstants.Y.equalsIgnoreCase(familyPlanReqrdActnsDto.getIndRsrcUtlzd())) {
									addBookmarkCommRsrc = createBookmark(BookmarkConstants.COMMUNITY_RESOURCE_CHILD,
											YES);
								} else {
									addBookmarkCommRsrc = createBookmark(BookmarkConstants.COMMUNITY_RESOURCE_CHILD,
											NO);
								}
								reqActBookmarkLi.add(addBookmarkCommRsrc);
							}
							BookmarkDto dtCmpltdBookMark = createBookmark(BookmarkConstants.DT_CMPLT_CHILD,
									TypeConvUtil.formDateFormat(familyPlanReqrdActnsDto.getDtTrgtCmpltn()));

							BookmarkDto addBookmarkPriorityStatus = createBookmarkWithCodesTable(
									BookmarkConstants.PRIORITY_STATUS_CHILD, familyPlanReqrdActnsDto.getCdPriorty(),
									CodesConstant.CFPRQACP);

							BookmarkDto addBookmarkCourt = new BookmarkDto();
							if (FormConstants.Y.equalsIgnoreCase(familyPlanReqrdActnsDto.getIndCourtOrdrd())) {
								addBookmarkCourt = createBookmark(BookmarkConstants.COURT_ORDERED_CHILD, YES);
							} else {
								addBookmarkCourt = createBookmark(BookmarkConstants.COURT_ORDERED_CHILD,
										FormConstants.NO);
							}

							for (FamilyPlanActnRsrcDto familyPlanActnRsrcDto : familyPlanReqrdActnsDto
									.getFamilyPlanActnRsrcDtoList()) {

								FormDataGroupDto tmplatRrscFrmDataGrpDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_CHILD_RSRC,
										FormGroupsConstants.TMPLAT_CHILD_REQ_ACTION);
								BookmarkDto addBookmarkNmRrsc = createBookmark(BookmarkConstants.NM_RESOURCE_CHILD,
										familyPlanActnRsrcDto.getNmRsrc());
								BookmarkDto addBookmarkRsrcPhone = createBookmark(
										BookmarkConstants.RESOURCE_PHONE_CHILD,
										TypeConvUtil.formatPhone(familyPlanActnRsrcDto.getNbrRsrcPhone()));

								BookmarkDto addBookmarkRsrcExt = createBookmark(
										BookmarkConstants.RESOURCE_PHONE_EXT_CHILD,
										familyPlanActnRsrcDto.getNbrRsrcPhoneExt());

								BookmarkDto addBookmarkRsrcAddr1 = createBookmark(
										BookmarkConstants.RESOURCE_ADDRESS_1_CHILD,
										familyPlanActnRsrcDto.getAddrRsrcStLn1());

								BookmarkDto addBookmarkRsrcAddr2 = createBookmark(
										BookmarkConstants.RESOURCE_ADDRESS_2_CHILD,
										familyPlanActnRsrcDto.getAddrRsrcStLn2());

								BookmarkDto addBookmarkRsrcCity = createBookmark(BookmarkConstants.RESOURCE_CITY_CHILD,
										familyPlanActnRsrcDto.getAddrRsrcCity());

								BookmarkDto addBookmarkRsrcZip = createBookmark(BookmarkConstants.RESOURCE_ZIP_CHILD,
										familyPlanActnRsrcDto.getAddrRsrcZip());
								List<BookmarkDto> rsrcBookmarkLi = new ArrayList<BookmarkDto>();
								rsrcBookmarkLi.add(addBookmarkRsrcExt);
								rsrcBookmarkLi.add(addBookmarkRsrcPhone);
								rsrcBookmarkLi.add(addBookmarkRsrcZip);
								rsrcBookmarkLi.add(addBookmarkRsrcCity);
								rsrcBookmarkLi.add(addBookmarkRsrcAddr2);
								rsrcBookmarkLi.add(addBookmarkRsrcAddr1);
								rsrcBookmarkLi.add(addBookmarkNmRrsc);
								tmplatRrscFrmDataGrpDto.setBookmarkDtoList(rsrcBookmarkLi);

								tmplatRsrcLi.add(tmplatRrscFrmDataGrpDto);
							}

							reqActBookmarkLi.add(addBookmarkRequiredAction);
							reqActBookmarkLi.add(addBookmarkCourt);
							reqActBookmarkLi.add(addBookmarkPriorityStatus);
							reqActBookmarkLi.add(dtCmpltdBookMark);
							tmplatReqActionFrmDataGrpDto.setBookmarkDtoList(reqActBookmarkLi);
							tmplatReqActionFrmDataGrpDto.setFormDataGroupList(tmplatRsrcLi);
							tmpParentReqNeedGrpLi.add(tmplatReqActionFrmDataGrpDto);
						}

						tmplatChildStrQuesFrmDataGrpDto.setBookmarkDtoList(ChildStrQuesBookmarkList);
						tmplatChildStrQuesFrmDataGrpDto.setFormDataGroupList(tmpParentReqNeedGrpLi);
						childStrGrpLi.add(tmplatChildStrQuesFrmDataGrpDto);

					}
					childStrNeedBookmarkList.add(addBookmarkParentName);
					tmplatChildNeedFrmDataGrpDto.setBookmarkDtoList(childStrNeedBookmarkList);
					tmplatChildNeedFrmDataGrpDto.setFormDataGroupList(childStrGrpLi);
					formDataGroupList.add(tmplatChildNeedFrmDataGrpDto);
				}
			}
		}

		boolean yesAnswered = false;
		for (FamilyPlanPartcpntDto familyPlanPartcpntDto : familyPlanDtlEvalRes.getFamilyPlanPartcpntDto()) {
			if (familyPlanPartcpntDto.getIndPartcpntType().equalsIgnoreCase("P")) {
				FormDataGroupDto tmplatParentFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_ADDTL_SUPP_SERV, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkParentLi = new ArrayList<BookmarkDto>();
				BookmarkDto nmParentBookmark = createBookmark(BookmarkConstants.PARENT_FULL_NAME,
						familyPlanPartcpntDto.getNmParticipant());
				BookmarkDto addSuppBookmark = new BookmarkDto();
				if (FormConstants.Y.equalsIgnoreCase(familyPlanPartcpntDto.getIndAdtnlSprtSvcs())) {
					addSuppBookmark = createBookmark(BookmarkConstants.ANSWER, YES);
					yesAnswered = true;
				} else if (FormConstants.N.equalsIgnoreCase(familyPlanPartcpntDto.getIndAdtnlSprtSvcs())) {
					addSuppBookmark = createBookmark(BookmarkConstants.ANSWER, NO);
				} else {
					addSuppBookmark = createBookmark(BookmarkConstants.ANSWER, FormConstants.EMPTY_STRING);
				}
				bookmarkParentLi.add(nmParentBookmark);
				bookmarkParentLi.add(addSuppBookmark);

				tmplatParentFrmDataGrpDto.setBookmarkDtoList(bookmarkParentLi);
				formDataGroupList.add(tmplatParentFrmDataGrpDto);
			}
		}

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		BookmarkDto bookmarkCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkCaseNm);
		BookmarkDto bookmarkCaseId = createBookmark(BookmarkConstants.TITLE_CASE_NBR,
				familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkCaseId);

		BookmarkDto bookmarkCauseNbr = createBookmark(BookmarkConstants.TITLE_CAUSE_NBR,
				familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtCauseNbr());
		bookmarkNonFrmGrpList.add(bookmarkCauseNbr);

		BookmarkDto bookmarkPrimPart = createBookmark(BookmarkConstants.PRIMARY_PARTICIPANTS,
				familyPlanDtlEvalRes.getTxtPrimParticp());
		bookmarkNonFrmGrpList.add(bookmarkPrimPart);

		if (familyPlanDtlEvalRes.getCdStage().equalsIgnoreCase("FPR")) {
			BookmarkDto bookmarkChildAssessed = createBookmark(BookmarkConstants.CHILDREN_ASSESSED,
					familyPlanDtlEvalRes.getChildAssessed());
			bookmarkNonFrmGrpList.add(bookmarkChildAssessed);
		}

		BookmarkDto bookmarkplanDt = createBookmark(BookmarkConstants.DT_PLAN_CMPLT,
				TypeConvUtil.formDateFormat(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getDtCompleted()));
		bookmarkNonFrmGrpList.add(bookmarkplanDt);

		BookmarkDto bookmarkNextRvw = createBookmark(BookmarkConstants.DT_NEXT_REVIEW,
				TypeConvUtil.formDateFormat(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getDtNextReview()));
		bookmarkNonFrmGrpList.add(bookmarkNextRvw);

		if (!ObjectUtils.isEmpty(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtOtherParticipants())) {

			FormDataGroupDto tmplatOtherParticipSection = createFormDataGroup(
					FormGroupsConstants.TMPLAT_OTHER_PARTICIP_SECTION, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkOtherParticpList = new ArrayList<BookmarkDto>();

			// Warranty Defect - 12087 - To retain the CR/LF format in Form
			BookmarkDto bookmarkOtherParticp = createBookmark(BookmarkConstants.OTHER_PARTICIP,
					formatTextValue(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtOtherParticipants()));
			bookmarkOtherParticpList.add(bookmarkOtherParticp);
			tmplatOtherParticipSection.setBookmarkDtoList(bookmarkOtherParticpList);
			formDataGroupList.add(tmplatOtherParticipSection);

		}

		BookmarkDto bookmarkHopeDrmChildren = createBookmark(BookmarkConstants.HOPES_AND_DREAM_FOR_CHILDREN,
				formatTextValue(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtHopesDreams()));
		bookmarkNonFrmGrpList.add(bookmarkHopeDrmChildren);

		BookmarkDto bookmarkSftyCmmSupp = createBookmark(BookmarkConstants.SAFETY_NETWORK_COMMUNITY_SUPPORT,
				formatTextValue(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtCommunitySupports()));
		bookmarkNonFrmGrpList.add(bookmarkSftyCmmSupp);

		BookmarkDto bookmarkDangerWorryStmts = createBookmark(BookmarkConstants.DANGER_WORRY_STATEMENTS,
				formatTextValue(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtDngrWorry()));
		bookmarkNonFrmGrpList.add(bookmarkDangerWorryStmts);

		BookmarkDto bookmarkGoalStmts = createBookmark(BookmarkConstants.GOAL_STATEMENTS,
				formatTextValue(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtGoalStatmnts()));
		bookmarkNonFrmGrpList.add(bookmarkGoalStmts);

		if (!ObjectUtils.isEmpty(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtAdtnlCmntDngr())) {

			FormDataGroupDto template = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDITIONAL_INFO_3,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmark = createBookmark(BookmarkConstants.ADDITIONAL_INFO_1,
					formatTextValue(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtAdtnlCmntDngr()));
			bookmarkList.add(bookmark);
			template.setBookmarkDtoList(bookmarkList);
			formDataGroupList.add(template);

		}

		if (!ObjectUtils.isEmpty(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtAdtnlCmntGoal())) {

			FormDataGroupDto template = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDITIONAL_INFO_4,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmark = createBookmark(BookmarkConstants.ADDITIONAL_INFO_2,
					formatTextValue(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtAdtnlCmntGoal()));
			bookmarkList.add(bookmark);
			template.setBookmarkDtoList(bookmarkList);
			formDataGroupList.add(template);
		}

		if (yesAnswered) {
			FormDataGroupDto templateAdditionalSupport = createFormDataGroup(
					FormGroupsConstants.TMPLAT_DESCR_PARENT_ADDTN_SUPP, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkGoalAddSupv = createBookmark(BookmarkConstants.TXT_DESCR_PARENT_ADDTN_SUPP,
					formatTextValue(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtAdtnlSprtSrvc()));
			bookmarkList.add(bookmarkGoalAddSupv);

			templateAdditionalSupport.setBookmarkDtoList(bookmarkList);
			formDataGroupList.add(templateAdditionalSupport);
		}

		BookmarkDto bookmarkPartSign = new BookmarkDto();
		if (FormConstants.Y.equalsIgnoreCase(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getIndPrntSign())) {
			bookmarkPartSign = createBookmark(BookmarkConstants.TXT_PARENT_PARTICIP_SIGN, YES);
		} else if (FormConstants.N.equalsIgnoreCase(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getIndPrntSign())) {
			bookmarkPartSign = createBookmark(BookmarkConstants.TXT_PARENT_PARTICIP_SIGN, NO);
		} else {
			bookmarkPartSign = createBookmark(BookmarkConstants.TXT_PARENT_PARTICIP_SIGN, FormConstants.EMPTY_STRING);
		}
		bookmarkNonFrmGrpList.add(bookmarkPartSign);

		BookmarkDto bookmarktxtPartSign = createBookmark(BookmarkConstants.TXT_PARENT_PARTICP,
				formatTextValue(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtNoSignRsn()));
		bookmarkNonFrmGrpList.add(bookmarktxtPartSign);

		BookmarkDto bookmarktxtPartComm = createBookmark(BookmarkConstants.TXT_PARENT_COMMENTS,
				formatTextValue(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtPrntCmnt()));
		bookmarkNonFrmGrpList.add(bookmarktxtPartComm);

		BookmarkDto bookmarkNmContact = createBookmark(BookmarkConstants.NM_CONTACT,
				familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getNmPersonFull());
		bookmarkNonFrmGrpList.add(bookmarkNmContact);

		BookmarkDto bookmarkPhoneNoContact = createBookmark(BookmarkConstants.CONTACT_PHONE,
				TypeConvUtil.formatPhone(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getNbrWrkrPhone()));
		bookmarkNonFrmGrpList.add(bookmarkPhoneNoContact);

		BookmarkDto bookmarkPhoneExt = createBookmark(BookmarkConstants.CONTACT_PHONE_EXT,
				familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getNbrWrkrPhoneExt());
		bookmarkNonFrmGrpList.add(bookmarkPhoneExt);


		BookmarkDto bookmarkFosterCarePlan = createBookmark(BookmarkConstants.FOSTER_CARE_PREV_PLAN,
				formatTextValue(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getTxtFosCarePrevPlan()));
		bookmarkNonFrmGrpList.add(bookmarkFosterCarePlan);
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}
	
	// Warranty Defect Fix - 11319 - To Replace the Carriage Return /Line Feed with Break Tag
	public String formatTextValue(String txtToFormat) {		
		String[] txtConcurrently = null;
		if (!ObjectUtils.isEmpty(txtToFormat)) {
			txtConcurrently = txtToFormat.split("\r\n");
		}
		StringBuffer txtConcrBuf = new StringBuffer();
		if (!ObjectUtils.isEmpty(txtConcurrently)) {
			for (String txtConcr : txtConcurrently) {
				txtConcrBuf.append(txtConcr);
				txtConcrBuf.append("</br>");
			}
		}
		return txtConcrBuf.toString();
	}

}
