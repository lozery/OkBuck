/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Piasy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.piasy.okbuck.generator

import com.github.piasy.okbuck.dependency.DependencyAnalyzer
import com.github.piasy.okbuck.generator.configs.BUCKFile
import org.gradle.api.Project

/**
 * Created by Piasy{github.com/Piasy} on 15/10/6.
 *
 * used to generate BUCK file content. Designed to support X os family, Linux, Unix, OS X;
 * and Windows os family (in the future).
 */
public abstract class BuckFileGenerator {
    protected final Project mRootProject
    protected final DependencyAnalyzer mDependencyAnalyzer
    protected final File mOkBuckDir
    protected final Map<String, String> mResPackages
    protected final String mKeystoreDir
    protected final String mSignConfigName
    protected final String mBuildVariant

    public BuckFileGenerator(
            Project rootProject, DependencyAnalyzer dependencyAnalyzer, File okBuckDir,
            Map<String, String> resPackages, String keystoreDir, String signConfigName,
            String buildVariant
    ) {
        mRootProject = rootProject
        mDependencyAnalyzer = dependencyAnalyzer
        mOkBuckDir = okBuckDir
        mResPackages = resPackages
        mKeystoreDir = keystoreDir
        mSignConfigName = signConfigName
        mBuildVariant = buildVariant
    }

    /**
     * generate {@code BUCKFile}
     */
    public abstract Map<Project, BUCKFile> generate()
}