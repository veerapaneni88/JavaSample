package us.tx.state.dfps.service.legal.daoimpl;

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
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dao.LegalActionPersonFetchDao;
import us.tx.state.dfps.service.legal.dto.LegalActionPersonArrDto;
import us.tx.state.dfps.service.legal.dto.LegalActionPersonDto;
import us.tx.state.dfps.service.legal.dto.LegalActionPersonRtrvInDto;
import us.tx.state.dfps.service.legal.dto.LegalActionPersonRtrvOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This DAO
 * will retrieve full rows from the LEGAL_ACTION table where ID_PERSON equals
 * the host Nov 1, 2017- 3:50:54 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class LegalActionPersonFetchDaoImpl implements LegalActionPersonFetchDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${LegalActionPersonFetchDaoImpl.legalActionSelectSql}")
	private transient String legalActionSelectSql;

	private static final Logger log = Logger.getLogger(LegalActionPersonFetchDaoImpl.class);

	/**
	 * Method Name: fetchLegalActionPerson Method Description:Retrieves from the
	 * LEGAL_ACTION table where ID_PERSON equals the host DAM: clscg2d
	 * 
	 * @param legalActionPersonRtrvInDto
	 * @return LegalActionPersonArrDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LegalActionPersonRtrvOutDto fetchLegalActionPerson(LegalActionPersonRtrvInDto legalActionPersonRtrvInDto) {
		log.debug("Entering method fetchLegalActionPerson in LegalActionPersonFetchDaoImpl");
		List<String> szCdLegalActActionArray = new ArrayList<String>();
		szCdLegalActActionArray.add(ServiceConstants.LEG_ACT_TYP_JPC_STARTS_270);
		szCdLegalActActionArray.add(ServiceConstants.LEG_ACT_TYP_JPC_ENDS_280);
		szCdLegalActActionArray.add(ServiceConstants.LEG_ACT_TYP_TYC_STARTS_290);
		szCdLegalActActionArray.add(ServiceConstants.LEG_ACT_TYP_TYC_ENDS_300);
		LegalActionPersonRtrvOutDto legalActionPersonRtrvOutDto = new LegalActionPersonRtrvOutDto();

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(legalActionSelectSql)
				.addScalar("idLegalActEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdLegalActAction", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdLegalActActnSubtype", StandardBasicTypes.STRING)
				.addScalar("cdLegalActOutcome", StandardBasicTypes.STRING)
				.addScalar("cdQrtpCourtStatus",StandardBasicTypes.STRING)
				.addScalar("dtLegalActDateFiled", StandardBasicTypes.DATE)
				.addScalar("dtLegalActOutcomeDate", StandardBasicTypes.DATE)
				.addScalar("indLegalActDocsNCase", StandardBasicTypes.CHARACTER)
				.addScalar("txtLegalActComment", StandardBasicTypes.STRING)
				.setParameter("idPerson", legalActionPersonRtrvInDto.getIdPerson())
				.setParameterList("listLegalAct", szCdLegalActActionArray)
				.setParameter("cdLegalActAction", ServiceConstants.CLEGCPS_CCUU)
				.setResultTransformer(Transformers.aliasToBean(LegalActionPersonDto.class));

		List<LegalActionPersonDto> legalActionPersonDtoList = sQLQuery1.list();
		LegalActionPersonArrDto legalActionPersonArrDto = new LegalActionPersonArrDto();
		if (ObjectUtils.isEmpty(legalActionPersonDtoList)) {						
			legalActionPersonRtrvOutDto.setLegalActionPersonArrDto(legalActionPersonArrDto);
		}
		else {			
			legalActionPersonArrDto.setLegalActionPersonDtoList(legalActionPersonDtoList);
			legalActionPersonRtrvOutDto.setLegalActionPersonArrDto(legalActionPersonArrDto);
		}
		
		log.debug("Exiting method fetchLegalActionPerson in LegalActionPersonFetchDaoImpl");
		return legalActionPersonRtrvOutDto;
	}
}
