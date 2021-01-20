// Copyright (c) 2020 Marko Ristin <marko@ristin.ch>

package ch.ristin.icontract_hypothesis_pycharm;

import com.intellij.execution.RunManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;

/**
 * Ghostwrite Hypothesis strategies explicitly with icontract-hypothesis to a file.
 */
public class GhostwriteExplicitToAction extends AnAction {
    /**
     * Specify and run the "icontract-hypothesis ghostwrite --explicit" command as a run configuration to a file.
     *
     * @param event Event received when the associated menu item is chosen.
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final Project project = event.getProject();
        assert project != null;

        if (!Checking.icontractHypothesis(project)) {
            return;
        }

        Context context = Contextualizing.infer(event);

        assert context != null;

        assert context.module != null : "Expected module to be defined in the context";

        final FileSaverDialog dialog = FileChooserFactory.getInstance().createSaveFileDialog(
                new FileSaverDescriptor(
                        "Ghostwritten Test File", "File to ghostwrite the test file", "py"),
                project);

        @Nullable VirtualFileWrapper result;
        final @Nullable String basePath = project.getBasePath();
        if (basePath == null) {
            result = dialog.save(null, null);
        } else {
            result = dialog.save(
                    LocalFileSystem.getInstance().findFileByIoFile(new File(basePath)),
                    "test_" + context.module);
        }

        if (result == null) {
            return;
        }

        final String path = Pathing.stripBaseBath(
                result.getFile().getPath(),
                Pathing.systemDependentPath(basePath));


        String runName = String.format("icontract-hypothesis ghostwrite explicit %s to %s",
                context.module, path);

        var scriptParameters = new ArrayList<String>();
        scriptParameters.add("ghostwrite");
        scriptParameters.add("--module");
        scriptParameters.add(context.module);
        scriptParameters.add("--explicit");
        scriptParameters.add("--output");
        scriptParameters.add(path);

        var runManager = RunManager.getInstance(project);
        var runner = Running.FindOrCreateRunner(
                runManager, runName, scriptParameters,
                Pathing.systemDependentPath(context.project.getBasePath()));

        Running.execute(runManager, runner);
    }

    /**
     * Change the visibility/enabled as well as the title of the action depending on the context.
     *
     * @param event Event received when the associated menu item is chosen.
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        Context context = Contextualizing.infer(event);

        final Presentation presentation = event.getPresentation();

        if (context == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }

        if (context.module == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }

        presentation.setText(
                String.format("ghostwrite explicit %s to ...", context.module), false);

        presentation.setDescription(
                String.format(
                        "Infer the Hypothesis strategies for %s and " +
                                "ghostwrite an explicit test file",
                        context.module));

        presentation.setEnabledAndVisible(true);
    }
}
