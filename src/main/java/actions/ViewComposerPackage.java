package actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.json.psi.JsonElement;
import com.intellij.json.psi.JsonObject;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ViewComposerPackage extends DumbAwareAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);

        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(e.getData(LangDataKeys.EDITOR).getDocument());

        int offset = editor.getCaretModel().getOffset();

        PsiElement psiElement = PsiManager.getInstance(e.getProject()).findFile(virtualFile).findElementAt(offset);

        PsiElement parent = PsiTreeUtil.getParentOfType(psiElement, JsonElement.class);

        if (parent.getText() == null) {
            return;
        }

        String packagistLink = "https://packagist.org/packages/" + parent.getText().replace("\"", "");
        BrowserUtil.browse(packagistLink);
    }

    @Override
    public void update(AnActionEvent e) {
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(e.getData(LangDataKeys.EDITOR).getDocument());
        Presentation presentation = e.getPresentation();
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        int offset = editor.getCaretModel().getOffset();
        PsiElement psiElement = PsiManager.getInstance(e.getProject()).findFile(virtualFile).findElementAt(offset);

        boolean insideRequire = Optional.ofNullable(PsiTreeUtil.getParentOfType(psiElement, JsonObject.class))
                .map(PsiElement::getParent)
                .map(PsiElement::getText)
                .map(text -> text.startsWith("\"require"))
                .orElse(false);

        presentation.setEnabledAndVisible(virtualFile.getName().equals("composer.json") && insideRequire);
    }
}
