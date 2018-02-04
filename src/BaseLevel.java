/**
 * @author jkrishnan
 *
 */
/**
 * BaseLevel is a parent class for all the three data levels. It stores the
 * common data between these three data level objects. Just has setters and
 * getters as methods.
 */
public class BaseLevel {
	String sCampaignId;
	String sLogDate;
	int nImpressions = 0;
	int n25PercentViews = 0;
	int n50PercentViews = 0;
	int n75PercentViews = 0;
	int n100PercentViews = 0;
	int nClicks;

	public BaseLevel() {

	}

	public String getCampaignId() {
		return sCampaignId;
	}

	public String getLogDate() {
		return sLogDate;
	}

	public int getClicks() {
		return nClicks;
	}

	public int getImpressions() {
		return nImpressions;
	}

	public int getPercentageViews(int percent) {
		if (percent == 25)
			return this.n25PercentViews;
		if (percent == 50)
			return this.n50PercentViews;
		if (percent == 75)
			return this.n75PercentViews;
		if (percent == 100)
			return this.n100PercentViews;
		return -1;
	}

	public void addToClicksCount(int count) {
		this.nClicks += count;
	}

	public void addToImpressionCount(int count) {
		this.nImpressions += count;
	}

	public void addToPercentage(int i25percent, int i50Percent, int i75Percent,
			int i100Percent) {
		this.n25PercentViews += i25percent;
		this.n50PercentViews += i50Percent;
		this.n75PercentViews += i75Percent;
		this.n100PercentViews += i100Percent;
	}
}
