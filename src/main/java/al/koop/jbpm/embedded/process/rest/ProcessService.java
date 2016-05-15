package al.koop.jbpm.embedded.process.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.drools.persistence.info.WorkItemInfo;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.runtime.manager.context.EmptyContext;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;

@ApplicationScoped
@Path("/process")
public class ProcessService {
    private static final String BASE_URL = "/jbpm-embedded-ee7-1.0.0-SNAPSHOT/api/process";

    @Inject
    private EntityManager entityManager;

    @Inject
    @Singleton
    private RuntimeManager singletonManager;

    @GET
    @Path("/start")
    public String startProcess() {
        KieSession ksession = getKSession();

        // Start our custom process
        ProcessInstance processInstance = ksession.startProcess("customProcess", emptyMap());
        ksession.fireAllRules();

        return "Started customProcess. Has process-id: " + processInstance.getId() + "<br /><a href=\"" + BASE_URL + "\">back</a>";
    }

    @GET
    @Path("/complete/{id}")
    public String complete(@PathParam("id") Long workitemId) {
        getKSession().getWorkItemManager().completeWorkItem(workitemId, emptyMap());
        return "Completed workitem: " + workitemId + ". Process finished." + "<br /><a href=\"" + BASE_URL + "\">back</a>";
    }

    // Yes we should use a DOA/Repository through a Service here, for now use this.
    @GET
    @Transactional
    public String getOpenWorkitems() {
        String collect = entityManager.createQuery("from org.drools.persistence.info.WorkItemInfo", WorkItemInfo.class)
                .getResultList()
                .stream()
                .map(WorkItemInfo::getId)
                .map(i -> "<a href=\"" + BASE_URL + "/complete/" + i.toString() + "\">" + i.toString() + "</a>")
                .collect(joining(", "));
        return "This http interface to the process service is just for testing purposes.<br />"
                + "Start new process: <a href=\"" + BASE_URL + "/start\">start</a><br />"
                + "Open workitem-ids: " + collect;
    }

    private KieSession getKSession() {
        RuntimeEngine runtime = singletonManager.getRuntimeEngine(EmptyContext.get());
        return runtime.getKieSession();
    }
}
