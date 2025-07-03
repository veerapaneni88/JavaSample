package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
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

import us.tx.state.dfps.service.admin.dao.CriminalHistoryRecordsCheckDao;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecordsCheckInDto;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecordsCheckOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO Impl to
 * fetch Criminal Records> Aug 8, 2017- 3:42:23 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class CriminalHistoryRecordsCheckDaoImpl implements CriminalHistoryRecordsCheckDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CriminalHistoryRecordsCheckDaoImpl.getCriminalCheckRecords}")
	private transient String getCriminalCheckRecords;

	private static final Logger log = Logger.getLogger(CriminalHistoryRecordsCheckDaoImpl.class);

	public CriminalHistoryRecordsCheckDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getCriminalCheckRecords Method Description: This method will
	 * get data from Criminal History table.
	 * 
	 * @param pInputDataRec
	 * @return List<CriminalHistoryRecordsCheckOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CriminalHistoryRecordsCheckOutDto> getCriminalCheckRecords(
			CriminalHistoryRecordsCheckInDto pInputDataRec) {
		log.debug("Entering method CriminalHistoryRecordsCheckQUERYdam in CriminalHistoryRecordsCheckDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCriminalCheckRecords)
				.setResultTransformer(Transformers.aliasToBean(CriminalHistoryRecordsCheckOutDto.class)));
		sQLQuery1.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("hI_ulIdStage",
				pInputDataRec.getIdStage());
		List<CriminalHistoryRecordsCheckOutDto> liCsesc2doDto = new ArrayList<>();
		liCsesc2doDto = (List<CriminalHistoryRecordsCheckOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCsesc2doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Csesc2dDaoImpl.not.found.person", null, Locale.US));
		}
		log.debug("Exiting method CriminalHistoryRecordsCheckQUERYdam in CriminalHistoryRecordsCheckDaoImpl");
		return liCsesc2doDto;
	}
}
