package us.tx.state.dfps.service.formreferrals.service;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.AudDiligentSearchReq;
import us.tx.state.dfps.service.common.request.CourtesyFormReq;
import us.tx.state.dfps.service.common.request.DiligentSearchRtrvReq;
import us.tx.state.dfps.service.common.request.FormReferralsReq;
import us.tx.state.dfps.service.common.request.OnLoadDlgntHdrReq;
import us.tx.state.dfps.service.common.response.AudDiligentSearchRes;
import us.tx.state.dfps.service.common.response.CourtesyFormRes;
import us.tx.state.dfps.service.common.response.DiligentSearchRtrvRes;
import us.tx.state.dfps.service.common.response.FormReferralsRes;
import us.tx.state.dfps.service.common.response.OnLoadDlgntHdrRes;
import us.tx.state.dfps.service.common.response.PrsnListRtrvForDlgntSrchRes;
import us.tx.state.dfps.service.formreferrals.dto.CaseWorkerDtlDto;
import us.tx.state.dfps.service.formreferrals.dto.FormReferralsDto;
import us.tx.state.dfps.service.formreferrals.dto.QuickFindPersonDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 7, 2017- 2:26:22 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FormReferralsService {

	/**
	 * Method Name: formReferralsList Method Description: Method to retrieve
	 * Forms list.
	 *
	 * @param formReq
	 * @return @
	 */
	public FormReferralsRes getFormReferralsList(FormReferralsReq formReq);

	/**
	 * Method Name: formReferralsDelete Method Description: Method to delete a
	 * record in FORMS_REFERRALS Table
	 *
	 * @param formReq
	 * @return @
	 */
	public FormReferralsRes formReferralsDelete(FormReferralsReq formReq);

	/**
	 * Method Name: getCourtesyReferralDetail Method Description: Method to
	 * retrieve Courtesy referrals
	 *
	 * @param CourtesyFormReq
	 * @return @
	 */

	public CourtesyFormRes getCourtesyReferralDetail(CourtesyFormReq courtesyFormReq);

	/**
	 * Method Name: saveCourtesyReferralDetail Method Description: Method to
	 * save Courtesy referral.
	 *
	 * @param formReq
	 * @return @
	 */
	public CourtesyFormRes saveCourtesyReferralDetail(FormReferralsReq formReferralsReq);

	/**
	 * Method Name: getQuickFind Method Description:Method to retrieve record
	 * from quick_find table.
	 *
	 * @param formReq
	 * @return @
	 */
	public FormReferralsRes getQuickFind(FormReferralsReq formReq);

	/**
	 * Method Name: quickFindSave Method Description:Method to save record in
	 * quick_find table.
	 *
	 * @param formReq
	 * @return
	 * @ @throws
	 *       InvalidRequestException
	 */
	public FormReferralsRes saveQuickFind(FormReferralsReq formReq);

	/**
	 * Method Name: getFbssReferrals Method Description:Method to retrieve
	 * record from fbss_referrals table.
	 *
	 * @param formReq
	 * @return @
	 */
	public FormReferralsRes getFbssReferrals(FormReferralsReq formReq);

	/**
	 * Method Name: fbssSave Method Description:Method to save record in
	 * fbss_referrals table.
	 *
	 * @param formReq
	 * @return
	 * @ @throws
	 *       InvalidRequestException
	 */
	public FormReferralsRes saveFBSS(FormReferralsReq formReq);

	/**
	 * Method Name: quickFindDelete Method Description:Method to delete record
	 * from quick_find table.
	 *
	 * @param formReq
	 * @return @
	 */
	public FormReferralsRes deleteQuickFind(FormReferralsReq formReq);

	/**
	 * Method Name: fbssDelete Method Description:Method to delete record from
	 * the fbss_referrals table.
	 *
	 * @param formReq
	 * @return @
	 */
	public FormReferralsRes deleteFBSS(FormReferralsReq formReq);

	/**
	 * Method Name: saveCourtesyReferralDetail Method Description: Method to
	 * save Courtesy referral.
	 *
	 * @param formReq
	 * @return @
	 */
	public CourtesyFormRes saveCourtesyReferralIntrvw(FormReferralsReq formReferralsReq);

	/**
	 * Method Name: CourtesyReferralDelete Method Description: Method to delete
	 * Courtesy referral.
	 *
	 * @param formReq
	 * @return @
	 */
	public FormReferralsRes courtesyReferralDelete(CourtesyFormReq courtesyFormReq);

	/**
	 * Method Name: getQuickFindPerson Method Description:This method is used to
	 * fetch the quch_find page values onload.
	 *
	 * @param formReq
	 * @return @
	 */
	public FormReferralsRes getQuickFindPerson(FormReferralsReq formReq);

	/**
	 *
	 * Method Name: getDiligentSearchRtrv Method Description: This service will
	 * retrieve data for Diligent Search screen.
	 *
	 * @param diligentSearchRtrvReq
	 * @return DiligentSearchRtrvRes @
	 */
	DiligentSearchRtrvRes getDiligentSearchRtrv(DiligentSearchRtrvReq diligentSearchRtrvReq);

	/**
	 *
	 * Method Name: saveAndUpdateDiligentSearch Method Description:This method
	 * will perform SAVE and UPDATE operations on Diligent Search Screen.
	 *
	 * @param audDiligentSearchReq
	 * @return AudDiligentSearchRes @
	 */
	AudDiligentSearchRes saveAndUpdateDiligentSearch(AudDiligentSearchReq audDiligentSearchReq);

	/**
	 *
	 * Method Name: getPersonList Method Description: This service will retrieve
	 * list for persons from STAGE, STAGE_PERSON_LINK and PERSON tables.
	 *
	 * @param diligentSearchRtrvReq
	 * @return PrsnListRtrvForDlgntSrchRes @
	 */
	PrsnListRtrvForDlgntSrchRes getPersonList(DiligentSearchRtrvReq diligentSearchRtrvReq);

	/**
	 * Method Name: deletedlgtSearch Method Description:Method to delete record
	 * from the
	 * DLGNT_SRCH_HDR,DLGNT_SRCH_DTL,DLGNT_SRCH_CHILD_DTL,FORMS_REFERRALS table.
	 *
	 * @param formReq
	 * @return FormReferralsRes @
	 */
	public FormReferralsRes deletedlgtSearch(FormReferralsReq formReq);

	/**
	 *
	 * Method Name: getDlgntSrchHdrByStageId Method Description: This Service
	 * will retrieve Case Worker, Requester and Supervisor info for Diligent
	 * Search Header.
	 *
	 * @param onLoadDlgntHdrReq
	 * @return OnLoadDlgntHdrRes
	 * @throws InvalidRequestException
	 * @
	 */
	OnLoadDlgntHdrRes getDlgntSrchHdrByStageId(OnLoadDlgntHdrReq onLoadDlgntHdrReq);

	public CaseWorkerDtlDto getCaseWorkerCounty(Long idPerson);

	/**[artf151021] UC 561 FBSS Referral Printable Form
	 * Method Name: getFbssReferralForm Method Description:Method to retrieve
	 * record from fbss_referrals table and generate the form.
	 *
	 * @param formReq
	 * @return @
	 */
	public PreFillDataServiceDto getFbssReferralForm(FormReferralsReq formReq);

	/**PPM#46797 artf150671
	 * Method Name: getHouseHoldDetails
	 * Method Description: This method retrieves household SDM safety assessment, and address details.
	 * @param idStage
	 * @return List<QuickFindPersonDto>
	 */
	public List<QuickFindPersonDto> getHouseHoldDetails(Long idStage);

	/**PPM#46797 artf150671
	 * Method Name: getHouseHoldDetailsBySA
	 * Method Description: This method retrieves household SDM safety assessment, and address details by cpssa.
	 *
	 * @param idcpssa
	 * @return List<QuickFindPersonDto>
	 */
	public List<QuickFindPersonDto> getHouseHoldDetailsBySA(Long idcpssa);

	/**
	 * PPM#46797 artf150671
	 * Method Name: valdiateSaveAndSubmit
	 * Method Description: This method valdiates on SaveAndSubmit.
	 * @param idStage
	 * @param indApprovalFlow
	 * @param idCpsSa
	 * @param idPersonHouseHold
	 * @param idApproval
	 * @return int
	 */
	public String validateSaveAndSubmit(Long idStage, String indApprovalFlow, Long idCpsSa, Long idPersonHouseHold,
									 Long IdApproval);

	/**
	 * artf151569
	 * Method Name: getFormReferralByApprovalId
	 * Method Description: This method retrieves the Form Referral Id based on the Approval ID.
	 *
	 * @param idApproval
	 * @return
	 */
	Long getFormReferralByApprovalId(Long idApproval);

	/**
	 * PPM#46797 artf150671
	 * Method Name: getFBSSReferralsForFPR
	 * Method Description:  Method to retrieve
	 * 	 * FBSS referral details
	 * @param idFPRStage
	 * @return FormReferralsDto
	 */
	public FormReferralsDto getFBSSReferralsForFPR(Long idFPRStage);

	DiligentSearchRtrvRes getDiligentSearchHdrId(DiligentSearchRtrvReq diligentSearchRtrvReq);
}
