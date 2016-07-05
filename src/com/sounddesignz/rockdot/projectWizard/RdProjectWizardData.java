package com.sounddesignz.rockdot.projectWizard;

import com.intellij.ide.bookmarks.Bookmark;
import com.intellij.ide.browsers.chrome.ChromeSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RdProjectWizardData {
  @NotNull public final String dartSdkPath;
  @NotNull public final String dartiumPath;
  @NotNull public final ChromeSettings dartiumSettings;
  @Nullable public final RdProjectTemplate myTemplate;

  @NotNull public final Boolean installSamples;
  @NotNull public final Boolean installMaterial;
  @NotNull public final Boolean installGoogle;
  @NotNull public final Boolean installFacebook;
  @NotNull public final Boolean installPhysics;
  @NotNull public final Boolean installUGC;

  public RdProjectWizardData(@NotNull final String dartSdkPath,
                             @NotNull final String dartiumPath,
                             @NotNull final ChromeSettings dartiumSettings,
                             @Nullable final RdProjectTemplate template,
                             @NotNull final Boolean installSamples,
                             @NotNull final Boolean installMaterial,
                             @NotNull final Boolean installGoogle,
                             @NotNull final Boolean installFacebook,
                             @NotNull final Boolean installPhysics,
                             @NotNull final Boolean installUGC) {
    this.dartSdkPath = dartSdkPath;
    this.dartiumPath = dartiumPath;
    this.dartiumSettings = dartiumSettings;
    myTemplate = template;

    this.installSamples = installSamples;
    this.installMaterial = installMaterial;
    this.installGoogle = installGoogle;
    this.installFacebook = installFacebook;
    this.installPhysics = installPhysics;
    this.installUGC = installUGC;
  }
}
