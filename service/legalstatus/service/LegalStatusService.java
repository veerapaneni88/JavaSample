package us.tx.state.dfps.service.legalstatus.service;

import us.tx.state.dfps.common.dto.SSCCExceptCareDesignationDto;
import us.tx.state.dfps.service.admin.dto.*;
import us.tx.state.dfps.service.common.request.LegalStatusUpdateReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * invoke to retrieve Legal status Aug 19, 2017- 9:34:02 PM © 2017 Texas
 * Department of Family and Protective Services.
 */
public interface LegalStatusService {

	/**
	 * Method Name: getLegalStatusDetails Method Description: This is the
	 * retrieval service for the Legal Status.
	 *
	 * @param pInputMsg
	 *            the input msg
	 * @return List<LegalStatusRtrvoDto> @ the service exception
	 */
	public LegalStatusRtrvoDto getLegalStatusDetails(LegalStatusRtrviDto pInputMsg);

	/**
	 * Method Name: getLegalStatusPersonDetail Method Description: This is the
	 * retrieval service for the Person Detail.
	 *
	 * @param idStage
	 *            the input msg
	 * @return Long @ the service exception
	 */
	public Long getLegalStatusPersonDetail(Long idStage);

	/**
	 * Method Name: updateLegalStatus Method Description: update legal status .
	 *
	 * @param legalStatusReq
	 *            the legal status req
	 * @return the int @ the service exception
	 */
	public int updateLegalStatus(LegalStatusUpdateReq legalStatusReq);

	/**
	 * Method Name: getIndLegalStatMissing Method Description: get
	 * indLegalStatMissing flag.
	 *
	 * @param idReferral
	 *            the id referral
	 * @return the ind legal stat missing
	 */
	public SSCCExceptCareDesignationDto getIndLegalStatMissing(Long idReferral);

	/**
	 * Method Name: updateSsccIndLegalStatus Method Description:update SSCC_LIST
	 * TABLE , IND_LEGAL_STATUS_MISSING.
	 *
	 * @param indLegalStatusMissing
	 *            the ind legal status missing
	 * @param idSsccReferral
	 *            the id sscc referral
	 */
	public void updateSsccIndLegalStatus(String indLegalStatusMissing, Long idSsccReferral);

	/**
	 * Select latest legal action sub type.
	 *
	 * @param legalActionEventInDto
	 *            the legal action event in dto
	 * @return the common string res
	 */
	public CommonStringRes selectLatestLegalActionSubType(LegalActionEventInDto legalActionEventInDto);

	/**
	 * Select latest legal status.
	 *
	 * @param legalActionEventInDto
	 *            the legal action event in dto
	 * @return the legal status rtrvo res
	 */
	public LegalStatusDetailDto selectLatestLegalStatus(LegalActionEventInDto legalActionEventInDto);

	/**
	 * Gets the legal status for child. UIDS 2.3.3.5 - Remove a child from home
	 * - Income and Expenditures
	 *
	 * @param idPerson
	 *            the id person
	 * @param idCase
	 *            the id case
	 * @return the legal status for child
	 */
	public int getLegalStatusForChild(Long idPerson, Long idCase);

	/**
	 * Gets the recent legal region for child. UIDS 2.3.3.5 - Remove a child
	 * from home - To-Do Detail
	 * 
	 * @param idPerson
	 *            the id person
	 * @return the recent legal region for child
	 */
	public String getRecentLegalRegionForChild(Long idPerson);

	//PPM 77834 – FCL CLASS Webservice for Data Exchange
	/**
	 * @param idPerson
	 * @return
	 */
	public LegalStatusDetailDto getLatestLegalStatusByPersonId(Long idPerson);

	/**
	 * @param idPerson
	 * @return
	 */
	public LegalStatusDetailDto getLatestLegalStatusInfoByPersonId(Long idPerson);

	/**
	 * @param idEvent
	 * @return
	 */
	public LegalStatusDetailDto getLatestLegalStatusInfoByEventId(Long idEvent);

}
