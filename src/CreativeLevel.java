/**
 * @author jkrishnan
 *
 */
/**
 * CreativeLevel is the third and last level of the three data levels.
 * 
 * 
 */
public class CreativeLevel extends BaseLevel {

	String sCreativeId;
	String sCreativeName;
	String sOrderId;
	String sPreviewURL;

	public CreativeLevel(String creativeId, String creativeName,
			String orderId, String logDate, String previewURL,
			int iImpressions, int iClicks, int i25Percent, int i50Percent,
			int i75Percent, int i100Percent) {
		this.sCreativeId = creativeId;
		this.sCreativeName = creativeName;
		this.sOrderId = orderId;
		this.sLogDate = logDate;
		this.sPreviewURL = previewURL;
		this.nClicks = iClicks;
		this.nImpressions = iImpressions;
		this.n25PercentViews = i25Percent;
		this.n50PercentViews = i50Percent;
		this.n75PercentViews = i75Percent;
		this.n100PercentViews = i100Percent;
	}

	public String getCreativeId() {
		return this.sCreativeId;
	}

	public String getCreativeName() {
		return this.sCreativeName;
	}

	public String getOrderId() {
		return this.sOrderId;
	}

	public String getPreviewURL() {
		return this.sPreviewURL;
	}
}
