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
package de.tweerlei.dbgrazer.visualization.model;

/**
 * SVG shape
  * 
 * @author Robert Wruck
*/
public class SVGShape
	{
	private final String name;
	private final double c1;
	private final double c2;
	private final double c3;
	private final double c4;
	private final String link;
	private final String attrs;
	
	/**
	 * Constructor
	 * @param name Node text
	 * @param c1 First coordinate
	 * @param c2 Second coordinate
	 * @param c3 Third coordinate
	 * @param c4 Fourth coordinate
	 * @param link URL parameter
	 * @param attrs Node attributes
	 */
	public SVGShape(String name, double c1, double c2, double c3, double c4, String link, String attrs)
		{
		this.name = name;
		this.c1 = c1;
		this.c2 = c2;
		this.c3 = c3;
		this.c4 = c4;
		this.link = link;
		this.attrs = attrs;
		}
	
	/**
	 * @return the name
	 */
	public String getName()
		{
		return name;
		}

	/**
	 * @return the c1
	 */
	public double getCoordinate1()
		{
		return c1;
		}
	
	/**
	 * @return the c2
	 */
	public double getCoordinate2()
		{
		return c2;
		}
	
	/**
	 * @return the c3
	 */
	public double getCoordinate3()
		{
		return c3;
		}
	
	/**
	 * @return the c4
	 */
	public double getCoordinate4()
		{
		return c4;
		}
	
	/**
	 * @return the link
	 */
	public String getLink()
		{
		return link;
		}

	/**
	 * Get the attrs
	 * @return the attrs
	 */
	public String getAttrs()
		{
		return attrs;
		}
	
	@Override
	public int hashCode()
		{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(c1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(c2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(c3);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(c4);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
		}
	
	@Override
	public boolean equals(Object obj)
		{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SVGShape other = (SVGShape) obj;
		if (Double.doubleToLongBits(c1) != Double.doubleToLongBits(other.c1))
			return false;
		if (Double.doubleToLongBits(c2) != Double.doubleToLongBits(other.c2))
			return false;
		if (Double.doubleToLongBits(c3) != Double.doubleToLongBits(other.c3))
			return false;
		if (Double.doubleToLongBits(c4) != Double.doubleToLongBits(other.c4))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
		}
	}
