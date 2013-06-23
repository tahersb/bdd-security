package net.continuumsecurity;


import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.lightbody.bmp.core.har.HarContent;
import net.lightbody.bmp.core.har.HarCookie;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarNameValuePair;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;
import net.lightbody.bmp.proxy.ProxyServer;

import org.openqa.selenium.Proxy;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

public class BMProxy implements InterceptingProxy {
	private ProxyServer server;
	int harLabel = 1;

	public BMProxy(int port) throws Exception {
		server = new ProxyServer(port);
		server.start();
		server.setCaptureContent(true);
		server.setCaptureHeaders(true);
	}

	public void newHar(String label) {
		server.newHar(label);
	}

	public void start() throws Exception {
		//server.start();
	}
	
	public void newLabel(String label) {
		server.newHar(label);
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
	 * 
	 * @see
	 * net.continuumsecurity.InterceptingProxy#findInRequestHistory(java.lang
	 * .String)
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
	public HarResponse makeRequest(HarRequest har) throws Exception {
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);
		URL url;
		
			url = new URL(har.getUrl());
			WebRequest request = new WebRequest(url,com.gargoylesoftware.htmlunit.HttpMethod.valueOf(har.getMethod()));
			request.getAdditionalHeaders().clear();
			for (HarNameValuePair header : har.getHeaders()) {
				request.setAdditionalHeader(header.getName(), header.getValue());
			}
			if (har.getPostData() != null) {
				request.setRequestBody(har.getPostData().getText());
			}
			WebResponse webResponse = webClient.getPage(request).getWebResponse();
			HarResponse response = new HarResponse();
			response.setStatus(webResponse.getStatusCode());
			response.setStatusText(webResponse.getStatusMessage());
			for (NameValuePair header : webResponse.getResponseHeaders()) {
				response.getHeaders().add(new HarNameValuePair(header.getName(),header.getValue()));
			}
			HarContent harContent = new HarContent();
			harContent.setMimeType(webResponse.getContentType());
			harContent.setText(webResponse.getContentAsString());
			response.setContent(harContent);
			return response;
	}

	public boolean responseContains(HarResponse response, String regex) {
		Pattern pat = Pattern.compile(".*" + regex + ".*");
		if (pat.matcher(Integer.toString(response.getStatus())).matches())
			return true;
		if (pat.matcher(response.getStatusText()).matches())
			return true;

		for (HarNameValuePair header : response.getHeaders()) {
			if (pat.matcher(header.getName()).matches()
					|| pat.matcher(header.getValue()).matches()) {
				return true;
			}
		}
		for (HarCookie cookie : response.getCookies()) {
			if (pat.matcher(cookie.getName()).matches()
					|| pat.matcher(cookie.getValue()).matches()) {
				return true;
			}
		}
		if (pat.matcher(response.getContent().getText()).matches())
			return true;
		return false;
	}

	public boolean requestContains(HarRequest request, String regex) {
		Pattern pat = Pattern.compile(".*" + regex + ".*");

		for (HarNameValuePair qs : request.getQueryString()) {
			if (pat.matcher(qs.getName()).matches()
					|| pat.matcher(qs.getValue()).matches()) {
				return true;
			}
		}
		for (HarNameValuePair header : request.getHeaders()) {
			if (pat.matcher(header.getName()).matches()
					|| pat.matcher(header.getValue()).matches()) {
				return true;
			}
		}
		for (HarCookie cookie : request.getCookies()) {
			if (pat.matcher(cookie.getName()).matches()
					|| pat.matcher(cookie.getValue()).matches()) {
				return true;
			}
		}
		if (request.getPostData() != null && pat.matcher(request.getPostData().getText()).matches()) {
			return true;
		}
		return false;
	}

	@Override
	public Proxy seleniumProxy() throws UnknownHostException {
		return server.seleniumProxy();
	}
}
