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
package de.tweerlei.dbgrazer.extension.wiki.parser;

import java.util.LinkedList;

/**
 * Consume XML tokens, counting tag levels
 * 
 * @author Robert Wruck
 */
public class CreoleConsumer
	{
	private static enum Tag
		{
		PARAGRAPH
			{
			public void start(CreoleHandler handler)
				{
				handler.startParagraph();
				}
			public void end(CreoleHandler handler)
				{
				handler.endParagraph();
				}
			},
		BOLD
			{
			public boolean isTextLevel()
				{
				return (true);
				}
			public void start(CreoleHandler handler)
				{
				handler.startBold();
				}
			public void end(CreoleHandler handler)
				{
				handler.endBold();
				}
			},
		ITALIC
			{
			public boolean isTextLevel()
				{
				return (true);
				}
			public void start(CreoleHandler handler)
				{
				handler.startItalic();
				}
			public void end(CreoleHandler handler)
				{
				handler.endItalic();
				}
			},
		CODE
			{
			public boolean isTextLevel()
				{
				return (true);
				}
			public void start(CreoleHandler handler)
				{
				handler.startCode();
				}
			public void end(CreoleHandler handler)
				{
				handler.endCode();
				}
			},
		ORDERED_LIST
			{
			public void start(CreoleHandler handler)
				{
				handler.startOrderedList();
				}
			public void end(CreoleHandler handler)
				{
				handler.endOrderedList();
				}
			},
		UNORDERED_LIST
			{
			public void start(CreoleHandler handler)
				{
				handler.startUnorderedList();
				}
			public void end(CreoleHandler handler)
				{
				handler.endUnorderedList();
				}
			},
		LIST_ITEM
			{
			public void start(CreoleHandler handler)
				{
				handler.startListItem();
				}
			public void end(CreoleHandler handler)
				{
				handler.endListItem();
				}
			},
		TABLE
			{
			public void start(CreoleHandler handler)
				{
				handler.startTable();
				}
			public void end(CreoleHandler handler)
				{
				handler.endTable();
				}
			},
		TABLE_ROW
			{
			public void start(CreoleHandler handler)
				{
				handler.startTableRow();
				}
			public void end(CreoleHandler handler)
				{
				handler.endTableRow();
				}
			},
		TABLE_HEADING
			{
			public void start(CreoleHandler handler)
				{
				handler.startTableHeading();
				}
			public void end(CreoleHandler handler)
				{
				handler.endTableHeading();
				}
			},
		TABLE_CELL
			{
			public void start(CreoleHandler handler)
				{
				handler.startTableCell();
				}
			public void end(CreoleHandler handler)
				{
				handler.endTableCell();
				}
			},
		CODE_BLOCK
			{
			public void start(CreoleHandler handler)
				{
				handler.startCodeBlock();
				}
			public void end(CreoleHandler handler)
				{
				handler.endCodeBlock();
				}
			};
		
		public boolean isTextLevel()
			{
			return (false);
			}
		public abstract void start(CreoleHandler handler);
		public abstract void end(CreoleHandler handler);
		}
	
	private final CreoleHandler handler;
	private final LinkedList<Tag> tagStack;
	private boolean expectValue;
	private boolean elementSeen;
	
	/**
	 * Constructor
	 * @param handler Tag handler
	 */
	public CreoleConsumer(CreoleHandler handler)
		{
		this.handler = handler;
		this.tagStack = new LinkedList<Tag>();
		this.expectValue = true;
		this.elementSeen = false;
		}
	
	private void push(Tag tag)
		{
		tag.start(handler);
		tagStack.push(tag);
		}
	
	private Tag pop()
		{
		final Tag tag = tagStack.pop();
		tag.end(handler);
		return (tag);
		}
	
	private Tag peek()
		{
		return (tagStack.peek());
		}
	
	private int depth()
		{
		return (tagStack.size());
		}
	
	private void startText()
		{
		if (tagStack.isEmpty())
			push(Tag.PARAGRAPH);
		}
	
	private void endText()
		{
		while (!tagStack.isEmpty())
			{
			if (tagStack.peek().isTextLevel())
				pop();
			else
				break;
			}
		}
	
	private void endAll()
		{
		while (!tagStack.isEmpty())
			pop();
		}
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @return this
	 */
	public CreoleConsumer append(String tag)
		{
		startText();
		handler.text(tag);
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @return this
	 */
	public CreoleConsumer toggleBold()
		{
		startText();
		if (peek() == Tag.BOLD)
			pop();
		else
			push(Tag.BOLD);
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @return this
	 */
	public CreoleConsumer toggleItalic()
		{
		startText();
		if (peek() == Tag.ITALIC)
			pop();
		else
			push(Tag.ITALIC);
		return (this);
		}
	
	public CreoleConsumer heading(int level, String text)
		{
		endAll();
		handler.heading(level, text);
		return (this);
		}
	
	public CreoleConsumer link(String url, String label)
		{
		startText();
		handler.link(url, label);
		return (this);
		}
	
	public CreoleConsumer newParagraph()
		{
		endAll();
		return (this);
		}
	
	public CreoleConsumer newLine()
		{
		startText();
		handler.newLine();
		return (this);
		}
	
	public CreoleConsumer rule()
		{
		endAll();
		handler.rule();
		return (this);
		}
	
	public CreoleConsumer image(String url, String label)
		{
		startText();
		handler.image(url, label);
		return (this);
		}
	
	public CreoleConsumer startTableRow()
		{
		endText();
		if (peek() == Tag.TABLE_ROW)
			{
			pop();
			push(Tag.TABLE_ROW);
			}
		else if (peek() == Tag.TABLE_HEADING || peek() == Tag.TABLE_CELL)
			{
			pop();
			pop();
			push(Tag.TABLE_ROW);
			}
		else
			{
			endAll();
			push(Tag.TABLE);
			push(Tag.TABLE_ROW);
			}
		return (this);
		}
	
	public CreoleConsumer startTableHeading()
		{
		endText();
		if (peek() == Tag.TABLE_HEADING || peek() == Tag.TABLE_CELL)
			pop();
		push(Tag.TABLE_HEADING);
		return (this);
		}
	
	public CreoleConsumer startTableCell()
		{
		endText();
		if (peek() == Tag.TABLE_HEADING || peek() == Tag.TABLE_CELL)
			pop();
		push(Tag.TABLE_CELL);
		return (this);
		}
	
	public CreoleConsumer startCode()
		{
		startText();
		push(Tag.CODE);
		return (this);
		}
	
	public CreoleConsumer endCode()
		{
		if (peek() == Tag.CODE)
			pop();
		return (this);
		}
	
	public CreoleConsumer startCodeblock()
		{
		endAll();
		push(Tag.CODE_BLOCK);
		return (this);
		}
	
	public CreoleConsumer endCodeblock()
		{
		if (peek() == Tag.CODE_BLOCK)
			pop();
		return (this);
		}
	
	public CreoleConsumer startOrderedListItem(int level)
		{
		endText();
		if (peek() == Tag.LIST_ITEM)
			{
			int d = depth() / 2;	// pairs of OL,LI
			if (level > d)
				push(Tag.ORDERED_LIST);
			else
				{
				while (level < d)
					{
					pop();
					pop();
					d--;
					}
				pop();
				}
			push(Tag.LIST_ITEM);
			}
		else
			{
			endAll();
			push(Tag.ORDERED_LIST);
			push(Tag.LIST_ITEM);
			}
		return (this);
		}
	
	
	public CreoleConsumer startUnorderedListItem(int level)
		{
		endText();
		if (peek() == Tag.LIST_ITEM)
			{
			int d = depth() / 2;	// pairs of OL,LI
			if (level > d)
				push(Tag.UNORDERED_LIST);
			else
				{
				while (level < d)
					{
					pop();
					pop();
					d--;
					}
				pop();
				}
			push(Tag.LIST_ITEM);
			}
		else
			{
			endAll();
			push(Tag.UNORDERED_LIST);
			push(Tag.LIST_ITEM);
			}
		return (this);
		}
	
	/**
	 * Finish processing
	 * @return this
	 */
	public CreoleConsumer finish()
		{
		endAll();
		return (this);
		}
	
	@Override
	public String toString()
		{
		return (handler.toString());
		}
	}
