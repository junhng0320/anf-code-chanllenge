package com.anf.core.listeners;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import javax.jcr.Session;
import javax.jcr.Workspace;

import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.apache.jackrabbit.api.observation.JackrabbitEventFilter;
import org.apache.jackrabbit.api.observation.JackrabbitObservationManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;


import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component(service = EventListener.class, immediate = true)
public class PageCreationListener implements EventListener {


private static final Logger logger = LoggerFactory.getLogger(PageCreationListener.class);
   
@Reference
private ResourceResolverFactory resolverFactory;

/**
 * Resource Resolver
 */
private ResourceResolver resolver;

@Reference
private SlingRepository repository;


private Session session;


@Activate
protected void activate(ComponentContext componentContext) {
	
	logger.info("Activating the observation");
	
	try {
		
		resolver = getResourceResolver();
		
		session = resolver.adaptTo(Session.class);
		
		String[] nodeTypes = {"cq:Page"};
		
		JackrabbitEventFilter jackrabbitEventFilter = new JackrabbitEventFilter()
				.setAbsPath( "/content/anf-code-challenge/us/en")
				.setNodeTypes(nodeTypes)				
				.setEventTypes(Event.NODE_ADDED)
				.setIsDeep(true)
				.setNoExternal(true)
				.setNoLocal(false);
		Workspace workSpace = session.getWorkspace();
		
		if (null != workSpace) {
			JackrabbitObservationManager observationManager = (JackrabbitObservationManager) workSpace.getObservationManager();
			observationManager.addEventListener(this, jackrabbitEventFilter);
			logger.info("The Page Event Listener is Registered at {} for the event type {}.", "/content/anf-code-challenge/us/en",
				Event.NODE_ADDED);
		}
	
		
	} catch (Exception e) {		
		logger.error(e.getMessage(), e);
	}
}

@Deactivate
protected void deactivate() {
	
	if(session != null) {
		
		session.logout();
	}
}

@Override
public void onEvent(EventIterator events) {

	try {
		
		while(events.hasNext()) {
			
			logger.info("Something has been added: {} ", events.nextEvent().getPath() );
	
			addPageCreatedProp(events.nextEvent().getPath());
		}
	} catch (Exception e) {
		
		logger.error(e.getMessage());
	}
}

private void addPageCreatedProp(String pagePath) {
    try (ResourceResolver resolver = getResourceResolver()) {
    	Resource resource = resolver.getResource(pagePath);
    	ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
    	properties.put("pageCreated", true);
//       java.util.Optional.of(resolver)
//          .map(r -> resolver.getResource(pagePath))
//          .map(res -> resolver.adaptTo(ModifiableValueMap.class))
//          .map(m -> m.put("pageCreated", true));
       resolver.commit();
       logger.info("CREATE ID FOR page {}", pagePath);
    } catch (PersistenceException e) {
    	logger.error("Can't save pageId on page {}", pagePath);
    } catch (LoginException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
 }

private ResourceResolver getResourceResolver() throws LoginException {
	return resolverFactory.getServiceResourceResolver(Collections.singletonMap(
            ResourceResolverFactory.SUBSERVICE,(Object) "subSer"));
}

}