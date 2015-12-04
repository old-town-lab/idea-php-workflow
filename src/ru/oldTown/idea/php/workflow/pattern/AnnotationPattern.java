package ru.oldTown.idea.php.workflow.pattern;


import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes;
import com.jetbrains.php.lang.documentation.phpdoc.parser.PhpDocElementTypes;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;


public class AnnotationPattern {

    /**
     * matches "@Callback(propertyName="<value>")"
     */
    public static PsiElementPattern.Capture<StringLiteralExpression> getPropertyIdentifierValue(String propertyName) {
        return PlatformPatterns.psiElement(StringLiteralExpression.class)
            .afterLeafSkipping(
                PlatformPatterns.or(
                    PlatformPatterns.psiElement(PsiWhiteSpace.class),
                    PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_TEXT).withText("=")
                ),
                PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER).withText(propertyName)
            )
            .withParent(PlatformPatterns.psiElement(PhpDocElementTypes.phpDocAttributeList));
    }


}
