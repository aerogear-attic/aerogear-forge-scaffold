package org.jboss.forge.scaffold.html5.resource;

import org.apache.bcel.verifier.exc.StaticCodeConstraintException;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sebastien
 * Date: 4/4/13
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScaffoldResource {

    private String source;

    private String destination;

    private Map<String, Object> context;

    private ResourceType type;

    public ScaffoldResource(String source, ResourceType type, Map<String, Object> context, String destination) {
        this.source = source;
        this.type = type;
        this.context = context;
        this.destination = destination;
    }

    public ScaffoldResource(String source, String destination, ResourceType type) {
        this.source = source;
        this.destination = destination;
        this.type = type;
    }

    public enum ResourceType {
        JAVA_TEMPLATE, WEB_STATIC, WEB_TEMPLATE
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
