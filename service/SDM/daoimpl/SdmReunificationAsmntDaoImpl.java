/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Data Access Implementation Class for SDM Reunification Assessment Screen.
 *Jun 13, 2018- 6:09:59 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.SDM.daoimpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.ReunfctnAsmnt;
import us.tx.state.dfps.common.domain.ReunfctnAsmntAnsLkp;
import us.tx.state.dfps.common.domain.ReunfctnAsmntChld;
import us.tx.state.dfps.common.domain.ReunfctnAsmntLookup;
import us.tx.state.dfps.common.domain.ReunfctnAsmntQstnLkp;
import us.tx.state.dfps.common.domain.ReunfctnAsmntRspn;
import us.tx.state.dfps.common.exception.TimeMismatchException;
import us.tx.state.dfps.service.SDM.dao.SdmReunificationAsmntDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentAnsDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentChildDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentQuestionsDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentResponseDto;
import us.tx.state.dfps.service.workload.dto.EventDto;

@Repository
public class SdmReunificationAsmntDaoImpl implements SdmReunificationAsmntDao {

	private static final Logger log = Logger.getLogger(SdmReunificationAsmntDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private EventDao eventDao;

	public SdmReunificationAsmntDaoImpl() {
	}

	/**
	 * Method Name: addSdmReunificationAssessment Method Description: Method
	 * Adds the SdmReunification Assessment Record for the Stage.
	 * 
	 * @param reunificatoinAsmntDto
	 * @return idReunificationAsmnt
	 */
	@Override
	public Long addSdmReunificationAssessment(ReunificationAssessmentDto reunificatoinAsmntDto) {
		log.info("addSdmReunificationAssessment Method in SdmReunificationAsmntDaoImpl : Execution Started.");
		// Set the Domain Value from The Reunification Assessment DTO.
		ReunfctnAsmnt reunfctnAsmntEntity = new ReunfctnAsmnt();
		Set<ReunfctnAsmntChld> reunfctnAsmntChldsEntitySet = new HashSet<>();
		Set<ReunfctnAsmntRspn> reunfctnAsmntRspnsEntitySet = new HashSet<>();
		// Set the ReunfctnAsmnt Entity from the Dto.
		BeanUtils.copyProperties(reunificatoinAsmntDto, reunfctnAsmntEntity);
		// Set the reference child and Rspns object to persist along with parent
		// entity.
		if (!ObjectUtils.isEmpty(reunificatoinAsmntDto.getReunificationAsmntChildList())) {
			reunificatoinAsmntDto.getReunificationAsmntChildList().stream().forEach(child -> {
				ReunfctnAsmntChld childEntity = new ReunfctnAsmntChld();
				BeanUtils.copyProperties(child, childEntity);
				childEntity.setDtCreated(new Date());
				childEntity.setDtLastUpdate(new Date());
				childEntity.setReunfctnAsmnt(reunfctnAsmntEntity);
				//Added the code to set the idCreated and idLastUpdated person value for warranty defect 11827
				childEntity.setIdCreatedPerson(reunificatoinAsmntDto.getIdCreatedPerson());
				childEntity.setIdLastUpdatePerson(reunificatoinAsmntDto.getIdLastUpdatePerson());
				reunfctnAsmntChldsEntitySet.add(childEntity);
			});
			reunfctnAsmntEntity.setReunfctnAsmntChlds(reunfctnAsmntChldsEntitySet);
		}
		if (!ObjectUtils.isEmpty(reunificatoinAsmntDto.getReunificationAsmntRspnsList())) {
			reunificatoinAsmntDto.getReunificationAsmntRspnsList().stream().forEach(rspns -> {
				ReunfctnAsmntRspn rspnsEnity = new ReunfctnAsmntRspn();
				BeanUtils.copyProperties(rspns, rspnsEnity);
				rspnsEnity.setIdReunfctnAsmntQstn(rspns.getIdReunfctnAsmntQstn());
				rspnsEnity.setIdReunfctnAsmntAns(rspns.getIdReunfctnAsmntAns());
				rspnsEnity.setReunfctnAsmnt(reunfctnAsmntEntity);
				reunfctnAsmntRspnsEntitySet.add(rspnsEnity);
			});
			reunfctnAsmntEntity.setReunfctnAsmntRspns(reunfctnAsmntRspnsEntitySet);
		}
		// Set the Assessment Version
		ReunfctnAsmntLookup asmntLookup = (ReunfctnAsmntLookup) sessionFactory.getCurrentSession()
				.createCriteria(ReunfctnAsmntLookup.class)
				.add(Restrictions.eq("nbrVersion", reunificatoinAsmntDto.getNbrReunfctnVersion())).uniqueResult();
		reunfctnAsmntEntity.setReunfctnAsmntLookup(asmntLookup);
		// Call the save method for the entity.
		Long returnId = (Long) sessionFactory.getCurrentSession().save(reunfctnAsmntEntity);
		if (!StringUtils.isEmpty(returnId) && !ServiceConstants.ZERO_VAL.equals(returnId)) {
			returnId = reunificatoinAsmntDto.getIdEvent();
		} else {
			returnId = ServiceConstants.ZERO_VAL;
		}
		log.info("addSdmReunificationAssessment Method in SdmReunificationAsmntDaoImpl : Return response event. "
				+ returnId);
		return returnId;
	}

	/**
	 * Method Name: fetchSdmReunificationAssessment Method Description: Method
	 * fetches the Saved Sdm Reunification Assessment Record
	 * 
	 * @param idEvent
	 * @return reunificationAsmntDto
	 */
	@Override
	public ReunificationAssessmentDto fetchSdmReunificationAssessment(Long idEvent) {
		ReunificationAssessmentDto reunificationAsmntDto = new ReunificationAssessmentDto();
		log.info("fetchSdmReunificationAssessment Method in SdmReunificationAsmntDaoImpl : Execution Started.");
		// Fetch the Assessment against the given Id.
		ReunfctnAsmnt reunfctnAsmnt = (ReunfctnAsmnt) sessionFactory.getCurrentSession()
				.createCriteria(ReunfctnAsmnt.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("idEvent", idEvent)).uniqueResult();
		// After fetching the Assessment Records, set the values to DTO to
		// return response.
		if (!ObjectUtils.isEmpty(reunfctnAsmnt)) {
			List<ReunificationAssessmentResponseDto> asmntRspnsList = new ArrayList<>();
			List<ReunificationAssessmentChildDto> asmntChildList = new ArrayList<>();
			if (!ObjectUtils.isEmpty(reunfctnAsmnt.getReunfctnAsmntChlds())) {
				reunfctnAsmnt.getReunfctnAsmntChlds().stream().forEach(child -> {
					ReunificationAssessmentChildDto childDto = new ReunificationAssessmentChildDto();
					BeanUtils.copyProperties(child, childDto);
					asmntChildList.add(childDto);
				});
			}
			if (!ObjectUtils.isEmpty(reunfctnAsmnt.getReunfctnAsmntRspns())) {
				reunfctnAsmnt.getReunfctnAsmntRspns().stream().forEach(res -> {
					ReunificationAssessmentResponseDto responseDto = new ReunificationAssessmentResponseDto();
					BeanUtils.copyProperties(res, responseDto);
					responseDto.setIdReunfctnAsmnt(res.getReunfctnAsmnt().getIdReunfctnAsmnt());
					asmntRspnsList.add(responseDto);
				});
			}
			// Set the Reunification Assessment Dto.
			BeanUtils.copyProperties(reunfctnAsmnt, reunificationAsmntDto);
			reunificationAsmntDto.setReunificationAsmntChildList(asmntChildList);
			reunificationAsmntDto.setReunificationAsmntRspnsList(asmntRspnsList);
			reunificationAsmntDto.setNbrReunfctnVersion(reunfctnAsmnt.getReunfctnAsmntLookup().getNbrVersion());
		}
		log.info("fetchSdmReunificationAssessment Method in SdmReunificationAsmntDaoImpl : response Return for asmnt "
				+ reunificationAsmntDto.getIdReunfctnAsmnt());
		return reunificationAsmntDto;
	}

	/**
	 * Method Name: updateSdmReunificationAssessment Method Description: Method
	 * updates the existing Sdm Reunification Assessment Record
	 * 
	 * @param reunificationAsmntDto
	 * @return idReunificationAsmnt
	 */
	@Override
	public Long updateSdmReunificationAssessment(ReunificationAssessmentDto reunificationAsmntDto) {
		log.info("updateSdmReunificationAssessment Method in SdmReunificationAsmntDaoImpl : Execution Started.");
		Long returnId = ServiceConstants.ZERO_VAL;
		// Load the entity in order to update the values.
		ReunfctnAsmnt reunfctnAsmnt = (ReunfctnAsmnt) sessionFactory.getCurrentSession().get(ReunfctnAsmnt.class,
				reunificationAsmntDto.getIdReunfctnAsmnt());
		if (reunificationAsmntDto.getDtLastUpdate()
				.compareTo(reunfctnAsmnt.getDtLastUpdate()) == ServiceConstants.Zero_INT) {
			// Update the values in DTO which are not supposed to be updated In
			// Entity.
			reunificationAsmntDto.setDtCreated(reunfctnAsmnt.getDtCreated());
			reunificationAsmntDto.setIdCreatedPerson(reunfctnAsmnt.getIdCreatedPerson());
			BeanUtils.copyProperties(reunificationAsmntDto, reunfctnAsmnt);
			// Check and update the Child List.
			log.info(
					"updateSdmReunificationAssessment Method in SdmReunificationAsmntDaoImpl : Updating the Child Records");
			if (!ObjectUtils.isEmpty(reunfctnAsmnt.getReunfctnAsmntChlds())
					&& !ObjectUtils.isEmpty(reunificationAsmntDto.getReunificationAsmntChildList())) {
				List<Long> updatedChildIds = new ArrayList<>();
				Set<ReunfctnAsmntChld> childToBeRemoved = new HashSet<>();
				reunfctnAsmnt.getReunfctnAsmntChlds().stream().forEach(child -> {
					ReunificationAssessmentChildDto inChild = reunificationAsmntDto.getReunificationAsmntChildList()
							.stream().filter(ReunificationAssessmentChildDto -> ReunificationAssessmentChildDto
									.getIdPerson().equals(child.getIdPerson()))
							.findAny().orElse(null);
					if (!ObjectUtils.isEmpty(inChild)) {
						// Child Record needs to be updated.
						inChild.setDtCreated(child.getDtCreated());
						inChild.setIdCreatedPerson(child.getIdCreatedPerson());
						//Added the code to set the idLastUpdated person value for warranty defect 11827
						inChild.setIdLastUpdatePerson(reunificationAsmntDto.getIdLastUpdatePerson());
						inChild.setIdReunfctnAsmntChld(child.getIdReunfctnAsmntChld());
						BeanUtils.copyProperties(inChild, child);
						updatedChildIds.add(child.getIdPerson());
					} else {
						// Child Record need to be deleted.
						childToBeRemoved.add(child);
					}
				});
				// Remove all the child which are not there in incoming child
				// list.
				reunfctnAsmnt.getReunfctnAsmntChlds().removeAll(childToBeRemoved);
				// Add the Child which are not saved already.
				Set<ReunfctnAsmntChld> childToBeAddedSet = new HashSet<>();
				reunificationAsmntDto.getReunificationAsmntChildList().stream().forEach(child -> {
					if (!updatedChildIds.contains(child.getIdPerson())) {
						ReunfctnAsmntChld childEntity = new ReunfctnAsmntChld();
						BeanUtils.copyProperties(child, childEntity);
						childEntity.setDtCreated(new Date());
						childEntity.setDtLastUpdate(new Date());
						//Added the code to set the idCreated and idLastUpdated person value for warranty defect 11827
						childEntity.setIdCreatedPerson(reunificationAsmntDto.getIdCreatedPerson());
						childEntity.setIdLastUpdatePerson(reunificationAsmntDto.getIdLastUpdatePerson());
						childEntity.setReunfctnAsmnt(reunfctnAsmnt);
						childToBeAddedSet.add(childEntity);
					}
				});
				reunfctnAsmnt.getReunfctnAsmntChlds().addAll(childToBeAddedSet);
			} else if (!ObjectUtils.isEmpty(reunfctnAsmnt.getReunfctnAsmntChlds())
					&& ObjectUtils.isEmpty(reunificationAsmntDto.getReunificationAsmntChildList())) {
				// If saved record has the child but Updated record doesn't then
				// remove the one from Session.
				reunfctnAsmnt.getReunfctnAsmntChlds().removeAll(reunfctnAsmnt.getReunfctnAsmntChlds());
			} else if (ObjectUtils.isEmpty(reunfctnAsmnt.getReunfctnAsmntChlds())
					&& !ObjectUtils.isEmpty(reunificationAsmntDto.getReunificationAsmntChildList())) {
				// If Saved record doesn't have any saved child but incoming
				// details have it.
				Set<ReunfctnAsmntChld> reunfctnAsmntChldsEntitySet = new HashSet<>();
				reunificationAsmntDto.getReunificationAsmntChildList().stream().forEach(child -> {
					ReunfctnAsmntChld childEntity = new ReunfctnAsmntChld();
					BeanUtils.copyProperties(child, childEntity);
					childEntity.setDtCreated(new Date());
					childEntity.setDtLastUpdate(new Date());
					childEntity.setReunfctnAsmnt(reunfctnAsmnt);
					reunfctnAsmntChldsEntitySet.add(childEntity);
				});
				reunfctnAsmnt.getReunfctnAsmntChlds().addAll(reunfctnAsmntChldsEntitySet);
			}
			log.info("updateSdmReunificationAssessment Method in SdmReunificationAsmntDaoImpl : Updating Responses.");
			// Check and Update the Response List.
			if (!ObjectUtils.isEmpty(reunfctnAsmnt.getReunfctnAsmntRspns())
					&& !ObjectUtils.isEmpty(reunificationAsmntDto.getReunificationAsmntRspnsList())) {
				// If both Assessment has both saved and Incoming responses
				List<Long> updatedQidList = new ArrayList<>();
				Set<ReunfctnAsmntRspn> responseToBeRemoved = new HashSet<>();
				reunfctnAsmnt.getReunfctnAsmntRspns().stream().forEach(response -> {
					// Check the Incoming response for saved question, and
					// update the Entity with incoming details.
					ReunificationAssessmentResponseDto inResponse = reunificationAsmntDto
							.getReunificationAsmntRspnsList().stream()
							.filter(ReunificationAssessmentResponseDto -> response.getIdReunfctnAsmntQstn()
									.equals(ReunificationAssessmentResponseDto.getIdReunfctnAsmntQstn()))
							.findAny().orElse(null);
					if (!ObjectUtils.isEmpty(inResponse)) {
						// Update the Record.
						inResponse.setIdReunfctnAsmntRspns(response.getIdReunfctnAsmntRspns());
						inResponse.setDtCreated(response.getDtCreated());
						inResponse.setIdCreatedPerson(response.getIdCreatedPerson());
						BeanUtils.copyProperties(inResponse, response);
						updatedQidList.add(response.getIdReunfctnAsmntQstn());
					} else {
						// set Current record if it is not in incoming Response
						// to To be removed Responses.
						responseToBeRemoved.add(response);
					}
				});
				// Remove all the responses which are there in
				// responseToBeRemoved.
				reunfctnAsmnt.getReunfctnAsmntRspns().removeAll(responseToBeRemoved);
				// Save the Responses which are not saved already.
				Set<ReunfctnAsmntRspn> responseToBeAddedSet = new HashSet<>();
				reunificationAsmntDto.getReunificationAsmntRspnsList().stream().forEach(res -> {
					if (!updatedQidList.contains(res.getIdReunfctnAsmntQstn())) {
						ReunfctnAsmntRspn rspnsEnity = new ReunfctnAsmntRspn();
						BeanUtils.copyProperties(res, rspnsEnity);
						rspnsEnity.setIdReunfctnAsmntQstn(res.getIdReunfctnAsmntQstn());
						rspnsEnity.setIdReunfctnAsmntAns(res.getIdReunfctnAsmntAns());
						rspnsEnity.setReunfctnAsmnt(reunfctnAsmnt);
						responseToBeAddedSet.add(rspnsEnity);
					}
				});
				reunfctnAsmnt.getReunfctnAsmntRspns().addAll(responseToBeAddedSet);
			} else if (!ObjectUtils.isEmpty(reunfctnAsmnt.getReunfctnAsmntRspns())
					&& ObjectUtils.isEmpty(reunificationAsmntDto.getReunificationAsmntRspnsList())) {
				// If Assessment has saved Responses but Incoming Response list
				// is empty. Remove all the saved responses.
				reunfctnAsmnt.getReunfctnAsmntRspns().removeAll(reunfctnAsmnt.getReunfctnAsmntRspns());
			} else if (ObjectUtils.isEmpty(reunfctnAsmnt.getReunfctnAsmntRspns())
					&& !ObjectUtils.isEmpty(reunificationAsmntDto.getReunificationAsmntRspnsList())) {
				// If Assessment doesn't have any Saved Response but Incoming
				// Response list is not empty. Add all the Incoming Responses.
				Set<ReunfctnAsmntRspn> reunfctnAsmntRspnsEntitySet = new HashSet<>();
				reunificationAsmntDto.getReunificationAsmntRspnsList().stream().forEach(rspns -> {
					ReunfctnAsmntRspn rspnsEnity = new ReunfctnAsmntRspn();
					BeanUtils.copyProperties(rspns, rspnsEnity);
					rspnsEnity.setIdReunfctnAsmntQstn(rspns.getIdReunfctnAsmntQstn());
					rspnsEnity.setIdReunfctnAsmntAns(rspns.getIdReunfctnAsmntAns());
					rspnsEnity.setReunfctnAsmnt(reunfctnAsmnt);
					reunfctnAsmntRspnsEntitySet.add(rspnsEnity);
				});
				reunfctnAsmnt.getReunfctnAsmntRspns().addAll(reunfctnAsmntRspnsEntitySet);
			}
			log.info("updateSdmReunificationAssessment Method in SdmReunificationAsmntDaoImpl : Saving Assessment");
			sessionFactory.getCurrentSession().saveOrUpdate(reunfctnAsmnt);
			returnId = reunfctnAsmnt.getidEvent();
		} else {
			log.error("updateSdmReunificationAssessment Method in SdmReunificationAsmntDaoImpl : Time Stamp Mismatch");
			throw new TimeMismatchException();
		}
		log.info(
				"updateSdmReunificationAssessment Method in SdmReunificationAsmntDaoImpl : response returned for event "
						+ returnId);
		return returnId;
	}

	/**
	 * Method Name: deleteSdmReunificationAssessment Method Description: Method
	 * deletes the existing reunfication Assessment Record
	 * 
	 * @param ReunificationAsmnt
	 */
	@Override
	public void deleteSdmReunificationAssessment(ReunificationAssessmentDto reunificationAsmntDto) {
		log.info("deleteSdmReunificationAssessment Method in SdmReunificationAsmntDaoImpl : Execution Started.");
		// Load the Reunification Assessment and Delete the entries.
		ReunfctnAsmnt reunfctnAsmnt = (ReunfctnAsmnt) sessionFactory.getCurrentSession().get(ReunfctnAsmnt.class,
				reunificationAsmntDto.getIdReunfctnAsmnt());
		if (reunfctnAsmnt.getidEvent().equals(reunificationAsmntDto.getIdEvent())) {
			sessionFactory.getCurrentSession().delete(reunfctnAsmnt);
		}
		log.info("deleteSdmReunificationAssessment Method in SdmReunificationAsmntDaoImpl : Return Assessment Deleted "
				+ reunificationAsmntDto.getIdReunfctnAsmnt());
	}

	/**
	 * Method Name: fetchQuestionListForAsmnt Method Description: Method fetches
	 * the Question List for ReunificationAsmnt for specific Assessment Version.
	 * 
	 * @param asmentDto
	 * @return questionList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ReunificationAssessmentQuestionsDto> fetchQuestionListForAsmnt(ReunificationAssessmentDto asmentDto) {
		log.info("fetchQuestionListForAsmnt Method in SdmReunificationAsmntDaoImpl : Execution Started.");
		List<ReunfctnAsmntQstnLkp> questionList = null;
		List<ReunificationAssessmentQuestionsDto> questionDtoList = new ArrayList<>();
		ReunfctnAsmntLookup asmntLookup = (ReunfctnAsmntLookup) sessionFactory.getCurrentSession()
				.createCriteria(ReunfctnAsmntLookup.class)
				.add(Restrictions.eq("nbrVersion", asmentDto.getNbrReunfctnVersion())).uniqueResult();
		questionList = (List<ReunfctnAsmntQstnLkp>) sessionFactory.getCurrentSession()
				.createCriteria(ReunfctnAsmntQstnLkp.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("idReunfctnAsmntLkp", asmntLookup.getIdReunfctnAsmntLookup())).list();
		// Set the Question and Answer DTO from the Result
		questionList.stream().forEach(q -> {
			ReunificationAssessmentQuestionsDto questionDto = new ReunificationAssessmentQuestionsDto();
			List<ReunfctnAsmntAnsLkp> ansList = q.getReunfctnAsmntAnsLkps().stream().collect(Collectors.toList());
			List<ReunificationAssessmentAnsDto> ansDtoList = new ArrayList<>();
			ansList.stream().forEach(a -> {
				ReunificationAssessmentAnsDto ansDto = new ReunificationAssessmentAnsDto();
				BeanUtils.copyProperties(a, ansDto);
				ansDtoList.add(ansDto);
			});
			BeanUtils.copyProperties(q, questionDto);
			ansDtoList.sort(Comparator.comparing(ReunificationAssessmentAnsDto::getNbrOrder));
			questionDto.setReunfctnAsmntAnsList(ansDtoList);
			ReunificationAssessmentResponseDto quesResponse = new ReunificationAssessmentResponseDto();
			// Set the Response to Each Question if the Response is present for
			// Assessment.
			if (!ObjectUtils.isEmpty(asmentDto.getReunificationAsmntRspnsList())) {
				log.info(
						"fetchQuestionListForAsmnt Method in SdmReunificationAsmntDaoImpl : Mapping the Response to Question");
				List<ReunificationAssessmentResponseDto> asmntResponse = asmentDto.getReunificationAsmntRspnsList();
				quesResponse = asmntResponse.stream()
						.filter(r -> r.getIdReunfctnAsmntQstn().equals(q.getIdReunfctnAsmntQstnLkp())).findFirst()
						.orElse(null);
			}
			questionDto.setReunfctnAsmntRspns(quesResponse);
			questionDtoList.add(questionDto);
		});
		log.info("fetchQuestionListForAsmnt Method in SdmReunificationAsmntDaoImpl : Return Question list.");
		return questionDtoList;
	}

	/**
	 * Method Name: getReunificationAsmntLkpVersion Method Description:This
	 * method returns the the latest Assessment Version for Reunificaiton
	 * Assessment.
	 * 
	 * @return nbrVersion
	 */
	@Override
	public Long getReunificationAsmntLkpVersion() {
		log.info("getReunificationAsmntLkpVersion Method in SdmReunificationAsmntDaoImpl : Execution Started.");
		ReunfctnAsmntLookup asmntLookup = (ReunfctnAsmntLookup) sessionFactory.getCurrentSession()
				.createCriteria(ReunfctnAsmntLookup.class).addOrder(Order.desc("nbrVersion")).setMaxResults(1)
				.uniqueResult();
		log.info(
				"getReunificationAsmntLkpVersion Method in SdmReunificationAsmntDaoImpl : return current asmnt version "
						+ asmntLookup.getNbrVersion());
		return asmntLookup.getNbrVersion();
	}

	/**
	 * Method Name: getidAsmntEventsForStageAndHshld Method Description: Method
	 * fetches the list of reunification assessment event ids for given stage
	 * and household.
	 * 
	 * @param idStage
	 * @param idPerson
	 * @return eventList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventDto> getidAsmntEventsForStageAndHshld(Long idStage, Long idHshldPerson) {
		log.info("getidAsmntEventsForStageAndHshld Method in SdmReunificationAsmntDaoImpl : Execution Started.");
		List<EventDto> eventList = new ArrayList<>();
		List<ReunfctnAsmnt> asmntLst = (List<ReunfctnAsmnt>) sessionFactory.getCurrentSession()
				.createCriteria(ReunfctnAsmnt.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("idStage", idStage)).add(Restrictions.eq("idHshldPerson", idHshldPerson)).list();
		if (!ObjectUtils.isEmpty(asmntLst)) {
			asmntLst.stream().forEach(a -> {
				EventDto event = eventDao.getEventByid(a.getidEvent());
				eventList.add(event);
			});
		}
		log.info("getidAsmntEventsForStageAndHshld Method in SdmReunificationAsmntDaoImpl : Returned Event list.");
		return eventList;
	}

	/**
	 * Method Name: getidAsmntEventsForStageAndParent Method Description: Method
	 * fetches the list of reunification assessment events for the given Stage,
	 * having given person as primary or secondary parent.
	 * 
	 * @param idStage
	 * @param idParent
	 * @return eventList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventDto> getidAsmntEventsForStageAndParent(Long idStage, Long idParent) {
		log.info("getidAsmntEventsForStageAndParent Method in SdmReunificationAsmntDaoImpl : Execution Started.");
		List<EventDto> eventList = new ArrayList<>();
		List<ReunfctnAsmnt> asmntLst = (List<ReunfctnAsmnt>) sessionFactory.getCurrentSession()
				.createCriteria(ReunfctnAsmnt.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("idStage", idStage)).add(Restrictions
						.or(Restrictions.eq("idPrmryPerson", idParent), Restrictions.eq("idSecndryPerson", idParent)))
				.list();
		if (!ObjectUtils.isEmpty(asmntLst)) {
			asmntLst.stream().forEach(a -> {
				EventDto event = eventDao.getEventByid(a.getidEvent());
				eventList.add(event);
			});
		}
		log.info("getidAsmntEventsForStageAndParent Method in SdmReunificationAsmntDaoImpl : Returned Event list.");
		return eventList;
	}

	/**
	 * Method Name: anyChildWithRtnHome Method Description: Method finds if any
	 * Assessment has the child assessed with Recommendation summary as Return
	 * Home.
	 * 
	 * @param idEvent
	 * @return childWithRtnHmExists
	 */
	@Override
	public Boolean anyChildWithRtnHome(Long idEvent) {
		boolean childWithRtnHmExists = false;
		ReunfctnAsmnt asmnt = (ReunfctnAsmnt) sessionFactory.getCurrentSession().createCriteria(ReunfctnAsmnt.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).add(Restrictions.eq("idEvent", idEvent))
				.uniqueResult();
		if (!ObjectUtils.isEmpty(asmnt)) {
			// Stream over all the Children assessed in the Fetched assessment
			// and check if any has the Recommendation
			// Summary as Return Home.
			childWithRtnHmExists = asmnt.getReunfctnAsmntChlds().stream()
					.anyMatch(child -> CodesConstant.OVRDECSN_RTNHME.equalsIgnoreCase(child.getCdRecmndtnSumm()));
		}
		return childWithRtnHmExists;
	}

	/**
	 * Method Name: getLatestAssessmentDateInStage Method Description: The
	 * method fetches the latest date of assessment for all available
	 * assessments in the stage.
	 * 
	 * @param idStage
	 * @return dtlatestAsmnt
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Date getLatestAssessmentDateInStage(Long idStage, Long idEvent) {
		Date dtlatestAsmnt = null;
		List<ReunfctnAsmnt> asmntLst = (List<ReunfctnAsmnt>) sessionFactory.getCurrentSession()
				.createCriteria(ReunfctnAsmnt.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("idStage", idStage)).add(Restrictions.ne("idEvent", idEvent))
				.addOrder(Order.desc("dtAsmntCmpltd")).list();
		if (!ObjectUtils.isEmpty(asmntLst)) {
			dtlatestAsmnt = asmntLst.get(0).getDtAsmntCmpltd();
		}
		return dtlatestAsmnt;
	}

}
