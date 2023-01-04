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

package com.intellij.idea.plugin.hybris.toolwindow.system.type.components

import com.intellij.idea.plugin.hybris.psi.utils.PsiUtils
import com.intellij.idea.plugin.hybris.toolwindow.components.AbstractTable
import com.intellij.idea.plugin.hybris.system.type.meta.TSMetaItemService
import com.intellij.idea.plugin.hybris.system.type.meta.model.TSGlobalMetaItem
import com.intellij.idea.plugin.hybris.system.type.meta.model.TSMetaRelation
import com.intellij.openapi.project.Project
import com.intellij.util.ui.ListTableModel

private const val COLUMN_CUSTOM = "C"
private const val COLUMN_ORDERED = "O"
private const val COLUMN_DEPRECATED = "D"
private const val COLUMN_MODULE = "Module"
private const val COLUMN_OWNER = "Owner"
private const val COLUMN_QUALIFIER = "Qualifier"
private const val COLUMN_TYPE = "Type"
private const val COLUMN_COLLECTION_TYPE = "Collection Type"
private const val COLUMN_CARDINALITY = "Cardinality"
private const val COLUMN_DESCRIPTION = "Description"

class TSMetaRelationElementsTable private constructor(myProject: Project) : AbstractTable<TSGlobalMetaItem, TSMetaRelation.TSMetaRelationElement>(myProject) {

    override fun getSearchableColumnNames() = listOf(COLUMN_QUALIFIER, COLUMN_DESCRIPTION)
    override fun getFixedWidthColumnNames() = listOf(COLUMN_CUSTOM, COLUMN_ORDERED, COLUMN_DEPRECATED)
    override fun select(meta: TSMetaRelation.TSMetaRelationElement) = selectRowWithValue(meta.name, COLUMN_QUALIFIER)
    override fun getItems(meta: TSGlobalMetaItem) = TSMetaItemService.getInstance(myProject).getRelationEnds(meta, true)
        .sortedWith(compareBy(
            { !it.isCustom },
            { it.module.name },
            { it.name })
        )

    override fun createModel(): ListTableModel<TSMetaRelation.TSMetaRelationElement> = with(ListTableModel<TSMetaRelation.TSMetaRelationElement>()) {
        columnInfos = arrayOf(
            createColumn(
                name = COLUMN_CUSTOM,
                valueProvider = { attr -> attr.isCustom },
                columnClass = Boolean::class.java,
                tooltip = "Custom"
            ),
            createColumn(
                name = COLUMN_ORDERED,
                valueProvider = { attr -> attr.isOrdered },
                columnClass = Boolean::class.java,
                tooltip = "Ordered"
            ),
            createColumn(
                name = COLUMN_DEPRECATED,
                valueProvider = { attr -> attr.isDeprecated },
                columnClass = Boolean::class.java,
                tooltip = "Deprecated"
            ),
            createColumn(
                name = COLUMN_MODULE,
                valueProvider = { attr -> PsiUtils.getModuleName(attr.module) }
            ),
            createColumn(
                name = COLUMN_OWNER,
                valueProvider = { attr -> attr.owner.name }
            ),
            createColumn(
                name = COLUMN_QUALIFIER,
                valueProvider = { attr -> attr.name }
            ),
            createColumn(
                name = COLUMN_CARDINALITY,
                valueProvider = { attr -> attr.cardinality }
            ),
            createColumn(
                name = COLUMN_TYPE,
                valueProvider = { attr -> attr.type }
            ),
            createColumn(
                name = COLUMN_COLLECTION_TYPE,
                valueProvider = { attr -> attr.collectionType }
            ),
            createColumn(
                name = COLUMN_DESCRIPTION,
                valueProvider = { attr -> attr.description }
            )
        )

        this
    }

    companion object {

        fun getInstance(project: Project): TSMetaRelationElementsTable = with(TSMetaRelationElementsTable(project)) {
            init()

            this
        }
    }

}