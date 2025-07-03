package us.tx.state.dfps.service.arsafetyassmt.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ArSafetyAreaLookup;
import us.tx.state.dfps.common.domain.ArSafetyFactorLookup;
import us.tx.state.dfps.common.domain.ArSafetyFactorValLookup;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtAreaValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtFactorValValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtFactorValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.arsafetyassmt.dao.ARSafetyAssmtDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ARSafetyAssmtDaoImpl Sep 22, 2017- 9:30:45 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class ARSafetyAssmtDaoImpl implements ARSafetyAssmtDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ARSafetyAssmtDaoImpl.getARSafetyAssmtDtl}")
	private String getARSafetyAssmtSql;

	@Value("${ARSafetyAssmtDaoImpl.getARSafetyAssmtFactors}")
	private String getARSafetyAssmtFactorsSql;

	@Value("${ARSafetyAssmtDaoImpl.getARSafetyAssmtFactor}")
	private String getARSafetyAssmtFactorSql;

	@Value("${ARSafetyAssmtDaoImpl.isConclusionPageAprv}")
	private String isConclusionPageAprvSql;

	@Autowired
	EventDao eventDao;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: getARSafetyAssmt Method Description:This method is called
	 * from display method in SafetyAssmtConversation if the page has been
	 * previously saved. It retrieves back all the responses
	 * 
	 * @param idStage
	 * @param cdAssmtType
	 * @param idUser
	 * @return ARSafetyAssmtValueDto
	 */
	@Override
	public ARSafetyAssmtValueDto getARSafetyAssmt(Integer idStage, String cdAssmtType, Integer idUser) {
		Query getARSafetyAssmtQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getARSafetyAssmtSql)
				.addScalar("idArSafetyAssmt", StandardBasicTypes.INTEGER)
				.addScalar("idEvent", StandardBasicTypes.INTEGER).addScalar("idStage", StandardBasicTypes.INTEGER)
				.addScalar("idCase", StandardBasicTypes.INTEGER).addScalar("immediateAction", StandardBasicTypes.STRING)
				.addScalar("furtherAssmt", StandardBasicTypes.STRING).addScalar("version", StandardBasicTypes.INTEGER)
				.addScalar("indAssmtType", StandardBasicTypes.STRING)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).setParameter("stageId", idStage)
				.setParameter("indAssmtType", cdAssmtType)
				.setResultTransformer(Transformers.aliasToBean(ARSafetyAssmtValueDto.class)));
		ARSafetyAssmtValueDto arSafetyAssmtValueDto = (ARSafetyAssmtValueDto) getARSafetyAssmtQuery.uniqueResult();

		return arSafetyAssmtValueDto;
	}

	/**
	 * Method Name: getARSafetyAssmtAreas Method Description:Retrieves
	 * ARSafetyAssmtArea List from database.
	 * 
	 * @param cdAssmtType
	 * @param idStage
	 * @return List<ARSafetyAssmtAreaValueDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ARSafetyAssmtAreaValueDto> getARSafetyAssmtAreas(String cdAssmtType, Integer idStage) {
		List<ARSafetyAssmtAreaValueDto> arSafetyAssmtAreaValueDtoList = new ArrayList<ARSafetyAssmtAreaValueDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArSafetyAreaLookup.class);
		criteria.add(Restrictions.eq("indAssmtType", cdAssmtType));
		criteria.add(Restrictions.eq("nbrVersion", (byte) ServiceConstants.NBR_VERSION));
		criteria.addOrder(Order.asc("nbrAreaOrder"));
		List<ArSafetyAreaLookup> arSafetyAreaLookupList = criteria.list();
		for (ArSafetyAreaLookup arSafetyAreaLookup : arSafetyAreaLookupList) {
			ARSafetyAssmtAreaValueDto arSafetyAssmtAreaValueDto = new ARSafetyAssmtAreaValueDto();
			Byte nbrVersion = new Byte(arSafetyAreaLookup.getNbrVersion());
			arSafetyAssmtAreaValueDto.setVersion(nbrVersion.intValue());
			arSafetyAssmtAreaValueDto.setIdArea(new Long(arSafetyAreaLookup.getIdArea()).intValue());
			if (!ObjectUtils.isEmpty(arSafetyAreaLookup.getIdAreaInitial())) {
				arSafetyAssmtAreaValueDto.setIdAreaInitial(new Long(arSafetyAreaLookup.getIdAreaInitial()).intValue());
			}
			arSafetyAssmtAreaValueDto.setAreaOrder(new Long(arSafetyAreaLookup.getNbrAreaOrder()).intValue());
			arSafetyAssmtAreaValueDto.setArea(arSafetyAreaLookup.getTxtArea());
			arSafetyAssmtAreaValueDto.setIndAssmtType(arSafetyAreaLookup.getIndAssmtType());
			arSafetyAssmtAreaValueDto.setIdStage(idStage);
			arSafetyAssmtAreaValueDtoList.add(arSafetyAssmtAreaValueDto);

		}

		return arSafetyAssmtAreaValueDtoList;
	}

	/**
	 * Method Name: getARSafetyAssmtFactors Method Description:Retrieves
	 * ARSafetyAssmtFactor List from database.
	 * 
	 * @param idArea
	 * @param idArSafetyAssmt
	 * @return List<ARSafetyAssmtFactorValueDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ARSafetyAssmtFactorValueDto> getARSafetyAssmtFactors(Integer idArea, Integer idArSafetyAssmt) {
		List<ARSafetyAssmtFactorValueDto> arSafetyAssmtFactorValueDtoList = new ArrayList<ARSafetyAssmtFactorValueDto>();
		List<ARSafetyAssmtFactorValueDto> arSafetyAssmtFactorQueryValueDtoList = new ArrayList<ARSafetyAssmtFactorValueDto>();
		if (ObjectUtils.isEmpty(idArSafetyAssmt) || idArSafetyAssmt == ServiceConstants.Zero_INT) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArSafetyFactorLookup.class);
			criteria.add(Restrictions.eq("nbrVersion", (byte) ServiceConstants.NBR_VERSION));
			criteria.add(Restrictions.eq("idArea", (long) idArea));
			criteria.addOrder(Order.asc("nbrFactorOrder"));
			List<ArSafetyFactorLookup> arSafetyFactorLookupList = criteria.list();
			for (ArSafetyFactorLookup arSafetyFactorLookup : arSafetyFactorLookupList) {
				ARSafetyAssmtFactorValueDto arSafetyAssmtFactorValueDto = new ARSafetyAssmtFactorValueDto();
				arSafetyAssmtFactorValueDto.setIdArSafetyFactor(ServiceConstants.Zero_INT);
				arSafetyAssmtFactorValueDto.setIdArSafetyAssmt(ServiceConstants.Zero_INT);
				arSafetyAssmtFactorValueDto.setResponse(ServiceConstants.NULL_STRING);
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIdFactor())) {
					arSafetyAssmtFactorValueDto.setIdFactor(new Long(arSafetyFactorLookup.getIdFactor()).intValue());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIdFactorInitial())) {
					arSafetyAssmtFactorValueDto
							.setIdFactorInitial(new Long(arSafetyFactorLookup.getIdFactorInitial()).intValue());
				}
				arSafetyAssmtFactorValueDto.setIndAssmtType(arSafetyFactorLookup.getIndAssmtType());
				Byte nbrVersion = new Byte(arSafetyFactorLookup.getNbrVersion());
				arSafetyAssmtFactorValueDto.setVersion(nbrVersion.intValue());
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getNbrFactorOrder())) {
					arSafetyAssmtFactorValueDto
							.setFactorOrder(new Long(arSafetyFactorLookup.getNbrFactorOrder()).intValue());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIdArea())) {
					arSafetyAssmtFactorValueDto.setIdArea(new Long(arSafetyFactorLookup.getIdArea()).intValue());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIdFactorDep())) {
					arSafetyAssmtFactorValueDto
							.setIdFactorDep(new Long(arSafetyFactorLookup.getIdFactorDep()).intValue());
				}
				arSafetyAssmtFactorValueDto.setFactorDepVal(arSafetyFactorLookup.getTxtFactorDepVal());
				arSafetyAssmtFactorValueDto.setIndVertical(arSafetyFactorLookup.getIndVertical());
				arSafetyAssmtFactorValueDto.setFactor(arSafetyFactorLookup.getTxtFactor());
				arSafetyAssmtFactorValueDto.setIndFactor2(arSafetyFactorLookup.getIndFactor2());
				arSafetyAssmtFactorValueDto.setIndFactorType(arSafetyFactorLookup.getIndFactorType());
				arSafetyAssmtFactorValueDto.setIndRequiredSave(arSafetyFactorLookup.getIndRequiredSave());
				arSafetyAssmtFactorValueDto.setIndRequiredSubmit(arSafetyFactorLookup.getIndRequiredSubmit());
				arSafetyAssmtFactorValueDtoList.add(arSafetyAssmtFactorValueDto);
			}
		} else {

			arSafetyAssmtFactorQueryValueDtoList = (List<ARSafetyAssmtFactorValueDto>) sessionFactory
					.getCurrentSession().createSQLQuery(getARSafetyAssmtFactorsSql)
					.addScalar("idArSafetyFactor", StandardBasicTypes.INTEGER)
					.addScalar("idArSafetyAssmt", StandardBasicTypes.INTEGER)
					.addScalar("response", StandardBasicTypes.STRING).addScalar("idFactor", StandardBasicTypes.INTEGER)
					.addScalar("idFactorInitial", StandardBasicTypes.INTEGER)
					.addScalar("indAssmtType", StandardBasicTypes.STRING)
					.addScalar("version", StandardBasicTypes.INTEGER)
					.addScalar("factorOrder", StandardBasicTypes.INTEGER)
					.addScalar("idArea", StandardBasicTypes.INTEGER)
					.addScalar("idFactorDep", StandardBasicTypes.INTEGER)
					.addScalar("factorDepVal", StandardBasicTypes.STRING)
					.addScalar("indVertical", StandardBasicTypes.STRING).addScalar("factor", StandardBasicTypes.STRING)
					.addScalar("indFactor2", StandardBasicTypes.STRING)
					.addScalar("indFactorType", StandardBasicTypes.STRING)
					.addScalar("indRequiredSave", StandardBasicTypes.STRING)
					.addScalar("indRequiredSubmit", StandardBasicTypes.STRING).setParameter("idArea", idArea)
					.setParameter("idArSafetyAssmt", idArSafetyAssmt)
					.setResultTransformer(Transformers.aliasToBean(ARSafetyAssmtFactorValueDto.class)).list();
			for (ARSafetyAssmtFactorValueDto arSafetyAssmtFactorValueDto : arSafetyAssmtFactorQueryValueDtoList) {
				arSafetyAssmtFactorValueDtoList.add(arSafetyAssmtFactorValueDto);
			}

		}
		return arSafetyAssmtFactorValueDtoList;
	}

	/**
	 * Method Name: getARSafetyAssmtFactorVals Method Description:Retrieves
	 * ARSafetyAssmtFactorVal List from database.
	 * 
	 * @param idFactor
	 * @return List<ARSafetyAssmtFactorValValueDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ARSafetyAssmtFactorValValueDto> getARSafetyAssmtFactorVals(Integer idFactor) {
		List<ARSafetyAssmtFactorValValueDto> arSafetyAssmtFactorValValueDtoList = new ArrayList<ARSafetyAssmtFactorValValueDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArSafetyFactorValLookup.class);
		criteria.add(Restrictions.eq("idFactor", (long) idFactor));
		criteria.addOrder(Order.asc("nbrFactorValOrder"));
		List<ArSafetyFactorValLookup> arSafetyFactorValLookupsList = criteria.list();
		for (ArSafetyFactorValLookup arSafetyFactorValLookup : arSafetyFactorValLookupsList) {
			ARSafetyAssmtFactorValValueDto arSafetyAssmtFactorValValueDto = new ARSafetyAssmtFactorValValueDto();
			if (!ObjectUtils.isEmpty(arSafetyFactorValLookup.getIdFactorVal())) {
				arSafetyAssmtFactorValValueDto
						.setIdFactorVal(new Long(arSafetyFactorValLookup.getIdFactorVal()).intValue());
			}
			if (!ObjectUtils.isEmpty(arSafetyFactorValLookup.getIdFactor())) {
				arSafetyAssmtFactorValValueDto.setIdFactor(new Long(arSafetyFactorValLookup.getIdFactor()).intValue());
			}
			if (!ObjectUtils.isEmpty(arSafetyFactorValLookup.getNbrFactorValOrder())) {
				arSafetyAssmtFactorValValueDto
						.setFactorValOrder(new Long(arSafetyFactorValLookup.getNbrFactorValOrder()).intValue());
			}
			if (!ObjectUtils.isEmpty(arSafetyFactorValLookup.getTxtFactorVal())) {
				arSafetyAssmtFactorValValueDto.setFactorVal(arSafetyFactorValLookup.getTxtFactorVal());
			}
			if (!ObjectUtils.isEmpty(arSafetyFactorValLookup.getIndCheckFactor2())) {
				arSafetyAssmtFactorValValueDto.setIndCheckFactor2(arSafetyFactorValLookup.getIndCheckFactor2());
			}
			if (!ObjectUtils.isEmpty(arSafetyFactorValLookup.getTxtFactorValHelp())) {
				arSafetyAssmtFactorValValueDto.setFactorValHelp(arSafetyFactorValLookup.getTxtFactorValHelp());
			}
			arSafetyAssmtFactorValValueDto.setIndAssmtType(arSafetyFactorValLookup.getIndAssmtType());
			arSafetyAssmtFactorValValueDtoList.add(arSafetyAssmtFactorValValueDto);
		}

		return arSafetyAssmtFactorValValueDtoList;
	}

	/**
	 * Method Name: isConclusionPageAprv Method Description:This methos returns
	 * true When AR Conclusion page is Approved and Closure reason is 'INV -
	 * Child Fatality Allegations or INV - Removal or INV - CPS Decision or INV
	 * - Family Request for a given stage
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	@Override
	public Boolean isConclusionPageAprv(Integer idStage) {
		List<String> conclusionPageAprvList = new ArrayList<String>();
		boolean isConclusionPageAprv = false;
		conclusionPageAprvList.add(ServiceConstants.CCLOSAR_070);
		conclusionPageAprvList.add(ServiceConstants.CCLOSAR_080);
		conclusionPageAprvList.add(ServiceConstants.CCLOSAR_090);
		conclusionPageAprvList.add(ServiceConstants.CCLOSAR_100);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isConclusionPageAprvSql);
		query.setParameter("idStage", idStage);
		query.setParameterList("conclusionPageAprvList", conclusionPageAprvList);
		query.setParameter("cdEventStatus", ServiceConstants.CEVTSTAT_APRV)
				.setResultTransformer(Transformers.aliasToBean(ARSafetyAssmtValueDto.class));
		int countConclusionPageAprv = (int) query.uniqueResult();
		if (countConclusionPageAprv > 0) {
			isConclusionPageAprv = true;
		}
		return isConclusionPageAprv;
	}

	/**
	 * Method Name: getARSafetyAssmtFactor Method Description:Retrieves
	 * ARSafetyAssmtFactor from database
	 * 
	 * @param idFactor
	 * @param idSafetyAssmt
	 * @return ARSafetyAssmtValueDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ARSafetyAssmtFactorValueDto getARSafetyAssmtFactor(Integer idFactor, Integer idSafetyAssmt) {

		List<ARSafetyAssmtFactorValueDto> arSafetyAssmtFactorValueDtoList = new ArrayList<ARSafetyAssmtFactorValueDto>();
		ARSafetyAssmtFactorValueDto arSafetyAssmtFactorValueDto = new ARSafetyAssmtFactorValueDto();
		if (idSafetyAssmt == ServiceConstants.Zero_INT) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArSafetyFactorLookup.class);
			criteria.add(Restrictions.eq("nbrVersion", (byte) ServiceConstants.NBR_VERSION));
			criteria.add(Restrictions.eq("idFactor", (long) idFactor));
			criteria.addOrder(Order.asc("nbrFactorOrder"));
			List<ArSafetyFactorLookup> arSafetyFactorLookupList = criteria.list();
			for (ArSafetyFactorLookup arSafetyFactorLookup : arSafetyFactorLookupList) {
				arSafetyAssmtFactorValueDto.setIdArSafetyFactor(ServiceConstants.Zero);
				arSafetyAssmtFactorValueDto.setIdArSafetyAssmt(ServiceConstants.Zero);
				arSafetyAssmtFactorValueDto.setResponse(ServiceConstants.NULL_VALUE);
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIdFactor())) {
					arSafetyAssmtFactorValueDto.setIdFactor(new Long(arSafetyFactorLookup.getIdFactor()).intValue());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIdFactorInitial())) {
					arSafetyAssmtFactorValueDto
							.setIdFactorInitial(new Long(arSafetyFactorLookup.getIdFactorInitial()).intValue());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIndAssmtType())) {
					arSafetyAssmtFactorValueDto.setIndAssmtType(arSafetyFactorLookup.getIndAssmtType());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getNbrVersion())) {
					Byte nbrVersion = new Byte(arSafetyFactorLookup.getNbrVersion());
					arSafetyAssmtFactorValueDto.setVersion(nbrVersion.intValue());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getNbrFactorOrder())) {
					arSafetyAssmtFactorValueDto
							.setFactorOrder(new Long(arSafetyFactorLookup.getNbrFactorOrder()).intValue());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIdArea())) {
					arSafetyAssmtFactorValueDto.setIdArea(new Long(arSafetyFactorLookup.getIdArea()).intValue());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIdFactorDep())) {
					arSafetyAssmtFactorValueDto
							.setIdFactorDep(new Long(arSafetyFactorLookup.getIdFactorDep()).intValue());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getTxtFactorDepVal())) {
					arSafetyAssmtFactorValueDto.setFactorDepVal(arSafetyFactorLookup.getTxtFactorDepVal());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIndVertical())) {
					arSafetyAssmtFactorValueDto.setIndVertical(arSafetyFactorLookup.getIndVertical());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getTxtFactor())) {
					arSafetyAssmtFactorValueDto.setFactor(arSafetyFactorLookup.getTxtFactor());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIndFactor2())) {
					arSafetyAssmtFactorValueDto.setIndFactor2(arSafetyFactorLookup.getIndFactor2());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIndFactorType())) {
					arSafetyAssmtFactorValueDto.setIndFactorType(arSafetyFactorLookup.getIndFactorType());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIndRequiredSave())) {
					arSafetyAssmtFactorValueDto.setIndRequiredSave(arSafetyFactorLookup.getIndRequiredSave());
				}
				if (!ObjectUtils.isEmpty(arSafetyFactorLookup.getIndRequiredSubmit())) {
					arSafetyAssmtFactorValueDto.setIndRequiredSubmit(arSafetyFactorLookup.getIndRequiredSubmit());
				}
				arSafetyAssmtFactorValueDtoList.add(arSafetyAssmtFactorValueDto);
			}
		} else {
			arSafetyAssmtFactorValueDtoList = (List<ARSafetyAssmtFactorValueDto>) sessionFactory.getCurrentSession()
					.createSQLQuery(getARSafetyAssmtFactorSql).addScalar("idArSafetyFactor", StandardBasicTypes.INTEGER)
					.addScalar("idArSafetyAssmt", StandardBasicTypes.INTEGER)
					.addScalar("txtResponse", StandardBasicTypes.STRING)
					.addScalar("idFactor", StandardBasicTypes.INTEGER)
					.addScalar("idFactorInitial", StandardBasicTypes.INTEGER)
					.addScalar("indAssmtType", StandardBasicTypes.STRING)
					.addScalar("nbrVersion", StandardBasicTypes.INTEGER)
					.addScalar("nbrFactorOrder", StandardBasicTypes.INTEGER)
					.addScalar("idArea", StandardBasicTypes.INTEGER)
					.addScalar("idFactorDep", StandardBasicTypes.INTEGER)
					.addScalar("txtFactorDepVal", StandardBasicTypes.STRING)
					.addScalar("indVertical", StandardBasicTypes.STRING)
					.addScalar("txtFactor", StandardBasicTypes.STRING)
					.addScalar("indFactor2", StandardBasicTypes.STRING)
					.addScalar("indFactorType", StandardBasicTypes.STRING)
					.addScalar("indRequiredSave", StandardBasicTypes.STRING)
					.addScalar("indRequiredSubmit", StandardBasicTypes.STRING)
					.setParameter("idArSafetyAssmt", idSafetyAssmt).setParameter("idFactor", idFactor)
					.setResultTransformer(Transformers.aliasToBean(ARSafetyAssmtFactorValueDto.class)).list();

			arSafetyAssmtFactorValueDto = arSafetyAssmtFactorValueDtoList.get(0);
		}

		return arSafetyAssmtFactorValueDto;
	}

}