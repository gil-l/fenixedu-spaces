package org.fenixedu.spaces.ui;

import java.util.List;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequestState;
import org.fenixedu.spaces.ui.services.OccupationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.base.Strings;

@SpringFunctionality(app = SpacesController.class, title = "title.space.occupations.requests")
@RequestMapping("/spaces/occupations/requests")
public class OccupationRequestsController {

    @Autowired
    OccupationService occupationService;
    private int parseInt;

    @RequestMapping(value = "/search/{id}", method = RequestMethod.GET)
    public String search(@PathVariable Integer id, Model model) {
        model.addAttribute("occupationRequest", occupationService.search(id));
        return "occupations/requests/single";
    }

    @RequestMapping(value = "/{occupationRequest}", method = RequestMethod.GET)
    public String view(@PathVariable OccupationRequest occupationRequest, Model model) {
        model.addAttribute("occupationRequest", occupationRequest);
        return "occupations/requests/single";
    }

    @RequestMapping(value = "/{occupationRequest}/comments", method = RequestMethod.POST)
    public RedirectView addComment(@PathVariable OccupationRequest occupationRequest, @RequestParam String description,
            Model model, @RequestParam OccupationRequestState state) {
        occupationService.addComment(occupationRequest, description, state);
        return new RedirectView("/spaces/occupations/requests/" + occupationRequest.getExternalId(), true);
    }

    @RequestMapping(value = "/{occupationRequest}/{state}", method = RequestMethod.GET)
    public RedirectView changeRequestState(@PathVariable OccupationRequest occupationRequest, Model model,
            @PathVariable OccupationRequestState state) {
        if (occupationRequest.getCurrentState() != OccupationRequestState.NEW) {
            return new RedirectView("/spaces/occupations/requests/", true);
        }
        switch (state) {
        case OPEN:
            occupationService.openRequest(occupationRequest, Authenticate.getUser());
        case RESOLVED:
            occupationService.closeRequest(occupationRequest, Authenticate.getUser());
            break;
        }
        return new RedirectView("/spaces/occupations/requests/", true);
    }

    @RequestMapping(value = "/filter/{campus}", method = RequestMethod.GET)
    public String filter(@PathVariable Space campus, Model model, @RequestParam(defaultValue = "1") String p, @RequestParam(
            required = false) OccupationRequestState state) {
        addCampus(model);
        model.addAttribute("selectedCampi", campus);
        addRequests(model, campus, p, state);
        return "occupations/requests/view";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String viewRequests(Model model, @RequestParam(defaultValue = "1") String p,
            @RequestParam(required = false) OccupationRequestState state) {
        addCampus(model);
        addRequests(model, null, p, state);
        return "occupations/requests/view";
    }

    public void addRequests(Model model, Space campus) {
        addRequests(model, campus, null, null);
    }

    public void addRequests(Model model, Space campus, String page, OccupationRequestState state) {

        String myRequestsPage = state == null ? page : null;
        String openRequestsPage = OccupationRequestState.OPEN.equals(state) ? page : null;
        String newRequestsPage = OccupationRequestState.NEW.equals(state) ? page : null;
        String resolvedRequestsPage = OccupationRequestState.RESOLVED.equals(state) ? page : null;

        List<OccupationRequest> myRequests = occupationService.getRequestsToProcess(Authenticate.getUser(), campus);
        List<OccupationRequest> openRequests = occupationService.all(OccupationRequestState.OPEN, campus);
        List<OccupationRequest> newRequests = occupationService.all(OccupationRequestState.NEW, campus);
        List<OccupationRequest> resolvedRequests = occupationService.all(OccupationRequestState.RESOLVED, campus);

        model.addAttribute("myRequests", getBook(myRequests, myRequestsPage));
        model.addAttribute("openRequests", getBook(openRequests, openRequestsPage));
        model.addAttribute("newRequests", getBook(newRequests, newRequestsPage));
        model.addAttribute("resolvedRequests", getBook(resolvedRequests, resolvedRequestsPage));
    }

    private PagedListHolder<OccupationRequest> getBook(List<OccupationRequest> requests, String pageString) {
        PagedListHolder<OccupationRequest> book = new PagedListHolder<>(requests);
        book.setPageSize(30);
        int page = 0;

        if (Strings.isNullOrEmpty(pageString)) {
            page = 0;
        } else {
            try {
                page = Integer.parseInt(pageString);
            } catch (NumberFormatException nfe) {
                if ("f".equals(pageString)) {
                    page = 0;
                } else if ("l".equals(pageString)) {
                    page = book.getPageCount();
                }
            }
        }
        book.setPage(page == 0 ? 0 : page - 1);
        return book;
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String showCreateForm(Model model) {
        addCampus(model);
        model.addAttribute("occupation", new OccupationRequestBean());
        return "occupations/requests/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RedirectView createRequest(@ModelAttribute OccupationRequestBean bean, BindingResult errors) {
        occupationService.createRequest(bean);
        return new RedirectView("/spaces/occupations/requests", true);
    }

    private Model addCampus(Model model) {
        return model.addAttribute("campus", occupationService.getAllCampus());
    }

}