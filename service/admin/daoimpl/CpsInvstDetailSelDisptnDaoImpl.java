package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.CpsInvstDetailSelDisptnDao;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This will
 * retrieve the overall disposition for the CPS Investigation. Aug 5, 2017-
 * 10:41:28 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CpsInvstDetailSelDisptnDaoImpl implements CpsInvstDetailSelDisptnDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CpsInvstDetailSelDisptnDaoImpl.retCPSInvest}")
	private String retCPSInvest;

	private static final Logger log = Logger.getLogger(CpsInvstDetailSelDisptnDaoImpl.class);

	public CpsInvstDetailSelDisptnDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: retCPSInvest Method Description: This method will retrieve
	 * CD_CPS_INVST_DTL_OVRLL_DISPTN from CPS_INVST_DTL table. Cses0ad
	 * 
	 * @param cpsInvstDetailInDto
	 * @return List<CpsInvstDetailOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvstDetailOutDto> retCPSInvest(CpsInvstDetailInDto cpsInvstDetailInDto) {
		log.debug("Entering method retCPSInvest in CpsInvstDetailSelDisptnDaoImpl");
		if (cpsInvstDetailInDto.getIdStage() == null)
			cpsInvstDetailInDto.setIdStage(0L);
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(retCPSInvest)
				.addScalar("cdCpsInvstDtlOvrllDisptn", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(CpsInvstDetailOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdStage", cpsInvstDetailInDto.getIdStage());
		List<CpsInvstDetailOutDto> cpsInvstDetailOutDtos = (List<CpsInvstDetailOutDto>) sQLQuery1.list();
		log.debug("Exiting method retCPSInvest in CpsInvstDetailSelDisptnDaoImpl");
		return cpsInvstDetailOutDtos;
	}
}
