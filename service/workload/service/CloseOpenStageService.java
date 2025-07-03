package us.tx.state.dfps.service.workload.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.dto.CloseOpenStageInputDto;
import us.tx.state.dfps.common.dto.CloseOpenStageOutputDto;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.dto.SituationDto;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.investigation.dto.ApsInvstDetailDto;
import us.tx.state.dfps.service.investigation.dto.CpsInvstDetailDto;
import us.tx.state.dfps.service.investigation.dto.FacilAllegDto;
import us.tx.state.dfps.service.investigation.dto.FacilityInvstDtlDto;
import us.tx.state.dfps.service.investigation.dto.LicensingInvstDtlDto;
import us.tx.state.dfps.service.person.dto.PersonCategoryDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.AdptAsstAppEventLinkDto;
import us.tx.state.dfps.service.workload.dto.AdptSubEventLinkDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.EventPlanLinkDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.ReopenStageDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StageLinkDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StageProgDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN03U
 * Description: This archived library function provides the necessary edits and
 * updates required to close a stage and open a new one. It generates all the
 * required events and to-do's related with the closure of a stage and the
 * opening of a new one. Apr 4, 2017 - 2:06:05 PM
 */

public interface CloseOpenStageService {

	/**
	 * closeOpenStage This archived library function provides the necessary
	 * edits and updates required to close a stage and open a new one. It
	 * generates all the required events and to-do's related with the closure of
	 * a stage and the opening of a new one.
	 * 
	 * Service Name - CCMN03U
	 * 
	 * @param closeOpenStageInputDto
	 * @return
	 * @ @throws
	 *       ParseException
	 */
	public CloseOpenStageOutputDto closeOpenStage(CloseOpenStageInputDto closeOpenStageInputDto);

	/**
	 * This DAM will receives CD STAGE, CD STAGE PROGRAM, and CD STAGE REASON
	 * CLOSED from the Service and retrieves entire row(s) from the STAGE_PROG
	 * table. SIR#2417-KDB
	 * 
	 * Service Name - CCMN03U, DAM Name - CCMNB8D
	 * 
	 * @param closeOpenStageInputDto
	 * @return @
	 */
	public List<StageProgDto> getStgProgroession(CloseOpenStageInputDto closeOpenStageInputDto);

	/**
	 * If the required function is UPDATE: This DAM performs an update of a
	 * record in the STAGE table. If the ID STAGE in the record equals the ID
	 * STAGE value passed in the Input Message, then the DT STAGE CLOSE for the
	 * selected record is updated to the current system's date
	 * 
	 * Service Name - CCMN03U, CCMN88S, DAM Name - CCMND4D
	 * 
	 * @param reClass
	 * @param idStage
	 * @throws DataNotFoundException
	 * @
	 */
	public void updateStageCloseRecord(Long idStage, Date dtClose, String reasonClosed, String cdStage);

	/**
	 * This dam was written to add, update, and delete from the STAGE table.
	 * 
	 * Service Name - CCMN03U, DAM Name - CINT12T
	 * 
	 * @param stageDto
	 * @param action
	 * @
	 */
	public Long stageAUD(StageDto stageDto, String action);

	/**
	 * Adds, updates or deletes one row from the CAPS_CASE table for a given
	 * ID_CASE.
	 * 
	 * Service Name : CCMN03U, DAM Name : CCMNB2D
	 * 
	 * @param capsCaseDto
	 * @param action
	 * @
	 */
	public Long capsCaseAUD(CapsCaseDto capsCaseDto, String action);

	/**
	 * Performs AUD functions on the SITUATION table. Note: This DAM was not
	 * written to account for intervening updates. Warning: There is
	 * non-GENDAM'd generated code in this file.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINT13D
	 * 
	 * @param situationDto
	 * @param action
	 * @return @
	 */
	public Long situationAUD(SituationDto situationDto, String action);

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 * 
	 * Outputs: Service return code.
	 * 
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 * 
	 * 
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 * 
	 * @param contactDto
	 * @param action
	 * @
	 */
	public void contactAUD(ContactDto contactDto, String action);

	/**
	 * This dam will update rows on the Admin Review table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CAUDA3D
	 * 
	 * @param adminReviewDto
	 * @param action
	 * @throws ServicException
	 */
	public void adminReviewAUD(AdminReviewDto adminReviewDto, String action);

	/**
	 * This DAM adds, updates, or deletes a full row in the ALLEGATION table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CINV07D
	 * 
	 * @param allegationDetailDto
	 * @param action
	 * @
	 */
	public Long allegationAUD(AllegationDetailDto allegationDetailDto, String action);

	/**
	 * This DAM is used by the CloseOpenStage common function (CCMN03U) to add a
	 * dummy row to the FACIL_ALLEG table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CINVB4D
	 * 
	 * @param facilAllegDto
	 * @
	 */
	public void facilAllegSave(FacilAllegDto facilAllegDto);

	/**
	 * This DAM inserts/updates APS_INVST_DETAIl
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV24D
	 * 
	 * @param apsInvestDetailDto
	 * @param action
	 * @
	 */
	public void apsInvestDetailAUD(ApsInvstDetailDto apsInvstDetailDto, String action, String indAPSInvCnclsn);

	/**
	 * This DAM will add, update, and delete from the CPS_INVST_DETAIL table
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV12D
	 * 
	 * @param cpsInvstDetailDto
	 * @param action
	 * @
	 */
	public void cpsInvestDetailAUD(CpsInvstDetailDto cpsInvstDetailDto, String action);

	/**
	 * This DAM will add, update, or delete a full record from the
	 * LICENSING_INVST_DTL table.
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV53D
	 * 
	 * @param licensingInvstDtlDto
	 * @param action
	 * @
	 */
	public void licensingInvstDtlAUD(LicensingInvstDtlDto licensingInvstDtlDto, String action);

	/**
	 * This DAM performs AUD functionality on the FACILITY INVST DTL table. This
	 * DAM only inserts.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV54D
	 * 
	 * @param facilityInvstDtlDto
	 * @param action
	 * @
	 */

	public void facilityInvstDtlAUD(FacilityInvstDtlDto facilityInvstDtlDto, String action);

	/**
	 * AUD DAM for the EVENT_PLAN_LINK table. Currently only performs INSERTs.
	 * 
	 * Service Name : CCMN03U, DAM Name : CAUDE8D
	 * 
	 * @param eventPlanLinkDto
	 * @param action
	 * @
	 */
	public void eventPlanLinkAUD(EventPlanLinkDto eventPlanLinkDto, String action);

	/**
	 * This DAM update of PERSON CHARACTERISTICS table. It does a full row add,
	 * or updates the end date. There is no delete functionality.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV48D
	 * 
	 * @param characteristicsDto
	 * @param lNbrPersonAge
	 * @param action
	 * @
	 */
	void characteristicsAUD(CharacteristicsDto characteristicsDto, int lNbrPersonAge, String action,
			List<Long> idCharacteristics);

	/**
	 * This DAM will update all tables associated with the Investigation Person
	 * Detail window. It evolved with the functionality of the window, but in
	 * essence just updates those fields needed for Person Detail. Case ADD:
	 * Always Add to PERSON table, CATEGORY STAGE PERSON LINK Table. If a person
	 * has a a full name of Unknown and is a Principle in the Case, concatenate
	 * the ID PERSON to the end (eg, Unknown 1119). Do not update name table in
	 * this case. Case UPDATE: Always update PERSON and STAGE PERSON LINK
	 * tables. Case DELETE: Always Delete from STAGE PERSON LINK, Update the
	 * Status on the PERSON table. Case LOWER:(A person is being related to the
	 * Stage.) Add to the STAGE_PERSON LINK table. Update the CATEGORY table
	 * only if the Client Sends a category. Case PERSON: A full row update of
	 * the PERSON TABLE.
	 * 
	 * WARNING - This DAM contain non-GENDAM-generated code. Care should be
	 * taken when regenerating.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV41D
	 *
	 * @param personDto
	 * @param stagePersonDto
	 * @param personIdDto
	 * @param personCategoryDto
	 * @param action
	 * @
	 */
	void investigationPersonDtlAUD(PersonDto personDto, StagePersonLinkDto stagePersonLinkDto, PersonIdDto personIdDto,
			PersonCategoryDto personCategoryDto, String action);

	/**
	 * This DAM will update all tables associated with the Investigation Person
	 * Detail window. It evolved with the functionality of the window, but in
	 * essence just updates those fields needed for Person Detail. Case ADD:
	 * Always Add to PERSON table, CATEGORY STAGE PERSON LINK Table. If a person
	 * has a a full name of Unknown and is a Principle in the Case, concatenate
	 * the ID PERSON to the end (eg, Unknown 1119). Do not update name table in
	 * this case. Case UPDATE: Always update PERSON and STAGE PERSON LINK
	 * tables. Case DELETE: Always Delete from STAGE PERSON LINK, Update the
	 * Status on the PERSON table. Case LOWER:(A person is being related to the
	 * Stage.) Add to the STAGE_PERSON LINK table. Update the CATEGORY table
	 * only if the Client Sends a category. Case PERSON: A full row update of
	 * the PERSON TABLE.
	 * 
	 * WARNING - This DAM contain non-GENDAM-generated code. Care should be
	 * taken when regenerating.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV41D
	 * 
	 * @param personDto
	 * @param action
	 * @return @
	 */
	public Long personAUD(PersonDto personDto, String action);

	/**
	 * This DAM ADDs a full row in the STAGE_LINK table. It does not perform any
	 * UPDATE or DELETE functionality.
	 * 
	 * Service Name : CCMN03U, CCMNC1D
	 * 
	 * @param stageLink
	 * @
	 */
	public void stageLinkSave(StageLinkDto stageLinkDto);

	/**
	 * The DAM will insert a new SVC_AUTH_ID for a particular event.
	 * 
	 * Service Name : CCMN03U, DAM Name : CAUD34D
	 * 
	 * @param svcAuthEventLink
	 * @
	 */
	public void svcAuthEventLinkSave(Long idSvcAuthEvent, Long idSvcAuth);

	/**
	 * This DAM will add and delete a row from the ADPT_SUB_EVENT_LINK table.
	 * 
	 * Service Name : CCMN03U, DAM Name : CAUDB2D
	 * 
	 * @param adptSubEventLinkEditDto
	 * @param action
	 * @
	 */
	public void adptSubEventLinkEdit(AdptSubEventLinkDto adptSubEventLinkDto, String action);

	/**
	 * This DAM will add and delete a row from the ADPT_ASST_APP_EVENT_LINK
	 * table.
	 * 
	 * Service Name : CCMN03U, DAM Name : CAUDM2D
	 * 
	 * @param adptAsstAppEventLinkDto
	 * @param action
	 * @
	 */
	public void adptAsstAppEventLinkEdit(AdptAsstAppEventLinkDto adptAsstAppEventLinkDto, String action);

	void reopenClosedStage(ReopenStageDto reopenStageDto);

	/**
	 * Method Name: createOrUpdatePersonEligibility
	 * Method Description: This method is used to Create or Update the Person Eligibility for INV Stage
	 *
	 * @param idStage
	 * @return
	 */
	String createOrUpdatePersonEligibility(Long idStage);
}
