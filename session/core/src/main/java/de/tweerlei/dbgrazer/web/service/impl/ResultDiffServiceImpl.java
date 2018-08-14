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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.util.ObjectUtils;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.text.service.TextDiffService;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.CompareHandler;
import de.tweerlei.dbgrazer.web.model.CompareProgressMonitor;
import de.tweerlei.dbgrazer.web.model.DiffCompareHandler;
import de.tweerlei.dbgrazer.web.model.TaskCompareProgressMonitor;
import de.tweerlei.dbgrazer.web.service.ResultDiffService;
import de.tweerlei.dbgrazer.web.service.ResultTransformerService;
import de.tweerlei.dbgrazer.web.service.TextTransformerService;

/**
 * Transform query results
 *
 * @author Robert Wruck
 */
@Service
public class ResultDiffServiceImpl implements ResultDiffService
	{
	private static enum MatchResult
		{
		NOT_MATCHED,
		PK_MATCHED,
		ALL_MATCHED
		}
	
	private static final String DIFF_FORMAT = "Diff";
	
	private final ResultTransformerService resultTransformerService;
	private final TextTransformerService textFormatterService;
	private final TextDiffService textDiffService;
	
	/**
	 * Constructor
	 * @param resultTransformerService ResultTransformerService
	 * @param textFormatterService TextFormatterService
	 * @param textDiffService TextDiffService
	 */
	@Autowired
	public ResultDiffServiceImpl(ResultTransformerService resultTransformerService,
			TextTransformerService textFormatterService, TextDiffService textDiffService)
		{
		this.resultTransformerService = resultTransformerService;
		this.textFormatterService = textFormatterService;
		this.textDiffService = textDiffService;
		}
	
	@Override
	public RowSet diffTextResults(RowSet l, RowSet r, String ln, String rn, DataFormatter fmt)
		{
		final List<ColumnDef> effectiveColumns = l.getColumns().isEmpty() ? r.getColumns() : l.getColumns();
		final RowSetImpl rs = new RowSetImpl(l.getQuery(), l.getSubQueryIndex(), effectiveColumns);
		rs.setMoreAvailable(l.isMoreAvailable());
		rs.setQueryTime(l.getQueryTime());
		
		final String lhs = resultTransformerService.convertToString(l, fmt);
		final String rhs = resultTransformerService.convertToString(r, fmt);
		
		final String result = textDiffService.diff(lhs, rhs, ln, rn, null, null);
		if (result != null)
			{
			final String formatted = textFormatterService.format(result, DIFF_FORMAT, EnumSet.of(TextTransformerService.Option.LINE_NUMBERS));
			rs.getRows().add(new DefaultResultRow(formatted));
			}
		
		return (rs);
		}
	
	@Override
	public RowSet diffResults(RowSet l, RowSet r, DataFormatter fmt, boolean trim)
		{
		final DiffCompareHandler h = new DiffCompareHandler(l.getColumns().isEmpty() ? r : l, new TranslateVisitor(fmt, trim, false));
		final TaskCompareProgressMonitor c = new TaskCompareProgressMonitor();
		
		compareResults(l, r, h, c, null, Collections.<Integer>emptySet());
		
		return (h.getRowSet());
		}
	
	@Override
	public void compareResults(RowSet l, RowSet r, CompareHandler h, CompareProgressMonitor monitor, String tableName, Set<Integer> pk)
		{
		monitor.getSourceRows().progress(l.getRows().size());
		monitor.getDestinationRows().progress(r.getRows().size());
		
		for (Iterator<ResultRow> i = l.getRows().iterator(); i.hasNext(); )
			{
			final ResultRow ll = i.next();
			boolean found = false;
			for (Iterator<ResultRow> j = r.getRows().iterator(); !found && j.hasNext(); )
				{
				final ResultRow rl = j.next();
				switch (matchRows(ll.getValues(), rl.getValues(), pk))
					{
					case PK_MATCHED:
						h.rowChanged(tableName, l.getColumns(), ll, rl, pk);
						monitor.getUpdatedRows().progress(1);
						i.remove();
						j.remove();
						found = true;
						break;
					
					case ALL_MATCHED:
						monitor.getMatchedRows().progress(1);
						i.remove();
						j.remove();
						found = true;
						break;
					
					case NOT_MATCHED:
						break;
					}
				}
			if (!found)
				{
				h.rowRemoved(tableName, l.getColumns(), ll, pk);
				monitor.getDeletedRows().progress(1);
				}
			}
		
		// Remaining rows in r have to be added
		for (ResultRow rl : r.getRows())
			{
			h.rowAdded(tableName, r.getColumns(), rl, pk);
			monitor.getInsertedRows().progress(1);
			}
		
		h.flush();
		}
	
	private MatchResult matchRows(List<Object> a, List<Object> b, Set<Integer> pk)
		{
		final MatchResult ret;
		if (!pk.isEmpty())
			{
			for (Integer i : pk)
				{
				if (!ObjectUtils.equals(a.get(i), b.get(i)))
					return (MatchResult.NOT_MATCHED);
				}
			ret = MatchResult.PK_MATCHED;
			}
		else
			ret = MatchResult.NOT_MATCHED;
		
		final Iterator<Object> ia = a.iterator();
		final Iterator<Object> ib = b.iterator();
		while (ia.hasNext() && ib.hasNext())
			{
			if (!ObjectUtils.equals(ia.next(), ib.next()))
				return (ret);
			}
		
		return (MatchResult.ALL_MATCHED);
		}
	}
