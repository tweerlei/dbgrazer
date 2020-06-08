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

import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.exception.QueryException;
import de.tweerlei.dbgrazer.web.exception.QueryNotFoundException;
import de.tweerlei.dbgrazer.web.model.PathInfo;
import de.tweerlei.dbgrazer.web.service.FrontendExtensionService;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.SecurityService;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.RequestSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.spring.service.TimeService;
import de.tweerlei.spring.web.service.RequestHelperService;
import de.tweerlei.spring.web.service.WebappResourceService;
import de.tweerlei.spring.web.view.DownloadSource;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Interceptor for passing global variables to all views
 * 
 * @author Robert Wruck
 */
public class ModelInterceptor implements HandlerInterceptor, HandlerExceptionResolver
	{
	// View name for "401 authorization required"
	private static final String VIEW_BASICAUTH_FAILED = "ws/denied";
	// View name for "500 unknown connection name"
	private static final String VIEW_BASICAUTH_CONNECTION = "ws/connection";
	// View name for "500 internal server error"
	private static final String VIEW_BASICAUTH_ERROR = "ws/error";
	// View name for "500 internal server error"
	private static final String VIEW_BASICAUTH_CONFERROR = "ws/conferror";
	// View name for "500 internal server error"
	private static final String VIEW_BASICAUTH_TECHERROR = "ws/techerror";
	
	// View name for showing a login dialog via JavaScript
	private static final String VIEW_AJAX_LOGIN = "ajax/denied";
	// View name for redirecting to an unprivileged page via JavaScript
	private static final String VIEW_AJAX_REDIRECT = "ajax/reload";
	// View name for showing an error message via JavaScript
	private static final String VIEW_AJAX_ERROR = "ajax/error";
	// View name for showing an error message via JavaScript
	private static final String VIEW_AJAX_CONFERROR = "ajax/conferror";
	// View name for showing an error message via JavaScript
	private static final String VIEW_AJAX_TECHERROR = "ajax/techerror";
	
	// View name for showing the connection view
	private static final String VIEW_LOGIN = "connection";
	// View name for redirecting to an unprivileged page
	private static final String VIEW_REDIRECT = "redirect:index.html";
	// View name for showing an error message
	private static final String VIEW_ERROR = "error";
	// View name for showing an error message
	private static final String VIEW_CONFERROR = "conferror";
	// View name for showing an error message
	private static final String VIEW_TECHERROR = "techerror";
	
	private final TimeService timeService;
	private final LinkService linkService;
	private final FrontendHelperService frontendHelper;
	private final FrontendExtensionService extensionService;
	private final SecurityService securityService;
	private final UserSettingsManager userSettingsManager;
	private final QuerySettingsManager querySettingsManager;
	private final WebappResourceService webappResourceService;
	private final RequestHelperService requestHelperService;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	private final RequestSettings themeSettings;
	private final View downloadView;
	private final Logger logger;
	
	private List<String> excludedPaths;
	private List<String> ajaxPaths;
	private List<String> authPaths;
	
	/**
	 * Constructor
	 * @param timeService TimeService
	 * @param linkService LinkService
	 * @param frontendHelper FrontendHelperService
	 * @param extensionService FrontendExtensionService
	 * @param securityService SecurityService
	 * @param userSettingsManager UserSettingsManager
	 * @param querySettingsManager QuerySettingsManager
	 * @param webappResourceService WebappResourceService
	 * @param requestHelperService RequestHelperService
	 * @param downloadView GenericDownloadView
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 * @param themeSettings ThemeSettings
	 */
	@Autowired
	public ModelInterceptor(TimeService timeService, LinkService linkService,
			FrontendHelperService frontendHelper, WebappResourceService webappResourceService,
			UserSettingsManager userSettingsManager, QuerySettingsManager querySettingsManager,
			RequestHelperService requestHelperService, SecurityService securityService,
			FrontendExtensionService extensionService, GenericDownloadView downloadView,
			UserSettings userSettings, ConnectionSettings connectionSettings, RequestSettings themeSettings)
		{
		this.timeService = timeService;
		this.linkService = linkService;
		this.frontendHelper = frontendHelper;
		this.extensionService = extensionService;
		this.securityService = securityService;
		this.userSettingsManager = userSettingsManager;
		this.querySettingsManager = querySettingsManager;
		this.webappResourceService = webappResourceService;
		this.requestHelperService = requestHelperService;
		this.userSettings = userSettings;
		this.connectionSettings = connectionSettings;
		this.themeSettings = themeSettings;
		this.downloadView = downloadView;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Set the excludedPaths
	 * @param excludedPaths the excludedPaths to set
	 */
	public void setExcludedPaths(List<String> excludedPaths)
		{
		this.excludedPaths = excludedPaths;
		}
	
	/**
	 * Set the ajaxPaths
	 * @param ajaxPaths the ajaxPaths to set
	 */
	public void setAjaxPaths(List<String> ajaxPaths)
		{
		this.ajaxPaths = ajaxPaths;
		}
	
	/**
	 * Set the authPaths
	 * @param authPaths the authPaths to set
	 */
	public void setAuthPaths(List<String> authPaths)
		{
		this.authPaths = authPaths;
		}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
		{
		connectionSettings.resetLink();
		securityService.cleanupConnection();
		
		final PathInfo pi = getRequestPath(request);
		if (pi.getCategory() != null)
			{
			// Page requires a connection
			
			if (isBasicAuth(pi.getViewName()))
				{
				final User user = authenticate(request);
				if (user == null)
					{
					// HTTP 401 Authorization required
					throw new ModelAndViewDefiningException(new ModelAndView(VIEW_BASICAUTH_FAILED));
					}
				
				final LinkDef def = getLink(pi.getSubcategory(), user);
				if (def == null)
					{
					// HTTP 500 Unknown link name
					throw new ModelAndViewDefiningException(new ModelAndView(VIEW_BASICAUTH_CONNECTION));
					}
				
				// will be reset in afterCompletion
				connectionSettings.setLink(def, null);
				securityService.initializeConnection(user);
				}
			else
				{
				final LinkDef def = getLink(pi.getSubcategory(), userSettings.getPrincipal());
				if (def == null)
					{
					if (isAjax(pi.getViewName()))
						{
						// Send back a JavaScript that prompts the user to log in
						throw new ModelAndViewDefiningException(new ModelAndView(VIEW_AJAX_LOGIN));
						}
					else
						{
						saveSourceURL(request, pi);
						
						// Show the "connection" view
						final ModelAndView modelAndView = new ModelAndView(VIEW_LOGIN);
						addAttributes(modelAndView);
						throw new ModelAndViewDefiningException(modelAndView);
						}
					}
				
				if (!isAjax(pi.getViewName()))
					saveSourceURL(request, pi);
				
				// will be reset in afterCompletion
				connectionSettings.setLink(def, querySettingsManager.getSchemaSettings(def.getSchema().getName()));
				securityService.initializeConnection(userSettings.getPrincipal());
				}
			}
		else
			{
			// Page requires no connection
			if (!isAjax(pi.getViewName()))
				saveSourceURL(request, pi);
			}
		
		return (true);
		}
	
	@Override
	public void postHandle(HttpServletRequest req, HttpServletResponse resp, Object handler, ModelAndView modelAndView) throws Exception
		{
		if (modelAndView != null)
			{
			if (((modelAndView.getView() != null) && (modelAndView.getView() instanceof RedirectView)) ||
				((modelAndView.getViewName() != null) && modelAndView.getViewName().startsWith(UrlBasedViewResolver.REDIRECT_URL_PREFIX)))
				{
				// Don't expose model attributes in redirect URLs
				modelAndView.getModel().clear();
				}
			else
				{
				// Use GenericDownloadView if a DownloadSource was returned
				if (modelAndView.getModel().get(GenericDownloadView.SOURCE_ATTRIBUTE) instanceof DownloadSource)
					modelAndView.setView(downloadView);
				else
					addAttributes(modelAndView);
				}
			}
		}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
		{
		// Clear the connection context
		connectionSettings.resetLink();
		securityService.cleanupConnection();
		}
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
		{
		final PathInfo pi = getRequestPath(request);
		
		if (ex instanceof AccessDeniedException)
			{
			if (isBasicAuth(pi.getViewName()))
				return (new ModelAndView(VIEW_BASICAUTH_CONNECTION));
			else if (userSettings.getPrincipal() == null)
				{
				// Not logged in: Prompt for login
				if (isAjax(pi.getViewName()))
					return (new ModelAndView(VIEW_AJAX_LOGIN));
				else
					return (new ModelAndView(VIEW_LOGIN));
				}
			else
				{
				// Logged in but not authorized: Redirect to an allowed page
				if (isAjax(pi.getViewName()))
					return (new ModelAndView(VIEW_AJAX_REDIRECT));
				else
					return (new ModelAndView(VIEW_REDIRECT));
				}
			}
		else if (ex instanceof QueryNotFoundException)
			{
			final QueryNotFoundException qnfe = (QueryNotFoundException) ex;
			
			if (isBasicAuth(pi.getViewName()))
				return (new ModelAndView(VIEW_BASICAUTH_CONFERROR, qnfe.getModel()));
			else if (isAjax(pi.getViewName()))
				return (new ModelAndView(VIEW_AJAX_CONFERROR, qnfe.getModel()));
			else
				return (new ModelAndView(VIEW_CONFERROR, qnfe.getModel()));
			}
		else if (ex instanceof QueryException)
			{
			logger.log(Level.WARNING, "resolveException", ex);
			
			final QueryException qnfe = (QueryException) ex;
			
			if (isBasicAuth(pi.getViewName()))
				return (new ModelAndView(VIEW_BASICAUTH_TECHERROR, qnfe.getModel()));
			else if (isAjax(pi.getViewName()))
				return (new ModelAndView(VIEW_AJAX_TECHERROR, qnfe.getModel()));
			else
				return (new ModelAndView(VIEW_TECHERROR, qnfe.getModel()));
			}
		else
			{
			logger.log(Level.WARNING, "resolveException", ex);
			
			if (isBasicAuth(pi.getViewName()))
				return (new ModelAndView(VIEW_BASICAUTH_ERROR, "exception", ex));
			else if (isAjax(pi.getViewName()))
				return (new ModelAndView(VIEW_AJAX_ERROR, "exception", ex));
			else
				return (new ModelAndView(VIEW_ERROR, "exception", ex));
			}
		}
	
	private PathInfo getRequestPath(HttpServletRequest request)
		{
		return (frontendHelper.parsePath(request.getServletPath(), request.getQueryString()));
		}
	
	private PathInfo getReferrerPath(HttpServletRequest request)
		{
		final URI ref = requestHelperService.getReferrerURI(request);
		if (ref != null)
			{
			final URI base = requestHelperService.getRequestURI(request);
			final URI rel = base.relativize(ref);
			if (!rel.isAbsolute())
				return (frontendHelper.parsePath(rel.getRawPath(), rel.getRawQuery()));
			}
		
		return (null);
		}
	
	private void saveSourceURL(HttpServletRequest request, PathInfo pi)
		{
		final PathInfo pi2 = getReferrerPath(request);
		if ((pi2 != null) && (pi2.getCategory() != null) && !isExcluded(pi2.getViewName()))
			{
			// Save the referring page as redirect target after login or connection switch
			connectionSettings.setSourceURL(pi2);
			userSettings.setSourceURL(pi2);
			}
		
		if (request.getMethod().equals("GET") && !isExcluded(pi.getViewName()))
			{
			// Save the last visited page as redirect target after login
			userSettings.setSourceURL(pi);
			}
		}
	
	private User authenticate(HttpServletRequest request)
		{
		final String[] auth = requestHelperService.getBasicAuthentication(request);
		if (auth == null)
			return (null);
		
		return (securityService.login(auth[0], auth[1]));
		}
	
	private LinkDef getLink(String name, User user)
		{
		if ((user == null) && userSettingsManager.isLoginRequired())
			return (null);
		
		return (linkService.getLink(name, userSettingsManager.getEffectiveUserGroups(user)));
		}
	
	private void addAttributes(ModelAndView modelAndView)
		{
		modelAndView.addObject("appRelease", webappResourceService.getManifestParser().getVersion());
		modelAndView.addObject("appVersion", webappResourceService.getManifestParser().getRevision());
		modelAndView.addObject("appDate", webappResourceService.getManifestParser().getDate());
		
		modelAndView.addObject("baseURI", userSettingsManager.getBaseURI());
		modelAndView.addObject("loginEnabled", userSettingsManager.isLoginEnabled());
		modelAndView.addObject("loginRequired", userSettingsManager.isLoginRequired());
		modelAndView.addObject("historyEnabled", querySettingsManager.isHistoryEnabled());
		modelAndView.addObject("menuRatio", userSettingsManager.getMenuRatio());
		modelAndView.addObject("menuExtensions", extensionService.getTopMenuExtensions());
		
		modelAndView.addObject("currentDate", timeService.getCurrentDate());
		
		modelAndView.addObject("currentRequest", themeSettings);
		modelAndView.addObject("currentUser", userSettings);
		modelAndView.addObject("currentConnection", connectionSettings);
		}
	
	private boolean isExcluded(String path)
		{
		if (excludedPaths == null)
			return (false);
		
		for (String s : excludedPaths)
			{
			if (path.startsWith(s))
				return (true);
			}
		
		return (false);
		}
	
	private boolean isAjax(String path)
		{
		if (ajaxPaths == null)
			return (false);
		
		for (String s : ajaxPaths)
			{
			if (path.startsWith(s))
				return (true);
			}
		
		return (false);
		}
	
	private boolean isBasicAuth(String path)
		{
		if (authPaths == null)
			return (false);
		
		for (String s : authPaths)
			{
			if (path.startsWith(s))
				return (true);
			}
		
		return (false);
		}
	}
