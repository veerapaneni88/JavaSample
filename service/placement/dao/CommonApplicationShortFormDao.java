package us.tx.state.dfps.service.placement.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.CaExCommonApplication;
import us.tx.state.dfps.common.domain.CaExEducationService;
import us.tx.state.dfps.common.domain.CaExHospitalization;
import us.tx.state.dfps.common.domain.CaExMedication;
import us.tx.state.dfps.common.domain.CaExPlacementLog;
import us.tx.state.dfps.common.domain.CaExReturnFromRunaway;
import us.tx.state.dfps.common.domain.CaExServicesProvided;
import us.tx.state.dfps.common.domain.CaExSiblings;
import us.tx.state.dfps.common.domain.CaExSpecialProgramming;
import us.tx.state.dfps.common.domain.CaExSubstanceUse;
import us.tx.state.dfps.service.common.request.CommonAppShortFormReq;
import us.tx.state.dfps.service.common.response.CommonAppShortFormRes;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.placement.dto.CommonApplicationShortFormDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.ShortFormCsaEpisodeIncdntsDto;
import us.tx.state.dfps.service.placement.dto.ShortFormSiblingsDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CommonApplicationShortDao will have all Dao operation to fetch
 * the records from table. © 2017 Texas Department of Family and Protective
 * Services
 */
public interface CommonApplicationShortFormDao {

	CaExCommonApplication retrieveCommonById(Long idCaExCommonApplication);

	CaExCommonApplication retrieveCommonByEventId(Long idEvent);
	
	CommonAppShortFormRes saveShortFormData(CommonAppShortFormReq req);
	
	FceEligibilityDto getFceEligibility(Long idPerson);

	FceApplicationDto getRemovalAddr(Long personId);

	PersonLocDto getServiceLevelInfo(Long idPerson);
	
	List<CaExSpecialProgramming> retrieveSplProgById(Long idCaEx);

	CommonAppShortFormRes approveShortForm(CommonApplicationShortFormDto commonApplicationShortFormDto, Long approverId);

	CommonAppShortFormRes rejectShortForm(CommonApplicationShortFormDto commonApplicationShortFormDto, Long approverId,
			String rejectReason);

	List<CaExReturnFromRunaway> retrieveReturnRunaway(Long idCaEx);

	List<CaExEducationService> retrieveEducationService(Long idCaEx);

	List<CaExSiblings> retrieveSiblingList(Long idCaEx);

	List<CaExPlacementLog> retrievePlacementLogByExId(Long idCaExCommonApplication);

	List<CaExMedication> retrieveMedicationLst(Long idCaEx);

	List<CaExServicesProvided> retrieveTherapyLst(Long idCaEx);

	List<CaExSubstanceUse> retrieveSubstanceAbuse(Long idCaEx);

	List<CaExHospitalization> retrieveHospitalizationByExId(Long idCaExCommonApplication);

	List<ShortFormCsaEpisodeIncdntsDto> getCSAEpisodeIncdntDtls(Long idPerson);

	CnsrvtrshpRemovalDto getRemovalDate(Long idPerson);

	List<SexualVictimIncidentDto> getSFSexualVictimization(Long idCaEx);

	List<TraffickingDto> getSFTraffickingHistory(Long idCaEx);

	List<ShortFormCsaEpisodeIncdntsDto> getSFEpisodeIncidents(Long idCaEx);
	
	List<ShortFormSiblingsDto> retrieveSiblingListByExId(Long idCaEx);
}
