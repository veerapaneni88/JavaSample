package us.tx.state.dfps.service.fcl.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ChildSxVctmztn;
import us.tx.state.dfps.common.domain.ChildSxVctmztnIncdnt;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseSearchDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CaseSearchInputReq;
import us.tx.state.dfps.service.fcl.dao.SexualVictimizationHistoryDao;
import us.tx.state.dfps.service.fcl.service.SexualVictimizationHistoryService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.TraffickingDao;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;

/**
 * 
 *
 *Class Description:<Servise Impl Class for Sexual Victimization History Page>
 *Oct 23, 2019- 3:04:03 PM
 *© 2019 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class SexualVictimizationHistoryServiceImpl implements SexualVictimizationHistoryService{

	private static final Logger log = Logger.getLogger(SexualVictimizationHistoryServiceImpl.class);
	
	@Autowired
	private SexualVictimizationHistoryDao sexualVictimizationHistoryDao;
	
	@Autowired
	private PersonDao personDao;
	
	@Autowired
	private CapsCaseDao capsCaseDao;

	@Autowired
	private TraffickingDao traffickingDao;
	
	@Override
	public SexualVictimHistoryDto getSexualVictimHistoryDto(Long idPerson) {
		log.debug("getSexualVictimHistoryDto - begin");
		SexualVictimHistoryDto sexualVictimHistoryDto = new SexualVictimHistoryDto();
		if (!ObjectUtils.isEmpty(idPerson) && !ServiceConstants.ZERO.equals(idPerson)) {
			ChildSxVctmztn childSxVctmztn = sexualVictimizationHistoryDao.getChildSxVctmztnByPersonId(idPerson);
			if (!ObjectUtils.isEmpty(childSxVctmztn)) {
				mapEntityToDto(childSxVctmztn, sexualVictimHistoryDto);
				List<ChildSxVctmztnIncdnt> incidents = sexualVictimizationHistoryDao.fetchSexualVictimHistory(idPerson);
				if (CollectionUtils.isNotEmpty(incidents)) {
					List<SexualVictimIncidentDto> incidentDtoList = new ArrayList<>();
					incidents.stream().forEach(c -> {
						SexualVictimIncidentDto incidentDto = new SexualVictimIncidentDto();
						mapEntityToDto(c, incidentDto);
						PersonDto createdPersonDto = personDao.getPersonById(c.getIdCreatedPerson());
						incidentDto.setNmCreatedBy(createdPersonDto.getNmPersonFull());
						PersonDto lastUpdatedPersonDto = personDao.getPersonById(c.getIdLastUpdatePerson());
						incidentDto.setNmModifiedBy(lastUpdatedPersonDto.getNmPersonFull());

						if (incidentDto.getIdAggressor() != null) {
							PersonDto person = personDao.getPersonById(incidentDto.getIdAggressor());
							incidentDto.setNmAggressor(person.getNmPersonFull());
						}
						incidentDtoList.add(incidentDto);
					});
					sexualVictimHistoryDto.setSvhIncidents(incidentDtoList);
				}
			} 
			PersonDto person = personDao.getPersonById(idPerson);
			sexualVictimHistoryDto.setIdPerson(idPerson);
			sexualVictimHistoryDto.setNmPerson(person.getNmPersonFull());
			sexualVictimHistoryDto.setHasSensitiveCase(checkHasSensitiveCase(idPerson));
		}
		// look for any unconfirmed trafficking
		TraffickingDto paramGetList = new TraffickingDto();
		paramGetList.setIdPerson(BigDecimal.valueOf(sexualVictimHistoryDto.getIdPerson()));
		List<TraffickingDto> traffickingList = traffickingDao.getTraffickingList(paramGetList);
		sexualVictimHistoryDto.setIndUnconfirmedTrafficking(traffickingList.stream().anyMatch(currTrafficking ->
				CodesConstant.CTRFTYP_SXTR.equals(currTrafficking.getCdtrfckngType()) &&
				CodesConstant.CTRFSTAT_SUSP.equals(currTrafficking.getCdtrfckngStat())));
		sexualVictimHistoryDto.setIndConfirmedTrafficking(traffickingList.stream().anyMatch(currTrafficking ->
				CodesConstant.CTRFTYP_SXTR.equals(currTrafficking.getCdtrfckngType()) &&
						CodesConstant.CTRFSTAT_CONF.equals(currTrafficking.getCdtrfckngStat())));
		log.debug("getSexualVictimHistoryDto - end");
		return sexualVictimHistoryDto;
	}

	@Override
	public void saveOrUpdateSexualVictimHistory(SexualVictimHistoryDto sexualVictimHistoryDto) {
		log.debug("saveOrUpdateSexualVictimHistory - begin");
		sexualVictimizationHistoryDao.saveChildSxVctmztn(sexualVictimHistoryDto);
		log.debug("saveOrUpdateSexualVictimHistory - end");
	}

	@Override
	public void deleteIncidents(List<Long> incidentIds, Long idPerson) {
		log.debug("deleteIncidents - begin");
		sexualVictimizationHistoryDao.deleteIncident(incidentIds, idPerson);
		log.debug("deleteIncidents - end");
	}
	
	public void mapEntityToDto(ChildSxVctmztn childSxVctmztn, SexualVictimHistoryDto sexualVictimHistoryDto) {
		sexualVictimHistoryDto.setIdChildSxVctmtzn(childSxVctmztn.getIdChildSxVctmztn());
		sexualVictimHistoryDto.setIdLastUpdatedPerson(childSxVctmztn.getIdLastUpdatePerson());
		sexualVictimHistoryDto.setDtCreated(childSxVctmztn.getDtCreated());
		sexualVictimHistoryDto.setDtLastUpdated(childSxVctmztn.getDtLastUpdate());
		sexualVictimHistoryDto.setIdCreatedPerson(childSxVctmztn.getIdCreatedPerson());
		sexualVictimHistoryDto.setIndChildHasSxVictimHistory(childSxVctmztn.getIndChildSxVctmztnHist());
		sexualVictimHistoryDto.setPreviousUnconfirmFinds(childSxVctmztn.getTxtPreviousUnconfirmFinds());
		sexualVictimHistoryDto.setSupervisionContactDesc(childSxVctmztn.getTxtSupervisionContactDesc());
        sexualVictimHistoryDto.setIndUnconfirmedVictimHistory(childSxVctmztn.getIndUnconfirmedVicHist());
        sexualVictimHistoryDto.setIndSexualBehaviorProblem(childSxVctmztn.getIndSxBehaviorProb());
        sexualVictimHistoryDto.setTxtBehaviorProblems(childSxVctmztn.getTxtSxBehaviorProb());
		sexualVictimHistoryDto.setTxtUnconfirmVic(childSxVctmztn.getTxtSxVicDesc());
		sexualVictimHistoryDto.setIdUpdPrsUnconfVicHistUser(childSxVctmztn.getIdUpdPrsUnconfVicHistUser());
	}
	
	public void mapEntityToDto(ChildSxVctmztnIncdnt incident, SexualVictimIncidentDto incidentDto) {
		incidentDto.setIdIncident(incident.getIdChildSxVctmztnIncdnt());
		incidentDto.setDtCreated(incident.getDtCreated());
		incidentDto.setDtIncident(incident.getDtIncident());
		incidentDto.setDtLastUpdated(incident.getDtLastUpdate());
		incidentDto.setIdCreatedBy(incident.getIdCreatedPerson());
		incidentDto.setIdModifiedBy(incident.getIdLastUpdatePerson());
		incidentDto.setIndApproxDate(incident.getIndApproxDate());
		incidentDto.setResponsibleComments(incident.getTxtResponsibleComments());
		incidentDto.setVictimComments(incident.getTxtVictimizationComments());
		incidentDto.setIdPerson(incident.getPerson().getIdPerson());
		incidentDto.setIdPlacement(incident.getIdPlcmtEvent());
		incidentDto.setIndChildInCare(incident.getIndChildInCare());
		incidentDto.setCdAggressorCategory(incident.getCdAggressorCtgy());
		if(null!=incident.getIdPersonAggressor()){
			incidentDto.setIdAggressor(incident.getIdPersonAggressor().getIdPerson());

		}
		incidentDto.setIdPlacementTa(incident.getIdPlacementTa());
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
