/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This class is used to create new alert.
 *Aug 13, 2018- 12:37:56 PM © 2017 Texas Department of Family and
 * Protective Services
 *
 */
package us.tx.state.dfps.service.alert.daoimpl;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.service.alert.dao.AlertDao;
import us.tx.state.dfps.service.common.ServiceConstants;
//import us.tx.state.dfps.service.workload.controller.SDMAssessmentListTest;

@Repository
public class AlertDaoImpl implements AlertDao {
	private static final String CHILD_PLAN_REVIEW_INITIATED_FOR = "Child Plan Review Initiated for ";
	private static final String IS_READY_FOR_REVIEW_AND_COMPLETION = " is ready for review and completion.";
	private static final String CHILD_PLAN_FOR = "Child Plan for ";
	private static final String STRING_PERIOD = ".";
	private static final String DISTRIBUTE_CHILD_PLAN_FOR = "Distribute Child Plan for ";
	private static final String MEDICAL_MENTAL_ASSESSMENT_INFORMATION_HAS_BEEN_UPDATED = "Medical/Mental Assessment information has been updated.";
	private static final String EDUCATION_INFORMATION_HAS_BEEN_UPDATED = "Education information has been updated.";
	private static final String INITIAL_CHILD_PLAN_DUE_FOR = "Initial Child Plan due for ";

	private static final String SDM_SAFETY_ASSESSMENT_TEXT_DESC = "A SDM Safety Assessment was completed in the referring INV/A-R stage";
	private static final String SDM_SAFETY_ASSESSMENT_LONG_TEXT_DESC = "";
	private static final String SDM_DATE_OF_ASSESSMENT = "Date of Assessment: ";
	private static final String SDM_HOUSE_HOLD ="Household: "; 
	private static final String SDM_SAFETY_DECISION ="Safety Decision: "; 
	
	@Autowired
	MessageSource messageSource;

	@Autowired
	SessionFactory sessionFactory;

	/**
	 * 
	 * Method Name: createAlert Method Description: This method is used to
	 * Create New Alert
	 * 
	 * @return
	 */
	@Override
	public Long createAlert(Long idStage, Long idPersonAssigned, Long idPerson, Long idCase, String alertType,
			Date dueDate) {
		Todo todoEntity = new Todo();
		String childName = "";
		todoEntity.setCdTodoType(ServiceConstants.ALERT_TODO);
		todoEntity.setDtTodoDue(new Date());
		todoEntity.setDtLastUpdate(new Date());
		todoEntity.setDtTodoCreated(new Date());
		todoEntity.setDtTodoCompleted(new Date());
		todoEntity.setCdTodoTask(ServiceConstants.CDTASK_CHILD_SERVICE_PLAN);
		// Checking if the Id Person to be assigned is null or not.
		if (!ObjectUtils.isEmpty(idPersonAssigned)) {
			Person personAssigned = (Person) sessionFactory.getCurrentSession().get(Person.class, idPersonAssigned);
			todoEntity.setPersonByIdTodoPersAssigned(personAssigned);
		}
		// Fetching the stage from given stage ID.
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);
		if (stage != null)
			todoEntity.setStage(stage);
		// Fetching the Case from given Id Case.
		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, idCase);
		if (capsCase != null)
			todoEntity.setCapsCase(capsCase);
		// Fetching the Child from Person Table to get the Child Name.
		if (!ObjectUtils.isEmpty(idPerson)) {
			Person child = (Person) sessionFactory.getCurrentSession().get(Person.class, idPerson);
			if (!ObjectUtils.isEmpty(child)) {
				childName = child.getNmPersonFull();
			}
		}

		// Below code it to differentiate different description for different
		// alert
		switch (alertType) {
		case ServiceConstants.EDUCATION_UPDATE:
			todoEntity.setTxtTodoDesc(EDUCATION_INFORMATION_HAS_BEEN_UPDATED);
			break;
		case ServiceConstants.MEDICAL_UPDATE:
			todoEntity.setTxtTodoDesc(MEDICAL_MENTAL_ASSESSMENT_INFORMATION_HAS_BEEN_UPDATED);
			break;
		case ServiceConstants.DISTRIBUTE_CHILD_PLAN:
			todoEntity.setTxtTodoDesc(DISTRIBUTE_CHILD_PLAN_FOR + childName + STRING_PERIOD);
			break;
		case ServiceConstants.CHILD_PLAN_REVIEW_COMPL:
			todoEntity.setTxtTodoDesc(CHILD_PLAN_FOR + childName + IS_READY_FOR_REVIEW_AND_COMPLETION);
			break;
		case ServiceConstants.CHILD_PLAN_REVIEW_INITIATED_NON_EXT:
			todoEntity.setTxtTodoDesc(CHILD_PLAN_REVIEW_INITIATED_FOR + childName + STRING_PERIOD);
			break;
		case ServiceConstants.CHILD_PLAN_REVIEW_INITIATED_EXT:
			todoEntity.setTxtTodoDesc(CHILD_PLAN_REVIEW_INITIATED_FOR + childName + STRING_PERIOD);
			break;
		case ServiceConstants.INITIAL_CHILD_PLAN_DUE:
			todoEntity.setTxtTodoDesc(INITIAL_CHILD_PLAN_DUE_FOR + childName + STRING_PERIOD);
			todoEntity.setDtTodoDue(addOneMonthFromCurrentDate(dueDate));
			todoEntity.setDtTodoCompleted(null);
			break;
		case ServiceConstants.PCSP_UPDATE:
			todoEntity.setTxtTodoDesc(ServiceConstants.PCSP_TEXT_DESC + childName + STRING_PERIOD);
			todoEntity.setTxtTodoLongDesc(ServiceConstants.PCSP_TEXT_DESC + childName + STRING_PERIOD);
			break;
		default:
			break;
		}
		Long idTodo = (Long) sessionFactory.getCurrentSession().save(todoEntity);
		return idTodo;
	}

	private Date addOneMonthFromCurrentDate(Date currentDate) {
		// convert date to calendar
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.DAY_OF_MONTH, 30);
		// convert calendar to date
		Date currentDatePlusOne = c.getTime();
		return currentDatePlusOne;
	}

	@Override
	public	Long createFbssAlert(Long idStage, Long idPersonAssigned, Long userId, Long idCase, String longDesText,
								   String desText){
		
		Todo todoEntity = new Todo();
		todoEntity.setCdTodoType(ServiceConstants.ALERT_TODO);
		todoEntity.setDtTodoDue(new Date());
		todoEntity.setDtLastUpdate(new Date());
		todoEntity.setDtTodoCreated(new Date());
		todoEntity.setDtTodoCompleted(new Date());
		// Checking if the Id Person to be assigned is null or not.
		if (!ObjectUtils.isEmpty(idPersonAssigned)) {
			Person personAssigned = (Person) sessionFactory.getCurrentSession().get(Person.class, idPersonAssigned);
			todoEntity.setPersonByIdTodoPersAssigned(personAssigned);
		}
		// Checking if the Id Person to be assigned is null or not.
		if (!ObjectUtils.isEmpty(userId)) {
			Person personByIdTodoPersCreator = (Person) sessionFactory.getCurrentSession().get(Person.class, userId);
			todoEntity.setPersonByIdTodoPersCreator(personByIdTodoPersCreator);
		}
		// Fetching the stage from given stage ID.
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);
		if (stage != null)
			todoEntity.setStage(stage);
		// Fetching the Case from given Id Case.
		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, idCase);
		if (capsCase != null)
			todoEntity.setCapsCase(capsCase);

		todoEntity.setTxtTodoDesc(desText);
		todoEntity.setTxtTodoLongDesc(longDesText);

		Long idTodo = (Long) sessionFactory.getCurrentSession().save(todoEntity);
		return idTodo;
	}
}
