/**
 * @author jkrishnan
 *
 */
/**
 * CampaignManager is a class that can download a CSV file from an FTP server and 
 * update the local SQL database with the data  
 *
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

public class CampaignManager {
	/*
	 * NOTE : The following three properties should be read from a configuration
	 * file or any other secure persistent storage. Due to time constraints it
	 * has been hard coded here.
	 */
	private static final String SUB_DIR = "/yashi/";
	private static final String FILE_PATTERN = "Yashi_2016-05*";
	private static final String ADVERTISERS_MAPFILE_NAME = "Yashi_Advertisers.csv";

	private static final int LOG_DATE = 0;
	private static final int ADV_ID = 1;
	private static final int ADV_NAME = 2;
	private static final int CAMP_ID = 3;
	private static final int CAMP_NAME = 4;
	private static final int ORDER_ID = 5;
	private static final int ORDER_NAME = 6;
	private static final int CREATIVE_ID = 7;
	private static final int CREATIVE_NAME = 8;
	private static final int PREV_URL = 9;
	private static final int IMPRESSIONS = 10;
	private static final int CLICKS = 11;
	private static final int TWENTY_FIVE_VIEW = 12;
	private static final int FIFTY_VIEW = 13;
	private static final int SEVENTY_FIVE_VIEW = 14;
	private static final int HUNDRED_VIEW = 15;

	HashMap<String, String> map = new HashMap(); // Stores the mappings for the
													// advertisers
	HashMap<String, CampaignLevel> campMap = new HashMap();

	FTPConnector ftpCon = new FTPConnector();
	DBConnector dbCon = new DBConnector();

	public CampaignManager() {
	}

	/*
	 * This method reads the pre-existing advertiser mapping file for filtering
	 * the data to be added to the database
	 */
	public void readMappingFile(HashMap<String, String> map) {
		File mapFile = new File(ADVERTISERS_MAPFILE_NAME);
		FileReader fileReader;
		try {
			fileReader = new FileReader(mapFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				List<String> items = Arrays.asList(line.split(","));
				Iterator<String> itr = items.iterator();
				map.put(itr.next(), itr.next());
			}
			bufferedReader.close();
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method is the "run" method that starts the ball rolling 1. Here we
	 * connect to the FTP server and download the data files 2. The data is
	 * filtered based on advertiser mapping file. 3. The memory cache is built
	 * for each day. 4. The cache is then written out to the SQL database.
	 */
	public void start() {
		// Connect to the FTP server and the database
		ftpCon.connectToServer();
		dbCon.connectToDB();

		// Lets read the map file which is already downloaded.
		readMappingFile(map);

		//get all the files for the file pattern specified.
		FTPFile[] files = ftpCon.getFileList(FILE_PATTERN, SUB_DIR);
		
		for (FTPFile file : files) {
			if (ftpCon.downloadFile(SUB_DIR, file.getName())) {
				importData(file.getName());// Imports data into memory cache for
											// one day.
				updateDatabase(); // Updates the cached data into the database.
				cleanCache(); // Clean the cache so that we can start on the
								// next day's data
			}
		}

		dbCon.closeConnection();
		ftpCon.closeConnection();
	}

	/*
	 * This method does the actual work to read the data file from the FTP
	 * server line by line Each line is parsed and a cache as the three data
	 * levels. While each of the data is cached the sum of the data columns in
	 * each level is calculated to be updated into the database.
	 */
	public void importData(String localFileName) {
		File localfile = new File(localFileName);
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(localfile);
			bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				// load the comma separated line into array items
				ArrayList<String> items = new ArrayList(Arrays.asList(line.split(",")));
				CampaignLevel campaign = null;
				boolean bNewCampaign = false;
				
				// Only interested in rows that have the advertisers present in the map
				if (map.containsKey(items.get(ADV_ID))) { 
					// if we have not seen this campaign before create a new one					
					if (!campMap.containsKey(items.get(CAMP_ID))) { 
						campaign = new CampaignLevel(items.get(CAMP_ID),
								items.get(CAMP_NAME), items.get(ADV_ID),
								items.get(ADV_NAME), items.get(LOG_DATE),
								items.get(IMPRESSIONS), items.get(CLICKS),
								items.get(TWENTY_FIVE_VIEW),
								items.get(FIFTY_VIEW),
								items.get(SEVENTY_FIVE_VIEW),
								items.get(HUNDRED_VIEW));
						campMap.put(items.get(CAMP_ID), campaign);
						bNewCampaign = true;
					} else {
						campaign = campMap.get(items.get(CAMP_ID));
					}
					OrderLevel order = null;
					order = campaign.getOrderLevel(items.get(ORDER_ID));
					if (order == null) {// No such order existed before. Lets
										// create one
						order = campaign.addOrder(items.get(ORDER_ID),
								items.get(ORDER_NAME), items.get(CAMP_ID),
								items.get(LOG_DATE), items.get(IMPRESSIONS),
								items.get(CLICKS), items.get(TWENTY_FIVE_VIEW),
								items.get(FIFTY_VIEW),
								items.get(SEVENTY_FIVE_VIEW),
								items.get(HUNDRED_VIEW));
					} else { // An order already existed. So lets just add to
								// the totals of the order
						order.addToImpressionCount(Integer.parseInt(items
								.get(IMPRESSIONS)));
						order.addToClicksCount(Integer.parseInt(items
								.get(CLICKS)));
						order.addToPercentage(
								Integer.parseInt(items.get(TWENTY_FIVE_VIEW)),
								Integer.parseInt(items.get(FIFTY_VIEW)),
								Integer.parseInt(items.get(SEVENTY_FIVE_VIEW)),
								Integer.parseInt(items.get(HUNDRED_VIEW)));

					}
					// Add the creative data level for this order
					if (order.getCreativeLevel(items.get(CREATIVE_ID)) == null) 
						order.addCreative(items.get(CREATIVE_ID),
								items.get(CREATIVE_NAME), items.get(PREV_URL));

					// Add this new row's data to the counts of the campaign
					// itself
					campaign.addToImpressionCount(Integer.parseInt(items
							.get(IMPRESSIONS)));
					campaign.addToClicksCount(Integer.parseInt(items
							.get(CLICKS)));
					campaign.addToPercentage(
							Integer.parseInt(items.get(TWENTY_FIVE_VIEW)),
							Integer.parseInt(items.get(FIFTY_VIEW)),
							Integer.parseInt(items.get(SEVENTY_FIVE_VIEW)),
							Integer.parseInt(items.get(HUNDRED_VIEW)));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try { // Lets do some cleanup here
				bufferedReader.close();
				fileReader.close();
				localfile.deleteOnExit(); // don't want to leave the downloaded
											// data file persisting on our local
											// drive
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * This routine connects to the database and updates it with the cached data
	 * that was built in the previous step
	 */
	public void updateDatabase() {
		Iterator iter = campMap.values().iterator();
		while (iter.hasNext()) {
			CampaignLevel campaign = (CampaignLevel) iter.next();
			int db_campaign_id = 0;
			if ((db_campaign_id = dbCon.isCampaignAlreadyPresent(campaign
					.getCampaignId())) == -1) {
				db_campaign_id = dbCon.addCampaign(campaign.getCampaignId(),
						campaign.getCampaignName(), campaign.getAdvId(),
						campaign.sAdvName);
			}
			if (dbCon.isCampaignDataPresent(Integer.toString(db_campaign_id),
					campaign.getLogDate()) == -1) {
				dbCon.addCampaignData(Integer.toString(db_campaign_id),
						campaign.getLogDate(), campaign.getImpressions(),
						campaign.getClicks(), campaign.getPercentageViews(25),
						campaign.getPercentageViews(50),
						campaign.getPercentageViews(75),
						campaign.getPercentageViews(100));
			}

			Iterator orderIter = campaign.getOrderMap().values().iterator();
			while (orderIter.hasNext()) {
				OrderLevel order = (OrderLevel) orderIter.next();
				int db_order_id = 0;
				if ((db_order_id = dbCon.isOrderAlreadyPresent(order
						.getOrderId())) == -1) {
					db_order_id = dbCon.addOrder(order.getOrderId(),
							Integer.toString(db_campaign_id),
							order.getOrderName());
				}
				if (dbCon.isOrderDataPresent(Integer.toString(db_order_id),
						order.getLogDate()) == -1) {
					dbCon.addOrderData(Integer.toString(db_order_id),
							order.getLogDate(), order.getImpressions(),
							order.getClicks(), order.getPercentageViews(25),
							order.getPercentageViews(50),
							order.getPercentageViews(75),
							order.getPercentageViews(100));
				}

				Iterator cIter = order.getCreativeMap().values().iterator();
				while (cIter.hasNext()) {
					CreativeLevel cLevel = (CreativeLevel) cIter.next();
					int db_creative_id = 0;
					if ((db_creative_id = dbCon.isCreativeAlreadyPresent(cLevel
							.getCreativeId())) == -1) {
						db_creative_id = dbCon.addCreative(
								cLevel.getCreativeId(),
								Integer.toString(db_order_id),
								cLevel.getCreativeName(),
								cLevel.getPreviewURL());
					}
					if (dbCon.isCreativeDataPresent(
							Integer.toString(db_creative_id),
							cLevel.getLogDate()) == -1) {
						dbCon.addCreativeData(Integer.toString(db_creative_id),
								cLevel.getLogDate(), cLevel.getImpressions(),
								cLevel.getClicks(),
								cLevel.getPercentageViews(25),
								cLevel.getPercentageViews(50),
								cLevel.getPercentageViews(75),
								cLevel.getPercentageViews(100));
					}
				}

			}
		}

	}

	/*
	 * Routine that clears the in-memory cache at the end of each file after the
	 * database is updated
	 */
	public void cleanCache() {
		Iterator iter = campMap.values().iterator();
		while (iter.hasNext()) {
			CampaignLevel campaign = (CampaignLevel) iter.next();
			Iterator orderIter = campaign.getOrderMap().values().iterator();
			while (orderIter.hasNext()) {
				OrderLevel order = (OrderLevel) orderIter.next();
				order.getCreativeMap().clear();
			}
			campaign.getOrderMap().clear();
		}
		campMap.clear();
	}

	/*
	 * Debug routine to show the values in the in-memory cache
	 */
	public void display() {
		Iterator iter = campMap.values().iterator();
		while (iter.hasNext()) {
			CampaignLevel campaign = (CampaignLevel) iter.next();
			System.out.println("CampaignName = " + campaign.getCampaignName()
					+ " Advertiser Name = " + campaign.getAdvName());
			Iterator orderIter = campaign.getOrderMap().values().iterator();
			while (orderIter.hasNext()) {
				OrderLevel order = (OrderLevel) orderIter.next();
				System.out.println("\t" + "OrderName = " + order.getOrderName()
						+ " Clicks = " + order.getClicks());
				Iterator creativeIterator = order.getCreativeMap().values()
						.iterator();
				while (creativeIterator.hasNext()) {
					CreativeLevel creative = (CreativeLevel) creativeIterator
							.next();
					System.out.println("\t\t" + "CreativeName = "
							+ creative.getCreativeName() + " Clicks = "
							+ creative.getClicks());
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CampaignManager mgr = new CampaignManager();

		mgr.start(); // Start the ball rolling here.
		// mgr.display();
		System.out
				.println("--------------Completed storing all the data------------------");

	}

}
