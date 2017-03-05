package com.xenovation.jbpmjee7.embedded.process.factory;

import static org.jbpm.services.cdi.impl.manager.InjectableRegisterableItemsFactory.getFactory;
import static org.kie.api.io.ResourceType.BPMN2;
import static org.kie.internal.io.ResourceFactory.newClassPathResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.cdi.Kjar;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.manager.WorkItemHandlerProducer;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;

import com.xenovation.jbpmjee7.embedded.process.handler.NotSoImmediateServiceWorkItemHandler;
import com.xenovation.jbpmjee7.embedded.process.handler.SomeImmediateServiceWorkItemHandler;

/**
 * This class is responsible to initialize the jBPM for usage in a JEE7  * environment.
 * 
 * @author Darko Palic
 */
public class JBPMEnvironmentFactory {

	/** the SPI bean manager for accessing SPI services. */
	@Inject
	private BeanManager beanManager;

	/** the jBPM deployment service for new processes. */
	@Inject
	@Kjar
	private DeploymentService deploymentService;

	/** the JPA persistency unit entitymanager. */
	@PersistenceUnit(unitName = "org.jbpm.domain")
	private EntityManagerFactory emf;

	/**
	 * CDI Producer reference for the entitymanager factory.
	 * 
	 * @return a initialized entity manager
	 */
	@Produces
	public EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}

	/**
	 * CDI Producer reference for the {@link EntityManager}
	 * 
	 * @return a initialized {@link EntityManager}
	 */
	@Produces
	@RequestScoped
	public EntityManager getEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}

	/**
	 * CDI Producer reference for the jBPM {@link DeploymentService}.
	 * 
	 * @return a initialized jBPM deployment service
	 */
	@Produces
	@Default
	public DeploymentService deploymentService() {
		return deploymentService;
	}

	/**
	 * CDI Producer reference for the jBPM {@link IdentityProvider}.
	 * 
	 * @return a initialized jBPM identity provider
	 */
	@Produces
	public IdentityProvider produceIdentityProvider() {
		return new IdentityProvider() {
			@Override
			public String getName() {
				return null;
			}

			@Override
			public List<String> getRoles() {
				return null;
			}

			@Override
			public boolean hasRole(String role) {
				return false;
			}
		};
	}

	/**
	 * CDI Producer reference for the jBPM {@link RuntimeEnvironment}.
	 * 
	 * @return a initialized jBPM {@link RuntimeEnvironment}r
	 */
	@Produces
	@Singleton
	@PerRequest
	@PerProcessInstance
	public RuntimeEnvironment produceEnvironment() {
		return RuntimeEnvironmentBuilder.getDefault().entityManagerFactory(getEntityManagerFactory())
				.registerableItemsFactory(getFactory(beanManager, (AuditEventBuilder) null))
				.addAsset(newClassPathResource("process.bpmn"), BPMN2).get();
	}

	@Produces
	public UserGroupCallback produceSelectedUserGroupCalback() {
		return new JBossUserGroupCallbackImpl("classpath:/usergroup.properties");
	}

	@Produces
	private WorkItemHandlerProducer handlers(NotSoImmediateServiceWorkItemHandler notSoImmediateServiceWorkItemHandler,
			SomeImmediateServiceWorkItemHandler someImmediateServiceWorkItemHandler) {
		Map<String, WorkItemHandler> handlers = new HashMap<>();
		handlers.put("NotSoImmediateService", notSoImmediateServiceWorkItemHandler);
		handlers.put("SomeImmediateService", someImmediateServiceWorkItemHandler);

		return (identifier, params) -> handlers;
	}
}
