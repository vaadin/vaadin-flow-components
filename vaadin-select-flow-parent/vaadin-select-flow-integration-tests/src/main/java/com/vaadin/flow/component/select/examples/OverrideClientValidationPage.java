/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.vaadin.flow.component.select.examples;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;

@Route("vaadin-select/override-client-validation")
public class OverrideClientValidationPage extends Div {
    public OverrideClientValidationPage() {
        Select<String> select = new Select<>("a", "b", "c");
        Span validationStateSpan = new Span();
        validationStateSpan.setId("validation-state-span");

        NativeButton setInvalidButton = new NativeButton("Set invalid",
                e -> select.setInvalid(true));
        setInvalidButton.setId("set-invalid-button");

        NativeButton logValidationStateButton = new NativeButton("Log validation state",
                e -> validationStateSpan.setText(select.isInvalid() ? "invalid" : "valid"));
        logValidationStateButton.setId("log-validation-state-button");

        add(select, validationStateSpan, setInvalidButton, logValidationStateButton);
    }
}
