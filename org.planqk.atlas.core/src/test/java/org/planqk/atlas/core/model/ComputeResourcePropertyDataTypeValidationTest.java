/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.planqk.atlas.core.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComputeResourcePropertyDataTypeValidationTest {
    @Test
    void validate_Integer_Pass() {
        Assertions.assertThat(ComputeResourcePropertyDataType.INTEGER.isValid("1000")).isTrue();
    }

    @Test
    void validate_Integer_Fail() {
        Assertions.assertThat(ComputeResourcePropertyDataType.INTEGER.isValid("100-0")).isFalse();
    }

    @Test
    void validate_Float_Pass() {
        Assertions.assertThat(ComputeResourcePropertyDataType.FLOAT.isValid("1.0001")).isTrue();
    }

    @Test
    void validate_Float_Fail() {
        Assertions.assertThat(ComputeResourcePropertyDataType.FLOAT.isValid("100-0")).isFalse();
    }

    @Test
    void validate_StringNull_Fail() {
        Assertions.assertThat(ComputeResourcePropertyDataType.STRING.isValid(null)).isFalse();
    }

    @Test
    void validate_String_Pass() {
        Assertions.assertThat(ComputeResourcePropertyDataType.STRING.isValid("Hello World")).isTrue();
    }
}
