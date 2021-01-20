// Copyright (c) 2020 Marko Ristin <marko@ristin.ch>

package ch.ristin.icontract_hypothesis_pycharm;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import org.jetbrains.annotations.NotNull;

public class ActionGroup extends DefaultActionGroup {
    @Override
    public void update(@NotNull AnActionEvent e) {
        Context context = Contextualizing.infer(e);

        final Presentation presentation = e.getPresentation();

        if (context == null) {
            presentation.setEnabledAndVisible(false);
        }

        presentation.setEnabledAndVisible(true);
    }
}
