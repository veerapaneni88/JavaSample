package us.tx.state.dfps.service.arfamilynotification.daoimpl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.ConclusionNotifctnInfo;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.arfamilynotification.dao.ARCnclnFamilyNotifDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Dao
 * implementation class for ARCnclnFamilyNotifDao> Apr 11, 2018- 9:32:39 AM ©
 * 2017 Texas Department of Family and Protective Services
 * ********Change History**********
 * 02/07/2023 thompswa artf238090 PPM 73576 add getConclusionNotifctnInfo.
 */

@Repository
public class ARCnclnFamilyNotifDaoImpl implements ARCnclnFamilyNotifDao {

	@Value("${ARCnclnFamilyNotifDaoImpl.getARStageDateBegin}")
	private transient String getARStageDatesql;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	PersonDao personDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	CaseUtils caseUtils;

	private static final Logger log = Logger.getLogger(ARCnclnFamilyNotifDaoImpl.class);

	public ARCnclnFamilyNotifDaoImpl() {

	}

	/**
	 * 
	 * Method Name: getARStageBegin Method Description: get date stage start
	 * 
	 * @param idCase
	 * @return StageDto
	 */

	@Override
	public StageDto getARStageBegin(Long idCase) {
		
		StageDto stageDto = new StageDto();
		SQLQuery sqlQuery=((SQLQuery)sessionFactory.getCurrentSession().createSQLQuery(getARStageDatesql)
				.setParameter("idCase", idCase));
		Date dtStageStart = (Date) sqlQuery.uniqueResult();
		stageDto.setDtStageStart(dtStageStart);
		return stageDto;
	}

	/**
	 *
	 * Method Name: getPersonAddrInfo Method Description: fetch person name and
	 * suffix based on person id
	 *
	 * @param idPerson
	 * @return PersonDto
	 */
	@Override
	public PersonDto getPersonAddrInfo(Long idPerson) {

		Person person = personDao.getPerson(idPerson);
		PersonDto personDto = new PersonDto();
		personDto.setNmPersonFirst(person.getNmPersonFirst());
		personDto.setNmPersonMiddle(person.getNmPersonMiddle());
		personDto.setNmPersonLast(person.getNmPersonLast());
		personDto.setCdPersonSuffix(person.getCdPersonSuffix());
		return personDto;
	}

	/**
	 *
	 * Method Name: getConclusionNotifctnInfo Method Description: fetch event and
	 * 	 * ConclusionNotifctnInfo based on stage id and person id and event cd_type
	 *
	 * @param idStage
	 * @param idPerson
	 * @return anonymous ConclusionNotifctnInfo
	 */
	@Override
	public ConclusionNotifctnInfo getConclusionNotifctnInfo(Long idStage, Long idPerson) {
		Event event = caseUtils.getEvent(idStage, ServiceConstants.AR_CONCLUSION_TASK_CODE);
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConclusionNotifctnInfo.class);
		criteria.add(Restrictions.eq("idEvent", BigDecimal.valueOf(event.getIdEvent())))
				.add(Restrictions.eq("idPerson", BigDecimal.valueOf(idPerson)))
				.addOrder(Order.desc("dtCreated"));
		List<ConclusionNotifctnInfo> results= (List<ConclusionNotifctnInfo>) criteria.list();
		if (results.isEmpty()){
			return null;
		}
		return results.get(0);
	}
}
