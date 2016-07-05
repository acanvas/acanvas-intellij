package com.sounddesignz.rockdot.projectWizard;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.ide.browsers.WebBrowser;
import com.intellij.ide.browsers.impl.WebBrowserServiceImpl;
import com.intellij.javascript.debugger.execution.JavaScriptDebugConfiguration;
import com.intellij.javascript.debugger.execution.JavascriptDebugConfigurationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Consumer;
import com.intellij.util.Url;
import com.jetbrains.lang.dart.ide.runner.client.DartiumUtil;
import com.jetbrains.lang.dart.ide.runner.server.DartCommandLineRunConfiguration;
import com.jetbrains.lang.dart.ide.runner.server.DartCommandLineRunConfigurationType;
import com.jetbrains.lang.dart.ide.runner.server.DartCommandLineRunnerParameters;
import com.sounddesignz.rockdot.projectWizard.RockdotGenerator.RockdotGeneratorDescriptor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class RdProjectTemplate {

  private static final RockdotGenerator ROCKDOT_GENERATOR = new RockdotGenerator();
  private static List<RdProjectTemplate> ourTemplateCache;

  private static final Logger LOG = Logger.getInstance(RdProjectTemplate.class.getName());

  @NotNull private final String myName;
  @NotNull private final String myDescription;

  public RdProjectTemplate(@NotNull final String name, @NotNull final String description) {
    myName = name;
    myDescription = description;
  }

  @NotNull
  public String getName() {
    return myName;
  }

  @NotNull
  public String getDescription() {
    return myDescription;
  }

  public abstract Collection<VirtualFile> generateProject(@NotNull final RdProjectWizardData wizardData,
                                                          @NotNull final Module module,
                                                          @NotNull final VirtualFile baseDir)
    throws IOException;


  /**
   * Must be called in pooled thread without read action; <code>templatesConsumer</code> will be invoked in EDT
   */
  public static void loadTemplatesAsync(final String sdkRoot, @NotNull final Consumer<List<RdProjectTemplate>> templatesConsumer) {
    if (ApplicationManager.getApplication().isReadAccessAllowed()) {
      LOG.error("RdProjectTemplate.loadTemplatesAsync() must be called in pooled thread without read action");
    }

    final List<RdProjectTemplate> templates = new ArrayList<RdProjectTemplate>();
    try {
      templates.addAll(getStagehandTemplates(sdkRoot));
    }
    finally {
      if (templates.isEmpty()) {
       // templates.add(new WebAppTemplate());
       // templates.add(new CmdLineAppTemplate());
        templates.add(new RockdotGeneratorTemplate(ROCKDOT_GENERATOR, new RockdotGeneratorDescriptor("basic", "Basic", "Basic Rockdot Template", "web/public/index.html")));
      }

      ApplicationManager.getApplication().invokeLater(() -> templatesConsumer.consume(templates), ModalityState.any());
    }
  }

  @NotNull
  private static List<RdProjectTemplate> getStagehandTemplates(@NotNull final String sdkRoot) {
    if (ourTemplateCache != null) {
      return ourTemplateCache;
    }

    ROCKDOT_GENERATOR.install(sdkRoot);

    final List<RockdotGeneratorDescriptor> templates = ROCKDOT_GENERATOR.getAvailableTemplates(sdkRoot);

    ourTemplateCache = new ArrayList<RdProjectTemplate>();

    for (RockdotGeneratorDescriptor template : templates) {
      ourTemplateCache.add(new RockdotGeneratorTemplate(ROCKDOT_GENERATOR, template));
    }

    return ourTemplateCache;
  }

  static void createWebRunConfiguration(final @NotNull Module module, final @NotNull VirtualFile htmlFile) {
    RdModuleBuilder.runWhenNonModalIfModuleNotDisposed(() -> {
      final WebBrowser dartium = DartiumUtil.getDartiumBrowser();
      if (dartium == null) return;

      final Url url = WebBrowserServiceImpl.getDebuggableUrl(PsiManager.getInstance(module.getProject()).findFile(htmlFile));
      if (url == null) return;

      final RunManager runManager = RunManager.getInstance(module.getProject());
      try {
        final RunnerAndConfigurationSettings settings =
          runManager.createRunConfiguration("", JavascriptDebugConfigurationType.getTypeInstance().getFactory());

        ((JavaScriptDebugConfiguration)settings.getConfiguration()).setUri(url.toDecodedForm());
        ((JavaScriptDebugConfiguration)settings.getConfiguration()).setEngineId(dartium.getId().toString());
        settings.setName(((JavaScriptDebugConfiguration)settings.getConfiguration()).suggestedName());

        runManager.addConfiguration(settings, false);
        runManager.setSelectedConfiguration(settings);
      }
      catch (Throwable t) {/* ClassNotFound in IDEA Community or if JS Debugger plugin disabled */}
    }, module);
  }

  static void createCmdLineRunConfiguration(final @NotNull Module module, final @NotNull VirtualFile mainDartFile) {
    RdModuleBuilder.runWhenNonModalIfModuleNotDisposed(() -> {
      final RunManager runManager = RunManager.getInstance(module.getProject());
      final RunnerAndConfigurationSettings settings =
        runManager.createRunConfiguration("", DartCommandLineRunConfigurationType.getInstance().getConfigurationFactories()[0]);

      final DartCommandLineRunConfiguration runConfiguration = (DartCommandLineRunConfiguration)settings.getConfiguration();
      runConfiguration.getRunnerParameters().setFilePath(mainDartFile.getPath());
      runConfiguration.getRunnerParameters()
        .setWorkingDirectory(DartCommandLineRunnerParameters.suggestDartWorkingDir(module.getProject(), mainDartFile));

      settings.setName(runConfiguration.suggestedName());

      runManager.addConfiguration(settings, false);
      runManager.setSelectedConfiguration(settings);
    }, module);
  }
}