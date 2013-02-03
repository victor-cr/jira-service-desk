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
    private final HandlerModel model = new HandlerModel();

    public EditSmartMailHandlerDetailsWebAction(PluginAccessor pluginAccessor, IssueTypeManager issueTypeManager, ProjectManager projectManager) {
        this.pluginAccessor = pluginAccessor;

        configuration = new ServiceConfigurationAdapter(pluginAccessor);
    }

    public boolean isEditing() {
        return !configuration.isNull() && configuration.getServiceId() != null;
    }

    public HandlerModel getModel() {
        return model;
    }

    public void setProjectKey(String projectKey) {
        model.setProjectKey(projectKey);
    }

    public void setIssueTypeKey(String issueTypeKey) {
        model.setIssueTypeKey(issueTypeKey);
    }

    public void setStripQuotes(boolean stripQuotes) {
        model.setStripQuotes(stripQuotes);
    }

    public void setReporterUsername(String reporterUsername) {
        model.setReporterUsername(reporterUsername);
    }

    public void setCatchEmail(String catchEmail) {
        model.setCatchEmail(catchEmail);
    }

    public void setCreateUsers(boolean createUsers) {
        model.setCreateUsers(createUsers);
    }

    public void setNotifyUsers(boolean notifyUsers) {
        model.setNotifyUsers(notifyUsers);
    }

    public void setCcWatcher(boolean ccWatcher) {
        model.setCcWatcher(ccWatcher);
    }

    public void setCcAssignee(boolean ccAssignee) {
        model.setCcAssignee(ccAssignee);
    }

    public void setSplitRegex(String splitRegex) {
        model.setSplitRegex(splitRegex);
    }

    public BrowserUtils getBrowserUtils() {
        return new BrowserUtils();
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

            copyServiceSettings(serviceContainer);
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
                .add("project", new String[]{projectKey})
                .add(AbstractMessageHandlingService.KEY_HANDLER_PARAMS, new String[]{ServiceUtils.toParameterString(getHandlerParams())})
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

    protected void copyServiceSettings(JiraServiceContainer container) throws ObjectConfigurationException {
        String params = container.getProperty(AbstractMessageHandlingService.KEY_HANDLER_PARAMS);

        Map<String, String> parameterMap = ServiceUtils.getParameterMap(params);

        projectKey = parameterMap.get(KEY_PROJECT);
    }

    protected Map<String, String> getHandlerParams() {
        return MapBuilder.build(KEY_PROJECT, projectKey);
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
