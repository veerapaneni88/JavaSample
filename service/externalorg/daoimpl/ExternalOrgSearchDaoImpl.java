/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Implementation Class for External Organization Search
 * Data Access Interface.
 *Jul 9, 2018- 3:45:48 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.externalorg.dto.ExternalOrgDto;
import us.tx.state.dfps.common.externalorg.dto.ExternalOrgSearchParamDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.externalorg.dao.ExternalOrgSearchDao;

@Repository
public class ExternalOrgSearchDaoImpl implements ExternalOrgSearchDao {

	public ExternalOrgSearchDaoImpl() {
	}

	public static final Logger log = Logger.getLogger(ExternalOrgSearchDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ExternalOrgDaoImpl.searchSelectStmt}")
	private String searchSelectStmt;

	@Value("${ExternalOrgDaoImpl.searchbyType}")
	private String searchbyType;

	@Value("${ExternalOrgDaoImpl.searchWhare}")
	private String searchWhare;

	@Value("${ExternalOrgDaoImpl.searchByEinTin}")
	private String searchByEinTin;

	@Value("${ExternalOrgDaoImpl.searchByEin}")
	private String searchByEin;

	@Value("${ExternalOrgDaoImpl.searchByTin}")
	private String searchByTin;

	@Value("${ExternalOrgDaoImpl.searchByLglName}")
	private String searchByLglName;

	@Value("${ExternalOrgDaoImpl.searchByCity}")
	private String searchByCity;

	@Value("${ExternalOrgDaoImpl.searchByState}")
	private String searchByState;

	@Value("${ExternalOrgDaoImpl.searchByStatus}")
	private String searchByStatus;

	@Value("${ExternalOrgDaoImpl.searchByPocNmFirst}")
	private String searchByPocNmFirst;

	@Value("${ExternalOrgDaoImpl.searchByPocNmLast}")
	private String searchByPocNmLast;

	@Value("${ExternalOrgDaoImpl.searchByStreet}")
	private String searchByStreet;

	@Value("${ExternalOrgDaoImpl.searchByZip}")
	private String searchByZip;

	@Value("${ExternalOrgDaoImpl.searchByZipExt}")
	private String searchByZipExt;

	/**
	 * Method Name: searchExternalOrg Method Description: Implementation method
	 * for External Organization search for the given criteria.
	 * 
	 * @param searchParam
	 * @return searchResult
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExternalOrgDto> searchExternalOrg(ExternalOrgSearchParamDto searchParam) {
		log.info("searchExternalOrg method of ExternalOrgSearchDaoImpl : Execution Started");
		StringBuilder qryString = new StringBuilder(searchSelectStmt);
		// if the user entered organization types
		if (!ObjectUtils.isEmpty(searchParam.getCdOrgTypeList()))
			qryString.append(searchbyType);
		qryString.append(searchWhare);
		// If both EIN/TIN are entered for the search
		if (!ObjectUtils.isEmpty(searchParam.getIdEin()) && !ObjectUtils.isEmpty(searchParam.getIdTin())) {
			qryString.append(searchByEinTin);
		} else if (!ObjectUtils.isEmpty(searchParam.getIdEin())) {
			// if the user entered idEin
			qryString.append(searchByEin);
		} else if (!ObjectUtils.isEmpty(searchParam.getIdTin())) {
			// if the user entered idTin
			qryString.append(searchByTin);
		} else {
			// if the user entered legal name
			if (!ObjectUtils.isEmpty(searchParam.getNmLegalOrg()))
				qryString.append(searchByLglName);
			// if the user entered street address
			if (!ObjectUtils.isEmpty(searchParam.getTxtStreetAddress()))
				qryString.append(searchByStreet);
			// if the user entered nmCity
			if (!ObjectUtils.isEmpty(searchParam.getNmCity()))
				qryString.append(searchByCity);
			// if the user entered ZIP
			if (!ObjectUtils.isEmpty(searchParam.getZip()))
				qryString.append(searchByZip);
			// if user entered ZIP extension
			if (!ObjectUtils.isEmpty(searchParam.getZipExt()))
				qryString.append(searchByZipExt);
			// if the user entered state
			if (!ObjectUtils.isEmpty(searchParam.getNmState()))
				qryString.append(searchByState);
			// if the user entered organization status
			if (!ObjectUtils.isEmpty(searchParam.getCdOrgStatus()))
				qryString.append(searchByStatus);
			// if the user entered organization POC first name
			if (!ObjectUtils.isEmpty(searchParam.getNmPocFirst()))
				qryString.append(searchByPocNmFirst);
			// if the user entered organization POC last name
			if (!ObjectUtils.isEmpty(searchParam.getNmPocLast()))
				qryString.append(searchByPocNmLast);
		}
		log.info("searchExternalOrg method of ExternalOrgSearchDaoImpl : Execution Search Query");
		SQLQuery sQLQuery = (SQLQuery) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(qryString.toString())).addScalar("idOrgDtl", StandardBasicTypes.LONG)
						.addScalar("nmLegal").addScalar("nmOther").addScalar("idEin", StandardBasicTypes.LONG)
						.addScalar("idTin", StandardBasicTypes.LONG).addScalar("cdOrgStatus").addScalar("nmCity")
						.addScalar("cdState").addScalar("nbrOrgPhone").addScalar("txtEmail").addScalar("txtCntctEmail")
						.setResultTransformer(Transformers.aliasToBean(ExternalOrgDto.class));
		// if the user entered organization types
		if (!ObjectUtils.isEmpty(searchParam.getCdOrgTypeList()))
			sQLQuery.setParameterList("cdOrgTypeList", searchParam.getCdOrgTypeList());
		// if the user entered idEin
		if (!ObjectUtils.isEmpty(searchParam.getIdEin()))
			sQLQuery.setParameter("idEin", searchParam.getIdEin());
		// if the user entered idTin
		if (!ObjectUtils.isEmpty(searchParam.getIdTin()))
			sQLQuery.setParameter("idTin", searchParam.getIdTin());
		// if the user entered legal name it is like search
		if (!ObjectUtils.isEmpty(searchParam.getNmLegalOrg()))
			sQLQuery.setParameter("nmLegal",
					ServiceConstants.PERCENT + searchParam.getNmLegalOrg() + ServiceConstants.PERCENT);
		// if the user entered street address
		if (!ObjectUtils.isEmpty(searchParam.getTxtStreetAddress()))
			sQLQuery.setParameter("txtStreetAddress",
					ServiceConstants.PERCENT + searchParam.getTxtStreetAddress() + ServiceConstants.PERCENT);
		if (!ObjectUtils.isEmpty(searchParam.getNmCity()))
			sQLQuery.setParameter("nmCity",
					ServiceConstants.PERCENT + searchParam.getNmCity() + ServiceConstants.PERCENT);

		if (!ObjectUtils.isEmpty(searchParam.getZip())) {

			sQLQuery.setParameter("zip", ServiceConstants.PERCENT + searchParam.getZip() + ServiceConstants.PERCENT);
		}
		if (!ObjectUtils.isEmpty(searchParam.getZipExt())) {

			sQLQuery.setParameter("zipExt",
					ServiceConstants.PERCENT + searchParam.getZipExt() + ServiceConstants.PERCENT);
		}
		if (!ObjectUtils.isEmpty(searchParam.getNmState()))
			sQLQuery.setParameter("nmState", searchParam.getNmState());
		if (!ObjectUtils.isEmpty(searchParam.getCdOrgStatus()))
			sQLQuery.setParameter("cdOrgStatus", searchParam.getCdOrgStatus());
		if (!ObjectUtils.isEmpty(searchParam.getNmPocFirst()))
			sQLQuery.setParameter("nmPocFirst",
					ServiceConstants.PERCENT + searchParam.getNmPocFirst() + ServiceConstants.PERCENT);
		if (!ObjectUtils.isEmpty(searchParam.getNmPocLast()))
			sQLQuery.setParameter("nmPocLast",
					ServiceConstants.PERCENT + searchParam.getNmPocLast() + ServiceConstants.PERCENT);

		List<ExternalOrgDto> searchResult = new ArrayList<>();

		searchResult = (List<ExternalOrgDto>) sQLQuery.list();
		log.info("searchExternalOrg method of ExternalOrgSearchDaoImpl : Returning Result List " + searchResult.size());
		return searchResult;
	}

}
