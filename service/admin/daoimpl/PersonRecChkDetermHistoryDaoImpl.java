package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.PersonRecChkDetermHistoryDao;
import us.tx.state.dfps.service.admin.dto.PersonRecChkDetermHistoryInDto;
import us.tx.state.dfps.service.admin.dto.PersonRecChkDetermHistoryOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Fetched Data
 * from Records check determination history table Aug 6, 2017- 4:24:27 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class PersonRecChkDetermHistoryDaoImpl implements PersonRecChkDetermHistoryDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonRecChkDetermHistoryDaoImpl.getRedCheckDetermHistDetails}")
	private transient String getRedCheckDetermHistDetails;

	private static final Logger log = Logger.getLogger("ServiceBusiness-PersonRecChkDetermHistoryDao");

	/**
	 * 
	 * Method Name: getRecordCheckDeterminationDtls Method Description: This
	 * method will query the RecordCheckDeter table to fetch the record check
	 * details for a given record ID
	 * 
	 * @param pInputDataRec
	 * @return List<PersonRecChkDetermHistoryOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonRecChkDetermHistoryOutDto> getRecordCheckDeterminationDtls(
			PersonRecChkDetermHistoryInDto pInputDataRec) {
		log.debug("Entering method PersonRecChkDetermHistoryQUERYdam in PersonRecChkDetermHistoryDaoImpl");
		Query queryRecordsCheck = sessionFactory.getCurrentSession().createSQLQuery(getRedCheckDetermHistDetails)
				.addScalar("idRecChkDetermHist", StandardBasicTypes.LONG)
				.addScalar("idRecCheck", StandardBasicTypes.LONG)
				.addScalar("dtRecChkDeterminCreated", StandardBasicTypes.DATE)
				.addScalar("cdRecChkDeterm", StandardBasicTypes.STRING)
				.addScalar("indComplete", StandardBasicTypes.STRING)
				.addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PersonRecChkDetermHistoryOutDto.class));
		queryRecordsCheck.setParameter("hI_idRecCheck", pInputDataRec.getIdRecCheck());
		List<PersonRecChkDetermHistoryOutDto> recordsCheckDtos = (List<PersonRecChkDetermHistoryOutDto>) queryRecordsCheck
				.list();
		if (TypeConvUtil.isNullOrEmpty(recordsCheckDtos)) {
			throw new DataNotFoundException(
					messageSource.getMessage("recordCheck.not.found.attributes", null, Locale.US));
		}
		log.debug("Exiting method PersonRecChkDetermHistoryQUERYdam in PersonRecChkDetermHistoryDaoImpl");
		return recordsCheckDtos;
	}
}
