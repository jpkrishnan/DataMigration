/**
 * @author jkrishnan
 *
 */
/**
 * CampaignLevel is the first level of the three data levels.  
 * It is a container for the order data level
 *
 */
import java.util.HashMap;

public class CampaignLevel extends BaseLevel {

	String sCampaignName;
	String sAdvId;
	String sAdvName;

	HashMap<String, OrderLevel> orderMap = new HashMap();

	public CampaignLevel(String campaignId, String campaignName, String advId,
			String advName, String logDate, String impCnt, String sClicks,
			String s25Percent, String s50Percent, String s75Percent,
			String s100Percent) {

		this.sCampaignId = campaignId;
		this.sCampaignName = campaignName;
		this.sAdvId = advId;
		this.sAdvName = advName;
		this.sLogDate = logDate;
		this.nImpressions = Integer.parseInt(impCnt);
		this.nClicks = Integer.parseInt(sClicks);
		this.n25PercentViews = Integer.parseInt(s25Percent);
		this.n50PercentViews = Integer.parseInt(s50Percent);
		this.n75PercentViews = Integer.parseInt(s75Percent);
		this.n100PercentViews = Integer.parseInt(s100Percent);

	}

	public String getAdvId() {
		return sAdvId;
	}

	public String getAdvName() {
		return sAdvName;
	}

	public String getCampaignName() {
		return sCampaignName;
	}

	public HashMap<String, OrderLevel> getOrderMap() {
		return this.orderMap;
	}

	public OrderLevel getOrderLevel(String orderId) {
		return orderMap.get(orderId);
	}

	public OrderLevel addOrder(String orderId, String orderName,
			String campaignId, String logDate, String sImpressions,
			String clicks, String s25Percent, String s50Percent,
			String s75Percent, String s100Percent) {
		OrderLevel order = new OrderLevel(orderId, orderName, campaignId,
				logDate, sImpressions, clicks, s25Percent, s50Percent,
				s75Percent, s100Percent);
		orderMap.put(orderId, order);

		return order;
	}

}
