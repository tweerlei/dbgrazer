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
package de.tweerlei.spring.web.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * HandlerAdapter that handles ModelAndViewDefiningExceptions thrown from the handler
 * so that any registered interceptors are called.
 * 
 * If an errorView is configured, any other exceptions are caught and redirected to that view.
 * Additionally, if an exceptionAttribute is configured, the caught exception is exposed as
 * a model attribute in the errorView.
 * Since this is considered handler-level error handling, we don't allow setting an HTTP error code. 
 * 
 * @author Robert Wruck
 */
public class ExceptionResolvingHandlerAdapter extends AnnotationMethodHandlerAdapter
	{
	private final Logger logger;
	private String errorView = null;
	private String exceptionAttribute = null;
	private HandlerExceptionResolver exceptionResolver = null; 
	
	/**
	 * Constructor
	 */
	public ExceptionResolvingHandlerAdapter()
		{
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Set the errorView
	 * @param errorView the errorView to set
	 */
	public void setErrorView(String errorView)
		{
		this.errorView = errorView;
		}

	/**
	 * Set the exceptionAttribute
	 * @param exceptionAttribute the exceptionAttribute to set
	 */
	public void setExceptionAttribute(String exceptionAttribute)
		{
		this.exceptionAttribute = exceptionAttribute;
		}

	/**
	 * Set the exceptionResolver
	 * @param exceptionResolver the exceptionResolver to set
	 */
	public void setExceptionResolver(HandlerExceptionResolver exceptionResolver)
		{
		this.exceptionResolver = exceptionResolver;
		}

	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
		{
		try	{
			return super.handle(request, response, handler);
			}
		catch (ModelAndViewDefiningException e)
			{
			return e.getModelAndView();
			}
		catch (Exception e)
			{
			if (exceptionResolver != null)
				{
				final ModelAndView mv = exceptionResolver.resolveException(request, response, handler, e);
				if (mv != null)
					{
					// Exception completely resolved
					return (mv);
					}
				}
			
			if (errorView == null)
				{
				// Pass exception to DispatcherServlet
				throw e;
				}
			
			logger.log(Level.WARNING, "Caught exception from handler [" + handler + "]", e);
			
			final ModelAndView mv = new ModelAndView(errorView);
			if (exceptionAttribute != null)
				mv.addObject(exceptionAttribute, e);
			return (mv);
			}
		}
	}
