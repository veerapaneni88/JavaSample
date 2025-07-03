package us.tx.state.dfps.service.formreferrals.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.CourtesyFormReferrals;
import us.tx.state.dfps.common.domain.CourtesyReferralIntrvw;
import us.tx.state.dfps.common.domain.FormsReferrals;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.AudDiligentSearchReq;
import us.tx.state.dfps.service.common.request.FormReferralsReq;
import us.tx.state.dfps.service.common.response.AudDiligentSearchRes;
import us.tx.state.dfps.service.common.response.FormReferralsRes;
import us.tx.state.dfps.service.formreferrals.dto.CaseWorkerDtlDto;
import us.tx.state.dfps.service.formreferrals.dto.CourtesyFormReferlDto;
import us.tx.state.dfps.service.formreferrals.dto.CourtesyInterviewDto;
import us.tx.state.dfps.service.formreferrals.dto.DlgntSrchChildDtlDto;
import us.tx.state.dfps.service.formreferrals.dto.DlgntSrchDtlDto;
import us.tx.state.dfps.service.formreferrals.dto.DlgntSrchHdrDto;
import us.tx.state.dfps.service.formreferrals.dto.FormReferralsDto;
import us.tx.state.dfps.service.formreferrals.dto.OnLoadDlgntHdrDto;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.formreferrals.dto.QuickFindPersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 7, 2017- 2:25:37 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FormReferralsDao {

	/**
	 * Method Name: formReferralsList Method Description: Method to retrieve
	 * forms list.
	 * 
	 * @param formReq
	 * @return @
	 */
	public FormReferralsRes formReferralsList(FormReferralsReq formReq);

	/**
	 * Method Name: formReferralsSave Method Description: Method to save a
	 * record in FORMS_REFFERALS table.
	 * 
	 * @param formReq
	 * @return @
	 */
	public Long formReferralsSave(FormsReferrals formsReferrals);

	/**
	 * Method Name: formReferralsDelete Method Description: Method to delete a
	 * record in Forms_referral table.
	 * 
	 * @param formReq
	 * @
	 */
	public void formReferralsDelete(FormReferralsReq formReq);

	/**
	 * Method Name: getCourtesyReferralDetail Method Description: Method to get
	 * Courtesy referral Detail
	 * 
	 * @param formReq
	 * @
	 */

	public CourtesyFormReferlDto getCourtesyReferralDetail(Long idCourtesyReferl);

	/**
	 * Method Name: getCourtesyReferralInterview Method Description: Method to
	 * retrieve list of interview information.
	 * 
	 * @param idFormRefrl
	 * @
	 */
	public List<CourtesyInterviewDto> getCourtesyReferralInterview(Long idFormRefrl);

	/**
	 * Method Name: saveCourtesyReferralDetail Method Description: Method to
	 * save courtesy referrral detail.
	 * 
	 * @param idFormRefrl
	 * @
	 */
	CourtesyFormReferrals saveCourtesyReferralDetail(CourtesyFormReferrals courtesyFormReferrals);

	/**
	 * Method Name: getQuickFind Method Description:Method to retrieve record
	 * from quick_find table.
	 * 
	 * @param formReq
	 * @return @
	 */
	public FormsReferrals getFormReferrals(FormReferralsReq formReq);

	/**
	 * Method Name: quickFindSave Method Description:Method to save record in
	 * quick_find table.
	 * 
	 * @param formReq
	 * @return
	 * @ @throws
	 *       InvalidRequestException
	 */
	public FormsReferrals saveQuickFind(FormReferralsReq formReq);

	/**
	 * Method Name: fbssSave Method Description:Method to save record in
	 * fbss_referrals table.
	 * 
	 * @param formReq
	 * @return
	 * @ @throws
	 *       InvalidRequestException
	 */
	public FormsReferrals saveFBSS(FormReferralsReq formReq);

	/**
	 * Method Name: quickFindDelete Method Description:Method to delete record
	 * from quick_find,fbss_referrals table.
	 * 
	 * @param formReq
	 * @return @
	 */
	public String deleteQuickFind(FormReferralsReq formReq);

	/**
	 * Method Name: postEvent Method Description:Method to create a Event
	 * 
	 * @param formReq
	 * @
	 */

	public Long postEvent(FormReferralsReq formReq, String description);

	/**
	 * Method Name: saveCourtesyReferralIntrvwDetail Method Description:Method
	 * to Save a record in Courtesy referral interview table.
	 * 
	 * @param formReq
	 * @
	 */
	public CourtesyReferralIntrvw saveCourtesyReferralIntrvwDetail(CourtesyReferralIntrvw courtesyReferralIntrvw);

	/**
	 * Method Name: CourtesyReferralDelete Method Description: Method to delete
	 * a record in CourtesyReferral table.
	 * 
	 * @param formReq
	 * @
	 */
	public void courtesyReferralDelete(Long idCourtesyFormReferrals);

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
	 * Method Name: getDiligentSearchHeader Method Description:This method
	 * retrieves data from DLGNT_SRCH_HDR, PERSON and STAGE_PERSON_LINK tables.
	 * 
	 * @param idFormsReferrals
	 * @return DlgntSrchHdrDto @
	 */
	DlgntSrchHdrDto getDiligentSearchHeader(Long idFormsReferrals);

	/**
	 * 
	 * Method Name: getDiligentSearchDetail Method Description: This method
	 * retrieves data from DLGNT_SRCH_DTL table.
	 * 
	 * @param idDlgntSrchHdr
	 * @return List<DlgntSrchDtlDto> @
	 */
	List<DlgntSrchDtlDto> getDiligentSearchDetail(Long idDlgntSrchHdr);

	/**
	 * 
	 * Method Name: getDiligentSearchChildDetails Method Description: This
	 * method retrieves data from DLGNT_SRCH_CHILD_DTL table.
	 * 
	 * @param idDlgntSrchDtl
	 * @return List<DlgntSrchChildDtlDto> @
	 */
	List<DlgntSrchChildDtlDto> getDiligentSearchChildDetails(Long idDlgntSrchDtl);

	/**
	 * Method Name: deletedlgtSearch Method Description:Method to delete record
	 * from the
	 * DLGNT_SRCH_HDR,DLGNT_SRCH_DTL,DLGNT_SRCH_CHILD_DTL,FORMS_REFERRALS table.
	 * 
	 * @param formReq
	 * @return @
	 */
	public String deletedlgtSearch(FormReferralsReq formReq);

	/**
	 * 
	 * Method Name: saveAndUpdateDiligentSearch Method Description: This method
	 * will perform SAVE & UPDATE operations on FORMS_REFERRALS, DLGNT_SRCH_HDR,
	 * DLGNTSRCH_DTL and DLGNT_SRCH_CHILD_DTL
	 * 
	 * @param audDiligentSearchReq
	 * @return AudDiligentSearchRes
	 * @ @throws
	 *       InvalidRequestException
	 */
	AudDiligentSearchRes saveAndUpdateDiligentSearch(AudDiligentSearchReq audDiligentSearchReq);

	/**
	 * 
	 * Method Name: getPersonDtlByStageId Method Description: This method will
	 * retrieves data from PERSON,PERSON_DTL and STAGE_PERSON_LINK tables.
	 * 
	 * @param idStage
	 * @return List<DlgntSrchDtlDto> @
	 */
	List<DlgntSrchDtlDto> getPersonDtlByStageId(Long idStage);

	/**
	 * 
	 * Method Name: getCaseWorkerDtl Method Description: This method will
	 * retrieve idCase and Region by passing idStage.
	 * 
	 * @param idStage
	 * @return DlgntSrchHdrDto @
	 */
	DlgntSrchHdrDto getCaseWorkerDtl(Long idStage);

	/**
	 * 
	 * Method Name: getSupervisorId Method Description: This method will
	 * retrieve ID Person for the Supervisor.
	 * 
	 * @param idPerson
	 * @return Long @
	 */
	Long getSupervisorId(Long idPerson);

	/**
	 * 
	 * Method Name: getDlgntHdrByStageId Method Description: This method
	 * retrieves case worker's information by passing idPerson.
	 * 
	 * @param idPerson
	 * @return OnLoadDlgntHdrDto @
	 */
	OnLoadDlgntHdrDto getDlgntHdrByStageId(Long idPerson);

	/**
	 * 
	 * Method Name: getCaseWorkerCounty Method Description: This method
	 * retrieves case worker's information by passing idPerson.
	 * 
	 * @param idPerson
	 * @return caseWorkerDto @
	 */

	public CaseWorkerDtlDto getCaseWorkerCounty(Long idPerson);

	/**PPM#46797 artf150671
	 * Method Name: getHouseHoldDetails
	 * Method Description: This method retrieves household SDM safety assessment, and address details.
	 * @param householdList,idCase
	 * @return List<QuickFindPersonDto>
	 */
	public List<QuickFindPersonDto> getHouseHoldDetails( List<Long> householdList,Long idCase);

	/**PPM#46797 artf150671
	 * Method Name: getHouseHoldDetailsBySA
	 * Method Description: This method retrieves household id for the cpssa.
	 * @param idcpssa
	 * @return List
	 */
	public List<QuickFindPersonDto> getHouseHoldDetailsBySA(Long idcpssa);

	/**
	 * PPM#46797 artf150671
	 * Method Name: valdiateSaveAndSubmit
	 * Method Description: This method valdiates on SaveAndSubmit.
	 * @param idStage
	 * @param indApprovalFlow
	 * @param idcpssa
	 * @param idPersonHouseHold
	 * @return int
	 */
	public String validateSaveAndSubmit(Long idStage,String indApprovalFlow,Long idcpssa,Long idPersonHouseHold);
	/**
	 * PPM#46797 method to retrieve forms referral based on event id
	 * @param formReq
	 * @return
	 */
	public FormsReferrals getFormReferralsByEvent(FormReferralsReq formReq);

	/**
	 * artf151569
	 * Method Name: getFormsReferralIdByApproval
	 * Method Description: This method retrieves the idFormReferral based on the idApproval
	 *
	 * @param idApproval
	 * @return
	 */
	Long getFormsReferralIdByApproval(Long idApproval);

	/**PPM#46797 artf150671
	 * Method Name: getFBSSReferralsForFPR Method Description: Method to retrieve
	 * FBSS referral details .
	 *
	 * @param formReq
	 * @return @
	 */
	public FormReferralsDto getFBSSReferralsForFPR(Long idStage);

	/**
	 *  Method Name:getDiligentSearchId Method Description: Method to retrieve Diligent Search Header Id by
	 *  idFormsReferrals
	 * @param idFormsReferrals
	 * @return
	 */
	public DlgntSrchHdrDto getDiligentSearchId(Long idFormsReferrals);




}
