/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.anf.core.servlets;

import com.anf.core.models.User;
import com.anf.core.services.ContentService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = { Servlet.class }, property = {
		ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
		ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
		ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/saveUserDetails",
		//ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/newfeedsDump",
		ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/getUserAgeRange"
		
		}
		
		)

public class UserServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private ContentService contentService;

    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
        // Make use of ContentService to write the business logic
    	String path = req.getPathInfo();
    	if (path.contains("/bin/getUserAgeRange")) {
    		resp.getWriter().write(contentService.getUserAgeRange());
    	}
//    	else
//    	if (path.contains("/bin/newfeedsDump")) {
//    		resp.getWriter().write(contentService.getNewfeedsDump());
//    	}
    }
    
    @Override
    protected void doPost(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
    	String path = req.getPathInfo();
    	if (path.contains("/bin/saveUserDetails")) {
	    	User user = new User();
	    	user.setFname(req.getParameter("fname"));
	    	user.setLname(req.getParameter("lname"));
	    	user.setAge(req.getParameter("age"));
	    	user.setCountry(req.getParameter("country"));
	    	resp.setContentType("text/plain");
	    	if(contentService.commitUserDetails(user)) {
	    		
	    		resp.getWriter().write("saved");
	    	}
    	}
    	//TODO handle exception, return http 500 and error message back
    }
}
