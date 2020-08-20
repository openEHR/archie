package org.openehr.bmm.utils;

/*
 * #%L
 * OpenEHR - Java Model Stack
 * %%
 * Copyright (C) 2016 - 2017  Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 * Author: Claude Nanjo
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReferenceModelVersionUtils {
    public static final String standardVersionRegex = "[0-9]+(\\.[0-9]+){0,2}";
    public static final Pattern standardVersionPattern = Pattern.compile(standardVersionRegex);

    public static boolean validateVersion(String version) {
        Matcher matcher = standardVersionPattern.matcher(version);
        return matcher.matches();
    }
}
