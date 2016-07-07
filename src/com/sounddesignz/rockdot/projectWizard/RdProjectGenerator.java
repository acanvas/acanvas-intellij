package com.sounddesignz.rockdot.projectWizard;

import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;
import com.sounddesignz.rockdot.common.RockdotBundle;
import org.jetbrains.annotations.NotNull;

public class RdProjectGenerator extends WebProjectTemplate<RdProjectWizardData> implements Comparable<RdProjectGenerator> {

  @NotNull
  public final String getName() {
    return RockdotBundle.message("rockdot.title");
  }

  @NotNull
  public final String getDescription() {
    return RockdotBundle.message("dart.project.description");
  }

  /*
  public Icon getIcon() {
    return DartIcons.Dart_16;
  }
  */

  @NotNull
  public GeneratorPeer<RdProjectWizardData> createPeer() {
    return new RdProjectGeneratorPeer();
  }

  @Override
  public final void generateProject(@NotNull final Project project,
                                    @NotNull final VirtualFile baseDir,
                                    @NotNull final RdProjectWizardData data,
                                    @NotNull final Module module) {
    ApplicationManager.getApplication().runWriteAction(
      () -> {
        final ModifiableRootModel modifiableModel = ModifiableModelsProvider.SERVICE.getInstance().getModuleModifiableModel(module);
        RdModuleBuilder.setupProject(modifiableModel, baseDir, data);
        ModifiableModelsProvider.SERVICE.getInstance().commitModuleModifiableModel(modifiableModel);
      });
  }

  @Override
  public int compareTo(@NotNull final RdProjectGenerator generator) {
    return getName().compareTo(generator.getName());
  }
}
