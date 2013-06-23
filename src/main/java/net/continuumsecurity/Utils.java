package net.continuumsecurity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.lightbody.bmp.core.har.HarCookie;
import net.lightbody.bmp.core.har.HarRequest;

import org.apache.log4j.Logger;

import difflib.DiffUtils;
import difflib.Patch;

public class Utils {
	static Logger log = Logger.getLogger(Utils.class);

	public static String extractSessionIDName(String target) {
		if (Config.getSessionIDs().size() == 0) {
			log.warn("Attempting to extract session ID from string, but no session IDs defined in the configuration.");
		}
		for (String sessId : Config.getSessionIDs()) {
			Pattern p = Pattern.compile(".*"+sessId+".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
			Matcher m = p.matcher(target);
			log.trace("Search for sessionID: "+sessId+" in string: "+target);
			if (m.matches()) {
				log.trace("\t Found.");
				return sessId;
			}
		}
		log.trace("\t Not found.");
		return null;
	}

    public static String stripTags(String html) {
        return html.replaceAll("<.*?>","");
    }

    public static int getDiffScore(String one, String two) {
        List<String> first = Arrays.asList(one.split("[\\n\\ ]+"));
        List<String> second = Arrays.asList(two.split("[\\n\\ ]+"));

        Patch p = DiffUtils.diff(first,second);
        return p.getDeltas().size();
    }

	public static void replaceCookies(HarRequest manual,
			Map<String, String> cookieMap) {
		for (String name : cookieMap.keySet()) {
			HarCookie cookie = Utils.findCookieByName(manual.getCookies(),name);
			if (cookie != null) {
				cookie.setValue(cookieMap.get(name));
			} else {
				cookie = new HarCookie();
				cookie.setName(name);
				cookie.setValue(cookieMap.get(name));
				manual.getCookies().add(cookie);
			}
		}
	}
	
	public static HarCookie findCookieByName(List<HarCookie> harCookies, String name) {
		for (HarCookie cookie : harCookies) {
			if (name.equalsIgnoreCase(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}
    
    /*public HarEntry copyHarEntry(HarEntry src) {
    	HarEntry dst = new HarEntry();
    	//Request
    	HarRequest request = new HarRequest(src.getRequest().getMethod(),src.getRequest().getUrl(),src.getRequest().getHttpVersion());
    	Collections.copy(dst.getRequest().getQueryString(), src.getRequest().getQueryString());
    	Collections.copy(dst.getRequest().getHeaders(), src.getRequest().getHeaders());
    	
    	
    }*/
}
