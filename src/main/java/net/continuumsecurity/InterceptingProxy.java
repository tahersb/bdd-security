package net.continuumsecurity;

import java.util.List;

import org.browsermob.core.har.HarEntry;
import org.browsermob.core.har.HarResponse;
import org.browsermob.core.har.HarRequest;


public interface InterceptingProxy {
	void start(String label) throws Exception;
	void stop() throws Exception; //Stop the proxy
	void clear();
	List<HarEntry> getHistory(); //Return all the HarEntrys belonging to this harlog
	List<HarEntry> findInRequestHistory(String regex); //Return all HarEntries where the request headers or content match the regex
	List<HarEntry> findInResponseHistory(String regex);
	HarResponse makeRequest(HarRequest request); //Make an HTTP request using the HarRequest object and store results in HarResponse. Do not follow redirects.
}
