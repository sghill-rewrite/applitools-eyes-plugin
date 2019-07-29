package com.applitools.jenkins;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;

@Extension(optional = true)
public class ApplitoolsJobDsl extends ContextExtensionPoint {
    @DslExtensionMethod(context = WrapperContext.class)
    public Object applitools(String serverUrl) {
        return new ApplitoolsBuildWrapper(serverUrl);
    }

    @DslExtensionMethod(context = WrapperContext.class)
    public Object applitools() {
        return new ApplitoolsBuildWrapper(ApplitoolsCommon.APPLITOOLS_DEFAULT_URL);
    }

}
