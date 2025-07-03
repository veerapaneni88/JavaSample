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

import us.tx.state.dfps.service.admin.dao.PersonRaceDetailsDao;
import us.tx.state.dfps.service.admin.dto.PersonRaceInDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * Retrieves Person Race information for the person Aug 5, 2017- 11:41:40 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class PersonRaceDetailsDaoImpl implements PersonRaceDetailsDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonRaceDetailsDaoImpl.getRaceDetails}")
	private String getRaceDetails;

	private static final Logger log = Logger.getLogger(PersonRaceDetailsDaoImpl.class);

	public PersonRaceDetailsDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getRaceDetails Method Description: This method will get data
	 * from Person Race table. Clss79d
	 * 
	 * @param personRaceInDto
	 * @return List<PersonRaceOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonRaceOutDto> getRaceDetails(PersonRaceInDto personRaceInDto) {
		log.debug("Entering method getRaceDetails in PersonRaceDetailsDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRaceDetails)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdPersonRace", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PersonRaceOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdPerson", personRaceInDto.getIdPerson());
		List<PersonRaceOutDto> personRaceOutDtos = (List<PersonRaceOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(personRaceOutDtos) && personRaceOutDtos.size() == 0) {
			throw new DataNotFoundException(
					messageSource.getMessage("Clss79dDaoImpl.personrace.not.found", null, Locale.US));
		}
		log.debug("Exiting method getRaceDetails in PersonRaceDetailsDaoImpl");
		return personRaceOutDtos;
	}
}
