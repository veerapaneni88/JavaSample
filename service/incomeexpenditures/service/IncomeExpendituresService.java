package us.tx.state.dfps.service.incomeexpenditures.service;

import java.util.List;

import us.tx.state.dfps.service.common.request.IncomeExpenditureReq;
import us.tx.state.dfps.service.common.request.SaveIncRsrcReq;
import us.tx.state.dfps.service.common.response.IncomeExpenditureRes;
import us.tx.state.dfps.service.fce.IncomeExpenditureDto;
import us.tx.state.dfps.service.person.dto.PersonIncomeResourceDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:EJB Name:
 * IncomeExpendituresBean Nov 21, 2017- 12:58:58 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface IncomeExpendituresService {

	/**
	 * Method Name: readIncomeExpDtl Method Description: Method is EJB Name:
	 * IncomeExpendituresBean
	 * 
	 * @param IncomeExpenditureReq
	 * @return IncomeExpenditureRes @
	 */

	public IncomeExpenditureRes readIncomeExpDtl(IncomeExpenditureReq incomeExpenditureReq);

	/**
	 * Method Name: saveFceApplication Method Description: Method is EJB Name:
	 * IncomeExpendituresBean
	 * 
	 * @param IncomeExpenditureReq
	 * @return IncomeExpenditureRes @
	 */
	public IncomeExpenditureRes saveFceApplication(IncomeExpenditureReq incomeExpenditureReq);

	/**
	 * Method Name: syncApplication Method Description: Method is used to sync
	 * fce application data EJB Name: IncomeExpenditureDto
	 * 
	 * @param IncomeExpenditureReq
	 * @return IncomeExpenditureRes
	 */
	public IncomeExpenditureRes submitApplication(IncomeExpenditureDto incomeExpenditureDto);

	/**
	 * Method Name: calcualteFceData Method Description: Method is used to
	 * calcualte fce income expenditures data. EJB Name: IncomeExpendituresBean
	 * 
	 * @param IncomeExpenditureReq
	 * @return IncomeExpenditureRes
	 */
	public IncomeExpenditureRes calcualteFceData(IncomeExpenditureReq incomeExpenditureReq);

	/**
	 * 
	 * Method Name: readDepCareDeduction Method Description: this method used to
	 * read the DepcareDeduction
	 * 
	 * @param incomeExpenditureReq
	 * @return
	 */
	public IncomeExpenditureRes readDepCareDeduction(IncomeExpenditureReq incomeExpenditureReq);

	/**
	 * Method Name: calcualteFceData Method Description: Method is used to
	 * calcualte fce income expenditures data. EJB Name: IncomeExpendituresBean
	 * 
	 * @param IncomeExpenditureReq
	 * @return IncomeExpenditureRes
	 */
	public IncomeExpenditureRes checkDepCareDeductionErrors(IncomeExpenditureDto incomeExpenditureDto);

	/**
	 * Method Name: getIncomeResource Method Description: This method will fetch
	 * person's income and resources from the table
	 * 
	 * @param personReq
	 * @return personIncomeResourceRequest
	 */
	public List<PersonIncomeResourceDto> getIncomeResource(Long idPerson);

	/**
	 * Method Name : saveIncomeResource Method Description: Provides save logic
	 * for incomeAndResources of a person
	 * 
	 * @param saveIncRsrcReq
	 */
	public void saveIncomeResource(SaveIncRsrcReq saveIncRsrcReq);

	/**
	 * Method Name: deleteIncomeResource Method Description:This method will
	 * delete the incomeAndResours of a person
	 * 
	 * @param saveIncRsrcReq
	 */
	public void deleteIncomeResource(SaveIncRsrcReq saveIncRsrcReq);

	void setIncomeAndResources(IncomeExpenditureDto incomeExpenditureDto);
}
