/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This is the DAO Implementation class for CSA Page.
 *Sep 17, 2018- 4:43:30 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.csa.daoimpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.casepackage.dto.CSADto;
import us.tx.state.dfps.service.casepackage.dto.CSAEpisodeDto;
import us.tx.state.dfps.service.casepackage.dto.CsaEpisodesIncdntDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.csa.dao.CSADao;
import us.tx.state.dfps.service.person.dao.PersonCharDao;
import us.tx.state.dfps.service.person.dto.PersCharsDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This is the
 * DAO Implementation class for Child Sexual Aggression Page.> Sep 17, 2018-
 * 4:43:30 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CSADaoImpl implements CSADao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Autowired
	PersonCharDao personCharDao;

	@Value("${CSADaoImpl.getOpenSUBStageYouth}")
	private String getOpenSUBStageYouthSql;

	private static String PERSON_NO_CHAR_NUMBER_ONE = "1";

	/**
	 * Method Name: fetchCSA Method Description: Method to fetch Episodes and
	 * Incidents recorded for Child Sexual Aggression page.
	 *
	 * @return CSADto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CSADto fetchCSA(CSADto csaDto) {
		int childAge = 0;
		List<CSAEpisodeDto> finalEpisodeList = new ArrayList<>();
		/*
		 * Fetching all the recorded Episode's and Incident's for Child Sexual
		 * Aggression Page for the selected Person.
		 */
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CsaEpisode.class);
		criteria.add(Restrictions.eq("idPerson", csaDto.getIdPerson()));
		List<CsaEpisode> episodeList = criteria.list();
		/* If response is not empty, Copying the data from Entity to DTO. */
		if (!ObjectUtils.isEmpty(episodeList)) {
			episodeList.forEach(episode -> {
				List<CsaEpisodesIncdntDto> finalIncidentList = new ArrayList<>();
				CSAEpisodeDto csaEpisodeDto = new CSAEpisodeDto();
				BeanUtils.copyProperties(episode, csaEpisodeDto);
				/*
				 * If the episode is End Dated, setting the flag to true, to
				 * Lock the respective Episode and Incident's.
				 */
				if (!ObjectUtils.isEmpty(csaEpisodeDto.getDtEpisodeEnd())) {
					csaEpisodeDto.setEpisodeEndDated(Boolean.TRUE);
				} else {
					csaEpisodeDto.setEpisodeEndDated(Boolean.FALSE);
				}
				/*
				 * If incidents List is Not empty, Copying all the incidents
				 * recorded from Entity to DTO.
				 */
				if (!ObjectUtils.isEmpty(episode.getCsaEpisodesIncdnts())) {
					episode.getCsaEpisodesIncdnts().forEach(incident -> {
						CsaEpisodesIncdntDto csaincidentDto = new CsaEpisodesIncdntDto();
						BeanUtils.copyProperties(incident, csaincidentDto);
						csaincidentDto.setCdVictimCategory(incident.getCdVictimCtgy());
						csaincidentDto.setIdPlacement(incident.getIdPlcmtEvent());
						csaincidentDto.setIdPlacementTa(incident.getIdPlacementTa());
						if (incident.getIdPersonVictim() != null) {
							csaincidentDto.setIdVictim(incident.getIdPersonVictim().getIdPerson());
						}
						csaincidentDto.setIdCsaEpisodes(incident.getCsaEpisode().getIdCsaEpisodes());
						finalIncidentList.add(csaincidentDto);
					});
					/* Sorting Incident List based on Date of Incident */
					List<CsaEpisodesIncdntDto> sortedIncidentList = finalIncidentList.stream()
							.sorted(Comparator.comparing(CsaEpisodesIncdntDto::getDtIncdnt).reversed())
							.collect(Collectors.toList());
					csaEpisodeDto.setCsaEpisodesIncdntList(sortedIncidentList);
				}
				finalEpisodeList.add(csaEpisodeDto);
			});
			/* Sorting Episode List based on Start Date */
			List<CSAEpisodeDto> sortedCSAEpisodesList = finalEpisodeList.stream()
					.sorted(Comparator.comparing(CSAEpisodeDto::getDtCreated).reversed()).collect(Collectors.toList());
			/*
			 * Forming the response structure and adding final list to csaDto.
			 */
			csaDto.setCsaEpisodeList(sortedCSAEpisodesList);
			csaDto.setIsCSAEmptyList(Boolean.FALSE);
		} else {
			/* if List is empty, setting IsCSAEmptyList Flag to true. */
			csaDto.setIsCSAEmptyList(Boolean.TRUE);
		}
		/* Validate that the youth has an open SUB stage. */
		if (!ObjectUtils.isEmpty(csaDto.getIdPerson())) {
			SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getOpenSUBStageYouthSql)
					.setParameter(ServiceConstants.IDPERSON, csaDto.getIdPerson()));
			if (null != query.uniqueResult()) {
				childAge = DateUtils.getAge((Date) query.uniqueResult());
				if (childAge < 18) {
					csaDto.setIsOpenSUBStageYouth(Boolean.TRUE);
				}
			} else {
				csaDto.setIsOpenSUBStageYouth(Boolean.FALSE);
			}
		}
		return csaDto;
	}

	/**
	 * Method Name: fetchCSA Method Description: Method to fetch Episodes and
	 * Incidents recorded for Child Sexual Aggression page.
	 *
	 * @return CSADto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CSADto fetchCSAIncdnts(CSADto csaDto) {
		int childAge = 0;
		List<CsaEpisodesIncdntDto> finalIncidentList = new ArrayList<>();
		/*
		 * Fetching all the recorded Episode's and Incident's for Child Sexual
		 * Aggression Page for the selected Person.
		 */
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CsaEpisodesIncdnt.class);
		criteria.add(Restrictions.eq("idPerson", csaDto.getIdPerson()));
		criteria.addOrder(Order.desc("dtIncdnt"));
		List<CsaEpisodesIncdnt> incidentList = criteria.list();
		/* If response is not empty, Copying the data from Entity to DTO. */
		if (!ObjectUtils.isEmpty(incidentList)) {
			incidentList.forEach(incident -> {
				CsaEpisodesIncdntDto csaincidentDto = new CsaEpisodesIncdntDto();
				BeanUtils.copyProperties(incident, csaincidentDto);
				csaincidentDto.setCdVictimCategory(incident.getCdVictimCtgy());
				csaincidentDto.setIdPlacement(incident.getIdPlcmtEvent());
				csaincidentDto.setIdPlacementTa(incident.getIdPlacementTa());
				if (incident.getCsaEpisode() != null) {
					csaincidentDto.setIdCsaEpisodes(incident.getCsaEpisode().getIdCsaEpisodes());
				}
				if (incident.getIdPersonVictim() != null) {
					csaincidentDto.setIdVictim(incident.getIdPersonVictim().getIdPerson());
				}
				finalIncidentList.add(csaincidentDto);
			});
			/*
			 * Forming the response structure and adding final list to csaDto.
			 */
			csaDto.setCsaIncidents(finalIncidentList);
			csaDto.setIsCSAEmptyList(Boolean.FALSE);
		} else {
			/* if List is empty, setting IsCSAEmptyList Flag to true. */
			csaDto.setIsCSAEmptyList(Boolean.TRUE);
		}
		/* Validate that the youth has an open SUB stage. */
		if (!ObjectUtils.isEmpty(csaDto.getIdPerson())) {
			SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getOpenSUBStageYouthSql)
					.setParameter(ServiceConstants.IDPERSON, csaDto.getIdPerson()));
			if (null != query.uniqueResult()) {
				childAge = DateUtils.getAge((Date) query.uniqueResult());
				if (childAge < 18) {
					csaDto.setIsOpenSUBStageYouth(Boolean.TRUE);
				}
			} else {
				csaDto.setIsOpenSUBStageYouth(Boolean.FALSE);
			}
		}
		return csaDto;
	}

	/**
	 *
	 * Method Name: fetchCSAList Method Description: fetch CSA List
	 *
	 * @param idPerson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CSAEpisodeDto> fetchCSAList(Long idPerson) {
		return fetchCSAList(idPerson,false);
	}

	/**
	 *
	 * Method Name: fetchCSAIncidentList Method Description: fetch CSA List
	 *
	 * @param idPerson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CsaEpisodesIncdntDto> fetchCSAIncidentList(Long idPerson) {
		return fetchCSAIncidentList(idPerson,false);
	}

	/**
	 * Method Name: saveOrupdateCSA Method Description: Method to Save or Update
	 * the Episodes and Incidents recorded for Child Sexual Aggression page.
	 * 
	 * @param idPerson
	 * @return CSADto
	 */
	@Override
	public CSADto saveOrupdateCSA(CSADto csaDto) {
		/*
		 * Performing the request to save or Update the data in CSA_Episode_Incdnt table.
		 */
		if (csaDto.getCsaIncidents() != null && csaDto.getCsaIncidents().size() > 0) {
			csaDto.getCsaIncidents().forEach(newIncident -> {
				/*
				 * If Approximate Date is not selected, by default setting
				 * the Value to 'N'.
				 */
				if (ObjectUtils.isEmpty(newIncident.getIndAppxDt())) {
					newIncident.setIndAppxDt(ServiceConstants.N);
				}
				if (ObjectUtils.isEmpty(newIncident.getDtCreated())) {
					newIncident.setDtCreated(new Date());
				}
				newIncident.setDtLastUpdate(new Date());
				// Defect# 12885 - setting the logged in user id
				if (ObjectUtils.isEmpty(newIncident.getIdCreatedPerson())) {
					newIncident.setIdCreatedPerson(csaDto.getUserId());
				}
				// Defect# 12885 - setting the logged in user id
				newIncident.setIdLastUpdatePerson(csaDto.getUserId());
				CsaEpisodesIncdnt incident = new CsaEpisodesIncdnt();
				BeanUtils.copyProperties(newIncident, incident);
				incident.setCdVictimCtgy(newIncident.getCdVictimCategory());
				incident.setIdPlcmtEvent(newIncident.getIdPlacement());
				if (newIncident.getIdVictim() != null) {
					Person victim = (Person) sessionFactory.getCurrentSession().load(Person.class, newIncident.getIdVictim());
					incident.setIdPersonVictim(victim);
				}
				incident.setIdPlacementTa(newIncident.getIdPlacementTa());
				if (newIncident.getIdCsaEpisodes() != null) {
					CsaEpisode historicalEpisode = (CsaEpisode) sessionFactory.getCurrentSession().load(CsaEpisode.class, newIncident.getIdCsaEpisodes());
					incident.setCsaEpisode(historicalEpisode);
				}

				/* Hibernate call to Save or Update the CSA records. */
				sessionFactory.getCurrentSession().saveOrUpdate(incident);
				updateNoCharacteristics(csaDto.getIdPerson());
			});
			updatePersonCharacteristics(csaDto);

		}
		return csaDto;
	}

	private void updatePersonCharacteristics(CSADto csaDto) {
		/*
		 * Updating Sexual Behavioral Program related to Person
		 * Characteristics Page.
		 */
		if (csaDto.getCsaIncidents() != null && csaDto.getCsaIncidents().size() > 0) {
			List<CsaEpisodesIncdntDto> incidents = csaDto.getCsaIncidents();
			Date earliestIncdntDt = incidents
					.stream()
					.min(Comparator.comparing(CsaEpisodesIncdntDto::getDtIncdnt)).get().getDtIncdnt();
			// Check if the person has SBP or not
			List<PersCharsDto> personCharList = personCharDao.getPersonCharData(csaDto.getIdPerson(),
					ServiceConstants.CPL);
			boolean cpl70CharExists = false;
			if (!CollectionUtils.isEmpty(personCharList)){
				cpl70CharExists= personCharList.stream().anyMatch(personCharDto -> ServiceConstants.CPL_70.equals(personCharDto.getCdCharacteristic()));
			}
			// Artf262860 - removed code to update existing CPL_70 since only the start date is updated. Add a new one if none exist.
			if(!cpl70CharExists){
				CharacteristicsDto characteristicsDto = new CharacteristicsDto();
				characteristicsDto.setCdCharacteristic(ServiceConstants.CPL_70);
				characteristicsDto.setCdCharCategory(ServiceConstants.CPL);
				characteristicsDto.setCdStatus(ServiceConstants.S);
				characteristicsDto.setDtCharStart(earliestIncdntDt);
				characteristicsDto.setIdPerson(csaDto.getIdPerson());
				characteristicsDto.setDtLastUpdate(new Date());
				characteristicsDto.setIndAfcars(ServiceConstants.N);
				personCharDao.savePersonChar(characteristicsDto);
			}
		}
	}

	/**
	 *
	 * Method Name: updateCsaEpisode Method Description:Update CsaEpisode for
	 * Person merge
	 *
	 * @param csaEpisodeDto
	 */
	@Override
	public void updateCsaEpisode(CSAEpisodeDto csaEpisodeDto) {
		CsaEpisode csaEpisode = (CsaEpisode) sessionFactory.getCurrentSession().createCriteria(CsaEpisode.class)
				.add(Restrictions.eq("idCsaEpisodes", csaEpisodeDto.getIdCsaEpisodes())).uniqueResult();
		csaEpisode.setIdPerson(csaEpisodeDto.getIdPerson());
		csaEpisode.setIdLastUpdatePerson(csaEpisodeDto.getIdLastUpdatePerson());
		csaEpisode.setDtLastUpdate(new Date());
		if (CollectionUtils.isNotEmpty(csaEpisodeDto.getCsaEpisodesIncdntList())) {
			List<CsaEpisodesIncdntDto> filteredIncidentList = csaEpisodeDto.getCsaEpisodesIncdntList();
			List<CsaEpisodesIncdnt> csaEpisodesIncdntList = new ArrayList<>();
			filteredIncidentList.forEach(newIncident -> {
				CsaEpisodesIncdnt incident = (CsaEpisodesIncdnt) sessionFactory.getCurrentSession()
						.createCriteria(CsaEpisodesIncdnt.class)
						.add(Restrictions.eq("idCsaEpisodesIncdnt", newIncident.getIdCsaEpisodesIncdnt()))
						.uniqueResult();
				incident.setIdLastUpdatePerson(newIncident.getIdLastUpdatePerson());
				incident.setCsaEpisode(csaEpisode);
				incident.setDtLastUpdate(new Date());
				csaEpisodesIncdntList.add(incident);
			});
			csaEpisode.setCsaEpisodesIncdnts(csaEpisodesIncdntList);
		}
		sessionFactory.getCurrentSession().saveOrUpdate(csaEpisode);
	}

	/**
	 * 
	 * Method Name: Override Method Description:
	 * Update CsaEpisode incident for Person merge
	 * 
	 * @param csaEpisodeDto
	 */
	@Override
	public void updateCsaIncident(CsaEpisodesIncdntDto csaEpisodeDto) {
		CsaEpisodesIncdnt incident = (CsaEpisodesIncdnt) sessionFactory.getCurrentSession()
				.createCriteria(CsaEpisodesIncdnt.class)
				.add(Restrictions.eq("idCsaEpisodesIncdnt", csaEpisodeDto.getIdCsaEpisodesIncdnt()))
				.uniqueResult();
		incident.setIdPerson(csaEpisodeDto.getIdPerson());
		incident.setIdLastUpdatePerson(csaEpisodeDto.getIdLastUpdatePerson());
		incident.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(incident);
	}

	@Override
	public void updateNoCharacteristics(Long idPerson) {
		Person person = (Person) sessionFactory.getCurrentSession().load(Person.class, idPerson);
		if (!ObjectUtils.isEmpty(person)) {
			person.setCdPersonChar(PERSON_NO_CHAR_NUMBER_ONE);
			person.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().update(person);
		}
	}

	@Override
	public List<CSAEpisodeDto> fetchCSAList(Long idPerson, Boolean doSortByEpisodeStart) {
		List<CSAEpisodeDto> finalEpisodeList = new ArrayList<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CsaEpisode.class);
		criteria.add(Restrictions.eq("idPerson", idPerson));

		if (doSortByEpisodeStart != null && Boolean.TRUE.equals(doSortByEpisodeStart)) {
			criteria.addOrder(Order.desc("dtEpisodeStart"));
		}

		List<CsaEpisode> episodeList = criteria.list();
		if (CollectionUtils.isNotEmpty(episodeList)) {
			episodeList.forEach(episode -> {
				List<CsaEpisodesIncdntDto> finalIncidentList = new ArrayList<>();
				CSAEpisodeDto csaEpisodeDto = new CSAEpisodeDto();
				BeanUtils.copyProperties(episode, csaEpisodeDto);
				if (!ObjectUtils.isEmpty(episode.getCsaEpisodesIncdnts())) {
					episode.getCsaEpisodesIncdnts().forEach(incident -> {
						CsaEpisodesIncdntDto csaincidentDto = new CsaEpisodesIncdntDto();
						BeanUtils.copyProperties(incident, csaincidentDto);
						csaincidentDto.setCdVictimCategory(incident.getCdVictimCtgy());
						csaincidentDto.setIdPlacement(incident.getIdPlcmtEvent());
						csaincidentDto.setIdPlacementTa(incident.getIdPlacementTa());
						csaincidentDto.setIdVictim(
								ObjectUtils.isEmpty(incident.getIdPersonVictim())? 0L : incident.getIdPersonVictim().getIdPerson());
						csaincidentDto.setIdCsaEpisodes(incident.getCsaEpisode().getIdCsaEpisodes());
						finalIncidentList.add(csaincidentDto);
					});
					csaEpisodeDto.setCsaEpisodesIncdntList(finalIncidentList);
				}
				finalEpisodeList.add(csaEpisodeDto);
			});
		}
		return finalEpisodeList;

	}

	@Override
	// TODO UNTESTED
	public List<CsaEpisodesIncdntDto> fetchCSAIncidentList(Long idPerson, Boolean doSortByIncident) {
		List<CsaEpisodesIncdntDto> finalIncidentList = new ArrayList<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CsaEpisodesIncdnt.class);
		criteria.add(Restrictions.eq("idPerson", idPerson));

		if (doSortByIncident != null && Boolean.TRUE.equals(doSortByIncident)) {
			criteria.addOrder(Order.desc("dtIncdnt"));
		}

		List<CsaEpisodesIncdnt> incidentList = criteria.list();
		if (CollectionUtils.isNotEmpty(incidentList)) {
			incidentList.forEach(incident -> {
				CsaEpisodesIncdntDto csaincidentDto = new CsaEpisodesIncdntDto();
				BeanUtils.copyProperties(incident, csaincidentDto);
				csaincidentDto.setCdVictimCategory(incident.getCdVictimCtgy());
				csaincidentDto.setIdPlacement(incident.getIdPlcmtEvent());
				csaincidentDto.setIdPlacementTa(incident.getIdPlacementTa());
				csaincidentDto.setIdVictim(
						ObjectUtils.isEmpty(incident.getIdPersonVictim())? 0L : incident.getIdPersonVictim().getIdPerson());
				finalIncidentList.add(csaincidentDto);
			});
		}
		return finalIncidentList;
	}

	@Override
	public List<CsaEpisodesIncdntDto> getCSAVictimList(Long idVictim) {
		List<CsaEpisodesIncdnt> incidentList = sessionFactory.getCurrentSession().createCriteria(CsaEpisodesIncdnt.class)
				.add(Restrictions.eq("idPersonVictim.idPerson", idVictim))
				.list();

		List<CsaEpisodesIncdntDto> finalIncidentList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(incidentList)) {
			incidentList.forEach(incident -> {
				CsaEpisodesIncdntDto csaincidentDto = new CsaEpisodesIncdntDto();
				BeanUtils.copyProperties(incident, csaincidentDto);
				csaincidentDto.setCdVictimCategory(incident.getCdVictimCtgy());
				csaincidentDto.setIdPlacement(incident.getIdPlcmtEvent());
				csaincidentDto.setIdPlacementTa(incident.getIdPlacementTa());
				csaincidentDto.setIdVictim(
						ObjectUtils.isEmpty(incident.getIdPersonVictim())? 0L : incident.getIdPersonVictim().getIdPerson());
				finalIncidentList.add(csaincidentDto);
			});
		}
		return finalIncidentList;
	}


	@Override
	public void deleteCsaEpisodes(List<Long> idCsaEpisodes, Long idUser) {
		for (Long idCsaEpisode : idCsaEpisodes) {
			if (!ObjectUtils.isEmpty(idCsaEpisode) && !idCsaEpisode.equals(ServiceConstants.ZERO)) {
				CsaEpisode incident = (CsaEpisode) sessionFactory.getCurrentSession()
						.createCriteria(CsaEpisode.class).add(Restrictions.eq("idCsaEpisodes", idCsaEpisode))
						.uniqueResult();
				if (!ObjectUtils.isEmpty(incident)) {
					incident.setIdLastUpdatePerson(idUser);
					incident.setDtLastUpdate(new Date());
					sessionFactory.getCurrentSession().saveOrUpdate(incident);
					sessionFactory.getCurrentSession().delete(incident);
				}
			}
		}
	}

	@Override
	public void deleteCsaEpisodeIncidents(List<Long> episodeIncidentIds, Long idUser) {
		for (Long idIncident : episodeIncidentIds) {
			if (!ObjectUtils.isEmpty(idIncident) && !idIncident.equals(ServiceConstants.ZERO)) {
				CsaEpisodesIncdnt incident = (CsaEpisodesIncdnt) sessionFactory.getCurrentSession()
						.createCriteria(CsaEpisodesIncdnt.class).add(Restrictions.eq("idCsaEpisodesIncdnt", idIncident))
						.uniqueResult();
				if (!ObjectUtils.isEmpty(incident)) {
					incident.setIdLastUpdatePerson(idUser);
					incident.setDtLastUpdate(new Date());
					sessionFactory.getCurrentSession().saveOrUpdate(incident);
					sessionFactory.getCurrentSession().delete(incident);
				}
			}
		}
	}

	public List<CsaEpisodesIncdnt> fetchCSAEpiIncdntByTA(Long idPlacementTa) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CsaEpisodesIncdnt.class);
		criteria.add(Restrictions.eq("idPlacementTa", idPlacementTa)).addOrder(Order.desc("dtIncdnt"));
		return (List<CsaEpisodesIncdnt>) criteria.list();
	}

	public void updateCSAForTA(Long idPlacementTa, Long user){
		List<CsaEpisodesIncdnt> csaList = fetchCSAEpiIncdntByTA(idPlacementTa);
		if(!ObjectUtils.isEmpty(csaList)){
			csaList.stream().forEach((s)->{
				s.setIdPlacementTa(null);
				s.setIdLastUpdatePerson(user);
				sessionFactory.getCurrentSession().saveOrUpdate(s);
			});
		}
	}

}
