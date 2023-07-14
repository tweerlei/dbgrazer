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
package de.tweerlei.dbgrazer.web.controller.kubernetes;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.extension.kubernetes.KubernetesApiService;
import de.tweerlei.dbgrazer.text.service.TextTransformerService;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.FrontendNotificationService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class KubernetesEditController
	{
	private final KubernetesApiService clientService;
	private final TextTransformerService textFormatterService;
	private final FrontendNotificationService frontendNotificationService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param clientService KubernetesApiService
	 * @param textFormatterService TextFormatterService
	 * @param frontendNotificationService FrontendNotificationService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public KubernetesEditController(KubernetesApiService clientService, TextTransformerService textFormatterService,
			FrontendNotificationService frontendNotificationService,
			ConnectionSettings connectionSettings)
		{
		this.clientService = clientService;
		this.textFormatterService = textFormatterService;
		this.frontendNotificationService = frontendNotificationService;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show the file browser
	 * @param namespace Namespace name
	 * @param kind Kind
	 * @param name Object name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/kube-delete.html", method = RequestMethod.GET)
	public String deleteApiObject(
			@RequestParam("namespace") String namespace,
			@RequestParam("kind") String kind,
			@RequestParam("name") String name
			)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final ObjectKind parts = ObjectKind.parse(kind);
		
		final String result = clientService.deleteApiObject(connectionSettings.getLinkName(), namespace, parts.group, parts.version, parts.kind, name);
		
		frontendNotificationService.logError("resultMessage", result);
		
		return ("redirect:apiobjects.html?namespace="+namespace+"&kind="+kind);
		}
	
	/**
	 * Show the file browser
	 * @param namespace Namespace name
	 * @param kind Kind
	 * @param name Object name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/kube-apply.html", method = RequestMethod.GET)
	public Map<String, Object> showApiObject(
			@RequestParam("namespace") String namespace,
			@RequestParam("kind") String kind,
			@RequestParam(value = "name", required = false) String name
			)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("namespace", namespace);
		model.put("kind", kind);
		model.put("name", name);
		
		final String txt;
		if (!StringUtils.empty(name))
			{
			final ObjectKind parts = ObjectKind.parse(kind);
			
			final String json = clientService.getApiObject(connectionSettings.getLinkName(), namespace, parts.group, parts.version, parts.kind, name);
			
			if (json == null)
				txt = null;
			else
				{
				final Set<TextTransformerService.Option> options = EnumSet.of(TextTransformerService.Option.FORMATTING);
				txt = textFormatterService.format(json, "JSON", options);
				}
			}
		else
			txt = null;
		
		model.put("content", txt);
		model.put("modes", Arrays.asList("create", "replace", "patch"));
		model.put("mode", (txt == null) ? "create" : "replace");
		model.put("extensionJS", KubernetesMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param namespace Namespace name
	 * @param kind Kind
	 * @param name Object name
	 * @param content Object content as JSON
	 * @param mode Apply mode
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/kube-apply.html", method = RequestMethod.POST)
	public Map<String, Object> applyApiObject(
			@RequestParam("namespace") String namespace,
			@RequestParam("kind") String kind,
			@RequestParam("name") String name,
			@RequestParam("content") String content,
			@RequestParam("mode") String mode
			)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("namespace", namespace);
		model.put("kind", kind);
		model.put("name", name);
		
		final ObjectKind parts = ObjectKind.parse(kind);
		
		final String result;
		if (!StringUtils.empty(name) && mode.equals("replace"))
			result = clientService.replaceApiObject(connectionSettings.getLinkName(), namespace, parts.group, parts.version, parts.kind, name, content);
		else if (!StringUtils.empty(name) && mode.equals("patch"))
			result = clientService.patchApiObject(connectionSettings.getLinkName(), namespace, parts.group, parts.version, parts.kind, name, content);
		else
			result = clientService.createApiObject(connectionSettings.getLinkName(), namespace, parts.group, parts.version, parts.kind, content);
		
		model.put("result", result);
		model.put("extensionJS", KubernetesMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	}
