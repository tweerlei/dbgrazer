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
package de.tweerlei.common.util;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * \addtogroup misc Verschiedenes
 * @{
 */

/**
 * Routinen zur einfachen Konvertierung Date <-> String
 * 
 * @author Robert Wruck
 */
public final class DateUtils
	{
	/** Unerreichbarer Konstruktor */
	private DateUtils()
		{
		// s.o.
		}
	
	/**
	 * \name Häufig verwendete Datumsformate
	 * @{
	 */
	/** ISO 8601: JJJJ-MM-TT */
	public static final String DATE_ISO8601 = "yyyy-MM-dd";
	/** ISO 8601: HH:MM:SS */
	public static final String TIME_ISO8601 = "HH:mm:ss";
	/** ISO 8601: JJJJ-MM-TT HH:MM:SS */ 
	public static final String DATETIME_ISO8601 = "yyyy-MM-dd HH:mm:ss";
	/** ISO 8601: JJJJ-MM-TT HH:MM */ 
	public static final String DATETIME_ISO8601_NOSEC = "yyyy-MM-dd HH:mm";
	/** @} */
	
	/**
	 * Liefert den Abstand zu GMT in Millisekunden für das gegebene Datum
	 * @param d Datum, kann NULL sein
	 * @return Millisekunden
	 */
	public static int getGMTOffset(Date d)
		{
		final Calendar gc = Calendar.getInstance();
		if (d != null)
			gc.setTime(d);
		return (gc.get(Calendar.ZONE_OFFSET) + gc.get(Calendar.DST_OFFSET));
		}
	
	/**
	 * Erzeugt ein Datum aus einem formatierten String
	 * @param s Der String
	 * @param fmt Das Eingabeformat (siehe java.text.SimpleDateFormat)
	 * @param tz TimeZone
	 * @return Date oder null
	 * @throws ParseException, wenn der String nicht umgewandelt werden konnte
	 */
	public static final Date parseDate(String s, String fmt, TimeZone tz) throws ParseException
		{
		if (s == null)
			return (null);
		final DateFormat df = new SimpleDateFormat(fmt);
		df.setTimeZone(tz);
		final ParsePosition pos = new ParsePosition(0);
		final Date d = df.parse(s, pos);
		if (pos.getErrorIndex() >= 0)
			throw new ParseException("Ungültiges Datum: " + s, pos.getErrorIndex());
		return (d);
		}
	
	/**
	 * Erzeugt ein Datum aus einem formatierten String
	 * @param s Der String
	 * @param fmt Das Eingabeformat (siehe java.text.SimpleDateFormat)
	 * @return Date oder null
	 * @throws ParseException, wenn der String nicht umgewandelt werden konnte
	 */
	public static final Date parseDate(String s, String fmt) throws ParseException
		{
		return (parseDate(s, fmt, TimeZone.getDefault()));
		}
	
	/**
	 * Erzeugt einen formatierten String aus dem gegebenen Datum
	 * @param d Das Datum oder null
	 * @param fmt Das Ausgabeformat (siehe java.text.SimpleDateFormat)
	 * @param tz TimeZone
	 * @return String
	 */
	public static final String asString(Date d, String fmt, TimeZone tz)
		{
		if ((d == null) || (fmt == null))
			return (null);
		final DateFormat df = new SimpleDateFormat(fmt);
		df.setTimeZone(tz);
		final StringBuffer sb = new StringBuffer();
		final FieldPosition fp = new FieldPosition(0);
		df.format(d, sb, fp);
		return (sb.toString());
		}
	
	/**
	 * Erzeugt einen formatierten String aus dem gegebenen Datum
	 * @param d Das Datum oder null
	 * @param fmt Das Ausgabeformat (siehe java.text.SimpleDateFormat)
	 * @return String
	 */
	public static final String asString(Date d, String fmt)
		{
		return (asString(d, fmt, TimeZone.getDefault()));
		}
	
	/**
	 * Konvertiert das Argument in ein java.sql.Date
	 * @param d Date
	 * @return java.sql.Date
	 */
	public static final java.sql.Date toSqlDate(java.util.Date d)
		{
		if (d == null)
			return (null);
		return (new java.sql.Date(d.getTime()));
		}
	
	/**
	 * Addiert die angegebene Anzahl an Stunden auf ein Datum
	 * @param d Das Datum
	 * @param i Anzahl der Stunden
	 * @return Date
	 */
	public static final Date addHours(Date d, int i)
		{
		if (d == null)
			return (null);
		final Calendar gc = Calendar.getInstance();
		gc.setTime(d);
		gc.add(Calendar.HOUR_OF_DAY, i);
		return (gc.getTime());
		}
	
	/**
	 * Addiert die angegebene Anzahl an Tagen auf ein Datum
	 * @param d Das Datum
	 * @param i Anzahl der Tage
	 * @return Date
	 */
	public static final Date addDays(Date d, int i)
		{
		if (d == null)
			return (null);
		final Calendar gc = Calendar.getInstance();
		gc.setTime(d);
		gc.add(Calendar.DAY_OF_YEAR, i);
		return (gc.getTime());
		}
	
	/**
	 * Addiert die angegebene Anzahl an Monaten auf ein Datum
	 * @param d Das Datum
	 * @param i Anzahl der Monate
	 * @return Date
	 */
	public static final Date addMonths(Date d, int i)
		{
		if (d == null)
			return (null);
		final Calendar gc = Calendar.getInstance();
		gc.setTime(d);
		gc.add(Calendar.MONTH, i);
		return (gc.getTime());
		}
	
	/**
	 * Addiert die angegebene Anzahl an Wochen auf ein Datum
	 * @param d Das Datum
	 * @param i Anzahl der Wochen
	 * @return Date
	 */
	public static final Date addWeeks(Date d, int i)
		{
		if (d == null)
			return (null);
		final Calendar gc = Calendar.getInstance();
		gc.setTime(d);
		gc.add(Calendar.WEEK_OF_YEAR, i);
		return (gc.getTime());
		}
	
	/**
	 * Addiert die angegebene Anzahl an Werktagen auf ein Datum
	 * @param d Das Datum
	 * @param i Anzahl der Werktage
	 * @return Date
	 */
	public static final Date addWorkDays(Date d, int i)
		{
		if (d == null)
			return (null);
		
		final Calendar gc = Calendar.getInstance();
		gc.setTime(d);
		
		int wochen;
		int tage;
		if (i < 0)
			{
			wochen = i / 5;
			tage = i % 5;
			
			gc.add(Calendar.WEEK_OF_YEAR, wochen);
			while (tage < 0)
				{
				switch (gc.get(Calendar.DAY_OF_WEEK))
					{
					case Calendar.MONDAY:
						gc.add(Calendar.DAY_OF_YEAR, -3);
						break;
					case Calendar.SUNDAY:
						gc.add(Calendar.DAY_OF_YEAR, -2);
						break;
					default:
						gc.add(Calendar.DAY_OF_YEAR, -1);
						break;
					}
				tage++;
				}
			}
		else
			{
			wochen = i / 5;
			tage = i % 5;
			
			gc.add(Calendar.WEEK_OF_YEAR, wochen);
			while (tage > 0)
				{
				switch (gc.get(Calendar.DAY_OF_WEEK))
					{
					case Calendar.FRIDAY:
						gc.add(Calendar.DAY_OF_YEAR, 3);
						break;
					case Calendar.SATURDAY:
						gc.add(Calendar.DAY_OF_YEAR, 2);
						break;
					default:
						gc.add(Calendar.DAY_OF_YEAR, 1);
						break;
					}
				tage--;
				}
			}
		
		return (gc.getTime());
		}
	
	/**
	 * Ermittelt die Differenz zweier Daten in vollen Stunden.
	 * Vorsicht! Ein Tag hat nicht immer 24 Stunden! (=> Sommer-/Winterzeit)
	 * @param d1 Erstes Datum
	 * @param d2 Zweites Datum
	 * @return Die Differenz in Stunden (> 0 wenn d2 > d1)
	 */
	public static final int differenceHours(Date d1, Date d2)
		{
		final Calendar gc1 = Calendar.getInstance();
		final Calendar gc2 = Calendar.getInstance();
		
		gc1.setTime(d1);
		gc2.setTime(d2);
		
		return ((int) ((d2.getTime() - d1.getTime()) / (60L * 60L * 1000L)));
		}
	
	/**
	 * Ermittelt die Differenz zweier Daten in vollen Tagen
	 * Vorsicht! Ein Tag hat nicht immer 24 Stunden! (=> Sommer-/Winterzeit)
	 * @param d1 Erstes Datum
	 * @param d2 Zweites Datum
	 * @return Die Differenz in Tagen (> 0, wenn d2 > d1)
	 */
	public static final int differenceDays(Date d1, Date d2)
		{
		final Calendar gc1 = Calendar.getInstance();
		final Calendar gc2 = Calendar.getInstance();
		
		gc1.setTime(d1);
		gc2.setTime(d2);
		
		// Auch dann als vollen Tag zählen, wenn weniger als 24 Stunden
		long z1 = gc1.get(Calendar.DST_OFFSET);
		long z2 = gc2.get(Calendar.DST_OFFSET);
		
		return ((int) (((d2.getTime() + z2) - (d1.getTime() + z1)) / (24L * 60L * 60L * 1000L)));
		}
	
	/**
	 * Setzt die Zeitkomponente des Datums auf 00:00:00
	 * @param d Date
	 * @return Date ohne Zeitanteil
	 */
	public static final Date clearTime(Date d)
		{
		if (d == null)
			return (null);
		
		final Calendar gc = Calendar.getInstance();
		gc.setTime(d);
		
		gc.set(Calendar.HOUR_OF_DAY, 0);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
		
		return (gc.getTime());
		}
	
	/**
	 * Construct a date from components
	 * @param year Year
	 * @param month Month (january is 1)
	 * @param day Day of month, starting with 1
	 * @return Date
	 */
	public static final Date getDate(int year, int month, int day)
		{
		return (getDate(year, month, day, 0, 0, 0, 0, null));
		}
	
	/**
	 * Construct a date from components
	 * @param year Year
	 * @param month Month (january is 1)
	 * @param day Day of month, starting with 1
	 * @param hour Hour (0 - 24)
	 * @param minute Minute (0 - 59)
	 * @param second Second (0 - 59)
	 * @return Date
	 */
	public static final Date getDate(int year, int month, int day, int hour, int minute, int second)
		{
		return (getDate(year, month, day, hour, minute, second, 0, null));
		}
	
	/**
	 * Construct a date from components
	 * @param year Year
	 * @param month Month (january is 1)
	 * @param day Day of month, starting with 1
	 * @param hour Hour (0 - 24)
	 * @param minute Minute (0 - 59)
	 * @param second Second (0 - 59)
	 * @param millis Millisecond
	 * @param tz TimeZone (null to use default)
	 * @return Date
	 */
	public static final Date getDate(int year, int month, int day, int hour, int minute, int second, int millis, TimeZone tz)
		{
		final Calendar gc = Calendar.getInstance();
		if (tz != null)
			gc.setTimeZone(tz);
		
		gc.set(Calendar.YEAR, year);
		gc.set(Calendar.MONTH, month - 1 + Calendar.JANUARY);
		gc.set(Calendar.DAY_OF_MONTH, day);
		gc.set(Calendar.HOUR_OF_DAY, hour);
		gc.set(Calendar.MINUTE, minute);
		gc.set(Calendar.SECOND, second);
		gc.set(Calendar.MILLISECOND, millis);
		
		return (gc.getTime());
		}
	
	/**
	 * Ermittelt die Differenz zweier Daten in der angegebenen Einheit
	 * d1: kleineres Datum
	 * d2: größeres Datum
	 * field: Einheit (siehe java.util.Calendar)
	 */
/*
	public static final long difference(Date d1, Date d2, int field)
		{
		long ret = 0;
		GregorianCalendar gc1 = new GregorianCalendar();
		GregorianCalendar gc2 = new GregorianCalendar();
		
		gc1.setTime(d1);
		gc2.setTime(d2);
		
		switch (field)
			{
			case Calendar.YEAR:
				ret = gc2.get(Calendar.YEAR) - gc1.get(Calendar.YEAR);
				break;
			
			case Calendar.MILLISECOND:
				ret = d2.getTime() - d1.getTime();
				break;
			
			case Calendar.SECOND:
				ret = (d2.getTime() - d1.getTime()) / 1000L;
				break;
			
			case Calendar.MINUTE:
				ret = (d2.getTime() - d1.getTime()) / (60L * 1000L);
				break;
			
			case Calendar.HOUR:
			case Calendar.HOUR_OF_DAY:
				ret = (d2.getTime() - d1.getTime()) / (60L * 60L * 1000L);
				break;
			
			// Vorsicht! Ein Tag hat nicht immer 24 Stunden! (=> Sommer-/Winterzeit)
			case Calendar.DAY_OF_MONTH:
			case Calendar.DAY_OF_WEEK:
			case Calendar.DAY_OF_WEEK_IN_MONTH:
				{
				long z1 = gc1.get(Calendar.DST_OFFSET);
				long z2 = gc2.get(Calendar.DST_OFFSET);
				
				ret = ((d2.getTime() - z2) - (d1.getTime() - z1)) / (24L * 60L * 60L * 1000L);
				}
				break;
// Ab hier wird's richtig knifflig... lassen wir's erstmal
			case Calendar.WEEK_OF_MONTH:
			case Calendar.WEEK_OF_YEAR:
				{
				}
				break;
			
			case Calendar.MONTH:
				{
				}
				break;
			
			default:
				throw new IllegalArgumentException("Field not defined: " + field);
			}
		
		return (ret);
		}
*/
	}

/** @} */
