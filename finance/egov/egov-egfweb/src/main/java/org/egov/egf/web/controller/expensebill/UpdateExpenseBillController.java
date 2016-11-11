/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.egf.web.controller.expensebill;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.commons.service.ChartOfAccountsService;
import org.egov.commons.service.CheckListService;
import org.egov.egf.expensebill.service.ExpenseBillService;
import org.egov.egf.utils.FinancialUtils;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.utils.DateUtils;
import org.egov.infstr.models.EgChecklists;
import org.egov.model.bills.EgBillregister;
import org.egov.utils.FinancialConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/expensebill")
public class UpdateExpenseBillController extends BaseBillController {

    @Autowired
    private ExpenseBillService expenseBillService;

    @Autowired
    @Qualifier("chartOfAccountsService")
    private ChartOfAccountsService chartOfAccountsService;

    @Autowired
    private FinancialUtils financialUtils;

    @Autowired
    private CheckListService checkListService;

    public UpdateExpenseBillController(final AppConfigValueService appConfigValuesService) {
        super(appConfigValuesService);
    }

    @ModelAttribute("egBillregister")
    public EgBillregister getEgBillregister(@PathVariable final String billId) {
        final EgBillregister egBillregister = expenseBillService.getById(Long.parseLong(billId));
        return egBillregister;
    }

    @RequestMapping(value = "/update/{billId}", method = RequestMethod.GET)
    public String updateForm(final Model model, @PathVariable final String billId,
            final HttpServletRequest request) throws ApplicationException {
        final EgBillregister egBillregister = expenseBillService.getById(Long.parseLong(billId));
        setDropDownValues(model);
        model.addAttribute("stateType", egBillregister.getClass().getSimpleName());
        if (egBillregister.getState() != null)
            model.addAttribute("currentState", egBillregister.getState().getValue());
        model.addAttribute("workflowHistory",
                expenseBillService.getHistory(egBillregister.getState(), egBillregister.getStateHistory()));
        prepareWorkflow(model, egBillregister, new WorkflowContainer());
        egBillregister.getBillDetails().addAll(egBillregister.getEgBilldetailes());
        prepareBillDetailsForView(egBillregister);
        prepareCheckList(egBillregister);
        model.addAttribute("egBillregister", egBillregister);
        if (egBillregister.getState() != null
                && FinancialConstants.WORKFLOW_STATE_REJECTED.equals(egBillregister.getState().getValue())) {
            final List<AppConfigValues> cutOffDateconfigValue = appConfigValuesService
                    .getConfigValuesByModuleAndKey(FinancialConstants.MODULE_NAME_APPCONFIG,
                            FinancialConstants.KEY_DATAENTRYCUTOFFDATE);

            if (!cutOffDateconfigValue.isEmpty()) {
                final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                List<String> validActions = Collections.emptyList();
                validActions = Arrays.asList(FinancialConstants.BUTTONFORWARD, FinancialConstants.CREATEANDAPPROVE);
                model.addAttribute("validActionList", validActions);
                try {
                    model.addAttribute("cutOffDate",
                            DateUtils.getDefaultFormattedDate(df.parse(cutOffDateconfigValue.get(0).getValue())));
                } catch (final ParseException e) {

                }
            }
            model.addAttribute("mode", "edit");
            return "expensebill-update";
        } else {
            model.addAttribute("mode", "view");
            return "expensebill-view";
        }
    }

    @RequestMapping(value = "/update/{billId}", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute("egBillregister") final EgBillregister egBillregister,
            final BindingResult resultBinder, final RedirectAttributes redirectAttributes, final Model model,
            final HttpServletRequest request, @RequestParam final String workFlowAction)
            throws ApplicationException, IOException {

        String mode = "";
        EgBillregister updatedEgBillregister = null;

        if (request.getParameter("mode") != null)
            mode = request.getParameter("mode");

        Long approvalPosition = 0l;
        String approvalComment = "";

        if (request.getParameter("approvalComent") != null)
            approvalComment = request.getParameter("approvalComent");

        if (request.getParameter("approvalPosition") != null && !request.getParameter("approvalPosition").isEmpty())
            approvalPosition = Long.valueOf(request.getParameter("approvalPosition"));

        if ((approvalPosition == null || approvalPosition.equals(Long.valueOf(0)))
                && request.getParameter("approvalPosition") != null
                && !request.getParameter("approvalPosition").isEmpty())
            approvalPosition = Long.valueOf(request.getParameter("approvalPosition"));

        if (egBillregister.getStatus() != null
                && FinancialConstants.CONTINGENCYBILL_REJECTED_STATUS.equals(egBillregister.getStatus().getCode())) {
            populateBillDetails(egBillregister);
            validateBillNumber(egBillregister, resultBinder);
            validateLedgerAndSubledger(egBillregister, resultBinder);
        }
        if (resultBinder.hasErrors()) {
            setDropDownValues(model);
            model.addAttribute("stateType", egBillregister.getClass().getSimpleName());
            model.addAttribute("approvalDesignation", request.getParameter("approvalDesignation"));
            model.addAttribute("approvalPosition", request.getParameter("approvalPosition"));
            prepareWorkflow(model, egBillregister, new WorkflowContainer());
            model.addAttribute("approvalDesignation", request.getParameter("approvalDesignation"));
            model.addAttribute("approvalPosition", request.getParameter("approvalPosition"));
            model.addAttribute("designation", request.getParameter("designation"));
            if (egBillregister.getState() != null
                    && FinancialConstants.WORKFLOW_STATE_REJECTED.equals(egBillregister.getState().getValue())) {
                final List<AppConfigValues> cutOffDateconfigValue = appConfigValuesService
                        .getConfigValuesByModuleAndKey(FinancialConstants.MODULE_NAME_APPCONFIG,
                                FinancialConstants.KEY_DATAENTRYCUTOFFDATE);

                if (!cutOffDateconfigValue.isEmpty()) {
                    final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    List<String> validActions = Collections.emptyList();
                    validActions = Arrays.asList(FinancialConstants.BUTTONFORWARD, FinancialConstants.CREATEANDAPPROVE);
                    model.addAttribute("validActionList", validActions);
                    try {
                        model.addAttribute("cutOffDate",
                                DateUtils.getDefaultFormattedDate(df.parse(cutOffDateconfigValue.get(0).getValue())));
                    } catch (final ParseException e) {

                    }
                }
                model.addAttribute("mode", "edit");
                return "expensebill-update";
            } else {
                model.addAttribute("mode", "view");
                return "expensebill-view";
            }
        } else {
            if (null != workFlowAction)
                updatedEgBillregister = expenseBillService.update(egBillregister, approvalPosition, approvalComment, null,
                        workFlowAction);
            redirectAttributes.addFlashAttribute("egBillregister", updatedEgBillregister);

            // For Get Configured ApprovalPosition from workflow history
            if (approvalPosition == null || approvalPosition.equals(Long.valueOf(0)))
                approvalPosition = expenseBillService.getApprovalPositionByMatrixDesignation(
                        egBillregister, null, mode, workFlowAction);

            final String approverDetails = financialUtils.getApproverDetails(updatedEgBillregister.getStatus(),
                    updatedEgBillregister.getState(), updatedEgBillregister.getId(), approvalPosition);

            return "redirect:/expensebill/success?approverDetails= " + approverDetails + "&billNumber="
                    + updatedEgBillregister.getBillnumber();
        }
    }

    @RequestMapping(value = "/view/{billId}", method = RequestMethod.GET)
    public String view(final Model model, @PathVariable final String billId,
            final HttpServletRequest request) throws ApplicationException {
        final EgBillregister egBillregister = expenseBillService.getById(Long.parseLong(billId));
        setDropDownValues(model);
        egBillregister.getBillDetails().addAll(egBillregister.getEgBilldetailes());
        model.addAttribute("mode", "readOnly");
        prepareBillDetailsForView(egBillregister);
        prepareCheckList(egBillregister);
        model.addAttribute("egBillregister", egBillregister);
        return "expensebill-view";
    }

    private void prepareCheckList(final EgBillregister egBillregister) {
        final List<EgChecklists> checkLists = checkListService.getByObjectId(egBillregister.getId());
        egBillregister.getCheckLists().addAll(checkLists);
    }

    @Override
    protected void setDropDownValues(final Model model) {
        super.setDropDownValues(model);
    }

}
