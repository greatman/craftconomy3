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
package com.alta189.simplesave;

import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.exceptions.UnknownTableException;
import com.alta189.simplesave.internal.TableFactory;
import com.alta189.simplesave.internal.TableRegistration;
import com.alta189.simplesave.query.Query;
import com.alta189.simplesave.query.QueryResult;
import com.alta189.simplesave.query.SelectQuery;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class Database {
	private final Map<Class<?>, TableRegistration> tables = new HashMap<Class<?>, TableRegistration>();
	private Logger logger = Logger.getLogger(getClass().getCanonicalName());
	private boolean lock = false;

	protected Database() {

	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void registerTable(Class<?> tableClass) throws TableRegistrationException {
		if (lock) {
			throw new TableRegistrationException("The database is connected. You cannot register a new table");
		}

		TableRegistration table = TableFactory.buildTable(tableClass);
		if (table == null) {
			throw new TableRegistrationException("The TableFactory returned a null table");
		}

		tables.put(tableClass, table);
	}

	protected Map<Class<?>, TableRegistration> getTables() {
		return tables;
	}

	public TableRegistration getTableRegistration(Class<?> tableClass) {
		return tables.get(tableClass);
	}
	
	public Collection<TableRegistration> getTableRegistrations() {
		return Collections.unmodifiableCollection(tables.values());
	}

	public void connect() throws ConnectionException {
		lock = true;
	}

	public void close() throws ConnectionException {
		lock = false;
	}

	public <T> SelectQuery<T> select(Class<T> tableClass) {
		if (getTableRegistration(tableClass) == null) {
			throw new UnknownTableException("Cannot select from an unregistered table!");
		}

		return new SelectQuery<T>(this, tableClass);
	}

	public abstract boolean isConnected();

	public abstract <T> QueryResult<T> execute(Query<T> query);

	public abstract void save(Class<?> tableClass, Object o);

	public void save(Object o) {
		save(o.getClass(), o);
	}

	public abstract void remove(Class<?> tableClass, Object o);

	public void remove(Object o) {
		remove(o.getClass(), o);
	}

}
