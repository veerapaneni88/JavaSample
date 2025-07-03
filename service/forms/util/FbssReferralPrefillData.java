/**
 * 
 */
package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.response.FormReferralsRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.formreferrals.dto.QuickFindPersonDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * @author MUDDUR
 * artf151021 
 *
 */
@Component
public class FbssReferralPrefillData extends DocumentServiceUtil{

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		FormReferralsRes formReferralsRes = (FormReferralsRes) parentDtoobj; 
		
		/**
		 * List to add all the non-group bookmark data to set into prefill data
		 */
		List<BookmarkDto> bookmarkNoGroupList = new ArrayList<BookmarkDto>();
		
		//Section1 
		bookmarkNoGroupList.add(createBookmark(CASE_NAME,
				formReferralsRes.getFbssReferralsList().get(0).getNmCase()));
		bookmarkNoGroupList.add(createBookmark(CASE_ID,
				formReferralsRes.getFbssReferralsList().get(0).getIdCase()));
		bookmarkNoGroupList.add(createBookmark(REFERRRING_CASEWRK,
				formReferralsRes.getFbssReferralsList().get(0).getCaseWorker()));
		bookmarkNoGroupList.add(createBookmark(CELL_PHONE,
				TypeConvUtil.formatPhone(formReferralsRes.getFbssReferralsList().get(0).getCaseWorkerPhn())));
		bookmarkNoGroupList.add(createBookmark(REFERRRING_SUPVISOR,
				formReferralsRes.getFbssReferralsList().get(0).getSupervisor()));
		bookmarkNoGroupList.add(createBookmark(SUP_CELL_PHONE,
				TypeConvUtil.formatPhone(formReferralsRes.getFbssReferralsList().get(0).getSupervisorPhn())));
		bookmarkNoGroupList.add(createBookmark(REFERRRING_UNIT,
				formReferralsRes.getFbssReferralsList().get(0).getReferringUnit()));
		bookmarkNoGroupList.add(createBookmarkWithCodesTable(COUNTY,
						formReferralsRes.getFbssReferralsList().get(0).getReferringCnty(), CodesConstant.CCOUNT));
		bookmarkNoGroupList.add(createBookmark(DATE_FBSS,
				TypeConvUtil.formDateFormat(formReferralsRes.getFbssReferralsList().get(0).getDtReferred())));
		
		//section 2
		bookmarkNoGroupList.add(createBookmark(STREET_ADDR,
				formReferralsRes.getFbssReferralsList().get(0).getAddrSt1()));
		bookmarkNoGroupList.add(createBookmark(APT_ADDR,
				formReferralsRes.getFbssReferralsList().get(0).getAddrSt2()));
		bookmarkNoGroupList.add(createBookmark(CITY_ADDR,
				formReferralsRes.getFbssReferralsList().get(0).getAddrCity()));
		bookmarkNoGroupList.add(createBookmark(ZIPCODE_ADDR,
				formReferralsRes.getFbssReferralsList().get(0).getAddrZip()));
		
		//section 3
		bookmarkNoGroupList.add(createBookmark(CONTACT_FAMILY,
				formReferralsRes.getFbssReferralsList().get(0).getContactFamily()));
		bookmarkNoGroupList.add(createBookmark(PREFFERED_LANG,
				formReferralsRes.getFbssReferralsList().get(0).getReferredLang()));
		bookmarkNoGroupList.add(createBookmarkWithCodesTable(PRNT_RELOC,
				formReferralsRes.getFbssReferralsList().get(0).getNonResident(), CodesConstant.NOTSTAT));
		bookmarkNoGroupList.add(createBookmark(PRNT_RELOC_EXPLAIN,
				formReferralsRes.getFbssReferralsList().get(0).getNonResidentStr()));
		bookmarkNoGroupList.add(createBookmarkWithCodesTable(CHILD_NEEDS,
				formReferralsRes.getFbssReferralsList().get(0).getSpecialChildern(),CodesConstant.NOTSTAT));
		bookmarkNoGroupList.add(createBookmark(CHILD_NEEDS_EXPLAIN,
				formReferralsRes.getFbssReferralsList().get(0).getSpecialChildernStr()));
		bookmarkNoGroupList.add(createBookmarkWithCodesTable(CHILD_SAFETY,
				formReferralsRes.getFbssReferralsList().get(0).getWorkerSafety(),CodesConstant.NOTSTAT));
		bookmarkNoGroupList.add(createBookmark(CHILD_SAFETY_EXPLAIN,
				formReferralsRes.getFbssReferralsList().get(0).getWorkerSafetyStr()));
		
		if("U".equalsIgnoreCase(formReferralsRes.getFbssReferralsList().get(0).getPendingLeAction())){
			bookmarkNoGroupList.add(createBookmark(FAMILY_PRECEEDINGS,"Unknown"));
		}else{
			bookmarkNoGroupList.add(createBookmarkWithCodesTable(FAMILY_PRECEEDINGS,
					formReferralsRes.getFbssReferralsList().get(0).getPendingLeAction(),CodesConstant.NOTSTAT));
		}
		bookmarkNoGroupList.add(createBookmark(FAMILY_PRECEEDINGS_EXPLAIN,
				formReferralsRes.getFbssReferralsList().get(0).getPendingLeActionStr()));
		
		if("U".equalsIgnoreCase(formReferralsRes.getFbssReferralsList().get(0).getFamilyAccept())){
			bookmarkNoGroupList.add(createBookmark(FAMILY_FBSS,"Unknown"));
		}else{
			bookmarkNoGroupList.add(createBookmarkWithCodesTable(FAMILY_FBSS,
					formReferralsRes.getFbssReferralsList().get(0).getFamilyAccept(),CodesConstant.NOTSTAT));
		}
		bookmarkNoGroupList.add(createBookmark(FAMILY_FBSS_EXPLAIN,
				formReferralsRes.getFbssReferralsList().get(0).getFamilyAcceptStr()));
		List<FormDataGroupDto> formDataGroupIndList = new ArrayList<FormDataGroupDto>();
		if(null != formReferralsRes.getQuickFindPersonList() && formReferralsRes.getQuickFindPersonList().size() > 0){
			
			List<QuickFindPersonDto> quickFindPersonLst = formReferralsRes.getQuickFindPersonList().stream()                
	                .filter(personId -> formReferralsRes.getFbssReferralsList().get(0).getIdPerson().equals(personId.getIdPerson()))
	                .collect(Collectors.toList());
			BiConsumer<String,String> indicators = (indStr,indType) -> {
				String templateStr= "DANGER_IND".equalsIgnoreCase(indType) ? TMPLAT_DANGER_INDICATORS :TMPLAT_PROTECTIVE_ACTIONS;
				FormDataGroupDto formDataGroupIndicators = createFormDataGroup(templateStr, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkIndList = new ArrayList<BookmarkDto>();
				bookmarkIndList.add(createBookmark(indType,indStr));
				formDataGroupIndicators.setBookmarkDtoList(bookmarkIndList);
				formDataGroupIndList.add(formDataGroupIndicators);
			};
			if(null != quickFindPersonLst && quickFindPersonLst.size() > 0){
					if(null != quickFindPersonLst.get(0).getDangerIndicators() && quickFindPersonLst.get(0).getDangerIndicators().size() > 0){
						bookmarkNoGroupList.add(createBookmark(DANGER_INDICATORS_HEADING,"Danger Indicators from last Safety Assessment"));
						quickFindPersonLst.get(0).getDangerIndicators().forEach(indStr-> indicators.accept(indStr,DANGER_IND));
					}else{
						bookmarkNoGroupList.add(createBookmark(DANGER_INDICATORS_HEADING,"No danger indicators were identified in the last Safety Assessment."));
					}

					if(null != quickFindPersonLst.get(0).getProtectiveActions() && quickFindPersonLst.get(0).getProtectiveActions().size() > 0){
						bookmarkNoGroupList.add(createBookmark(PROTECTIVE_ACTIONS_HEADING,"Protective Actions from last Safety Assessment"));
						quickFindPersonLst.get(0).getProtectiveActions().forEach( indStr-> indicators.accept(indStr,PROTECTIVE_ACTIONS));
					}else{
						bookmarkNoGroupList.add(createBookmark(PROTECTIVE_ACTIONS_HEADING,"No Protective Actions were identified in the last Safety Assessment."));
					}
					bookmarkNoGroupList.add(createBookmark(HOUSE_HLD,quickFindPersonLst.get(0).getFullname()));
			}
		}/*else{
			bookmarkNoGroupList.add(createBookmark(DANGER_IND,"No danger indicators were identified in the last SDM Safety Assessment."));
			bookmarkNoGroupList.add(createBookmark(PROTECTIVE_ACTIONS,"No Protective Actions were identified in the last SDM Safety Assessment."));
		}*/
		bookmarkNoGroupList.add(createBookmark(DANGER_IND_ADDTL_INFO,
				formReferralsRes.getFbssReferralsList().get(0).getDangerIndicator()));
		bookmarkNoGroupList.add(createBookmark(PROTECTIVE_ACTIONS_ADDTL_INFO,
				formReferralsRes.getFbssReferralsList().get(0).getParentProtective()));
		bookmarkNoGroupList.add(createBookmarkWithCodesTable(CHILD_PCSP,
				formReferralsRes.getFbssReferralsList().get(0).getChildPcsp(),CodesConstant.NOTSTAT));
		bookmarkNoGroupList.add(createBookmarkWithCodesTable(FAMILY_BARRIERS,
				formReferralsRes.getFbssReferralsList().get(0).getBarriersFamily(),CodesConstant.NOTSTAT));
		bookmarkNoGroupList.add(createBookmark(FAMILY_BARRIERS_EXPLAIN,
				formReferralsRes.getFbssReferralsList().get(0).getBarriersFamilyStr()));
		bookmarkNoGroupList.add(createBookmarkWithCodesTable(COURT_ORDERS,
				formReferralsRes.getFbssReferralsList().get(0).getCourtOrder(),CodesConstant.NOTSTAT));
		bookmarkNoGroupList.add(createBookmark(COURT_HEARING_DT,
				TypeConvUtil.formDateFormat(formReferralsRes.getFbssReferralsList().get(0).getDtCourtHearing())));
		if("A".equalsIgnoreCase(formReferralsRes.getFbssReferralsList().get(0).getCourtReportUpld())){
			bookmarkNoGroupList.add(createBookmark(AFFIDAVIT_UPLOAD_IND,"N/A"));
		}else{
			bookmarkNoGroupList.add(createBookmarkWithCodesTable(AFFIDAVIT_UPLOAD_IND,
					formReferralsRes.getFbssReferralsList().get(0).getCourtReportUpld(),CodesConstant.NOTSTAT));
		}
		bookmarkNoGroupList.add(createBookmarkWithCodesTable(FTM_HELD,
				formReferralsRes.getFbssReferralsList().get(0).getFtmHeld(),CodesConstant.NOTSTAT));
		bookmarkNoGroupList.add(createBookmark(REFERRAL_COMMENTS,
				formReferralsRes.getFbssReferralsList().get(0).getTxtComments()));
		
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupIndList);
		preFillData.setBookmarkDtoList(bookmarkNoGroupList);
		return preFillData;
	}
	
	//Rererral Header section
	private static final String CASE_NAME = "CASE_NAME";
	private static final String CASE_ID = "CASE_ID";
	private static final String REFERRRING_CASEWRK = "REFERRRING_CASEWRK";
	private static final String CELL_PHONE = "CELL_PHONE";
	private static final String REFERRRING_SUPVISOR = "REFERRRING_SUPVISOR";
	private static final String SUP_CELL_PHONE = "SUP_CELL_PHONE";
	private static final String REFERRRING_UNIT = "REFERRRING_UNIT";
	private static final String COUNTY = "COUNTY";
	private static final String DATE_FBSS = "DATE_FBSS";
	private static final String HOUSE_HLD = "HOUSE_HLD";
	
	//Household Address section
	private static final String STREET_ADDR = "STREET_ADDR";
	private static final String APT_ADDR = "APT_ADDR";
	private static final String CITY_ADDR = "CITY_ADDR";
	private static final String ZIPCODE_ADDR = "ZIPCODE_ADDR";
	
	//Referral Details section
	private static final String CONTACT_FAMILY = "CONTACT_FAMILY";
	private static final String PREFFERED_LANG = "PREFFERED_LANG";
	private static final String PRNT_RELOC = "PRNT_RELOC";
	private static final String PRNT_RELOC_EXPLAIN = "PRNT_RELOC_EXPLAIN";
	private static final String CHILD_NEEDS = "CHILD_NEEDS";
	private static final String CHILD_NEEDS_EXPLAIN = "CHILD_NEEDS_EXPLAIN";
	private static final String CHILD_SAFETY = "CHILD_SAFETY";
	private static final String CHILD_SAFETY_EXPLAIN = "CHILD_SAFETY_EXPLAIN";
	private static final String FAMILY_PRECEEDINGS = "FAMILY_PRECEEDINGS";
	private static final String FAMILY_PRECEEDINGS_EXPLAIN = "FAMILY_PRECEEDINGS_EXPLAIN";
	private static final String FAMILY_FBSS = "FAMILY_FBSS";
	private static final String FAMILY_FBSS_EXPLAIN = "FAMILY_FBSS_EXPLAIN";
	private static final String DANGER_IND = "DANGER_IND";
	private static final String DANGER_IND_ADDTL_INFO = "DANGER_IND_ADDTL_INFO";
	private static final String PROTECTIVE_ACTIONS = "PROTECTIVE_ACTIONS";
	private static final String PROTECTIVE_ACTIONS_ADDTL_INFO = "PROTECTIVE_ACTIONS_ADDTL_INFO";
	private static final String CHILD_PCSP = "CHILD_PCSP";
	private static final String FAMILY_BARRIERS = "FAMILY_BARRIERS";
	private static final String FAMILY_BARRIERS_EXPLAIN = "FAMILY_BARRIERS_EXPLAIN";
	private static final String COURT_ORDERS = "COURT_ORDERS";
	private static final String COURT_HEARING_DT = "COURT_HEARING_DT";
	private static final String AFFIDAVIT_UPLOAD_IND = "AFFIDAVIT_UPLOAD_IND";
	private static final String FTM_HELD = "FTM_HELD";
	private static final String REFERRAL_COMMENTS = "REFERRAL_COMMENTS";
	private static final String DANGER_INDICATORS_HEADING = "DANGER_INDICATORS_HEADING";
	private static final String TMPLAT_DANGER_INDICATORS = "TMPLAT_DANGER_INDICATORS";
	private static final String PROTECTIVE_ACTIONS_HEADING = "PROTECTIVE_ACTIONS_HEADING";
	private static final String TMPLAT_PROTECTIVE_ACTIONS = "TMPLAT_PROTECTIVE_ACTIONS";

}
