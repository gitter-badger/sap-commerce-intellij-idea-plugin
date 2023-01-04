/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2019 EPAM Systems <hybrisideaplugin@epam.com>
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

package com.intellij.idea.plugin.hybris.system.type.file;

import com.intellij.idea.plugin.hybris.system.type.meta.TSMetaModelAccess;
import com.intellij.idea.plugin.hybris.system.type.meta.model.TSGlobalMetaEnum;
import com.intellij.idea.plugin.hybris.system.type.meta.model.TSMetaEnum;
import com.intellij.idea.plugin.hybris.system.type.meta.model.TSMetaType;
import com.intellij.idea.plugin.hybris.system.type.model.EnumType;
import com.intellij.psi.PsiElement;
import com.intellij.util.xml.ConvertContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnumTypeConverter extends TSConverterBase<EnumType> {

    public EnumTypeConverter() {
        super(EnumType.class);
    }

    @Override
    protected EnumType searchForName(
        @NotNull final String name, @NotNull final ConvertContext context, final TSMetaModelAccess meta
    ) {
        return Optional.ofNullable(meta.findMetaEnumByName(name))
                       .map(TSMetaEnum::retrieveDom)
                       .orElse(null);
    }

    @Override
    protected Collection<? extends EnumType> searchAll(
        @NotNull final ConvertContext context, final TSMetaModelAccess meta
    ) {
        return meta.<TSGlobalMetaEnum>getAll(TSMetaType.META_ENUM).stream()
                   .map(TSMetaEnum::retrieveDom)
                   .filter(Objects::nonNull)
                   .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public String toString(@Nullable final EnumType t, final ConvertContext context) {
        return useAttributeValue(t, EnumType::getCode);
    }

    @Nullable
    @Override
    public PsiElement getPsiElement(@Nullable final EnumType resolvedValue) {
        return navigateToValue(resolvedValue, EnumType::getCode);
    }
}