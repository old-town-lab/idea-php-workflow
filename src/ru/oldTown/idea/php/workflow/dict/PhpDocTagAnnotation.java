package ru.oldTown.idea.php.workflow.dict;

import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.oldTown.idea.php.workflow.util.PhpElementsUtil;
import ru.oldTown.idea.php.workflow.pattern.AnnotationPattern;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpDocTagAnnotation {

    final private PhpClass phpClass;
    final private PhpDocTag phpDocTag;

    public PhpDocTagAnnotation(@NotNull PhpClass phpClass, @NotNull PhpDocTag phpDocTag) {
        this.phpClass = phpClass;
        this.phpDocTag = phpDocTag;
    }


    @NotNull
    public PhpClass getPhpClass() {
        return phpClass;
    }

    @NotNull
    public PhpDocTag getPhpDocTag() {
        return phpDocTag;
    }

    /**
     * Get property Value from "@Template(template="foo");
     *
     * @param propertyName property name template=""
     * @return Property value
     */
    @Nullable
    public String getPropertyValue(String propertyName) {
        StringLiteralExpression literalExpression = getPropertyValuePsi(propertyName);
        if(literalExpression != null) {
            return literalExpression.getContents();
        }

        return null;
    }

    /**
     * Get property psi element
     *
     * @param propertyName property name template=""
     * @return Property value
     */
    @Nullable
    public StringLiteralExpression getPropertyValuePsi(String propertyName) {
        PhpPsiElement docAttrList = phpDocTag.getFirstPsiChild();
        if(docAttrList != null) {
            return PhpElementsUtil.getChildrenOnPatternMatch(docAttrList, AnnotationPattern.getPropertyIdentifierValue(propertyName));
        }

        return null;
    }
}
