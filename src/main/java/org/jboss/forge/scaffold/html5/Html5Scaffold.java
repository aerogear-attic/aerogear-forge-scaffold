package org.jboss.forge.scaffold.html5;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.*;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.scaffold.AccessStrategy;
import org.jboss.forge.scaffold.ScaffoldProvider;
import org.jboss.forge.scaffold.TemplateStrategy;
import org.jboss.forge.scaffold.html5.client.IntrospectorClient;
import org.jboss.forge.scaffold.html5.json.util.JsonArray;
import org.jboss.forge.scaffold.html5.json.util.JsonObject;
import org.jboss.forge.scaffold.html5.metawidget.inspector.ForgeInspector;
import org.jboss.forge.scaffold.html5.metawidget.inspector.propertystyle.ForgePropertyStyle;
import org.jboss.forge.scaffold.html5.metawidget.inspector.propertystyle.ForgePropertyStyleConfig;
import org.jboss.forge.scaffold.html5.resource.ResourceProvider;
import org.jboss.forge.scaffold.util.ScaffoldUtil;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.spec.javaee.EJBFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;
import org.metawidget.inspector.composite.CompositeInspector;
import org.metawidget.inspector.composite.CompositeInspectorConfig;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.inspector.jpa.JpaInspector;
import org.metawidget.inspector.jpa.JpaInspectorConfig;
import org.metawidget.inspector.propertytype.PropertyTypeInspector;
import org.metawidget.util.XmlUtils;
import org.w3c.dom.Element;
import org.jboss.forge.scaffold.html5.resource.AeroGearResourceProvider;
import javax.inject.Inject;
import java.io.*;
import java.util.*;

/**
 *
 */
@Alias("html5-aerogear")
@Help("HTML5 scaffolding")
@RequiresFacet({WebResourceFacet.class, DependencyFacet.class, PersistenceFacet.class, EJBFacet.class, CDIFacet.class})
public class Html5Scaffold extends BaseFacet implements ScaffoldProvider {

    protected ShellPrompt prompt;

    private String packageName;

    private JsonObject jsonObject;

    private boolean hasSecurity;

    private boolean hasCordova;


    protected IntrospectorClient introspectorClient;


    protected AeroGearResourceProvider resourceProvider;

    @Inject
    public Html5Scaffold(final ShellPrompt prompt) {

        this.prompt = prompt;
    }

    @Override
    public boolean install() {
        DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
        Dependency agDep = DependencyBuilder.create()
                .setGroupId("org.jboss.aerogear")
                .setArtifactId("aerogear-controller")
                .setVersion("1.0.1");
        Dependency secDep = DependencyBuilder.create()
                .setGroupId("org.jboss.aerogear")
                .setArtifactId("aerogear-security")
                .setVersion("1.0.0");
        Dependency secPLDep = DependencyBuilder.create()
                .setGroupId("org.jboss.aerogear")
                .setArtifactId("aerogear-security-picketlink")
                .setVersion("1.0.0");
        dependencyFacet.addDirectDependency(agDep);
        dependencyFacet.addDirectDependency(secDep);
        dependencyFacet.addDirectDependency(secPLDep);
        return true;
    }

    @Override
    public boolean isInstalled() {
        // TODO Looks unnecessary for this scaffold. See comments on install(). We could extract install() and installed() out.
        return true;
    }

    @Override
    public List<Resource<?>> setup(String targetDir, Resource<?> template, boolean overwrite) {


        ArrayList<Resource<?>> result = new ArrayList<Resource<?>>();
        WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

        writeConfigFile();
        refreshConfig();



        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/styles/bootstrap.css"), getClass()
                .getResourceAsStream("/scaffold/angularjs/styles/bootstrap.css"), overwrite));
        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/styles/main.css"), getClass()
                .getResourceAsStream("/scaffold/angularjs/styles/main.css"), overwrite));
        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/styles/bootstrap-responsive.css"),
                getClass().getResourceAsStream("/scaffold/angularjs/styles/bootstrap-responsive.css"), overwrite));
        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/scripts/vendor/angular.js"), getClass()
                .getResourceAsStream("/scaffold/angularjs/scripts/vendor/angular.js"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/scripts/vendor/aerogear.js"),
                getClass().getResourceAsStream("/scaffold/angularjs/scripts/vendor/aerogear.js"), overwrite));
        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/scripts/vendor/bootstrap.min.js"),
                getClass().getResourceAsStream("/scaffold/angularjs/scripts/vendor/bootstrap.min.js"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/scripts/vendor/jquery.min.js"),
                getClass().getResourceAsStream("/scaffold/angularjs/scripts/vendor/jquery.min.js"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/images/forge-logo.png"), getClass()
                .getResourceAsStream("/scaffold/angularjs/images/forge-logo.png"), overwrite));
        return result;
    }

    private void refreshConfig() {
        //retrieve the config object
        try {
            Scanner scanner = new Scanner(new File(this.project.getProjectRoot().getFullyQualifiedName(), "conf.json")).useDelimiter("\\A");
            String conf = scanner.next();
            jsonObject = new JsonObject(conf);

        } catch (FileNotFoundException e) {

        } catch (NoSuchElementException e) {

        }
    }

    @Override
    public List<Resource<?>> generateTemplates(String targetDir, boolean overwrite) {
        ArrayList<Resource<?>> result = new ArrayList<Resource<?>>();
        return result;
    }

    @Override
    public List<Resource<?>> generateIndex(String targetDir, Resource<?> template, boolean overwrite) {
        refreshConfig();
        hasSecurity = jsonObject.getObject("security").getBoolean("enable");
        hasCordova = jsonObject.getObject("cordova").getBoolean("enable");
        Configuration config = new Configuration();
        config.setClassForTemplateLoading(getClass(), "/scaffold");
        //config.setObjectWrapper(new DefaultObjectWrapper());
        BeansWrapper wrapper = new BeansWrapper();
        wrapper.setSimpleMapWrapper(true);
        config.setObjectWrapper(wrapper);
        ArrayList<Resource<?>> result = new ArrayList<Resource<?>>();
        List<String> entityNames = new ArrayList<String>();
        WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);
        FileResource<?> partialsDirectory = web.getWebResource("partials");
        for (Resource<?> resource : partialsDirectory.listResources()) {
            entityNames.add(resource.getName());
        }
        Map root = new HashMap();
        root.put("entityNames", entityNames);
        MetadataFacet metadata = this.project.getFacet(MetadataFacet.class);
        root.put("project", metadata);
        root.put("packageName", packageName.substring(0, packageName.lastIndexOf(".")));
        root.put("hasSecurity", hasSecurity);
        root.put("hasCordova", hasCordova);
        if(hasCordova){
            root.put("baseURL",jsonObject.getObject("cordova").getString("baseURL"));
        }
        Map securityMap = jsonObject.getObject("security").toMap();
        root.put("securityMap", securityMap);


        try {
            Template indexTemplate = config.getTemplate("angularjs/index.html.ftl");
            Writer contents = new StringWriter();
            indexTemplate.process(root, contents);
            contents.flush();
            result.add(ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("index.html"), contents.toString(), overwrite));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }

        try {
            Template appJsTemplate = config.getTemplate("angularjs/scripts/app.js.ftl");
            Writer contents = new StringWriter();
            appJsTemplate.process(root, contents);
            contents.flush();
            result.add(ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("scripts/app.js"), contents.toString(),
                    overwrite));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }

        try {
            Template dataServiceTemplate = config.getTemplate("angularjs/scripts/dataService.js.ftl");
            Writer contents = new StringWriter();
            dataServiceTemplate.process(root, contents);
            contents.flush();
            result.add(ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("scripts/services/dataService.js"), contents.toString(),
                    overwrite));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }


        generateController(config, root);


        try {
            Template controllerTemplate = config.getTemplate("angularjs/scripts/filters.js.ftl");
            Writer contents = new StringWriter();
            controllerTemplate.process(root, contents);
            contents.flush();
            result.add(ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("scripts/filters.js"), contents.toString(),
                    overwrite));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }

        try {
            Template controllerTemplate = config.getTemplate("angularjs/scripts/LoginController.js.ftl");
            Writer contents = new StringWriter();
            controllerTemplate.process(root, contents);
            contents.flush();
            result.add(ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("LoginController.js"), contents.toString(),
                    overwrite));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }


        return result;
    }

    private void generateController(Configuration config, Map root) {
        JavaSourceFacet javaSourceFacet = this.project.getFacet(JavaSourceFacet.class);

        try {


            Template controllerTemplate = config.getTemplate("route/Route.java.ftl");
            Writer contents = new StringWriter();
            controllerTemplate.process(root, contents);
            contents.flush();
            JavaClass resource = JavaParser.parse(JavaClass.class, contents.toString());
            resource.setPackage(javaSourceFacet.getBasePackage());
            javaSourceFacet.saveJavaSource(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
        if (hasSecurity) {
            DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);

            Dependency secDep = DependencyBuilder.create()
                    .setGroupId("org.jboss.aerogear")
                    .setArtifactId("aerogear-security")
                    .setVersion("1.0.0");
            Dependency secPLDep = DependencyBuilder.create()
                    .setGroupId("org.jboss.aerogear")
                    .setArtifactId("aerogear-security-picketlink")
                    .setVersion("1.0.0");

            dependencyFacet.addDirectDependency(secDep);
            dependencyFacet.addDirectDependency(secPLDep);

            try {
                Template controllerTemplate = config.getTemplate("security/PicketLinkDefaultUsers.java.ftl");
                Writer contents = new StringWriter();
                root.put("hasSecurity", jsonObject.getObject("security").getBoolean("enable"));
                Map securityMap = jsonObject.getObject("security").toMap();
                root.put("securityMap", securityMap);

                controllerTemplate.process(root, contents);
                contents.flush();
                JavaClass resource = JavaParser.parse(JavaClass.class, contents.toString());
                resource.setPackage(javaSourceFacet.getBasePackage() + ".config");
                javaSourceFacet.saveJavaSource(resource);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TemplateException e) {
                throw new RuntimeException(e);
            }

            try {
                Template controllerTemplate = config.getTemplate("security/Error.java.ftl");
                Writer contents = new StringWriter();
                controllerTemplate.process(root, contents);
                contents.flush();
                JavaClass resource = JavaParser.parse(JavaClass.class, contents.toString());
                resource.setPackage(javaSourceFacet.getBasePackage() + ".rest");
                javaSourceFacet.saveJavaSource(resource);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TemplateException e) {
                throw new RuntimeException(e);
            }

            try {
                Template controllerTemplate = config.getTemplate("config/CustomMediaTypeResponder.java.ftl");
                Writer contents = new StringWriter();
                controllerTemplate.process(root, contents);
                contents.flush();
                JavaClass resource = JavaParser.parse(JavaClass.class, contents.toString());
                resource.setPackage(javaSourceFacet.getBasePackage() + ".config");
                javaSourceFacet.saveJavaSource(resource);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TemplateException e) {
                throw new RuntimeException(e);
            }

            try {
                Template controllerTemplate = config.getTemplate("spi/AeroGearSecurityProvider.java.ftl");
                Writer contents = new StringWriter();
                controllerTemplate.process(root, contents);
                contents.flush();
                JavaClass resource = JavaParser.parse(JavaClass.class, contents.toString());
                resource.setPackage(javaSourceFacet.getBasePackage() + ".spi");
                javaSourceFacet.saveJavaSource(resource);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TemplateException e) {
                throw new RuntimeException(e);
            }

            try {
                Template controllerTemplate = config.getTemplate("security/Register.java.ftl");
                Writer contents = new StringWriter();
                root.put("defaultRole", jsonObject.getObject("security").getString("defaultRole"));
                controllerTemplate.process(root, contents);
                contents.flush();
                JavaClass resource = JavaParser.parse(JavaClass.class, contents.toString());
                resource.setPackage(javaSourceFacet.getBasePackage() + ".rest");
                javaSourceFacet.saveJavaSource(resource);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TemplateException e) {
                throw new RuntimeException(e);
            }

            try {
                Template controllerTemplate = config.getTemplate("security/Login.java.ftl");
                Writer contents = new StringWriter();
                controllerTemplate.process(root, contents);
                contents.flush();
                JavaClass resource = JavaParser.parse(JavaClass.class, contents.toString());
                resource.setPackage(javaSourceFacet.getBasePackage() + ".rest");
                javaSourceFacet.saveJavaSource(resource);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TemplateException e) {
                throw new RuntimeException(e);
            }

            try {
                Template controllerTemplate = config.getTemplate("config/ResponseHeaders.java.ftl");
                Writer contents = new StringWriter();
                controllerTemplate.process(root, contents);
                contents.flush();
                JavaClass resource = JavaParser.parse(JavaClass.class, contents.toString());
                resource.setPackage(javaSourceFacet.getBasePackage() + ".rest");
                javaSourceFacet.saveJavaSource(resource);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TemplateException e) {
                throw new RuntimeException(e);
            }

            try {
                Template controllerTemplate = config.getTemplate("config/Resources.java.ftl");
                Writer contents = new StringWriter();
                controllerTemplate.process(root, contents);
                contents.flush();
                JavaClass resource = JavaParser.parse(JavaClass.class, contents.toString());
                resource.setPackage(javaSourceFacet.getBasePackage() + ".config");
                javaSourceFacet.saveJavaSource(resource);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TemplateException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Template controllerTemplate = config.getTemplate("security/HttpSecurityException.java.ftl");
            Writer contents = new StringWriter();
            controllerTemplate.process(root, contents);
            contents.flush();
            JavaClass resource = JavaParser.parse(JavaClass.class, contents.toString());
            resource.setPackage(javaSourceFacet.getBasePackage() + ".exceptions");
            javaSourceFacet.saveJavaSource(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }

        try {
            Template controllerTemplate = config.getTemplate("config/CorsConfigProducer.java.ftl");
            Writer contents = new StringWriter();
            controllerTemplate.process(root, contents);
            contents.flush();
            JavaClass resource = JavaParser.parse(JavaClass.class, contents.toString());
            resource.setPackage(javaSourceFacet.getBasePackage() + ".config");
            javaSourceFacet.saveJavaSource(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Resource<?>> generateFromEntity(String targetDir, Resource<?> template, JavaClass entity, boolean overwrite) {
        this.refreshConfig();
        Configuration config = new Configuration();
        config.setClassForTemplateLoading(getClass(), "/scaffold/angularjs");
        config.setObjectWrapper(new DefaultObjectWrapper());

        ArrayList<Resource<?>> result = new ArrayList<Resource<?>>();
        WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

        Map root = new HashMap();
        // TODO: Provide a 'utility' class for allowing transliteration across language naming schemes
        // We need this to use contextual naming schemes instead of performing toLowerCase etc. in FTLs.
        root.put("entityName", entity.getName());
        root.put("hasSecurity", jsonObject.getObject("security").getBoolean("enable"));
        packageName = entity.getPackage();
        ForgePropertyStyleConfig forgePropertyStyleConfig = new ForgePropertyStyleConfig();
        forgePropertyStyleConfig.setProject(project);
        BaseObjectInspectorConfig baseObjectInspectorConfig = new BaseObjectInspectorConfig();
        baseObjectInspectorConfig.setPropertyStyle(new ForgePropertyStyle(forgePropertyStyleConfig));

        PropertyTypeInspector propertyTypeInspector = new PropertyTypeInspector(baseObjectInspectorConfig);

        ForgeInspector forgeInspector = new ForgeInspector(baseObjectInspectorConfig);

        JpaInspectorConfig jpaInspectorConfig = new JpaInspectorConfig();
        jpaInspectorConfig.setHideIds(true);
        jpaInspectorConfig.setHideVersions(true);
        jpaInspectorConfig.setHideTransients(true);
        jpaInspectorConfig.setPropertyStyle(new ForgePropertyStyle(forgePropertyStyleConfig));
        JpaInspector jpaInspector = new JpaInspector(jpaInspectorConfig);

        CompositeInspectorConfig compositeInspectorConfig = new CompositeInspectorConfig();
        compositeInspectorConfig.setInspectors(propertyTypeInspector, forgeInspector, jpaInspector);
        CompositeInspector compositeInspector = new CompositeInspector(compositeInspectorConfig);

        Element inspectionResult = compositeInspector.inspectAsDom(null, entity.getQualifiedName(), null);
        Element inspectedEntity = XmlUtils.getFirstChildElement(inspectionResult);
        System.out.println(XmlUtils.nodeToString(inspectedEntity, true));

        Element inspectedProperty = XmlUtils.getFirstChildElement(inspectedEntity);
        List<Map<String, String>> viewPropertyAttributes = new ArrayList<Map<String, String>>();
        while (inspectedProperty != null) {
            System.out.println(XmlUtils.nodeToString(inspectedProperty, true));
            Map<String, String> propertyAttributes = XmlUtils.getAttributesAsMap(inspectedProperty);

            // Canonicalize all numerical types in Java to "number" for HTML5 form input type support
            String propertyType = propertyAttributes.get("type");
            if (propertyType.equals(short.class.getName()) || propertyType.equals(int.class.getName())
                    || propertyType.equals(long.class.getName()) || propertyType.equals(float.class.getName())
                    || propertyType.equals(double.class.getName()) || propertyType.equals(Short.class.getName())
                    || propertyType.equals(Integer.class.getName()) || propertyType.equals(Long.class.getName())
                    || propertyType.equals(Float.class.getName()) || propertyType.equals(Double.class.getName())) {
                propertyAttributes.put("type", "number");
            }

            // Extract simple type name of the relationship types
            String manyToOneRel = propertyAttributes.get("many-to-one");
            if ("true".equals(manyToOneRel)) {
                String manyToOneType = propertyAttributes.get("type");
                propertyAttributes.put("simpleType", getSimpleName(manyToOneType));
            }
            String oneToOneRel = propertyAttributes.get("one-to-one");
            if ("true".equals(oneToOneRel)) {
                String oneToOneType = propertyAttributes.get("type");
                propertyAttributes.put("simpleType", getSimpleName(oneToOneType));
            }

            // Add the property attributes into a list, made accessible as a sequence to the FTL
            viewPropertyAttributes.add(propertyAttributes);
            inspectedProperty = XmlUtils.getNextSiblingElement(inspectedProperty);
        }
        root.put("properties", viewPropertyAttributes);
        //retrieve security fragment
        JsonObject securityMap = jsonObject.getObject("security");
        JsonArray entities = securityMap.getArray("entities");
        Iterator iterator = entities.iterator();
        Map securityFragmentMap = null;
        while (iterator.hasNext()) {
            JsonObject current = (JsonObject) iterator.next();
            if (current.getString("name").equals(entity.getName())) {
                securityFragmentMap = current.toMap();
            }
        }
        root.put("securitySettings", securityFragmentMap);

        // TODO: The list of template files to be processed per-entity (like detail.html.ftl and search.html.ftl) needs to
        // be obtained dynamically. Another list to be processed for all entities (like index.html.ftl) also needs to be
        // maintained. In short, a template should be associated with a processing directive like PER_ENTITY, ALL_ENTITIES etc.
        try {
            Template controllerTemplate = config.getTemplate("scripts/controllers.js.ftl");
            Writer contents = new StringWriter();
            controllerTemplate.process(root, contents);
            contents.flush();
            result.add(ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource(entity.getName() + "Controllers.js"),
                    contents.toString(), overwrite));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }

        try {
            Template indexTemplate = config.getTemplate("partials/detail.html.ftl");
            Writer out = new StringWriter();
            indexTemplate.process(root, out);
            out.flush();
            result.add(ScaffoldUtil.createOrOverwrite(prompt,
                    web.getWebResource("/partials/" + entity.getName() + "/detail.html"), out.toString(), overwrite));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }

        try {
            Template indexTemplate = config.getTemplate("partials/search.html.ftl");
            Writer out = new StringWriter();
            indexTemplate.process(root, out);
            out.flush();
            result.add(ScaffoldUtil.createOrOverwrite(prompt,
                    web.getWebResource("/partials/" + entity.getName() + "/search.html"), out.toString(), overwrite));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }

        generateIndex(targetDir, template, overwrite);
        return result;
    }

    @Override
    public List<Resource<?>> getGeneratedResources(String targetDir) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccessStrategy getAccessStrategy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TemplateStrategy getTemplateStrategy() {
        // TODO Auto-generated method stub
        return null;
    }

    private String getSimpleName(String manyToOneType) {
        JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
        try {
            JavaResource relatedResource = java.getJavaResource(manyToOneType);
            return relatedResource.getJavaSource().getName();
        } catch (FileNotFoundException fileEx) {
            // This is not supposed to happen, since the JPA entity class/file is supposed to be present by now.
            throw new RuntimeException(fileEx);
        }
    }

    private void writeConfigFile() {
        PersistenceFacet persistenceFacet = this.project.getFacet(PersistenceFacet.class);
        PersistenceDescriptor persistenceConfig = persistenceFacet.getConfig();
        //create picketlink PU if not exist
        List<PersistenceUnitDef> unitDefs = persistenceConfig.listUnits();
        boolean found = false;
        for (PersistenceUnitDef unitDef : unitDefs) {
            if (unitDef.getName().equals("picketlink-default")) {
                found = true;
            }
        }
        if (!found) {
            PersistenceUnitDef persistenceUnitDef = persistenceConfig.persistenceUnit("picketlink-default");
            persistenceUnitDef.classes("org.picketlink.idm.jpa.schema.IdentityObject",
                    "org.picketlink.idm.jpa.schema.PartitionObject",
                    "org.picketlink.idm.jpa.schema.RelationshipObject",
                    "org.picketlink.idm.jpa.schema.RelationshipIdentityObject",
                    "org.picketlink.idm.jpa.schema.RelationshipObjectAttribute",
                    "org.picketlink.idm.jpa.schema.IdentityObjectAttribute",
                    "org.picketlink.idm.jpa.schema.CredentialObject",
                    "org.picketlink.idm.jpa.schema.CredentialObjectAttribute");
            persistenceUnitDef.provider("org.hibernate.ejb.HibernatePersistence");
            persistenceUnitDef.jtaDataSource("java:jboss/datasources/ExampleDS");
            persistenceUnitDef.excludeUnlistedClasses();
            persistenceUnitDef.property("hibernate.hbm2ddl.auto", "create-drop");
            persistenceUnitDef.property("hibernate.show_sql", "false");
            persistenceUnitDef.property("hibernate.transaction.flush_before_completion", "true");
            persistenceFacet.saveConfig(persistenceConfig);
        }
        List<JavaClass> javaClasses = persistenceFacet.getAllEntities();


        JsonFactory jsonF = new JsonFactory();
        File configFile = new File(this.project.getProjectRoot().getFullyQualifiedName(), "conf.json");
        if (!configFile.exists()) {
            try {
                JsonGenerator jsonGenerator = jsonF.createGenerator(new File(this.project.getProjectRoot().getFullyQualifiedName(), "conf.json"), JsonEncoding.UTF8);
                jsonGenerator.useDefaultPrettyPrinter();
                jsonGenerator.writeStartObject();

                jsonGenerator.writeFieldName("cordova");
                jsonGenerator.writeStartObject();
                jsonGenerator.writeBooleanField("enable", true);
                jsonGenerator.writeStringField("baseURL", "http://localhost:8080");
                jsonGenerator.writeEndObject();

                jsonGenerator.writeFieldName("security");
                jsonGenerator.writeStartObject();
                jsonGenerator.writeBooleanField("enable", true);

                jsonGenerator.writeFieldName("users");
                jsonGenerator.writeStartArray();
                jsonGenerator.writeString("john");
                jsonGenerator.writeEndArray();

                jsonGenerator.writeFieldName("roles");
                jsonGenerator.writeStartArray();
                jsonGenerator.writeString("admin");
                jsonGenerator.writeString("simple");
                jsonGenerator.writeEndArray();

                jsonGenerator.writeFieldName("roleMap");
                jsonGenerator.writeStartArray();
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("user", "john");
                jsonGenerator.writeFieldName("roles");
                jsonGenerator.writeStartArray();
                jsonGenerator.writeString("admin");
                jsonGenerator.writeString("simple");
                jsonGenerator.writeEndArray();
                jsonGenerator.writeEndObject();
                jsonGenerator.writeEndArray();

                jsonGenerator.writeStringField("defaultRole", "simple");


                jsonGenerator.writeFieldName("entities");
                jsonGenerator.writeStartArray();
                for (JavaClass javaClass : javaClasses) {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("name", javaClass.getName());

                    jsonGenerator.writeFieldName("GET");
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeBooleanField("authentification", true);
                    jsonGenerator.writeFieldName("authorization");
                    jsonGenerator.writeStartArray();
                    jsonGenerator.writeString("simple");
                    jsonGenerator.writeEndArray();
                    jsonGenerator.writeEndObject();

                    jsonGenerator.writeFieldName("GETById");
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeBooleanField("authentification", true);
                    jsonGenerator.writeFieldName("authorization");
                    jsonGenerator.writeStartArray();
                    jsonGenerator.writeString("simple");
                    jsonGenerator.writeEndArray();
                    jsonGenerator.writeEndObject();

                    jsonGenerator.writeFieldName("POST");
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeBooleanField("authentification", true);
                    jsonGenerator.writeFieldName("authorization");
                    jsonGenerator.writeStartArray();
                    jsonGenerator.writeString("simple");
                    jsonGenerator.writeEndArray();
                    jsonGenerator.writeEndObject();

                    jsonGenerator.writeFieldName("PUT");
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeBooleanField("authentification", true);
                    jsonGenerator.writeFieldName("authorization");
                    jsonGenerator.writeStartArray();
                    jsonGenerator.writeString("simple");
                    jsonGenerator.writeEndArray();
                    jsonGenerator.writeEndObject();

                    jsonGenerator.writeFieldName("DELETE");
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeBooleanField("authentification", true);
                    jsonGenerator.writeFieldName("authorization");
                    jsonGenerator.writeStartArray();
                    jsonGenerator.writeString("simple");
                    jsonGenerator.writeEndArray();
                    jsonGenerator.writeEndObject();

                    jsonGenerator.writeEndObject();
                }
                jsonGenerator.writeEndArray();

                jsonGenerator.writeEndObject();
                jsonGenerator.writeEndObject();
                jsonGenerator.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }


}
