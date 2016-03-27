package pl.mkapiczynski.dron.helpers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;

public class HttpHelper {

    private static final Logger log = Logger.getLogger(HttpHelper.class);

    
    
    public static void setStatusOrError(HttpServletResponse response, ServerResponse status) {
        if (status == ServerResponse.OK) {
            response.setStatus(status.getCode());
        } else {
            try {
                response.sendError(status.getCode(), status.getContent());
                log.error("Error " + status.getContent());
            } catch (IOException e) {
                log.error("Error when sending error", e);
            }
        }
    }
    
    public static void sendJSON(HttpServletResponse response, String json) {   
	    try {
	    	response.setHeader("Content-Type", "application/json; charset=UTF-8");
			response.getWriter().write(json);
			setStatusOrError(response, ServerResponse.OK);
			log.info("JSON send");
		} catch (IOException e) {
			log.error("Error sending json", e);
			setStatusOrError(response, ServerResponse.INTERNAL_SERVER_ERROR);
		}
	   
        
    }
    
    public static Map<String, String> readRequestParameters(String url) {
		List<NameValuePair> params = new ArrayList<>();
		try {
			params = URLEncodedUtils.parse(new URI(url), "UTF-8");
		} catch (URISyntaxException e1) {
			log.info("Exception during parsing address url", e1);
		}
		Map<String, String> parameters = new HashMap<String, String>();
		for (NameValuePair param : params) {
			parameters.put(param.getName(), param.getValue());
		}
		return parameters;
	}
    
}