package com.anf.core.servlets;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_ID + "=" + "Dynamic DataSource Servlet",
                Constants.SERVICE_DESCRIPTION + "=" + "Dynamic DataSource Servlet for dropdown",
                SLING_SERVLET_RESOURCE_TYPES + "=" + "/apps/anfcodechallenge/dropdowns"
        })
public class DynamicDataSourceServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        try {
            ResourceResolver resolver = request.getResourceResolver();
            Resource currentResource = request.getResource();
            // set fallback
            request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());

            //Get json path through datasource options
            String jsonStr = null;
            Resource datasource = currentResource.getChild("datasource");
            String resourcePath = datasource.getValueMap().get("resourcePath", String.class);
            Resource jsonResource = resolver.getResource(resourcePath+"/jcr:content/renditions/original");
            jsonStr = getJsonFromFile(jsonResource);//Json to String
            JSONObject jsonObj = new JSONObject(jsonStr);
            List<Option> optionList = new ArrayList<Option>();
            jsonObj.keys().forEachRemaining(key -> {
				try {
					optionList.add(new Option(jsonObj.getString((String) key),(String) key));
				} catch (JSONException e) {
					logger.error("Error while parsing json", e);
				}
			});

           
            List<Resource> optionResourceList = new ArrayList<Resource>();

            Iterator<Option> oi = optionList.iterator();
            while (oi.hasNext()) {
                Option opt = oi.next();
                ValueMap vm = getOptionValueMap(opt);//Option to ValueMap
                optionResourceList
                        .add(new ValueMapResource(resolver, new ResourceMetadata(), "JcrConstants.NT_UNSTRUCTURED", vm));
            }

            DataSource ds = new SimpleDataSource(optionResourceList.iterator());
            request.setAttribute(DataSource.class.getName(), ds);

        } catch (IOException io) {
            logger.info("Error fetching JSON data ");
            io.printStackTrace();
        } catch (Exception e) {
            logger.info("Error in Getting Drop Down Values ");
            e.printStackTrace();
        }
    }

    private ValueMap getOptionValueMap(Option opt) {
        ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());

        vm.put("value", opt.getValue());
        vm.put("text", opt.getText());
    
        return vm;
    }

    private String getJsonFromFile(Resource jsonResource)
            throws RepositoryException, ValueFormatException, PathNotFoundException, IOException {
        String json = null;
        if (!ResourceUtil.isNonExistingResource(jsonResource)) {
            Node cfNode = jsonResource.adaptTo(Node.class).getNode("jcr:content");
            InputStream in = cfNode.getProperty("jcr:data").getBinary().getStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            json = sb.toString();
            reader.close();

        }
        return json;
    }

    private class Option {
    	
    	private Option(final String value , final String text) {
			this.text = text;
			this.value = value;
		}
        String text;
        String value;
       

        public String getText() {
            return text;
        }

        public String getValue() {
            return value;
        }

    }

}