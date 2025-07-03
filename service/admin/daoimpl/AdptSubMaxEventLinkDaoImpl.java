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

import us.tx.state.dfps.service.admin.dao.AdptSubMaxEventLinkDao;
import us.tx.state.dfps.service.admin.dto.AdptSubMaxEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.AdptSubMaxEventLinkOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Get max
 * eventId value. Aug 10, 2017- 10:16:42 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
public class AdptSubMaxEventLinkDaoImpl implements AdptSubMaxEventLinkDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AdptSubMaxEventLinkDaoImpl.getMaxEventID}")
	private String getMaxEventID;

	private static final Logger log = Logger.getLogger(AdptSubMaxEventLinkDaoImpl.class);

	public AdptSubMaxEventLinkDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getMaxEventID Method Description: Fetch latest event id.
	 * 
	 * @param pInputDataRec
	 * @return List<Clssc5doDto>
	 * @, DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AdptSubMaxEventLinkOutDto> getMaxEventID(AdptSubMaxEventLinkInDto pInputDataRec) {
		log.debug("Entering method AdptSubMaxEventLinkQUERYdam in AdptSubMaxEventLinkDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getMaxEventID)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setParameter("hI_ulIdAdptSub", pInputDataRec.getIdAdptSub())
				.setResultTransformer(Transformers.aliasToBean(AdptSubMaxEventLinkOutDto.class)));
		List<AdptSubMaxEventLinkOutDto> liClssc5doDto = (List<AdptSubMaxEventLinkOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liClssc5doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Clssc5dDaoImpl.no.event.record", null, Locale.US));
		}
		log.debug("Exiting method AdptSubMaxEventLinkQUERYdam in AdptSubMaxEventLinkDaoImpl");
		return liClssc5doDto;
	}
}
