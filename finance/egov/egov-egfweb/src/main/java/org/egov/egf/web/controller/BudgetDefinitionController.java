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

package org.egov.egf.web.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.egov.commons.service.CFinancialYearService;
import org.egov.egf.web.adaptor.BudgetJsonAdaptor;
import org.egov.model.budget.Budget;
import org.egov.model.budget.BudgetDetail;
import org.egov.model.service.BudgetDefinitionService;
import org.egov.utils.BeReType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Controller
@RequestMapping("/budgetdefinition")
public class BudgetDefinitionController {
    private final static String BUDGET_NEW = "budgetdefinition-new";
    private final static String BUDGET_RESULT = "budgetdefinition-result";
    private final static String BUDGET_EDIT = "budgetdefinition-edit";
    private final static String BUDGET_VIEW = "budgetdefinition-view";
    private final static String BUDGET_SEARCH = "budgetdefinition-search";
    @Autowired
    private BudgetDefinitionService budgetDefinitionService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CFinancialYearService cFinancialYearService;

    private void prepareNewForm(Model model) {
        model.addAttribute("financialYearList", cFinancialYearService.getFinancialYearNotClosed());
        model.addAttribute("isBereList", Arrays.asList(BeReType.values()));
    };

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newForm(final Model model) {
        model.addAttribute("budget", new Budget());
        prepareNewForm(model);
        return BUDGET_NEW;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute final Budget budget, final BindingResult errors, final Model model,
            final RedirectAttributes redirectAttrs) {
        String validationMessage = budgetDefinitionService.validate(budget, errors);
        if (errors.hasErrors() || !StringUtils.isEmpty(validationMessage)) {
            prepareNewForm(model);
            model.addAttribute("budget", new Budget());
            model.addAttribute("validationMessage", validationMessage);
            return BUDGET_NEW;
        }
        budget.setStatus(budgetDefinitionService.getBudgetStatus("Created"));
        budgetDefinitionService.create(budget);
        redirectAttrs.addFlashAttribute("message",
                messageSource.getMessage("msg.budget.success", null, Locale.ENGLISH));
        return "redirect:/budgetdefinition/result/" + budget.getId();
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") final Long id, Model model) {
        Budget budget = budgetDefinitionService.findOne(id);
        prepareNewForm(model);
        model.addAttribute("budget", budget);
        model.addAttribute("modify", "modify");
        List<BudgetDetail> bd = budgetDefinitionService.getBudgetDetailList(budget.getId());
        if (!bd.isEmpty()) {
            model.addAttribute("mode", "edit");
        }
        return BUDGET_EDIT;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute final Budget budget, final BindingResult errors, final Model model,
            final RedirectAttributes redirectAttrs) {
        String validationMessage = budgetDefinitionService.validate(budget, errors);
        if (errors.hasErrors() || !StringUtils.isEmpty(validationMessage)) {
            prepareNewForm(model);
            model.addAttribute("validationMessage", validationMessage);
            model.addAttribute("modify", "modify");
            return BUDGET_EDIT;
        }
        budgetDefinitionService.update(budget);
        redirectAttrs.addFlashAttribute("message", messageSource.getMessage("msg.budget.update", null, null));
        return "redirect:/budgetdefinition/result/" + budget.getId();
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String view(@PathVariable("id") final Long id, Model model) {
        Budget budget = budgetDefinitionService.findOne(id);
        prepareNewForm(model);
        model.addAttribute("budget", budget);
        return BUDGET_VIEW;
    }

    @RequestMapping(value = "/result/{id}", method = RequestMethod.GET)
    public String result(@PathVariable("id") final Long id, Model model) {
        Budget budget = budgetDefinitionService.findOne(id);
        model.addAttribute("budget", budget);
        return BUDGET_RESULT;
    }

    @RequestMapping(value = "/search/{mode}", method = RequestMethod.GET)
    public String search(@PathVariable("mode") final String mode, Model model) {
        Budget budget = new Budget();
        prepareNewForm(model);
        model.addAttribute("budget", budget);
        return BUDGET_SEARCH;

    }

    @RequestMapping(value = "/ajaxsearch/{mode}", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String ajaxsearch(@PathVariable("mode") final String mode, Model model,
            @ModelAttribute final Budget budget) {
        List<Budget> searchResultList = budgetDefinitionService.search(budget);
        String result = new StringBuilder("{ \"data\":").append(toSearchResultJson(searchResultList)).append("}")
                .toString();
        return result;
    }

    public Object toSearchResultJson(final Object object) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.registerTypeAdapter(Budget.class, new BudgetJsonAdaptor()).create();
        final String json = gson.toJson(object);
        return json;
    }
//rename String financialYearId to long
    @RequestMapping(value = "/parents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getParents(@RequestParam("financialYearId") final String financialYearId,
            @RequestParam("isBeRe") String isBere) {
        final List<Budget> budgetList = budgetDefinitionService.parentList(isBere, Long.parseLong(financialYearId));
        String jsonResponse = toSearchResultJson(budgetList).toString();
        return jsonResponse;
    }

    @RequestMapping(value = "/referencebudget", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getRefencebudget(@RequestParam("financialYearId") final String financialYearId) {
        final List<Budget> referenceBudgetList = budgetDefinitionService
                .referenceBudgetList(Long.parseLong(financialYearId));
        final String jsonResponse = toSearchResultJson(referenceBudgetList).toString();
        return jsonResponse;
    }

    @RequestMapping(value = "/ajaxgetdropdownsformodify", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getReferenceBudgetForModify(@RequestParam("budgetId") final String budgetId) {
        final Budget budget = budgetDefinitionService.findOne((Long.parseLong(budgetId)));
        String jsonResponse = new StringBuilder("{ \"data\":").append(toSearchResultJson(budget)).append("}")
                .toString();
        return jsonResponse;
    }

}
