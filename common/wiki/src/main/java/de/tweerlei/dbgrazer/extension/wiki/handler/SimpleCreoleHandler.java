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
package de.tweerlei.dbgrazer.extension.wiki.handler;

import java.io.IOException;

import de.tweerlei.dbgrazer.extension.wiki.parser.CreoleHandler;

/**
 * Concatenate XML tokens
 * 
 * @author Robert Wruck
 */
public class SimpleCreoleHandler implements CreoleHandler
	{
	private final Appendable sb;
	
	/**
	 * Constructor
	 */
	public SimpleCreoleHandler()
		{
		this(new StringBuilder());
		}
	
	/**
	 * Constructor
	 * @param a Appendable to receive formatted output
	 */
	public SimpleCreoleHandler(Appendable a)
		{
		this.sb = a;
		}
	
	@Override
	public void text(String text)
		{
		try	{
			sb.append(textEncode(text));
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startBold()
		{
		try	{
			sb.append("<strong>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endBold()
		{
		try	{
			sb.append("</strong>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startItalic()
		{
		try	{
			sb.append("<em>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endItalic()
		{
		try	{
			sb.append("</em>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void heading(int level, String text)
		{
		try	{
			sb.append("<h").append(String.valueOf(level)).append(">");
			sb.append(textEncode(text));
			sb.append("</h").append(String.valueOf(level)).append(">\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void link(String url, String label)
		{
		try	{
			if (label.length() == 0)
				sb.append("<a href=\"").append(attrEncode(url)).append("\">").append(textEncode(url)).append("</a>");
			else
				sb.append("<a href=\"").append(attrEncode(url)).append("\">").append(textEncode(label)).append("</a>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startParagraph()
		{
		try	{
			sb.append("<p>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endParagraph()
		{
		try	{
			sb.append("</p>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void newLine()
		{
		try	{
			sb.append("<br/>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startUnorderedList()
		{
		try	{
			sb.append("<ul>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endUnorderedList()
		{
		try	{
			sb.append("</ul>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startOrderedList()
		{
		try	{
			sb.append("<ol>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endOrderedList()
		{
		try	{
			sb.append("</ol>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startListItem()
		{
		try	{
			sb.append("<li>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endListItem()
		{
		try	{
			sb.append("</li>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void rule()
		{
		try	{
			sb.append("<hr/>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void image(String url, String label)
		{
		try	{
			sb.append("<img src=\"").append(attrEncode(url)).append("\" alt=\"").append(textEncode(label)).append("\"/>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startTable()
		{
		try	{
			sb.append("<table>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endTable()
		{
		try	{
			sb.append("</table>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startTableRow()
		{
		try	{
			sb.append("<tr>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endTableRow()
		{
		try	{
			sb.append("</tr>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startTableHeading()
		{
		try	{
			sb.append("<th>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endTableHeading()
		{
		try	{
			sb.append("</th>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startTableCell()
		{
		try	{
			sb.append("<td>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endTableCell()
		{
		try	{
			sb.append("</td>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startCode()
		{
		try	{
			sb.append("<code>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endCode()
		{
		try	{
			sb.append("</code>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startCodeBlock()
		{
		try	{
			sb.append("<pre>");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endCodeBlock()
		{
		try	{
			sb.append("</pre>\n");
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	private String textEncode(String s)
		{
		return (s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
		}
	
	private String attrEncode(String s)
		{
		return (s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;"));
		}
	
	@Override
	public String toString()
		{
		return (sb.toString());
		}
	}
