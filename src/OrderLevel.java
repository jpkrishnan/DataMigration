/**
 * @author jkrishnan
 *
 */
/**
 * OrderLevel is the second level of the three data levels.  
 * It is a container for the creative data level
 *
 */
import java.util.HashMap;

public class OrderLevel extends BaseLevel {
	String sOrderId;
	String sOrderName;

	HashMap<String, CreativeLevel> creativeMap = new HashMap();

	public OrderLevel(String orderId, String orderName, String campaignId,
			String logDate, String sImpressions, String clicks,
			String s25Percent, String s50Percent, String s75Percent,
			String s100Percent) {
		this.sOrderId = orderId;
		this.sOrderName = orderName;
		this.sCampaignId = campaignId;
		this.sLogDate = logDate;
		this.nClicks = Integer.parseInt(clicks);
		this.nImpressions = Integer.parseInt(sImpressions);
		this.n25PercentViews = Integer.parseInt(s25Percent);
		this.n50PercentViews = Integer.parseInt(s50Percent);
		this.n75PercentViews = Integer.parseInt(s75Percent);
		this.n100PercentViews = Integer.parseInt(s100Percent);

	}

	public String getOrderId() {
		return this.sOrderId;
	}

	public String getOrderName() {
		return this.sOrderName;
	}

	public HashMap<String, CreativeLevel> getCreativeMap() {
		return creativeMap;
	}

	public CreativeLevel getCreativeLevel(String creativeId) {
		return creativeMap.get(creativeId);
	}

	public CreativeLevel addCreative(String creativeId, String creativeName, String previewURL,String sImpressions, String clicks,
			String s25Percent, String s50Percent, String s75Percent,
			String s100Percent) {
		CreativeLevel creative = new CreativeLevel(creativeId, creativeName,
				this.sOrderId, this.sLogDate, previewURL, Integer.parseInt(sImpressions),
				Integer.parseInt(clicks), Integer.parseInt(s25Percent), Integer.parseInt(s50Percent),
				Integer.parseInt(s75Percent), Integer.parseInt(s100Percent));
		creativeMap.put(creativeId, creative);

		return creative;
	}

}
