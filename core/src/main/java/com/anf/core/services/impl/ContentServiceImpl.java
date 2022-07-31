package com.anf.core.services.impl;

import com.anf.core.models.User;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.jcr.JsonItemWriter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.anf.core.services.ContentService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = ContentService.class)
public class ContentServiceImpl implements ContentService {
	
	private static Logger logger = LoggerFactory.getLogger(ContentServiceImpl.class);
	
	@Reference
	ResourceResolverFactory resourceResolverFactory;
	
    @Override
    public boolean commitUserDetails(User user) {
    	
    	
    	try {
			ResourceResolver resourceResolver = getResourceResolver();
			final Resource parent = ResourceUtil.getOrCreateResource(resourceResolver, "/var/anf-code-challenge",
				      Collections.singletonMap("jcr:primaryType", (Object) "sling:OrderedFolder"), null, false);
			Map<String, Object> props = new HashMap<>();
			props.put("jcr:primaryType", (Object) "nt:unstructured");
			props.put("firstname", user.getFname());
			props.put("lastname", user.getLname());
			props.put("age", user.getAge());
			props.put("country", user.getCountry());
			resourceResolver.create(parent, new Date().getTime()+"",
				      props);
			resourceResolver.commit();
			resourceResolver.close();
			return true;
		} catch (LoginException | PersistenceException e) {
			logger.error(e.getMessage());
		}
    	return false;
    }
    
    @Override
    public String getUserAgeRange() {
    	
    	ResourceResolver resourceResolver;
		try {
			resourceResolver = getResourceResolver();
			Resource ageResource = resourceResolver.getResource("/etc/age");
			String maxAge = ageResource.getValueMap().get("maxAge", "");
			String minAge = ageResource.getValueMap().get("minAge", "");
			
			JsonObject ageJsonObj = new JsonObject();
			ageJsonObj.addProperty("maxAge", maxAge);
			ageJsonObj.addProperty("minAge", minAge);

			Gson gson = new Gson();
			String ageJsonString = gson.toJson(ageJsonObj);
			return ageJsonString;
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		
		return null;
    	
    }
    
    private ResourceResolver getResourceResolver() throws LoginException {
    	return resourceResolverFactory.getServiceResourceResolver(Collections.singletonMap(
                ResourceResolverFactory.SUBSERVICE,(Object) "subSer"));
    }

    @Override
	public String getNewfeedsDump() {
    	ResourceResolver resourceResolver;
		try {
			resourceResolver = getResourceResolver();
			Resource newsfeedResource = resourceResolver.getResource("/var/commerce/products/anf-code-challenge/newsData");
			Node node = newsfeedResource.adaptTo(Node.class);
			StringWriter stringWriter = new StringWriter();
			JsonItemWriter jsonWriter = new JsonItemWriter(null);
			jsonWriter.dump(node, stringWriter, -1, true);
			
			
			return stringWriter.toString();
			
		} catch (LoginException | RepositoryException | JSONException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		
		return null;
	}
}
