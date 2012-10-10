/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2012, Greatman <http://github.com/greatman/>
 *
 * Craftconomy3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Craftconomy3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Craftconomy3.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.greatmancode.craftconomy3.database.tables;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table("cc3_currency")
public class CurrencyTable {

	@Id
	private int id;

	@Field
	private String name;

	@Field
	private String plural;

	@Field
	private String minor;

	@Field
	private String minorplural;
	
	/**
	 * 1 = Default currency, 0 = Non Default
	 */
	@Field
	private boolean status;
	
	@Field
	private double hardCap;
	
	@Field
	private String sign;

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlural() {
		return plural;
	}

	public void setPlural(String plural) {
		this.plural = plural;
	}

	public String getMinor() {
		return minor;
	}

	public void setMinor(String minor) {
		this.minor = minor;
	}

	public String getMinorplural() {
		return minorplural;
	}

	public void setMinorplural(String minorplural) {
		this.minorplural = minorplural;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public double getHardCap() {
		return hardCap;
	}

	public void setHardCap(double hardCap) {
		this.hardCap = hardCap;
	}

}
