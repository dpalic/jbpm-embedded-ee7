package al.koop.jbpm.embedded.process.handler;

import al.koop.jbpm.embedded.service.SomeImmediateService;
import org.drools.core.process.instance.WorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static java.util.Collections.emptyMap;
import static org.slf4j.LoggerFactory.getLogger;

@ApplicationScoped
public class SomeImmediateServiceWorkItemHandler implements WorkItemHandler {
    private static final Logger LOG = getLogger(SomeImmediateServiceWorkItemHandler.class);

    @Inject
    private SomeImmediateService someImmediateService;

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        LOG.info("Whoot the immediate service is called, lets finish it!");
        manager.completeWorkItem(workItem.getId(), emptyMap());
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    }
}
