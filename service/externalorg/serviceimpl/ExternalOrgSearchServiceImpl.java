/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: The class implements the External Organization Search Service.
 *Jul 9, 2018- 3:35:05 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.serviceimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.externalorg.dto.ExternalOrgDto;
import us.tx.state.dfps.common.externalorg.dto.ExternalOrgSearchParamDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.ExternalOrgSearchRes;
import us.tx.state.dfps.service.common.util.ResourceUtil;
import us.tx.state.dfps.service.externalorg.dao.ExternalOrgSearchDao;
import us.tx.state.dfps.service.externalorg.service.ExternalOrgSearchService;

@Transactional
@Service
public class ExternalOrgSearchServiceImpl implements ExternalOrgSearchService {

	public ExternalOrgSearchServiceImpl() {
	}

	public static final Logger log = Logger.getLogger(ExternalOrgSearchServiceImpl.class);

	@Autowired
	ExternalOrgSearchDao extOrgSearchDao;

	/**
	 * Method Name: executeOrganizationSearch Method Description: Search Service
	 * for External Organization Records.
	 * 
	 * @param searchParam
	 * @return response
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExternalOrgSearchRes executeOrganizationSearch(ExternalOrgSearchParamDto searchParam) {
		log.info("executeOrganizationSearch method of ExternalOrgSearchServiceImpl : Execution Started");
		ExternalOrgSearchRes response = new ExternalOrgSearchRes();
		// Remove the wild cards if search is made with wild card chars.
		filterWildCardChars(searchParam);
		// Search the Org against all other search criteria.
		List<ExternalOrgDto> orgResultList = extOrgSearchDao.searchExternalOrg(searchParam);
		// Check if the result list is empty or records exceeds maximum
		// allowable limit.
		if (!ObjectUtils.isEmpty(orgResultList)) {
			// Return message for narrowing search criteria.
			if (ServiceConstants.MAX_SIZE.intValue() < orgResultList.size()) {
				ErrorDto error = new ErrorDto();
				error.setErrorMsg(ServiceConstants.TOO_MANY_RECORD_EXCEPTION);
				response.setErrorDto(error);
			} else
				response.setResultList(orgResultList);
		}
		log.info("executeOrganizationSearch method of ExternalOrgSearchServiceImpl : Returns "
				+ response.getResultList().size() + " Results");
		return response;
	}

	/**
	 * Method Name: filterWildCardChars Method Description: This method removes
	 * the wild Card characters from the Search parameters As we have wild card
	 * search on Following elements on Org Search Page. 1. Org Name, 2. First
	 * Name, 3. Last Name, 4. Street Address.
	 * 
	 * @param searchParam
	 */
	private void filterWildCardChars(ExternalOrgSearchParamDto searchParam) {
		if (!StringUtils.isEmpty(searchParam.getNmLegalOrg())) {
			searchParam.setNmLegalOrg(ResourceUtil.removeOracleSpChars(searchParam.getNmLegalOrg()));
		}
		if (!StringUtils.isEmpty(searchParam.getTxtStreetAddress())) {
			searchParam.setTxtStreetAddress(ResourceUtil.removeOracleSpChars(searchParam.getTxtStreetAddress()));
		}
		if (!StringUtils.isEmpty(searchParam.getNmPocFirst())) {
			searchParam.setNmPocFirst(ResourceUtil.removeOracleSpChars(searchParam.getNmPocFirst()));
		}
		if (!StringUtils.isEmpty(searchParam.getNmPocLast())) {
			searchParam.setNmPocLast(ResourceUtil.removeOracleSpChars(searchParam.getNmPocLast()));
		}
		if (!StringUtils.isEmpty(searchParam.getNmCity())) {
			searchParam.setNmCity(ResourceUtil.removeOracleSpChars(searchParam.getNmCity()));
		}
	}
}
