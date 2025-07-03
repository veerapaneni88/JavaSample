package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.response.SdmReunificationAsmntFetchRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentAnsDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentChildDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentQuestionsDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: CVS
 * Reunification PrefillData for FSU stage CVSReunificationPrefillData will
 * implement returnPrefillData operation defined in DocumentServiceUtil
 * Interface to populate the prefill data for Form CVSREUNIFICATION Aug 22,
 * 2018- 11:58:48 AM © 2017 Texas Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 07/13/2020 thompswa artf149049 : display/hide Sibling Group, Safety Assessment sections
 */
@Component
public class CVSReunificationPrefillData extends DocumentServiceUtil {

	public CVSReunificationPrefillData() {
		super();
	}
	private static final Logger logger = Logger.getLogger(CVSReunificationPrefillData.class);

	@Autowired
	PersonUtil personUtil;
	
	@Autowired
	private CodesDao codesDao;

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dto object as input request
	 * 
	 * @param parentDtoobj
	 *
	 * @return PreFillData
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		SdmReunificationAsmntFetchRes sdmReunificationAsmntFetchRes = (SdmReunificationAsmntFetchRes) parentDtoobj;

		ReunificationAssessmentDto reunificationAsmntDto = sdmReunificationAsmntFetchRes.getReunificationAsmntDto();

		// Get person map for easy access
		Map<Long, PersonListDto> personsMap = new LinkedHashMap<Long, PersonListDto>();
		// Elder Households
		if (!ObjectUtils.isEmpty(sdmReunificationAsmntFetchRes.getEldersList())) {
			sdmReunificationAsmntFetchRes.getEldersList().stream().forEach(o -> personsMap.put(o.getIdPerson(), o));
		}
		// Children
		if (!ObjectUtils.isEmpty(sdmReunificationAsmntFetchRes.getChildList())) {
			sdmReunificationAsmntFetchRes.getChildList().stream().forEach(o -> personsMap.put(o.getIdPerson(), o));
		}

		// Setup response object
		if (ObjectUtils.isEmpty(sdmReunificationAsmntFetchRes.getHouseHoldList())) {
			sdmReunificationAsmntFetchRes.setHouseHoldList(new ArrayList<PersonListDto>());
		}

		if (ObjectUtils.isEmpty(sdmReunificationAsmntFetchRes.getChildList())) {
			sdmReunificationAsmntFetchRes.setChildList(new ArrayList<PersonListDto>());
		}
		
		// Remove childs not required for prefill
		if(!ObjectUtils.isEmpty(reunificationAsmntDto.getReunificationAsmntChildList()) && 0<reunificationAsmntDto.getReunificationAsmntChildList().size()) {
			reunificationAsmntDto.getReunificationAsmntChildList().removeIf(p->ObjectUtils.isEmpty(p.getIdReunfctnAsmntChld()));
		}
		
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// Bookmarks without groups
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

		/**QCR 62702 Change of title depending on Date of Assessment completed
		 * Before or After 9/1/2020
		 **/
		BookmarkDto bookmarkTitleForForm = null;
		if(showSdm(reunificationAsmntDto))	{				
			bookmarkTitleForForm = createBookmark(BookmarkConstants.SDM_PRINT_TITLE, FormConstants.TXT_SDMTITLE);
		} else {
			bookmarkTitleForForm = createBookmark(BookmarkConstants.SDM_PRINT_TITLE,FormConstants.EMPTY_STRING);
		}
	
		bookmarkNonFormGrpList.add(bookmarkTitleForForm);
		/**End of QCR 62702**/		
		
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				sdmReunificationAsmntFetchRes.getsDMReunificationAsmntBean().getNmCase());
		bookmarkNonFormGrpList.add(bookmarkNmCase);
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NBR,
				sdmReunificationAsmntFetchRes.getsDMReunificationAsmntBean().getIdCase());
		bookmarkNonFormGrpList.add(bookmarkIdCase);

		BookmarkDto bookmarkHousehold = createBookmark(BookmarkConstants.HOUSEHOLD_ASSESSED,
				personUtil.getPersonFullName(reunificationAsmntDto.getIdHshldPerson()));
		bookmarkNonFormGrpList.add(bookmarkHousehold);

		BookmarkDto bookmarkDTAssess = createBookmark(BookmarkConstants.DATE_OF_ASSESSMENT,
				DateUtils.stringDt(reunificationAsmntDto.getDtAsmntCmpltd()));
		bookmarkNonFormGrpList.add(bookmarkDTAssess);

		// Primary and secondary person details
		if (!ObjectUtils.isEmpty(personsMap)) {
			String nmPrimary = ServiceConstants.SPACE;
			String nmSecondary = ServiceConstants.SPACE;
			if (!ObjectUtils.isEmpty(reunificationAsmntDto.getIdPrmryPerson())) {
				nmPrimary = personUtil.getPersonFullName(reunificationAsmntDto.getIdPrmryPerson());
			}

			BookmarkDto bookmarkPrimaryCare = createBookmark(BookmarkConstants.TITLE_NAME_FULL, nmPrimary);
			bookmarkNonFormGrpList.add(bookmarkPrimaryCare);

			if (!ObjectUtils.isEmpty(reunificationAsmntDto.getIdSecndryPerson())) {
				nmSecondary = personUtil.getPersonFullName(reunificationAsmntDto.getIdSecndryPerson());
			}

			FormDataGroupDto secCareGiverGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SEC_CAREGIVER,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> secCareGiverList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkSecondaryCare = createBookmark(BookmarkConstants.TITLE_SECN_NAME_FULL, nmSecondary);
			secCareGiverList.add(bookmarkSecondaryCare);

			secCareGiverGroupDto.setBookmarkDtoList(secCareGiverList);
			formDataGroupList.add(secCareGiverGroupDto);
		}

		// Populate persons who helped filling the assessment
		for (PersonListDto personDto : sdmReunificationAsmntFetchRes.getEldersList()) {

			if (!ObjectUtils.isEmpty(sdmReunificationAsmntFetchRes.getIdEventPersonList())
					&& sdmReunificationAsmntFetchRes.getIdEventPersonList().contains(personDto.getIdPerson())) {

				FormDataGroupDto childAssessedGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_HOUSEHOLD_ASSISTED, FormConstants.EMPTY_STRING);

				List<BookmarkDto> bookmarkChildAssessedList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkNm = createBookmark(BookmarkConstants.HOUSEHOLD_ASSISTED_NAME,
						personDto.getPersonFull());
				bookmarkChildAssessedList.add(bookmarkNm);

				if (sdmReunificationAsmntFetchRes.getIdHdhldMemberList().contains(personDto.getIdPerson())) {
					BookmarkDto bookmarkIsHsld = createBookmark(BookmarkConstants.HOUSEHOLD_ASSISTED_CHECKED,
							ServiceConstants.CHECKED);
					bookmarkChildAssessedList.add(bookmarkIsHsld);
				}
				BookmarkDto bookmarkAge = createBookmark(BookmarkConstants.HOUSEHOLD_ASSISTED_AGE,
						personDto.getPersonAge());
				bookmarkChildAssessedList.add(bookmarkAge);

				BookmarkDto bookmarkRelatn = createBookmarkWithCodesTable(BookmarkConstants.HOUSEHOLD_ASSISTED_REL,
						personDto.getStagePersRelInt(), CodesConstant.CRPTRINT);
				bookmarkChildAssessedList.add(bookmarkRelatn);

				childAssessedGroupDto.setBookmarkDtoList(bookmarkChildAssessedList);
				formDataGroupList.add(childAssessedGroupDto);

			}

		}

		// FormDataGroups for Children Assessed
		for (PersonListDto personDto : sdmReunificationAsmntFetchRes.getChildList()) {

			// artf149049 TODO : child not in event_person_link will display if this if edit can be removed
			if (!ObjectUtils.isEmpty(sdmReunificationAsmntFetchRes.getIdEventPersonList())
					&& sdmReunificationAsmntFetchRes.getIdEventPersonList().contains(personDto.getIdPerson())) {

				// Check if this child is selected for assessment
				ReunificationAssessmentChildDto childAssmtExists = reunificationAsmntDto
						.getReunificationAsmntChildList().stream()
						.filter(p -> p.getIdPerson().equals(personDto.getIdPerson())).findFirst().orElse(null);

				if (!ObjectUtils.isEmpty(childAssmtExists)) {

					FormDataGroupDto childAssessedGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_ASSD,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkChildAssessedList = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkNm = createBookmark(BookmarkConstants.CHLD_ASSD_NAME,
							personDto.getPersonFull());
					bookmarkChildAssessedList.add(bookmarkNm);

					BookmarkDto bookmarkAge = createBookmark(BookmarkConstants.CHLD_ASSD_AGE, personDto.getPersonAge());
					bookmarkChildAssessedList.add(bookmarkAge);

					BookmarkDto bookmarkRelatn = createBookmarkWithCodesTable(BookmarkConstants.CHLD_ASSD_REL,
							personDto.getStagePersRelInt(), CodesConstant.CRPTRINT);
					bookmarkChildAssessedList.add(bookmarkRelatn);

					childAssessedGroupDto.setBookmarkDtoList(bookmarkChildAssessedList);
					formDataGroupList.add(childAssessedGroupDto);
				}

			}

		}

		// Assessment Score and Risk Level
		if (!ObjectUtils.isEmpty(reunificationAsmntDto.getNbrTotalScore())) {
			BookmarkDto bookmarkTotalScore = createBookmark(BookmarkConstants.ASSMT_SCORE_LEVEL,
					reunificationAsmntDto.getNbrTotalScore());
			bookmarkNonFormGrpList.add(bookmarkTotalScore);
		}
		if (!ObjectUtils.isEmpty(reunificationAsmntDto.getNbrTotalScore())) {
			BookmarkDto bookmarkRiskLevel = createBookmark(BookmarkConstants.ASSMT_RISK_LEVEL,
					reunificationAsmntDto.getCdRiskLvl());
			bookmarkNonFormGrpList.add(bookmarkRiskLevel);
		}

		// Fill sections based on Questions and Responses
		if (!ObjectUtils.isEmpty(sdmReunificationAsmntFetchRes.getReunificationAsmntQstnDtoList())) {

			// Risk Assessment question and response section
			// Get questions and responses related to risk
			List<ReunificationAssessmentQuestionsDto> riskQuestions = sdmReunificationAsmntFetchRes
					.getReunificationAsmntQstnDtoList().stream()
					.filter(p -> "RISK".equals(p.getCdReunfctnAsmntQstnSectn())).collect(Collectors.toList());
			// Populate Questions and response
			if (!ObjectUtils.isEmpty(riskQuestions)) {
				for (ReunificationAssessmentQuestionsDto currentQuestion : riskQuestions) {
					FormDataGroupDto questionsGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_RISK_QUESTION_SECTION, FormConstants.EMPTY_STRING);
					List<BookmarkDto> questionsBookmarkList = new ArrayList<BookmarkDto>();
					List<FormDataGroupDto> questionChildGroups = new ArrayList<FormDataGroupDto>();

					// question number
					BookmarkDto bookmarkQuestionNbr = createBookmark(BookmarkConstants.RISK_QSTN_NBR,
							currentQuestion.getCdReunfctnAsmntQstn());
					questionsBookmarkList.add(bookmarkQuestionNbr);

					// question label
					BookmarkDto bookmarkQuestionlabel = createBookmark(BookmarkConstants.RISK_QSTN_LABEL,
							currentQuestion.getTxtQuestion());
					questionsBookmarkList.add(bookmarkQuestionlabel);

					// Populate response
					if (!ObjectUtils.isEmpty(currentQuestion.getReunfctnAsmntAnsList())) {
						for (ReunificationAssessmentAnsDto currentResponse : currentQuestion
								.getReunfctnAsmntAnsList()) {
							FormDataGroupDto reponseGroupDto = createFormDataGroup(
									FormGroupsConstants.TMPLAT_RISK_QSTN_RESPONSE,
									FormGroupsConstants.TMPLAT_RISK_QUESTION_SECTION);
							List<BookmarkDto> responseBookmarkList = new ArrayList<BookmarkDto>();

							// response checked
							String responseChecked = ServiceConstants.EMPTY_STRING;
							if (!ObjectUtils.isEmpty(currentResponse.getCdReunfctnAsmntAnswr())
									&& !ObjectUtils.isEmpty(currentQuestion.getReunfctnAsmntRspns())
									&& !ObjectUtils
											.isEmpty(currentQuestion.getReunfctnAsmntRspns().getCdReunfctnAsmntAnswr())
									&& currentResponse.getCdReunfctnAsmntAnswr().equals(
											currentQuestion.getReunfctnAsmntRspns().getCdReunfctnAsmntAnswr())) {
								responseChecked = ServiceConstants.CHECKED;
							}
							BookmarkDto bookmarkResponseChecked = createBookmark(
									BookmarkConstants.IP_RISK_RSPNS_CHECKED, responseChecked);
							responseBookmarkList.add(bookmarkResponseChecked);

							// response label
							BookmarkDto bookmarkResponselabel = createBookmark(BookmarkConstants.RISK_RSPNS_LABEL,
									currentResponse.getTxtAnswr());
							responseBookmarkList.add(bookmarkResponselabel);

							reponseGroupDto.setBookmarkDtoList(responseBookmarkList);
							questionChildGroups.add(reponseGroupDto);
						}
					}

					// add question number and question label bookmarks and
					// children to
					// questionsBookmarkList
					questionsGroupDto.setFormDataGroupList(questionChildGroups);
					questionsGroupDto.setBookmarkDtoList(questionsBookmarkList);
					formDataGroupList.add(questionsGroupDto);
				}
			}

			// ENDS: Risk Assessment question and response section

			// Siblings Group question and response section
			// Get questions and responses related to risk
			ReunificationAssessmentQuestionsDto siblingQuestion = sdmReunificationAsmntFetchRes
					.getReunificationAsmntQstnDtoList().stream()
					.filter(p -> "SBLNG".equals(p.getCdReunfctnAsmntQstnSectn())).findFirst().orElse(null);
			if (!ObjectUtils.isEmpty(siblingQuestion)) {
				if (!ObjectUtils.isEmpty(siblingQuestion.getReunfctnAsmntRspns())
						&& !ObjectUtils.isEmpty(siblingQuestion.getReunfctnAsmntRspns().getCdReunfctnAsmntAnswr())) {
					String bookmarkSiblingGroupToCheck = ServiceConstants.EMPTY_STRING;

					if ("8A".equals(siblingQuestion.getReunfctnAsmntRspns().getCdReunfctnAsmntAnswr())) {
						bookmarkSiblingGroupToCheck = BookmarkConstants.IP_CHECK_SIBLING_NO_GROUP;
					} else if ("8B".equals(siblingQuestion.getReunfctnAsmntRspns().getCdReunfctnAsmntAnswr())) {
						bookmarkSiblingGroupToCheck = BookmarkConstants.IP_CHECK_SIBLING_GROUP;
					}

					if (!ObjectUtils.isEmpty(bookmarkSiblingGroupToCheck)) {
						FormDataGroupDto siblingGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SIBLING,
								FormConstants.EMPTY_STRING); // artf149049 hide/display the section with siblingGroupDto
						List<BookmarkDto> siblingBookmarkList = new ArrayList<BookmarkDto>();
						// Sibling label
						siblingBookmarkList.add(createBookmark(bookmarkSiblingGroupToCheck, ServiceConstants.CHECKED) );
						siblingGroupDto.setBookmarkDtoList(siblingBookmarkList );
						formDataGroupList.add(siblingGroupDto);
					}
				}
			}
			// ENDS: Siblings Group question and response section
		}

		// Populated fields related to overrides
		if (!ObjectUtils.isEmpty(reunificationAsmntDto.getCdOvride())) {
			if (CodesConstant.CDOVRIDE_NOOVERRIDE.equals(reunificationAsmntDto.getCdOvride())) {
				// IP_CHECK_NO_OVERRIDES
				BookmarkDto bookmarNoOverride = createBookmark(BookmarkConstants.IP_CHECK_NO_OVERRIDES,
						ServiceConstants.CHECKED);
				bookmarkNonFormGrpList.add(bookmarNoOverride);

			}
			if (CodesConstant.CDOVRIDE_POLICYOVERRIDE.equals(reunificationAsmntDto.getCdOvride())) {
				// IP_CHECK_OVERRIDES
				BookmarkDto bookmarOverride = createBookmark(BookmarkConstants.IP_CHECK_OVERRIDES,
						ServiceConstants.CHECKED);
				bookmarkNonFormGrpList.add(bookmarOverride);

				// policy Override Options
				if (!ObjectUtils.isEmpty(reunificationAsmntDto.getIndPoInjryChild3())
						&& ServiceConstants.Y.equals(reunificationAsmntDto.getIndPoInjryChild3())) {
					BookmarkDto bookmarOverrideOption1 = createBookmark(BookmarkConstants.IP_CHECK_OVERRIDE_INJURY3,
							ServiceConstants.CHECKED);
					bookmarkNonFormGrpList.add(bookmarOverrideOption1);
				}
				if (!ObjectUtils.isEmpty(reunificationAsmntDto.getIndPoSxab())
						&& ServiceConstants.Y.equals(reunificationAsmntDto.getIndPoSxab())) {
					BookmarkDto bookmarOverrideOption2 = createBookmark(BookmarkConstants.IP_CHECK_OVERRIDE_SEX_ABUSE,
							ServiceConstants.CHECKED);
					bookmarkNonFormGrpList.add(bookmarOverrideOption2);
				}
				if (!ObjectUtils.isEmpty(reunificationAsmntDto.getIndPoSrsNonAcdntal())
						&& ServiceConstants.Y.equals(reunificationAsmntDto.getIndPoSrsNonAcdntal())) {
					BookmarkDto bookmarOverrideOption3 = createBookmark(
							BookmarkConstants.IP_CHECK_OVERRIDE_INJURY_SERIOUS, ServiceConstants.CHECKED);
					bookmarkNonFormGrpList.add(bookmarOverrideOption3);
				}
				if (!ObjectUtils.isEmpty(reunificationAsmntDto.getIndPoChildDeath())
						&& ServiceConstants.Y.equals(reunificationAsmntDto.getIndPoChildDeath())) {
					BookmarkDto bookmarOverrideOption4 = createBookmark(BookmarkConstants.IP_CHECK_OVERRIDE_DEATH,
							ServiceConstants.CHECKED);
					bookmarkNonFormGrpList.add(bookmarOverrideOption4);
				}
			}
			if (CodesConstant.CDOVRIDE_DISCRETIONARYOVERRID.equals(reunificationAsmntDto.getCdOvride())) {
				// IP_CHECK_OVERRIDES_DESCR
				BookmarkDto bookmarDescrOverride = createBookmark(BookmarkConstants.IP_CHECK_OVERRIDES_DESCR,
						ServiceConstants.CHECKED);
				bookmarkNonFormGrpList.add(bookmarDescrOverride);
				// Discretionary override reason
				if (!ObjectUtils.isEmpty(reunificationAsmntDto.getTxtDscrtnryOvrideRsn())) {
					// DISCR_OVERRIDE_REASON
					BookmarkDto bookmarOverrideReason = createBookmark(BookmarkConstants.DISCR_OVERRIDE_REASON,
							reunificationAsmntDto.getTxtDscrtnryOvrideRsn());
					bookmarkNonFormGrpList.add(bookmarOverrideReason);

				}
			}
		}

		// Final Risk Level
		if (!ObjectUtils.isEmpty(reunificationAsmntDto.getCdFinalRiskLvl())) {
			String bookmarkToSelect = ServiceConstants.EMPTY_STRING;
			switch (reunificationAsmntDto.getCdFinalRiskLvl()) {
			case CodesConstant.CSDMRLVL_LOW:
				bookmarkToSelect = BookmarkConstants.IP_CHECK_DESCR_LOW;
				break;
			case CodesConstant.CSDMRLVL_MOD:
				bookmarkToSelect = BookmarkConstants.IP_CHECK_DESCR_MODERATE;
				break;
			case CodesConstant.CSDMRLVL_HIGH:
				bookmarkToSelect = BookmarkConstants.IP_CHECK_DESCR_HIGH;
				break;
			case CodesConstant.CSDMRLVL_VERYHIGH:
				bookmarkToSelect = BookmarkConstants.IP_CHECK_DESCR_VERYHIGH;
				break;
			}

			if (!ObjectUtils.isEmpty(bookmarkToSelect)) {
				BookmarkDto bookmarDiscSelection = createBookmark(bookmarkToSelect, ServiceConstants.CHECKED);
				bookmarkNonFormGrpList.add(bookmarDiscSelection);
			}
		}

		// List for Recommendation Summary Continue Reunification section - Will
		// be
		// displayed only when we have data to show.
		List<FormDataGroupDto> childRecSummaryReUnGroupList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto childReUnExists = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_REUNIFICATION_EXISTS,
				FormConstants.EMPTY_STRING);
		// Recommendation Summary - Option selection
		if (!ObjectUtils.isEmpty(reunificationAsmntDto.getReunificationAsmntChildList())) {
			for (ReunificationAssessmentChildDto currentChildDto : reunificationAsmntDto
					.getReunificationAsmntChildList()) {
				// Recommendation Summary
				FormDataGroupDto childRecSummaryGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_SUMMARY,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> recSummaryBookmarkList = new ArrayList<BookmarkDto>();

				if (!ObjectUtils.isEmpty(personsMap.get(currentChildDto.getIdPerson()))) {
					BookmarkDto bookmarkChldName = createBookmark(BookmarkConstants.CHLD_SUMMARY_NAME,
							personsMap.get(currentChildDto.getIdPerson()).getPersonFull());
					recSummaryBookmarkList.add(bookmarkChldName);
				}

				// Recommendation Summary
				String bookmarkToSelectRecommendation = ServiceConstants.EMPTY_STRING;
				if (!ObjectUtils.isEmpty(currentChildDto.getCdRecmndtnSumm())) {
					if (CodesConstant.OVRDECSN_RTNHME.equals(currentChildDto.getCdRecmndtnSumm())) {
						bookmarkToSelectRecommendation = BookmarkConstants.IP_CHECK_SUMMARY_CHILD_RETURN_HOME;
					} else if (CodesConstant.OVRDECSN_CONTRE.equals(currentChildDto.getCdRecmndtnSumm())) {
						bookmarkToSelectRecommendation = BookmarkConstants.IP_CHECK_SUMMARY_CONTINUE_FRE;
					} else if (CodesConstant.OVRDECSN_CHGPPG.equals(currentChildDto.getCdRecmndtnSumm())) {
						bookmarkToSelectRecommendation = BookmarkConstants.IP_CHECK_CHANGE_PERMANENCY;
					}
				}
				if (!bookmarkToSelectRecommendation.equals(ServiceConstants.EMPTY_STRING)) {
					BookmarkDto bookmarkRecommendation = createBookmark(bookmarkToSelectRecommendation,
							ServiceConstants.CHECKED);
					recSummaryBookmarkList.add(bookmarkRecommendation);
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getCdRecmndtnSumm()) && CodesConstant.OVRDECSN_CONTRE.equals(currentChildDto.getCdRecmndtnSumm())) {
					FormDataGroupDto childRecSummReun = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CHILD_REUNIFICATION,
							FormGroupsConstants.TMPLAT_CHILD_REUNIFICATION_EXISTS);
					List<BookmarkDto> recSummReunBookmarkList = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkReunChldName = createBookmark(BookmarkConstants.CHLD_REUNIFICATION_NAME,
							personsMap.get(currentChildDto.getIdPerson()).getPersonFull());
					recSummReunBookmarkList.add(bookmarkReunChldName);
					
					String finalReunifDesc = ServiceConstants.EMPTY_STRING;
					if(ServiceConstants.Y.equals(currentChildDto.getIndPpgFnlRlvlLowMod()) && ServiceConstants.N.equals(currentChildDto.getIndPpgFfVistatnAccptbl())) {
						finalReunifDesc = "Risk is low/moderate and visitation is unacceptable.";
					}
					else if(ServiceConstants.N.equals(currentChildDto.getIndPpgFnlRlvlLowMod()) && ServiceConstants.Y.equals(currentChildDto.getIndPpgFfVistatnAccptbl())) {
						finalReunifDesc = "Risk is high/very high and visitation is acceptable.";
					}
					
					BookmarkDto bookmarkReunDesc = createBookmark(BookmarkConstants.CHLD_REUNIFICATION_DESC,
							finalReunifDesc);
					recSummReunBookmarkList.add(bookmarkReunDesc);
					String textAreaRsn = currentChildDto.getTxtRecmndtnSummReunDesc();
					if(ObjectUtils.isEmpty(textAreaRsn)) {
						textAreaRsn = FormConstants.SPACE;
					}
					BookmarkDto bookmarkReunText = createBookmark(BookmarkConstants.CHLD_REUNIFICATION_TEXT,
							textAreaRsn);
					recSummReunBookmarkList.add(bookmarkReunText);

					childRecSummReun.setBookmarkDtoList(recSummReunBookmarkList);
					childRecSummaryReUnGroupList.add(childRecSummReun);
				}

				// add recommendation to formdata
				childRecSummaryGroup.setBookmarkDtoList(recSummaryBookmarkList);
				formDataGroupList.add(childRecSummaryGroup);
				// End: Recommendation Summary
			}		
			// Add the continue reunification efforts list at the end. This repeats for each child if selected.
			if(!ObjectUtils.isEmpty(childRecSummaryReUnGroupList) && 0<childRecSummaryReUnGroupList.size()) {
				childReUnExists.setFormDataGroupList(childRecSummaryReUnGroupList);
				formDataGroupList.add(childReUnExists);
			}
		}

		// Family Visitation Plan Evaluation
		if (!ObjectUtils.isEmpty(reunificationAsmntDto.getReunificationAsmntChildList())) {
			// Visits
			for (ReunificationAssessmentChildDto currentChildDto : reunificationAsmntDto
					.getReunificationAsmntChildList()) {
				FormDataGroupDto childFvpSummaryGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_FVP,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> childFvpSummaryBookmarkList = new ArrayList<BookmarkDto>();

				if (!ObjectUtils.isEmpty(personsMap.get(currentChildDto.getIdPerson()))) {
					BookmarkDto bookmarkChldName = createBookmark(BookmarkConstants.CHLD_FVP_NAME,
							personsMap.get(currentChildDto.getIdPerson()).getPersonFull());
					childFvpSummaryBookmarkList.add(bookmarkChldName);
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getNbrVpSchedldVisit())) {
					BookmarkDto bookmarkScheduledVisits = createBookmark(BookmarkConstants.CHILD_FVP_SCH_VISITS,
							currentChildDto.getNbrVpSchedldVisit());
					childFvpSummaryBookmarkList.add(bookmarkScheduledVisits);
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getNbrVpCmpltdVisit())) {
					BookmarkDto bookmarkCompVisits = createBookmark(BookmarkConstants.CHILD_FVP_COMP_VISITS,
							currentChildDto.getNbrVpCmpltdVisit());
					childFvpSummaryBookmarkList.add(bookmarkCompVisits);
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getIndVpNoVistatn())
						&& ServiceConstants.Y.equals(currentChildDto.getIndVpNoVistatn())) {
					BookmarkDto bookmarkNoVisits = createBookmark(BookmarkConstants.IP_CHECK_FVP_NO_VISIT,
							ServiceConstants.CHECKED);
					childFvpSummaryBookmarkList.add(bookmarkNoVisits);
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getTxtVpNoVistatnRsn())) {
					BookmarkDto bookmarkNoVisits = createBookmark(BookmarkConstants.CHILD_FVP_NO_VISIT_REASON,
							currentChildDto.getTxtVpNoVistatnRsn());
					childFvpSummaryBookmarkList.add(bookmarkNoVisits);
				}

				childFvpSummaryGroup.setBookmarkDtoList(childFvpSummaryBookmarkList);
				formDataGroupList.add(childFvpSummaryGroup);
			}

			// Face - to - face
			for (ReunificationAssessmentChildDto currentChildDto : reunificationAsmntDto
					.getReunificationAsmntChildList()) {
				FormDataGroupDto childF2FSummaryGroup = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CHILD_F2F_SUMMARY, FormConstants.EMPTY_STRING);
				List<BookmarkDto> childF2FSummaryBookmarkList = new ArrayList<BookmarkDto>();

				if (!ObjectUtils.isEmpty(personsMap.get(currentChildDto.getIdPerson()))) {
					BookmarkDto bookmarkF2FChldName = createBookmark(BookmarkConstants.CHLD_F2F_NAME,
							personsMap.get(currentChildDto.getIdPerson()).getPersonFull());
					childF2FSummaryBookmarkList.add(bookmarkF2FChldName);
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getCdVpCmplncFvPln())) {
					BookmarkDto bookmarkF2FCompliance = createBookmarkWithCodesTable(BookmarkConstants.CHLD_F2F_COMP,
							currentChildDto.getCdVpCmplncFvPln(), "CMPLNCFV");
					if ("EXCL".equalsIgnoreCase(currentChildDto.getCdVpCmplncFvPln())
							|| "GOOD".equalsIgnoreCase(currentChildDto.getCdVpCmplncFvPln())
							|| "FAIR".equalsIgnoreCase(currentChildDto.getCdVpCmplncFvPln())) {

						bookmarkF2FCompliance.setBookmarkData(bookmarkF2FCompliance.getBookmarkData() + " Compliance");
					}
					childF2FSummaryBookmarkList.add(bookmarkF2FCompliance);
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getCdVpFtfFmlyVistatnQlty())) {
					if ("STAC".equals(currentChildDto.getCdVpFtfFmlyVistatnQlty())) {
						BookmarkDto bookmarkF2FStrong = createBookmark(BookmarkConstants.IP_CHECK_F2F_STRONG,
								ServiceConstants.CHECKED);
						childF2FSummaryBookmarkList.add(bookmarkF2FStrong);
					} else if ("LTDH".equals(currentChildDto.getCdVpFtfFmlyVistatnQlty())) {
						BookmarkDto bookmarkF2FStrong = createBookmark(BookmarkConstants.IP_CHECK_F2F_LIMITED,
								ServiceConstants.CHECKED);
						childF2FSummaryBookmarkList.add(bookmarkF2FStrong);
					}
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getCdVpInitFmlyVistatn())) {
					BookmarkDto bookmarkF2FInitial = createBookmarkWithCodesTable(BookmarkConstants.CHLD_F2F_INITIAL,
							currentChildDto.getCdVpInitFmlyVistatn(), "FVSTSTAT");
					childF2FSummaryBookmarkList.add(bookmarkF2FInitial);
				}

				childF2FSummaryGroup.setBookmarkDtoList(childF2FSummaryBookmarkList);
				formDataGroupList.add(childF2FSummaryGroup);
			}

			// Overrides
			for (ReunificationAssessmentChildDto currentChildDto : reunificationAsmntDto
					.getReunificationAsmntChildList()) {
				FormDataGroupDto childOverrideGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_FVP_OVERRIDE,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> childOverrideBookmarkList = new ArrayList<BookmarkDto>();

				if (!ObjectUtils.isEmpty(personsMap.get(currentChildDto.getIdPerson()))) {
					BookmarkDto bookmarkOverrideChldName = createBookmark(BookmarkConstants.CHLD_FVP_OVR_NAME,
							personsMap.get(currentChildDto.getIdPerson()).getPersonFull());
					childOverrideBookmarkList.add(bookmarkOverrideChldName);
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getCdVpOvride())) {
					if (CodesConstant.CDOVRIDE_NOOVERRIDE.equals(currentChildDto.getCdVpOvride())) {
						BookmarkDto bookmarNoOverride = createBookmark(BookmarkConstants.IP_CHECK_FVP_OVR_NO,
								ServiceConstants.CHECKED);
						childOverrideBookmarkList.add(bookmarNoOverride);

					}
					if (CodesConstant.CDOVRIDE_POLICYOVERRIDE.equals(currentChildDto.getCdVpOvride())) {
						BookmarkDto bookmarOverride = createBookmark(BookmarkConstants.IP_CHECK_FVP_OVR_OVR,
								ServiceConstants.CHECKED);
						childOverrideBookmarkList.add(bookmarOverride);
					}
					if (CodesConstant.CDOVRIDE_DISCRETIONARYOVERRID.equals(currentChildDto.getCdVpOvride())) {
						BookmarkDto bookmarDescrOverride = createBookmark(BookmarkConstants.IP_CHECK_FVP_OVR_DISC,
								ServiceConstants.CHECKED);
						childOverrideBookmarkList.add(bookmarDescrOverride);
					}
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getTxtVpDscrtnryOvrideRsn())) {
					BookmarkDto bookmarkF2FInitial = createBookmark(BookmarkConstants.CHLD_FVP_OVR_REASON,
							currentChildDto.getTxtVpDscrtnryOvrideRsn());
					childOverrideBookmarkList.add(bookmarkF2FInitial);
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getCdVpFinalFmlyVistatn())) {
					BookmarkDto bookmarkF2FInitial = createBookmarkWithCodesTable(BookmarkConstants.CHLD_FVP_OVR_FINAL,
							currentChildDto.getCdVpFinalFmlyVistatn(), "FVSTSTAT");
					childOverrideBookmarkList.add(bookmarkF2FInitial);
				}

				childOverrideGroup.setBookmarkDtoList(childOverrideBookmarkList);
				formDataGroupList.add(childOverrideGroup);
			}
		}
		// End: Family Visitation Plan Evaluation

		// Reunification Safety Assessment
		if (!ObjectUtils.isEmpty(sdmReunificationAsmntFetchRes.getReunificationAsmntQstnDtoList())) {
			// Get safety questions alone from question list
			List<ReunificationAssessmentQuestionsDto> safetyQuestionList = sdmReunificationAsmntFetchRes
					.getReunificationAsmntQstnDtoList().stream()
					.filter(p -> "SFTY".equals(p.getCdReunfctnAsmntQstnSectn())).collect(Collectors.toList());

			/*
			 * artf149049 display the Reunification Safety Assessment section with rsaGroupDto bookmark
			 * when risk is low to moderate and family visitation is acceptable for at least one child
			 */
			boolean bReunfctnAsmntRspns = false; // artf149049
			if (!ObjectUtils.isEmpty(safetyQuestionList)) {
				FormDataGroupDto rsaGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_RSA, FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> questionsGroupDtoList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> rsaBookmarkList = new ArrayList<BookmarkDto>();
				for (ReunificationAssessmentQuestionsDto currentSafetyQuestion : safetyQuestionList) {
					boolean bCurrentResponse = false; // artf149049
					// Ignore followup questions. They will be handled as part
					// of main safety
					// question.
					if (ObjectUtils.isEmpty(currentSafetyQuestion.getIdReunfctnAsmntAnsLkp())) {
						FormDataGroupDto questionsGroupDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_SAFETY_QUESTION_SECTION, rsaGroupDto.getFormDataGroupBookmark());
						List<BookmarkDto> questionsBookmarkList = new ArrayList<BookmarkDto>();
						List<FormDataGroupDto> questionChildGroups = new ArrayList<FormDataGroupDto>();

						// Bookmark related to safety question
						// question number
						BookmarkDto bookmarkQuestionNbr = createBookmark(BookmarkConstants.SAFETY_QSTN_NBR,
								currentSafetyQuestion.getCdReunfctnAsmntQstn());
						questionsBookmarkList.add(bookmarkQuestionNbr);

						// question label
						BookmarkDto bookmarkQuestionlabel = createBookmark(BookmarkConstants.SAFETY_QSTN_LABEL,
								currentSafetyQuestion.getTxtQuestion());
						questionsBookmarkList.add(bookmarkQuestionlabel);

						String txtResponseDesc = ServiceConstants.EMPTY_STRING;

						// Populate response
						if (!ObjectUtils.isEmpty(currentSafetyQuestion.getReunfctnAsmntAnsList())) {
							for (ReunificationAssessmentAnsDto currentResponse : currentSafetyQuestion
									.getReunfctnAsmntAnsList()) {
								FormDataGroupDto reponseGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SAFETY_QSTN_RESPONSE,
										FormGroupsConstants.TMPLAT_SAFETY_QUESTION_SECTION);
								List<BookmarkDto> responseBookmarkList = new ArrayList<BookmarkDto>();

								// response checked
								String responseChecked = ServiceConstants.EMPTY_STRING;

								if (!ObjectUtils.isEmpty(currentResponse.getCdReunfctnAsmntAnswr())
										&& !ObjectUtils.isEmpty(currentSafetyQuestion.getReunfctnAsmntRspns())
										&& !ObjectUtils.isEmpty(
												currentSafetyQuestion.getReunfctnAsmntRspns().getCdReunfctnAsmntAnswr())
										&& currentResponse.getCdReunfctnAsmntAnswr().equals(currentSafetyQuestion
												.getReunfctnAsmntRspns().getCdReunfctnAsmntAnswr())) {
									responseChecked = ServiceConstants.CHECKED;
									bReunfctnAsmntRspns = true;
									bCurrentResponse = true;
									if (!ObjectUtils
											.isEmpty(currentSafetyQuestion.getReunfctnAsmntRspns().getTextDesc())) {
										txtResponseDesc = currentSafetyQuestion.getReunfctnAsmntRspns().getTextDesc();
									}
								}
								BookmarkDto bookmarkResponseChecked = createBookmark(
										BookmarkConstants.IP_SAFETY_RSPNS_CHECKED, responseChecked);
								responseBookmarkList.add(bookmarkResponseChecked);

								// response label
								BookmarkDto bookmarkResponselabel = createBookmark(BookmarkConstants.SAFETY_RSPNS_LABEL,
										currentResponse.getTxtAnswr());
								//////////////////////////////////////////////////////////////// currentResponse.getTxtAnswr());
								responseBookmarkList.add(bookmarkResponselabel);

								// Check if current question has any followup
								// question
								if (!ObjectUtils.isEmpty(currentResponse.getIdReunfctnAsmntAnsLkp())) {
									// Check if the reponse is answered to
									// display followup question and response
									ReunificationAssessmentQuestionsDto followupQuestion = safetyQuestionList.stream()
											.filter(p -> !ObjectUtils.isEmpty(p.getIdReunfctnAsmntAnsLkp())
													&& p.getIdReunfctnAsmntAnsLkp()
															.equals(currentResponse.getIdReunfctnAsmntAnsLkp()))
											.findFirst().orElse(null);
									if (!ObjectUtils.isEmpty(followupQuestion)
											&& !ObjectUtils.isEmpty(followupQuestion.getReunfctnAsmntRspns())
											&& !ObjectUtils.isEmpty(followupQuestion.getReunfctnAsmntRspns()
													.getCdReunfctnAsmntAnswr())) {

										FormDataGroupDto reponseFollowUpExistsGroupDto = createFormDataGroup(
												FormGroupsConstants.TMPLAT_SAFETY_FOLLOWUP_EXISTS,
												FormGroupsConstants.TMPLAT_SAFETY_QUESTION_SECTION);
										List<BookmarkDto> responseFollowUpBookmarkDescList = new ArrayList<BookmarkDto>();

										List<FormDataGroupDto> responseFollowupChildGroups = new ArrayList<FormDataGroupDto>();
										String txtDescription = ServiceConstants.EMPTY_STRING;

										// Question number and label
										BookmarkDto bookmarkFollowUpQuestionNumber = createBookmark(
												BookmarkConstants.SAFETY_FOLLOWUP_QSTN_NUM,
												followupQuestion.getCdReunfctnAsmntQstn());
										responseFollowUpBookmarkDescList.add(bookmarkFollowUpQuestionNumber);

										BookmarkDto bookmarkFollowUpQuestionLabel = createBookmark(
												BookmarkConstants.SAFETY_FOLLOWUP_QSTN_LABEL,
												followupQuestion.getTxtQuestion());
										responseFollowUpBookmarkDescList.add(bookmarkFollowUpQuestionLabel);

										// For each response do populate
										// response details
										for (ReunificationAssessmentAnsDto currentFollowUpResponse : followupQuestion
												.getReunfctnAsmntAnsList()) {
											FormDataGroupDto reponseFollowUpGroupDto = createFormDataGroup(
													FormGroupsConstants.TMPLAT_SAFETY_FOLLOWUP_QSTN_RESPONSE,
													FormGroupsConstants.TMPLAT_SAFETY_FOLLOWUP_EXISTS);
											List<BookmarkDto> responseFollowUpBookmarkList = new ArrayList<BookmarkDto>();

											String followUpresponseChecked = ServiceConstants.EMPTY_STRING;
											// Followup response checked
											if (!ObjectUtils.isEmpty(currentFollowUpResponse.getCdReunfctnAsmntAnswr())
													&& followupQuestion.getReunfctnAsmntRspns()
															.getCdReunfctnAsmntAnswr().equals(currentFollowUpResponse
																	.getCdReunfctnAsmntAnswr())) {
												followUpresponseChecked = ServiceConstants.CHECKED;
												if (!ObjectUtils.isEmpty(
														followupQuestion.getReunfctnAsmntRspns().getTextDesc())) {
													txtDescription = followupQuestion.getReunfctnAsmntRspns()
															.getTextDesc();
												}
											}

											BookmarkDto bookmarkFollowUpResponseChecked = createBookmark(
													BookmarkConstants.IP_SAFETY_FOLLOWUP_RSPNS_CHECKED,
													followUpresponseChecked);
											responseFollowUpBookmarkList.add(bookmarkFollowUpResponseChecked);

											// response label
											BookmarkDto bookmarkFollowUpResponselabel = createBookmark(
													BookmarkConstants.SAFETY_FOLLOWUP_RSPNS_LABEL,
													currentFollowUpResponse.getTxtAnswr());
											responseFollowUpBookmarkList.add(bookmarkFollowUpResponselabel);

											reponseFollowUpGroupDto.setBookmarkDtoList(responseFollowUpBookmarkList);
											responseFollowupChildGroups.add(reponseFollowUpGroupDto);
										}
										BookmarkDto bookmarkFollowUpResponseDesc = createBookmark(
												BookmarkConstants.SAFETY_RSPNS_FOLLOWUP_DESCRIBE, txtDescription);
										responseFollowUpBookmarkDescList.add(bookmarkFollowUpResponseDesc);

										reponseFollowUpExistsGroupDto
												.setBookmarkDtoList(responseFollowUpBookmarkDescList);
										reponseFollowUpExistsGroupDto.setFormDataGroupList(responseFollowupChildGroups);

										questionChildGroups.add(reponseFollowUpExistsGroupDto);
									}
								}

								reponseGroupDto.setBookmarkDtoList(responseBookmarkList);
								questionChildGroups.add(reponseGroupDto);
							}
						}

						BookmarkDto bookmarkResponseDesc = createBookmark(BookmarkConstants.SAFETY_RSPNS_DESCRIBE,
								txtResponseDesc);
						questionsBookmarkList.add(bookmarkResponseDesc);

						// add question number and question label bookmarks and
						// children to
						// questionsBookmarkList
						questionsGroupDto.setFormDataGroupList(questionChildGroups);
						questionsGroupDto.setBookmarkDtoList(questionsBookmarkList);
						// artf149049 only show the answered response
						if(true == bCurrentResponse) questionsGroupDtoList.add(questionsGroupDto);
					}
				}
				// Safety Decision
				if (!ObjectUtils.isEmpty(reunificationAsmntDto.getCdSftyDecsn())) {
					StringBuilder decisionText = new StringBuilder();

					if (CodesConstant.CSDMDCSN_SAFE.equals(reunificationAsmntDto.getCdSftyDecsn())) {
						decisionText.append("<u>Safe.</u> No danger indicators were identified at this time. Based on currently available information, no child is likely to be in immediate danger of serious harm.");
					} else if (CodesConstant.CSDMDCSN_UNSAFE.equals(reunificationAsmntDto.getCdSftyDecsn())) {
						decisionText.append("<u>Unsafe.</u> One or more danger indicators are present, and continued placement is the only safety intervention possible for one or more children. Without continued placement, one or more children will likely be in danger of immediate and/or serious harm.");
					} else if (CodesConstant.CSDMDCSN_SAFEWITHPLAN.equals(reunificationAsmntDto.getCdSftyDecsn())) {
						decisionText.append("<u>Safe with interventions.</u> One or more danger indicators are present, and safety interventions have been planned or taken and documented in the FPOS or court orders. Based on safety interventions, the child would be safe with FPOS or court orders in place upon his/her return home. Any additional support services can occur with the child in the home.");
					}
					rsaBookmarkList.add(createBookmark(BookmarkConstants.SAFETY_DECISION, decisionText.toString()));
					rsaGroupDto.setBookmarkDtoList(rsaBookmarkList);
				}
				// artf149049 turn on display of the RSA section by adding all in rsaGroupDto to formDataGroupList
				rsaGroupDto.setFormDataGroupList(questionsGroupDtoList);
				if(true == bReunfctnAsmntRspns) formDataGroupList.add(rsaGroupDto);
			}
		}

		// End: Reunification Safety Assessment

		// Placement/Permanency Plan Guidelines
		if (!ObjectUtils.isEmpty(reunificationAsmntDto.getReunificationAsmntChildList())) {
			// Populate bookmarks for each child details available
			for (ReunificationAssessmentChildDto currentChildDto : reunificationAsmntDto
					.getReunificationAsmntChildList()) {
				FormDataGroupDto ppmgGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PPMP_SECTION,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> ppmgBookmarkList = new ArrayList<BookmarkDto>();

				// Name
				if (!ObjectUtils.isEmpty(personsMap.get(currentChildDto.getIdPerson()))) {
					BookmarkDto bookmarkChldName = createBookmark(BookmarkConstants.PPMP_CHILD_NAME,
							personsMap.get(currentChildDto.getIdPerson()).getPersonFull());
					ppmgBookmarkList.add(bookmarkChldName);
				}

				// ppmp questions
				if (!ObjectUtils.isEmpty(currentChildDto.getIndPpgFnlRlvlLowMod())) {
					if (ServiceConstants.Y.equals(currentChildDto.getIndPpgFnlRlvlLowMod())) {
						BookmarkDto bookmarkQuestionLowModerateyes = createBookmark(
								BookmarkConstants.IP_CHECK_PPMP_LOWMOD_YES, ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarkQuestionLowModerateyes);
					}
					if (ServiceConstants.N.equals(currentChildDto.getIndPpgFnlRlvlLowMod())) {
						BookmarkDto bookmarkQuestionLowModerateyes = createBookmark(
								BookmarkConstants.IP_CHECK_PPMP_LOWMOD_NO, ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarkQuestionLowModerateyes);
					}
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getIndPpgFfVistatnAccptbl())) {
					if (ServiceConstants.Y.equals(currentChildDto.getIndPpgFfVistatnAccptbl())) {
						BookmarkDto bookmarkQuestionAcceptyes = createBookmark(
								BookmarkConstants.IP_CHECK_PPMP_ACCPT_YES, ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarkQuestionAcceptyes);
					}
					if (ServiceConstants.N.equals(currentChildDto.getIndPpgFfVistatnAccptbl())) {
						BookmarkDto bookmarkQuestionLowModerateyes = createBookmark(
								BookmarkConstants.IP_CHECK_PPMP_ACCEPT_NO, ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarkQuestionLowModerateyes);
					}
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getIndPpgHomeSafe())) {
					if (ServiceConstants.Y.equals(currentChildDto.getIndPpgHomeSafe())) {
						BookmarkDto bookmarkQuestionHomeSafeYes = createBookmark(
								BookmarkConstants.IP_CHECK_PPMP_HOMESAFE_YES, ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarkQuestionHomeSafeYes);
					}
					if (ServiceConstants.N.equals(currentChildDto.getIndPpgHomeSafe())) {
						BookmarkDto bookmarkQuestionHomeSafeNo = createBookmark(
								BookmarkConstants.IP_CHECK_PPMP_HOMESAFE_NO, ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarkQuestionHomeSafeNo);
					}
				}
				else {
					BookmarkDto bookmarkQuestionHomeSafeNo = createBookmark(
							BookmarkConstants.IP_CHECK_PPMP_HOMESAFE_NO, ServiceConstants.CHECKED);
					ppmgBookmarkList.add(bookmarkQuestionHomeSafeNo);
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getIndPpgFiveMonth())) {
					if (ServiceConstants.Y.equals(currentChildDto.getIndPpgFiveMonth())) {
						BookmarkDto bookmarkQuestionHomeSafeYes = createBookmark(
								BookmarkConstants.IP_CHECK_PPMP_5MONTH_YES, ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarkQuestionHomeSafeYes);
					}
					if (ServiceConstants.N.equals(currentChildDto.getIndPpgFiveMonth())) {
						BookmarkDto bookmarkQuestionHomeSafeNo = createBookmark(
								BookmarkConstants.IP_CHECK_PPMP_5MONTH_NO, ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarkQuestionHomeSafeNo);
					}
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getIndPpgNineMonth())) {
					if (ServiceConstants.Y.equals(currentChildDto.getIndPpgNineMonth())) {
						BookmarkDto bookmarkQuestionHomeSafeYes = createBookmark(
								BookmarkConstants.IP_CHECK_PPMP_9MONTH_YES, ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarkQuestionHomeSafeYes);
					}
					if (ServiceConstants.N.equals(currentChildDto.getIndPpgNineMonth())) {
						BookmarkDto bookmarkQuestionHomeSafeNo = createBookmark(
								"IP_CHECK_PPMP_9MONTH_NO", ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarkQuestionHomeSafeNo);
					}
				}

				if (!ObjectUtils.isEmpty(currentChildDto.getIndPpgQstnThree())) {
					if (ServiceConstants.Y.equals(currentChildDto.getIndPpgNineMonth())) {
						BookmarkDto bookmarkQuestionHomeSafeYes = createBookmark(
								BookmarkConstants.IP_CHECK_PPMP_3ANS_YES, ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarkQuestionHomeSafeYes);
					}
					if (ServiceConstants.N.equals(currentChildDto.getIndPpgQstnThree())) {
						BookmarkDto bookmarkQuestionHomeSafeNo = createBookmark(BookmarkConstants.IP_CHECK_PPMP_3ANS_NO,
								ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarkQuestionHomeSafeNo);
					}
				}

				// Initial decision
				if (!ObjectUtils.isEmpty(currentChildDto.getCdPpgInitDecsn())) {

					BookmarkDto bookmarkInitialDecision = createBookmarkWithCodesTable(
							BookmarkConstants.PPMP_INITIAL_DECISION, currentChildDto.getCdPpgInitDecsn(),
							CodesConstant.INIDECSN);
					ppmgBookmarkList.add(bookmarkInitialDecision);

				}

				// Overrides
				if (!ObjectUtils.isEmpty(currentChildDto.getCdPpgOvride())) {
					if (CodesConstant.CDOVRIDE_NOOVERRIDE.equals(currentChildDto.getCdPpgOvride())) {
						BookmarkDto bookmarNoOverride = createBookmark(BookmarkConstants.IP_PPMP_NO_OVERRIDES,
								ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarNoOverride);

					}
					if (CodesConstant.CDOVRIDE_POLICYOVERRIDE.equals(currentChildDto.getCdPpgOvride())) {
						BookmarkDto bookmarOverride = createBookmark(BookmarkConstants.IP_PPMP_OVERRIDES,
								ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarOverride);

						if (!ObjectUtils.isEmpty(currentChildDto.getIndPpgPoFollowp())) {
							if (ServiceConstants.ONE.equals(currentChildDto.getIndPpgPoFollowp())) {
								BookmarkDto bookmarkQuestionoverrideFollowup1 = createBookmark(
										"IP_PPMP_OVERRIDES_CHANGE", ServiceConstants.CHECKED);
								ppmgBookmarkList.add(bookmarkQuestionoverrideFollowup1);
							}
							if (ServiceConstants.TWO.equals(currentChildDto.getIndPpgPoFollowp())) {
								BookmarkDto bookmarkQuestionoverrideFollowup2 = createBookmark(
										"IP_PPMP_OVERRIDES_CONTINUE", ServiceConstants.CHECKED);
								ppmgBookmarkList.add(bookmarkQuestionoverrideFollowup2);
							}
						}

						if (!ObjectUtils.isEmpty(currentChildDto.getTxtPpgPlcyOvrideDesc())) {
							BookmarkDto bookmarOverrideDesc = createBookmark(BookmarkConstants.PPMP_NO_OVERRIDE_DESC,
									currentChildDto.getTxtPpgPlcyOvrideDesc());
							ppmgBookmarkList.add(bookmarOverrideDesc);
						}

					}
					if (CodesConstant.CDOVRIDE_DISCRETIONARYOVERRID.equals(currentChildDto.getCdPpgOvride())) {
						BookmarkDto bookmarDescrOverride = createBookmark(BookmarkConstants.IP_PPMP_DISC_OVERRIDES,
								ServiceConstants.CHECKED);
						ppmgBookmarkList.add(bookmarDescrOverride);

						if (!ObjectUtils.isEmpty(currentChildDto.getTxtPpgDscrtnryOvrideDesc())) {
							BookmarkDto bookmarOverrideDesc = createBookmark(BookmarkConstants.PPMP_DISC_OVERRIDE_DESC,
									currentChildDto.getTxtPpgDscrtnryOvrideDesc());
							ppmgBookmarkList.add(bookmarOverrideDesc);
						}
					}

					// Discretionary Override

					if (!ObjectUtils.isEmpty(currentChildDto.getCdPpgDscrtnryOvrideDecsn())) {

						if (currentChildDto.getCdPpgDscrtnryOvrideDecsn().equals(CodesConstant.RTNHME)) {
							BookmarkDto bookmarPpgDscrtnryOvrideDecsn = createBookmark(
									BookmarkConstants.IP_DISC_RETURNHOME, ServiceConstants.CHECKED);
							ppmgBookmarkList.add(bookmarPpgDscrtnryOvrideDecsn);

						}
						if (currentChildDto.getCdPpgDscrtnryOvrideDecsn().equals(CodesConstant.CONTRE)) {

							BookmarkDto bookmarPpgDscrtnryOvrideDecsn = createBookmark(
									BookmarkConstants.IP_DISC_REUN_EFFORT, ServiceConstants.CHECKED);
							ppmgBookmarkList.add(bookmarPpgDscrtnryOvrideDecsn);

						}
						if (currentChildDto.getCdPpgDscrtnryOvrideDecsn().equals(CodesConstant.CHGPPG)) {

							BookmarkDto bookmarPpgDscrtnryOvrideDecsn = createBookmark(
									BookmarkConstants.IP_DISC_CHANGE_PPG, ServiceConstants.CHECKED);
							ppmgBookmarkList.add(bookmarPpgDscrtnryOvrideDecsn);

						}

					}

				}

				// Final decision
				// if(!ObjectUtils.isEmpty(currentChildDto.get)) {
				BookmarkDto bookmarOverrideDesc = createBookmark("PPMP_FINAL_DECISION",
						currentChildDto.getTxtPpgDscrtnryOvrideDesc());
				ppmgBookmarkList.add(bookmarOverrideDesc);
				// }

				ppmgGroupDto.setBookmarkDtoList(ppmgBookmarkList);
				formDataGroupList.add(ppmgGroupDto);
			}
		}

		// End: Placement/Permanency Plan Guidelines


		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);
		return preFillData;

	}
	
	/**
	 * QCR 62702
	 * @param reunificationAsmntDto
	 * @return
	 */
	private boolean showSdm(ReunificationAssessmentDto reunificationAsmntDto) {
		boolean showSdm = false;
		Date dtSDMRemoval=codesDao.getAppRelDate(ServiceConstants.CRELDATE_SDM_REMOVAL_2020);
		if (!ObjectUtils.isEmpty(reunificationAsmntDto)) {
			if (!ObjectUtils.isEmpty(reunificationAsmntDto.getDtAsmntCmpltd())) {
				if (reunificationAsmntDto.getDtAsmntCmpltd().compareTo(dtSDMRemoval)<0){
					showSdm = true;
				}
			} else {
				if(!ObjectUtils.isEmpty(reunificationAsmntDto.getDtCreated())
					&& !ObjectUtils.isEmpty(reunificationAsmntDto.getDtStageClose())
					&& reunificationAsmntDto.getDtCreated().compareTo(dtSDMRemoval) < 0 
					&& reunificationAsmntDto.getDtStageClose().compareTo(dtSDMRemoval) < 0)
				{
					showSdm = true;
				}
			}
		}
		
		return showSdm;
	}

}
