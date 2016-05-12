package com.alphabet.prototype.process.factory;

import com.alphabet.prototype.process.handler.NotSoImmediateServiceWorkItemHandler;
import com.alphabet.prototype.process.handler.SomeImmediateServiceWorkItemHandler;
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

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jbpm.services.cdi.impl.manager.InjectableRegisterableItemsFactory.getFactory;
import static org.kie.api.io.ResourceType.BPMN2;
import static org.kie.internal.io.ResourceFactory.newClassPathResource;

public class JBPMEnvironmentFactory {

    @Inject
    private BeanManager beanManager;

    @Inject
    @Kjar
    private DeploymentService deploymentService;

    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;

    @Produces
    @Default
    public DeploymentService deploymentService() {
        return deploymentService;
    }

    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    @Produces
    public IdentityProvider produceIdentityProvider () {
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

    @Produces
    @RequestScoped
    public EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public void closeEM(@Disposes EntityManager em) {
        em.close();
    }

    @Produces
    @Singleton
    @PerRequest
    @PerProcessInstance
    public RuntimeEnvironment produceEnvironment() {
        return RuntimeEnvironmentBuilder.getDefault()
                .entityManagerFactory(getEntityManagerFactory())
                .registerableItemsFactory(getFactory(beanManager, (AuditEventBuilder) null))
                .addAsset(newClassPathResource("process.bpmn"), BPMN2)
                .get();
    }

    @Produces
    public UserGroupCallback produceSelectedUserGroupCalback() {
        return new JBossUserGroupCallbackImpl("classpath:/usergroup.properties");
    }

    @Produces
    private WorkItemHandlerProducer handlers(NotSoImmediateServiceWorkItemHandler notSoImmediateServiceWorkItemHandler, SomeImmediateServiceWorkItemHandler someImmediateServiceWorkItemHandler) {
        Map<String, WorkItemHandler> handlers = new HashMap<>();
        handlers.put("NotSoImmediateService", notSoImmediateServiceWorkItemHandler);
        handlers.put("SomeImmediateService", someImmediateServiceWorkItemHandler);

        return (identifier, params) -> handlers;
    }
}
