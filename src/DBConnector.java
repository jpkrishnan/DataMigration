/**
 * @author jkrishnan
 *
 */
/**
 * Wrapper class to be used for SQL operations
 * It is a self contained class with the configuration to connect and use the database
 * defined in the class itself
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnector {
	// Connection properties
	/*
	 * Normally the following five properties should be read of a configuration
	 * file. But for simplicity and time constraints it has been "#defined" here
	 */
	private static final String DB_DRIVER_STRING = "com.mysql.jdbc.Driver";
	private static final String DB_NAME = "tapclicks";
	private static final String DB_CONNECTION_STRING = "jdbc:mysql://localhost:3306/"
			+ DB_NAME;
	private static final String DB_USER = "root";
	private static final String DB_PASSWD = "admin";

	// Table names
	private static final String ZZ_YASHI_CGN = "zz__yashi_cgn";
	private static final String ZZ_YASHI_CGN_DATA = "zz__yashi_cgn_data";
	private static final String ZZ_YASHI_ORDER = "zz__yashi_order";
	private static final String ZZ_YASHI_ORDER_DATA = "zz__yashi_order_data";
	private static final String ZZ_YASHI_CREATIVE = "zz__yashi_creative";
	private static final String ZZ_YASHI_CREATIVE_DATA = "zz__yashi_creative_data";

	private Connection con;

	public DBConnector() {
	}

	/*
	 * Method to create the connection to the database
	 */
	public Connection connectToDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(DB_CONNECTION_STRING, DB_USER,
					DB_PASSWD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	/*
	 * Call this method to close the connection to the database.
	 */
	public void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Helper method to make sure a string is surrounded by quotes as per SQL
	 * specification
	 */
	private String formatStringToSQL(String str) {
		if (!str.startsWith("\""))
			str = "\"" + str;
		if (!str.endsWith("\""))
			str = str + "\"";
		return str;
	}

	/*
	 * Method to check if a campaign is already present in the database
	 */
	public int isCampaignAlreadyPresent(String yCampaignId) {
		String sqlStatement = "SELECT * FROM " + ZZ_YASHI_CGN
				+ " WHERE yashi_campaign_id=" + yCampaignId;

		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sqlStatement);
			if (rs.next())
				return rs.getInt("campaign_id");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*
	 * Method that checks if a campaign data for a given campaign id and
	 * specific date is already present
	 */
	public int isCampaignDataPresent(String yCampaignId, String log_date) {
		String sqlStatement = "SELECT * FROM " + ZZ_YASHI_CGN_DATA
				+ " WHERE campaign_id=" + yCampaignId
				+ " AND log_date=unix_timestamp(" + formatStringToSQL(log_date)
				+ ")";
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sqlStatement);
			if (rs.next())
				return rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;

	}

	/*
	 * Method that checks if a specific order is already in the database
	 */
	public int isOrderAlreadyPresent(String yOrderId) {
		String sqlStatement = "SELECT * FROM " + ZZ_YASHI_ORDER
				+ " WHERE yashi_order_id=" + yOrderId;
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sqlStatement);
			if (rs.next())
				return rs.getInt("order_id");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*
	 * Method to check if a specific order is already present in the database
	 * for a given day
	 */
	public int isOrderDataPresent(String yOrderId, String log_date) {
		String sqlStatement = "SELECT * FROM " + ZZ_YASHI_ORDER_DATA
				+ " WHERE order_id=" + yOrderId
				+ " AND log_date=unix_timestamp(" + formatStringToSQL(log_date)
				+ ")";
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sqlStatement);
			if (rs.next())
				return rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;

	}

	/*
	 * Method to check if a specific creative level record already exists in the
	 * database
	 */
	public int isCreativeAlreadyPresent(String yCreativeId) {
		String sqlStatement = "SELECT * FROM " + ZZ_YASHI_CREATIVE
				+ " WHERE yashi_creative_id=" + yCreativeId;
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sqlStatement);
			if (rs.next())
				return rs.getInt("creative_id");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*
	 * Method to check if a specific creative level data is present in the
	 * database on a given day
	 */
	public int isCreativeDataPresent(String yCreativeId, String log_date) {
		String sqlStatement = "SELECT * FROM " + ZZ_YASHI_CREATIVE_DATA
				+ " WHERE creative_id=" + yCreativeId
				+ " AND log_date=unix_timestamp(" + formatStringToSQL(log_date)
				+ ")";
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sqlStatement);
			if (rs.next())
				return rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;

	}

	/*
	 * Method to add a campaign record to the database Returns the campaign
	 * record database id
	 */
	public int addCampaign(String yCampaignId, String campaignName,
			String yAdvId, String advName) {
		String sqlStatement = "INSERT INTO "
				+ ZZ_YASHI_CGN
				+ "(yashi_campaign_id, name, yashi_advertiser_id, advertiser_name) VALUES("
				+ yCampaignId + "," + formatStringToSQL(campaignName) + ","
				+ yAdvId + "," + formatStringToSQL(advName) + ")";
		// System.out.println(sqlStatement);
		try {
			Statement stmt = con.createStatement();
			if (stmt.executeUpdate(sqlStatement) > 0) {
				sqlStatement = "SELECT * FROM " + ZZ_YASHI_CGN
						+ " WHERE yashi_campaign_id=" + yCampaignId;
				ResultSet rs = stmt.executeQuery(sqlStatement);
				if (rs.next())
					return rs.getInt("campaign_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*
	 * Method to add a campaign data record to the database Returns the campaign
	 * data record database id
	 */
	public boolean addCampaignData(String campaignId, String log_date,
			int impressionCount, int clickCount, int s25viewed, int s50Viewed,
			int s75Viewed, int s100Viewed) {
		String sqlStatement = "INSERT INTO "
				+ ZZ_YASHI_CGN_DATA
				+ "(campaign_id, log_date, impression_count, click_count, "
				+ "25viewed_count, 50viewed_count, 75viewed_count, 100viewed_count) VALUES("
				+ campaignId + "," + "unix_timestamp("
				+ formatStringToSQL(log_date) + ")" + "," + impressionCount
				+ "," + clickCount + "," + s25viewed + "," + s50Viewed + ","
				+ s75Viewed + "," + s100Viewed + ")";
		// System.out.println(sqlStatement);
		try {
			Statement stmt = con.createStatement();
			if (stmt.executeUpdate(sqlStatement) > 0)
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/*
	 * Method to add a order record to the database Returns the order record
	 * database id
	 */
	public int addOrder(String orderId, String campaignId, String orderName) {
		String sqlStatement = "INSERT INTO " + ZZ_YASHI_ORDER
				+ "(campaign_id, yashi_order_id, name) VALUES(" + campaignId
				+ "," + orderId + "," + formatStringToSQL(orderName) + ")";
		// System.out.println(sqlStatement);
		try {
			Statement stmt = con.createStatement();
			if (stmt.executeUpdate(sqlStatement) > 0) {
				sqlStatement = "SELECT * FROM " + ZZ_YASHI_ORDER
						+ " WHERE yashi_order_id=" + orderId;
				ResultSet rs = stmt.executeQuery(sqlStatement);
				if (rs.next())
					return rs.getInt("order_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*
	 * Method to add a order data record to the database Returns the order data
	 * record database id
	 */
	public boolean addOrderData(String orderId, String log_date,
			int impressionCount, int clickCount, int s25viewed, int s50Viewed,
			int s75Viewed, int s100Viewed) {
		String sqlStatement = "INSERT INTO "
				+ ZZ_YASHI_ORDER_DATA
				+ "(order_id, log_date, impression_count, click_count, "
				+ "25viewed_count, 50viewed_count, 75viewed_count, 100viewed_count) VALUES("
				+ orderId + "," + "unix_timestamp("
				+ formatStringToSQL(log_date) + ")" + "," + impressionCount
				+ "," + clickCount + "," + s25viewed + "," + s50Viewed + ","
				+ s75Viewed + "," + s100Viewed + ")";
		// System.out.println(sqlStatement);
		try {
			Statement stmt = con.createStatement();
			if (stmt.executeUpdate(sqlStatement) > 0)
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/*
	 * Method to add a creative record to the database Returns the creative
	 * record database id
	 */
	public int addCreative(String creativeId, String orderId,
			String creativeName, String previewURL) {
		String sqlStatement = "INSERT INTO " + ZZ_YASHI_CREATIVE
				+ "(order_id, yashi_creative_id, name, preview_url) VALUES("
				+ orderId + "," + creativeId + ","
				+ formatStringToSQL(creativeName) + ","
				+ formatStringToSQL(previewURL) + ")";
		// System.out.println(sqlStatement);
		try {
			Statement stmt = con.createStatement();
			if (stmt.executeUpdate(sqlStatement) > 0) {
				sqlStatement = "SELECT * FROM " + ZZ_YASHI_CREATIVE
						+ " WHERE yashi_creative_id=" + creativeId;
				ResultSet rs = stmt.executeQuery(sqlStatement);
				if (rs.next())
					return rs.getInt("creative_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*
	 * Method to add a creative data record to the database Returns the creative
	 * data record database id
	 */
	public boolean addCreativeData(String creativeId, String log_date,
			int impressionCount, int clickCount, int s25viewed, int s50Viewed,
			int s75Viewed, int s100Viewed) {
		String sqlStatement = "INSERT INTO "
				+ ZZ_YASHI_CREATIVE_DATA
				+ "(creative_id, log_date, impression_count, click_count, "
				+ "25viewed_count, 50viewed_count, 75viewed_count, 100viewed_count) VALUES("
				+ creativeId + "," + "unix_timestamp("
				+ formatStringToSQL(log_date) + ")" + "," + impressionCount
				+ "," + clickCount + "," + s25viewed + "," + s50Viewed + ","
				+ s75Viewed + "," + s100Viewed + ")";
		// System.out.println(sqlStatement);
		try {
			Statement stmt = con.createStatement();
			if (stmt.executeUpdate(sqlStatement) > 0)
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
}
