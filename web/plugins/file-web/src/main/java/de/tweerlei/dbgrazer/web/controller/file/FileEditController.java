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
package de.tweerlei.dbgrazer.web.controller.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import de.tweerlei.common.io.CopyStreamWriter;
import de.tweerlei.dbgrazer.extension.file.RemoteResourceService;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class FileEditController
	{
	private final RemoteResourceService remoteResourceService;
	private final FrontendHelperService frontendHelperService;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param remoteResourceService RemoteResourceService
	 * @param frontendHelperService FrontendHelperService
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public FileEditController(RemoteResourceService remoteResourceService, FrontendHelperService frontendHelperService,
			UserSettings userSettings, ConnectionSettings connectionSettings)
		{
		this.remoteResourceService = remoteResourceService;
		this.frontendHelperService = frontendHelperService;
		this.userSettings = userSettings;
		this.connectionSettings = connectionSettings;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Show the file browser
	 * @param path Path
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/file.html", method = RequestMethod.GET)
	public Map<String, Object> getFile(@RequestParam("path") String path)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new RemoteResourceDownloadSource(remoteResourceService, connectionSettings.getLinkName(), path));
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param path Path
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/file-upload.html", method = RequestMethod.GET)
	public Map<String, Object> showUploadDialog(@RequestParam("path") String path)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("path", path);
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param path Path
	 * @param file Uploaded file
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/file-upload.html", method = RequestMethod.POST)
	public Map<String, Object> uploadFile(@RequestParam("path") String path, @RequestParam("file") MultipartFile file)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		try	{
			final InputStream is = file.getInputStream();
			final String rsrc = frontendHelperService.filename(path, frontendHelperService.basename(file.getOriginalFilename().replace('\\', '/')));
			try	{
				remoteResourceService.createResource(connectionSettings.getLinkName(), rsrc, new CopyStreamWriter(is), userSettings.getPrincipal().getLogin());
				}
			finally
				{
				is.close();
				}
			
			model.put("result", frontendHelperService.toJSONString(rsrc));
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "uploadFile", e);
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param path Path
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/file-rename.html", method = RequestMethod.GET)
	public Map<String, Object> showRenameDialog(@RequestParam("path") String path)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("dirname", frontendHelperService.dirname(path));
		model.put("basename", frontendHelperService.basename(path));
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param dirname Path
	 * @param basename Old name
	 * @param newname New name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/file-rename.html", method = RequestMethod.POST)
	public Map<String, Object> renameFile(@RequestParam("dirname") String dirname, @RequestParam("basename") String basename, @RequestParam("newname") String newname)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		try	{
			final String path = frontendHelperService.filename(dirname, basename);
			final String rsrc = frontendHelperService.filename(dirname, newname);
			remoteResourceService.renameResource(connectionSettings.getLinkName(), path, rsrc, userSettings.getPrincipal().getLogin());
			
			model.put("result", frontendHelperService.toJSONString(rsrc));
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "renameFile", e);
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param path Path
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/file-remove.html", method = RequestMethod.GET)
	public Map<String, Object> showRemoveDialog(@RequestParam("path") String path)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("path", path);
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param path Path
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/file-remove.html", method = RequestMethod.POST)
	public Map<String, Object> removeFile(@RequestParam("path") String path)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		try	{
			remoteResourceService.removeResource(connectionSettings.getLinkName(), path, userSettings.getPrincipal().getLogin());
			
			model.put("result", frontendHelperService.toJSONString(path));
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "removeFile", e);
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param path Path
	 * @param left Tree node to reload after change
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/file-mkdir.html", method = RequestMethod.GET)
	public Map<String, Object> showMkdirDialog(@RequestParam("path") String path, @RequestParam("left") String left)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("left", trimLeft(left));
		model.put("dirname", path);
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param dirname Path
	 * @param newname New name
	 * @param left Tree node to reload after change
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/file-mkdir.html", method = RequestMethod.POST)
	public Map<String, Object> createDir(@RequestParam("dirname") String dirname, @RequestParam("newname") String newname, @RequestParam("left") String left)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("left", trimLeft(left));
		
		try	{
			final String path = frontendHelperService.filename(dirname, newname);
			remoteResourceService.createPath(connectionSettings.getLinkName(), path, userSettings.getPrincipal().getLogin());
			
			model.put("result", frontendHelperService.toJSONString(path));
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "createDir", e);
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param path Path
	 * @param left Tree node to reload after change
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/file-rendir.html", method = RequestMethod.GET)
	public Map<String, Object> showRendirDialog(@RequestParam("path") String path, @RequestParam("left") String left)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("left", trimLeft(left));
		model.put("dirname", frontendHelperService.dirname(path));
		model.put("basename", frontendHelperService.basename(path));
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param dirname Path
	 * @param basename Old name
	 * @param newname New name
	 * @param left Tree node to reload after change
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/file-rendir.html", method = RequestMethod.POST)
	public Map<String, Object> renameDir(@RequestParam("dirname") String dirname, @RequestParam("basename") String basename, @RequestParam("newname") String newname, @RequestParam("left") String left)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("left", trimLeft(left));
		
		try	{
			final String path = frontendHelperService.filename(dirname, basename);
			final String rsrc = frontendHelperService.filename(dirname, newname);
			remoteResourceService.renameResource(connectionSettings.getLinkName(), path, rsrc, userSettings.getPrincipal().getLogin());
			
			model.put("result", frontendHelperService.toJSONString(rsrc));
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "renameDir", e);
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param path Path
	 * @param left Tree node to reload after change
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/file-rmdir.html", method = RequestMethod.GET)
	public Map<String, Object> showRmdirDialog(@RequestParam("path") String path, @RequestParam("left") String left)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("left", trimLeft(left));
		model.put("dirname", path);
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param path Path
	 * @param left Tree node to reload after change
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/file-rmdir.html", method = RequestMethod.POST)
	public Map<String, Object> removeDir(@RequestParam("path") String path, @RequestParam("left") String left)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("left", trimLeft(left));
		
		try	{
			remoteResourceService.removeResource(connectionSettings.getLinkName(), path, userSettings.getPrincipal().getLogin());
			
			model.put("result", frontendHelperService.toJSONString(path));
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "removeDir", e);
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	
	private String trimLeft(String left)
		{
		if (left.endsWith("-"))
			return (left.substring(0, left.length() - 1));
		else
			return (left);
		}
	}
