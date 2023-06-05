package com.boxuk.jenkins.jslint;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import hudson.tasks.Builder;
import hudson.util.DescribableList;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 * @author Gavin Davies <gavin.davies@boxuk.com>
 * @license MIT License http://www.opensource.org/licenses/MIT
 */
public class JSLintBuilderTest extends HudsonTestCase {

    private WebClient webClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        webClient = new WebClient();
        webClient.setCssEnabled(false);
        webClient.setThrowExceptionOnFailingStatusCode(false);
    }

    public void testConfigurationOptionsCanBeSet() throws Exception {
        FreeStyleProject project = createFreeStyleProject();
        JSLintBuilder builder = new JSLintBuilder("Default", "build.xml", "install", "-Dbrowser=true");
        project.getBuildersList().add(builder);

        HtmlForm form = webClient.goTo(project.getUrl() + "/configure").getFormByName("config");
        HtmlInput includePattern = form.getInputByName("jslint.includePattern");
        includePattern.setValue("lib/**/*.js");

        HtmlInput excludePattern = form.getInputByName("jslint.excludePattern");
        excludePattern.setValue("lib/**/foobar.js");

        HtmlInput logfile = form.getInputByName("jslint.logfile");
        logfile.setValue("jslint-output.xml");

        submit(form);

        DescribableList<Builder, Descriptor<Builder>> builders = project.getBuildersList();
        JSLintBuilder builder2 = builders.get(JSLintBuilder.class);

        assertEquals("lib/**/*.js", builder2.getIncludePattern());
        assertEquals("lib/**/foobar.js", builder2.getExcludePattern());
        assertEquals("jslint-output.xml", builder2.getLogfile());
        assertEquals("-Dbrowser=true", builder2.getArguments());
    }
}
