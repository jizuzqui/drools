/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
 */

package org.kie.pmml.models.drools.scorecard.tests;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.core.PMMLContextImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(Parameterized.class)
public class CompoundNestedPredicateScorecardTest extends AbstractPMMLScorecardTest {

    private static final String MODEL_NAME = "CompoundNestedPredicateScorecard";
    private static final String PMML_SOURCE = "CompoundNestedPredicateScorecard.pmml";
    private static final String TARGET_FIELD = "Score";
    private static final String REASON_CODE1_FIELD = "Reason Code 1";
    private static final String REASON_CODE2_FIELD = "Reason Code 2";

    private static KiePMMLModel pmmlModel;

    private double input1;
    private String input2;
    private double score;
    private String reasonCode1;
    private String reasonCode2;

    public CompoundNestedPredicateScorecardTest(double input1, String input2, double score,
                                                String reasonCode1, String reasonCode2) {
        this.input1 = input1;
        this.input2 = input2;
        this.score = score;
        this.reasonCode1 = reasonCode1;
        this.reasonCode2 = reasonCode2;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlModel = loadPMMLModel(PMML_SOURCE);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                { -50, "classB", -8, "characteristic2ReasonCode", null },
                { -50, "classD", -8, "characteristic2ReasonCode", null },
                { -9, "classB", 75, "characteristic1ReasonCode", null },
                { 25.4, "classB", 75, "characteristic1ReasonCode", null },
                { -7, "classA", -8, "characteristic2ReasonCode", null },
                { -7, "classC", -15.5, "characteristic1ReasonCode", "characteristic2ReasonCode" },
                { 5, "classB", -15.5, "characteristic1ReasonCode", "characteristic2ReasonCode" },
                { 7.4, "classB", -15.5, "characteristic1ReasonCode", "characteristic2ReasonCode" },
                { 12, "classB", 75, "characteristic1ReasonCode", null },
                { 12, "classD", 75, "characteristic1ReasonCode", null },
        });
    }

    @Test
    public void testCompoundNestedPredicateScorecard() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("input1", input1);
        inputData.put("input2", input2);

        final PMMLRequestData pmmlRequestData = getPMMLRequestData(MODEL_NAME, inputData);
        PMML4Result pmml4Result = EXECUTOR.evaluate(pmmlModel, new PMMLContextImpl(pmmlRequestData), RELEASE_ID);

        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(score);
        /* TODO: Uncomment when reason codes are implemented
        Assertions.assertThat(pmml4Result.getResultVariables().get(REASON_CODE1_FIELD)).isEqualTo(reasonCode1);
        Assertions.assertThat(pmml4Result.getResultVariables().get(REASON_CODE2_FIELD)).isEqualTo(reasonCode2);
         */
    }
}
