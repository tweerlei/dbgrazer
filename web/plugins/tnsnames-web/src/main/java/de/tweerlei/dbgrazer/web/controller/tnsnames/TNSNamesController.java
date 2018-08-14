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
package de.tweerlei.dbgrazer.web.controller.tnsnames;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.link.model.impl.LinkDefImpl;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.web.constant.CacheClass;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.session.ResultCache;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.ermtools.tns.TNSNamesHelper;
import de.tweerlei.ermtools.tns.TNSNamesParser;
import de.tweerlei.ermtools.tns.TNSNamesWriter;
import de.tweerlei.spring.web.view.GenericDownloadView;
import de.tweerlei.spring.web.view.StringDownloadSource;

/**
 * Controller for up-/downloading links as Oracle TNSNames
 * 
 * @author Robert Wruck
 */
@Controller
public class TNSNamesController
	{
	/** Helper object for a TNS parser error */
	public static final class TNSError implements Serializable
		{
		private final String message;
		private final int lineNumber;
		
		/**
		 * Constructor
		 * @param message Error message
		 * @param lineNumber Line number
		 */
		public TNSError(String message, int lineNumber)
			{
			this.message = message;
			this.lineNumber = lineNumber;
			}

		/**
		 * Get the message
		 * @return the message
		 */
		public String getMessage()
			{
			return message;
			}

		/**
		 * Get the lineNumber
		 * @return the lineNumber
		 */
		public int getLineNumber()
			{
			return lineNumber;
			}
		}
	
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String entry;
		private String link;
		private String name;
		private String description;
		private String username;
		private String password;
		private String schema;
		private String subSchema;
		private boolean applyToAll;
		
		/**
		 * Get the entry
		 * @return the entry
		 */
		public String getEntry()
			{
			return entry;
			}

		/**
		 * Set the entry
		 * @param entry the entry to set
		 */
		public void setEntry(String entry)
			{
			this.entry = entry;
			}

		/**
		 * Get the link
		 * @return the link
		 */
		public String getLink()
			{
			return link;
			}

		/**
		 * Set the link
		 * @param link the link to set
		 */
		public void setLink(String link)
			{
			this.link = link;
			}

		/**
		 * Get the schema
		 * @return the schema
		 */
		public String getSchema()
			{
			return schema;
			}

		/**
		 * Set the schema
		 * @param schema the schema to set
		 */
		public void setSchema(String schema)
			{
			this.schema = schema;
			}

		/**
		 * Get the subSchema
		 * @return the subSchema
		 */
		public String getSubSchema()
			{
			return subSchema;
			}

		/**
		 * Set the subSchema
		 * @param subSchema the subSchema to set
		 */
		public void setSubSchema(String subSchema)
			{
			this.subSchema = subSchema;
			}

		/**
		 * Get the name
		 * @return the name
		 */
		public String getName()
			{
			return name;
			}

		/**
		 * Set the name
		 * @param name the name to set
		 */
		public void setName(String name)
			{
			this.name = name;
			}

		/**
		 * Get the description
		 * @return the description
		 */
		public String getDescription()
			{
			return description;
			}

		/**
		 * Set the description
		 * @param description the description to set
		 */
		public void setDescription(String description)
			{
			this.description = description;
			}

		/**
		 * Get the password
		 * @return the password
		 */
		public String getPassword()
			{
			return password;
			}

		/**
		 * Set the password
		 * @param password the password to set
		 */
		public void setPassword(String password)
			{
			this.password = password;
			}

		/**
		 * Get the username
		 * @return the username
		 */
		public String getUsername()
			{
			return username;
			}

		/**
		 * Set the username
		 * @param username the username to set
		 */
		public void setUsername(String username)
			{
			this.username = username;
			}

		/**
		 * @return the applyToAll
		 */
		public boolean isApplyToAll()
			{
			return applyToAll;
			}
		
		/**
		 * @param applyToAll the applyToAll to set
		 */
		public void setApplyToAll(boolean applyToAll)
			{
			this.applyToAll = applyToAll;
			}
		}
	
	private final LinkService linkService;
	private final UserSettings userSettings;
	private final ResultCache resultCache;
	
	/**
	 * Constructor
	 * @param linkService LinkService
	 * @param userSettings UserSettings
	 * @param resultCache ResultCache
	 */
	@Autowired
	public TNSNamesController(LinkService linkService, UserSettings userSettings,
			ResultCache resultCache)
		{
		this.linkService = linkService;
		this.userSettings = userSettings;
		this.resultCache = resultCache;
		}
	
	/**
	 * Display a single link
	 * @param conn Link name
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/tnsname.html", method = RequestMethod.GET)
	public Map<String, Object> showTNSName(@RequestParam("q") String conn)
		{
		if (!userSettings.isLinkEditorEnabled())
			throw new AccessDeniedException();
		
		final LinkDef u = linkService.findLinkByName(conn);
		if (u == null)
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final StringWriter sw = new StringWriter();
		final TNSNamesWriter tw = new TNSNamesWriter(sw, true);
		
		try	{
			final Map<String, Object> entry = TNSNamesHelper.getTNSEntry(u.getUrl());
			tw.writeComment(u.getFullDescription());
			tw.writeEntry(u.getName(), entry);
			}
		catch (Exception e)
			{
			// ignore
			}
		
		model.put("connection", conn);
		model.put("tnsname", sw.toString());
		
		return (model);
		}
	
	/**
	 * Apply a TNSName to a link
	 * @param conn Link name
	 * @param tnsname TNSNames entry
	 * @return Model
	 */
	@RequestMapping(value = "/tnsname-apply.html", method = RequestMethod.POST)
	public String applyTNSName(
			@RequestParam("q") String conn,
			@RequestParam("tnsname") String tnsname
			)
		{
		if (!userSettings.isLinkEditorEnabled())
			throw new AccessDeniedException();
		
		final LinkDef c = linkService.findLinkByName(conn);
		if (c == null)
			throw new AccessDeniedException();
		
		final LineNumberReader lnr = new LineNumberReader(new StringReader(tnsname));
		final Map<String, Object> map;
		
		try	{
			final TNSNamesParser p = new TNSNamesParser();
			map = p.parse(lnr);
			if (!map.isEmpty())
				{
				@SuppressWarnings("unchecked")
				final Map<String, Object> entry = (Map<String, Object>) map.values().iterator().next();
				final String url = TNSNamesHelper.getJdbcURL(entry);
				
				final LinkDef u = new LinkDefImpl(
						c.getType(), c.getName(), c.getDescription(), c.getDriver(),
						url, c.getUsername(), c.getPassword(), c.isWritable(),
						c.getPreDMLStatement(), c.getPostDMLStatement(), c.getGroupName(), c.getSetName(),
						c.getDialectName(), c.getProperties(), c.getSchema().getName(), c.getSchema().getVersion(),
						c.getQuerySetNames()
						);
				linkService.updateLink(userSettings.getPrincipal().getLogin(), conn, u);
				}
			}
		catch (Exception e)
			{
			// ignore and redirect
			}
		
		return ("redirect:link.html?q="+conn);
		}
	
	/**
	 * Display the links
	 * @return Model
	 */
	@RequestMapping(value = "/tnsnames.html", method = RequestMethod.GET)
	public Map<String, Object> showTNSNames()
		{
		if (!userSettings.isLinkEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<SchemaDef, List<LinkDef>> links = linkService.groupAllLinks();
		
		final StringWriter sw = new StringWriter();
		final TNSNamesWriter tw = new TNSNamesWriter(sw, true);
		
		for (Map.Entry<SchemaDef, List<LinkDef>> ent : links.entrySet())
			{
			for (LinkDef c : ent.getValue())
				{
				try	{
					final Map<String, Object> entry = TNSNamesHelper.getTNSEntry(c.getUrl());
					tw.writeComment(c.getFullDescription());
					tw.writeEntry(c.getName(), entry);
					}
				catch (Exception e)
					{
					// ignore
					}
				}
			}
		
		final StringDownloadSource ds = new StringDownloadSource(sw.toString(), "text/plain", "ISO-8859-1", false);
		ds.setAttachment(true);
		ds.setFileName("tnsnames.ora");
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, ds);
		
		return (model);
		}
	
	/**
	 * Upload TNSNames
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/tnsnames-upload.html", method = RequestMethod.GET)
	public Map<String, Object> showUploadForm()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		return (model);
		}
	
	/**
	 * Upload TNSNames
	 * @param file Uploaded file
	 * @return Model
	 */
	@RequestMapping(value = "/tnsnames-upload.html", method = RequestMethod.POST)
	public String uploadTNSNames(@RequestParam("file") MultipartFile file)
		{
		if (!userSettings.isLinkEditorEnabled())
			throw new AccessDeniedException();
		
		try	{
			final InputStream is = file.getInputStream();
			try	{
				final LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is, "ISO-8859-1"));
				final Map<String, Object> map;
				
				try	{
					final TNSNamesParser p = new TNSNamesParser();
					map = p.parse(lnr);
					}
				catch (IllegalStateException e)
					{
					resultCache.addCachedObject(CacheClass.UPLOAD, null, "TNSError", null, new TNSError(e.getMessage(), lnr.getLineNumber()));
					return ("redirect:tnsnames-error.html");
					}
				
				final TreeMap<String, String> parsedURLs = new TreeMap<String, String>();
				for (Map.Entry<String, Object> ent : map.entrySet())
					{
					final String name = ent.getKey();
					@SuppressWarnings("unchecked")
					final Map<String, Object> entry = (Map<String, Object>) ent.getValue();
					
					try	{
						parsedURLs.put(name, TNSNamesHelper.getJdbcURL(entry));
						}
					catch (IllegalArgumentException e)
						{
						// ignore invalid entries...
						}
					}
				
				resultCache.addCachedObject(CacheClass.UPLOAD, null, "TNSNames", null, parsedURLs);
				}
			finally
				{
				is.close();
				}
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		
		return ("redirect:tnsnames-apply.html");
		}
	
	/**
	 * Show upload errors
	 * @return Model
	 */
	@RequestMapping(value = "/tnsnames-error.html", method = RequestMethod.GET)
	public Map<String, Object> showTNSNamesErrors()
		{
		if (!userSettings.isLinkEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("error", resultCache.getCachedObject(CacheClass.UPLOAD, null, "TNSError", null, TNSError.class));
		
		return (model);
		}
	
	/**
	 * Get the FormBackingObject
	 * @return FormBackingObject
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject()
		{
		return (new FormBackingObject());
		}
	
	/**
	 * Show uploaded TNSNames
	 * @param updated Update counter
	 * @return Model
	 */
	@RequestMapping(value = "/tnsnames-apply.html", method = RequestMethod.GET)
	public Map<String, Object> selectTNSNames(@RequestParam(value = "updated", required = false) Integer updated)
		{
		if (!userSettings.isLinkEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final List<LinkDef> links = new ArrayList<LinkDef>();
		final Map<SchemaDef, List<LinkDef>> allLinks = linkService.groupAllLinks();
		for (List<LinkDef> l : allLinks.values())
			{
			for (LinkDef c : l)
				{
				if (c.getType().getName().equals("JDBC"))
					links.add(c);
				}
			}
		
		model.put("links", links);
		model.put("entries", resultCache.getCachedObject(CacheClass.UPLOAD, null, "TNSNames", null, TreeMap.class));
		model.put("updated", updated);
		
		return (model);
		}
	
	/**
	 * Apply an uploaded TNSName to a link
	 * @param fbo FormBackingObject
	 * @param result BindingResult
	 * @return Model
	 */
	@RequestMapping(value = "/tnsnames-apply.html", method = RequestMethod.POST)
	public String applyTNSNames(@ModelAttribute("model") FormBackingObject fbo, BindingResult result)
		{
		if (!userSettings.isLinkEditorEnabled())
			throw new AccessDeniedException();
		
		int ret = -1;
		
		@SuppressWarnings("unchecked")
		final TreeMap<String, String> parsedURLs = resultCache.getCachedObject(CacheClass.UPLOAD, null, "TNSNames", null, TreeMap.class);
		if (parsedURLs != null)
			{
			final String url = parsedURLs.get(fbo.getEntry());
			if (url != null)
				{
				try	{
					final LinkDef c = linkService.getLink(fbo.getLink(), null);
					if (c == null)
						{
						final LinkDef conn = new LinkDefImpl(
								linkService.findLinkType("JDBC"), fbo.getName(), fbo.getDescription(), "oracle.jdbc.OracleDriver",
								url, fbo.getUsername(), fbo.getPassword(), false,
								"", "", "", "",
								"oracle", new Properties(), fbo.getSchema(), fbo.getSubSchema(),
								null);
						linkService.createLink(userSettings.getPrincipal().getLogin(), conn);
						ret = 0;
						}
					else if (fbo.isApplyToAll())
						{
						ret = 0;
						for (LinkDef conn : linkService.findLinksByUrl("JDBC", c.getUrl()))
							{
							updateLinkUrl(conn, url);
							ret++;
							}
						}
					else
						{
						updateLinkUrl(c, url);
						ret = 1;
						}
					}
				catch (BindException e)
					{
					result.addAllErrors(e);
					return ("tnsnames-apply");
					}
				}
			}
		
		return ("redirect:tnsnames-apply.html?updated=" + ret);
		}
	
	private void updateLinkUrl(LinkDef c, String url) throws BindException
		{
		final LinkDef conn = new LinkDefImpl(
				c.getType(), c.getName(), c.getDescription(), c.getDriver(),
				url, c.getUsername(), c.getPassword(), c.isWritable(),
				c.getPreDMLStatement(), c.getPostDMLStatement(), c.getGroupName(), c.getSetName(),
				c.getDialectName(), c.getProperties(), c.getSchema().getName(), c.getSchema().getVersion(),
				c.getQuerySetNames()
				);
		
		linkService.updateLink(userSettings.getPrincipal().getLogin(), c.getName(), conn);
		}
	}
