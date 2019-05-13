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
package de.tweerlei.dbgrazer.web.controller.linkedit;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.LinkErrorKeys;
import de.tweerlei.dbgrazer.link.model.LinkType;
import de.tweerlei.dbgrazer.link.model.impl.LinkDefImpl;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.web.constant.ErrorKeys;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.FrontendExtensionService;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;

/**
 * Controller for editing link definitions
 * 
 * @author Robert Wruck
 */
@Controller
public class LinkEditController
	{
	private static final int MAX_PARAMS = 10;
	
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String originalName;
		private String name;
		private String type;
		private String description;
		private String driver;
		private String url;
		private String username;
		private String originalPassword;
		private String password;
		private boolean writable;
		private String preDMLStatement;
		private String postDMLStatement;
		private String schema;
		private String subSchema;
		private String group;
		private String setName;
		private String dialect;
		private boolean showOnLogin;
		private String newQuerySet;
		private boolean applySetToAll;
		private boolean applyGroupToAll;
		private boolean applyUrlToAll;
		private final Map<String, Boolean> querySets;
		private final Map<Integer, String> propNames;
		private final Map<Integer, String> propValues;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.querySets = new TreeMap<String, Boolean>();
			this.propNames = new TreeMap<Integer, String>();
			this.propValues = new TreeMap<Integer, String>();
			}
		
		/**
		 * Get the type
		 * @return the type
		 */
		public String getType()
			{
			return type;
			}
		
		/**
		 * Set the type
		 * @param type the type to set
		 */
		public void setType(String type)
			{
			this.type = type;
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
		 * Get the visible
		 * @return the visible
		 */
		public String getGroup()
			{
			return group;
			}

		/**
		 * Set the group
		 * @param group the group to set
		 */
		public void setGroup(String group)
			{
			this.group = group;
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
		 * Get the originalPassword
		 * @return the originalPassword
		 */
		public String getOriginalPassword()
			{
			return originalPassword;
			}

		/**
		 * Set the originalPassword
		 * @param originalPassword the originalPassword to set
		 */
		public void setOriginalPassword(String originalPassword)
			{
			this.originalPassword = originalPassword;
			}

		/**
		 * Get the driver
		 * @return the driver
		 */
		public String getDriver()
			{
			return driver;
			}

		/**
		 * Set the driver
		 * @param driver the driver to set
		 */
		public void setDriver(String driver)
			{
			this.driver = driver;
			}

		/**
		 * Get the url
		 * @return the url
		 */
		public String getUrl()
			{
			return url;
			}

		/**
		 * Set the url
		 * @param url the url to set
		 */
		public void setUrl(String url)
			{
			this.url = url;
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
		 * Get the propNames
		 * @return the propNames
		 */
		public Map<Integer, String> getPropNames()
			{
			return propNames;
			}

		/**
		 * Get the propValues
		 * @return the propValues
		 */
		public Map<Integer, String> getPropValues()
			{
			return propValues;
			}

		/**
		 * Get the originalName
		 * @return the originalName
		 */
		public String getOriginalName()
			{
			return originalName;
			}

		/**
		 * Set the originalName
		 * @param originalName the originalName to set
		 */
		public void setOriginalName(String originalName)
			{
			this.originalName = originalName;
			}

		/**
		 * Get the showOnLogin
		 * @return the showOnLogin
		 */
		public boolean isShowOnLogin()
			{
			return showOnLogin;
			}

		/**
		 * Set the showOnLogin
		 * @param showOnLogin the showOnLogin to set
		 */
		public void setShowOnLogin(boolean showOnLogin)
			{
			this.showOnLogin = showOnLogin;
			}

		/**
		 * Get the dialect
		 * @return the dialect
		 */
		public String getDialect()
			{
			return dialect;
			}
		
		/**
		 * Set the dialect
		 * @param dialect the dialect to set
		 */
		public void setDialect(String dialect)
			{
			this.dialect = dialect;
			}

		/**
		 * @return the newQuerySet
		 */
		public String getNewQuerySet()
			{
			return newQuerySet;
			}

		/**
		 * @param newQuerySet the newQuerySet to set
		 */
		public void setNewQuerySet(String newQuerySet)
			{
			this.newQuerySet = newQuerySet;
			}

		/**
		 * @return the querySets
		 */
		public Map<String, Boolean> getQuerySets()
			{
			return querySets;
			}

		/**
		 * @return the writable
		 */
		public boolean isWritable()
			{
			return writable;
			}

		/**
		 * @param writable the writable to set
		 */
		public void setWritable(boolean writable)
			{
			this.writable = writable;
			}

		/**
		 * Get the preDMLStatement
		 * @return the preDMLStatement
		 */
		public String getPreDMLStatement()
			{
			return preDMLStatement;
			}

		/**
		 * Set the preDMLStatement
		 * @param preDMLStatement the preDMLStatement to set
		 */
		public void setPreDMLStatement(String preDMLStatement)
			{
			this.preDMLStatement = preDMLStatement;
			}

		/**
		 * Get the postDMLStatement
		 * @return the postDMLStatement
		 */
		public String getPostDMLStatement()
			{
			return postDMLStatement;
			}

		/**
		 * Set the postDMLStatement
		 * @param postDMLStatement the postDMLStatement to set
		 */
		public void setPostDMLStatement(String postDMLStatement)
			{
			this.postDMLStatement = postDMLStatement;
			}

		/**
		 * Get the setName
		 * @return the setName
		 */
		public String getSetName()
			{
			return setName;
			}

		/**
		 * Set the setName
		 * @param setName the setName to set
		 */
		public void setSetName(String setName)
			{
			this.setName = setName;
			}
		
		/**
		 * @return the applySetToAll
		 */
		public boolean isApplySetToAll()
			{
			return applySetToAll;
			}
		
		/**
		 * @param applySetToAll the applySetToAll to set
		 */
		public void setApplySetToAll(boolean applySetToAll)
			{
			this.applySetToAll = applySetToAll;
			}
		
		/**
		 * @return the applyGroupToAll
		 */
		public boolean isApplyGroupToAll()
			{
			return applyGroupToAll;
			}
		
		/**
		 * @param applyGroupToAll the applyGroupToAll to set
		 */
		public void setApplyGroupToAll(boolean applyGroupToAll)
			{
			this.applyGroupToAll = applyGroupToAll;
			}
		
		/**
		 * @return the applyUrlToAll
		 */
		public boolean isApplyUrlToAll()
			{
			return applyUrlToAll;
			}
		
		/**
		 * @param applyUrlToAll the applyUrlToAll to set
		 */
		public void setApplyUrlToAll(boolean applyUrlToAll)
			{
			this.applyUrlToAll = applyUrlToAll;
			}
		}
	
	private final LinkService connectionService;
	private final FrontendExtensionService extensionService;
	private final UserSettings userSettings;
	
	/**
	 * Constructor
	 * @param connectionService ConnectionService
	 * @param extensionService FrontendExtensionService
	 * @param userSettings UserSettings
	 */
	@Autowired
	public LinkEditController(LinkService connectionService, FrontendExtensionService extensionService,
			UserSettings userSettings)
		{
		this.connectionService = connectionService;
		this.extensionService = extensionService;
		this.userSettings = userSettings;
		}
	
	/**
	 * Get the FormBackingObject
	 * @param conn Connection name
	 * @param template Template user name
	 * @return FormBackingObject
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject(
			@RequestParam(value = "q", required = false) String conn,
			@RequestParam(value = "template", required = false) String template
			)
		{
		if (!userSettings.isLinkEditorEnabled())
			throw new AccessDeniedException();
		
		final FormBackingObject ret = new FormBackingObject();
		
		final boolean creating = StringUtils.empty(conn);
		
		final String queryName = creating ? template : conn;
		
		if (queryName != null)
			{
			final LinkDef u = connectionService.findLinkByName(queryName);
			if (u == null)
				throw new AccessDeniedException();
			
			ret.setType(u.getType().getName());
			ret.setName(creating ? "" : u.getName());
			ret.setOriginalName(creating ? "" : u.getName());
			ret.setDescription(u.getDescription());
			ret.setDriver(u.getDriver());
			ret.setUrl(u.getUrl());
			ret.setUsername(u.getUsername());
			ret.setPassword("");
			ret.setWritable(u.isWritable());
			ret.setPreDMLStatement(u.getPreDMLStatement());
			ret.setPostDMLStatement(u.getPostDMLStatement());
			ret.setOriginalPassword(u.getPassword());
			ret.setDialect(u.getDialectName());
			if (LinkDef.LOGIN_GROUP.equals(u.getGroupName()))
				{
				ret.setShowOnLogin(true);
				ret.setGroup("");
				}
			else
				{
				ret.setShowOnLogin(false);
				ret.setGroup(u.getGroupName());
				}
			ret.setSetName(u.getSetName());
			ret.setSchema(u.getSchema().getName());
			ret.setSubSchema(u.getSchema().getVersion());
			for (String s : u.getQuerySetNames())
				ret.getQuerySets().put(s, Boolean.TRUE);
			
			int i = 0;
			for (Map.Entry<Object, Object> ent : u.getProperties().entrySet())
				{
				ret.getPropNames().put(i, ent.getKey().toString());
				ret.getPropValues().put(i, ent.getValue().toString());
				i++;
				}
			}
		else
			{
			ret.setType("");
			ret.setName("");
			ret.setOriginalName("");
			ret.setDescription("");
			ret.setDriver("");
			ret.setUrl("jdbc:");
			ret.setUsername("");
			ret.setPassword("");
			ret.setWritable(false);
			ret.setPreDMLStatement("");
			ret.setPostDMLStatement("");
			ret.setOriginalPassword("");
			ret.setGroup("");
			ret.setSetName("");
			ret.setDialect("");
			ret.setShowOnLogin(false);
			ret.setSchema("");
			ret.setSubSchema("");
			}
		
		for (int i = 0; i < MAX_PARAMS; i++)
			{
			if (!ret.getPropNames().containsKey(i))
				{
				ret.getPropNames().put(i, "");
				ret.getPropValues().put(i, "");
				}
			}
		
		return (ret);
		}
	
	/**
	 * Get available SQLDialect names
	 * @return SQLDialect names
	 */
	@ModelAttribute("dialects")
	public Set<String> getDialects()
		{
		return (SQLDialectFactory.getSQLDialects().keySet());
		}
	
	/**
	 * Get available query set names
	 * @return Query set names
	 */
	@ModelAttribute("querySets")
	public Set<String> getQuerySets()
		{
		return (connectionService.findAllQuerySets());
		}
	
	/**
	 * Get all connectionTypes
	 * @return connectionTypes
	 */
	@ModelAttribute("connectionTypes")
	public Set<LinkType> getConnectionTypes()
		{
		return (connectionService.findAllLinkTypes());
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/link.html", method = RequestMethod.GET)
	public Map<String, Object> showLinkForm(@ModelAttribute("model") FormBackingObject fbo)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("extensions", extensionService.getLinkViewExtensions(fbo.getOriginalName()));
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param result BindingResult
	 * @return Model
	 */
	@RequestMapping(value = "/link.html", method = RequestMethod.POST)
	public String updateLink(@ModelAttribute("model") FormBackingObject fbo, BindingResult result)
		{
		final boolean creating = StringUtils.empty(fbo.getOriginalName());
		final String oldURL;
		final String oldSet;
		final String oldGroup;
		if (creating)
			{
			final LinkDef u = connectionService.findLinkByName(fbo.getName());
			if (u != null)
				{
				fbo.setName(u.getName());
				result.reject(ErrorKeys.EXISTS);
				return ("link");
				}
			oldURL = null;
			oldSet = null;
			oldGroup = null;
			}
		else
			{
			final LinkDef qOld = connectionService.findLinkByName(fbo.getOriginalName());
			final LinkDef qNew = connectionService.findLinkByName(fbo.getName());
			if ((qNew != null) && (qNew != qOld))
				{
				fbo.setName(qNew.getName());
				result.reject(ErrorKeys.EXISTS);
				return ("link");
				}
			oldURL = qOld.getUrl();
			oldSet = qOld.getSetName();
			oldGroup = qOld.getGroupName();
			}
		
		final LinkType type = connectionService.findLinkType(fbo.getType());
		if (type == null)
			{
			result.reject(LinkErrorKeys.UNKNOWN_TYPE);
			return ("link");
			}
		
		final String password = StringUtils.empty(fbo.getPassword()) ? fbo.getOriginalPassword() : fbo.getPassword();
		final String group = fbo.isShowOnLogin() ? LinkDef.LOGIN_GROUP : fbo.getGroup();
		final Set<String> querySets = new TreeSet<String>();
		for (Map.Entry<String, Boolean> ent : fbo.getQuerySets().entrySet())
			{
			if ((ent.getValue() != null) && ent.getValue())
				querySets.add(ent.getKey());
			}
		if (!StringUtils.empty(fbo.getNewQuerySet()))
			querySets.add(fbo.getNewQuerySet());
		final Properties props = new Properties();
		for (Map.Entry<Integer, String> ent : fbo.getPropNames().entrySet())
			{
			if (!StringUtils.empty(ent.getValue()))
				{
				final String v = fbo.getPropValues().get(ent.getKey());
				props.setProperty(ent.getValue(), StringUtils.notNull(v));
				}
			}
		
		final String setName;
		final String descr;
		if (StringUtils.empty(fbo.getSetName()))
			{
			setName = fbo.getDescription();
			descr = "";
			}
		else
			{
			setName = fbo.getSetName();
			descr = fbo.getDescription();
			}
		
		final LinkDef u = new LinkDefImpl(type, fbo.getName(), descr, fbo.getDriver(), fbo.getUrl(), fbo.getUsername(), password, fbo.isWritable(),
				fbo.getPreDMLStatement(), fbo.getPostDMLStatement(), group, setName, fbo.getDialect(), props, fbo.getSchema(), fbo.getSubSchema(), querySets);
		
		try	{
			final String name;
			if (creating)
				{
				name = connectionService.createLink(userSettings.getPrincipal().getLogin(), u);
				if (name == null)
					{
					result.reject(ErrorKeys.WRITE_FAILED);
					return ("link");
					}
				}
			else
				{
				name = connectionService.updateLink(userSettings.getPrincipal().getLogin(), fbo.getOriginalName(), u);
				if (name == null)
					{
					result.reject(ErrorKeys.WRITE_FAILED);
					return ("link");
					}
				if (fbo.isApplySetToAll() && (oldSet != null) && !oldSet.equals(setName))
					{
					for (LinkDef conn : connectionService.findLinksBySetName(type.getName(), oldSet))
						updateConnectionSet(conn, setName);
					}
				if (fbo.isApplyGroupToAll() && (oldGroup != null) && !oldGroup.equals(group))
					{
					for (LinkDef conn : connectionService.findLinksByGroupName(type.getName(), oldGroup))
						updateConnectionGroup(conn, group);
					}
				if (fbo.isApplyUrlToAll() && (oldURL != null) && !oldURL.equals(fbo.getUrl()))
					{
					for (LinkDef conn : connectionService.findLinksByUrl(type.getName(), oldURL))
						updateConnectionUrl(conn, fbo.getUrl());
					}
				}
			
			return ("redirect:links.html");
			}
		catch (BindException e)
			{
			result.addAllErrors(e);
			return ("link");
			}
		}
	
	private void updateConnectionSet(LinkDef c, String name) throws BindException
		{
		final LinkDef conn = new LinkDefImpl(
				c.getType(), c.getName(), c.getDescription(), c.getDriver(),
				c.getUrl(), c.getUsername(), c.getPassword(), c.isWritable(),
				c.getPreDMLStatement(), c.getPostDMLStatement(), c.getGroupName(), name,
				c.getDialectName(), c.getProperties(), c.getSchema().getName(), c.getSchema().getVersion(),
				c.getQuerySetNames()
				);
		
		connectionService.updateLink(userSettings.getPrincipal().getLogin(), c.getName(), conn);
		}
	
	private void updateConnectionGroup(LinkDef c, String name) throws BindException
		{
		final LinkDef conn = new LinkDefImpl(
				c.getType(), c.getName(), c.getDescription(), c.getDriver(),
				c.getUrl(), c.getUsername(), c.getPassword(), c.isWritable(),
				c.getPreDMLStatement(), c.getPostDMLStatement(), name, c.getSetName(),
				c.getDialectName(), c.getProperties(), c.getSchema().getName(), c.getSchema().getVersion(),
				c.getQuerySetNames()
				);
		
		connectionService.updateLink(userSettings.getPrincipal().getLogin(), c.getName(), conn);
		}
	
	private void updateConnectionUrl(LinkDef c, String url) throws BindException
		{
		final LinkDef conn = new LinkDefImpl(
				c.getType(), c.getName(), c.getDescription(), c.getDriver(),
				url, c.getUsername(), c.getPassword(), c.isWritable(),
				c.getPreDMLStatement(), c.getPostDMLStatement(), c.getGroupName(), c.getSetName(),
				c.getDialectName(), c.getProperties(), c.getSchema().getName(), c.getSchema().getVersion(),
				c.getQuerySetNames()
				);
		
		connectionService.updateLink(userSettings.getPrincipal().getLogin(), c.getName(), conn);
		}
	}
