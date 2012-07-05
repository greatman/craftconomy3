/*
 * This file is part of SimpleSave
 *
 * SimpleSave is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SimpleSave is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.alta189.simplesave.query;

import java.util.ArrayList;
import java.util.List;

public class WhereQuery<T> extends Query<T> {
	private final List<WhereEntry<T>> entries;
	private final Query<T> parent;

	public WhereQuery(Query<T> parent) {
		super(QueryType.WHERE);
		this.parent = parent;
		entries = new ArrayList<WhereEntry<T>>();
	}

	public <E> WhereEntry<T> equal(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.EQUAL, field, new Comparison<E>(comparison), this);
		entries.add(entry);
		return entry;
	}

	public <E> WhereEntry<T> notEqual(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.NOT_EQUAL, field, new Comparison<E>(comparison), this);
		entries.add(entry);
		return entry;
	}

	public <E> WhereEntry<T> greaterThan(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.GREATER_THAN, field, new Comparison<E>(comparison), this);
		entries.add(entry);
		return entry;
	}

	public <E> WhereEntry<T> lessThan(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.LESS_THAN, field, new Comparison<E>(comparison), this);
		entries.add(entry);
		return entry;
	}

	public <E> WhereEntry<T> greaterThanOrEqual(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.GREATER_THAN_OR_EQUAL, field, new Comparison<E>(comparison), this);
		entries.add(entry);
		return entry;
	}

	public <E> WhereEntry<T> lessThanOrEqual(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.LESS_THAN_OR_EQUAL, field, new Comparison<E>(comparison), this);
		entries.add(entry);
		return entry;
	}

	public <E> WhereEntry<T> contains(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.CONTAINS, field, new Comparison<E>(comparison), this);
		entries.add(entry);
		return entry;
	}

	public List<WhereEntry<T>> getEntries() {
		return entries;
	}

	public QueryResult<T> execute() {
		return parent.execute();
	}
}
