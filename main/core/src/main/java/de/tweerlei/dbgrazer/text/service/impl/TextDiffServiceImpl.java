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
package de.tweerlei.dbgrazer.text.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.text.backend.DiffCalculator;
import de.tweerlei.dbgrazer.text.backend.DiffFormatter;
import de.tweerlei.dbgrazer.text.backend.Hunk;
import de.tweerlei.dbgrazer.text.backend.impl.SimpleDiffCalculator;
import de.tweerlei.dbgrazer.text.backend.impl.UnifiedDiffFormatter;
import de.tweerlei.dbgrazer.text.service.TextDiffService;
import de.tweerlei.spring.service.ModuleLookupService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class TextDiffServiceImpl implements TextDiffService, ConfigListener
	{
	private final ConfigService configService;
	private final ModuleLookupService moduleService;
	private final Logger logger;
	
	private DiffCalculator calculator;
	private DiffFormatter formatter;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param moduleService ModuleLookupService
	 */
	@Autowired
	public TextDiffServiceImpl(ConfigService configService, ModuleLookupService moduleService)
		{
		this.configService = configService;
		this.moduleService = moduleService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Register for config changes
	 */
	@PostConstruct
	public void init()
		{
		configService.addListener(this);
		configChanged();
		}
	
	@Override
	public void configChanged()
		{
		final String calcPrefix = configService.get(ConfigKeys.DIFF_ALGORITHM);
		
		logger.log(Level.INFO, "Using diff algorithm: " + calcPrefix);
		try	{
			calculator = moduleService.findModuleInstance(calcPrefix + "DiffCalculator", DiffCalculator.class);
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "findModuleInstance", e);
			calculator = new SimpleDiffCalculator();
			}
		
		final String fmtPrefix = configService.get(ConfigKeys.DIFF_FORMAT);
		
		logger.log(Level.INFO, "Using diff format: " + fmtPrefix);
		try	{
			formatter = moduleService.findModuleInstance(fmtPrefix + "DiffFormatter", DiffFormatter.class);
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "findModuleInstance", e);
			formatter = new UnifiedDiffFormatter();
			}
		}
	
	@Override
	public String diff(String lhs, String rhs, String lname, String rname, Date ldate, Date rdate)
		{
		final String[] lraw = StringUtils.split(lhs, "\\r?\\n");
		final String[] rraw = StringUtils.split(rhs, "\\r?\\n");
		
		// Lines used for diff calculation (trimmed to ignore indentation change)
		final List<String> lcmp = new ArrayList<String>(lraw.length);
		for (String s : lraw)
			lcmp.add(s.trim());
		final List<String> rcmp = new ArrayList<String>(rraw.length);
		for (String s : rraw)
			rcmp.add(s.trim());
		
		// Lines used for diff formatting
		final List<String> lfmt = Arrays.asList(lraw);
		final List<String> rfmt = Arrays.asList(rraw);
		
		final List<Hunk> diff = calculator.diff(lcmp,  rcmp);
		if (diff.isEmpty())
			return (null);
		
		final String result = formatter.formatDiff(lfmt, rfmt, diff, lname, rname, ldate, rdate);
		
		return (result);
		}
	}
