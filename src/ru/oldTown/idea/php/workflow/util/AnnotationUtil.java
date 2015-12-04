package ru.oldTown.idea.php.workflow.util;

import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileContent;
import com.jetbrains.php.lang.documentation.phpdoc.PhpDocUtil;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.oldTown.idea.php.workflow.dict.PhpDocCommentAnnotation;
import ru.oldTown.idea.php.workflow.dict.PhpDocTagAnnotation;

import java.util.*;

public class AnnotationUtil {

    final public static String  WORKFLOW_SERVICE_ANNOTATION_CLASS = "\\OldTown\\Workflow\\ZF2\\ServiceEngine\\Annotations\\Service";

    public static Set<String> NON_ANNOTATION_TAGS = new HashSet<String>() {{
        addAll(Arrays.asList(PhpDocUtil.ALL_TAGS));
        add("@Annotation");
        add("@inheritDoc");
        add("@Enum");
        add("@inheritdoc");
        add("@Target");
    }};

    public static boolean isValidForIndex(FileContent inputData) {

        String fileName = inputData.getPsiFile().getName();
        return !fileName.startsWith(".");
    }


    public static String getWorkflowServiceName(PhpClass phpClass)
    {
        PhpDocComment docComment = phpClass.getDocComment();
        if(docComment == null) {
            return null;
        }

        PhpDocCommentAnnotation container = AnnotationUtil.getPhpDocCommentAnnotationContainer(docComment);

        if (null == container) {
            return null;
        }
        PhpDocTagAnnotation phpDocBlock = container.getPhpDocBlock(WORKFLOW_SERVICE_ANNOTATION_CLASS);
        if (null == phpDocBlock) {
            return null;
        }

        return phpDocBlock.getPropertyValue("name");
    }



    @Nullable
    public static PhpDocCommentAnnotation getPhpDocCommentAnnotationContainer(@Nullable PhpDocComment phpDocComment) {
        if(phpDocComment == null) return null;

        Map<String, String> uses = AnnotationUtil.getUseImportMap(phpDocComment);

        Map<String, PhpDocTagAnnotation> annotationRefsMap = new HashMap<>();
        for(PhpDocTag phpDocTag: PsiTreeUtil.findChildrenOfType(phpDocComment, PhpDocTag.class)) {
            if(!AnnotationUtil.NON_ANNOTATION_TAGS.contains(phpDocTag.getName())) {
                PhpClass annotationClass = AnnotationUtil.getAnnotationReference(phpDocTag, uses);
                if(annotationClass != null && annotationClass.getPresentableFQN() != null) {
                    annotationRefsMap.put(annotationClass.getPresentableFQN(), new PhpDocTagAnnotation(annotationClass, phpDocTag));
                }
            }

        }

        return new PhpDocCommentAnnotation(annotationRefsMap, phpDocComment);
    }


    /*
    * Collect file use imports and resolve alias with their class name
    *
    * @param PhpDocComment current doc scope
    * @return map with class names as key and fqn on value
    */
    @NotNull
    public static Map<String, String> getUseImportMap(@Nullable PhpDocComment phpDocComment) {

        // search for use alias in local file
        final Map<String, String> useImports = new HashMap<>();

        if(phpDocComment == null) {
            return useImports;
        }

        GroupStatement phpNamespace = PsiTreeUtil.getParentOfType(phpDocComment, GroupStatement.class);
        if(phpNamespace == null) {
            return useImports;
        }

        for(PhpUseList phpUseList : PsiTreeUtil.getChildrenOfTypeAsList(phpNamespace, PhpUseList.class)) {
            PhpUse[] declarations = phpUseList.getDeclarations();
            if(declarations != null) {
                for(PhpUse phpUse : declarations) {
                    String alias = phpUse.getAliasName();
                    if (alias != null) {
                        useImports.put(alias, phpUse.getOriginal());
                    } else {
                        useImports.put(phpUse.getName(), phpUse.getOriginal());
                    }
                }
            }
        }

        return useImports;
    }



    @Nullable
    public static PhpClass getAnnotationReference(PhpDocTag phpDocTag, final Map<String, String> useImports) {

        String tagName = phpDocTag.getName();
        if(tagName.startsWith("@")) {
            tagName = tagName.substring(1);
        }

        String className = tagName;
        String subNamespaceName = "";
        if(className.contains("\\")) {
            className = className.substring(0, className.indexOf("\\"));
            subNamespaceName = tagName.substring(className.length());
        }

        if(!useImports.containsKey(className)) {

            // allow full classes on annotations #17 eg: @Doctrine\ORM\Mapping\PostPersist()
            PhpClass phpClass = PhpElementsUtil.getClass(phpDocTag.getProject(), tagName);
            if(phpClass != null && isAnnotationClass(phpClass)) {
                return phpClass;
            }

            return null;
        }

        return PhpElementsUtil.getClass(phpDocTag.getProject(), useImports.get(className) + subNamespaceName);

    }

    public static boolean isAnnotationClass(@NotNull PhpClass phpClass) {
        PhpDocComment phpDocComment = phpClass.getDocComment();
        if(phpDocComment != null) {
            PhpDocTag[] annotationDocTags = phpDocComment.getTagElementsByName("@Annotation");
            if(annotationDocTags.length > 0) {
                return true;
            }
        }

        return false;
    }
}


