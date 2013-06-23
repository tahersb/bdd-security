package net.continuumsecurity;

import java.net.UnknownHostException;
import java.util.List;

import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;

import org.openqa.selenium.Proxy;


public interface InterceptingProxy {
	void stop() throws Exception; //Stop the proxy
	void clear();
	void newLabel(String label);
	List<HarEntry> getHistory(); //Return all the HarEntrys belonging to this harlog
	List<HarEntry> findInRequestHistory(String regex); //Return all HarEntries where the request headers or content match the regex
	List<HarEntry> findInResponseHistory(String regex);
	HarResponse makeRequest(HarRequest request) throws Exception; //Make an HTTP request using the HarRequest object and store results in HarResponse. Do not follow redirects.
	Proxy seleniumProxy() throws UnknownHostException;
}
