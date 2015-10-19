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

package com.github.piasy.okbuck.rules

import com.github.piasy.okbuck.rules.base.BuckRule

import static com.github.piasy.okbuck.helper.CheckUtil.checkNotEmpty
import static com.github.piasy.okbuck.helper.CheckUtil.checkNotNull

/**
 * android_build_config()
 * */
public final class AndroidBuildConfigRule extends BuckRule {
    private final String mPackage
    private final List<String> mValues

    public AndroidBuildConfigRule(
            List<String> visibility, String packageName, List<String> values
    ) {
        super("android_build_config", "build_config", visibility)
        checkNotEmpty(packageName, "AndroidBuildConfigRule package can't be empty.")
        mPackage = packageName
        checkNotNull(values, "AndroidBuildConfigRule values must be non-null.")
        mValues = values
    }

    @Override
    protected final void printDetail(PrintStream printer) {
        printer.println("\tpackage = '${mPackage}',")
        if (!mValues.empty) {
            printer.println("\tvalues = [")
            for (String value : mValues) {
                printer.println("\t\t'${value}',")
            }
            printer.println("\t],")
        }
    }
}