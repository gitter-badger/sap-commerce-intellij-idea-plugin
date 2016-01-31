/*
 * This file is part of "hybris integration" plugin for Intellij IDEA.
 * Copyright (C) 2014-2015 Alexander Bartash <AlexanderBartash@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.intellij.idea.plugin.hybris.impex.utils;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created 4:20 PM 31 May 2015
 *
 * @author Alexander Bartash <AlexanderBartash@gmail.com>
 */
public class CommonPsiUtils {

    @Nullable
    @Contract(pure = true)
    public static IElementType getNullSafeElementType(@Nullable final PsiElement element) {
        return element == null ? null : CommonAstUtils.getNullSafeElementType(element.getNode());
    }

    @Nullable
    @Contract(pure = true)
    public static PsiElement getNextNonWhitespaceElement(@NotNull final PsiElement element) {
        PsiElement nextSibling = element.getNextSibling();

        while (null != nextSibling && ImpexPsiUtils.isWhiteSpace(nextSibling)) {
            nextSibling = nextSibling.getNextSibling();
        }

        return nextSibling;
    }

    @NotNull
    @Contract(pure = true)
    public static List<PsiElement> findChildrenByIElementType(@NotNull final PsiElement element,
                                                              @NotNull final IElementType elementType) {
        List<PsiElement> result = Collections.emptyList();
        ASTNode child = element.getNode().getFirstChildNode();

        while (child != null) {
            if (elementType == child.getElementType()) {
                if (null == result || result.isEmpty()) {
                    result = new ArrayList<PsiElement>();
                }
                result.add(child.getPsi());
            }
            child = child.getTreeNext();
        }

        return result;
    }

}
