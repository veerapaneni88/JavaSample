package us.tx.state.dfps.service.contacts.daoimpl;

import java.sql.Blob;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.ContactGuideNarr;
import us.tx.state.dfps.common.domain.ContactGuideTopic;
import us.tx.state.dfps.common.domain.DocumentTemplate;
import us.tx.state.dfps.common.domain.DocumentTemplateType;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.GuideTopicLookup;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.CommonIndEnum;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contact.dto.ContactFetchDto;
import us.tx.state.dfps.service.contact.dto.ContactGuideDto;
import us.tx.state.dfps.service.contact.dto.PrincipalParentDto;
import us.tx.state.dfps.service.contacts.dao.ContactGuideDao;
import us.tx.state.dfps.service.exception.DataLayerException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Implements
 * the methods from ContactGuideBean Sep 6, 2017- 9:50:06 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class ContactGuideDaoImpl implements ContactGuideDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${contactGuideDaoImpl.isPrincipalParentSql}")
	private String isPrincipalParentSql;

	@Value("${ContactGuideDaoImpl.princChildPCSql}")
	private String princChildPCSql;

	@Value("${ContactGuideDaoImpl.princChildInfoWhenStageCloseSql}")
	private String princChildInfoWhenStageCloseSql;

	@Value("${ContactGuideDaoImpl.princHasSUBStageSql}")
	private String princHasSUBStageSql;

	@Value("${ContactGuideDaoImpl.isPrincChildSql}")
	private String isPrincChildSql;

	@Value("${ContactGuideDaoImpl.isPrincChildMpsSql}")
	private String isPrincChildMpsSql;

	/**
	 * Method Name: saveGuidePlanForPrincipal Method Description: Method to save
	 * the Contact guide narrative into the Contact_guide_narr table.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	@SuppressWarnings("unchecked")
	public ContactGuideDto saveGuidePlanForPrincipal(ContactGuideDto contactGuideDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DocumentTemplate.class);
		DocumentTemplateType documentTemplateType = new DocumentTemplateType();
		documentTemplateType.setIdDocumentTemplateType(29);
		criteria.add(Restrictions.eq("documentTemplateType", documentTemplateType));
		List<DocumentTemplate> idDocumentTemps = criteria.list();
		long idDocumentTemp = idDocumentTemps.get(0).getIdDocumentTemplate();

		ContactGuideNarr contactGuideNarr = new ContactGuideNarr();
		contactGuideNarr.setDtLastUpdate(new java.sql.Timestamp(System.currentTimeMillis()));
		Event event = new Event();
		event.setIdEvent(contactGuideDto.getIdEvent());
		contactGuideNarr.setEvent(event);
		Person person = new Person();
		if (contactGuideDto.getIdPerson() != 0 && contactGuideDto.getIdPerson() > 0) {
			person.setIdPerson(contactGuideDto.getIdPerson());
		} else {
			person.setIdPerson((long) Types.INTEGER);
		}
		contactGuideNarr.setPerson(person);
		CapsCase capsCase = new CapsCase();
		capsCase.setIdCase(contactGuideDto.getIdCase());
		contactGuideNarr.setCapsCase(capsCase);
		Stage stage = new Stage();
		stage.setIdStage(contactGuideDto.getIdStage());
		contactGuideNarr.setStage(stage);

		if (contactGuideDto.getGuidePlan() != null) {
			// = wrapBlob(contactGuideDto.getGuidePlan());
			contactGuideNarr.setNarrative(contactGuideDto.getGuidePlanBlob());
		}
		if (contactGuideDto.getCdGuideRole() != "" && contactGuideDto.getCdGuideRole() != null) {
			contactGuideNarr.setCdGuideRole(contactGuideDto.getCdGuideRole());
		}
		if (contactGuideDto.getGuideplanType() != "" && contactGuideDto.getGuideplanType() != null) {
			contactGuideNarr.setCdType(contactGuideDto.getGuideplanType());
		}
		contactGuideNarr.setIdDocumentTemplate(idDocumentTemp);
		sessionFactory.getCurrentSession().save(contactGuideNarr);
		contactGuideDto.setIdContactGuideNarr(contactGuideNarr.getIdContactGuideNarr());
		return contactGuideDto;
	}

	/**
	 * Method Name: saveGuideTopics Method Description: Method to save the Guide
	 * Topic information for a person contacted.
	 * 
	 * @param contactGuideDto
	 * @param guideTopic
	 * @return long @
	 */
	public long saveGuideTopics(ContactGuideDto contactGuideDto, String guideTopic) {

		ContactGuideTopic contactGuideTopic = new ContactGuideTopic();
		contactGuideTopic.setDtLastUpdate(new java.sql.Timestamp(System.currentTimeMillis()));
		ContactGuideNarr contactGuideNarr = new ContactGuideNarr();
		contactGuideNarr.setIdContactGuideNarr(contactGuideDto.getIdContactGuideNarr());
		contactGuideTopic.setContactGuideNarr(contactGuideNarr);
		contactGuideTopic.setCdGuideTopic(guideTopic);
		return (long) sessionFactory.getCurrentSession().save(contactGuideTopic);
	}

	/**
	 * Method Name: deleteContactGuideTopic Method Description: Method to delete
	 * the Contact Guide Topics
	 * 
	 * @param contactGuideDto
	 * @return long @
	 */
	@SuppressWarnings("unchecked")
	public long deleteContactGuideTopic(ContactGuideDto contactGuideDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactGuideTopic.class);
		ContactGuideNarr contactGuideNarr = new ContactGuideNarr();
		contactGuideNarr.setIdContactGuideNarr(contactGuideDto.getIdContactGuideNarr());
		criteria.add(Restrictions.eq("contactGuideNarr", contactGuideNarr));
		List<ContactGuideTopic> contactGuideTopicList = criteria.list();

		for (ContactGuideTopic contactGuideTopic : contactGuideTopicList) {
			sessionFactory.getCurrentSession().delete(contactGuideTopic);
		}
		return (long) contactGuideTopicList.size();
	}

	/**
	 * Method Name: deleteContactGuidePlan Method Description: Method to delete
	 * the Contact Guide Narrative
	 * 
	 * @param contactGuideDto
	 * @return long @
	 */
	public long deleteContactGuidePlan(ContactGuideDto contactGuideDto) {
		ContactGuideNarr contactGuideNarr = (ContactGuideNarr) sessionFactory.getCurrentSession()
				.get(ContactGuideNarr.class, contactGuideDto.getIdContactGuideNarr());
		sessionFactory.getCurrentSession().delete(contactGuideNarr);
		return 1;
	}

	/**
	 * Method Name: updateContactGuidePlan Method Description: Method to update
	 * the Contact Guide Narrative
	 * 
	 * @param contactGuideDto
	 * @return long
	 */
	@SuppressWarnings("unchecked")
	public long updateContactGuidePlan(ContactGuideDto contactGuideDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactGuideNarr.class);
		criteria.add(Restrictions.eq("idContactGuideNarr", contactGuideDto.getIdContactGuideNarr()));
		List<ContactGuideNarr> contactGuideNarrs = criteria.list();
		// Commented Unused Code
		for (ContactGuideNarr contactGuideNarr : contactGuideNarrs) {
			if (contactGuideDto.getGuidePlan() != null) {
				byte[] blob = contactGuideDto.getGuidePlanBlob();
				contactGuideNarr.setNarrative(blob);
				sessionFactory.getCurrentSession().save(contactGuideNarr);
			}
		}
		return (long) (!ObjectUtils.isEmpty(contactGuideNarrs) ? contactGuideNarrs.size() : 0l);
	}

	/**
	 * Method Name: saveNarrColCargvr Method Description: Method to save
	 * Narrative information for Collateral/Caregiver.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	@SuppressWarnings("unchecked")
	public ContactGuideDto saveNarrColCargvr(ContactGuideDto contactGuideDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DocumentTemplate.class);
		criteria.add(Restrictions.eq("documentTemplateType.idDocumentTemplateType", 29L));
		ProjectionList reqcolumns = Projections.projectionList();
		reqcolumns.add(Projections.property("idDocumentTemplate"));
		criteria.setProjection(reqcolumns);
		List<Long> idDocumentTemps = criteria.list();
		long idDocumentTemp = idDocumentTemps.get(0).longValue();

		ContactGuideNarr contactGuideNarr = new ContactGuideNarr();
		contactGuideNarr.setDtLastUpdate(new java.sql.Timestamp(System.currentTimeMillis()));
		Event event = new Event();
		event.setIdEvent(contactGuideDto.getIdEvent());
		contactGuideNarr.setEvent(event);
		CapsCase capsCase = new CapsCase();
		capsCase.setIdCase(contactGuideDto.getIdCase());
		contactGuideNarr.setCapsCase(capsCase);
		Stage stage = new Stage();
		stage.setIdStage(contactGuideDto.getIdStage());
		contactGuideNarr.setStage(stage);
		byte[] blob;
		if (contactGuideDto.getGuidePlan() != null) {

			blob = contactGuideDto.getGuidePlanBlob();
			contactGuideNarr.setNarrative(blob);
		}
		if (contactGuideDto.getGuideplanType() != "" && contactGuideDto.getGuideplanType() != null) {
			contactGuideNarr.setCdType(contactGuideDto.getGuideplanType());
		}

		contactGuideNarr.setIdDocumentTemplate((long) idDocumentTemp);
		sessionFactory.getCurrentSession().save(contactGuideNarr);
		contactGuideDto.setIdContactGuideNarr(contactGuideNarr.getIdContactGuideNarr());
		return contactGuideDto;
	}

	/**
	 * Method Name: saveCaregvrGuideTopics Method Description: Method to save
	 * the Guide Topic information for Caregivers.
	 * 
	 * @param contactGuideDto
	 * @param guideTopic
	 * @return long @
	 */
	public long saveCaregvrGuideTopics(ContactGuideDto contactGuideDto, String guideTopic) {

		ContactGuideTopic contactGuideTopic = new ContactGuideTopic();
		contactGuideTopic.setDtLastUpdate(new java.sql.Timestamp(System.currentTimeMillis()));
		ContactGuideNarr contactGuideNarr = new ContactGuideNarr();
		contactGuideNarr.setIdContactGuideNarr(contactGuideDto.getIdContactGuideNarr());
		contactGuideTopic.setContactGuideNarr(contactGuideNarr);
		contactGuideTopic.setCdGuideTopic(guideTopic);
		return (long) sessionFactory.getCurrentSession().save(contactGuideTopic);

	}

	/**
	 * Method Name: fetchGuideTopicDescr Method Description: This method
	 * retrieves the description of Guide Topics.
	 * 
	 * @return ContactDetailDto @
	 */
	@SuppressWarnings("unchecked")
	public List<ContactFetchDto> fetchGuideTopicDescr() {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(GuideTopicLookup.class);
		List<GuideTopicLookup> guideTopicLookups = criteria.list();
		List<ContactFetchDto> contactFetchDtos = new ArrayList<>();
		for (GuideTopicLookup guideTopicLookup : guideTopicLookups) {
			ContactFetchDto contactFetchDto = new ContactFetchDto();
			contactFetchDto.setCdGuideTopic(guideTopicLookup.getCdGuideTopic());
			contactFetchDto.setDescription(guideTopicLookup.getTxtDescription());
			contactFetchDtos.add(contactFetchDto);
		}
		return contactFetchDtos;
	}

	/**
	 * Method Name: checkifGuideNarrExists Method Description: Method to
	 * indicate if a Contact has Contact Guide Narrative records
	 * 
	 * @param idEvent
	 * @return boolean @
	 */
	@SuppressWarnings("unchecked")
	public boolean checkifGuideNarrExists(long idEvent) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactGuideNarr.class);
		criteria.setProjection(Projections.property("narrative"));
		Event event = new Event();
		event.setIdEvent(idEvent);
		criteria.add(Restrictions.eq("event", event));
		List<Blob> blobs = criteria.list();
		if (blobs.size() > 0) {
			return ServiceConstants.TRUE_VALUE;
		} else {
			return ServiceConstants.FALSEVAL;
		}
	}

	/**
	 * Method Name: isPrincipalParent Method Description: Identifies if a person
	 * is a parent.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ContactGuideDto isPrincipalParent(ContactGuideDto contactGuideDto) {
		List<PrincipalParentDto> principalParentDtos = (List<PrincipalParentDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(isPrincipalParentSql)
				.setLong("idStage", contactGuideDto.getIdStage())
				.setLong(ServiceConstants.IDPERSON, contactGuideDto.getIdPerson()))
						.addScalar(ServiceConstants.CD_PERSON_MARITAL_STATUS, StandardBasicTypes.STRING)
						.addScalar(ServiceConstants.CDSTAGEPERSRELINT, StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PrincipalParentDto.class)).list();
		if (principalParentDtos.size() > 0) {
			contactGuideDto.setIndParent(CommonIndEnum.valueOf(ServiceConstants.YES));
			contactGuideDto.setCdGuideRole(ServiceConstants.CGPROLE_020);
		}
		return contactGuideDto;
	}

	/**
	 * Method Name: isStageOpen Method Description: Checks if Stage is open
	 * 
	 * @param contactGuideDto
	 * @return boolean
	 */
	@Override
	public boolean isStageOpen(ContactGuideDto contactGuideDto) {
		boolean isStageOpen = ServiceConstants.FALSEVAL;
		if (contactGuideDto != null) {
			Date compareDate = null;
			try {
				compareDate = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_MMDDYYYY)
						.parse(ServiceConstants.STAGE_OPEN_DT);
			} catch (ParseException e) {
				DataLayerException dataException = new DataLayerException(e.getMessage());
				dataException.initCause(e);
				throw dataException;
			}
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, contactGuideDto.getIdStage());
			if (!ObjectUtils.isEmpty(stage) && ((ObjectUtils.isEmpty(stage.getDtStageClose())
					|| stage.getDtStageClose().equals(compareDate)))) {
				isStageOpen = ServiceConstants.TRUE_VALUE;
			}
		}
		return isStageOpen;
	}

	/**
	 * Method Name: princChildPC Method Description: Method to determine whether
	 * principal was child (Under 18) when the event_person_link was established
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto @
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public ContactGuideDto princChildPC(ContactGuideDto contactGuideDto) {
		List subStageList = sessionFactory.getCurrentSession().createSQLQuery(princChildPCSql)
				.setLong("idEvent1", contactGuideDto.getIdEvent()).setLong("idEvent2", contactGuideDto.getIdEvent())
				.setLong(ServiceConstants.IDPERSON, contactGuideDto.getIdPerson()).list();
		if (!subStageList.isEmpty()) {
			// contactGuideDto.setIndChild(CommonIndEnum.valueOf(ServiceConstants.YES));
			contactGuideDto.setCdGuideRole(ServiceConstants.CGPROLE_010);
		}
		return contactGuideDto;
	}

	/**
	 * Method Name: princChildInfoWhenStageClose Method Description: Method to
	 * determine whether principal was child (Under 18) when the
	 * event_person_link was established
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ContactGuideDto princChildInfoWhenStageClose(ContactGuideDto contactGuideDto) {
		List<PrincipalParentDto> principalParentDtos = (List<PrincipalParentDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(princChildInfoWhenStageCloseSql)
				.setLong("idStageOut", contactGuideDto.getIdStage())
				.setLong("idPersonOut", contactGuideDto.getIdPerson())
				.setLong("idPersonIn", contactGuideDto.getIdPerson()).setLong("idEventIn", contactGuideDto.getIdEvent())
				.setLong(ServiceConstants.IDPERSON, contactGuideDto.getIdPerson()))
						.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
						.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PrincipalParentDto.class)).list();
		if (principalParentDtos.size() > 0) {
			// defect 13878 : uncommented the code 
			contactGuideDto.setIndChild(CommonIndEnum.valueOf(ServiceConstants.YES));
			contactGuideDto.setCdGuideRole(ServiceConstants.CGPROLE_010);
		}
		return contactGuideDto;
	}

	/**
	 * Method Name: princHasSUBStage Method Description: Method to identify if
	 * person has a SUBCARE stage.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto @
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public ContactGuideDto princHasSUBStage(ContactGuideDto contactGuideDto) {
		List<Long> subStageList = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(princHasSUBStageSql)
				.addScalar(ServiceConstants.IDSTAGE, StandardBasicTypes.LONG)
				.setParameter(ServiceConstants.IDPERSON, contactGuideDto.getIdPerson());
		if (null != query.list()) {
			subStageList = (List<Long>) query.list();
			if (!CollectionUtils.isEmpty(subStageList)) {
				contactGuideDto.setIndChild(CommonIndEnum.valueOf(ServiceConstants.YES));
				contactGuideDto.setCdGuideRole(ServiceConstants.CGPROLE_010);
			}
		}
		return contactGuideDto;
	}

	/**
	 * Method Name: isPrincChild Method Description: Method to identify if
	 * person is a Child.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ContactGuideDto isPrincChild(ContactGuideDto contactGuideDto) {
		String isPrincChildFinalSql;
		if (ServiceConstants.SERVER_IMPACT) {
			isPrincChildFinalSql = isPrincChildSql;
		} else {
			isPrincChildFinalSql = isPrincChildMpsSql;
		}
		List<PrincipalParentDto> principalParentDtos = (List<PrincipalParentDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(isPrincChildFinalSql)
				.setLong("idStage", contactGuideDto.getIdStage())
				.setLong(ServiceConstants.IDPERSON, contactGuideDto.getIdPerson())
				.setLong("idPersonIn", contactGuideDto.getIdPerson()))
						.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
						.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PrincipalParentDto.class)).list();
		if (principalParentDtos.size() > 0) {
			contactGuideDto.setIndChild(CommonIndEnum.valueOf(ServiceConstants.YES));
			contactGuideDto.setCdGuideRole(ServiceConstants.CGPROLE_010);
		}
		return contactGuideDto;
	}

	/**
	 * Method Name: fetchContactGuidePlan Method Description: Method to fetch
	 * the Contact Guide Narrative for a Principal(Parent/Child) who was
	 * contacted.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ContactGuideDto fetchContactGuidePlan(ContactGuideDto contactGuideDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactGuideNarr.class);
		Stage stage = new Stage();
		stage.setIdStage(contactGuideDto.getIdStage());
		criteria.add(Restrictions.eq("stage", stage));
		Event event = new Event();
		event.setIdEvent(contactGuideDto.getIdEvent());
		criteria.add(Restrictions.eq("event", event));
		Person person = new Person();
		person.setIdPerson(contactGuideDto.getIdPerson());
		criteria.add(Restrictions.eq("person", person));
		List<ContactGuideNarr> contactGuideNarrs = (List<ContactGuideNarr>) criteria.list();
		if (contactGuideNarrs.size() > 0) {
			ContactGuideNarr contactGuideNarr = contactGuideNarrs.get(0);
			contactGuideDto.setDtLastUpdate(contactGuideNarr.getDtLastUpdate());
			contactGuideDto.setIdContactGuideNarr(contactGuideNarr.getIdContactGuideNarr());
			contactGuideDto.setCdGuideRole(contactGuideNarr.getCdGuideRole());
			contactGuideDto.setGuidePlanBlob(contactGuideNarr.getNarrative());
		}
		return contactGuideDto;
	}

	/**
	 * Method Name: fetchGuideTopicsForPerson Method Description: Method to
	 * fetch Guide Topics for person contacted.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ContactGuideDto fetchGuideTopicsForPerson(ContactGuideDto contactGuideDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactGuideTopic.class);
		ContactGuideNarr contactGuideNarr = new ContactGuideNarr();
		contactGuideNarr.setIdContactGuideNarr(contactGuideDto.getIdContactGuideNarr());
		criteria.add(Restrictions.eq("contactGuideNarr", contactGuideNarr));
		List<ContactGuideTopic> contactGuideTopics = (List<ContactGuideTopic>) criteria.list();
		if (CollectionUtils.isEmpty(contactGuideDto.getSelectedGuideTopics())) {
			List<String> selectedGuideTopic = new ArrayList<String>();
			contactGuideDto.setSelectedGuideTopics(selectedGuideTopic);
		}
		for (ContactGuideTopic contactGuideTopic : contactGuideTopics) {
			contactGuideDto.getSelectedGuideTopics().add(contactGuideTopic.getCdGuideTopic());
		}
		return contactGuideDto;
	}

	/**
	 * Method Name: fetchGuidePlanNarr Method Description: Method to fetch
	 * Contact Guide Narrative for Caregiver/Collateral contacted.
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ContactGuideDto fetchGuidePlanNarr(ContactGuideDto contactGuideDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactGuideNarr.class);
		Stage stage = new Stage();
		stage.setIdStage(contactGuideDto.getIdStage());
		criteria.add(Restrictions.eq("stage", stage));
		Event event = new Event();
		event.setIdEvent(contactGuideDto.getIdEvent());
		criteria.add(Restrictions.eq("event", event));
		criteria.add(Restrictions.eq("cdType", contactGuideDto.getGuideplanType()));
		List<ContactGuideNarr> contactGuideNarrs = (List<ContactGuideNarr>) criteria.list();
		if (contactGuideNarrs.size() > 0) {
			ContactGuideNarr contactGuideNarr = contactGuideNarrs.get(0);
			contactGuideDto.setIdContactGuideNarr(contactGuideNarr.getIdContactGuideNarr());
			contactGuideDto.setDtLastUpdate(contactGuideNarr.getDtLastUpdate());
			byte[] blob = contactGuideNarr.getNarrative();
			contactGuideDto.setGuidePlanBlob(blob);
		}
		return contactGuideDto;
	}

}
