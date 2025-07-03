package us.tx.state.dfps.service.placement.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.CSAEpisodeDto;
import us.tx.state.dfps.service.casepackage.dto.CsaEpisodesIncdntDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanAdtnlSctnDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEducationDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEmtnlThrptcDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanHealthCareSummaryDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanQrtpPrmnncyMeetingDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanLegalGrdnshpDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanTransAdltAbvDtlDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.person.dto.EducationHistoryDto;
import us.tx.state.dfps.service.person.dto.PersonDtlDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.placement.dto.AllegationCpsInvstDtlDto;
import us.tx.state.dfps.service.placement.dto.CPAdoptionDto;
import us.tx.state.dfps.service.placement.dto.EventChildPlanDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.RsrcAddrPhoneDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.placement.dto.TemporaryAbsenceDto;
import us.tx.state.dfps.service.placement.dto.VisitationPlanInfoDtlsDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CommonApplicationDao will have all Dao operation to fetch the
 * records from table which are mapped Placement module. Feb 9, 2018- 2:18:13 PM
 * © 2017 Texas Department of Family and Protective Services
 */
public interface CommonApplicationDao {

	/**
	 * Method Description: This method is used to retrieve the Stage,Person,
	 * Case and StagePersonLink information by passing idStage as input request.
	 * Dam Name: CSEC15D
	 * 
	 * @param idStage
	 * @param cdStagePersRole
	 * @return StagePersonLinkCaseDto
	 */
	public StagePersonLinkCaseDto getStagePersonCaseDtl(Long idStage, String cdStagePersRole);

	/**
	 * Method Description: This method is used to retrieve the child's most
	 * recent authorized level of care by passing idPerson and cdPlocType as
	 * input request. Dam Name: CSES35D
	 * 
	 * @param idPerson
	 * @param cdPlocType
	 * @return List<PersonLocDto>
	 */
	public List<PersonLocDto> getPersonLocDtls(Long idPerson, String cdPlocType);

	/**
	 * Method Description: This method is used to retrieve tall allegation types
	 * and dispositions associated with the Id Person of the child by passing
	 * idPerson as input request. Dam Name: CLSS29D
	 * 
	 * @param idPersonVictim
	 * @return AllegationCpsInvstDtlDto
	 */
	public List<AllegationCpsInvstDtlDto> getAllegationCpsInvstDetails(Long idPersonVictim);

	/**
	 * Method Description: This method is used to retrieve the most recent row
	 * from the Child Plan and Event table by passing idPerson as input request.
	 * Dam Name: CSEC20D
	 * 
	 * @param idPerson
	 * @return EventChildPlanDto
	 */
	public EventChildPlanDto getEventChildPlanDtls(Long idPerson);

	/**
	 * Method Description: This method is used to retrieve the name of the
	 * Primary Child from Name table by passing idPerson as input request. Dam
	 * Name: CSEC35D
	 * 
	 * @param idPerson
	 * @return NameDetailDto
	 */
	public NameDetailDto getNameDetails(Long idPerson);

	/**
	 * Method Description: This Method will retrieve the child Medicaid Number
	 * and the Medicaid id number.DAM: CCMN72D
	 * 
	 * @param idPerson
	 * @param cdPersonIdType,
	 * @param indPersonIdInvalid,
	 * @param dtPersonIdEnd
	 * @return PersonIdDto
	 */
	public PersonIdDto getMedicaidNbrByPersonId(Long idPerson, String cdPersonIdType, String indPersonIdInvalid,
			Date dtPersonIdEnd);

	/**
	 * Method Description: This Method will retrieve the person details
	 * information from PERSON_DTL table.DAM Name:CSES31D
	 * 
	 * @param personId
	 * @return PersonDtlDto
	 */
	public PersonDtlDto getPersonDtlById(long idPerson);

	/**
	 * 
	 * Method Name: getFceEligibility. Method Description: This Method to get
	 * FceEligiblity details by passing idPerson of the child
	 * 
	 * @param idPerson
	 * @return FceEligibilityDto
	 */
	public FceEligibilityDto getFceEligibility(Long idPerson);

	/**
	 * 
	 * Method Name: getCSAEpisodesDtl. Method Description: This Method to get
	 * CSAEpisode details by passing idPerson of the child
	 * 
	 * @param idPerson
	 * @return CSAEpisodeDto
	 */
	public CSAEpisodeDto getCSAEpisodesDtl(Long idPerson);

	/**
	 * 
	 * Method Name: getCSAEpisodesIncdntDtl. Method Description: This Method to
	 * get CSAEpisode Incident details by passing idCsaEpisodes of the child
	 * 
	 * @param idCsaEpisodes
	 * @return List<CsaEpisodesIncdntDto>
	 */
	public List<CsaEpisodesIncdntDto> getCSAEpisodesIncdntDtl(Long idCsaEpisodes);

	/**
	 * ogetPersonLocDtls Method Name: getPlacementDtl. Method Description: This
	 * Method to get latest placement details by passing idPerson of the child
	 * 
	 * @param idPlcmntChild
	 * @return PlacementDtlDto
	 */
	public PlacementDtlDto getPlacementDtl(Long idPlcmntChild);

	/**
	 * Method Name: getServiceLevelInfo. Method Description: This method is used
	 * to retrieve the child's most recent authorized level of care by passing
	 * idPerson as input request.
	 * 
	 * @param idPerson
	 * @return PersonLocDto
	 */
	public PersonLocDto getServiceLevelInfo(Long idPerson);

	/**
	 * Method Name: getServiceLevelInfo. Method Description: This method is used
	 * to retrieve the child's most recent trafficking by passing idPerson as
	 * input request.
	 * 
	 * @param idPerson
	 * @return TraffickingDto
	 */
	public TraffickingDto getTrfckngDtl(Long idPerson);

	/**
	 * 
	 * Method Name: getChildPlanEmtnlSectnDtl. Method Description: This Method
	 * to get ChildPlanEmtnlSectnDtl by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanEmtnlThrptcDtlDto
	 */
	public ChildPlanEmtnlThrptcDtlDto getChildPlanEmtnlSectnDtl(Long idChildPlanEvent);

	/**
	 * 
	 * Method Name: getChildPlanHlthCareSummDtl. Method Description: This Method
	 * to get ChildPlanHlthCareSummDtl by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanHealthCareSummaryDto
	 */
	public ChildPlanHealthCareSummaryDto getChildPlanHlthCareSummDtl(Long idChildPlanEvent);

	/**
	 *
	 * Method Name: getChildPlanQrtpPtmDtl. Method Description: This Method
	 * to get ChildPlanQrtpPtmDtl by passing idChildPlanEvent of the child
	 *
	 * @param idChildPlanEvent
	 * @return ChildPlanQrtpPrmnncyMeetingDto
	 */
	public ChildPlanQrtpPrmnncyMeetingDto getChildPlanQrtpPtmDtl(Long idChildPlanEvent);

	/**
	 * 
	 * Method Name: getEducationDtls Method Description: This Method is used to
	 * retrieve the child's most recent education Detail information.
	 * 
	 * @param idPerson
	 * @return EducationHistoryDto
	 */
	public List<EducationHistoryDto> getEducationDtls(Long idPerson);

	/**
	 * 
	 * Method Name: getRsrcAddrPhoneDtl Method Description: This Method is used
	 * to retrieve the child's most recent education Detail information.
	 * 
	 * @param idPerson
	 * @return EducationHistoryDto
	 */
	public RsrcAddrPhoneDto getRsrcAddrPhoneDtl(Long idResource);

	/**
	 * 
	 * Method Name: getChildPlanEducationDtl. Method Description: This Method to
	 * get ChildPlanEducationDtl by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanEducationDto
	 */
	public ChildPlanEducationDto getChildPlanEducationDtl(Long idChildPlanEvent);

	/**
	 * 
	 * Method Name: getChildPlanTranstmAdultAbv. Method Description: This Method
	 * to get ChildPlanTranstmAdultAbv by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanTransAdltAbvDtlDto
	 */
	public ChildPlanTransAdltAbvDtlDto getChildPlanTranstmAdultAbv(Long idChildPlanEvent);

	/**
	 * 
	 * Method Name: getChildPlanAdtnlSctnDtls. Method Description: This Method
	 * to get ChildPlanAdtnlSctnDtls by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanAdtnlSctnDtlDto
	 */
	public ChildPlanAdtnlSctnDtlDto getChildPlanAdtnlSctnDtls(Long idChildPlanEvent);

	/**
	 * 
	 * Method Name: getChildPlanAdoptnDtls. Method Description: This Method to
	 * get ChildPlanAdoptnDtls by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return List<CPAdoptionDto>
	 */
	public List<CPAdoptionDto> getChildPlanAdoptnDtls(Long idChildPlanEvent);

	/**
	 * 
	 * Method Name: getVisitationPlanInfoDtl Method Description: This Method is
	 * used to retrieve the child's most recent Visitation Plan information.
	 * 
	 * @param idPerson
	 * @return List<VisitationPlanInfoDtlsDto>
	 */
	public List<VisitationPlanInfoDtlsDto> getVisitationPlanInfoDtl(Long idPerson, Long idStage);

	/**
	 * 
	 * Method Name: getChildPlanLegalGrdnShpDtl. Method Description: This Method
	 * to get ChildPlanLegalGrdnShpDtl by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanHealthCareSummaryDto
	 */
	public ChildPlanLegalGrdnshpDto getChildPlanLegalGrdnShpDtl(Long idChildPlanEvent);

	/**
	 * 
	 * Method Name: getPlacementLogDtl. Method Description: This Method to get
	 * all placement details available for the child by passing idPerson of the
	 * child
	 * 
	 * @param idPlcmntChild
	 * @return List<PlacementDtlDto>
	 */
	public List<PlacementDtlDto> getPlacementLogDtl(Long idPlcmntChild);
	
	/**
	 * 
	 * Method Name: getCSAEpisodesDtl. Method Description: This Method to get
	 * CSAEpisode closed details by passing idPerson of the child
	 * 
	 * @param idPerson
	 * @return CSAEpisodeDto
	 */	
	public List<CSAEpisodeDto> getCSAEpisodesClosedDtl(Long idPerson);
	
	/**
	 * 
	 * Method Name: getCSAEpisodesIncdntDtl. Method Description: This Method to
	 * get CSAEpisode Closed Incident details by passing list of idCsaEpisodes of the child
	 * 
	 * @param idCsaEpisodes
	 * @return CsaEpisodesIncdntDto
	 */	
	public List<CsaEpisodesIncdntDto> getCSAEpisodesClosedIncdntDtl(List<Long> idCsaEpisodesList);

	/**
	 * 
	 * Method Name: getCSAEpisodesIncdntDtl. Method Description: This Method to
	 * get CSAEpisode Incident details by passing idPerson of the child
	 * 
	 * @param idCsaEpisodes
	 * @return List<CsaEpisodesIncdntDto>
	 */
	public List<CsaEpisodesIncdntDto> getCSAEpisodesIncdntDtlByIdPerson(Long idPerson);

	//PPM 65209 - Temporary Absences for Placements
	/**
	 * This method gets all the Temporary Absences for the idPerson of the child
	 * @param idPlcmntChild
	 * @return
	 */
	public List<TemporaryAbsenceDto> getTemporaryAbsenceList(Long idPlcmntChild);
}
