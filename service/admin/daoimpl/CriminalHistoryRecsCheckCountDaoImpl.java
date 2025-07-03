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

import us.tx.state.dfps.service.admin.dao.CriminalHistoryRecsCheckCountDao;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecsCheckCountInDto;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecsCheckCountOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * gives the count of criminal records. Aug 7, 2017- 5:25:41 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class CriminalHistoryRecsCheckCountDaoImpl implements CriminalHistoryRecsCheckCountDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CriminalHistoryRecsCheckCountDaoImpl.criminalHistoryRecCheckCount}")
	private String criminalHistoryRecCheckCount;

	private static final Logger log = Logger.getLogger("ServiceBusiness-CriminalHistoryRecsCheckCountDao");

	/**
	 * 
	 * Method Name: rtrvCriminalHistoryRecords Method Description: This method
	 * retrieves data from CRIMINAL_HISTORY and RECORDS_CHECK tables.
	 * Equivivalent Legacy Method : cinvf1dQUERYdam
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return List<CriminalHistoryRecsCheckCountOutDto>
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CriminalHistoryRecsCheckCountOutDto> rtrvCriminalHistoryRecords(
			CriminalHistoryRecsCheckCountInDto pInputDataRec, CriminalHistoryRecsCheckCountOutDto pOutputDataRec) {
		log.debug("Entering method CriminalHistoryRecsCheckCountQUERYdam in CriminalHistoryRecsCheckCountDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(criminalHistoryRecCheckCount)
				.setResultTransformer(Transformers.aliasToBean(CriminalHistoryRecsCheckCountOutDto.class)));
		sQLQuery1.addScalar("ulRowQty", StandardBasicTypes.STRING);
		sQLQuery1.setParameter("hI_idRecCheck", pInputDataRec.getIdRecCheck());
		List<CriminalHistoryRecsCheckCountOutDto> liCinvf1doDto = (List<CriminalHistoryRecsCheckCountOutDto>) sQLQuery1
				.list();
		if (TypeConvUtil.isNullOrEmpty(liCinvf1doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("cinvf1dQUERYdam.not.found.ulIdRecCheckPerson", null, Locale.US));
		}
		log.debug("Exiting method CriminalHistoryRecsCheckCountQUERYdam in CriminalHistoryRecsCheckCountDaoImpl");
		return liCinvf1doDto;
	}
}
