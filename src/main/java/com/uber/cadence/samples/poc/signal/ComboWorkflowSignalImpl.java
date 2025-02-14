/*
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package com.uber.cadence.samples.poc.signal;

import com.uber.cadence.samples.poc.UploadActivity;
import com.uber.cadence.workflow.Async;
import com.uber.cadence.workflow.Promise;
import com.uber.cadence.workflow.Workflow;

public class ComboWorkflowSignalImpl implements ComboWorkflowSignal {

  boolean completeManual = false;

  @Override
  public void execute(String input) {

    UploadActivity activities = Workflow.newActivityStub(UploadActivity.class);
    System.out.println("Workflow Called " + input + " " + completeManual);
    Promise<String> first = Async.function(activities::signalDecomposeComboCSV, input);
    Workflow.await(() -> completeManual);
    Promise<String> second = Async.function(activities::updateDecomposedData, first.get());
    Promise<String> third = Async.function(activities::validateAndSaveMasterData, second.get());
    Promise<String> fourth = Async.function(activities::createDraft, third.get());
    System.out.println(fourth.get());
  }

  @Override
  public void completeManualTask(boolean complete) {
    System.out.println("Current value " + completeManual);
    completeManual = complete;
    System.out.println("New value " + completeManual);
  }
}
