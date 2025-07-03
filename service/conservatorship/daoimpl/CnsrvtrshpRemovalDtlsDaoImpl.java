package us.tx.state.dfps.service.conservatorship.daoimpl;

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

import us.tx.state.dfps.service.conservatorship.dao.CnsrvtrshpRemovalDtlsDao;
import us.tx.state.dfps.service.cvs.dto.CnsrvtrshpRemovalInDto;
import us.tx.state.dfps.service.cvs.dto.CnsrvtrshpRemovalOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * fetches the removal event details using eventID Aug 10, 2017- 6:58:45 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CnsrvtrshpRemovalDtlsDaoImpl implements CnsrvtrshpRemovalDtlsDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CnsrvtrshpRemovalDtlsDaoImpl.getrmvldtls}")
	private transient String getrmvldtls;

	private static final Logger log = Logger.getLogger(CnsrvtrshpRemovalDtlsDaoImpl.class);

	/**
	 * Method name: Method desc:This method fetches the removal event details
	 * using eventID.Legacy Name:cses20dQUERYdam
	 * 
	 * @param cnsrvtrshpRemovalInDto
	 * @param pOutputDataRec
	 * @return List<CnsrvtrshpRemovalOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CnsrvtrshpRemovalOutDto> getrmvldtls(CnsrvtrshpRemovalInDto cnsrvtrshpRemovalInDto) {
		log.debug("Entering method getrmvldtls in CnsrvtrshpRemovalDtlsDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getrmvldtls)
				.setResultTransformer(Transformers.aliasToBean(CnsrvtrshpRemovalOutDto.class)));
		sQLQuery1.addScalar("idEvent", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("tsLastUpdate", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("idVictim", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("dtRemoval", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("indRemovalNACare", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indRemovalNaChild", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("nbrRemovalAgeMo", StandardBasicTypes.SHORT);
		sQLQuery1.addScalar("nbrRemovalAgeYr", StandardBasicTypes.SHORT);
		sQLQuery1.setParameter("idEvent", cnsrvtrshpRemovalInDto.getIdEvent());
		List<CnsrvtrshpRemovalOutDto> liCses20doDto = (List<CnsrvtrshpRemovalOutDto>) sQLQuery1.list();
		log.debug("Exiting method getrmvldtls in CnsrvtrshpRemovalDtlsDaoImpl");
		return liCses20doDto;
	}
}
