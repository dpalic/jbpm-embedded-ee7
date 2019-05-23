package com.xenovation.jbpmjee7.embedded.process.handler;

import static java.util.Collections.emptyMap;
import static org.slf4j.LoggerFactory.getLogger;

import javax.enterprise.context.ApplicationScoped;

import org.drools.core.process.instance.WorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;

/**
 * a workitem handler.
 * 
 * @author Darko Palic
 */
@ApplicationScoped
public class SomeImmediateServiceWorkItemHandler implements WorkItemHandler {
    private static final Logger LOG = getLogger(SomeImmediateServiceWorkItemHandler.class);

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        LOG.info("Whoot the immediate service is called, lets finish it!");
        manager.completeWorkItem(workItem.getId(), emptyMap());
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    }
}
