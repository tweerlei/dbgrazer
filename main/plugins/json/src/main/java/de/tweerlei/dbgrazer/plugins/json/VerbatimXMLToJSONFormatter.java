package de.tweerlei.dbgrazer.plugins.json;

import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.extension.json.handler.SimpleJSONHandler;
import de.tweerlei.dbgrazer.extension.json.handler.XMLToJSONHandler;
import de.tweerlei.dbgrazer.extension.json.parser.JSONHandler;
import de.tweerlei.dbgrazer.extension.xml.parser.XMLParser;
import de.tweerlei.dbgrazer.text.backend.BaseTextFormatter;

/**
 * Format text
 * 
 * @author Robert Wruck <wruck@tweerlei.de>
 */
@Service
public class VerbatimXMLToJSONFormatter extends BaseTextFormatter
	{
	/**
	 * Constructor
	 */
	public VerbatimXMLToJSONFormatter()
		{
		super("XMLJSON");
		}
	
	@Override
	public String format(String value)
		{
		final JSONHandler h = new SimpleJSONHandler();
		new XMLParser(new XMLToJSONHandler(h)).parse(value);
		return (h.toString());
		}
	
	@Override
	public boolean isXMLEncoded()
		{
		return (false);
		}
	}
