package com.bics.jira.mail.web;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.service.JiraServiceContainer;
import com.atlassian.jira.service.ServiceManager;
import com.atlassian.jira.service.services.file.AbstractMessageHandlingService;
import com.atlassian.jira.service.util.ServiceUtils;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.PluginAccessor;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.ServiceConfigurationAdapter;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:59
 */
public abstract class AbstractServiceDeskMailHandlerDetailsWebAction extends JiraWebActionSupport {
    private static final Logger LOG = Logger.getLogger(AbstractServiceDeskMailHandlerDetailsWebAction.class);
    private static final String TO_HOME_PAGE = "IncomingMailServers.jspa";
    private static final String TO_WIZARD_FIRST_PAGE = "EditServerDetails!default.jspa";
    private static final String TO_SECURITY_BREACH = "securitybreach";
    private static final String TO_DEFAULT = "input";

    private final ServiceConfigurationAdapter configuration;
    private final PluginAccessor pluginAccessor;
    private final IssueTypeManager issueTypeManager;
    private final ProjectManager projectManager;

    protected final HandlerModel model = new HandlerModel();

    public AbstractServiceDeskMailHandlerDetailsWebAction(PluginAccessor pluginAccessor, IssueTypeManager issueTypeManager, ProjectManager projectManager) {
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

    public String getPluginKey() {
        if (configuration.isNull()) {
            return "";
        }

        String completeKey = configuration.getHandlerKey();

        return completeKey.substring(0, completeKey.indexOf(':'));
    }

    public String getHandlerName() {
        return configuration.isNull() ? "" : pluginAccessor.getEnabledPluginModule(configuration.getHandlerKey()).getName();
    }

    public JiraServiceContainer getService(Long id) {
        try {
            return getServiceManager().getServiceWithId(id);
        } catch (Exception e) {
            LOG.error("Unable to get service with id " + id, e);
            return null;
        }
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

            String handlerParams = serviceContainer.getProperty(AbstractMessageHandlingService.KEY_HANDLER_PARAMS);

            Map<String, String> params = ServiceUtils.getParameterMap(handlerParams);

            model.fromServiceParams(params);
        }

        return result;
    }

    protected String doExecute() throws Exception {
        if (configuration.isNull()) {
            return returnCompleteWithInlineRedirect(TO_WIZARD_FIRST_PAGE);
        }

        try {
            if (configuration.getServiceId() != null && !canEditService(configuration.getServiceId())) {
                return TO_SECURITY_BREACH;
            }

            if (configuration.getServiceId() == null) {
                getServiceManager().addService(configuration.getServiceName(), configuration.getServiceClass(), configuration.getDelay() * 60000L, getServiceParams()).getId();
            } else {
                JiraServiceContainer service = getServiceManager().getServiceWithId(configuration.getServiceId());

                if (service.getName().equals(configuration.getServiceName()) && service.getServiceClass().equals(configuration.getServiceClass())) {
                    getServiceManager().editService(configuration.getServiceId(), configuration.getDelay() * 60000L, getServiceParams());
                } else {
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

    private ServiceManager getServiceManager() {
        return ComponentAccessor.getServiceManager();
    }

    private boolean canEditService(final Long serviceId) throws Exception {
        return Iterables.any(getServiceManager().getServicesManageableBy(getLoggedInUser()), new Predicate<JiraServiceContainer>() {
            public boolean apply(JiraServiceContainer container) {
                return container != null && serviceId.equals(container.getId());
            }
        });
    }
}
