package us.tx.state.dfps.service.subcare.dao;

import us.tx.state.dfps.common.domain.BatchParameters;

public interface BatchParametersDao {

    BatchParameters getBatchParameters (String program, String parameter);
}
