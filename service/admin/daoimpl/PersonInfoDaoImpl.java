package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.PersonInfoDao;
import us.tx.state.dfps.service.admin.dto.EmployeeDto;
import us.tx.state.dfps.service.admin.dto.PersonDiDto;
import us.tx.state.dfps.service.admin.dto.PersonDoDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.PersonEmployeeInDto;
import us.tx.state.dfps.xmlstructs.outputstructs.PersonEmployeeOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Retrieve
 * Person name on Person table. Aug 10, 2017- 3:14:21 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class PersonInfoDaoImpl implements PersonInfoDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Ccmn60dDaoImpl.getPersonName}")
	private transient String getPersonName;

	@Value("${PersonInfoDaoImpl.getSupervisor}")
	private transient String getSupervisor;

	@Value("${PersonInfoDaoImpl.getSelectEmployee}")
	private String getSelectEmployeeSql;

	private static final Logger log = Logger.getLogger(PersonInfoDaoImpl.class);

	/**
	 * 
	 * Method Name: getPersonName Method Description:
	 * 
	 * @param personDiDto
	 * @return List<PersonDoDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonDoDto> getPersonName(PersonDiDto personDiDto) {
		log.debug("Entering method getPersonName in Ccmn60dDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonName)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.setParameter("idPerson", personDiDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(PersonDoDto.class)));

		List<PersonDoDto> liCcmn60doDto = (List<PersonDoDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCcmn60doDto) || liCcmn60doDto.size() == ServiceConstants.Zero) {
			throw new DataNotFoundException(
					messageSource.getMessage("Ccmn60dDaoImpl.person.not.found", null, Locale.US));
		}
		log.debug("Exiting method getPersonName in Ccmn60dDaoImpl");
		return liCcmn60doDto;
	}

	/**
	 * 
	 * Method Name: getSupervisor Method Description: This gets an employee's
	 * supervisor name and ID.
	 * 
	 * @param personEmployeeInDto
	 * @return PersonEmployeeOutDto @
	 */
	@Override
	public List<PersonEmployeeOutDto> getSupervisor(PersonEmployeeInDto personEmployeeInDto) {
		log.debug("Entering method getSupervisor in PersonInfoDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSupervisor)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.setParameter("idPerson", personEmployeeInDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(PersonEmployeeOutDto.class)));
		List<PersonEmployeeOutDto> personEmployeeOutDto = (List<PersonEmployeeOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(personEmployeeOutDto) || personEmployeeOutDto.size() == 0) {
			personEmployeeOutDto = new ArrayList<>();
		}
		log.debug("Exiting method getSupervisor in PersonInfoDaoImpl");
		return personEmployeeOutDto;
	}

	@Override
	public EmployeeDto getSelectEmployee(Long personId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSelectEmployeeSql)
				.addScalar("idOffice", StandardBasicTypes.LONG)
				.addScalar("idEmpUnit", StandardBasicTypes.LONG)
				.setParameter("personId", personId)
				.setResultTransformer(Transformers.aliasToBean(EmployeeDto.class));

		return (EmployeeDto) query.uniqueResult();
	}

}
