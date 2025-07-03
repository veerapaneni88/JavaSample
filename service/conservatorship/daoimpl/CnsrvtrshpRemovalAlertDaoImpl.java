/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 30, 2017- 8:45:01 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.conservatorship.daoimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Employee;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.domain.Unit;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.dao.UnitDao;
import us.tx.state.dfps.service.conservatorship.dao.CnsrvtrshpRemovalAlertDao;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 30, 2017- 8:45:01 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class CnsrvtrshpRemovalAlertDaoImpl implements CnsrvtrshpRemovalAlertDao {

	@Autowired
	SessionFactory sessionFactory;

	@Value("${CnsrvtrshpRemovalAlertDaoImpl.getAlerQuery}")
	private String getAlertQuerySql;

	@Autowired
	StageDao stageDao;

	@Autowired
	UnitDao unitDao;

	public CnsrvtrshpRemovalAlertDaoImpl() {

	}

	/**
	 * Method Name: CVSRemovalAlert Method Description: This method will trigger
	 * alert in “CVS Removal” page
	 * 
	 * @param cnsrvtrshpRemovalAlertReq
	 * @param idEvent
	 * @param idPerson
	 * @return @
	 */
	public String getAlertForCVSRemoval(Long idStage, String stageProgram, Long idCase, Long idTodoEvent,
			Long idToDoPersCreator) {
		boolean isAlertSend = false;
		StageDto stageDto = stageDao.getStageById(idStage);
		if (!ObjectUtils.isEmpty(stageDto) && !StringUtils.isEmpty(stageDto.getIdUnit())) {
			Unit unit = unitDao.getUnitbyid(stageDto.getIdUnit());
			List<String> positionList = getPositionIdbyUnitRegion(unit.getCdUnitRegion());
			if (!ObjectUtils.isEmpty(positionList)) {
				Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Employee.class)
						.add(Restrictions.in("cdEmpBjnEmp", positionList))
						.add(Restrictions.eq("indEmpActiveStatus", ServiceConstants.YES))
						.setProjection(Projections.projectionList().add(Projections.property("idPerson"), "idPerson"));
				List<Long> idPersonList = criteria.list();
				for (Long idPerson : idPersonList) {
					Person personFromRegion = (Person) sessionFactory.getCurrentSession().get(Person.class, idPerson);
					Todo todoEntityOnReg = createTodo(idStage, stageProgram, idCase, idTodoEvent, idToDoPersCreator);
					if (personFromRegion != null)
						todoEntityOnReg.setPersonByIdTodoPersAssigned(personFromRegion);
					sessionFactory.getCurrentSession().saveOrUpdate(todoEntityOnReg);
					isAlertSend = true;
				}
			}
		}
		return (!isAlertSend) ? ServiceConstants.CONTACT_SUCCESS : ServiceConstants.CONTACT_FAILURE;
	}

	/**
	 * Used to get the position list based on region
	 * 
	 * @param unitRegion
	 * @return
	 */
	private List<String> getPositionIdbyUnitRegion(String unitRegion) {
		List<String> positionList = null;
		if (ServiceConstants.UNIT_REGION_001.equals(unitRegion)) {
			positionList = ServiceConstants.CVS_ALERT_UNIT_001;
		}
		if (ServiceConstants.UNIT_REGION_002.equals(unitRegion)) {
			positionList = ServiceConstants.CVS_ALERT_UNIT_002;
		}
		if (ServiceConstants.UNIT_REGION_003.equals(unitRegion)) {
			positionList = ServiceConstants.CVS_ALERT_UNIT_003;
		}
		if (ServiceConstants.UNIT_REGION_004.equals(unitRegion)) {
			positionList = ServiceConstants.CVS_ALERT_UNIT_004;
		}
		if (ServiceConstants.UNIT_REGION_005.equals(unitRegion)) {
			positionList = ServiceConstants.CVS_ALERT_UNIT_005;
		}
		if (ServiceConstants.UNIT_REGION_006.equals(unitRegion)) {
			positionList = ServiceConstants.CVS_ALERT_UNIT_006;
		}
		if (ServiceConstants.UNIT_REGION_007.equals(unitRegion)) {
			positionList = ServiceConstants.CVS_ALERT_UNIT_007;
		}
		if (ServiceConstants.UNIT_REGION_008.equals(unitRegion)) {
			positionList = ServiceConstants.CVS_ALERT_UNIT_008;
		}
		if (ServiceConstants.UNIT_REGION_009.equals(unitRegion)) {
			positionList = ServiceConstants.CVS_ALERT_UNIT_009;
		}
		if (ServiceConstants.UNIT_REGION_010.equals(unitRegion)) {
			positionList = ServiceConstants.CVS_ALERT_UNIT_010;
		}
		if (ServiceConstants.UNIT_REGION_011.equals(unitRegion)) {
			positionList = ServiceConstants.CVS_ALERT_UNIT_011;
		}
		return positionList;
	}

	/**
	 * Used to insert the alert in todo table
	 * 
	 * @param idStage
	 * @param stageProgram
	 * @param idCase
	 * @param idTodoEvent
	 * @param idToDoPersCreator
	 * @return
	 */
	private Todo createTodo(Long idStage, String stageProgram, Long idCase, Long idTodoEvent, Long idToDoPersCreator) {
		String desc = null;
		Todo todoEntity = new Todo();
		todoEntity.setCdTodoType(ServiceConstants.ALERT_TODO);
		todoEntity.setCdTodoTask(ServiceConstants.CPS_ALERT_TASK);
		todoEntity.setDtTodoDue(new Date());
		todoEntity.setDtLastUpdate(new Date());
		todoEntity.setDtTodoCreated(new Date());

		// in case if event also need to considered uncomment bellow code
		/*
		 * Event event = (Event)
		 * sessionFactory.getCurrentSession().get(Event.class, idTodoEvent);
		 * todoEntity.setEvent(event); Person person = (Person)
		 * sessionFactory.getCurrentSession().get(Person.class,
		 * idToDoPersCreator); todoEntity.setPersonByIdTodoPersCreator(person);
		 */

		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);
		if (stage != null) {
			todoEntity.setStage(stage);
			String queryCodes = (String) sessionFactory.getCurrentSession().createSQLQuery(getAlertQuerySql)
					.addScalar("decode", StandardBasicTypes.STRING).setParameter("codeType", ServiceConstants.CCOUNT)
					.setParameter("code", stage.getCdStageCnty()).uniqueResult();

			desc = ServiceConstants.CONSERVATORSHIP_TXT + queryCodes + ServiceConstants.CONSERVATORSHIP_CC + idCase;
			todoEntity.setTxtTodoDesc(desc);
		}
		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, idCase);
		if (capsCase != null)
			todoEntity.setCapsCase(capsCase);
		return todoEntity;
	}

}
