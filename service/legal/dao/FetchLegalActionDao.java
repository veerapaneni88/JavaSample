package us.tx.state.dfps.service.legal.dao;

import java.util.List;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dto.CaseDbDto;
import us.tx.state.dfps.service.legal.dto.FetchLegalActionInDto;
import us.tx.state.dfps.service.legal.dto.FetchLegalActionOutDto;
import us.tx.state.dfps.service.legal.dto.PersonInfoDbDto;
import us.tx.state.dfps.service.sdmriskassessment.dto.StageDBDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Fetches the
 * Legal Action for the event Nov 1, 2017- 10:19:16 AM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface FetchLegalActionDao {

	/**
	 * Method Name: fetchLegalActionForEvent Method Description:Retrieves the
	 * Legal Action for the event DAM: cses06d
	 * 
	 * @param fetchLegalActionInDto
	 * @return FetchLegalActionOutDto
	 * @throws DataNotFoundException
	 */
	public FetchLegalActionOutDto fetchLegalActionForEvent(FetchLegalActionInDto fetchLegalActionInDto);

	/**
	 * Method Name: getStageList Method Description:The getStageList method
	 * finds all the stage information related to the user.
	 * 
	 * @param caseDbDto
	 * @return List<StageDBDto>
	 * @throws DataNotFoundException
	 */
	public List<StageDBDto> getStageList(CaseDbDto caseDbDto);

	/**
	 * Method Name: getCase Method Description:The getCase method selects the
	 * case information related to the user.
	 * 
	 * @param caseDbDto
	 * @return CaseDbDto
	 * @throws DataNotFoundException
	 */
	public CaseDbDto getCase(CaseDbDto caseDbDto);

	/**
	 * Method Name: getPerson Method Description:The getPerson method selects
	 * the id, full name, and phone number of the person who is assigned to the
	 * case.
	 * 
	 * @param stageDBDto
	 * @return PersonInfoDbDto
	 * @throws DataNotFoundException
	 */
	public PersonInfoDbDto getPerson(StageDBDto stageDBDto);

}
