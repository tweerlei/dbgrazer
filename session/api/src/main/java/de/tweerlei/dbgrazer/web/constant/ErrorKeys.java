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
package de.tweerlei.dbgrazer.web.constant;

/**
 * Keys for localized messages in messages.properties
 * 
 * @author Robert Wruck
 */
public final class ErrorKeys
	{
	/** Write failed */
	public static final String WRITE_FAILED = "error_writeFailed";
	/** Object with same name exists */
	public static final String EXISTS = "error_exists";
	/** Password mismatch */
	public static final String PASSWORD_MISMATCH = "error_passwordMismatch";
	
	/** Too few columns */
	public static final String TOO_FEW_COLUMNS = "error_tooFewColumns";
	/** Unsupported data types */
	public static final String UNSUPPORTED_DATA_TYPES = "error_unsupportedDataTypes";
	/** Data types mismatch */
	public static final String DATA_TYPES_MISMATCH = "error_dataTypesMismatch";
	/** Data conversion error */
	public static final String DATA_CONVERSION_ERROR = "error_dataConversionError";
	/** Range conversion error */
	public static final String RANGE_CONVERSION_ERROR = "error_rangeConversionError";
	/** Table not found */
	public static final String TABLE_NOT_FOUND = "error_tableNotFound";
	
	private ErrorKeys()
		{
		}
	}
