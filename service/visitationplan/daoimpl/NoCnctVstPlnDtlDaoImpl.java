package us.tx.state.dfps.service.visitationplan.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.VisitPlan;
import us.tx.state.dfps.common.domain.VisitPlanNoCntct;
import us.tx.state.dfps.common.domain.VisitPlanPartcpnt;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.NoCnctVstPlnDtlReq;
import us.tx.state.dfps.service.common.request.VisitationPlanDtlReq;
import us.tx.state.dfps.service.common.response.VisitationPlanDtlRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.visitationplan.dao.NoCnctVstPlnDtlDao;
import us.tx.state.dfps.visitationplan.dto.NoCnctVstPlnDetailDto;
import us.tx.state.dfps.visitationplan.dto.VisitationPlanDetailDto;
import us.tx.state.dfps.visitationplan.dto.VstPlanPartcpntDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:this class
 * is for No Contact Visitation Plan Detail and visitation plan service Sep 20,
 * 2018- 12:08:39 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class NoCnctVstPlnDtlDaoImpl implements NoCnctVstPlnDtlDao {

	private static final String ID_VISIT_PLAN = "idVisitPlan";
	private static final String ID_CREATED_PERSON = "idCreatedPerson";
	private static final String DT_LAST_UPDATED = "dtLastUpdated";
	private static final String DT_CREATED = "dtCreated";
	private static final String NO_VISITATION_PLAN_TASK_CD = "4391";
	private static final String VISITATION_PLAN_TASK_CD = "4392";
	private static final String NO_CONTACT = "NoContact";
	private static final String VISIT_PLAN = "VisitPlan";

	private static final Logger log = Logger.getLogger(NoCnctVstPlnDtlDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Autowired
	PersonDao personDao;

	/**
	 * Method Name: saveNoContactVisitationPlnDetails Method Description: To
	 * save no contact visitation plan details
	 * 
	 * @param noCnctVstPlnDtlReq
	 * @return NoCnctVstPlnDtlRes
	 */
	@Override
	public NoCnctVstPlnDetailDto updateNoContactVisitationPlnDetails(NoCnctVstPlnDtlReq noCnctVstPlnDtlReq) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(VisitPlanNoCntct.class);
		NoCnctVstPlnDetailDto noCnctVstPlnDetailDto = new NoCnctVstPlnDetailDto();
		criteria.add(Restrictions.eq("event.idEvent", noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto().getIdEvent()));
		VisitPlanNoCntct visitPlanNoCntct = (VisitPlanNoCntct) criteria.uniqueResult();
		String[] ignoreProperties = { DT_CREATED, DT_LAST_UPDATED, ID_CREATED_PERSON, "idVisitPlanNoCntct" };
		BeanUtils.copyProperties(noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto(), visitPlanNoCntct, ignoreProperties);
		visitPlanNoCntct.setDtLastUpdated(new Date());
		List<VisitPlanPartcpnt> visitPlanPartcpnts = populateUpdateVisitPlanPartcpnt(
				noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto(), visitPlanNoCntct);

		visitPlanNoCntct.getVisitPlanPartcpnts().clear();
		visitPlanNoCntct.getVisitPlanPartcpnts().addAll(visitPlanPartcpnts);
		sessionFactory.getCurrentSession().saveOrUpdate(visitPlanNoCntct);
		noCnctVstPlnDetailDto.setDtLastUpdated(new Date());
		BeanUtils.copyProperties(visitPlanNoCntct, noCnctVstPlnDetailDto);
		noCnctVstPlnDetailDto.setIdEvent(noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto().getIdEvent());
		return noCnctVstPlnDetailDto;

	}

	/**
	 * Method Name: populateSaveNoContactVisitationPlnDetails Method
	 * Description: This method is used to Save the No Contact Visitation Plan.
	 * 
	 * @param noCnctVstPlnDetailDto
	 * @return
	 */
	@Override
	public Long saveNoContactVisitationPlnDetails(NoCnctVstPlnDetailDto noCnctVstPlnDetailDto) {

		Long primaryKey = ServiceConstants.LongZero;
		VisitPlanNoCntct visitPlanNoCntct = new VisitPlanNoCntct();
		if (!TypeConvUtil.isNullOrEmpty(noCnctVstPlnDetailDto.getIdLastUpdatePerson())) {
			visitPlanNoCntct.setIdLastUpdatePerson(noCnctVstPlnDetailDto.getIdLastUpdatePerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(noCnctVstPlnDetailDto.getIdCreatedPerson())) {
			visitPlanNoCntct.setIdCreatedPerson(noCnctVstPlnDetailDto.getIdCreatedPerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(noCnctVstPlnDetailDto.getIdEvent())) {
			Event event = new Event();
			event.setIdEvent(noCnctVstPlnDetailDto.getIdEvent());
			visitPlanNoCntct.setEvent(event);
		}
		visitPlanNoCntct.setDtCreated(new Date());
		visitPlanNoCntct.setDtLastUpdated(new Date());

		if (!TypeConvUtil.isNullOrEmpty(noCnctVstPlnDetailDto.getTxtAdtnlSprtvAdults())) {
			visitPlanNoCntct.setTxtAdtnlSprtvAdults(noCnctVstPlnDetailDto.getTxtAdtnlSprtvAdults());
		}

		if (!TypeConvUtil.isNullOrEmpty(noCnctVstPlnDetailDto.getTxtCntctBeginCndtn())) {
			visitPlanNoCntct.setTxtCntctBeginCndtn(noCnctVstPlnDetailDto.getTxtCntctBeginCndtn());
		}

		if (!TypeConvUtil.isNullOrEmpty(noCnctVstPlnDetailDto.getTxtRsnNoCntct())) {
			visitPlanNoCntct.setTxtRsnNoCntct(noCnctVstPlnDetailDto.getTxtRsnNoCntct());
		}

		if (!TypeConvUtil.isNullOrEmpty(noCnctVstPlnDetailDto.getTxtSprtvAdults())) {
			visitPlanNoCntct.setTxtSprtvAdults(noCnctVstPlnDetailDto.getTxtSprtvAdults());
		}

		List<VisitPlanPartcpnt> visitPlanPartcpnts = populateSaveVisitPlanPartcpnt(noCnctVstPlnDetailDto,
				visitPlanNoCntct);
		visitPlanNoCntct.setVisitPlanPartcpnts(visitPlanPartcpnts);
		primaryKey = (Long) sessionFactory.getCurrentSession().save(visitPlanNoCntct);
		if (TypeConvUtil.isNullOrEmpty(primaryKey)) {
			log.fatal("NoCnctVstPlnDtlDao.insertNoContactVisitationPlnDetails.NotFound");
			throw new DataNotFoundException(messageSource
					.getMessage("NoCnctVstPlnDtlDao.insertNoContactVisitationPlnDetails.NotFound", null, Locale.US));
		}
		noCnctVstPlnDetailDto.setIdVisitPlanNoCntct(primaryKey);
		return primaryKey;

	}

	/**
	 * Method Name: populateSaveVisitPlanPartcpnt Method Description:This method
	 * 
	 * @param noCnctVstPlnDetailDto
	 * @param visitPlanNoCntct
	 * @return
	 */
	@Override
	public List<VisitPlanPartcpnt> populateSaveVisitPlanPartcpnt(NoCnctVstPlnDetailDto noCnctVstPlnDetailDto,
			VisitPlanNoCntct visitPlanNoCntct) {
		List<VisitPlanPartcpnt> visitPlanPartcpnts = new ArrayList<>();
		List<VstPlanPartcpntDto> noCnctVstPlanPartcpntList = noCnctVstPlnDetailDto.getNoCnctVstPlanPartcpntList();
		if (!ObjectUtils.isEmpty(noCnctVstPlanPartcpntList)) {
			noCnctVstPlanPartcpntList.forEach(vstPlanPartcpntDto -> {
				if (!TypeConvUtil.isNullOrEmpty(vstPlanPartcpntDto.getIdPerson())) {
					VisitPlanPartcpnt visitPlanPartcpnt = new VisitPlanPartcpnt();
					visitPlanPartcpnt.setIdLastUpdatePerson(noCnctVstPlnDetailDto.getIdLastUpdatePerson());
					visitPlanPartcpnt.setIdCreatedPerson(noCnctVstPlnDetailDto.getIdCreatedPerson());
					visitPlanPartcpnt.setDtCreated(new Date());
					visitPlanPartcpnt.setDtLastUpdated(new Date());
					visitPlanPartcpnt.setIndDfpsRecmnd(vstPlanPartcpntDto.getIndDfpsRecmnd());
					visitPlanPartcpnt.setIndCourtOrdrd(vstPlanPartcpntDto.getIndCourtOrdrd());
					visitPlanPartcpnt.setTxtCauseNbr(vstPlanPartcpntDto.getTxtCauseNbr());
					visitPlanPartcpnt.setIndChildRmvd(vstPlanPartcpntDto.getIndChildRmvd());
					visitPlanPartcpnt.setIdPerson(vstPlanPartcpntDto.getIdPerson());
					visitPlanPartcpnt.setVisitPlanNoCntct(visitPlanNoCntct);
					visitPlanPartcpnts.add(visitPlanPartcpnt);

				}
			});
		}

		return visitPlanPartcpnts;

	}

	/**
	 * Method Name: insertDayCareRequest Method Description:Inserts the details
	 * of DayCareRequestValueDto
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<VstPlanPartcpntDto> reteriveVisitPlanPartcpnt(Long idVstPlan, Long idStage, Date dtCreated,
			String plan) {
		String restrctn = ServiceConstants.EMPTY_STRING;
		if (NO_CONTACT.equalsIgnoreCase(plan)) {
			restrctn = "visitPlanNoCntct.idVisitPlanNoCntct";
		} else if (VISIT_PLAN.equalsIgnoreCase(plan)) {
			restrctn = "visitPlan.idVisitPlan";
		}
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(VisitPlanPartcpnt.class)
				.add(Restrictions.eq(restrctn, idVstPlan));
		List<VisitPlanPartcpnt> result = (List<VisitPlanPartcpnt>) criteria.list();
		List<VstPlanPartcpntDto> vstPlanPartcpntList = new ArrayList<>();
		if (!ObjectUtils.isEmpty(result)) {
			List<PersonListDto> personListDto = personDao.getPersonListByStage(idStage);
			for (VisitPlanPartcpnt visitPlanPartcpnt : result) {
				VstPlanPartcpntDto vstPlanPartcpntDto = new VstPlanPartcpntDto();
				BeanUtils.copyProperties(visitPlanPartcpnt, vstPlanPartcpntDto);
				personListDto.stream().filter(person -> person.getIdPerson().equals(vstPlanPartcpntDto.getIdPerson()))
						.collect(Collectors.toList()).forEach(personList -> {
							if (!ObjectUtils.isEmpty(personList.getPersonDateOfbirth())) {
								vstPlanPartcpntDto.setPartcpntAge(new Long(
										DateUtils.getPersonListAge(personList.getPersonDateOfbirth(), dtCreated)));
							}
							vstPlanPartcpntDto.setPartcpntName(personList.getPersonFull());
							vstPlanPartcpntDto.setPartcpntGender(personList.getPersonSex());
							vstPlanPartcpntDto.setRelInt(personList.getStagePersRelInt());
						});
				vstPlanPartcpntList.add(vstPlanPartcpntDto);
			}
		}

		return vstPlanPartcpntList;
	}

	/**
	 * Method Name: insertDayCareRequest Method Description:Inserts the details
	 * of DayCareRequestValueDto
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long
	 * 
	 */
	@Override
	public NoCnctVstPlnDetailDto reteriveNoContactVisitPlan(Long idEvent, Long idStage) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(VisitPlanNoCntct.class)
				.add(Restrictions.eq("event.idEvent", idEvent));
		VisitPlanNoCntct result = (VisitPlanNoCntct) criteria.uniqueResult();
		NoCnctVstPlnDetailDto noCnctVstPlnDetailDto = new NoCnctVstPlnDetailDto();
		BeanUtils.copyProperties(result, noCnctVstPlnDetailDto);
		Date dtCreated = ObjectUtils.isEmpty(result.getDtCreated()) ? new Date() : result.getDtCreated();
		noCnctVstPlnDetailDto.setIdEvent(result.getEvent().getIdEvent());
		noCnctVstPlnDetailDto.setEventStatus(result.getEvent().getCdEventStatus());
		List<VstPlanPartcpntDto> list = reteriveVisitPlanPartcpnt(noCnctVstPlnDetailDto.getIdVisitPlanNoCntct(),
				idStage, dtCreated, NO_CONTACT);
		noCnctVstPlnDetailDto.setNoCnctVstPlanPartcpntList(list);
		return noCnctVstPlnDetailDto;
	}

	/**
	 * Method Name: populateUpdateVisitPlanPartcpnt Method Description: This
	 * method is used to populate the Participant list be saved
	 * 
	 * @param noCnctVstPlnDetailDto
	 * @param visitPlanNoCntct
	 * @return List<VisitPlanPartcpnt>
	 */
	private List<VisitPlanPartcpnt> populateUpdateVisitPlanPartcpnt(NoCnctVstPlnDetailDto noCnctVstPlnDetailDto,
			VisitPlanNoCntct visitPlanNoCntct) {
		List<VisitPlanPartcpnt> visitPlanPartcpnts = new ArrayList<>();
		List<VstPlanPartcpntDto> noCnctVstPlanPartcpntList = noCnctVstPlnDetailDto.getNoCnctVstPlanPartcpntList();
		if (!ObjectUtils.isEmpty(noCnctVstPlanPartcpntList)) {
			noCnctVstPlanPartcpntList.forEach(vstPlanPartcpntDto -> {
				if (!TypeConvUtil.isNullOrEmpty(vstPlanPartcpntDto.getIdPerson())) {
					VisitPlanPartcpnt visitPlanPartcpnt = new VisitPlanPartcpnt();
					visitPlanPartcpnt.setIdVisitPlanPartcpnt(noCnctVstPlnDetailDto.getIdVisitPlanNoCntct());
					if (!TypeConvUtil.isNullOrEmpty(noCnctVstPlnDetailDto.getIdLastUpdatePerson())) {
						visitPlanPartcpnt.setIdLastUpdatePerson(noCnctVstPlnDetailDto.getIdLastUpdatePerson());
					}
					visitPlanPartcpnt.setIdCreatedPerson(visitPlanNoCntct.getIdCreatedPerson());
					visitPlanPartcpnt.setDtCreated(visitPlanNoCntct.getDtCreated());
					visitPlanPartcpnt.setDtLastUpdated(new Date());
					if (!TypeConvUtil.isNullOrEmpty(vstPlanPartcpntDto.getIndDfpsRecmnd())) {
						visitPlanPartcpnt.setIndDfpsRecmnd(vstPlanPartcpntDto.getIndDfpsRecmnd());
					}
					if (!TypeConvUtil.isNullOrEmpty(vstPlanPartcpntDto.getIndCourtOrdrd())) {
						visitPlanPartcpnt.setIndCourtOrdrd(vstPlanPartcpntDto.getIndCourtOrdrd());
					}
					visitPlanPartcpnt.setTxtCauseNbr(vstPlanPartcpntDto.getTxtCauseNbr());
					visitPlanPartcpnt.setIndChildRmvd(vstPlanPartcpntDto.getIndChildRmvd());
					visitPlanPartcpnt.setIdPerson(vstPlanPartcpntDto.getIdPerson());
					visitPlanPartcpnt.setVisitPlanNoCntct(visitPlanNoCntct);
					visitPlanPartcpnts.add(visitPlanPartcpnt);
				}
			});
		}
		return visitPlanPartcpnts;

	}

	/**
	 * 
	 * Method Name: reteriveVisitationPlanDetail Method Description: This method
	 * is used to get the visitation Plan Detail.
	 * 
	 * @param idEvent
	 * @param idStage
	 * @return
	 */
	@Override
	public VisitationPlanDetailDto reteriveVisitationPlanDetail(Long idEvent, Long idStage) {
		/* Criteria to fetch the existing visitation Plan based on Event id */
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(VisitPlan.class)
				.add(Restrictions.eq("event.idEvent", idEvent));
		VisitPlan result = (VisitPlan) criteria.uniqueResult();
		VisitationPlanDetailDto visitationPlanDetailDto = new VisitationPlanDetailDto();
		/* Populating the Visitation Plan DTO from the Entity. */
		BeanUtils.copyProperties(result, visitationPlanDetailDto);
		/* Date Created to calculate the age of the participant */
		Date dtCreated = ObjectUtils.isEmpty(result.getDtCreated()) ? new Date() : result.getDtCreated();
		visitationPlanDetailDto.setIdEvent(result.getEvent().getIdEvent());
		/* fetching the participant List */
		List<VstPlanPartcpntDto> list = reteriveVisitPlanPartcpnt(visitationPlanDetailDto.getIdVisitPlan(), idStage,
				dtCreated, VISIT_PLAN);
		visitationPlanDetailDto.setVisitPlanPartcpntList(list);
		return visitationPlanDetailDto;
	}

	/**
	 * 
	 * Method Name: saveVisitationPlnDetail Method Description: This method is
	 * used to save the Visitation Plan detail Screen.
	 * 
	 * @param visitationPlanDetailDto
	 * @return
	 */
	@Override
	public Long saveVisitationPlnDetail(VisitationPlanDetailDto visitationPlanDetailDto) {
		Long primaryKey = ServiceConstants.LongZero;
		VisitPlan visitPlan = new VisitPlan();
		/* Copying the DTO properties to the Entity */
		BeanUtils.copyProperties(visitationPlanDetailDto, visitPlan);
		/* Setting the Event Detail to The Entity */
		if (!TypeConvUtil.isNullOrEmpty(visitationPlanDetailDto.getIdEvent())) {
			Event event = new Event();
			event.setIdEvent(visitationPlanDetailDto.getIdEvent());
			visitPlan.setEvent(event);
		}
		/*
		 * As its the new Visitation Plan we will set the created and Last
		 * update Date to new date
		 */
		visitPlan.setDtCreated(new Date());
		visitPlan.setDtLastUpdated(new Date());
		/* Creating the entity list of visitation plan participant list */
		List<VisitPlanPartcpnt> visitPlanPartcpntList = new ArrayList<>();
		/* Setting the participant entity list to the visitation plan entity. */
		if (!ObjectUtils.isEmpty(visitationPlanDetailDto.getVisitPlanPartcpntList())) {
			visitationPlanDetailDto.getVisitPlanPartcpntList().forEach(visitPlanPartcpntDto -> {
				if (!ObjectUtils.isEmpty(visitPlanPartcpntDto)
						&& !ObjectUtils.isEmpty(visitPlanPartcpntDto.getIdPerson())) {

					VisitPlanPartcpnt visitPlanPartcpnt = new VisitPlanPartcpnt();
					BeanUtils.copyProperties(visitPlanPartcpntDto, visitPlanPartcpnt);
					/*
					 * As its the new Visitation Plan we will set the created
					 * and Last update Date to new date
					 */
					visitPlanPartcpnt.setDtCreated(new Date());
					visitPlanPartcpnt.setDtLastUpdated(new Date());
					visitPlanPartcpnt.setIdLastUpdatePerson(visitPlan.getIdLastUpdatePerson());
					visitPlanPartcpnt.setIdCreatedPerson(visitPlan.getIdCreatedPerson());
					visitPlanPartcpnt.setVisitPlan(visitPlan);
					visitPlanPartcpntList.add(visitPlanPartcpnt);
				}
			});
		}
		if (!ObjectUtils.isEmpty(visitPlanPartcpntList)) {
			visitPlan.setVisitPlanPartcpnts(visitPlanPartcpntList);
		}

		primaryKey = (Long) sessionFactory.getCurrentSession().save(visitPlan);
		if (TypeConvUtil.isNullOrEmpty(primaryKey)) {
			throw new DataNotFoundException(
					messageSource.getMessage("NoCnctVstPlnDtlDao.saveVisitationPlnDetail.NotFound", null, Locale.US));
		}
		visitationPlanDetailDto.setIdVisitPlan(primaryKey);
		return primaryKey;

	}

	/**
	 * 
	 * Method Name: updateVisitationPlnDetail Method Description: This method is
	 * used to update the existing visitation Plan.
	 * 
	 * @param visitationPlanDtlReq
	 * @return VisitationPlanDetailDto
	 */
	@Override
	public VisitationPlanDetailDto updateVisitationPlnDetail(VisitationPlanDtlReq visitationPlanDtlReq) {
		VisitationPlanDetailDto visitationPlanDetailDto = new VisitationPlanDetailDto();
		/* Loading the existing visitation plan entity to make the changes. */
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(VisitPlan.class)
				.add(Restrictions.eq("event.idEvent", visitationPlanDtlReq.getVisitationPlanDetailDto().getIdEvent()));
		VisitPlan visitPlan = (VisitPlan) criteria.uniqueResult();
		String[] ignoreProperties = { DT_CREATED, DT_LAST_UPDATED, ID_CREATED_PERSON, ID_VISIT_PLAN };
		/*
		 * Copying the properties from DTO to entity and ignoring the values
		 * which has to be same.
		 */
		BeanUtils.copyProperties(visitationPlanDtlReq.getVisitationPlanDetailDto(), visitPlan, ignoreProperties);
		visitPlan.setDtLastUpdated(new Date());
		/*
		 * If the participant list is not empty then checking for update in the
		 * participant list
		 */
		if (!ObjectUtils.isEmpty(visitPlan.getVisitPlanPartcpnts())
				&& !ObjectUtils.isEmpty(visitationPlanDtlReq.getVisitationPlanDetailDto().getVisitPlanPartcpntList())) {
			List<Long> updatedParticipant = new ArrayList<>();
			List<VisitPlanPartcpnt> participantToRemove = new ArrayList<>();
			visitPlan.getVisitPlanPartcpnts().forEach(visitplanPartcpntDto -> {
				VstPlanPartcpntDto vstPlanPartcpntDto = visitationPlanDtlReq.getVisitationPlanDetailDto()
						.getVisitPlanPartcpntList().stream()
						.filter(participant -> participant.getIdPerson().equals(visitplanPartcpntDto.getIdPerson()))
						.findAny().orElse(null);
				if (!ObjectUtils.isEmpty(vstPlanPartcpntDto)) {
					// Participant Record needs to be updated.
					vstPlanPartcpntDto.setDtCreated(visitplanPartcpntDto.getDtCreated());
					vstPlanPartcpntDto.setIdCreatedPerson(visitplanPartcpntDto.getIdCreatedPerson());
					vstPlanPartcpntDto.setDtLastUpdated(new Date());
					vstPlanPartcpntDto.setIdLastUpdatePerson(visitationPlanDtlReq.getIdUser());
					BeanUtils.copyProperties(vstPlanPartcpntDto, visitplanPartcpntDto);
					updatedParticipant.add(visitplanPartcpntDto.getIdPerson());
				} else {
					// Participant Record need to be deleted.
					participantToRemove.add(visitplanPartcpntDto);
				}
			});
			// Remove all the Participant which are not there in incoming child
			// list.
			visitPlan.getVisitPlanPartcpnts().removeAll(participantToRemove);
			// Add the Participant which are not saved already.
			List<VisitPlanPartcpnt> participantListNew = new ArrayList<>();
			visitationPlanDtlReq.getVisitationPlanDetailDto().getVisitPlanPartcpntList().forEach(participant -> {
				if (!updatedParticipant.contains(participant.getIdPerson())) {
					VisitPlanPartcpnt visitPlanPartcpnt = new VisitPlanPartcpnt();
					BeanUtils.copyProperties(participant, visitPlanPartcpnt);
					visitPlanPartcpnt.setDtCreated(new Date());
					visitPlanPartcpnt.setDtLastUpdated(new Date());
					visitPlanPartcpnt.setVisitPlan(visitPlan);
					visitPlanPartcpnt.setIdLastUpdatePerson(visitationPlanDtlReq.getIdUser());
					visitPlanPartcpnt.setIdCreatedPerson(visitationPlanDtlReq.getIdUser());
					participantListNew.add(visitPlanPartcpnt);
				}
			});
			visitPlan.getVisitPlanPartcpnts().addAll(participantListNew);
		} else if (!ObjectUtils.isEmpty(visitPlan.getVisitPlanPartcpnts())
				&& ObjectUtils.isEmpty(visitationPlanDtlReq.getVisitationPlanDetailDto().getVisitPlanPartcpntList())) {
			// If saved record has the participant but Updated record doesn't
			// then
			// remove the one from Session.
			visitPlan.getVisitPlanPartcpnts().removeAll(visitPlan.getVisitPlanPartcpnts());
		} else if (ObjectUtils.isEmpty(visitPlan.getVisitPlanPartcpnts())
				&& !ObjectUtils.isEmpty(visitationPlanDtlReq.getVisitationPlanDetailDto().getVisitPlanPartcpntList())) {
			// If Saved record doesn't have any saved child but incoming
			// details have it.
			List<VisitPlanPartcpnt> participantListNew = new ArrayList<>();
			visitationPlanDtlReq.getVisitationPlanDetailDto().getVisitPlanPartcpntList().forEach(participant -> {
				VisitPlanPartcpnt visitPlanPartcpnt = new VisitPlanPartcpnt();
				BeanUtils.copyProperties(participant, visitPlanPartcpnt);
				visitPlanPartcpnt.setDtCreated(new Date());
				visitPlanPartcpnt.setDtLastUpdated(new Date());
				visitPlanPartcpnt.setVisitPlan(visitPlan);
				visitPlanPartcpnt.setIdLastUpdatePerson(visitationPlanDtlReq.getIdUser());
				visitPlanPartcpnt.setIdCreatedPerson(visitationPlanDtlReq.getIdUser());
				participantListNew.add(visitPlanPartcpnt);
			});
			visitPlan.getVisitPlanPartcpnts().addAll(participantListNew);
		}
		sessionFactory.getCurrentSession().saveOrUpdate(visitPlan);
		visitationPlanDetailDto.setDtLastUpdated(new Date());
		BeanUtils.copyProperties(visitPlan, visitationPlanDetailDto);
		visitationPlanDetailDto.setIdEvent(visitationPlanDtlReq.getVisitationPlanDetailDto().getIdEvent());
		return visitationPlanDetailDto;
	}

	/**
	 * 
	 * Method Name: deleteVisitationPlanDtl Method Description:This method is
	 * used to delete the visitation Plan and No contact visitation Plan.
	 * 
	 * @param idEvent
	 * @return String
	 */
	@Override
	public String deleteVisitationPlanDtl(Long idEvent) {
		String result = ServiceConstants.EMPTY_STRING;
		Criteria criteriaVisitation = sessionFactory.getCurrentSession().createCriteria(VisitPlan.class)
				.add(Restrictions.eq("event.idEvent", idEvent));
		VisitPlan visitPlan = (VisitPlan) criteriaVisitation.uniqueResult();
		List<VisitPlanPartcpnt> visitPlanPartcpntlist = new ArrayList<>();
		/*
		 * if the visitation plan Entity is not empty delete the visitation
		 * Plan. Else check for no contact visitation plan
		 */
		if (!ObjectUtils.isEmpty(visitPlan)) {
			visitPlanPartcpntlist = visitPlan.getVisitPlanPartcpnts();
			/*
			 * Checking if the visitation plan is having any participant
			 * associated to it. if yes then delete those participants also.
			 */
			if (!CollectionUtils.isEmpty(visitPlanPartcpntlist)) {
				deleteParticipant(visitPlanPartcpntlist);
			}
			/* deleting the main visitation plan. */
			sessionFactory.getCurrentSession().delete(visitPlan);
		} else {
			Criteria criteriaNoCntctVisitation = sessionFactory.getCurrentSession()
					.createCriteria(VisitPlanNoCntct.class);
			criteriaNoCntctVisitation.add(Restrictions.eq("event.idEvent", idEvent));
			VisitPlanNoCntct visitPlanNoCntct = (VisitPlanNoCntct) criteriaNoCntctVisitation.uniqueResult();
			visitPlanPartcpntlist = visitPlanNoCntct.getVisitPlanPartcpnts();
			/*
			 * Checking if the No contact visitation plan is having any
			 * participant associated to it. if yes then delete those
			 * participants also.
			 */
			if (!CollectionUtils.isEmpty(visitPlanPartcpntlist)) {
				deleteParticipant(visitPlanPartcpntlist);
			}
			/* deleting the main visitation plan. */
			sessionFactory.getCurrentSession().delete(visitPlanNoCntct);
		}
		/* Deleting the event related to that event id. */
		Criteria eventCriteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		eventCriteria.add(Restrictions.eq("idEvent", idEvent));
		Event event = (Event) eventCriteria.uniqueResult();
		if (!ObjectUtils.isEmpty(event)) {
			sessionFactory.getCurrentSession().delete(event);
			result = "success";
		}
		return result;
	}

	/**
	 * 
	 * Method Name: deleteParticipant Method Description:This method is used to
	 * delete all the participant List related to a Visitation Plan/No Contact
	 * Visitation Plan.
	 * 
	 * @param visitPlanPartcpntlist
	 */
	private void deleteParticipant(List<VisitPlanPartcpnt> visitPlanPartcpntlist) {
		/*
		 * Iterating through the participant list if not empty deleting the
		 * same.
		 */
		visitPlanPartcpntlist.forEach(participant -> {
			if (!ObjectUtils.isEmpty(participant)) {
				sessionFactory.getCurrentSession().delete(participant);
			}

		});
	}

	/**
	 * 
	 * Method Name: visitationPlanExist Method Description:This method is used
	 * to find out Whether a Visitation Plan for exists for given participant.
	 * 
	 * @param idStage
	 * @param participantMap
	 * @return VisitationPlanDtlRes
	 */
	@SuppressWarnings("unchecked")
	public VisitationPlanDtlRes visitationPlanExist(Long idStage,
			HashMap<VstPlanPartcpntDto, List<VstPlanPartcpntDto>> participantMap, Long idEvent) {
		VisitationPlanDtlRes visitationPlanDtlRes = new VisitationPlanDtlRes();
		List<Event> eventList = new ArrayList<>();
		/*
		 * Below criteria is used to fetch all the event of visitation plan and
		 * no visitation plan having event status as "PROC"
		 */
		Criteria criteriaEvent = sessionFactory.getCurrentSession().createCriteria(Event.class)
				.add(Restrictions.eq("stage.idStage", idStage))
				.add(Restrictions.ne("cdEventStatus", ServiceConstants.EVENT_STATUS_APRV))
				.add(Restrictions.in("cdTask", new String[] { VISITATION_PLAN_TASK_CD, NO_VISITATION_PLAN_TASK_CD }));
		eventList = (List<Event>) criteriaEvent.list();
		/*
		 * if the event list is not empty then splitting out the visitation plan
		 * event list and no visitation plan event List.
		 */
		if (!ObjectUtils.isEmpty(eventList)) {
			List<Long> idEventVisitationList = new ArrayList<>();
			List<Long> idEventNoVisitationList = new ArrayList<>();
			// Splitting out the list of visitation plan and no visitation plan.
			eventList.forEach(event -> {
				if (VISITATION_PLAN_TASK_CD.equalsIgnoreCase(event.getCdTask()))
					idEventVisitationList.add(event.getIdEvent());
				else
					idEventNoVisitationList.add(event.getIdEvent());
			});
			/* Removing the current id event in case of update. */
			if (!ObjectUtils.isEmpty(idEvent) && 0 != idEvent) {
				if (!ObjectUtils.isEmpty(idEventVisitationList) && idEventVisitationList.contains(idEvent)) {
					idEventVisitationList.remove(idEvent);
				} else if (!ObjectUtils.isEmpty(idEventNoVisitationList) && idEventNoVisitationList.contains(idEvent)) {
					idEventNoVisitationList.remove(idEvent);
				}
			}
			/*
			 * If the visitation plan event list is not empty then fetching the
			 * visitation plan.
			 */
			if (!ObjectUtils.isEmpty(idEventVisitationList)) {
				Criteria criteriaVisitation = sessionFactory.getCurrentSession().createCriteria(VisitPlan.class)
						.add(Restrictions.in("event.idEvent", idEventVisitationList));
				List<VisitPlan> visitPlanEntityList = (List<VisitPlan>) criteriaVisitation.list();
				/*
				 * If the visitation plan entity list is not empty then
				 * iterating through the list and passing the list of
				 * participant to check the duplicate participant.
				 */
				if (!ObjectUtils.isEmpty(visitPlanEntityList)) {
					visitPlanEntityList.forEach(visitationPlan -> {
						if (!ObjectUtils.isEmpty(visitationPlan)) {
							Criteria criteriaVisitParticipant = sessionFactory.getCurrentSession()
									.createCriteria(VisitPlanPartcpnt.class)
									.add(Restrictions.eq("visitPlan.idVisitPlan", visitationPlan.getIdVisitPlan()));
							List<VisitPlanPartcpnt> result = (List<VisitPlanPartcpnt>) criteriaVisitParticipant.list();
							if (!ObjectUtils.isEmpty(result)) {
								checkDuplicateParticipant(participantMap, result, visitationPlanDtlRes);
							}
						}

					});
				}
			}
			/*
			 * If still the duplicate is not found and the no visitation plan
			 * event list not empty then iterating through the list and fetching
			 * the no visitation plan.
			 */
			if ((ObjectUtils.isEmpty(visitationPlanDtlRes.getResult())
					|| ServiceConstants.NO.equalsIgnoreCase(visitationPlanDtlRes.getResult()))
					&& !ObjectUtils.isEmpty(idEventNoVisitationList)) {
				/*
				 * Fetching the list of no visitation plan for the given list of
				 * no visitation plan event id
				 */
				Criteria criteriaNoVisitation = sessionFactory.getCurrentSession()
						.createCriteria(VisitPlanNoCntct.class)
						.add(Restrictions.in("event.idEvent", idEventNoVisitationList));
				List<VisitPlanNoCntct> noVisitPlanEntityList = (List<VisitPlanNoCntct>) criteriaNoVisitation.list();
				/*
				 * If the no visitation plan entity list is not empty then
				 * iterating through the list and passing the list of
				 * participant to check the duplicate participant.
				 */
				if (!ObjectUtils.isEmpty(noVisitPlanEntityList)) {
					noVisitPlanEntityList.forEach(noVisitationPlan -> {
						if (!ObjectUtils.isEmpty(noVisitationPlan)) {
							Criteria criteriaVisitParticipant = sessionFactory.getCurrentSession()
									.createCriteria(VisitPlanPartcpnt.class)
									.add(Restrictions.eq("visitPlanNoCntct.idVisitPlanNoCntct",
											noVisitationPlan.getIdVisitPlanNoCntct()));
							List<VisitPlanPartcpnt> result = (List<VisitPlanPartcpnt>) criteriaVisitParticipant.list();
							if (!ObjectUtils.isEmpty(result)) {
								checkDuplicateParticipant(participantMap, result, visitationPlanDtlRes);
							}
						}
					});
				}
			}
		}
		return visitationPlanDtlRes;
	}

	/**
	 * 
	 * Method Name: checkDuplicateParticipant Method Description:This method is
	 * used to check whether the participant list is found in already existing
	 * 
	 * visitation/no visitation Plan which are in PROC Status.
	 * 
	 * @param participantMap
	 * @param visitPlanPartcpntlist
	 * @param visitationPlanDtlRes
	 */
	private void checkDuplicateParticipant(HashMap<VstPlanPartcpntDto, List<VstPlanPartcpntDto>> participantMap,
			List<VisitPlanPartcpnt> visitPlanPartcpntlist, VisitationPlanDtlRes visitationPlanDtlRes) {
		/*
		 * Iterating through the map of currently selected participant list to
		 * find out if participant is already existing.
		 */
		participantMap.entrySet().stream().forEach(entry -> {
			/*
			 * Finding out if the existing list is having the child who is in
			 * sub care in current map.
			 */
			if (visitPlanPartcpntlist.stream()
					.anyMatch(participant -> entry.getKey().getIdPerson().equals(participant.getIdPerson()))) {
				/*
				 * If child in sub care is found then looking for the
				 * participant list for checking if other participants are
				 * matching. Iterating through the other participant list if any
				 * of them found matching with the current participant list then
				 * duplicate is found.
				 */
				entry.getValue().stream().forEach(person -> {
					boolean isPresent = visitPlanPartcpntlist.stream()
							.anyMatch(participant -> (person.getIdPerson().equals(participant.getIdPerson())));
					if (isPresent) {
						visitationPlanDtlRes.setResult(ServiceConstants.YES);
						return;
					}
				});
			}
		});
	}
}
