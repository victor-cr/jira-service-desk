package com.bics.jira.mail.web;

import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.service.JiraServiceContainer;
import com.atlassian.jira.service.ServiceManager;
import com.atlassian.jira.service.ServiceTypes;
import com.atlassian.jira.service.services.file.AbstractMessageHandlingService;
import com.atlassian.jira.service.services.file.FileService;
import com.atlassian.jira.service.services.mail.MailFetcherService;
import com.atlassian.jira.service.util.ServiceUtils;
import com.atlassian.jira.util.BrowserUtils;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.ServiceConfigurationAdapter;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:59
 */
@WebSudoRequired
public class EditSmartMailHandlerDetailsWebAction extends JiraWebActionSupport {
    private static final Logger LOG = Logger.getLogger(EditSmartMailHandlerDetailsWebAction.class);
    private static final String TO_HOME_PAGE = "IncomingMailServers.jspa";
    private static final String TO_WIZARD_FIRST_PAGE = "EditServerDetails!default.jspa";
    private static final String TO_SECURITY_BREACH = "securitybreach";
    private static final String TO_DEFAULT = "input";

    private final ServiceConfigurationAdapter configuration;
    private final PluginAccessor pluginAccessor;
    private final IssueTypeManager issueTypeManager;
    private final ProjectManager projectManager;

    private HandlerModel model = new HandlerModel();

    public EditSmartMailHandlerDetailsWebAction(PluginAccessor pluginAccessor, IssueTypeManager issueTypeManager, ProjectManager projectManager) {
        this.pluginAccessor = pluginAccessor;

        configuration = new ServiceConfigurationAdapter(pluginAccessor);
        this.issueTypeManager = issueTypeManager;
        this.projectManager = projectManager;
    }

    public boolean isEditing() {
        return !configuration.isNull() && configuration.getServiceId() != null;
    }

    public HandlerModel getModel() {
        return model;
    }

    public Collection<Project> getProjects() {
        return projectManager.getProjectObjects();
    }

    public Collection<IssueType> getIssueTypes() {
        return issueTypeManager.getIssueTypes();
    }

    public String getHandlerName() {
        return configuration.isNull() ? "" : pluginAccessor.getEnabledPluginModule(configuration.getHandlerKey()).getName();
    }

    public String getDetailsJson() {
        try {
            return new ObjectMapper().writeValueAsString(model);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDetailsJson(String detailsJson) {
        try {
            model = new ObjectMapper().readValue(detailsJson, HandlerModel.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JiraServiceContainer getService(Long id) {
        try {
            JiraServiceContainer service = getServiceManager().getServiceWithId(id);

            for (Class<?> clazz : ImmutableSet.of(MailFetcherService.class, FileService.class)) {
                if (clazz.isAssignableFrom(service.getServiceClassObject())) {
                    return service;
                }
            }
        } catch (Exception e) {
            LOG.error(String.format("Unable to get service with id %d", id), e);
        }

        return null;
    }

    public String doDefault() throws Exception {
        String result = super.doDefault();

        if (configuration.isNull()) {
            return returnCompleteWithInlineRedirect(TO_WIZARD_FIRST_PAGE);
        }

        if (configuration.getServiceId() != null) {
            JiraServiceContainer serviceContainer = getService(configuration.getServiceId());

            if (serviceContainer == null) {
                return returnCompleteWithInlineRedirect(TO_HOME_PAGE);
            }

            model.fromServiceParams(serviceContainer);
        }

        return result;
    }

    protected String doExecute() throws Exception {
        if (configuration.isNull()) {
            return returnCompleteWithInlineRedirect(TO_WIZARD_FIRST_PAGE);
        }

        try {
            if (configuration.getServiceId() != null && !canEditService(configuration.getServiceId()) || configuration.getServiceId() == null && !canAddService(configuration.getServiceClass())) {
                return TO_SECURITY_BREACH;
            }

            if (configuration.getServiceId() == null) {
                getServiceManager().addService(configuration.getServiceName(), configuration.getServiceClass(), configuration.getDelay() * 60000L, getServiceParams()).getId();
            } else {
                JiraServiceContainer service = getServiceManager().getServiceWithId(configuration.getServiceId());

                if (service.getName().equals(configuration.getServiceName()) && service.getServiceClass().equals(configuration.getServiceClass())) {
                    getServiceManager().editService(configuration.getServiceId(), configuration.getDelay() * 60000L, getServiceParams());
                } else {
                    if (!canAddService(configuration.getServiceClass())) {
                        return TO_SECURITY_BREACH;
                    }

                    Long serviceId = getServiceManager().addService(configuration.getServiceName(), configuration.getServiceClass(), configuration.getDelay() * 60000L, getServiceParams()).getId();

                    getServiceManager().removeService(configuration.getServiceId());

                    configuration.applyServiceId(serviceId);
                }
            }


            if (getHasErrorMessages()) {
                return TO_DEFAULT;
            }

            return returnCompleteWithInlineRedirect(TO_HOME_PAGE);
        } catch (Exception e) {
            LOG.error(getText("jmp.editHandlerDetails.cant.add.service", configuration.getServiceName()), e);
            addErrorMessage(getText("admin.errors.error.adding.service") + " " + e + ".");
            return TO_DEFAULT;
        }
    }

    private Map<String, String[]> getServiceParams() throws Exception {
        return MapBuilder.<String, String[]>newBuilder()
                .addAll(configuration.toServiceParams())
                .addAll(model.toServiceParams())
                .toMutableMap();
    }

    private ServiceTypes getServiceTypes() {
        return ComponentManager.getComponentInstanceOfType(ServiceTypes.class);
    }

    private ServiceManager getServiceManager() {
        return ComponentAccessor.getServiceManager();
    }

    private boolean canAddService(String clazz) {
        return getServiceTypes().isManageableBy(getLoggedInUser(), clazz);
    }

    private boolean canEditService(final Long serviceId) throws Exception {
        return Iterables.any(getServiceManager().getServicesManageableBy(getLoggedInUser()), new Predicate<JiraServiceContainer>() {
            public boolean apply(JiraServiceContainer container) {
                return container != null && serviceId.equals(container.getId());
            }
        });
    }

    /*
        protected class WebWorkErrorCollector implements MessageHandlerErrorCollector {
            public void info(String info) {
                LOG.info(info);
            }

            public void info(String info, Throwable e) {
                LOG.info(info, e);
            }

            public void error(String s, Throwable throwable) {
                addErrorMessage(s);
            }

            public void error(String s) {
                addErrorMessage(s);
            }

            public void warning(String s) {
                LOG.warn(s);
            }

            public void warning(String s, Throwable throwable) {
                LOG.warn(s, throwable);
            }
        }
    */
}
