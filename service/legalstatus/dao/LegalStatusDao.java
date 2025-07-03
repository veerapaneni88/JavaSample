package us.tx.state.dfps.service.legalstatus.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.LegalStatus;
import us.tx.state.dfps.common.dto.SSCCExceptCareDesignationDto;
import us.tx.state.dfps.service.admin.dto.EligibilityByPersonInDto;
import us.tx.state.dfps.service.admin.dto.LegalActionEventInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusDetailDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusOutDto;
import us.tx.state.dfps.service.admin.dto.ResourcePlacementOutDto;
import us.tx.state.dfps.service.admin.dto.WorkLoadDto;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.kin.dto.KinChildDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * to invoke legalstatus. Aug 20, 2017- 3:38:23 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface LegalStatusDao {

	/**
	 * Method Name: getLegalStatusForEvent Method Description:Fetch legal status
	 * for provided event, cses11dQUERYdam.
	 *
	 * @param pInputDataRec
	 *            the input data rec
	 * @return the legal status for event
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public List<LegalStatusOutDto> getLegalStatusForEvent(LegalStatusInDto pInputDataRec);

	/**
	 * Method Name: getLegalStatusCount Method Description: CSUB80D - This
	 * method will look in the Legal Status table for legal statuses that
	 * already exist for the same Person ID and date.
	 *
	 * @param idPerson
	 *            the id person
	 * @param dtLegalStatStatusDt
	 *            the dt legal stat status dt
	 * @return the legal status count
	 */
	public int getLegalStatusCount(Long idPerson, Date dtLegalStatStatusDt);

	/**
	 * Method Name: getEligibilityEventForStage Method Description:CSESG3D -
	 * selects a Rec from the ELEGIBILITY table.
	 *
	 * @param idStage
	 *            the id stage
	 * @return the eligibility event for stage
	 */
	public EligibilityByPersonInDto getEligibilityEventForStage(Long idStage);

	/**
	 * Method Name: getPersonNameForStage Method Description:CSESG4D - select
	 * person name for stage.
	 *
	 * @param idStage
	 *            the id stage
	 * @return the person name for stage
	 */
	public List<String> getPersonNameForStage(Long idStage);

	/**
	 * Method Name: getLegalStatusCode Method Description:CMSC54D - select Cd
	 * Legal Stat Status for an Id Person and dt legal stat status dt from the
	 * Legal Status table.
	 *
	 * @param idPerson
	 *            the id person
	 * @param idLegalStatEvent
	 *            the id legal stat event
	 * @param dtLegalStatStatusDt
	 *            the dt legal stat status dt
	 * @return the legal status code
	 */
	public List<String> getLegalStatusCode(Long idPerson, Long idLegalStatEvent, Date dtLegalStatStatusDt);

	/**
	 * Method Name: updateLegalStatus Method Description: CAUD05D - update Legal
	 * Status.
	 *
	 * @param pInputDataRec
	 *            the input data rec
	 */
	public void updateLegalStatus(LegalStatusDto pInputDataRec);

	/**
	 * Method Name: updatePersonGuardCnsrv Method Description:CAUDB8D - updates
	 * the Person Table for individuals with the status of conservatorship, and
	 * updates the opposite case .
	 *
	 * @param idPerson
	 *            the id person
	 * @param cdPersGuardCnsrv
	 *            the cd pers guard cnsrv
	 */
	public void updatePersonGuardCnsrv(Long idPerson, String cdPersGuardCnsrv);

	/**
	 * Method Name: deleteSubcareEvent Method Description: CAUD07D - Call stored
	 * procedure COMPLEX_DELETE.DELETE_SUBCARE_EVENT to delete a set of tables.
	 *
	 * @param idEvent
	 *            the id event
	 * @param lastUpdateDate
	 *            the last update date
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void deleteSubcareEvent(Long idEvent, Date lastUpdateDate) throws SQLException;

	/**
	 * Method Name: getResourceDetail Method Description: CSES28D - Get Resource
	 * and Placement details for person.
	 *
	 * @param idPerson
	 *            the id person
	 * @return the resource placement detail
	 */
	public List<ResourcePlacementOutDto> getResourcePlacementDetail(Long idPerson);

	/**
	 * Method Name: modifyNbrRscOpenSlots Method Description:CMSC16D - will
	 * increment or decrement NBR_RSRSC_OPEN_SLOTS by 1 * depending on what is
	 * passed into the DAM through ID_RESOURCE.
	 *
	 * @param idResource
	 *            the id resource
	 * @param nbrRsrcOpenSlots
	 *            the nbr rsrc open slots
	 */
	public void modifyNbrRscOpenSlots(Long idResource, String nbrRsrcOpenSlots);
	
	/**
	 * Method Name: modifyNbrRscOpenSlots Method Description:CMSC16D - will
	 * increment or decrement NBR_RSRSC_OPEN_SLOTS by 1 * depending on what is
	 * passed into the DAM through ID_RESOURCE.
	 *
	 * @param idResource
	 *            the id resource
	 * @param nbrRsrcOpenSlots
	 *            the nbr rsrc open slots
	 */
	public Long updateNbrRscOpenSlots(Long idResource, String nbrRsrcOpenSlots);

	/**
	 * Method Name: getWorkLoadsForStage Method Description: CSEC86D - Get
	 * workload detail for stage.
	 *
	 * @param idStage
	 *            the id stage
	 * @return the work loads for stage
	 */
	public List<WorkLoadDto> getWorkLoadsForStage(long idStage);

	/**
	 * Method Name: getIndLegalStatMissing Method Description: GET
	 * IND_LEGAL_STATE_MISSING.
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
	 * Method Name: getRecentLegalStatusForChild Method Description:Fetches the
	 * Latest legal Status of the child
	 * 
	 * @param idPerson
	 * @return
	 */
	public LegalStatusDto getRecentLegalStatusForChild(Long idPerson);

	/**
	 * 
	 * Method Name: getLatestLegalStatus (CSES78D) Method Description:This
	 * Service retrieves a full row from LEGAL_STATUS.
	 * 
	 * @param idPerson
	 * @param idCase
	 * @return
	 */
	LegalStatusDetailDto getLatestLegalStatus(Long idPerson, Long idCase);

	/**
	 * UIDS 2.3.3.5 - Remove a child from home - Income and Expenditures Gets
	 * the legal status for child.
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

	/**
	 * Method Name: getLegalStatusSubTypeForEvent Method Description:Fetch legal
	 * status subtype for provided event
	 *
	 * @param pInputDataRec
	 *            the input data rec
	 * @return the legal status subtype for event
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public List<LegalStatusOutDto> getLegalStatusSubTypeForEvent(LegalStatusInDto pInputDataRec); // ADS
																									// Change
																									// -
																									// 2.6.2.4
																									// -
																									// Legal
																									// Status

	/**
	 * Method Name: getPersonFullName Method Description:Fetch the Person full
	 * name by joining the legal_statu and person table from ID_LEGAL_STAT_EVENT
	 * passing to the legal_status
	 * 
	 * @param idEvent
	 * @return String
	 */
	public String getPersonFullName(Long idEvent);


	public Date getMaxLegalStatusDate(Long childId);

	public KinChildDto getPlacementLegalStatusInfoWithDate(Long childId, Date maxLegalStatusDate);


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

