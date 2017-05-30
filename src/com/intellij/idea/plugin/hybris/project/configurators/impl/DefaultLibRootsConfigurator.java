/*
 * This file is part of "hybris integration" plugin for Intellij IDEA.
 * Copyright (C) 2014-2016 Alexander Bartash <AlexanderBartash@gmail.com>
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

package com.intellij.idea.plugin.hybris.project.configurators.impl;

import com.intellij.idea.plugin.hybris.common.HybrisConstants;
import com.intellij.idea.plugin.hybris.project.configurators.LibRootsConfigurator;
import com.intellij.idea.plugin.hybris.project.descriptors.CoreHybrisModuleDescriptor;
import com.intellij.idea.plugin.hybris.project.descriptors.HybrisModuleDescriptor;
import com.intellij.idea.plugin.hybris.project.descriptors.JavaLibraryDescriptor;
import com.intellij.idea.plugin.hybris.project.descriptors.OotbHybrisModuleDescriptor;
import com.intellij.idea.plugin.hybris.project.descriptors.PlatformHybrisModuleDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.roots.IdeaModifiableModelsProvider;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Created 11:45 PM 24 June 2015.
 *
 * @author Vlad Bozhenok <VladBozhenok@gmail.com>
 * @author Alexander Bartash <AlexanderBartash@gmail.com>
 */
public class DefaultLibRootsConfigurator implements LibRootsConfigurator {

    protected final ModifiableModelsProvider modifiableModelsProvider = new IdeaModifiableModelsProvider();

    @Override
    public void configure(
        @NotNull final ModifiableRootModel modifiableRootModel,
        @NotNull final HybrisModuleDescriptor moduleDescriptor
    ) {
        ApplicationManager.getApplication().invokeAndWait(() -> WriteAction.run(
            () -> configureInner(modifiableRootModel, moduleDescriptor)));
    }

    protected void configureInner(
        @NotNull final ModifiableRootModel modifiableRootModel,
        @NotNull final HybrisModuleDescriptor moduleDescriptor
    ) {
        Validate.notNull(modifiableRootModel);
        Validate.notNull(moduleDescriptor);

        final VirtualFile sourceCodeRoot = this.getSourceCodeRoot(moduleDescriptor);

        for (JavaLibraryDescriptor javaLibraryDescriptor : moduleDescriptor.getLibraryDescriptors()) {
            if (!javaLibraryDescriptor.isValid()) {
                continue;
            }
            if (javaLibraryDescriptor.isDirectoryWithClasses()) {
                this.addClassesToModuleLibs(modifiableRootModel, sourceCodeRoot, javaLibraryDescriptor);
            } else {
                this.addJarFolderToModuleLibs(modifiableRootModel, sourceCodeRoot, javaLibraryDescriptor);
            }
        }

        if (moduleDescriptor instanceof PlatformHybrisModuleDescriptor) {
            final PlatformHybrisModuleDescriptor hybrisModuleDescriptor = (PlatformHybrisModuleDescriptor) moduleDescriptor;
            hybrisModuleDescriptor.createBootstrapLib(sourceCodeRoot, modifiableModelsProvider);
        }

        if (moduleDescriptor instanceof CoreHybrisModuleDescriptor) {
            addLibsToModule(modifiableRootModel, HybrisConstants.PLATFORM_LIBRARY_GROUP, true);
        }

        if (moduleDescriptor instanceof OotbHybrisModuleDescriptor) {
            final OotbHybrisModuleDescriptor hybrisModuleDescriptor = (OotbHybrisModuleDescriptor) moduleDescriptor;
            if (hybrisModuleDescriptor.hasBackofficeModule()) {
                final File backofficeJarDirectory = new File(
                    hybrisModuleDescriptor.getRootDirectory(),
                    HybrisConstants.BACKOFFICE_JAR_DIRECTORY
                );
                if (backofficeJarDirectory.exists()) {
                    hybrisModuleDescriptor.createGlobalLibrary(
                        modifiableModelsProvider,
                        backofficeJarDirectory,
                        HybrisConstants.BACKOFFICE_LIBRARY_GROUP
                    );
                }
            }
            if (moduleDescriptor.getName().equals(HybrisConstants.BACK_OFFICE_EXTENSION_NAME)) {
                addLibsToModule(modifiableRootModel, HybrisConstants.BACKOFFICE_LIBRARY_GROUP, true);
            }
        }
    }

    @Nullable
    private VirtualFile getSourceCodeRoot(final @NotNull HybrisModuleDescriptor moduleDescriptor) {
        final VirtualFile sourceCodeRoot;
        final File sourceCodeZip = moduleDescriptor.getRootProjectDescriptor().getSourceCodeZip();

        if (null != sourceCodeZip) {
            final VirtualFile sourceZip = VfsUtil.findFileByIoFile(sourceCodeZip, true);
            if (null == sourceZip) {
                sourceCodeRoot = null;
            } else {
                sourceCodeRoot = JarFileSystem.getInstance().getJarRootForLocalFile(sourceZip);
            }
        } else {
            sourceCodeRoot = null;
        }

        return sourceCodeRoot;
    }

    protected void addClassesToModuleLibs(
        @NotNull final ModifiableRootModel modifiableRootModel,
        @Nullable final VirtualFile sourceCodeRoot,
        @NotNull final JavaLibraryDescriptor javaLibraryDescriptor
    ) {
        Validate.notNull(modifiableRootModel);
        Validate.notNull(javaLibraryDescriptor);

        final Library library = modifiableRootModel.getModuleLibraryTable().createLibrary();
        final Library.ModifiableModel libraryModifiableModel = library.getModifiableModel();
        libraryModifiableModel.addRoot(
            VfsUtil.getUrlForLibraryRoot(javaLibraryDescriptor.getLibraryFile()), OrderRootType.CLASSES
        );

        if (null != javaLibraryDescriptor.getSourcesFile()) {
            final VirtualFile srcDirVF = VfsUtil.findFileByIoFile(javaLibraryDescriptor.getSourcesFile(), true);
            if (null != srcDirVF) {
                libraryModifiableModel.addRoot(srcDirVF, OrderRootType.SOURCES);
            }
        }

        if (sourceCodeRoot != null && javaLibraryDescriptor.getLibraryFile().getName().endsWith("server.jar")) {
            libraryModifiableModel.addRoot(sourceCodeRoot, OrderRootType.SOURCES);
        }

        if (javaLibraryDescriptor.isExported()) {
            this.setLibraryEntryExported(modifiableRootModel, library);
        }

        libraryModifiableModel.commit();
    }

    protected void addJarFolderToModuleLibs(
        @NotNull final ModifiableRootModel modifiableRootModel,
        @Nullable final VirtualFile sourceCodeRoot,
        @NotNull final JavaLibraryDescriptor javaLibraryDescriptor
    ) {
        Validate.notNull(modifiableRootModel);
        Validate.notNull(javaLibraryDescriptor);

        final LibraryTable projectLibraryTable = modifiableRootModel.getModuleLibraryTable();

        final Library library = projectLibraryTable.createLibrary();
        final Library.ModifiableModel libraryModifiableModel = library.getModifiableModel();

        libraryModifiableModel.addJarDirectory(
            VfsUtil.getUrlForLibraryRoot(javaLibraryDescriptor.getLibraryFile()), true
        );

        if (null != javaLibraryDescriptor.getSourcesFile()) {
            final VirtualFile srcDirVF = VfsUtil.findFileByIoFile(javaLibraryDescriptor.getSourcesFile(), true);
            if (null != srcDirVF) {
                libraryModifiableModel.addRoot(srcDirVF, OrderRootType.SOURCES);
            }
        }

        if (null != sourceCodeRoot) {
            libraryModifiableModel.addRoot(sourceCodeRoot, OrderRootType.SOURCES);
        }

        if (javaLibraryDescriptor.isExported()) {
            this.setLibraryEntryExported(modifiableRootModel, library);
        }

        libraryModifiableModel.commit();
    }

    protected void addLibsToModule(
        @NotNull final ModifiableRootModel modifiableRootModel,
        @NotNull final String libraryName,
        @NotNull final boolean export
    ) {
        Validate.notNull(modifiableRootModel);

        final LibraryTable.ModifiableModel libraryTableModifiableModel = modifiableModelsProvider
            .getLibraryTableModifiableModel(modifiableRootModel.getProject());

        Library libsGroup = libraryTableModifiableModel.getLibraryByName(libraryName);

        if (null == libsGroup) {
            libsGroup = libraryTableModifiableModel.createLibrary(libraryName);
            libraryTableModifiableModel.commit();
        }

        modifiableRootModel.addLibraryEntry(libsGroup);

        if (export) {
            setLibraryEntryExported(modifiableRootModel, libsGroup);
        }
    }

    protected void setLibraryEntryExported(
        @NotNull final ModifiableRootModel modifiableRootModel,
        @NotNull final Library library
    ) {
        Validate.notNull(modifiableRootModel);
        Validate.notNull(library);

        final LibraryOrderEntry libraryOrderEntry = modifiableRootModel.findLibraryOrderEntry(library);
        if (null != libraryOrderEntry) {
            libraryOrderEntry.setExported(true);
        }
    }
}
