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
package de.tweerlei.common5.jdbc.model;

/**
 * Privilege description
 * 
 * @author Robert Wruck
 */
public class PrivilegeDescription
	{
	private final String grantor;
	private final String grantee;
	private final String privilege;
	private final boolean grantable;
	
	/**
	 * Constructor
	 * @param grantor Grantor name
	 * @param grantee Grantee name
	 * @param privilege Privilege name
	 * @param grantable Grantable by grantee
	 */
	public PrivilegeDescription(String grantor, String grantee, String privilege, boolean grantable)
		{
		this.grantor = grantor;
		this.grantee = grantee;
		this.privilege = privilege;
		this.grantable = grantable;
		}

	/**
	 * Get the grantor
	 * @return the grantor
	 */
	public String getGrantor()
		{
		return grantor;
		}

	/**
	 * Get the grantee
	 * @return the grantee
	 */
	public String getGrantee()
		{
		return grantee;
		}

	/**
	 * Get the privilege
	 * @return the privilege
	 */
	public String getPrivilege()
		{
		return privilege;
		}

	/**
	 * Get the grantable
	 * @return the grantable
	 */
	public boolean isGrantable()
		{
		return grantable;
		}
	
	/**
	 * Accept a visitor
	 * @param v Visitor
	 */
	public void accept(TableVisitor v)
		{
		v.visitPrivilege(this);
		}
	
	@Override
	public int hashCode()
		{
		final int prime = 31;
		int result = 1;
		result = prime * result + (grantable ? 1231 : 1237);
		result = prime * result + ((grantee == null) ? 0 : grantee.hashCode());
		result = prime * result + ((privilege == null) ? 0 : privilege.hashCode());
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
		PrivilegeDescription other = (PrivilegeDescription) obj;
		if (grantable != other.grantable)
			return false;
		if (grantee == null)
			{
			if (other.grantee != null)
				return false;
			}
		else if (!grantee.equals(other.grantee))
			return false;
		if (privilege == null)
			{
			if (other.privilege != null)
				return false;
			}
		else if (!privilege.equals(other.privilege))
			return false;
		return true;
		}
	}
