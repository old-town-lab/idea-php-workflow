package ru.oldTown.idea.php.workflow.dict;


import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import org.jetbrains.annotations.Nullable;

import java.util.Map;


/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpDocCommentAnnotation {

    final private Map<String, PhpDocTagAnnotation> annotationReferences;
    final private PhpDocComment phpDocComment;

    public PhpDocCommentAnnotation(Map<String, PhpDocTagAnnotation> annotationReferences, PhpDocComment phpDocComment) {
        this.annotationReferences = annotationReferences;
        this.phpDocComment = phpDocComment;
    }

    @Nullable
    public PhpDocTagAnnotation getPhpDocBlock(String className) {
        if(className.startsWith("\\")) className = className.substring(1);
        return annotationReferences.containsKey(className) ? annotationReferences.get(className) : null;
    }
}
