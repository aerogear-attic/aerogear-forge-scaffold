package org.jboss.forge.scaffold.html5.resource;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sebastien
 * Date: 5/15/13
 * Time: 9:28 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ResourceProvider {
    List<ScaffoldResource> getStaticResources();

    List<ScaffoldResource> getCommonTemplateResources();

    List<ScaffoldResource> getPerEntityTemplateResources();
}
