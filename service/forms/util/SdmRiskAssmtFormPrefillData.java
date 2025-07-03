package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.SdmRiskAssmtFormDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentAnswerDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentFollowupDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentQuestionDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssmtSecondaryFollowupDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssmentDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.workload.dto.EventDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: May 8,
 * 2018- 11:10:11 AM
 *
 */
@Component
public class SdmRiskAssmtFormPrefillData extends DocumentServiceUtil {

	@Autowired
	private PersonDao personDao;
	
	@Autowired
	private CodesDao codesDao;
	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.forms.util.DocumentServiceUtil#
	 * returnPrefillData( java.lang.Object)
	 */
	@SuppressWarnings("unused")
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		SdmRiskAssmtFormDto sdmRiskAssmtFormDto = (SdmRiskAssmtFormDto) parentDtoobj;
		boolean hideBookmarks = false;
		if (ObjectUtils.isEmpty(sdmRiskAssmtFormDto.getsDMRiskAssessmentDto())) {
			sdmRiskAssmtFormDto.setsDMRiskAssessmentDto(new SDMRiskAssessmentDto());
		}

		if (ObjectUtils.isEmpty(sdmRiskAssmtFormDto.getRiskEvent())) {
			sdmRiskAssmtFormDto.setRiskEvent(new EventDto());

		}

		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		BookmarkDto addBookmarkHtmlTitle = createBookmark(BookmarkConstants.HTML_TITLE, BookmarkConstants.RA_TITLE);
		bookmarkNonFrmGrpList.add(addBookmarkHtmlTitle);
		FormDataGroupDto caseGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CASE, FormConstants.EMPTY_STRING);
		List<BookmarkDto> caseGrpBookmarkList = new ArrayList<BookmarkDto>();
		BookmarkDto caseIdBookmark = createBookmark(BookmarkConstants.CASE_ID,
				sdmRiskAssmtFormDto.getStageDto().getIdCase());
		BookmarkDto caseNmBookmark = createBookmark(BookmarkConstants.CASE_NM,
				sdmRiskAssmtFormDto.getStageDto().getNmCase());		
		
		// Warranty Defect - 12431 - To set the HouseHold Person Id
		if(!ObjectUtils.isEmpty(sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getIdHouseHoldPerson()))
		{
		BookmarkDto caseHHBookmark = createBookmark(BookmarkConstants.CASE_HOUSEHOLD,
				personDao.getPerson(sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getIdHouseHoldPerson()).getNmPersonFull());
		caseGrpBookmarkList.add(caseHHBookmark);
		}
		
		caseGrpBookmarkList.add(caseIdBookmark);
		caseGrpBookmarkList.add(caseNmBookmark);
	
		caseGroup.setBookmarkDtoList(caseGrpBookmarkList);
		formDataGroupList.add(caseGroup);
		
		/**QCR 62702 SDM Removal-Change of title depending on Date of Assessment completed
		 * Before or After 9/1/2020
		 * artf159087, artf159088,artf160001 **/
		BookmarkDto addBookmarkPrintTitle = null;		
		boolean showSDMTitle=showSDM(sdmRiskAssmtFormDto);
		if(showSDMTitle){
			addBookmarkPrintTitle = createBookmark(BookmarkConstants.PRINT_TITLE, BookmarkConstants.RA_TITLE);
		}else{
			addBookmarkPrintTitle = createBookmark(BookmarkConstants.PRINT_TITLE, BookmarkConstants.RA_TITLE_NOSDM);
		}
		bookmarkNonFrmGrpList.add(addBookmarkPrintTitle);
		/**End of QCR 62702**/

		/** DISPLAY/HIDE MESSAGES/BOOKMARKS **/

		if (ObjectUtils.isEmpty(sdmRiskAssmtFormDto.getRiskEvent().getIdEvent())) {
			// when there is no risk assessment, hide bookmarks, populate
			// messages
			hideBookmarks = true;
			if (FormConstants.IND_Y.equals(sdmRiskAssmtFormDto.getStageDto().getIndStageClose())) {
				// populate the "not required" message for closed stage with no
				// risk assessment
				BookmarkDto stageBookmark = createBookmark(BookmarkConstants.FORM_MESSAGE,
						sdmRiskAssmtFormDto.getFormtxt1());
				bookmarkNonFrmGrpList.add(stageBookmark);
			} else {
				// populate the "not complete" message for open stage with no
				// risk assessment
				BookmarkDto stageBookmark = createBookmark(BookmarkConstants.FORM_MESSAGE,
						sdmRiskAssmtFormDto.getFormtxt2());
				bookmarkNonFrmGrpList.add(stageBookmark);
			}
		} else if (!FormConstants.MATURE_EVENT_STATUS.contains(sdmRiskAssmtFormDto.getRiskEvent().getCdEventStatus())) {
			if (FormConstants.IND_Y.equals(sdmRiskAssmtFormDto.getStageDto().getIndStageClose())) {
				// populate the "not required" message for closed stage with
				// incomplete
				// assessment
				BookmarkDto stageBookmark = createBookmark(BookmarkConstants.FORM_MESSAGE,
						sdmRiskAssmtFormDto.getFormtxt1());
				bookmarkNonFrmGrpList.add(stageBookmark);
			} else {
				// populate the "not complete" message for open stage with
				// incomplete assessment
				BookmarkDto stageBookmark = createBookmark(BookmarkConstants.FORM_MESSAGE,
						sdmRiskAssmtFormDto.getFormtxt2());
				bookmarkNonFrmGrpList.add(stageBookmark);
			}
		}

		if (!hideBookmarks) {
			FormDataGroupDto formBodyGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_FORM_BODY,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> formBodyGrpList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> formbodayBookMarkList = new ArrayList<BookmarkDto>();
			for (String primCaregiver : sdmRiskAssmtFormDto.getPrimCaregiverList()) {
				BookmarkDto primCaregiveBookmark = createBookmark(BookmarkConstants.PRIMARY_CAREGIVER, primCaregiver);
				formbodayBookMarkList.add(primCaregiveBookmark);
			}
			for (String secCaregiver : sdmRiskAssmtFormDto.getSecCaregiverList()) {
				BookmarkDto secCaregiveBookmark = createBookmark(BookmarkConstants.SECONDARY_CAREGIVER, secCaregiver);
				formbodayBookMarkList.add(secCaregiveBookmark);
			}

			/** SCORED RISK LEVEL **/

			if (sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getIndSection1Complete()) {
				FormDataGroupDto score = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE,
						FormGroupsConstants.TMPLAT_FORM_BODY);
				List<BookmarkDto> scorebookMmarkList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> scoreFrmGrpList = new ArrayList<FormDataGroupDto>();
				scorebookMmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.SCORE_LEVEL,
						sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getScoredRiskLevelCode(),
						CodesConstant.CSDMRLVL));
				scorebookMmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.SCORE_NEGLECTLEVEL,
						sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getFutureNeglectRiskLevelCode(),
						CodesConstant.CSDMRLVL));
				scorebookMmarkList.add(createBookmark(BookmarkConstants.SCORE_NEGLECTSCORE,
						sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getFutureNeglectScore()));
				scorebookMmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.SCORE_ABUSELEVEL,
						sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getFutureAbuseRiskLevelCode(),
						CodesConstant.CSDMRLVL));
				scorebookMmarkList.add(createBookmark(BookmarkConstants.SCORE_ABUSESCORE,
						sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getFutureAbuseScore()));
				scorebookMmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.SCORE_FINALSCORE,
						sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getFinalRiskLevelCode(), CodesConstant.CSDMRLVL));
				// ... adding in first of 3 override alternatives to
				// TMPLAT_SCORE_OVRD
				FormDataGroupDto noOverrides = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD,
						FormGroupsConstants.TMPLAT_SCORE);
				List<BookmarkDto> noOverridesbookMmarkList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> noOverridesFrmGrpList = new ArrayList<FormDataGroupDto>();
				if (FormConstants.CDOVRIDE_NOOVERRIDE
						.equalsIgnoreCase(sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getOverrideCode())) {
					noOverridesbookMmarkList.add(createBookmark(BookmarkConstants.SCORE_OVRD_BOX, FormConstants.X));
				} else {
					FormDataGroupDto noOverridesSpace = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD_SPACE,
							FormGroupsConstants.TMPLAT_SCORE_OVRD);
					noOverridesFrmGrpList.add(noOverridesSpace);
				}
				noOverridesbookMmarkList
						.add(createBookmark(BookmarkConstants.SCORE_OVRD_TEXT, BookmarkConstants.SCORE_OVRD_TEXT_1));
				noOverrides.setBookmarkDtoList(noOverridesbookMmarkList);
				noOverrides.setFormDataGroupList(noOverridesFrmGrpList);
				scoreFrmGrpList.add(noOverrides);
				// ... adding in second of 3 override alternatives to
				// TMPLAT_SCORE_OVRD
				FormDataGroupDto policyOverrides = createFormDataGroup(BookmarkConstants.TMPLAT_SCORE_OVRD,
						BookmarkConstants.TMPLAT_SCORE);
				List<BookmarkDto> policyOverridesbookMmarkList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> policyOverridesFrmGrpList = new ArrayList<FormDataGroupDto>();

				if (FormConstants.CDOVRIDE_POLICYOVERRIDE
						.equalsIgnoreCase(sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getOverrideCode())) {
					policyOverridesbookMmarkList.add(createBookmark(BookmarkConstants.SCORE_OVRD_BOX, FormConstants.X));
				} else {
					FormDataGroupDto noOverridesSpace = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD_SPACE,
							FormGroupsConstants.TMPLAT_SCORE_OVRD);
					policyOverridesFrmGrpList.add(noOverridesSpace);
				}
				policyOverridesbookMmarkList
						.add(createBookmark(BookmarkConstants.SCORE_OVRD_TEXT, BookmarkConstants.SCORE_OVRD_TEXT_2));
				// ... adding first of four policy overrides to
				// TMPLAT_SCORE_OVRD_POL
				FormDataGroupDto policyOverrides1 = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD_POL,
						FormGroupsConstants.TMPLAT_SCORE_OVRD);
				List<BookmarkDto> policyOverrides1bookMmarkList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> policyOverrides1FrmGrpList = new ArrayList<FormDataGroupDto>();

				if (FormConstants.IND_Y
						.equals(sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getIsPOChildLessThanThreeInjured())) {
					policyOverrides1bookMmarkList
							.add(createBookmark(BookmarkConstants.SCORE_OVRD_POL_BOX, FormConstants.X));
				} else {
					FormDataGroupDto policy1 = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD_POL_SPACE,
							FormGroupsConstants.TMPLAT_SCORE_OVRD_POL);
					policyOverrides1FrmGrpList.add(policy1);
				}
				policyOverrides1bookMmarkList.add(
						createBookmark(BookmarkConstants.SCORE_OVRD_POL_TEXT, BookmarkConstants.SCORE_OVRD_POL_TEXT_1));
				policyOverrides1.setBookmarkDtoList(policyOverrides1bookMmarkList);
				policyOverrides1.setFormDataGroupList(policyOverrides1FrmGrpList);
				policyOverridesFrmGrpList.add(policyOverrides1);
				// ... adding second of four policy overrides to
				// TMPLAT_SCORE_OVRD_POL
				FormDataGroupDto policyOverrides2 = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD_POL,
						FormGroupsConstants.TMPLAT_SCORE_OVRD_POL);
				List<BookmarkDto> policyOverrides2bookMmarkList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> policyOverrides2FrmGrpList = new ArrayList<FormDataGroupDto>();

				if (FormConstants.IND_Y
						.equals(sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getIsPOChildSexualAbuse())) {
					policyOverrides2bookMmarkList
							.add(createBookmark(BookmarkConstants.SCORE_OVRD_POL_BOX, FormConstants.X));
				} else {
					FormDataGroupDto policy2 = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD_POL_SPACE,
							FormGroupsConstants.TMPLAT_SCORE_OVRD_POL);
					policyOverrides2FrmGrpList.add(policy2);
				}
				policyOverrides2bookMmarkList.add(
						createBookmark(BookmarkConstants.SCORE_OVRD_POL_TEXT, BookmarkConstants.SCORE_OVRD_POL_TEXT_2));
				policyOverrides2.setBookmarkDtoList(policyOverrides2bookMmarkList);
				policyOverrides2.setFormDataGroupList(policyOverrides2FrmGrpList);
				policyOverridesFrmGrpList.add(policyOverrides2);
				// ... adding third of four policy overrides to
				// TMPLAT_SCORE_OVRD_POL
				FormDataGroupDto policyOverrides3 = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD_POL,
						FormGroupsConstants.TMPLAT_SCORE_OVRD);
				List<BookmarkDto> policyOverrides3bookMmarkList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> policyOverrides3FrmGrpList = new ArrayList<FormDataGroupDto>();

				if (FormConstants.IND_Y
						.equals(sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getIsPOChildLessThanSixteenInjured())) {
					policyOverrides3bookMmarkList
							.add(createBookmark(BookmarkConstants.SCORE_OVRD_POL_BOX, FormConstants.X));
				} else {
					FormDataGroupDto policy3 = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD_POL_SPACE,
							FormGroupsConstants.TMPLAT_SCORE_OVRD_POL);
					policyOverrides3FrmGrpList.add(policy3);
				}
				policyOverrides3bookMmarkList.add(
						createBookmark(BookmarkConstants.SCORE_OVRD_POL_TEXT, BookmarkConstants.SCORE_OVRD_POL_TEXT_3));
				policyOverrides3.setBookmarkDtoList(policyOverrides3bookMmarkList);
				policyOverrides3.setFormDataGroupList(policyOverrides3FrmGrpList);
				policyOverridesFrmGrpList.add(policyOverrides3);
				// ... adding fourth of four policy overrides to
				// TMPLAT_SCORE_OVRD_POL
				FormDataGroupDto policyOverrides4 = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD_POL,
						FormGroupsConstants.TMPLAT_SCORE_OVRD);
				List<BookmarkDto> policyOverrides4bookMmarkList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> policyOverrides4FrmGrpList = new ArrayList<FormDataGroupDto>();

				if (FormConstants.IND_Y.equals(sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getIsPOChildDeath())) {
					policyOverrides4bookMmarkList
							.add(createBookmark(BookmarkConstants.SCORE_OVRD_POL_BOX, FormConstants.X));
				} else {
					FormDataGroupDto policy4 = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD_POL_SPACE,
							FormGroupsConstants.TMPLAT_SCORE_OVRD_POL);
					policyOverrides4FrmGrpList.add(policy4);
				}
				policyOverrides4bookMmarkList.add(
						createBookmark(BookmarkConstants.SCORE_OVRD_POL_TEXT, BookmarkConstants.SCORE_OVRD_POL_TEXT_4));
				FormDataGroupDto doubleRowSpan = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD_POL_NEW,
						FormGroupsConstants.TMPLAT_SCORE_OVRD_POL);
				List<BookmarkDto> doubleRowSpanbookMmarkList = new ArrayList<BookmarkDto>();

				doubleRowSpanbookMmarkList.add(createBookmark(BookmarkConstants.SCORE_OVRD_POL_NEW_LINE,
						BookmarkConstants.SCORE_OVRD_POL_TEXT_4_PLUS));
				doubleRowSpan.setBookmarkDtoList(doubleRowSpanbookMmarkList);
				policyOverrides4FrmGrpList.add(doubleRowSpan);
				policyOverrides4.setBookmarkDtoList(policyOverrides4bookMmarkList);
				policyOverrides4.setFormDataGroupList(policyOverrides4FrmGrpList);
				policyOverridesFrmGrpList.add(policyOverrides4);
				policyOverrides.setBookmarkDtoList(policyOverridesbookMmarkList);
				policyOverrides.setFormDataGroupList(policyOverridesFrmGrpList);
				scoreFrmGrpList.add(policyOverrides);
				// ... adding in third of 3 override alternatives to
				// TMPLAT_SCORE_OVRD
				FormDataGroupDto discretionaryOverrides = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD,
						FormGroupsConstants.TMPLAT_SCORE);
				List<BookmarkDto> discretionaryOverridesbookMmarkList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> discretionaryOverridesFrmGrpList = new ArrayList<FormDataGroupDto>();

				if (FormConstants.CDOVRIDE_DISCRETIONARYOVERRID
						.equalsIgnoreCase(sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getOverrideCode())) {
					discretionaryOverridesbookMmarkList
							.add(createBookmark(BookmarkConstants.SCORE_OVRD_BOX, FormConstants.X));
				} else {
					FormDataGroupDto noOverridesSpace = createFormDataGroup(FormGroupsConstants.TMPLAT_SCORE_OVRD_SPACE,
							FormGroupsConstants.TMPLAT_SCORE_OVRD);
					discretionaryOverridesFrmGrpList.add(noOverridesSpace);
				}
				discretionaryOverridesbookMmarkList
						.add(createBookmark(BookmarkConstants.SCORE_OVRD_TEXT, BookmarkConstants.SCORE_OVRD_TEXT_3));
				discretionaryOverrides.setBookmarkDtoList(discretionaryOverridesbookMmarkList);
				discretionaryOverrides.setFormDataGroupList(discretionaryOverridesFrmGrpList);
				scoreFrmGrpList.add(discretionaryOverrides);
				// ... adding override reason comment section
				if (null != sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getOverrideReason()) {
					scorebookMmarkList.add(createBookmark(BookmarkConstants.SCORE_COMMENTS,
							sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getOverrideReason()));
				}

				score.setFormDataGroupList(scoreFrmGrpList);
				score.setBookmarkDtoList(scorebookMmarkList);
				formBodyGrpList.add(score);
			}

			/** QUESTIONS AND ANSWERS **/

			List<SDMRiskAssessmentQuestionDto> questions = sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getQuestions();

			for (SDMRiskAssessmentQuestionDto questionDto : questions) {
				FormDataGroupDto question = createFormDataGroup(FormGroupsConstants.TMPLAT_QSTN,
						FormGroupsConstants.TMPLAT_FORM_BODY);
				List<BookmarkDto> questionbookMmarkList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> questionFrmGrpList = new ArrayList<FormDataGroupDto>();

				questionbookMmarkList.add(createBookmark(BookmarkConstants.QSTN_CODE, questionDto.getQuestionCode()));
				questionbookMmarkList.add(createBookmark(BookmarkConstants.QSTN_TEXT, questionDto.getQuestionText()));

				List<SDMRiskAssessmentAnswerDto> answers = questionDto.getAnswers();
				for (SDMRiskAssessmentAnswerDto answerDto : answers) {
					FormDataGroupDto answer = createFormDataGroup(FormGroupsConstants.TMPLAT_QSTN_ANSW,
							FormGroupsConstants.TMPLAT_QSTN);
					List<BookmarkDto> answerbookMmarkList = new ArrayList<BookmarkDto>();
					List<FormDataGroupDto> answerFrmGrpList = new ArrayList<FormDataGroupDto>();

					if (StringUtil.isValid(answerDto.getResponseCode())) {
						answerbookMmarkList.add(createBookmark(BookmarkConstants.QSTN_ANSW_BOX, FormConstants.X));
					} else {
						FormDataGroupDto answerSpace = createFormDataGroup(FormGroupsConstants.TMPLAT_QSTN_ANSW_SPACE,
								FormGroupsConstants.TMPLAT_QSTN_ANSW);
						answerFrmGrpList.add(answerSpace);
					}
					answerbookMmarkList
							.add(createBookmark(BookmarkConstants.QSTN_ANSW_TEXT, answerDto.getAnswerText()));
					if (FormConstants.ANSWERCODE_WITH_FOLLOWUPS.contains(answerDto.getAnswerCode())) {
						List<SDMRiskAssessmentFollowupDto> followupQuestions = answerDto.getFollowupQuestions();
						for (SDMRiskAssessmentFollowupDto followupDto : followupQuestions) {
							FormDataGroupDto followup = createFormDataGroup(FormGroupsConstants.TMPLAT_QSTN_ANSW_FUP,
									FormGroupsConstants.TMPLAT_QSTN_ANSW);
							List<BookmarkDto> followupbookMmarkList = new ArrayList<BookmarkDto>();
							List<FormDataGroupDto> followupFrmGrpList = new ArrayList<FormDataGroupDto>();

							if (FormConstants.IND_Y.equals(followupDto.getIndRaFollowup())) {
								followupbookMmarkList
										.add(createBookmark(BookmarkConstants.QSTN_ANSW_FUP_BOX, FormConstants.X));
							} else {
								FormDataGroupDto answerFollowupSpace = createFormDataGroup(
										FormGroupsConstants.TMPLAT_QSTN_ANSW_FUP_SPACE,
										FormGroupsConstants.TMPLAT_QSTN_ANSW_FUP);
								followupFrmGrpList.add(answerFollowupSpace);
							}
							followupbookMmarkList.add(createBookmark(BookmarkConstants.QSTN_ANSW_FUP_QUES,
									followupDto.getFollowupQuestionText()));
							if (FormConstants.SECONDARY_FOLLOWUP_QUESTIONS.contains(questionDto.getQuestionCode())) {
								List<SDMRiskAssmtSecondaryFollowupDto> secondaryFollowupQuestions = followupDto
										.getSecondaryFollowupQuestions();
								for (SDMRiskAssmtSecondaryFollowupDto secondaryFollowupDto : secondaryFollowupQuestions) {
									FormDataGroupDto secondFollowUp = createFormDataGroup(
											FormGroupsConstants.TMPLAT_QSTN_ANSW_FUP_2UP,
											FormGroupsConstants.TMPLAT_QSTN_ANSW_FUP);
									List<BookmarkDto> secondFollowUpbookMmarkList = new ArrayList<BookmarkDto>();
									List<FormDataGroupDto> secondFollowUpFrmGrpList = new ArrayList<FormDataGroupDto>();

									if (FormConstants.IND_Y.equals(secondaryFollowupDto.getIndSecFollowupLookup())) {
										secondFollowUpbookMmarkList.add(createBookmark(
												BookmarkConstants.QSTN_ANSW_FUP_2UP_BOX, FormConstants.X));
									} else {
										FormDataGroupDto secondaryFollowupSpace = createFormDataGroup(
												FormGroupsConstants.TMPLAT_QSTN_ANSW_FUP_2UP_SPACE,
												FormGroupsConstants.TMPLAT_QSTN_ANSW_FUP_2UP);
										secondFollowUpFrmGrpList.add(secondaryFollowupSpace);
									}
									secondFollowUpbookMmarkList
											.add(createBookmark(BookmarkConstants.QSTN_ANSW_FUP_2UP_QUES,
													secondaryFollowupDto.getSecondaryFollowupQnText()));
									secondFollowUp.setBookmarkDtoList(secondFollowUpbookMmarkList);
									secondFollowUp.setFormDataGroupList(secondFollowUpFrmGrpList);
									followupFrmGrpList.add(secondFollowUp);
								}
							}
							followup.setBookmarkDtoList(followupbookMmarkList);
							followup.setFormDataGroupList(followupFrmGrpList);
							answerFrmGrpList.add(followup);
						}
					}
					answer.setBookmarkDtoList(answerbookMmarkList);
					answer.setFormDataGroupList(answerFrmGrpList);
					questionFrmGrpList.add(answer);
				}
				question.setBookmarkDtoList(questionbookMmarkList);
				question.setFormDataGroupList(questionFrmGrpList);
				formBodyGrpList.add(question);
			}
			formBodyGroup.setBookmarkDtoList(formbodayBookMarkList);
			formBodyGroup.setFormDataGroupList(formBodyGrpList);
			formDataGroupList.add(formBodyGroup);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}
	
	//QCR 62702 - SDM Removal to show/hide SDM word in the Form title
	private boolean showSDM(SdmRiskAssmtFormDto sdmRiskAssmtFormDto){
		boolean showSDMInTitle=false;
		Date dtSDMRemoval=codesDao.getAppRelDate(ServiceConstants.CRELDATE_SDM_REMOVAL_2020);	
		
		if (!ObjectUtils.isEmpty(sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getDateAssessmentCompleted())) {
			if (sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getDateAssessmentCompleted()
					.compareTo(dtSDMRemoval) < 0) {
				showSDMInTitle = true;
			}
		} else {
			if (!ObjectUtils.isEmpty(sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getCreatedDate())
					&& sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getCreatedDate().compareTo(dtSDMRemoval) < 0
					&& !ObjectUtils.isEmpty(sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getdateStageClosure())
					&& sdmRiskAssmtFormDto.getsDMRiskAssessmentDto().getdateStageClosure()
							.compareTo(dtSDMRemoval) < 0) {
				showSDMInTitle = true;
			}
		}
		return showSDMInTitle;
	}
}
