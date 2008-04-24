package org.mifos.framework.persistence;

import static org.junit.Assert.assertEquals;
import static org.mifos.framework.persistence.DatabaseVersionPersistence.APPLICATION_VERSION;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.Assert;
import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.datastore.DataStore;
import net.sourceforge.mayfly.dump.SqlDumper;

import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.DatabaseSetup;

/*
 * This class contains common methods used to test database upgrade and downgrade
 * scripts on the test classes that extend this base class.
 */
public class LatestTestBase {
	

	protected int version(Database database) throws SQLException {
		return new DatabaseVersionPersistence(database.openConnection()).read();
	}

	/**
	 * Similar to what we get from {@link DatabaseSetup#getStandardStore()}
	 * but without testdbinsertionscript.sql.
	 */
	protected void loadRealLatest(Database database) {
	    DatabaseSetup.executeScript(database, "sql/latest-schema.sql");
	    DatabaseSetup.executeScript(database, "sql/latest-data.sql");
	}

	protected int largestLookupId(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(
			"select max(lookup_id) from LOOKUP_VALUE");
		if (!results.next()) {
			throw new SystemException(SystemException.DEFAULT_KEY, 
				"Did not find an existing lookup_id in lookup_value table");
		}
		int largestLookupId = results.getInt(1);
		results.close();
		statement.close();
		return largestLookupId;
	}

	protected DataStore upgrade(int fromVersion, DataStore current) throws Exception {
		for (int currentVersion = fromVersion; 
			currentVersion < APPLICATION_VERSION;
			++currentVersion) {
			int higherVersion = currentVersion + 1;
			try {
				current = upgrade(current, higherVersion);
			}
			catch (Exception failure) {
				throw new Exception("Cannot upgrade to " + higherVersion,
					failure);
			}
		}
		return current;
	}

	protected DataStore upgrade(DataStore current, int nextVersion) throws Exception {
		Database database = new Database(current);
		DatabaseVersionPersistence persistence =
			new FileReadingPersistence(database.openConnection());
		Upgrade upgrade = persistence.findUpgrade(nextVersion);
		if (upgrade instanceof SqlUpgrade)
			assertNoHardcodedValues((SqlUpgrade) upgrade, nextVersion);
		
		upgrade.upgrade(database.openConnection(), persistence);
		return database.dataStore();
	}

	private void assertNoHardcodedValues(SqlUpgrade upgrade, int version) throws Exception {
		String[] sqlStatements = upgrade.readFile((InputStream) upgrade.sql().getContent());
		for (int i = 0; i < sqlStatements.length; i++) {
			Assert.assertTrue("Upgrade " + version + " contains hard-coded lookup values", HardcodedValues.checkLookupValue(sqlStatements[i]));
			Assert.assertTrue("Upgrade " + version + " contains hard-coded lookup value locales", HardcodedValues.checkLookupValueLocale(sqlStatements[i]));
		}
	}
}
