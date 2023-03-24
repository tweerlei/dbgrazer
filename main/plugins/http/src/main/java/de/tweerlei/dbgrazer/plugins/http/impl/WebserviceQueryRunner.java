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
package de.tweerlei.dbgrazer.plugins.http.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.extension.http.HttpClientService;
import de.tweerlei.dbgrazer.plugins.http.types.HeadQueryType;
import de.tweerlei.dbgrazer.plugins.http.types.MultipartQueryType;
import de.tweerlei.dbgrazer.plugins.http.types.PostQueryType;
import de.tweerlei.dbgrazer.plugins.http.types.QueryTypeAttributes;
import de.tweerlei.dbgrazer.plugins.http.types.SOAPQueryType;
import de.tweerlei.dbgrazer.query.backend.BaseQueryRunner;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.service.ResultBuilderService;
import de.tweerlei.spring.http.HttpEntity;
import de.tweerlei.spring.http.HttpHeaders;
import de.tweerlei.spring.http.MimeType;
import de.tweerlei.spring.http.impl.MimeTypeBuilder;
import de.tweerlei.spring.http.impl.StringHttpEntity;
import de.tweerlei.spring.service.TimeService;

/**
 * Run webservice queries
 * 
 * @author Robert Wruck
 */
@Service
public class WebserviceQueryRunner extends BaseQueryRunner
	{
	private static final String MULTIPART_BOUNDARY = "--next-part\n";
	
	private static final String SOAPACTION_HEADER = "SOAPAction";
	private static final String SOAP_CONTENT_TYPE = "application/soap+xml";
	private static final String DEFAULT_CONTENT_TYPE = "text/xml";
	private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String CHARSET_PARAM = "charset";
	
	private static final String BODY_TAB = "$bodyTab";
	private static final String HEADER_TAB = "$headerTab";
	private static final String STATUS_HEADER = "Status";
	
	private final TimeService timeService;
	private final HttpClientService httpClient;
	private final ResultBuilderService resultBuilder;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param timeService TimeService
	 * @param httpClient HttpClientService
	 * @param resultBuilder ResultBuilderService
	 */
	@Autowired
	public WebserviceQueryRunner(TimeService timeService, HttpClientService httpClient,
			ResultBuilderService resultBuilder)
		{
		super("Webservice");
		this.timeService = timeService;
		this.httpClient = httpClient;
		this.resultBuilder = resultBuilder;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public boolean supports(QueryType t)
		{
		return (t.getLinkType() instanceof WebserviceLinkType);
		}
	
	@Override
	public Result performQuery(String link, Query query, int subQueryIndex, List<Object> params, TimeZone timeZone, int limit, CancelableProgressMonitor monitor) throws PerformQueryException
		{
		final Result res = new ResultImpl(query);
		
		final String endpoint = buildRequestURL(query, params);
		
		if (query.getType() instanceof PostQueryType)
			performPOST(res, link, endpoint, query, subQueryIndex, params);
		else if (query.getType() instanceof MultipartQueryType)
			performMultipart(res, link, endpoint, query, subQueryIndex, params);
		else if (query.getType() instanceof SOAPQueryType)
			performSOAP(res, link, endpoint, query, subQueryIndex, params);
		else
			performGET(res, link, endpoint, query, subQueryIndex, params);
		
		return (res);
		}
	
	private void performGET(Result res, String link, String url, Query query, int subQueryIndex, List<Object> params) throws PerformQueryException
		{
		try	{
			final HttpEntity request = parseEntity(buildFormQuery(query, params), null);
			
			final String getParams = new String(request.getRawContent(), request.getContentType().getParams().get(CHARSET_PARAM)).trim();
			final String getURL;
			if (getParams.startsWith("?"))
				{
				if (getParams.length() == 1)
					getURL = url;
				else
					getURL = url + getParams;
				}
			else
				getURL = url + "?" + getParams;
			
			final long start = timeService.getCurrentTime();
			final HttpEntity response = httpClient.get(link, getURL, request.getHeaders());
			final long end = timeService.getCurrentTime();
			
			res.getRowSets().put(BODY_TAB, createBodyRowSet(query, subQueryIndex, response.toString(), end - start, link));
			res.getRowSets().put(HEADER_TAB, createHeaderRowSet(query, subQueryIndex + 1, response.getStatus(), response.getHeaders(), end - start));
			}
		catch (IOException e)
			{
			logger.log(Level.SEVERE, "performGET", e);
			throw new PerformQueryException(query.getName(), new RuntimeException("performGET: " + e.getMessage(), e));
			}
		}
	
	private void performPOST(Result res, String link, String url, Query query, int subQueryIndex, List<Object> params) throws PerformQueryException
		{
		try	{
			final String mimeType = query.getAttributes().get(QueryTypeAttributes.ATTR_CONTENT_TYPE);
			final HttpEntity request;
			if (FORM_CONTENT_TYPE.equals(mimeType))
				request = parseEntity(buildFormQuery(query, params), mimeType);
			else
				request = parseEntity(buildXMLQuery(query, params), mimeType);
			
			final long start = timeService.getCurrentTime();
			final HttpEntity response = httpClient.post(link, url, request);
			final long end = timeService.getCurrentTime();
			
			res.getRowSets().put(BODY_TAB, createBodyRowSet(query, subQueryIndex, response.toString(), end - start, link));
			res.getRowSets().put(HEADER_TAB, createHeaderRowSet(query, subQueryIndex + 1, response.getStatus(), response.getHeaders(), end - start));
			}
		catch (IOException e)
			{
			logger.log(Level.SEVERE, "performPOST", e);
			throw new PerformQueryException(query.getName(), new RuntimeException("performPOST: " + e.getMessage(), e));
			}
		}
	
	private void performMultipart(Result res, String link, String url, Query query, int subQueryIndex, List<Object> params) throws PerformQueryException
		{
		try	{
			final List<HttpEntity> request = parseMultipart(buildXMLQuery(query, params));
			
			final long start = timeService.getCurrentTime();
			final HttpEntity response = httpClient.post(link, url, request);
			final long end = timeService.getCurrentTime();
			
			res.getRowSets().put(BODY_TAB, createBodyRowSet(query, subQueryIndex, response.toString(), end - start, link));
			res.getRowSets().put(HEADER_TAB, createHeaderRowSet(query, subQueryIndex + 1, response.getStatus(), response.getHeaders(), end - start));
			}
		catch (IOException e)
			{
			logger.log(Level.SEVERE, "performMultipart", e);
			throw new PerformQueryException(query.getName(), new RuntimeException("performMultipart: " + e.getMessage(), e));
			}
		}
	
	private List<HttpEntity> parseMultipart(String statement) throws IOException
		{
		final List<HttpEntity> ret = new ArrayList<HttpEntity>();
		
		// Split statement into parts
		for (String part : statement.split(MULTIPART_BOUNDARY))
			{
			// Empty part
			if (part.length() == 0)
				continue;
			
			final HttpEntity mimePart = parseEntity(part, null);
			
			ret.add(mimePart);
			}
		
		return (ret);
		}
	
	private HttpEntity parseEntity(String part, String fallbackContentType) throws IOException
		{
		final Map<String, String> headers = new HashMap<String, String>();
		final String[] lines = part.split("\n");
		final String body;
		if (lines.length == 1)
			body = part;
		else
			{
			final StringBuilder sb = new StringBuilder();
			boolean inBody = false;
			// Split part into lines
			for (String line : lines)
				{
				if (inBody)
					sb.append(line).append("\n");
				else if (line.length() == 0)
					inBody = true;
				else
					{
					// Split header
					final String[] header = line.split(":", 2);
					if (header.length == 2)
						headers.put(header[0].trim(), header[1].trim());
					}
				}
			body = sb.toString();
			}
		
		final String ct = headers.get(HttpHeaders.CONTENT_TYPE);
		final StringHttpEntity mimePart = new StringHttpEntity(getContentType(ct, fallbackContentType, DEFAULT_CONTENT_TYPE), body);
		for (Map.Entry<String, String> ent : headers.entrySet())
			mimePart.setHeader(ent.getKey(), ent.getValue());
		
		return (mimePart);
		}
	
	private void performSOAP(Result res, String link, String url, Query query, int subQueryIndex, List<Object> params) throws PerformQueryException
		{
		try	{
			final MimeType mimeType = getContentType(query.getAttributes().get(QueryTypeAttributes.ATTR_CONTENT_TYPE), SOAP_CONTENT_TYPE);
			final StringHttpEntity request = new StringHttpEntity(mimeType, createSOAPMessage(buildXMLQuery(query, params)));
			request.setHeader(SOAPACTION_HEADER, query.getAttributes().get(QueryTypeAttributes.ATTR_ACTION));
			
			final long start = timeService.getCurrentTime();
			final HttpEntity response = httpClient.post(link, url, request);
			final long end = timeService.getCurrentTime();
			
			// TODO: Remove SOAP envelope from response
			
			res.getRowSets().put(BODY_TAB, createBodyRowSet(query, subQueryIndex, response.toString(), end - start, link));
			res.getRowSets().put(HEADER_TAB, createHeaderRowSet(query, subQueryIndex + 1, response.getStatus(), response.getHeaders(), end - start));
			}
		catch (IOException e)
			{
			logger.log(Level.SEVERE, "performSOAP", e);
			throw new PerformQueryException(query.getName(), new RuntimeException("performSOAP: " + e.getMessage(), e));
			}
		}
	
	private String buildRequestURL(Query query, List<Object> params) throws PerformQueryException
		{
		try	{
			final String url = StringUtils.notNull(query.getAttributes().get(QueryTypeAttributes.ATTR_ENDPOINT));
			return (new FormParamReplacer(params).replaceAll(url));
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "buildRequestURL", e);
			throw new PerformQueryException(query.getName(), e);
			}
		}
	
	private String buildFormQuery(Query query, List<Object> params) throws PerformQueryException
		{
		try	{
			return (new FormParamReplacer(params).replaceAll(query.getStatement()));
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "buildFormQuery", e);
			throw new PerformQueryException(query.getName(), e);
			}
		}
	
	private String buildXMLQuery(Query query, List<Object> params) throws PerformQueryException
		{
		try	{
			return (new XMLParamReplacer(params).replaceAll(query.getStatement()));
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "buildFormQuery", e);
			throw new PerformQueryException(query.getName(), e);
			}
		}
	
	private String createSOAPMessage(String body)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
		sb.append("\t<SOAP:Header>\n");
		// insert header fields here...
		sb.append("\t</SOAP:Header>\n");
		sb.append("\t<SOAP:Body>\n");
		sb.append(body).append("\n");
		sb.append("\t</SOAP:Body>\n");
		sb.append("</SOAP:Envelope>");
		return (sb.toString());
		}
	
	private RowSet createBodyRowSet(Query query, int subQueryIndex, String body, long time, String link)
		{
		final RowSetImpl rs = resultBuilder.createSingletonRowSet(query, subQueryIndex, "body", body, time);
		
		rs.getAttributes().put(QueryTypeAttributes.ATTR_LINK, link);
		
		return (rs);
		}
	
	private RowSet createHeaderRowSet(Query query, int subQueryIndex, int status, Map<String, String> headers, long time)
		{
		final Query q = new QueryImpl(query.getName(), query.getSourceSchema(), query.getGroupName(), query.getStatement(), query.getStatementVariants(), new HeadQueryType(null), query.getParameters(), null, query.getAttributes());
		final RowSetImpl rs = resultBuilder.createMapRowSet(q, subQueryIndex, "name", "value", headers, time);
		rs.getRows().add(0, new DefaultResultRow(STATUS_HEADER, status));
		return (rs);
		}
	
	private MimeType getContentType(String... contentTypes)
		{
		for (String contentType : contentTypes)
			{
			try	{
				final MimeTypeBuilder mimeType = MimeTypeBuilder.parse(contentType);
				if (mimeType.getParam(CHARSET_PARAM) == null)
					mimeType.setParam(CHARSET_PARAM, DEFAULT_CHARSET);
				return (mimeType);
				}
			catch (RuntimeException e)
				{
				// continue
				}
			}
		
		throw new RuntimeException("No content type could be parsed");
		}
	}
