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

import us.tx.state.dfps.service.admin.dao.RecordsCheckPersonDao;
import us.tx.state.dfps.service.admin.dto.RecordsCheckPersonInDto;
import us.tx.state.dfps.service.admin.dto.RecordsCheckPersonOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Class
 * Checks The Record Check Details Aug 7, 2017- 4:03:34 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class RecordsCheckPersonDaoImpl implements RecordsCheckPersonDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	@Value("${RecordsCheckPersonDaoImpl.recCheckDtls}")
	private String recCheckDtls;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger("ServiceBusiness-RecordsCheckPersonDao");

	/**
	 * 
	 * Method Name: recCheckDtls Method Description: This method retrieves data
	 * from RECORDS_CHECK and PERSON tables.
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return List<RecordsCheckPersonOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RecordsCheckPersonOutDto> recCheckDtls(RecordsCheckPersonInDto pInputDataRec,
			RecordsCheckPersonOutDto pOutputDataRec) {
		log.debug("Entering method RecordsCheckPersonQUERYdam in RecordsCheckPersonDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(recCheckDtls)
				.setResultTransformer(Transformers.aliasToBean(RecordsCheckPersonOutDto.class)));
		sQLQuery1.addScalar("idRecCheck", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("idRecCheckPerson", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idRecCheckRequestor", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idStage", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("cdRecCheckCheckType", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdRecCheckEmpType", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdRecCheckStatus", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtRecCheckCompleted", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtRecCheckRequest", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("txtRecCheckComments", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indClearedEmail", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtClearedEmailSent", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("cdRecChkDeterm", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtDetermFinal", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtClrdEmailRequested", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("indAccptRej", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indReviewNow", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("nmPersonFull", StandardBasicTypes.STRING);
		sQLQuery1.setParameter("hI_idRecCheckPerson", pInputDataRec.getIdRecCheckPerson());
		List<RecordsCheckPersonOutDto> liClsc53doDto = (List<RecordsCheckPersonOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liClsc53doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("recCheckDtls.not.found.ulIdRecCheckPerson", null, Locale.US));
		}
		log.debug("Exiting method RecordsCheckPersonQUERYdam in RecordsCheckPersonDaoImpl");
		return liClsc53doDto;
	}
}
