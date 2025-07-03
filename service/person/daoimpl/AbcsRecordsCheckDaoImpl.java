package us.tx.state.dfps.service.person.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.person.dao.AbcsRecordsCheckDao;
import us.tx.state.dfps.service.recordscheck.dto.AbcsRecordsCheckDto;
import us.tx.state.dfps.service.recordscheck.dto.AddressPersonLinkDto;
import us.tx.state.dfps.service.recordscheck.dto.EmployeePersonDto;
import us.tx.state.dfps.service.recordscheck.dto.ResourceContractInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * AbcsRecordsCheckDaoImpl implemented all operation defined in
 * AbcsRecordsCheckDao Interface to fetch the records from table which are
 * mapped record check screen. Mar 15, 2018- 2:18:50 PM © 2017 Texas Department
 * of Family and Protective Services
 */

@Repository
public class AbcsRecordsCheckDaoImpl implements AbcsRecordsCheckDao {

	@Value("${AbcsRecordsCheckDaoImpl.getResourceContractInfoDtl}")
	String getResourceContractInfoDtlSql;

	@Value("${AbcsRecordsCheckDaoImpl.getStaffContactInfoDtl}")
	String getStaffContactInfoDtlSql;

	@Value("${AbcsRecordsCheckDaoImpl.getEmployeeSupervisorDtl}")
	String getEmployeeSupervisorDtlSql;

	@Value("${AbcsRecordsCheckDaoImpl.getEmployeeWorkPhoneDtl}")
	String getEmployeeWorkPhoneDtlSql;

	@Value("${AbcsRecordsCheckDaoImpl.getPersonAddressDtl}")
	String getPersonAddressDtlSql;

	@Value("${AbcsRecordsCheckDaoImpl.getAbcsRecordsCheckDetails}")
	String getAbcsRecordsCheckDetailsSql;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Method Description: This Method will retrieve the resource contract Info
	 * details form record check, resource address tables by passing idRecCheck
	 * as input.
	 * 
	 * @param idRecCheck
	 * @return ResourceContractInfoDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResourceContractInfoDto getResourceContractInfo(Long idRecCheck) {
		List<ResourceContractInfoDto> resourceContractInfoDtoList = new ArrayList<ResourceContractInfoDto>();
		resourceContractInfoDtoList = (List<ResourceContractInfoDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getResourceContractInfoDtlSql).setParameter("idRecCheck", idRecCheck))
						.addScalar("idContract", StandardBasicTypes.LONG)
						.addScalar("cdCntrctType", StandardBasicTypes.STRING)
						.addScalar("txtEmailAddress", StandardBasicTypes.STRING)
						.addScalar("nmResource", StandardBasicTypes.STRING)
						.addScalar("rsrcAddrStLn1", StandardBasicTypes.STRING)
						.addScalar("rsrcAddrStLn2", StandardBasicTypes.STRING)
						.addScalar("cdRsrcAddrState", StandardBasicTypes.STRING)
						.addScalar("rsrcAddrCity", StandardBasicTypes.STRING)
						.addScalar("rsrcAddrZip", StandardBasicTypes.STRING)
						.addScalar("idrecCheckPerson", StandardBasicTypes.LONG)
						.addScalar("idrecCheckRequestor", StandardBasicTypes.LONG)
						.addScalar("cdRecChecktype", StandardBasicTypes.STRING)
						.addScalar("idBackGroundCheck", StandardBasicTypes.LONG)
						.addScalar("backGroundCheckReqid", StandardBasicTypes.STRING)
						.addScalar("indBgcRsltRecpnt", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(ResourceContractInfoDto.class)).list();
		if (!ObjectUtils.isEmpty(resourceContractInfoDtoList)) {
			return resourceContractInfoDtoList.get(0);
		} else {
			return null;
		}

	}

	/**
	 * Method Description: This Method will retrieve the staff contact Info
	 * details form employee and person tables by passing idPerson as input.
	 * 
	 * @param idPerson
	 * @return EmployeePersonDto
	 */
	@Override
	public EmployeePersonDto getStaffContactInfo(Long idPerson) {
		EmployeePersonDto employeePersonDto = (EmployeePersonDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getStaffContactInfoDtlSql).setParameter("idPerson", idPerson))
						.addScalar("nmEmployeeFirst", StandardBasicTypes.STRING)
						.addScalar("nmEmployeeMiddle", StandardBasicTypes.STRING)
						.addScalar("nmEmployeeLast", StandardBasicTypes.STRING)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("cdEmpBjnEmp", StandardBasicTypes.STRING)
						.addScalar("cdEmployeeClass", StandardBasicTypes.STRING)
						.addScalar("dtEmpLastAssigned", StandardBasicTypes.DATE)
						.addScalar("nbrEmpUnitEmpIn", StandardBasicTypes.STRING)
						.addScalar("cdEmpUnitRegion", StandardBasicTypes.STRING)
						.addScalar("idEmpUnit", StandardBasicTypes.LONG)
						.addScalar("nmEmpOfficeName", StandardBasicTypes.STRING)
						.addScalar("cdEmpOfficeMail", StandardBasicTypes.STRING)
						.addScalar("cdExternalType", StandardBasicTypes.STRING)
						.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
						.addScalar("txtEmailAddress", StandardBasicTypes.STRING)
						.addScalar("txtEmployeeEmailAddress", StandardBasicTypes.STRING)
						.addScalar("cdEmpProgram", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(EmployeePersonDto.class)).uniqueResult();
		return employeePersonDto;
	}

	/**
	 * Method Description: This Method will retrieve the employee supervisor
	 * Info details form employee job history and person tables by passing
	 * idPerson as input.
	 * 
	 * @param idPerson
	 * @return EmployeePersonDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EmployeePersonDto> getEmployeeSupervisorInfo(Long idPerson) {
		List<EmployeePersonDto> employeePersonDtoList = (List<EmployeePersonDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(getEmployeeSupervisorDtlSql).setParameter("idPerson", idPerson))
						.addScalar("idJobPersSupv", StandardBasicTypes.LONG)
						.addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(EmployeePersonDto.class)).list();
		return employeePersonDtoList;
	}

	/**
	 * Method Description: This Method will retrieve the staff worker phone Info
	 * details form person phone table by passing idPerson as input.
	 * 
	 * @param idPerson
	 * @return EmployeePersonDto
	 */
	@Override
	public EmployeePersonDto getEmployeeWorkPhoneInfo(Long idPerson) {
		EmployeePersonDto employeePersonDto = (EmployeePersonDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getEmployeeWorkPhoneDtlSql).setParameter("idPerson", idPerson))
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
						.addScalar("nbrPersonPhoneExtension", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(EmployeePersonDto.class)).uniqueResult();
		return employeePersonDto;
	}

	/**
	 * Method Description: This Method will fetches the current primary address
	 * for a person by passing idPerson as input.
	 * 
	 * @param idPerson
	 * @return AddressPersonLinkDto
	 */
	@Override
	public AddressPersonLinkDto getPersonAddressDtl(Long idPerson) {
		AddressPersonLinkDto addressPersonLinkDto = (AddressPersonLinkDto) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(getPersonAddressDtlSql).setParameter("idPerson", idPerson))
						.addScalar("idAddrPersonLink", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idPersonAddr", StandardBasicTypes.LONG)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("cdPersAddrLinkType", StandardBasicTypes.STRING)
						.addScalar("indPersAddrLinkInvalid", StandardBasicTypes.STRING)
						.addScalar("indPersAddrLinkPrimary", StandardBasicTypes.STRING)
						.addScalar("txtPersAddrCmnts", StandardBasicTypes.STRING)
						.addScalar("dtPersAddrLinkStart", StandardBasicTypes.DATE)
						.addScalar("dtPersAddrLinkEnd", StandardBasicTypes.DATE)
						.addScalar("idPersonMerge", StandardBasicTypes.LONG)
						.addScalar("idPersonAddrPrsnAddr", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdatePrsnAddr", StandardBasicTypes.DATE)
						.addScalar("addrPersonAddrZip", StandardBasicTypes.STRING)
						.addScalar("cdPersonAddrState", StandardBasicTypes.STRING)
						.addScalar("nbrPersonAddrHash", StandardBasicTypes.INTEGER)
						.addScalar("addrPersonAddrCity", StandardBasicTypes.STRING)
						.addScalar("addrPersonAddrAttn", StandardBasicTypes.STRING)
						.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
						.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING)
						.addScalar("cdPersonAddrCounty", StandardBasicTypes.STRING)
						.addScalar("dtPurgedPrsnAddr", StandardBasicTypes.DATE)
						.addScalar("nmCnty", StandardBasicTypes.STRING).addScalar("nmCntry", StandardBasicTypes.STRING)
						.addScalar("nbrGcdLat", StandardBasicTypes.BIG_DECIMAL)
						.addScalar("nbrGcdLong", StandardBasicTypes.BIG_DECIMAL)
						.addScalar("cdAddrRtrn", StandardBasicTypes.STRING)
						.addScalar("cdGcdRtrn", StandardBasicTypes.STRING)
						.addScalar("indValdtd", StandardBasicTypes.STRING)
						.addScalar("dtValdtd", StandardBasicTypes.DATE)
						.addScalar("txtMailbltyScore", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(AddressPersonLinkDto.class)).uniqueResult();
		return addressPersonLinkDto;
	}

	@Override
	public AbcsRecordsCheckDto getAbcsRecordsCheckDetails(Long idRecCheck) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getAbcsRecordsCheckDetailsSql);
		query.setParameter("idRecCheck", idRecCheck);
		query.addScalar("subjectName", StandardBasicTypes.STRING);
		query.addScalar("abcsAccount", StandardBasicTypes.STRING);
		query.addScalar("pid", StandardBasicTypes.STRING);
		query.addScalar("resourceId", StandardBasicTypes.STRING);
		query.addScalar("dtRecCheckRequest", StandardBasicTypes.DATE);

		query.setResultTransformer(Transformers.aliasToBean(AbcsRecordsCheckDto.class));
		List<AbcsRecordsCheckDto> abcsRecordsCheckDtoList = query.list();
		if (abcsRecordsCheckDtoList != null && !abcsRecordsCheckDtoList.isEmpty()) {
			return abcsRecordsCheckDtoList.get(0);
		} else {
			return null;
		}
	}

}

