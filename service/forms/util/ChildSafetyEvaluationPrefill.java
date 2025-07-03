package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import us.tx.state.dfps.riskandsafetyassmt.dto.SafetyFactorEvalDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.ChildSafetyEvalFormDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service for
 * the Child Safety Eval Form on Safety and Risk Assessment Page. Prefill Data.
 * May 9, 2018- 10:29:31 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@Component
public class ChildSafetyEvaluationPrefill extends DocumentServiceUtil {

	/**
	 * Method Description: The method is used to return the Prefill Data for the
	 * Child Safety Evaluation form.
	 * 
	 * @param parentDtoobj
	 * @return PreFillDataServiceDto
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		ChildSafetyEvalFormDto childSafetyEvalFormDto = (ChildSafetyEvalFormDto) parentDtoobj;

		/**
		 * Description: Populating the non form group data into prefill data
		 * GroupName: None BookMark: Condition: None
		 */
		List<BookmarkDto> bookmarkDtoDefaultDtoList = new ArrayList<BookmarkDto>();
		BookmarkDto bkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				childSafetyEvalFormDto.getGenericCaseInfoDto().getNmStage());
		bookmarkDtoDefaultDtoList.add(bkTitleCaseName);

		BookmarkDto bkTitleCaseNum = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				childSafetyEvalFormDto.getGenericCaseInfoDto().getIdCase());
		bookmarkDtoDefaultDtoList.add(bkTitleCaseNum);

		BookmarkDto bkTxtSafetyEvlCmtsAr = createBookmark(BookmarkConstants.AVAIL_RESP_COMMENTS,
				childSafetyEvalFormDto.getSafteyEvalResDto().getSafetyEvalCmntsAr());
		bookmarkDtoDefaultDtoList.add(bkTxtSafetyEvlCmtsAr);

		BookmarkDto bkTxtSafetyEvlCmtsCnc = createBookmark(BookmarkConstants.CONCL_COMMENTS,
				childSafetyEvalFormDto.getSafteyEvalResDto().getSafetyEvalCmntsCnc());
		bookmarkDtoDefaultDtoList.add(bkTxtSafetyEvlCmtsCnc);

		BookmarkDto bkTxtSafetyEvlCmtsSoc = createBookmark(BookmarkConstants.SRC_CNCERN_COMMENTS,
				childSafetyEvalFormDto.getSafteyEvalResDto().getSafetyEvalCmntsSoc());
		bookmarkDtoDefaultDtoList.add(bkTxtSafetyEvlCmtsSoc);

		BookmarkDto bkcdSafetyEvlLegalActionTaken = createBookmarkWithCodesTable(BookmarkConstants.LGL_ACTION_TAKEN,
				childSafetyEvalFormDto.getSafteyEvalResDto().getCdSftyEvalLegalAct(), CodesConstant.CLEGLACT);
		bookmarkDtoDefaultDtoList.add(bkcdSafetyEvlLegalActionTaken);

		BookmarkDto bkdtLastUpdate = createBookmark(BookmarkConstants.EVAL_DATE_LAST_UPDATE,
				DateUtils.stringDt(childSafetyEvalFormDto.getSafteyEvalResDto().getDtLastUpdate()));
		bookmarkDtoDefaultDtoList.add(bkdtLastUpdate);

		// creating groups using formDatagroup
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		/**
		 * Description: All of the factors marked on the Safety Eval window for
		 * sources of concern GroupName: cfiv0601 BookMark:
		 * TMPLAT_SOURCES_OF_CONCERN Condition: CdSafetyEvalType = "S"
		 */
		for (SafetyFactorEvalDto safetyEval : childSafetyEvalFormDto.getSafetyFactorEvalDtoList()) {

			if (ServiceConstants.S.equalsIgnoreCase(safetyEval.getCdSafetyEvalType())) {
				FormDataGroupDto fdSourcesConcernGrp = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SOURCES_OF_CONCERN, FormConstants.EMPTY_STRING);

				List<BookmarkDto> grpSourcesConcernBkList = new ArrayList<BookmarkDto>();
				BookmarkDto bkcdSafetyFactor = createBookmarkWithCodesTable(BookmarkConstants.SOURCE_OF_CONCERN,
						safetyEval.getCdSafetyFactor(), CodesConstant.CSRCOCON);
				grpSourcesConcernBkList.add(bkcdSafetyFactor);

				fdSourcesConcernGrp.setBookmarkDtoList(grpSourcesConcernBkList);
				formDataGroupList.add(fdSourcesConcernGrp);
			}

			/**
			 * Description: All of the factors marked on the Safety Eval window
			 * for sources of concern GroupName: cfiv0601 BookMark:
			 * TMPLAT_AVAILABLE_RESPONSES Condition: CdSafetyEvalType = "A"
			 */

			if (ServiceConstants.ONLY_A.equalsIgnoreCase(safetyEval.getCdSafetyEvalType())) {
				FormDataGroupDto fdAvailableResponsesGrp = createFormDataGroup(
						FormGroupsConstants.TMPLAT_AVAILABLE_RESPONSES, FormConstants.EMPTY_STRING);

				List<BookmarkDto> grpAvailRespBkList = new ArrayList<BookmarkDto>();
				BookmarkDto bkcdSafetyFactor = createBookmarkWithCodesTable(BookmarkConstants.AVAILABLE_RESPONSES,
						safetyEval.getCdSafetyFactor(), CodesConstant.CAVLRESP);
				grpAvailRespBkList.add(bkcdSafetyFactor);

				fdAvailableResponsesGrp.setBookmarkDtoList(grpAvailRespBkList);
				formDataGroupList.add(fdAvailableResponsesGrp);
			}

			/**
			 * Description: Group inserts 'Yes' for action sufficient question
			 * GroupName: cfzz0301 BookMark: TMPLAT_ACTION_SUFFICIENT Condition:
			 * CdSafetyEvalType = "CO1"
			 */
			if (ServiceConstants.CO1.startsWith(safetyEval.getCdSafetyEvalType())) {
				FormDataGroupDto fdAvailableResponsesGrp = createFormDataGroup(
						FormGroupsConstants.TMPLAT_ACTION_SUFFICIENT, FormConstants.EMPTY_STRING);
				formDataGroupList.add(fdAvailableResponsesGrp);
			}

			/**
			 * Description: Group inserts 'Yes' for family willing question
			 * GroupName: cfzz0301 BookMark: TMPLAT_FAMILY_WILLING Condition:
			 * CdSafetyEvalType = "CO2"
			 */

			if (ServiceConstants.CO2.contains(safetyEval.getCdSafetyEvalType())) {
				FormDataGroupDto fdFamilyWillingGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_FAMILY_WILLING,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(fdFamilyWillingGrp);
			}

			/**
			 * Description: Group inserts 'No' for actions sufficient question
			 * GroupName: cfiv0603 BookMark: TMPLAT_ACTION_SUFFICIENT_NO
			 * Condition: szCdSafetyFactor != "YY" AND szCdSafetyFactor != "YN"
			 * AND cCdSafetyEvalType =="X"
			 */

			if (!ServiceConstants.YY.equalsIgnoreCase(safetyEval.getCdSafetyFactor())
					&& !ServiceConstants.YN.equalsIgnoreCase(safetyEval.getCdSafetyFactor())
					&& ServiceConstants.X.equalsIgnoreCase(safetyEval.getCdSafetyFactor())) {
				FormDataGroupDto fdActionSuffNoGrp = createFormDataGroup(
						FormGroupsConstants.TMPLAT_ACTION_SUFFICIENT_NO, FormConstants.EMPTY_STRING);
				formDataGroupList.add(fdActionSuffNoGrp);
			}

			/**
			 * Description: Group inserts 'No' for family willing question
			 * GroupName: cfiv0604 BookMark: TMPLAT_FAMILY_WILLING_NO Condition:
			 * szCdSafetyFactor != "YY" AND szCdSafetyFactor != "NY" AND
			 * cCdSafetyEvalType =="X"
			 */

			if (!ServiceConstants.YY.equalsIgnoreCase(safetyEval.getCdSafetyFactor())
					&& !ServiceConstants.NY.equalsIgnoreCase(safetyEval.getCdSafetyFactor())
					&& ServiceConstants.X.equalsIgnoreCase(safetyEval.getCdSafetyFactor())) {
				FormDataGroupDto fdActionSuffNoGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_FAMILY_WILLING_NO,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(fdActionSuffNoGrp);
			}

		}
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkDtoDefaultDtoList);
		return preFillData;

	}
}
