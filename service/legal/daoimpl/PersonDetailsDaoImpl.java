package us.tx.state.dfps.service.legal.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dao.PersonDetailsDao;
import us.tx.state.dfps.service.legal.dto.PersonDetailsdiDto;
import us.tx.state.dfps.service.legal.dto.PersonDetailsdoDto;
import us.tx.state.dfps.service.legal.dto.ServiceOutputDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * Retrieves a full row from the person table Aug 5, 2017- 11:15:40 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class PersonDetailsDaoImpl implements PersonDetailsDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private LookupDao lookupDao;

	@Value("${Ccmn44dDaoImpl.getPersonRecord}")
	private String getPersonRecord;

	private static final Logger log = Logger.getLogger(PersonDetailsDaoImpl.class);

	/**
	 * 
	 * Method Name: getPersonRecord Method Description: fetch person record
	 * 
	 * @param personDetailsdiDto
	 * @return List<PersonDetailsdoDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonDetailsdoDto> getPersonRecord(PersonDetailsdiDto personDetailsdiDto) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonRecord)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdDeathRsnCps", StandardBasicTypes.STRING)
				.addScalar("cdMannerDeath", StandardBasicTypes.STRING)
				.addScalar("cdDeathCause", StandardBasicTypes.STRING)
				.addScalar("cdDeathAutpsyRslt", StandardBasicTypes.STRING)
				.addScalar("cdDeathFinding", StandardBasicTypes.STRING)
				.addScalar("txtFatalityDetails", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE).addScalar("dtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("nbrPersonAge", StandardBasicTypes.SHORT)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("cdNmPersonSuffix", StandardBasicTypes.STRING)
				.addScalar("txtOccupation", StandardBasicTypes.STRING)
				.addScalar("cdOccupation", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdDisasterRlf", StandardBasicTypes.STRING)
				.addScalar("indEducationPortfolio", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PersonDetailsdoDto.class)));

		sQLQuery1.setParameter("hI_ulIdPerson", personDetailsdiDto.getIdPerson());

		List<PersonDetailsdoDto> personDetailsdoDtos = (List<PersonDetailsdoDto>) sQLQuery1.list();

		if (TypeConvUtil.isNullOrEmpty(personDetailsdoDtos) && personDetailsdoDtos.size() == 0) {
			throw new DataNotFoundException(
					messageSource.getMessage("Ccmn44dDaoImpl.person.record.not.found", null, Locale.US));
		}


		return personDetailsdoDtos;
	}

	/**
	 * Method Name: getPersonInformation Method Description: This method is
	 * called from RecordCheckRetrieve EJB used to get Person Details.
	 * 
	 * @param personDetailsdiDto
	 * @return List<PersonDetailsdoDto>
	 */
	@Override
	public PersonDetailsdoDto getPersonInformation(PersonDetailsdiDto personDetailsdiDto) {
		PersonDetailsdoDto personDetailsdoDto = new PersonDetailsdoDto();
		int rowCount = ServiceConstants.Zero;
		int pageNum = personDetailsdiDto.getServiceInputDto().getUsPageNbr();
		if (pageNum <= ServiceConstants.Zero)
			pageNum = ServiceConstants.One;

		int pageSize = personDetailsdiDto.getServiceInputDto().getUlPageSizeNbr();
		if (pageSize <= ServiceConstants.Zero)
			pageSize = ServiceConstants.Fifty_Value;

		int firstRow = ServiceConstants.One + ((pageNum - ServiceConstants.One) * pageSize);
		int lastRow = pageNum * pageSize;
		int index = ServiceConstants.Zero;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Person.class);
		criteria.add(Restrictions.eq("idPerson", personDetailsdiDto.getIdPerson()));
		Person person = (Person) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(person)) {
			throw new DataNotFoundException(lookupDao.getMessageByNumber("1403"));
		}
		ServiceOutputDto serviceOutputDto = new ServiceOutputDto();

		index++;
		if (index <= lastRow && index >= firstRow) {

			personDetailsdoDto.setCdPersonDeath(person.getCdPersonDeath());
			personDetailsdoDto.setCdDeathRsnCps(person.getCdDeathRsnCps());
			personDetailsdoDto.setCdMannerDeath(person.getCdMannerDeath());
			personDetailsdoDto.setCdDeathCause(person.getCdDeathCause());
			personDetailsdoDto.setCdDeathAutpsyRslt(person.getCdDeathAutpsyRslt());
			personDetailsdoDto.setCdDeathFinding(person.getCdDeathFinding());
			personDetailsdoDto.setTxtFatalityDetails(person.getTxtFatalityDetails());
			personDetailsdoDto.setCdPersonEthnicGroup(person.getCdPersonEthnicGroup());
			personDetailsdoDto.setCdPersonLanguage(person.getCdPersonLanguage());
			personDetailsdoDto.setCdPersonMaritalStatus(person.getCdPersonMaritalStatus());
			personDetailsdoDto.setCdPersonReligion(person.getCdPersonReligion());
			personDetailsdoDto.setCCdPersonSex(person.getCdPersonSex());
			personDetailsdoDto.setCdPersonStatus(person.getCdPersonStatus());
			personDetailsdoDto.setDtPersonBirth(person.getDtPersonBirth());
			personDetailsdoDto.setDtPersonDeath(person.getDtPersonDeath());
			personDetailsdoDto.setNbrPersonAge(person.getNbrPersonAge());
			personDetailsdoDto.setNmPersonFull(person.getNmPersonFull());
			personDetailsdoDto.setTxtOccupation(person.getTxtPersonOccupation());
			personDetailsdoDto.setBIndPersCancelHist(person.getIndPersCancelHist());
			personDetailsdoDto.setCdPersGuardCnsrv(person.getCdPersGuardCnsrv());
			personDetailsdoDto.setTsLastUpdate(person.getDtLastUpdate());
			personDetailsdoDto.setCdPersonLivArr(person.getCdPersonLivArr());
			personDetailsdoDto.setBCdPersonChar(person.getCdPersonChar());
			personDetailsdoDto.setBIndPersonDobApprox(person.getIndPersonDobApprox());
			personDetailsdoDto.setCdDisasterRlf(person.getCdDisasterRlf());
			personDetailsdoDto.setCdOccupation(person.getCdOccupation());
			personDetailsdoDto.setNmPersonFirst(person.getNmPersonFirst());
			personDetailsdoDto.setNmPersonMiddle(person.getNmPersonMiddle());
			personDetailsdoDto.setNmPersonLast(person.getNmPersonLast());
			personDetailsdoDto.setCdNmPersonSuffix(person.getCdPersonSuffix());
			rowCount++;
			if (index > pageNum * pageSize) {
				serviceOutputDto.setMoreDataInd(ServiceConstants.Y);
			} else {
				serviceOutputDto.setMoreDataInd(ServiceConstants.N);
			}
			serviceOutputDto.setRowQty(rowCount);
			personDetailsdoDto.setServiceOutputDto(serviceOutputDto);
		}

		return personDetailsdoDto;
	}

}
