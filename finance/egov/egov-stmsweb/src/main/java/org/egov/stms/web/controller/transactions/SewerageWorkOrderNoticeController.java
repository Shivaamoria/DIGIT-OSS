/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

        1) All versions of this program, verbatim or modified must carry this
           Legal Notice.

        2) Any misrepresentation of the origin of the material is prohibited. It
           is required that all modified versions of this material be marked in
           reasonable ways as different from the original version.

        3) This license does not grant any rights to any user of the program
           with regards to rights under trademark law for use of the trade names
           or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.stms.web.controller.transactions;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.DesignationService;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.stms.masters.service.FeesDetailMasterService;
import org.egov.stms.notice.service.SewerageNoticeService;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.egov.stms.transactions.service.SewerageApplicationDetailsService;
import org.egov.stms.transactions.service.SewerageConnectionFeeService;
import org.egov.stms.utils.SewerageTaxUtils;
import org.egov.stms.utils.constants.SewerageTaxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/transactions")
public class SewerageWorkOrderNoticeController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private SewerageTaxUtils sewerageTaxUtils;

    public static final String WORKORDERNOTICE = "sewerageWorkOrderNotice";

    private final Map<String, Object> reportParams = new HashMap<String, Object>();
    private ReportRequest reportInput = null;
    private ReportOutput reportOutput = null;
    String errorMessage = "";
    @Autowired
    private SewerageApplicationDetailsService sewerageApplicationDetailsService;
    @Autowired
    @Qualifier("fileStoreService")
    protected FileStoreService fileStoreService;
    @Autowired
    private FeesDetailMasterService feesDetailMasterService;

    @Autowired
    private SewerageConnectionFeeService SewerageConnectionFeeService;
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private DesignationService designationService;
    
    @Autowired
    private SewerageNoticeService sewerageNoticeService;

    @RequestMapping(value = "/workordernotice", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<byte[]> createWorkOrderReport(final HttpServletRequest request,
            final HttpSession session) {
        final SewerageApplicationDetails sewerageApplicationDetails = sewerageApplicationDetailsService
                .findByApplicationNumber(request.getParameter("pathVar"));
        if (!errorMessage.isEmpty())
            return redirect();
        return generateReport(sewerageApplicationDetails, session);
    }

    private ResponseEntity<byte[]> generateReport(final SewerageApplicationDetails sewerageApplicationDetails,
            final HttpSession session) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.add("content-disposition", "inline;filename=WorkOrderNotice.pdf");
        reportOutput = sewerageNoticeService.generateReportOutputForWorkOrder(sewerageApplicationDetails, session);
        return new ResponseEntity<byte[]>(reportOutput.getReportOutputData(), headers, HttpStatus.CREATED);
    }

    public void validateWorkOrder(final SewerageApplicationDetails sewerageApplicationDetails, final Boolean isView) {
        if (null != sewerageApplicationDetails && sewerageApplicationDetails.getConnection().getLegacy())
            errorMessage = messageSource.getMessage("err.validate.workorder.for.legacy", new String[] { "" }, null);
        else if (isView && null == sewerageApplicationDetails.getWorkOrderNumber())
            errorMessage = messageSource.getMessage("err.validate.workorder.view",
                    new String[] { sewerageApplicationDetails.getApplicationNumber() }, null);
        else if (!isView
                && !sewerageApplicationDetails.getStatus().getCode()
                        .equalsIgnoreCase(SewerageTaxConstants.APPLICATION_STATUS_WOGENERATED))
            errorMessage = messageSource.getMessage("err.validate.workorder.view",
                    new String[] { sewerageApplicationDetails.getApplicationNumber() }, null);
    }

    @RequestMapping(value = "/workorder/view/{applicationNumber}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<byte[]> viewReport(@PathVariable final String applicationNumber,
            final HttpSession session) {
        final SewerageApplicationDetails sewerageApplicationDetails = sewerageApplicationDetailsService
                .findByApplicationNumber(applicationNumber);
        validateWorkOrder(sewerageApplicationDetails, true);
        if (!errorMessage.isEmpty())
            return redirect();
        return generateReport(sewerageApplicationDetails, session);
    }

    private ResponseEntity<byte[]> redirect() {
        errorMessage = "<html><body><p style='color:red;border:1px solid gray;padding:15px;'>" + errorMessage
                + "</p></body></html>";
        final byte[] byteData = errorMessage.getBytes();
        errorMessage = "";
        return new ResponseEntity<byte[]>(byteData, HttpStatus.CREATED);
    }
}
