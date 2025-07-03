package us.tx.state.dfps.service.pal.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.Pal;
import us.tx.state.dfps.common.domain.PalFollowUp;
import us.tx.state.dfps.common.domain.PalPublicAssist;
import us.tx.state.dfps.common.domain.PalPublicAssistId;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.pal.dao.PalFollowUpDao;
import us.tx.state.dfps.service.pal.dto.PalFollowUpDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Implementation for PalFollowUpDao. Oct 9, 2017- 11:27:17 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class PalFollowUpDaoImpl implements PalFollowUpDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: selectPal Method Description: Retrieves information from
	 * fields CD_NO_ILS_REASON and DT_TRAINING_CMPLTD in from the PAL table.
	 * 
	 * @param palFollowUpDto
	 * @return PalFollowUpDto
	 */
	@Override
	public PalFollowUpDto selectPal(PalFollowUpDto palFollowUpDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Pal.class);
		criteria.add(Restrictions.eq("idPalStage", palFollowUpDto.getIdPalFollupStage()));
		Pal pal = (Pal) criteria.uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(pal)) {
			if (TypeConvUtil.isNullOrEmpty(pal.getCdNoIlsReason())
					|| TypeConvUtil.isNullOrEmpty(pal.getDtTrainingCmpltd())) {
				throw new DataNotFoundException(messageSource.getMessage("common.pal.data", null, Locale.US));
			} else {
				palFollowUpDto.setNoIlsReason(pal.getCdNoIlsReason());
				palFollowUpDto.setDtTrainingCmpltd(pal.getDtTrainingCmpltd());
			}
		}
		return palFollowUpDto;
	}

	/**
	 * Method Name: selectPalFollowUp Method Description: Queries the specified
	 * Pal Follow Up records from the database.
	 * 
	 * @param palFollowUpDto
	 * @return PalFollowUpDto
	 */
	@Override
	public PalFollowUpDto selectPalFollowUp(PalFollowUpDto palFollowUpDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PalFollowUp.class);
		criteria.add(Restrictions.eq("event.idEvent", palFollowUpDto.getIdEvent()));
		PalFollowUp palFollowUp = (PalFollowUp) criteria.uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(palFollowUp)) {
			if (!TypeConvUtil.isNullOrEmpty(palFollowUp.getStage())) {
				palFollowUpDto.setIdPalFollupStage(palFollowUp.getStage().getIdStage());
			}
			palFollowUpDto.setDtLastUpdate(palFollowUp.getDtLastUpdate());
			palFollowUpDto.setDtPalFollupDate(palFollowUp.getDtPalFollupDate());
			palFollowUpDto.setIdCase(palFollowUp.getIdCase());
			palFollowUpDto.setPalFollupEducStat(palFollowUp.getCdPalFollupEducStat());
			palFollowUpDto.setPalFollupEmployed(palFollowUp.getCdPalFollupEmployed());
			palFollowUpDto.setPalFollupHighestEdu(palFollowUp.getCdPalFollupHighestEdu());
			palFollowUpDto.setPalFollupLivArr(palFollowUp.getCdPalFollupLivArr());
			palFollowUpDto.setPalFollupMarital(palFollowUp.getCdPalFollupMarital());
			palFollowUpDto.setPalFollupReunified(palFollowUp.getCdPalFollupReunified());
			palFollowUpDto.setIdPalFollowUp(palFollowUp.getIdPalFollowUp());
			palFollowUpDto.setIndPalFollupNoPubAst(palFollowUp.getIndPalFollupNoPubAst());
			if (TypeConvUtil.isNullOrEmpty(palFollowUp.getIndPalFollupNoPubAst())
					|| palFollowUp.getIndPalFollupNoPubAst().equals(ServiceConstants.AR_NO)) {
				executeQueryExistingPublicAssist(palFollowUpDto);
			}
			if (TypeConvUtil.isNullOrEmpty(palFollowUp.getIndPalFollupNotLocate())
					|| palFollowUp.getIndPalFollupNotLocate().equals(ServiceConstants.AR_NO)) {
				palFollowUpDto.setIndPalFollupNotLocate(Boolean.FALSE);
			} else {
				palFollowUpDto.setIndPalFollupNotLocate(Boolean.TRUE);
			}
			if (!TypeConvUtil.isNullOrEmpty(palFollowUp.getNbrPalFollupNumChldrn())) {
				palFollowUpDto.setNbrPalFollupNumchldrn(Long.valueOf(palFollowUp.getNbrPalFollupNumChldrn()));
			}
			if (!TypeConvUtil.isNullOrEmpty(palFollowUp.getIndCaringAdult())) {
				palFollowUpDto.setIndPalFollupCaringAdult(palFollowUp.getIndCaringAdult());
			}
			if (!TypeConvUtil.isNullOrEmpty(palFollowUp.getCdNonReport())) {
				palFollowUpDto.setNonReport(palFollowUp.getCdNonReport());
			}
			if (!TypeConvUtil.isNullOrEmpty(palFollowUp.getCdInternship())) {
				palFollowUpDto.setInternship(palFollowUp.getCdInternship());
			}
			if (!TypeConvUtil.isNullOrEmpty(palFollowUp.getCdHomeless())) {
				palFollowUpDto.setHomeless(palFollowUp.getCdHomeless());
			}
			if (!TypeConvUtil.isNullOrEmpty(palFollowUp.getCdReferralAbuse())) {
				palFollowUpDto.setReferralAbuse(palFollowUp.getCdReferralAbuse());
			}
			if (!TypeConvUtil.isNullOrEmpty(palFollowUp.getCdIncarcerated())) {
				palFollowUpDto.setIncarcerated(palFollowUp.getCdIncarcerated());
			}
		}
		return palFollowUpDto;
	}

	/**
	 * Method Name: insertPalFollowUp Method Description: inserts all records
	 * that make up a pal_follow_up .
	 * 
	 * @param palFollowUpDto
	 * @return PalFollowUpDto @
	 */
	@Override
	public PalFollowUpDto insertPalFollowUp(PalFollowUpDto palFollowUpDto) {

		palFollowUpDto = insertPalFollowUpEvent(palFollowUpDto);
		palFollowUpDto = insertFUPEventPersonLink(palFollowUpDto);
		palFollowUpDto = insertFUPPalFollowUp(palFollowUpDto);
		String indPalPublicAssist = palFollowUpDto.getIndPalPublicAssist();
		if ((!TypeConvUtil.isNullOrEmpty(indPalPublicAssist)) && indPalPublicAssist.equals(ServiceConstants.AR_YES)) {
			palFollowUpDto = getPalFollowUpId(palFollowUpDto);
			palFollowUpDto = insertFUPPalPublicAssist(palFollowUpDto);
		}
		return palFollowUpDto;
	}

	/**
	 * Method Name: insertPalFollowUpEvent Method Description: Queries the
	 * specified guide topic from the database.
	 * 
	 * @param palFollowUpDto
	 * @return
	 */
	private PalFollowUpDto insertPalFollowUpEvent(PalFollowUpDto palFollowUpDto) {

		Event event = new Event();
		event.setDtLastUpdate(new Date());
		event.setDtEventCreated(new Date());

		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, palFollowUpDto.getIdPalFollupStage());

		event.setStage(stage);
		event.setCdEventType(ServiceConstants.FUP);
		event.setIdCase(palFollowUpDto.getIdCase());

		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, palFollowUpDto.getIdPerson());
		event.setPerson(person);
		event.setCdTask(ServiceConstants.CD_TASK);
		event.setTxtEventDescr(ServiceConstants.TXT_EVENT_DESCR);
		event.setDtEventOccurred(new Date());
		event.setCdEventStatus(ServiceConstants.EVENT_COMPLETE);

		palFollowUpDto.setIdEvent((Long) sessionFactory.getCurrentSession().save(event));
		return palFollowUpDto;
	}

	/**
	 * Method Name: insertFUPPalPublicAssist Method Description: Inserts into
	 * Pal_Public_Assist all rows that have been checked in the assistance
	 * Received section of the PAL FOLLOW UP page
	 * 
	 * @param palFollowUpDto
	 * @return
	 */
	private PalFollowUpDto insertFUPPalPublicAssist(PalFollowUpDto palFollowUpDto) {

		for (int i = 0; i < palFollowUpDto.getPalPublicAssistList().size(); i++) {
			if (palFollowUpDto.getPalPublicAssistList().get(i).equals(ServiceConstants.NONE_APPLY)) {
				palFollowUpDto.setIndPalFollupNoPubAst(ServiceConstants.AR_NO);
				continue;
			}

			PalPublicAssist palpublicassist = new PalPublicAssist();
			palpublicassist.setId(new PalPublicAssistId());

			if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getIdPalFollupStage())) {
				palpublicassist.getId().setIdPalFollowUp(palFollowUpDto.getIdPalFollupStage());
			}
			palpublicassist.setDtLastUpdate(new Date());

			if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getPalPublicAssistList().get(i))) {
				palpublicassist.getId().setCdPalPublicAssist(palFollowUpDto.getPalPublicAssistList().get(i).toString());
			}

			if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getIdPalFollowUp())) {
				palpublicassist.getId().setIdPalFollowUp(palFollowUpDto.getIdPalFollowUp());
			}

			sessionFactory.getCurrentSession().save(palpublicassist);
		}

		return palFollowUpDto;
	}

	/**
	 * Method Name: getPalFollowUpId Method Description: Gets the the primary
	 * key created in insertFUPPalFollowUp in creating the new rows in
	 * pal_public_assist
	 * 
	 * @param palFollowUpDto
	 * @return
	 */
	private PalFollowUpDto getPalFollowUpId(PalFollowUpDto palFollowUpDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PalFollowUp.class);
		criteria.add(Restrictions.eq("event.idEvent", palFollowUpDto.getIdEvent()));
		PalFollowUp palFollowUp = (PalFollowUp) criteria.uniqueResult();
		palFollowUpDto.setIdPalFollowUp(palFollowUp.getIdPalFollowUp());
		return palFollowUpDto;
	}

	/**
	 * Method Name: insertFUPPalFollowUp Method Description: Inserts a row into
	 * PAL_FOLLOW_UP.
	 * 
	 * @param palFollowUpDto
	 * @return
	 */
	private PalFollowUpDto insertFUPPalFollowUp(PalFollowUpDto palFollowUpDto) {

		PalFollowUp palFollowUp = new PalFollowUp();
		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getIdPalFollupStage())) {
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class,
					palFollowUpDto.getIdPalFollupStage());
			palFollowUp.setStage(stage);
		}

		palFollowUp.setDtLastUpdate(new Date());

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getDtPalFollupDate())) {
			palFollowUp.setDtPalFollupDate(palFollowUpDto.getDtPalFollupDate());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getIdCase())) {
			palFollowUp.setIdCase(palFollowUpDto.getIdCase());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getPalFollupEducStat())) {
			palFollowUp.setCdPalFollupEducStat(palFollowUpDto.getPalFollupEducStat());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getPalFollupEmployed())) {
			palFollowUp.setCdPalFollupEmployed(palFollowUpDto.getPalFollupEmployed());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getPalFollupHighestEdu())) {
			palFollowUp.setCdPalFollupHighestEdu(palFollowUpDto.getPalFollupHighestEdu());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getPalFollupLivArr())) {
			palFollowUp.setCdPalFollupLivArr(palFollowUpDto.getPalFollupLivArr());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getPalFollupMarital())) {
			palFollowUp.setCdPalFollupMarital(palFollowUpDto.getPalFollupMarital());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getPalFollupReunified())) {
			palFollowUp.setCdPalFollupReunified(palFollowUpDto.getPalFollupReunified());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getIndPalFollupNoPubAst())) {
			palFollowUp.setIndPalFollupNoPubAst(palFollowUpDto.getIndPalFollupNoPubAst());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getIndPalFollupNotLocate())) {
			if (palFollowUpDto.getIndPalFollupNotLocate()) {
				palFollowUp.setIndPalFollupNotLocate(ServiceConstants.AR_YES);
			} else {
				palFollowUp.setIndPalFollupNotLocate(ServiceConstants.AR_NO);
			}
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getNbrPalFollupNumchldrn())) {
			palFollowUp.setNbrPalFollupNumChldrn(palFollowUpDto.getNbrPalFollupNumchldrn().byteValue());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getIdEvent())) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, palFollowUpDto.getIdEvent());
			palFollowUp.setEvent(event);
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getIndPalFollupCaringAdult())) {
			palFollowUp.setIndCaringAdult(palFollowUpDto.getIndPalFollupCaringAdult());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getNonReport())) {
			if (palFollowUpDto.getNonReport().equals(ServiceConstants.AR_YES)) {
				palFollowUp.setCdNonReport(ServiceConstants.AR_NO);
			} else {
				if (palFollowUpDto.getIndPalPublicAssist().equals(ServiceConstants.AR_NO)) {
					palFollowUp.setCdNonReport(ServiceConstants.AR_YES);
				} else {
					palFollowUp.setCdNonReport(ServiceConstants.AR_NO);
				}
			}
			palFollowUp.setCdNonReport(palFollowUpDto.getNonReport());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getInternship())) {
			palFollowUp.setCdInternship(palFollowUpDto.getInternship());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getHomeless())) {
			palFollowUp.setCdHomeless(palFollowUpDto.getHomeless());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getReferralAbuse())) {
			palFollowUp.setCdReferralAbuse(palFollowUpDto.getReferralAbuse());
		}

		if (!TypeConvUtil.isNullOrEmpty(palFollowUpDto.getIncarcerated())) {
			palFollowUp.setCdIncarcerated(palFollowUpDto.getIncarcerated());
		}

		sessionFactory.getCurrentSession().save(palFollowUp);
		palFollowUpDto.setIdPalFollowUp(palFollowUp.getIdPalFollowUp());
		return palFollowUpDto;
	}

	/**
	 * Method Name: insertFUPEventPersonLink Method Description: Queries the
	 * specified guide topic from the database.
	 * 
	 * @param palFollowUpDto
	 * @return
	 */
	private PalFollowUpDto insertFUPEventPersonLink(PalFollowUpDto palFollowUpDto) {

		EventPersonLink eventPersonLink = new EventPersonLink();
		eventPersonLink.setIdEventPersLink(ServiceConstants.Zero_Value);
		eventPersonLink.setDtLastUpdate(new Date());
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, palFollowUpDto.getIdPerson());
		eventPersonLink.setPerson(person);
		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, palFollowUpDto.getIdEvent());

		eventPersonLink.setEvent(event);
		eventPersonLink.setIdCase(palFollowUpDto.getIdCase());
		eventPersonLink.setCdFamPlanPermGoal(null);
		eventPersonLink.setDtFamPlanPermGoalTarget(null);

		sessionFactory.getCurrentSession().save(eventPersonLink);
		return palFollowUpDto;
	}

	/**
	 * Method Name: updatePal Method Description: Updates Pal table.
	 * 
	 * @param palFollowUpDto
	 * @return PalFollowUpDto @
	 */
	@Override
	public PalFollowUpDto updatePal(PalFollowUpDto palFollowUpDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Pal.class);
		criteria.add(Restrictions.eq("idPalStage", palFollowUpDto.getIdPalFollupStage()));
		Pal pal = (Pal) criteria.uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(pal)) {
			pal.setCdNoIlsReason(palFollowUpDto.getNoIlsReason());
			pal.setDtTrainingCmpltd(palFollowUpDto.getDtTrainingCmpltd());
			sessionFactory.getCurrentSession().saveOrUpdate(pal);
		}

		return palFollowUpDto;
	}

	private void executeQueryExistingPublicAssist(PalFollowUpDto palFollowUpDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PalPublicAssist.class);
		criteria.add(Restrictions.eqOrIsNull("palFollowUp.idPalFollowUp", palFollowUpDto.getIdPalFollowUp()));
		ProjectionList requiredColumns = Projections.projectionList();
		requiredColumns.add(Projections.property("id.cdPalPublicAssist"));
		criteria.setProjection(requiredColumns);
		List<?> cdPalPublicAssistList = criteria.list();
		List<String> palPublicAssistList = new ArrayList<>();
		for (Object assist : cdPalPublicAssistList) {
			palPublicAssistList.add(assist.toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(palPublicAssistList)) {
			palFollowUpDto.setPalPublicAssistList(palPublicAssistList);
			palFollowUpDto.setIndPalPublicAssist(ServiceConstants.STRING_IND_Y);
		}
	}
}
