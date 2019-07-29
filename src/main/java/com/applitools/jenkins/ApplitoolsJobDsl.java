package com.applitools.jenkins;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;

@Extension(optional = true)
public class ApplitoolsJobDsl extends ContextExtensionPoint {
    @DslExtensionMethod(context = WrapperContext.class)
    public Object Applitools(String serverUrl) {
        return new ApplitoolsBuildWrapper(serverUrl);
    }

    @DslExtensionMethod(context = WrapperContext.class)
    public Object Applitools() {
        return new ApplitoolsBuildWrapper(ApplitoolsCommon.APPLITOOLS_DEFAULT_URL);
    }

}
