package us.tx.state.dfps.service.fcl.daoimpl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ChildSxVctmztn;
import us.tx.state.dfps.common.domain.ChildSxVctmztnIncdnt;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.csa.dao.CSADao;
import us.tx.state.dfps.service.fcl.dao.SexualVictimizationHistoryDao;
import us.tx.state.dfps.service.person.dao.PersonCharDao;
import us.tx.state.dfps.service.person.dao.TraffickingDao;
import us.tx.state.dfps.service.person.dto.PersCharsDto;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;

import static us.tx.state.dfps.service.common.ServiceConstants.NULL_JAVA_DATE_DATE;

/**
 * 
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:<DaoImpl class for Sexual Victimization History>
 *Oct 11, 2019- 12:03:01 PM
 *© 2017 Texas Department of Family and Protective Services
 */
@Repository
public class SexualVictimizationHistoryDaoImpl implements SexualVictimizationHistoryDao {
	
	@Autowired
	private SessionFactory sessionFactory;

    @Autowired
    PersonCharDao personCharDao;

    @Autowired
    CSADao csaDao;

	@Autowired
	TraffickingDao traffickingDao;
	/**
	 * 
	 *Method Name:	fetchSexualVitimHistory
	 *Method Description: get Sexual Victimization History list by person id
	 *@param idPerson
	 *@return List<ChildSxVctmztnIncdnt>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ChildSxVctmztnIncdnt> fetchSexualVictimHistory(Long idPerson) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildSxVctmztnIncdnt.class);
		criteria.add(Restrictions.eq("person.idPerson", idPerson)).addOrder(Order.desc("dtIncident"));
		return (List<ChildSxVctmztnIncdnt>) criteria.list();
	}

	private List<TraffickingDto> fetchSexualTraffickingVictim(Long idPerson) {
		TraffickingDto simpleParam = new TraffickingDto();
		simpleParam.setIdPerson(BigDecimal.valueOf(idPerson));
		return traffickingDao.getTraffickingList(simpleParam);
	}

	@Override
	public List<ChildSxVctmztnIncdnt> getIdPersonAggressorIncidents(Long idPersonAggressor) {
		return (List<ChildSxVctmztnIncdnt>)  sessionFactory.getCurrentSession().createCriteria(ChildSxVctmztnIncdnt.class)
			.add(Restrictions.eq("idPersonAggressor.idPerson", idPersonAggressor)).list();
	}

	/**
	 * 
	 *Method Name:	saveIncident
	 *Method Description: save incidents
	 *@param incidentDtos
	 */	
	@Override
	public void saveIncident(List<SexualVictimIncidentDto> incidentDtos) {
		for (SexualVictimIncidentDto incidentDto : incidentDtos) {
			//Need consider two types : newly added or update
			ChildSxVctmztnIncdnt incident;
			Date date = new Date();
			Boolean saveIncident = false;
			if (ObjectUtils.isEmpty(incidentDto.getIdIncident()) || incidentDto.getIdIncident().equals(ServiceConstants.ZERO)) {
				incident = new ChildSxVctmztnIncdnt();
				incident.setDtCreated(date);
				incident.setIdCreatedPerson(incidentDto.getIdCreatedBy());
				Person person = (Person) sessionFactory.getCurrentSession().load(Person.class, incidentDto.getIdPerson());
				incident.setPerson(person);
				saveIncident = true;
			} else {
				incident = (ChildSxVctmztnIncdnt) sessionFactory.getCurrentSession().createCriteria(ChildSxVctmztnIncdnt.class).add(Restrictions.eq("idChildSxVctmztnIncdnt", incidentDto.getIdIncident())).uniqueResult();
				incidentDto.setIndApproxDate(ServiceConstants.Y.equals(incidentDto.getIndApproxDate()) ? ServiceConstants.Y : ServiceConstants.N);
				saveIncident = hasModifiedIncident(incident, incidentDto);
			}
			if (saveIncident) {
				incident.setDtIncident(incidentDto.getDtIncident());
				incident.setDtLastUpdate(date);
				incident.setIdLastUpdatePerson(incidentDto.getIdModifiedBy());
				incident.setIndApproxDate(ServiceConstants.Y.equals(incidentDto.getIndApproxDate()) ? ServiceConstants.Y : ServiceConstants.N);
				incident.setTxtResponsibleComments(formatString(incidentDto.getResponsibleComments()));
				incident.setTxtVictimizationComments(formatString(incidentDto.getVictimComments()));
				incident.setIndChildInCare(incidentDto.getIndChildInCare());
				incident.setIdPlcmtEvent(incidentDto.getIdPlacement());
				incident.setCdAggressorCtgy(incidentDto.getCdAggressorCategory());
				if(null!=incidentDto.getIdAggressor() && incidentDto.getIdAggressor()!=0) {
					Person personAggressor = (Person) sessionFactory.getCurrentSession().load(Person.class, incidentDto.getIdAggressor());
					incident.setIdPersonAggressor(personAggressor);
				} else {
					incident.setIdPersonAggressor(null);
				}
				incident.setIdPlacementTa(incidentDto.getIdPlacementTa());

				sessionFactory.getCurrentSession().saveOrUpdate(incident);
			}
		}
	}
	
	private boolean hasModifiedIncident(ChildSxVctmztnIncdnt incident, SexualVictimIncidentDto incidentDto) {
		return !incident.getDtIncident().equals(incidentDto.getDtIncident())
				|| !incident.getTxtResponsibleComments().equals(incidentDto.getResponsibleComments())
				|| !incident.getTxtVictimizationComments().equals(incidentDto.getVictimComments())
				|| !incident.getIndApproxDate().equals(incidentDto.getIndApproxDate())
				|| !ObjectUtils.nullSafeEquals(incident.getIndChildInCare(), incidentDto.getIndChildInCare())
				|| !ObjectUtils.nullSafeEquals(incident.getIdPlcmtEvent(), incidentDto.getIdPlacement())
				|| !ObjectUtils.nullSafeEquals(incident.getCdAggressorCtgy(), incidentDto.getCdAggressorCategory())
				|| !ObjectUtils.nullSafeEquals(incident.getIdPersonAggressor(), incidentDto.getIdAggressor())
				|| !ObjectUtils.nullSafeEquals(incident.getIdPlacementTa(), incidentDto.getIdPlacementTa());
	}
	/**
	 * 
	 *Method Name:	deleteIncident
	 *Method Description: delete incident
	 */
	@Override
	public void deleteIncident(List<Long> idIncidents, Long idUser) {
		ChildSxVctmztn childSxVctmztn = null;
		for (Long idIncident : idIncidents) {
			if (!ObjectUtils.isEmpty(idIncident) && !idIncident.equals(ServiceConstants.ZERO)) {
				ChildSxVctmztnIncdnt incident = (ChildSxVctmztnIncdnt) sessionFactory.getCurrentSession().createCriteria(ChildSxVctmztnIncdnt.class).add(Restrictions.eq("idChildSxVctmztnIncdnt", idIncident)).uniqueResult();
				if (!ObjectUtils.isEmpty(incident)) {
					if (ObjectUtils.isEmpty(childSxVctmztn)) {
						Long idPerson = incident.getPerson().getIdPerson();
						childSxVctmztn = getChildSxVctmztnByPersonId(idPerson);
					}
					incident.setIdLastUpdatePerson(idUser);
					incident.setDtLastUpdate(new Date());
					sessionFactory.getCurrentSession().saveOrUpdate(incident);
					sessionFactory.getCurrentSession().delete(incident);
				}
			}
		}

		// artf279006 After deleting, if there is no incidents *or trafficking incidents*, mark indicater as N
		if (!ObjectUtils.isEmpty(childSxVctmztn)) {
			List<ChildSxVctmztnIncdnt> incidents = fetchSexualVictimHistory(childSxVctmztn.getPerson().getIdPerson());
			List<TraffickingDto> traffickingIncidents = fetchSexualTraffickingVictim(childSxVctmztn.getPerson().getIdPerson());
			if (CollectionUtils.isEmpty(incidents) && CollectionUtils.isEmpty(traffickingIncidents)) {
				childSxVctmztn.setIndChildSxVctmztnHist(ServiceConstants.N);
				childSxVctmztn.setDtLastUpdate(new Date());
				childSxVctmztn.setIdLastUpdatePerson(idUser);
				sessionFactory.getCurrentSession().saveOrUpdate(childSxVctmztn);
			}
		}
	}
	
	@Override
	public ChildSxVctmztn getChildSxVctmztnById(Long idChildSxVctmztn) {
		return (ChildSxVctmztn) sessionFactory.getCurrentSession().createCriteria(ChildSxVctmztn.class).add(Restrictions.eq("idChildSxVctmztn", idChildSxVctmztn)).uniqueResult();
	}
	
	@Override
	public ChildSxVctmztn getChildSxVctmztnByPersonId(Long idPerson) {
		return (ChildSxVctmztn) sessionFactory.getCurrentSession()
				.createCriteria(ChildSxVctmztn.class).add(Restrictions.eq("person.idPerson", idPerson)).uniqueResult();
	}
	@Override
	public void saveChildSxVctmztn(SexualVictimHistoryDto sexualVictimHistoryDto) {
		ChildSxVctmztn childSxVctmztn;
		List<SexualVictimIncidentDto> incidentDtos = sexualVictimHistoryDto.getSvhIncidents();
		if (sexualVictimHistoryDto.getIdChildSxVctmtzn() == null || ServiceConstants.ZERO.equals(sexualVictimHistoryDto.getIdChildSxVctmtzn())) {
			//Add new Sx Vctmztn
			childSxVctmztn = new ChildSxVctmztn();
			childSxVctmztn.setIdCreatedPerson(sexualVictimHistoryDto.getIdCreatedPerson());
			childSxVctmztn.setDtCreated(new Date());
			// we're creating a new entry. If they provided an answer to unconfirmed victimization history, record who
            // and when. It's currently a required field so this SHOULD be always.
            if (sexualVictimHistoryDto.getIndUnconfirmedVictimHistory() != null) {
                childSxVctmztn.setIdUpdPrsUnconfVicHist(sexualVictimHistoryDto.getIdLastUpdatedPerson());
                childSxVctmztn.setDtUpdUnconfVicHist(new Date());

				// for create new record, we know the user answered the required question, so mark it as user answered.
				if (sexualVictimHistoryDto.isIndUserRequest()) {
                    if (sexualVictimHistoryDto.getIndUnconfirmedVictimHistory().equals(ServiceConstants.NO)) {
                        childSxVctmztn.setIdUpdPrsUnconfVicHistUser(null);
                    } else {
                        childSxVctmztn.setIdUpdPrsUnconfVicHistUser(sexualVictimHistoryDto.getIdLastUpdatedPerson());
                    }
				}
            }
		} else {
			// Update exist Sx Vctmztn
			childSxVctmztn = getChildSxVctmztnById(sexualVictimHistoryDto.getIdChildSxVctmtzn());
            // we're updating an existing entry. If they changed the answer to unconfirmed victimization history,
            // update who and when.
            if (sexualVictimHistoryDto.getIndUnconfirmedVictimHistory() != null &&
                !sexualVictimHistoryDto.getIndUnconfirmedVictimHistory().equalsIgnoreCase(childSxVctmztn.getIndUnconfirmedVicHist())) {
                childSxVctmztn.setIdUpdPrsUnconfVicHist(sexualVictimHistoryDto.getIdLastUpdatedPerson());
                childSxVctmztn.setDtUpdUnconfVicHist(new Date());
				// isIndUserRequest indicates a user update, but we also need to check that there is a change as
				// a value could be set by system, then saved -unchanged- by user. That still counts as set by system even
				// though the user did save it.
				if (sexualVictimHistoryDto.isIndUserRequest()) {
					if (!sexualVictimHistoryDto.getIndUnconfirmedVictimHistory().equals(childSxVctmztn.getIndUnconfirmedVicHist())) {
						if (sexualVictimHistoryDto.getIndUnconfirmedVictimHistory().equals(ServiceConstants.NO)) {
							childSxVctmztn.setIdUpdPrsUnconfVicHistUser(null);
						} else {
							childSxVctmztn.setIdUpdPrsUnconfVicHistUser(sexualVictimHistoryDto.getIdLastUpdatedPerson());
						}
					}
				}
            }
		}
		Person person = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class).add(Restrictions.eq("idPerson", sexualVictimHistoryDto.getIdPerson())).uniqueResult();
		childSxVctmztn.setPerson(person);
		childSxVctmztn.setDtLastUpdate(new Date());
		childSxVctmztn.setIdLastUpdatePerson(sexualVictimHistoryDto.getIdLastUpdatedPerson());
		childSxVctmztn.setIndChildSxVctmztnHist(sexualVictimHistoryDto.getIndChildHasSxVictimHistory());
		childSxVctmztn.setTxtPreviousUnconfirmFinds(formatString(sexualVictimHistoryDto.getPreviousUnconfirmFinds()));
		childSxVctmztn.setTxtSupervisionContactDesc(formatString(sexualVictimHistoryDto.getSupervisionContactDesc()));
        childSxVctmztn.setIndUnconfirmedVicHist(sexualVictimHistoryDto.getIndUnconfirmedVictimHistory());
        childSxVctmztn.setIndSxBehaviorProb(sexualVictimHistoryDto.getIndSexualBehaviorProblem());
        childSxVctmztn.setTxtSxBehaviorProb(sexualVictimHistoryDto.getTxtBehaviorProblems());
		childSxVctmztn.setTxtSxVicDesc(sexualVictimHistoryDto.getTxtUnconfirmVic());

		sessionFactory.getCurrentSession().saveOrUpdate(childSxVctmztn);
		if (CollectionUtils.isNotEmpty(incidentDtos))
			saveIncident(incidentDtos);

        // Defect 23881 - uncheck no characteristic any time we see it checked and we know it should be unchecked because SBP exists.
        if ("Y".equals(sexualVictimHistoryDto.getIndSexualBehaviorProblem()) ||
                (sexualVictimHistoryDto.getIndSexualBehaviorProblem() == null && "Y".equals(sexualVictimHistoryDto.getIndSexualBehaviorProblemHidden()))) {
            csaDao.updateNoCharacteristics(sexualVictimHistoryDto.getIdPerson());
        }
        // PPM 67321 PCR 202 Track Sexual Behavior Problems.
        updatePersonCharacteristics(sexualVictimHistoryDto);
	}
	
	private String formatString(String s) {
		if (!ObjectUtils.isEmpty(s) && s.length() > 4000)
			s = s.substring(0, 4000);
		return s;
	}
	
	
	/**
	 * Method Name:	updateSexualHistoryQuestionToYes
	 * Method Description: Typically this is used when a Trafficking incident has been created. If there is an existing
	 * record for the person in CHILD_SX_VCTMZTN and the indicator for a confirmed history is already set to Y, this
	 * method does nothing. If the record exists and is set to N, this method updated to Y, and the audit fields. If
	 * the record does not exist, it is created and set to Y.
	 * artf130771 - set SVH indicator
	 *
	 * @param idPerson id of the person that we want to indicate has a confirmed history of sexual victimization
	 * @param createdBy id of the worker that is making the change
	 */
	@Override
	public void updateSexualHistoryQuestionToYes(BigDecimal idPerson, String createdBy) {
		// note that SexualVictimHistoryDto has nmPerson, hasSensitiveCase and showPage that are not used during a save,
		// and since we are just building a SexualVictimHistoryDto to do a save, we don't bother populating them, unlike
		// other examples present in this service.
		SexualVictimHistoryDto sexualVictimHistoryDto = new SexualVictimHistoryDto();
		ChildSxVctmztn childSxVctmztn = getChildSxVctmztnByPersonId(idPerson.longValue());

		if (ObjectUtils.isEmpty(childSxVctmztn)) {
			sexualVictimHistoryDto.setIdPerson(idPerson.longValue());

			sexualVictimHistoryDto.setIdChildSxVctmtzn(null);
			sexualVictimHistoryDto.setDtCreated(new Date());
			sexualVictimHistoryDto.setIdCreatedPerson(Long.valueOf(createdBy));
			sexualVictimHistoryDto.setDtLastUpdated(new Date());
			sexualVictimHistoryDto.setIdLastUpdatedPerson(Long.valueOf(createdBy));
			sexualVictimHistoryDto.setIndChildHasSxVictimHistory(ServiceConstants.YES);
			// do not set setIndUnconfirmedTrafficking since this is a system update
			saveChildSxVctmztn(sexualVictimHistoryDto);
		} else if (ServiceConstants.NO.equals(childSxVctmztn.getIndChildSxVctmztnHist())) {
			mapEntityToDto(childSxVctmztn, sexualVictimHistoryDto);
			sexualVictimHistoryDto.setIdPerson(idPerson.longValue());

			sexualVictimHistoryDto.setIndChildHasSxVictimHistory(ServiceConstants.YES);
			sexualVictimHistoryDto.setDtLastUpdated(new Date());
			sexualVictimHistoryDto.setIdLastUpdatedPerson(Long.valueOf(createdBy));
			// do not set setIndUnconfirmedTrafficking since this is a system update
			saveChildSxVctmztn(sexualVictimHistoryDto);
		}
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
        sexualVictimHistoryDto.setIndSexualBehaviorProblem(childSxVctmztn.getIndSxBehaviorProb());
        sexualVictimHistoryDto.setTxtBehaviorProblems(childSxVctmztn.getTxtSxBehaviorProb());
        sexualVictimHistoryDto.setIndUnconfirmedVictimHistory(childSxVctmztn.getIndUnconfirmedVicHist());
        sexualVictimHistoryDto.setIdUpdPrsUnconfVicHist(childSxVctmztn.getIdUpdPrsUnconfVicHist());
        sexualVictimHistoryDto.setDtUpdUnconfVicHist(childSxVctmztn.getDtUpdUnconfVicHist());
		sexualVictimHistoryDto.setTxtUnconfirmVic(childSxVctmztn.getTxtSxVicDesc());
	}

	public List<ChildSxVctmztnIncdnt> fetchSexualVictimHistoryByTA(Long idPlacementTa) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildSxVctmztnIncdnt.class);
		criteria.add(Restrictions.eq("idPlacementTa", idPlacementTa)).addOrder(Order.desc("dtIncident"));
		return (List<ChildSxVctmztnIncdnt>) criteria.list();
	}

	public void updateSVHForTA(Long idPlacementTa, Long user){
		List<ChildSxVctmztnIncdnt> svhList = fetchSexualVictimHistoryByTA(idPlacementTa);
		if(!ObjectUtils.isEmpty(svhList)){
			svhList.stream().forEach((s)->{
				s.setIdPlacementTa(null);
				s.setIdLastUpdatePerson(user);
				sessionFactory.getCurrentSession().saveOrUpdate(s);
			});
		}
	}

	// Case Worker has indicated that there is a Sexual Behavior Problem for a child on SIH screen
    // 9.1.6.1. Person Detal>Child Placement>check box: System will mark the Person Characteristics for Child-Placement category as "Sexual Behavior Problem"
    // 9.1.6.2. Person Detail>Child Placement>Begin Date: System will also populate the "Begin Date" for Sexual Behavior Problem as the date when the Sexual Behavior Problem was designated as a problem on SIH screen
    // User with higher role will undo the mistake ... answering "No" to Sexual Behavior Problem question
    // 9.1.7.1. System will unmark the Person Characteristics for Child-Placement category as "Sexual Behavior Problem"
    // 9.1.7.2. System will remove the "Begin Date"
    // 9.1.7.3: System will not populate "End Date"
    // Note: Program has confirmed that they do NOT use "End Date" at all. It has been creating a lot of confusion during auditing. They have requested not to even touch "End Date"
    private void updatePersonCharacteristics(SexualVictimHistoryDto sxVictimHistoryDto) {
	    Date nowDate = new Date();
        if (sxVictimHistoryDto.getIndSexualBehaviorProblem() != null) {
            // Check if the person has SBP or not
            List<PersCharsDto> personCharList = personCharDao.getPersonCharData(sxVictimHistoryDto.getIdPerson(),
                ServiceConstants.CPL);


            if (!CollectionUtils.isEmpty(personCharList)) {
                Optional<PersCharsDto> persCharsDtoHolder = personCharList.stream().filter(
                    personCharDto -> NULL_JAVA_DATE_DATE.equals(personCharDto.getDtCharEnd()) &&
                        ServiceConstants.CPL_70.equals(personCharDto.getCdCharacteristic()))
                    .findFirst(); // find Existing Y
                // if there is currently an open SBP characteristic, and N is selected. End the existing characteristic
                if (persCharsDtoHolder.isPresent() && ServiceConstants.N.equalsIgnoreCase(sxVictimHistoryDto.getIndSexualBehaviorProblem())) {
                        PersCharsDto persCharsDto = persCharsDtoHolder.get();
                        CharacteristicsDto characteristicsDto = new CharacteristicsDto();
                        characteristicsDto.setIdCharacteristics(persCharsDto.getIdCharacteristics());
                        characteristicsDto.setCdCharacteristic(persCharsDto.getCdCharacteristic());
                        characteristicsDto.setCdCharCategory(persCharsDto.getCdCharCategory());
                        characteristicsDto.setCdStatus(persCharsDto.getCdStatus());
                        characteristicsDto.setIdPerson(persCharsDto.getIdPerson());
                        characteristicsDto.setDtLastUpdate(nowDate);
                        characteristicsDto.setIndAfcars(persCharsDto.getIndAfcars());
                        characteristicsDto.setDtCharStart(persCharsDto.getDtCharStart());
                        characteristicsDto.setDtCharEnd(nowDate);
                        personCharDao.updatePersonChar(characteristicsDto);
                }
                // if there is not currently an open SBP characteristic, and Y is selected. Create a new characteristic.
                // (this works in both cases where and ended characteristic exists, and an ended characteristic does not exist.)
                if (!persCharsDtoHolder.isPresent() && ServiceConstants.Y.equalsIgnoreCase(sxVictimHistoryDto.getIndSexualBehaviorProblem())) {
                    doSavePersonChar(sxVictimHistoryDto.getIdPerson(), nowDate);
                }
                // If there is currently an open SBP characteristic, and Y is selected, do nothing
                // If there is currently not an open SBP characteristic, and N is selected, do nothing
            } else { // if there were no Existing Placement characteristics at all and the question indicates a SBP, this will be the first.
                if (ServiceConstants.Y.equalsIgnoreCase(sxVictimHistoryDto.getIndSexualBehaviorProblem())) {
                    doSavePersonChar(sxVictimHistoryDto.getIdPerson(), nowDate);
                }
            }
        }
    }

    private void doSavePersonChar(Long idPerson, Date nowDate) {
        CharacteristicsDto characteristicsDto = new CharacteristicsDto();
        characteristicsDto.setCdCharacteristic(ServiceConstants.CPL_70);
        characteristicsDto.setCdCharCategory(ServiceConstants.CPL);
        characteristicsDto.setCdStatus(ServiceConstants.S);
        characteristicsDto.setDtCharStart(nowDate);
        characteristicsDto.setIdPerson(idPerson);
        characteristicsDto.setDtLastUpdate(nowDate);
        characteristicsDto.setIndAfcars(ServiceConstants.N);
        characteristicsDto.setDtCharStart(nowDate);
        characteristicsDto.setDtCharEnd(DateUtils.stringToDate(ServiceConstants.MAX_JAVA_DATE_STRING));
        personCharDao.savePersonChar(characteristicsDto);
    }

}
