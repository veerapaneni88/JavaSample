package us.tx.state.dfps.service.legal.service;

import java.util.List;

import us.tx.state.dfps.service.legal.dto.CaseDbDto;
import us.tx.state.dfps.service.legal.dto.RetrvInDto;
import us.tx.state.dfps.service.legal.dto.RetrvOutDto;
import us.tx.state.dfps.service.sdmriskassessment.dto.StageDBDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is the
 * interface for LegalActionRetrievalServiceImpl Oct 30, 2017- 4:34:59 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface LegalActionRetrievalService {
	/**
	 * 
	 * Method Name: legalActionOutcomeRtrv Method Description:This is the
	 * retrieval service for the Legal Action/Outcome window. Tuxedo: CSUB38S
	 * 
	 * @param retrvInDto
	 * @return RetrvOutDto
	 */
	public RetrvOutDto legalActionOutcomeRtrv(RetrvInDto retrvInDto);

	/**
	 * 
	 * Method Name: fetchStageDBAndPrincipals Method Description:This is the
	 * retrieval for stages and related principals for the case.
	 * 
	 * @param ulIdCase
	 * @return List<StageDBDto>
	 */
	public List<StageDBDto> fetchStageDBAndPrincipals(Long ulIdCase);

	/**
	 * 
	 * Method Name: getCaseSummary Method Description:This is the retrieval of
	 * Case with all stages info.
	 * 
	 * @param ulIdCase
	 * @return CaseDbDto @
	 */
	public CaseDbDto getCaseSummary(Long ulIdCase);
}
