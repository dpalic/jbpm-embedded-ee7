import static java.util.Collections.emptyMap;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.runtime.manager.context.EmptyContext;

@Startup
@Singleton
public class AniStartFlowEJB {

	private volatile boolean processStarted = false;

	@Inject
	private EntityManager entityManager;

	@Inject
	@Singleton
	private RuntimeManager singletonManager;

	@PostConstruct
	public void startProcess() {
		ProcessInstance processInstance = null;
		if (!processStarted) {

			RuntimeEngine runtime = singletonManager.getRuntimeEngine(EmptyContext.get());

			KieSession ksession = runtime.getKieSession();

			// Start our custom process
			processInstance = ksession.startProcess("customProcess", emptyMap());
			ksession.fireAllRules();

			System.out.println("Started customProcess. Has process-id: " + processInstance.getId());
			
			processStarted = true;
		} else {
			System.err.println("process " + processInstance.getId() + " was already started skipping it");
		}
	}

}
