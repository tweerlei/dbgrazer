/*
 * Copyright 2018 tweerlei Wruck + Buchmeier GbR - http://www.tweerlei.de/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tweerlei.dbgrazer.web.support;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import de.tweerlei.dbgrazer.security.event.SessionCreationEvent;
import de.tweerlei.dbgrazer.security.event.SessionDestructionEvent;

/**
 * Listen for session creation and destruction, posting events to the web application context.
 * Note that the events will propagate to the parent application context as well.
 * Copied from Spring's HttpSessionEventPublisher.
 * 
 * @author Robert Wruck
 */
public class SessionListener implements HttpSessionListener
	{
	@Override
	public void sessionCreated(HttpSessionEvent e)
		{
		getContext(e.getSession().getServletContext()).publishEvent(new SessionCreationEvent(e.getSession().getId()));
		}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent e)
		{
		getContext(e.getSession().getServletContext()).publishEvent(new SessionDestructionEvent(e.getSession().getId()));
		}
	
	private ApplicationContext getContext(ServletContext servletContext)
		{
		return (WebApplicationContextUtils.getWebApplicationContext(servletContext));
		}
	}
