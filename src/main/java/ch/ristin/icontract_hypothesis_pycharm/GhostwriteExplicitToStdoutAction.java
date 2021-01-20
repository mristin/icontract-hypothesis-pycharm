// Copyright 2020 Marko Ristin <marko@ristin.ch>

package ch.ristin.icontract_hypothesis_pycharm;

import com.intellij.execution.RunManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Ghostwrite Hypothesis strategies explicitly with icontract-hypothesis to STDOUT.
 */
public class GhostwriteExplicitToStdoutAction extends AnAction {
    /**
     * Specify and run the "icontract-hypothesis ghostwrite --explicit" command as a run configuration.
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

        String runName = String.format("icontract-hypothesis ghostwrite explicit %s", context.module);

        var scriptParameters = new ArrayList<String>();
        scriptParameters.add("ghostwrite");
        scriptParameters.add("--module");
        scriptParameters.add(context.module);
        scriptParameters.add("--explicit");

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
                String.format("ghostwrite explicit %s to STDOUT", context.module), false);

        presentation.setDescription(
                String.format(
                        "Infer the Hypothesis strategies for %s and " +
                                "ghostwrite an explicit test file to the standard output",
                        context.module));

        presentation.setEnabledAndVisible(true);
    }
}
