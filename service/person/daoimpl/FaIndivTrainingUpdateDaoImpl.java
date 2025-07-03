package us.tx.state.dfps.service.person.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.person.dao.FaIndivTrainingUpdateDao;
import us.tx.state.dfps.service.person.dto.FaIndivTrainingInDto;
import us.tx.state.dfps.service.person.dto.FaIndivTrainingOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> May 8, 2018- 6:36:44 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class FaIndivTrainingUpdateDaoImpl implements FaIndivTrainingUpdateDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FaIndivTrainingUpdateDaoImpl.strQuery1}")
	String strQuery1;

	@Value("${FaIndivTrainingUpdateDaoImpl.strQuery2}")
	String strQuery2;

	@Value("${FaIndivTrainingUpdateDaoImpl.strQuery3}")
	String strQuery3;

	@Value("${FaIndivTrainingUpdateDaoImpl.strQuery4}")
	String strQuery4;

	private static final Logger log = Logger.getLogger("ServiceBusiness-EmployeeDaoLog");

	/**
	 * Method Name: updateFaIndivTraining Method Description: update the detail
	 * of FaIndivTraining caud86d
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateFaIndivTraining(FaIndivTrainingInDto pInputDataRec, FaIndivTrainingOutDto pOutputDataRec) {
		log.debug("Entering method updateFaIndivTraining in FaIndivTrainingUpdateDaoImpl");
		switch (pInputDataRec.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery1)
					.addScalar(":hI_ulIdIndivTraining")
					.setResultTransformer(Transformers.aliasToBean(FaIndivTrainingOutDto.class)));
			List<FaIndivTrainingOutDto> liCaud86doDto = new ArrayList<>();
			liCaud86doDto = (List<FaIndivTrainingOutDto>) sQLQuery1.list();

			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery2)
					.setParameter("hI_ldNbrIndivTrnHrs", pInputDataRec.getNbrIndivTrnHrs())
					.setParameter("hI_sNbrIndivTrnSession", pInputDataRec.getNbrIndivTrnSession())
					.setParameter("hI_szTxtIndivTrnTitle", pInputDataRec.getTxtIndivTrnTitle())
					.setParameter("hI_ulIdIndivTraining", pInputDataRec.getIdIndivTraining())
					.setParameter("hI_dtDtIndivTrn", pInputDataRec.getDtIndivTrn())
					.setParameter("hI_szCdIndivTrnType", pInputDataRec.getCdIndivTrnType())
					.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
					.setParameter("hI_tsLastUpdate", pInputDataRec.getTsLastUpdate())
					.setParameter("hI_cIndIndivTrnEc", pInputDataRec.getIndIndivTrnEc())
					.setParameter("hI_cIndIndivTrnAktc", pInputDataRec.getIndIndivTrnAktc()));
			sQLQuery2.executeUpdate();

			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery3 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery3)
					.setParameter("hI_ldNbrIndivTrnHrs", pInputDataRec.getNbrIndivTrnHrs())
					.setParameter("hI_sNbrIndivTrnSession", pInputDataRec.getNbrIndivTrnSession())
					.setParameter("hI_szTxtIndivTrnTitle", pInputDataRec.getTxtIndivTrnTitle())
					.setParameter("hI_ulIdIndivTraining", pInputDataRec.getIdIndivTraining())
					.setParameter("hI_dtDtIndivTrn", pInputDataRec.getDtIndivTrn())
					.setParameter("hI_szCdIndivTrnType", pInputDataRec.getCdIndivTrnType())
					.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
					.setParameter("hI_tsLastUpdate", pInputDataRec.getTsLastUpdate())
					.setParameter("hI_cIndIndivTrnEc", pInputDataRec.getIndIndivTrnEc())
					.setParameter("hI_cIndIndivTrnAktc", pInputDataRec.getIndIndivTrnAktc()));
			sQLQuery3.executeUpdate();

			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			SQLQuery sQLQuery4 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery4)
					.setParameter("hI_ulIdIndivTraining", pInputDataRec.getIdIndivTraining())
					.setParameter("hI_tsLastUpdate", pInputDataRec.getTsLastUpdate()));
			sQLQuery4.executeUpdate();

			break;
		}

		log.debug("Exiting method updateFaIndivTraining in FaIndivTrainingUpdateDaoImpl");
	}

}
