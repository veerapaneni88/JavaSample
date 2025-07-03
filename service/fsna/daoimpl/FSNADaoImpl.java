/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 15, 2017- 3:18:47 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.fsna.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CpsFsna;
import us.tx.state.dfps.common.domain.CpsFsnaDomainLookup;
import us.tx.state.dfps.common.domain.CpsFsnaPrtyStrngthNeed;
import us.tx.state.dfps.common.domain.CpsFsnaRspn;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaDomainLookupDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaPrtyStrngthNeedDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaRspnDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.SdmFsnaReq;
import us.tx.state.dfps.service.common.response.FSNAAssessmentDtlGetRes;
import us.tx.state.dfps.service.fsna.dao.FSNADao;
import us.tx.state.dfps.service.workload.dto.EventDto;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: May 3, 2018 - 12:57:59 PM
 */
@Service
public class FSNADaoImpl implements FSNADao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FSNADaoImpl.queryFsna}")
	private transient String queryFsnaSql;

	@Value("${FSNADaoImpl.queryDmnLook}")
	private transient String queryDmnLookSql;

	@Value("${FSNADaoImpl.getAllAssmnts}")
	private transient String getAllAssmntsSql;
	
	@Value("${FSNADaoImpl.getLatestAssmntsForPrimCaregiver}")
	private transient String getLatestAssmntsForPrimCaregiverSql;

	@Value("${FSNADaoImpl.getPersonFSNA}")
	private transient String getPersonFSNA;

	@Value("${FSNADaoImpl.getAprvInitialFsna}")
	private transient String getAprvInitialFsnaSql;

	public static final String LIST_INDEX_START = "<li>";
	public static final String LIST_INDEX_END = "</li>";
	public static final String PATTERN_CHECK = "; and";
	public static final String NO_NEED_IDENTIFIED = "NOND";
	public static final String STRENGTH = "STRN";
	public static final String NEED = "NEED";

	/**
	 * This method is to load Blank FSNA assessment record Method Description:
	 * 
	 * @param getFSNAAssessmentDtlReq
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsFsnaDomainLookupDto> queryFsna(String cdStage) {

		Long nbrVersion = getLatestVersion();

		List<CpsFsnaDomainLookupDto> domainDtos = (List<CpsFsnaDomainLookupDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(queryFsnaSql).setParameter("nbrVersion", nbrVersion)
				.setParameter("cdStage", cdStage)).addScalar("idCpsFsnaDomainLookup", StandardBasicTypes.LONG)
						.addScalar("cdFsnaDmn").addScalar("cdFSNADomainType").addScalar("cdSection")
						.addScalar("nmDefntn").addScalar("txtDmnDisplayOrder")
						.addScalar("nbrVersion", StandardBasicTypes.LONG)
						.addScalar("nbrSortOrder", StandardBasicTypes.INTEGER)
						.setResultTransformer(Transformers.aliasToBean(CpsFsnaDomainLookupDto.class)).list();

		if (!CollectionUtils.isEmpty(domainDtos)) {
			domainDtos.forEach(domaindto -> {
				domaindto.setCdFsnaDmn(domaindto.getCdFsnaDmn());
				String nmDefinition = ServiceConstants.EMPTY_STRING;

				// When the stage is FSU, caregiver is the parent. Replace
				// occurrences of caregiver with Parent
				if (!StringUtils.isEmpty(cdStage) && ServiceConstants.FAMILY_SUB_STAGE.equalsIgnoreCase(cdStage)) {
					nmDefinition = domaindto.getNmDefntn().replaceAll("caregiver", "parent");
				} else {
					nmDefinition = domaindto.getNmDefntn();
				}

				String[] nmArray = nmDefinition.split("\\; and");
				StringBuilder nmDefntn = new StringBuilder();
				if (nmArray.length > 0) {
					if (domaindto.getNmDefntn().contains(PATTERN_CHECK))
						nmDefntn.append(LIST_INDEX_START).append(nmArray[0]).append(PATTERN_CHECK)
								.append(LIST_INDEX_END);
					else
						nmDefntn = nmDefntn.append(LIST_INDEX_START).append(nmArray[0]).append(LIST_INDEX_END);
				}
				if (nmArray.length > 1)
					nmDefntn = nmDefntn.append(LIST_INDEX_START).append(nmArray[1]).append(LIST_INDEX_END);
				domaindto.setNmDefntn(nmDefntn.toString());

			});
		}
		return domainDtos;
	}

	/**
	 * 
	 * Method Description: this method is to get latest assessment version
	 * 
	 * @return
	 */
	private Long getLatestVersion() {
		return (Long) sessionFactory.getCurrentSession().createCriteria(CpsFsnaDomainLookup.class)
				.setProjection(Projections.projectionList().add(Projections.max("nbrVersion"))).uniqueResult();
	}

	/**
	 * This method is to load existing FSNA assessment Method Description:
	 * 
	 * @param getFSNAAssessmentDtlReq
	 * @return
	 */
	@Override

	public CpsFsna getFSNAAsmt(Long idEvent) {
		CpsFsna cpsFsna = (CpsFsna) sessionFactory.getCurrentSession().createCriteria(CpsFsna.class)

				.add(Restrictions.eq("event.idEvent", idEvent)).uniqueResult();
		return cpsFsna;
	}

	/**
	 * 
	 * Method Name: getFSNAAsmtById Method Description: This method loads cps
	 * fsna using id.
	 * 
	 * @param idCpsFsna
	 * @return
	 */
	@Override
	public CpsFsna getFSNAAsmtById(Long idCpsFsna) {
		CpsFsna cpsFsna = (CpsFsna) sessionFactory.getCurrentSession().load(CpsFsna.class, idCpsFsna);
		return cpsFsna;
	}

	/**
	 * this method is to save or update CpsFsna entity with CpsFsnaRspn and
	 * CpsFsnaPrtyStrngthNeed if exists
	 */
	@Override
	public Long saveOrUpdateCpsFsna(SdmFsnaReq sdmFsnaReq) {

		CpsFsna cpsFsna = null;
		CpsFsnaDto cpsFsnaDto = sdmFsnaReq.getCpsFsnaDto();
		boolean indUpdateEvent = false;
		// if existing assessment get based on id_event
		if (!ObjectUtils.isEmpty(cpsFsnaDto.getIdCpsFsna())) {
			cpsFsna = getFSNAAsmt(cpsFsnaDto.getIdEvent());
			indUpdateEvent = true;
		} else {
			cpsFsna = new CpsFsna();
			Event event = new Event();
			event.setIdEvent(cpsFsnaDto.getIdEvent());
			cpsFsna.setEvent(event);
			cpsFsna.setDtCreated(new Date());
			cpsFsnaDto.setIdCreatedPerson(sdmFsnaReq.getUserProfile().getIdUser());
			cpsFsna.setIdCreatedPerson(sdmFsnaReq.getUserProfile().getIdUser());
		}
		BeanUtils.copyProperties(cpsFsnaDto, cpsFsna,"dtCreated");
		cpsFsna.setDtLastUpdate(new Date());
		cpsFsna.setIdLastUpdatePerson(sdmFsnaReq.getUserProfile().getIdUser());
		// when exception previous responses have to be cleared
		if (!ObjectUtils.isEmpty(cpsFsnaDto.getIndExcptnExists())
				&& ServiceConstants.STRING_IND_Y.equals(cpsFsnaDto.getIndExcptnExists()))
			cpsFsna.getResponses().clear();
		else {
			List<CpsFsnaRspnDto> parenWithChildRspns = new ArrayList<>();
			// Collecting all parent or caregiver responses from request
			sdmFsnaReq.getCareGiverSectionsMap().entrySet().stream().forEach(domain -> {
				if (!ObjectUtils.isEmpty(domain.getValue())) {
					domain.getValue().stream().forEach(respDtos -> {
						if (!ObjectUtils.isEmpty(respDtos.getFsnaRspns())) {
							respDtos.getFsnaRspns().stream().forEach(rspn -> parenWithChildRspns.add(rspn));
						}
					});
				}
			});
			// Collecting all child responses from request
			sdmFsnaReq.getChildSectionsMap().entrySet().stream().forEach(domain -> {
				if (!ObjectUtils.isEmpty(domain.getValue())) {
					domain.getValue().stream().forEach(respDtos -> {
						if (!ObjectUtils.isEmpty(respDtos.getFsnaRspns())) {
							respDtos.getFsnaRspns().stream().forEach(rspn -> parenWithChildRspns.add(rspn));
						}
					});
				}
			});

			// Check the if it is new response or existing response
			Set<CpsFsnaRspn> deleteList = new HashSet<>();
			List<CpsFsnaPrtyStrngthNeedDto> strengthNeedsTempList = new ArrayList<>();
			for (CpsFsnaRspn rspnEntity : cpsFsna.getResponses()) {
				CpsFsnaRspnDto rspnDto = null;
				// retrieve the corresponding dto from request for the matching
				// idCpsFsnaRspns
				if (!ObjectUtils.isEmpty(parenWithChildRspns)) {
					rspnDto = parenWithChildRspns.stream()
							.filter(o -> !ObjectUtils.isEmpty(o.getIdCpsFsnaRspns())
									&& o.getIdCpsFsnaRspns().equals(rspnEntity.getIdCpsFsnaRspns()))
							.findFirst().orElse(new CpsFsnaRspnDto());
				}
				// if the corresponding dto is null that means could have been
				// deleted, collecting the responses to be deleted
				if (ObjectUtils.isEmpty(rspnDto.getIdCpsFsnaRspns())) {
					deleteList.add(rspnEntity);
				} else {
					BeanUtils.copyProperties(rspnDto, rspnEntity);
					rspnEntity.setIdLastUpdatePerson(sdmFsnaReq.getUserProfile().getIdUser());
					rspnEntity.setDtLastUpdate(new Date());
					// create CpsFsnaPrtyStrngthNeed only if FPR stage and the
					// cdAnswer is STRN or NEED
					if (CodesConstant.CSTAGES_FPR.equals(cpsFsnaDto.getCdStage())) {
						if (ObjectUtils.isEmpty(rspnDto.getCdAnswr())
								|| NO_NEED_IDENTIFIED.equals(rspnDto.getCdAnswr())) {
							rspnEntity.setCpsFsnaPrtyStrngthNeed(null);
						} else {
							// create CpsFsnaPrtyStrngthNeed if the
							// corresponding
							// does not exists(domains changed)
							CpsFsnaPrtyStrngthNeed strengthNeed = null;
							if (ObjectUtils.isEmpty(rspnEntity.getCpsFsnaPrtyStrngthNeed())) {
								strengthNeed = new CpsFsnaPrtyStrngthNeed();
								strengthNeed.setIdCreatedPerson(sdmFsnaReq.getUserProfile().getIdUser());
								strengthNeed.setDtCreated(new Date());
							} else {
								strengthNeed = rspnEntity.getCpsFsnaPrtyStrngthNeed();
							}
							CpsFsnaPrtyStrngthNeedDto strenghtNeedsDto;
							if (!ObjectUtils.isEmpty(sdmFsnaReq.getStregthsNeedsMap())) {
								strengthNeedsTempList = sdmFsnaReq.getStregthsNeedsMap().get(rspnEntity.getIdRelPrsn());
								if (!CollectionUtils.isEmpty(strengthNeedsTempList)) {
									strenghtNeedsDto = strengthNeedsTempList.stream()
											.filter(sn -> !ObjectUtils.isEmpty(sn.getIdCpsFsnaDomainLookup())
													&& sn.getIdCpsFsnaDomainLookup()
															.equals(rspnEntity.getIdCpsFsnaDomainLookup()))
											.findFirst().orElse(new CpsFsnaPrtyStrngthNeedDto());
								} else {
									strenghtNeedsDto = new CpsFsnaPrtyStrngthNeedDto();
								}
							} else {
								strenghtNeedsDto = new CpsFsnaPrtyStrngthNeedDto();
							}
							// BeanUtils.copyProperties(strenghtNeedsDto,
							// strengthNeed);
							/*
							 * If the Strength/Need Record was already present
							 * in the database for the response and if we have
							 * reached this piece of code, then it only means
							 * that the answer has been changed from need to
							 * strength or vice-versa. So the only updates
							 * needed are the answers are to be replaced from
							 * the screen
							 */
							strengthNeed.setIndInCurrPlan(strenghtNeedsDto.getIndInCurrPlan());
							strengthNeed.setIndCmntyRsrc(strenghtNeedsDto.getIndCmntyRsrc());
							strengthNeed.setIndInSbsqntPlan(strenghtNeedsDto.getIndInSbsqntPlan());
							strengthNeed.setIdLastUpdatePerson(sdmFsnaReq.getUserProfile().getIdUser());
							strengthNeed.setDtLastUpdate(new Date());
							strengthNeed.setCpsFsnaRspns(rspnEntity);
							rspnEntity.setCpsFsnaPrtyStrngthNeed(strengthNeed);
						}
					}
				}

			} // Responses that needs to be added
			cpsFsna.getResponses().removeAll(deleteList);
			CpsFsnaRspn rspnEntity = null;
			if (!ObjectUtils.isEmpty(parenWithChildRspns)) {
				Date lastUpdatedDate = new Date();
				List<CpsFsnaRspnDto> addList = parenWithChildRspns.stream()
						.filter(o -> !ObjectUtils.isEmpty(o.getIdCpsFsnaDomainLookup())
								&& ObjectUtils.isEmpty(o.getIdCpsFsnaRspns()))
						.collect(Collectors.toList());
				for (CpsFsnaRspnDto dto : addList) {
					rspnEntity = new CpsFsnaRspn();
					BeanUtils.copyProperties(dto, rspnEntity);
					rspnEntity.setDtCreated(new Date());
					rspnEntity.setIdCreatedPerson(sdmFsnaReq.getUserProfile().getIdUser());
					rspnEntity.setIdLastUpdatePerson(sdmFsnaReq.getUserProfile().getIdUser());
					rspnEntity.setDtLastUpdate(new Date());
					rspnEntity.setCpsFsna(cpsFsna);
					if ((CodesConstant.CSTAGES_FPR.equals(cpsFsnaDto.getCdStage()))
							&& (!ObjectUtils.isEmpty(dto.getCdAnswr())
									&& (STRENGTH.equals(dto.getCdAnswr()) || NEED.equals(dto.getCdAnswr())))) {
						CpsFsnaPrtyStrngthNeed strengthNeed = new CpsFsnaPrtyStrngthNeed();
						Long idRelPerson = rspnEntity.getIdRelPrsn();
						//artf132865: Date and Id Created values should be set on strengthNeed not on Dto object 
						if (!ObjectUtils.isEmpty(sdmFsnaReq.getStregthsNeedsMap())
								&& !ObjectUtils.isEmpty(sdmFsnaReq.getStregthsNeedsMap().get(idRelPerson))) {
							BeanUtils.copyProperties(dto, strengthNeed);
						} else {
							BeanUtils.copyProperties(dto, strengthNeed);
							strengthNeed.setIdCreatedPerson(sdmFsnaReq.getUserProfile().getIdUser());
							strengthNeed.setDtCreated(lastUpdatedDate);
						}
						strengthNeed.setIdLastUpdatePerson(sdmFsnaReq.getUserProfile().getIdUser());
						strengthNeed.setDtLastUpdate(lastUpdatedDate);
						strengthNeed.setCpsFsnaRspns(rspnEntity);
						rspnEntity.setCpsFsnaPrtyStrngthNeed(strengthNeed);
					}
					cpsFsna.getResponses().add(rspnEntity);
				}
			}
		}
		if (ObjectUtils.isEmpty(cpsFsna.getIdCpsFsna()))
			sessionFactory.getCurrentSession().save(cpsFsna);
		else
			sessionFactory.getCurrentSession().saveOrUpdate(cpsFsna);
		
		if (indUpdateEvent && !sdmFsnaReq.getIsApprovalMode()) {
			String cdEventStatus = ServiceConstants.CEVTSTAT_PROC;
			if (sdmFsnaReq.getIsApprovalMode()) {
				cdEventStatus = ServiceConstants.CEVTSTAT_PEND;
			}
			updateEvent(cpsFsnaDto.getIdEvent(), sdmFsnaReq.getUserProfile().getIdUser(),
					cdEventStatus);
		} 
		return cpsFsna.getIdCpsFsna();
	}

	/**
	 * Delete the event record with all is child records
	 */
	@Override
	public FSNAAssessmentDtlGetRes deleteSdmFsna(Long idEvent) {
		Event eventtoDelete = (Event) sessionFactory.getCurrentSession().load(Event.class, idEvent);
		sessionFactory.getCurrentSession().delete(eventtoDelete);
		return new FSNAAssessmentDtlGetRes();
	}

	/**
	 * getDominLookUp for the given idCpsFsnaDomainLookup and cdStage
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsFsnaDomainLookupDto> getDominLookUp(String cdStage) {
		List<CpsFsnaDomainLookupDto> domainDtos = (List<CpsFsnaDomainLookupDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(queryDmnLookSql).setParameter("nbrVersion", getLatestVersion())
				.setParameter("cdStage", cdStage)).addScalar("idCpsFsnaDomainLookup", StandardBasicTypes.LONG)
						.addScalar("cdFsnaDmn").addScalar("cdFSNADomainType").addScalar("cdSection")
						.addScalar("nmDefntn").addScalar("txtDmnDisplayOrder")
						.addScalar("nbrVersion", StandardBasicTypes.LONG)
						.addScalar("nbrSortOrder", StandardBasicTypes.INTEGER)
						.setResultTransformer(Transformers.aliasToBean(CpsFsnaDomainLookupDto.class)).list();
		return domainDtos;
	}

	/**
	 * Complete the assessment by setting dtAsgnmntCmpltd to system date
	 */
	@Override
	public CpsFsnaDto completeAssessment(CpsFsnaDto cpsFsnaDto, UserProfileDto userProfileDB) {
		updateEvent(cpsFsnaDto.getIdEvent(), userProfileDB.getIdUser(), ServiceConstants.CEVTSTAT_COMP);
		CpsFsna cpsFsna = (CpsFsna) sessionFactory.getCurrentSession().createCriteria(CpsFsna.class)
				.add(Restrictions.eq("event.idEvent", cpsFsnaDto.getIdEvent())).uniqueResult();
		cpsFsna.setDtAsgnmntCmpltd(new Date());
		cpsFsna.setIdLastUpdatePerson(userProfileDB.getIdUser());
		cpsFsna.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().update(cpsFsna);
		cpsFsnaDto.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
		return cpsFsnaDto;
	}

	/**
	 * 
	 * Method Description: update the event status to COMP
	 * 
	 * @param cpsFsnaDto
	 * @param idUser
	 * @return
	 */
	private Long updateEvent(Long idEvent, Long idUser, String cdEventStatus) {
		Event updateEvent = (Event) sessionFactory.getCurrentSession().get(Event.class, idEvent);
		updateEvent.setDtLastUpdate(new Date());
		Person person = new Person();
		person.setIdPerson(idUser);
		updateEvent.setPerson(person);
		updateEvent.setCdEventStatus(cdEventStatus);
		sessionFactory.getCurrentSession().update(sessionFactory.getCurrentSession().merge(updateEvent));
		return updateEvent.getIdEvent();
	}

	/**
	 * Load all the CPS FSNA assessments for the given stage
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsFsnaDto> getAllAssmnts(Long idStage) {
		List<CpsFsnaDto> assessments = (List<CpsFsnaDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getAllAssmntsSql).setParameter("idStage", idStage))
						.addScalar("idCpsFsna", StandardBasicTypes.LONG).addScalar("cdAsgnmntType")
						.addScalar("cdExcptnRsn").addScalar("cdFposReqrdRsn")
						.addScalar("dtAsgnmntCmpltd", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtOfAsgnmnt", StandardBasicTypes.TIMESTAMP)
						.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
						.addScalar("idPrmryCrgvrPrnt", StandardBasicTypes.LONG)
						.addScalar("idSecndryCrgvrPrnt", StandardBasicTypes.LONG)
						.addScalar("idStage", StandardBasicTypes.LONG).addScalar("indExcptnExists")
						.addScalar("indFposReqrd").addScalar("txtDngrWorry").addScalar("txtGoalStatmnts")
						.addScalar("txtOtherExcptnRsn").addScalar("txtOtherFposRsn").addScalar("cdEventStatus")
						.setResultTransformer(Transformers.aliasToBean(CpsFsnaDto.class)).list();

		return assessments;
	}
	
	//Method written for defect 16193
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsFsnaDto> getLatestAssmntsForPrimCaregiver(Long idStage) {
		List<CpsFsnaDto> assessments = (List<CpsFsnaDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getLatestAssmntsForPrimCaregiverSql).setParameter("idStage", idStage))
						.addScalar("idCpsFsna", StandardBasicTypes.LONG).addScalar("cdAsgnmntType")
						.addScalar("cdExcptnRsn").addScalar("cdFposReqrdRsn")
						.addScalar("dtAsgnmntCmpltd", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtOfAsgnmnt", StandardBasicTypes.TIMESTAMP)
						.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
						.addScalar("idPrmryCrgvrPrnt", StandardBasicTypes.LONG)
						.addScalar("idSecndryCrgvrPrnt", StandardBasicTypes.LONG)
						.addScalar("idStage", StandardBasicTypes.LONG).addScalar("indExcptnExists")
						.addScalar("indFposReqrd").addScalar("txtDngrWorry").addScalar("txtGoalStatmnts")
						.addScalar("txtOtherExcptnRsn").addScalar("txtOtherFposRsn").addScalar("cdEventStatus")
						.setResultTransformer(Transformers.aliasToBean(CpsFsnaDto.class)).list();

		return assessments;
	}

	@Override
	public CpsFsnaDto getPersonFSNA(Long idPerson, Long idStage) {
		List<CpsFsnaDto>cpsFsnaDtoList =  ((SQLQuery) sessionFactory.getCurrentSession()
			.createSQLQuery(getPersonFSNA).setParameter("idPerson", idPerson)
			.setParameter("idStage", idStage))
			.addScalar("idCpsFsna", StandardBasicTypes.LONG)
			.addScalar("idPrmryCrgvrPrnt", StandardBasicTypes.LONG)
			.addScalar("idSecndryCrgvrPrnt", StandardBasicTypes.LONG)
			.setResultTransformer(Transformers.aliasToBean(CpsFsnaDto.class)).list();

		if(!ObjectUtils.isEmpty(cpsFsnaDtoList))
			return cpsFsnaDtoList.get(0);
		return null;
	}

	/**
	 * this method is to get previous TxtDngrWorry
	 */
	@Override
	public CpsFsnaDto getPerviousStatements(Long idStage) {
		CpsFsna cpsFsna = (CpsFsna) sessionFactory.getCurrentSession().createCriteria(CpsFsna.class)
				.add(Restrictions.eq("idStage", idStage)).addOrder(Order.desc("dtOfAsgnmnt")).setMaxResults(1)
				.uniqueResult();
		CpsFsnaDto cpsFsnaDto = new CpsFsnaDto();
		if (!ObjectUtils.isEmpty(cpsFsna)) {
			cpsFsnaDto.setPreviousTxtDngrWorry(cpsFsna.getTxtDngrWorry());
			cpsFsnaDto.setPrevioustxtGoalStatmnts(cpsFsna.getTxtGoalStatmnts());
			cpsFsnaDto.setIdCpsFsna(cpsFsna.getIdCpsFsna());
		}
		return cpsFsnaDto;
	}

	/**
	 * Method Description: This method is to get previous Danger/Worry and Goal
	 * Statement Gets previous value from same stage which is not current
	 * assessment
	 * 
	 * @param cpsFsnaDto
	 * @param idUser
	 * @return cpsFsnaDto
	 */
	@Override
	public CpsFsnaDto getPerviousStatements(Long idStage, CpsFsna cpsFsnaInput) {
		Criterion matchPrimSecn = Restrictions.or(
				Restrictions.eq("idPrmryCrgvrPrnt", cpsFsnaInput.getIdPrmryCrgvrPrnt()),
				Restrictions.eq("idSecndryCrgvrPrnt",
						ObjectUtils.isEmpty(cpsFsnaInput.getIdSecndryCrgvrPrnt()) ? ServiceConstants.ZERO
								: cpsFsnaInput.getIdSecndryCrgvrPrnt()));
		CpsFsna cpsFsna = (CpsFsna) sessionFactory.getCurrentSession().createCriteria(CpsFsna.class)
				.add(Restrictions.and(Restrictions.eq("idStage", idStage),
						Restrictions.ne("idCpsFsna", cpsFsnaInput.getIdCpsFsna()), matchPrimSecn))
				.addOrder(Order.desc("dtOfAsgnmnt")).setMaxResults(1).uniqueResult();
		CpsFsnaDto cpsFsnaDto = new CpsFsnaDto();
		if (!ObjectUtils.isEmpty(cpsFsna)) {
			cpsFsnaDto.setPreviousTxtDngrWorry(cpsFsna.getTxtDngrWorry());
			cpsFsnaDto.setPrevioustxtGoalStatmnts(cpsFsna.getTxtGoalStatmnts());
			cpsFsnaDto.setIdCpsFsna(cpsFsna.getIdCpsFsna());
		}
		return cpsFsnaDto;
	}

	/**
	 * Method Name: getMostRecentAprvInitialFsna Method Description:This method
	 * is used to retrieve the most recent approved FSNA event.
	 * 
	 * @param idStage
	 * @return Long
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long getMostRecentAprvInitialFsna(Long idStage) {
		Long idEvent = null;
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAprvInitialFsnaSql)
				.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(EventDto.class));
		List<EventDto> idEventList = query.list();
		if (!CollectionUtils.isEmpty(idEventList)) {
			idEvent = idEventList.get(0).getIdEvent();
		}

		return idEvent;
	}

}
