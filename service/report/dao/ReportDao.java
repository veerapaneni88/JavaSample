package us.tx.state.dfps.service.report.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.ReportList;
import us.tx.state.dfps.service.reports.dto.ReportDto;
import us.tx.state.dfps.service.reports.dto.ReportListDto;
import us.tx.state.dfps.service.reports.dto.ReportParameterDto;

public interface ReportDao {

	public List<ReportParameterDto> getReportParameters(String sqrName, String sqrVer);

	List<ReportParameterDto> getRunnableReportParameters();

	public ReportDto getReportTypeDetails(String sqrName, String sqrVer);

	List<ReportDto> getRunnableReportDetails();

	public ReportListDto saveReportList(ReportListDto reportListDto);

	public ReportList getReportList(Long idReportList);

	public void updateReportList(ReportList reportList);

	public void deleteReportList(ReportList reportList);

}
