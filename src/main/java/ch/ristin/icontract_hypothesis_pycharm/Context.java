// Copyright (c) 2020 Marko Ristin <marko@ristin.ch>

package ch.ristin.icontract_hypothesis_pycharm;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.psi.PyFunction;

import javax.annotation.Nullable;

/**
 * Represent the context inferred from the caret.
 */
public class Context {
    /**
     * A global function corresponding to the caret as we go up the AST
     */
    @Nullable
    final PyFunction pyFunction;

    /**
     * Python file which is currently edited
     */
    final PsiFile psiFile;

    /**
     * Python module corresponding to the virtualFile, if possible to infer
     */
    @Nullable
    final String module;

    final Project project;

    public Context(@Nullable PyFunction pyFunction, PsiFile psiFile, @Nullable String module, Project project) {
        this.pyFunction = pyFunction;
        this.psiFile = psiFile;
        this.module = module;
        this.project = project;
    }
}
