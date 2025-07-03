package us.tx.state.dfps.service.legalnotice.daoimpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.LegalNoticeChildLink;
import us.tx.state.dfps.common.domain.LegalNoticeDtl;
import us.tx.state.dfps.common.domain.LegalNoticeRecpnt;
import us.tx.state.dfps.common.domain.LegalStatus;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.legal.dto.LegalNoticeDtlDto;
import us.tx.state.dfps.service.legal.dto.LegalNoticeRecpntDto;
import us.tx.state.dfps.service.legalnotice.dao.LegalNoticeDao;
import us.tx.state.dfps.service.person.dao.PersonAddressDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonEmailDao;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.PersonEmailValueDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Legal
 * Notice Dao Impl layer> June 07, 2018- 1:07:08 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class LegalNoticeDaoImpl implements LegalNoticeDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	PersonDao personDao;

	@Autowired
	PersonAddressDao personAddressDao;

	@Autowired
	PersonEmailDao personEmailDao;

	@Value("${LegalNoticeDaoImpl.getNoticesForStage}")
	private String getNoticesForStageSql;

	@Value("${LegalNoticeDaoImpl.getMostRecentLegalActionDtl}")
	private String getLegalActionDtlSql;

	@Value("${LegalNoticeDaoImpl.getMostRecentLegalStatusDtl}")
	private String getLegalStatusDtlSql;

	@Value("${LegalNoticeDaoImpl.getChildrenListOfSameCauseNumber}")
	private String getChildrenListSql;

	@Value("${LegalNoticeDaoImpl.checkIfMoreThanOneCauseExists}")
	private String checkIfMoreThanOneCauseExistsSql;

	private static final Logger log = Logger.getLogger(LegalNoticeDaoImpl.class);

	/**
	 * 
	 * Method Name: getLegalNoticeList Method Description: This method to get
	 * legal notice list
	 * 
	 * @param idCase
	 * @param idStage
	 * @return
	 */
	@Override
	public List<LegalNoticeDtlDto> getLegalNoticeList(Long idCase, Long idStage) {
		List<Long> idLegalStatus = getLglStsForCaseSameCause(idCase, idStage);
		List<LegalNoticeDtlDto> noticeDtos = new ArrayList<>();

		// fetch and set current legal status event ids and child list having
		// same cause number

		if (!ObjectUtils.isEmpty(idLegalStatus) && idLegalStatus.size() > 0) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LegalNoticeDtl.class)
					.add(Restrictions.in("legalStatus.idLegalStatEvent", idLegalStatus));
			List<LegalNoticeDtl> notices = (List<LegalNoticeDtl>) criteria.list();
			notices.forEach(notice -> {
				LegalNoticeDtlDto dto = new LegalNoticeDtlDto();

				BeanUtils.copyProperties(notice, dto);
				if (!ObjectUtils.isEmpty(notice.getChildren())) {
					Map<Long, String> childrenMap = new HashMap<>();
					List<String> childList = new ArrayList<>();
					List personList = new ArrayList<>();
					notice.getChildren().forEach(child -> {
						Person p = child.getPerson();

						childrenMap.put(p.getIdPerson(), p.getNmPersonFull());
						childList.add(p.getNmPersonFull());
						personList.add(p.getIdPerson());

					});
					dto.setChildrenMap(childrenMap);
					dto.setChildrenList(childList);
					dto.setChildPersonIds(personList);

					if (!ObjectUtils.isEmpty(notice.getDtCourtSchedle())) {
						dto.setCourtScheduledTime(DateUtils.getTime(notice.getDtCourtSchedle()));
					}
				}
				BeanUtils.copyProperties(notice, dto);

				if (!ObjectUtils.isEmpty(notice.getRecipients())) {
					List<LegalNoticeRecpntDto> recipientDtos = new ArrayList<>();
					notice.getRecipients().forEach(recipient -> {
						LegalNoticeRecpntDto recipientDto = new LegalNoticeRecpntDto();
						BeanUtils.copyProperties(recipient, recipientDto);
						AddressDto a = personAddressDao
								.fetchCurrentPrimaryAddress(recipient.getPerson());
						if(!TypeConvUtil.isNullOrEmpty(a)) {
							recipientDto.setIdPerson(a.getIdPerson());
							recipientDto.setPersonFull(a.getNmPersonFull());
							recipientDto.setPersAddrStLn1(a.getAddrPersAddrStLn1());
							recipientDto.setPersAddrStLn2(a.getAddrPersAddrStLn2());
							recipientDto.setPersonAddrCity(a.getAddrCity());
							recipientDto.setPersonAddrZip(a.getAddrZip());
							recipientDto.setCdPersonAddrState(a.getCdAddrState());
						}else{
							PersonDto p = personDao.getPersonById(recipient.getPerson());
							if(!TypeConvUtil.isNullOrEmpty(p)) {
								recipientDto.setIdPerson(p.getIdPerson());
								recipientDto.setPersonFull(p.getNmPersonFull());
								recipientDto.setPersAddrStLn1(p.getAddrPersonStLn1());
								recipientDto.setPersAddrStLn2(p.getAddrPersonStLn2());
								recipientDto.setPersonAddrCity(p.getAddrPersonCity());
								recipientDto.setPersonAddrZip(p.getAddrPersonZip());
								recipientDto.setCdPersonAddrState(p.getCdPersonState());
							}
						}
						PersonEmailValueDto personemail = personDao.getPersonPrimaryEmail(recipient.getPerson());

						if (!ObjectUtils.isEmpty(personemail) && !ObjectUtils.isEmpty(personemail.getTxtEmail())) {
							recipientDto.setPersonEmail(personemail.getTxtEmail());
						}
						recipientDto.setTxtAdditionalMsg(recipient.getTxtAdditionalMsg());
						recipientDtos.add(recipientDto);
					});
					dto.setRecipients(recipientDtos);

				}
				if (!ObjectUtils.isEmpty(notice.getLegalStatus())) {
					LegalStatus ls = notice.getLegalStatus();
					dto.setLegalStatusCauseNum(ls.getTxtLegalStatCauseNbr());
					dto.setLegalStatusCourtNbr(ls.getTxtLegalStatCourtNbr());
					dto.setCdLegalCounty(ls.getCdLegalStatCnty());
				}
				if (!ObjectUtils.isEmpty(dto.getIdCreatedPerson()))
					dto.setNmCreatedPerson(personDao.getPersonById(dto.getIdCreatedPerson()).getNmPersonFull());
				noticeDtos.add(dto);
			});
		}

		return noticeDtos;
	}

	/**
	 * 
	 * Method Name: getLglStsForCaseSameCause Method Description: This method to
	 * get idLegalNoticeDtl by idCase
	 * 
	 * @param idCase
	 * @param idStage
	 * @return
	 */
	@Override
	public List<Long> getLglStsForCaseSameCause(Long idCase, Long idStage) {
		List<Long> idLegalNotices = (List<Long>) (sessionFactory.getCurrentSession()
				.createSQLQuery(getNoticesForStageSql).addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.setParameter("idCase", idCase).setParameter("idStage", idStage).list());

		return idLegalNotices;

	}

	/**
	 * 
	 * Method Name: saveLegalNoticeRecpnt Method Description: This method is
	 * used to save LegalNoticeRecpnt record
	 * 
	 * @param LegalNoticeRecpntDto
	 * @param idLastUpdatedBy
	 * @return
	 */
	@Override
	public ServiceResHeaderDto saveLegalNoticeRecpnt(LegalNoticeRecpntDto recepnt, Long idLastUpdatedBy) {

		LegalNoticeRecpnt legalNoticeRecpnt = (LegalNoticeRecpnt) sessionFactory.getCurrentSession()
				.createCriteria(LegalNoticeRecpnt.class)
				.add(Restrictions.eq("idLegalNoticeRecpnt", recepnt.getIdLegalNoticeRecpnt())).uniqueResult();

		legalNoticeRecpnt.setDtCreated(recepnt.getDtCreated());
		legalNoticeRecpnt.setCdNoticeStatus(recepnt.getCdNoticeStatus());
		legalNoticeRecpnt.setDtLastUpdate(new Date());
		legalNoticeRecpnt.setIdLastUpdatePerson(idLastUpdatedBy);
		sessionFactory.getCurrentSession().saveOrUpdate(legalNoticeRecpnt);
		return new ServiceResHeaderDto();
	}

	/**
	 * 
	 * Method Name: fetchLegalNoticeDtl Method Description: This method will
	 * fetch legal notice detail
	 * 
	 * @param idCase
	 * @param idStage
	 * @return LegalNoticeDtlDto
	 */
	public LegalNoticeDtlDto fetchLegalNoticeDtl(Long idCase, Long idStage) {
		LegalNoticeDtlDto legalNoticeDtlDto = new LegalNoticeDtlDto();

		/*
		 * 1. Fetching legal action record details INLCUDING COURT DATE AND TIME
		 * for Notice detail page When user clicks on Add Notice Button on Legal
		 * Notice List page
		 */
		legalNoticeDtlDto = getLegalActionDtl(idCase, idStage);

		/*
		 * 2. Fetching most recent legal status record details for notice detail
		 * page When user clicks on Add Notice Button on Legal Notice List page
		 */
		legalNoticeDtlDto = getLegalStatusDtl(idCase, idStage, legalNoticeDtlDto);

		/*
		 * 3. For current case id, check how many children are associated with
		 * same cause number
		 */
		legalNoticeDtlDto = getChildrenList(idCase, idStage, legalNoticeDtlDto);

		/* 4. Fetch current sub stage child cause number */
		legalNoticeDtlDto = checkIfMoreThanOneCauseExists(idCase, legalNoticeDtlDto);

		return legalNoticeDtlDto;
	}

	/**
	 * 
	 * Method Name: saveLegalNoticeDtl Method Description: This method will save
	 * legal notice detail
	 * 
	 * @param LegalNoticeDtlDto
	 * @param idUser
	 * @param generateNotice
	 * @return ServiceResHeaderDto
	 */
	public ServiceResHeaderDto saveLegalNoticeDtl(LegalNoticeDtlDto legalNoticeDtlDto, Long idUser,
			Boolean generateNotice){

		if (null != legalNoticeDtlDto) {
			// set values in LGL NTC DTL entity
			// Insert legal notice record for each child of children list who
			// have same legal status cause number
			LegalNoticeDtl legalNoticeDtl;
			if (ObjectUtils.isEmpty(legalNoticeDtlDto.getIdLegalNoticeDtl())) {
				legalNoticeDtl = new LegalNoticeDtl();
			} else {
				legalNoticeDtl = (LegalNoticeDtl) sessionFactory.getCurrentSession().get(LegalNoticeDtl.class,
						legalNoticeDtlDto.getIdLegalNoticeDtl());
			}

			// Copy dto values to entity
			BeanUtils.copyProperties(legalNoticeDtlDto, legalNoticeDtl);

			if (ObjectUtils.isEmpty(legalNoticeDtlDto.getIdLegalNoticeDtl())) {
				legalNoticeDtl.setDtCreated(new Date());
				legalNoticeDtl.setIdCreatedPerson(idUser);
			}
			legalNoticeDtl.setDtLastUpdate(new Date());
			legalNoticeDtl.setIdLastUpdatePerson(idUser);

			try {
				legalNoticeDtl.setDtCourtSchedle(DateUtils.getTimestamp(legalNoticeDtlDto.getDtCourtSchedle(),
						legalNoticeDtlDto.getCourtScheduledTime()));
			} catch (ParseException e) {
				new DataLayerException(e.getMessage());
			}

			// If save and complete is done then update notice generation field
			if (generateNotice == true) {
				legalNoticeDtl.setDtGenarated(new Date());
			}
			// //Save and insert the legal notice record
			// sessionFactory.getCurrentSession().saveOrUpdate(legalNoticeDtl);

			// Save legal notice recipients detail for each notice
			Set<LegalNoticeRecpnt> recipients;
			if (CollectionUtils.isEmpty(legalNoticeDtl.getRecipients())) {
				recipients = new HashSet<>(0);
			} else {
				recipients = legalNoticeDtl.getRecipients();
				// if present in entity but does not come from screen , should
				// be removed
				Set<LegalNoticeRecpnt> removeRecipients = new HashSet<>(0);

				recipients.stream().forEach(o -> {
					LegalNoticeRecpntDto legalNoticeRecpntDto = legalNoticeDtlDto.getRecipients().stream()
							.filter(p -> o.getPerson().equals(p.getIdPerson())).findFirst()
							.orElse(null);
					if (ObjectUtils.isEmpty(legalNoticeRecpntDto)) {
						removeRecipients.add(o);
					}
				});

				if (!ObjectUtils.isEmpty(removeRecipients)) {
					recipients.removeAll(removeRecipients);
				}

			}

			for (LegalNoticeRecpntDto legalNoticeRecpntDto : legalNoticeDtlDto.getRecipients()) {
				LegalNoticeRecpnt legalNoticeRecpnt;
				if (ObjectUtils.isEmpty(legalNoticeRecpntDto.getIdLegalNoticeRecpnt())) {
					legalNoticeRecpnt = new LegalNoticeRecpnt();
					legalNoticeRecpnt.setCdNoticeStatus(ServiceConstants.CLEGSTAT_030);
					legalNoticeRecpnt.setDtCreated(new Date());
					legalNoticeRecpnt.setIdCreatedPerson(idUser);
				} else {
					legalNoticeRecpnt = recipients.stream().filter(
							o -> o.getIdLegalNoticeRecpnt().equals(legalNoticeRecpntDto.getIdLegalNoticeRecpnt()))
							.findFirst().orElse(new LegalNoticeRecpnt());
				}
				// set legal notice to legalNoticeRecpnt
				legalNoticeRecpnt.setLegalNoticeDtl(legalNoticeDtl);
				legalNoticeRecpnt.setPerson(legalNoticeRecpntDto.getIdPerson());

				legalNoticeRecpnt.setTxtAdditionalMsg(legalNoticeRecpntDto.getTxtAdditionalMsg());

				legalNoticeRecpnt.setDtLastUpdate(new Date());
				legalNoticeRecpnt.setIdLastUpdatePerson(idUser);
				if (ObjectUtils.isEmpty(legalNoticeRecpntDto.getIdLegalNoticeRecpnt())) {
					recipients.add(legalNoticeRecpnt);
				}

			}

			// fetch child list
			List<Long> childList = legalNoticeDtlDto.getChildPersonIds();

			Set<LegalNoticeChildLink> children;
			if (CollectionUtils.isEmpty(legalNoticeDtl.getChildren())) {
				children = new HashSet<>(0);
			} else {
				children = legalNoticeDtl.getChildren();

				Set<LegalNoticeChildLink> removeChildren = new HashSet<>(0);

				children.stream().forEach(o -> {

					if (!childList.contains(o.getPerson().getIdPerson())) {
						removeChildren.add(o);
					}

				});
				if (!ObjectUtils.isEmpty(removeChildren)) {
					recipients.removeAll(removeChildren);
				}
			}
			// insert or update record for notice child link table

			// Map each child of same cause number with respective notice email
			// id
			if (CollectionUtils.isNotEmpty(childList)) {
				for (Long childPersonId : childList) {
					LegalNoticeChildLink legalNoticeChildLink = children.stream()
							.filter(o -> childPersonId.equals(o.getPerson().getIdPerson())).findFirst().orElse(null);

					if (ObjectUtils.isEmpty(legalNoticeChildLink)) {
						legalNoticeChildLink = new LegalNoticeChildLink();
						legalNoticeChildLink.setDtCreated(new Date());
						legalNoticeChildLink.setIdCreatedPerson(idUser);

					}

					// Fetch person detail of each child which has to be
					// associated
					// with notice id
					Person childEntity = new Person();
					childEntity.setIdPerson(childPersonId);

					legalNoticeChildLink.setLegalNoticeDtl(legalNoticeDtl);
					legalNoticeChildLink.setPerson(childEntity);

					legalNoticeChildLink.setDtLastUpdate(new Date());
					legalNoticeChildLink.setIdLastUpdatePerson(idUser);

					if (ObjectUtils.isEmpty(legalNoticeChildLink.getIdLegalNoticeChildLink()))
						children.add(legalNoticeChildLink);

				}
			}
			if (!ObjectUtils.isEmpty(legalNoticeDtlDto.getLegalStatEventId())) {
				LegalStatus ls = new LegalStatus();
				ls.setIdLegalStatEvent(legalNoticeDtlDto.getLegalStatEventId());
				legalNoticeDtl.setLegalStatus(ls);
			}

			if (ObjectUtils.isEmpty(legalNoticeDtlDto.getIdLegalNoticeDtl())) {
				legalNoticeDtl.setChildren(children);
				legalNoticeDtl.setRecipients(recipients);
				sessionFactory.getCurrentSession().save(legalNoticeDtl);
			} else {
				legalNoticeDtl.setChildren(children);
				legalNoticeDtl.setRecipients(recipients);
				sessionFactory.getCurrentSession().saveOrUpdate(legalNoticeDtl);
			}
		}

		return new ServiceResHeaderDto();
	}

	/**
	 * 
	 * Method Name: getLegalActionDtl Method Description: This method will fetch
	 * legal action detail
	 * 
	 * @param idCase
	 * @param idStage
	 * @return LegalNoticeDtlDto
	 */
	@Override
	public LegalNoticeDtlDto getLegalActionDtl(Long idCase, Long idStage) {
		SQLQuery sQLQuery = (SQLQuery) (sessionFactory.getCurrentSession().createSQLQuery(getLegalActionDtlSql)
				.addScalar("cdLegalAction", StandardBasicTypes.STRING)
				.addScalar("cdLegalActionSubtype", StandardBasicTypes.STRING)
				.addScalar("dtCourtSchedle", StandardBasicTypes.DATE).setParameter("idCase", idCase)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(LegalNoticeDtlDto.class)));

		List<LegalNoticeDtlDto> legalNoticeDtlDtos = sQLQuery.list();
		LegalNoticeDtlDto legalNoticeDtlDto = new LegalNoticeDtlDto();

		if (!TypeConvUtil.isNullOrEmpty(legalNoticeDtlDtos)) {
			if (legalNoticeDtlDtos.size() > 0) {

				legalNoticeDtlDto.setCourtScheduledTime((null != legalNoticeDtlDtos.get(0).getDtCourtSchedle())
						? (DateUtils.getTime(legalNoticeDtlDtos.get(0).getDtCourtSchedle())) : "");
				legalNoticeDtlDto.setDtCourtSchedle(legalNoticeDtlDtos.get(0).getDtCourtSchedle());
				legalNoticeDtlDto.setCdLegalAction(legalNoticeDtlDtos.get(0).getCdLegalAction());
				legalNoticeDtlDto.setCdLegalActionSubtype(legalNoticeDtlDtos.get(0).getCdLegalActionSubtype());
			}
		}
		return legalNoticeDtlDto;
	}

	/**
	 * 
	 * Method Name: getLegalStatusDtl Method Description: This method will fetch
	 * legal status detail
	 * 
	 * @param idCase
	 * @param idStage
	 * @return LegalNoticeDtlDto
	 */
	public LegalNoticeDtlDto getLegalStatusDtl(Long idCase, Long idStage, LegalNoticeDtlDto legalNoticeDtlDto) {

		SQLQuery sQLQuery = (SQLQuery) (sessionFactory.getCurrentSession().createSQLQuery(getLegalStatusDtlSql)
				.addScalar("legalStatusCauseNum", StandardBasicTypes.STRING)
				.addScalar("legalStatusCourtNbr", StandardBasicTypes.STRING)
				.addScalar("cdLegalCounty", StandardBasicTypes.STRING)
				.addScalar("legalStatEventId", StandardBasicTypes.LONG)
				.addScalar("currentChildPersonId", StandardBasicTypes.LONG).setParameter("idCase", idCase)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(LegalNoticeDtlDto.class)));

		List<LegalNoticeDtlDto> legalNoticeDtlDtos = sQLQuery.list();
		if (!TypeConvUtil.isNullOrEmpty(legalNoticeDtlDtos)) {
			if (legalNoticeDtlDtos.size() > 0) {
				if (null != legalNoticeDtlDto) {
					legalNoticeDtlDto.setLegalStatusCauseNum(legalNoticeDtlDtos.get(0).getLegalStatusCauseNum());
					legalNoticeDtlDto.setLegalStatusCourtNbr(legalNoticeDtlDtos.get(0).getLegalStatusCourtNbr());
					legalNoticeDtlDto.setCdLegalCounty(legalNoticeDtlDtos.get(0).getCdLegalCounty());
					legalNoticeDtlDto.setLegalStatEventId(legalNoticeDtlDtos.get(0).getLegalStatEventId());
					legalNoticeDtlDto.setCurrentChildPersonId(legalNoticeDtlDtos.get(0).getCurrentChildPersonId());
				} else {
					legalNoticeDtlDto = legalNoticeDtlDtos.get(0);
				}
			}

		}
		return legalNoticeDtlDto;
	}

	/**
	 * 
	 * Method Name: getChildrenList Method Description: This method will fetch
	 * children list of same case number who have same cause numbers
	 * 
	 * @param idCase
	 * @param idStage
	 * @return LegalNoticeDtlDto
	 */
	public LegalNoticeDtlDto getChildrenList(Long idCase, Long idStage, LegalNoticeDtlDto legalNoticeDtlDto) {

		List<Long> personList = new ArrayList<Long>();
		SQLQuery sQLQuery = (SQLQuery) (sessionFactory.getCurrentSession().createSQLQuery(getChildrenListSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idCase", idCase)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(LegalNoticeDtlDto.class)));

		List<LegalNoticeDtlDto> legalNoticeDtlDtos = sQLQuery.list();
		List<String> childList = new ArrayList<String>();
		Map<Long, String> childrenMap = new HashMap<Long, String>();
		if (!TypeConvUtil.isNullOrEmpty(legalNoticeDtlDtos)) {
			if (legalNoticeDtlDtos.size() > 0) {
				if (null != legalNoticeDtlDto) {
					for (LegalNoticeDtlDto legalNoticeDtlDtoFetched : legalNoticeDtlDtos) {
						personList.add(legalNoticeDtlDtoFetched.getIdPerson());
						String personFullName = personDao.getPerson(legalNoticeDtlDtoFetched.getIdPerson())
								.getNmPersonFull();
						childList.add(personFullName);
						childrenMap.put(legalNoticeDtlDtoFetched.getIdPerson(), personFullName);
					}
					legalNoticeDtlDto.setChildPersonIds(personList);
					legalNoticeDtlDto.setChildrenList(childList);
					legalNoticeDtlDto.setChildrenMap(childrenMap);

				} else {
					for (LegalNoticeDtlDto legalNoticeDtlDtoFetched : legalNoticeDtlDtos) {
						personList.add(legalNoticeDtlDtoFetched.getIdPerson());
						childList.add(legalNoticeDtlDtoFetched.getNmStage());
						childrenMap.put(legalNoticeDtlDtoFetched.getIdPerson(), legalNoticeDtlDtoFetched.getNmStage());
					}
					legalNoticeDtlDto.setChildPersonIds(personList);
					legalNoticeDtlDto.setChildrenList(childList);
					legalNoticeDtlDto.setChildrenMap(childrenMap);
				}
			}
		}
		return legalNoticeDtlDto;
	}

	/**
	 * 
	 * Method Name: checkIfMoreThanOneCauseExists Method Description: This
	 * method will fetch current stage cause number
	 * 
	 * @param idCase
	 * @param legalNoticeDtlDto
	 * @return LegalNoticeDtlDto
	 */
	public LegalNoticeDtlDto checkIfMoreThanOneCauseExists(Long idCase, LegalNoticeDtlDto legalNoticeDtlDto) {

		SQLQuery sQLQuery = (SQLQuery) (sessionFactory.getCurrentSession()
				.createSQLQuery(checkIfMoreThanOneCauseExistsSql)
				.addScalar("legalStatusCauseNum", StandardBasicTypes.STRING).setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(LegalNoticeDtlDto.class)));

		List<LegalNoticeDtlDto> legalNoticeDtlDtos = sQLQuery.list();
		if (!TypeConvUtil.isNullOrEmpty(legalNoticeDtlDtos)) {
			if (legalNoticeDtlDtos.size() > 1) {
				if (null != legalNoticeDtlDto) {
					legalNoticeDtlDto.setDiffCauseNumExists(true);
				} else {
					legalNoticeDtlDto.setDiffCauseNumExists(true);
				}
			}

		}
		return legalNoticeDtlDto;
	}

}
