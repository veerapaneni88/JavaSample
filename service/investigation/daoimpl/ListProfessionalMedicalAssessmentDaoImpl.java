package us.tx.state.dfps.service.investigation.daoimpl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.FacilAlleg;
import us.tx.state.dfps.common.domain.ProfAssmtNarr;
import us.tx.state.dfps.service.casepackage.dto.MdclMentalAssmntDtlDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.dao.ListProfessionalMedicalAssessmentDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 *
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class class name
 * :ListProfessionalMedicalAssessmentDaoImpl Description:<enter the description
 * of class> Jan 4, 2017- 1:22:20 PM © 2017 Texas Department of Family and
 * Protective Services
 * 
 * abajis
 */

@Repository
public class ListProfessionalMedicalAssessmentDaoImpl implements ListProfessionalMedicalAssessmentDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${MedicalMentalAssessmentDaoImpl.getPersonDetail}")
	private transient String getPersonDetail;

	@Value("${MedicalMentalAssessmentDaoImpl.getProfessionalAssmt}")
	private transient String getProfessionalAssmt;

	@Value("${MedicalMentalAssessmentDaoImpl.getTodoDtl}")
	private transient String getTodoDtl;

	public ListProfessionalMedicalAssessmentDaoImpl() {

	}
	/*
	 * Method Description: DAM Name CINV47D
	 * 
	 * @List<String> personType
	 * 
	 * @idStage
	 * 
	 * @ returns List<PersonDto>
	 */

	@Override
	public List<PersonDto> getPersonDetails(List<String> personType, Long idStage) {

		StringBuilder personTypes = new StringBuilder();
		personType.forEach(type -> {
			personTypes.append('\'');
			personTypes.append(type);
			personTypes.append('\'');
			personTypes.append(',');
		});
		String personTypesFinal = personTypes.toString();
		personTypesFinal = personTypesFinal.substring(0, personTypesFinal.lastIndexOf(','));
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonDetail)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.setParameterList("szCdStagePersTypeArray", personType).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));

		List<PersonDto> personList = (List<PersonDto>) query.list();
		if (TypeConvUtil.isNullOrEmpty(personList)) {
			return personList;
		}
		return personList;

	}

	/*
	 * Method Description: Select from Professional Assesment table given the id
	 * Event. DAM Name CINV45D
	 * 
	 * @idEvent @ returns ProfessionalAssmt
	 */
	  //  added indResultsPending for artf227810
	@Override
	public MdclMentalAssmntDtlDto getProfessionalAssesmentByEventId(Long idEvent) {

		MdclMentalAssmntDtlDto mdclMentalAssmntDtlDto = new MdclMentalAssmntDtlDto();

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getProfessionalAssmt)
				.addScalar("idPersonPrincipal", StandardBasicTypes.LONG)
				.addScalar("idPersonProfessional", StandardBasicTypes.LONG)
				.addScalar("nameProfAssmtName", StandardBasicTypes.STRING)
				.addScalar("nameProfAssmtPrincipal", StandardBasicTypes.STRING)
				.addScalar("cdProfAssmtApptRsn", StandardBasicTypes.STRING)
				.addScalar("profAssmtFindings", StandardBasicTypes.STRING)
				.addScalar("profAssmtOther", StandardBasicTypes.STRING)
				.addScalar("commentsOther", StandardBasicTypes.STRING)
				.addScalar("dtProfAssmtAppt", StandardBasicTypes.TIMESTAMP)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("addressLine1Other", StandardBasicTypes.STRING)
				.addScalar("addressLine2Other", StandardBasicTypes.STRING)
				.addScalar("cityOther", StandardBasicTypes.STRING).addScalar("zipOther", StandardBasicTypes.STRING)
				.addScalar("stateOther", StandardBasicTypes.STRING).addScalar("countyOther", StandardBasicTypes.STRING)
				.addScalar("numberOther", StandardBasicTypes.STRING)
				.addScalar("extensionOther", StandardBasicTypes.STRING)
				.addScalar("cdApptPurpose", StandardBasicTypes.STRING)
				.addScalar("indResultsPending", StandardBasicTypes.STRING)
				.addScalar("dtApptEnd", StandardBasicTypes.TIMESTAMP).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(MdclMentalAssmntDtlDto.class));
		mdclMentalAssmntDtlDto = (MdclMentalAssmntDtlDto) query.uniqueResult();

		return mdclMentalAssmntDtlDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.investigation.dao.
	 * ListProfessionalMedicalAssessmentDao#getTodoDtl(java.lang.Long)
	 */
	@Override
	public TodoDto getTodoDtl(Long idEvent) {
		TodoDto todoDto = new TodoDto();

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getTodoDtl)
				.addScalar("idTodo", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
				.addScalar("idTodoCase", StandardBasicTypes.LONG).addScalar("idTodoEvent", StandardBasicTypes.LONG)
				.addScalar("idTodoPersCreator", StandardBasicTypes.LONG)
				.addScalar("idTodoStage", StandardBasicTypes.LONG)
				.addScalar("idTodoPersWorker", StandardBasicTypes.LONG)
				.addScalar("dtTodoDue", StandardBasicTypes.TIMESTAMP).addScalar("cdTodoTask", StandardBasicTypes.STRING)
				.addScalar("todoDesc", StandardBasicTypes.STRING)
				.addScalar("todoLongDesc", StandardBasicTypes.STRING)
				.addScalar("dtTodoCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoTaskDue", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoCompleted", StandardBasicTypes.TIMESTAMP)
				.addScalar("nmTodoCreatorInit", StandardBasicTypes.STRING)
				.addScalar("idTodoInfo", StandardBasicTypes.LONG).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(TodoDto.class));
		todoDto = (TodoDto) query.uniqueResult();
		return todoDto;
	}

	/* (non-Javadoc)
	 * @see us.tx.state.dfps.service.investigation.dao.ListProfessionalMedicalAssessmentDao#checkNarrExists(java.lang.Long)
	 */
	@Override
	public boolean checkNarrExists(Long idEvent) {
		ProfAssmtNarr profAssmtNarr = (ProfAssmtNarr) sessionFactory.getCurrentSession().get(ProfAssmtNarr.class, idEvent);
		return !ObjectUtils.isEmpty(profAssmtNarr);
	}

}
