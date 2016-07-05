/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sounddesignz.rockdot.action;

import com.intellij.CommonBundle;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.ide.IdeBundle;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.psi.PsiFileSystemItem;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public abstract class CreateHandler implements InputValidatorEx {
  @Nullable private final Project myProject;
  @Nullable
  private final Component myDialogParent;
  @Nullable
  private PsiFileSystemItem myCreatedElement = null;
  private String myErrorText;
  protected String dartCommand;

  public CreateHandler(@Nullable Project project, @Nullable Component dialogParent) {
    myProject = project;
    myDialogParent = dialogParent;
  }

  @Override
  public boolean checkInput(String inputString) {
    if(inputString.length() >= 3){
      myErrorText = null;
      return true;
    }
    else{
      myErrorText = "Three or more letters, please.";
      return false;
    }
  }

  @Override
  public String getErrorText(String inputString) {
    return myErrorText;
  }

  @Override
  public boolean canClose(final String className) {

    if (className.length() == 0) {
      showErrorDialog(IdeBundle.message("error.name.should.be.specified"));
      return false;
    }

    return createClass(className, 10);
  }


    public boolean createClass(final String commandName, final int timeoutInSeconds) {
        String workingdir = myProject.getBaseDir().getCanonicalPath();
        final GeneralCommandLine cmd = new GeneralCommandLine().withWorkDirectory(workingdir);

        cmd.setExePath(SystemInfo.isWindows?"dart.bat":"dart");
        cmd.addParameters("bin/"+ dartCommand +".dart", "--name", commandName);

        try {
            ProcessOutput out = new CapturingProcessHandler(cmd).runProcess(timeoutInSeconds * 1000, false);
            if(out.getExitCode() != 0){
                showErrorDialog("An error occured: \n" + out.getStdout());
                return false;
            }
            else return true;
        }
        catch (ExecutionException e) {
            showErrorDialog("An error occured: \n" + e.getMessage());
            return false;
        }
  }

  private void showErrorDialog(String message) {
    String title = CommonBundle.getErrorTitle();
    Icon icon = Messages.getErrorIcon();
    if (myDialogParent != null) {
      Messages.showMessageDialog(myDialogParent, message, title, icon);
    }
    else {
      Messages.showMessageDialog(myProject, message, title, icon);
    }
  }
}
