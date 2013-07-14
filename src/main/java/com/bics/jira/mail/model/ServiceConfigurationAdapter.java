package com.bics.jira.mail.model;

import com.atlassian.jira.plugins.mail.ServiceConfiguration;
import com.atlassian.mail.MailException;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.bics.jira.mail.web.CreateOrCommentMailHandlerDetailsWebAction;
import webwork.action.ActionContext;

import java.util.Map;

/**
* Class-workaround for <a href="https://jira.atlassian.com/browse/JRA-30451">JRA-30451</a>
*
* @author Victor Polischuk
* @since 03.02.13 21:05
*/
public class ServiceConfigurationAdapter {
    private static Class<ServiceConfiguration> configurationClass;
    private static String id;

    private final PluginAccessor pluginAccessor;
    private final Object configuration;

    public ServiceConfigurationAdapter(PluginAccessor pluginAccessor) {
        loadClasses(pluginAccessor);

        this.pluginAccessor = pluginAccessor;
        this.configuration = ActionContext.getSession().get(id);

        String className = configuration == null ? "null" : configuration.getClass().getName();

        if (!className.equals("com.atlassian.jira.plugins.mail.ServiceConfiguration")) {
            throw new IllegalArgumentException("Incorrect parameter type. 'com.atlassian.jira.plugins.mail.ServiceConfiguration' expected, but was: " + className);
        }

    }

    public boolean isNull() {
        return configuration == null;
    }

    public Long getServiceId() {
        return invoke("getServiceId");
    }

    public String getServiceClass() {
        return invoke("getServiceClass");
    }

    public String getHandlerKey() {
        return invoke("getHandlerKey");
    }

    public long getDelay() {
        return invoke("getDelay");
    }

    public String getServiceName() {
        return invoke("getServiceName");
    }

    public void applyServiceId(Long serviceId) {
        try {
            configurationClass.getMethod("setServiceId", Long.class).invoke(configuration, serviceId);

            ActionContext.getSession().put(id, configuration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String[]> toServiceParams() throws MailException {
        try {
            return (Map<String, String[]>) configurationClass.getMethod("toServiceParams", PluginAccessor.class).invoke(configuration, pluginAccessor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T invoke(String getMethod) {
        try {
            return (T) configurationClass.getMethod(getMethod).invoke(configuration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadClasses(PluginAccessor pluginAccessor) {
        if (configurationClass != null && id != null) {
            return;
        }

        synchronized (ServiceConfigurationAdapter.class) {
            if (configurationClass != null && id != null) {
                return;
            }

            try {
                Plugin mailPlugin = pluginAccessor.getEnabledPlugin("com.atlassian.jira.jira-mail-plugin");

                if (mailPlugin != null) {
                    configurationClass = mailPlugin.loadClass("com.atlassian.jira.plugins.mail.ServiceConfiguration", CreateOrCommentMailHandlerDetailsWebAction.class);

                    id = (String) configurationClass.getField("ID").get(null);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
