package ru.oldTown.idea.php.workflow.index;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.stubs.indexes.PhpConstantNameIndex;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import ru.oldTown.idea.php.workflow.util.AnnotationUtil;

import java.util.Map;


public class WorkflowServiceNameStubIndex extends FileBasedIndexExtension<String, Void> {
    public static final ID<String, Void> KEY = ID.create(" ru.oldTown.idea.php.workflow.services");
    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return new DataIndexer<String, Void, FileContent>() {
            @NotNull
            @Override
            public Map<String, Void> map(@NotNull FileContent inputData) {
                final Map<String, Void> map = new THashMap<>();

                PsiFile psiFile = inputData.getPsiFile();
                if(!(psiFile instanceof PhpFile)) {
                    return map;
                }

                if(!AnnotationUtil.isValidForIndex(inputData)) {
                    return map;
                }

                psiFile.accept(new PsiRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        if ((element instanceof PhpClass)) {
                            visitPhpClass((PhpClass) element);
                        }

                        super.visitElement(element);
                    }

                    private void visitPhpClass(PhpClass phpClass) {
                        String fqn = phpClass.getPresentableFQN();
                        if (fqn == null) {
                            return;
                        }

                        String serviceName = AnnotationUtil.getWorkflowServiceName(phpClass);

                        if (null != serviceName) {
                            map.put(serviceName, null);
                        }
                    }

                });

                return map;
            }
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return this.myKeyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<Void> getValueExternalizer() {
        return ScalarIndexExtension.VOID_DATA_EXTERNALIZER;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return PhpConstantNameIndex.PHP_INPUT_FILTER;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 2;
    }
}
