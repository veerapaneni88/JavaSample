package us.tx.state.dfps.service.contacts.daoimpl;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.common.domain.ContactNarrative;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.SimpleChildNameDto;
import us.tx.state.dfps.common.exception.FormsException;
import us.tx.state.dfps.service.casepackage.dto.ContactNarrativeDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.InrFollowupPendingReq;
import us.tx.state.dfps.service.common.request.StageSearchReq;
import us.tx.state.dfps.service.common.response.StageSearchRes;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.contact.dto.ContactListSearchDto;
import us.tx.state.dfps.service.contact.dto.ContactSearchDto;
import us.tx.state.dfps.service.contact.dto.ContactSearchListDto;
import us.tx.state.dfps.service.contact.dto.InrContactFollowUpPendingDto;
import us.tx.state.dfps.service.contacts.dao.ContactGuideDao;
import us.tx.state.dfps.service.contacts.dao.ContactNocPersonDetailsDao;
import us.tx.state.dfps.service.contacts.dao.ContactSearchDao;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.stage.dto.StageSearchDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.contact.bean.ContactNocPersonDetailDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactDetailSearchDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactPurposeDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FindContactDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ServiceOutputDto;



/**
 *
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactSearchDaoImpl Aug 2, 2018- 6:41:29 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class ContactSearchDaoImpl implements ContactSearchDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${Csys04dDaoImpl.searchContactsSql}")
	private String searchContactsSql;

	private static final Logger logger = LogManager.getLogger(ContactSearchDaoImpl.class.getName());
	@Autowired
	private MobileUtil mobileUtil;

	public ContactSearchDaoImpl() {

	}

	@Autowired
	ContactGuideDao contactGuideDao;

	@Autowired
	ContactNocPersonDetailsDao contactNocPersonDetailsDao;

	@Value("${ContactSearchDaoImpl.getFollowupPendingGroupsSql}")
	private String getFollowupPendingGroups;

	@Value("${ContactSearchDaoImpl.getFollowupPendingGroupsStageSuffixSql}")
	private String getFollowupPendingGroupsStageSuffix;

	@Value("${ContactSearchDaoImpl.getFollowupPendingGroupsCaseSuffixSql}")
	private String getFollowupPendingGroupsCaseSuffix;

	@Value("${ContactSearchDaoImpl.getFollowupPendingChildrenSql}")
	private String getFollowupPendingChildren;

  // CANIRSP-465 SD79227: Sensitive Case Handling
  @Value("${ContactSearchDaoImpl.hasSensitiveAccessSql}")
  private String hasSensitiveAccess;

  @Value("${ContactSearchDaoImpl.hasSupervisorialSensitiveAccessSql}")
  private String hasSupervisorialSensitiveAccessSql;

  @SuppressWarnings("unchecked")
	@Override
	public List<ContactSearchListDto> searchContacts(List<Long> personIds, String indPersonPhonePrimary,
													 String cdPersonPhoneType, String cdEventStatus, Long idCase, Date dtScrSearchDateFrom,
													 Date dtScrSearchDateTo, Long uiIdEvent, String cdContactType, String cdContactPurpose,
													 String cdContactMethod, String cdContactLocation, String cdContactOthers, List<Long> stageIds) {
		Criteria cr = getCriteria(cdEventStatus, idCase, dtScrSearchDateFrom, dtScrSearchDateTo, uiIdEvent, cdContactType, cdContactPurpose, cdContactMethod, cdContactLocation, cdContactOthers, stageIds);
		List<ContactSearchListDto> resultList = cr.list();
		List<ContactSearchListDto> result = new ArrayList<>();
		formatSearchResults(personIds, resultList, result);
		return result;
	}
	@Override
	public List<ContactSearchListDto> searchContactsForAPIPagination(List<Long> personIds, String indPersonPhonePrimary,
																	 String cdPersonPhoneType, String cdEventStatus, Long idCase, Date dtScrSearchDateFrom,
																	 Date dtScrSearchDateTo, Long uiIdEvent, String cdContactType, String cdContactPurpose,
																	 String cdContactMethod, String cdContactLocation, String cdContactOthers, List<Long> stageIds,int offset,int pageSize) {
		Criteria cr = getCriteria(cdEventStatus, idCase, dtScrSearchDateFrom, dtScrSearchDateTo, uiIdEvent, cdContactType, cdContactPurpose, cdContactMethod, cdContactLocation, cdContactOthers, stageIds);
		List<ContactSearchListDto> resultList = cr.setFirstResult(offset).setMaxResults(pageSize).list();
		List<ContactSearchListDto> result = new ArrayList<>();
		formatSearchResults(personIds, resultList, result);
		return result;
	}
	@Override
	public Integer getCountOFContactsInStage(Long idStage){
		Query query = sessionFactory.getCurrentSession().createQuery("SELECT COUNT(*) FROM Contact WHERE ID_CONTACT_STAGE = "+idStage);
		return ((Long)query.uniqueResult()).intValue();
	}

	private void formatSearchResults(List<Long> personIds, List<ContactSearchListDto> resultList, List<ContactSearchListDto> result) {
		for (ContactSearchListDto contactSearchListDto : resultList) {
			try {
				int personCount = 0;
				if ((ServiceConstants.CCNTCTYP_BREG.equals(contactSearchListDto.getCdContactType())
						|| ServiceConstants.CCNTCTYP_GREG.equals(contactSearchListDto.getCdContactType()))
						&& (ServiceConstants.CCNTPURP_GFTF.equals(contactSearchListDto.getCdContactPurpose())
						|| ServiceConstants.CCNTPURP_GCMR.equals(contactSearchListDto.getCdContactPurpose()))) {
					contactSearchListDto.setIndStructNarrExists(
							contactGuideDao.checkifGuideNarrExists(contactSearchListDto.getIdEvent()));
				}else {
					contactSearchListDto.setIndStructNarrExists(indStructNarrExists(contactSearchListDto.getIdEvent()));
				}
				if(contactSearchListDto.getNmContact1() == null) {
					//if getNmContact1 is empty get name from ContactNoc table
					List<ContactNocPersonDetailDto> contactNOCResultList = contactNocPersonDetailsDao.getAllNewPersonBasedOnEventId(contactSearchListDto.getIdEvent());
					if(!contactNOCResultList.isEmpty()) {
						contactSearchListDto.setNmContact1(contactNOCResultList.get(0).getNmPersonFull());
					}
				}
				Map<Long, String> personRowList = new HashMap<>();
				List<Long> tempIds = new ArrayList<>();
				if(contactSearchListDto.getNmContact1() == null) {
					Criteria cr2 = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class)
							.createAlias("person", "person").createAlias("event", "event")
							.add(Restrictions.eq("event.idEvent", contactSearchListDto.getIdEvent()))
							.setProjection(
									Projections.projectionList().add(Projections.property("person.idPerson"), "idPerson")
											.add(Projections.property("person.nmPersonFull"), "nmPersonFull")
											.add(Projections.property("person.cdPersonSuffix"), "cdPersonSuffix"))
							.setResultTransformer(Transformers.aliasToBean(PersonDto.class));
					if (null != personIds) {
						if (personIds.size() != 0) {
							cr2.add(Restrictions.in("person.idPerson", personIds));
						}
					}
					List<PersonDto> persons = cr2.list();

					for (PersonDto person : persons) {
						tempIds.add(person.getIdPerson());
						personRowList.put(person.getIdPerson(), !StringUtils.isEmpty(person.getCdPersonSuffix())
								? person.getNmPersonFull() + " " + person.getCdPersonSuffix() : person.getNmPersonFull());
					}
					Long[] tempRowList = new Long[tempIds.size()];
					int rowCount = 0;
					if (personIds != null && personIds.size() > 0) {
						for (Long idPerson : personIds) {
							if (tempIds.contains(idPerson)) {
								tempRowList[rowCount] = idPerson;
								rowCount++;
							}
						}
						tempIds = new ArrayList<>(Arrays.asList(tempRowList));
					}
					for (Long idPerson : tempIds) {
						if (personCount > ServiceConstants.MAX_NAMES) {
							break;
						}
						if (personCount == 0) {
							contactSearchListDto.setNmContact1(personRowList.get(idPerson));
						} else if (personCount == 1) {
							contactSearchListDto.setNmContact2(personRowList.get(idPerson));
						} else if (personCount == 2) {
							contactSearchListDto.setNmContact3(personRowList.get(idPerson));
						} else if (personCount == 3) {
							contactSearchListDto.setNmContact4(personRowList.get(idPerson));
						} else if (personCount == 4) {
							contactSearchListDto.setNmContact5(personRowList.get(idPerson));
						}
						personCount++;
					}
				}
				// Move Code from Web to Service Layer
				if ((null != contactSearchListDto.getDtStageClose() && !ObjectUtils.isEmpty(contactSearchListDto.getDtStageClose())
						&& !ServiceConstants.CSTAGES_ARI.equalsIgnoreCase(contactSearchListDto.getCdStage()) &&
						!ServiceConstants.CSTAGES_ARF.equalsIgnoreCase(contactSearchListDto.getCdStage()))
						|| ((ServiceConstants.CSTAGES_ARI.equalsIgnoreCase(contactSearchListDto.getCdStage())
						|| ServiceConstants.CSTAGES_ARF.equalsIgnoreCase(contactSearchListDto.getCdStage()))
						&& null != contactSearchListDto.getDtStageClose() && !ObjectUtils.isEmpty(contactSearchListDto.getDtStageClose())
						&& !ServiceConstants.GENERIC_END_DATE.equals(contactSearchListDto.getDtStageClose()))) {
					contactSearchListDto.setIndStageClosed(ServiceConstants.TRUE);
				} else {
					contactSearchListDto.setIndStageClosed("");
				}
				if (ServiceConstants.FPR_PROGRAM.equalsIgnoreCase(contactSearchListDto.getCdStage())
						&& ServiceConstants.BMTH.equalsIgnoreCase(contactSearchListDto.getCdContactType())) {
					contactSearchListDto.setDisableFPRMonthEval(ServiceConstants.TRUE);
				}

				if (personIds == null || personIds.size() == 0) {
					result.add(contactSearchListDto);
				} else {
					if (contactSearchListDto.getNmContact1() != null && !Collections.disjoint(tempIds, personIds)) {
						result.add(contactSearchListDto);
					}
				}

			} catch (DataLayerException e) {
				logger.fatal("DataLayerException occured in Search Contacts method of ContactSearchDaoImpl Class "
						+ e.getMessage());
			}
		}
	}

	private Criteria getCriteria(String cdEventStatus, Long idCase, Date dtScrSearchDateFrom, Date dtScrSearchDateTo, Long uiIdEvent, String cdContactType, String cdContactPurpose, String cdContactMethod, String cdContactLocation, String cdContactOthers, List<Long> stageIds) {
		Calendar cal = Calendar.getInstance();
		List<String> contactType = new ArrayList<>();
		List<String> contactPurpose = new ArrayList<>();
		// Modified the code to remove the join with Person table to get all
		// contacts created for that stage for warranty defect 12105
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(Contact.class).createAlias("stage", "stage")
				.setProjection(
						Projections.projectionList().add(Projections.property("dtLastUpdate"), "dtLastUpdate")
								.add(Projections.property("cdContactType"), "cdContactType")
								.add(Projections.property("dtContactOccurred"), "dtContactOccurred")
								.add(Projections.property("idEvent"), "idEvent")
								.add(Projections.property("indContactAttempted"), "indContactAttempted")
								.add(Projections.property("stage.cdStage"), "cdStage")
								.add(Projections.property("cdContactPurpose"), "cdContactPurpose")
								.add(Projections.property("stage.idStage"), "idStage")
								.add(Projections.property("dtContactApprv"), "dtContactApprv")
								.add(Projections.property("stage.dtStageClose"), "dtStageClose"))
				.setResultTransformer(Transformers.aliasToBean(ContactSearchListDto.class));
		boolean validSzCdEventStatus = StringUtil.isValid(cdEventStatus);
		if (validSzCdEventStatus) {
			cr.createCriteria("event").add(Restrictions.eq("idEvent", uiIdEvent));
		}
		if (null != idCase) {
			cr.add(Restrictions.eq("idCase", idCase));
		} else if (!stageIds.isEmpty()) {
			cr.add(Restrictions.in("stage.idStage", stageIds));
		}

		if (null != dtScrSearchDateFrom) {
			cal.setTime(dtScrSearchDateFrom);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			if (null == dtScrSearchDateTo) {
				cr.add(Restrictions.ge("dtContactOccurred", cal.getTime()));
			} else {
				Date start = cal.getTime();
				cal.setTime(dtScrSearchDateTo);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				Date end = cal.getTime();
				cr.add(Restrictions.between("dtContactOccurred", start, end));
			}
		} else if (null != dtScrSearchDateTo) {
			cal.setTime(dtScrSearchDateTo);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cr.add(Restrictions.le("dtContactOccurred", cal.getTime()));
		}
		if (null != uiIdEvent) {
			cr.add(Restrictions.eq("idEvent", uiIdEvent));
		}
		// if searching on Type
		if (StringUtil.isValid(cdContactType)) {
			if (cdContactType.charAt(0) == 'A' || cdContactType.charAt(0) == 'C' || cdContactType.charAt(0) == 'E') {
				contactType.add(cdContactType);
				contactType.add("D" + cdContactType.substring(1));
				contactType.add("L" + cdContactType.substring(1));
			} else if (cdContactType.charAt(0) == 'L') {
				contactType.add(cdContactType);
				contactType.add("D" + cdContactType.substring(1));
			} else {
				contactType.add(cdContactType);
			}
			cr.add(Restrictions.in("cdContactType", contactType));
		}
		if (StringUtil.isValid(cdContactPurpose)) {
			if (cdContactPurpose.charAt(0) == 'A' || cdContactPurpose.charAt(0) == 'B') {
				contactPurpose.add(cdContactPurpose);
				contactPurpose.add("D" + cdContactPurpose.substring(1));
				contactType.add("L" + cdContactPurpose.substring(1));
			} else if (cdContactPurpose.charAt(0) == 'L') {
				contactPurpose.add(cdContactPurpose);
				contactPurpose.add("D" + cdContactPurpose.substring(1));
			} else {
				contactPurpose.add(cdContactPurpose);
			}
			cr.add(Restrictions.in("cdContactPurpose", contactPurpose));
		}
		// if searching on Method
		if (StringUtil.isValid(cdContactMethod)) {
			cr.add(Restrictions.eq("cdContactMethod", cdContactMethod));
		}
		// if searching on Location
		if (StringUtil.isValid(cdContactLocation)) {
			cr.add(Restrictions.eq("cdContactLocation", cdContactLocation));
		}
		// if searching on Others
		if (StringUtil.isValid(cdContactOthers)) {
			cr.add(Restrictions.eq("cdContactOthers", cdContactOthers));
		}
		cr.addOrder(Order.desc("dtContactOccurred"));
		return cr;
	}

	@Override
	public boolean indStructNarrExists(Long uiIdEvent) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(ContactNarrative.class)
				.setProjection(Projections.projectionList().add(Projections.property("idEvent"), "idEvent")
						.add(Projections.property("idCase"), "idCase").add(Projections.property("idDocumentTemplate"),
								"idDocumentTemplate"))
				.setResultTransformer(Transformers.aliasToBean(ContactNarrativeDto.class));
		cr.add(Restrictions.eq("idEvent", uiIdEvent));
		ContactNarrativeDto contactNarrativeDto = (ContactNarrativeDto) cr.uniqueResult();
		if (null != contactNarrativeDto) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.contacts.dao.ContactSearchDao#
	 * getPersonDetailsForEvent(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonDto> getPersonDetailsForEvent(Long uiIdEvent, Long idPerson) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class)
				.setProjection(Projections.projectionList().add(Projections.property("idPerson"), "idPerson")
						.add(Projections.property("nmPersonFull"), "nmPersonFull"))
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));
		cr.add(Restrictions.or(Restrictions.eq("idEvent", uiIdEvent), Restrictions.eq("person.idPerson", idPerson)));
		List<PersonDto> result = (List<PersonDto>) cr.list();
		return result;
	}

	/**
	 * Method Name: searchContacts Method Description: This is NOT thread safe
	 * because it uses a field for its bind variables.
	 *
	 * @param contactSearchDto
	 * @return FindContactDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public FindContactDto searchContacts(ContactSearchDto contactSearchDto) {
		String sql = getsearchContactsSql(contactSearchDto);
		FindContactDto findContactDto = new FindContactDto();
		ContactDetailSearchDto contactDetailSearchDto = new ContactDetailSearchDto();
		List<ContactPurposeDto> contactPurposeDto = (List<ContactPurposeDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(sql).addScalar("dtContactOccurred", StandardBasicTypes.DATE)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdContactType", StandardBasicTypes.STRING)
				.addScalar("cdContactPurpose", StandardBasicTypes.STRING)
				.addScalar("cdContactMethod", StandardBasicTypes.STRING)
				.addScalar("indContactAttempted", StandardBasicTypes.STRING)
				.addScalar("idContactStage", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(ContactPurposeDto.class)).list();

		int pageNum = 0;
		if (!ObjectUtils.isEmpty(contactSearchDto.getArchInputStruct())
				&& contactSearchDto.getArchInputStruct().getUsPageNbr() != 0) {
			pageNum = contactSearchDto.getArchInputStruct().getUsPageNbr();
		}
		if (pageNum <= ServiceConstants.Zero)
			pageNum = ServiceConstants.One;

		int pageSize = 0;
		if (!ObjectUtils.isEmpty(contactSearchDto.getArchInputStruct())
				&& contactSearchDto.getArchInputStruct().getUlPageSizeNbr() != 0) {
			pageSize = contactSearchDto.getArchInputStruct().getUlPageSizeNbr();
		}
		if (pageSize <= ServiceConstants.Zero)
			pageSize = ServiceConstants.Fifty_Value;

		int firstRow = ServiceConstants.One + ((pageNum - ServiceConstants.One) * pageSize);
		int lastRow = pageNum * pageSize;
		int index = ServiceConstants.Zero;

		for (ContactPurposeDto contact : contactPurposeDto) {
			index++;
			if (index <= lastRow && index >= firstRow) {

				contactDetailSearchDto.getContactPurposeDtos().add(contact);
			}
		}

		findContactDto.setContactDetailSearchDto(contactDetailSearchDto);

		ServiceOutputDto serviceOutputDto = new ServiceOutputDto();

		if (index > pageNum * pageSize) {
			serviceOutputDto.setMoreDataInd(ServiceConstants.Y);
		} else {
			serviceOutputDto.setMoreDataInd(ServiceConstants.N);
		}
		serviceOutputDto.setRowQtySize(contactDetailSearchDto.getContactPurposeDtos().size());
		findContactDto.setServiceOutputDto(serviceOutputDto);

		return findContactDto;
	}

	private String getsearchContactsSql(ContactSearchDto contactSearchDto) {
		String sql = searchContactsSql;
		boolean validSzCdEventStatus = isValid(contactSearchDto.getSzCdEventStatus());
		if (validSzCdEventStatus) {
			sql += ", EVENT B";
		}

		sql += " WHERE";

		sql = setId(contactSearchDto, sql);

		// if searching on Event Status
		if (validSzCdEventStatus) {
			sql += " AND A.ID_EVENT = B.ID_EVENT  ";
		}

		// if searching on date from
		sql = setFromDate(contactSearchDto, sql);

		// if searching on date to
		sql = setToDate(contactSearchDto, sql);

		// if searching on Id Event
		if (contactSearchDto.getUlIdEvent() > ServiceConstants.Zero) {
			sql += " AND A.ID_EVENT = ";
			sql += String.valueOf(contactSearchDto.getUlIdEvent());
			sql += "   ";
		}

		// if searching on Type
		if (isValid(contactSearchDto.getSzCdContactType())) {
			if (contactSearchDto.getUlIdCase() != ServiceConstants.Zero) {
				sql += " AND A.CD_CONTACT_TYPE LIKE \'";
				sql += contactSearchDto.getSzCdContactType();
				sql += "\'  ";
			} else {
				sql += " AND A.CD_CONTACT_TYPE = '";
				sql += contactSearchDto.getSzCdContactType();
				sql += "\'  ";
			}
		}

		// if searching on Purpose
		if (isValid(contactSearchDto.getSzCdContactPurpose())) {

			if (contactSearchDto.getUlIdCase() != ServiceConstants.Zero) {
				sql += " AND A.CD_CONTACT_PURPOSE LIKE \'";
				sql += contactSearchDto.getSzCdContactPurpose();
				sql += "\'  ";
			} else {
				sql += " AND A.CD_CONTACT_PURPOSE = ? \n";
				sql += contactSearchDto.getSzCdContactPurpose();
				sql += "   ";
			}
		}

		// if searching on Method
		if (isValid(contactSearchDto.getSzCdContactMethod())) {
			sql += " AND A.CD_CONTACT_METHOD = ";
			sql += contactSearchDto.getSzCdContactMethod();
			sql += "   ";
		}

		// if searching on Location
		if (isValid(contactSearchDto.getSzCdContactLocation())) {
			sql += " AND A.CD_CONTACT_LOCATION = ";
			sql += contactSearchDto.getSzCdContactLocation();
			sql += "   ";
		}

		// if searching on Others
		if (isValid(contactSearchDto.getSzCdContactOthers())) {
			sql += " AND A.CD_CONTACT_OTHERS = '";
			sql += contactSearchDto.getSzCdContactOthers();
			sql += "\'";
		}

		// if searching on Event Status
		if (validSzCdEventStatus) {
			sql += " AND B.CD_EVENT_STATUS =  '";
			sql += contactSearchDto.getSzCdEventStatus();
			sql += "\'   ";
		}
		sql += " ORDER BY A.DT_CONTACT_OCCURRED DESC ";

		return sql;
	}

	private boolean isValid(String value) {
		return (value != null && value.trim().length() > ServiceConstants.Zero);
	}

	private String setFromDate(ContactSearchDto contactSearchDto, String sql) {
		if (contactSearchDto.getDtScrSearchDateFrom() != null) {
			if (mobileUtil.isMPSEnvironment()) {
				sql += " AND DATE(A.DT_CONTACT_OCCURRED) >= DATE( ";
				sql += contactSearchDto.getDtScrSearchDateFrom();
				sql += " )  ";
			} else {
				sql += " AND TRUNC(A.DT_CONTACT_OCCURRED) >= to_date('";
				sql += contactSearchDto.getDtScrSearchDateFrom();
				sql += "','yyyy-mm-dd')   ";
			}

		}
		return sql;
	}

	private String setToDate(ContactSearchDto csys04diDto, String sql) {
		if (csys04diDto.getDtScrSearchDateTo() != null) {
			if (mobileUtil.isMPSEnvironment()) {
				sql += " AND DATE(A.DT_CONTACT_OCCURRED) <= DATE( ";
				sql += csys04diDto.getDtScrSearchDateTo();
				sql += " )  ";
			} else {
				sql += " AND TRUNC(A.DT_CONTACT_OCCURRED) <= to_date('";
				sql += csys04diDto.getDtScrSearchDateTo();
				sql += "','yyyy-mm-dd')   ";
			}
		}
		return sql;
	}

	private String setId(ContactSearchDto contactSearchDto, String sql) {
		if (contactSearchDto.getUlIdCase() != ServiceConstants.Zero) {
			sql += " A.ID_CASE = ";
			sql += String.valueOf(contactSearchDto.getUlIdCase());
			sql += "  ";
		} else {
			sql += " A.ID_CONTACT_STAGE = ";
			sql += String.valueOf(contactSearchDto.getUlIdStage());
			sql += "  ";
		}
		return sql;
	}

	/**
	 * Tuxedo Service Name: CSVC22S, DAM Name: CDYN03D This is a dynamic DAM
	 * that retrieves rows from the CONTACT table given a Stage ID and dynamic
	 * criteria that could include one or more Types, Locations, Methods,
	 * Purposes, and Others Contacted. The records will be retrieved in the
	 * order of Type and then Date Contact Occurred (earliest first).
	 *
	 * @param contactListSearchDto
	 * @return contactDtoList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ContactDto> searchContactList(ContactListSearchDto contactListSearchDto) {

		List<ContactDto> contactDtoList = null;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Contact.class)
				.setProjection(Projections.projectionList().add(Projections.property("stage.idStage"), "idContactStage")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("idEvent"), "idEvent")
						.add(Projections.property("person.idPerson"), "idContactWorker")
						.add(Projections.property("cdContactLocation"), "cdContactLocation")
						.add(Projections.property("cdContactMethod"), "cdContactMethod")
						.add(Projections.property("cdContactOthers"), "cdContactOthers")
						.add(Projections.property("cdContactPurpose"), "cdContactPurpose")
						.add(Projections.property("cdContactType"), "cdContactType")
						.add(Projections.property("dtCntctMnthlySummBeg"), "dtCntctMnthlySummBeg")
						.add(Projections.property("dtCntctMnthlySummEnd"), "dtCntctMnthlySummEnd")
						.add(Projections.property("dtCntctNextSummDue"), "dtCntctNextSummDue")
						.add(Projections.property("dtContactApprv"), "dtContactApprv")
						.add(Projections.property("dtContactOccurred"), "dtContactOccurred")
						.add(Projections.property("dtLastEmpUpdate"), "dtLastEmpUpdate")
						.add(Projections.property("indContactAttempted"), "indContactAttempted"))
				.setResultTransformer(Transformers.aliasToBean(ContactDto.class));

		criteria.add(Restrictions.eq("stage.idStage", contactListSearchDto.getIdStage()));

		if (!ObjectUtils.isEmpty(contactListSearchDto.getCdContactTypeList()))
			criteria.add(Restrictions.in("cdContactType", contactListSearchDto.getCdContactTypeList()));

		if (!ObjectUtils.isEmpty(contactListSearchDto.getCdContactPurposeList()))
			criteria.add(Restrictions.in("cdContactPurpose", contactListSearchDto.getCdContactPurposeList()));

		if (!ObjectUtils.isEmpty(contactListSearchDto.getCdContactMethodList()))
			criteria.add(Restrictions.in("cdContactMethod", contactListSearchDto.getCdContactMethodList()));

		if (!ObjectUtils.isEmpty(contactListSearchDto.getCdContactLocationList()))
			criteria.add(Restrictions.in("cdContactLocation", contactListSearchDto.getCdContactLocationList()));

		if (!ObjectUtils.isEmpty(contactListSearchDto.getCdContactOthersList()))
			criteria.add(Restrictions.in("cdContactOthers", contactListSearchDto.getCdContactOthersList()));

		criteria.addOrder(Order.asc("cdContactType")).addOrder(Order.asc("dtContactOccurred"));

		contactDtoList = criteria.list();
		return contactDtoList;
	}

	/**
	 * Tuxedo Service Name: CSVC22S, DAM Name: CDYN03D This is a dynamic DAM
	 * that retrieves rows from the CONTACT table given a Stage ID and dynamic
	 * criteria that could include one or more Types, Locations, Methods,
	 * Purposes, and Others Contacted. The records will be retrieved in the
	 * order of Type and then Date Contact Occurred (earliest first).
	 *
	 * @param contactListSearchDto
	 * @return contactDtoList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ContactDto> searchContactListCPSClosingSummary(ContactListSearchDto contactListSearchDto) {

		List<ContactDto> contactDtoList = null;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Contact.class)
				.setProjection(Projections.projectionList().add(Projections.property("stage.idStage"), "idContactStage")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("idEvent"), "idEvent")
						.add(Projections.property("person.idPerson"), "idContactWorker")
						.add(Projections.property("cdContactLocation"), "cdContactLocation")
						.add(Projections.property("cdContactMethod"), "cdContactMethod")
						.add(Projections.property("cdContactOthers"), "cdContactOthers")
						.add(Projections.property("cdContactPurpose"), "cdContactPurpose")
						.add(Projections.property("cdContactType"), "cdContactType")
						.add(Projections.property("dtCntctMnthlySummBeg"), "dtCntctMnthlySummBeg")
						.add(Projections.property("dtCntctMnthlySummEnd"), "dtCntctMnthlySummEnd")
						.add(Projections.property("dtCntctNextSummDue"), "dtCntctNextSummDue")
						.add(Projections.property("dtContactApprv"), "dtContactApprv")
						.add(Projections.property("dtContactOccurred"), "dtContactOccurred")
						.add(Projections.property("dtLastEmpUpdate"), "dtLastEmpUpdate")
						.add(Projections.property("indContactAttempted"), "indContactAttempted"))
				.setResultTransformer(Transformers.aliasToBean(ContactDto.class));

		criteria.add(Restrictions.eq("stage.idStage", contactListSearchDto.getIdStage()));

		if (!ObjectUtils.isEmpty(contactListSearchDto.getCdContactTypeList()))
			criteria.add(Restrictions.in("cdContactType", contactListSearchDto.getCdContactTypeList()));

		if (!ObjectUtils.isEmpty(contactListSearchDto.getCdContactPurposeList()))
			criteria.add(Restrictions.in("cdContactPurpose", contactListSearchDto.getCdContactPurposeList()));

		if (!ObjectUtils.isEmpty(contactListSearchDto.getCdContactMethodList()))
			criteria.add(Restrictions.in("cdContactMethod", contactListSearchDto.getCdContactMethodList()));

		if (!ObjectUtils.isEmpty(contactListSearchDto.getCdContactLocationList()))
			criteria.add(Restrictions.in("cdContactLocation", contactListSearchDto.getCdContactLocationList()));

		if (!ObjectUtils.isEmpty(contactListSearchDto.getCdContactOthersList()))
			criteria.add(Restrictions.in("cdContactOthers", contactListSearchDto.getCdContactOthersList()));

		criteria.addOrder(Order.asc("cdContactType")).addOrder(Order.asc("dtContactOccurred"));

		contactDtoList = criteria.list();
		if (!ObjectUtils.isEmpty(contactDtoList)) {
			return contactDtoList;
		} else {
			throw new FormsException(messageSource.getMessage("contactSearchDao.noclosingSumamry", null, Locale.US));

		}

	}

	// CANIRSP-23 For I&R Staff Search
	@Override
	public StageSearchRes stageSearch(StageSearchReq stageSearchBean) {
		StageSearchRes retVal = new StageSearchRes();

		Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(Stage.class);
		if (!ObjectUtils.isEmpty(stageSearchBean.getIdCase())) {
			criteria1.add(Restrictions.eq("capsCase.idCase", stageSearchBean.getIdCase()));
		}
		if (!ObjectUtils.isEmpty(stageSearchBean.getIdStage())) {
			criteria1.add(Restrictions.eq("idStage", stageSearchBean.getIdStage()));
		}
		// For I&R search, this will ALWAYS be INT but we let it be a parameter for clarity and reusability
		if (!ObjectUtils.isEmpty(stageSearchBean.getCdStage())) {
			criteria1.add(Restrictions.eq("cdStage", stageSearchBean.getCdStage()));
		}
		List<Stage> rawResults = criteria1.list();
		if (!ObjectUtils.isEmpty(rawResults)) {
			List<StageSearchDto> returnList = new LinkedList<>();
			for (Stage currResult : rawResults) {
				StageSearchDto newReturnableRow = new StageSearchDto();
				newReturnableRow.setIdStage(currResult.getIdStage());
				newReturnableRow.setCdStage(currResult.getCdStage());
				newReturnableRow.setCdStageType(currResult.getCdStageType());
				newReturnableRow.setDtOpened(currResult.getDtStageStart());
				newReturnableRow.setDtClosed(currResult.getDtStageClose());
				newReturnableRow.setNmPrimary(currResult.getNmStage());
        newReturnableRow.setIdCase(currResult.getCapsCase().getIdCase());
				returnList.add(newReturnableRow);
			}
			retVal.setStageSearchResultList(returnList);
		}
		return retVal;
	}

  @Override
  public Boolean isCaseSensitive(Long idCase) {
    Boolean retVal = false;
    Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(CapsCase.class);
    criteria1.add(Restrictions.eq("idCase", idCase));
    CapsCase rawResults = (CapsCase) criteria1.uniqueResult();
    if (!ObjectUtils.isEmpty(rawResults) && "Y".equalsIgnoreCase(rawResults.getIndCaseSensitive())) {
      retVal = true;
    }
    return retVal;
  }

  @Override
  public Boolean hasSensitiveAccess(Long idCaseworker, Long idCase) {
    Boolean retVal = true;
    StringBuilder sqlBuffer = new StringBuilder(hasSensitiveAccess);
    Query sqlQuery = sessionFactory.getCurrentSession()
        .createSQLQuery(sqlBuffer.toString())
        .addScalar("idCase", StandardBasicTypes.LONG)
        .setParameter("idCaseworker", idCaseworker)
        .setParameter("idCase", idCase);
    sqlQuery.setResultTransformer(Transformers.aliasToBean(ContactNarrativeDto.class));

    List<ContactNarrativeDto> caseworkerAccessCases = sqlQuery.list();
    if (ObjectUtils.isEmpty(caseworkerAccessCases)) {
      retVal = false;
    }
    return retVal;
  }

	@Override
	// CANIRSP-244 'Create' Alerts for Identified Follow-up Action Item
	public List<InrContactFollowUpPendingDto> getFollowupPending(InrFollowupPendingReq stageSearchReq) {
		List<InrContactFollowUpPendingDto> returnList = new LinkedList<>();
		HashMap<Long, InrContactFollowUpPendingDto> groupsAlreadyFound = new HashMap<>();

		StringBuilder sqlBuffer = new StringBuilder(getFollowupPendingGroups);
		if (stageSearchReq.getIdStage() != null) {
			sqlBuffer.append(getFollowupPendingGroupsStageSuffix);
		}
		if (stageSearchReq.getIdCase() != null) {
			sqlBuffer.append(getFollowupPendingGroupsCaseSuffix);
		}
		SQLQuery sqlQuery = sessionFactory.getCurrentSession()
				.createSQLQuery(sqlBuffer.toString())
				.addScalar("dtStaffing", StandardBasicTypes.DATE)
				.addScalar("idNotificationEvent", StandardBasicTypes.LONG)
				.addScalar("idGroupNum", StandardBasicTypes.LONG);
		if (stageSearchReq.getIdStage() != null) {
			sqlQuery.setParameter("idStage", stageSearchReq.getIdStage());
		}
		if (stageSearchReq.getIdCase() != null) {
			sqlQuery.setParameter("idCase", stageSearchReq.getIdCase());
		}
		sqlQuery.setResultTransformer(Transformers.aliasToBean(InrContactFollowUpPendingDto.class));

		List<InrContactFollowUpPendingDto> contactsWithPendfingFollowupList = sqlQuery.list();
		for (InrContactFollowUpPendingDto currContactsWithPendfingFollowup : contactsWithPendfingFollowupList) {
			if (!groupsAlreadyFound.containsKey(currContactsWithPendfingFollowup.getIdGroupNum())) {
				groupsAlreadyFound.put(currContactsWithPendfingFollowup.getIdGroupNum(), currContactsWithPendfingFollowup);
				returnList.add(currContactsWithPendfingFollowup);
			}
		}

		if (stageSearchReq.getIncludeChildNames() != null && stageSearchReq.getIncludeChildNames()) {
			List<SimpleChildNameDto> childrenSqlList = sessionFactory.getCurrentSession()
					.createSQLQuery(getFollowupPendingChildren)
					.addScalar("strName", StandardBasicTypes.STRING)
					.addScalar("idGroup", StandardBasicTypes.LONG)
					.setParameterList("nmbGrpNumList", groupsAlreadyFound.keySet().toArray())
					.setResultTransformer(Transformers.aliasToBean(SimpleChildNameDto.class)).list();

			for (SimpleChildNameDto currChild : childrenSqlList) {
				InrContactFollowUpPendingDto existingEntry = groupsAlreadyFound.get(currChild.getIdGroup());
				if (existingEntry.getInvolvedYouthList() == null) {
					existingEntry.setInvolvedYouthList(new LinkedList<>());
				}
				existingEntry.getInvolvedYouthList().add(currChild.getStrName());
			}
		}

		return returnList;
	}
}