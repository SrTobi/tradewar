package tradewar.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import tradewar.api.IVersion;


/**
 * 
 * @author Tobi
 *
 * Models a version in Tradewar.
 * 
 * Format is:
 * 
 * (api-version).(netcode-version):(build-day).(build-month).(build-year)
 *
 * For example:
 *	1.1:23.8.2013
 */
public final class Version implements IVersion {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	public static final String VERSION_PATTERN = "\\d+\\.\\d+\\:.+";
	
	private String productName;
	private int apiVersion;
	private int releaseVersion;
	private Calendar buildDate;
	
	public Version(String productName, int apiVersion, int releaseVersion, Calendar buildDate) {
		this.productName = productName;
		this.apiVersion = apiVersion;
		this.releaseVersion = releaseVersion;
		this.buildDate = (Calendar) buildDate.clone();
	}
	
	public Version(String productName, int apiVersion, int releaseVersion, int buildDay, int buildMonth, int buildYear) {
		this.productName = productName;
		this.apiVersion = apiVersion;
		this.releaseVersion = releaseVersion;
		this.buildDate = new GregorianCalendar(buildYear, buildMonth - 1, buildDay);
	}
	
	
	public int getApiVersion() {
		return apiVersion;
	}
	public int getReleaseVersion() {
		return releaseVersion;
	}
	
	public Calendar getBuildDate() {
		return buildDate;
	}
	
	@Override
	public boolean isNewerThen(IVersion other) {
		if(other instanceof Version) {
			Version oth = (Version) other;
			return buildDate.after(oth.buildDate);
		}else{
			return false;
		}
	}
	
	@Override
	public boolean isCompatible(IVersion other) {
		
		if(other instanceof Version) {
			Version oth = (Version) other;
			return oth.apiVersion == apiVersion
					&& oth.releaseVersion == releaseVersion;
		}else{
			return false;
		}
	}

	@Override
	public String getProductName() {
		return productName;
	}
	
	@Override
	public String getVersionCode() {
		return getApiVersion() + "." + getReleaseVersion() + ":" + DATE_FORMAT.format(buildDate.getTime());
	}
	
	@Override
	public String toString() {
		return getVersionCode();
	}
	
	public static Version parse(String productName, String version_string) throws ParseException {
		
		if(!version_string.matches(VERSION_PATTERN)) {
			throw new ParseException("String does not contain a version!", 0);
		}
		
		String[] parts = version_string.split("\\:");
		
		assert parts.length == 2;
		
		String[] preParts = parts[0].split("\\.");
		assert preParts.length == 2;
		int apiVersion = Integer.parseInt(preParts[0]);
		int releaseVersion = Integer.parseInt(preParts[1]);
		Calendar buildDate = Calendar.getInstance();
		buildDate.setTime(DATE_FORMAT.parse(parts[1]));
		
		return new Version(productName, apiVersion, releaseVersion, buildDate);
	}
}
