package org.radarcns.redcap.managementportal;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Copyright 2017 King's College London
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Management Portal {@link Project} and {@link Subject} support tags: a key-value attribute.
 */
public class Tag {

    private final String key;
    private final String value;

    public Tag(
            @JsonProperty("key") String key,
            @JsonProperty("value") String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Tag{" + "\n"
            + "key='" + key + "'\n"
            + ", value='" + value + "'}";
    }
}
