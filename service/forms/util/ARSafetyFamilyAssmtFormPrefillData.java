package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.tx.state.dfps.common.exception.FormsException;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtAreaValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtFactorValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyFamilyAssmtFormDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.EventDto;

@Component
public class ARSafetyFamilyAssmtFormPrefillData extends DocumentServiceUtil {

	private static final String INITIAL_ASSESSMENT = "Initial Safety and Family Needs Assessment";
	private static final String CLOSURE_ASSESSMENT = "Closure Safety and Family Needs Assessment";
	private static final String INITIAL_ACTION_HEADER = "Immediate Action";
	private static final String CLOSURE_ACTION_HEADER = "Action Taken";
	private static final String INITIAL_ASSESSMENT_HEADER = "Further Assessment";
	private static final String CLOSURE_ASSESSMENT_HEADER = "Final Assessment";
	private static final int IN_ACTION_AREA = 12;
	private static final int IN_IMMEDIATE_ACTION_ANSWER = 7;
	private static final int IN_FURTHER_ASSESSMENT_ANSWER = 9;
	private static final int CL_ACTION_AREA = 25;
	private static final int CL_IMMEDIATE_ACTION_ANSWER = 76;
	private static final int CL_FURTHER_ASSESSMENT_ANSWER = 78;
	private static final int IN_ACTION_EXPLAIN = 8;
	private static final int CL_ACTION_EXPLAIN = 77;
	private static final String ANSWER_HEADING = "Answer: ";
	private static final String APPROVED = "APRV";
	private static final String INITIAL = "INITIAL";

	@Autowired
	EventDao eventDao;

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		ARSafetyFamilyAssmtFormDto arFormDto = (ARSafetyFamilyAssmtFormDto) parentDtoobj;

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkDtoList = new ArrayList<BookmarkDto>();

		int ctr = 0;// counter for initial and closure assmts

		/**
		 * GET BOTH ASSESSMENTS AND DISPLAY INITIAL LEGACY SAFETY ASSESSMENT
		 */
		List<ARSafetyAssmtValueDto> arSaVb = arFormDto.getaRSafetyAssmtValueDto();
		ARSafetyAssmtValueDto initialSA = new ARSafetyAssmtValueDto();
		ARSafetyAssmtValueDto closureSA = new ARSafetyAssmtValueDto();
		// only display assessments if the list size > 0
		if (null != arSaVb && 0 < arSaVb.size()) {
			for (ARSafetyAssmtValueDto saBean : arSaVb) {
				if (0 == ctr++) {
					initialSA = saBean;
				} else {
					closureSA = saBean;
				}
			}
			if (arFormDto.getIndAssmentStatus().equalsIgnoreCase("INITIAL")) {
				try {
					fillSafetyAssessment(preFillData, initialSA, null, formDataGroupList,arFormDto.getIndAssmentStatus());
				} catch (Exception e) {
					FormsException formsException = new FormsException(e.getMessage());
					formsException.initCause(e);
					throw formsException;
				}
			}
		}

		/** DISPLAY CLOSURE LEGACY SAFETY ASSESSMENT **/

		if (arFormDto.getIndAssmentStatus().equalsIgnoreCase("CLOSURE") && 0 < closureSA.getIdArSafetyAssmt()) {
			try {
				fillSafetyAssessment(preFillData, closureSA, initialSA,formDataGroupList,arFormDto.getIndAssmentStatus());
			} catch (Exception e) {
				FormsException formsException = new FormsException(e.getMessage());
				formsException.initCause(e);
				throw formsException;
			}
		}

		/**
		 * Populating the non form group data into prefill data
		 */

		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME, arFormDto.getNmCase());
		BookmarkDto bookmarkTitleCaseNo = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER, arFormDto.getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseName);
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNo);
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);

		return preFillData;

	}

	/**
	 * Fills in safety assessment information
	 * 
	 * @param preFillData
	 * @param p_safetyAssessment
	 * @param p_initialSA
	 *            The closure needs to be able to look up answers on initial to
	 *            determine if we should show the discuss question
	 * @param string 
	 * @throws Exception
	 */
	@SuppressWarnings("null")
	private void fillSafetyAssessment(PreFillDataServiceDto preFillData, ARSafetyAssmtValueDto p_safetyAssessment,
			ARSafetyAssmtValueDto p_initialSA, List<FormDataGroupDto> formDataGroupList, String indSafetyClosure) {

		EventDto eventValueBean = null;

		if (p_safetyAssessment.getIdEvent() != 0) {

			eventValueBean = (EventDto) eventDao.getEventByid(p_safetyAssessment.getIdEvent().longValue());

		}
		// tempSafetyAssmtFrmGrpDto is the group so the header always displays

		/**
		 * prefill data for group - TMPLAT_SA
		 */

		List<FormDataGroupDto> tempSafetyAssmtFrmGrpDtoList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto tempSafetyAssmtFrmGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SA,
				FormConstants.EMPTY_STRING);

		/**
		 * prefill data for group - TMPLAT_SA_HASDATA
		 */

		List<FormDataGroupDto> tempSafetyAssmtDataFrmGrpDtoList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto tempSafetyAssmtDataFrmGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SA_HASDATA,
				FormGroupsConstants.TMPLAT_SA);

		/**
		 * Bookmark List for group - TMPLAT_SA_HASDATA
		 */

		List<BookmarkDto> bookmarkSADataList = new ArrayList<BookmarkDto>();

		if (INITIAL.equalsIgnoreCase(indSafetyClosure)) {

			/**
			 * prefill data for Bookmark - SA_INITIAL_OR_CLOSURE_HEADER
			 */

			List<BookmarkDto> bookmarkHeaderList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkHeaderDto = createBookmark(BookmarkConstants.SA_INITIAL_OR_CLOSURE_HEADER,
					INITIAL_ASSESSMENT);
			bookmarkHeaderList.add(bookmarkHeaderDto);
			tempSafetyAssmtFrmGrpDto.setBookmarkDtoList(bookmarkHeaderList);

			/**
			 * prefill data for Bookmark - SA_ACTION_HEADER and SA_ASSESS_HEADER
			 */

			BookmarkDto bookmarkActionHeaderDto = createBookmark(BookmarkConstants.SA_ACTION_HEADER,
					INITIAL_ACTION_HEADER);
			BookmarkDto bookmarkAssmtHeaderDto = createBookmark(BookmarkConstants.SA_ASSESS_HEADER,
					INITIAL_ASSESSMENT_HEADER);
			bookmarkSADataList.add(bookmarkActionHeaderDto);
			bookmarkSADataList.add(bookmarkAssmtHeaderDto);

			// Add the approval date if record present
			if (p_safetyAssessment.getIdEvent() != 0 && eventValueBean.getCdEventStatus() != null
					&& eventValueBean.getCdEventStatus().equals(APPROVED)) {
				/**
				 * prefill data for group - TMPLAT_SA_INITIAL
				 */

				FormDataGroupDto tempSafetyAssmtInitialFrmGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SA_INITIAL, FormConstants.EMPTY_STRING);

				/**
				 * TMPLAT_SA_INITIAL - Added data to
				 * tempSafetyAssmtDataFrmGrpDtoList
				 */

				tempSafetyAssmtDataFrmGrpDtoList.add(tempSafetyAssmtInitialFrmGrpDto);

				/**
				 * prefill data for Bookmark - SA_APPROVED_DATE
				 */

				List<BookmarkDto> bookmarkSAApprovedDateList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkapprovalDto = createBookmark(BookmarkConstants.SA_APPROVED_DATE,
						TypeConvUtil.formDateFormat(eventValueBean.getDtEventOccurred()));
				bookmarkSAApprovedDateList.add(bookmarkapprovalDto);
				tempSafetyAssmtInitialFrmGrpDto.setBookmarkDtoList(bookmarkSAApprovedDateList);

			}
		} else {

			/**
			 * prefill data for Bookmark - SA_INITIAL_OR_CLOSURE_HEADER
			 */

			List<BookmarkDto> bookmarkHeaderList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkHeaderDto = createBookmark(BookmarkConstants.SA_INITIAL_OR_CLOSURE_HEADER,
					CLOSURE_ASSESSMENT);
			bookmarkHeaderList.add(bookmarkHeaderDto);
			tempSafetyAssmtFrmGrpDto.setBookmarkDtoList(bookmarkHeaderList);

			/**
			 * prefill data for Bookmark - SA_ACTION_HEADER and SA_ASSESS_HEADER
			 */

			BookmarkDto bookmarkActionHeaderDto = createBookmark(BookmarkConstants.SA_ACTION_HEADER,
					CLOSURE_ACTION_HEADER);
			BookmarkDto bookmarkAssmtHeaderDto = createBookmark(BookmarkConstants.SA_ASSESS_HEADER,
					CLOSURE_ASSESSMENT_HEADER);
			bookmarkSADataList.add(bookmarkActionHeaderDto);
			bookmarkSADataList.add(bookmarkAssmtHeaderDto);
			// tempSafetyAssmtDataFrmGrpDto.setBookmarkDtoList(bookmarkSAHdrList);

		}
		List<ARSafetyAssmtAreaValueDto> safety_areas = new ArrayList<ARSafetyAssmtAreaValueDto>();
		if (null != p_safetyAssessment && 0 < p_safetyAssessment.getIdEvent()) {
			if (0 < p_safetyAssessment.getaRSafetyAssmtAreas().size()) {
				safety_areas = p_safetyAssessment.getaRSafetyAssmtAreas();
			}
		}

		// Only add the group to the form if there was something to display
		boolean safety_threats_header_display = false;
		boolean conclusion_header_display = false;
		if (null != safety_areas && 0 < safety_areas.size()) {
			for (ARSafetyAssmtAreaValueDto current_area : safety_areas) {
				List<FormDataGroupDto> tempSafteyAreaGrpDtoList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto tempSafetyAssmtAreaFrmGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SA_SAFETY_AREA, FormGroupsConstants.TMPLAT_SA_HASDATA);

				int id_area = current_area.getIdArea();
				// Display safety threats header at beginning
				if (!safety_threats_header_display) {
					safety_threats_header_display = true;

					/**
					 * prefill data for group - TMPLAT_SA_SAFETY_THREATS
					 */

					FormDataGroupDto tempSafetyThreatsHeaderFrmGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SA_SAFETY_THREATS, FormGroupsConstants.TMPLAT_SA_SAFETY_AREA);
					tempSafteyAreaGrpDtoList.add(tempSafetyThreatsHeaderFrmGroupDto);

				}
				// Display conclusion header when those areas appear
				if ((IN_ACTION_AREA == id_area || CL_ACTION_AREA == id_area) && !conclusion_header_display) {
					conclusion_header_display = true;

					/**
					 * prefill data for group - TMPLAT_SA_CONCLUSION
					 */

					FormDataGroupDto tempSafetyAssmtCnclnHeaderFrmGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SA_CONCLUSION, FormGroupsConstants.TMPLAT_SA_SAFETY_AREA);
					tempSafteyAreaGrpDtoList.add(tempSafetyAssmtCnclnHeaderFrmGrpDto);

				}

				/**
				 * prefill data for group - SA_AREA
				 */

				List<BookmarkDto> bookmarkSAAreaList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkSAAreaDto = createBookmark(BookmarkConstants.SA_AREA, current_area.getArea());
				bookmarkSAAreaList.add(bookmarkSAAreaDto);
				tempSafetyAssmtAreaFrmGrpDto.setBookmarkDtoList(bookmarkSAAreaList);

				// display factors
				List<ARSafetyAssmtFactorValueDto> factors = new ArrayList<ARSafetyAssmtFactorValueDto>();
				factors = (List<ARSafetyAssmtFactorValueDto>) p_safetyAssessment.getaRSafetyAssmtFactors();
				if (null != factors && 0 < factors.size()) {
					for (ARSafetyAssmtFactorValueDto currentFactor : factors) {
						if (null == currentFactor.getResponse()) {
							continue; // Skip the areas that were not completed
						} else if (current_area.getIdArea() == currentFactor.getIdArea()) {
							// The discuss factor value should only display if
							// the closure value is 'N' and initial value is
							// 'Y'.
							if (currentFactor.getFactorDepVal() != null
									&& currentFactor.getFactorDepVal().equals("N")) {
								// The closure needs to be able to look up
								// answers on initial to determine if we should
								// show the discuss question
								ARSafetyAssmtFactorValueDto closureFactor = getFactor(p_safetyAssessment,
										currentFactor.getIdFactorDep());
								ARSafetyAssmtFactorValueDto initialFactor = getFactor(p_initialSA,
										closureFactor.getIdFactorInitial());
								if (closureFactor == null || closureFactor.getResponse() == null
										|| initialFactor == null || initialFactor.getResponse() == null
										|| !(closureFactor.getResponse().equals("N")
												&& initialFactor.getResponse().equals("Y"))) {
									continue;
								}
							}
							// Fill in assessment header fields as they are
							// encountered.
							if (currentFactor.getIdFactor() == IN_IMMEDIATE_ACTION_ANSWER
									|| currentFactor.getIdFactor() == CL_IMMEDIATE_ACTION_ANSWER) {

								/**
								 * prefill data for Bookmark - SA_ACTION
								 */

								BookmarkDto bookmarkActionAnsDto = createBookmark(BookmarkConstants.SA_ACTION,
										currentFactor.getResponse());
								bookmarkSADataList.add(bookmarkActionAnsDto);

							}
							if (currentFactor.getIdFactor() == IN_FURTHER_ASSESSMENT_ANSWER
									|| currentFactor.getIdFactor() == CL_FURTHER_ASSESSMENT_ANSWER) {

								/**
								 * prefill data for Bookmark - SA_ASSESS
								 */

								BookmarkDto bookmarkAsmtAnsDto = createBookmark(BookmarkConstants.SA_ASSESS,
										currentFactor.getResponse());
								bookmarkSADataList.add(bookmarkAsmtAnsDto);

							}

							/**
							 * prefill data for group - TMPLAT_SA_FACTOR
							 */

							List<FormDataGroupDto> tempFactorGrpList = new ArrayList<FormDataGroupDto>();
							FormDataGroupDto tempFactorFrmGrpDto = createFormDataGroup(
									FormGroupsConstants.TMPLAT_SA_FACTOR, FormGroupsConstants.TMPLAT_SA_SAFETY_AREA);

							// Level 2 questions get spaced out
							String answer = "";
							if (currentFactor.getIdFactor() == IN_ACTION_EXPLAIN
									|| currentFactor.getIdFactor() == CL_ACTION_EXPLAIN
									|| (currentFactor.getIndFactor2() != null
											&& currentFactor.getIndFactor2().equals("Y"))
									|| (currentFactor.getFactorDepVal() != null
											&& (currentFactor.getFactorDepVal().equals("Y")
													|| currentFactor.getFactorDepVal().equals("I")))) {

								/**
								 * Prefill for group - TMPLAT_SPACE1
								 */

								FormDataGroupDto tempSpace1FrmGrpDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SPACE1, FormGroupsConstants.TMPLAT_SA_FACTOR);
								tempFactorGrpList.add(tempSpace1FrmGrpDto);

								/**
								 * Prefill for group - TMPLAT_SPACE2
								 */

								FormDataGroupDto tempSpace2FrmGrpDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SPACE2, FormGroupsConstants.TMPLAT_SA_FACTOR);
								tempFactorGrpList.add(tempSpace2FrmGrpDto);

							} else {
								// Level 1 answers start with Answer header
								answer += ANSWER_HEADING;
							}

							// Two answer types are 'T' Text and 'R' Radio
							if (currentFactor.getIndFactorType().equals("T")) {
								answer += currentFactor.getResponse();
							}

							else {
								// Radio response should never be null but we
								// had some bad devl data, so avoid for testing
								// purposes
								String answer_code = currentFactor.getResponse() == null ? ""
										: currentFactor.getResponse().trim();
								String answer_translate = indTranslate(answer_code);
								if (!answer_translate.equals("")) {
									answer += answer_translate;
								} else {
									answer += answer_code;
								}
							}

							/**
							 * Prefill for group - SA_QUESTION
							 */

							List<BookmarkDto> bookmarkSAFactorList = new ArrayList<BookmarkDto>();
							BookmarkDto bookmarkSAQuestion = createBookmark(BookmarkConstants.SA_QUESTION,
									currentFactor.getFactor());
							bookmarkSAFactorList.add(bookmarkSAQuestion);

							/**
							 * Prefill for group - SA_ANSWER
							 */

							BookmarkDto bookmarkSAAnswer = createBookmark(BookmarkConstants.SA_ANSWER, answer);
							bookmarkSAFactorList.add(bookmarkSAAnswer);

							tempFactorFrmGrpDto.setBookmarkDtoList(bookmarkSAFactorList);

							/**
							 * Added data to tempSafteyAreaGrpDtoList -
							 * TMPLAT_SA_FACTOR
							 */

							tempFactorFrmGrpDto.setFormDataGroupList(tempFactorGrpList);
							tempSafteyAreaGrpDtoList.add(tempFactorFrmGrpDto);

						}
					}
				}
				/**
				 * Added data to tempSafetyAssmtDataFrmGrpDtoList -
				 * TMPLAT_SA_SAFETY_AREA
				 */

				tempSafetyAssmtAreaFrmGrpDto.setFormDataGroupList(tempSafteyAreaGrpDtoList);
				tempSafetyAssmtDataFrmGrpDtoList.add(tempSafetyAssmtAreaFrmGrpDto);

			}

		}

		/**
		 * Added data to tempSafetyAssmtFrmGrpDtoList - TMPLAT_SA_HASDATA
		 * 
		 */

		tempSafetyAssmtDataFrmGrpDto.setFormDataGroupList(tempSafetyAssmtDataFrmGrpDtoList);
		tempSafetyAssmtDataFrmGrpDto.setBookmarkDtoList(bookmarkSADataList);
		tempSafetyAssmtFrmGrpDtoList.add(tempSafetyAssmtDataFrmGrpDto);

		/**
		 * Added data to formDataGroupList - TMPLAT_SA
		 * 
		 */

		tempSafetyAssmtFrmGrpDto.setFormDataGroupList(tempSafetyAssmtFrmGrpDtoList);
		formDataGroupList.add(tempSafetyAssmtFrmGrpDto);

	}

	/**
	 * Returns the answer to a question given an assessment bean and ID_FACTOR.
	 * Needed for display of answer changed discussion.
	 *
	 * @param input
	 *            ARSafetyAssmtValueBean p_safetyAssessment
	 * @param input
	 *            int p_idFactor
	 * @return output ARSafetyAssmtFactorValueBean factor
	 */
	public static ARSafetyAssmtFactorValueDto getFactor(ARSafetyAssmtValueDto p_safetyAssessment, int p_idFactor) {

		List<ARSafetyAssmtAreaValueDto> safety_areas = new ArrayList<ARSafetyAssmtAreaValueDto>(
				p_safetyAssessment.getaRSafetyAssmtAreas());

		if (null != safety_areas && 0 < safety_areas.size()) {
			for (ARSafetyAssmtAreaValueDto safetyArea : safety_areas) {
				if (null != safetyArea && null != safetyArea.getaRSafetyAssmtFactors()) {
					List<ARSafetyAssmtFactorValueDto> factors = new ArrayList<ARSafetyAssmtFactorValueDto>(
							safetyArea.getaRSafetyAssmtFactors());

					if (null != factors && 0 < factors.size()) {
						for (ARSafetyAssmtFactorValueDto factor : factors) {
							if (factor.getIdFactor() == p_idFactor) {
								return factor;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Ind translate.
	 *
	 * @param p_ind_char the p ind char
	 * @return the string
	 */
	private String indTranslate(String p_ind_char)
	  {
	    String retval = "";
	    if (p_ind_char != null) {
	      if (p_ind_char.equals("Y"))
	        retval = "Yes";
	      else if (p_ind_char.equals("N"))
	        retval = "No";
	      else if (p_ind_char.equals("I")) {
	        retval = "Needs More Information";
	      }
	    }
	    return retval;
	  }

}
