package net.continuumsecurity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.browsermob.core.har.HarCookie;
import org.browsermob.core.har.HarEntry;
import org.browsermob.core.har.HarNameValuePair;
import org.browsermob.core.har.HarRequest;
import org.browsermob.core.har.HarResponse;
import org.browsermob.proxy.ProxyServer;

public class BMProxy implements InterceptingProxy {
	private ProxyServer server;
	int harLabel = 1;

	public BMProxy(int port) throws Exception{
		server = new ProxyServer(port);
		server.setCaptureContent(true);
		server.setCaptureHeaders(true);
		
	}

	public void newHar(String label) {
		server.newHar(label);
	}
	
	public void start(String label) throws Exception {
		server.start();
		server.
		newHar(label);
	}

	@Override
	public void stop() throws Exception {
		server.stop();
	}
	
	public void clear() {
		harLabel++;
		newHar(Integer.toString(harLabel));
	}

	@Override
	public List<HarEntry> getHistory() {
		return server.getHar().getLog().getEntries();
	}

	/*
	 * FIXME change to regex match
	 * @see net.continuumsecurity.InterceptingProxy#findInRequestHistory(java.lang.String)
	 */
	@Override
	public List<HarEntry> findInRequestHistory(String regex) {
		List<HarEntry> result = new ArrayList<HarEntry>();
		for (HarEntry entry : getHistory()) {
			if (requestContains(entry.getRequest(), regex)) {
				result.add(entry);
			}
		}
		return result;
	}

	@Override
	public List<HarEntry> findInResponseHistory(String regex) {
		List<HarEntry> result = new ArrayList<HarEntry>();
		for (HarEntry entry : getHistory()) {
			if (responseContains(entry.getResponse(), regex)) {
				result.add(entry);
			}
		}
		return result;
	}

	@Override
	public HarResponse makeRequest(HarRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean responseContains(HarResponse response,String regex) {
		Pattern pat = Pattern.compile(".*"+regex+".*");
		if (pat.matcher(Integer.toString(response.getStatus())).matches()) return true;
		if (pat.matcher(response.getStatusText()).matches()) return true;

		for (HarNameValuePair header : response.getHeaders()) {
			if (pat.matcher(header.getName()).matches() || pat.matcher(header.getValue()).matches()) {
				return true;
			}
		}
		for (HarCookie cookie : response.getCookies()) {
			if (pat.matcher(cookie.getName()).matches() || pat.matcher(cookie.getValue()).matches()) {
				return true;
			}
		}
		if (pat.matcher(response.getContent().getText()).matches()) return true;
		return false;
	}
	
	public boolean requestContains(HarRequest request,String regex) {
		Pattern pat = Pattern.compile(".*"+regex+".*");

		for (HarNameValuePair qs : request.getQueryString()) {
			if (pat.matcher(qs.getName()).matches() || pat.matcher(qs.getValue()).matches()) {
				return true;
			}
		}
		for (HarNameValuePair header : request.getHeaders()) {
			if (pat.matcher(header.getName()).matches() || pat.matcher(header.getValue()).matches()) {
				return true;
			}
		}
		for (HarCookie cookie : request.getCookies()) {
			if (pat.matcher(cookie.getName()).matches() || pat.matcher(cookie.getValue()).matches()) {
				return true;
			}
		}
		if (pat.matcher(request.getPostData().getText()).matches()) return true;
		return false;
	}
}
