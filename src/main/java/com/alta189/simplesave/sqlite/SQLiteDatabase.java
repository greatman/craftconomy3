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
package com.alta189.simplesave.sqlite;

import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.UnknownTableException;
import com.alta189.simplesave.internal.FieldRegistration;
import com.alta189.simplesave.internal.IdRegistration;
import com.alta189.simplesave.internal.PreparedStatementUtils;
import com.alta189.simplesave.internal.ResultSetUtils;
import com.alta189.simplesave.internal.TableRegistration;
import com.alta189.simplesave.internal.TableUtils;
import com.alta189.simplesave.query.Comparator;
import com.alta189.simplesave.query.Query;
import com.alta189.simplesave.query.QueryResult;
import com.alta189.simplesave.query.SelectQuery;
import com.alta189.simplesave.query.WhereEntry;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

@SuppressWarnings({ "all" })
public class SQLiteDatabase extends Database {
	private static final String driver = "sqlite";
	private final String uri;
	private Connection connection;

	static {
		DatabaseFactory.registerDatabase(SQLiteDatabase.class);
	}

	public SQLiteDatabase(Configuration config) {
		String path = config.getProperty(SQLiteConstants.Path);
		if (path == null || path.isEmpty()) {
			throw new IllegalArgumentException("Path is null or empty!");
		}
		this.uri = "jdbc:sqlite:" + path;
	}

	public SQLiteDatabase(String uri) {
		this.uri = uri;
	}

	public static String getDriver() {
		return driver;
	}

	@Override
	public void connect() throws ConnectionException {
		if (!isConnected()) {
			try {
				super.connect();
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection(uri);
				createTables();
			} catch (ClassNotFoundException e) {
				throw new ConnectionException("Could not find the SQLite JDBC driver!", e);
			} catch (SQLException sql) {
				throw new ConnectionException(sql);
			}
		}
	}

	@Override
	public boolean isConnected() {
		try {
			return connection != null && !connection.isClosed() && connection.isValid(5000);
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public <T> QueryResult<T> execute(Query<T> query) {
		if (isConnected()) {
			try {
				connect();
			} catch (ConnectionException e) {
				throw new RuntimeException(e);
			}
		}

		try {

			// Prepare the query
			switch (query.getType()) {
				case SELECT:
					SelectQuery select = (SelectQuery) query;
					TableRegistration table = getTableRegistration(select.getTableClass());
					PreparedStatement statement = null;
					StringBuilder buffer = new StringBuilder("SELECT * FROM ").append(table.getName()).append(" ");
					if (!select.where().getEntries().isEmpty()) {
						buffer.append("WHERE ");
						int iter = 0;
						for (Object o : select.where().getEntries()) {
							iter++;
							if (!(o instanceof WhereEntry)) {
								continue;
							}

							WhereEntry entry = (WhereEntry) o;
							buffer.append(entry.getField());
							switch (entry.getComparator()) {
								case EQUAL:
									buffer.append("== ");
									break;
								case NOT_EQUAL:
									buffer.append("!= ");
									break;
								case GREATER_THAN:
									buffer.append("> ");
									break;
								case LESS_THAN:
									buffer.append("< ");
									break;
								case GREATER_THAN_OR_EQUAL:
									buffer.append(">= ");
									break;
								case LESS_THAN_OR_EQUAL:
									buffer.append("<=");
									break;
								case CONTAINS:
									buffer.append("LIKE ");
									break;
							}
							if (iter != select.where().getEntries().size()) {
								buffer.append(entry.getOperator().name())
										.append(" ");
							}
						}

						statement = connection.prepareStatement(buffer.toString());
						iter = 0;
						for (Object o : select.where().getEntries()) {
							iter++;
							if (!(o instanceof WhereEntry)) {
								continue;
							}

							WhereEntry entry = (WhereEntry) o;
							if (entry.getComparator() == Comparator.CONTAINS) {
								statement.setString(iter, "%" + entry.getComparison().getValue().toString() + "%");
							} else {
								PreparedStatementUtils.setObject(statement, iter, entry.getComparison().getValue());
							}
						}
					}

					// Execute and return
					if (statement == null) {
						statement = connection.prepareStatement(buffer.toString());
					}
					ResultSet results = statement.executeQuery();
					QueryResult<T> result = new QueryResult<T>(ResultSetUtils.buildResultList(table, (Class<T>) table.getTableClass(), results));
					return result;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public void save(Class<?> tableClass, Object o) {
		if (!isConnected()) {
			try {
				connect();
			} catch (ConnectionException e) {
				throw new RuntimeException(e);
			}
		}
		if (!tableClass.isAssignableFrom(o.getClass())) {
			throw new IllegalArgumentException("The provided table class and save objects classes were not compatible.");
		}

		TableRegistration table = getTableRegistration(tableClass);

		if (table == null) {
			throw new UnknownTableException("The table class '" + tableClass.getCanonicalName() + "' is not registered!");
		}

		StringBuilder buffer = new StringBuilder();
		long id = TableUtils.getIdValue(table, o);
		if (id == 0) {
			buffer.append("INSERT INTO ")
					.append(table.getName())
					.append(" (");
			StringBuilder values = new StringBuilder();
			values.append("VALUES ( ");
			int iter = 0;
			for (FieldRegistration fieldRegistration : table.getFields()) {
				iter++;
				buffer.append(fieldRegistration.getName());
				values.append("?");
				if (iter == table.getFields().size()) {
					buffer.append(") ");
					values.append(")");
				} else {
					buffer.append(", ");
					values.append(", ");
				}
			}
			buffer.append(values.toString());
		} else {
			buffer.append("UPDATE ")
					.append(table.getName())
					.append(" SET ");
			int iter = 0;
			for (FieldRegistration fieldRegistration : table.getFields()) {
				iter++;
				buffer.append(fieldRegistration.getName())
						.append("=?");
				if (iter != table.getFields().size()) {
					buffer.append(", ");
				}
			}
			buffer.append(" WHERE ")
					.append(table.getId().getName())
					.append(" = ?");
		}

		try {
			PreparedStatement statement;
			if (id == 0) {
				statement = connection.prepareStatement(buffer.toString(), Statement.RETURN_GENERATED_KEYS);
			} else {
				statement = connection.prepareStatement(buffer.toString());
			}
			int i = 0;
			for (FieldRegistration fieldRegistration : table.getFields()) {
				i++;
				if (fieldRegistration.isSerializable()) {
					PreparedStatementUtils.setObject(statement, i, o);
				} else {
					if (fieldRegistration.getType().equals(int.class) || fieldRegistration.getType().equals(Integer.class)) {
						PreparedStatementUtils.setObject(statement, i, (Integer) TableUtils.getValue(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(long.class) || fieldRegistration.getType().equals(Long.class)) {
						PreparedStatementUtils.setObject(statement, i, (Long) TableUtils.getValue(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(double.class) || fieldRegistration.getType().equals(Double.class)) {
						PreparedStatementUtils.setObject(statement, i, (Double) TableUtils.getValue(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(String.class)) {
						PreparedStatementUtils.setObject(statement, i, (String) TableUtils.getValue(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(boolean.class) || fieldRegistration.getType().equals(Boolean.class)) {
						boolean value = (Boolean) TableUtils.getValue(fieldRegistration, o);
						if (value) {
							PreparedStatementUtils.setObject(statement, i, 1);
						} else {
							PreparedStatementUtils.setObject(statement, i, 0);
						}
					} else if (fieldRegistration.getType().equals(short.class) || fieldRegistration.getType().equals(Short.class)) {
						PreparedStatementUtils.setObject(statement, i, (Short) TableUtils.getValue(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(float.class) || fieldRegistration.getType().equals(Float.class)) {
						PreparedStatementUtils.setObject(statement, i, (Float) TableUtils.getValue(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(byte.class) || fieldRegistration.getType().equals(Byte.class)) {
						PreparedStatementUtils.setObject(statement, i, (Byte) TableUtils.getValue(fieldRegistration, o));
					}
				}
			}

			if (id != 0) {
				i++;
				IdRegistration idRegistration = table.getId();
				if (idRegistration.getType().equals(Integer.class) || idRegistration.getType().equals(int.class)) {
					PreparedStatementUtils.setObject(statement, i, (Integer) TableUtils.getValue(idRegistration, o));
				} else if (idRegistration.getType().equals(Long.class) || idRegistration.getType().equals(long.class)) {
					PreparedStatementUtils.setObject(statement, i, (Long) TableUtils.getValue(idRegistration, o));
				}
			}

			statement.executeUpdate();
			if (id == 0) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet != null && resultSet.next()) {
					try {
						Field field = table.getId().getField();
						field.setAccessible(true);
						if (table.getId().getType().equals(int.class)) {
							field.setInt(o, resultSet.getInt(1));
						} else if (table.getId().getType().equals(Integer.class)) {
							field.set(o, resultSet.getObject(1));
						} else if (table.getId().getType().equals(long.class)) {
							field.setLong(o, resultSet.getLong(1));
						} else if (table.getId().getType().equals(Long.class)) {
							field.set(o, resultSet.getObject(1));
						}
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove(Class<?> tableClass, Object o) {
		if (!isConnected()) {
			try {
				connect();
			} catch (ConnectionException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void createTables() {
		// Query - "CREATE TABLE IF NOT EXISTS <table> (<field> <type>...)"
		for (TableRegistration table : getTables().values()) {
			StringBuilder buffer = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table.getName());
			int iter = 0;
			Collection<FieldRegistration> fields = table.getFields();
			for (FieldRegistration field : fields) {
				iter++;
				buffer.append(" ( ").append(field.getName())
						.append(" ").append(field.getType());
				if (iter > fields.size()) {
					buffer.append(",");
				} else {
					buffer.append(")");
				}
			}
		}
	}
}
