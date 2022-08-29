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
package de.tweerlei.dbgrazer.web.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringJoiner;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.common5.jdbc.model.TypeDescription;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.visualization.model.GraphEdge;
import de.tweerlei.dbgrazer.visualization.model.GraphNode;
import de.tweerlei.dbgrazer.visualization.service.GraphBuilder;
import de.tweerlei.dbgrazer.visualization.service.GraphStyle;
import de.tweerlei.dbgrazer.web.constant.VisualizationSettings;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;
import de.tweerlei.dbgrazer.web.model.Visualization;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.SchemaTransformerService;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;
import de.tweerlei.ermtools.model.SQLSchema;
import de.tweerlei.ermtools.schema.CompareVisitor;
import de.tweerlei.ermtools.schema.ObjectMatcher;
import de.tweerlei.ermtools.schema.SchemaNamingStrategy;
import de.tweerlei.ermtools.schema.matchers.DialectTypeMatcher;
import de.tweerlei.ermtools.schema.matchers.LaxColumnMatcher;
import de.tweerlei.ermtools.schema.matchers.LaxForeignKeyMatcher;
import de.tweerlei.ermtools.schema.matchers.LaxIndexMatcher;
import de.tweerlei.ermtools.schema.matchers.LaxPrivilegeMatcher;
import de.tweerlei.ermtools.schema.matchers.StrictTypeMatcher;
import de.tweerlei.ermtools.schema.matchers.UnorderedIndexMatcher;
import de.tweerlei.ermtools.schema.naming.CrossNamingStrategy;
import de.tweerlei.ermtools.schema.naming.StrictNamingStrategy;

/**
 * Transform SQLSchema objects
 * 
 * @author Robert Wruck
 */
@Service
public class SchemaTransformerServiceImpl implements SchemaTransformerService
	{
	private static final String IN_PORT_SUFFIX = "_in";
	private static final String OUT_PORT_SUFFIX = "_out";
	
	private static final class DMLStatementProducer implements StatementProducer
		{
		private final DataFormatterFactory dataFormatterFactory;
		private final TableDescription t;
		private final SQLDialect dialect;
		
		public DMLStatementProducer(DataFormatterFactory dataFormatterFactory,
				TableDescription t, SQLDialect dialect)
			{
			this.dataFormatterFactory = dataFormatterFactory;
			this.t = t;
			this.dialect = dialect;
			}
		
		@Override
		public void produceStatements(StatementHandler h)
			{
			final List<ColumnDef> columns = new ArrayList<ColumnDef>(t.getColumns().size());
			final Set<Integer> pk = t.getPKColumns();
			for (ColumnDescription c : t.getColumns())
				columns.add(new ColumnDefImpl(c.getName(), ColumnType.forSQLType(c.getType()), dialect.dataTypeToString(c.getType()), null, null, null));
			
			final SQLWriter sqlWriter = dataFormatterFactory.getSQLWriter(h, dialect, true);
			sqlWriter.writeInsert(t.getName(), columns, null);
			sqlWriter.writeUpdate(t.getName(), columns, null, null, pk);
			sqlWriter.writeMerge(t.getName(), columns, Collections.<ResultRow>singletonList(null), pk);
			sqlWriter.writeDelete(t.getName(), columns, null, pk);
			}
		
		@Override
		public String getPrepareStatement()
			{
			return null;
			}
		
		@Override
		public String getCleanupStatement()
			{
			return null;
			}
		}
	
	private static final class FKTable
		{
		public final Integer id;
		public final String link;
		public final Map<String, Boolean> columns;
		
		public FKTable(Integer id, String link)
			{
			this.id = id;
			this.link = link;
			this.columns = new TreeMap<String, Boolean>();
			}
		}
	
	private final DataFormatterFactory dataFormatterFactory;
	private final GraphBuilder graphBuilder;
	private final GraphStyle graphStyle;
	
	/**
	 * Constructor
	 * @param dataFormatterFactory DataFormatterFactory
	 * @param graphBuilder GraphBuilder
	 * @param graphStyle GraphStyle
	 */
	@Autowired
	public SchemaTransformerServiceImpl(DataFormatterFactory dataFormatterFactory,
			GraphBuilder graphBuilder, GraphStyle graphStyle)
		{
		this.dataFormatterFactory = dataFormatterFactory;
		this.graphBuilder = graphBuilder;
		this.graphStyle = graphStyle;
		}
	
	@Override
	public TableDescription findTable(Set<TableDescription> s, QualifiedName qn, SQLDialect dialect)
		{
		final String name = dialect.getQualifiedTableName(qn);
		for (TableDescription td : s)
			{
			if (name.equals(dialect.getQualifiedTableName(td.getName())))
				return (td);
			}
		return (null);
		}
	
	@Override
	public List<String> getKeyIndices(TableDescription info)
		{
		final List<String> pkIndices = new ArrayList<String>(info.getColumns().size());
		
		for (ColumnDescription c : info.getColumns())
			{
			final StringJoiner sb = new StringJoiner(", ");
			
			// Occurrence in primary key
			int pkIndex = -1;
			if (info.getPrimaryKey() != null)
				{
				pkIndex = info.getPrimaryKey().getColumns().indexOf(c.getName());
				if (pkIndex >= 0)
					sb.append("P" + (pkIndex + 1));
				}
			
			// Occurrence in unique indices
			int ukIndex = -1;
			for (IndexDescription ix : info.getIndices())
				{
				if (ix.isUnique())
					{
					final int i = ix.getColumns().indexOf(c.getName());
					if ((i >= 0) && ((ukIndex < 0) || (i < ukIndex)))
						ukIndex = i;
					}
				}
			if ((ukIndex >= 0) && (pkIndex < 0))
				sb.append("U" + (ukIndex + 1));
			
			// Occurrence in other indices
			int secIndex = -1;
			for (IndexDescription ix : info.getIndices())
				{
				if (!ix.isUnique())
					{
					final int i = ix.getColumns().indexOf(c.getName());
					if ((i >= 0) && ((secIndex < 0) || (i < secIndex)))
						secIndex = i;
					}
				}
			if ((secIndex >= 0) && (pkIndex < 0) && (ukIndex < 0))
				sb.append("I" + (secIndex + 1));
			
			// Occurrence in foreign keys (position is not relevant)
			boolean fk = false;
			for (ForeignKeyDescription ix : info.getReferencedKeys())
				{
				if (ix.getColumns().containsKey(c.getName()))
					fk = true;
				}
			if (fk)
				sb.append("F");
			
			pkIndices.add(sb.toString());
			}
		
		return (pkIndices);
		}
	
	@Override
	public StatementProducer buildDML(TableDescription t, SQLDialect dialect)
		{
		return (new DMLStatementProducer(dataFormatterFactory, t, dialect));
		}
	
	@Override
	public StatementProducer buildDDL(SQLSchema schema, SQLDialect dialect)
		{
		return (compareSchemas(new SQLSchema(null, null), schema, true, dialect, false));
		}
	
	@Override
	public StatementProducer compareSchemas(SQLSchema left, SQLSchema right, boolean ignoreCatalogSchema, SQLDialect dialect, boolean crossDialect)
		{
		final StatementDifferenceHandler handler = new StatementDifferenceHandler(dialect);
		
		final SchemaNamingStrategy namingStrategy = ignoreCatalogSchema ? new CrossNamingStrategy() : new StrictNamingStrategy();
		final ObjectMatcher<TypeDescription> typeMatcher = crossDialect ? new DialectTypeMatcher(dialect) : new StrictTypeMatcher();
		final ObjectMatcher<PrivilegeDescription> privMatcher = (ignoreCatalogSchema || crossDialect) ? null : new LaxPrivilegeMatcher();
		
		right.accept(new CompareVisitor(left, handler,
				namingStrategy,
				new LaxColumnMatcher(typeMatcher),
				crossDialect ? new UnorderedIndexMatcher() : new LaxIndexMatcher(),
				new LaxForeignKeyMatcher(namingStrategy),
				privMatcher
				));
		return (handler);
		}
	
	@Override
	public Visualization buildGraph(Set<TableDescription> tableSet, QualifiedName start, String name, GraphMode mode, LinkBuilder linkBuilder, SQLDialect dialect)
		{
		final Map<String, TableDescription> tables = new HashMap<String, TableDescription>();
		
		// Use the generic dialect to output table names without quotes
		final SQLDialect generic = SQLDialectFactory.getSQLDialect(null);
		final String startName = (start == null) ? null : generic.getQualifiedTableName(start);
		
		for (TableDescription td : tableSet)
			tables.put(generic.getQualifiedTableName(td.getName()), td);
		
		final Map<String, Integer> tableMap = new HashMap<String, Integer>();
		final Map<String, FKTable> fkTableMap = new HashMap<String, FKTable>();
		final Set<GraphNode> nodeSet = new HashSet<GraphNode>();
		final Set<GraphEdge> edgeSet = new HashSet<GraphEdge>();
		
		int i = 0;
		for (Map.Entry<String, TableDescription> ent : tables.entrySet())
			{
			final String tname = ent.getKey();
			final TableDescription info = ent.getValue();
			
			tableMap.put(tname, i);
			nodeSet.add(new GraphNode(String.valueOf(i), writeTable(tname, info, linkBuilder.buildLink(info.getName(), false), dialect), null, "shape=plaintext"));
			i++;
			}
		
		for (Map.Entry<String, TableDescription> ent : tables.entrySet())
			{
			final String refname = ent.getKey();
			final TableDescription info = ent.getValue();
			final Integer refix = tableMap.get(refname);
			
			for (ForeignKeyDescription fk : info.getReferencedKeys())
				{
				final String tname = generic.getQualifiedTableName(fk.getTableName());
				Integer ix = tableMap.get(tname);
				if (ix == null)
					{
					if (mode == GraphMode.NO_REFS)
						continue;
					
					FKTable ft = fkTableMap.get(tname);
					if (ft == null)
						{
						ft = new FKTable(i, linkBuilder.buildLink(fk.getTableName(), true));
						fkTableMap.put(tname, ft);
						ix = i;
						i++;
						}
					else
						ix = ft.id;
					
					for (String colname : fk.getColumns().values())
						ft.columns.put(colname, Boolean.TRUE);
					}
				
				for (Map.Entry<String, String> fkent : fk.getColumns().entrySet())
					edgeSet.add(new GraphEdge(refix + ":" + fkent.getKey() + OUT_PORT_SUFFIX, ix.toString() + ":" + fkent.getValue() + IN_PORT_SUFFIX, null/*fk.getName()*/, null));
				}
			
			if ((mode != GraphMode.ALL_REFS) && ((mode != GraphMode.START_REFS) || !refname.equals(startName)))
				continue;	// Show referencing keys only for the start table
			
			for (ForeignKeyDescription fk : info.getReferencingKeys())
				{
				final String tname = generic.getQualifiedTableName(fk.getTableName());
				Integer ix = tableMap.get(tname);
				if (ix == null)
					{
					FKTable ft = fkTableMap.get(tname);
					if (ft == null)
						{
						ft = new FKTable(i, linkBuilder.buildLink(fk.getTableName(), true));
						fkTableMap.put(tname, ft);
						ix = i;
						i++;
						}
					else
						ix = ft.id;
					
					for (String colname : fk.getColumns().keySet())
						ft.columns.put(colname, Boolean.FALSE);
					}
				
				for (Map.Entry<String, String> fkent : fk.getColumns().entrySet())
					edgeSet.add(new GraphEdge(ix.toString() + ":" + fkent.getKey() + OUT_PORT_SUFFIX, refix + ":" + fkent.getValue() + IN_PORT_SUFFIX, null/*fk.getName()*/, null));
				}
			}
		
		for (Map.Entry<String, FKTable> ent : fkTableMap.entrySet())
			nodeSet.add(new GraphNode(String.valueOf(ent.getValue().id), writeFKTable(ent.getKey(), ent.getValue().link, ent.getValue().columns), null, "shape=plaintext"));
		
		return (new Visualization(VisualizationSettings.GRAPH_QUERY_TYPE, name,
				graphBuilder.buildGraph(name, graphBuilder.getGraphType(VisualizationSettings.ERM_GRAPH_TYPE), graphStyle, null, null, null, nodeSet, edgeSet, null)));
		}
	
	private String writeTable(String name, TableDescription info, String link, SQLDialect dialect)
		{
		final StringBuilder label = new StringBuilder();
		final List<String> pkIndices = getKeyIndices(info);
		
		beginTable(label, link, name, true);
		int i = 0;
		for (ColumnDescription c : info.getColumns())
			{
			final boolean partOfPK = isPartOfPK(c.getName(), info);
			final String pkIndex = pkIndices.get(i);
			
			label.append("<tr>");
			
			label.append("<td align=\"left\" port=\"").append(c.getName()).append(IN_PORT_SUFFIX).append("\"");
			if (partOfPK)
				label.append("><font face=\"").append(graphStyle.getNormalFont()).append("\"><b>");
			else if (c.isNullable())
				label.append("><font face=\"").append(graphStyle.getNormalFont()).append("\"><i>");
			else
				label.append(">");
			label.append(c.getName());
			if (partOfPK)
				label.append("</b></font>");
			else if (c.isNullable())
				label.append("</i></font>");
			label.append("</td>");
			
			label.append("<td align=\"left\"");
			if (partOfPK)
				label.append("><font face=\"").append(graphStyle.getNormalFont()).append("\"><b>");
			else if (c.isNullable())
				label.append("><font face=\"").append(graphStyle.getNormalFont()).append("\"><i>");
			else
				label.append(">");
			if (StringUtils.empty(pkIndex))
				label.append(" ");
			else
				label.append(pkIndex);
			if (partOfPK)
				label.append("</b></font>");
			else if (c.isNullable())
				label.append("</i></font>");
			label.append("</td>");
			
			label.append("<td align=\"left\" port=\"").append(c.getName()).append(OUT_PORT_SUFFIX).append("\"");
			if (partOfPK)
				label.append("><font face=\"").append(graphStyle.getNormalFont()).append("\"><b>");
			else if (c.isNullable())
				label.append("><font face=\"").append(graphStyle.getNormalFont()).append("\"><i>");
			else
				label.append(">");
			label.append(dialect.dataTypeToString(c.getType()));
			if (partOfPK)
				label.append("</b></font>");
			else if (c.isNullable())
				label.append("</i></font>");
			label.append("</td>");
			
			label.append("</tr>");
			i++;
			}
		endTable(label);
		
		return (label.toString());
		}
	
	private boolean isPartOfPK(String column, TableDescription info)
		{
		return ((info.getPrimaryKey() != null) && info.getPrimaryKey().getColumns().contains(column));
		}
	
	private String writeFKTable(String name, String link, Map<String, Boolean> columns)
		{
		final StringBuilder label = new StringBuilder();
		beginTable(label, link, name, false);
		for (Map.Entry<String, Boolean> ent : columns.entrySet())
			{
			label.append("<tr><td align=\"left\" port=\"").append(ent.getKey()).append(ent.getValue() ? IN_PORT_SUFFIX : OUT_PORT_SUFFIX).append("\">");
			label.append(ent.getKey());
			label.append("</td></tr>");
			}
		label.append("<tr><td align=\"left\">...</td></tr>");
		endTable(label);
		
		return (label.toString());
		}
	
	private void beginTable(StringBuilder sb, String link, String plainName, boolean highlight)
		{
		sb.append("<table border=\"0\" cellborder=\"1\" cellspacing=\"0\"");
		if (highlight)
			sb.append(" bgcolor=\"").append(graphStyle.getHighlightBackgroundColor()).append("\" color=\"").append(graphStyle.getHighlightForegroundColor()).append("\"");
		if (link != null)
			sb.append(" href=\"").append(link).append("\"");
		sb.append(" tooltip=\"").append(plainName).append("\">");
		sb.append("<tr><td align=\"left\" cellpadding=\"8\"><font face=\"").append(graphStyle.getNormalFont()).append("\"><b><i>").append(plainName).append("</i></b></font></td></tr>");
		sb.append("<tr><td cellpadding=\"4\"><table border=\"0\" cellborder=\"0\" cellspacing=\"0\" cellpadding=\"4\">");
		}
	
	private void endTable(StringBuilder sb)
		{
		sb.append("</table></td></tr>");
		sb.append("</table>");
		}
	}
