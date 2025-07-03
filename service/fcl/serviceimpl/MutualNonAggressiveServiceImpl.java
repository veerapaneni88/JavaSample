package us.tx.state.dfps.service.fcl.serviceimpl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.common.domain.ChildSxMutalIncdnt;
import us.tx.state.dfps.common.domain.ChildSxVctmztnIncdnt;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseSearchDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CaseSearchInputReq;
import us.tx.state.dfps.service.fcl.dao.MutualNonAggressiveIncidentDao;
import us.tx.state.dfps.service.fcl.service.MutualNonAggressiveService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.fcl.dto.MutualNonAggressiveIncidentDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 *
 *Class Description:<Service Impl Class for Mutual Non-Aggressive Incidents>
 *Jan 05, 2022 - 3:10:09 PM
 *© 2022 Texas Department of Family and Protective Services
 *
 * There is no longer a way to create, update, or delete Mutual Non-Aggressive Incidents, but MutualNonAggressiveService
 * is still used during person merge to handle any Mutual non-Aggressive incidents that were created in the brief window
 * where the functionality was live. We expect this functionality to be needed in the future, and so
 * we expect this code to come into full use in the future.
 *
 */
@Service
@Transactional
public class MutualNonAggressiveServiceImpl implements MutualNonAggressiveService {

	private static final Logger log = Logger.getLogger(MutualNonAggressiveServiceImpl.class);
	@Autowired
	private MutualNonAggressiveIncidentDao mutualNonAggressiveIncidentDao;
	@Autowired
	private PersonDao personDao;
	@Autowired
	private CapsCaseDao capsCaseDao;

	@Override
	public SexualVictimHistoryDto getMutualNonAggressiveIncidents(Long idPerson) {
		log.debug("getSexualVictimHistoryDto - begin");
		SexualVictimHistoryDto sexualVictimHistoryDto = new SexualVictimHistoryDto();
		List<ChildSxMutalIncdnt> incidents = mutualNonAggressiveIncidentDao.getMutualNonAggressiveIncidents(idPerson);
		if (CollectionUtils.isNotEmpty(incidents)) {
			List<MutualNonAggressiveIncidentDto> childConsensualSexIncidents = new ArrayList<>();
			for(ChildSxMutalIncdnt c:incidents){
				MutualNonAggressiveIncidentDto childConsensualSexIncidentDto = new MutualNonAggressiveIncidentDto();
				childConsensualSexIncidentDto.setIdIncident(c.getIdChildSxMutualIncdnt());
				childConsensualSexIncidentDto.setDtIncident(c.getDtIncident());
				childConsensualSexIncidentDto.setDtCreated(c.getDtCreated());
				childConsensualSexIncidentDto.setCdMutualCategory(c.getCdMutualCtgy());
				childConsensualSexIncidentDto.setIndChildInCare(c.getIndChildInCare());
				childConsensualSexIncidentDto.setIdPlacement(c.getIdPlcmtEvent());
				if(null!=c.getPerson()) {
					childConsensualSexIncidentDto.setIdPerson(c.getPerson().getIdPerson());
				}
				if(null!=c.getPersonMutual()) {
					childConsensualSexIncidentDto.setNmPersonOther(c.getPersonMutual().getNmPersonFull());
					childConsensualSexIncidentDto.setIdPersonOther(c.getPersonMutual().getIdPerson());
				}
				childConsensualSexIncidentDto.setIndApproxDate(c.getIndApproxDate());
				childConsensualSexIncidentDto.setAllPertinentInfoDesc(c.getTxtMutualIncdntDesc());
				if(null!=(c.getIdCreatedPerson())) {
					PersonDto person = personDao.getPersonById(c.getIdCreatedPerson());
					childConsensualSexIncidentDto.setNmCreatedBy(person.getNmPersonFull());
				}
				if(null!=c.getIdLastUpdatePerson()) {
					PersonDto lastUpdatedPersonDto = personDao.getPersonById(c.getIdLastUpdatePerson());
					childConsensualSexIncidentDto.setNmModifiedBy(lastUpdatedPersonDto.getNmPersonFull());
					childConsensualSexIncidentDto.setDtLastUpdated(c.getDtLastUpdate());
				}
				childConsensualSexIncidents.add(childConsensualSexIncidentDto);

			};
			sexualVictimHistoryDto.setConsensualIncidents(childConsensualSexIncidents);
		}
		log.debug("getSexualVictimHistoryDto - end");
		return sexualVictimHistoryDto;
	}

	@Override
	public void saveOrUpdateMutualNonAggressiveIncidents(SexualVictimHistoryDto sexualVictimHistoryDto) {
		log.debug("saveOrUpdateSexualVictimHistory - begin");
		mutualNonAggressiveIncidentDao.saveMutualNonAggressiveIncidents(sexualVictimHistoryDto);
		log.debug("saveOrUpdateSexualVictimHistory - end");
	}

	// TODO merely untested
	@Override
	public void deleteMutualNonAggressiveIncidents(List<Long> incidentIds, Long idPerson) {
		log.debug("deleteIncidents - begin");
		mutualNonAggressiveIncidentDao.deleteMutualNonAggressiveIncidents(incidentIds, idPerson);
		log.debug("deleteIncidents - end");
	}

	/**
	 *
	 *Method Name:	checkHasSensitiveCase
	 *Method Description: This method is used to check if person is in any sensitive case or not
	 *@param idPerson
	 *@return
	 */
	private boolean checkHasSensitiveCase(Long idPerson) {
		boolean retVal = false;
		//Check person has sensitive case or not
		CaseSearchInputReq caseSearchInputReq = new CaseSearchInputReq();
		List<Long> idPersonList = new ArrayList<>();
		idPersonList.add(idPerson);
		caseSearchInputReq.setIdPersonList(idPersonList);
		List<CapsCaseSearchDto> caseList = capsCaseDao.getCaseByInput(caseSearchInputReq);
		if (!CollectionUtils.isEmpty(caseList)) {
			for (CapsCaseSearchDto caseDto : caseList) {
				if (ServiceConstants.Y.equals(caseDto.getIndCaseSensitive())) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}
}
