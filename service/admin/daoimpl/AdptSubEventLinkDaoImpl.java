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

import us.tx.state.dfps.service.admin.dao.AdptSubEventLinkDao;
import us.tx.state.dfps.service.admin.dto.AdptSubEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.AdptSubEventLinkOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Get Adoption
 * and Event Link Aug 10, 2017- 6:47:30 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class AdptSubEventLinkDaoImpl implements AdptSubEventLinkDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AdptSubEventLinkDaoImpl.getAdoptionEventLink}")
	private String getAdoptionEventLink;

	private static final Logger log = Logger.getLogger(AdptSubEventLinkDaoImpl.class);

	public AdptSubEventLinkDaoImpl() {
		super();
	}

	/**
	 * Method Name: getAdoptionEventLink Method Description: Fetch event for
	 * given Adoption subsidy
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return List<Clssc4doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AdptSubEventLinkOutDto> getAdoptionEventLink(AdptSubEventLinkInDto pInputDataRec) {
		log.debug("Entering method AdptSubEventLinkQUERYdam in AdptSubEventLinkDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAdoptionEventLink)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setParameter("hI_ulIdAdptSub", pInputDataRec.getIdAdptSub())
				.setResultTransformer(Transformers.aliasToBean(AdptSubEventLinkOutDto.class)));
		List<AdptSubEventLinkOutDto> liClssc4doDto = (List<AdptSubEventLinkOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liClssc4doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Clssc4dDaoImpl.no.event.exists.Adoption.subsidy", null, Locale.US));
		}
		log.debug("Exiting method AdptSubEventLinkQUERYdam in AdptSubEventLinkDaoImpl");
		return liClssc4doDto;
	}
}
