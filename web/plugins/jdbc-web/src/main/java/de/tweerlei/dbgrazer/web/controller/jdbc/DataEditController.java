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
package de.tweerlei.dbgrazer.web.controller.jdbc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService.ColumnMode;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.jdbc.QueryGeneratorService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;

/**
 * Controller for running queries
 * 
 * @author Robert Wruck
 */
@Controller
public class DataEditController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String catalog;
		private String schema;
		private String object;
		private String backTo;
		private final Map<Integer, String> ids;
		private final Map<Integer, String> params;
		private final Map<Integer, Boolean> nulls;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.ids = new TreeMap<Integer, String>();
			this.params = new TreeMap<Integer, String>();
			this.nulls = new TreeMap<Integer, Boolean>();
			}
		
		/**
		 * Get the settings
		 * @return the settings
		 */
		public Map<Integer, String> getParams()
			{
			return params;
			}

		/**
		 * Get the settings
		 * @return the settings
		 */
		public Map<Integer, Boolean> getNulls()
			{
			return nulls;
			}

		/**
		 * @return the catalog
		 */
		public String getCatalog()
			{
			return catalog;
			}

		/**
		 * @param catalog the catalog to set
		 */
		public void setCatalog(String catalog)
			{
			this.catalog = catalog;
			}

		/**
		 * @return the schema
		 */
		public String getSchema()
			{
			return schema;
			}

		/**
		 * @param schema the schema to set
		 */
		public void setSchema(String schema)
			{
			this.schema = schema;
			}

		/**
		 * @return the object
		 */
		public String getObject()
			{
			return object;
			}

		/**
		 * @param object the object to set
		 */
		public void setObject(String object)
			{
			this.object = object;
			}

		/**
		 * @return the ids
		 */
		public Map<Integer, String> getIds()
			{
			return ids;
			}

		/**
		 * @return the backTo
		 */
		public String getBackTo()
			{
			return backTo;
			}

		/**
		 * @param backTo the backTo to set
		 */
		public void setBackTo(String backTo)
			{
			this.backTo = backTo;
			}
		}
	
	private final MetadataService metadataService;
	private final QueryService queryService;
	private final QueryPerformerService runner;
	private final QueryGeneratorService queryGeneratorService;
	private final ConnectionSettings connectionSettings;
	private final DataFormatterFactory factory;
	private final FrontendHelperService frontendHelperService;
	
	/**
	 * Constructor
	 * @param metadataService MetadataService
	 * @param queryService QueryService
	 * @param runner QueryPerformerService
	 * @param queryGeneratorService QueryGeneratorService
	 * @param connectionSettings ConnectionSettings
	 * @param factory DataFormatterFactory
	 * @param frontendHelperService FrontendHelperService
	 */
	@Autowired
	public DataEditController(MetadataService metadataService, QueryService queryService, QueryPerformerService runner,
			QueryGeneratorService queryGeneratorService, ConnectionSettings connectionSettings,
			DataFormatterFactory factory, FrontendHelperService frontendHelperService)
		{
		this.metadataService = metadataService;
		this.queryService = queryService;
		this.runner = runner;
		this.queryGeneratorService = queryGeneratorService;
		this.connectionSettings = connectionSettings;
		this.factory = factory;
		this.frontendHelperService = frontendHelperService;
		}
	
	/**
	 * Get the FormBackingObject
	 * @param obj DB object name
	 * @return FormBackingObject
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject(@RequestParam(value = "q", required = false) String obj)
		{
		final FormBackingObject fbo = new FormBackingObject();
		// Hack to recognize object name in "q" as well as "object" parameter
		fbo.setObject(obj);
		return (fbo);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/insert-simple.html", method = RequestMethod.GET)
	public Map<String, Object> showInsertDialog(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		final DataFormatter fmt = factory.getExportFormatter();
		
		final Query src = queryService.findQueryByName(connectionSettings.getLinkName(), fbo.getBackTo());
		final Query query;
		if (src == null)
			query = queryGeneratorService.createInsertQuery(info, getSQLDialect(), fmt, null, null);
		else
			query = queryGeneratorService.createInsertQuery(info, getSQLDialect(), fmt, src.getAttributes().get(RowSetConstants.ATTR_TABLE_PK_SELECT), null);
		
		model.put("parameters", query.getParameters());
		model.put("fkTables", extractFkTables(query));
		for (int i = 0, n = query.getParameters().size(); i < n; i++)
			fbo.getNulls().put(i, true);
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/insert-simple.html", method = RequestMethod.POST)
	public Map<String, Object> performInsertQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		for (Map.Entry<Integer, String> ent : fbo.getParams().entrySet())
			{
			if (fbo.getNulls().get(ent.getKey()) != Boolean.TRUE)
				ent.setValue(null);
			}
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription t = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		final DataFormatter fmt = factory.getExportFormatter();
		
		final Query src = queryService.findQueryByName(connectionSettings.getLinkName(), fbo.getBackTo());
		final Query q;
		if (src == null)
			q = queryGeneratorService.createInsertQuery(t, getSQLDialect(), fmt, null, fbo.getParams());
		else
			q = queryGeneratorService.createInsertQuery(t, getSQLDialect(), fmt, src.getAttributes().get(RowSetConstants.ATTR_TABLE_PK_SELECT), fbo.getParams());
		
		try	{
			final Result r = runner.performQuery(connectionSettings.getLinkName(), q, fbo.getParams());
			model.put("result", frontendHelperService.toJSONString(String.valueOf(r.getFirstRowSet().getFirstValue())));
			model.put("exceptionText", null);
			}
		catch (PerformQueryException e)
			{
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/update-simple.html", method = RequestMethod.GET)
	public Map<String, Object> showUpdateDialog(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		final DataFormatter fmt = factory.getExportFormatter();
		
		try	{
			final Query sel = queryGeneratorService.createSelectQuery(info, getSQLDialect(), fmt);
			
			final Result r = runner.performQuery(connectionSettings.getLinkName(), sel, fbo.getIds());
			
			final RowSet rs = r.getFirstRowSet();
			if (!rs.getRows().isEmpty())
				{
				final ResultRow row = rs.getFirstRow();
				int i = 0;
				for (Object o : row.getValues())
					{
					final ColumnType type = rs.getColumns().get(i).getType();
					if (type == ColumnType.BOOLEAN)
						fbo.getParams().put(i, String.valueOf(o));
					else
						fbo.getParams().put(i, fmt.format(type, o));
					fbo.getNulls().put(i, o != null);
					i++;
					}
				}
			}
		catch (PerformQueryException e)
			{
			model.put("exceptionText", e.getMessage());
			}
		
		final Query query = queryGeneratorService.createUpdateQuery(info, getSQLDialect(), fmt, false);
		
		model.put("parameters", query.getParameters());
		model.put("fkTables", extractFkTables(query));
		for (int i = 0, n = query.getParameters().size(); i < n; i++)
			{
			if (!fbo.getNulls().containsKey(i))
				fbo.getNulls().put(i, fbo.getParams().get(i) != null);
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/update-simple.html", method = RequestMethod.POST)
	public Map<String, Object> performUpdateQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		for (Map.Entry<Integer, String> ent : fbo.getParams().entrySet())
			{
			if (fbo.getNulls().get(ent.getKey()) != Boolean.TRUE)
				ent.setValue(null);
			}
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription t = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		final DataFormatter fmt = factory.getExportFormatter();
		
		final Query q = queryGeneratorService.createUpdateQuery(t, getSQLDialect(), fmt, true);
		
		// The last query parameters are the PK values in the WHERE clause
		final int np = fbo.getParams().size();
		final int ni = fbo.getIds().size();
		final int n = q.getParameters().size();
		for (int i = 0; (i < ni) && (np + i < n); i++)
			fbo.getParams().put(np + i, fbo.getIds().get(i));
		
		try	{
			final Result r = runner.performQuery(connectionSettings.getLinkName(), q, fbo.getParams());
			model.put("result", frontendHelperService.toJSONString(String.valueOf(r.getFirstRowSet().getFirstValue())));
			model.put("exceptionText", null);
			}
		catch (PerformQueryException e)
			{
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/copy-simple.html", method = RequestMethod.GET)
	public Map<String, Object> showCopyDialog(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		final DataFormatter fmt = factory.getExportFormatter();
		
		final Query src = queryService.findQueryByName(connectionSettings.getLinkName(), fbo.getBackTo());
		final Query query;
		final boolean includePK;
		if (src == null)
			{
			query = queryGeneratorService.createInsertQuery(info, getSQLDialect(), fmt, null, null);
			includePK = true;
			}
		else
			{
			final String pkExpr = src.getAttributes().get(RowSetConstants.ATTR_TABLE_PK_SELECT);
			query = queryGeneratorService.createInsertQuery(info, getSQLDialect(), fmt, pkExpr, null);
			includePK = StringUtils.empty(pkExpr);
			}
		
		try	{
			final Query sel = queryGeneratorService.createSelectQuery(info, getSQLDialect(), fmt);
			
			final Result r = runner.performQuery(connectionSettings.getLinkName(), sel, fbo.getIds());
			
			final RowSet rs = r.getFirstRowSet();
			if (!rs.getRows().isEmpty())
				{
				final ResultRow row = rs.getFirstRow();
				final Set<Integer> pk = info.getPKColumns();
				int i = 0;
				int j = 0;
				int k = 0;
				for (int c = 0; c < info.getColumns().size(); c++)
					{
					if (pk.contains(c))
						{
						if (includePK)
							{
							// PK columns are not returned by the SELECT, yet they part of the INSERT parameter list
							// Copy the PK values from the FBO
							final String keyValue = fbo.getIds().get(k);
							fbo.getParams().put(j, StringUtils.notNull(keyValue));
							fbo.getNulls().put(j, Boolean.TRUE);
							j++;
							k++;
							}
						}
					else
						{
						final Object o = row.getValues().get(i);
						final ColumnType type = rs.getColumns().get(i).getType();
						if (type == ColumnType.BOOLEAN)
							fbo.getParams().put(j, String.valueOf(o));
						else
							fbo.getParams().put(j, fmt.format(type, o));
						fbo.getNulls().put(j, o != null);
						i++;
						j++;
						}
					}
				}
			}
		catch (PerformQueryException e)
			{
			model.put("exceptionText", e.getMessage());
			}
		
		model.put("parameters", query.getParameters());
		model.put("fkTables", extractFkTables(query));
		for (int i = 0, n = query.getParameters().size(); i < n; i++)
			{
			if (!fbo.getNulls().containsKey(i))
				fbo.getNulls().put(i, fbo.getParams().get(i) != null);
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/delete-simple.html", method = RequestMethod.GET)
	public Map<String, Object> showDeleteDialog(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		final DataFormatter fmt = factory.getExportFormatter();
		
		final Query query = queryGeneratorService.createDeleteQuery(info, getSQLDialect(), fmt);
		
		model.put("parameters", query.getParameters());
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/delete-simple.html", method = RequestMethod.POST)
	public Map<String, Object> performDeleteQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription t = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		final DataFormatter fmt = factory.getExportFormatter();
		
		final Query q = queryGeneratorService.createDeleteQuery(t, getSQLDialect(), fmt);
		
		try	{
			final Result r = runner.performQuery(connectionSettings.getLinkName(), q, fbo.getIds());
			model.put("result", frontendHelperService.toJSONString(String.valueOf(r.getFirstRowSet().getFirstValue())));
			model.put("exceptionText", null);
			}
		catch (PerformQueryException e)
			{
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	
	private Map<Integer, QualifiedName> extractFkTables(Query query)
		{
		final Map<Integer, QualifiedName> ret = new HashMap<Integer, QualifiedName>();
		
		int i = 0;
		for (ParameterDef p : query.getParameters())
			{
			final QualifiedName qn = QualifiedName.valueOf(p.getValueQuery());
			if (qn != null)
				ret.put(i, qn);
			i++;
			}
		
		return (ret);
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	}
