package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.dto.NameDto;
import us.tx.state.dfps.service.casemanagement.dao.CpsIntakeNotificationDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.cpsinv.dto.CaseDtlsDto;
import us.tx.state.dfps.service.cpsinv.dto.OfficePhoneDto;
import us.tx.state.dfps.service.intake.dto.IncmgDetermFactorsDto;
import us.tx.state.dfps.service.person.dto.IntakeAllegationDto;
import us.tx.state.dfps.service.person.dto.PersonAddrLinkDto;

@Repository
public class CpsIntakeNotificationDaoImpl implements CpsIntakeNotificationDao {

	@Value("${CpsIntakeNotificationDao.getResidenceAddress}")
	String getResidenceAddressByStageID;

	@Value("${CpsIntakeNotificationDao.getWorkerOfficeDetail}")
	String getWorkerOfficeDetailByStageID;

	@Value("${CpsIntakeNotificationDao.getCaseDetailsByStageId}")
	String getCaseDetailsByStageId;

	@Value("${CpsIntakeNotificationDao.getNameAliases}")
	String getNameAliasesByStageId;

	@Value("${CpsIntakeNotificationDao.getAddrInfoByStageId}")
	String getAddrInfo;

	@Value("${CpsIntakeNotificationDao.getAllegationTypeByStageId}")
	String getAllegationTypeByStageId;

	@Value("${CpsIntakeNotificationDao.getIncmgDetermFactors}")
	String getIncmgDetermFactors;

	@Autowired
	private SessionFactory sessionFactory;

	public static final Logger log = Logger.getLogger(CpsIntakeNotificationDaoImpl.class);

	public CpsIntakeNotificationDaoImpl() {
	}

	/**
	 * Method name:getAllegationTypeByStageId Method Description: Returns the
	 * distinct AllegationTypes from Intake Allegation by passing the Stage id
	 * Tuxedo Service Name: CINT42S Tuxedo DAM Name: CINT69D
	 * 
	 * @param idStage
	 * @ @return List<IntakeAllegationDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IntakeAllegationDto> getAllegationTypeByStageId(Long idStage) {
		List<IntakeAllegationDto> intkAllegatnTypes = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAllegationTypeByStageId)
				.addScalar("cdIntakeAllegType", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(IntakeAllegationDto.class));

		intkAllegatnTypes = query.list();

		return intkAllegatnTypes;
	}

	/**
	 * Method Name:getResidenceAddress Method Description: Retreives the
	 * residence address by passing the Stage id Tuxedo Service Name: CINT42S
	 * Tuxedo DAM Name: CINT70D
	 * 
	 * @param idStage
	 * @return
	 * @ @return PersonAddrLinkDto
	 */
	@Override
	public PersonAddrLinkDto getResidenceAddress(Long idStage) {

		PersonAddrLinkDto personAddrLinkDto = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getResidenceAddressByStageID)
				.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrCity", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrState", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrZip", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(PersonAddrLinkDto.class));

		personAddrLinkDto = (PersonAddrLinkDto) query.uniqueResult();

		return personAddrLinkDto;
	}

	/**
	 * Method Name:getWorkerOfficeDetail Method Description: Retreives the
	 * Worker's office details by passing the Stage id Tuxedo Service Name:
	 * CINT42S Tuxedo DAM Name: CINT71D
	 * 
	 * @param idStage
	 * @return
	 * @ @return OfficePhoneDto
	 */
	@Override
	public OfficePhoneDto getWorkerOfficeDetail(Long idStage, String indOfficePrimary) {

		OfficePhoneDto officePhoneDto = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getWorkerOfficeDetailByStageID)
				.addScalar("officePhoneNumber", StandardBasicTypes.LONG)
				.addScalar("officePhoneExtension", StandardBasicTypes.LONG)
				.addScalar("nmOfficeName", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("indOfficePhonePrimary", indOfficePrimary)
				.setResultTransformer(Transformers.aliasToBean(OfficePhoneDto.class));

		officePhoneDto = (OfficePhoneDto) query.uniqueResult();

		return officePhoneDto;
	}

	/**
	 * Method Name:getCaseDetails Method Description:perform a search given
	 * input Id Stage to retreive basic case information, and using input Id
	 * Resource it will retreive Fax Number information for a Resource if it is
	 * available. Tuxedo Service Name: CINT42S Tuxedo DAM Name: CSEC68D
	 * 
	 * @param idStage
	 * @return
	 * @ @return CaseDtlsDto
	 */
	@Override
	public CaseDtlsDto getCaseDetails(Long idStage) {

		CaseDtlsDto caseDtlsDto = null;
		//Modified the code to set the correct variable name (idResource) for warranty defect 12477
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getCaseDetailsByStageId)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("nmResource", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("nmCase", StandardBasicTypes.STRING)
				.addScalar("resourcePhoneType", StandardBasicTypes.STRING)
				.addScalar("resourcePhoneNumber", StandardBasicTypes.STRING)
				.addScalar("resourcePhoneExtension", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CaseDtlsDto.class));

		caseDtlsDto = (CaseDtlsDto) query.uniqueResult();

		return caseDtlsDto;

	}

	/**
	 * Method Name:getNameAliases Method Description:Retrieves all aliases for a
	 * specified stage ID, considering closure and end dating Tuxedo Service
	 * Name: CINT42S Tuxedo DAM Name: CINT64D
	 * 
	 * @param idStage
	 * @return
	 * @ @return List<NameDto>
	 */

	@Override
	public List<NameDto> getNameAliases(Long idStage) {

		List<NameDto> name = null;
		SimpleDateFormat format = new SimpleDateFormat(ServiceConstants.DATE_FORMATT);
		Date szMaxDate = null;
		try {
			szMaxDate = format.parse(ServiceConstants.MAX_JAVA_DATE);
		} catch (ParseException e) {

			log.error(e);
		}
		String indReporter = ServiceConstants.Y;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getNameAliasesByStageId)
				.addScalar("idPerson", StandardBasicTypes.INTEGER).addScalar("firstName", StandardBasicTypes.STRING)
				.addScalar("middleName", StandardBasicTypes.STRING).addScalar("lastName", StandardBasicTypes.STRING)
				.addScalar("nameSuffix", StandardBasicTypes.STRING).addScalar("dtEnd", StandardBasicTypes.DATE)
				.addScalar("dtStart", StandardBasicTypes.DATE).addScalar("idName", StandardBasicTypes.INTEGER)
				.addScalar("invalid", StandardBasicTypes.STRING).addScalar("primary", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).setParameter("idStage", idStage)
				.setParameter("szMaxDate", szMaxDate).setParameter("indReporter", indReporter)
				.setResultTransformer(Transformers.aliasToBean(NameDto.class));

		name = (List<NameDto>) query.list();

		return name;

	}

	/**
	 * Method Name:getAddrInfoByStageId Method Description:Retrieves all aliases
	 * for a specified stage ID, considering closure and end dating Tuxedo
	 * Service Name: CINT42S Tuxedo DAM Name: CINT63D
	 * 
	 * @param idStage
	 * @return
	 * @ @return List<PersonAddrLinkDto>
	 */

	@Override
	public List<PersonAddrLinkDto> getAddrInfoByStageId(Long idStage) {

		SimpleDateFormat format = new SimpleDateFormat(ServiceConstants.DATE_FORMATT);
		Date szMaxDate = null;
		try {
			szMaxDate = format.parse(ServiceConstants.MAX_JAVA_DATE);
		} catch (ParseException e) {

			log.error(e);
		}
		String cdPrnType = ServiceConstants.PRN_TYPE;
		String cdColType = ServiceConstants.COL_TYPE;
		String indReporter = ServiceConstants.Y;

		List<PersonAddrLinkDto> personAddrLinkDtoList = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAddrInfo)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdPersAddrLinkType", StandardBasicTypes.STRING)
				.addScalar("persAddressAttention", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrCity", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrState", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrZip", StandardBasicTypes.STRING)
				.addScalar("persAddrCmnts", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrCounty", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("dtPersAddrLinkStart", StandardBasicTypes.DATE)
				.addScalar("dtPersAddrLinkEnd", StandardBasicTypes.DATE)
				.addScalar("indPersAddrLinkPrimary", StandardBasicTypes.STRING)
				.addScalar("indPersAddrLinkInvalid", StandardBasicTypes.STRING)
				.addScalar("idPersonAddr", StandardBasicTypes.LONG)
				.addScalar("idAddrPersonLink", StandardBasicTypes.LONG).setParameter("idStage", idStage)
				.setParameter("cdPrnType", cdPrnType).setParameter("cdColType", cdColType)
				.setParameter("szMaxDate", szMaxDate).setParameter("indReporter", indReporter)
				.setResultTransformer(Transformers.aliasToBean(PersonAddrLinkDto.class));

		personAddrLinkDtoList = (List<PersonAddrLinkDto>) query.list();

		return personAddrLinkDtoList;
	}

	/**
	 * Method Name:getincmgDetermFactorsById Method Description:Retrieves all
	 * aliases for a specified stage ID, considering closure and end dating
	 * Tuxedo Service Name: CINT42S Tuxedo DAM Name: CINT15D
	 * 
	 * @param idStage
	 * @return
	 * @ @return List<IncmgDetermFactorsDto>
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<IncmgDetermFactorsDto> getincmgDetermFactorsById(Long idStage) {
		List<IncmgDetermFactorsDto> incmgDetermFactors = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getIncmgDetermFactors)
				.addScalar("cdIncmgDeterm", StandardBasicTypes.STRING)
				.addScalar("cdIncmgDetermType", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(IncmgDetermFactorsDto.class));

		incmgDetermFactors = (List<IncmgDetermFactorsDto>) query.list();

		return incmgDetermFactors;
	}

}
