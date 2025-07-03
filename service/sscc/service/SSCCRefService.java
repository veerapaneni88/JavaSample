package us.tx.state.dfps.service.sscc.service;

import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCParameterDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCResourceDto;
import us.tx.state.dfps.service.common.request.HasSSCCReferralReq;
import us.tx.state.dfps.service.common.request.SSCCRefListKeyReq;
import us.tx.state.dfps.service.common.request.SSCCReferralReq;
import us.tx.state.dfps.service.common.response.HasSSCCReferralRes;
import us.tx.state.dfps.service.common.response.SSCCRefListKeyRes;
import us.tx.state.dfps.service.common.response.SSCCReferralRes;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Ejb Service
 * SSCCRef Class Description: This class is use for retrieving CapsCase
 *
 */
public interface SSCCRefService {

	/**
	 * This method will return SSCCRefListValueBean with indicators to display
	 * the SSCC Referral Section on the Case Summary page and indicator to
	 * display Add button on the SSCC Referral Section and the SSCC Catchment
	 * region for this case.
	 * 
	 * @param sSCCRefListDto
	 * @param ulIdPerson
	 * @return
	 */
	public SSCCParameterDto fetchValidStageSUBForRefDisplay(SSCCRefListDto sSCCRefListDto, Long ulIdPerson);

	/**
	 * Method validates and identifies open FSU and FRE stages within the case
	 * that can be used as Reference stages for new SSCC Referrals
	 * 
	 * @param sSCCRefListDto
	 * @param ulIdPerson
	 * @param sSCCResouceDto
	 * @
	 */
	public void fetchValidFamilyStageForRefDisplay(SSCCRefListDto sSCCRefListDto, Long ulIdPerson,
			SSCCResourceDto sSCCResouceDto);

	/**
	 * This method will be used by CaseSummary.displayCaseSummary() to check if
	 * the SSCC Referral Section and Add button need to be displayed on the Case
	 * Summary page. Note: Calling method must set the Case Id into the
	 * ssccRefListValBean
	 * 
	 * @param ulIdPerson
	 * @param sSCCRefListDto
	 * @returnSSCCRefListDto @
	 */
	public SSCCRefListDto processDisplayLogicForAddButton(SSCCRefListDto sSCCRefListDto, Long ulIdPerson);

	/**
	 * getSSCCRefListKey
	 * 
	 * @param sSCCRefListKeyReq
	 * @return
	 */
	public SSCCRefListKeyRes getSSCCRefListKey(SSCCRefListKeyReq sSCCRefListKeyReq);

	/**
	 * Returns true if there is at least one SSCC Referral for given stage
	 * 
	 * @param idStage
	 * @param cdRefType
	 * @return @
	 */
	public boolean hasSSCCReferralForStage(Long idStage, String cdRefType);

	/**
	 * Method calculates the status that needs to be displayed on the SSCC
	 * Referral List expandable section
	 * 
	 * @param sSCCRefListDto
	 * @return @
	 */
	public SSCCRefListDto ssccReferralListSummaryStatus(SSCCRefListDto sSCCRefListDto);

	/**
	 * Returns true if there is at least one SSCC Referral for given stage
	 * 
	 * @param hasSSCCReferralReq
	 * @return HasSSCCReferralRes @
	 */
	public HasSSCCReferralRes hasSSCCReferral(HasSSCCReferralReq hasSSCCReferralReq);

	/**
	 * Check if Case has an active SSCC Referral
	 * 
	 * @param hasSSCCReferralReq
	 * @return HasSSCCReferralRes @
	 */
	public HasSSCCReferralRes hasActiveSSCCReferral(HasSSCCReferralReq hasSSCCReferralReq);

	/**
	 * Returns true if idPerson is SSCC
	 * 
	 * @param idPerson
	 * @return true or false @
	 */
	public boolean isUserSSCC(Long idUser);

	/**
	 * Returns all active sscc referral for given stage
	 * 
	 * @param idStage
	 * @param strReferralType
	 * @return List<SSCCRefDto> all active SSCC Referrals @
	 */
	public List<SSCCRefDto> GetActiveSSCCReferral(Long idStage, String strReferralType);

	/**
	 * Method Name: fetchSSCCReferralListForCase Method Description:Fetches the
	 * list of SSCC Referrals for the case and sets it into the
	 * SSCCRefListValueBean
	 * 
	 * @param ssccRefListDto
	 * @return SSCCRefListDto
	 */
	public SSCCRefListDto fetchSSCCReferralListForCase(SSCCRefListDto ssccRefListDto);

	/**
	 * MethodName:updateSSCCReferralDetail MethodDescription: Method updates an
	 * SSCC Referral record in the SSCC_REFERRAL table
	 * 
	 * @param ssccReferralDto
	 * @return
	 */
	public void updateSSCCReferralDetail(SSCCRefDto ssccReferralDto, String userId);

	/**
	 * Method Name: fetchReferralById Method Description: Fetches the Referral
	 * information for idSSCCReferral legacy name: readReferralByPK
	 * 
	 * @param idSSCCReferral
	 * @param userId
	 * @return
	 */
	public SSCCReferralRes fetchReferralById(Long idSSCCReferral, String userId, boolean isUserFixer);

	/**
	 * 
	 * Method Name: fetchSSCCRefHeaderDataForNewReferral Method Description:
	 * Method fetches the SSCC Header information for a new SSCC Referral 1. The
	 * SSCC catchment region for the case 2. Populates the reference stage
	 * options lists (for Reference Stage drop down)
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	public SSCCReferralRes fetchSSCCRefHeaderDataForNewReferral(SSCCReferralReq ssccReferralReq);

	/**
	 * Method Name: saveSSCCReferralHeader Method Description: This method helps
	 * to add the SSCC referral Header information.
	 * 
	 * @param ssccRefDto
	 * @return
	 */
	public SSCCReferralRes saveSSCCReferralHeader(SSCCRefDto ssccRefDto);

	/**
	 * Method Name: saveAndTransmitSSCCReferral Method Description: This method
	 * updates all the sscc referral, sscc list, sscc timeline and sscc event
	 * tables
	 * 
	 * @param ssccRefDto
	 * @param ssccListDto
	 * @param idUser
	 * @return
	 */
	public SSCCReferralRes saveAndTransmitSSCCReferral(SSCCRefDto ssccRefDto, SSCCListDto ssccListDto, String idUser);

	/**
	 * Method Name: deleteSSCCReferralHeader Method Description: This method
	 * deletes the sscc referral header information.
	 * 
	 * @param idSSCCReferral
	 * @return
	 */
	public Long deleteSSCCReferralHeader(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: finalizeSSCCReferralDischarge Method Description: Method is
	 * invoked when user clicks on the Finalize Discharge button on the SSCC
	 * Referral Detail page
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	public SSCCReferralRes finalizeSSCCReferralDischarge(SSCCRefDto ssccRefDto, String idUser);

	/**
	 * Method Name:undoDischargeSSCCReferral. Method Description:Method is
	 * invoked when SSCC Fixer clicks on Undo Discharge button
	 * 
	 * @param SSCCRefDto
	 *            ssccRefDto
	 * @param SSCCReferralRes
	 *
	 */

	public SSCCReferralRes undoDischargeSSCCReferral(SSCCRefDto ssccRefDto, String idUser);

	/**
	 * Method Name: updateAndNotifySSCCReferral Method Description: This method
	 * is does required actions when update and notify button is clicked.
	 * 
	 * @param ssccRefDto
	 * @param idUser
	 * @return
	 */
	public SSCCReferralRes updateAndNotifySSCCReferral(SSCCRefDto ssccRefDto, String idUser);

	/**
	 * Method Name: acknowledgeSSCCReferral Method Description:This method is
	 * used update the sscc referral when acknowledge button is clicked.
	 * 
	 * @param ssccRefDto
	 * @param idUser
	 * @return
	 */
	public SSCCReferralRes acknowledgeSSCCReferral(SSCCRefDto ssccRefDto, String idUser);

	/**
	 * Method Name: deleteSSCCReferral Method Description: Method deletes the
	 * SSCC Referral Record from the SSCC Referral Table
	 * 
	 * @param ssccRefDto
	 * @param idUser
	 * @return
	 */
	public SSCCReferralRes deleteSSCCReferral(SSCCRefDto ssccRefDto);

	/**
	 * Method Name: acknowledgeDischargeSSCCReferral Method Description: This
	 * method will be triggered when user clicks on acknowledge discharge
	 * button.
	 * 
	 * @param ssccRefDto
	 * @param idUser
	 * @return
	 */
	public SSCCReferralRes acknowledgeDischargeSSCCReferral(SSCCRefDto ssccRefDto, String idUser);

	/**
	 *Method Name:	addOrRemoveSSCCRefFamilyPerson
	 *Method Description:This method used when user adds/removes the person to family referral.
	 *@param ssccRefDto
	 *@param idUser
	 *@return
	 */
	SSCCReferralRes addOrRemoveSSCCRefFamilyPerson(SSCCRefDto ssccRefDto, String idUser, boolean indRemovePerson);
	
	/**
	 *Method Name:	retrieveSSCCRefByStageId
	 *Method Description:This method used to get active referral for a stageid.
	 *@param stageId
	 *@return List<SSCCRefDto>
	 */
	public SSCCReferralRes retrieveSSCCRefByStageId(long stageId);

	/**
	 * code added for artf231094
	 * @param ssccRefDto
	 * @return
	 */
	public SSCCReferralRes getSSCCRefCount(SSCCRefDto ssccRefDto);

}
