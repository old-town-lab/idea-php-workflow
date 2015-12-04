package ru.oldTown.idea.php.workflow.util;


import com.intellij.openapi.project.Project;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PhpElementsUtil {
    @Nullable
    static public PhpClass getClass(Project project, String className) {
        Collection<PhpClass> classes = PhpIndex.getInstance(project).getClassesByFQN(className);
        return classes.isEmpty() ? null : classes.iterator().next();
    }

    @Nullable
    public static <T extends PsiElement> T getChildrenOnPatternMatch(@Nullable PsiElement element, ElementPattern<T> pattern) {
        if (element == null) return null;

        for (PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (pattern.accepts(child)) {
                //noinspection unchecked
                return (T)child;
            }
        }

        return null;
    }
}


