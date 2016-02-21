/**
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
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
package com.greatmancode.craftconomy3.converter;

import com.greatmancode.craftconomy3.converter.converters.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the list of loaded converters.
 *
 * @author greatman
 */
public class ConverterList {
    /**
     * Contains the list of the loaded converters.
     */
    private final Map<String, Converter> converterList = new HashMap<>();

    public Map<String, Converter> getConverterList() {
        return converterList;
    }

    /**
     * Load the converters.
     */
    public ConverterList() {
        converterList.put("iconomy6", new Iconomy6());
        converterList.put("boseeconomy", new BoseEconomy());
        converterList.put("essentials", new Essentials());
        converterList.put("feconomy", new Feconomy());
        converterList.put("mineconomy", new Mineconomy());

    }
}
