package com.sounddesignz.rockdot.common;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.impl.libraries.ApplicationLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.lang.dart.DartProjectComponent;
import com.jetbrains.lang.dart.sdk.DartSdkUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RdSdk {
  public static final String DART_SDK_GLOBAL_LIB_NAME = "Dart SDK";
  private static final String UNKNOWN_VERSION = "unknown";
  private static final Key<CachedValue<RdSdk>> CACHED_DART_SDK_KEY = Key.create("CACHED_DART_SDK_KEY");

  private final @NotNull
  String myHomePath;
  private final @NotNull
  String myVersion;

  private RdSdk(@NotNull final String homePath, @NotNull final String version) {
    myHomePath = homePath;
    myVersion = version;
  }

  @NotNull
  public String getHomePath() {
    return myHomePath;
  }

  /**
   * @return presentable version with revision, like "1.9.1_r44672" or "1.9.0-dev.10.9_r44532" or "1.10.0-edge.44829"
   */
  @NotNull
  public String getVersion() {
    return myVersion;
  }

  /**
   * Returns the same as {@link #getGlobalDartSdk()} but much faster
   */
  @Nullable
  public static RdSdk getDartSdk(@NotNull final Project project) {
    CachedValue<RdSdk> cachedValue = project.getUserData(CACHED_DART_SDK_KEY);

    if (cachedValue == null) {
      cachedValue = CachedValuesManager.getManager(project).createCachedValue(() -> {
        final RdSdk sdk = getGlobalDartSdk();
        if (sdk == null) {
          return new CachedValueProvider.Result<RdSdk>(null, DartProjectComponent.getProjectRootsModificationTracker(project));
        }

        List<Object> dependencies = new ArrayList<Object>(3);
        dependencies.add(DartProjectComponent.getProjectRootsModificationTracker(project));
        ContainerUtil.addIfNotNull(dependencies, LocalFileSystem.getInstance().findFileByPath(sdk.getHomePath() + "/version"));
        ContainerUtil.addIfNotNull(dependencies, LocalFileSystem.getInstance().findFileByPath(sdk.getHomePath() + "/lib/core/core.dart"));

        return new CachedValueProvider.Result<RdSdk>(sdk, ArrayUtil.toObjectArray(dependencies));
      }, false);

      project.putUserData(CACHED_DART_SDK_KEY, cachedValue);
    }

    return cachedValue.getValue();
  }

  @Nullable
  public static RdSdk getGlobalDartSdk() {
    return findDartSdkAmongGlobalLibs(ApplicationLibraryTable.getApplicationTable().getLibraries());
  }

  @Nullable
  public static RdSdk findDartSdkAmongGlobalLibs(final Library[] globalLibraries) {
    for (final Library library : globalLibraries) {
      if (DART_SDK_GLOBAL_LIB_NAME.equals(library.getName())) {
        return getSdkByLibrary(library);
      }
    }

    return null;
  }

  @Nullable
  static RdSdk getSdkByLibrary(@NotNull final Library library) {
    final VirtualFile[] roots = library.getFiles(OrderRootType.CLASSES);
    if (roots.length == 1 && RdSdkLibraryPresentationProvider.isDartSdkLibRoot(roots[0])) {
      final String homePath = roots[0].getParent().getPath();
      final String version = StringUtil.notNullize(DartSdkUtil.getSdkVersion(homePath), UNKNOWN_VERSION);
      return new RdSdk(homePath, version);
    }

    return null;
  }
}
