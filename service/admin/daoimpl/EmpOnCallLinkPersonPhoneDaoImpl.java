package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.EmpOnCallLinkPersonPhoneDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.EmpOnCallLinkPersonPhoneInDto;
import us.tx.state.dfps.service.workload.dto.EmpOnCallLinkPersonPhoneOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:The purpose
 * of this class (Ccmn21dDaoImpl) is to retrieve all rows of the
 * EMP_ON_CALL_LINK table given a particular ID_ON_CALL, and also retrieve the
 * NM_PERSON_FULL, and HOME telephone number of each ID_PERSON found in the
 ** EMP_ON_CALL_LINK table for the given ID_ON_CALL. Aug 2, 2017- 8:38:12 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class EmpOnCallLinkPersonPhoneDaoImpl implements EmpOnCallLinkPersonPhoneDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EmpOnCallLinkPersonPhoneDaoImpl.getPersonDetails}")
	private transient String getPersonDetails;

	private static final Logger log = Logger.getLogger(EmpOnCallLinkPersonPhoneDaoImpl.class);
	public static final String MAX_DATE_STRING = "31-DEC-4712";

	/**
	 * Method Name: getEmployeeOnCallList Method Description:This method
	 * retrieves data from EMP_ON_CALL_LINK,PERSON and PERSON_PHONE tables.
	 * 
	 * @param empOnCallLinkPersonPhoneInDto
	 * @return List<EmpOnCallLinkPersonPhoneOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EmpOnCallLinkPersonPhoneOutDto> getEmployeeOnCallList(
			EmpOnCallLinkPersonPhoneInDto empOnCallLinkPersonPhoneInDto) {
		log.debug("Entering method EmpOnCalLinkPersonPhoneQUERYdam in EmpOnCalLinkPersonPhoneDaoImpl");
		String maxDate = MAX_DATE_STRING;
		String cdPhoneType = ServiceConstants.PERSON_PHONE_TYPE_HOME;
		// Creating the query to get the employee on call link list.
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonDetails)
				.setResultTransformer(Transformers.aliasToBean(EmpOnCallLinkPersonPhoneOutDto.class)));
		sQLQuery1.addScalar("idPerson", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("empOnCallCntctOrd", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("cdEmpOnCallDesig", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("empOnCallPhone1", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("empOnCallExt1", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("empOnCallPhone2", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("empOnCallExt2", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("idEmpOnCallLink", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idOnCall", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("phoneNumber", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("nmPersonFull", StandardBasicTypes.STRING);

		sQLQuery1.setParameter("maxDate", maxDate);
		sQLQuery1.setParameter("cdPhoneType", cdPhoneType);
		sQLQuery1.setParameter("idOnCall", empOnCallLinkPersonPhoneInDto.getIdOnCall());
		// executing the query to fetch the emp_on_call_link list from the db.
		List<EmpOnCallLinkPersonPhoneOutDto> empOnCallLinkList = (List<EmpOnCallLinkPersonPhoneOutDto>) sQLQuery1
				.list();
		log.debug("Exiting method EmpOnCalLinkPersonPhoneQUERYdam in EmpOnCalLinkPersonPhoneDaoImpl");
		if (TypeConvUtil.isNullOrEmpty(empOnCallLinkList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("EmpOnCallLinkPersonPhoneDaoImpl.getPersonDetails", null, Locale.US));
		}
		return empOnCallLinkList;
	}
}
