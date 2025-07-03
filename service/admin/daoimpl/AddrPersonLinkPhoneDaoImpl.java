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

import us.tx.state.dfps.service.admin.dao.AddrPersonLinkPhoneDao;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneInDto;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Cinv46dDaoImpl Aug 18, 2017- 3:39:34 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class AddrPersonLinkPhoneDaoImpl implements AddrPersonLinkPhoneDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AddrPersonLinkPhoneDaoImpl.cinv46dQUERYdam}")
	private String cinv46dQUERYdam;

	@Value("${AddrPersonLinkPhoneDaoImpl.getPersonAddressAndPhoneForMPS}")
	private String getPersonAddressAndPhoneForMPSSql;

	@Autowired
	MobileUtil mobileUtil;

	public static final String MAX_DATE_STRING = "4712-12-31";

	private static final Logger log = Logger.getLogger(AddrPersonLinkPhoneDaoImpl.class);

	/**
	 * Method Name: getStagePersonLinkDetails LegacyName: clssa5dQUERYdam Method
	 * Desc:This method fetches details from
	 * ADDRESS_PERSON_LINK,PERSON_ADDRESS,PERSON_PHONE table
	 * 
	 * @param pInputDataRec
	 * @return liCinv46doDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AddrPersonLinkPhoneOutDto> cinv46dQUERYdam(AddrPersonLinkPhoneInDto pInputDataRec) {
		log.debug("Entering method AddrPersonLinkPhoneQUERYdam in AddrPersonLinkPhoneDaoImpl");
		List<AddrPersonLinkPhoneOutDto> liCinv46doDto;
		if (!mobileUtil.isMPSEnvironment()) {
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(cinv46dQUERYdam)
					.setResultTransformer(Transformers.aliasToBean(AddrPersonLinkPhoneOutDto.class)));
			sQLQuery1.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("addrCity", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("addrZip", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdAddrCounty", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdAddrState", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("phone", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("phoneExtension", StandardBasicTypes.STRING);
			sQLQuery1.setParameter("hI_ulIdPerson", pInputDataRec.getUlIdPerson());
			sQLQuery1.setParameter("hI_MaxDate", MAX_DATE_STRING);
			liCinv46doDto = (List<AddrPersonLinkPhoneOutDto>) sQLQuery1.list();
		} else {
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonAddressAndPhoneForMPSSql)
					.setResultTransformer(Transformers.aliasToBean(AddrPersonLinkPhoneOutDto.class)));
			sQLQuery1.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("addrCity", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("addrZip", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdAddrCounty", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdAddrState", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("phone", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("phoneExtension", StandardBasicTypes.STRING);
			sQLQuery1.setParameter("hI_ulIdPerson", pInputDataRec.getUlIdPerson());
			sQLQuery1.setParameter("hI_MaxDate", MAX_DATE_STRING);
			liCinv46doDto = (List<AddrPersonLinkPhoneOutDto>) sQLQuery1.list();
		}
		if (TypeConvUtil.isNullOrEmpty(liCinv46doDto) && liCinv46doDto.size() == 0) {
			throw new DataNotFoundException(messageSource.getMessage("Cinv46d.not.found.ulIdPerson", null, Locale.US));
		}
		log.debug("Exiting method AddrPersonLinkPhoneQUERYdam in AddrPersonLinkPhoneDaoImpl");
		return liCinv46doDto;
	}
}
