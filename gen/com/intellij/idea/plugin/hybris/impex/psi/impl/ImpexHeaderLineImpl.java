// This is a generated file. Not intended for manual editing.
package com.intellij.idea.plugin.hybris.impex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.intellij.idea.plugin.hybris.impex.psi.ImpexTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.idea.plugin.hybris.impex.psi.*;

public class ImpexHeaderLineImpl extends ASTWrapperPsiElement implements ImpexHeaderLine {

  public ImpexHeaderLineImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ImpexVisitor) ((ImpexVisitor)visitor).visitHeaderLine(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ImpexAnyHeaderMode getAnyHeaderMode() {
    return findNotNullChildByClass(ImpexAnyHeaderMode.class);
  }

  @Override
  @NotNull
  public List<ImpexFullHeaderParameter> getFullHeaderParameterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ImpexFullHeaderParameter.class);
  }

  @Override
  @Nullable
  public ImpexFullHeaderType getFullHeaderType() {
    return findChildByClass(ImpexFullHeaderType.class);
  }

}
