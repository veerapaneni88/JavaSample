package us.tx.state.dfps.service.sscc.serviceimpl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.SSCCCatchmentsRes;
import us.tx.state.dfps.service.sscc.controller.SSCCEmployeeDetailController;
import us.tx.state.dfps.service.sscc.dao.SSCCCatchmentsDao;
import us.tx.state.dfps.service.sscc.dao.SSCCEmployeeDetailDao;
import us.tx.state.dfps.service.sscc.service.SSCCCatchmentsService;
import us.tx.state.dfps.web.sscc.bean.SSCCEmployeeDetailsBean;

import java.util.*;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@Service
@Transactional
public class SSCCCatchmentsServiceImpl implements SSCCCatchmentsService {

    @Autowired
    SSCCCatchmentsDao ssccCatchmentsDao;

    private static final Logger logger = Logger.getLogger(SSCCCatchmentsServiceImpl.class);
    @Override
    public String getCatchmentsForRegion(String region) throws Exception {

        return ssccCatchmentsDao.getCatchmentsByRegion(region).replaceAll("\t","    ");
    }

    @Override
    public TreeMap<String, String> getCatchmentsByRegionMap(String region) throws PatternSyntaxException {

        TreeMap<String,String> catchments = ssccCatchmentsDao.getCatchmentsByRegionMap(region);

        for (Map.Entry<String, String> entry : catchments.entrySet()) {
            catchments.put(entry.getKey(),entry.getValue().replaceAll("\t","    "));
        }
        return catchments;
    }
}
