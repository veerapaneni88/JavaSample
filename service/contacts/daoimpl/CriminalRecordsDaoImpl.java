package us.tx.state.dfps.service.contacts.daoimpl;

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

import us.tx.state.dfps.service.admin.dto.CriminalRecordsDoDto;
import us.tx.state.dfps.service.admin.dto.CriminalRecordsDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.CriminalRecordsDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;

@Repository
public class CriminalRecordsDaoImpl implements CriminalRecordsDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Csesc2dDaoImpl.getCriminalCheckRecords}")
	private transient String getCriminalCheckRecords;

	private static final Logger log = Logger.getLogger(CriminalRecordsDaoImpl.class);

	/**
	 * 
	 * Method Name: getCriminalCheckRecords Method
	 * Description:getCriminalCheckRecords
	 * 
	 * @param pInputDataRec
	 * @return @
	 */

	@Override
	public List<CriminalRecordsDoDto> getCriminalCheckRecords(CriminalRecordsDto pInputDataRec) {
		log.debug("Entering method csesc2dQUERYdam in Csesc2dDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCriminalCheckRecords)
				.setResultTransformer(Transformers.aliasToBean(CriminalRecordsDoDto.class)));
		sQLQuery1.addScalar("ulIdPerson", StandardBasicTypes.LONG).setParameter("hI_ulIdStage",
				pInputDataRec.getUlIdStage());
		List<CriminalRecordsDoDto> liCsesc2doDto = new ArrayList<>();
		liCsesc2doDto = (List<CriminalRecordsDoDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCsesc2doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Csesc2dDaoImpl.not.found.person", null, Locale.US));
		}
		log.debug("Exiting method csesc2dQUERYdam in Csesc2dDaoImpl");
		return liCsesc2doDto;
	}

}
