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

@SuppressWarnings({ "all" })
public class WhereEntry<T> {
	private final Comparator comparator;
	private final String field;
	private final Comparison comparison;
	private final WhereQuery<T> parent;
	private Operator operator;

	public WhereEntry(Comparator comparator, String field, Comparison comparison, WhereQuery<T> parent) {
		this.comparator = comparator;
		this.field = field;
		this.comparison = comparison;
		this.parent = parent;
	}

	public Comparator getComparator() {
		return comparator;
	}

	public String getField() {
		return field;
	}

	public Comparison getComparison() {
		return comparison;
	}

	public WhereQuery<T> setOperator(Operator operator) {
		this.operator = operator;
		return parent;
	}

	public Operator getOperator() {
		return operator;
	}

	public WhereQuery<T> and() {
		return setOperator(Operator.AND);
	}

	public WhereQuery<T> or() {
		return setOperator(Operator.OR);
	}

	public QueryResult<T> execute() {
		return parent.execute();
	}
}