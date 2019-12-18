package com.zq.kyb.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.InputStream;

public class ClassPathEntityResolver implements EntityResolver {


    Log log = LogFactory.getLog(this.getClass());

    private String hostname = "http://www.mc888.net/";

    @Override
    public InputSource resolveEntity
            (String
                    publicId, String
                    systemId) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if (systemId != null && systemId.startsWith(hostname)) {
            log.debug("trying to locate " + systemId + " in classpath under com/joey/core/resources/dtd");
            // Search for DTD

            InputStream dtdStream = classLoader.getResourceAsStream("com/joey/core/resources/dtd/" + systemId.substring(hostname.length()));
            if (dtdStream == null) {
                log.debug(systemId + "not found in classpath");
                return null;
            } else {
                log.debug("found " + systemId + " in classpath");
                InputSource source = new InputSource(dtdStream);
                source.setPublicId(publicId);
                source.setSystemId(systemId);
                return source;
            }
        } else {
            return null;
        }
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

}
