package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.RetrieveNamesInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveNamesOutputDto;

//Ccmnd7dDao
public interface FetchNamesDao {
	public void fetchNames(RetrieveNamesInputDto retrieveNamesInputDto, RetrieveNamesOutputDto retrieveNamesOutputDto);

}
