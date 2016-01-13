package pl.mkapiczynski.dron.helpers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.http.HttpServletResponse;

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

    public static void sendXML(HttpServletResponse response, String xml) {   
	    try {
	    	response.setHeader("Content-Type", "application/xml; charset=UTF-8");
	    	response.getOutputStream().write(xml.getBytes("UTF-8"));
			setStatusOrError(response, ServerResponse.OK);
			log.info("XML send");
		} catch (IOException e) {
			log.error("Error sending xml", e);
			setStatusOrError(response, ServerResponse.INTERNAL_SERVER_ERROR);
		}
	   
        
    }
    
    public static void sendXMLLikeInWebservice(HttpServletResponse response,String nameOfTheMethod,  String xml) {   
	   String begin = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
	   		+ "<soap:Body>"
	   		+ "<ns2:"+
	   		nameOfTheMethod +
	   		" xmlns:ns2=\"urn:smartcity:integration-channel:1.0\">";
	   
	   String end = "</ns2:"+
			   nameOfTheMethod 
	   			+ "></soap:Body>"
	   			+ "</soap:Envelope>";
       xml = removeListAnnotationsFromXml(xml);
	   sendXML(response, begin + xml + end);
    }
    
    private static String removeListAnnotationsFromXml(String xml) {
		return xml.replaceAll("<list>", "").replaceAll("</list>", "");
	}

	public static void sendMessage(HttpServletResponse response, int sc,  String message){
    	try {
    		response.setStatus(sc);
    		response.getWriter().write(message);
    		response.getWriter().flush();
    		response.getWriter().close();
    	} catch (IOException e) {
			log.error("Error sending xml", e);
			setStatusOrError(response, ServerResponse.INTERNAL_SERVER_ERROR);
		}
    }
    
}