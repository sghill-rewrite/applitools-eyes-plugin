package com.applitools.jenkins;

import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Job;

import java.io.Serializable;

/**
 * Encapsulates Applitools plugin configuration.
 */
public class ApplitoolsProjectConfigProperty extends JobProperty<AbstractProject<?, ?>> implements Serializable{
    private String serverURL;

    public ApplitoolsProjectConfigProperty(String serverURL) {
        this.serverURL = serverURL;
    }

    public String getServerURL()
    {
        return this.serverURL;
    }

    public void setServerURL(String serverURL)
    {
        this.serverURL = serverURL;
    }

    @Override
    public JobPropertyDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    /* even though we are not displaying it, it should be there*/
    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    public static class DescriptorImpl extends JobPropertyDescriptor {

        public DescriptorImpl() {
            super(ApplitoolsProjectConfigProperty.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "Set Applitools URL";
        }

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return AbstractProject.class.isAssignableFrom(jobType);
        }
    }

}
