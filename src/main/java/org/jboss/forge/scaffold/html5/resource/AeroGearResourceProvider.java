package org.jboss.forge.scaffold.html5.resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sebastien
 * Date: 4/4/13
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class AeroGearResourceProvider implements ResourceProvider {

    @Override
    public List<ScaffoldResource> getStaticResources(){
        List<ScaffoldResource> staticResources = new ArrayList<ScaffoldResource>();
        staticResources.add(new ScaffoldResource("/scaffold/styles/bootstrap.css","/styles/bootstrap.css", ScaffoldResource.ResourceType.WEB_STATIC));
        staticResources.add(new ScaffoldResource("/scaffold/styles/main.css","/styles/main.css", ScaffoldResource.ResourceType.WEB_STATIC));
        staticResources.add(new ScaffoldResource("/scaffold/styles/bootstrap-responsive.css","/styles/bootstrap-responsive.css", ScaffoldResource.ResourceType.WEB_STATIC));
        staticResources.add(new ScaffoldResource("/scaffold/scripts/vendor/angular.js","/scripts/vendor/angular.js", ScaffoldResource.ResourceType.WEB_STATIC));
        staticResources.add(new ScaffoldResource("/scaffold/scripts/vendor/aerogear.js","/scripts/vendor/aerogear.js", ScaffoldResource.ResourceType.WEB_STATIC));
        staticResources.add(new ScaffoldResource("/scaffold/scripts/vendor/bootstrap.min.js","/scripts/vendor/bootstrap.min.js", ScaffoldResource.ResourceType.WEB_STATIC));
        staticResources.add(new ScaffoldResource("/scaffold/scripts/vendor/jquery.min.js","/scripts/vendor/jquery.min.js", ScaffoldResource.ResourceType.WEB_STATIC));
        staticResources.add(new ScaffoldResource("/scaffold/angularjs/images/forge-logo.png","/images/forge-logo.png", ScaffoldResource.ResourceType.WEB_STATIC));
        return staticResources;
    }

    @Override
    public List<ScaffoldResource> getCommonTemplateResources(){
        List<ScaffoldResource> resources = new ArrayList<ScaffoldResource>();
        resources.add(new ScaffoldResource("views/index.html.ftl","index.html", ScaffoldResource.ResourceType.WEB_TEMPLATE));
        resources.add(new ScaffoldResource("scripts/app.js.ftl","scripts/app.js", ScaffoldResource.ResourceType.WEB_TEMPLATE));
        resources.add(new ScaffoldResource("scripts/services/locationParser.js.ftl","scripts/services/locationParser.js", ScaffoldResource.ResourceType.WEB_TEMPLATE));
        resources.add(new ScaffoldResource("scripts/filters/genericSearchFilter.js.ftl","scripts/filters/genericSearchFilter.js", ScaffoldResource.ResourceType.WEB_TEMPLATE));
        resources.add(new ScaffoldResource("scripts/filters/startFromFilter.js.ftl","scripts/filters/startFromFilter.js", ScaffoldResource.ResourceType.WEB_TEMPLATE));
        resources.add(new ScaffoldResource("scripts/filters/startFromFilter.js.ftl","scripts/filters/startFromFilter.js", ScaffoldResource.ResourceType.WEB_TEMPLATE));
        return resources;
    }

    @Override
    public List<ScaffoldResource> getPerEntityTemplateResources(){
        List<ScaffoldResource> resources = new ArrayList<ScaffoldResource>();
        resources.add(new ScaffoldResource("views/detail.html.ftl","/views/{entity}/detail.html", ScaffoldResource.ResourceType.WEB_TEMPLATE));
        resources.add(new ScaffoldResource("views/search.html.ftl","/views/{entity}/search.html", ScaffoldResource.ResourceType.WEB_TEMPLATE));
        resources.add(new ScaffoldResource("scripts/services/entityFactory.js.ftl","/scripts/services/{entity}Factory.js", ScaffoldResource.ResourceType.WEB_TEMPLATE));
        resources.add(new ScaffoldResource("scripts/controllers/newEntityController.js.ftl","/scripts/controllers/new{entity}Controller.js", ScaffoldResource.ResourceType.WEB_TEMPLATE));
        resources.add(new ScaffoldResource("scripts/controllers/searchEntityController.js.ftl","/scripts/controllers/search{entity}Controller.js", ScaffoldResource.ResourceType.WEB_TEMPLATE));
        resources.add(new ScaffoldResource("scripts/controllers/editEntityController.js.ftl","/scripts/controllers/edit{entity}Controller.js", ScaffoldResource.ResourceType.WEB_TEMPLATE));
        return resources;
    }

}
