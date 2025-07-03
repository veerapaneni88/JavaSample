/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Service Implementation Class for Child Sexual Aggression page.
 *Sep 17, 2018- 4:39:01 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.csa.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.casepackage.dto.CSADto;
import us.tx.state.dfps.service.casepackage.dto.CSAEpisodeDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.csa.dao.CSADao;
import us.tx.state.dfps.service.csa.service.CSAService;
import us.tx.state.dfps.service.fcl.serviceimpl.SexualVictimizationHistoryServiceImpl;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CSAPrefillData;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This is the
 * Service Implementation for Child Sexual Aggression Page.> Sep 17, 2018-
 * 4:39:01 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class CSAServiceImpl implements CSAService {

	@Autowired
	CSAPrefillData csaPrefillData;

	@Autowired
	CSADao csaDao;

	@Autowired
	PersonDao personDao;

	/**
	 * Method Name: fetchCSA Method Description: Fetch CSA Episodes and
	 * Incidents
	 * 
	 * @param csaDto
	 * @return
	 */
	@Override
	public CSADto fetchCSA(CSADto csaDto) {
//		CSADto returnVal = csaDao.fetchCSA(csaDto);
		CSADto returnVal = csaDao.fetchCSAIncdnts(csaDto);
		if (returnVal.getCsaIncidents() != null) {
			returnVal.getCsaIncidents().stream().forEach(currIncident -> {
				// fetch names for created by and updated by
				PersonDto createdPersonDto = personDao.getPersonById(currIncident.getIdCreatedPerson());
				currIncident.setNmCreatedPerson(createdPersonDto.getNmPersonFull());
				if (currIncident.getIdCreatedPerson().equals(currIncident.getIdLastUpdatePerson())) {
					currIncident.setNmLastUpdatePerson(createdPersonDto.getNmPersonFull());
				} else {
					PersonDto lastUpdatedPersonDto = personDao.getPersonById(currIncident.getIdLastUpdatePerson());
					currIncident.setNmLastUpdatePerson(lastUpdatedPersonDto.getNmPersonFull());
				}
				if (currIncident.getIdVictim() != null) {
					PersonDto person = personDao.getPersonById(currIncident.getIdVictim());
					currIncident.setNmVictim(person.getNmPersonFull());
				}
			});
		}

		return returnVal;
	}

	/**
	 * Method Name: getCSADetailsByIDPersonAndEpisodes Method Description: This
	 * is the service to get the CSA Episode details for CSA form display.
	 * 
	 * @param csaDto
	 * @param selectedEpisodes
	 * @return csaPrefillData
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	// TODO REMOVE EPISDOES PARAMETER SINCE THEY ARE OBSOLETE
	public PreFillDataServiceDto getCSADetailsByIDPersonAndEpisodes(CSADto csaDto, String selectedEpisodes) {
		CSADto csaDtoResponse = csaDao.fetchCSAIncdnts(csaDto);

		PersonDto personDto = personDao.getPersonById(csaDtoResponse.getIdPerson());
		csaDtoResponse.setNmPerson(personDto.getNmPersonFull());

		return csaPrefillData.returnPrefillData(csaDtoResponse);
	}

	@Override
	public void deleteCsaEpisodeIncidents(List<Long> episodeIncidentIds, Long idPerson) {
		csaDao.deleteCsaEpisodeIncidents(episodeIncidentIds, idPerson);
	}

	@Override
	public void deleteCsaEpisodes(List<Long> idCsaEpisodes, Long idUser){
		csaDao.deleteCsaEpisodes(idCsaEpisodes,idUser);
	}

	@Override
	public CSADto saveCSA(CSADto csaDto) {
		return csaDao.saveOrupdateCSA(csaDto);
	}

}
