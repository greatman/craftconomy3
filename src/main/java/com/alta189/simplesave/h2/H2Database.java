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
package com.alta189.simplesave.h2;

import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.exceptions.UnknownTableException;
import com.alta189.simplesave.internal.FieldRegistration;
import com.alta189.simplesave.internal.IdRegistration;
import com.alta189.simplesave.internal.PreparedStatementUtils;
import com.alta189.simplesave.internal.ResultSetUtils;
import com.alta189.simplesave.internal.TableFactory;
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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings({ "all" })
public class H2Database extends Database {
	private static final String driver = "h2";
	private String connectionURL;
	private Connection connection;

	static {
		DatabaseFactory.registerDatabase(H2Database.class);
	}

	public H2Database(Configuration config) {
		String h2db = config.getProperty(H2Configuration.H2_DATABASE);
		if (h2db == null || h2db.isEmpty()) {
			throw new IllegalArgumentException("Database is null or empty!");
		}
		this.connectionURL = "jdbc:h2:file:" + h2db + ";MODE=MYSQL;IGNORECASE=TRUE;AUTO_SERVER=TRUE";
	}

	public H2Database(String connectionURL) {
		this.connectionURL = connectionURL;
	}

	public static String getDriver() {
		return driver;
	}

	@Override
	public void connect() throws ConnectionException {
		if (!isConnected()) {
			super.connect();

			try {
				Class.forName("org.h2.Driver");
			} catch (ClassNotFoundException e) {
				throw new ConnectionException("Could not find the H2 JDBC Driver!", e);
			}

			try {
				connection = DriverManager.getConnection(connectionURL);
				createTables();
			} catch (SQLException e) {
				throw new ConnectionException(e);
			}
		}
	}

	@Override
	public void close() throws ConnectionException {
		if (isConnected()) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new ConnectionException(e);
			}
			super.close();
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
		if (!isConnected()) {
			try {
				connect();
			} catch (ConnectionException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			switch (query.getType()) {
				case SELECT:
					SelectQuery selectQuery = (SelectQuery) query;
					TableRegistration table = getTableRegistration(selectQuery.getTableClass());
					PreparedStatement statement = null;
					StringBuilder queryBuilder = new StringBuilder();
					queryBuilder.append("SELECT * from ")
							.append(table.getName())
							.append(" ");
					if (!selectQuery.where().getEntries().isEmpty()) {
						queryBuilder.append("WHERE ");
						int count = 0;
						for (Object o : selectQuery.where().getEntries()) {
							count++;
							if (!(o instanceof WhereEntry)) {
								throw new InternalError("Something has gone very wrong!");
							}

							WhereEntry entry = (WhereEntry) o;
							queryBuilder.append(entry.getField());
							switch (entry.getComparator()) {
								case EQUAL:
									queryBuilder.append("=? ");
									break;
								case NOT_EQUAL:
									queryBuilder.append("<>? ");
									break;
								case GREATER_THAN:
									queryBuilder.append(">? ");
									break;
								case LESS_THAN:
									queryBuilder.append("<? ");
									break;
								case GREATER_THAN_OR_EQUAL:
									queryBuilder.append(">=? ");
									break;
								case LESS_THAN_OR_EQUAL:
									queryBuilder.append("<=? ");
									break;
								case CONTAINS:
									queryBuilder.append(" LIKE ? ");
									break;
							}
							if (count != selectQuery.where().getEntries().size()) {
								queryBuilder.append(entry.getOperator().name())
										.append(" ");
							}
						}
						statement = connection.prepareStatement(queryBuilder.toString());
						count = 0;
						for (Object o : selectQuery.where().getEntries()) {
							count++;
							if (!(o instanceof WhereEntry)) {
								throw new InternalError("Something has gone very wrong!");
							}

							WhereEntry entry = (WhereEntry) o;
							if (entry.getComparator() == Comparator.CONTAINS) {
								statement.setString(count, "%" + entry.getComparison().getValue().toString() + "%");
							} else {
								PreparedStatementUtils.setObject(statement, count, entry.getComparison().getValue());
							}
						}
					}
					if (statement == null) {
						statement = connection.prepareStatement(queryBuilder.toString());
					}
					ResultSet set = statement.executeQuery();
					QueryResult<T> result = new QueryResult<T>(ResultSetUtils.buildResultList(table, (Class<T>) table.getTableClass(), set));
					set.close();
					return result;
				default:
					break;
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

		StringBuilder query = new StringBuilder();
		long id = TableUtils.getIdValue(table, o);
		if (id == 0) {
			query.append("INSERT INTO ")
					.append(table.getName())
					.append(" (");
			StringBuilder valuesBuilder = new StringBuilder();
			valuesBuilder.append("VALUES ( ");
			int count = 0;
			for (FieldRegistration fieldRegistration : table.getFields()) {
				count++;
				query.append(fieldRegistration.getName());
				valuesBuilder.append("?");
				if (count == table.getFields().size()) {
					query.append(") ");
					valuesBuilder.append(")");
				} else {
					query.append(", ");
					valuesBuilder.append(", ");
				}
			}
			query.append(valuesBuilder.toString());
		} else {
			query.append("UPDATE ")
					.append(table.getName())
					.append(" SET ");
			int count = 0;
			for (FieldRegistration fieldRegistration : table.getFields()) {
				count++;
				query.append(fieldRegistration.getName())
						.append("=?");
				if (count != table.getFields().size()) {
					query.append(", ");
				}
			}
			query.append(" WHERE ")
					.append(table.getId().getName())
					.append("=?");
		}

		try {
			PreparedStatement statement;
			if (id == 0)
				statement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
			else
				statement = connection.prepareStatement(query.toString());
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
					} else {
						PreparedStatementUtils.setObject(statement, i, (Object) TableUtils.getValue(fieldRegistration, o));
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
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void registerTable(Class<?> tableClass)
			throws TableRegistrationException {

		TableRegistration table = TableFactory.buildTable(tableClass);
		if (table == null) {
			throw new TableRegistrationException("The TableFactory returned a null table");
		}

		for (TableRegistration t : getTables().values()) {
			if (t.getName().equalsIgnoreCase(table.getName()))
				throw new TableRegistrationException("This table matches another with the same name!");
		}
		super.registerTable(tableClass);
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
		if (!tableClass.isAssignableFrom(o.getClass())) {
			throw new IllegalArgumentException("The provided table class and save objects classes were not compatible.");
		}

		TableRegistration table = getTableRegistration(tableClass);

		if (table == null) {
			throw new UnknownTableException("The table class '" + tableClass.getCanonicalName() + "' is not registered!");
		}

		StringBuilder query = new StringBuilder();
		long id = TableUtils.getIdValue(table, o);
		if (id == 0)
			throw new IllegalArgumentException("Object was never inserted into database!");
		query.append("DELETE FROM ")
				.append(table.getName())
				.append(" WHERE ")
				.append(table.getId().getName())
				.append("=?");

		try {
			PreparedStatement statement = connection.prepareStatement(query.toString());

			IdRegistration idRegistration = table.getId();
			if (idRegistration.getType().equals(Integer.class) || idRegistration.getType().equals(int.class)) {
				PreparedStatementUtils.setObject(statement, 1, (Integer) TableUtils.getValue(idRegistration, o));
			} else if (idRegistration.getType().equals(Long.class) || idRegistration.getType().equals(long.class)) {
				PreparedStatementUtils.setObject(statement, 1, (Long) TableUtils.getValue(idRegistration, o));
			}
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void createTables() {
		for (TableRegistration table : getTables().values()) {
			StringBuilder query = new StringBuilder();
			query.append("CREATE TABLE IF NOT EXISTS ")
					.append(table.getName())
					.append(" (")
					.append(table.getId().getName())
					.append(" ")
					.append(H2Util.getTypeFromClass(table.getId().getType()))
					.append(" NOT NULL AUTO_INCREMENT PRIMARY KEY, ");
			int count = 0;
			for (FieldRegistration field : table.getFields()) {
				count++;
				String type = null;
				if (field.isSerializable()) {
					type = H2Util.getTypeFromClass(String.class);
				} else {
					type = H2Util.getTypeFromClass(field.getType());
				}
				query.append(field.getName())
						.append(" ")
						.append(type);
				if (count != table.getFields().size()) {
					query.append(", ");
				}
			}
			query.append(") ");
			try {
				PreparedStatement statement = connection.prepareStatement(query.toString());
				statement.executeUpdate();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void checkTableStructure(TableRegistration table) {
		// TODO Update table structure
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM ")
				.append(table.getName())
				.append(" LIMIT 1");
		try {
			ResultSetMetaData meta = connection.prepareStatement(query.toString()).executeQuery().getMetaData();
			for (int i = 1; i <= meta.getColumnCount(); i++) {

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
